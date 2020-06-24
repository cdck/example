package xlk.demo.test

import android.Manifest.permission.*
import android.app.Activity
import android.content.Intent
import android.content.Intent.ACTION_OPEN_DOCUMENT
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log.d
import android.util.Log.e
import android.view.View
import com.hjq.permissions.OnPermission
import com.hjq.permissions.XXPermissions
import kotlinx.android.synthetic.main.activity_launch.*
import xlk.demo.test.camera2.Camera2Activity
import xlk.demo.test.camera2.Camera2Util
import xlk.demo.test.media.PlayActivity
import xlk.demo.test.media.VideoPlayView.strVideo
import xlk.demo.test.util.UriUtil
import xlk.demo.test.util.toast
import xlk.demo.test.wifi.WifiActivity

class LaunchActivity : AppCompatActivity(), View.OnClickListener {
    private val TAG = "LaunchActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)
        canJump()
    }

    private fun canJump() {
        button.setOnClickListener(this)
        button2.setOnClickListener(this)
        button3.setOnClickListener(this)
    }

    private fun applyPermissions(vararg pers: String) {
        XXPermissions.with(this)
            .permission(pers)
            .request(object : OnPermission {
                override fun hasPermission(granted: MutableList<String>?, all: Boolean) {
                    d(TAG, "同意了${granted}权限")
                }

                override fun noPermission(denied: MutableList<String>?, quick: Boolean) {
                    e(TAG, "拒绝了${denied}权限")
                }
            })
    }


    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.button -> {
                if (XXPermissions.isHasPermission(this, WRITE_EXTERNAL_STORAGE)) {
                    val i = Intent(ACTION_OPEN_DOCUMENT);
                    i.addCategory(Intent.CATEGORY_OPENABLE);
                    i.setData(MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                    i.setType("video/*");
                    startActivityForResult(i, 1);
                } else {
                    applyPermissions(WRITE_EXTERNAL_STORAGE)
                }
            }
            R.id.button2 -> {
                if (XXPermissions.isHasPermission(this, CHANGE_WIFI_STATE, ACCESS_WIFI_STATE,ACCESS_FINE_LOCATION)) {
                    startActivity(Intent(this, WifiActivity::class.java))
                } else {
                    applyPermissions(CHANGE_WIFI_STATE, ACCESS_WIFI_STATE,ACCESS_FINE_LOCATION)
                }
            }
            R.id.button3 -> {
                if (Camera2Util.isHaveCamera(this)) {
                    if (XXPermissions.isHasPermission(this, CAMERA)) {
                        startActivity(Intent(this, Camera2Activity::class.java))
                    } else {
                        applyPermissions(CAMERA)
                    }
                } else {
                    "该设备没有摄像头".toast()
                }
            }
        }
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

}
