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
import zj.com.mc.SelectiveDatials;

/**
 * 抽查信息单Fragment
 */
public class  Dailyselectlisting extends BaseFragment {

    private DBOpenHelper dbOpenHelper;
    private List<Map> listselec;
    private CommonAdapter<Map> adapterselec1;
    private ListView addlv;
    private TextView tv_noselect;
    private Intent intentselect;
    private Bundle bundleselect;
    private String personId;
    private ISystemConfig systemConfig;

    @Override
    View initView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dailyselectlisting, container, false);
        addlv = (ListView) view.findViewById(R.id.daily_Listing_list);
        tv_noselect = (TextView) view.findViewById(R.id.tv_noselect);
        title.setText("抽 查 信 息 单");
        dbOpenHelper = DBOpenHelper.getInstance(getActivity().getApplicationContext());
        systemConfig = SystemConfigFactory.getInstance(getActivity()).getSystemConfig();
        personId = systemConfig.getUserId();
        return view;
    }

    private void initview() {
        listselec = dbOpenHelper.queryListMap("select * from InstructorCheck where InstructorId=?", new String[]{personId});
        if (listselec.size() != 0) {
            tv_noselect.setVisibility(View.GONE);
            Collections.reverse(listselec);
            adapterselec1 = new CommonAdapter<Map>(getActivity(), listselec, R.layout.dailylistinglistitem) {
                @Override
                protected void convertlistener(final ViewHolder holder, final Map map) {
                    holder.getView(R.id.daily_Listing_itemdo).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (map.get("IsUploaded").equals(0)) {
                                Myapplilcation.getExecutorService().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        NetUtils.updataarguments3dingle(dbOpenHelper, systemConfig, "InstructorCheck", map.get("Id") + "");
                                    }
                                });
                                List<Map> isUpdata = dbOpenHelper.queryListMap("select * from InstructorCheck where Id=?", new String[]{map.get("Id") + ""});
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
                    holder.setText(R.id.daily_Listing_itemmessage, String.valueOf(map.get("Location")));
                    holder.setText(R.id.daily_Listing_itemtype, String.valueOf(map.get("CheckType")));
                    holder.setText(R.id.daily_Listing_itemnumber, map.get("ProblemCount") + "");
                    if (map.get("StartTime").toString().length() > 11) {
                        holder.setText(R.id.daily_Listing_itemtime, map.get("StartTime").toString().split(" ")[0]);
                    } else {
                        holder.setText(R.id.daily_Listing_itemtime, map.get("StartTime").toString());
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
            addlv.setAdapter(adapterselec1);
            addlv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    intentselect = new Intent(getActivity(), SelectiveDatials.class);
                    bundleselect = new Bundle();
                    bundleselect.putInt("listitemId", (Integer) listselec.get(i).get("Id"));
                    bundleselect.putString("IsUploaded", listselec.get(i).get("IsUploaded").toString());
                    intentselect.putExtra("InstructorCheck", bundleselect);
                    startActivity(intentselect);
                }
            });
        } else {
            tv_noselect.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initview();
    }

    @Override
    protected void setDateChanged(String date) {
        initview();
    }

    @Override
    protected void setTestButton() {
    }

    @Override
    protected void showAddButton(TextView textView) {
    }

    @Override
    protected void setDataButton() {
        intentselect = new Intent(getActivity(), SelectiveDatials.class);
        bundleselect = new Bundle();
        bundleselect.putInt("listitemId", -1);
        intentselect.putExtra("InstructorCheck", bundleselect);
        startActivity(intentselect);
    }

    public void showToast(String str) {
        Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
