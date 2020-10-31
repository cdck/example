package xlk.demo.test.util

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import xlk.demo.test.R

/**
 * @author by xlk
 * @date 2020/8/26 16:28
 * @desc
 * startService(new Intent(MainActivity.this, MyServer.class))
 * stopService(new Intent(MainActivity.this, MyServer.class))
 */
class MyServer : Service() {
    private var mediaPlayer: MediaPlayer? = null
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        mediaPlayer = MediaPlayer.create(this, R.raw.jzj)
        "开始播放".toast()
        mediaPlayer!!.start()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        "结束播放".toast()
        mediaPlayer!!.stop()
        mediaPlayer!!.release()
        mediaPlayer = null
    }
}