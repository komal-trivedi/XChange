package org.knowm.xchange.binance.dto.marketdata;

import java.math.BigDecimal;
import java.util.Date;

import org.knowm.xchange.binance.BinanceAdapters;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.Ticker;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class BinanceTicker24h {

  private final String symbol;
  private final BigDecimal priceChange;
  private final BigDecimal priceChangePercent;
  private final BigDecimal weightedAvgPrice;
  private final BigDecimal lastPrice;
  private final BigDecimal lastQty;
  private final BigDecimal openPrice;
  private final BigDecimal highPrice;
  private final BigDecimal lowPrice;
  private final BigDecimal volume;
  private final BigDecimal quoteVolume;
  private final long openTime;
  private final long closeTime;
  private final long firstId;
  private final long lastId;
  private final long count;

  // The curency pair that is unfortunately not returned in the response
  private CurrencyPair pair;

  // The cached ticker
  private Ticker ticker;

  public BinanceTicker24h(
      @JsonProperty("priceChange") BigDecimal priceChange,
      @JsonProperty("priceChangePercent") BigDecimal priceChangePercent,
      @JsonProperty("weightedAvgPrice") BigDecimal weightedAvgPrice,
      @JsonProperty("lastPrice") BigDecimal lastPrice,
      @JsonProperty("lastQty") BigDecimal lastQty,
      @JsonProperty("openPrice") BigDecimal openPrice,
      @JsonProperty("highPrice") BigDecimal highPrice,
      @JsonProperty("lowPrice") BigDecimal lowPrice,
      @JsonProperty("volume") BigDecimal volume,
      @JsonProperty("quoteVolume") BigDecimal quoteVolume,
      @JsonProperty("openTime") long openTime,
      @JsonProperty("closeTime") long closeTime,
      @JsonProperty("firstId") long firstId,
      @JsonProperty("lastId") long lastId,
      @JsonProperty("count") long count,
      @JsonProperty("symbol") String symbol) {
    this.priceChange = priceChange;
    this.priceChangePercent = priceChangePercent;
    this.weightedAvgPrice = weightedAvgPrice;
    this.lastPrice = lastPrice;
    this.lastQty = lastQty;
    this.openPrice = openPrice;
    this.highPrice = highPrice;
    this.lowPrice = lowPrice;
    this.volume = volume;
    this.quoteVolume = quoteVolume;
    this.openTime = openTime;
    this.closeTime = closeTime;
    this.firstId = firstId;
    this.lastId = lastId;
    this.count = count;
    this.symbol = symbol;
  }

  public String getSymbol() {
    return symbol;
  }

  public CurrencyPair getCurrencyPair() {
    return pair;
  }

  public void setCurrencyPair(CurrencyPair pair) {
    this.pair = pair;
  }

  public BigDecimal getPriceChange() {
    return priceChange;
  }

  public BigDecimal getPriceChangePercent() {
    return priceChangePercent;
  }

  public BigDecimal getWeightedAvgPrice() {
    return weightedAvgPrice;
  }

  public BigDecimal getLastPrice() {
    return lastPrice;
  }

  public BigDecimal getLastQty() {
    return lastQty;
  }

  public BigDecimal getOpenPrice() {
    return openPrice;
  }

  public BigDecimal getHighPrice() {
    return highPrice;
  }

  public BigDecimal getLowPrice() {
    return lowPrice;
  }

  public BigDecimal getVolume() {
    return volume;
  }

  public BigDecimal getQuoteVolume() {
    return quoteVolume;
  }

  public long getFirstTradeId() {
    return firstId;
  }

  public long getLastTradeId() {
    return lastId;
  }

  public long getTradeCount() {
    return count;
  }

  public Date getOpenTime() {
    return new Date(openTime);
  }

  public Date getCloseTime() {
    return new Date(closeTime);
  }

  public synchronized Ticker toTicker() {
    CurrencyPair currencyPair = pair;
    if (currencyPair == null) {
      currencyPair = BinanceAdapters.adaptSymbol(symbol);
    }
    if (ticker == null) {
      ticker =
          new Ticker.Builder()
              .currencyPair(currencyPair)
              .open(openPrice)
              .last(lastPrice)
              .high(highPrice)
              .low(lowPrice)
              .volume(volume)
              .vwap(weightedAvgPrice)
              .quoteVolume(quoteVolume)
              .timestamp(closeTime > 0 ? new Date(closeTime) : null)
              .percentageChange(priceChangePercent)
              .build();
    }
    return ticker;
  }
}
