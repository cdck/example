package xlk.demo.test.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import xlk.demo.test.R;

import static java.lang.Math.PI;

/**
 * Created by xlk on 2018/11/14.
 */
public class CustomView3 extends View {
    private final static String TAG = "CustomView-->";
    private Paint paint;
    private int mWidth, mHeight;
    private int number = 7;
    private int startAngle = 0;
    private int sweepAngle = 360 / number;
    private List<Path> paths;//弧形区域path
    private List<Region> regions;//中心圆周围的各个区域
    private List<Point> cutPoints;//分割线点坐标
    private List<Point> radianCentrePoints;//区域圆弧中心点坐标
    private boolean[] bs;//是否点击
    private int centreColor;//中心圆颜色
    private int centreClickColor;//中心圆选中颜色
    private int areaColor;//区域颜色
    private int areaClickColor;//区域选中颜色
    private float[] startAngles;//存放每个弧型开始的角度
    private RectF areaRectF;
    private int rotateAngle;
    private AreaClick mListener;
    private int dx, dy;//坐标偏移量
    private int diameter;//直径
    private int radius;//半径

    public CustomView3(Context context) {
        super(context, null);
        init();
    }

    public CustomView3(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, 0);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CustomView3);
        number = array.getInteger(R.styleable.CustomView3_numbers, 5);
        sweepAngle = 360 / number;
        areaColor = array.getColor(R.styleable.CustomView3_areaColor, 0x0c1f35);
        areaClickColor = array.getColor(R.styleable.CustomView3_areaClickColor, 0x04937f);
        centreColor = array.getColor(R.styleable.CustomView3_centreColor, 0x7f1aa7);
        centreClickColor = array.getColor(R.styleable.CustomView3_centreClickColor, 0xffffff);
        init();
    }

    public CustomView3(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);//抗锯齿
        paint.setDither(true);// 防抖动
        paint.setStrokeWidth(2);
        paint.setColor(areaColor);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeJoin(Paint.Join.ROUND);// 设置线段连接处的样式为圆弧连接
        paint.setStrokeCap(Paint.Cap.ROUND);// 设置两端的线帽为圆的
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int defaultwSize = getSize(300, widthMeasureSpec);
        int defaulthSize = getSize(300, heightMeasureSpec);
        //直径取较小值
        diameter = Math.min(defaultwSize, defaulthSize);
        Log.e(TAG, "CustomView.onMeasure :   --> " + defaultwSize + "," + defaulthSize + ",其中较小值：" + diameter);
        setMeasuredDimension(diameter, diameter);
    }

    public static int getSize(int size, int spec) {
        int result = size;
        int specMode = MeasureSpec.getMode(spec);
        int specSize = MeasureSpec.getSize(spec);
        Log.e(TAG, "CustomView.getSize :  specSize --> " + specSize);
        switch (specMode) {
            case MeasureSpec.UNSPECIFIED:
            case MeasureSpec.AT_MOST:
                result = size;
                break;
            case MeasureSpec.EXACTLY:
                result = specSize;
        }
        return result;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Log.e(TAG, "onSizeChanged :   --> " + w + "," + h);
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        /** **** **  获取坐标偏移量  ** **** **/
        dx = (getWidth() + getPaddingLeft() - getPaddingRight()) / 2;
        dy = (getHeight() + getPaddingTop() - getPaddingBottom()) / 2;
        radius = diameter - (Math.min(dx, dy));
        Log.e("CustomView", "坐标偏移量 :   --> " + dx + "," + dy + ", 实际大小：" + radius);
        areaRectF = new RectF(-radius, -radius, radius, radius);
        getPathData();
        //获取需要旋转的角度
        rotateAngle = getRotateAngle();
        /** **** **  不等于0说明需要旋转画布，则进行重新设置区域数据  ** **** **/
        if (rotateAngle != 0) updateRegions(rotateAngle);
    }

    private void getPathData() {
        if (paths == null) paths = new ArrayList<>();
        else paths.clear();
        if (regions == null) regions = new ArrayList<>();
        else regions.clear();
        if (cutPoints == null) cutPoints = new ArrayList<>();
        else cutPoints.clear();
        bs = new boolean[number + 1];
        radianCentrePoints = new ArrayList<>();
        startAngles = new float[number];
        for (int i = 0; i < number; i++) {
            if (i == number - 1) {
                //最后一个区域有可能因为除不尽会有空留角度
                sweepAngle = 360 - sweepAngle * (number - 1);
            }
            //算出每条开始角度圆上点坐标
            int x = (int) (radius * Math.cos(startAngle * PI / 180));
            int y = (int) (radius * Math.sin(startAngle * PI / 180));
            cutPoints.add(new Point(x, y));
            //算出圆弧中心点坐标
            int radianCentre = startAngle + sweepAngle / 2;
            int rx = (int) (radius * Math.cos(radianCentre * PI / 180));
            int ry = (int) (radius * Math.sin(radianCentre * PI / 180));
            radianCentrePoints.add(new Point(rx, ry));
            //重新设置下一个起始度数
            startAngle += sweepAngle;
            /** **** **  3.保存当前扇形区域的数据  ** **** **/
            Path p = new Path();
            p.moveTo(0, 0);
            p.lineTo(x, y);
            p.addArc(areaRectF, startAngle - sweepAngle, sweepAngle);
            startAngles[i] = startAngle - sweepAngle;//存放开始角度
            p.lineTo(0, 0);
            p.close();
            paths.add(p);
            RectF r = new RectF();
            p.computeBounds(r, true);//计算path所占用的空间，放入矩形 r 中
            Region region = new Region();
            region.setPath(p, new Region((int) r.left, (int) r.top, (int) r.right, (int) r.bottom));
            bs[i] = false;//默认没有点击
            regions.add(region);
        }
        /** **** **  中心圆部分区域  ** **** **/
        Path centreCrclePath = new Path();
        centreCrclePath.moveTo(0, 0);
        int centerRadius = radius / 3;//中心圆半径设置为外圆半径的1/3
        centreCrclePath.addCircle(0, 0, centerRadius, Path.Direction.CW);
        paths.add(centreCrclePath);
        RectF r = new RectF();
        centreCrclePath.computeBounds(r, true);
        Region centreCircle = new Region();
        centreCircle.setPath(centreCrclePath, new Region((int) r.left, (int) r.top, (int) r.right, (int) r.bottom));
        bs[number] = false;
        regions.add(centreCircle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //将坐标原点移动到view的中心
        canvas.translate(mWidth / 2, mHeight / 2);//将坐标原点移动到view的中心
        canvas.save();
        canvas.rotate(rotateAngle);//坐标系顺时针旋转
        for (int i = 0; i < number; i++) {//绘制弧型区域
            paint.setColor(bs[i] ? areaClickColor : areaColor);
            canvas.drawPath(paths.get(i), paint);
        }
        if (number > 1) {//如果只有一个就不需要绘制分割线了
            //绘制分割线
            for (int i = 0; i < cutPoints.size(); i++) {
                Point point = cutPoints.get(i);
                paint.setColor(Color.WHITE);
                paint.setStrokeWidth(3);
                canvas.drawLine(0, 0, point.x, point.y, paint);
            }
        }
        //绘制中心圆
        paint.setColor(bs[number] ? centreClickColor : centreColor);
        canvas.drawPath(paths.get(paths.size() - 1), paint);
//        canvas.restore();//回到没有旋转坐标系的时候
    }

    /**
     * 设置将其中的一块区域正对上方的方向
     * 正对上方的 x坐标为0,y坐标根据坐标系应该是小于0（y轴往上是递减）
     */
    private int getRotateAngle() {
        int aa = 0;
        boolean isEnfilade = false;
        for (int i = 0; i < number; i++) {
            Point point = radianCentrePoints.get(i);//获取每个弧型区域的中心点（圆上）
            if (point.x == 0 && point.y < 0) {
                Log.e(TAG, "onDraw :  出现了 迪迦奥特曼 --> ");
                isEnfilade = true;
            }
        }
        if (isEnfilade) return 0;//已经有正对上方的区域了,则旋转角度返回0
        else {//(0,-radius)坐标不在顶部弧区域的正中心
            for (int i = 0; i < regions.size(); i++) {
                if (regions.get(i).contains(0, -radius)) {//找到包含（0，-radius）坐标点的区域
                    float startAngle = startAngles[i];//该区域开始的角度
                    //计算该区域当前的中心角度与当前的（0,-radius）的角度差
                    int cenAngle = (int) (270 - (startAngle + sweepAngle / 2));
                    Log.e(TAG, "需要旋转的度数 :  cenAngle --> " + cenAngle);
                    aa = cenAngle;
                    break;
                }
            }
        }
        return aa;
    }

    public interface AreaClick {
        void callback(int posion);
    }

    public void setAreaClickListener(AreaClick listener) {
        mListener = listener;
    }

    private void updateRegions(float cenAngle) {
        //将首次的开始角度设为需要旋转的度数
        startAngle = (int) cenAngle;
        for (int i = 0; i < number; i++) {
            if (i == number - 1) {
                //最后一个区域有可能因为除不尽会有空留角度
                sweepAngle = 360 - sweepAngle * (number - 1);
            }
            //算出圆上的点坐标
            int x = (int) (radius * Math.cos(startAngle * PI / 180));
            int y = (int) (radius * Math.sin(startAngle * PI / 180));
            startAngle += sweepAngle;
            /** **** **  重新设置当前扇形区域的数据  ** **** **/
            Path p = new Path();
            p.moveTo(0, 0);
            p.lineTo(x, y);
            p.addArc(areaRectF, startAngle - sweepAngle, sweepAngle);
            startAngles[i] = startAngle - sweepAngle;//存放开始角度
            p.lineTo(0, 0);
            p.close();
            RectF r = new RectF();
            p.computeBounds(r, true);//计算path所占用的空间，放入矩形 r 中
            Region region = new Region();
            region.setPath(p, new Region((int) r.left, (int) r.top, (int) r.right, (int) r.bottom));
            regions.set(i, region);//重新设置扇形区域
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //必须减去坐标偏移量
                int x = (int) (event.getX() - dx);
                int y = (int) (event.getY() - dy);
                for (int i = 0; i < regions.size(); i++) {
                    Region region = regions.get(i);
                    if (i < regions.size() - 1) {//中心圆的区域也同样包含所有弧形区域，所以将圆心区域区分开（最后一个是圆心区域）
                        if (region.contains(x, y) && !regions.get(regions.size() - 1).contains(x, y)) {//如果当前不是圆心区域，并且包含触摸坐标
                            bs[i] = !bs[i];
                            if (mListener != null) mListener.callback(i);
                        }
                    } else if (regions.get(regions.size() - 1).contains(x, y)) {//如果当前是圆心区域，并且包含触摸坐标
                        bs[i] = !bs[i];
                        if (mListener != null) mListener.callback(i);
                    }
                }
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                for (int i = 0; i < bs.length; i++) bs[i] = false;
                invalidate();
                break;
        }
        return true;
    }

}



























