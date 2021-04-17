package xlk.demo.test.media.decoder;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.os.Build;
import android.util.Log;

import com.blankj.utilcode.util.LogUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Created by xlk on 2021/1/13.
 * @desc
 */
public class JfVideoSupUtil {
    private static final String TAG = "JfVideoSupUtil-->";
    private static Map<String, String> codecMap = new HashMap<>();

    static {
        codecMap.put("h264", "video/avc");

    }


    public static String findVideoCodecName(String ffcodecname) {
        if (codecMap.containsKey(ffcodecname)) {
            return codecMap.get(ffcodecname);
        }
        return "";
    }


    //@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public static boolean isSupCodec(String ffcodecname) {
        boolean supVideo = false;
        MediaCodecList mediaCodecList = new MediaCodecList(MediaCodecList.ALL_CODECS);
        MediaCodecInfo[] codecInfos = mediaCodecList.getCodecInfos();
        Log.d(TAG, "数量=" + codecInfos.length);
        for (MediaCodecInfo codecInfo : codecInfos) {
            String msg = "";
            msg += codecInfo.getName() + ",是否是编码器=" + codecInfo.isEncoder() + ",";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                msg += "\n是否硬件加速=" + codecInfo.isHardwareAccelerated()
                        + "\n是否由设备制造商提供的=" + codecInfo.isVendor()
                        + "\n是否是另一个基础编解码器的别名=" + codecInfo.isAlias()
                        + "\n是否仅为软件=" + codecInfo.isSoftwareOnly()
                        + "\n检索基础编解码器名称=" + codecInfo.getCanonicalName();
            }
            String[] supportedTypes = codecInfo.getSupportedTypes();
            for (String type : supportedTypes) {
                msg += type + "  ";
//                if (type.equals(findVideoCodecName(ffcodecname))) {
//                    supVideo = true;
//                    break;
//                }
            }
            if (codecInfo.isEncoder()) {
                Log.i(TAG, msg);
            } else {
                Log.d(TAG, msg);
            }
            if (supVideo) {
                break;
            }
        }
        return supVideo;
    }
}

