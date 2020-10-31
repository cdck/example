package xlk.demo.test.media

import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.util.Log
import android.view.Surface
import xlk.demo.test.util.longToast
import xlk.demo.test.util.toast
import java.io.IOException
import java.util.concurrent.atomic.AtomicBoolean

/**
 * @author by xlk
 * @date 2020/5/30 14:03
 * @desc 说明
 */
class VideoDecodeThread(surface: Surface, path: String) :
    Thread() {
    private val TAG = "DecodingPlay-->"
    private val path: String
    private val surface: Surface
    private var mediaExtractor: MediaExtractor? = null
    private var mediaCodec: MediaCodec? = null
    private val quit =
        AtomicBoolean(false)

    fun quit() {
        quit.set(true)
    }

    private fun initMediaCodec() {
        mediaExtractor = MediaExtractor()
        try {
            mediaExtractor!!.setDataSource(path) // 设置数据源
        } catch (e1: IOException) {
            e1.printStackTrace()
        }
        var mimeType: String? = null
        //获取信道总数
        val trackCount = mediaExtractor!!.trackCount
        for (i in 0 until trackCount) {
            //获取音频文件信息
            val format = mediaExtractor!!.getTrackFormat(i)
            Log.d(TAG, "initMediaCodec :  获取视频文件信息 format --> $format")
            mimeType = format.getString(MediaFormat.KEY_MIME)
            if (mimeType.startsWith("video/")) { // 视频信道
                mediaExtractor!!.selectTrack(i) // 切换到视频信道
                try {
                    mediaCodec = MediaCodec.createDecoderByType(mimeType) // 创建解码器,提供数据输出
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                mediaCodec!!.configure(format, surface, null, 0)
                break
            }
        }
        if (mediaCodec == null) {
            Log.e(TAG, "initMediaCodec can't find video info -->")
            return
        }
        //启动MediaCodec ，等待传入数据
        mediaCodec!!.start()
    }

    private fun startDecode() {
//        // 输入
//        ByteBuffer[] inputBuffers = mediaCodec.getInputBuffers(); // 用来存放目标文件的数据
//        // 输出
//        ByteBuffer[] outputBuffers = mediaCodec.getOutputBuffers(); // 解码后的数据
        val info = MediaCodec.BufferInfo() // 用于描述解码得到的byte[]数据的相关信息
        var bIsEos = false
        val startMs = System.currentTimeMillis()
        // ==========开始解码=============
        while (!quit.get()) {
            if (!bIsEos) {
                val inIndex = mediaCodec!!.dequeueInputBuffer(0)
                if (inIndex >= 0) {
                    val buffer =
                        mediaCodec!!.getInputBuffer(inIndex) //inputBuffers[inIndex];
                    val nSampleSize =
                        mediaExtractor!!.readSampleData(buffer!!, 0) // 读取一帧数据至buffer中
                    if (nSampleSize < 0) {
                        Log.d(
                            TAG,
                            "InputBuffer BUFFER_FLAG_END_OF_STREAM 设置bIsEos为true"
                        )
                        mediaCodec!!.queueInputBuffer(
                            inIndex,
                            0,
                            0,
                            0,
                            MediaCodec.BUFFER_FLAG_END_OF_STREAM
                        )
                        bIsEos = true
                    } else {
                        // 填数据
                        mediaCodec!!.queueInputBuffer(
                            inIndex,
                            0,
                            nSampleSize,
                            mediaExtractor!!.sampleTime,
                            0
                        ) // 通知MediaDecode解码刚刚传入的数据
                        mediaExtractor!!.advance() // 继续下一取样
                    }
                }
            }
            val outIndex = mediaCodec!!.dequeueOutputBuffer(info, 0)
            when (outIndex) {
                MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED -> Log.d(
                    TAG,
                    "INFO_OUTPUT_BUFFERS_CHANGED"
                )
                MediaCodec.INFO_OUTPUT_FORMAT_CHANGED -> Log.d(
                    TAG,
                    "New format " + mediaCodec!!.outputFormat
                )
                MediaCodec.INFO_TRY_AGAIN_LATER -> Log.d(
                    TAG,
                    "dequeueOutputBuffer timed out!"
                )
                else -> {
                    val buffer =
                        mediaCodec!!.getOutputBuffer(outIndex) // outputBuffers[outIndex];
                    Log.v(
                        TAG,
                        "We can't use this buffer but render it due to the API limit, $buffer"
                    )
                    //防止视频播放过快
                    while (info.presentationTimeUs / 1000 > System.currentTimeMillis() - startMs) {
                        try {
                            sleep(10)
                        } catch (e: InterruptedException) {
                            e.printStackTrace()
                            break
                        }
                    }
                    mediaCodec!!.releaseOutputBuffer(outIndex, true)
                }
            }

            // All decoded frames have been rendered, we can stop playing
            // now
            if (info.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) {
                break
            }
        }
    }

    private fun release() {
        try {
            Log.e(TAG, "release  -->" + (mediaCodec == null))
            if (mediaCodec != null) {
                mediaCodec!!.stop()
                mediaCodec!!.release()
                mediaCodec = null
            }
            if (mediaExtractor != null) {
                mediaExtractor!!.release()
                mediaExtractor = null
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun run() {
        super.run()
        try {
            initMediaCodec()
            startDecode()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            release()
            "视频播放完毕".longToast()
        }
    }

    init {
        Log.e(TAG, "VideoDecodeThread 文件路径 -->$path")
        this.path = path
        this.surface = surface
    }
}