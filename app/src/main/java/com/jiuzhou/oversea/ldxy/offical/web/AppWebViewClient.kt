package com.jiuzhou.oversea.ldxy.offical.web

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.net.http.SslError

import android.util.Log
import android.view.View
import android.webkit.*
import com.jiuzhou.oversea.ldxy.offical.toActivity
import com.jiuzhou.oversea.ldxy.offical.util.ResourceUtils
import com.jiuzhou.overseasdk.utils.showToast
import java.util.regex.Pattern


class AppWebViewClient(
    private val context: Context,
    private val headers: HashMap<String, String?>?
) : WebViewClient() {
    companion object {
        private val TAG = AppWebViewClient::class.java.simpleName
    }

    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        if (url.startsWith("intent")) {
            val pattern1 = Pattern.compile("(?<=scheme=).*?(?=;)")
            val matcher1 = pattern1.matcher(url)//fb-messenger
            val pattern2 = Pattern.compile("(?<=intent).*\\d?(?=/)")
            val matcher2 = pattern2.matcher(url)//://user/112511690103448
            if (matcher1.find() && matcher2.find()) {
                val uri = matcher1.group() + matcher2.group()
                val toMessenger =
                    Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                try {
                    context.startActivity(toMessenger)
                } catch (e: ActivityNotFoundException) {
                    val matcher = Pattern.compile("(?<=package=).*?(?=;)").matcher(url)
                    if (matcher.find()) {
                        AlertDialog.Builder(context)
                            .setTitle("提示")
                            .setMessage("是否跳转到Google Play安装Messenger？")
                            .setPositiveButton(
                                android.R.string.yes
                            ) { dialog, which ->
                                 toActivity(
                                    context,
                                    matcher.group()
                                )
                            }
                            .setNegativeButton(android.R.string.no
                            ) { dialog, which ->
                                context.showToast(
                                    context.getString(
                                        ResourceUtils.getStringIdByName(
                                            "com_jiuzhou_overseasdk_send_message_need_login_first"
                                        )
                                    )
                                )
                            }
                            .show()

                    }
                }
            }
            return true
        }
        return super.shouldOverrideUrlLoading(view, url)
    }

    override fun onReceivedError(
        view: WebView,
        errorCode: Int,
        description: String,
        failingUrl: String
    ) {
        if (failingUrl.contains("http")) {
            if (description.equals("net::ERR_TIMED_OUT")) {
                view.stopLoading()
                view.reload()
                if (failingUrl.contains("https://m.me")) {
                    context.showToast(
                        context.getString(
                            ResourceUtils.getStringIdByName(
                                "com_jiuzhou_overseasdk_send_message_need_login_first"
                            )
                        )
                    )
                }
            } else {
                view.loadUrl("about:blank")
                val errorView = (view.parent as View).findViewById<View>(
                    ResourceUtils.getIdByName(
                        "com_jiuzhou_overseasdk_layout_net_error"
                    )
                )
                val retry = errorView.findViewById<View>(
                    ResourceUtils.getIdByName(
                        "com_jiuzhou_overseasdk_layout_net_error_retry_btn"
                    )
                )
                errorView.visibility = View.VISIBLE
                retry.setOnClickListener {
                    view.loadUrl(failingUrl)
                    errorView.visibility = View.GONE
                }
            }
        }
    }


}