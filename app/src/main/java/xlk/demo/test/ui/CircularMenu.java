package xlk.demo.test.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.graphics.Path;
import android.util.Log;
import android.view.MotionEvent;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import xlk.demo.test.R;

/**
 * @author by xlk
 * @date 2020/7/31 18:20
 * @desc 自定义圆形菜单，功能个数是固定8个
 */
public class CircularMenu extends CustomView {
    private final boolean isShowLog = false;
    private final int menuCount;
    /**
     * 区域块的颜色
     */
    private final int regionColor = 0xFF1E5888;
    /**
     * 菜单之间的间隔角度
     */
    private final int spacingAngle;
    private Matrix mMapMatrix;
    private Path[] paths;
    private Region[] regions;
    private int[] flags;
    private int currentFlag = -1, touchFlag = -1, centerFlag;
    private Path centerPath;
    private Region centerRegion;
    private MenuClickListener mListener;
    private final String[] title = new String[]{
            "结束投影", "截图批注", "结束同屏", "加入同屏", "发起同屏", "发起投影", "会议笔记",/* "软键盘", "手写输入",*/ "呼叫服务"
    };
    final String backStr = "返回";
    private List<Bitmap> unPressedBitmaps = new ArrayList<>();
    private List<Bitmap> pressedBitmaps = new ArrayList<>();
    private TextPaint unPressedTextPaint, pressedTextPaint;

    public CircularMenu(Context context) {
        this(context, null);
    }

