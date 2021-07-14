package com.jiuzhou.oversea.ldxy.offical.util;

import com.jiuzhou.oversea.ldxy.offical.channel.IllegalResponseException;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Response;

public class HandleErrorInterceptor extends ResponseBodyInterceptor {
    @Override
    Response intercept(@NotNull Response response, String url, String body) throws IOException {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(body);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (jsonObject != null) {
            if (jsonObject.optInt("code", -Integer.MAX_VALUE) != 0 &&
                    jsonObject.has("message")) {
                throw new IllegalResponseException(jsonObject.optString("message"),
                        jsonObject.optInt("code", -Integer.MAX_VALUE));
            }
        }
        return response;
    }
}
