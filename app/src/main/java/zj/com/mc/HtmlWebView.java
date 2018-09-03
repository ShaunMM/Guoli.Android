package zj.com.mc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

import java.io.File;

/**
 * HTML阅读工具通过WebView
 */
public class HtmlWebView extends Activity {
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.htmlwebviewlayout);
        Myapplilcation.addActivity(this);
        webView = (WebView) findViewById(R.id.html_web);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setUseWideViewPort(true);

        Intent intent = getIntent();
        String path = intent.getStringExtra("FilePath");
        String pathhead = "file://";
        String filepath = pathhead + path;
        File file = new File(path);

        String encoding = UtilisClass.getFileIncode(file);
        if (encoding != null && !encoding.equals("")) {
            System.out.println(encoding);
            webView.getSettings().setDefaultTextEncodingName(encoding);
        } else {
        }
        webView.loadUrl(pathhead + path);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Myapplilcation.removeActivity(this);
    }
}
