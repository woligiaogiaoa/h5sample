package com.jiuzhou.oversea.ldxy.offical

import android.app.Application
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.preference.PreferenceManager
import android.util.Base64
import android.util.Log
import android.widget.Toast
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.appsflyer.AppsFlyerProperties
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jiuzhou.oversea.ldxy.offical.data.AndroidQ
import com.jiuzhou.oversea.ldxy.offical.data.RegisterResult
import com.jiuzhou.oversea.ldxy.offical.util.HandleErrorInterceptor
import com.jiuzhou.oversea.ldxy.offical.util.PrintIntercepter
import com.jiuzhou.oversea.ldxy.offical.util.SomeUtil.getDeviceInfoJson
import com.jiuzhou.overseasdk.utils.DeviceUtils
import com.jiuzhou.overseasdk.utils.readIniValue
import com.jiuzhou.overseasdk.utils.showToast
import com.lzy.okgo.OkGo
import com.lzy.okgo.interceptor.HttpLoggingInterceptor
import com.lzy.okgo.model.HttpHeaders
import okhttp3.OkHttpClient
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import java.util.logging.Level

class MyApp : Application() {

    companion object{
        lateinit var    app: MyApp
        val sdkFloatWindiwPayStatusManager=SdkFloatWindiwPayStatusManager()
    }

    //val logger by lazy { AppEventsLogger.newLogger(this) }



    val sp by lazy { PreferenceManager.getDefaultSharedPreferences(this) }

    val deviceInfo :String
    get() = sp.getString(DEVICE_INFO_KEY,"") ?: ""

    val activated :Boolean
        get() = sp.getBoolean(ACTIVATION_INFO_KEY,false)

    val accountList :String
        get() = sp.getString(ACCOUNT_LIST_KEY,"") ?: ""


    val printFaceBookHash =true

    //game id to get games
    val gameId by lazy {
        com.jiuzhou.oversea.ldxy.offical.readIniValue(this,"config.ini","xuehuagameid")
    }

    override fun onCreate() {
        super.onCreate()
        //OverseaSdk.sdkInitialize(this, "7cdeabc44f314c85a7d6cd1cb494f8e6")
        app=this
        if(printFaceBookHash) facebookHash() //print facebook app hash
//        FacebookSdk.setApplicationId(getString(R.string.facebook_app_id))
//        FacebookSdk.sdkInitialize(this)
        //fixme check af :用不到
        //initAppsFlyerConfig(this)
        updateDeviceInfoHeader(this, AndroidQ("","",""),gameId)
        initOkGo()
        if(!activated){
            //activateOnlyOnce()
            sp.edit().putBoolean(ACTIVATION_INFO_KEY,true).apply()
        }
        handlePaymentStrategy() //获取支付方式
        //logger.logEvent("app_init_successfully");

    }

