package com.jiuzhou.oversea.ldxy.offical

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.jiuzhou.oversea.ldxy.offical.data.AndroidQ
import com.jiuzhou.oversea.ldxy.offical.util.SomeUtil

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.jsn.android.IntergrateTest", appContext.packageName)
    }

    @Test
    fun testHeader(){
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val deviceInfo =
                SomeUtil.getDeviceInfoJson(appContext, AndroidQ("","",""), "7cdeabc44f314c85a7d6cd1cb494f8e6")

        assertEquals("header",deviceInfo)

    }
}
