package xlk.demo.test.ui.activity;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.text.Layout;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/9/20
 * 不能旋转 点击定位精确
 */

public class CircleView extends View {
    //    private  float RADIUS = 60f;
    private float RADIUS;

    private static final int[] COLORS = new int[]{0xFFF06292, 0xFF81C784, 0xFFE57373, 0xff9575cd, 0xffffb74d, 0xffba68c8, 0xffaed581, 0xffd4e157, 0xff7986cb};

    private TextPaint textPaint;
    private TextPaint textPaint1;
    private TextPaint textPaint2;
    private TextPaint textPaint3;
    private TextPaint textPaint4;
    private Paint mPaint;
    private Paint mPaint1;
    private Paint mPaint2;
    private Paint mPaint3, mPaint4;
    private Context mContext;
    private RectF rectF;    //矩形

    private int mWidth;
    private int mHeight;
    List<Map<String, String>> list = new ArrayList<>();

    /**
     * 大园半径
     */
    private float bigR;

    public CircleView(Context context) {
        super(context);
        mContext = context;
    }

    public CircleView(Context context, AttributeSet attr) {
        super(context, attr);
        mContext = context;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widhtSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        mWidth = widhtSize;
        mHeight = heightSize;
        System.out.println("w==" + mWidth + ",h==" + mHeight);


        final int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        final int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        final int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        final int heightSpecSize = MeasureSpec.getMode(heightMeasureSpec);
        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(mWidth, mHeight);
        } else if (widthSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(mWidth, heightSpecSize);
        } else if (heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSpecSize, mHeight);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        RADIUS = 360f / COLORS.length;//平分360度
        initPaint();
        drawUI(canvas);
    }


    private void initPaint() {
        mPaint = new Paint();
        initPaintConfig(mPaint, Color.TRANSPARENT, 0, Paint.Style.FILL);

        mPaint1 = new Paint();
        initPaintConfig(mPaint1, Color.WHITE, 0, Paint.Style.FILL);

        mPaint2 = new Paint();
        initPaintConfig(mPaint2, Color.WHITE, 0, Paint.Style.STROKE);

        mPaint3 = new Paint();
        initPaintConfig(mPaint3, Color.BLACK, 0, Paint.Style.STROKE);

        mPaint4 = new Paint();
        initPaintConfig(mPaint4, 0xffffb74d, 2, Paint.Style.STROKE);


        textPaint = new TextPaint();//标题
        initTextPaint(textPaint, Color.WHITE, 40);

        textPaint1 = new TextPaint();//内容
        initTextPaint(textPaint1, Color.WHITE, 30);

        textPaint2 = new TextPaint();//LoGo
        initTextPaint(textPaint2, Color.BLACK, 40);

        textPaint3 = new TextPaint();//指标、计划
        initTextPaint(textPaint3, 0xffaed581, 25);

        textPaint4 = new TextPaint();//资金笔数
        initTextPaint(textPaint4, Color.WHITE, 20);

        //初始化区域
        rectF = new RectF();


    }

    /**
     * 配置Paint
     *
     * @param mPaint
     * @param black
     * @param stroke
     */
    private void initPaintConfig(Paint mPaint, int black, int mWidth, Paint.Style stroke) {
        mPaint.setColor(black);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(stroke);
        if (mWidth > 0)
            mPaint.setStrokeWidth(mWidth);
    }

    /**
     * 初始化TextPaint
     *
     * @param size
     */
    private void initTextPaint(TextPaint textPaint, int color, int size) {
        textPaint.setColor(color);
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(size);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    private void drawUI(Canvas canvas) {
        canvas.translate(mWidth / 2, mHeight / 2);//将画布坐标原点移到中心位置

        float currentStartAngle = 270f;//起始角度
        float r = (float) (Math.min(mWidth, mHeight) /3);//饼状图半径(取宽高里最小的值)
        bigR = r;
        System.out.println("大圆半径==" + r);

        rectF.set(-r, -r, r, r);


        for (int i = 0; i < list.size(); i++) {

            Map<String, String> map = list.get(i);

            mPaint.setColor(COLORS[i]);
            canvas.drawArc(rectF, currentStartAngle, RADIUS, true, mPaint);//6条圆弧 凑成一个大圆

            Path path = new Path();
            path.addArc(rectF, currentStartAngle, RADIUS);
            if (i == 0) {
                textPaint.setTextSize(30);
                canvas.drawTextOnPath(map.get("title"), path, 0, 30, textPaint);//圆弧文字 主要根据path路径
            } else {
                textPaint.setTextSize(40);
                canvas.drawTextOnPath(map.get("title"), path, 0, 50, textPaint);//圆弧文字 主要根据path路径

            }


            Path path1 = new Path();
            path1.addArc(rectF, currentStartAngle, RADIUS);
            if (i == 0) {

                canvas.drawCircle(0, 0, bigR - 35, mPaint4);// 小圆弧

                canvas.drawTextOnPath("指标", path1, -50, 60, textPaint3);
                canvas.drawTextOnPath("计划", path1, 45, 60, textPaint3);

                canvas.drawTextOnPath("13 笔", path1, -50, 90, textPaint4);
                canvas.drawTextOnPath("26 笔", path1, 45, 90, textPaint4);
            } else {
                canvas.drawTextOnPath(map.get("count"), path1, 0, 90, textPaint1);
            }
            currentStartAngle += RADIUS;
        }


        /**
         * 资金白色角度先
         */
        double angle = 70;//角度
        float radian = (float) Math.toRadians(angle);//角度换算弧度  根据弧度计算x y 坐标
        float x = (float) (Math.cos(radian) * (bigR - 50));//35间距 +15"资金"文字的一半
        float y = (float) (Math.sin(radian) * (bigR - 50));
        System.out.println("x==" + x + ",y==" + y);
        Path path2 = new Path();
        path2.lineTo(x, -(bigR - 50));//首次使用lineTo  先从中心坐标 然后再lineTo
        canvas.drawPath(path2, mPaint4);

        canvas.drawCircle(0, 0, bigR / 2, mPaint1);// 内圆


        //getWidth()表示绘制多宽后换行
        StaticLayout sl = new StaticLayout(generateCenterSpannableText(), textPaint2, getWidth(), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true);
        canvas.save();
        canvas.translate(0, -55);
        sl.draw(canvas);
        canvas.restore();


//        canvas.drawText("2018年"+"\n"+"10月20日", 0, 20, textPaint2);//内圆中添加Logo文字  也可以内圆中加Bitmap


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //中心坐标点
        int ww = getWidth() / 2;
        int hh = getHeight() / 2;

        // 获取点击屏幕时的点的坐标
        float x = event.getX();
        float y = event.getY();

        System.out.println("w==" + ww + ",h==" + hh + ",rx==" + x + ",ry==" + y);

        whichCircle(x, y);
        return super.onTouchEvent(event);
    }

    /**
     * 确定点击的点在哪个圆内
     *
     * @param x
     * @param y
     */
    private void whichCircle(float x, float y) {
        // 将屏幕中的点转换成以屏幕中心为原点的坐标点
        int ax = getWidth() / 2 - (int) x;
        int ay = getHeight() / 2 - (int) y;
        double ju = Math.sqrt((ax * ax + (ay * ay)));//根据x,y 坐标获取三角形的第三条斜边(开平方根)
        int m = (int) ju;
        System.out.println("半径范围==" + m);
        if (m <= bigR / 2) {//内圆的半径是bigR/2  如果点击的范围在140之间   是内圆
            listener.onItemSelect(20);//随便传的一个数据，区分大圆的触摸事件
            System.out.println("我是内圆");
        } else {
            //大圆范围
            if (m <= bigR) {//如果点击的范围不超过大圆的半径
                int aa = getRotationBetweenLines(mWidth / 2, mHeight / 2, x, y);//中心点与触摸点的夹角 角度数
                System.out.println("触摸角度==" + aa);
                if (aa <= 20) {
                    listener.onItemSelect(18);//指标
                    return;
                } else if (aa > 20 && aa <= 40) {
                    listener.onItemSelect(19);//计划
                    return;
                }
                int c = aa / (int) RADIUS;
                listener.onItemSelect(c);
                System.out.println("触摸角度==" + aa + ",c==" + c);
                try {
                    Log.i("applog", "数据==" + list.get(c));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }


    }

    /**
     * 获取两条线的夹角 角度
     *
     * @param centerX
     * @param centerY
     * @param xInView
     * @param yInView
     * @return
     */
    private int getRotationBetweenLines(float centerX, float centerY, float xInView, float yInView) {
        double rotation = 0;

        double k1 = (double) (centerY - centerY) / (centerX * 2 - centerX);
        double k2 = (double) (yInView - centerY) / (xInView - centerX);
        double tmpDegree = Math.atan((Math.abs(k1 - k2)) / (1 + k1 * k2)) / Math.PI * 180;

        if (xInView > centerX && yInView < centerY) {  //第一象限
            rotation = 90 - tmpDegree;
        } else if (xInView > centerX && yInView > centerY) { //第二象限
            rotation = 90 + tmpDegree;
        } else if (xInView < centerX && yInView > centerY) { //第三象限
            rotation = 270 - tmpDegree;
        } else if (xInView < centerX && yInView < centerY) { //第四象限
            rotation = 270 + tmpDegree;
        } else if (xInView == centerX && yInView < centerY) {
            rotation = 0;
        } else if (xInView == centerX && yInView > centerY) {
            rotation = 180;
        }
        return (int) rotation;
    }

    String[] title = {"资金", "出差", "公章", "加班", "用品", "评价", "会议", "值班", "请假"};

    /**
     * 传递数据并重新刷新View
     */
    public void setList() {
        for (int a = 0; a < title.length; a++) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("title", title[a]);
            map.put("count", a * 10 + " 人");
            list.add(map);
        }
//        if (null == list) {
//            list = new ArrayList<>();
//        }
//        this.list = list;
        postInvalidate();
    }

    //定义一个接口对象listerner
    private OnItemSelectListener listener;

    //获得接口对象的方法。
    public void setOnItemSelectListener(OnItemSelectListener listener) {
        this.listener = listener;
    }

    //定义一个接口
    public interface OnItemSelectListener {
        void onItemSelect(int index);
    }

    private SpannableString generateCenterSpannableText() {

        String days = "2018年\n09月22日";
        SpannableString spannableString = new SpannableString(days);

        int indext = days.indexOf("年");
        String name = days.substring(0, indext + 1);
        String date = days.substring(indext + 1);
//        System.out.println("s==" + spannableString.length() + "indext==" + indext + ",name==" + name + ",date==" + date);

        spannableString.setSpan(new RelativeSizeSpan(1.5f), 0, name.length(), 0);

        ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#0099EE"));
        spannableString.setSpan(colorSpan, name.length(), spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

        return spannableString;
    }

}
