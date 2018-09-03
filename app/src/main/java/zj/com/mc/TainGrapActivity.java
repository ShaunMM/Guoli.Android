package zj.com.mc;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.List;
import java.util.Map;

import Adapter.TainGrapAdapter;
import DBUtils.DBOpenHelper;

/**
 * 查看记点
 * Created by mao on 2016/12/3.
 */
public class TainGrapActivity extends Activity {

    private ListView lv_grap;
    private LinearLayout search_result_back;
    private DBOpenHelper openHelper;
    private TainGrapAdapter tainGrapAdapter;
    private List<Map> list, TrainFormationList, ViewTrainmentList;
    private String DriveRecordId, code,
            signSql = "select * from DriveSignPoint where DriveRecordId=?",
            TrainFormationSql = "select * from TrainFormation where DriveRecordId=?",
            ViewTrainSql = "select * from ViewTrainMoment where FullName=?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taingrap);
        Myapplilcation.addActivity(this);
        lv_grap = (ListView) findViewById(R.id.lv_grap);
        search_result_back = (LinearLayout) findViewById(R.id.search_result_back);
        inintData();
    }

    private void inintData() {
        DriveRecordId = getIntent().getStringExtra("id");
        code = getIntent().getStringExtra("code");
        openHelper = DBOpenHelper.getInstance(this);
        list = openHelper.queryListMap(signSql, new String[]{DriveRecordId});
        TrainFormationList = openHelper.queryListMap(TrainFormationSql, new String[]{DriveRecordId});
        ViewTrainmentList = openHelper.queryListMap(ViewTrainSql, new String[]{code});
        tainGrapAdapter = new TainGrapAdapter(this,"TainGrapActivity", DriveRecordId, list, TrainFormationList, ViewTrainmentList);
        lv_grap.setAdapter(tainGrapAdapter);

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
