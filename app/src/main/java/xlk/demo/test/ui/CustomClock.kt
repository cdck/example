package xlk.demo.test.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.Toast
import java.util.*

/**
 * Created by xlk on 2019/5/21.
 */
/**
 * 自定义时钟
 * Created by xlk on 2018/11/15.
 * https://www.jianshu.com/p/c2abd6226897
 */
class CustomClock : View {
    private lateinit var paint: Paint
    private var mSecondDegree =0f//秒针的度数 = 0f
    private var mMinDegree =0f//分针的度数 = 0f
    private var mHourDegree =0f//时针的度数 = 0f
    private val mTimer = Timer()
    private val mTimerTask: TimerTask = object : TimerTask() {
        override fun run() {
            //一圈的度数是360，所以走满一圈角度要清零
            if (mSecondDegree == 360f) mSecondDegree = 0f
            if (mMinDegree == 360f) mMinDegree = 0f
            if (mHourDegree == 360f) mHourDegree = 0f
            mSecondDegree += 6f
            mMinDegree += 0.1f
            mHourDegree += 1.0f / 240
            postInvalidate()
        }
    }
    private var mIsNeght = false

    /**
     * 直接获取系统时间进行设置
     */
    fun currentTime() {
        val instance = Calendar.getInstance()
        val hour = instance[Calendar.HOUR]
        val min = instance[Calendar.MINUTE]
        val second = instance[Calendar.SECOND]
        Log.e(
            "CustomClock",
            "CustomClock.currentTime :   --> $hour:$min:$second"
        )
        appointTime(hour, min, second)
    }

    /**
     * 设置指定时间
     */
    fun appointTime(hour: Int, min: Int, second: Int) {
        /**
         * 秒针一秒钟走6度（一圈60秒共走了360度）；分针一钟也是走6度（一圈60分钟走共走了360度）； 而时针一小时走30度（一圈12小时共走了360度）。
         * 所以我们就可以根据具体的时间来求出各指针的角度。比如我们要设置时间：1点30分30秒，那么根据上述关系求
         * 时针的角度为1*30 = 30度；分针的角度为30*6 = 180度；秒针的角度为30*6=180度；
         *
         * 问题：分针在30分的时候，时钟却还是在1点整，秒针都走了30多秒了，分针确还停在30分钟的位置上
         * 应该：30分30秒其实就是30.5分钟
         * 把传入的秒转换为分钟
         * 时针的角度和分针秒针都有关，我们得把传入的分和秒也都转换为小时再计算它的角度
         */
        if (hour >= 24 || hour < 0 || min >= 60 || min < 0 || second >= 60 || second < 0) {
            Toast.makeText(context, "时间格式错误", Toast.LENGTH_SHORT).show()
            return
        }
        if (hour >= 12) { //大于12时，就让它减去12小时计算它的角度
            mIsNeght = true
            mHourDegree = (hour + min * 1.0f / 60f + second * 1.0f / 3600f - 12) * 30f
        } else {
            mIsNeght = false
            mHourDegree = (hour + min * 1.0f / 60f + second * 1.0f / 3600f) * 30f
        }
        mMinDegree = (min + second * 1.0f / 60f) * 6f
        mSecondDegree = second * 6f
        invalidate()
    }

    val hour: Int
        get() = (currentTime / 3600).toInt()

    val min: Int
        get() = ((currentTime - hour * 3600) / 60).toInt()

    val second: Int
        get() = (currentTime - hour * 3600 - min * 60).toInt()

    /**
     * 获取一共走了多少秒
     *
     *
     * 时针走30度是一个小时，也就是3600秒，所以一度就是 3600秒/30度 = 120秒/度
     * 根据时针走的度数，来求出一共是多少秒
     *
     * @return 返回一共走了多少秒
     */
    private val currentTime: Float
        private get() = if (mIsNeght) mHourDegree * 120 + 12 * 3600 else mHourDegree * 120

