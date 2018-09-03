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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import DBUtils.DBOpenHelper;
import EventClass.BreakRulesEvent;
import Utils.CommonAdapter;
import Utils.NetUtils;
import Utils.ViewHolder;
import config.ISystemConfig;
import config.SystemConfigFactory;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import zj.com.mc.DailyBreakRulsDetails;
import zj.com.mc.MainActivity;
import zj.com.mc.Myapplilcation;
import zj.com.mc.R;
import zj.com.mc.UtilisClass;

/**
 * 违章违纪记录Fragment
 */
public class DailyBreakRuls extends BaseFragment {
    private DBOpenHelper dbOpenHelper;
    private List<Map> listbreak;
    private CommonAdapter<Map> adapterbreak;
    private ListView daily_breakruls_list;
    private TextView tv_nodailybreakruls;
    private Intent intentbreak;
    private Bundle bundlebreak;
    private String personId;
    private String sql;
    private ISystemConfig systemConfig;

    @Override
    View initView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dailybreakrules, container, false);
        dbOpenHelper = DBOpenHelper.getInstance(getActivity().getApplicationContext());
        systemConfig = SystemConfigFactory.getInstance(getActivity()).getSystemConfig();
        daily_breakruls_list = (ListView) view.findViewById(R.id.daily_breakruls_list);
        tv_nodailybreakruls = (TextView) view.findViewById(R.id.tv_nodailybreakruls);
        title.setText("违 章 违 纪 记 录");
        sql = "select * from InstructorPeccancy where InstructorId=?";
        personId = systemConfig.getUserId();
        listbreak = new ArrayList<>();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initview();
    }

    private void initview() {
        listbreak = dbOpenHelper.queryListMap(sql, new String[]{personId});
        if (listbreak.size() <= 0) {
            tv_nodailybreakruls.setVisibility(View.VISIBLE);
        } else {
            tv_nodailybreakruls.setVisibility(View.GONE);
            Collections.reverse(listbreak);
            adapterbreak = new CommonAdapter<Map>(getActivity(), listbreak, R.layout.dailylistinglistitem) {
                @Override
                protected void convertlistener(final ViewHolder holder, final Map map) {

                    holder.getView(R.id.daily_Listing_itemdo).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (map.get("IsUploaded").equals(0)) {
                                Myapplilcation.getExecutorService().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        NetUtils.updataarguments3dingle(dbOpenHelper, systemConfig, "InstructorPeccancy", map.get("Id") + "");
                                    }
                                });
                                List<Map> isUpdata = dbOpenHelper.queryListMap("select * from InstructorPeccancy where Id=?", new String[]{map.get("Id") + ""});
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
                    holder.setText(R.id.daily_Listing_itemmessage, String.valueOf(UtilisClass.getName(dbOpenHelper, map.get("DriverId") + "")));
                    holder.setText(R.id.daily_Listing_itemtype, String.valueOf(UtilisClass.getWorkNo(dbOpenHelper, map.get("DriverId") + "")));
                    holder.setText(R.id.daily_Listing_itemnumber, map.get("PeccancyType") + "");
                    holder.setText(R.id.daily_Listing_itemtime, map.get("WriteDate") + "");
                    if (map.get("IsUploaded").equals(0)) {
                        holder.setText(R.id.daily_Listing_itemdo, "上传");
                        holder.setTextViewBGD(R.id.daily_Listing_itemdo, R.mipmap.i2_3);
                    } else {
                        holder.setText(R.id.daily_Listing_itemdo, "已上传");
                        holder.setTextViewBGD(R.id.daily_Listing_itemdo, R.mipmap.i2_2);
                    }
                }
            };

            daily_breakruls_list.setAdapter(adapterbreak);

            daily_breakruls_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    intentbreak = new Intent(getActivity(), DailyBreakRulsDetails.class);
                    bundlebreak = new Bundle();
                    bundlebreak.putInt("listitemId", (Integer) listbreak.get(i).get("Id"));
                    bundlebreak.putString("IsUploaded", listbreak.get(i).get("IsUploaded").toString());
                    intentbreak.putExtra("InstructorPeccancy", bundlebreak);
                    startActivity(intentbreak);
                }
            });
        }
    }

    @Override
    protected void setDateChanged(String date) {
        initview();
    }

    @Override
    protected void setTestButton() {}

    @Override
    protected void setDataButton() {
        intentbreak = new Intent(getActivity(), DailyBreakRulsDetails.class);
        bundlebreak = new Bundle();
        bundlebreak.putInt("listitemId", -1);
        intentbreak.putExtra("InstructorPeccancy", bundlebreak);
        startActivity(intentbreak);
    }

    @Override
    protected void showAddButton(TextView textView) {
    }

}