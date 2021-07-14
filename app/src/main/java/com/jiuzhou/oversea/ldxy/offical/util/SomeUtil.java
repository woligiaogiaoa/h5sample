package com.jiuzhou.oversea.ldxy.offical.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.google.gson.Gson;
import com.jiuzhou.oversea.ldxy.offical.data.AndroidQ;
import com.jiuzhou.overseasdk.http.bean.DeviceInfo;
import com.jiuzhou.overseasdk.http.bean.GameConfig;
import com.jiuzhou.overseasdk.utils.DeviceUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import static com.jiuzhou.overseasdk.utils.DeviceUtils.NETWORK_CODE;
import static com.jiuzhou.overseasdk.utils.DeviceUtils.NETWORK_NAME;
import static com.jiuzhou.overseasdk.utils.DeviceUtils.NETWORK_TYPE;

public class SomeUtil {



    public static String getDeviceInfoJson(
            Context context,
            AndroidQ androidQ,
            String gameId
    ) {
        DeviceInfo deviceInfo = new DeviceInfo();
        GameConfig gameConfig = GameConfig.jsonToObject(context, "config.json");
        deviceInfo.setChannel_id(gameConfig.getChannel_id());
        if (TextUtils.isEmpty(gameConfig.getGame_id())) {
            deviceInfo.setGame_id(gameId);
        } else {
            deviceInfo.setGame_id(gameConfig.getGame_id());
        }
        deviceInfo.setPackage_id(gameConfig.getPackage_id());
        deviceInfo.setPlan_id(gameConfig.getPlan_id());
        deviceInfo.setSite_id(gameConfig.getSite_id());
        DeviceInfo.DeviceBean deviceBean = new DeviceInfo.DeviceBean();
        deviceBean.setOs("Android");
        DeviceInfo.DeviceBean.AndroidBean androidBean = new DeviceInfo.DeviceBean.AndroidBean();
        String imei1 = DeviceUtils.getImei1(context);
        String imei2 = DeviceUtils.getImei2(context);
        androidBean.setImei(Arrays.asList(imei1, imei2));
        androidBean.setAndroid_id(DeviceUtils.getAndroidID(context));
        androidBean.setSim_serial(Collections.singletonList(DeviceUtils.getSimIccid(context)));
        androidBean.setImsi(DeviceUtils.getImsi(context));
        androidBean.setVersion(Build.VERSION.RELEASE);
        androidBean.setBrand(Build.BRAND);
        androidBean.setModel(Build.MODEL);
        androidBean.setId(Build.ID);
        androidBean.setProduct(Build.PRODUCT);
        androidBean.setSerial(Build.SERIAL);
        androidBean.setSdk_package_name(DeviceUtils.getSdkPackageName());
        androidBean.setSdk_version(DeviceUtils.getSdkVersion());
        androidBean.setGame_package_name(DeviceUtils.getGamePackageName(context));
        androidBean.setGame_version(DeviceUtils.getGameVersion(context));
        //androidBean.setAndroid_q(androidQ);
        deviceBean.setAndroid(androidBean);
        DeviceInfo.DeviceBean.NetworkBean networkBean = new DeviceInfo.DeviceBean.NetworkBean();
        HashMap networkMap = DeviceUtils.getNetwork(context);
        String nCode = networkMap.get(NETWORK_CODE) == null ? "" : (String) networkMap.get(NETWORK_CODE);
        String nType = networkMap.get(NETWORK_TYPE) == null ? "" : (String) networkMap.get(NETWORK_TYPE);
        String nName = networkMap.get(NETWORK_NAME) == null ? "" : (String) networkMap.get(NETWORK_NAME);
        try {
            if (nCode == null) {
                networkBean.setCode(-1);
            } else {
                networkBean.setCode(Integer.valueOf(nCode));
            }
        } catch (Exception e) {
            networkBean.setCode(-1);
        }
        networkBean.setName(nName);
        networkBean.setType(nType);
        networkBean.setIntranet_ip(DeviceUtils.getInNetIp(context));
        networkBean.setMac(DeviceUtils.getMacAddress(context));
        deviceBean.setNetwork(networkBean);
        deviceInfo.setDevice(deviceBean);
        Gson gson = new Gson();
        return gson.toJson(deviceInfo);
    }

    /**
     * @param v     EditText
     * @param event 点击事件
     * @return
     */
    public static boolean isShouldHideKeyboard(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0],
                    top = l[1],
                    bottom = top + v.getHeight(),
                    right = left + v.getWidth();
            return !(event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom);
        }
        return false;
    }


    /*-------------------------------------------------*/

    public static void assistActivity (Activity activity) {
        new SomeUtil(activity);
    }

    private View mChildOfContent;
    private int usableHeightPrevious;
    private FrameLayout.LayoutParams frameLayoutParams;

    private SomeUtil(Activity activity) {
        FrameLayout content = (FrameLayout) activity.findViewById(android.R.id.content);
        mChildOfContent = content.getChildAt(0);
        mChildOfContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                possiblyResizeChildOfContent();
            }
        });
        frameLayoutParams = (FrameLayout.LayoutParams) mChildOfContent.getLayoutParams();
    }

    private void possiblyResizeChildOfContent() {
        int usableHeightNow = computeUsableHeight();
        if (usableHeightNow != usableHeightPrevious) {
            int usableHeightSansKeyboard = mChildOfContent.getRootView().getHeight();
            int heightDifference = usableHeightSansKeyboard - usableHeightNow;
            if (heightDifference > (usableHeightSansKeyboard/4)) {
                // keyboard probably just became visible
                frameLayoutParams.height = usableHeightSansKeyboard - heightDifference;
            } else {
                // keyboard probably just became hidden
                frameLayoutParams.height = usableHeightSansKeyboard;
            }
            mChildOfContent.requestLayout();
            usableHeightPrevious = usableHeightNow;
        }
    }

    private int computeUsableHeight() {
        Rect r = new Rect();
        mChildOfContent.getWindowVisibleDisplayFrame(r);
        return (r.bottom - r.top);// 全屏模式下： return r.bottom
    }

}
