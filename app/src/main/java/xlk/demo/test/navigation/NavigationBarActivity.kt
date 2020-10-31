package xlk.demo.test.navigation

import android.os.Bundle
import android.util.Log.i
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_navigation_bar.*
import me.majiajie.pagerbottomtabstrip.MaterialMode
import me.majiajie.pagerbottomtabstrip.listener.OnTabItemSelectedListener
import xlk.demo.test.R

class NavigationBarActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation_bar)
        val build = page_bar.material()
            .addItem(R.drawable.ic_set, "设置", getColor(R.color.color1))
            .addItem(R.drawable.ic_volume, "音量", getColor(R.color.color2))
//            .addItem(R.drawable.ic_show, "展示", testColors[2])
            .setDefaultColor(getColor(R.color.white))//未选中状态的颜色
            .setMode(MaterialMode.CHANGE_BACKGROUND_COLOR and  MaterialMode.HIDE_TEXT)//这里可以设置样式模式，总共可以组合出4种效果
            .build()
        val fs = mutableListOf<Fragment>()
        fs.add(SetFragment())
        fs.add((ShowFragment()))
        view_pager.adapter = MyPagerAdapter(supportFragmentManager, fs)
        view_pager.offscreenPageLimit = fs.size - 1
        //自动适配ViewPager页面切换
        build.setupWithViewPager(view_pager)
        build.addTabItemSelectedListener(object : OnTabItemSelectedListener {
            override fun onSelected(index: Int, old: Int) {
                i("NavigationBarActivity", "选中索引：$index,之前索引：$old")
            }

            override fun onRepeat(index: Int) {
                i("NavigationBarActivity", "onRepeat：$index")
            }
        })
    }
}
