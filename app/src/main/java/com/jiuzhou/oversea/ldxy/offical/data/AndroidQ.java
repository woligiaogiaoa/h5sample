package com.jiuzhou.oversea.ldxy.offical.data;

public class AndroidQ {

    public AndroidQ(String oaid, String vaid, String aaid) {
        this.oaid = oaid;
        this.vaid = vaid;
        this.aaid = aaid;
    }

    private String oaid;
        private String vaid;
        private String aaid;

        public String getOaid() {
            return oaid;
        }

        public void setOaid(String oaid) {
            this.oaid = oaid;
        }

        public String getVaid() {
            return vaid;
        }

        public void setVaid(String vaid) {
            this.vaid = vaid;
        }

        public String getAaid() {
            return aaid;
        }
        public void setAaid(String aaid) {
            this.aaid = aaid;
        }

}
