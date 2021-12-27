package org.knowm.xchange.huobi;

import java.io.IOException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.knowm.xchange.huobi.dto.account.HuobiCreateWithdrawRequest;
import org.knowm.xchange.huobi.dto.account.results.HuobiBalanceResult;
import org.knowm.xchange.huobi.dto.account.results.HuobiCreateWithdrawResult;
import org.knowm.xchange.huobi.dto.account.results.HuobiDepositAddressResult;
import org.knowm.xchange.huobi.dto.account.results.HuobiDepositAddressV2Result;
import org.knowm.xchange.huobi.dto.account.results.HuobiDepositAddressWithTagResult;
import org.knowm.xchange.huobi.dto.account.results.HuobiFeeRateResult;
import org.knowm.xchange.huobi.dto.account.results.HuobiFundingHistoryResult;
import org.knowm.xchange.huobi.dto.account.results.HuobiTransactFeeRateResult;
import org.knowm.xchange.huobi.dto.account.results.HuobiWithdrawFeeRangeResult;
import org.knowm.xchange.huobi.dto.marketdata.results.HuobiAllTickersResult;
import org.knowm.xchange.huobi.dto.marketdata.results.HuobiAssetPairsResult;
import org.knowm.xchange.huobi.dto.marketdata.results.HuobiAssetsResult;
import org.knowm.xchange.huobi.dto.marketdata.results.HuobiCurrenciesResult;
import org.knowm.xchange.huobi.dto.marketdata.results.HuobiDepthResult;
import org.knowm.xchange.huobi.dto.marketdata.results.HuobiKlinesResult;
import org.knowm.xchange.huobi.dto.marketdata.results.HuobiTickerResult;
import org.knowm.xchange.huobi.dto.marketdata.results.HuobiTradesResult;
import org.knowm.xchange.huobi.dto.trade.HuobiCancelClientOrderRequest;
import org.knowm.xchange.huobi.dto.trade.HuobiCreateOrderRequest;
import org.knowm.xchange.huobi.dto.trade.HuobiOpenOrdersRequest;
import org.knowm.xchange.huobi.dto.trade.HuobiOrderByOrderIdRequest;
import org.knowm.xchange.huobi.dto.trade.results.HuobiCancelOrderResult;
import org.knowm.xchange.huobi.dto.trade.results.HuobiMatchesResult;
import org.knowm.xchange.huobi.dto.trade.results.HuobiOrderInfoResult;
import org.knowm.xchange.huobi.dto.trade.results.HuobiOrderResult;
import org.knowm.xchange.huobi.dto.trade.results.HuobiOrdersResult;

