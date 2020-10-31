package xlk.demo.test.ui;

import android.content.Context;
import android.graphics.Region;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import xlk.demo.test.R;

/**
 * @author Created by xlk on 2020/10/31.
 * @desc 会议室席位设置，可拖动席位
 */
public class CustomSeatView extends AbsoluteLayout {

    private final String TAG = "CustomSeatView-->";
    /**
     * 底图的宽高
     */
    private int width = 1300, height = 760;
    /**
     * 显示区域的宽高
     */
    private int viewWidth, viewHeight;
    /**
     * 拖动底图时按下的坐标
     */
    private float moveDownX, moveDownY;
    private int l = 0, t = 0, r, b;
    /**
     * 所有的座位
     */
    List<View> subViews = new ArrayList<>();
    /**
     * 所有座位所占的区域
     */
    List<Region> regions = new ArrayList<>();
    /**
     * 拖动底图时按下的时间
     */
    private long moveDownTime;
    /**
     * 存放选中的view的id
     */
    List<Integer> selectedIds = new ArrayList<>();
    /**
     * 按下时获取到的席位索引
     */
    int touchSeatIndex = -1;
    /**
     * 存放席位数据
     */
    private ArrayList<SeatBean> seatBeans = new ArrayList<>();

    /**
     * 设置显示区域的宽高
     */
    public void setViewSize(int viewWidth, int viewHeight) {
        this.viewWidth = viewWidth;
        this.viewHeight = viewHeight;
        Log.i(TAG, "setViewSize 显示区域大小=" + viewWidth + "," + viewHeight);
    }

    /**
     * 设置底图的宽高
     */
    public void setImgSize(int width, int height) {
        this.width = width;
        this.height = height;
        Log.i(TAG, "setImgSize 底图区域大小=" + width + "," + height);
    }

    /**
     * 设置选中席位
     */
    public void setSelected(int id) {
        for (int i = 0; i < subViews.size(); i++) {
            View view = subViews.get(i);
            int viewId = view.getId();
            if (viewId == id) {
                view.setSelected(!view.isSelected());
                if (selectedIds.contains(viewId)) {
                    selectedIds.remove(selectedIds.indexOf(viewId));
                } else {
                    selectedIds.add(viewId);
                }
                break;
            }
        }
    }

    /**
     * 获取当前点击的席位索引
     *
     * @param x x坐标
     * @param y y坐标
     * @return 返回-1表示没有点击到席位
     */
    private int getCurrentClickSeat(int x, int y) {
        for (int i = 0; i < regions.size(); i++) {
            Region region = regions.get(i);
            if (region.contains(x, y)) {
                return i;
            }
        }
        return -1;
    }

    public CustomSeatView(Context context) {
        this(context, null);
    }

