package com.jiuzhou.oversea.ldxy.offical.data;

import com.jiuzhou.overseasdk.http.bean.SkuChild;

import java.util.List;

public class PayData {
    private List<SkuChild> skuList;

    public PayData(List<SkuChild> skuList) {
        this.skuList = skuList;
    }

    public List<SkuChild> getSkuList() {
        return skuList;
    }

    public void setSkuList(List<SkuChild> skuList) {
        this.skuList = skuList;
    }
}