package WPSutils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import java.io.File;

/**
 * WPS工具Android 实现 调用 WPS Office手机版接口
 */
public class Wpsutils {

    public static boolean wpsOpenFile(String path, Context context) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString(WpsModel.OPEN_MODE, WpsModel.OpenMode.READ_ONLY);
        bundle.putBoolean(WpsModel.SEND_CLOSE_BROAD, false);
        bundle.putString(WpsModel.THIRD_PACKAGE, context.getPackageName());
        bundle.putBoolean(WpsModel.CLEAR_TRACE, true);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setClassName(WpsModel.PackageName.NORMAL, WpsModel.ClassName.NORMAL);

        File file = new File(path);
        if (file == null || !file.exists()) {
            System.out.println("文件为空或者不存在");
            return false;
        }

        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        intent.putExtras(bundle);
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            System.out.println("打开wps异常："+e.toString());
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
