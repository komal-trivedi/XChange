package info.bitrich.xchangestream.binance;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.knowm.xchange.binance.BinanceAuthenticated;
import org.knowm.xchange.binance.BinanceFuturesExchange;
import org.knowm.xchange.binance.service.BinanceMarketDataService;
import org.knowm.xchange.client.ExchangeRestProxyBuilder;
import org.knowm.xchange.currency.CurrencyPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.bitrich.xchangestream.binance.BinanceUserDataChannel.NoActiveChannelException;
import info.bitrich.xchangestream.core.ProductSubscription;
import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.service.netty.ConnectionStateModel.State;
import info.bitrich.xchangestream.util.Events;
import io.reactivex.Completable;
import io.reactivex.Observable;

public class BinanceFuturesStreamingExchange extends BinanceFuturesExchange implements StreamingExchange {

  private static final Logger LOG = LoggerFactory.getLogger(BinanceFuturesStreamingExchange.class);
  private static final String WS_API_BASE_URI = "wss://fstream.binance.com/";
  private static final String WS_SANDBOX_API_BASE_URI = "wss://stream.binancefuture.com/";
  protected static final String USE_HIGHER_UPDATE_FREQUENCY =
      "Binance_Orderbook_Use_Higher_Frequency";
  protected static final String USE_REALTIME_BOOK_TICKER = "Binance_Ticker_Use_Realtime";
  private BinanceStreamingService streamingService;
  private BinanceUserDataStreamingService userDataStreamingService;

  private BinanceStreamingMarketDataService streamingMarketDataService;
  private BinanceStreamingAccountService streamingAccountService;
  private BinanceStreamingTradeService streamingTradeService;

  private BinanceUserDataChannel userDataChannel;
  private Runnable onApiCall;
  private String orderBookUpdateFrequencyParameter = "";
  private boolean realtimeOrderBookTicker;

  @Override
  protected void initServices() {
    super.initServices();
    this.onApiCall = Events.onApiCall(exchangeSpecification);
    boolean userHigherFrequency =
        Boolean.TRUE.equals(
            exchangeSpecification.getExchangeSpecificParametersItem(USE_HIGHER_UPDATE_FREQUENCY));
    realtimeOrderBookTicker =
        Boolean.TRUE.equals(
            exchangeSpecification.getExchangeSpecificParametersItem(USE_REALTIME_BOOK_TICKER));
    if (userHigherFrequency) {
      orderBookUpdateFrequencyParameter = "@100ms";
    }
  }

  /**
   * Binance streaming API expects connections to multiple channels to be defined at connection
   * time. To define the channels for this connection pass a `ProductSubscription` in at connection
   * time.
   *
   * @param args A single `ProductSubscription` to define the subscriptions required to be available
   *     during this connection.
   * @return A completable which fulfils once connection is complete.
   */
  @Override
  public Completable connect(ProductSubscription... args) {
    if (args == null || args.length == 0) {
      throw new IllegalArgumentException("Subscriptions must be made at connection time");
    }
    if (streamingService != null) {
      throw new UnsupportedOperationException(
          "Exchange only handles a single connection - disconnect the current connection.");
    }

    ProductSubscription subscriptions = args[0];
    streamingService = createStreamingService(subscriptions);

    List<Completable> completables = new ArrayList<>();

    if (subscriptions.hasUnauthenticated()) {
      completables.add(streamingService.connect());
    }

    if (subscriptions.hasAuthenticated()) {
      if (exchangeSpecification.getApiKey() == null) {
        throw new IllegalArgumentException("API key required for authenticated streams");
      }

      LOG.info("Connecting to authenticated web socket");
      BinanceAuthenticated binance =
          ExchangeRestProxyBuilder.forInterface(
                  BinanceAuthenticated.class, getExchangeSpecification())
              .build();
      userDataChannel =
          new BinanceUserDataChannel(binance, exchangeSpecification.getApiKey(), onApiCall);
      try {
        completables.add(createAndConnectUserDataService(userDataChannel.getListenKey()));
      } catch (NoActiveChannelException e) {
        throw new IllegalStateException("Failed to establish user data channel", e);
      }
    }

    streamingMarketDataService =
        new BinanceStreamingMarketDataService(
            streamingService,
            (BinanceMarketDataService) marketDataService,
            onApiCall,
            orderBookUpdateFrequencyParameter,
            realtimeOrderBookTicker);
    streamingAccountService = new BinanceStreamingAccountService(userDataStreamingService);
    streamingTradeService = new BinanceStreamingTradeService(userDataStreamingService);

    return Completable.concat(completables)
        .doOnComplete(() -> streamingMarketDataService.openSubscriptions(subscriptions))
        .doOnComplete(() -> streamingAccountService.openSubscriptions())
        .doOnComplete(() -> streamingTradeService.openSubscriptions());
  }