    /**
     * 开启定时器
     */
    fun start() {
        //每秒钟执行一次 mTimerTask
        mTimer.schedule(mTimerTask, 0, 1000)
    }

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context,
        attrs
    ) {
        init()
    }

    private fun init() {
        paint = Paint()
        paint.isAntiAlias = true
        paint.color = Color.BLACK
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 0f
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val sizew = MeasureSpec.getSize(widthMeasureSpec)
        val sizeh = MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(sizew, sizeh)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val width = width
        val height = height
        val radius = width / 3
        paint.strokeWidth = 5f
        //画出时钟的外圆
        canvas.drawCircle(width / 2.toFloat(), height / 2.toFloat(), radius.toFloat(), paint)
        //时钟的中心点
        canvas.drawPoint(width / 2.toFloat(), height / 2.toFloat(), paint)
        paint.strokeWidth = 1f
        //将坐标系原点移动到圆心
        canvas.translate(width / 2.toFloat(), height / 2.toFloat())
        //绘制每秒的刻度线，
        for (i in 0..359) {
            //这里刻度线长度我设置为25
            //因为坐标原点已经移动到圆心
            if (i % 30 == 0) { //秒的刻度长
                canvas.drawLine(radius - 14.toFloat(), 0f, radius.toFloat(), 0f, paint)
            } else if (i % 6 == 0) { //5分钟的刻度长
                canvas.drawLine(radius - 9.toFloat(), 0f, radius.toFloat(), 0f, paint)
            } /* else {//小时的刻度长
                canvas.drawLine(radius - 9, 0, radius, 0, paint);
            }*/
            //画完当前的刻度线，就将画布进行旋转一度
            canvas.rotate(1f)
        }
        paint.textSize = 20f
        //绘制每小时展示的数字，1点 2点...
        for (i in 0..11) {
            if (i == 0) {
                drawText(canvas, i * 30, 12.toString() + "", paint)
            } else {
                drawText(canvas, i * 30, i.toString() + "", paint)
            }
        }
        drawNeedle(canvas)
    }

    private fun drawNeedle(canvas: Canvas) {
        /** **** **  秒针  ** ****  */
        canvas.save() //保存状态
        paint.color = Color.RED
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2f
        canvas.rotate(mSecondDegree) //秒针进行旋转mSecondDegree度
        //其实坐标点（0,0）终点坐标（0，-190），这里的190为秒针长度
        canvas.drawLine(0f, 0f, 0f, -190f, paint)
        canvas.restore() //回滚到之前的状态
        /** **** **  分针  ** ****  */
        canvas.save() //保存状态
        paint.color = Color.BLACK
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 4f
        canvas.rotate(mMinDegree)
        canvas.drawLine(0f, 0f, 0f, -130f, paint)
        canvas.restore() //回滚到之前的状态
        /** **** **  时针  ** ****  */
        canvas.save() //保存状态
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 7f
        canvas.rotate(mHourDegree)
        canvas.drawLine(0f, 0f, 0f, -90f, paint)
        canvas.restore() //回滚到之前的状态
    }

    private fun drawText(
        canvas: Canvas,
        degree: Int,
        text: String,
        paint: Paint?
    ) {
        val textBound = Rect()
        paint!!.getTextBounds(text, 0, text.length, textBound)
        canvas.rotate(degree.toFloat())
        canvas.translate(0f, 50 - width / 3.toFloat()) //这里的50是坐标中心距离时钟最外边框的距离，当然你可以根据需要适当调节
        canvas.rotate(-degree.toFloat())
        canvas.drawText(
            text,
            -textBound.width() / 2.toFloat(),
            textBound.height() / 2.toFloat(),
            paint
        )
        canvas.rotate(degree.toFloat())
        canvas.translate(0f, width / 3 - 50.toFloat())
        canvas.rotate(-degree.toFloat())
    }
}