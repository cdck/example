package xlk.demo.test.ini;

import org.ini4j.Config;
import org.ini4j.Ini;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author Created by xlk on 2020/11/23.
 * @desc
 */
public class IniUtil {

    private static IniUtil instance;
    private final Ini ini;
    private File file;

    private IniUtil() {
        ini = new Ini();
        Config config = new Config();
        //不允许出现重复的部分和选项
        config.setMultiSection(false);
        config.setMultiOption(false);
        ini.setConfig(config);
    }

    public static IniUtil getInstance() {
        if (instance == null) {
            instance = new IniUtil();
        }
        return instance;
    }

    public boolean loadFile(String filePath) {
        try {
            File file = new File(filePath);
            ini.load(new FileReader(filePath));
            ini.setFile(file);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String get(String sectionName, String optionName) {
        String s = ini.get(sectionName, optionName);
        if (s == null) {
            s = "";
        }
        return s;
    }

    public void put(String sectionName, String optionName, Object value) {
        ini.put(sectionName, optionName, value);
    }

    public boolean store() {
        try {
            ini.store();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
