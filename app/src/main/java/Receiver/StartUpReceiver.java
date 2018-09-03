package Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import zj.com.mc.Register;

/**
 * Created by BYJ on 2017/8/10.
 */

public class StartUpReceiver extends BroadcastReceiver {
    public StartUpReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intent2=new Intent(context,Register.class);
        context.startActivity(intent2);
    }
}
