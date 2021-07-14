package com.jiuzhou.oversea.ldxy.offical

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jiuzhou.overseasdk.OverseaSdk
import com.jiuzhou.overseasdk.http.bean.ProductIdBean
import com.jiuzhou.overseasdk.utils.showToast
import kotlinx.android.synthetic.main.activity_test_pay.*

@Deprecated("will not be used ")
class PayTestActivity :AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_pay)
        val allProductIdList: List<ProductIdBean>? = OverseaSdk.getAllProductIdList()
        if(allProductIdList.isNullOrEmpty()){
            application.showToast("empty")
        }
        bt.setOnClickListener { OverseaSdk.pay(
                this,
                "788",
                "HKD",
                 "10007", //game product id
                "123456789",
                "jinshengnan",
                "xiyou",
                "64800元宝",
                "http://api2.test.9wangame.com/test",
                "extend")
        }
    }
}

//copy hashcode equals
data class PayBean(val gamePrice:String,
                   val gamePriceCurrencyCode:String,
                   val gameProductId:String,
                   val gameOrderId:String,
                   val roleName:String,
                   val server:String,
                   val productName:String,
                   val callbackUrl:String,
                   val extend:String)