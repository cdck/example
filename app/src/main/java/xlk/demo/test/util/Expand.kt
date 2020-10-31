package xlk.demo.test.util

import android.util.Log
import android.widget.Toast
import xlk.demo.test.MyApplication.Companion.mContext

/**
 * @author by xlk
 * @date 2020/6/8 17:25
 * @desc kotlin扩展类
 */

fun String.toast() {
    Toast.makeText(mContext, this, Toast.LENGTH_SHORT).show()
}

fun Int.toast() {
    Toast.makeText(mContext, mContext.resources.getString(this), Toast.LENGTH_SHORT).show()
}

fun String.longToast() {
    Toast.makeText(mContext, this, Toast.LENGTH_LONG).show()
}

fun Any.log(){
    Log.d("applog", "$this")
}
fun Any.logi(){
    Log.i("applog", "$this")
}
fun Any.loge(){
    Log.e("applog", "$this")
}
fun Any.logv(){
    Log.v("applog", "$this")
}
