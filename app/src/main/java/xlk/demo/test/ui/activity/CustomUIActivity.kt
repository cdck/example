package xlk.demo.test.ui.activity


import android.os.Bundle
import android.util.Log.i
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_custom_u_i.*
import xlk.demo.test.FuncAdapter
import xlk.demo.test.R
import xlk.demo.test.RvItemClick
import xlk.demo.test.ui.CircleMenuLayout
import xlk.demo.test.util.logi
import xlk.demo.test.util.toast


class CustomUIActivity : AppCompatActivity() {
    val TAG = "CustomUIActivity"
    private val data = arrayOf(
        "自定义时钟",
        "自定义菜单控件",
        "齐整菜单控件",
        "gcssloop"
    )
    private val mItemTexts = arrayOf(
//        "","","","","","","",""
        "呼叫服务", "加入同屏", "会议笔记", "截图批注",
        "开始投影", "开始同屏", "结束投影", "结束同屏"
    )
    private val title= arrayOf(
        "资金", "出差", "公章", "加班", "用品", "评价", "会议", "值班", "请假"
    )

    private val mItemImgs = intArrayOf(
        R.drawable.call_service_s, R.drawable.join_screen_s,
        R.drawable.meet_note_s, R.drawable.screenshot_s,
        R.drawable.start_pro_s, R.drawable.start_screen_s,
        R.drawable.stop_pro_s, R.drawable.stop_screen_s
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
        id_menulayout.setMenuItemIconsAndTexts(mItemImgs, mItemTexts)
        id_menulayout.setOnMenuItemClickListener(object : CircleMenuLayout.OnMenuItemClickListener {
            override fun itemCenterClick(view: View?) {
                i(TAG, "点击了中间控件")
            }

            override fun itemClick(view: View?, pos: Int) {
                i(TAG, "点击了$pos")
            }

        })

        custom_menu.setListener { index ->
            {
                "点击了$index".logi()
            }
        }
        circle_view.setList()
        circle_view.setOnItemSelectListener {

        }
    }

    private fun change(tag: String) {
        custom_clock.visibility = if (tag == data[0]) View.VISIBLE else View.GONE
        custom_menu.visibility = if (tag == data[1]) View.VISIBLE else View.GONE
        custom3_view.visibility = if (tag == data[2]) View.VISIBLE else View.GONE
        remote_custom_menu.visibility = if (tag == data[3]) View.VISIBLE else View.GONE
    }
}
