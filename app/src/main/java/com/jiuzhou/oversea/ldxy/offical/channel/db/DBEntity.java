package com.jiuzhou.oversea.ldxy.offical.channel.db;

import android.content.ContentValues;
import android.database.Cursor;

import com.lzy.okgo.utils.IOUtils;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public class DBEntity<T> implements Serializable {
    private static final long serialVersionUID = -4337711009801627866L;

    //表中的字段
    public static final String KEY = "key";
    public static final String DATA = "data";

    private String key;                    // 主键
    private T data;                        // 实体数据

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static <T> ContentValues getContentValues(DBEntity<T> DBEntity) {
        ContentValues values = new ContentValues();
        values.put(KEY, DBEntity.getKey());
        values.put(DATA, IOUtils.toByteArray(DBEntity.getData()));
        return values;
    }

    public static <T> DBEntity<T> parseCursorToBean(Cursor cursor) {
        DBEntity<T> DBEntity = new DBEntity<>();
        DBEntity.setKey(cursor.getString(cursor.getColumnIndex(KEY)));
        //noinspection unchecked
        DBEntity.setData((T) IOUtils.toObject(cursor.getBlob(cursor.getColumnIndex(DATA))));
        return DBEntity;
    }

    @NotNull
    @Override
    public String toString() {
        return "DBEntity{key='" + key + '\'' + //
               ", data=" + data + //
               '}';
    }
}
