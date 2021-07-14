package com.jiuzhou.oversea.ldxy.offical

import android.webkit.JsResult
import android.webkit.WebChromeClient
import android.webkit.WebView
import com.jiuzhou.overseasdk.utils.Logs
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.resume

inline suspend fun WebView.awaitLoading(crossinline applyToProgress:(Int)->Unit):Unit= suspendCancellableCoroutine { cont->
    val pending=AtomicBoolean(false)
    webChromeClient=object :WebChromeClient(){
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
            applyToProgress(newProgress)
            if(newProgress>=100){
                if(pending.compareAndSet(false,true)) //resume exactly once
                    cont.resume(Unit)
            }
        }

        override fun onJsAlert(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
            Logs.e("fuckmessage"+message)
            return super.onJsAlert(view, url, message, result)
        }
    }
}

