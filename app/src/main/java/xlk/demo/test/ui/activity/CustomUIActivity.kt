package xlk.demo.test.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log.i
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_custom_u_i.*
import xlk.demo.test.FuncAdapter
import xlk.demo.test.R
import xlk.demo.test.RvItemClick
import xlk.demo.test.util.toast

class CustomUIActivity : AppCompatActivity() {
    val TAG = "CustomUIActivity"
    private val data =
        arrayOf(
            "自定义时钟",
            "自定义菜单控件",
            "齐整菜单控件",
            "gcssloop"
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_u_i)

        val funcAdapter = (FuncAdapter(this, data))
        ui_rv.layoutManager = LinearLayoutManager(this)
        ui_rv.adapter = funcAdapter
        funcAdapter.setItemClickListener(object : RvItemClick {
            override fun onItemClick(position: Int, vararg obj: Any) {
                i(TAG, "点击$position")
                change(obj[0] as String)
            }
        })
    }

    private fun change(tag: String) {
        custom_clock.visibility = if (tag == data[0]) View.VISIBLE else View.GONE
        custom_menu.visibility = if (tag == data[1]) View.VISIBLE else View.GONE
        custom3_view.visibility = if (tag == data[2]) View.VISIBLE else View.GONE
        remote_custom_menu.visibility = if (tag == data[3]) View.VISIBLE else View.GONE
    }
}
