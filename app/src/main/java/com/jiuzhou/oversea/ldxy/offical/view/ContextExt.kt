package com.jiuzhou.oversea.ldxy.offical.view

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jiuzhou.oversea.ldxy.offical.ACCOUNT_LIST_KEY
import com.jiuzhou.oversea.ldxy.offical.MyApp
import com.jiuzhou.oversea.ldxy.offical.data.RegisterResult
import java.util.*





fun Context.saveAccount(registerResult: RegisterResult) {
    val spValue = MyApp.app.accountList
    val type = object : TypeToken<ArrayList<RegisterResult>>() {}.type
    val gson = Gson()
    var list = gson.fromJson<ArrayList<RegisterResult>>(spValue, type)
    if (list == null) {
        list = arrayListOf()
    }
    if (!list.contains(registerResult)) {
        list.add(0, registerResult)
        MyApp.app.sp.edit().putString(ACCOUNT_LIST_KEY,gson.toJson(list)).apply()
    } else {//调换顺序
        Collections.swap(list, 0, list.indexOf(registerResult))
        MyApp.app.sp.edit().putString(ACCOUNT_LIST_KEY,gson.toJson(list)).apply()
    }
}