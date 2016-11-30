package zj.com.mc;

import android.app.Application;

import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.rest.RequestQueue;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Utils.CrashHandler;

/**
 * Created by dell on 2016/7/29.
 */
public class Myapplilcation extends Application{

   public static ExecutorService upLoadPool= Executors.newCachedThreadPool();

    public static ExecutorService getExecutorService(){
        return upLoadPool;
    }

    public static RequestQueue queue = null;
    @Override
    public void onCreate() {
        //初始化NoHttp
        NoHttp.initialize(this);
        //初始化请求队列
        queue = NoHttp.newRequestQueue();
        super.onCreate();

//全局异常捕获初始化
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
    }
}