    private fun initOkGo() {
        val builder = OkHttpClient.Builder()
        builder.addInterceptor(HandleErrorInterceptor())
        val loggingInterceptor = HttpLoggingInterceptor("HttpManagerOkGo")
        loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY)
        builder.addInterceptor(PrintIntercepter())
        loggingInterceptor.setColorLevel(Level.INFO)
        builder.addInterceptor(loggingInterceptor)
        OkGo.getInstance().init(this).setOkHttpClient(builder.build())
                .addCommonHeaders(
                        HttpHeaders(
                                "Accept",
                                "application/json"
                        )
                )
                .addCommonHeaders(
                        HttpHeaders(
                                "Authorization",
                                ""
                        )).
                addCommonHeaders(HttpHeaders(
                        "Info",
                        deviceInfo
                ))

    }

    private fun facebookHash() {

        try {
            val info = packageManager.getPackageInfo(
                packageName,
                PackageManager.GET_SIGNATURES
            )
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.d("fuckhash", Base64.encodeToString(md.digest(), Base64.DEFAULT))
            }
        } catch (e: PackageManager.NameNotFoundException) {

        } catch (e: NoSuchAlgorithmException) {

        }
    }

    private fun initAppsFlyerConfig(application: Application) {
        val conversionListener: AppsFlyerConversionListener = object : AppsFlyerConversionListener {
            override fun onAppOpenAttribution(conversionData: Map<String, String>) {}
            override fun onConversionDataSuccess(p0: MutableMap<String, Any>?) {

            }

            override fun onConversionDataFail(p0: String?) {
            }

            override fun onAttributionFailure(errorMessage: String) {
                Log.d("LOG_TAG", "error onAttributionFailure : $errorMessage")
            }
        }

        val afDevKey = readIniValue(application, "afconf.ini", "AF_DEV_KEY")
        AppsFlyerLib.getInstance().run {
            if (AppsFlyerProperties.getInstance().getString(AppsFlyerProperties.APP_USER_ID).isNullOrEmpty()) {
                setCustomerUserId(DeviceUtils.getUniqueId(application))
            }
            AppsFlyerLib.getInstance().setCollectIMEI(false)
            init(afDevKey, conversionListener, application)
            startTracking(application)
        }
    }

    //todo:支付方式获取
    private fun handlePaymentStrategy() {
        sdkFloatWindiwPayStatusManager.syncStatus()
    }



}













/*---------------------------------------------------------------------utility funs--------------*/

//update deviceHeader in sp,it is added in okgo header
fun updateDeviceInfoHeader(context: Context, androidQ: AndroidQ?,gameId:String) { //only called after onCreate
    val deviceInfo = toUnicode(
            getDeviceInfoJson(context, androidQ, gameId), "UTF-8"
    )
    MyApp.app.sp.edit().putString(DEVICE_INFO_KEY,deviceInfo).apply()

}


const val DEVICE_INFO_KEY="DEVICE_INFO_KEY"
const val ACTIVATION_INFO_KEY="ACTIVATION_INFO_KEY"
const val ACCOUNT_LIST_KEY="ACCOUNT_LIST"
const val AUTH_TOKEN_KEY="AUTH_TOKEN_KEY"


//show a toast
inline fun Application.showErrorAnd(
    e:String?,
    noinline action: ((Application)->Unit )? =null
){
      action?.invoke(this)
      e?.also { Toast.makeText(this,it,Toast.LENGTH_SHORT).show() }

}

//转unicode
fun toUnicode(originStr: String?, charset: String?): String? {
    return try {
        URLEncoder.encode(originStr, charset)
    } catch (e: UnsupportedEncodingException) {
        e.printStackTrace()
        e.localizedMessage
    }
}

//从sp获取注册的列表
fun Context.getAccountList(): ArrayList<RegisterResult>? {
    val spValue = MyApp.app.accountList
    val type = object : TypeToken<ArrayList<RegisterResult>>() {}.type
    val gson = Gson()
    val list = gson.fromJson<ArrayList<RegisterResult>>(spValue, type)
    return list
}
//跳谷歌市场
fun toActivity(context: Context, dstPackageName: String) {
    val uri = Uri.parse("https://play.google.com/store/apps/details?id=$dstPackageName")
    val intent = Intent(Intent.ACTION_VIEW, uri).apply {
        setPackage("com.android.vending")
    }
    try {
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        context.showToast(context.getString(R.string.com_jiuzhou_overseasdk_cannot_find_play_store))
    }
}

internal fun Context.showToastIfDebug(s:String){
    if(BuildConfig.DEBUG){
        Toast.makeText(this,s,Toast.LENGTH_SHORT).show()
    }
}
internal fun Context.showError(s:String){

        Toast.makeText(this,s,Toast.LENGTH_SHORT).show()

}

fun readIniValue(context: Context?, fileName: String, key: String): String {
    val properties = Properties()
    context?.apply {
        properties.load(this.assets.open(fileName))
    }
    return properties[key]?.toString() ?: ""
}
