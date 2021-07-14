package com.jiuzhou.oversea.ldxy.offical.pay

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.jiuzhou.oversea.ldxy.offical.MyApp
import com.jiuzhou.oversea.ldxy.offical.util.ResourceUtils
import com.jiuzhou.oversea.ldxy.offical.util.ScreenUtils
import com.jiuzhou.oversea.ldxy.offical.util.ScreenUtils.dp2px
import kotlinx.android.synthetic.main.com_jiuzhou_overseasdk_pay_dialog.*


const val GAME_PRICE="gamePrice"
const val GAME_PRICE_CURRENCY_CODE="gamePriceCurrencyCode"
const val GAME_PRODUCT_ID="gameProductId"
const val GAME_ORDER_ID="gameOrderId"
const val ROLE_NAME="role_name"
const val SERVER="server"
const val PRODUCT_NAME="PRODUCT_NAME"
const val CALLBACK_URL="CALLBACK_URL"
const val EXTEND="EXTEND"

const val REQ_TYPE="REQ_TYPE"

class PayFragment:Fragment() {
    protected var mActivityChooseUrl: ChooseUrlPayActivity? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivityChooseUrl = context as ChooseUrlPayActivity
    }

    override fun onDetach() {
        super.onDetach()
        if (mActivityChooseUrl?.isFinishing != true) {
            mActivityChooseUrl?.finish()
        }
    }

    override fun onStart() {
        super.onStart()
        measureWidthHeight()
    }

    private fun measureWidthHeight(){
        val displayMetrics = DisplayMetrics()
        if (ScreenUtils.isLandscape()) {
            activity?.window?.setLayout(dp2px(400f), ViewGroup.LayoutParams.WRAP_CONTENT)
        } else {
            activity?.window?.setLayout(
                (ScreenUtils.getAppScreenWidth() * 0.9).toInt(),
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val contentView = inflater.inflate(
            ResourceUtils.getLayoutIdByName("com_jiuzhou_overseasdk_pay_dialog"),
            container, false
        )
        return contentView

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_google_pay.visibility=if(MyApp.sdkFloatWindiwPayStatusManager.inAppPurchase) View.VISIBLE else View.GONE
        tv_alipay.visibility=if(MyApp.sdkFloatWindiwPayStatusManager.inAppPurchase) View.GONE else View.VISIBLE
        tv_wxpay.visibility=if(MyApp.sdkFloatWindiwPayStatusManager.inAppPurchase) View.GONE else View.VISIBLE
        tv_paypal.visibility=if(MyApp.sdkFloatWindiwPayStatusManager.inAppPurchase) View.GONE else View.VISIBLE
        tv_server_name.text= arguments!!.getString(SERVER)
        tv_server_name.text= "【"+arguments!!.getString(SERVER)+"】"+ arguments!!.getString(ROLE_NAME)
        tv_pro_name.text=arguments!!.getString(PRODUCT_NAME)
        tv_price.text="¥"+arguments!!.getString(GAME_PRICE)!!

        iv.setOnClickListener {
            mActivityChooseUrl?.setResult(PAY_CODE)
            (activity as? ChooseUrlPayActivity) ?.close(this)
        }

        tv_alipay.setOnClickListener {
           mActivityChooseUrl?.apply {
               setResult(PAY_CODE, Intent().apply {
                   putExtra(PAY_CHANNNEL, ALI)
               })
               finish()
           }
        }
        tv_wxpay.setOnClickListener {
            mActivityChooseUrl?.apply {
                setResult(PAY_CODE, Intent().apply {
                    putExtra(PAY_CHANNNEL, WX)
                })
                finish()
            }
        }
        tv_paypal.setOnClickListener {
            mActivityChooseUrl?.apply {
                setResult(PAY_CODE, Intent().apply {
                    putExtra(PAY_CHANNNEL, PAYPAL)
                })
                finish()
            }
        }

    }

}
const val PAY_CODE=10001
const val ALI="ali"
const val WX="wx"
const val PAYPAL="paypal"
const val PAY_CHANNNEL="PAY WAY"

//选择了哪个 url
const val CHOOSE_CODE=10009
const val CHOOSED_ID="PAY WAY"

