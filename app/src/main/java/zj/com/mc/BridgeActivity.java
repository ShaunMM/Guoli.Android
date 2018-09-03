package zj.com.mc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import WPSutils.Wpsutils;

/**
 * Created by BYJ on 2017/10/18.
 */

public class BridgeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bridge);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String filepath = bundle.getString("FilePath");
        Wpsutils.wpsOpenFile(filepath, BridgeActivity.this);
        finish();
    }
}
