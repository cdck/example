package xlk.demo.test.media.decoder

import android.media.AudioFormat
import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.util.Log
import xlk.demo.test.util.loge
import java.io.IOException

/**
 * @author by xlk
 * @date 2020/5/30 14:27
 * @desc 视频文件音频解码
 */
class SoundDecodeThread(private val path: String) : Thread() {
    private var mediaCodec: MediaCodec? = null
    private var mPlayer: AudioPlayer? = null
    override fun run() {
        try {
            val mediaExtractor = MediaExtractor()
            try {
//                val fis = FileInputStream(path)
//                val fd = fis.fd
//                mediaExtractor.setDataSource(fd)
                mediaExtractor.setDataSource(path) // 设置数据源
            } catch (e1: IOException) {
                e1.printStackTrace()
            }
            var mimeType: String
            for (i in 0 until mediaExtractor.trackCount) { // 信道总数
                val format = mediaExtractor.getTrackFormat(i) // 音频文件信息
                Log.e(
                    TAG,
                    "run :音频文件信息  format --> $format"
                )
                mimeType = format.getString(MediaFormat.KEY_MIME)
                if (mimeType.startsWith("audio/")) { // 音频信道
                    mediaExtractor.selectTrack(i) // 切换到 音频信道
                    try {
                        mediaCodec = MediaCodec.createDecoderByType(mimeType) // 创建解码器,提供数据输出
                    } catch (e: IllegalArgumentException) {
                        e.printStackTrace()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    mediaCodec!!.configure(format, null, null, 0)
                    mPlayer = AudioPlayer(
                        format.getInteger(MediaFormat.KEY_SAMPLE_RATE),
                        AudioFormat.CHANNEL_OUT_STEREO,
                        AudioFormat.ENCODING_PCM_16BIT
                    )
                    mPlayer!!.init()
                    break
                }
            }

            if (mediaCodec == null) {
                Log.e(TAG, "Can't find video info!")
                return
            }
            mediaCodec!!.start() // 启动MediaCodec ，等待传入数据
            val inputBuffers =
                mediaCodec!!.inputBuffers // 用来存放目标文件的数据
            // 输入
            var outputBuffers =
                mediaCodec!!.outputBuffers // 解码后的数据
            // 输出
            val info = MediaCodec.BufferInfo() // 用于描述解码得到的byte[]数据的相关信息
            var bIsEos = false
            val startMs = System.currentTimeMillis()

            // ==========开始解码=============
            while (!interrupted()) {
                if (!bIsEos) {
                    val inIndex = mediaCodec!!.dequeueInputBuffer(0)
                    if (inIndex >= 0) {
                        val buffer = inputBuffers[inIndex]
                        val nSampleSize =
                            mediaExtractor.readSampleData(buffer, 0) // 读取一帧数据至buffer中
                        if (nSampleSize < 0) {
                            Log.d(
                                TAG,
                                "InputBuffer BUFFER_FLAG_END_OF_STREAM"
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
                                mediaExtractor.sampleTime,
                                0
                            ) // 通知MediaDecode解码刚刚传入的数据
                            mediaExtractor.advance() // 继续下一取样
                        }
                    }
                }
                val outIndex = mediaCodec!!.dequeueOutputBuffer(info, 0)
                when (outIndex) {
                    MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED -> {
                        Log.d(
                            TAG,
                            "INFO_OUTPUT_BUFFERS_CHANGED"
                        )
                        outputBuffers = mediaCodec!!.outputBuffers
                    }
                    MediaCodec.INFO_OUTPUT_FORMAT_CHANGED -> Log.d(
                        TAG,
                        "New format " + mediaCodec!!.outputFormat
                    )
                    MediaCodec.INFO_TRY_AGAIN_LATER -> Log.d(
                        TAG,
                        "dequeueOutputBuffer timed out!"
                    )
                    else -> {
                        val buffer = outputBuffers[outIndex]
                        Log.v(
                            TAG,
                            "We can't use this buffer but render it due to the API limit, $buffer"
                        )
                        while (info.presentationTimeUs / 1000 > System.currentTimeMillis() - startMs) {
                            try {
                                sleep(10)
                            } catch (e: InterruptedException) {
                                e.printStackTrace()
                                break
                            }
                        }
                        //用来保存解码后的数据
                        val outData = ByteArray(info.size)
                        buffer[outData]
                        //清空缓存
                        buffer.clear()
                        //播放解码后的数据
                        mPlayer!!.play(outData, 0, info.size)
                        mediaCodec!!.releaseOutputBuffer(outIndex, true)
                    }
                }

                // All decoded frames have been rendered, we can stop playing
                // now
                if (info.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) {
                    break
                }
            }
            mediaCodec!!.stop()
            mediaCodec!!.release()
            mediaExtractor.release()
        } catch (e: Exception) {
            "无法对音频解码".loge()
            e.printStackTrace()
        }
    }

    companion object {
        private const val TAG = "SoundDecodeThread"
    }

}