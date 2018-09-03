package Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import DBUtils.DBOpenHelper;
import Utils.CommonAdapter;
import Utils.NetUtils;
import Utils.ViewHolder;
import config.ISystemConfig;
import config.SystemConfigFactory;
import zj.com.mc.Myapplilcation;
import zj.com.mc.R;
import zj.com.mc.StandardizedActivity;
import zj.com.mc.UtilisClass;

/**
 * 标准化验收Fragment
 */
public class StandardizedAcceptance extends BaseFragment {

    private DBOpenHelper dbOpenHelper;
    private List<Map> listacceptanceine;
    private CommonAdapter<Map> adapteracceptanceine;
    private ListView acceptanceinlist;
    private TextView tv_nostandardizedacceptance;
    private Intent intentacceptance;
    private Bundle bundleacceptance;
    private String personId;
    private ISystemConfig systemConfig;

    @Override
    View initView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.standardizedacceptance, container, false);
        acceptanceinlist = (ListView) view.findViewById(R.id.daily_acceptance_list2);
        tv_nostandardizedacceptance = (TextView) view.findViewById(R.id.tv_nostandardizedacceptance);
        dbOpenHelper = DBOpenHelper.getInstance(getActivity().getApplicationContext());
        systemConfig = SystemConfigFactory.getInstance(getActivity()).getSystemConfig();
        title.setText("标 准 化 验 收");
        personId = systemConfig.getUserId();
        listacceptanceine = new ArrayList<>();
        return view;
    }

    private void initview() {
        listacceptanceine = dbOpenHelper.queryListMap("select * from InstructorAccept where InstructorId=?", new String[]{personId});
        if (listacceptanceine.size() > 0) {
            tv_nostandardizedacceptance.setVisibility(View.GONE);
            Collections.reverse(listacceptanceine);
            adapteracceptanceine = new CommonAdapter<Map>(getActivity(), listacceptanceine, R.layout.dailylistinglistitem) {
                @Override
                protected void convertlistener(final ViewHolder holder, final Map map) {
                    holder.getView(R.id.daily_Listing_itemdo).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (map.get("IsUploaded").equals(0)) {
                                Myapplilcation.getExecutorService().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        NetUtils.updataarguments3dingle(dbOpenHelper, systemConfig, "InstructorAccept", map.get("Id") + "");
                                    }
                                });
                                map.put("IsUploaded", "1");
                                holder.setText(R.id.daily_Listing_itemdo, "已上传");
                                holder.setTextViewBGD(R.id.daily_Listing_itemdo, R.mipmap.i2_2);
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
                    }else{
                        holder.setText(R.id.daily_Listing_itemtype, "");
                    }

                    List<Map> auxdriverinfo = dbOpenHelper.queryListMap("select * from PersonInfo where Id= ?", new String[]{map.get("ViceDriverId").toString()});
                    if (auxdriverinfo.size() != 0) {
                        holder.setText(R.id.daily_Listing_itemnumber, auxdriverinfo.get(0).get("Name").toString());
                    }else{
                        holder.setText(R.id.daily_Listing_itemnumber, "");
                    }
                    holder.setText(R.id.daily_Listing_itemtime, map.get("AcceptDate") + "");
                    if (map.get("IsUploaded").equals(0)) {
                        holder.setText(R.id.daily_Listing_itemdo, "上传");
                        holder.setTextViewBGD(R.id.daily_Listing_itemdo, R.mipmap.i2_3);
                    } else {
                        holder.setText(R.id.daily_Listing_itemdo, "已上传");
                        holder.setTextViewBGD(R.id.daily_Listing_itemdo, R.mipmap.i2_2);
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
                    bundle.putString("IsUploaded", listacceptanceine.get(i).get("IsUploaded").toString());
                    intent.putExtra("InstructorAccept", bundle);
                    startActivity(intent);
                }
            });
        }else {
            tv_nostandardizedacceptance.setVisibility(View.VISIBLE);
        }

    }


    @Override
    protected void setDateChanged(String date) {
        initview();
    }

    @Override
    protected void setTestButton() {}

    @Override
    protected void showAddButton(TextView textView) {}

    @Override
    protected void setDataButton() {
        UtilisClass.showToast(getActivity(), "添加记录");
        intentacceptance = new Intent(getActivity(), StandardizedActivity.class);
        bundleacceptance = new Bundle();
        bundleacceptance.putInt("listitemId", -1);
        intentacceptance.putExtra("InstructorAccept", bundleacceptance);
        startActivity(intentacceptance);
    }

    @Override
    public void onResume() {
        super.onResume();
        initview();
    }
}