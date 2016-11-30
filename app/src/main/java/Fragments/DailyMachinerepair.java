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
import Utils.CommonAdapter;
import Utils.NetUtils;
import Utils.ViewHolder;
import zj.com.mc.MachineDetails;
import zj.com.mc.Myapplilcation;
import zj.com.mc.R;
import zj.com.mc.UtilisClass;

/**
 * Created by dell on 2016/8/3.
 */
public class DailyMachinerepair extends BaseFragment {

    private DBOpenHelper dbOpenHelper;
    private List<Map>listmachine;
    private CommonAdapter<Map> adaptermachine;
    private ListView machinlist;
    private Intent intentmach;
    private Bundle bundlemach;
    private String personId;
private String searchdate;


    @Override
    View initView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.dailymachinerepair,container,false);
        machinlist= (ListView) view.findViewById(R.id.daily_machinerepair_list);
        title.setText("机破维修记录");
        searchdate=UtilisClass.getStringDate2();
        dbOpenHelper= DBOpenHelper.getInstance(getActivity().getApplicationContext());
        personId=getActivity().getSharedPreferences("PersonInfo", Context.MODE_PRIVATE).getString("PersonId",null);
//        initview();
        return view;
    }

    private void initview() {

        listmachine=dbOpenHelper.queryListMap("select * from InstructorRepair where InstructorId=? and HappenTime like ?",new String[]{personId,searchdate+"%"});

        Collections.reverse(listmachine);
        adaptermachine= new CommonAdapter<Map>(getActivity(), listmachine, R.layout.dailylistinglistitem) {
            @Override
            protected void convertlistener(final ViewHolder holder, final Map map) {
                holder.getView(R.id.daily_Listing_itemdo).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (map.get("IsUploaded").equals(1)){
                                Myapplilcation.getExecutorService().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        NetUtils.updataarguments3dingle(dbOpenHelper,"InstructorRepair",map.get("Id")+"");
                                    }
                                });
                                List<Map> isUpdata= dbOpenHelper.queryListMap("select * from InstructorRepair where Id=?",new String[]{map.get("Id")+""});
                                String isuploaded=isUpdata.get(0).get("IsUploaded")+"";
//                                if (isuploaded.equals(0)){
                                    map.put("IsUploaded", "0");
                                    holder.setText(R.id.daily_Listing_itemdo,"已上传");
//                                }else {
//                                    UtilisClass.showToast(getActivity(),"上传失败！");
//                                }



                            }else {

                        }
                    }
                });
            }

            @Override
            public void convert(ViewHolder holder, Map map) {
                holder.setText(R.id.daily_Listing_itemmessage, String.valueOf(map.get("LocomotiveType")));
                holder.setText(R.id.daily_Listing_itemtype, UtilisClass.getName(dbOpenHelper,String.valueOf(map.get("DriverId"))+""));
                holder.setText(R.id.daily_Listing_itemnumber, map.get("TrainCode") + "");
                holder.setText(R.id.daily_Listing_itemtime, map.get("HappenTime") + "");
                if (map.get("IsUploaded").equals(1)){
                    holder.setText(R.id.daily_Listing_itemdo,"上传");
                }else {
                    holder.setText(R.id.daily_Listing_itemdo,"已上传");
                }
            }

        };
        machinlist.setAdapter(adaptermachine);

        machinlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), MachineDetails.class);
                Bundle bundle = new Bundle();
                bundle.putInt("listitemId", (Integer) listmachine.get(i).get("Id"));
                intent.putExtra("InstructorRepair", bundle);
                startActivity(intent);

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

        intentmach = new Intent(getActivity(), MachineDetails.class);
        bundlemach = new Bundle();

        bundlemach.putInt("listitemId", -1);
        intentmach.putExtra("InstructorRepair", bundlemach);
        startActivity(intentmach);
    }


    @Override
    public void onResume() {
        super.onResume();
        initview();
    }
}
