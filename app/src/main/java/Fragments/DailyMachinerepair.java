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
import zj.com.mc.MachineDetails;
import zj.com.mc.Myapplilcation;
import zj.com.mc.R;
import zj.com.mc.UtilisClass;

/**
 * 机破维修记录Fragment
 */
public class DailyMachinerepair extends BaseFragment {

    private DBOpenHelper dbOpenHelper;
    private List<Map> listmachine;
    private CommonAdapter<Map> adaptermachine;
    private ListView machinlist;
    private TextView tv_nomachinerepair;
    private Intent intentmach;
    private Bundle bundlemach;
    private String personId;
    private ISystemConfig systemConfig;

    @Override
    View initView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dailymachinerepair, container, false);
        machinlist = (ListView) view.findViewById(R.id.daily_machinerepair_list);
        tv_nomachinerepair = (TextView) view.findViewById(R.id.tv_nomachinerepair);
        title.setText("机 破 维 修 记 录");
        dbOpenHelper = DBOpenHelper.getInstance(getActivity().getApplicationContext());
        systemConfig = SystemConfigFactory.getInstance(getActivity()).getSystemConfig();
        personId = systemConfig.getUserId();
        listmachine = new ArrayList<>();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initview();
    }

    private void initview() {
        listmachine = dbOpenHelper.queryListMap("select * from InstructorRepair where InstructorId=?", new String[]{personId});
        if (listmachine.size() > 0) {
            tv_nomachinerepair.setVisibility(View.GONE);
            Collections.reverse(listmachine);
            adaptermachine = new CommonAdapter<Map>(getActivity(), listmachine, R.layout.dailylistinglistitem) {
                @Override
                protected void convertlistener(final ViewHolder holder, final Map map) {
                    holder.getView(R.id.daily_Listing_itemdo).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (map.get("IsUploaded").equals(0)) {
                                Myapplilcation.getExecutorService().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        NetUtils.updataarguments3dingle(dbOpenHelper, systemConfig, "InstructorRepair", map.get("Id") + "");
                                    }
                                });
                                List<Map> isUpdata = dbOpenHelper.queryListMap("select * from InstructorRepair where Id=?", new String[]{map.get("Id") + ""});
                                String isuploaded = isUpdata.get(0).get("IsUploaded") + "";
                                map.put("IsUploaded", "1");
                                holder.setText(R.id.daily_Listing_itemdo, "已上传");
                                holder.setTextViewBGD(R.id.daily_Listing_itemdo, R.mipmap.i2_2);
                            }
                        }
                    });
                }

                @Override
                public void convert(ViewHolder holder, Map map) {
                    holder.setText(R.id.daily_Listing_itemmessage, String.valueOf(map.get("LocomotiveType")));
                    holder.setText(R.id.daily_Listing_itemtype, UtilisClass.getName(dbOpenHelper, String.valueOf(map.get("DriverId")) + ""));
                    holder.setText(R.id.daily_Listing_itemnumber, map.get("TrainCode") + "");
                    holder.setText(R.id.daily_Listing_itemtime, map.get("HappenTime") + "");
                    if (map.get("IsUploaded").equals(0)) {
                        holder.setText(R.id.daily_Listing_itemdo, "上传");
                    } else {
                        holder.setText(R.id.daily_Listing_itemdo, "已上传");
                        holder.setTextViewBGD(R.id.daily_Listing_itemdo, R.mipmap.i2_2);
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
                    bundle.putString("IsUploaded", listmachine.get(i).get("IsUploaded").toString());
                    intent.putExtra("InstructorRepair", bundle);
                    startActivity(intent);
                }
            });
        } else {
            tv_nomachinerepair.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void setDateChanged(String date) {
        initview();
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
    protected void setTestButton() {}

    @Override
    protected void showAddButton(TextView textView) {}
}