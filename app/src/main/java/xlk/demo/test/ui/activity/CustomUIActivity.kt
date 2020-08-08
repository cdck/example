package xlk.demo.test.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_custom_u_i.*
import xlk.demo.test.R
import xlk.demo.test.util.toast

class CustomUIActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_u_i)
        custom_menu.setListener {
            "$it".toast()
        }
    }
}
