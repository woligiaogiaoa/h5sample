package com.jiuzhou.oversea.ldxy.offical.data;

public class AliFormBean {
    DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        String form;

        public String getForm() {
            return form;
        }

        public void setForm(String url) {
            this.form = url;
        }
    }
}
