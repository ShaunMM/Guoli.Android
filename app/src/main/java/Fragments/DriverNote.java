package Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.kymjs.kjframe.KJHttp;
import org.kymjs.kjframe.http.HttpCallBack;
import org.kymjs.kjframe.http.HttpParams;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Adapter.CarKeepAdapter;
import Adapter.CarplanAdapter;
import DBUtils.DBOpenHelper;
import Utils.CommonAdapter;
import Utils.NetUtils;
import Utils.ViewHolder;
import config.ISystemConfig;
import config.SystemConfigFactory;
import zj.com.mc.AttentInfoActivity;
import zj.com.mc.MainActivity;
import zj.com.mc.R;

/**
 * 司机手账
 */
public class DriverNote extends Fragment implements View.OnClickListener {

    private ListView lv_carplan;//今日行车计划
    private ListView lv_carkeep;//近期手账记录
    private CarplanAdapter carplanAdapter;//今日行车计划
    private CarKeepAdapter carkeepAdapter;//近期手账记录
    private TextView tv_one_up;//一键上传
    private TextView tv_work;//出勤
    private LinearLayout ll_noplan;
    private TextView tv_nokeep;
    private DBOpenHelper openHelper;
    private String sql;//行车计划
    private String recordsql;//行车记录
    private List<Map> records;
    private boolean isClick;
    private ISystemConfig systemConfig;

