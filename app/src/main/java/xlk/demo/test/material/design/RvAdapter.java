package xlk.demo.test.material.design;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseSectionQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * @author Created by xlk on 2020/9/9.
 * @desc
 */
public class RvAdapter extends BaseQuickAdapter<String, BaseViewHolder>/*extends BaseSectionQuickAdapter<>*/ {

    public RvAdapter(int layoutResId, @Nullable List<String> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {

    }
}
