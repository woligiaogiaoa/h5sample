package com.jiuzhou.oversea.ldxy.offical.channel


const val AF_DEV_KEY="4sYZr8duSaF6TTjSQfr8DJ"

const val API_HOST_TEST = "https://api6.9wangame.com/"

const val API_HOST="https://osapi.9wangame.com/" //zhiqiand

const val HOST = "https://api.mfms.xhppgames.com/"

const val APP_INIT= HOST + "app/v1/android"


const val QUERY_ORDER_STATUS =
  HOST + "v1/google/pay/state"

const val QUERY_ORDER_CONSUME_STATUS =
    HOST + "v1/google/order/state"


const val FLOAT_PAY_STATUS_CONTROL =
    HOST + "v1/hello/world" //"v1/hello/world"

/*ali_pay*/
const val  ALIPAY: String = HOST + "v1/order/pay/ali/h5"

/*微信支付*/
const val  WXPAY: String = HOST + "v1/order/pay/wx/h5"

/*paypal支付*/
const val PAYPALPAY: String = HOST + "v1/order/pay/pal/h5"

/*sdk订单是否支付*/
const val ORDER_IS_PAID: String = HOST + "v1/order"

