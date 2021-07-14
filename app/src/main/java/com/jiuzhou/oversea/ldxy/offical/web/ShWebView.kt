package com.jiuzhou.oversea.ldxy.offical.web

import android.annotation.SuppressLint
import android.os.Build
import android.view.View
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity

@SuppressLint("JavascriptInterface")
class ShWebView(
    private var webView: WebView,
    private var url: String,
    private var headers: HashMap<String, String?>?,
    private var jsInterface: Any?,
    private var jsName: String?,
    private var loadingView: View?
) {

    private val APP_CACAHE_DIRNAME: String = "cache_web_app"

    init {
        val context = webView.context
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)
        }
        //sdk21之后CookieSyncManager被抛弃了，换成了CookieManager来进行管理。
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.getInstance().sync()//同步cookie
        } else {
            CookieManager.getInstance().flush()
        }
        webView.isScrollContainer = false
        webView.isHorizontalScrollBarEnabled = false// 水平不显示
        webView.isVerticalScrollBarEnabled = false // 垂直不显示
        webView.setBackgroundColor(0) // 设置背景色
        webView.overScrollMode = View.OVER_SCROLL_NEVER
        if (jsInterface != null && jsName != null)
            webView.addJavascriptInterface(jsInterface, jsName)
        val webSettings = webView.settings
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH)
        webSettings.cacheMode = WebSettings.LOAD_DEFAULT
        webSettings.databaseEnabled = true
        val cacheDirPath = context.filesDir.absolutePath + APP_CACAHE_DIRNAME
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
        webSettings.layoutAlgorithm = WebSettings.LayoutAlgorithm.NARROW_COLUMNS
        webView.webViewClient = AppWebViewClient(context, headers)
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                if (context is AppCompatActivity && !context.isFinishing) {
                    if (!context.isFinishing)
                        if (newProgress < 100) {
                            loadingView?.visibility = View.VISIBLE
                        } else if (newProgress == 100) {
                            loadingView?.visibility = View.GONE
                            webSettings.blockNetworkImage = false
                        }
                }

            }
        }
        if (headers != null) {
            webView.loadUrl(url, headers)
        } else {
            webView.loadUrl(url)
        }
    }

    class Builder {
        private var webView: WebView? = null
        private var url: String? = null
        private var headers: HashMap<String, String?>? = null
        private var jsInterface: Any? = null
        private var jsName: String? = null
        private var loadingView: View? = null
        fun webView(webView: WebView): Builder {
            this.webView = webView
            return this
        }

        fun url(url: String): Builder {
            this.url = url
            return this
        }

        fun addHeader(headers: HashMap<String, String?>): Builder {
            this.headers = headers
            return this
        }

        fun addHeader(key: String, value: String?): Builder {
            if (this.headers == null) {
                headers = hashMapOf()
            }
            headers!![key] = value
            return this
        }

        fun addJavaScriptInterface(jsInterface: Any, jsName: String): Builder {
            this.jsInterface = jsInterface
            this.jsName = jsName
            return this
        }

        fun addLoadingView(loadingView: View): Builder {
            this.loadingView = loadingView
            return this
        }

        fun build(): ShWebView {
            return ShWebView(webView!!, url!!, headers, jsInterface, jsName, loadingView)
        }
    }
}