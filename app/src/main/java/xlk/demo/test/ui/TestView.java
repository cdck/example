package xlk.demo.test.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;

/**
 * @author by xlk
 * @date 2020/8/8 10:22
 * @desc
 */
public class TestView extends CustomView {

    public TestView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mDefaultPaint.setAntiAlias(true);
        mDefaultPaint.setColor(Color.BLACK);
        mDefaultPaint.setStyle(Paint.Style.STROKE);//描边
//        mDeafultPaint.setStyle(Paint.Style.FILL);//填充
//        mDeafultPaint.setStyle(Paint.Style.FILL_AND_STROKE);// 描边+填充
        mDefaultPaint.setStrokeWidth(3);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int dx = mViewWidth / 2;
        int dy = mViewHeight / 2;
        canvas.translate(dx, dy);  //移动坐标系到屏幕中心
//        canvas.scale(1, -1); // <-- 注意 翻转y坐标轴
        canvas.drawPoint(0, 0, mDefaultPaint);//绘制原点

        // 注意这个区域的大小
        Region globalRegion = new Region(-mViewWidth, -mViewHeight, mViewWidth, mViewHeight);
        int minWidth = mViewWidth > mViewHeight ? mViewHeight : mViewWidth;
        minWidth *= 0.8;

        int br = minWidth / 2;
        RectF bigCircle = new RectF(-br, -br, br, br);
        mDefaultPaint.setColor(Color.RED);
        canvas.drawRect(bigCircle, mDefaultPaint);

        int sr = minWidth / 4;
        RectF smallCircle = new RectF(-sr, -sr, sr, sr);
        mDefaultPaint.setColor(Color.BLUE);
        canvas.drawRect(smallCircle, mDefaultPaint);

        Path path = new Path();
        mDefaultPaint.setColor(Color.BLACK);
        path.addArc(bigCircle, 0, 45);
        path.arcTo(smallCircle, 40, -40);
        path.close();
        mDefaultPaint.setColor(Color.BLACK);
        canvas.drawPath(path, mDefaultPaint);

        Path path1 = new Path();
        mDefaultPaint.setColor(Color.BLACK);
        path1.addArc(bigCircle, 50, 45);
        path1.arcTo(smallCircle, 90, -40);
        path1.close();
        mDefaultPaint.setColor(Color.BLACK);
        canvas.drawPath(path1, mDefaultPaint);

        Path path2 = new Path();
        mDefaultPaint.setColor(Color.BLACK);
        path2.addArc(bigCircle, 100, 45);
        path2.arcTo(smallCircle, 140, -40);
        path2.close();
        mDefaultPaint.setColor(Color.BLACK);
        canvas.drawPath(path2, mDefaultPaint);


    }
}
