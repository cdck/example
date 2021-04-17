package xlk.demo.test.media.decoder

import android.media.AudioManager
import android.media.AudioTrack

/**
 * @author by xlk
 * @date 2020/5/30 14:28
 * @desc 说明
 */
class AudioPlayer(
    private val mFrequency: Int,// 采样率
    private val mChannel: Int,  // 声道
    private val mSampBit: Int   // 采样精度
) {
    private var mAudioTrack: AudioTrack? = null

    /**
     * 初始化
     */
    fun init() {
        if (mAudioTrack != null) {
            release()
        }
        // 获得构建对象的最小缓冲区大小
        val minBufSize =
            AudioTrack.getMinBufferSize(mFrequency, mChannel, mSampBit)
        mAudioTrack = AudioTrack(
            AudioManager.STREAM_MUSIC,
            mFrequency, mChannel, mSampBit, minBufSize, AudioTrack.MODE_STREAM
        )
        mAudioTrack!!.play()
    }

    /**
     * 释放资源
     */
    private fun release() {
        if (mAudioTrack != null) {
            mAudioTrack!!.stop()
            mAudioTrack!!.release()
        }
    }

    /**
     * 将解码后的pcm数据写入audioTrack播放
     *
     * @param data   数据
     * @param offset 偏移
     * @param length 需要播放的长度
     */
    fun play(data: ByteArray?, offset: Int, length: Int) {
        if (data == null || data.isEmpty()) {
            return
        }
        try {
            mAudioTrack!!.write(data, offset, length)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}