import si.mazi.rescu.ParamsDigest;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public interface Huobi {

  @GET
  @Path("market/detail/merged")
  HuobiTickerResult getTicker(@QueryParam("symbol") String symbol) throws IOException;

  @GET
  @Path("market/tickers")
  HuobiAllTickersResult getAllTickers() throws IOException;

  @GET
  @Path("market/depth")
  HuobiDepthResult getDepth(@QueryParam("symbol") String symbol, @QueryParam("type") String type)
      throws IOException;

  @GET
  @Path("market/history/trade")
  HuobiTradesResult getTrades(@QueryParam("symbol") String symbol, @QueryParam("size") int size)
      throws IOException;

  @GET
  @Path("market/history/kline")
  HuobiKlinesResult getKlines(
      @QueryParam("symbol") String symbol,
      @QueryParam("period") String period,
      @QueryParam("size") int size)
      throws IOException;

  @GET
  @Path("v1/common/symbols")
  HuobiAssetPairsResult getAssetPairs() throws IOException;

  @GET
  @Path("v1/common/currencys")
  HuobiAssetsResult getAssets() throws IOException;

  @GET
  @Path("v1/fee/fee-rate/get")
  HuobiFeeRateResult getFeeRate(
      @QueryParam("symbols") String symbols,
      @QueryParam("AccessKeyId") String apiKey,
      @QueryParam("SignatureMethod") String signatureMethod,
      @QueryParam("SignatureVersion") int signatureVersion,
      @QueryParam("Timestamp") String nonce,
      @QueryParam("Signature") ParamsDigest signature)
      throws IOException;

  @GET
  @Path("v2/reference/currencies")
  HuobiCurrenciesResult getCurrencies(
      @QueryParam("currency") String currency,
      @QueryParam("authorizedUser") boolean authorizedUser,
      @QueryParam("AccessKeyId") String apiKey,
      @QueryParam("SignatureMethod") String signatureMethod,
      @QueryParam("SignatureVersion") int signatureVersion,
      @QueryParam("Timestamp") String nonce,
      @QueryParam("Signature") ParamsDigest signature)
      throws IOException;

  @GET
  @Path("v2/reference/transact-fee-rate")
  HuobiTransactFeeRateResult getTransactFeeRate(
      @QueryParam("symbols") String symbols,
      @QueryParam("AccessKeyId") String apiKey,
      @QueryParam("SignatureMethod") String signatureMethod,
      @QueryParam("SignatureVersion") int signatureVersion,
      @QueryParam("Timestamp") String nonce,
      @QueryParam("Signature") ParamsDigest signature)
      throws IOException;

  @GET
  @Path("v1/dw/deposit-virtual/addresses")
  HuobiDepositAddressResult getDepositAddress(
      @QueryParam("currency") String currency,
      @QueryParam("AccessKeyId") String apiKey,
      @QueryParam("SignatureMethod") String signatureMethod,
      @QueryParam("SignatureVersion") int signatureVersion,
      @QueryParam("Timestamp") String nonce,
      @QueryParam("Signature") ParamsDigest signature)
      throws IOException;

  @GET
  @Path("v2/account/deposit/address")
  HuobiDepositAddressV2Result getDepositAddressV2(
      @QueryParam("currency") String currency,
      @QueryParam("AccessKeyId") String apiKey,
      @QueryParam("SignatureMethod") String signatureMethod,
      @QueryParam("SignatureVersion") int signatureVersion,
      @QueryParam("Timestamp") String nonce,
      @QueryParam("Signature") ParamsDigest signature)
      throws IOException;

  @GET
  @Path("v1/query/deposit-withdraw")
  HuobiFundingHistoryResult getFundingHistory(
      @QueryParam("currency") String currency,
      @QueryParam("type") String type,
      @QueryParam("from") String from,
      @QueryParam("size") String size,
      @QueryParam("AccessKeyId") String apiKey,
      @QueryParam("SignatureMethod") String signatureMethod,
      @QueryParam("SignatureVersion") int signatureVersion,
      @QueryParam("Timestamp") String nonce,
      @QueryParam("Signature") ParamsDigest signature)
      throws IOException;

  @GET
  @Path("v1/dw/deposit-virtual/sharedAddressWithTag")
  HuobiDepositAddressWithTagResult getDepositAddressWithTag(
      @QueryParam("currency") String currency,
      @QueryParam("AccessKeyId") String apiKey,
      @QueryParam("SignatureMethod") String signatureMethod,
      @QueryParam("SignatureVersion") int signatureVersion,
      @QueryParam("Timestamp") String nonce,
      @QueryParam("Signature") ParamsDigest signature)
      throws IOException;

  @POST
  @Path("v1/dw/withdraw/api/create")
  @Consumes(MediaType.APPLICATION_JSON)
  HuobiCreateWithdrawResult createWithdraw(
      HuobiCreateWithdrawRequest body,
      @QueryParam("AccessKeyId") String apiKey,
      @QueryParam("SignatureMethod") String signatureMethod,
      @QueryParam("SignatureVersion") int signatureVersion,
      @QueryParam("Timestamp") String nonce,
      @QueryParam("Signature") ParamsDigest signature)
      throws IOException;

  @GET
  @Path("v1/dw/withdraw-virtual/fee-range")
  @Consumes(MediaType.APPLICATION_JSON)
  HuobiWithdrawFeeRangeResult getWithdrawFeeRange(
      @QueryParam("currency") String currency,
      @QueryParam("AccessKeyId") String apiKey,
      @QueryParam("SignatureMethod") String signatureMethod,
      @QueryParam("SignatureVersion") int signatureVersion,
      @QueryParam("Timestamp") String nonce,
      @QueryParam("Signature") ParamsDigest signature)
      throws IOException;

//  @POST
//  @Path("api/v1/contract_account_info")
//  HuobiAccountResult getAccount(
//	  HuobiAccountInfoRequest request,
//      @QueryParam("AccessKeyId") String apiKey,
//      @QueryParam("SignatureMethod") String signatureMethod,
//      @QueryParam("SignatureVersion") int signatureVersion,
//      @QueryParam("Timestamp") String nonce,
//      @QueryParam("Signature") ParamsDigest signature)
//      throws IOException;

  @POST
  @Path("api/v1/contract_account_info")
  HuobiBalanceResult getBalance(
      @QueryParam("AccessKeyId") String apiKey,
      @QueryParam("SignatureMethod") String signatureMethod,
      @QueryParam("SignatureVersion") int signatureVersion,
      @QueryParam("Timestamp") String nonce,
      @QueryParam("Signature") ParamsDigest signature)
      throws IOException;

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("api/v1/contract_openorders")
  HuobiOrdersResult getOpenOrders(
	  HuobiOpenOrdersRequest request,
      @QueryParam("AccessKeyId") String apiKey,
      @QueryParam("SignatureMethod") String signatureMethod,
      @QueryParam("SignatureVersion") int signatureVersion,
      @QueryParam("Timestamp") String nonce,
      @QueryParam("Signature") ParamsDigest signature)
      throws IOException;

  @GET
  @Path("v1/order/orders")
  HuobiOrdersResult getOrders(
      @QueryParam("symbol") String symbol,
      @QueryParam("states") String states,
      @QueryParam("start-time") Long startTime,
      @QueryParam("end-time") Long endTime,
      @QueryParam("start-date") String startDate,
      @QueryParam("end-date") String endDate,
      @QueryParam("from") String from,
      @QueryParam("direct") String direct,
      @QueryParam("AccessKeyId") String apiKey,
      @QueryParam("SignatureMethod") String signatureMethod,
      @QueryParam("SignatureVersion") int signatureVersion,
      @QueryParam("Timestamp") String nonce,
      @QueryParam("Signature") ParamsDigest signature)
      throws IOException;

  @GET
  @Path("v1/order/history")
  HuobiOrdersResult getOrdersHistory(
      @QueryParam("symbol") String symbol,
      @QueryParam("start-time") Long startTime,
      @QueryParam("end-time") Long endTime,
      @QueryParam("direct") String direct,
      @QueryParam("size") Integer size,
      @QueryParam("AccessKeyId") String apiKey,
      @QueryParam("SignatureMethod") String signatureMethod,
      @QueryParam("SignatureVersion") int signatureVersion,
      @QueryParam("Timestamp") String nonce,
      @QueryParam("Signature") ParamsDigest signature)
      throws IOException;

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("api/v1/contract_order_info")
  HuobiOrderInfoResult getOrder(
      HuobiOrderByOrderIdRequest request,
      @QueryParam("AccessKeyId") String apiKey,
      @QueryParam("SignatureMethod") String signatureMethod,
      @QueryParam("SignatureVersion") int signatureVersion,
      @QueryParam("Timestamp") String nonce,
      @QueryParam("Signature") ParamsDigest signature)
      throws IOException;

  @GET
  @Path("v1/order/matchresults")
  HuobiMatchesResult getMatchResults(
      @QueryParam("symbol") String symbol,
      @QueryParam("types") String types,
      @QueryParam("start-date") String startDate,
      @QueryParam("end-date") String endDate,
      @QueryParam("from") String from,
      @QueryParam("direct") String direct,
      @QueryParam("size") Integer size,
      @QueryParam("AccessKeyId") String apiKey,
      @QueryParam("SignatureMethod") String signatureMethod,
      @QueryParam("SignatureVersion") int signatureVersion,
      @QueryParam("Timestamp") String nonce,
      @QueryParam("Signature") ParamsDigest signature)
      throws IOException;

  @POST
  @Path("v1/order/orders/{order-id}/submitcancel")
  HuobiCancelOrderResult cancelOrder(
      @PathParam("order-id") String orderID,
      @QueryParam("AccessKeyId") String apiKey,
      @QueryParam("SignatureMethod") String signatureMethod,
      @QueryParam("SignatureVersion") int signatureVersion,
      @QueryParam("Timestamp") String nonce,
      @QueryParam("Signature") ParamsDigest signature)
      throws IOException;
  
  @POST
  @Path("api/v1/order/orders/submitCancelClientOrder")
  HuobiCancelOrderResult cancelClientOrder(
	  HuobiCancelClientOrderRequest cancelRequest,
      @QueryParam("AccessKeyId") String apiKey,
      @QueryParam("SignatureMethod") String signatureMethod,
      @QueryParam("SignatureVersion") int signatureVersion,
      @QueryParam("Timestamp") String nonce,
      @QueryParam("Signature") ParamsDigest signature)
      throws IOException;

  @POST
  @Path("api/v1/contract_order")
  @Consumes(MediaType.APPLICATION_JSON)
  HuobiOrderResult placeContractOrder(
      HuobiCreateOrderRequest body,
      @QueryParam("AccessKeyId") String apiKey,
      @QueryParam("SignatureMethod") String signatureMethod,
      @QueryParam("SignatureVersion") int signatureVersion,
      @QueryParam("Timestamp") String nonce,
      @QueryParam("Signature") ParamsDigest signature)
      throws IOException;
}
