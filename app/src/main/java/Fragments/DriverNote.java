package Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import Adapter.CarKeepAdapter;
import Adapter.CarplanAdapter;
import zj.com.mc.R;


/**
 * Created by dell on 2016/7/29.
 */
public class DriverNote extends Fragment implements View.OnClickListener{
    private ListView lv_carplan;
    private ListView lv_carkeep;
    private CarplanAdapter carplanAdapter;
    private CarKeepAdapter carkeepAdapter;
    private TextView tv_one_up,tv_work;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.drivernote,container,false);
        inintView(view);
        inintDate();
        return view;
    }

    private void inintDate() {
        List<Integer> list=new ArrayList<>();
        for (int i=0;i<4;i++){
            list.add(i);
        }
        carplanAdapter=new CarplanAdapter(list,getActivity());
        carkeepAdapter=new CarKeepAdapter(list,getActivity());

        lv_carplan.setAdapter(carplanAdapter);
        lv_carkeep.setAdapter(carkeepAdapter);
        setListViewHeightBasedOnChildren(lv_carkeep);
        setListViewHeightBasedOnChildren(lv_carplan);

        carkeepAdapter.setOnbtnClick(new CarKeepAdapter.onbtnclick() {
            //上传按钮
            @Override
            public void onUpbtnClick(View v) {

            }
            //修改按钮
            @Override
            public void onModifybtnClick(View v) {

            }
        });

        lv_carkeep.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getContext(),"item",Toast.LENGTH_SHORT);
            }
        });
    }

    private void inintView(View v) {
        lv_carplan= (ListView) v.findViewById(R.id.lv_carplan);
        lv_carkeep= (ListView) v.findViewById(R.id.lv_carkeep);
        tv_one_up= (TextView) v.findViewById(R.id.tv_one_up);
        tv_work= (TextView) v.findViewById(R.id.tv_work);

        tv_one_up.setOnClickListener(this);
        tv_work.setOnClickListener(this);
    }

    public void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            // 计算子项View 的宽高
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //出勤
            case R.id.tv_work:
                break;
            //一键上传
            case R.id.tv_one_up:
                break;
        }
    }
}
