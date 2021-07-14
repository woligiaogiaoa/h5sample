package com.jiuzhou.oversea.ldxy.offical.data;

public class OrderIsPaidBean {


        private DataBean data;


        public DataBean getData() {
            return data;
        }

        public void setData(DataBean data) {
            this.data = data;
        }

        public static class DataBean {
           String is_pay;

            public String getIs_pay() {
                return is_pay;
            }

            public void setIs_pay(String is_pay) {
                this.is_pay = is_pay;
            }
        }

}
