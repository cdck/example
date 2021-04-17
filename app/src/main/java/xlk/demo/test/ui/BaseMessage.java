package xlk.demo.test.ui;

import android.graphics.Bitmap;

/**
 * @author Created by xlk on 2020/12/19.
 * @desc
 */
public class BaseMessage {
    public int color;
    public int percent;
    public String content;
    public Bitmap image;

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
}
