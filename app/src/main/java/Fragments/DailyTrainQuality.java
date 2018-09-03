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
import Utils.CommonAdapter;
import Utils.NetUtils;
import Utils.ViewHolder;
import config.ISystemConfig;
import config.SystemConfigFactory;
import zj.com.mc.MainActivity;
import zj.com.mc.Myapplilcation;
import zj.com.mc.R;
import zj.com.mc.TrainQualityDetais;
import zj.com.mc.UtilisClass;

/**
 * 机车质量登记Fragment
 */
public class DailyTrainQuality extends BaseFragment {

    private DBOpenHelper dbOpenHelper;
    private List<Map> listquality;
    private CommonAdapter adapter;
    private ListView qualitylv;
    private TextView tv_notrainquality;
    private Intent intentquality;
    private Bundle bundlequality;
    private String personId;
    private ISystemConfig systemConfig;

    @Override
    View initView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dailytrainquality, container, false);
        dbOpenHelper = DBOpenHelper.getInstance(getActivity().getApplicationContext());
        systemConfig = SystemConfigFactory.getInstance(getActivity()).getSystemConfig();
        qualitylv = (ListView) view.findViewById(R.id.daily_trainquality_list);
        tv_notrainquality = (TextView) view.findViewById(R.id.tv_notrainquality);
        title.setText("机 车 质 量 登 记");
        personId = systemConfig.getUserId();
        listquality = new ArrayList<>();
        return view;
    }

    private void initview() {
        listquality = dbOpenHelper.queryListMap("select * from InstructorLocoQuality where InstructorId=?",
                new String[]{personId});
        if (listquality.size() > 0) {
            tv_notrainquality.setVisibility(View.GONE);
            Collections.reverse(listquality);
            adapter = new CommonAdapter<Map>(getActivity(), listquality, R.layout.dailylistinglistitem) {
                @Override
                protected void convertlistener(final ViewHolder holder, final Map map) {
                    holder.getView(R.id.daily_Listing_itemdo).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            if (map.get("IsUploaded").equals(0)) {
                                Myapplilcation.getExecutorService().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        NetUtils.updataarguments3dingle(dbOpenHelper, systemConfig, "InstructorLocoQuality", map.get("Id") + "");
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
                    holder.setText(R.id.daily_Listing_itemmessage, String.valueOf(map.get("TrainCode")));
                    String drivername = UtilisClass.getName(dbOpenHelper, map.get("DriverId") + "");
                    holder.setText(R.id.daily_Listing_itemtype, drivername);
                    holder.setText(R.id.daily_Listing_itemnumber, map.get("LocomotiveType") + "");
                    holder.setText(R.id.daily_Listing_itemtime, map.get("RegistDate") + "");
                    if (map.get("IsUploaded").equals(0)) {
                        holder.setText(R.id.daily_Listing_itemdo, "上传");
                        holder.setTextViewBGD(R.id.daily_Listing_itemdo, R.mipmap.i2_3);
                    } else {
                        holder.setText(R.id.daily_Listing_itemdo, "已上传");
                        holder.setTextViewBGD(R.id.daily_Listing_itemdo, R.mipmap.i2_2);
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
                    bundlequality.putString("IsUploaded", listquality.get(i).get("IsUploaded").toString());
                    intentquality.putExtra("InstructorLocoQuality", bundlequality);
                    startActivity(intentquality);
                }
            });
        } else {
            tv_notrainquality.setVisibility(View.VISIBLE);
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
        intentquality = new Intent(getActivity(), TrainQualityDetais.class);
        bundlequality = new Bundle();
        bundlequality.putInt("listitemId", -1);
        intentquality.putExtra("InstructorLocoQuality", bundlequality);
        startActivity(intentquality);
    }

    @Override
    public void onResume() {
        super.onResume();
        initview();
    }
}
