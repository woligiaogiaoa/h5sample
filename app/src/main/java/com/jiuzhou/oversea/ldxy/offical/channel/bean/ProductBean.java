package com.jiuzhou.oversea.ldxy.offical.channel.bean;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.util.List;

public class ProductBean implements Serializable {
    private String gameProductId;
    private String googleProductId;
    private String orderId;
    private String price;
    private String priceCurrencyCode;
    private boolean isConsumed;

    public ProductBean(String gameProductId, String googleProductId, String orderId, String price, String priceCurrencyCode, boolean isConsumed) {
        this.gameProductId = gameProductId;
        this.googleProductId = googleProductId;
        this.orderId = orderId;
        this.price = price;
        this.priceCurrencyCode = priceCurrencyCode;
        this.isConsumed = isConsumed;
    }

    public String getGoogleProductId() {
        return googleProductId;
    }

    public void setGoogleProductId(String googleProductId) {
        this.googleProductId = googleProductId;
    }

    public String getGameProductId() {
        return gameProductId;
    }

    public void setGameProductId(String gameProductId) {
        this.gameProductId = gameProductId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPriceCurrencyCode() {
        return priceCurrencyCode;
    }

    public void setPriceCurrencyCode(String priceCurrencyCode) {
        this.priceCurrencyCode = priceCurrencyCode;
    }

    public boolean isConsumed() {
        return isConsumed;
    }

    public void setConsumed(boolean consumed) {
        isConsumed = consumed;
    }

    @Override
    public String toString() {
        return "ProductBean{" +
                "gameProductId='" + gameProductId + '\'' +
                ", googleProductId='" + googleProductId + '\'' +
                ", orderId='" + orderId + '\'' +
                ", price='" + price + '\'' +
                ", priceCurrencyCode='" + priceCurrencyCode + '\'' +
                ", isConsumed=" + isConsumed +
                '}';
    }
}
