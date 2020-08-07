package xlk.demo.test.tbs

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_web.*
import xlk.demo.test.R

class WebActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)
        btn_home.setOnClickListener { x5_view.loadUrl("http://debugtbs.qq.com") }
        btn_baidu.setOnClickListener { x5_view.loadUrl("http://www.baidu.com/") }
    }
}
