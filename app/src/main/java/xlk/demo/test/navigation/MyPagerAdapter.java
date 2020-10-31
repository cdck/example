package xlk.demo.test.navigation;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

/**
 * @author by xlk
 * @date 2020/8/20 17:09
 * @desc
 */
public class MyPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> fs;

    public MyPagerAdapter(@NonNull FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        fs = fragments;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Log.i("MyPagerAdapter", "getItem position=" + position);
        return fs.get(position);
    }

    @Override
    public int getCount() {
        Log.i("MyPagerAdapter", "getItem count=" + fs.size());
        return fs.size();
    }
}
