package com.jiuzhou.oversea.ldxy.offical.channel.bean;

import java.io.Serializable;
import java.util.List;


public class AppInitBean implements Serializable {


    /**
     * cation : notice
     * cash : cashier/mobilepay
     * sendOd : callToPay
     * completeOd : inAppPay
     * everyDayOd : everyDayPay
     * sku : http://h5sdk.mzyh.arcticwolfgame.com/#/play?gid=
     * list : [{"nameId":"d08aba70b0644858bf8d8625dcf0bf12","name":"盟重霸主-东南亚（安卓）"},{"nameId":"aaec56a61acc4b2989c5aa57574c0669","name":"盟重霸主-欧美（安卓）"}]
     * bgImg : https://pub-xhpp.us-ws.ufileos.com/bgImg/bgimg.jpg
     * chooseMore : 1
     */

    private String cation;
    private String cash;
    private String sendOd;
    private String completeOd;
    private String everyDayOd;
    private String sku;
    private List<ListBean> list;
    private String bgImg;
    private String chooseMore;

    public String getCation() {
        return cation;
    }

    public void setCation(String cation) {
        this.cation = cation;
    }

    public String getCash() {
        return cash;
    }

    public void setCash(String cash) {
        this.cash = cash;
    }

    public String getSendOd() {
        return sendOd;
    }

    public void setSendOd(String sendOd) {
        this.sendOd = sendOd;
    }

    public String getCompleteOd() {
        return completeOd;
    }

    public void setCompleteOd(String completeOd) {
        this.completeOd = completeOd;
    }

    public String getEveryDayOd() {
        return everyDayOd;
    }

    public void setEveryDayOd(String everyDayOd) {
        this.everyDayOd = everyDayOd;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public String getBgImg() {
        return bgImg;
    }

    public void setBgImg(String bgImg) {
        this.bgImg = bgImg;
    }

    public String getChooseMore() {
        return chooseMore;
    }

    public void setChooseMore(String chooseMore) {
        this.chooseMore = chooseMore;
    }

    public static class ListBean implements Serializable {
        /**
         * nameId : d08aba70b0644858bf8d8625dcf0bf12
         * name : 盟重霸主-东南亚（安卓）
         */

        private String nameId;
        private String name;

        public String getNameId() {
            return nameId;
        }

        public void setNameId(String nameId) {
            this.nameId = nameId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
