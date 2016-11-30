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
import zj.com.mc.Myapplilcation;
import zj.com.mc.R;
import zj.com.mc.TrainQualityDetais;
import zj.com.mc.UtilisClass;

/**
 * Created by dell on 2016/8/3.
 */
public class DailyTrainQuality extends BaseFragment {

private DBOpenHelper dbOpenHelper;
    private List<Map>listquality;
    private CommonAdapter adapter;
    private ListView qualitylv;
    private Intent intentquality ;
    private Bundle bundlequality;
    private String personId;
    private String searchdate;


    @Override
    View initView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.dailytrainquality,container,false);
        dbOpenHelper= DBOpenHelper.getInstance(getActivity().getApplicationContext());
        qualitylv= (ListView) view.findViewById(R.id.daily_trainquality_list);
        title.setText("机车质量登记");
        personId=getActivity().getSharedPreferences("PersonInfo", Context.MODE_PRIVATE).getString("PersonId",null);
        searchdate=UtilisClass.getStringDate2();
//        initview();
        return view;
    }

    private void initview() {

        listquality=dbOpenHelper.queryListMap("select * from InstructorLocoQuality where InstructorId=? and RegistDate like ?",
                new String[]{personId,searchdate+"%"});

        if (listquality.size()!=0) {
            Collections.reverse(listquality);
            adapter = new CommonAdapter<Map>(getActivity(), listquality, R.layout.dailylistinglistitem) {
                @Override
                protected void convertlistener(final ViewHolder holder, final Map map) {
                    holder.getView(R.id.daily_Listing_itemdo).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {


                            if (map.get("IsUploaded").equals(1)) {
                                Myapplilcation.getExecutorService().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        NetUtils.updataarguments3dingle(dbOpenHelper, "InstructorLocoQuality", map.get("Id") + "");
                                    }
                                });
//                        List<Map> isUpdata= dbOpenHelper.queryListMap("select * from InstructorLocoQuality where Id=?",new String[]{map.get("Id")+""});
//                        String isuploaded=isUpdata.get(0).get("IsUploaded")+"";
//                        if (isuploaded.equals(0)){
                                map.put("IsUploaded", "0");
                                holder.setText(R.id.daily_Listing_itemdo, "已上传");
//                        }else {
//                            UtilisClass.showToast(getActivity(),"上传失败！");
//                        }


                            } else {

                            }
                        }

                    });
                }

                @Override
                public void convert(ViewHolder holder, Map map) {
                    holder.setText(R.id.daily_Listing_itemmessage, String.valueOf(map.get("TrainCode")));
                    String drivername = UtilisClass.getName(dbOpenHelper, map.get("Id") + "");
                    holder.setText(R.id.daily_Listing_itemtype, drivername);
                    holder.setText(R.id.daily_Listing_itemnumber, map.get("LocomotiveType") + "");
                    holder.setText(R.id.daily_Listing_itemtime, map.get("RegistDate") + "");
                    if (map.get("IsUploaded").equals(1)) {
                        holder.setText(R.id.daily_Listing_itemdo, "上传");
                    } else {
                        holder.setText(R.id.daily_Listing_itemdo, "已上传");
                    }
                }
            };
            qualitylv.setAdapter(adapter);

            qualitylv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    intentquality = new Intent(getActivity(), TrainQualityDetais.class);
                    bundlequality = new Bundle();
                    bundlequality.putInt("listitemId", (Integer) listquality.get(i).get("Id"));
                    intentquality.putExtra("InstructorLocoQuality", bundlequality);
                    startActivity(intentquality);

                }
            });

        }
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
        intentquality= new Intent(getActivity(), TrainQualityDetais.class);
        bundlequality = new Bundle();
        bundlequality.putInt("listitemId",-1);
        intentquality.putExtra("InstructorLocoQuality", bundlequality);
        startActivity(intentquality);
    }

    @Override
    public void onResume() {
        super.onResume();

        initview();
    }
}