  private Completable createAndConnectUserDataService(String listenKey) {
	String path = getWebsocketURI();
		        
    userDataStreamingService = BinanceUserDataStreamingService.create(path,listenKey);
    return userDataStreamingService
        .connect()
        .doOnComplete(
            () -> {
              LOG.info("Connected to authenticated web socket");
              userDataChannel.onChangeListenKey(
                  newListenKey -> {
                    userDataStreamingService
                        .disconnect()
                        .doOnComplete(
                            () -> {
                              createAndConnectUserDataService(newListenKey)
                                  .doOnComplete(
                                      () -> {
                                        streamingAccountService.setUserDataStreamingService(
                                            userDataStreamingService);
                                        streamingTradeService.setUserDataStreamingService(
                                            userDataStreamingService);
                                      });
                            });
                  });
            });
  }

  @Override
  public Completable disconnect() {
    List<Completable> completables = new ArrayList<>();
    completables.add(streamingService.disconnect());
    streamingService = null;
    if (userDataStreamingService != null) {
      completables.add(userDataStreamingService.disconnect());
      userDataStreamingService = null;
    }
    if (userDataChannel != null) {
      userDataChannel.close();
      userDataChannel = null;
    }
    streamingMarketDataService = null;
    return Completable.concat(completables);
  }

  @Override
  public boolean isAlive() {
    return streamingService != null && streamingService.isSocketOpen();
  }

  @Override
  public Observable<Throwable> reconnectFailure() {
    return streamingService.subscribeReconnectFailure();
  }

  @Override
  public Observable<Object> connectionSuccess() {
    return streamingService.subscribeConnectionSuccess();
  }

  @Override
  public Observable<State> connectionStateObservable() {
    return streamingService.subscribeConnectionState();
  }

  @Override
  public BinanceStreamingMarketDataService getStreamingMarketDataService() {
    return streamingMarketDataService;
  }

  @Override
  public BinanceStreamingAccountService getStreamingAccountService() {
    return streamingAccountService;
  }

  @Override
  public BinanceStreamingTradeService getStreamingTradeService() {
    return streamingTradeService;
  }

  private String getWebsocketURI() {
	  String path="";
	  if(exchangeSpecification.getOverrideWebsocketApiUri()!=null) {
		  path = exchangeSpecification.getOverrideWebsocketApiUri();
	  }
	  else {
		  path = Boolean.TRUE.equals(exchangeSpecification.getExchangeSpecificParametersItem(USE_SANDBOX))
				  ? WS_SANDBOX_API_BASE_URI
						  : WS_API_BASE_URI;
	  }
	  return path;
  }
  
  protected BinanceStreamingService createStreamingService(ProductSubscription subscription) {
    String path = getWebsocketURI();
    path += "stream?streams=" + buildSubscriptionStreams(subscription);
    return new BinanceStreamingService(path, subscription);
  }

  public String buildSubscriptionStreams(ProductSubscription subscription) {
    return Stream.of(
            buildSubscriptionStrings(
                subscription.getTicker(),
                realtimeOrderBookTicker
                    ? BinanceSubscriptionType.BOOK_TICKER.getType()
                    : BinanceSubscriptionType.TICKER.getType()),
            buildSubscriptionStrings(
                subscription.getOrderBook(), BinanceSubscriptionType.DEPTH.getType()),
            buildSubscriptionStrings(
                subscription.getTrades(), BinanceSubscriptionType.TRADE.getType()))
        .filter(s -> !s.isEmpty())
        .collect(Collectors.joining("/"));
  }

  private String buildSubscriptionStrings(
      List<CurrencyPair> currencyPairs, String subscriptionType) {
    if (BinanceSubscriptionType.DEPTH.getType().equals(subscriptionType)) {
      return subscriptionStrings(currencyPairs)
          .map(s -> s + "@" + subscriptionType + orderBookUpdateFrequencyParameter)
          .collect(Collectors.joining("/"));
    } else {
      return subscriptionStrings(currencyPairs)
          .map(s -> s + "@" + subscriptionType)
          .collect(Collectors.joining("/"));
    }
  }

  private static Stream<String> subscriptionStrings(List<CurrencyPair> currencyPairs) {
    return currencyPairs.stream()
        .map(pair -> String.join("", pair.toString().split("/")).toLowerCase());
  }

  @Override
  public void useCompressedMessages(boolean compressedMessages) {
    streamingService.useCompressedMessages(compressedMessages);
  }

  public void enableLiveSubscription() {
    if (this.streamingService == null) {
      throw new UnsupportedOperationException(
          "You must connect to streams before enabling live subscription.");
    }
    this.streamingService.enableLiveSubscription();
  }

  public void disableLiveSubscription() {
    if (this.streamingService != null) this.streamingService.disableLiveSubscription();
  }
}
