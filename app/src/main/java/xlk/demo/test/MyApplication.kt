package xlk.demo.test

import android.app.Application
import android.content.Context

/**
 * @author by xlk
 * @date 2020/6/8 17:29
 * @desc 说明
 */
class MyApplication : Application() {
    companion object {
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}