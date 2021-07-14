package com.jiuzhou.oversea.ldxy.offical.util;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MediaUtils {
    public static boolean savePictureFile(Context context, File file) {
        if (file == null) {
            return false;
        }
        Uri uri = insertFileIntoMediaStore(context, file, true);
        return saveFile(context, file, uri);
    }

    public static boolean saveVideoFile(Context context, File file) {
        if (file == null) {
            return false;
        }
        Uri uri = insertFileIntoMediaStore(context, file, false);
        return saveFile(context, file, uri);
    }

    private static boolean saveFile(Context context, File file, Uri uri) {
        if (uri == null) {
            return false;
        }
        ContentResolver contentResolver = context.getContentResolver();

        ParcelFileDescriptor parcelFileDescriptor = null;
        try {
            parcelFileDescriptor = contentResolver.openFileDescriptor(uri, "w");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (parcelFileDescriptor == null) {
            return false;
        }

        FileOutputStream outputStream = new FileOutputStream(parcelFileDescriptor.getFileDescriptor());
        FileInputStream inputStream;
        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            try {
                outputStream.close();
            } catch (IOException ex) {
            }
            return false;
        }

        try {
            copy(inputStream, outputStream);
        } catch (IOException e) {
            return false;
        } finally {
            try {
                outputStream.close();
            } catch (IOException e) {
            }
            try {
                inputStream.close();
            } catch (IOException e) {
            }
        }

        return true;
    }

    //注意当文件比较大时该方法耗时会比较大
    private static void copy(InputStream ist, OutputStream ost) throws IOException {
        byte[] buffer = new byte[4096];
        int byteCount;
        while ((byteCount = ist.read(buffer)) != -1) {
            ost.write(buffer, 0, byteCount);
        }
        ost.flush();
    }

    //创建视频或图片的URI
    private static Uri insertFileIntoMediaStore(Context context, File file, boolean isPicture) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Video.Media.DISPLAY_NAME, file.getName());
        contentValues.put(MediaStore.Video.Media.MIME_TYPE, isPicture ? "image/jpeg" : "video/mp4");
        if (Build.VERSION.SDK_INT >= 29) {
            contentValues.put(MediaStore.Video.Media.DATE_TAKEN, file.lastModified());
        }

        Uri uri = null;
        try {
            uri = context.getContentResolver().insert(
                    (isPicture ? MediaStore.Images.Media.EXTERNAL_CONTENT_URI : MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
                    , contentValues
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return uri;
    }


}
