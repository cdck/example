package xlk.demo.test.component;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.binioter.guideview.Component;

import xlk.demo.test.R;

/**
 * @author by xlk
 * @date 2020/8/18 14:01
 * @desc
 */
public class LottieComponent implements Component {
    @Override
    public View getView(LayoutInflater inflater) {
        LinearLayout ll = (LinearLayout) inflater.inflate(R.layout.component_lottie_layout, null);
        ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        return ll;
    }

    @Override
    public int getAnchor() {
        return Component.ANCHOR_RIGHT;
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
