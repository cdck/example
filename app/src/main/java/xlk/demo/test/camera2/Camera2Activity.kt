package xlk.demo.test.camera2

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.hardware.camera2.*
import android.media.ImageReader
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log.d
import android.util.Log.i
import android.util.SparseIntArray
import android.view.Surface
import android.view.SurfaceHolder
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_camera2.*
import xlk.demo.test.R
import xlk.demo.test.util.toast


class Camera2Activity : AppCompatActivity(), SurfaceHolder.Callback, View.OnClickListener {
    private val TAG = "Camera2Activity-->"
    private var currentCameraId = "0" //=1前置，=0后置
    private lateinit var cm: CameraManager
    private lateinit var childHandler: Handler
    private lateinit var mainHandler: Handler
    private var mCameraDevice: CameraDevice? = null
    private var mImageReader: ImageReader? = null
    private var mCameraCaptureSession: CameraCaptureSession? = null
    private val ORIENTATIONS = SparseIntArray()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera2)
        //为了使照片竖直显示
        ORIENTATIONS.append(Surface.ROTATION_0, 90)
        ORIENTATIONS.append(Surface.ROTATION_90, 0)
        ORIENTATIONS.append(Surface.ROTATION_180, 270)
        ORIENTATIONS.append(Surface.ROTATION_270, 180)
        camera2_sfv.holder.addCallback(this)
        camera2_capture.setOnClickListener(this)
        camera2_switch.setOnClickListener(this)
        camera2_back.setOnClickListener(this)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        i(TAG, "surfaceCreated")
        camera2_sfv.keepScreenOn = true
        initCamera()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        i(TAG, "surfaceDestroyed")
        mCameraDevice?.let {
            it.close()
            mCameraDevice = null
        }
    }

    @Throws(CameraAccessException::class)
    private fun initCamera() {
        val handlerThread = HandlerThread("camera2")
        handlerThread.start()
        childHandler = Handler(handlerThread.looper);
        mainHandler = Handler(mainLooper);
        cm = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val cameraIdList = cm.cameraIdList
        mImageReader = ImageReader.newInstance(1080, 1920, ImageFormat.JPEG, 1)
        mImageReader!!.setOnImageAvailableListener(onImageAvailableListener, mainHandler)
        for (cameraId in cameraIdList) {
            if (currentCameraId == cameraId) {
                openCamera()
                return
            }
        }
    }

    private val onImageAvailableListener: ImageReader.OnImageAvailableListener =
        object : ImageReader.OnImageAvailableListener {
            override fun onImageAvailable(reader: ImageReader?) {
                i(TAG, "onImageAvailable")
                mCameraDevice!!.close()
                camera2_sfv.visibility = View.GONE
                camera2_iv.visibility = View.VISIBLE
                reader?.let {
                    val image = it.acquireLatestImage()
                    val buffer = image.planes[0].buffer
                    val bytes = ByteArray(buffer.remaining())
                    buffer.get(bytes)
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    bitmap?.let {
                        camera2_iv.setImageBitmap(bitmap)
                    }
                }
            }
        }

    @SuppressLint("MissingPermission")
    private fun openCamera() {
        try {
            cm.openCamera(currentCameraId, deviceStateCallback, mainHandler)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private val deviceStateCallback: CameraDevice.StateCallback =
        object : CameraDevice.StateCallback() {
            override fun onOpened(camera: CameraDevice) {
                i(TAG, "onOpened 开始预览")
                mCameraDevice = camera
                startPerView()
            }

            override fun onDisconnected(camera: CameraDevice) {
                i(TAG, "onDisconnected")
                camera.close()
                mCameraDevice = null
            }

            override fun onError(camera: CameraDevice, error: Int) {
                i(TAG, "onError")
                camera.close()
                mCameraDevice = null
            }
        }

    fun startPerView() {
        //创建预览需要的CaptureRequest.Builder
        val captureRequestBuilder =
            mCameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
        //将SurfaceView的surface作为CaptureRequest.Builder的目标
        captureRequestBuilder.addTarget(camera2_sfv.holder.surface)
        //创建CameraCaptureSession，该对象负责管理处理预览请求和拍照请求
        mCameraDevice!!.createCaptureSession(
            listOf(//拍照需要两个：一个预览，一个拍照；录制视频需要两个：一个预览，一个录制
                camera2_sfv.holder.surface,
                mImageReader!!.surface
            ), object :
                CameraCaptureSession.StateCallback() {
                override fun onConfigured(session: CameraCaptureSession) {
                    mCameraCaptureSession = session
                    try {
                        //自动对焦
                        captureRequestBuilder.set(
                            CaptureRequest.CONTROL_AF_MODE,
                            CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
                        )
                        val characteristics = cm.getCameraCharacteristics(currentCameraId)
                        if (Camera2Util.isSupportedFlash(characteristics)) {
                            d(TAG, "开启闪光灯")
                            //开启闪光灯
                            captureRequestBuilder.set(
                                CaptureRequest.CONTROL_AE_MODE,
                                CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH
                            )
                        }
                        //显示预览
                        mCameraCaptureSession!!.setRepeatingRequest(
                            captureRequestBuilder.build(),
                            null,
                            childHandler
                        )
                    } catch (e: CameraAccessException) {
                        e.printStackTrace()
                    }
                }

                override fun onConfigureFailed(session: CameraCaptureSession) {
                    "配置相机失败".toast()
                }
            }, childHandler
        )
    }

    private fun capture() {
        val captureRequest =
            mCameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
        captureRequest.addTarget(mImageReader!!.surface)

        val characteristics = cm.getCameraCharacteristics(currentCameraId)
        //自动对焦
        captureRequest.set(
            CaptureRequest.CONTROL_AF_MODE,
            CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
        )
        if (Camera2Util.isSupportedFlash(characteristics)) {
            //开启闪光灯
            captureRequest.set(
                CaptureRequest.CONTROL_AE_MODE,
                CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH
            )
        }
        // 获取手机方向
        val rotation = windowManager.defaultDisplay.rotation
        //根据设备方向计算设置照片的方向
        captureRequest.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));
        mCameraCaptureSession!!.capture(captureRequest.build(), null, childHandler)
    }

    override fun onBackPressed() {
        if (camera2_sfv.visibility == View.GONE) {
            camera2_iv.visibility = View.GONE
            camera2_sfv.visibility = View.VISIBLE
        } else {
            super.onBackPressed()
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.camera2_capture -> {
                mCameraDevice?.let { capture() }
            }
            R.id.camera2_switch -> {
                mCameraDevice?.let {
                    currentCameraId = if (currentCameraId == "0") "1" else "0"
                    it.close()
                    openCamera()
                }
            }
            R.id.camera2_back -> {
                onBackPressed()
            }
        }
    }
}