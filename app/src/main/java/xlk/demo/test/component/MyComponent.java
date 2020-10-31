package xlk.demo.test.component;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.binioter.guideview.Component;

import xlk.demo.test.R;

/**
 * @author by xlk
 * @date 2020/8/18 10:22
 * @desc
 */
public class MyComponent implements Component {
    @Override
    public View getView(LayoutInflater inflater) {
        RelativeLayout ll = (RelativeLayout) inflater.inflate(R.layout.component_layout, null);
        ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("", "引导层被点击了");
            }
        });
        return ll;
    }

    @Override
    public int getAnchor() {
        return Component.ANCHOR_TOP;
    }

    @Override
    public int getFitPosition() {
        return Component.FIT_CENTER;
    }

    @Override
    public int getXOffset() {
        return 0;
    }

    @Override
    public int getYOffset() {
        return 0;
    }
}
