package xlk.demo.test.util

import android.content.Context
import android.widget.Toast
import xlk.demo.test.MyApplication.Companion.context

/**
 * @author by xlk
 * @date 2020/6/8 17:25
 * @desc kotlin扩展类
 */

fun String.toast() {
    Toast.makeText(context, this, Toast.LENGTH_SHORT).show()
}
fun String.longToast() {
    Toast.makeText(context, this, Toast.LENGTH_LONG).show()
}
