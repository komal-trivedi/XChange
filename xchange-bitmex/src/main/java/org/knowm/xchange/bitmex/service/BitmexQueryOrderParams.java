package org.knowm.xchange.bitmex.service;

import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.service.trade.params.orders.OrderQueryParamCurrencyPair;

public class BitmexQueryOrderParams implements OrderQueryParamCurrencyPair {
  private String orderId;
  private CurrencyPair pair;

  public BitmexQueryOrderParams() {}

  public BitmexQueryOrderParams(CurrencyPair pair, String orderId) {
    this.pair = pair;
    this.orderId = orderId;
  }

  @Override
  public CurrencyPair getCurrencyPair() {
    return pair;
  }

  @Override
  public void setCurrencyPair(CurrencyPair pair) {
    this.pair = pair;
  }

  @Override
  public String getOrderId() {
    return orderId;
  }

  @Override
  public void setOrderId(String orderId) {
    this.orderId = orderId;
  }
}
