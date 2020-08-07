package xlk.demo.test

import android.app.Application
import android.content.Context
import android.util.Log
import android.util.Log.d
import android.util.Log.i
import com.tencent.smtt.export.external.TbsCoreSettings
import com.tencent.smtt.sdk.QbSdk
import com.tencent.smtt.sdk.QbSdk.PreInitCallback
import com.tencent.smtt.sdk.TbsDownloader
import com.tencent.smtt.sdk.TbsListener
import xlk.demo.test.util.longToast
import xlk.demo.test.util.toast


/**
 * @author by xlk
 * @date 2020/6/8 17:29
 * @desc 说明
 */
class MyApplication : Application() {
    companion object {
        lateinit var context: Context
        val TAG = "MyApplication"
        var successful = false
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        loadX5()
    }


    var cb: PreInitCallback = object : PreInitCallback {
        override fun onCoreInitFinished() {
            //x5内核初始化完成回调接口，此接口回调并表示已经加载起来了x5，有可能特殊情况下x5内核加载失败，切换到系统内核。
            i(TAG, "x5内核 onCoreInitFinished-->")
        }

        override fun onViewInitFinished(b: Boolean) {
            //ToastUtil.showToast(usedX5 ? R.string.tencent_x5_load_successfully : R.string.tencent_x5_load_failed);
            //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
            if (b) "X5内核加载成功".longToast() else "X5内核加载失败".longToast()
            successful = b
            d(
                TAG,
                "onViewInitFinished: 加载X5内核是否成功: $b"
            )
        }
    }

    fun loadX5() {
        val canLoadX5 =
            QbSdk.canLoadX5(context)
        i(TAG, "x5内核  是否可以加载X5内核 -->$canLoadX5")
        if (canLoadX5) {
            initX5()
        } else {
            QbSdk.setDownloadWithoutWifi(true)
            QbSdk.setTbsListener(object : TbsListener {
                override fun onDownloadFinish(i: Int) {
                    d(
                        TAG,
                        "x5内核 onDownloadFinish -->下载X5内核：$i"
                    )
                }

                override fun onInstallFinish(i: Int) {
                    d(
                        TAG,
                        "x5内核 onInstallFinish -->安装X5内核：$i"
                    )
                    if (i == TbsListener.ErrorCode.INSTALL_SUCCESS_AND_RELEASE_LOCK) {
                        initX5()
                    }
                }

                override fun onDownloadProgress(i: Int) {
                    d(
                        TAG,
                        "x5内核 onDownloadProgress -->下载X5内核：$i"
                    )
                }
            })
            Thread(Runnable {
                //判断是否要自行下载内核
//                boolean needDownload = TbsDownloader . needDownload (mContext, TbsDownloader.DOWNLOAD_OVERSEA_TBS);
//                i(TAG, "loadX5 是否需要自行下载X5内核" + needDownload);
//                if (needDownload) {
//                    // 根据实际的网络情况下，选择是否下载或是其他操作
//                    // 例如: 只有在wifi状态下，自动下载，否则弹框提示
//                    // 启动下载
//                    TbsDownloader.startDownload(mContext);
//                }
                TbsDownloader.startDownload(context)
            }).start()
        }
    }

    fun initX5() {
        //目前线上sdk存在部分情况下initX5Enviroment方法没有回调，您可以不用等待该方法回调直接使用x5内核。
        QbSdk.initX5Environment(context, cb)
        //如果您需要得知内核初始化状态，可以使用QbSdk.preinit接口代替
//        QbSdk.preInit(applicationContext, cb);
    }
}