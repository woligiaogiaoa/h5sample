package com.jiuzhou.oversea.ldxy.offical.util

import com.appsflyer.AFInAppEventType
import com.appsflyer.AppsFlyerLib
import com.jiuzhou.oversea.ldxy.offical.MyApp
import com.jiuzhou.overseasdk.OverseaSdk
import com.jiuzhou.overseasdk.utils.DeviceUtils
import com.jiuzhou.overseasdk.utils.Logs
import java.text.SimpleDateFormat
import java.util.*



const  val ACTIVATION_DATE_FORMAT="yyyy年MM月dd日 HH时mm分ss秒"

val simpleDateFormat:SimpleDateFormat=  SimpleDateFormat(ACTIVATION_DATE_FORMAT);

/*用户激活*/
const  val AF_CUSTOM_EVENT_ACTIVATION="af_custom_event_activation"

const  val AF_CUSTOM_EVENT_ACTIVATION_TIME="af_custom_event_activation_time"

const  val AF_CUSTOM_EVENT_ACTIVATION_TIMEZONE="af_custom_event_activation_timeZone"

const  val AF_CUSTOM_EVENT_ACTIVATION_ANDROID_ID="device_num" //用来上传andorid id的字段




//用户激活
internal fun activateOnlyOnce(){
    val androidID = DeviceUtils.getAndroidID(MyApp.app)

        val activationEventValue = hashMapOf<String, Any?>()
            //暂时是只上报了激活的事件
        activationEventValue.put(AF_CUSTOM_EVENT_ACTIVATION_TIME, simpleDateFormat.format(System.currentTimeMillis()))
        val default = TimeZone.getDefault()
        activationEventValue.put(AF_CUSTOM_EVENT_ACTIVATION_TIMEZONE, default.getDisplayName(false,TimeZone.SHORT))

        activationEventValue.put(AF_CUSTOM_EVENT_ACTIVATION_ANDROID_ID,androidID)

        Logs.e("激活事件上报"+activationEventValue.toString())

        AppsFlyerLib.getInstance().trackEvent(
            MyApp.app,
            AF_CUSTOM_EVENT_ACTIVATION, activationEventValue
        )

}

