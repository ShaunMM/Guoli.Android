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
import EventClass.TeachDetailsEvent;
import Utils.CommonAdapter;
import Utils.NetUtils;
import Utils.ViewHolder;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import zj.com.mc.CourseTrain;
import zj.com.mc.Myapplilcation;
import zj.com.mc.R;
import zj.com.mc.UtilisClass;

/**
 * Created by dell on 2016/8/3.
 */
public class DailyCourseTrain extends BaseFragment {

    private Intent intentcourse;
    private Bundle bundlecourse;
    private DBOpenHelper dbOpenHelper;
    private ListView daily_coursetrain_list;
    private CommonAdapter adapteraourse;
    private List<Map> listcourse;
    private String personId;
    private String searchdate;
    private String sql;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(DailyCourseTrain.this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(DailyCourseTrain.this);
    }

    @Override
    View initView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.dailycoursetrain,container,false);
        daily_coursetrain_list= (ListView) view.findViewById(R.id.daily_coursetrain_list);
        title.setText("授课培训记录");
        sql="select * from InstructorTeach where TeachStart like ? and InstructorId=?";
        personId=getActivity().getSharedPreferences("PersonInfo", Context.MODE_PRIVATE).getString("PersonId",null);
        dbOpenHelper= DBOpenHelper.getInstance(getActivity().getApplicationContext());
        searchdate=UtilisClass.getStringDate2();
//        inintview();


        return view;
    }


    private void inintview() {
//        listcourse=dbOpenHelper.queryListMap(sql,new String[]{searchdate+"%",personId});

        Myapplilcation.getExecutorService().execute(new Runnable() {
            @Override
            public void run() {


                List<Map>  listmap=dbOpenHelper.queryListMap(sql,
                        new String[]{searchdate+"%",personId});

                EventBus.getDefault().post(new TeachDetailsEvent(listmap));
            }
        });



    }


    //自动刷新数据
    @Subscribe(threadMode = ThreadMode.MainThread)
    public void setDateChangeAdapter(TeachDetailsEvent event){

        listcourse=event.getList();
        if (listcourse.size()!=0) {
Collections.reverse(listcourse);
            adapteraourse = new CommonAdapter<Map>(getActivity(), listcourse, R.layout.dailylistinglistitem) {
                @Override
                protected void convertlistener(final ViewHolder holder, final Map map) {

                    holder.getView(R.id.daily_Listing_itemdo).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {


//                        UtilisClass.setconverlisten(holder,map,dbOpenHelper,"InstructorTeach");
                            if (map.get("IsUploaded").equals(1)) {
                                Myapplilcation.getExecutorService().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        NetUtils.updataarguments3dingle(dbOpenHelper, "InstructorTeach", map.get("Id") + "");
                                    }
                                });
                                List<Map> isUpdata = dbOpenHelper.queryListMap("select * from InstructorTeach where Id=?", new String[]{map.get("Id") + ""});
                                String isuploaded = isUpdata.get(0).get("IsUploaded") + "";
                                if (isuploaded.equals(0)) {
                                    map.put("IsUploaded", "0");
                                    holder.setText(R.id.daily_Listing_itemdo, "已上传");
                                } else {
                                    UtilisClass.showToast(getActivity(), "上传失败！");
                                }


                            } else {

                            }
                        }

                    });
                }

                @Override
                public void convert(ViewHolder holder, Map map) {
                    String start = map.get("TeachStart").toString().split(" ")[1];

                    String starttime1 = start.toString().split(":")[0];
                    String starttime2 = start.toString().split(":")[1];
                    int s = Integer.parseInt(starttime1) * 60 + Integer.parseInt(starttime2);
                    String stop = map.get("TeachEnd").toString().split(" ")[1];
                    String stoptime1 = stop.toString().split(":")[0];
                    String stoptime2 = stop.toString().split(":")[1];

                    int e = Integer.parseInt(stoptime1) * 60 + Integer.parseInt(stoptime2);
                    double d = (e - s) / 60;

                    holder.setText(R.id.daily_Listing_itemmessage, String.valueOf(map.get("TeachPlace")));
                    holder.setText(R.id.daily_Listing_itemtype, d + "");
                    holder.setText(R.id.daily_Listing_itemnumber, String.valueOf(map.get("JoinCount")));

                    holder.setText(R.id.daily_Listing_itemtime, map.get("TeachStart") + "");
                    if (map.get("IsUploaded").equals(1)) {
                        holder.setText(R.id.daily_Listing_itemdo, "上传");
                    } else {
                        holder.setText(R.id.daily_Listing_itemdo, "已上传");

                    }


                }

            };
            daily_coursetrain_list.setAdapter(adapteraourse);

            daily_coursetrain_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    intentcourse = new Intent(getActivity(), CourseTrain.class);
                    bundlecourse = new Bundle();
                    bundlecourse.putInt("listitemId", (Integer) listcourse.get(i).get("Id"));
                    intentcourse.putExtra("InstructorTeach", bundlecourse);
                    startActivity(intentcourse);
                }
            });
        }
    }



    @Override
    protected void setDateChanged(String date) {
        searchdate=date;
        inintview();
    }

    @Override
    protected void setTestButton() {

    }

    @Override
    protected void setDataButton() {
        //添加记录

        intentcourse = new Intent(getActivity(), CourseTrain.class);
        bundlecourse = new Bundle();
        bundlecourse.putInt("listitemId", -1);
        intentcourse.putExtra("InstructorTeach", bundlecourse);
        startActivity(intentcourse);
    }

    public void setToast(String str){
        Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onResume() {
        super.onResume();
        inintview();
    }
}
