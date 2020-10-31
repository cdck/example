package xlk.demo.test.material.design

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nightonke.boommenu.BoomButtons.*
import com.nightonke.boommenu.BoomMenuButton
import com.nightonke.boommenu.ButtonEnum
import com.nightonke.boommenu.OnBoomListener
import com.nightonke.boommenu.OnBoomListenerAdapter
import com.nightonke.boommenu.Piece.PiecePlaceEnum
import xlk.demo.test.FuncAdapter
import xlk.demo.test.R

class DesignActivity : AppCompatActivity() {
    private val TAG = "DesignActivity-->"
    private val data = arrayOf(
        "MediaCodec解码播放视频文件",
        "Wifi",
        "Camera2",
        "列表视图",
        "Tbs浏览文件",
        "X5浏览器",
        "播放动画",
        "RxJava",
        "自定义控件",
        "导航栏",
        "测试重复点击",
        "Material Design"
    )
    private lateinit var textArrays: Array<String>
    private val imageResources = intArrayOf(
        R.drawable.bat,
        R.drawable.bear,
        R.drawable.bee,
        R.drawable.butterfly,
        R.drawable.cat
    )
    private var rv: RecyclerView? = null
    private var bmb_simple_circle: BoomMenuButton? = null
    private var bmb_text_inside: BoomMenuButton? = null
    private var bmb_text_outside: BoomMenuButton? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_design)
        initView()
        textArrays = resources.getStringArray(R.array.bmb_text_array)
        val adapter = FuncAdapter(this, data)
        rv!!.layoutManager = LinearLayoutManager(this)
        rv!!.adapter = adapter
        initBoomMenuButton()
    }

    private fun initBoomMenuButton() {
        bmb_simple_circle!!.buttonEnum = ButtonEnum.SimpleCircle
        bmb_simple_circle!!.piecePlaceEnum = PiecePlaceEnum.DOT_5_1
        bmb_simple_circle!!.buttonPlaceEnum = ButtonPlaceEnum.SC_5_1
        for (imageResource in imageResources) {
            val builder = SimpleCircleButton.Builder()
                .listener { index -> Log.e(TAG, "onBoomButtonClick $index") }
                .normalImageRes(imageResource)
            bmb_simple_circle!!.addBuilder(builder)
        }
        bmb_simple_circle!!.onBoomListener = object : OnBoomListenerAdapter() {
            override fun onClicked(index: Int, boomButton: BoomButton) {
                Log.e(TAG, "onClicked index=$index,bmb_simple_circle")
                bmb_text_inside!!.visibility = if (index == 0) View.GONE else View.VISIBLE
            }
        }
        bmb_text_inside!!.buttonEnum = ButtonEnum.TextInsideCircle
        bmb_text_inside!!.piecePlaceEnum = PiecePlaceEnum.DOT_5_1
        bmb_text_inside!!.buttonPlaceEnum = ButtonPlaceEnum.SC_5_2
        for (i in imageResources.indices) {
            val builder = TextInsideCircleButton.Builder()
                .normalImageRes(imageResources[i])
                .normalText(textArrays[i])
            bmb_text_inside!!.addBuilder(builder)
        }
        bmb_text_inside!!.onBoomListener = object : OnBoomListenerAdapter() {
            override fun onClicked(index: Int, boomButton: BoomButton) {
                Log.i(TAG, "onClicked index=$index,bmb_text_inside")
            }
        }
        bmb_text_outside!!.buttonEnum = ButtonEnum.TextOutsideCircle
        bmb_text_outside!!.piecePlaceEnum = PiecePlaceEnum.DOT_5_1
        bmb_text_outside!!.buttonPlaceEnum = ButtonPlaceEnum.SC_5_1
        for (i in imageResources.indices) {
            val builder = TextOutsideCircleButton.Builder()
                .normalImageRes(imageResources[i])
                .normalText(textArrays[i])
            bmb_text_outside!!.addBuilder(builder)
        }
        bmb_text_outside!!.onBoomListener = object : OnBoomListener {
            override fun onClicked(index: Int, boomButton: BoomButton) {
                Log.d(TAG, "onClicked index=$index,bmb_text_outside")
            }

            override fun onBackgroundClick() {
                Log.d(TAG, "onBackgroundClick ")
            }

            override fun onBoomWillHide() {
                Log.d(TAG, "onBoomWillHide ")
            }

            override fun onBoomDidHide() {
                Log.d(TAG, "onBoomDidHide ")
            }

            override fun onBoomWillShow() {
                Log.d(TAG, "onBoomWillShow ")
            }

            override fun onBoomDidShow() {
                Log.d(TAG, "onBoomDidShow ")
            }
        }
    }

    private fun initView() {
        rv = findViewById<View>(R.id.rv) as RecyclerView
        bmb_simple_circle = findViewById<View>(R.id.bmb_simple_circle) as BoomMenuButton
        bmb_text_inside = findViewById<View>(R.id.bmb_text_inside) as BoomMenuButton
        bmb_text_outside = findViewById<View>(R.id.bmb_text_outside) as BoomMenuButton
    }
}