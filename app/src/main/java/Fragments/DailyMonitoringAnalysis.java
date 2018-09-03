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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import DBUtils.DBOpenHelper;
import EventClass.MonitoringAnalysisEvent;
import Utils.CommonAdapter;
import Utils.NetUtils;
import Utils.ViewHolder;
import config.ISystemConfig;
import config.SystemConfigFactory;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import zj.com.mc.MainActivity;
import zj.com.mc.MonitoringAnalysis;
import zj.com.mc.Myapplilcation;
import zj.com.mc.R;
import zj.com.mc.UtilisClass;

/**
 * 监控分析单Fragment
 */
public class DailyMonitoringAnalysis extends BaseFragment {

    private DBOpenHelper dbOpenHelper;
    private List<Map> listanlysis;
    private CommonAdapter adapteranlysis;
    private ListView lvanlysis;
    private TextView tv_nomonitoringanalysis;
    private Intent intentanlysis;
    private Bundle bundleanlysis;
    private String personId;

    private ISystemConfig systemConfig;

    @Override
    View initView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dailymonitoringanaly, container, false);
        lvanlysis = (ListView) view.findViewById(R.id.daily_monitoringanalysis_list);
        tv_nomonitoringanalysis = (TextView) view.findViewById(R.id.tv_nomonitoringanalysis);
        dbOpenHelper = DBOpenHelper.getInstance(getActivity().getApplicationContext());
        systemConfig = SystemConfigFactory.getInstance(getActivity()).getSystemConfig();
        title.setText("监 控 分 析 单");
        personId = systemConfig.getUserId();
        listanlysis = new ArrayList<>();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initview();
    }

    private void initview() {
        listanlysis = dbOpenHelper.queryListMap("select * from InstructorAnalysis where InstructorId=?", new String[]{personId});
        if (listanlysis.size() > 0) {
            tv_nomonitoringanalysis.setVisibility(View.GONE);
            Collections.reverse(listanlysis);
            adapteranlysis = new CommonAdapter<Map>(getActivity(), listanlysis, R.layout.dailylistinglistitem) {
                @Override
                protected void convertlistener(final ViewHolder holder, final Map map) {
                    holder.getView(R.id.daily_Listing_itemdo).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (map.get("IsUploaded").equals(0)) {
                                Myapplilcation.getExecutorService().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        NetUtils.updataarguments3dingle(dbOpenHelper, systemConfig, "InstructorAnalysis", map.get("Id") + "");
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
                    holder.setText(R.id.daily_Listing_itemmessage, map.get("TrainCode") + "");
                    holder.setText(R.id.daily_Listing_itemtype, UtilisClass.getName(dbOpenHelper, String.valueOf(map.get("DriverId")) + ""));
                    if (map.get("RunSection").equals("-")) {
                        holder.setText(R.id.daily_Listing_itemnumber, "");
                    } else {
                        holder.setText(R.id.daily_Listing_itemnumber, map.get("RunSection") + "");
                    }
                    String analysistime = map.get("AnalysisStart") + "";
                    if (analysistime.length() > 1) {
                        String analysisdata = analysistime.split(" ")[0];
                        holder.setText(R.id.daily_Listing_itemtime, analysisdata);
                    } else {
                        holder.setText(R.id.daily_Listing_itemtime, "");
                    }

                    if (map.get("IsUploaded").equals(0)) {
                        holder.setText(R.id.daily_Listing_itemdo, "上传");
                        holder.setTextViewBGD(R.id.daily_Listing_itemdo, R.mipmap.i2_3);
                    } else {
                        holder.setText(R.id.daily_Listing_itemdo, "已上传");
                        holder.setTextViewBGD(R.id.daily_Listing_itemdo, R.mipmap.i2_2);
                    }
                }
            };
            lvanlysis.setAdapter(adapteranlysis);
            lvanlysis.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    intentanlysis = new Intent(getActivity(), MonitoringAnalysis.class);
                    bundleanlysis = new Bundle();
                    bundleanlysis.putInt("listitemId", (Integer) listanlysis.get(i).get("Id"));
                    bundleanlysis.putString("IsUploaded", listanlysis.get(i).get("IsUploaded").toString());
                    intentanlysis.putExtra("InstructorAnalysis", bundleanlysis);
                    startActivity(intentanlysis);
                }
            });
        } else {
            tv_nomonitoringanalysis.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void setDateChanged(String date) {
        initview();
    }

    @Override
    protected void setDataButton() {
        intentanlysis = new Intent(getActivity(), MonitoringAnalysis.class);
        bundleanlysis = new Bundle();
        bundleanlysis.putInt("listitemId", -1);
        intentanlysis.putExtra("InstructorAnalysis", bundleanlysis);
        startActivity(intentanlysis);
    }

    @Override
    protected void setTestButton() {
    }

    @Override
    protected void showAddButton(TextView textView) {
    }
}