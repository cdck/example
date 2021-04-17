package xlk.demo.test.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * @author by xlk
 * @date 2020/8/26 16:44
 * @desc 监听用户点击了HOME或任务键时开启后台播放音频，当用户回到应用后关闭音频
 */
class MyBroadCaseReceiver : BroadcastReceiver() {
    var canStart = true
    override fun onReceive(context: Context, intent: Intent) {
        if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS == intent.action) {
            val reason = intent.getStringExtra("reason")
            "reason=$reason".logd()
            if (reason != null) {
                if (reason == "homekey") {
                    "点击了HOME键".logd()
                } else if (reason == "recentapps") {
                    "点击了任务键".logd()
                }
                context.startService(Intent(context, MyServer::class.java))
            }
        } else if (Intent.ACTION_SCREEN_OFF == intent.action) {//屏幕息屏
            "屏幕息屏".logd()
            context.startService(Intent(context, MyServer::class.java))
        } else if (Intent.ACTION_SCREEN_ON == intent.action) {//屏幕亮屏
            "屏幕亮屏".logd()
        } else if ("reStart" == intent.action) {
            "收到重新进入会议广播".logd()
            context.stopService(Intent(context, MyServer::class.java))
        }
    }

    fun start(context: Context) {
        if (canStart) {
            context.startService(Intent(context, MyServer::class.java))
            canStart = false
        }
    }

    fun stop(context: Context) {
        if (!canStart) {
            context.stopService(Intent(context, MyServer::class.java))
            canStart = true
        }
    }
}