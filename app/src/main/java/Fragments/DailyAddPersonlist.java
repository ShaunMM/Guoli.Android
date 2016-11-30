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
import EventClass.AddpersonListItemEvent;
import Utils.CommonAdapter;
import Utils.NetUtils;
import Utils.ViewHolder;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import zj.com.mc.AddPersonListItem;
import zj.com.mc.Myapplilcation;
import zj.com.mc.R;
import zj.com.mc.UtilisClass;

/**
 * Created by dell on 2016/8/3.
 */
public class DailyAddPersonlist extends BaseFragment {

    private DBOpenHelper dbOpenHelper;
    private ListView addlv;
    private List<Map> list;
    private CommonAdapter<Map> adapter;
    private Intent intentadd;
    private Bundle bundleadd;
    private String personId;
    private String searchdate;
    private String sql;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(DailyAddPersonlist.this);

    }

    @Override
    View initView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.tianchengdetail,container,false);
        addlv= (ListView) view.findViewById(R.id.daily_tiancheng_list);
        view.findViewById(R.id.dodata1).setOnClickListener(this);
        sql="select * from InstructorTempTake where TakeDate like ? and InstructorId=?";
        personId=getActivity().getSharedPreferences("PersonInfo",Context.MODE_PRIVATE).getString("PersonId",null);
        searchdate=UtilisClass.getStringDate2();

        title.setText("添乘信息单");
        addlv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                intentadd = new Intent(getActivity(), AddPersonListItem.class);
                bundleadd = new Bundle();
                bundleadd.putInt("listitemId", (Integer) list.get(i).get("Id"));
                intentadd.putExtra("InstructorTempTake", bundleadd);
                startActivity(intentadd);
            }
        });


        return view;
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case R.id.tainjia_shuju:

                break;
            case R.id.daily_tiancheng_spmonth:

                break;

        }

    }

    @Override
    protected void setDateChanged(String date) {

        searchdate=date;
        initDateChanged();
    }

//改变时间刷新listview
    private void initDateChanged() {
//        list=dbOpenHelper.queryListMap("select * from InstructorTempTake where TakeDate like ? and InstructorId=?",
//                new String[]{searchdate+"%",personId});
//        if (list.size()!=0) {
//            adapter = new PersonAdapter(getActivity(), list, R.layout.dailylistinglistitem);
//
//            addlv.setAdapter(adapter);
//        }

        Myapplilcation.getExecutorService().execute(new Runnable() {
            @Override
            public void run() {


              List<Map>  listmap=dbOpenHelper.queryListMap(sql,
                        new String[]{searchdate+"%",personId});

                EventBus.getDefault().post(new AddpersonListItemEvent(listmap));
            }
        });
    }
    //自动刷新数据
    @Subscribe(threadMode = ThreadMode.MainThread)
    public void setDateChangeAdapter(AddpersonListItemEvent event){

        list=event.getList();
        if (list.size()!=0) {
            Collections.reverse(list);
            adapter = new PersonAdapter(getActivity(), list, R.layout.dailylistinglistitem);
            addlv.setAdapter(adapter);
        }

    }





    @Override
    protected void setTestButton() {

    }

    @Override
    protected void setDataButton() {
        //添加记录
        intentadd=new Intent(getActivity(), AddPersonListItem.class);
        bundleadd=new Bundle();
        bundleadd.putInt("listitemId",-1);
        intentadd.putExtra("InstructorTempTake",bundleadd);
        startActivity(intentadd);
    }

    public void setToast(String str){
        Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        dbOpenHelper= DBOpenHelper.getInstance(getActivity().getApplicationContext());
        initDateChanged();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        setToast("关闭");
        EventBus.getDefault().unregister(DailyAddPersonlist.this);
    }

    class PersonAdapter extends CommonAdapter<Map> {

        public PersonAdapter(Context context, List<Map> datas, int item_id) {
            super(context, datas, item_id);
        }
        @Override
        protected void convertlistener(final ViewHolder holder, final Map map) {

            holder.getView(R.id.daily_Listing_itemdo).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (map.get("IsUploaded").equals(1)){
                        Myapplilcation.getExecutorService().execute(new Runnable() {
                            @Override
                            public void run() {
                                NetUtils.updataarguments3dingle(dbOpenHelper,"InstructorTempTake",map.get("Id")+"");
                            }
                        });
                            map.put("IsUploaded", "0");
                            holder.setText(R.id.daily_Listing_itemdo,"已上传");
                    }else {
                    }
                }

            });
        }

        @Override
        public void convert(ViewHolder holder, Map map) {
            holder.setText(R.id.daily_Listing_itemmessage, String.valueOf(map.get("TrainCode")));
            String drivername= UtilisClass.getName(dbOpenHelper,String.valueOf(map.get("DriverId")));
            holder.setText(R.id.daily_Listing_itemtype,drivername);
            holder.setText(R.id.daily_Listing_itemnumber, map.get("TakeSection") + "");
            holder.setText(R.id.daily_Listing_itemtime, map.get("TakeDate") + "");
            if (map.get("IsUploaded").equals(1)){
                holder.setText(R.id.daily_Listing_itemdo,"上传");
            }else {
                holder.setText(R.id.daily_Listing_itemdo,"已上传");

            }

        }
    }




}
