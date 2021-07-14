package com.jiuzhou.oversea.ldxy.offical.channel.bean;

import java.io.Serializable;

public class ProductIdBean implements Serializable {
    private String game_product_id;
    private String google_product_id;

    public String getGame_product_id() {
        return game_product_id;
    }

    public void setGame_product_id(String game_product_id) {
        this.game_product_id = game_product_id;
    }

    public String getGoogle_product_id() {
        return google_product_id;
    }

    public void setGoogle_product_id(String google_product_id) {
        this.google_product_id = google_product_id;
    }

    @Override
    public String toString() {
        return "ProductIdBean{" +
                "game_product_id='" + game_product_id + '\'' +
                ", google_product_id='" + google_product_id + '\'' +
                '}';
    }
}