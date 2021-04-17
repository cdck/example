package xlk.demo.test.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by xlk on 2020/12/19.
 * @desc
 */
public class CakeView extends View {

    private Context ctx;
    private DecimalFormat format;
    private List<BaseMessage> mList;

    private Paint arcPaint;
    private Paint linePaint;
    private Paint textPaint;

    private float centerX;
    private float centerY;
    private float radius;
    private float total;
    private float startAngle;
    private float textAngle;
    private float roundAngle = 360f;
    private boolean isAddText = true;
    private float mFontSize;
    private Bitmap mCenterBitMap;
    private int mCenterWidth = 110;

    private List<PointF[]> lineList;
    private List<PointF> textList;
    private Region[] regionList;
    private RectF centerRect;
    private Canvas mCanvas;

    private MyHandler handler;
    private ViewOnclickListener mViewOnclickListener;

    private int refeshNum = 0;
    private boolean isRefesh = false;

    /**
     * 检测按下到抬起时旋转的角度
     */
    private float mTmpAngle;
    /**
     * 检测按下到抬起时使用的时间
     */
    private long mDownTime;

    /**
     * 判断是否正在自动滚动
     */
    private boolean isFling;

    /**
     * 整个View的旋转角度
     */
    private float rotationAngle;

    private float start = 0;
    /**
     * 获得当前的角度
     */
    private float end = 0;

    /**
     * 每秒最大移动角度
     */
    private int mMax_Speed;
    /**
     * 如果移动角度达到该值，则屏蔽点击
     */
    private int mMin_Speed;

    /**
     * 判断是否正在自动滚动
     */
    private boolean isQuickMove;

    public CakeView(Context context) {
        this(context, null);
    }

    public CakeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CakeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.ctx = context;
        this.handler = new MyHandler(this);
        this.lineList = new ArrayList<>();
        this.textList = new ArrayList<>();
        this.mList = new ArrayList<>();
        this.format = new DecimalFormat("##0.00");

        this.arcPaint = new Paint();
        this.arcPaint.setAntiAlias(true);
        this.arcPaint.setDither(true);
        this.arcPaint.setStyle(Paint.Style.STROKE);
//        this.arcPaint.setColor(Color.parseColor("#FFFFFF"));

        this.linePaint = new Paint();
        this.linePaint.setAntiAlias(true);
        this.linePaint.setDither(true);
        this.linePaint.setStyle(Paint.Style.STROKE);
        this.linePaint.setStrokeWidth(dip2px(ctx, 20));
        this.linePaint.setColor(Color.parseColor("#FFFFFF"));

        this.textPaint = new Paint();
        this.textPaint.setAntiAlias(true);
        this.textPaint.setDither(true);
        this.textPaint.setTextSize(40);
        this.textPaint.setStyle(Paint.Style.FILL);
        this.textPaint.setColor(Color.parseColor("#FFFFFF"));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width;
        int height;
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.EXACTLY) {
            height = heightSpecSize;
            width = Math.min(heightSpecSize, Math.min(getScreenSize(ctx)[0], getScreenSize(ctx)[1]));
        } else if (widthSpecMode == MeasureSpec.EXACTLY && heightSpecMode == MeasureSpec.AT_MOST) {
            width = widthSpecSize;
            height = Math.min(widthSpecSize, Math.min(getScreenSize(ctx)[0], getScreenSize(ctx)[1]));
        } else if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            width = height = Math.min(getScreenSize(ctx)[0], getScreenSize(ctx)[1]);
        } else {
            width = widthSpecSize;
            height = heightSpecSize;
        }

        Log.e("宽高", width + "," + height);
//        setMeasuredDimension(width + (int) DisplayUtil.dpToPx(ctx,20), height+(int) DisplayUtil.dpToPx(ctx,20));
        setMeasuredDimension(width , height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerX = w / 2;
        centerY = h / 2;
        radius = Math.min(centerX, centerY) * 0.725f;//0.725f
        arcPaint.setStrokeWidth(radius / 3 * 2);
        textPaint.setTextSize(radius / 10);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        refeshNum++;
        Log.e("onDraw--->>", "重绘次数=" + refeshNum);
        textList.clear();
        lineList.clear();
        lineList = new ArrayList<>();
        textList = new ArrayList<>();
        mCanvas = null;
        mCanvas = canvas;
        if (mList != null) {

            RectF mRectF = new RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
            for (int i = 0; i < mList.size(); i++) {
                arcPaint.setColor(mList.get(i).color);
                canvas.drawArc(mRectF, startAngle, mList.get(i).percent / total * roundAngle, false, arcPaint);

                if (mCenterBitMap != null) {
                    centerRect = new RectF(centerX - mCenterWidth / 2, centerY - mCenterWidth / 2, centerX + mCenterWidth / 2, centerY + mCenterWidth / 2);
                    canvas.drawBitmap(mCenterBitMap, null, centerRect, null);
                }
                if (isAddText) {
                    lineList.add(getLinePointFs(startAngle));//获取直线 开始坐标 结束坐标
                    textAngle = startAngle + mList.get(i).percent / total * roundAngle / 2;
                    textList.add(getTextPointF(textAngle));   //获取文本文本
                }

                startAngle += mList.get(i).percent / total * roundAngle;
            }

//            drawSpacingLine(canvas, lineList);
            // 绘制文字
//            drawText(canvas);

        }

        if (roundAngle < 360f) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    roundAngle += 10f;
                    if (roundAngle == 360f - 10f) {
                        isAddText = true;
                    }
//                    postInvalidate();
                }
            }, 0);
