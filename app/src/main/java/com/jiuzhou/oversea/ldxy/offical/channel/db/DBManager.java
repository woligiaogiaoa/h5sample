package com.jiuzhou.oversea.ldxy.offical.channel.db;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.List;

public class DBManager extends BaseDao<DBEntity<?>> {

    public static DBManager getInstance() {
        return CacheManagerHolder.instance;
    }

    private static class CacheManagerHolder {
        private static final DBManager instance = new DBManager();
    }

    private DBManager() {
        super(new DBHelper());
    }

    @Override
    public DBEntity<?> parseCursorToBean(Cursor cursor) {
        return DBEntity.parseCursorToBean(cursor);
    }

    @Override
    public ContentValues getContentValues(DBEntity<?> cacheEntity) {
        return DBEntity.getContentValues(cacheEntity);
    }

    @Override
    public String getTableName() {
        return DBHelper.TABLE_SKU;
    }

    @Override
    public void unInit() {
    }

    /** 根据key获取缓存 */
    public DBEntity<?> get(String key) {
        if (key == null) return null;
        List<DBEntity<?>> cacheEntities = query(DBEntity.KEY + "=?", new String[]{key});
        return cacheEntities.size() > 0 ? cacheEntities.get(0) : null;
    }

    /** 移除一个缓存 */
    public boolean remove(String key) {
        if (key == null) return false;
        return delete(DBEntity.KEY + "=?", new String[]{key});
    }

    /** 返回带泛型的对象,注意必须确保泛型和对象对应才不会发生类型转换异常 */
    @SuppressWarnings("unchecked")
    public <T> DBEntity<T> get(String key, Class<T> clazz) {
        return (DBEntity<T>) get(key);
    }

    /** 获取所有缓存 */
    public List<DBEntity<?>> getAll() {
        return queryAll();
    }

    /**
     * 更新缓存，没有就创建，有就替换
     *
     * @param key    缓存的key
     * @param entity 需要替换的的缓存
     * @return 被替换的缓存
     */
    public <T> DBEntity<T> replace(String key, DBEntity<T> entity) {
        entity.setKey(key);
        replace(entity);
        return entity;
    }

    /** 清空缓存 */
    public boolean clear() {
        return deleteAll();
    }
}
