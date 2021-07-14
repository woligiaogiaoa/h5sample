package com.jiuzhou.oversea.ldxy.offical.web

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.jiuzhou.oversea.ldxy.offical.util.ResourceUtils
import kotlinx.android.synthetic.main.com_jiuzhou_overseasdk_web_view_activity.*

class WebViewActivity : AppCompatActivity() {

    companion object {
        const val KEY_FEEDBACK_URL = "feedback_url"
        const val SHOW_BACK_ICON = "show_back"
        fun openUrl(context: Context?, url: String,displayBack:Boolean=false) {
            context?.let {
                val intent = Intent(context, WebViewActivity::class.java)
                intent.putExtra(KEY_FEEDBACK_URL, url)
                intent.putExtra(SHOW_BACK_ICON, displayBack)
                it.startActivity(intent)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ResourceUtils.getLayoutIdByName("com_jiuzhou_overseasdk_web_view_activity"))
        val webView = findViewById<WebView>(
            ResourceUtils.getIdByName(
                "com_jiuzhou_overseasdk_web_view_activity_wv"
            )
        )
        val progressBar = findViewById<ProgressBar>(
            ResourceUtils.getIdByName(
                "com_jiuzhou_overseasdk_web_view_activity_pb"
            )
        )
        intent.getBooleanExtra(SHOW_BACK_ICON,false).also { showBack ->
            if(showBack) {
                tv_back.visibility=View.VISIBLE
                tv_back.setOnClickListener {
                    finish()
                }
            }
        }

        val feedbackUrl = intent.getStringExtra(KEY_FEEDBACK_URL)
        webView.loadUrl(feedbackUrl)
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
        ShWebView.Builder()
            .webView(webView)
            .url(feedbackUrl)
            .addLoadingView(progressBar)
            .build()
    }
}
