//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.jiuzhou.oversea.ldxy.offical.channel.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.db.ColumnEntity;
import com.lzy.okgo.db.DBUtils;
import com.lzy.okgo.db.TableEntity;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class DBHelper extends SQLiteOpenHelper {
    private static final String DB_CACHE_NAME = "pay.db";
    private static final int DB_SKU_VERSION = 1;
    static final String TABLE_SKU = "sku";
    static final Lock lock = new ReentrantLock();
    private TableEntity skuTableEntity;

    DBHelper() {
        this(OkGo.getInstance().getContext());
    }

    DBHelper(Context context) {
        super(context, DB_CACHE_NAME, (CursorFactory)null, DB_SKU_VERSION);
        this.skuTableEntity = new TableEntity(TABLE_SKU);
        this.skuTableEntity.addColumn(new ColumnEntity("key", "VARCHAR", true, true)).addColumn(new ColumnEntity("data", "BLOB"));
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(this.skuTableEntity.buildTableString());
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (DBUtils.isNeedUpgradeTable(db, this.skuTableEntity)) {
            db.execSQL("DROP TABLE IF EXISTS cache");
        }

        this.onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        this.onUpgrade(db, oldVersion, newVersion);
    }
}
