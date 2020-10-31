package xlk.demo.test.material.design;

import com.chad.library.adapter.base.entity.SectionEntity;

/**
 * @author Created by xlk on 2020/9/9.
 * @desc
 */
public class Level0Section extends SectionEntity<ItemBean> {
    public Level0Section(boolean isHeader, String header) {
        super(isHeader, header);
    }

    public Level0Section(ItemBean itemBean) {
        super(itemBean);
    }
}