//            postInvalidate();
        } else {
            // 绘制间隔空白线
            drawSpacingLine(canvas, lineList);
            // 绘制文字
            drawText(canvas);

//            if (handler != null) {
//                handler.removeCallbacksAndMessages(null);
//            }
        }
//         绘制间隔空白线
//        Log.e("cs", "" + roundAngle);
        if (!isRefesh || isFling) {
            postInvalidate();
        }
        isRefesh = !isRefesh;
    }

    /**
     * 获取文本文本
     *
     * @return
     */
    private PointF getTextPointF(float angle) {
        float textPointX = (float) (centerX + radius * Math.cos(Math.toRadians(angle)));
        float textPointY = (float) (centerY + radius * Math.sin(Math.toRadians(angle)));
        return new PointF(textPointX, textPointY);
    }

    /**
     * 获取直线 开始坐标 结束坐标
     */
    private PointF[] getLinePointFs(float angle) {
        float stopX = (float) (centerX + (radius + arcPaint.getStrokeWidth()) * Math.cos(Math.toRadians(angle)));
        float stopY = (float) (centerY + (radius + arcPaint.getStrokeWidth()) * Math.sin(Math.toRadians(angle)));
        float startX = (float) (centerX + (radius - arcPaint.getStrokeWidth()) * Math.cos(Math.toRadians(angle)));
        float startY = (float) (centerY + (radius - arcPaint.getStrokeWidth()) * Math.sin(Math.toRadians(angle)));
        PointF startPoint = new PointF(startX, startY);
        PointF stopPoint = new PointF(stopX, stopY);
        return new PointF[]{startPoint, stopPoint};
    }

    /**
     * 画间隔线
     *
     * @param canvas
     */
    private void drawSpacingLine(Canvas canvas, List<PointF[]> pointFs) {
        for (int i = 0; i < pointFs.size(); i++) {
            PointF[] fp = pointFs.get(i);
            canvas.drawLine(fp[0].x, fp[0].y, fp[1].x, fp[1].y, linePaint);
        }
    }

    /**
     * 画文本
     *
     * @param canvas
     */
    private void drawText(Canvas canvas) {
        regionList = new Region[textList.size()];
        for (int i = 0; i < textList.size(); i++) {
            textPaint.setTextAlign(Paint.Align.CENTER);
            String text = mList.get(i).content;
            Bitmap bitmap = mList.get(i).image;
            canvas.drawText(text, textList.get(i).x, textList.get(i).y + dip2px(getContext(), 25), textPaint);
//            Paint.FontMetrics fm = textPaint.getFontMetrics();
//            canvas.drawText(format.format(mList.get(i).percent * 100 / total) + "%", textList.get(i).x, textList.get(i).y + (fm.descent - fm.ascent), textPaint);
            // 设置绘制图片的区域
            // 设为默认位置
            // todo 计算默认方位有错，自行修改
            Rect rect = new Rect((int) (textList.get(i).x - (bitmap.getWidth() / 2)),
                    (int) (textList.get(i).y) - (bitmap.getHeight() * 5 / 4 - dip2px(getContext(), 20)),
                    (int) textList.get(i).x + (bitmap.getWidth() / 2),
                    (int) textList.get(i).y - (bitmap.getHeight() / 4) + dip2px(getContext(), 20));

            Region re = new Region();
            Path path = new Path();
            path.moveTo((float) (textList.get(i).x - dip2px(getContext(), 35)), (float) textList.get(i).y - dip2px(getContext(), 35));
            path.lineTo((float) (textList.get(i).x + dip2px(getContext(), 35)), (float) textList.get(i).y - dip2px(getContext(), 35));
            path.lineTo((float) (textList.get(i).x + dip2px(getContext(), 35)), (float) textList.get(i).y + dip2px(getContext(), 35));
            path.lineTo((float) (textList.get(i).x - dip2px(getContext(), 35)), (float) textList.get(i).y + dip2px(getContext(), 35));
            path.close();

            RectF r = new RectF();
            //计算控制点的边界
            path.computeBounds(r, true);
            //设置区域路径和剪辑描述的区域
            re.setPath(path, new Region((int) r.left, (int) r.top, (int) r.right, (int) r.bottom));
            regionList[i] = re;
            canvas.drawBitmap(bitmap, null, rect, null);
        }
    }


    /**
     * 设置间隔线的颜色
     *
     * @param color
     */
    public void setSpacingLineColor(int color) {
        linePaint.setColor(color);
    }


    /**
     * 设置间隔线的宽度
     *
     * @param width
     */
    public void setSpacingLineWidth(float width) {
        linePaint.setStrokeWidth(width);
    }

    /**
     * 设置文本颜色
     *
     * @param color
     */
    public void setTextColor(int color) {
        textPaint.setColor(color);
    }

    /**
     * 设置中心颜色
     *
     * @param color
     */
    public void setCenterColor(int color) {
        arcPaint.setColor(color);
    }

    /**
     * 设置开始角度
     *
     * @param startAngle
     */
    public void setStartAngle(float startAngle) {
        this.startAngle = startAngle;
    }


    /**
     * 设置标签文字大小
     *
     * @param mFontSize
     */
    public void setFontSize(float mFontSize) {
        this.mFontSize = mFontSize;
    }

    /**
     * 设置中心图标
     *
     * @param mCenterBitMap
     */
    public void setCenterBitMap(Bitmap mCenterBitMap) {
        this.mCenterBitMap = mCenterBitMap;
    }

    /**
     * 设置中心图标宽度
     *
     * @param mCenterWidth
     */
    public void setCenterWidth(int mCenterWidth) {
        this.mCenterWidth = mCenterWidth;
    }

    /**
     * 设置饼的宽度
     *
     * @param width
     */
    public void setCakeStrokeWidth(int width) {
        arcPaint.setStrokeWidth(dip2px(ctx, width));
    }

    /**
     * 设置饼的数据
     *
     * @param mList
     */
    public void setCakeData(List<BaseMessage> mList) {
        total = 0;
        if (mList == null) {
            return;
        }
        for (int i = 0; i < mList.size(); i++) {
            total += mList.get(i).percent;
        }
        this.mList.clear();
        this.mList = mList;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int[] getScreenSize(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(
                Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return new int[]{outMetrics.widthPixels, outMetrics.heightPixels};
    }

    private static class MyHandler extends Handler {
        private WeakReference<CakeView> activityWeakReference;

        public MyHandler(CakeView activity) {
            activityWeakReference = new WeakReference<CakeView>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            CakeView activity = activityWeakReference.get();
            if (activity == null) {
                return;
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        getParent().requestDisallowInterceptTouchEvent(true);
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.e(TAG, "ACTION_DOWN"+isClick);
                mLastX = x;
                mLastY = y;
                mDownTime = System.currentTimeMillis();
                mTmpAngle = 0;

                // 如果当前已经在快速滚动
                if (isQuickMove) {
                    // 移除快速滚动的回调
                    removeCallbacks(mFlingRunnable);
                    isQuickMove = false;
                    return true;
                }
                isClick = true;

                break;
            case MotionEvent.ACTION_MOVE:
                Log.e(TAG, "ACTION_MOVE"+isClick);
                isClick = false;

                start = (float) getAngle(mLastX, mLastY);
                end = (float) getAngle(x, y);
                Log.e(TAG, start + " =start " + end + " ,  =end");

                //  二、三象限，色角度值是付值
                if (getQuadrant(x, y) == 3 || getQuadrant(x, y) == 2) {
                    rotationAngle += start - end;
                    mTmpAngle += start - end;
                } else {
                    //如果是一、四象限，则直接end-start，角度值都是正值
                    rotationAngle += end - start;
                    mTmpAngle += end - start;
                }

                // 重新布局
//                postInvalidate();
                getCheck();
                break;
            case MotionEvent.ACTION_UP:
                Log.e(TAG, "ACTION_UP"+isClick);
                // 获取每秒移动的角度
                float anglePerSecond = mTmpAngle * 1000
                        / (System.currentTimeMillis() - mDownTime);
                // 如果达到最大速度
                if (Math.abs(anglePerSecond) > mMax_Speed && !isQuickMove) {
                    // 惯性滚动
                    if (Math.abs(start-end)>=1){ //放手时大于1度才惯性滚动
                        post(mFlingRunnable = new AngleRunnable(anglePerSecond));
                    }
                    return true;
                }

                // 如果当前旋转角度超过minSpeed屏蔽点击
                if (Math.abs(mTmpAngle) > mMin_Speed) {
                    return true;
                }

                if (!isQuickMove) {
                    Log.e(TAG, "ACTION_UP-点击");
                    for (int i = 0; i < regionList.length; i++) {
                        Region rect = regionList[i];
                        if (rect.contains((int) x, (int) y)) {
                            if (mViewOnclickListener != null) {
                                mViewOnclickListener.onViewClick(this, i);
                            }
                        }
                    }

                    if (centerRect != null) {
                        if (centerRect.contains((int) x, (int) y)) {
                            if (mViewOnclickListener != null) {
                                mViewOnclickListener.onViewCenterClick();
                            }
                        }
                    }
                }
                break;
        }

        return true;
    }

    private void getCheck() {
        rotationAngle %= 360;
        setRotation(rotationAngle);
    }

    private double distanceTwoPointF(PointF A,PointF B){
        double disX=Math.abs(A.x)-Math.abs(B.x);
        double disY=Math.abs(A.y)-Math.abs(B.y);
        return Math.sqrt((disX)*(disX)+(disY)*(disY));
    }

    public interface onYuanPanClickListener {
        void onClick(View v, int position);
    }

    public interface ViewOnclickListener {
        void onViewClick(View v, int position);

        void onViewCenterClick();
    }

    public ViewOnclickListener getViewOnclickListener() {
        return mViewOnclickListener;
    }

    public void setViewOnclickListener(ViewOnclickListener viewOnclickListener) {
        mViewOnclickListener = viewOnclickListener;
    }

    /**
     * 记录上一次的x，y坐标
     */
    private float mLastX;
    private float mLastY;


    /**
     * 移动转盘
     */
    private boolean isClick = false;

    /**
     * 自动滚动的Runnable
     */
    private AngleRunnable mFlingRunnable;

    /**
     * 当每秒移动角度达到该值时，认为是快速移动
     */
    private static final int FLINGABLE_VALUE = 300;

    /**
     * 如果移动角度达到该值，则屏蔽点击
     */
    private static final int NOCLICK_VALUE = 3;

    /**
     * 当每秒移动角度达到该值时，认为是快速移动
     */
    private int mFlingableValue = FLINGABLE_VALUE;


    private static final String TAG = "CakeView";


    /**
     * 根据触摸的位置，计算角度
     *
     * @param xTouch
     * @param yTouch
     * @return
     */
    private float getAngle(float xTouch, float yTouch) {
        double x = xTouch - (getWidth() / 2d);
        double y = yTouch - (getHeight() / 2d);
        return (float) (Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI);
    }

    /**
     * 根据当前位置计算象限
     *
     * @param x
     * @param y
     * @return
     */
    private int getQuadrant(float x, float y) {
        int tmpX = (int) (x - getWidth() / 2);
        int tmpY = (int) (y - getHeight() / 2);
        if (tmpX >= 0) {
            return tmpY >= 0 ? 4 : 1;
        } else {
            return tmpY >= 0 ? 3 : 2;
        }
    }

    /**
     * 惯性滚动
     */
    private class AngleRunnable implements Runnable {

        private float angelPerSecond;

        public AngleRunnable(float velocity) {
            this.angelPerSecond = velocity;
        }

        public void run() {
            //小于20停止
            if ((int) Math.abs(angelPerSecond) < 20) {
                isQuickMove = false;
                return;
            }
            isQuickMove = true;
            // 滚动时候不断修改滚动角度大小
            rotationAngle += (angelPerSecond / 30);
            //逐渐减小这个值
            angelPerSecond /= 1.0666F;
            postDelayed(this, 30);
            // 重新布局
            getCheck();
        }
    }


    /**
     * 自动滚动的任务
     *
     * @author zhy
     */
    private class AutoFlingRunnable implements Runnable {

        private float angelPerSecond;
        private int flg;

        public AutoFlingRunnable(float velocity,float flg) {
            this.angelPerSecond = velocity;
            this.flg=(int) flg;
        }

        public void run() {
            // 如果小于20,则停止
            if ((int) Math.abs(angelPerSecond) < 20) {
                isFling = false;
                return;
            }
            isFling = true;
            // 不断改变mStartAngle，让其滚动，/30为了避免滚动太快
            startAngle += (flg*angelPerSecond / 10);
            // 逐渐减小这个值
            angelPerSecond /= 1.0666F;
            // 绘制间隔空白线
            drawSpacingLine(mCanvas, lineList);
            postDelayed(this, 30);
            // 重新布局
            requestLayout();
        }
    }

}


