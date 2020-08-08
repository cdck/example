package xlk.demo.test.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.graphics.Path;
import android.util.Log;
import android.view.MotionEvent;

import androidx.annotation.Nullable;

import xlk.demo.test.R;

/**
 * @author by xlk
 * @date 2020/7/31 18:20
 * @desc
 */
public class CustomMenu extends CustomView {
    private final String TAG = "CustomMenu-->";
    private int menuCount;
    private int pressedColor;
    private int unPressedColor;
    private int spacingAngle = 4;//菜单之间的间隔角度
    private Matrix mMapMatrix;
    private Path[] paths;
    private Region[] regions;
    private int[] flags;
    private int currentFlag = -1, touchFlag = -1, centerFlag;
    private Path centerPath;
    private Region centerRegion;
    private MenuListener mListener;

    public CustomMenu(Context context) {
        this(context, null);
    }

    public CustomMenu(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CustomMenu);
        unPressedColor = ta.getColor(R.styleable.CustomMenu_colorUnPressed, 0xFF4E5268);
        pressedColor = ta.getColor(R.styleable.CustomMenu_colorPressed, 0xFFDF9C81);
        menuCount = ta.getInt(R.styleable.CustomMenu_count, 4);
        spacingAngle = ta.getInt(R.styleable.CustomMenu_spacingAngle, 4);
        init();
    }

    private void init() {
        paths = new Path[menuCount];
        regions = new Region[menuCount];
        flags = new int[menuCount];
        for (int i = 0; i < menuCount; i++) {
            paths[i] = new Path();
            regions[i] = new Region();
            flags[i] = i;
        }
        centerPath = new Path();
        centerRegion = new Region();
        centerFlag = menuCount;

        // https://blog.csdn.net/m0_37041332/article/details/80680835
        mDefaultPaint.setColor(unPressedColor);
        mDefaultPaint.setStrokeCap(Paint.Cap.ROUND);//设置画笔线帽
        mDefaultPaint.setAntiAlias(true);//开启抗锯齿
        mDefaultPaint.setDither(true);//设置防抖动
        mMapMatrix = new Matrix();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mMapMatrix.reset();

        Log.i(TAG, "onSizeChanged " + mViewWidth + "," + mViewHeight);
        // 注意这个区域的大小
        Region globalRegion = new Region(-w, -h, w, h);
        int minWidth = w > h ? h : w;
        minWidth *= 0.8;

        //获取外部圆半径
        int br = minWidth / 2;
        RectF bigCircle = new RectF(-br, -br, br, br);

        //获取内部圆半径
        int sr = minWidth / 4;
        RectF smallCircle = new RectF(-sr, -sr, sr, sr);

        // 根据视图大小，初始化 Path 和 Region
        centerPath.addCircle(0, 0, 0.2f * minWidth, Path.Direction.CW);
        centerRegion.setPath(centerPath, globalRegion);

        //正数顺时针
        final float bigSweepAngle = (float) (360 - (spacingAngle * menuCount)) / menuCount;
        //负数逆时针
        final float smallSweepAngle = -(bigSweepAngle - spacingAngle);
        Log.i(TAG, "onSizeChanged bigSweepAngle=" + bigSweepAngle + ",smallSweepAngle=" + smallSweepAngle);

        float bigStartAngle = 0;
        for (int i = 0; i < menuCount; i++) {
            Path path = paths[i];
            Region region = regions[i];
            Log.d(TAG, "开始角度：" + bigStartAngle);
            path.addArc(bigCircle, bigStartAngle, bigSweepAngle);
            path.arcTo(smallCircle, bigStartAngle + bigSweepAngle - spacingAngle, smallSweepAngle);
            path.close();
            region.setPath(path, globalRegion);
            bigStartAngle = bigStartAngle + bigSweepAngle + spacingAngle;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float[] pts = new float[2];
        pts[0] = event.getRawX();
        pts[1] = event.getRawY();
        mMapMatrix.mapPoints(pts);
        int x = (int) pts[0];
        int y = (int) pts[1];
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                touchFlag = getTouchedPath(x, y);
                currentFlag = touchFlag;
                break;
            case MotionEvent.ACTION_MOVE:
                currentFlag = getTouchedPath(x, y);
                break;
            case MotionEvent.ACTION_UP:
                currentFlag = getTouchedPath(x, y);
                // 如果手指按下区域和抬起区域相同且不为空，则判断点击事件
                if (currentFlag == touchFlag && currentFlag != -1 && mListener != null) {
                    mListener.onClicked(currentFlag);
                }
                touchFlag = currentFlag = -1;
                break;
            case MotionEvent.ACTION_CANCEL:
                touchFlag = currentFlag = -1;
                break;
        }
        invalidate();
        return true;
    }

    private int getTouchedPath(int x, int y) {
        for (int i = 0; i < regions.length; i++) {
            if (regions[i].contains(x, y)) {
                return i;
            }
        }
        if (centerRegion.contains(x, y)) {
            return centerFlag;
        }
        return -1;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int dx = mViewWidth / 2;
        int dy = mViewHeight / 2;
        canvas.translate(dx, dy);  //移动坐标系到屏幕中心
//        canvas.scale(1, -1); // <-- 注意 翻转y坐标轴
        // 获取测量矩阵(逆矩阵)
        if (mMapMatrix.isIdentity()) {
            canvas.getMatrix().invert(mMapMatrix);
        }

        // 绘制默认颜色
        canvas.drawPath(centerPath, mDefaultPaint);
        for (int i = 0; i < menuCount; i++) {
            canvas.drawPath(paths[i], mDefaultPaint);
        }
        // 绘制触摸区域颜色
        mDefaultPaint.setColor(pressedColor);
        for (int i = 0; i < menuCount; i++) {
            if (currentFlag == flags[i]) {
                canvas.drawPath(paths[i], mDefaultPaint);
            }
        }
        if (currentFlag == centerFlag) {
            canvas.drawPath(centerPath, mDefaultPaint);
        }
        mDefaultPaint.setColor(unPressedColor);
    }

    public void setListener(MenuListener listener) {
        mListener = listener;
    }

    // 点击事件监听器
    public interface MenuListener {
        void onClicked(int index);
    }
}
