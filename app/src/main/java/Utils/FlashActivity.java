package Utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.util.List;

import zj.com.mc.R;

/**
 * Created by BYJ on 2017/8/2.
 */

public class FlashActivity extends Activity {
    private WebView mWebView;
    private Handler mHandler = new Handler();
    private String mFlashFilename;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashwebview);
        mWebView = (WebView) findViewById(R.id.flashwebview);
        setTitle("flash播放器");
        setTitleColor(Color.RED);
//        mWebView.getSettings().setPluginsEnabled(true);
        mWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
        Intent intent = getIntent();
        String str = intent.getStringExtra("flashpath");
//        if(str==null)
//            mFlashFilename=new String("file:///android_asset/kaka.swf");
//        else
        mFlashFilename = str;

        try {
            Thread.sleep(500);// 主线程暂停下，否则容易白屏，原因未知
        } catch (InterruptedException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();
        }

        mProgressDialog = ProgressDialog.show(this, "请稍等...", "加载flash中...", true);
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                // TODO Auto-generated method stub
                super.onProgressChanged(view, newProgress);
                System.out.println("newProgress:" + String.valueOf(newProgress));
                if (newProgress == 100) {
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            mProgressDialog.dismiss();
                        }
                    }, 500);
                }
            }
        });

        mWebView.loadUrl(mFlashFilename);
//        if(checkinstallornotadobeflashapk()){
//            mWebView.loadUrl(mFlashFilename);
//        }else{
//            installadobeapk();
//        }

    }


    //退出时关闭flash播放
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        mWebView.destroy();
        this.finish();
        System.gc();
    }

    //按下Back按键时关闭flash播放
    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        mWebView.destroy();
        this.finish();
        System.gc();
        super.onBackPressed();
    }

    //    //后台运行
//    @Override
//    protected void onUserLeaveHint() {
//        // TODO Auto-generated method stub
//        mWebView.destroy();
//        this.finish();
//        System.gc();
//        super.onUserLeaveHint();
//    }
    //检查机子是否安装的有Adobe Flash相关APK,l
    private boolean checkinstallornotadobeflashapk() {
        PackageManager pm = getPackageManager();
        List<PackageInfo> infoList = pm
                .getInstalledPackages(PackageManager.GET_SERVICES);
        for (PackageInfo info : infoList) {
            if ("com.adobe.flashplayer".equals(info.packageName)) {
                return true;
            }
        }
        return false;
    }

    //安装Adobe Flash APK
    private void installadobeapk() {
        mWebView.addJavascriptInterface(new AndroidBridge(), "android");
        mWebView.loadUrl("file:///android_asset/go_market.html");
    }

    private class AndroidBridge {
        public void goMarket() {
            mHandler.post(new Runnable() {
                public void run() {
                    Intent installIntent = new Intent(
                            "android.intent.action.VIEW");
                    installIntent.setData(Uri.parse("market://details?id=com.adobe.flashplayer"));
                    startActivity(installIntent);
                }
            });
        }
    }
}
