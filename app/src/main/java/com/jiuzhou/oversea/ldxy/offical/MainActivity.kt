package com.jiuzhou.oversea.ldxy.offical

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.webkit.JavascriptInterface
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.widget.Toast
import androidx.annotation.MainThread
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.android.billingclient.api.*
import com.facebook.AccessToken
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginFragment
import com.facebook.login.LoginManager
import com.github.lzyzsd.jsbridge.BridgeWebView
import com.github.lzyzsd.jsbridge.BridgeWebViewClient
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.jiuzhou.h5game.bean.DaysLoginEventBean
import com.jiuzhou.h5game.bean.H5OrderBean
import com.jiuzhou.h5game.bean.OnlineTimeEventBean
import com.jiuzhou.oversea.ldxy.offical.channel.*
import com.jiuzhou.oversea.ldxy.offical.channel.bean.AppInitBean
import com.jiuzhou.oversea.ldxy.offical.channel.bean.ProductBean
import com.jiuzhou.oversea.ldxy.offical.channel.bean.ProductIdBean
import com.jiuzhou.oversea.ldxy.offical.channel.bean.SkuChild
import com.jiuzhou.oversea.ldxy.offical.channel.util.OrderUtils
import com.jiuzhou.oversea.ldxy.offical.data.*
import com.jiuzhou.oversea.ldxy.offical.data.LoginResultBean.e
import com.jiuzhou.oversea.ldxy.offical.databinding.ActivityMainBinding
import com.jiuzhou.oversea.ldxy.offical.pay.*
import com.jiuzhou.oversea.ldxy.offical.util.DoubleClickUtils
import com.jiuzhou.oversea.ldxy.offical.util.ScreenShot
import com.jiuzhou.oversea.ldxy.offical.util.ScreenShot.saveImageToGallery
import com.jiuzhou.oversea.ldxy.offical.util.SomeUtil
import com.jiuzhou.oversea.ldxy.offical.util.SomeUtil.isShouldHideKeyboard
import com.jiuzhou.oversea.ldxy.offical.view.*
import com.jiuzhou.oversea.ldxy.offical.web.WebViewActivity
import com.jiuzhou.overseasdk.OverseaSdk
import com.jiuzhou.overseasdk.http.bean.LoginResult
import com.jiuzhou.overseasdk.http.callback.OverseaPayListener
import com.jiuzhou.overseasdk.http.callback.OverseaUserListener
import com.jiuzhou.overseasdk.share.OverseaShareListener
import com.jiuzhou.overseasdk.share.ShareResult
import com.jiuzhou.overseasdk.share.ShareType
import com.jiuzhou.overseasdk.utils.Logs
import com.jiuzhou.overseasdk.utils.showToast
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.HttpHeaders
import com.lzy.okgo.model.Response
import com.lzy.okgo.request.base.Request
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import org.json.JSONObject
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

fun Boolean.toVisiBility(): Int {
    return if (this) View.VISIBLE else View.GONE
}
const val AF_DEV_KEY="4sYZr8duSaF6TTjSQfr8DJ"

class MainActivity : AppCompatActivity() {

    companion object{
        var appInitBean:AppInitBean?=null
    }

    var failTime=0

    var showed=false

    var retryInteval:Long=0

    //init enter game
    fun initFromBackEnd(){
        showProgress("Loading..")
        OkGo.get<LzyResponse<AppInitBean>>(APP_INIT)
            .tag(APP_INIT)
            .headers("Authorization", token ?: "")
            .execute(object : JsonCallback<LzyResponse<AppInitBean>>() {
                override fun onSuccess(p0: Response<LzyResponse<AppInitBean>>?) {
                    p0?.body()?.data?.also {
                        appInitBean=it
                        hideProgress()
                        ChooseUrlenterGame()
                    }
                }

                override fun onError(errorMsg: String?, code: Int) {
                    super.onError(errorMsg, code)
                    failTime++

                    if(failTime>3 && !showed){
                        showToast("请检查网络链接")
                        showed=true
                    }
                    if(retryInteval<3000){
                        retryInteval+=100
                    }
                    Handler().postDelayed({initFromBackEnd()},retryInteval)
                }
            })
    }

    val sp by lazy { PreferenceManager.getDefaultSharedPreferences(this) }

    val LAST_GAMEID="LAST_URL_game_id"

    val lastChoose: String? //进游戏才设置
        get() = sp.getString(LAST_GAMEID,"")

    //APP 启动后!!!!!    最后一次进游戏的id, null 代表还没进游戏
    var lastEnterGameId:String?=null


    fun okgoSyncHeadersWithGameId(){
        okGo.addCommonHeaders(
            HttpHeaders(
                "Info",MyApp.app.deviceInfo
            ))
    }


    //choose gameid
    private fun ChooseUrlenterGame() {

        if(!lastChoose.isNullOrEmpty() && appInitBean?.chooseMore.equals("0") ){
            //直接进游戏
            lastEnterGameId=lastChoose
            updateDeviceInfoHeader(MyApp.app,AndroidQ("","",""),lastEnterGameId!!)
            okgoSyncHeadersWithGameId()
            webView.loadUrl(url)
        }
        else{ //fixme: 点击返回不能取消的
            startActivityForResult(Intent(this, ChooseUrlPayActivity::class.java).apply {
                // put a request type
                putExtra(REQ_TYPE, "choose")
                putExtra(CANCELABLE_KEY, false)
            }, CODE_CHOOSE)
        }

    }

    val CODE_CHOOSE=10034

    val okGo
    get() = OkGo.getInstance()



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        ChannelUtil.fbCallbackManager.onActivityResult(requestCode, resultCode, data)
        if(requestCode==CODE_PAY){
            if(resultCode== PAY_CODE && data!=null){
                val payChannel = data.getStringExtra(PAY_CHANNNEL)
                when(payChannel){
                    ALI -> { //todo: maybe check here
                        aliPay(h5OrderBean)
                    }
                    WX -> {
                        wxPay(h5OrderBean)
                    }
                    PAYPAL -> {
                        paypalPay(h5OrderBean)
                    }
                    else->{}
                }
            }
        }

