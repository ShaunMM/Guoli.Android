package Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import DBUtils.DBOpenHelper;
import Utils.CommonAdapter;
import Utils.NetUtils;
import Utils.ViewHolder;
import zj.com.mc.R;


/**
 * Created by dell on 2016/7/29.
 */
public class TrainSchedule extends Fragment implements View.OnClickListener {

    private FragmentManager manager;
    private FragmentTransaction transaction;
    private GridView searchhistory;

    private AutoCompleteTextView edit;
    private Toast toast;
    private DBOpenHelper dbOpenHelper;
    private List<String> searchCheci;
    private List<String> historyList;
    private String search;
    private boolean Issame;
    private List<String>list=new ArrayList<>();
    private List<String> lists=new ArrayList<>();
    private String searchcode;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.trainschedule_listitem, container, false);
        edit = (AutoCompleteTextView) view.findViewById(R.id.trainsche_search_edit);
        searchhistory= (GridView) view.findViewById(R.id.searchhistory);
        view.findViewById(R.id.trainsche_search).setOnClickListener(this);
        dbOpenHelper = DBOpenHelper.getInstance(getActivity().getApplicationContext());
        manager = getActivity().getSupportFragmentManager();
        getdbhistory();

        initspinnersearch();

        return view;
    }
//动态添加搜索下拉联想列表
    private void initspinnersearch() {
        List<Map> searchCheci1 = dbOpenHelper.queryListMap("select FullName from TrainNo", null);
        searchCheci = new ArrayList<String>();
        if (searchCheci1.size() != 0) {
            for (int i = 0; i < searchCheci1.size(); i++) {
                searchCheci.add(searchCheci1.get(i).get("FullName") + "");
            }
        }
        edit.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, searchCheci));
        edit.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView textView= (TextView) view;
                String s=textView.getText()+"";
                edit.setText(s);
                searchcodedata();
            }
        });
    }

    private void getdbhistory(){
        historyList=new ArrayList<>();
        List<Map> list=dbOpenHelper.queryListMap("select * from SeacherHistory",null);
//        historyList=
        if (list.size()!=0) {
            String searchcodes="";
            for (int i = 0; i < list.size(); i++) {
                    searchcodes=list.get(i).get("SearchCode")+"";
                historyList.add(searchcodes);
            }
        }

        getHistory(historyList);

    }


    //自定义显示中间位置Toast
    public void setToast(String str) {
        toast = Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    private void searchcodedata(){
        searchcode=edit.getText()+"";
        if (!searchcode.equals("")) {
            //输入内容为指定值
            List<Map> list=dbOpenHelper.queryListMap("select * from TrainNo where FullName=?",new String[]{searchcode});
            if (list.size()!=0) {

                dbOpenHelper.insert("SeacherHistory",new String[]{"SearchCode"},new Object[]{searchcode});

                lists.add(searchcode);
                getHistory(lists);
                TrainScheduleresult tResult = new TrainScheduleresult();
                SharedPreferences s=getActivity().getSharedPreferences("TrainScheduleCode", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = s.edit();
                editor.putString("seachcode", edit.getText().toString());
                edit.setText("");
                editor.commit();
                initFragment(tResult);

            } else {
                //输入内容找不到
                setToast("没有该车次信息，请检查输入是否正确");
                edit.setText("");
            }
        }else {//输入内容为空
            setToast("输入内容不能为空");
            edit.setText("");
        }
    }


    @Override
    public void onClick(View view) {
//        输入内容不能为空
        searchcodedata();
    }

    private void initFragment(Fragment fragment) {
        if (!fragment.isAdded()) {
            getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null).add(R.id.frg_conn, fragment).show(fragment).hide(this).commit();
        } else {
            if (fragment.isHidden()) {
                getActivity().getSupportFragmentManager().beginTransaction().show(fragment).commit();
                getActivity().getSupportFragmentManager().beginTransaction().hide(this).commit();
            }
        }
    }
//获得搜索历史
    public void getHistory(List<String> list){

        lists= NetUtils.removeDuplicate(list);//去重


        if (lists.size()>6){
            lists=lists.subList(0,5);
        }
        List<String> newhistorycode=lists;
        Collections.reverse(newhistorycode);
        if (newhistorycode.size()!=0) {

                setadapters(newhistorycode);
//                final List<String> finalLists = lists;


                searchhistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                        SharedPreferences s = getActivity().getSharedPreferences("TrainScheduleCode", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = s.edit();
                        editor.putString("seachcode", lists.get(i));
                        editor.commit();
                        TrainScheduleresult tResult = new TrainScheduleresult();
                        initFragment(tResult);

                    }
                });
            }


    }

    private void setadapters(List<String> list){
//        Collections.reverse(list);

        searchhistory.setAdapter(new CommonAdapter<String>(getActivity(), list, R.layout.trainsche_historylist) {
            @Override
            protected void convertlistener(ViewHolder holder, String s) {
            }
            @Override
            public void convert(ViewHolder holder, String s) {
                holder.setText(R.id.grid1_item2, s);
            }
        });


    }


    @Override
    public void onResume() {
//        getHistory();
        super.onResume();
    }
}

