package Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Adapter.CarCodeNewAdapter;
import Adapter.CarLineNewAdapter;
import DBUtils.DBOpenHelper;
import Utils.CommonAdapter;
import Utils.ViewHolder;
import config.ISystemConfig;
import config.SystemConfigFactory;
import zj.com.mc.MainActivity;
import zj.com.mc.R;

/**
 * 行车线路确认
 */
public class CarCodeCrimFragment extends Fragment implements View.OnClickListener {

    private String sql;
    private LinearLayout search_result_back;
    private TextView tv_work;
    private MainActivity mActivity;
    private GridView gv_carcode, gv_carline;
    private DBOpenHelper dbOpenHelper;
    private RelativeLayout tiancheng_popuwindow;
    private EditText txtkey;
    private List<Map> listsearch;
    private List<Map> drivePlanList;
    private String key;//弹窗内edittext中的文字
    private ListView listView;

    private TextView edt_carcode;//手动输入车次

    private String code = "", line = "";
    private CarCodeNewAdapter codenewAdapter;
    private CarLineNewAdapter lineAdapter;
    private ISystemConfig systemConfig;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_carcode, null);
        inintView(view);
        inintData();
        inintListner();
        return view;
    }

    private void inintView(View view) {
        search_result_back = (LinearLayout) view.findViewById(R.id.search_result_back);
        tv_work = (TextView) view.findViewById(R.id.tv_work);
        gv_carcode = (GridView) view.findViewById(R.id.gv_carcode);
        gv_carline = (GridView) view.findViewById(R.id.gv_carline);
        edt_carcode = (TextView) view.findViewById(R.id.edt_carcode);

        tiancheng_popuwindow = (RelativeLayout) view.findViewById(R.id.tiancheng_popuwindow);
        tiancheng_popuwindow.setOnClickListener(this);
        tiancheng_popuwindow.setVisibility(View.GONE);
        txtkey = (EditText) view.findViewById(R.id.edit_key);
        listView = (ListView) view.findViewById(R.id.lstv_all);

    }

    private void inintData() {
        mActivity = (MainActivity) getActivity();
        systemConfig = SystemConfigFactory.getInstance(mActivity).getSystemConfig();
        search_result_back.setOnClickListener(this);
        tv_work.setOnClickListener(this);
        edt_carcode.setOnClickListener(this);
        sql = "select * from DrivePlan where DriverNo=?";
        dbOpenHelper = DBOpenHelper.getInstance(mActivity);
        drivePlanList = dbOpenHelper.queryListMap(sql, new String[]{systemConfig.getUserAccount()});

        codenewAdapter = new CarCodeNewAdapter(mActivity, drivePlanList);
        lineAdapter = new CarLineNewAdapter(mActivity, drivePlanList);

        gv_carcode.setAdapter(codenewAdapter);
        gv_carline.setAdapter(lineAdapter);
    }

    int i = 0;

    private void inintListner() {
        gv_carcode.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                codenewAdapter.setSelectIndex(position);
                code = codenewAdapter.getItem(position);
                edt_carcode.setText("");
                edt_carcode.clearFocus();
            }
        });

        gv_carline.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                lineAdapter.setSelectIndex(position);
                line = lineAdapter.getItem(position);
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_result_back:
                mActivity.changgeFragment("CarCodeCrim", new DriverNote(), "DriverNote");
                break;
            case R.id.tv_work:
                String edt = edt_carcode.getText().toString();
                if (!TextUtils.isEmpty(edt)) {
                    code = edt;
                } else {
                    code = codenewAdapter.getCode();
                }
                if (TextUtils.isEmpty(line) || TextUtils.isEmpty(code)) {
                    Toast.makeText(mActivity, "请选择车次和线路", Toast.LENGTH_SHORT).show();
                    break;
                }
                FragmentManager manager = mActivity.getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.hide(manager.findFragmentByTag("CarCodeCrim"));
                AttendanceFragment attendance = new AttendanceFragment();
                Bundle b = new Bundle();
                b.putString("code", code);
                b.putString("line", line);
                b.putString("isShowDialog", "false");
                attendance.setArguments(b);
                transaction.add(R.id.frg_conn, attendance, MainActivity.F_Attendance);
                transaction.commit();
                mActivity.setShowTag(MainActivity.F_Attendance);
                break;
            case R.id.edt_carcode:
                tiancheng_popuwindow.setVisibility(View.VISIBLE);
                getsearch(edt_carcode, "TrainCode");
                break;
            case R.id.tiancheng_popuwindow:
                tiancheng_popuwindow.setVisibility(View.GONE);
                search_result_back.setEnabled(true);
                break;
        }
    }

    private void getsearch(final TextView ed, final String tablename) {
        txtkey.setText("");
        txtkey.setFocusable(true);
        txtkey.setFocusableInTouchMode(true);
        txtkey.requestFocus();
        search_result_back.setEnabled(false);
        InputMethodManager inputManager =
                (InputMethodManager) txtkey.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(txtkey, 0);

        txtkey.setHint("请输入车次...");

        txtkey.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                key = txtkey.getText().toString();
                if (key != null && !"".equals(key.trim())) {

                    listsearch = getTrainNo(key);
                    listView.setAdapter(new CommonAdapter<Map>(mActivity, listsearch, R.layout.editlist1) {

                        @Override
                        protected void convertlistener(ViewHolder holder, Map map) {
                        }

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

                    setListViewHeightBasedOnChildren(listView);

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                            tiancheng_popuwindow.setVisibility(View.GONE);
                            search_result_back.setEnabled(true);
                            //((InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                            String str = listsearch.get(i).toString();
                            String str1 = str;
                            //把查到的信息添加显示出来
                            HashMap searchMap = new HashMap();
                            searchMap.put("TrainCode", listsearch.get(i).get("FullName").toString());
                            searchMap.put("DriverName", systemConfig.getUserName());
                            searchMap.put("LineName", listsearch.get(i).get("FirstStation").toString() + "-" + listsearch.get(i).get("LastStation").toString());
                            drivePlanList.add(searchMap);

                            gv_carcode.setAdapter(codenewAdapter);
                            gv_carline.setAdapter(lineAdapter);
                            codenewAdapter.notifyDataSetChanged();
                            lineAdapter.notifyDataSetChanged();
                        }
                    });

                } else {
                    listView.setAdapter(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    //车次查询
    private List<Map> getTrainNo(String coads) {

        String sql = "select * from TrainNo" + " where FullName like ? ";
        String[] selectionArgs = new String[]{"%" + coads + "%"};
        List<Map> listsearch = dbOpenHelper.queryListMap(sql, selectionArgs);
        return listsearch;
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



