package xlk.demo.test.aspectjx;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

/**
 * @author Created by xlk on 2020/9/12.
 * @desc
 */
public class ApplyPermissionFragment extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setGravity(Gravity.START | Gravity.TOP);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.x = 0;
        params.y = 0;
        params.height = 1;
        params.width = 1;
        getWindow().setAttributes(params);
    }

    public void apply(String[] pers, int requestCode) {
        ActivityCompat.requestPermissions(this, pers, requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
