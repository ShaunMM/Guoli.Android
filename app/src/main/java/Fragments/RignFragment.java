package Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import Adapter.TainGrapAdapter;
import DBUtils.DBOpenHelper;
import zj.com.mc.AttentInfoActivity;
import zj.com.mc.R;

import static Adapter.TainGrapAdapter.*;

/**
 * 签点信息Fragment
 */
public class RignFragment extends Fragment {

    private View view;
    private ListView lv_grap;//签点信息列表
    private DBOpenHelper openHelper;
    private TainGrapAdapter tainGrapAdapter;
    private List<Map> list, TrainFormationList, ViewTrainmentList;
    private AttentInfoActivity mActivity;
    private String DriveRecordId, code,
            signSql = "select * from DriveSignPoint where DriveRecordId=?",
            TrainFormationSql = "select * from TrainFormation where DriveRecordId=?",
            ViewTrainSql = "select * from ViewTrainMoment where FullName=?",
            sql = "select * from ViewDriveRecord where Id=?";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_regin, null);
        inintView(view);
        inintData();
        return view;
    }

    private void inintView(View view) {
        lv_grap = (ListView) view.findViewById(R.id.lv_grap);
    }

    private void inintData() {
        mActivity = (AttentInfoActivity) getActivity();
        DriveRecordId = getArguments().getString("id");
        openHelper = DBOpenHelper.getInstance(mActivity);
        final Map map = openHelper.queryItemMap(sql, new String[]{DriveRecordId});
        code = String.valueOf(map.get("FullName"));
        list = openHelper.queryListMap(signSql, new String[]{DriveRecordId});
        TrainFormationList = openHelper.queryListMap(TrainFormationSql, new String[]{DriveRecordId});//签点信息
        ViewTrainmentList = openHelper.queryListMap(ViewTrainSql, new String[]{code});
        tainGrapAdapter = new TainGrapAdapter(mActivity,"RignFragment", DriveRecordId, list, TrainFormationList, ViewTrainmentList);


        lv_grap.setAdapter(tainGrapAdapter);

        mActivity.setOnSavebtnClickLisner(new AttentInfoActivity.onSavebtnClick() {
            @Override
            public void onSaveClick(View v) {
                boolean a = tainGrapAdapter.upRign(openHelper);
                boolean b = tainGrapAdapter.uporInsetGrap(openHelper);
                if (a && b) {
                    Toast.makeText(mActivity, "数据已保存", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mActivity, "数据保存失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}