    private TextView tv_addcarcode;//手动添加出勤计划
    private RelativeLayout tiancheng_popuwindow;
    private EditText et_addcode;
    private ListView lv_addcodes;
    private List<Map> listsearch;//手动添加的车次信息
    private String addkey;//弹窗内edittext中的文字
    private List<Map> drivePlanList;
    private MainActivity mActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.drivernote, container, false);
        sql = "select * from DrivePlan where DriverNo=?";
        recordsql = "select * from DriveRecords order by Id desc";
        mActivity = (MainActivity) getActivity();
        openHelper = DBOpenHelper.getInstance(getContext());
        systemConfig = SystemConfigFactory.getInstance(getActivity()).getSystemConfig();
        inintView(view);
        inintDate();
        return view;
    }

    private void inintView(View v) {
        lv_carplan = (ListView) v.findViewById(R.id.lv_carplan);
        lv_carkeep = (ListView) v.findViewById(R.id.lv_carkeep);
        tv_one_up = (TextView) v.findViewById(R.id.tv_one_up);
        tv_work = (TextView) v.findViewById(R.id.tv_work);
        ll_noplan = (LinearLayout) v.findViewById(R.id.ll_noplan);
        tv_nokeep = (TextView) v.findViewById(R.id.tv_nokeep);

        tv_addcarcode = (TextView) v.findViewById(R.id.tv_addcarcode);
        tiancheng_popuwindow = (RelativeLayout) v.findViewById(R.id.tiancheng_popuwindow);
        et_addcode = (EditText) v.findViewById(R.id.et_addcode);
        lv_addcodes = (ListView) v.findViewById(R.id.lv_addcodes);

        tiancheng_popuwindow.setOnClickListener(this);
        tiancheng_popuwindow.setVisibility(View.GONE);
        tv_addcarcode.setOnClickListener(this);
        tv_one_up.setOnClickListener(this);
        tv_work.setOnClickListener(this);
    }

    private void inintDate() {

        drivePlanList = new ArrayList<>();
        records = new ArrayList<>();
        listsearch = new ArrayList<>();
        drivePlanList = openHelper.queryListMap(sql, new String[]{systemConfig.getUserAccount()});
        records = openHelper.queryListMap(recordsql, new String[]{});

        if (drivePlanList.size() <= 0) {
            ll_noplan.setVisibility(View.VISIBLE);
            lv_carplan.setVisibility(View.GONE);
        } else {
            ll_noplan.setVisibility(View.GONE);
            carplanAdapter = new CarplanAdapter(drivePlanList, getActivity());
            lv_carplan.setAdapter(carplanAdapter);
            setListViewHeightBasedOnChildren(lv_carplan);
        }

        if (records.size() <= 0) {
            isClick = false;
            tv_one_up.setBackgroundColor(getResources().getColor(R.color.llblack));
            tv_nokeep.setVisibility(View.VISIBLE);
            lv_carkeep.setVisibility(View.GONE);
        } else {
            isClick = true;
            tv_one_up.setBackground(getResources().getDrawable(R.drawable.buttonclickcolor));
            tv_nokeep.setVisibility(View.GONE);

            carkeepAdapter = new CarKeepAdapter(records, getActivity());
            lv_carkeep.setAdapter(carkeepAdapter);
            setListViewHeightBasedOnChildren(lv_carkeep);
        }

        if (carkeepAdapter != null) {
            carkeepAdapter.setOnbtnClick(new CarKeepAdapter.onbtnclick() {
                @Override
                public void onUpbtnClick(View v, int position) {
                    Map map = records.get(position);
                    List<Map> uprecords = new ArrayList<Map>();
                    uprecords.add(map);
                    List<Map> signPointList = openHelper.queryListMap("select * from DriveSignPoint where DriveRecordId=?", new String[]{String.valueOf(map.get("Id"))});
                    List<Map> trainformationList = openHelper.queryListMap("select * from TrainFormation where DriveRecordId=?", new String[]{String.valueOf(map.get("Id"))});
                    List<Map> noandlineList = openHelper.queryListMap("select * from DriveTrainNoAndLine where DriveRecordId=?", new String[]{String.valueOf(map.get("Id"))});
                    boolean up = upDate(uprecords, noandlineList, signPointList, trainformationList, map);
                }

                @Override
                public void onModifybtnClick(View v, int position) {
                    Intent intent = new Intent(getContext(), AttentInfoActivity.class);
                    intent.putExtra("id", String.valueOf(records.get(position).get("Id")));
                    intent.putExtra("modify", "1");
                    startActivity(intent);
                }
            });
        }

        lv_carkeep.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getContext(), "item", Toast.LENGTH_SHORT);
                Intent intent = new Intent(getContext(), AttentInfoActivity.class);
                intent.putExtra("id", String.valueOf(records.get(position).get("Id")));
                intent.putExtra("modify", "0");
                startActivity(intent);
            }
        });
    }

    boolean up = false;

    private boolean upDate(final List<Map> RecordsList, List<Map> DriveTrainNoAndLineList, List<Map> DriveSignPoint, List<Map> TrainFormation, final Map map) {
        KJHttp kjhttp = new KJHttp();
        HttpParams params = new HttpParams();
        params.put("signature", "bcad117ce31ac75fcfa347acefc8d198");

        StringBuffer recordsList = NetUtils.getjson(RecordsList);
        StringBuffer signPoint = NetUtils.getjsonlist(DriveSignPoint);
        StringBuffer trainFormation = NetUtils.getjsonlist(TrainFormation);
        StringBuffer noandline = NetUtils.getjsonlist(DriveTrainNoAndLineList);

        String json = "[{";
        json += "\"DriveRecords\":" + recordsList + ",";
        json += "\"DriveSignPoints\":" + signPoint + ",";
        json += "\"TrainFormations\":" + trainFormation + ",";
        json += "\"DriveTrainNoAndLines\":" + noandline + "}]";

        // \"代表一个双引号字符
        // \\代表一个反斜线字符\
        // json.replace去掉

        json = json.replace("\\\"", "\"")
                .replace("\"null\"", "\"\"")
                .replace("\"ArriveTime\":\"\"", "\"ArriveTime\":\"1970-01-01\"")
                .replace("\"LeaveTime\":\"\"", "\"LeaveTime\":\"1970-01-01\"")
                .replace("\"AttendTime\":\"\"", "\"AttendTime\":\"1970-01-01\"")
                .replace("\"GetTrainTime\":\"\"", "\"GetTrainTime\":\"1970-01-01\"")
                .replace("\"LeaveDepotTime1\":\"\"", "\"LeaveDepotTime1\":\"1970-01-01\"")
                .replace("\"LeaveDepotTime2\":\"\"", "\"LeaveDepotTime2\":\"1970-01-01\"")
                .replace("\"ArriveDepotTime1\":\"\"", "\"ArriveDepotTime1\":\"1970-01-01\"")
                .replace("\"ArriveDepotTime2\":\"\"", "\"ArriveDepotTime2\":\"1970-01-01\"")
                .replace("\"GiveTrainTime\":\"\"", "\"GiveTrainTime\":\"1970-01-01\"")
                .replace("\"RecordEndTime\":\"\"", "\"RecordEndTime\":\"1970-01-01\"");
        try {
            json = URLEncoder.encode(json, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        params.put("json", json);
        showMes();
        kjhttp.post(systemConfig.getHost() + "/App/DrivePlanUpload", params, new HttpCallBack() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                progressDialog.dismiss();
                Toast.makeText(getContext(), "上传成功", Toast.LENGTH_SHORT).show();
                up = true;
                try {
                    boolean ok = openHelper.update("DriveRecords", new String[]{"IsUploaded"}, new Object[]{1}, new String[]{"Id"}, new String[]{String.valueOf(map.get("Id"))});
                    if (ok) {
                        List<Map> list = openHelper.queryListMap("select * from DriveRecords order by Id desc", new String[]{});
                        carkeepAdapter.setList(list);
                    }
                } catch (Exception e) {
                    Log.e("上传手账记录", "数据库上传状态更改异常");
                }
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                progressDialog.dismiss();
                Toast.makeText(getContext(), "上传失败" + errorNo + ":" + strMsg, Toast.LENGTH_SHORT).show();
                up = false;
            }
        });
        return up;
    }

    private ProgressDialog progressDialog;

    private void showMes() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("正在上传，请等待...");
        progressDialog.show();
    }

    private void toWork() {
        lv_carplan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                FragmentManager manager = mActivity.getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                AttendanceFragment attendanceFragment = new AttendanceFragment();
                Bundle bundle = new Bundle();
                bundle.putString("code", drivePlanList.get(i).get("TrainCode").toString());
                bundle.putString("line", drivePlanList.get(i).get("LineName").toString());
                bundle.putString("LineId", drivePlanList.get(i).get("LineId").toString());
                bundle.putString("isShowDialog", "false");
                attendanceFragment.setArguments(bundle);
                mActivity.changgeFragment("DriverNote", attendanceFragment, MainActivity.F_Attendance);
                transaction.commit();
                mActivity.setShowTag(MainActivity.F_Attendance);

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        toWork();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_work:
                String edt = tv_addcarcode.getText().toString();
                ((MainActivity) (getActivity())).changgeFragment("DriverNote", new CarCodeCrimFragment(), "CarCodeCrim");
                break;
            case R.id.tv_one_up:
                if (isClick) {
                    StringBuilder builder = new StringBuilder();
                    for (int i = 0; i < records.size(); i++) {
                        Map map = records.get(i);
                        List<Map> signPointList = openHelper.queryListMap("select * from DriveSignPoint where DriveRecordId=?", new String[]{String.valueOf(map.get("Id"))});
                        List<Map> trainformationList = openHelper.queryListMap("select * from TrainFormation where DriveRecordId=?", new String[]{String.valueOf(map.get("Id"))});
                        List<Map> noandlineList = openHelper.queryListMap("select * from DriveTrainNoAndLine where DriveRecordId=?", new String[]{String.valueOf(map.get("Id"))});
                        String object = oneupDate(records, noandlineList, signPointList, trainformationList);
                        builder.append(object + ",");
                    }
                    String arry = builder.toString();
                    String result = "";
                    if (arry.length() > 0) {
                        result = arry.substring(0, arry.length() - 1);
                    }
                    String data = "[" + result + "]";
                    KJHttp kjhttp = new KJHttp();
                    HttpParams params = new HttpParams();
                    params.put("signature", "bcad117ce31ac75fcfa347acefc8d198");
                    params.put("json", data);
                    showMes();
                    kjhttp.post(systemConfig.getHost() + "/App/DrivePlanUpload", params, new HttpCallBack() {
                        @Override
                        public void onSuccess(String t) {
                            super.onSuccess(t);
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "上传成功", Toast.LENGTH_SHORT).show();
                            try {
                                for (int i = 0; i < records.size(); i++) {
                                    Map map = records.get(i);
                                    openHelper.update("DriveRecords", new String[]{"IsUploaded"}, new Object[]{1}, new String[]{"Id"}, new String[]{String.valueOf(map.get("Id"))});
                                }
                            } catch (Exception e) {
                                Log.e("上传手账记录", "数据库上传状态更改异常");
                            }
                        }

                        @Override
                        public void onFailure(int errorNo, String strMsg) {
                            super.onFailure(errorNo, strMsg);
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "上传失败" + errorNo + ":" + strMsg, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                break;
            case R.id.tv_addcarcode:
                tiancheng_popuwindow.setVisibility(View.VISIBLE);
                getsearch(tv_addcarcode, "TrainCode");
                break;
            case R.id.tiancheng_popuwindow:
                tiancheng_popuwindow.setVisibility(View.GONE);
                break;
        }
    }

    private void getsearch(final TextView ed, final String tablename) {
        et_addcode.setText("");
        et_addcode.setFocusable(true);
        et_addcode.setFocusableInTouchMode(true);
        et_addcode.requestFocus();
        InputMethodManager inputManager =
                (InputMethodManager) et_addcode.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(et_addcode, 0);

        et_addcode.setHint("请输入车次...");
        et_addcode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(final CharSequence charSequence, int i, int i1, int i2) {
                addkey = et_addcode.getText().toString();
                if (addkey != null && !"".equals(addkey.trim())) {

                    lv_addcodes.setVisibility(View.VISIBLE);
                    listsearch = getTrainNo(addkey);
                    lv_addcodes.setAdapter(new CommonAdapter<Map>(getActivity(), listsearch, R.layout.editlist1) {

                        @Override
                        protected void convertlistener(ViewHolder holder, Map map) {}

                        @Override
                        public void convert(ViewHolder holder, Map map) {
                            if (tablename.equals("ViewPersonInfo")) {
                                holder.setText(R.id.editlist_item1, map.get("WorkNo").toString());
                                holder.setText(R.id.editlist_item2, map.get("Name").toString());
                                holder.setText(R.id.editlist_item3, map.get("Spell").toString());
                            } else if (tablename.equals("BaseStation")) {
                                holder.setText(R.id.editlist_item1, map.get("StationName").toString());
                                holder.setText(R.id.editlist_item2, map.get("Spell").toString());
                            } else {
                                holder.setText(R.id.editlist_item1, map.get("FullName").toString());
                            }
                        }
                    });

                    setListViewHeightBasedOnChildren(lv_addcodes);

                    lv_addcodes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                            tiancheng_popuwindow.setVisibility(View.GONE);
                            //((InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                            //把查到的信息添加显示出来
                            HashMap searchMap = new HashMap();
                            searchMap.put("TrainCode", listsearch.get(i).get("FullName").toString());
                            searchMap.put("DriverName", systemConfig.getUserName());
                            searchMap.put("LineId", listsearch.get(i).get("Id").toString());
                            searchMap.put("LineName", listsearch.get(i).get("FirstStation").toString() + "-" + listsearch.get(i).get("LastStation").toString());
                            drivePlanList.add(searchMap);
                            if (lv_carplan.getVisibility()==View.GONE){
                                lv_carplan.setVisibility(View.VISIBLE);
                            }
                            if (carplanAdapter == null) {
                                carplanAdapter = new CarplanAdapter(drivePlanList, getActivity());
                                lv_carplan.setAdapter(carplanAdapter);
                                setListViewHeightBasedOnChildren(lv_carplan);
                                ll_noplan.setVisibility(View.GONE);

                            } else {
                                lv_carplan.setAdapter(carplanAdapter);
                                carplanAdapter.setData(drivePlanList);
                                setListViewHeightBasedOnChildren(lv_carplan);
                                ll_noplan.setVisibility(View.GONE);
                            }
                        }
                    });

                } else {
                    lv_addcodes.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private List<Map> getTrainNo(String coads) {
        String sql = "select * from TrainNo" + " where FullName like ? ";
        String[] selectionArgs = new String[]{"%" + coads + "%"};
        List<Map> listsearch = openHelper.queryListMap(sql, selectionArgs);
        return listsearch;
    }

    private String oneupDate(List<Map> RecordsList, List<Map> DriveTrainNoAndLineList, List<Map> DriveSignPoint, List<Map> TrainFormation) {
        StringBuffer recordsList = NetUtils.getjson(RecordsList);
        StringBuffer noandline = NetUtils.getjsonlist(DriveTrainNoAndLineList);
        StringBuffer signPoint = NetUtils.getjsonlist(DriveSignPoint);
        StringBuffer trainFormation = NetUtils.getjsonlist(TrainFormation);
        String json = "{";
        json += "\"DriveRecords\":" + recordsList + ",";
        json += "\"DriveSignPoints\":" + signPoint + ",";
        json += "\"TrainFormations\":" + trainFormation + ",";
        json += "\"DriveTrainNoAndLines\":" + noandline + "}";
        json = json.replace("\\\"", "\"")
                .replace("\"null\"", "\"\"")
                .replace("\"ArriveTime\":\"\"", "\"ArriveTime\":\"1970-01-01\"")
                .replace("\"LeaveTime\":\"\"", "\"LeaveTime\":\"1970-01-01\"")
                .replace("\"AttendTime\":\"\"", "\"AttendTime\":\"1970-01-01\"")
                .replace("\"GetTrainTime\":\"\"", "\"GetTrainTime\":\"1970-01-01\"")
                .replace("\"LeaveDepotTime1\":\"\"", "\"LeaveDepotTime1\":\"1970-01-01\"")
                .replace("\"LeaveDepotTime2\":\"\"", "\"LeaveDepotTime2\":\"1970-01-01\"")
                .replace("\"ArriveDepotTime1\":\"\"", "\"ArriveDepotTime1\":\"1970-01-01\"")
                .replace("\"ArriveDepotTime2\":\"\"", "\"ArriveDepotTime2\":\"1970-01-01\"")
                .replace("\"GiveTrainTime\":\"\"", "\"GiveTrainTime\":\"1970-01-01\"")
                .replace("\"RecordEndTime\":\"\"", "\"RecordEndTime\":\"1970-01-01\"");
        try {
            json = URLEncoder.encode(json, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return json;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            records.clear();
            inintDate();
        }
    }

    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
}