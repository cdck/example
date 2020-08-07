package xlk.demo.test.tree;

import java.util.ArrayList;
import java.util.List;

/**
 * @author by xlk
 * @date 2020/7/2 14:23
 * @desc 说明
 */
public abstract class Node {
    public boolean isExpand;
    public List<Node> children = new ArrayList<>();

    abstract int getId();

    abstract int getPid();

    abstract String getName();

    public void clearChildren() {
        if (children != null) {
            children.clear();
        }
    }

    public List<Node> getChildren() {
        return children;
    }

    public void setChildren(List<Node> children) {
        this.children = children;
    }

    public boolean isExpand() {
        return isExpand;
    }

    public void setExpand(boolean expand) {
        isExpand = expand;
    }
}
