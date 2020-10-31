package xlk.demo.test;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import xlk.clicklibrary.SingleClick;


public class JavaTestActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = "JavaTestActivity-->";
    private int count = 0;
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_java_test);
        tv = findViewById(R.id.single_click_log);
        findViewById(R.id.single_click_test).setOnClickListener(this);
        findViewById(R.id.single_click_test1).setOnClickListener(this);

    }

    //默认点击间隔为1秒，except：排除
    @SingleClick(except = {R.id.single_click_test})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.single_click_test:
                Log.i(TAG, "onClick 点击事件");
                break;
            case R.id.single_click_test1:
                Log.d(TAG, "onClick 点击事件1");
                break;
        }
    }
}
