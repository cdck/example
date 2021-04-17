package xlk.demo.test.ui.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import xlk.demo.test.R;
import xlk.demo.test.ui.CustomSeatView;
import xlk.demo.test.ui.SeatBean;

public class SeatActivity extends AppCompatActivity implements View.OnClickListener {
    List<SeatBean> seatBeans = new ArrayList<>();
    private CustomSeatView seat_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat);
        initView();
        initData();
        seat_view.post(new Runnable() {
            @Override
            public void run() {
                seat_view.setViewSize(seat_view.getWidth(), seat_view.getHeight());
                seat_view.addSeat(seatBeans);
            }
        });
    }

    private void initData() {
        for (int i = 0; i < 50; i++) {
            double x = Math.random();
            double y = Math.random();
            seatBeans.add(new SeatBean(i, "设备_" + i, "参会人_" + i, (float) x, (float) y, 1, true));
        }
    }

    private void initView() {
        findViewById(R.id.btn_show).setOnClickListener(this);
        findViewById(R.id.btn_arrangement).setOnClickListener(this);
        findViewById(R.id.btn_align_left).setOnClickListener(this);
        findViewById(R.id.btn_align_bottom).setOnClickListener(this);
        findViewById(R.id.btn_1300x760).setOnClickListener(this);
        findViewById(R.id.btn_1920x1080).setOnClickListener(this);
        findViewById(R.id.btn_1920x1200).setOnClickListener(this);
        findViewById(R.id.btn_2560x1600).setOnClickListener(this);
        seat_view = (CustomSeatView) findViewById(R.id.seat_view);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_show: {
                seat_view.setShowPic();
                break;
            }
            case R.id.btn_arrangement: {
                seat_view.sort();
                break;
            }
            case R.id.btn_align_left: {
                seat_view.alignLeft();
                break;
            }
            case R.id.btn_align_bottom: {
                seat_view.alignBottom();
                break;
            }
            case R.id.btn_1300x760: {
                seat_view.setBackgroundResource(R.drawable.room_bg_1300x760);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(1300, 760);
                seat_view.setLayoutParams(params);
                seat_view.setImgSize(1300, 760);
                seat_view.addSeat(seat_view.getSeatBean());
                break;
            }
            case R.id.btn_1920x1080: {
                seat_view.setBackgroundResource(R.drawable.room_bg_1920x1080);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(1920, 1080);
                seat_view.setLayoutParams(params);
                seat_view.setImgSize(1920, 1080);
                seat_view.addSeat(seat_view.getSeatBean());
                break;
            }
            case R.id.btn_1920x1200: {
                seat_view.setBackgroundResource(R.drawable.room_bg_1920x1200);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(1920, 1200);
                seat_view.setLayoutParams(params);
                seat_view.setImgSize(1920, 1200);
                seat_view.addSeat(seat_view.getSeatBean());
                break;
            }
            case R.id.btn_2560x1600: {
                seat_view.setBackgroundResource(R.drawable.room_bg_2560x1600);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(2560, 1600);
                seat_view.setLayoutParams(params);
                seat_view.setImgSize(2560, 1600);
                seat_view.addSeat(seat_view.getSeatBean());
                break;
            }
            default:
                break;
        }
    }
}
