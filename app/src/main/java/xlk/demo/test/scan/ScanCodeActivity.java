package xlk.demo.test.scan;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.scan.HmsScan;
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions;

import xlk.demo.test.R;

public class ScanCodeActivity extends AppCompatActivity {
    private final String TAG = "ScanCodeActivity-->";
    private final int REQUEST_CODE_SCAN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_code);

    }

    public void scanCode(View view) {
//        Intent intent = new Intent(ScanCodeActivity.this, CaptureActivity.class);
//        startActivityForResult(intent, REQUEST_CODE);
        /* **** **  使用华为扫描库  ** **** */
        //申请权限之后，调用DefaultView扫码界面
        int result = ScanUtil.startScan(ScanCodeActivity.this, REQUEST_CODE_SCAN, new HmsScanAnalyzerOptions.Creator()
                .setHmsScanTypes(HmsScan.ALL_SCAN_TYPE).create());

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SCAN) {
            //处理扫描结果（在界面上显示）
            /*if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String result = bundle.getString(CodeUtils.RESULT_STRING);
                    ToastUtils.showLong("解析二维码结果:" + result);
                    LogUtils.d("解析二维码结果:" + result);
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    ToastUtils.showLong("解析二维码失败");
                    LogUtils.e("解析二维码失败");
                }
            }*/
            //从onActivityResult返回data中，用 ScanUtil.RESULT作为key值取到HmsScan返回值
            if (requestCode == REQUEST_CODE_SCAN) {
                Object obj = data.getParcelableExtra(ScanUtil.RESULT);
                if (obj instanceof HmsScan) {
                    String value = ((HmsScan) obj).getOriginalValue();
                    if (!TextUtils.isEmpty(value)) {
                        ToastUtils.showLong(value);
                        LogUtils.d("解析二维码结果:" + value);
                    }
                }
            }
        }
    }
}