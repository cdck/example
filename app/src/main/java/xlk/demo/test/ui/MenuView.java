package xlk.demo.test.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import xlk.demo.test.R;

/**
 * @author Created by xlk on 2020/12/14.
 * @desc
 */
public class MenuView extends LinearLayout {

    private ImageView iv_menu;

    public MenuView(Context context) {
        super(context);
    }

    public MenuView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MenuView);
        Drawable drawable = typedArray.getDrawable(R.styleable.MenuView_drawable);
        View inflate = LayoutInflater.from(context).inflate(R.layout.menu_view, this);
        iv_menu = inflate.findViewById(R.id.iv_menu);
        iv_menu.setImageDrawable(drawable);
    }

}
