package com.jiuzhou.oversea.ldxy.offical.channel.util

import android.util.Log
import com.android.billingclient.api.SkuDetails
import com.jiuzhou.oversea.ldxy.offical.channel.bean.ProductBean
import com.jiuzhou.oversea.ldxy.offical.channel.bean.ProductIdBean
import com.jiuzhou.oversea.ldxy.offical.channel.db.DBEntity
import com.jiuzhou.oversea.ldxy.offical.channel.db.DBManager

object OrderUtils {
    private const val KEY_GAME_PRODUCT_ID_PREFIX = "game_product_id_"
    internal fun saveSkuDetailsList(skuDetailsList: MutableList<SkuDetails>) {
        val dbEntity = DBEntity<List<String>>()
        dbEntity.key = SkuDetails::class.java.simpleName
        dbEntity.data = skuDetailsList.map { skuDetails -> skuDetails.originalJson }
        DBManager.getInstance().replace(dbEntity.key, dbEntity)
    }

    internal fun getSkuDetailsList(): List<SkuDetails>? {
        val dbEntity = DBManager.getInstance().get(SkuDetails::class.java.simpleName)
        if (dbEntity != null && dbEntity.data != null && dbEntity.data is List<*>) {
            return (dbEntity.data as List<*>).map { SkuDetails(it as String) }
        }
        return null
    }

    fun saveProduct(productBean: ProductBean?) {
        productBean?.apply {
            val dbEntity = DBEntity<ProductBean>()
            dbEntity.key = this.googleProductId
            dbEntity.data = this
            val replacedDbEntity = DBManager.getInstance().replace(dbEntity.key, dbEntity)
        }
    }

    internal fun saveProduct(productBeanList: List<ProductBean>?) {
        productBeanList?.apply {
            val dbEntityList = listOf<DBEntity<ProductBean>>()
            this.forEach {
                val dbEntity = DBEntity<ProductBean>()
                dbEntity.key = it.googleProductId
                dbEntity.data = it
            }
            val replacedDbEntity = DBManager.getInstance().replace(dbEntityList)
        }
    }

    internal fun getProduct(googleProductId: String): ProductBean? {
        val dbEntity = DBManager.getInstance().get(googleProductId)
        if (dbEntity != null && dbEntity.data != null && dbEntity.data is ProductBean) {
            return dbEntity.data as ProductBean
        }
        return null
    }

    internal fun getProductList(googleProductIdList: List<String>): List<ProductBean>? {
        val resultList = DBManager.getInstance().query(
            DBEntity.KEY + "=?",
            googleProductIdList.toTypedArray()
        )
        val productBeanList = mutableListOf<ProductBean>()
        resultList.forEach {
            if (it.data is ProductBean) {
                productBeanList.add(it.data as ProductBean)
            }
        }
        return productBeanList
    }


    internal fun saveProductId(productIdBean: ProductIdBean?) {
        productIdBean?.apply {
            val dbEntity = DBEntity<ProductIdBean>()
            dbEntity.key = KEY_GAME_PRODUCT_ID_PREFIX + this.game_product_id
            dbEntity.data = this
            DBManager.getInstance().replace(dbEntity.key, dbEntity)
        }
    }

    internal fun saveProductId(productIdBeanList: List<ProductIdBean>?) {
        productIdBeanList?.apply {
            val dbEntityList = mutableListOf<DBEntity<ProductIdBean>>()
            this.forEach {
                val dbEntity = DBEntity<ProductIdBean>()
                dbEntity.key = KEY_GAME_PRODUCT_ID_PREFIX + it.game_product_id
                dbEntity.data = it
                dbEntityList.add(dbEntity)
            }
            val replacedDbEntity = DBManager.getInstance().replace(dbEntityList.toList())
        }
    }

    internal fun getProductId(gameProductId: String): ProductIdBean? {
        val dbEntity = DBManager.getInstance().get(KEY_GAME_PRODUCT_ID_PREFIX + gameProductId)
        if (dbEntity != null && dbEntity.data != null && dbEntity.data is ProductIdBean) {
            return dbEntity.data as ProductIdBean
        }
        return null
    }

    internal fun getProductIdList(gameProductIdList: List<String>): List<ProductIdBean>? {
        val keyArray = gameProductIdList.map { KEY_GAME_PRODUCT_ID_PREFIX + it }.toTypedArray()
        val resultList = DBManager.getInstance().query(
            DBEntity.KEY + "=?",
            keyArray
        )
        val productIdList = mutableListOf<ProductIdBean>()
        resultList.forEach {
            if (it.data is ProductIdBean) {
                productIdList.add(it.data as ProductIdBean)
            }
        }
        return productIdList
    }

    /*internal fun getProductIdAll(): List<ProductIdBean> {

    }*/

}
