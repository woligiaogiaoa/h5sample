package com.jiuzhou.oversea.ldxy.offical.pay

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.jiuzhou.oversea.ldxy.offical.util.ResourceUtils
import kotlinx.android.synthetic.main.com_jiuzhou_overseasdk_web_view_activity.*
import kotlinx.android.synthetic.main.com_jiuzhou_overseasdk_web_view_activity_text_loading.*


//支付宝
class TextLoadingWebViewActivity : AppCompatActivity() {
    companion object {
        const val KEY_FEEDBACK_URL = "feedback_url"
        fun open(context: Context?, text: String) {
            context?.let {
                val intent = Intent(context, TextLoadingWebViewActivity::class.java)
                intent.putExtra(KEY_FEEDBACK_URL, text)
                it.startActivity(intent)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ResourceUtils.getLayoutIdByName("com_jiuzhou_overseasdk_web_view_activity_text_loading"))
        val webView = findViewById<WebView>(
            ResourceUtils.getIdByName(
                "com_jiuzhou_overseasdk_web_view_activity_wv"
            )
        )
       /* val progressBar = findViewById<ProgressBar>(
            ResourceUtils.getIdByName(
                "com_jiuzhou_overseasdk_web_view_activity_pb"
            )
        )*/
        val content = intent.getStringExtra(KEY_FEEDBACK_URL)
        val webSettings = webView.settings
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH)
        webSettings.cacheMode = WebSettings.LOAD_DEFAULT
        webSettings.databaseEnabled = true
        val cacheDirPath = filesDir.absolutePath + "oversea_web_cache"
        //设置数据库缓存路径
        webSettings.databasePath = cacheDirPath
        //设置  Application Caches 缓存目录
        webSettings.setAppCacheEnabled(true) // 开启h5默认缓存
        webSettings.setAppCachePath(cacheDirPath)
        //开启 Application Caches 功能
        webSettings.setAppCacheEnabled(true)
        webSettings.loadsImagesAutomatically = true
        webSettings.blockNetworkImage = true // 最后做页面图片的渲染
        webSettings.domStorageEnabled = true // 设置可以使用localStorage
        webSettings.javaScriptEnabled = true // 开启javascript
        webSettings.allowFileAccess = true// 访问本地文件
        webSettings.setSupportZoom(true)
        webSettings.javaScriptCanOpenWindowsAutomatically = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            webSettings.mediaPlaybackRequiresUserGesture = false
        }
        webView.loadData(content, "text/html", "UTF-8")
        Log.e("fuckaaaa" ,content)
        tv_goback.visibility= View.VISIBLE
        tv_goback.setOnClickListener {
            finish()
        }
        webView.webViewClient=object :WebViewClient(){
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {

                // 获取上下文, H5PayDemoActivity为当前页面
                val context: Activity = this@TextLoadingWebViewActivity

                // ------  对alipays:相关的scheme处理 -------
                if (url!!.startsWith("alipays:") || url!!.startsWith("alipay")) {
                    try {
                        context.startActivity(Intent("android.intent.action.VIEW", Uri.parse(url)))
                    } catch (e: Exception) {
                        AlertDialog.Builder(context)
                            .setMessage("未检测到支付宝客户端，请安装后重试。")
                            .setPositiveButton("立即安装") { dialog, which ->
                                val alipayUrl = Uri.parse("https://d.alipay.com")
                                context.startActivity(
                                    Intent(
                                        "android.intent.action.VIEW",
                                        alipayUrl
                                    )
                                )
                            }.setNegativeButton("取消", null).show()
                    }
                    return true
                }
                // ------- 处理结束 -------

                // ------- 处理结束 -------
                if (!(url!!.startsWith("http") || url!!.startsWith("https"))) {
                    return true
                }

                view!!.loadUrl(url)
                return true
            }
        }
    }
}