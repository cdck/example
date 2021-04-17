package xlk.demo.test.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

/**
 * @author Created by xlk on 2021/2/23.
 * @desc
 */
class CircleMenuView extends ViewGroup {
    /**
     * 间距角度
     */
    private int spacing = 2;
    /**
     * 菜单个数
     */
    private int menuCount = 8;
    /**
     * 每个菜单的弧度 （360-spacing * menuCount）/menuCount
     */
    private int item_menu_radian;

    private int pressedColor;
    private int unPressedColor;

    private Matrix mMapMatrix;
    /**
     * 区域path
     */
    private Path[] paths;
    /**
     * 每个item的区域
     */
    private Region[] regions;
    /**
     * 每个item的区分flag
     */
    private int[] flags;
    private int currentFlag = -1, centerFlag, touchFlag = -1;
    private Path centerPath;
    private Region centerRegion;
    /**
     * default Paint.
     */
    private Paint mDefaultPaint = new Paint();

    public CircleMenuView(Context context) {
        this(context, null);
    }

    public CircleMenuView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
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
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int sizew = MeasureSpec.getSize(widthMeasureSpec);
        int sizeh = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(sizew, sizeh);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }
}