        if(requestCode==CODE_CHOOSE){
            if(resultCode== CHOOSE_CODE && data!=null){ //成功
                val choosedGameId = data.getStringExtra(CHOOSED_ID)
                //上次选择
                sp.edit().putString(LAST_GAMEID,choosedGameId).apply()
                updateDeviceInfoHeader(MyApp.app,AndroidQ("","",""),choosedGameId)
                okgoSyncHeadersWithGameId()
                if(lastEnterGameId!=null){
                    //用户进游戏 点击重新选择
                    if(choosedGameId.equals(lastEnterGameId)){
                        //选择了相同id
                        showToast("您已在游戏中")
                    }else{
                        //选择不用gameid
                        lastEnterGameId=choosedGameId
                        webView.reload()
                    }
                }else{
                    //应用 启动第一次进游戏
                    webView.loadUrl(url)
                    lastEnterGameId=choosedGameId
                }
            }
        }


    }

    val logger by lazy {
        AppEventsLogger.newLogger(this)
    }

    val firebaseAnalytics by lazy {
        FirebaseAnalytics.getInstance(this)
    }

    private lateinit var context: Context
    private lateinit var activity: Activity
    private lateinit var billingClient: BillingClient
    private var productIdList: List<ProductIdBean>? = null
    //private lateinit var mWebView: BridgeWebView
    private var skuDetailsList: List<SkuDetails>? = null
    private var skuChildList: List<SkuChild>? = null
    private var orderNo: String? = null

    private var isInitialized: Boolean=false

    var h5OrderBean:H5OrderBean?=null

    val job = SupervisorJob()

    val url by lazy {
        //"http://console.mfms.xhppgames.com/osh5sdk/#/play?fbid=845223162911514"
        readIniValue(this, "config.ini", "gameurl")
    }

    @ExperimentalCoroutinesApi
    val scope = CoroutineScope(job + Dispatchers.Main.immediate)
    lateinit var webView: BridgeWebView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //logger.logEvent("android_main_activity_onCreate")
        activity=this
        context=this
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        SomeUtil.assistActivity(this)
        containerPadNotch()
        webView = findViewById(R.id.webview)
        //webView.addJavascriptInterface(WebAppInterface(this), "Android")
        webView.settingsAndwhenFail({ webview_container.removeView(bt_login) })
        //setCallback()
        ChannelUtil.init(this)
        iniBilling()
        interactWithWebView()
        //OverseaSdk.onCreate(this)//,初始化facebook，初始化appsflyer,创建好billingclient ,loadurl之前
        awaitWebviewLoaing()//监听webviewloading的进度
        tv_logo.alpha = 0.0f
        tv_logo.animate().setDuration(1500).alpha(1.0f).start()
        //bt_login.setOnClickListener { OverseaSdk.login(this) }
        //tv_login_hint.setSafeListener {OverseaSdk.login(this)}
        //doUrl()
        Handler().postDelayed({
            initFromBackEnd()
        }, 1500)
         //cdn 地址加上时间戳
    }

    private fun interactWithWebView() {

        //必接
        webView.registerHandler("InitializeInfo") { data, function ->
            isInitialized = true
            var deviceInfoJson = MyApp.app.deviceInfo
            deviceInfoJson =toUnicode(deviceInfoJson, "UTF-8")!!.also { Logs.e("header$it") }
            val json = JsonObject()
            json.addProperty("Header", deviceInfoJson)//设备信息
            val accountList = getAccountList()
            val accountListJsonStr = if (accountList == null) {
                "[]"
            } else {
                Gson().toJson(accountList)
            }
            json.addProperty("accountList", accountListJsonStr)
            function.onCallBack(json.toString())
            //置x5初始化的标志为true
            //App.sp.x5Initialized = true
        }

        webView.registerHandler("backService") { data,function ->
            startActivityForResult(Intent(this, ChooseUrlPayActivity::class.java).apply {
                // put a request type
                putExtra(REQ_TYPE, "choose")
                putExtra(CANCELABLE_KEY, true)
            }, CODE_CHOOSE)
        }

        //保存账号相关信息在本地持久化存储中
        webView.registerHandler("setAccount") { data, function ->
            val account = Gson().fromJson(data, RegisterResult::class.java)
            if (account != null) {
                saveAccount(account)
            }
        }

        //必接
        //注册成功之后js调Android截图方法
        //fixme：截图功能
        webView.registerHandler("takeSnapShot") { data, function ->
            val registerResult = Gson().fromJson(data, RegisterResult::class.java)

            val prompt =
                    "亲爱的玩家，您的游客账号为：${registerResult.username}，密码为${registerResult.password}\n\n" +
                            "请妥善保存账号，账号密码截图已为您保存至相册。\n"
            val accountDialog = AlertDialog.Builder(this)
                    .setTitle("温馨提示")
                    .setMessage(prompt)
                    .setNegativeButton("确定") { dialog, which ->
                        dialog.dismiss()
                    }
                    .show()
            Handler().postDelayed({
                try {
                    ScreenShot.captureScreen(accountDialog).also {
                        saveImageToGallery(activity,it)
                    }
                }catch (e: java.lang.Exception){
                    Logs.e("截屏失败：${e.message} ")
                }
            }, 500)
        }

        webView.registerHandler("facebookLogout") { s, callBackFunction ->
            LoginManager.getInstance().logOut()
        }

        //必接
        //调用获取剪切板内容的方法
        webView.registerHandler("getCopyText") { data, function ->
            val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = cm.primaryClip
            val item = clipData?.getItemAt(0)
            val content = item?.text?.toString() ?: ""
            function.onCallBack(Gson().toJson(ClipDataBean(content)))
        }


        webView.registerHandler("facebookLogin") { data, function ->
            LoginManager.getInstance().registerCallback(ChannelUtil.fbCallbackManager,
                object : FacebookCallback<com.facebook.login.LoginResult> {
                    override fun onSuccess(result: com.facebook.login.LoginResult?) {
                        result?.run {
                            notifyJsFacebookLogin(webView, this.accessToken)
                        }
                    }

                    override fun onCancel() {

                    }

                    override fun onError(error: FacebookException?) {
                        Log.e(LoginFragment::class.java.simpleName, error.toString())
                        LoginManager.getInstance()
                            .logInWithReadPermissions(
                                this@MainActivity, listOf(
                                    "public_profile",
                                    "email",
                                    "user_friends"
                                )
                            )
                    }
                })

            val accessToken = AccessToken.getCurrentAccessToken()
            val isLoggedIn = accessToken != null && !accessToken.isExpired
            if (isLoggedIn) {
                notifyJsFacebookLogin(webView, accessToken)
            } else {
                LoginManager.getInstance()
                        .logInWithReadPermissions(this, listOf("email"))
            }
        }

        webView.registerHandler("orderParams") { data, function ->

            showToastIfDebug("receive order")
            if(DoubleClickUtils.isDoubleClick()) return@registerHandler

            Logs.e("order params :$data")
            val h5OrderBean = try{
                Gson().fromJson(data, H5OrderBean::class.java)
            } catch (e: java.lang.Exception){
                MyApp.app.showToast(e.message)
                null
            }
            h5OrderBean ?: return@registerHandler
            //fixme:根据现在的 Game Id 的支付方式来支付。
            if(MyApp.sdkFloatWindiwPayStatusManager.inAppPurchase){
                googlePay(h5OrderBean)
            }else{
                //三方支付
                this.h5OrderBean=h5OrderBean
                thirdPartyPay(h5OrderBean)
            }

        }


        //getSmartList
        registerQueryProductIdsFromJs()

        webview.registerHandler("loginSuccessful"){ data, function ->
            val bean = try {
                gson.fromJson(data, LoginInfo::class.java)
            }catch (e: java.lang.Exception){null}
            bean?:return@registerHandler
            Logs.e("fucktoken:$data")
            token=bean.token
        }
        webview.registerHandler("takeCustomer"){ data, function ->
            Log.e("takeCustomer", data)
            if(data.isNullOrEmpty()) return@registerHandler
            WebViewActivity.openUrl(activity, data)
        }

        registerEventMethod()
    }

    private fun registerEventMethod() {
        webView.registerHandler("levelGet") { data, function ->
            data?.also { loglevel(it) }
        }

        webView.registerHandler("zhuanSheng") { data, function ->
            data?.also { logZhuansheng(it) }
        }
        webView.registerHandler("time") { data, function ->
            data?: return@registerHandler
            val bean = try{
                Gson().fromJson(data, OnlineTimeEventBean::class.java)
            } catch (e: java.lang.Exception){
                //MyApp.app.showToast(e.message)
                null
            }
            bean?.time?.also {
                when(it){
                    "60"->{log60Online(data)}
                    "120"->{log120Online(data)}
                    else->{}
                }
            }
        }

        webView.registerHandler("loginDays") { data, function ->
            data?: return@registerHandler
            val bean = try{
                Gson().fromJson(data, DaysLoginEventBean::class.java)
            } catch (e: java.lang.Exception){
                //MyApp.app.showToast(e.message)
                null
            }
            bean?.day?.also {
                when(it){
                    "nextday"->{logNextDay(data)}
                    "sevenday"->{log7Day(data)}
                    "fifteenday"->{log15Day(data)}
                    else->{}
                }
            }
        }
        webView.registerHandler("baoZangFind") { data, function ->
            data?.also { logXunbao(it) }
        }
        webView.registerHandler("callToPay") { data, function ->
            showToastIfDebug("calltoPay")
            Logs.e("calltopayfuck")
            data?.also { logEnterPayScreenEvent(data) }
        }

        webView.registerHandler("inAppPay") { data, function ->
            data?.also { logInAppPay(data) }
        }
        webView.registerHandler("eachBuildGameName") { data, function ->
            data?.also { logEachBuildGameName(data) }
        }

        webView.registerHandler("everyDayPay") { data, function ->
            data?.also { logEveryDayPay(data) }
        }

        webView.registerHandler("vipTo") { data, function ->
            data?.also { logVipLevel(data) }
        }
    }


    var token:String?=null

    val gson by lazy { Gson() }

    data class LoginInfo(val token: String?, val header: String?)

    private fun iniBilling() {
        billingClient = BillingClient.newBuilder(activity)
                .setListener { billingResult, purchases ->
                    when (billingResult.responseCode) {
                        BillingClient.BillingResponseCode.OK -> {
                            // will handle server verification, consumables, and updating the local cache
                            purchases?.forEach {
                                isPurchaseValid(it)
                            }
                        }
                        BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {
                            // item already owned? call queryPurchases to verify and process all such items
                            Log.d(LOG_TAG, billingResult.debugMessage)
                            queryPurchases()
                        }
                        BillingClient.BillingResponseCode.SERVICE_DISCONNECTED -> {
                            connectToPlayBillingService()
                        }
                        BillingClient.BillingResponseCode.ERROR -> {
                            activity.showToastIfDebug(billingResult.debugMessage)
                            Log.d(
                                LOG_TAG, "billingResult.responseCode->" +
                                        "${BillingClient.BillingResponseCode.ERROR}" +
                                        " 可能是没有连接VPN"
                            )
                        }
                        else -> {
                            if (billingResult.debugMessage.isNotBlank()) {
                                activity.showToast(billingResult.debugMessage)
                            }
                            Log.i(LOG_TAG, billingResult.debugMessage)
                        }
                    }
                }
                .enablePendingPurchases()
                .build()
        connectToPlayBillingService()
    }



    private fun registerQueryProductIdsFromJs() {
        webView.registerHandler("getSmartList") { s, callbackFunction ->
            try {
                val productIds = Gson().fromJson<List<ProductIdBean>>(
                    s,
                    object :
                        TypeToken<List<ProductIdBean>>() {}.type
                )
                querySkuDetailsAndQueryPurchaseByIdList(productIds)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    private fun notifyJsFacebookLogin(webView: BridgeWebView, accessToken: AccessToken?) {
        showToast("登录成功")
        val json = JSONObject()
        json.put("token", accessToken?.token)
        webView.callHandler("facebookSetLogin", json.toString()) {}
    }

    val LOG_TAG="MainActivity"

    var canChangeOrderId=true

    private fun isPurchaseValid(purchase: Purchase) { //到这说明支付成功了
        canChangeOrderId=false
        Log.d(LOG_TAG, "isPurchaseValid called")
        val productBean = OrderUtils.getProduct(purchase.skus[0])
        val activity = context as Activity
        OkGo.post<SimpleResponse>(QUERY_ORDER_STATUS)
                .params("package_name", context.packageName)
                .params("product_id", purchase.skus[0])
                .params("token", purchase.getPurchaseToken())
                .params("order_number", purchase.accountIdentifiers?.obfuscatedProfileId ?: "")
                .headers("Authorization", token ?: "")
                .execute(object : JsonCallback<SimpleResponse?>() {

                    override fun onStart(request: Request<SimpleResponse?, out Request<Any, Request<*, *>>>?) {
                        super.onStart(request)
                        showProgress("")
                    }
                    override fun onSuccess(data: Response<SimpleResponse?>?) {
                        Log.d(
                            LOG_TAG,
                            "isPurchaseValid->onCallBackSuccess->验证成功，订单状态：${data?.code()}"
                        )
                        webView.callHandler("IAPCompletion", "") {}
                        handleConsumablePurchaseAsync(purchase)
                    }

                    override fun onFinish() {
                        super.onFinish()
                        canChangeOrderId = true
                        hideProgress()
                    }
                })
    }

    private fun connectToPlayBillingService(): Boolean {
        if (!billingClient.isReady) {
            billingClient.startConnection(object : BillingClientStateListener {
                override fun onBillingServiceDisconnected() {
                    connectToPlayBillingService()
                }

                override fun onBillingSetupFinished(billingResult: BillingResult) {
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        registerQueryProductIdsFromJs()
                        //链接上的时候 有没有
                        val idsWhenConnect=productIdList?.orEmpty()
                        if(!idsWhenConnect.isNullOrEmpty()){
                            val googleProductIdList = idsWhenConnect.map { it.google_product_id }
                            querySkuDetailsAsync(googleProductIdList)
                        }
                    }
                }
            })
            return true
        }
        return false
    }

    //查询有没有未消耗的，同时查询 商品列表
    private fun querySkuDetailsAndQueryPurchaseByIdList(productIdList: List<ProductIdBean>) {
        GlobalScope.launch(Dispatchers.IO) {
            val googleProductIdList = productIdList.map { it.google_product_id }
            Log.d(LOG_TAG, "save product id list($productIdList)")
            this@MainActivity.productIdList = productIdList


            val productList = OrderUtils.getProductList(googleProductIdList)
            Logs.e("fuckdatabase:$productList")
            querySkuDetailsAsync(googleProductIdList)
            //fixme:查询商品


            withContext(Dispatchers.Main) {
                queryPurchases()
            }
        }
    }

    //fixme:支付的时候实时查询支付方式 或者 登录的时候查询

    private fun querySkuDetailsAsync(
        skuList: List<String>,
        @BillingClient.SkuType skuType: String = BillingClient.SkuType.INAPP
    ) {
        val params = SkuDetailsParams.newBuilder().setSkusList(skuList).setType(skuType).build()
        billingClient.querySkuDetailsAsync(params) { billingResult, skuDetailsList ->
            when (billingResult.responseCode) {
                BillingClient.BillingResponseCode.OK -> {
                    if (skuDetailsList.orEmpty().isNotEmpty()) {
                        this.skuDetailsList = skuDetailsList
                        GlobalScope.launch(Dispatchers.IO) {
                            //把支付数据转化成游戏想要的格式
                            val skuChildList = mutableListOf<SkuChild>()
                            skuDetailsList?.forEach {
                                val skuDetails = it
                                val currentProductIdBean = productIdList?.findLast {
                                    it.google_product_id == skuDetails.sku
                                }
                                skuChildList.add(
                                    SkuChild(
                                        currentProductIdBean?.game_product_id,
                                        skuDetails
                                    )
                                )
                            }
                            val json =
                                Gson().toJson(skuChildList).also { Logs.e(" skudetails Data:$it") }
                            //setSmartList
                            withContext(Dispatchers.Main) {
                                webView.callHandler("setSmartList", json) {}
                            }
                            this@MainActivity.skuChildList = skuChildList
                        }
                    }
                }
                else -> {
                    Log.e(LOG_TAG, "billing message:"+billingResult.debugMessage)
                    runOnUiThread {
                        showToastIfDebug("Google iap error:${billingResult.debugMessage}:${billingResult.responseCode}")
                    }

                }
            }
        }
    }

    private fun queryPurchases() {
        val result = billingClient.queryPurchases(BillingClient.SkuType.INAPP)
        result.purchasesList?: return
        if (result.purchasesList!!.size > 0) {
            AlertDialog.Builder(activity)
                    .setTitle("提示")
                    .setMessage("您有一笔付款成功的订单未发放道具，点击确定重新发放道具")
                    .setPositiveButton(activity.getString(android.R.string.yes)) { dialog, which ->
                        result.purchasesList!!.forEach {
                            if (it.purchaseState == Purchase.PurchaseState.PURCHASED) {
                                handleConsumablePurchaseAsync(it)
                            }
                        }
                    }
                    .setNegativeButton(
                        activity.getString(android.R.string.no),
                        { dialog, which -> })
                    .show()
        }
    }

    private fun handleConsumablePurchaseAsync(purchase: Purchase) {
        val productBean = OrderUtils.getProduct(purchase.skus[0])
        val jsonObject = JSONObject()
        jsonObject.put("order_id", productBean?.orderId)
        jsonObject.put("price", productBean?.price)
        jsonObject.put("price_currency_code", productBean?.priceCurrencyCode)
        val params = ConsumeParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                //.setDeveloperPayload(jsonObject.toString())
                .build()
        billingClient.consumeAsync(params) { billingResult, purchaseToken ->
            when (billingResult.responseCode) {
                BillingClient.BillingResponseCode.OK -> {
                    // Update the appropriate tables/databases to grant user the items
                    productBean!!.isConsumed = true
                    Logs.e("saveCnsumed:$productBean")
                    OrderUtils.saveProduct(productBean)
                    requestServerDeliverProduct(purchase, productBean)
                }
                else -> {
                    Log.w(LOG_TAG, billingResult.debugMessage)
                }
            }
        }
    }

    private fun requestServerDeliverProduct(purchase: Purchase, productBean: ProductBean?) {
        Log.d(LOG_TAG, "requestServerDeliverProduct called")
        OkGo.post<SimpleResponse>(QUERY_ORDER_CONSUME_STATUS)
                .params("package_name", context.getPackageName())
                .params("product_id", purchase.skus[0])
                .params("token", purchase.getPurchaseToken())
                .params("order_number", purchase.accountIdentifiers?.obfuscatedProfileId?: "")
                .headers("Authorization", token ?: "")
                .execute(object : JsonCallback<SimpleResponse?>() {
                    override fun onSuccess(p0: Response<SimpleResponse?>?) {
                        Log.d(LOG_TAG, "游戏发货成功")
                        runOnUiThread {
                            //showToast("物品已下發")
                        }
                    }

                    override fun onStart(request: Request<SimpleResponse?, out Request<Any, Request<*, *>>>?) {
                        super.onStart(request)
                        showProgress("")
                    }

                    override fun onFinish() {
                        super.onFinish()
                        hideProgress()
                    }

                })
    }


    /*---------------pay method --------------------------------*/


    val CODE_PAY=1001

    private fun thirdPartyPay(h5OrderBean: H5OrderBean) {
        startActivityForResult(Intent(this, ChooseUrlPayActivity::class.java).apply {
            putExtra(ROLE_NAME, h5OrderBean.role_name)
            putExtra(SERVER, h5OrderBean.server)
            putExtra(PRODUCT_NAME, h5OrderBean.props_name)
            putExtra(GAME_PRICE, h5OrderBean.fs_value)
            // put a request type
            putExtra(REQ_TYPE, "pay")
        }, CODE_PAY)
    }

    fun googlePay(h5OrderBean: H5OrderBean){
        if(!canChangeOrderId) return
        GlobalScope.launch(Dispatchers.IO){
            val target=skuDetailsList?.findLast { h5OrderBean.google_product_id ==it.sku}
            target?.apply {
                //除法除以一百万
                val micor:Float = priceAmountMicros.toFloat() / 1000000
                val productBean=ProductBean(
                    h5OrderBean.google_product_id,
                    this.sku,
                    h5OrderBean.fs_number,
                    micor.toString(),
                    this.priceCurrencyCode,
                    false
                ).also { Logs.e("productBean$it") }
                Logs.e("价格" + price)
                Logs.e("价格" + micor.toString())
                OrderUtils.saveProduct(productBean)
            }?: Logs.e("not found ")
        }

        if (h5OrderBean != null) {
            orderNo = h5OrderBean.fs_number
            val element =
                skuDetailsList?.find { it.sku == h5OrderBean.google_product_id }
            element?: return
            val flowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(element)
                .setObfuscatedAccountId(orderNo?:"")
                .setObfuscatedProfileId(orderNo?:"")
                .build()
            billingClient.launchBillingFlow(activity, flowParams)

            //log here
            //logEnterPayScreenEvent(h5OrderBean.toString())
        }
    }


    /*                 val game_fs_num: String,  //gameId
                       val fs_value: String,
                       val props_name: String,
                       val role_name: String,
                       val server: String,
                       val callback_url: String,
                       val google_product_id: String,
                       val extend_data: String,
                       val sign: String,
                       val fs_number: String,
                       val rate: Int)                        */

    private fun aliPay(h5OrderBean: H5OrderBean?) {
        OkGo.post<LzyResponse<AliFormBean.DataBean>>(ALIPAY)
            .tag(ALIPAY)
            .headers("Authorization", token ?: "")
            .params("number", h5OrderBean!!.fs_number)
            .execute(object : JsonCallback<LzyResponse<AliFormBean.DataBean>>() {
                override fun onSuccess(p0: Response<LzyResponse<AliFormBean.DataBean>>?) {
                    val body = p0?.body()
                    body?.data?.apply {
                        form?.also {
                            try {
                                activity?.also {
                                    TextLoadingWebViewActivity.open(it, form)
                                }
                            } catch (e: Exception) {
                                e?.message?.also { showError(it) }
                            }
                        }
                    }
                }

                override fun onError(errorMsg: String?, code: Int) {
                    super.onError(errorMsg, code)
                    errorMsg?.also { showError(it) }
                }

                override fun onStart(request: Request<LzyResponse<AliFormBean.DataBean>, out Request<Any, Request<*, *>>>?) {
                    super.onStart(request)
                    showProgress("")
                }

                override fun onFinish() {
                    super.onFinish()
                    hideProgress()
                }
            })
    }
    private fun wxPay(h5OrderBean: H5OrderBean?) {
        OkGo.post<LzyResponse<WxUrlBean.DataBean>>(WXPAY)
            .tag(WXPAY)
            .headers("Authorization", token ?: "")
            .params("number", h5OrderBean!!.fs_number)
            .execute(object : JsonCallback<LzyResponse<WxUrlBean.DataBean>>() {
                override fun onSuccess(p0: Response<LzyResponse<WxUrlBean.DataBean>>?) {
                    val data = p0?.body()
                    data?.data?.url?.also { url ->
                        activity?.also {
                            try {
                                gotoUrl(activity, url)
                            } catch (e: Exception) {
                                showError("请安装微信并检查")
                            }
                        }
                    }
                }

                override fun onError(errorMsg: String?, code: Int) {
                    errorMsg?.also { showError(it) }
                }

                override fun onStart(request: Request<LzyResponse<WxUrlBean.DataBean>, out Request<Any, Request<*, *>>>?) {
                    super.onStart(request)
                    showProgress("")
                }

                override fun onFinish() {
                    super.onFinish()
                    hideProgress()
                }
            })
    }
    private fun paypalPay(h5OrderBean: H5OrderBean?) {
        OkGo.post<LzyResponse<WxUrlBean.DataBean>>(PAYPALPAY)
            .tag(PAYPALPAY)
            .headers("Authorization", token ?: "")
            .params("number", h5OrderBean!!.fs_number)
            .execute(object : JsonCallback<LzyResponse<WxUrlBean.DataBean>>() {
                override fun onSuccess(p0: Response<LzyResponse<WxUrlBean.DataBean>>?) {
                    p0?.body()?.data?.url?.also { url ->
                        activity?.also {
                            WebViewActivity.openUrl(activity, url, true)
                            //調起支付 首充上报
                            //logPayEvent(h5OrderBean)
                        }
                    }
                }

                override fun onError(errorMsg: String?, code: Int) {
                    super.onError(errorMsg, code)
                    errorMsg?.also { showError(it) }
                }

                override fun onStart(request: Request<LzyResponse<WxUrlBean.DataBean>, out Request<Any, Request<*, *>>>?) {
                    super.onStart(request)
                    showProgress("")
                }

                override fun onFinish() {
                    super.onFinish()
                    hideProgress()
                }
            })
    }


    lateinit var launched :Job


    private fun awaitWebviewLoaing() {
         launched = scope.launch {
           /* val deferred = async {
                OverseaSdk.awaitSkuDetailsReady()
            }*/
            webView.awaitLoading { progress ->
                val pending=AtomicBoolean(true)
                Logs.e("progress:$progress")
                view_loading.visibility = (progress != 100).toVisiBility()
                //tv_pro.text="正在加载 $progress%"
                if(progress>=100){
                    //if(pending.compareAndSet(true,false)) bt_login.visibility=View.VISIBLE
                }
            } //webview加载完成
            //deferred.await()  //skudetail加载完成
             //passSkuDetialsToGame()
        }
    }

    @MainThread
    fun showProgress(message: String?) {
        MLoadingDialog.show(this, message)
    }

    @MainThread
    fun hideProgress() {
        MLoadingDialog.dismiss()
    }

    inline fun BridgeWebView.settingsAndwhenFail(crossinline doOnError: () -> Unit){
        val webView=this
        webView.settings.javaScriptEnabled = true
        webView.settings.javaScriptCanOpenWindowsAutomatically = true
        webView.settings.setDomStorageEnabled(true)  // 开启 DOM storage 功能
        webView.settings.setAppCacheMaxSize((1024 * 1024 * 8).toLong())
        val appCachePath = webView.context.applicationContext.getCacheDir().getAbsolutePath()
        webView.settings.setAppCachePath(appCachePath)
        webView.settings.setAllowFileAccess(true)    // 可以读取文件缓存
        webView.settings.setAppCacheEnabled(true)
        webViewClient=object:BridgeWebViewClient(this){
            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                super.onReceivedError(view, request, error)
                doOnError.invoke()

            }

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                return super.shouldOverrideUrlLoading(view, url)
            }

            override fun onReceivedError(
                view: WebView?,
                errorCode: Int,
                description: String?,
                failingUrl: String?
            ) {
                super.onReceivedError(view, errorCode, description, failingUrl)
                Log.e("webViewError", "errorCode:$errorCode description:$description failingUrl:$failingUrl")
                webView?.loadUrl("about:blank")
                val errorView = (view?.parent as View?)?.findViewById<View>(R.id.errorLayout)
                val retry = errorView?.findViewById<View>(R.id.retry)
                errorView?.visibility = View.VISIBLE
                retry?.setOnClickListener {
                    view?.loadUrl(failingUrl)
                    errorView.visibility = View.GONE
                }

            }

        }
    }

    /*its safe to use them */
    @SuppressLint("InlinedApi")
    private fun fullScreen() {
        content.systemUiVisibility = FLAGS_FULLSCREEN
       /* if (Build.VERSION.SDK_INT in 12..18) {
            val view = window.decorView
            view.systemUiVisibility = View.GONE
        } else if (Build.VERSION.SDK_INT >= 19) {
            val decorView = window.decorView
            val uiOptions = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_FULLSCREEN)
            decorView.systemUiVisibility = uiOptions
        }*/
    }

    fun containerPadNotch(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) { //适配刘海屏
            // Use extension method to pad "inside" view containing UI using display cutout's bounds
            webview_container.padWithDisplayCutout()
        }
    }

    var exit = false
    /*再按一次推出app*/
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!exit) {
                exit = true
                showToast("再按一次退出程序")
                Handler().postDelayed({ exit = false }, 2000)
            } else {
                finish()
                System.exit(0)
            }
            return false
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if(hasFocus) fullScreen()
    }

    override fun onDestroy() {
        super.onDestroy()
        //logout
        job.cancel()
        System.exit(0)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        showToastIfDebug("new Intent")
        Logs.e("new Intent")
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (isShouldHideKeyboard(v, ev)) {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                v?.apply { imm.hideSoftInputFromWindow(v.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS
                ) }
            }
            fullScreen()
        }
        return super.dispatchTouchEvent(ev)
    }



    /*----------------------------------------------------logger event-----------------------------------------------------*/


    val useFacebookLogger by lazy{ readIniValue(this, "config.ini", "facebook").equals("true") }

    val useFireBaselogger by lazy { readIniValue(this, "config.ini", "firebase").equals("true") }

    /*facebook logger method*/
    /*调起支付页面的用户上报*/
    fun logEnterPayScreenEvent(order: String?) {
        if(useFacebookLogger){
            val params = Bundle()
            params.putString("orderInfo", order)
            logger.logEvent("callToPay", params)
        }
        if(useFireBaselogger){
            firebaseAnalytics.logEvent("callToPay", Bundle().apply {
                putString("orderInfo", order)
            })
        }

    }



    fun logEveryDayPay(orderInfo: String){
            if(useFacebookLogger){
                val params = Bundle()
                params.putString("value", orderInfo)
                logger.logEvent("everydaypay", params)
            }
            if(useFireBaselogger){
                firebaseAnalytics.logEvent("everydaypay", Bundle().apply {
                    putString("value", orderInfo)
                })
            }
    }

    val dayKey=Calendar.getInstance().get(Calendar.YEAR).toString()+
            Calendar.getInstance().get(Calendar.MONTH).toString()+
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH).toString()

    val todayPaid
        get() =MyApp.app.sp.getBoolean(dayKey, false)

    //nextday Login
    fun logNextDay(useInfo: String? = null){
        if(useFacebookLogger){
            val params = Bundle()
            params.putString("value", useInfo ?: "")
            logger.logEvent("nextday", params)
        }
        if(useFireBaselogger){
            firebaseAnalytics.logEvent("nextday", Bundle().apply {
                putString("value", useInfo ?: "")
            })
        }

    }

    //seven days login
    fun log7Day(useInfo: String? = null){
        if(useFacebookLogger){
            val params = Bundle()
            params.putString("value", useInfo ?: "")
            logger.logEvent("sevenday", params)
        }
        if(useFireBaselogger){
            firebaseAnalytics.logEvent("sevenday", Bundle().apply {
                putString("value", useInfo ?: "")
            })
        }

    }
    //15 days login
    fun log15Day(useInfo: String? = null){
        if(useFacebookLogger){
            val params = Bundle()
            params.putString("value", useInfo ?: "")
            logger.logEvent("fifteenday", params)
        }
        if(useFireBaselogger){
            firebaseAnalytics.logEvent("fifteenday", Bundle().apply {
                putString("value", useInfo ?: "")
            })
        }

    }
    //等级到达80/100
    fun loglevel(userInfo: String? = null) {
        if (useFacebookLogger) {
            logger.logEvent("levelget",
                Bundle().apply {
                    putString("value", userInfo ?: "")
                })
        }
        if(useFireBaselogger) {
            firebaseAnalytics.logEvent("levelget", Bundle().apply {
                putString("value", userInfo ?: "")
            })
        }
    }

    //转生
    fun logZhuansheng(userInfo: String? = null) {
        if (useFacebookLogger) {
            logger.logEvent("zhuanshengget",
                Bundle().apply {
                    putString("value", userInfo ?: "")
                })
        }
        if(useFireBaselogger) {
            firebaseAnalytics.logEvent("zhuanshengget", Bundle().apply {
                putString("value", userInfo ?: "")
            })
        }
    }


    //寻宝
    fun logXunbao(userInfo: String? = null){
        if(useFacebookLogger){
            logger.logEvent("baozangfind",
                Bundle().apply {
                    putString("value", userInfo ?: "")
                })
        }
        if(useFireBaselogger){
            firebaseAnalytics.logEvent("baozangfind", Bundle().apply {
                putString("value", userInfo ?: "")
            })
        }
    }


    //vip 等级达到3/5/7/9
    fun logVipLevel(userInfo: String? = null){
        if(useFacebookLogger){
            logger.logEvent("vipto",
                Bundle().apply {
                    putString("value", userInfo ?: "")
                })
        }
        if(useFireBaselogger){
            firebaseAnalytics.logEvent("vipto", Bundle().apply {
                putString("value", userInfo ?: "")
            })
        }
    }


    //加入帮会
    fun logAddGroup(userInfo: String? = null) {
        if(useFacebookLogger){
            logger.logEvent("addgroup",
                Bundle().apply {
                    putString("value", userInfo ?: "")
                })
        }
        if(useFireBaselogger){
            firebaseAnalytics.logEvent("addgroup", Bundle().apply {
                putString("value", userInfo ?: "")
            })
        }
    }


    //单个账号创建成功
    fun logEachBuildGameName(userInfo: String) {
        if (useFacebookLogger) {
            logger.logEvent("eachbuildgamename", Bundle().apply {
                putString("value", userInfo ?: "")
            })
        }
        if(useFireBaselogger) {
            firebaseAnalytics.logEvent("eachbuildgamename", Bundle().apply {
                putString("value", userInfo ?: "")
            })
        }
    }

    fun log60Online(info: String){
        if(useFacebookLogger){
            val params = Bundle()
            params.putString("value", info)
            logger.logEvent("time60", params)
        }
        if(useFireBaselogger){
            firebaseAnalytics.logEvent("time60", Bundle().apply {
                putString("value", info)
            })
        }

    }

    fun log120Online(info: String){
        if(useFacebookLogger){
            val params = Bundle()
            params.putString("value", info)
            logger.logEvent("time120", params)
        }
        if(useFireBaselogger){
            firebaseAnalytics.logEvent("time120", Bundle().apply {
                putString("value", info)
            })
        }
    }

    fun logInAppPay(data:String){
        if(useFacebookLogger){
            val params = Bundle()
            params.putString("value", data)
            logger.logEvent("inapppay", params)
        }
        if(useFireBaselogger){
            firebaseAnalytics.logEvent("inapppay", Bundle().apply {
                putString("value", data)
            })
        }
    }






















    /*--------------------------------------------------useless code---------------------------------------------------*/

    private fun logIfFirstPay(roleNameServername: String?, number: String) {
        GlobalScope.launch {
            delay(15_000)
            OkGo.post<LzyResponse<OrderIsPaidBean.DataBean>>(ORDER_IS_PAID)
                .tag(ORDER_IS_PAID)
                .headers("Authorization", token ?: "")
                .params("number", number)
                .execute(object : JsonCallback<LzyResponse<OrderIsPaidBean.DataBean>>() {
                    override fun onSuccess(p0: Response<LzyResponse<OrderIsPaidBean.DataBean>>?) {
                        p0?.body()?.data?.is_pay?.apply {
                            if (equals("true")) {
                                roleNameServername?.apply {}
                            } else {
                                Handler().postDelayed({
                                    OkGo.post<LzyResponse<OrderIsPaidBean.DataBean>>(ORDER_IS_PAID)
                                        .tag(ORDER_IS_PAID)
                                        .headers("Authorization", token ?: "")
                                        .params("number", number)
                                        .execute(object :
                                            JsonCallback<LzyResponse<OrderIsPaidBean.DataBean>>() {
                                            override fun onSuccess(p0: Response<LzyResponse<OrderIsPaidBean.DataBean>>?) {
                                                p0?.body()?.data?.is_pay?.apply {
                                                    if (equals("true")) {
                                                        roleNameServername?.apply {
                                                        }
                                                    }
                                                }
                                            }
                                        })
                                }, 30_000)
                            }
                        }
                    }
                })
        }
    }

    private fun passSkuDetialsToGame() {
        //showToast("pass to game ")
        val skuChildList = OverseaSdk.getSkuChildList()
        if (skuChildList.isNullOrEmpty()) return
        val sortedSkuChildList = skuChildList.sortedBy { it.gameProductId.toInt() }
        val payData = PayData(sortedSkuChildList)
        val json = Gson().toJson(payData)
        webView.loadUrl("javascript:OnSkuDetailsListAvailable($json)")
        e("fuckdata", "paydata" + json)
    }

    private fun setCallback() {
        OverseaSdk.setUserListener(object : OverseaUserListener {
            override fun loginFail(errorMsg: String?) {
                application.showErrorAnd(errorMsg) {}
            }

            override fun loginSuccess(loginResult: LoginResult.DataBean) {
                passSkuDetialsToGame()
                bt_login.visibility = View.GONE.also { webview_container.removeView(bt_login) }
                //tv_login_hint.visibility=View.GONE
                val loginResultBean = getLoginResultBean(loginResult)
                var res = loginResultBean.toString()
                Logs.e("debuglogin", res)
                res = "${res}"
                webview.loadUrl("javascript:onLoginSuccess(${res});")
                Logs.e("javascript:onLoginSuccess(${res})")
            }

            override fun logout() {
                showToast("已登出")
            }
        })
        OverseaSdk.setPayListener(object : OverseaPayListener {
            override fun paySuccess() {
                showToast("道具已下发")
            }

            override fun payFail(errorMsg: String?) {
                showToast(errorMsg)
            }
        })
        OverseaSdk.setShareListener(object : OverseaShareListener {
            override fun shareSuccess(shareType: ShareType, shareResult: ShareResult) {
                showToast(shareType.toString() + "分享成功")
            }

            override fun shareCancel() {
                showToast("分享取消")
            }

            override fun shareError(error: String?) {
                showToast("分享失败($error)")
            }
        })
        OverseaSdk.onCreate(this)
    }

    fun getLoginResultBean(loginResult: LoginResult.DataBean): LoginResultBean {
        val result = LoginResultBean()
        result.auth = OverseaSdk.preferences.userAuth
        result.account = loginResult.account
        result.slug = loginResult.slug
        result.nick_name = loginResult.nick_name
        return result
    }
}


