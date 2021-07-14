package com.jiuzhou.oversea.ldxy.offical.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Environment;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 屏幕截图工具类
 *
 * @author qyl
 * 截取当前界面，并保存到指定目录
 */
public class ScreenShot {// 获取指定Activity的截屏，保存到png文件

    /**
     * 获取整个窗口的截图
     *
     * @param dialog
     * @return
     */
    @SuppressLint("NewApi")
    public static Bitmap captureScreen(Dialog dialog) {
        View cv = dialog.getWindow().getDecorView();
        cv.setDrawingCacheEnabled(true);
        cv.buildDrawingCache();
        Bitmap bmp = cv.getDrawingCache();
        if (bmp == null) {
            return null;
        }
        bmp.setHasAlpha(false);
        bmp.prepareToDraw();
        return bmp;
    }

    @SuppressLint("NewApi")
    public static Bitmap captureActivity(Activity activity) {
        View cv = activity.getWindow().getDecorView();
        cv.setDrawingCacheEnabled(true);
        cv.buildDrawingCache();
        Bitmap bmp = cv.getDrawingCache();
        if (bmp == null) {
            return null;
        }
        bmp.setHasAlpha(false);
        bmp.prepareToDraw();
        return bmp;
    }


    @SuppressLint("NewApi")
    public static Bitmap captureView(View view) {
        View cv =view;
        cv.setDrawingCacheEnabled(true);
        cv.buildDrawingCache();
        Bitmap bmp = cv.getDrawingCache();
        if (bmp == null) {
            return null;
        }
        bmp.setHasAlpha(false);
        bmp.prepareToDraw();
        return bmp;
    }

    public static boolean saveImageToGallery(Activity context, Bitmap bmp) throws IOException {
        // 首先保存图片
        File store = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File appDir = new File(store, "capture");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        FileOutputStream fos = new FileOutputStream(file);
        //通过io流的方式来压缩保存图片
        bmp.compress(Bitmap.CompressFormat.JPEG, 60, fos);
        fos.flush();
        fos.close();
        return MediaUtils.savePictureFile(context, file);
    }


}