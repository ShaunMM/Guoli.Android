package zj.com.mc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.io.File;

/**
 * Created by dell on 2016/9/19.
 */
public class HtmlWebView extends Activity {
    private WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.htmlwebviewlayout);
        webView= (WebView) findViewById(R.id.html_web);

        //支持javascript
        webView.getSettings().setJavaScriptEnabled(true);
// 设置可以支持缩放
        webView.getSettings().setSupportZoom(true);
// 设置出现缩放工具
        webView.getSettings().setBuiltInZoomControls(true);
//扩大比例的缩放
        webView.getSettings().setUseWideViewPort(true);

        Intent intent =getIntent();
        String path=intent.getStringExtra("FilePath");
        String pathhead = "file://";
        String filepath=pathhead+path;
        File file=new File(path);

        String encoding=UtilisClass.getFileIncode(file);
        if (encoding!=null&&!encoding.equals("")) {
            System.out.println(encoding);
            webView.getSettings().setDefaultTextEncodingName(encoding);
        }else {
        }

        webView.loadUrl(pathhead+path);
//        webView.loadDataWithBaseURL(null,data.description, "text/html", "utf-8",null);


    }
}
