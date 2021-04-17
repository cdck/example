package xlk.demo.test.camera2

import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.media.MediaRecorder
import android.os.Build
import android.util.Log

/**
 * @author by xlk
 * @date 2020/6/6 17:43
 * @desc 说明
 */
object Camera2Util {
    private const val TAG = "Camera2Util-->"

    /**
     * 判断设备是否有摄像头
     */
    fun isHaveCamera(context: Context): Boolean {
        val cam = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        return cam.cameraIdList.isNotEmpty()
    }

    /**
     * 硬件层面支持的Camera2功能等级从低到高排序为以下4个:
     * LEVEL_LEGACY: 向后兼容模式, 如果是此等级，只支持 Camera1 的功能, 基本没有额外功能, HAL层大概率就是HAL1(我遇到过的都是)
     * LEVEL_LIMITED: 有最基本的功能, 还支持一些额外的高级功能, 这些高级功能是LEVEL_FULL的子集
     * LEVEL_FULL: 支持对每一帧数据进行控制,还支持高速率的图片拍摄，支持所有 Camera2 的高级特性
     * LEVEL_3: 支持YUV后处理和Raw格式图片拍摄, 还支持额外的输出流配置
     * LEVEL_EXTERNAL: API28中加入的, 应该是外接的摄像头, 功能和LIMITED类似
     *
     * @param characteristics
     * @return
     */
    fun isHardwareSupported(characteristics: CameraCharacteristics): Int {
        val deviceLevel =
            characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL)
        if (deviceLevel == null) {
            Log.e(TAG, "can not get INFO_SUPPORTED_HARDWARE_LEVEL")
            return -1
        }
        when (deviceLevel) {
            CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL -> Log.w(
                TAG,
                "hardware supported level:LEVEL_FULL"
            )
            CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY -> Log.w(
                TAG,
                "hardware supported level:LEVEL_LEGACY"
            )
            CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_3 -> Log.w(
                TAG,
                "hardware supported level:LEVEL_3"
            )
            CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED -> Log.w(
                TAG,
                "hardware supported level:LEVEL_LIMITED"
            )
        }
        return deviceLevel
    }

    /**
     * 判断是否支持闪光灯
     * @param characteristics
     * @return
     */
    fun isSupportedFlash(characteristics: CameraCharacteristics): Boolean {
        val aBoolean = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE)
        if (aBoolean != null) {
            Log.i(TAG, "是否支持闪光灯 :   --> $aBoolean")
            return aBoolean
        }
        return false
    }

    /**
     * 获取摄像头
     * @param characteristics
     * @return =1前置,=0后置
     */
    fun getLensFacing(characteristics: CameraCharacteristics): Int {
        val value = characteristics.get(CameraCharacteristics.LENS_FACING)
        if (value != null) {
            Log.i(TAG, "摄像头方向 :   --> $value")
            return value
        }
        return 0
    }

    /**
     * 获取摄像头方向
     * @param characteristics
     * @return
     */
    fun getSensorOrientation(characteristics: CameraCharacteristics): Int {
        val value = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION)
        if (value != null) {
            Log.i(TAG, "获取设备的方向 :   --> $value")
            return value
        }
        return 0
    }

    fun getSupportedVideoSize(characteristics: CameraCharacteristics){
        val map =characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
        val videoSizes = map!!.getOutputSizes(MediaRecorder::class.java)
        videoSizes[0]

    }
}