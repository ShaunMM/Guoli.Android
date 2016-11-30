package Fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import DBUtils.DBOpenHelper;
import Utils.CommonAdapter;
import Utils.ViewHolder;
import WPSutils.Wpsutils;
import zj.com.mc.NoticeDetails;
import zj.com.mc.R;
import zj.com.mc.UtilisClass;

/**
 * Created by dell on 2016/9/6.
 */
//最新公告
public class LatestAnnouncement extends Fragment implements View.OnClickListener{

    private ListView latestann_listview;
    private List<Map> latestAnnListMap;
    private DBOpenHelper dbOpenHelper;
    private String personId;
    private SharedPreferences sharedPreferences;
    private View view;
    private LinearLayout system_layout;
    private FrameLayout system_fragments;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         view=inflater.inflate(R.layout.latestannouncement,container,false);
        latestann_listview= (ListView) view.findViewById(R.id.latestann_listview);
        dbOpenHelper= DBOpenHelper.getInstance(getActivity());
        sharedPreferences=getActivity().getSharedPreferences("PersonInfo", Context.MODE_PRIVATE);
        personId = sharedPreferences.getString("PersonId", null);
        view.findViewById(R.id.latestannouncement_titleback).setOnClickListener(this);
        view.findViewById(R.id.test_insert).setOnClickListener(this);



        system_fragments=(FrameLayout) view.findViewById(R.id.system_fragments);
        system_layout= (LinearLayout) view.findViewById(R.id.system_layout);



        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        latestAnnListMap=dbOpenHelper.queryListMap("select * from Announcement",null);
        initsetListview();
    }

    private void initsetListview(){
        Collections.reverse(latestAnnListMap);
        CommonAdapter<Map> adapter=new CommonAdapter<Map>(getActivity(),latestAnnListMap, R.layout.latestannouncementlistview_item) {
            @Override
            protected void convertlistener(ViewHolder holder, Map map) {
            }

            @Override
            public void convert(ViewHolder holder, Map map) {

                holder.setText(R.id.laststan_listitem1,map.get("Title")+"");
                holder.setText(R.id.laststan_listitem2,map.get("PubTime")+"");
                String isread=map.get("IsRead")+"";
                if (isread.equals("1")){
                    holder.setText(R.id.laststan_listitem3,"已读");
                }else {
                    holder.setText(R.id.laststan_listitem3,"未读");

                }
            }
        };
        latestann_listview.setAdapter(adapter);
        latestann_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String AnnounceType=latestAnnListMap.get(i).get("AnnounceType")+"";

                if (AnnounceType.equals("2")) {

                    Intent intentLatestA = new Intent(getActivity(), NoticeDetails.class);
                    intentLatestA.putExtra("Id", latestAnnListMap.get(i).get("Id") + "");
                    startActivity(intentLatestA);
                }else {
                    String locapath=latestAnnListMap.get(i).get("LocaPath")+"";
                    Wpsutils.wpsOpenFile(locapath,getActivity());
                }
                dbOpenHelper.update("Announcement",new String[]{"IsRead"},new Object[]{1},
                        new String[]{"Id"},new String[]{latestAnnListMap.get(i).get("Id")+""});


            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.test_insert:
                String s= UtilisClass.getStringDate();
                dbOpenHelper.insert("Announcement",new String[]{"Title","Content","IsRead","PubTime"},new Object[]{"关于"+s,"环境报数","0",s});


                break;
            case R.id.latestannouncement_titleback:

                getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
                break;
        }

    }
}
