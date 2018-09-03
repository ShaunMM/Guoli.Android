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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import DBUtils.DBOpenHelper;
import Utils.CommonAdapter;
import Utils.NetUtils;
import Utils.ViewHolder;
import config.ISystemConfig;
import config.SystemConfigFactory;
import de.greenrobot.event.EventBus;
import zj.com.mc.CourseTrain;
import zj.com.mc.Myapplilcation;
import zj.com.mc.R;

/**
 * 授课培训记录Frgment
 */
public class DailyCourseTrain extends BaseFragment {

    private Intent intentcourse;
    private Bundle bundlecourse;
    private DBOpenHelper dbOpenHelper;
    private ListView daily_coursetrain_list;
    private TextView tv_nocoursetrain;
    private CommonAdapter adapteraourse;
    private List<Map> listcourse;
    private String personId;
    private String sql;
    private ISystemConfig systemConfig;

    @Override
    View initView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dailycoursetrain, container, false);
        daily_coursetrain_list = (ListView) view.findViewById(R.id.daily_coursetrain_list);
        tv_nocoursetrain = (TextView) view.findViewById(R.id.tv_nocoursetrain);
        dbOpenHelper = DBOpenHelper.getInstance(getActivity().getApplicationContext());
        systemConfig = SystemConfigFactory.getInstance(getActivity()).getSystemConfig();
        title.setText("授课培训记录");
        sql = "select * from InstructorTeach where InstructorId=?";
        personId = systemConfig.getUserId();
        listcourse = new ArrayList<>();
        return view;
    }

    private void inintview() {
        listcourse = dbOpenHelper.queryListMap(sql, new String[]{personId});
        if (listcourse.size() > 0) {
            tv_nocoursetrain.setVisibility(View.GONE);
            Collections.reverse(listcourse);
            adapteraourse = new CommonAdapter<Map>(getActivity(), listcourse, R.layout.dailylistinglistitem) {
                @Override
                protected void convertlistener(final ViewHolder holder, final Map map) {
                    holder.getView(R.id.daily_Listing_itemdo).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            if (map.get("IsUploaded").equals(0)) {
                                Myapplilcation.getExecutorService().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        NetUtils.updataarguments3dingle(dbOpenHelper, systemConfig, "InstructorTeach", map.get("Id") + "");
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
                    if ((map.get("TeachStart").toString().length()>11)&&(map.get("TeachEnd").toString().length()>11)){
                        long startlong = Long.valueOf(dateToStamp(map.get("TeachStart").toString() + ":00"));
                        long stoplong = Long.valueOf(dateToStamp(map.get("TeachEnd").toString() + ":00"));
                        holder.setText(R.id.daily_Listing_itemtype, stampToDate(String.valueOf(stoplong - startlong)));
                    }else{
                        holder.setText(R.id.daily_Listing_itemtype, "");
                    }
                    holder.setText(R.id.daily_Listing_itemmessage, String.valueOf(map.get("TeachPlace")));
                    holder.setText(R.id.daily_Listing_itemnumber, String.valueOf(map.get("JoinCount")));
                    holder.setText(R.id.daily_Listing_itemtime, map.get("TeachStart") + "");
                    if (map.get("IsUploaded").equals(0)) {
                        holder.setText(R.id.daily_Listing_itemdo, "上传");
                        holder.setTextViewBGD(R.id.daily_Listing_itemdo, R.mipmap.i2_3);
                    } else {
                        holder.setText(R.id.daily_Listing_itemdo, "已上传");
                        holder.setTextViewBGD(R.id.daily_Listing_itemdo, R.mipmap.i2_2);
                    }
                }
            };

            daily_coursetrain_list.setAdapter(adapteraourse);
            daily_coursetrain_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    intentcourse = new Intent(getActivity(), CourseTrain.class);
                    bundlecourse = new Bundle();
                    bundlecourse.putInt("listitemId", (Integer) listcourse.get(i).get("Id"));
                    bundlecourse.putString("IsUploaded", listcourse.get(i).get("IsUploaded").toString());
                    intentcourse.putExtra("InstructorTeach", bundlecourse);
                    startActivity(intentcourse);
                }
            });
        } else {
            tv_nocoursetrain.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void setDateChanged(String date) {
        inintview();
    }

    @Override
    protected void setTestButton() {}

    @Override
    protected void setDataButton() {
        //添加记录
        intentcourse = new Intent(getActivity(), CourseTrain.class);
        bundlecourse = new Bundle();
        bundlecourse.putInt("listitemId", -1);
        intentcourse.putExtra("InstructorTeach", bundlecourse);
        startActivity(intentcourse);
    }

    @Override
    protected void showAddButton(TextView textView) {}

    public void setToast(String str) {
        Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        inintview();
    }

    /*
     * 将时间转换为时间戳
     */
    public static String dateToStamp(String s) {
        String res = null;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = simpleDateFormat.parse(s);
            long ts = date.getTime();
            res = String.valueOf(ts);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    /*
     * 将时间戳转换为时间
     */
    public static String stampToDate(String s) {
        String res;
        long lt = new Long(s);
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        res = formatter.format(lt);
        return res;
    }
}