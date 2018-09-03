package WPSutils;

/**
 * Created by Dell on 2017/7/25.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyBroadCastReciver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case WpsModel.Reciver.ACTION_BACK://返回键广播
                System.out.println(WpsModel.Reciver.ACTION_BACK);
                Log.i("调用wps","------>"+WpsModel.Reciver.ACTION_BACK);
                break;
            case WpsModel.Reciver.ACTION_CLOSE://关闭文件时候的广播
                System.out.println(WpsModel.Reciver.ACTION_CLOSE);
                Log.i("调用wps","------>"+WpsModel.Reciver.ACTION_CLOSE);
                break;
            case WpsModel.Reciver.ACTION_HOME://home键广播
                System.out.println(WpsModel.Reciver.ACTION_HOME);
                Log.i("调用wps","------>"+WpsModel.Reciver.ACTION_HOME);
                break;
            case WpsModel.Reciver.ACTION_SAVE://保存广播
                System.out.println(WpsModel.Reciver.ACTION_SAVE);
                Log.i("调用wps","------>"+WpsModel.Reciver.ACTION_SAVE);
                break;
            default:
                break;
        }
    }
}