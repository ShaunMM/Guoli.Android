package zj.com.mc;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.List;
import java.util.Map;

import Adapter.TainmomentAdapter;
import DBUtils.DBOpenHelper;

/**
 * Created by mao on 2016/12/2.
 */
public class TainMomentActivity extends Activity {

    private LinearLayout search_result_back;
    private ListView lv_moment;
    private String tainMomentsql, code;
    private DBOpenHelper dbopHelper;
    private TainmomentAdapter tainmomentAdapter;
    private List<Map> list;
    private TextView tv_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tainmoment);
        Myapplilcation.addActivity(this);
        inintView();
        inintDate();
    }

    private void inintView() {
        search_result_back = (LinearLayout) findViewById(R.id.search_result_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        lv_moment = (ListView) findViewById(R.id.lv_tainmoment);
        tainMomentsql = "select * from ViewTrainMoment where FullName=?";
    }

    private void inintDate() {
        code = getIntent().getStringExtra("code");
        tv_title.setText(code + "次列车时刻");
        dbopHelper = DBOpenHelper.getInstance(this);
        list = dbopHelper.queryListMap(tainMomentsql, new String[]{code});
        tainmomentAdapter = new TainmomentAdapter(this, list);
        lv_moment.setAdapter(tainmomentAdapter);
        tainmomentAdapter.setSelectIndex(-1);
        search_result_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Myapplilcation.removeActivity(this);
    }
}
