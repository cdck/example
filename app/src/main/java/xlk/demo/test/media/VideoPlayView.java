package xlk.demo.test.media;

import android.content.Context;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * @author by xlk
 * @date 2020/5/30 14:26
 * @desc 说明
 */
public class VideoPlayView extends SurfaceView implements SurfaceHolder.Callback {

    public static String strVideo;//视频文件路径
    private VideoDecodeThread thread;
    private SoundDecodeThread soundDecodeThread;

    public VideoPlayView(Context context) {
        this(context, null);
    }

    public VideoPlayView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoPlayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.e("VideoPlayView", "surfaceCreated 文件路径：" + strVideo);
        if (strVideo == null) return;
        if (thread == null) {
            thread = new VideoDecodeThread(holder.getSurface(), strVideo);
            thread.start();
        }
        if (soundDecodeThread == null) {
            soundDecodeThread = new SoundDecodeThread(strVideo);
            soundDecodeThread.start();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.e("VideoPlayView", "surfaceChanged");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.e("VideoPlayView", "surfaceDestroyed");
        if (thread != null) {
            thread.quit();
        }
        if (soundDecodeThread != null) {
            soundDecodeThread.interrupt();
        }
    }

    public void restore() {

    }

    public void pause() {

    }
}
