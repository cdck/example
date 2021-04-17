package xlk.demo.test.ini;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.FileUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import xlk.demo.test.Constant;
import xlk.demo.test.R;

public class IniActivity extends AppCompatActivity {

    private IniUtil ini;
    private EditText edt_section, edt_option, edt_value;
    private TextView tv_show;
    private String result = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ini);
        initView();
        initIniFile();
        ini = IniUtil.getInstance();
        boolean b = ini.loadFile(Constant.INI_FILE_PATH);
        addShow(b ? "加载ini文件成功" : "加载ini文件失败");
    }

    private void initIniFile() {
        File file = new File(Constant.INI_FILE_PATH);
        if (!file.exists()) {
            FileUtils.createOrExistsDir(Constant.ROOT_DIR);
            try {
                // 根据文件名获取assets文件夹下的该文件的inputstream
                InputStream fromFileIs = getResources().getAssets().open("test.ini");
                //获取文件的字节数
                int length = fromFileIs.available();
                //创建byte数组
                byte[] buffer = new byte[length];
                //字节输入流
                FileOutputStream fileOutputStream = new FileOutputStream(Constant.INI_FILE_PATH);
                BufferedInputStream bufferedInputStream = new BufferedInputStream(
                        fromFileIs);
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
                        fileOutputStream);
                int len = bufferedInputStream.read(buffer);
                while (len != -1) {
                    bufferedOutputStream.write(buffer, 0, len);
                    len = bufferedInputStream.read(buffer);
                }
                bufferedInputStream.close();
                bufferedOutputStream.close();
                fromFileIs.close();
                fileOutputStream.close();
                addShow("ini文件复制成功");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (file.exists()) {
            addShow("ini文件路径：" + Constant.INI_FILE_PATH);
        }
    }

    public void getIniValue(View view) {
        String section = edt_section.getText().toString();
        String option = edt_option.getText().toString();
        if (section.isEmpty() || option.isEmpty()) {
            return;
        }
        String s = ini.get(section, option);
        addShow(s);
    }

    public void addIniValue(View view) {
        String section = edt_section.getText().toString();
        String option = edt_option.getText().toString();
        String value = edt_value.getText().toString();
        if (section.isEmpty() || option.isEmpty()) {
            return;
        }
        ini.put(section, option, value);
        boolean store = ini.store();
        addShow(store ? "提交成功" : "提交失败");
    }

    private void addShow(String msg) {
        runOnUiThread(() -> {
            result += msg + "\n";
            tv_show.setText(result);
        });
    }

    private void initView() {
        edt_section = (EditText) findViewById(R.id.edt_section);
        edt_option = (EditText) findViewById(R.id.edt_option);
        edt_value = (EditText) findViewById(R.id.edt_value);
        tv_show = (TextView) findViewById(R.id.tv_show);
    }

    public void cleanLog(View view) {
        result = "";
        tv_show.setText("");
    }
}
