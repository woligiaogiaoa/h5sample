package com.jiuzhou.oversea.ldxy.offical

import com.appsflyer.AppsFlyerLib
import com.google.firebase.messaging.FirebaseMessagingService

class MyNewFirebaseManager :FirebaseMessagingService(){
    override fun onNewToken(s: String) {
        super.onNewToken(s)

        // Sending new token to AppsFlyer
        AppsFlyerLib.getInstance().updateServerUninstallToken(getApplicationContext(), s);

        // the rest of the code that makes use of the token goes in this method as well
    }
}