package zj.com.mc;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;

import java.util.List;

/**
 * Created by Dell on 2017/10/13.
 */

/**
 * 功能：WebView加载播放SWF文件
 *
 * 安装插件：ADOBE FLASH PLAYER插件的版本必须高于SWF播放文件默认的播放器版本
 *
 * 包名：com.adobe.flashplayer
 *
 */

public class SWFPlayerActivity extends Activity {

    private WebView mWebView;
    private Intent swfintent;
    private boolean hasAdobePlayer = false;// ADOBE FLASH PLAYER插件安装状态
    private String HTMLURL = "file:///android_asset/abc.swf";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏标题栏
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_swf);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String filepath = bundle.getString("FilePath");

        swfintent = new Intent();
        mWebView = (WebView) findViewById(R.id.web_flash);

        // WebView相关设置
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setDefaultTextEncodingName("GBK");

        if (OnCheck() == true) {
            mWebView.loadUrl("file://" + filepath);
        } else {
            // Toast.makeText(SWFPlayerActivity.this, "ADOBE FLASH PLAYER插件未安装",
            // Toast.LENGTH_SHORT).show();
            new AlertDialog.Builder(this).setIcon(R.mipmap.hint)
                    .setTitle("温馨提醒：")
                    .setMessage(
                            "Flash Player未安装或版本过低，请下载安装新版本后重试~")
                    .setPositiveButton("重试",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    swfintent.setClass(SWFPlayerActivity.this,
                                            SWFPlayerActivity.class);
                                    startActivity(swfintent);
                                }
                            })
                    .setNegativeButton("退出",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                                    am.restartPackage(getPackageName());
                                    swfintent.setClass(SWFPlayerActivity.this,
                                            SWFPlayerActivity.class);
                                    Intent i = new Intent(Intent.ACTION_MAIN);
                                    i.addCategory(Intent.CATEGORY_HOME);
                                    swfintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(swfintent);
                                    startActivity(i);
                                }
                            }).show();
        }
    }

    /**
     * 判断是否安装ADOBE FLASH PLAYER插件
     *
     * @return
     */
    public boolean OnCheck() {
        // 判断是否安装ADOBE FLASH PLAYER插件
        PackageManager pm = getPackageManager();
        List<PackageInfo> lsPackageInfo = pm.getInstalledPackages(0);

        for (PackageInfo pi : lsPackageInfo) {
            if (pi.packageName.contains("com.adobe.flashplayer")) {
                hasAdobePlayer = true;
                break;
            }
        }
        // 如果插件安装一切正常
        if (hasAdobePlayer == true) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onPause() {
    mWebView.pauseTimers();
    if (isFinishing()) {
        mWebView.loadUrl("about:blank");
        setContentView(new FrameLayout(this));
    }
    super.onPause();
}

    @Override
    protected void onResume() {
        super.onResume();
    }
}
