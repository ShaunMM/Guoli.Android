package zj.com.mc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.File;

/**
 * Created by BYJ on 2016/9/18.
 */
public class SearchWebview extends Activity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driverdatalistviewitem);
        Myapplilcation.addActivity(this);
        webView = (WebView) findViewById(R.id.loadwebview);

        WebSettings webSettings = webView.getSettings();
        webSettings.setSavePassword(false);
        webSettings.setSaveFormData(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(false);
        // 设置可以支持缩放
        webView.getSettings().setSupportZoom(true);
        // 设置出现缩放工具
        webView.getSettings().setBuiltInZoomControls(true);
        //扩大比例的缩放
        webView.getSettings().setUseWideViewPort(true);

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bundle");
        String locapath = bundle.getString("LocaPath");
        String id = bundle.getString("id");
        String keyword = bundle.getString("keywords");
        String pathhead = "file://";

        File file = new File(locapath);
        if (file.exists()) {
            String encoding = UtilisClass.getFileIncode(file);
            if (encoding != null && !encoding.equals("")) {
                System.out.println(encoding);
                webView.getSettings().setDefaultTextEncodingName(encoding);
            }

            String filepath = pathhead + locapath;
            webView.loadUrl(filepath);
            final String jsargs = "('" + id + "','" + keyword + "')";
            final String js = "javascript:window.jump" + jsargs;
            webView.setWebChromeClient(new WebChromeClient() {
                public void onProgressChanged(WebView view, int progress) {// 载入进度改变而触发
                    if (progress == 100) {
                        webView.loadUrl(js);
                    }
                }
            });
        }else{
            UtilisClass.showToast(this, "未能准确定位");
            Myapplilcation.removeActivity(this);
            fileList();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Myapplilcation.removeActivity(this);
    }
}