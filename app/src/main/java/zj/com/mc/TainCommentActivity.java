package zj.com.mc;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.List;
import java.util.Map;

import Adapter.AnnounceCommandsAdpter;
import Adapter.TainmomentAdapter;
import DBUtils.DBOpenHelper;

/**
 *   全部揭示命令
 */
public class TainCommentActivity extends Activity {
    private LinearLayout search_result_back;
    private ListView lv_moment;
    private String tainMomentsql, code;
    private DBOpenHelper dbopHelper;
    private AnnounceCommandsAdpter tainmomentAdapter;
    private List<Map> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        Myapplilcation.addActivity(this);
        inintView();
        inintDate();
    }

    private void inintView() {
        search_result_back = (LinearLayout) findViewById(R.id.search_result_back);
        lv_moment = (ListView) findViewById(R.id.lv_tainmoment);
        tainMomentsql = "select * from AnnounceCommands where FullName=?";
    }

    private void inintDate() {
        code = getIntent().getStringExtra("code");
        dbopHelper = DBOpenHelper.getInstance(this);
        list = dbopHelper.queryListMap(tainMomentsql, new String[]{code});
        tainmomentAdapter = new AnnounceCommandsAdpter(this, list);
        lv_moment.setAdapter(tainmomentAdapter);
        search_result_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tainmomentAdapter.setOnPassbtnClick(new AnnounceCommandsAdpter.onPassbtnClick() {
            @Override
            public void onClick(View v, int position) {
                list.remove(position);
                tainmomentAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Myapplilcation.removeActivity(this);
    }
}