/** Instantiate the interface and set the context  */
class WebAppInterface(private val mContext: Context) {

    val gson by lazy { Gson() }
    /** Show a toast from the web page  */
    @JavascriptInterface
    fun showToast(toast: String) {
        Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show()
    }
    /*登录*/
    @JavascriptInterface
    fun login() {
        OverseaSdk.login(mContext)
    }
    /*原生log*/
    @JavascriptInterface
    fun log(debugMessage: String?) {
        Log.e("h5Log", debugMessage)
    }

    @Deprecated("it wont be called")
    @JavascriptInterface
    fun setProductIdList(productMap: String) {
        OverseaSdk.setProductIdList(productMap)
    }

    /*支付*/
    @JavascriptInterface
    fun pay(data: String) {
        val bean = gson.fromJson(data, PayBean::class.java)
        mContext as? Activity ?: return
        OverseaSdk.pay(
            mContext, bean.gamePrice, bean.gamePriceCurrencyCode,
            bean.gameProductId, bean.gameOrderId, bean.roleName, bean.server,
            bean.productName, bean.callbackUrl, bean.extend
        )
    }

    /*上传角色信息*/
    @JavascriptInterface
    fun createRole(data: String){
        Logs.e("role data:" + data)
        val fromJson = gson.fromJson(data, RoleBean::class.java)
        OverseaSdk.uploadGamePlayerInfo(fromJson.name, fromJson.server)
    }

    var last: Long =0L
    /*退出登录*/
    @JavascriptInterface
    fun logout(){
        showToast("已登出")
        val gap=System.currentTimeMillis()-last
        last=System.currentTimeMillis()
        if(gap<3000) return
        OverseaSdk.logout()
        mContext as? Activity ?: return
        val activity:Activity = mContext
        val intent = Intent(activity, MainActivity::class.java)
        /*intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                Intent.FLAG_ACTIVITY_CLEAR_TASK or
                Intent.FLAG_ACTIVITY_NEW_TASK*/
        activity.startActivity(intent)
        activity.finish()
    }
}

/*//另一个游戏地址测试用
  /*https://h5.919flying.com/#/newplay?band=%7B%22game_id%22%3A%222b37bc5d265b449bbe356fecf03ae1ab%22%7D*/*/