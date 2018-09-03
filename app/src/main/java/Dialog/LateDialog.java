package Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import zj.com.mc.R;

/**
 * 晚点Dialog
 */
public class LateDialog extends Dialog {

    private onbtnclic onbtnclic;
    private String late;
    private ListView lv_late;
    private Context context;


    public LateDialog(Context context,String late,onbtnclic onbtnclic) {
        super(context);
        this.context = context;
        this.onbtnclic=onbtnclic;
        this.late=late;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_late);
        lv_late = (ListView) findViewById(R.id.lv_late);
        final String[] strArr = new String[] { "客流量多，造成延误开车时间", "待避其他列车",
                "列车本事事故", "自然灾害影响列车的运行", "铁路车站调度出现问题" };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                context, android.R.layout.simple_list_item_1, strArr);
        lv_late.setAdapter(adapter);
        setCanceledOnTouchOutside(false);
        lv_late.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onbtnclic.getStr(strArr[position]);

            }
        });

    }

    public interface onbtnclic{
        public void getStr(String info);
    }
}