    public CircularMenu(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CustomMenu);
        spacingAngle = ta.getInt(R.styleable.CustomMenu_spacingAngle, 4);
        menuCount = title.length;
        init();
    }

    private void init() {
        initBitmap();
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

        //初始化未选中时的文本画笔
        unPressedTextPaint = new TextPaint();
        unPressedTextPaint.setColor(0xFFFFFFFF);
        unPressedTextPaint.setFakeBoldText(false);

        //初始化选中时的文本画笔
        pressedTextPaint = new TextPaint();
        pressedTextPaint.setColor(0xFF3475D3);
        pressedTextPaint.setFakeBoldText(false);

        // https://blog.csdn.net/m0_37041332/article/details/80680835
        //初始化绘制区域的画笔
        mDefaultPaint.setColor(regionColor);
        mDefaultPaint.setStrokeCap(Paint.Cap.ROUND);//设置画笔线帽
        mDefaultPaint.setAntiAlias(true);//开启抗锯齿
        mDefaultPaint.setDither(true);//设置防抖动
        mMapMatrix = new Matrix();
    }

    private void initBitmap() {
        unPressedBitmaps.add(drawableToBitmap(mCurrentContext.getResources().getDrawable(R.drawable.menu_stop_pro_t)));
        unPressedBitmaps.add(drawableToBitmap(mCurrentContext.getResources().getDrawable(R.drawable.menu_screenshot_t)));
        unPressedBitmaps.add(drawableToBitmap(mCurrentContext.getResources().getDrawable(R.drawable.menu_stop_screen_t)));
        unPressedBitmaps.add(drawableToBitmap(mCurrentContext.getResources().getDrawable(R.drawable.menu_join_screen_t)));
        unPressedBitmaps.add(drawableToBitmap(mCurrentContext.getResources().getDrawable(R.drawable.menu_start_screen_t)));
        unPressedBitmaps.add(drawableToBitmap(mCurrentContext.getResources().getDrawable(R.drawable.menu_start_pro_t)));
        unPressedBitmaps.add(drawableToBitmap(mCurrentContext.getResources().getDrawable(R.drawable.menu_meet_note_t)));
//        unPressedBitmaps.add(drawableToBitmap(mCurrentContext.getResources().getDrawable(R.drawable.menu_keyboard_t)));
//        unPressedBitmaps.add(drawableToBitmap(mCurrentContext.getResources().getDrawable(R.drawable.menu_handwriting_t)));
        unPressedBitmaps.add(drawableToBitmap(mCurrentContext.getResources().getDrawable(R.drawable.menu_call_service_t)));
        unPressedBitmaps.add(drawableToBitmap(mCurrentContext.getResources().getDrawable(R.drawable.menu_back_t)));

        pressedBitmaps.add(drawableToBitmap(mCurrentContext.getResources().getDrawable(R.drawable.menu_stop_pro_f)));
        pressedBitmaps.add(drawableToBitmap(mCurrentContext.getResources().getDrawable(R.drawable.menu_screenshot_f)));
        pressedBitmaps.add(drawableToBitmap(mCurrentContext.getResources().getDrawable(R.drawable.menu_stop_screen_f)));
        pressedBitmaps.add(drawableToBitmap(mCurrentContext.getResources().getDrawable(R.drawable.menu_join_screen_f)));
        pressedBitmaps.add(drawableToBitmap(mCurrentContext.getResources().getDrawable(R.drawable.menu_start_screen_f)));
        pressedBitmaps.add(drawableToBitmap(mCurrentContext.getResources().getDrawable(R.drawable.menu_start_pro_f)));
        pressedBitmaps.add(drawableToBitmap(mCurrentContext.getResources().getDrawable(R.drawable.menu_meet_note_f)));
//        pressedBitmaps.add(drawableToBitmap(mCurrentContext.getResources().getDrawable(R.drawable.menu_keyboard_f)));
//        pressedBitmaps.add(drawableToBitmap(mCurrentContext.getResources().getDrawable(R.drawable.menu_handwriting_f)));
        pressedBitmaps.add(drawableToBitmap(mCurrentContext.getResources().getDrawable(R.drawable.menu_call_service_f)));
        pressedBitmaps.add(drawableToBitmap(mCurrentContext.getResources().getDrawable(R.drawable.menu_back_f)));
    }

    private Bitmap drawableToBitmap(Drawable drawable) {
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        System.out.println("Drawable转Bitmap");
        Bitmap.Config config =
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        //注意，下面三行代码要用到，否在在View或者surfaceview里的canvas.drawBitmap会看不到图
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mMapMatrix.reset();

        // 这个区域的大小
        Region globalRegion = new Region(-w, -h, w, h);
        int minWidth = Math.min(w, h);
        minWidth *= 0.9;

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

        float bigStartAngle = 0;
        for (int i = 0; i < menuCount; i++) {
            Path path = paths[i];
            Region region = regions[i];
            if (i == 0) {
                bigStartAngle = spacingAngle;
            }
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
        pts[0] = event.getX();
        pts[1] = event.getY();
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
        //移动坐标系到屏幕中心
        canvas.translate(dx, dy);
//        canvas.scale(1, -1); // <-- 注意 翻转y坐标轴
        // 获取测量矩阵(逆矩阵)
        if (mMapMatrix.isIdentity()) {
            canvas.getMatrix().invert(mMapMatrix);
        }

        unPressedTextPaint.setTextSize(15);
        pressedTextPaint.setTextSize(15);
        for (int i = 0; i < menuCount; i++) {
            canvas.drawPath(paths[i], mDefaultPaint);
            boolean isPressed = currentFlag == flags[i];
            Rect strRect = new Rect();
            unPressedTextPaint.getTextBounds(title[i], 0, title[i].length(), strRect);

            Bitmap bitmap = isPressed ? pressedBitmaps.get(i) : unPressedBitmaps.get(i);
            Rect srcRect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            Rect dstRect = regions[i].getBounds();
            if (isShowLog) {
                Log.d("xlklog", "当前：" + title[i]);
                Log.i("xlklog", "四角位置111=" + dstRect.left + "," + dstRect.top + "," + dstRect.right + "," + dstRect.bottom);
            }
            if (i == 0) {
//                Log.d("xlklog", "当前：结束投影");
                int left = dstRect.left + (dstRect.right - dstRect.left) / 3;
                int top = dstRect.top + (dstRect.bottom - dstRect.top) / 6;
                int right = dstRect.right - (dstRect.right - dstRect.left) / 3;
                int bottom = dstRect.bottom - (dstRect.bottom - dstRect.top) / 2;
                dstRect.set(left, top, right, bottom);
            } else if (i == 1) {
//                Log.d("xlklog", "当前：截图批注");
                int left = dstRect.left + (dstRect.right - dstRect.left) / 4;
                int top = dstRect.top + (dstRect.bottom - dstRect.top) / 3;
                int right = dstRect.right - (dstRect.right - dstRect.left) / 3 - (dstRect.right - dstRect.left) / 12;
                int bottom = dstRect.bottom - (dstRect.bottom - dstRect.top) / 3;
                dstRect.set(left, top, right, bottom);
            } else if (i == 2) {
//                Log.d("xlklog", "当前：结束同屏");
                int left = dstRect.left + (Math.abs(dstRect.left) - Math.abs(dstRect.right)) / 3 + (Math.abs(dstRect.left) - Math.abs(dstRect.right)) / 12;
                int top = dstRect.top + (dstRect.bottom - dstRect.top) / 3;
                int right = dstRect.right - (Math.abs(dstRect.left) - Math.abs(dstRect.right)) / 4;
                int bottom = dstRect.bottom - (dstRect.bottom - dstRect.top) / 3;
                dstRect.set(left, top, right, bottom);
            } else if (i == 3) {
//                Log.d("xlklog", "当前：加入同屏");
                int left = dstRect.left + (Math.abs(dstRect.left) - Math.abs(dstRect.right)) / 3;
                int top = dstRect.top + (dstRect.bottom - dstRect.top) / 6;
                int right = dstRect.right - (Math.abs(dstRect.left) - Math.abs(dstRect.right)) / 3;
                int bottom = dstRect.bottom - (dstRect.bottom - dstRect.top) / 2;
                dstRect.set(left, top, right, bottom);
            } else if (i == 4) {
//                Log.d("xlklog", "当前：发起同屏");
                int left = dstRect.left + (Math.abs(dstRect.left) - Math.abs(dstRect.right)) / 3;
                int top = dstRect.top + (Math.abs(dstRect.top) - Math.abs(dstRect.bottom)) / 3;
                int right = dstRect.right - (Math.abs(dstRect.left) - Math.abs(dstRect.right)) / 3;
                int bottom = dstRect.bottom - (Math.abs(dstRect.top) - Math.abs(dstRect.bottom)) / 3;
                dstRect.set(left, top, right, bottom);
            } else if (i == 5) {
//                Log.d("xlklog", "当前：发起投影");
                int left = dstRect.left + (Math.abs(dstRect.left) - Math.abs(dstRect.right)) / 3 + (Math.abs(dstRect.left) - Math.abs(dstRect.right)) / 12;
                int top = dstRect.top + (Math.abs(dstRect.top) - Math.abs(dstRect.bottom)) / 4;
                int right = dstRect.right - (Math.abs(dstRect.left) - Math.abs(dstRect.right)) / 4;
                int bottom = dstRect.bottom - (Math.abs(dstRect.top) - Math.abs(dstRect.bottom)) / 3 - (Math.abs(dstRect.top) - Math.abs(dstRect.bottom)) / 12;
                dstRect.set(left, top, right, bottom);
            } else if (i == 6) {
//                Log.d("xlklog", "当前：会议笔记");
                int left = dstRect.left + (Math.abs(dstRect.right) - Math.abs(dstRect.left)) / 4;
                int top = dstRect.top + (Math.abs(dstRect.top) - Math.abs(dstRect.bottom)) / 4;
                int right = dstRect.right - (Math.abs(dstRect.right) - Math.abs(dstRect.left)) / 3 - (Math.abs(dstRect.right) - Math.abs(dstRect.left)) / 12;
                int bottom = dstRect.bottom - (Math.abs(dstRect.top) - Math.abs(dstRect.bottom)) / 3 - (Math.abs(dstRect.top) - Math.abs(dstRect.bottom)) / 12;
                dstRect.set(left, top, right, bottom);
            } else if (i == 7) {
//                Log.d("xlklog", "当前：呼叫服务");
                int left = dstRect.left + (Math.abs(dstRect.right) - Math.abs(dstRect.left)) / 3;
                int top = dstRect.top + (Math.abs(dstRect.top) - Math.abs(dstRect.bottom)) / 3;
                int right = dstRect.right - (Math.abs(dstRect.right) - Math.abs(dstRect.left)) / 3;
                int bottom = dstRect.bottom - (Math.abs(dstRect.top) - Math.abs(dstRect.bottom)) / 3;
                dstRect.set(left, top, right, bottom);
            }
            if (isShowLog) {
                Log.e("xlklog", "四角位置222=" + dstRect.left + "," + dstRect.top + "," + dstRect.right + "," + dstRect.bottom);
            }
            int x = (dstRect.left + (dstRect.right - dstRect.left) / 2) - strRect.width() / 2;
            int y = dstRect.bottom + strRect.height();
            canvas.drawText(title[i], x, y, isPressed ? pressedTextPaint : unPressedTextPaint);
            canvas.drawBitmap(bitmap, srcRect, dstRect, mDefaultPaint);
        }
        drawCenter(canvas);
    }

    private void drawCenter(Canvas canvas) {
        unPressedTextPaint.setTextSize(25);
        pressedTextPaint.setTextSize(25);
        canvas.drawPath(centerPath, mDefaultPaint);
        Bitmap bitmap = currentFlag == centerFlag
                ? pressedBitmaps.get(pressedBitmaps.size() - 1)
                : unPressedBitmaps.get(unPressedBitmaps.size() - 1);
        Rect srcRect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        Rect dstRect = centerRegion.getBounds();
        dstRect.set(dstRect.left / 2, dstRect.top / 2, dstRect.right / 2, dstRect.bottom / 2);
        dstRect.offset(0, -dstRect.bottom / 4);
        canvas.drawBitmap(bitmap, srcRect, dstRect, mDefaultPaint);
        if (currentFlag == centerFlag) {
            Rect rect = new Rect();
            pressedTextPaint.getTextBounds(backStr, 0, backStr.length(), rect);
            canvas.drawText(backStr, -rect.width() / 2, dstRect.bottom + rect.height() + 10, pressedTextPaint);
        } else {
            Rect rect = new Rect();
            unPressedTextPaint.getTextBounds(backStr, 0, backStr.length(), rect);
            canvas.drawText(backStr, -rect.width() / 2, dstRect.bottom + rect.height() + 10, unPressedTextPaint);
        }
    }

    public void setListener(MenuClickListener listener) {
        mListener = listener;
    }

    // 点击事件监听器
    public interface MenuClickListener {
        void onClicked(int index);
    }
}
