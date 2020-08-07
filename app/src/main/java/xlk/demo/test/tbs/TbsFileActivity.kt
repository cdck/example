package xlk.demo.test.tbs

import android.os.Bundle
import android.os.Environment
import android.util.Log.d
import android.util.Log.i
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.tencent.smtt.sdk.TbsReaderView
import kotlinx.android.synthetic.main.activity_tbs_file.*
import xlk.demo.test.R

class TbsFileActivity : AppCompatActivity(), TbsReaderView.ReaderCallback {
    val TAG = "TbsFileActivity"
    var tbsReaderView: TbsReaderView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tbs_file)
        tbsReaderView = TbsReaderView(this, this)
        tbs_root_layout.addView(
            tbsReaderView,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        val path =
            Environment.getExternalStorageDirectory().absolutePath.plus("/NETCONFIG/无纸化文件/会议议程/分区助手命令行与注意事项.doc")
        displayFile(path)
    }

    override fun onCallBackAction(p0: Int?, p1: Any?, p2: Any?) {
        d(TAG, "onCallBackAction $p0,$p1,$p2")
    }

    private fun displayFile(filepath: String) {
        val bundle = Bundle()
        bundle.putString("filePath", filepath)
        bundle.putString("tempPath", Environment.getExternalStorageDirectory().path)
        val suffix = filepath.substring(filepath.lastIndexOf(".") + 1)
        i(TAG, "打开文件 -->$filepath， 后缀： $suffix")
        try {
            val result = tbsReaderView!!.preOpen(suffix, false)
            i(TAG, "打开文件 -->$result")
            if (result) {
                tbsReaderView!!.openFile(bundle)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (tbsReaderView != null) {
            tbsReaderView!!.onStop()
            tbs_root_layout.removeAllViews()
            tbsReaderView = null
        }
    }
}
