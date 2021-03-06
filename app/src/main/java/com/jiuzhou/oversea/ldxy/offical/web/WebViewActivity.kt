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
        //???????????????????????????
        webSettings.databasePath = cacheDirPath
        //??????  Application Caches ????????????
        webSettings.setAppCacheEnabled(true) // ??????h5????????????
        webSettings.setAppCachePath(cacheDirPath)
        //?????? Application Caches ??????
        webSettings.setAppCacheEnabled(true)
        webSettings.loadsImagesAutomatically = true
        webSettings.blockNetworkImage = true // ??????????????????????????????
        webSettings.domStorageEnabled = true // ??????????????????localStorage
        webSettings.javaScriptEnabled = true // ??????javascript
        webSettings.allowFileAccess = true// ??????????????????
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
