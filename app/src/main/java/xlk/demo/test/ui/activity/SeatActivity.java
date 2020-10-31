package xlk.demo.test.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

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
        seatBeans.add(new SeatBean(0, "设备_" + 0, "参会人_" + 0, 0f, 0f, 1));
        seatBeans.add(new SeatBean(1, "设备_" + 1, "参会人_" + 1, 0.1f, 0.1f, 1));
        seatBeans.add(new SeatBean(2, "设备_" + 2, "参会人_" + 2, 0.2f, 0.2f, 1));
        seatBeans.add(new SeatBean(3, "设备_" + 3, "参会人_" + 3, 0.7f, 0.3f, 1));
        seatBeans.add(new SeatBean(4, "设备_" + 4, "参会人_" + 4, 0.9f, 0.4f, 1));
    }

    private void initView() {
        findViewById(R.id.btn_show).setOnClickListener(this);
        findViewById(R.id.btn_arrangement).setOnClickListener(this);
        findViewById(R.id.btn_1920x1080).setOnClickListener(this);
        findViewById(R.id.btn_1920x1200).setOnClickListener(this);
        findViewById(R.id.btn_2560x1600).setOnClickListener(this);
        seat_view = (CustomSeatView) findViewById(R.id.seat_view);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_show: {
                break;
            }
            case R.id.btn_arrangement:

                break;
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
