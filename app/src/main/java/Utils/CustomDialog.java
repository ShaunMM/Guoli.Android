package Utils;

import android.app.Dialog;
import android.content.Context;

/**
 * Created by BYJ on 2016/8/14.
 */
public class CustomDialog extends Dialog {


    int layoutRes;//布局文件
    Context context;

    public CustomDialog(Context context) {
        super(context);
    }

    public CustomDialog(Context context, int themeResId) {
        super(context, themeResId);
    }


    public CustomDialog(Context context, int theme, int resLayout) {
        super(context, theme);
        this.context = context;
        this.layoutRes = resLayout;
    }
}
