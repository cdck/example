package xlk.demo.test.aspectjx;

import android.content.Context;
import android.content.pm.PackageManager;

import java.util.Arrays;
import java.util.List;

/**
 * @author Created by xlk on 2020/9/12.
 * @desc
 */
public class PermissionUtil {
    /**
     * 返回应用程序在清单文件中注册的权限
     */
    static List<String> getManifestPermissions(Context context) {
        try {
            return Arrays.asList(context.getPackageManager().getPackageInfo(context.getPackageName(),
                    PackageManager.GET_PERMISSIONS).requestedPermissions);
        } catch (PackageManager.NameNotFoundException ignored) {
            return null;
        }
    }
}
