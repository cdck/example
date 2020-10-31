package xlk.demo.test

import android.app.Activity
import android.content.Intent
import android.content.Intent.ACTION_OPEN_DOCUMENT
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.hjq.permissions.OnPermission
import com.hjq.permissions.XXPermissions
import kotlinx.android.synthetic.main.activity_launch.*
import xlk.demo.test.MyApplication.Companion.successful
import xlk.demo.test.annotation.Mp3Activity
import xlk.demo.test.camera2.Camera2Activity
import xlk.demo.test.camera2.Camera2Util
import xlk.demo.test.itext.ITextActivity
import xlk.demo.test.material.design.DesignActivity
import xlk.demo.test.media.PlayActivity
import xlk.demo.test.media.VideoPlayView.strVideo
import xlk.demo.test.navigation.NavigationBarActivity
import xlk.demo.test.rxjava.RxJavaActivity
import xlk.demo.test.tbs.TbsFileActivity
import xlk.demo.test.tbs.WebActivity
import xlk.demo.test.tree.TreeListActivity
import xlk.demo.test.ui.activity.CustomUIActivity
import xlk.demo.test.ui.activity.SeatActivity
import xlk.demo.test.util.UriUtil
import xlk.demo.test.util.toast
import xlk.demo.test.wifi.WifiActivity

class LaunchActivity : AppCompatActivity() {
    private val TAG = "LaunchActivity"
    lateinit var activity: LaunchActivity
    private val data =
        arrayOf(
            "MediaCodec解码播放视频文件",
            "Wifi",
            "Camera2",
            "树形列表",
            "Tbs浏览文件",
            "X5浏览器",
            "播放动画",
            "RxJava",
            "自定义控件",
            "导航栏",
            "测试重复点击",
            "Material Design",
            "IText生成PDF",
            "座位排布"
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)
        activity = this
        applyPermissions()
        val funcAdapter = (FuncAdapter(this, data))
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = funcAdapter
        funcAdapter.setItemClickListener(object : RvItemClick {
            override fun onItemClick(position: Int, vararg obj: Any) {
                i(TAG, "点击$position")
                jump(obj[0] as String)
            }
        })
    }

    private fun applyPermissions(vararg pers: String) {
        XXPermissions.with(this)
//            .permission(pers)
            .request(object : OnPermission {
                override fun hasPermission(granted: MutableList<String>?, all: Boolean) {
                    d(TAG, "同意了${granted}权限")
                }

                override fun noPermission(denied: MutableList<String>?, quick: Boolean) {
                    e(TAG, "拒绝了${denied}权限")
                }
            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            val uri = data!!.getData();
            val path = UriUtil.getFilePath(applicationContext, uri);
            d(TAG, "将要播放的文件：$path")
            path?.let {
                strVideo = path;
                startActivity(Intent(this, PlayActivity::class.java))
            }
        }
    }

    fun jump(tag: String) {
        when (tag) {
            data[0] -> {
                val i = Intent(ACTION_OPEN_DOCUMENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setData(MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                i.setType("video/*");
                startActivityForResult(i, 1);
            }
            data[1] -> startActivity(Intent(activity, WifiActivity::class.java))
            data[2] -> {
                if (Camera2Util.isHaveCamera(activity)) {
                    startActivity(Intent(activity, Camera2Activity::class.java))
                } else {
                    "该设备没有摄像头".toast()
                }
            }
            data[3] -> startActivity(Intent(activity, TreeListActivity::class.java))
            data[4] -> {
                if (successful) {
                    startActivity(Intent(activity, TbsFileActivity::class.java))
                } else {
                    "加载X5内核失败".toast()
                }
            }
            data[5] -> startActivity(Intent(activity, WebActivity::class.java))
            data[6] -> startActivity(Intent(activity, Mp3Activity::class.java))
            data[7] -> startActivity(Intent(activity, RxJavaActivity::class.java))
            data[8] -> startActivity(Intent(activity, CustomUIActivity::class.java))
            data[9] -> startActivity(Intent(activity, NavigationBarActivity::class.java))
            data[10] -> startActivity(Intent(activity, JavaTestActivity::class.java))
            data[11] -> startActivity(Intent(activity, DesignActivity::class.java))
            data[12] -> startActivity(Intent(activity, ITextActivity::class.java))
            data[13] -> startActivity(Intent(activity, SeatActivity::class.java))
        }
    }
}
