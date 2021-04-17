package xlk.demo.test.ui;

/**
 * @author Created by xlk on 2020/10/31.
 * @desc
 */
public class SeatBean {
    int devId;
    String devName;
    String memberName;
    /**
     * 左上角x坐标百分比
     */
    float x;
    /**
     * 左上角y坐标百分比
     */
    float y;
    /**
     * 朝向
     */
    int direction;
    boolean showPic;

    public SeatBean(int devId, String devName, String memberName, float x, float y, int direction, boolean showPic) {
        this.devId = devId;
        this.devName = devName;
        this.memberName = memberName;
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.showPic = showPic;
    }

    public int getDevId() {
        return devId;
    }

    public void setDevId(int devId) {
        this.devId = devId;
    }

    public String getDevName() {
        return devName;
    }

    public void setDevName(String devName) {
        this.devName = devName;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public boolean isShowPic() {
        return showPic;
    }

    public void setShowPic(boolean showPic) {
        this.showPic = showPic;
    }
}
