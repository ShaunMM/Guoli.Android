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
import android.widget.Toast;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import DBUtils.DBOpenHelper;
import EventClass.BreakRulesEvent;
import Utils.CommonAdapter;
import Utils.NetUtils;
import Utils.ViewHolder;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import zj.com.mc.DailyBreakRulsDetails;
import zj.com.mc.Myapplilcation;
import zj.com.mc.R;
import zj.com.mc.UtilisClass;

/**
 * Created by dell on 2016/8/3.
 */
public class DailyBreakRuls extends BaseFragment {

    private DBOpenHelper dbOpenHelper;
    private List<Map> listbreak;
    private CommonAdapter<Map> adapterbreak;
    private ListView daily_breakruls_list;
    private Intent intentbreak;
    private Bundle bundlebreak;
    private String searchdate;
    private String personId;
    private String sql;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(DailyBreakRuls.this);
    }

    @Override
    View initView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.dailybreakrules,container,false);
        daily_breakruls_list= (ListView) view.findViewById(R.id.daily_breakruls_list);
        title.setText("违章违纪记录");
        sql="select * from InstructorPeccancy where WriteDate like ? and InstructorId=?";
        personId=getActivity().getSharedPreferences("PersonInfo",Context.MODE_PRIVATE).getString("PersonId",null);
        searchdate=UtilisClass.getStringDate2();
        dbOpenHelper = DBOpenHelper.getInstance(getActivity().getApplicationContext());
        daily_breakruls_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                intentbreak = new Intent(getActivity(), DailyBreakRulsDetails.class);
                bundlebreak = new Bundle();
                bundlebreak.putInt("listitemId", (Integer) listbreak.get(i).get("Id"));
                intentbreak.putExtra("InstructorPeccancy", bundlebreak);
                startActivity(intentbreak);
            }
        });


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
//        initview();
        initDateChanged();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(DailyBreakRuls.this);
    }

    private void initview() {

        listbreak = dbOpenHelper.queryListMap(sql,new String[]{searchdate+"%",personId});

        adapterbreak= new CommonAdapter<Map>(getActivity(), listbreak, R.layout.dailylistinglistitem) {
            @Override
            protected void convertlistener(final ViewHolder holder, final Map map) {

                    holder.getView(R.id.daily_Listing_itemdo).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (map.get("IsUploaded").equals(1)){

                                Myapplilcation.getExecutorService().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        NetUtils.updataarguments3dingle(dbOpenHelper,"InstructorPeccancy",map.get("Id")+"");
                                    }
                                });

                                List<Map> isUpdata= dbOpenHelper.queryListMap("select * from InstructorPeccancy where Id=?",new String[]{map.get("Id")+""});
                                String isuploaded=isUpdata.get(0).get("IsUploaded")+"";
                                map.put("IsUploaded", "0");
                                holder.setText(R.id.daily_Listing_itemdo,"已上传");
                            }
                        }
                    });
            }

            @Override
            public void convert(ViewHolder holder, Map map) {
                holder.setText(R.id.daily_Listing_itemmessage, String.valueOf(UtilisClass.getName(dbOpenHelper,map.get("DriverId")+"")));//联查姓名
                holder.setText(R.id.daily_Listing_itemtype, String.valueOf(UtilisClass.getWorkNo(dbOpenHelper,map.get("DriverId")+"")));//联查工号
                holder.setText(R.id.daily_Listing_itemnumber, map.get("PeccancyType") + "");
                holder.setText(R.id.daily_Listing_itemtime, map.get("WriteDate") + "");
                if (map.get("IsUploaded").equals(1)){
                    holder.setText(R.id.daily_Listing_itemdo,"上传");
                }else {
                    holder.setText(R.id.daily_Listing_itemdo,"已上传");
                }
            }
        };

        daily_breakruls_list.setAdapter(adapterbreak);





    }
    //改变时间刷新listview
    private void initDateChanged() {

        Myapplilcation.getExecutorService().execute(new Runnable() {
            @Override
            public void run() {
                List<Map>  listmap=dbOpenHelper.queryListMap(sql,
                        new String[]{searchdate+"%",personId});

                EventBus.getDefault().post(new BreakRulesEvent(listmap));
            }
        });
    }
    //自动刷新数据
    @Subscribe(threadMode = ThreadMode.MainThread)
    public void setDateChangeAdapter(BreakRulesEvent event){
        listbreak =event.getList();
        System.out.println("llllllll"+listbreak.size());
        if (listbreak.size()!=0) {
            Collections.reverse(listbreak);

            adapterbreak = new CommonAdapter<Map>(getActivity(), listbreak, R.layout.dailylistinglistitem) {
                @Override
                protected void convertlistener(final ViewHolder holder, final Map map) {

                    holder.getView(R.id.daily_Listing_itemdo).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (map.get("IsUploaded").equals(1)){

                                Myapplilcation.getExecutorService().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        NetUtils.updataarguments3dingle(dbOpenHelper,"InstructorPeccancy",map.get("Id")+"");
                                    }
                                });

                                List<Map> isUpdata= dbOpenHelper.queryListMap("select * from InstructorPeccancy where Id=?",new String[]{map.get("Id")+""});
                                String isuploaded=isUpdata.get(0).get("IsUploaded")+"";
                                map.put("IsUploaded", "0");
                                holder.setText(R.id.daily_Listing_itemdo,"已上传");
                            }
                        }
                    });
                }

                @Override
                public void convert(ViewHolder holder, Map map) {
                    holder.setText(R.id.daily_Listing_itemmessage, String.valueOf(UtilisClass.getName(dbOpenHelper,map.get("DriverId")+"")));//联查姓名
                    holder.setText(R.id.daily_Listing_itemtype, String.valueOf(UtilisClass.getWorkNo(dbOpenHelper,map.get("DriverId")+"")));//联查工号
                    holder.setText(R.id.daily_Listing_itemnumber, map.get("PeccancyType") + "");
                    holder.setText(R.id.daily_Listing_itemtime, map.get("WriteDate") + "");
                    if (map.get("IsUploaded").equals(1)) {
                        holder.setText(R.id.daily_Listing_itemdo, "上传");
                    } else {
                        holder.setText(R.id.daily_Listing_itemdo, "已上传");
                    }
                }
            };

            daily_breakruls_list.setAdapter(adapterbreak);
        }


    }




    @Override
    protected void setDateChanged(String date) {
        searchdate=date;
        initDateChanged();
    }

    @Override
    protected void setTestButton() {

    }

    @Override
    protected void setDataButton() {
//添加记录

        intentbreak=new Intent(getActivity(), DailyBreakRulsDetails.class);
        bundlebreak=new Bundle();
        bundlebreak.putInt("listitemId",-1);
        intentbreak.putExtra("InstructorPeccancy",bundlebreak);
        startActivity(intentbreak);
    }

    public void setToast(String str){
        Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();

    }




}
