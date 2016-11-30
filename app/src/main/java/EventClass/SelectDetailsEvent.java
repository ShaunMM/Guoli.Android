package EventClass;

import android.content.Context;

import java.util.List;
import java.util.Map;

/**
 * Created by zhou on 2016/10/14.
 */
public class SelectDetailsEvent {

    private String mMsg;
    private Context context;
    private List<Map> list;

    public SelectDetailsEvent(Context context) {
        this.context = context;
    }

    public SelectDetailsEvent(List<Map> list) {
        this.list = list;
    }

    public SelectDetailsEvent(String mMsg, Context context, List<Map> list) {
        this.mMsg = mMsg;
        this.context = context;
        this.list = list;
    }

    public SelectDetailsEvent() {
    }

    public String getmMsg() {
        return mMsg;
    }

    public void setmMsg(String mMsg) {
        this.mMsg = mMsg;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public List<Map> getList() {
        return list;
    }

    public void setList(List<Map> list) {
        this.list = list;
    }




}
