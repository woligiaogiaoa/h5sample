package com.jiuzhou.oversea.ldxy.offical.channel.bean;

import com.android.billingclient.api.SkuDetails;

public class SkuChild {

    private String gameProductId;
    private String googleProductId;
    private String description;
    private String title;
    private String price;
    private String priceCurrencyCode;
    private long priceAmountMicro;
    private String type;

    public SkuChild(String gameProductId, SkuDetails skuDetails) {
        if (gameProductId == null || skuDetails == null) {
            return;
        }
        this.gameProductId = gameProductId;
        this.googleProductId = skuDetails.getSku();
        this.description = skuDetails.getDescription();
        this.title = skuDetails.getTitle();
        this.price = skuDetails.getPrice();
        this.priceCurrencyCode = skuDetails.getPriceCurrencyCode();
        this.priceAmountMicro = skuDetails.getPriceAmountMicros();
        this.type = skuDetails.getType();
    }

    public String getGameProductId() {
        return gameProductId;
    }

    public void setGameProductId(String gameProductId) {
        this.gameProductId = gameProductId;
    }

    public String getGoogleProductId() {
        return googleProductId;
    }

    public void setGoogleProductId(String googleProductId) {
        this.googleProductId = googleProductId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public long getPriceAmountMicro() {
        return priceAmountMicro;
    }

    public void setPriceAmountMicro(long priceAmountMicro) {
        this.priceAmountMicro = priceAmountMicro;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "SkuChild{" +
                "gameProductId='" + gameProductId + '\'' +
                ", googleProductId='" + googleProductId + '\'' +
                ", description='" + description + '\'' +
                ", title='" + title + '\'' +
                ", price='" + price + '\'' +
                ", priceCurrencyCode='" + priceCurrencyCode + '\'' +
                ", priceAmountMicro=" + priceAmountMicro +
                ", type='" + type + '\'' +
                '}';
    }
}
