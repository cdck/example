package xlk.demo.test;

import android.os.Environment;

/**
 * @author Created by xlk on 2020/11/23.
 * @desc
 */
public class Constant {
    public static final String ROOT_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/测试目录/";
    public static final String INI_FILE_PATH = ROOT_DIR + "/test.ini";
}
