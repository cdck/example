package xlk.demo.test.tree;

import java.util.List;

/**
 * @author by xlk
 * @date 2020/7/2 14:22
 * @desc 说明
 */
public class GroupInfo extends Node {
    private int groupId;
    private String groupName;
    private int pid;
    private Node parent;

    public GroupInfo(int groupId, String groupName) {
        this.groupId = groupId;
        this.groupName = groupName;
    }

    @Override
    int getId() {
        return groupId;
    }

    public int getPid() {
        return pid;
    }

    @Override
    String getName() {
        return groupName;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
        pid = parent.getId();
    }

}
