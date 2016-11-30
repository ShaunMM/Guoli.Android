package Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import DBUtils.DBOpenHelper;
import Utils.CommonAdapter;
import Utils.ViewHolder;
import zj.com.mc.R;

/**
 * Created by dell on 2016/7/29.
 */
public class TrainScheduleresult extends Fragment implements View.OnClickListener{

    private GridView gv1;
    private ListView trainSchedulelistview;
    private List<Map> TrainScheduleresultlist;
    private DBOpenHelper dbOpenHelper;
    private String searchcode;
    private TextView trainsche_result_train,trainsche_result_startstop;
    private List<String> gridList;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.trainsche_result,container,false);
        view.findViewById(R.id.trainshe_result_back).setOnClickListener(this);
        trainsche_result_train= (TextView) view.findViewById(R.id.trainsche_result_train);//车次
        trainsche_result_startstop= (TextView) view.findViewById(R.id.trainsche_result_startstop);//线路对应表
        dbOpenHelper= DBOpenHelper.getInstance(getActivity().getApplicationContext());
        searchcode="";
        initList(view);
        SharedPreferences s=getActivity().getSharedPreferences("TrainScheduleCode", Context.MODE_PRIVATE);
        searchcode=s.getString("seachcode",null);
        dbOpenHelper.insert("SeacherHistory",new String[]{"SearchCode"},new Object[]{searchcode});
        return view;
    }
//加载集合数据
    private void initList(View view) {
        SharedPreferences s=getActivity().getSharedPreferences("TrainScheduleCode", Context.MODE_PRIVATE);
        searchcode=s.getString("seachcode",null);
//设置车次，线路
        trainsche_result_train.setText(searchcode);
        List<Map> list=dbOpenHelper.queryListMap("select * from TrainNo where FullName=?",new String[]{searchcode});
        if (list.size()!=0) {
            trainsche_result_startstop.setText(list.get(0).get("FirstStation") + "-" + list.get(0).get("LastStation"));
        }
//        UtilisClass.showToast(getActivity(),searchcode);
//查询线路信息
        TrainScheduleresultlist=dbOpenHelper.queryListMap("select * from ViewTrainMoment where FullName=? order by Sort",new String[]{searchcode});
        trainSchedulelistview= (ListView) view.findViewById(R.id.trainsche_result_grid2);
        trainSchedulelistview.setAdapter(new CommonAdapter<Map>(getActivity(),TrainScheduleresultlist, R.layout.trainsche_resullistitem) {
            @Override
            protected void convertlistener(ViewHolder holder, Map map) {

            }

            @Override
            public void convert(ViewHolder holder, Map map) {
                holder.setText(R.id.trainshce_resultlistietm1,map.get("StationName")+"");
                holder.setText(R.id.trainshce_resultlistietm2,map.get("ArriveTime")+"");
                holder.setText(R.id.trainshce_resultlistietm3,map.get("DepartTime")+"");
                holder.setText(R.id.trainshce_resultlistietm4,map.get("StopMinutes")+"");
                holder.setText(R.id.trainshce_resultlistietm5,map.get("IntervalKms")+"");
                holder.setText(R.id.trainshce_resultlistietm6,map.get("SuggestSpeed")+"");

            }
        });

    }

    //搜索事件
    @Override
    public void onClick(View view) {
        getActivity().getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onPause() {
        super.onPause();
        onDestroy();
    }
}
