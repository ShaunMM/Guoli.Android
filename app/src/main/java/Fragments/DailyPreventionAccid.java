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
import zj.com.mc.PreventionAccid;
import zj.com.mc.R;
import zj.com.mc.UtilisClass;

/**
 * Created by dell on 2016/8/3.
 */
public class DailyPreventionAccid extends BaseFragment {

    private DBOpenHelper dbOpenHelper;
    private List<Map> listgoodp;
    private CommonAdapter adaptergoodp;
    private ListView addlvgoodp;
    private Intent intentgoodp;
    private Bundle bundlegoodp;
    private String personId;
    private String searchdate;


    @Override
    View initView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.dailypreventofaccidents,container,false);
        addlvgoodp= (ListView) view.findViewById(R.id.daily_preventtofacc_list);
        dbOpenHelper= DBOpenHelper.getInstance(getActivity().getApplicationContext());
        title.setText("防止事故及好人好事记录");
        searchdate=UtilisClass.getStringDate2();
        personId=getActivity().getSharedPreferences("PersonInfo", Context.MODE_PRIVATE).getString("PersonId",null);
//        initview();
        return view;
    }

    private void initview() {

        listgoodp=dbOpenHelper.queryListMap("select * from InstructorGoodJob where WriteDate like ? and InstructorId=?",new String[]{searchdate+"%",personId});

        if (listgoodp.size()!=0) {
            Collections.reverse(listgoodp);
            adaptergoodp = new CommonAdapter<Map>(getActivity(), listgoodp, R.layout.dailylistinglistitem) {
                @Override
                protected void convertlistener(final ViewHolder holder, final Map map) {
                    holder.getView(R.id.daily_Listing_itemdo).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (map.get("IsUploaded").equals(1)) {
                                Myapplilcation.getExecutorService().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        NetUtils.updataarguments3dingle(dbOpenHelper, "InstructorGoodJob", map.get("Id") + "");
                                    }
                                });
//                            List<Map> isUpdata= dbOpenHelper.queryListMap("select * from InstructorAnalysis where Id=?",new String[]{map.get("Id")+""});
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
                    holder.setText(R.id.daily_Listing_itemmessage, UtilisClass.getName(dbOpenHelper, map.get("DriverId") + ""));//查别的表
                    holder.setText(R.id.daily_Listing_itemtype, String.valueOf((map.get("GeneralSituation"))));
                    holder.setText(R.id.daily_Listing_itemnumber, map.get("GoodJobType") + "");
                    holder.setText(R.id.daily_Listing_itemtime, map.get("WriteDate") + "");

                    if (map.get("IsUploaded").equals(1)) {
                        holder.setText(R.id.daily_Listing_itemdo, "上传");
                    } else {
                        holder.setText(R.id.daily_Listing_itemdo, "已上传");
                    }
                }

            };
            addlvgoodp.setAdapter(adaptergoodp);

            addlvgoodp.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    intentgoodp = new Intent(getActivity(), PreventionAccid.class);
                    bundlegoodp = new Bundle();
                    bundlegoodp.putInt("listitemId", (Integer) listgoodp.get(i).get("Id"));
                    intentgoodp.putExtra("InstructorGoodJob", bundlegoodp);
                    startActivity(intentgoodp);

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
        //添加记录
        intentgoodp = new Intent(getActivity(), PreventionAccid.class);
        bundlegoodp = new Bundle();
        bundlegoodp.putInt("listitemId", -1);
        intentgoodp.putExtra("InstructorGoodJob", bundlegoodp);
        startActivity(intentgoodp);
    }

    @Override
    public void onResume() {
        super.onResume();
        initview();
    }
}
