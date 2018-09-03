package zj.com.mc;

import android.app.Activity;
import android.app.Application;

import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.download.DownloadQueue;
import com.yanzhenjie.nohttp.rest.RequestQueue;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Utils.CrashHandler;

/**
 * Created by dell on 2016/7/29.
 */
public class Myapplilcation extends Application {

    public static ExecutorService upLoadPool = Executors.newCachedThreadPool();
    public static RequestQueue queue = null;//请求队列
    public static DownloadQueue downloadQueue;//下载队列

    public static ExecutorService getExecutorService() {
        return upLoadPool;
    }

    @Override
    public void onCreate() {

        //初始化NoHttp
        NoHttp.initialize(this);
        //初始化请求队列
        queue = NoHttp.newRequestQueue();
        //初始化下载队列
        downloadQueue = NoHttp.newDownloadQueue();

        super.onCreate();
        //全局异常捕获初始化
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
    }

    private static List<Activity> activityList = new LinkedList();

    public static void addActivity(Activity activity) {
        activityList.add(activity);
    }

    public static void removeActivity(Activity a) {
        activityList.remove(a);
    }

    public static void exit() {
        for (Activity activity : activityList) {
            if (activity != null) {
                activity.finish();
            }
        }
        System.exit(0);
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
