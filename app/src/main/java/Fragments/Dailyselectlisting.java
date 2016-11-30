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
import Utils.CommonAdapter;
import Utils.NetUtils;
import Utils.ViewHolder;
import zj.com.mc.Myapplilcation;
import zj.com.mc.R;
import zj.com.mc.SelectiveDatials;
import zj.com.mc.UtilisClass;

/**
 * Created by dell on 2016/8/2.
 */
public class Dailyselectlisting extends BaseFragment {

    private DBOpenHelper dbOpenHelper;
    private List<Map> listselec;
    private CommonAdapter<Map> adapterselec1;
    ListView addlv;
    private Intent intentselect;
    private Bundle bundleselect;
    private String personId;
    private String searchdate;


    @Override
    View initView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.dailyselectlisting,container,false);
        addlv= (ListView) view.findViewById(R.id.daily_Listing_list);
        title.setText("抽查信息单");
        personId=getActivity().getSharedPreferences("PersonInfo", Context.MODE_PRIVATE).getString("PersonId",null);
        searchdate= UtilisClass.getStringDate2();
        dbOpenHelper= DBOpenHelper.getInstance(getActivity().getApplicationContext());
//        initview();



        return view;
    }

    private void initview() {
        listselec=dbOpenHelper.queryListMap("select * from InstructorCheck where InstructorId=? and StartTime like ?",new String[]{personId,searchdate+"%"});
        if (listselec.size()!=0) {
            Collections.reverse(listselec);
            adapterselec1 = new CommonAdapter<Map>(getActivity(), listselec, R.layout.dailylistinglistitem) {
                @Override
                protected void convertlistener(final ViewHolder holder, final Map map) {
                    holder.getView(R.id.daily_Listing_itemdo).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (map.get("IsUploaded").equals(1)) {
                                Myapplilcation.getExecutorService().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        NetUtils.updataarguments3dingle(dbOpenHelper, "InstructorCheck", map.get("Id") + "");
                                    }
                                });
                                List<Map> isUpdata = dbOpenHelper.queryListMap("select * from InstructorCheck where Id=?", new String[]{map.get("Id") + ""});
                                String isuploaded = isUpdata.get(0).get("IsUploaded") + "";
//                                if (isuploaded.equals(0)) {
                                map.put("IsUploaded", "0");
                                holder.setText(R.id.daily_Listing_itemdo, "已上传");
//                                } else {
//                                    UtilisClass.showToast(getActivity(), "上传失败！");
//                                }
                            }
                        }
                    });
                }

                @Override
                public void convert(ViewHolder holder, Map map) {
                    holder.setText(R.id.daily_Listing_itemmessage, String.valueOf(map.get("Location")));
                    holder.setText(R.id.daily_Listing_itemtype, String.valueOf(map.get("CheckType")));
                    holder.setText(R.id.daily_Listing_itemnumber, map.get("ProblemCount") + "");
                    holder.setText(R.id.daily_Listing_itemtime, map.get("StartTime") + "");
                    if (map.get("IsUploaded").equals(1)) {
                        holder.setText(R.id.daily_Listing_itemdo, "上传");
                    } else {
                        holder.setText(R.id.daily_Listing_itemdo, "已上传");

                    }
                }
            };
            addlv.setAdapter(adapterselec1);
            addlv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    intentselect = new Intent(getActivity(), SelectiveDatials.class);
                    bundleselect = new Bundle();
                    bundleselect.putInt("listitemId", i);
                    intentselect.putExtra("InstructorCheck", bundleselect);
                    startActivity(intentselect);
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initview();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
        intentselect=new Intent(getActivity(), SelectiveDatials.class);
        bundleselect=new Bundle();
        bundleselect.putInt("listitemId",-1);
        intentselect.putExtra("InstructorCheck",bundleselect);
        startActivity(intentselect);

    }

    public void showToast(String str){
        Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();
    }
}
