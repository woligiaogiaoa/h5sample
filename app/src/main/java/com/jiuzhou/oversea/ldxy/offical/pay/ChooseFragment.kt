package com.jiuzhou.oversea.ldxy.offical.pay

import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.jiuzhou.oversea.ldxy.offical.MainActivity
import com.jiuzhou.oversea.ldxy.offical.R
import com.jiuzhou.oversea.ldxy.offical.channel.bean.AppInitBean
import com.jiuzhou.oversea.ldxy.offical.util.ResourceUtils
import com.jiuzhou.oversea.ldxy.offical.util.ScreenUtils

class ChooseFragment :Fragment() {


    val appInitInfo: AppInitBean?
        get() = MainActivity.appInitBean

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //没获取到配置
        appInitInfo?: mActivityChooseUrl?.close(this)
        //fixme: when user choose a game id,set result
        CHOOSE_CODE
        CHOOSED_ID

        /*  mActivityChooseUrl?.apply {
                setResult(CHOOSE_CODE, Intent().apply {
                    putExtra(CHOOSED_ID, game-id-user-choosed) //String
                })
                finish()
            }*/



    }

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
            activity?.window?.setLayout(ScreenUtils.dp2px(400f), ViewGroup.LayoutParams.WRAP_CONTENT)
        } else {
            activity?.window?.setLayout(
                (ScreenUtils.getAppScreenWidth() * 0.9).toInt(),
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val contentView = inflater.inflate(
            //fixme:create a view
            //ResourceUtils.getLayoutIdByName("com_jiuzhou_overseasdk_pay_dialog"),
            R.layout.fragment_choose_game_id,
            container, false
        )
        return contentView

    }

}