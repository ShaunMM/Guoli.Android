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
import zj.com.mc.StandardizedActivity;
import zj.com.mc.UtilisClass;

/**
 * Created by dell on 2016/8/3.
 */
public class StandardizedAcceptance extends BaseFragment {

    private DBOpenHelper dbOpenHelper;
    private List<Map>listacceptanceine;
    private CommonAdapter<Map> adapteracceptanceine;
    private ListView acceptanceinlist;
    private Intent intentacceptance;
    private Bundle bundleacceptance;
    private String personId;
    private String searchdate;
//    private int num=1;

    @Override
    View initView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.standardizedacceptance,container,false);
        acceptanceinlist= (ListView) view.findViewById(R.id.daily_acceptance_list2);
        dbOpenHelper= DBOpenHelper.getInstance(getActivity().getApplicationContext());
        title.setText("标准化验收");
        personId=getActivity().getSharedPreferences("PersonInfo", Context.MODE_PRIVATE).getString("PersonId",null);
        searchdate=UtilisClass.getStringDate2();

        return view;
    }

    private void initview() {
        listacceptanceine=dbOpenHelper.queryListMap("select * from InstructorAccept where AcceptDate like ? and InstructorId=?",
                new String[]{searchdate+"%",personId});

        if (listacceptanceine.size()!=0) {
            Collections.reverse(listacceptanceine);
            adapteracceptanceine = new CommonAdapter<Map>(getActivity(), listacceptanceine, R.layout.dailylistinglistitem) {
                @Override
                protected void convertlistener(final ViewHolder holder, final Map map) {
                    holder.getView(R.id.daily_Listing_itemdo).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (map.get("IsUploaded").equals(1)) {
                                Myapplilcation.getExecutorService().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        NetUtils.updataarguments3dingle(dbOpenHelper, "InstructorAccept", map.get("Id") + "");
                                    }
                                });
//                            List<Map> isUpdata= dbOpenHelper.queryListMap("select * from InstructorLocoQuality where Id=?",new String[]{map.get("Id")+""});
//                            String isuploaded=isUpdata.get(0).get("IsUploaded")+"";
//                            if (isuploaded.equals(0)){
                                map.put("IsUploaded", "0");
                                holder.setText(R.id.daily_Listing_itemdo, "已上传");
//                            }else {
//                                UtilisClass.showToast(getActivity(),"上传失败！");
//                            }


                            } else {

                            }
                        }
                    });
                }

                @Override
                public void convert(ViewHolder holder, Map map) {
                    holder.setText(R.id.daily_Listing_itemmessage, map.get("Id") + "");
                    List<Map> maindriverinfo = dbOpenHelper.queryListMap("select * from PersonInfo where Id=?", new String[]{map.get("DriverId").toString()});
                    if (maindriverinfo.size() != 0) {
                        holder.setText(R.id.daily_Listing_itemtype, maindriverinfo.get(0).get("Name").toString());
                    }

                    List<Map> auxdriverinfo = dbOpenHelper.queryListMap("select * from PersonInfo where Id= ?", new String[]{map.get("ViceDriverId").toString()});
                    if (maindriverinfo.size() != 0) {
                        holder.setText(R.id.daily_Listing_itemnumber, auxdriverinfo.get(0).get("Name").toString());
                    }
                    holder.setText(R.id.daily_Listing_itemtime, map.get("AcceptDate") + "");
                    if (map.get("IsUploaded").equals(1)) {
                        holder.setText(R.id.daily_Listing_itemdo, "上传");
                    } else {
                        holder.setText(R.id.daily_Listing_itemdo, "已上传");
                    }
                }

            };
            acceptanceinlist.setAdapter(adapteracceptanceine);

            acceptanceinlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(getActivity(), StandardizedActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("listitemId", (Integer) listacceptanceine.get(i).get("Id"));
                    intent.putExtra("InstructorAccept", bundle);
                    startActivity(intent);

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
        UtilisClass.showToast(getActivity(),"添加记录");
        intentacceptance=new Intent(getActivity(), StandardizedActivity.class);
        bundleacceptance=new Bundle();
        bundleacceptance.putInt("listitemId",-1);
        intentacceptance.putExtra("InstructorAccept", bundleacceptance);
        startActivity(intentacceptance);
    }

    @Override
    public void onResume() {
        super.onResume();
        initview();
    }
}
