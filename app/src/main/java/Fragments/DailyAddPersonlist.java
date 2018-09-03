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
import zj.com.mc.AddPersonListItem;
import zj.com.mc.Myapplilcation;
import zj.com.mc.R;
import zj.com.mc.UtilisClass;

/**
 * 添乘信息单Fragment"OperateSection" -> "---"
 * "TakeSection" -> "---"
 */
public class DailyAddPersonlist extends BaseFragment {

    private DBOpenHelper dbOpenHelper;
    private ListView addlv;
    private TextView tv_noaddperson;
    private List<Map> list;
    private CommonAdapter<Map> adapter;
    private Intent intentadd;
    private Bundle bundleadd;
    private String personId;
    private String sql;
    private ISystemConfig systemConfig;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        systemConfig = SystemConfigFactory.getInstance(getActivity()).getSystemConfig();
        dbOpenHelper = DBOpenHelper.getInstance(getActivity().getApplicationContext());
        list = new ArrayList<>();
    }

    @Override
    View initView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tianchengdetail, container, false);
        addlv = (ListView) view.findViewById(R.id.daily_tiancheng_list);
        tv_noaddperson = (TextView) view.findViewById(R.id.tv_noaddperson);
        view.findViewById(R.id.dodata1).setOnClickListener(this);
        sql = "select * from InstructorTempTake where InstructorId=?";
        personId = systemConfig.getUserId();
        title.setText("添 乘 信 息 单");
        return view;
    }

    @Override
    protected void setDateChanged(String date) {
        initview();
    }

    @Override
    public void onResume() {
        super.onResume();
        initview();
    }

    private void initview() {
        list = dbOpenHelper.queryListMap(sql, new String[]{personId});
        if (list.size() <= 0) {
            tv_noaddperson.setVisibility(View.VISIBLE);
        } else {
            tv_noaddperson.setVisibility(View.GONE);
            Collections.reverse(list);
            adapter = new CommonAdapter<Map>(getActivity(), list, R.layout.dailylistinglistitem) {
                @Override
                protected void convertlistener(final ViewHolder holder, final Map map) {

                    holder.getView(R.id.daily_Listing_itemdo).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (map.get("IsUploaded").equals(0)) {
                                Myapplilcation.getExecutorService().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        NetUtils.updataarguments3dingle(dbOpenHelper, systemConfig, "InstructorTempTake", map.get("Id") + "");
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
                    if (map.get("TrainCode") != null) {
                        holder.setText(R.id.daily_Listing_itemmessage, String.valueOf(map.get("TrainCode")));
                    }
                    if (map.get("DriverId") != null) {
                        String drivername = UtilisClass.getName(dbOpenHelper, String.valueOf(map.get("DriverId")));
                        holder.setText(R.id.daily_Listing_itemtype, drivername);
                    }
                    if (map.get("TakeSection").toString().equals("-")) {
                        holder.setText(R.id.daily_Listing_itemnumber, "");
                    } else {
                        holder.setText(R.id.daily_Listing_itemnumber, map.get("TakeSection") + "");
                    }
                    holder.setText(R.id.daily_Listing_itemtime, map.get("TakeDate") + "");
                    if (map.get("IsUploaded").equals(0)) {
                        holder.setText(R.id.daily_Listing_itemdo, "上传");
                        holder.setTextViewBGD(R.id.daily_Listing_itemdo, R.mipmap.i2_3);
                    } else {
                        holder.setText(R.id.daily_Listing_itemdo, "已上传");
                        holder.setTextViewBGD(R.id.daily_Listing_itemdo, R.mipmap.i2_2);
                    }
                }
            };

            addlv.setAdapter(adapter);
            addlv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    intentadd = new Intent(getActivity(), AddPersonListItem.class);
                    bundleadd = new Bundle();
                    bundleadd.putInt("listitemId", (Integer) list.get(i).get("Id"));
                    bundleadd.putString("IsUploaded", list.get(i).get("IsUploaded").toString());
                    intentadd.putExtra("InstructorTempTake", bundleadd);
                    startActivity(intentadd);
                }
            });
        }
    }

    @Override
    protected void setTestButton() {
    }

    @Override
    protected void setDataButton() {
        intentadd = new Intent(getActivity(), AddPersonListItem.class);
        bundleadd = new Bundle();
        bundleadd.putInt("listitemId", -1);
        intentadd.putExtra("InstructorTempTake", bundleadd);
        startActivity(intentadd);
    }

    @Override
    protected void showAddButton(TextView textView) {
    }
}
