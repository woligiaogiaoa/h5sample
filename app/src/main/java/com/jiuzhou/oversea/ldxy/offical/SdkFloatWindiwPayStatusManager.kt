package com.jiuzhou.oversea.ldxy.offical

import com.jiuzhou.oversea.ldxy.offical.channel.FLOAT_PAY_STATUS_CONTROL
import com.jiuzhou.oversea.ldxy.offical.channel.JsonCallback
import com.jiuzhou.oversea.ldxy.offical.channel.LzyResponse
import com.jiuzhou.oversea.ldxy.offical.channel.bean.FloatPayStatusBean
import com.jiuzhou.overseasdk.utils.Logs
import com.jiuzhou.overseasdk.utils.showToast
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response

class SdkFloatWindiwPayStatusManager {

    var inAppPurchase=true//默认是内购

    fun syncStatus(){
        payStatus("status", object : JsonCallback<LzyResponse<FloatPayStatusBean.DataBean>>() {

            override fun onError(errorMsg: String?, code: Int) {
                super.onError(errorMsg, code)
                Logs.e("支付状态获取失败：" + errorMsg)
            }

            override fun onSuccess(p0: Response<LzyResponse<FloatPayStatusBean.DataBean>>?) {
                val lzy=p0?.body()
                lzy?.data?.apply {
                    val data=this
                    data?.de?.also {
                        when(it){
                            //"0","1","4"->{showFloatPop=true}
                            //else->{showFloatPop=false}
                        }
                    }
                    data?.es?.also {
                        when(it){
                            "0" ->{
                                //MyApp.app.showToastIfDebug("三方")
                                inAppPurchase=false
                            }
                            "1"->{
                                MyApp.app.showToastIfDebug("内购")
                                inAppPurchase=true
                            }
                            else->{

                            }
                        }
                    } ?:MyApp.app.showToastIfDebug("es 空")
                }
            }
        })
    }

    /*悬浮窗内购，三方支付状态控制*/
    fun payStatus(param: String?, jsonCallBack: JsonCallback<LzyResponse<FloatPayStatusBean.DataBean>>) {
        OkGo.post<LzyResponse<FloatPayStatusBean.DataBean>>(FLOAT_PAY_STATUS_CONTROL)
            .tag(FLOAT_PAY_STATUS_CONTROL)
            .params("param", param)
            .execute(jsonCallBack)
    }


}