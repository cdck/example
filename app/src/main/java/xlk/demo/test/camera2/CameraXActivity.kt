package xlk.demo.test.camera2

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.core.impl.VideoCaptureConfig
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_camera.*
import xlk.demo.test.R
import xlk.demo.test.util.longToast
import java.io.File
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

typealias LumaListener = (luma: Double) -> Unit

/**
 * https://blog.csdn.net/luo_boke/article/details/106634873
 */
class CameraXActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "CameraXActivity"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

    private lateinit var outputDirectory: File
    private var preview: Preview? = null
    private var imageCapture: ImageCapture? = null
    private var videoCapture: VideoCapture? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var camera: Camera? = null
    lateinit var cameraExecutor: ExecutorService
    lateinit var cameraProvider: ProcessCameraProvider
    var width: Int = 0
    var height: Int = 0
    var isRecording: Boolean = false

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }
        viewFinder.post {
            width = viewFinder.width
            height = viewFinder.height
        }

        btn_take_photo.setOnClickListener { takePhoto() }
        btn_take_video.setOnClickListener {
            if (isRecording) {
                videoCapture!!.stopRecording()
                preview!!.clear()
                isRecording = !isRecording;
            } else {
                takeVideo()
                isRecording = !isRecording;
            }
        }
        outputDirectory = getOutputDirectory()
        // Initialize our background executor
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun takeVideo() {
        val videoCapture = videoCapture ?: return
        // Create timestamped output file to hold the image
        val videoFile = File(
            outputDirectory,
            SimpleDateFormat(
                FILENAME_FORMAT, Locale.US
            ).format(System.currentTimeMillis()) + ".mp4"
        )
        videoCapture.startRecording(
            videoFile,
            ContextCompat.getMainExecutor(this),
            object : VideoCapture.OnVideoSavedCallback {
                override fun onError(videoCaptureError: Int, message: String, cause: Throwable?) {
                    Log.e(TAG, "屏幕录制异常=$message")
                }

                override fun onVideoSaved(file: File) {
                    "视频文件保存位置：${file.absolutePath}".longToast()
                }
            })
    }

    private fun startCamera() {
        val cameraProviderFuture =
            ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener(Runnable {
            cameraProvider = cameraProviderFuture.get()
            preview = Preview.Builder().build()
            imageCapture = ImageCapture.Builder().build()
            videoCapture = VideoCaptureConfig.Builder()
                //设置宽高
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                //设置旋转角度
                .setTargetRotation(viewFinder.getDisplay().getRotation())
                .build();
            imageAnalyzer = ImageAnalysis.Builder()
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, LuminosityAnalyzer { luma ->
                        Log.v(TAG, "Average luminosity: $luma")
                    })
                }
            // Select back camera as a default
            val cameraSelector =
                CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()
                // Bind use cases to camera
                camera = cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture, imageAnalyzer, videoCapture
                )
                preview?.setSurfaceProvider(viewFinder.createSurfaceProvider(/*camera?.cameraInfo*/))
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {// Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create timestamped output file to hold the image
        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(
                FILENAME_FORMAT, Locale.US
            ).format(System.currentTimeMillis()) + ".jpg"
        )

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Setup image capture listener which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    val msg = "Photo capture succeeded: $savedUri"
                    Toast.makeText(baseContext, msg, Toast.LENGTH_LONG).show()
                    Log.d(TAG, msg)
                }
            })
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(
                    this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private class LuminosityAnalyzer(private val listener: LumaListener) : ImageAnalysis.Analyzer {

        private fun ByteBuffer.toByteArray(): ByteArray {
            rewind()    // Rewind the buffer to zero
            val data = ByteArray(remaining())
            get(data)   // Copy the buffer into a byte array
            return data // Return the byte array
        }

        override fun analyze(image: ImageProxy) {
            val rotation = image.imageInfo.rotationDegrees
            Log.i(TAG, "image.planes长度=${image.planes.size},旋转角度=$rotation")
            if (image.format != ImageFormat.YUV_420_888) {
                Log.e(TAG, "格式并不是YUV_420_888")
                return
            }
            val i420 = ByteBuffer.allocate(image.width * image.height * 3 / 2)
            // 3个元素 0：Y，1：U，2：V
            /* **** **  Y数据  ** **** */
            var pixelStride = image.planes[0].pixelStride//y的数据长度
            val yBuffer = image.planes[0].buffer
            var rowStride = image.planes[0].rowStride
            //1、rowStride 等于Width ，那么就是一个空数组
            //2、rowStride 大于Width ，那么就是每行多出来的数据大小个byte
            val skipRow = ByteArray(rowStride - image.width)
            val row = ByteArray(image.width)
            // for(int i=0;i<image.height;i++)
            for (i in 0 until image.height) {
                yBuffer.get(row)
                i420.put(row)
                // 不是最后一行才有无效占位数据，最后一行因为后面跟着U 数据，没有无效占位数据，不需要丢弃
                if (i < image.height - 1) {
                    yBuffer.get(skipRow)
                } else {
                    Log.i(TAG, "索引位置=$i")
                }
            }
            /* **** **  U、V数据  ** **** */
            for (i in 1..2) {
                val plane = image.planes[i]
                pixelStride = plane.pixelStride
                rowStride = plane.rowStride
                val buffer = plane.buffer
                //每次处理一行数据
                val uvWidth = image.width / 2
                val uvHeight = image.height / 2
                // 一次处理一个字节
                for (j in 0 until uvHeight) {
                    for (k in 0 until rowStride) {
                        //最后一行
                        if (j == uvHeight - 1) {
                            //uv没混合在一起
                            if (pixelStride == 1) {
                                //rowStride ：大于等于Width/2
                                // 结合外面的if：
                                //  如果是最后一行，我们就不管结尾的占位数据了
                                if (k >= uvWidth) {
                                    break
                                }
                            } else if (pixelStride == 2) {
                                //uv混在了一起
                                // rowStride：大于等于 Width
                                if (k >= uvWidth) {
                                    break
                                }
                            }
                        }

                        val b = buffer.get()
                        // uv没有混合在一起
                        if (pixelStride == 1) {
                            if (k < uvWidth) {
                                i420.put(b)
                            }
                        } else if (pixelStride == 2) {
                            // uv混合在一起了
                            //1、偶数位下标的数据是我们本次要获得的U/V数据
                            //2、占位无效数据要丢弃，不保存
                            if (k < image.width && k % 2 == 0) {
                                i420.put(b)
                            }
                        }
                    }
                }
            }
            //I420
            val result = i420.array()
//            val uvpixelStride = image.planes[1].pixelStride
//            val u = ByteArray(image.width / 2 * image.height / 2)
//
//            image.planes[1]
//            //图像数据
//            val data = buffer.toByteArray()
//            val pixels = data.map { it.toInt() and 0xFF }
//            val luma = pixels.average()
//
//            listener(luma)
//
//            image.close()
        }
    }
}