    public CustomSeatView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomSeatView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public CustomSeatView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * 动态设置setLayoutParams后会执行
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width, height);
        this.width = width;
        this.height = height;
        Log.d(TAG, "onMeasure : --> width= " + width + ", height= " + height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        this.l = l;
        this.t = t;
        this.r = r;
        this.b = b;
        Log.d(TAG, "onLayout --> l, t, r, b= " + l + "," + t + "," + r + "," + b);
    }

    /**
     * 重新设置席位信息
     *
     * @param data 席位数据
     */
    public void addSeat(List<SeatBean> data) {
        removeAllViews();
        seatBeans.clear();
        seatBeans.addAll(data);
        subViews.clear();
        regions.clear();
        Log.i(TAG, "addSeat 最新宽高=" + width + "," + height);
        for (int i = 0; i < seatBeans.size(); i++) {
            SeatBean seatBean = seatBeans.get(i);
            View inflate = LayoutInflater.from(getContext()).inflate(R.layout.item_seat, null);
            RelativeLayout.LayoutParams ivParams = new RelativeLayout.LayoutParams(30, 30);
            RelativeLayout.LayoutParams seatLinearParams = new RelativeLayout.LayoutParams(120, 40);
            ImageView item_seat_iv = inflate.findViewById(R.id.item_seat_iv);
            LinearLayout item_seat_ll = inflate.findViewById(R.id.item_seat_ll);
            TextView item_seat_device = inflate.findViewById(R.id.item_seat_device);
            TextView item_seat_member = inflate.findViewById(R.id.item_seat_member);
            switch (seatBean.getDirection()) {
                //上
                case 0:
                    item_seat_iv.setImageResource(R.drawable.icon_seat_bottom);
                    ivParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                    ivParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                    seatLinearParams.addRule(RelativeLayout.BELOW, item_seat_iv.getId());
                    seatLinearParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                    break;
                //下
                case 1:
                    item_seat_iv.setImageResource(R.drawable.icon_seat_top);
                    seatLinearParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                    seatLinearParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                    ivParams.addRule(RelativeLayout.BELOW, item_seat_ll.getId());
                    ivParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                    break;
                //左
                case 2:
                    item_seat_iv.setImageResource(R.drawable.icon_seat_right);
                    ivParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                    ivParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                    seatLinearParams.addRule(RelativeLayout.BELOW, item_seat_iv.getId());
                    seatLinearParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                    break;
                //右
                case 3:
                    item_seat_iv.setImageResource(R.drawable.icon_seat_left);
                    ivParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                    ivParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                    seatLinearParams.addRule(RelativeLayout.BELOW, item_seat_iv.getId());
                    seatLinearParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                    break;
                default:
                    break;
            }
            item_seat_device.setText(seatBean.getDevName());
            item_seat_member.setText(seatBean.getMemberName());

            item_seat_iv.setLayoutParams(ivParams);
            item_seat_ll.setLayoutParams(seatLinearParams);
            //左上角x坐标
            float x1 = seatBean.getX();
            //左上角y坐标
            float y1 = seatBean.getY();
            if (x1 > 1) {
                x1 = 1;
            } else if (x1 < 0) {
                x1 = 0;
            }
            if (y1 > 1) {
                y1 = 1;
            } else if (y1 < 0) {
                y1 = 0;
            }
            int x = (int) (x1 * width);
            int y = (int) (y1 * height);
            AbsoluteLayout.LayoutParams params = new AbsoluteLayout.LayoutParams(
                    120, 70,
                    x, y);
            inflate.setLayoutParams(params);
            int devId = seatBean.getDevId();
            inflate.setId(devId);
            Region region = new Region(x, y, x + 120, y + 70);
            if (selectedIds.contains(devId)) {
                inflate.setSelected(true);
            }
            regions.add(region);
            subViews.add(inflate);
            addView(inflate);
        }
    }

    /**
     * 拖动席位后更新区域
     *
     * @param index 索引位
     * @param left  新的区域左边坐标
     * @param top   新的区域上边坐标
     */
    private void updateRegion(int index, int left, int top) {
        Region region = regions.get(index);
        region.set(left, top, left + 120, top + 70);
    }

    /**
     * 更新某席位的数据
     *
     * @param index 索引
     * @param x1    新的x坐标
     * @param y1    新的y坐标
     */
    private void updateSeatBean(int index, float x1, float y1) {
        SeatBean seatBean = seatBeans.get(index);
        float x = x1 / width;
        float y = y1 / height;
        seatBean.setX(x);
        seatBean.setX(y);
        Log.i(TAG, "updateSeatBean 更新列表数据 x=" + x + ",y=" + y + ",width=" + width + ",height=" + height);
    }

    /**
     * 获取最新的席位数据
     *
     * @return 返回拖动修改过的数据
     */
    public List<SeatBean> getSeatBean() {
        List<SeatBean> temps = new ArrayList<>();
        temps.addAll(seatBeans);
        return temps;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //是否可以左右拖动
        boolean canMoveX = !(viewWidth >= width);
        //是否可以上下拖动
        boolean canMoveY = !(viewHeight >= height);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                moveDownX = event.getX();
                moveDownY = event.getY();
                touchSeatIndex = getCurrentClickSeat((int) moveDownX, (int) moveDownY);
                moveDownTime = System.currentTimeMillis();
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if (touchSeatIndex == -1) {
                    float moveX = event.getX();
                    float moveY = event.getY();
                    float dx = moveX - moveDownX;//负数,说明是向左滑动
                    float dy = moveY - moveDownY;//负数,说明是向上滑动
                    int left = getLeft();
                    int top = getTop();
                    int right = getRight();
                    int bottom = getBottom();
                    if (canMoveX) {
                        //左
                        if (left == 0) {
                            //当前左边已经封顶了
                            if (dx < 0 && right >= viewWidth) {
                                //还往左边移动
                                this.l = (int) (left + dx);
                            } else {
                                this.l = 0;
                            }
                        } else if (left < 0) {
                            //当前已经超过最左边
                            if (right >= viewWidth) {
                                if (dx < 0) {
                                    this.l = (int) (left + dx);
                                } else if (dx > 0) {
                                    this.l = (int) (left + dx);
                                }
                            }
                        } else {
                            this.l = 0;
                        }
                        //右
                        r = width + this.l;
                        int i1 = viewWidth - r;
                        if (i1 > 0) {
                            r = viewWidth;
                            this.l += i1;
                        }
                    } else {
                        //不可以拖动X轴
                        this.l = 0;
                        r = width;
                    }
                    if (canMoveY) {
                        //上
                        if (top == 0) {
                            if (dy < 0 && bottom > viewHeight) {
                                t = (int) (top + dy);
                            } else {
                                t = 0;
                            }
                        } else if (top < 0) {
                            if (dy < 0 && bottom > viewHeight) {
                                t = (int) (top + dy);
                            } else if (dy > 0) {
                                t = (int) (top + dy);
                            }
                        } else {
                            t = 0;
                        }
                        //下
                        b = height + t;
                        int i = viewHeight - b;
                        if (i > 0) {
                            b = viewHeight;
                            t += i;
                        }
                    } else {
                        //不可以拖动Y轴
                        t = 0;
                        b = height;
                    }
                    this.layout(this.l, t, r, b);
                } else {
                    View view = subViews.get(touchSeatIndex);
                    AbsoluteLayout.LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
                    Log.e(TAG, "dragSeat :  --> 按下的坐标=【" + moveDownX + "," + moveDownY
                            + "】, view的原坐标=【" + layoutParams.x + "," + layoutParams.y + "】");
                    float moveX = event.getX();
                    float moveY = event.getY();
                    float dx = moveX - moveDownX;
                    float dy = moveY - moveDownY;
                    layoutParams.x += dx;
                    layoutParams.y += dy;
                    view.setLayoutParams(layoutParams);
                    Log.i(TAG, "updateRegion left=" + getLeft() + ",top=" + getTop() + ",right=" + getRight() + ",bottom=" + getBottom()
                            + ", 新的位置=" + layoutParams.x + "," + layoutParams.y);
                    //更新 regions
                    updateRegion(touchSeatIndex, layoutParams.x, layoutParams.y);
                    //更新数据
                    updateSeatBean(touchSeatIndex, layoutParams.x, layoutParams.y);
                    moveDownX = moveX;
                    moveDownY = moveY;
                    Log.i(TAG, "dragSeat :  move end --> ");
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                Log.i(TAG, "onTouchEvent 显示宽高：" + viewWidth + "," + viewHeight +
                        ",底图宽高：" + width + "," + height + ",canMoveX：" + canMoveX + ",canMoveY：" + canMoveY);
                Log.e(TAG, "dragView :   --> " + getLeft() + "," + getTop() + "," + getRight() + "," + getBottom());
                if (System.currentTimeMillis() - moveDownTime < 200) {
                    //点击
                    Log.i(TAG, "dragView 点击=" + touchSeatIndex);
                    if (touchSeatIndex != -1) {
                        setSelected(subViews.get(touchSeatIndex).getId());
                    }
                }
                break;
            }
            default:
                break;
        }
        return true;
    }
}
