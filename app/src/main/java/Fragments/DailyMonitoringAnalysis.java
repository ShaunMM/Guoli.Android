package Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import DBUtils.DBOpenHelper;
import EventClass.MonitoringAnalysisEvent;
import Utils.CommonAdapter;
import Utils.NetUtils;
import Utils.ViewHolder;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import zj.com.mc.MonitoringAnalysis;
import zj.com.mc.Myapplilcation;
import zj.com.mc.R;
import zj.com.mc.UtilisClass;

/**
 * Created by dell on 2016/8/3.
 */
public class DailyMonitoringAnalysis extends BaseFragment {

    private DBOpenHelper dbOpenHelper;
    private List<Map> listanlysis;
    private CommonAdapter adapteranlysis;
    private ListView lvanlysis;
    private Intent intentanlysis;
    private Bundle bundleanlysis;
    private String personId;
    private String searchdate;
    private String sql;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(DailyMonitoringAnalysis.this);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(DailyMonitoringAnalysis.this);


    }

    @Override
    View initView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.dailymonitoringanaly,container,false);
        lvanlysis= (ListView) view.findViewById(R.id.daily_monitoringanalysis_list);
        dbOpenHelper= DBOpenHelper.getInstance(getActivity().getApplicationContext());
        title.setText("监控分析单");
        sql="select * from InstructorAnalysis where InstructorId=? and AnalysisStart like ?";
        personId=getActivity().getSharedPreferences("PersonInfo", Context.MODE_PRIVATE).getString("PersonId",null);
        searchdate=UtilisClass.getStringDate2();
//        initview();
        return view;
    }

    //自动刷新数据
    @Subscribe(threadMode = ThreadMode.MainThread)
    public void setDateChangeAdapter(MonitoringAnalysisEvent event){
        listanlysis=event.getList();
        if (listanlysis.size()!=0) {
            Collections.reverse(listanlysis);
            adapteranlysis = new CommonAdapter<Map>(getActivity(), listanlysis, R.layout.dailylistinglistitem) {
                @Override
                protected void convertlistener(final ViewHolder holder, final Map map) {
                    holder.getView(R.id.daily_Listing_itemdo).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (map.get("IsUploaded").equals(1)) {
                                Myapplilcation.getExecutorService().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        NetUtils.updataarguments3dingle(dbOpenHelper, "InstructorAnalysis", map.get("Id") + "");
                                    }
                                });
                                map.put("IsUploaded", "0");
                                holder.setText(R.id.daily_Listing_itemdo, "已上传");
                            } else {
                            }


                        }
                    });


                }

                @Override
                public void convert(ViewHolder holder, Map map) {
                    holder.setText(R.id.daily_Listing_itemmessage, map.get("TrainCode") + "");
                    holder.setText(R.id.daily_Listing_itemtype, UtilisClass.getName(dbOpenHelper, String.valueOf(map.get("DriverId")) + ""));
                    holder.setText(R.id.daily_Listing_itemnumber, map.get("RunSection") + "");
                    String analysistime = map.get("AnalysisStart") + "";
                    String analysisdata = analysistime.split(" ")[0];
                    holder.setText(R.id.daily_Listing_itemtime, analysisdata);
                    if (map.get("IsUploaded").equals(1)) {
                        holder.setText(R.id.daily_Listing_itemdo, "上传");
                    } else {
                        holder.setText(R.id.daily_Listing_itemdo, "已上传");
                    }
                }
            };
            lvanlysis.setAdapter(adapteranlysis);

            lvanlysis.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    intentanlysis = new Intent(getActivity(), MonitoringAnalysis.class);
                    bundleanlysis = new Bundle();
                    bundleanlysis.putInt("listitemId", (Integer) listanlysis.get(i).get("Id"));
                    intentanlysis.putExtra("InstructorAnalysis", bundleanlysis);
                    startActivity(intentanlysis);

                }
            });
        }

    }

    //改变时间刷新listview
    private void initview() {
        Myapplilcation.getExecutorService().execute(new Runnable() {
            @Override
            public void run() {
                List<Map>  listmap=dbOpenHelper.queryListMap(sql,
                        new String[]{personId,searchdate+"%"});
                System.out.println(">>>>>>>>"+searchdate+"//"+personId);
                EventBus.getDefault().post(new MonitoringAnalysisEvent(listmap));
            }
        });
    }



    @Override
    protected void setDateChanged(String date) {
        searchdate=date;
        initview();
    }

    @Override
    protected void setTestButton() {

    }

    @Override
    protected void setDataButton() {
        //添加记录
        intentanlysis= new Intent(getActivity(), MonitoringAnalysis.class);
        bundleanlysis= new Bundle();
        bundleanlysis.putInt("listitemId", -1);
        intentanlysis.putExtra("InstructorAnalysis", bundleanlysis);
        startActivity(intentanlysis);
    }

    @Override
    public void onResume() {
        super.onResume();
        initview();
    }
}
