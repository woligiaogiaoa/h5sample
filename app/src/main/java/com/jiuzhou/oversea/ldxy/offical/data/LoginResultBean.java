package com.jiuzhou.oversea.ldxy.offical.data;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.jiuzhou.overseasdk.http.bean.LoginResult;

public class LoginResultBean extends LoginResult.DataBean {

   /* private String account;
    private String slug;
    private String nick_name;
    private String tel;*/


    String auth;

    public String getAuth() {
        return auth;
    }
    public void setAuth(String auth) {
        this.auth = auth;
    }


    /*        {   account:xxxx,
                  slug:xxx,
                  info:xxxx,
                  nick_name:xxxx,
                  tel:xxxxxx,
                  auth:xxxxx}
    */
    @NonNull
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }



    public static void e(String TAG, String msg) {
        int strLength = msg.length();
        int start = 0;
        int end = 2000;
        for (int i = 0; i < 100; i++) {
            //剩下的文本还是大于规定长度则继续重复截取并输出
            if (strLength > end) {
                Log.e(TAG + i, msg.substring(start, end));
                start = end;
                end = end + 2000;
            } else {
                Log.e(TAG, msg.substring(start, strLength));
                break;
            }
        }
    }
}
