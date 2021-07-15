package com.jiuzhou.oversea.ldxy.offical.pay

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.jiuzhou.h5game.bean.H5OrderBean
import com.jiuzhou.oversea.ldxy.offical.MainActivity
import com.jiuzhou.oversea.ldxy.offical.channel.bean.AppInitBean
import com.jiuzhou.oversea.ldxy.offical.showError
import com.jiuzhou.oversea.ldxy.offical.showToastIfDebug
import com.jiuzhou.oversea.ldxy.offical.util.ResourceUtils


fun Activity.stringParams(key:String): String? =intent.getStringExtra(key)

fun Activity.booleanParams(key:String): Boolean =intent.getBooleanExtra(key,true)

val CANCELABLE_KEY="cChooseUrlPayActivity cancellable"

class ChooseUrlPayActivity:AppCompatActivity() {


    private var currentFragment: Fragment? = null
    private val fragmentMap = mutableMapOf<String, Fragment>()

    var cancelable=true

    val appInitInfo: AppInitBean?
        get() = MainActivity.appInitBean

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(
            ResourceUtils.getLayoutIdByName("com_jiuzhou_overseasdk_activity_oversea")
        )
        // fixme:handle other request
        if(!stringParams(REQ_TYPE).isNullOrEmpty() && stringParams(REQ_TYPE)!!.equals("pay",true)){
            handlePayRequest()
        }
        else if(!stringParams(REQ_TYPE).isNullOrEmpty() && stringParams(REQ_TYPE)!!.equals("choose",true)){
            //选一个gameid 返回，应用启动的时候按返回不可以 取消
                if(!booleanParams(CANCELABLE_KEY)){
                    cancelable=false
                }
            showChoosefragment()
        }
    }

    private fun showChoosefragment() {
        switchFragment(ChooseFragment().apply {
            arguments=Bundle().apply {
               // putString(ROLE_NAME, intent.getStringExtra(ROLE_NAME))
                //putString(SERVER, intent.getStringExtra(SERVER))
                //putString(PRODUCT_NAME, intent.getStringExtra(PRODUCT_NAME))
                //putString(GAME_PRICE, intent.getStringExtra(GAME_PRICE))
            }
        }).commitAllowingStateLoss()
    }

    override fun onBackPressed() {
        if(cancelable){
            super.onBackPressed()
        }
    }

    //todo :pay
    fun handlePayRequest(){
        intent.getStringExtra(ROLE_NAME) ?: showError("role name is empty").also { finish() }
        intent.getStringExtra(SERVER) ?: showError("server name is empty").also { finish() }
        intent.getStringExtra(PRODUCT_NAME) ?: showError("product name is empty").also { finish() }
        intent.getStringExtra(GAME_PRICE) ?: showError("price is empty").also { finish() }

        switchFragment(PayFragment().apply {
            arguments=Bundle().apply {
                putString(ROLE_NAME, intent.getStringExtra(ROLE_NAME))
                putString(SERVER, intent.getStringExtra(SERVER))
                putString(PRODUCT_NAME, intent.getStringExtra(PRODUCT_NAME))
                putString(GAME_PRICE, intent.getStringExtra(GAME_PRICE))
            }
        }).commitAllowingStateLoss()
    }

    private fun switchFragment(
        targetFragment: Fragment,
        bundle: Bundle? = null
    ): FragmentTransaction {
        bundle?.apply { targetFragment.arguments = this }
        val beginTransaction = supportFragmentManager.beginTransaction()
        if (targetFragment.isAdded.not()) {
            if (currentFragment != null) {
                beginTransaction.hide(currentFragment!!)
            }
            beginTransaction.add(
                ResourceUtils.getIdByName("com_jiuzhou_overseasdk_activity_oversea_container"),
                targetFragment, targetFragment::class.java.name
            )
        } else {
            beginTransaction.hide(currentFragment!!)
                .show(targetFragment)
        }
        currentFragment = targetFragment
        return beginTransaction
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (isShouldHideKeyboard(v, ev)) {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                v?.apply { imm.hideSoftInputFromWindow(
                    v.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS
                ) }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    fun close(fragment: Fragment) {
        supportFragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss()
        if (!this.isFinishing) {
            this.finish()
        }
    }

    /**
     * @param v     EditText
     * @param event 点击事件
     * @return
     */
    fun isShouldHideKeyboard(v: View?, event: MotionEvent): Boolean {
        if (v != null && v is EditText) {
            val l = intArrayOf(0, 0)
            v.getLocationInWindow(l)
            val left = l[0]
            val top = l[1]
            val bottom = top + v.getHeight()
            val right = left + v.getWidth()
            return !(event.x > left && event.x < right && event.y > top && event.y < bottom)
        }
        return false
    }

    /* if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0],
                    top = l[1],
                    bottom = top + v.getHeight(),
                    right = left + v.getWidth();
            return !(event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom);
        }
        return false;*/

}