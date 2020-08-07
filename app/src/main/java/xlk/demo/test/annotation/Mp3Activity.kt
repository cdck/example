package xlk.demo.test.annotation

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.LinearInterpolator
import kotlinx.android.synthetic.main.activity_mp3.*
import xlk.demo.test.R

class Mp3Activity : AppCompatActivity() {

    private lateinit var opticalDiskAnimator: ObjectAnimator
    private lateinit var plectrumAnimator: ObjectAnimator
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mp3)
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
