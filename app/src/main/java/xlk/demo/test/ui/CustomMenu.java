package xlk.demo.test.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.nio.file.Path;

/**
 * @author by xlk
 * @date 2020/7/31 18:20
 * @desc
 */
public class CustomMenu extends View {
    private Region circleRegion;
    private Path circlePath;
    private Paint mDeafultPaint;
    private final int defaultWidth = 300, defaultHeight = 300;
    private int width, height;

    public CustomMenu(Context context) {
        this(context, null);
    }

    public CustomMenu(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomMenu(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthsize = MeasureSpec.getSize(widthMeasureSpec);      //取出宽度的确切数值
        int widthmode = MeasureSpec.getMode(widthMeasureSpec);      //取出宽度的测量模式

        int heightsize = MeasureSpec.getSize(heightMeasureSpec);    //取出高度的确切数值
        int heightmode = MeasureSpec.getMode(heightMeasureSpec);    //取出高度的测量模式
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        width = w;
        height = h;
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    private void init() {
        mDeafultPaint = new Paint();
        mDeafultPaint.setColor(0xFF4E5268);
        circleRegion = new Region();
    }
}
