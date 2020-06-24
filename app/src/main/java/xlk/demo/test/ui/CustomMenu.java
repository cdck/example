package xlk.demo.test.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * @author by xlk
 * @date 2020/6/15 11:02
 * @desc 自定义的菜单控件
 */
public class CustomMenu extends View {
    public CustomMenu(Context context) {
        this(context,null);
    }

    public CustomMenu(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CustomMenu(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr,0);
    }

    public CustomMenu(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


}
