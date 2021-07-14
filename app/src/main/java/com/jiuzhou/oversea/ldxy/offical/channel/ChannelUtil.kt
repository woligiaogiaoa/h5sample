package com.jiuzhou.oversea.ldxy.offical.channel

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.util.Log
import com.appsflyer.AFInAppEventParameterName
import com.appsflyer.AFInAppEventType
import com.appsflyer.AppsFlyerLib
import com.appsflyer.AppsFlyerLibCore.LOG_TAG
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.share.Sharer
import com.facebook.share.model.ShareLinkContent
import com.facebook.share.widget.ShareDialog

object ChannelUtil {

    lateinit var fbCallbackManager: CallbackManager
    fun init(context: Context) {
        fbCallbackManager= CallbackManager.Factory.create()
    }

    fun onRegister() {}

    fun onPay(
        contentType: String, contentName: String, contentId: String,
        contentNumber: Int, paymentChannel: String, currency: String,
        isSuccess: Boolean, amount: Int
    ) {

    }

    fun shareToFacebook(activity: Activity) {
        val facebookShareDialog = ShareDialog(activity)
        facebookShareDialog.registerCallback(fbCallbackManager,
            object : FacebookCallback<Sharer.Result?> {
                override fun onSuccess(result: Sharer.Result?) {
                    Log.d(LOG_TAG, "facebook share success")
                }

                override fun onCancel() {
                    Log.d(LOG_TAG, "facebook share cancel")
                }

                override fun onError(error: FacebookException?) {
                    Log.d(LOG_TAG, "facebook share error")
                }
            })
        if (ShareDialog.canShow(ShareLinkContent::class.java)) {
            val url = Uri.parse("test")
            val content = ShareLinkContent.Builder()
                .setContentUrl(url)
                .build()

        }
        val eventValue = hashMapOf<String, Any>()
        eventValue.put(AFInAppEventParameterName.DESCRIPTION, "app share")
        eventValue.put("platform", "Facebook")
        AppsFlyerLib.getInstance().trackEvent(activity, AFInAppEventType.SHARE, eventValue)
    }
}