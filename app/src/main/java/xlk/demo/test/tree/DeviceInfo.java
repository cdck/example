package xlk.demo.test.tree;

/**
 * @author by xlk
 * @date 2020/7/2 14:21
 * @desc 说明
 */
public class DeviceInfo extends Node {
    private int deviceId;
    private String deviceName;
    private int pid;
    private Node parent;

    public DeviceInfo(int deviceId, String deviceName) {
        this.deviceId = deviceId;
        this.deviceName = deviceName;
    }

    public DeviceInfo(int deviceId, String deviceName, int pid) {
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.pid = pid;
    }

    @Override
    int getId() {
        return deviceId;
    }

    @Override
    public int getPid() {
        return pid;
    }

    @Override
    String getName() {
        return deviceName;
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
