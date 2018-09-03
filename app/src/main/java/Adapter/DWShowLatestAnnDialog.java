package Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import zj.com.mc.R;

/**
 * Created by BYJ on 2017/3/14.
 */
public class DWShowLatestAnnDialog extends Dialog {
    private Activity activity;
    private Map annMap;
    private TextView tv_AnnContent;
    private TextView tv_AnnTitle;

    public DWShowLatestAnnDialog(Context context) {
        super(context);
    }

    public DWShowLatestAnnDialog(Activity activity, Map annMap) {
        super(activity, R.style.dialog_LatestAnn); //dialog的样式
        this.activity = activity;
        this.annMap = annMap;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_latestann);
        tv_AnnContent = (TextView) findViewById(R.id.tv_AnnContent);
        tv_AnnTitle = (TextView) findViewById(R.id.tv_AnnTitle);

        WindowManager windowManager = activity.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = display.getWidth() * 3 / 5; // 设置dialog宽度为屏幕的3/5
        getWindow().setAttributes(lp);
        setCanceledOnTouchOutside(true);// 点击Dialog外部消失

        tv_AnnTitle.setText(annMap.get("Title").toString());
        tv_AnnContent.setText(annMap.get("Content").toString());
    }
}
