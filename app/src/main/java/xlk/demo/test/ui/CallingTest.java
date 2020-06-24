package xlk.demo.test.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.icu.util.TimeUnit;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import xlk.demo.test.R;

/**
 * @author by xlk
 * @date 2020/5/19 15:55
 * @desc 振铃动画
 */
public class CallingTest extends View {
    private Paint paint = new Paint();
    private float degree = 0f;
    private Timer timer;
    private TimerTask timerTask;
    private Bitmap mBitmap;

    public CallingTest(Context context) {
        this(context, null);
    }

    public CallingTest(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CallingTest(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public CallingTest(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_map_call);
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);
    }

    public void start() {
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                degree = (degree == 15) ? -15 : 15;
                postInvalidate();
            }
        };
        timer.schedule(timerTask, 0, 100);
    }

    public void stop() {
        timerTask.cancel();
        timer.cancel();
        timerTask = null;
        timer = null;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int sizew = MeasureSpec.getSize(widthMeasureSpec);
        int sizeh = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(sizew, sizeh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        int left = width / 2;
        int top = height / 2;
//        paint.setColor(Color.BLACK);
//        canvas.drawPoint(left, top, paint);
//        paint.setColor(Color.RED);
//        canvas.drawRect(0, 0, width, height, paint);
        Log.i("cdck", "onDraw -->width= " + width + ", height= " + height + ", left= " + left + ", top= " + top);
        int width1 = mBitmap.getWidth();
        int height1 = mBitmap.getHeight();
        int left1 = width - left - width1 / 2;
        int top1 = height - top - height1 / 2;

        canvas.save();//保存状态
        canvas.translate(left, top);
        canvas.rotate(degree);
        canvas.drawBitmap(mBitmap, left1 - left, top1 - top, paint);
        canvas.restore();
    }
}
