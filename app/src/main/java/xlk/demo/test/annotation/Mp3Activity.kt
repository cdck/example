package xlk.demo.test.annotation

import xlk.demo.test.component.MyComponent
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.LinearInterpolator
import com.binioter.guideview.GuideBuilder
import kotlinx.android.synthetic.main.activity_mp3.*
import xlk.demo.test.R
import xlk.demo.test.component.LottieComponent
import xlk.demo.test.util.MyBroadCaseReceiver
import xlk.demo.test.util.MyServer
import xlk.demo.test.util.loge

class Mp3Activity : AppCompatActivity() {

    private lateinit var opticalDiskAnimator: ObjectAnimator
    private lateinit var plectrumAnimator: ObjectAnimator
    private val myBroadCaseReceiver = MyBroadCaseReceiver()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mp3)
        val intentFilter = IntentFilter()
        //监听系统按键点击广播
        intentFilter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
        //亮屏和熄屏
        intentFilter.addAction(Intent.ACTION_SCREEN_ON)
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF)
        //重新回到应用
        intentFilter.addAction("reStart")
        registerReceiver(myBroadCaseReceiver, intentFilter)
        btn_start.post {
            showGuideView2()
        }
        btn_start.performClick()
    }

    override fun onRestart() {
        super.onRestart()
        "Mp3Activity onRestart".loge()
        val intent = Intent()
        intent.setAction("reStart")
        this.sendBroadcast(intent)
    }

    override fun onDestroy() {
        "Mp3Activity onDestroy".loge()
        unregisterReceiver(myBroadCaseReceiver)
        super.onDestroy()
    }

    private fun showGuideView() {
        val builder = GuideBuilder()
        builder.setTargetView(btn_start)
            .setAlpha(150)
            .setHighTargetCorner(20)
            .setHighTargetPadding(10)
        builder.setOnVisibilityChangedListener(object : GuideBuilder.OnVisibilityChangedListener {
            override fun onShown() {
            }

            override fun onDismiss() {
            }
        })
        builder.addComponent(MyComponent())
        val guide = builder.createGuide()
        guide.show(this)
    }

    private fun showGuideView2() {
        val builder = GuideBuilder()
        builder.setTargetView(sssss)
            .setAlpha(150)
//            .setHighTargetCorner(20)
//            .setHighTargetPadding(10)
        builder.setOnVisibilityChangedListener(object : GuideBuilder.OnVisibilityChangedListener {
            override fun onShown() {
//                startService(Intent(baseContext, MyServer::class.java))
            }

            override fun onDismiss() {
//                stopService(Intent(baseContext, MyServer::class.java))
            }
        })
        builder.setOverlayTarget(true);
        builder.addComponent(LottieComponent())
        val guide = builder.createGuide()
        guide.setShouldCheckLocInWindow(false);
        guide.show(this)
    }

    fun start(view: View) {
        plectrum(0f, 30f, 1000L)
        opticalDiskAnimator = ObjectAnimator.ofFloat(three, "rotation", 0f, 360f)
        opticalDiskAnimator.duration = 3000L
        opticalDiskAnimator.repeatCount = ValueAnimator.INFINITE//无限
        opticalDiskAnimator.repeatMode = ValueAnimator.RESTART//REVERSE：反向
        opticalDiskAnimator.interpolator = LinearInterpolator()//匀速
        opticalDiskAnimator.start()
    }

    private fun plectrum(form: Float, to: Float, duration: Long) {
        plectrumAnimator = ObjectAnimator.ofFloat(plectrum, "rotation", form, to)
        //设置旋转中心点位置
        plectrum.pivotX = 1f
        plectrum.pivotY = 1f
        plectrumAnimator.duration = duration
        plectrumAnimator.start()
    }

    fun pause(view: View) {
        opticalDiskAnimator.cancel();
        plectrum(30f, 0f, 500L)
    }
}
