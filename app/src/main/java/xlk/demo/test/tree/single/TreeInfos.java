package xlk.demo.test.tree.single;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author by xlk
 * @date 2020/8/4 16:36
 * @desc
 */
public class TreeInfos {
    int pid;
    int id;
    String name;
    int level;
    boolean isExpand;
    CopyOnWriteArrayList<TreeInfos> children;

    public TreeInfos(int pid, int id, String name) {
        this.pid = pid;
        this.id = id;
        this.name = name;
    }

    public CopyOnWriteArrayList<TreeInfos> getChildren() {
        return children;
    }

    public void setChildren(CopyOnWriteArrayList<TreeInfos> children) {
        this.children = children;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public boolean isExpand() {
        return isExpand;
    }

    public void setExpand(boolean expand) {
        isExpand = expand;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
