package com.jiuzhou.oversea.ldxy.offical.channel.bean;

public class FloatPayStatusBean {

    private DataBean data;


    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        String de;
        String nl;
        String es;
        String dk;
        String gr;

        public String getDe() {
            return de;
        }

        public void setDe(String de) {
            this.de = de;
        }

        public String getNl() {
            return nl;
        }

        public void setNl(String nl) {
            this.nl = nl;
        }

        public String getEs() {
            return es;
        }

        public void setEs(String es) {
            this.es = es;
        }

        public String getDk() {
            return dk;
        }

        public void setDk(String dk) {
            this.dk = dk;
        }

        public String getGr() {
            return gr;
        }

        public void setGr(String gr) {
            this.gr = gr;
        }
    }
}
