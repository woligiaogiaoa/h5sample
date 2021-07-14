package com.jiuzhou.oversea.ldxy.offical

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jiuzhou.oversea.ldxy.offical.data.RegisterResult
import org.junit.Test

import org.junit.Assert.*
import java.util.ArrayList

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    @Test
    fun list_is_null() {
        val spValue = ""
        val type = object : TypeToken<ArrayList<RegisterResult>>() {}.type
        val gson = Gson()
        val list = gson.fromJson<ArrayList<RegisterResult>>(spValue, type)
        assertEquals(null,list)

        val spValue1 = "[]"
        val type1 = object : TypeToken<ArrayList<RegisterResult>>() {}.type
        val gson1 = Gson()
        val list1 = gson1.fromJson<ArrayList<RegisterResult>>(spValue1, type1)
        assertEquals(emptyList<RegisterResult>(), list1)


    }
}
