package Fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import DBUtils.DBOpenHelper;
import Utils.CommonAdapter;
import Utils.CustomDialog;
import Utils.DateUtil;
import Utils.MyDatePickerDialog;
import Utils.MyTimePickerDialog;
import Utils.NetUtils;
import Utils.ViewHolder;
import bean.DrivePlan;
import config.ISystemConfig;
import config.SystemConfigFactory;
import zj.com.mc.MainActivity;
import zj.com.mc.R;

/**
 * 确认车次后填写的------>出勤信息Fragment
 */
public class AttendanceFragment extends Fragment implements View.OnClickListener {

    private TextView tv_work, tv_time_out, tv_time_this, tv_time_attendance, tv_time_getcar, tv_carcode;
    private TextView tv_departurestation, tv_arrivastation;

    private LinearLayout search_result_back, ll_time_out, ll_time_this, ll_time_attendance, ll_time_getcar;
    private MainActivity mActivity;

    //code车次信息
    private String code, line, isShowDialog;
    private EditText edt_AttendForecast;
    private EditText edt_carno;
    private TextView edt_cartype;
    private TextView edt_Driver2;
    private TextView edt_student;
    private TextView edt_drive1;
    private DBOpenHelper dbOpenHelper;
    private String sql;
    private String timeStr;
    private String table1 = "ViewPersonInfo";//需要查询的表名
    private MyTimePickerDialog myTimePickerDialog;
    private MyDatePickerDialog myDatePickerDialog;

    private RelativeLayout tiancheng_popuwindow;
    private EditText txtkey;
    private ListView listView;
    private TextView whichtv;//中转
    private String key;//弹窗内edittext中的文字
    private List<Map> listsearch;
    private String maindriverid, auxdriverid, studriverid;
    private ISystemConfig systemConfig;
    private List<Map> carInfo;
    private Map carMap;
    private String[] trainmodels = new String[]{"HXD2-", "SS4G-", "DF8B-", "HXD3C-", "HXD3D-", "DF4DK-", "HXN3B-", "DF7G-", "DF5-", "CRH5-", "CJ5-", "X-", "T-"};
    private String traintype;
    private CommonAdapter<Map> stationsAdapter;
    private String trainNoLineId;
    private String AttendTime, GetTrainTime, LeaveDepotTime1, LeaveDepotTime2;
    private int startPoint;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_attendanceinfo, null);
        sql = "select * from DrivePlan where DriverNo=? and TrainCode=?";
        inintView(view);
        inintData();
        return view;
    }

    private void inintView(View view) {
        tv_work = (TextView) view.findViewById(R.id.tv_work);
        tv_time_out = (TextView) view.findViewById(R.id.tv_time_out);
        tv_time_this = (TextView) view.findViewById(R.id.tv_time_this);
        tv_time_getcar = (TextView) view.findViewById(R.id.tv_time_getcar);
        tv_time_attendance = (TextView) view.findViewById(R.id.tv_time_attendance);
        search_result_back = (LinearLayout) view.findViewById(R.id.search_result_back);
        ll_time_out = (LinearLayout) view.findViewById(R.id.ll_time_out);
        ll_time_this = (LinearLayout) view.findViewById(R.id.ll_time_this);
        ll_time_attendance = (LinearLayout) view.findViewById(R.id.ll_time_attendance);
        ll_time_getcar = (LinearLayout) view.findViewById(R.id.ll_time_getcar);

        tv_carcode = (TextView) view.findViewById(R.id.tv_carcode);
        tv_departurestation = (TextView) view.findViewById(R.id.tv_departurestation);
        tv_arrivastation = (TextView) view.findViewById(R.id.tv_arrivastation);

        edt_cartype = (TextView) view.findViewById(R.id.edt_cartype);
        edt_carno = (EditText) view.findViewById(R.id.edt_carno);
        edt_cartype.setOnClickListener(this);

        edt_drive1 = (TextView) view.findViewById(R.id.edt_drive1);

        edt_Driver2 = (TextView) view.findViewById(R.id.edt_Driver2);
        edt_student = (TextView) view.findViewById(R.id.edt_student);
        edt_AttendForecast = (EditText) view.findViewById(R.id.edt_AttendForecast);

        tiancheng_popuwindow = (RelativeLayout) view.findViewById(R.id.tiancheng_popuwindow);
        tiancheng_popuwindow.setOnClickListener(this);
        txtkey = (EditText) view.findViewById(R.id.edit_key);
        listView = (ListView) view.findViewById(R.id.lstv_all);

    }

    private void inintData() {

        mActivity = (MainActivity) getActivity();
        systemConfig = SystemConfigFactory.getInstance(mActivity).getSystemConfig();
        code = getArguments().getString("code");
        line = getArguments().getString("line");
        trainNoLineId = getArguments().getString("LineId");
        isShowDialog = getArguments().getString("isShowDialog");

        tv_work.setOnClickListener(this);
        search_result_back.setOnClickListener(this);
        ll_time_this.setOnClickListener(this);
        ll_time_out.setOnClickListener(this);
        ll_time_attendance.setOnClickListener(this);
        ll_time_getcar.setOnClickListener(this);
        edt_Driver2.setOnClickListener(this);
        edt_student.setOnClickListener(this);
        tv_departurestation.setOnClickListener(this);
        tv_arrivastation.setOnClickListener(this);

        dbOpenHelper = DBOpenHelper.getInstance(mActivity);

        carInfo = dbOpenHelper.queryListMap(sql, new String[]{systemConfig.getUserAccount(), code});

        if (!TextUtils.isEmpty(code)) {
            tv_carcode.setText(code);
        }

        upDateUI();
    }

    private void upDateUI() {

        if (carInfo.size() <= 0) {
            edt_drive1.setText(systemConfig.getUserName());//主司机
            edt_cartype.setEnabled(true);
            return;
        }

        carMap = carInfo.get(0);
        String driverName = String.valueOf(carMap.get(DrivePlan.D_DriverName));
        String driverNo = String.valueOf(carMap.get(DrivePlan.D_DriverNo));
        String locoType = String.valueOf(carMap.get(DrivePlan.D_LocoType));
        String viceDriverName = String.valueOf(carMap.get(DrivePlan.D_ViceDriverName));
        String viceDriverNo = String.valueOf(carMap.get(DrivePlan.D_ViceDriverNo));
        String studentName = String.valueOf(carMap.get(DrivePlan.D_StudentName));
        String studentNo = String.valueOf(carMap.get(DrivePlan.D_StudentNo));

        edt_cartype.setText(locoType);//车型号
        edt_drive1.setText(driverName);//主司机
        edt_Driver2.setText(viceDriverName);//副司机
        edt_student.setText(studentName);//学员
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_work:
                String time_attendance = tv_time_attendance.getText().toString();
                String drivername = edt_drive1.getText().toString();
                if (TextUtils.isEmpty(drivername)) {
                    Toast.makeText(getActivity(), "请填写主司机姓名", Toast.LENGTH_SHORT).show();
                    break;
                }
                if (TextUtils.isEmpty(time_attendance)) {
                    Toast.makeText(getActivity(), "请确认出勤时间", Toast.LENGTH_SHORT).show();
                    break;
                }

                insertData();
                break;
            case R.id.search_result_back:
                mActivity.changgeFragment(MainActivity.F_Attendance, new DriverNote(), MainActivity.F_DriverNote);
                break;
            case R.id.ll_time_out:
                if (!isShowDialog.equals(false)) {
                    LeaveDepotTime2 = setDateFirst(tv_time_out);
                } else {
                    LeaveDepotTime2 = setDate(tv_time_out);
                }
                ll_time_this.setEnabled(false);
                break;
            case R.id.ll_time_this:
                if (!isShowDialog.equals(false)) {
                    LeaveDepotTime1 = setDateFirst(tv_time_this);
                } else {
                    LeaveDepotTime1 = setDate(tv_time_this);
                }
                ll_time_out.setEnabled(false);
                break;
            case R.id.ll_time_attendance:
                if (!isShowDialog.equals(false)) {
                    AttendTime = setDateFirst(tv_time_attendance);
                } else {
                    AttendTime = setDate(tv_time_attendance);
                }

                break;
            case R.id.ll_time_getcar:
                if (!isShowDialog.equals(false)) {
                    GetTrainTime = setDateFirst(tv_time_getcar);
                } else {
                    GetTrainTime = setDate(tv_time_getcar);
                }
                break;
            case R.id.edt_Driver2:
                tiancheng_popuwindow.setVisibility(View.VISIBLE);
                getsearch(edt_Driver2, table1);
                break;
            case R.id.edt_student:
                tiancheng_popuwindow.setVisibility(View.VISIBLE);
                getsearch(edt_student, table1);

                break;
            case R.id.tiancheng_popuwindow:
                tiancheng_popuwindow.setVisibility(View.GONE);
                search_result_back.setEnabled(true);
                break;
            case R.id.edt_cartype:
                hintDialog();
                break;
            case R.id.tv_departurestation:
                setAttendanceAnswer(tv_departurestation);
                break;
            case R.id.tv_arrivastation:
                setAttendanceAnswer(tv_arrivastation);
                break;

        }
    }

    private void hintDialog() {
        AlertDialog dialog = null;
        dialog = new AlertDialog.Builder(getActivity()).setTitle("机车型号").setItems(trainmodels, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                edt_cartype.setText(trainmodels[which]);
                dialog.dismiss();
            }
        }).create();

        dialog.show();
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        params.width = (int) (width * 0.4);
        params.height = (int) (height * 0.6);
        dialog.getWindow().setAttributes(params);
    }

    //出勤，添加或更改记录
    private void insertData() {
        String drivername = edt_drive1.getText().toString();
        String driver2name = edt_Driver2.getText().toString();
        String studentname = edt_student.getText().toString();
        traintype = edt_cartype.getText().toString();
        String carno = edt_carno.getText().toString();
        String attendForecast = edt_AttendForecast.getText().toString();
        String driver1No = "0";
        String driver2No = "0";
        String studentNo = "0";
        String personsql = "select * from PersonInfo where Name=?";

        if (!TextUtils.isEmpty(drivername)) {
            List<Map> list = dbOpenHelper.queryListMap(personsql, new String[]{drivername});
            if (list.size() > 0) {
                Map map = list.get(0);
                driver1No = String.valueOf(map.get("Id"));
            }
        }
        if (!TextUtils.isEmpty(driver2name)) {
            List<Map> list = dbOpenHelper.queryListMap(personsql, new String[]{driver2name});
            if (list.size() > 0) {
                Map map = list.get(0);
                driver2No = String.valueOf(map.get("Id"));
            }
        }
        if (!TextUtils.isEmpty(studentname)) {
            List<Map> list = dbOpenHelper.queryListMap(personsql, new String[]{studentname});
            if (list.size() > 0) {
                Map map = list.get(0);
                studentNo = String.valueOf(map.get("Id"));
            }
        }

        if (TextUtils.isEmpty(AttendTime)) {
            AttendTime = NetUtils.defultTime;
        }
        if (TextUtils.isEmpty(GetTrainTime)) {
            GetTrainTime = NetUtils.defultTime;
        }
        if (TextUtils.isEmpty(LeaveDepotTime1)) {
            LeaveDepotTime1 = NetUtils.defultTime;
        }
        if (TextUtils.isEmpty(LeaveDepotTime2)) {
            LeaveDepotTime2 = NetUtils.defultTime;
        }


        line = tv_departurestation.getText().toString() + "-" + tv_arrivastation.getText().toString();
        boolean isok = dbOpenHelper.insert("DriveRecords", new String[]{"TrainCode", "LineName", "DriverId1",
                        "ViceDriverId", "StudentDriverId", "LocomotiveType", "AttendForecast", "AttendTime", "GetTrainTime",
                        "LeaveDepotTime1", "LeaveDepotTime2", "IsDelete", "IsUploaded"},
                new Object[]{code, line, Integer.parseInt(driver1No), Integer.parseInt(driver2No), Integer.parseInt(studentNo)
                        , traintype + carno, attendForecast, AttendTime, GetTrainTime, LeaveDepotTime1, LeaveDepotTime2, "false", 0});
        if (isok) {
            String lastId = "select * from DriveRecords";
            List<Map> list = dbOpenHelper.queryListMap(lastId, new String[]{});
            String id = "";
            if (list.size() > 0) {
                id = String.valueOf(list.get(list.size() - 1).get("Id"));
            }

            FragmentManager manager = mActivity.getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            TainOperationFragment tainOperationFragment = new TainOperationFragment();
            Bundle b = new Bundle();
            b.putString("id", id);
            b.putString("line", line);
            b.putString("code", code);
            b.putInt("startPoint",startPoint);

            tainOperationFragment.setArguments(b);
            mActivity.changgeFragment(MainActivity.F_Attendance, tainOperationFragment, MainActivity.F_TainOperation);
            transaction.commit();
            mActivity.setShowTag(MainActivity.F_TainOperation);
        } else {
            Toast.makeText(mActivity, "信息保存失败...", Toast.LENGTH_SHORT).show();
        }
    }

    private String setDateFirst(final TextView tv) {
        final Calendar calender = Calendar.getInstance();
        timeStr = DateUtil.getY_M_D_H_M(calender.get(Calendar.YEAR), calender.get(Calendar.MONTH),
                calender.get(Calendar.DAY_OF_MONTH), calender.get(Calendar.HOUR_OF_DAY), calender.get(Calendar.MINUTE));
        tv.setText(timeStr);
        return timeStr;
    }

    private String setDate(final TextView tv) {
        final Calendar calender = Calendar.getInstance();
        myDatePickerDialog = new MyDatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, final int year, final int monthOfYear, final int dayOfMonth) {
                timeStr = year + "-" + monthOfYear + "-" + dayOfMonth;
                myDatePickerDialog.dismiss();
                myTimePickerDialog = new MyTimePickerDialog(mActivity, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        timeStr = DateUtil.getY_M_D_H_M(year, monthOfYear, dayOfMonth, hourOfDay, minute);
                        tv.setText(timeStr);
                        myTimePickerDialog.dismiss();
                    }
                }, calender.get(Calendar.HOUR_OF_DAY), calender.get(Calendar.MINUTE), true);

                myTimePickerDialog.show();
            }
        }, calender.get(Calendar.YEAR), calender.get(Calendar.MONTH), calender.get(Calendar.DAY_OF_MONTH));

        myDatePickerDialog.show();
        return timeStr;
    }

    private void getsearch(final TextView ed, final String tablename) {

        if (listView.getVisibility() == View.GONE) {
            listView.setVisibility(View.VISIBLE);
        }
        txtkey.setText("");
        txtkey.setFocusable(true);
        search_result_back.setEnabled(false);
        txtkey.setFocusableInTouchMode(true);
        txtkey.requestFocus();
        InputMethodManager inputManager =
                (InputMethodManager) txtkey.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(txtkey, 0);
        whichtv = ed;

        if (tablename.equals("ViewPersonInfo")) {
            txtkey.setHint("支持姓名，拼音简称搜索");
        } else if (tablename.equals("ViewTrainMoment")) {
            txtkey.setHint("输入要查询的车站名称或首字母");
        } else {
            txtkey.setHint("输入要查询的车次");
        }

        txtkey.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                key = txtkey.getText().toString();
                if (key != null && !"".equals(key.trim())) {

                    listsearch = getDBpersonname(tablename, key);
                    if (listsearch.size() > 0) {

                        listView.setAdapter(new CommonAdapter<Map>(mActivity, listsearch, R.layout.editlist1) {
                            @Override
                            protected void convertlistener(ViewHolder holder, final Map map) {
                            }

                            @Override
                            public void convert(ViewHolder holder, Map map) {
                                if (tablename.equals("ViewPersonInfo")) {
                                    holder.setText(R.id.editlist_item1, map.get("WorkNo").toString());
                                    holder.setText(R.id.editlist_item2, map.get("Name").toString());
                                    holder.setText(R.id.editlist_item3, map.get("Spell").toString());
                                } else if (tablename.equals("ViewTrainMoment")) {
                                    holder.setText(R.id.editlist_item1, map.get("StationName").toString());
                                } else {
                                    holder.setText(R.id.editlist_item1, map.get("FullName").toString());
                                }

                            }
                        });

                        setListViewHeightBasedOnChildren(listView);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                if (tablename.equals("ViewPersonInfo")) {
                                    txtkey.setText(listsearch.get(i).get("Name").toString());

                                    if (ed == edt_Driver2) {
                                        auxdriverid = listsearch.get(0).get("Id") + "";
                                    }
                                    if (ed == edt_student) {
                                        maindriverid = listsearch.get(0).get("Id") + "";
                                    }
                                }
                                ed.setText(txtkey.getText().toString());
                                tiancheng_popuwindow.setVisibility(View.GONE);
                            }
                        });
                    } else {
                        whichtv.setText(key);
                        listView.setVisibility(View.GONE);
                    }

                } else {
                    listView.setAdapter(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    //搜索view表中的数据
    private List<Map> getDBpersonname(String tablename, String coads) {

        String sql = "select * from " + tablename +
                " where WorkNo like ? or Name like ? or Spell like ? ";
        String[] selectionArgs = new String[]{"%" + coads + "%",
                "%" + coads + "%",
                "%" + coads + "%"};
        List<Map> listsearch = dbOpenHelper.queryListMap(sql, selectionArgs);
        return listsearch;
    }

    public void setAttendanceAnswer(final TextView tv) {
        String sql = "select Sort, StationName from  ViewTrainMoment where TrainNoLineId = ?";
        final List<Map> listsearch = dbOpenHelper.queryListMap(sql, new String[]{trainNoLineId});
        if (listsearch.size() != 0) {
            final CustomDialog dialog = new CustomDialog(getActivity(), R.style.mydialog);
            View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_selectstation, null);
            setDialog(dialog, contentView);

            stationsAdapter = new CommonAdapter<Map>(getActivity(), listsearch, R.layout.item_selectstation) {
                @Override
                protected void convertlistener(ViewHolder holder, Map map) {

                }

                @Override
                public void convert(ViewHolder holder, Map map) {
                    holder.setText(R.id.tv_sort, map.get("Sort").toString());
                    holder.setText(R.id.tv_afstationname, map.get("StationName").toString());
                }
            };

            ListView lv_allstations = (ListView) contentView.findViewById(R.id.lv_allstations);//分类gridview
            lv_allstations.setAdapter(stationsAdapter);
            lv_allstations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    tv.setText(listsearch.get(position).get("StationName").toString());
                    if (tv.getId() == R.id.tv_departurestation) {
                        startPoint = position ;
                    }
                    dialog.dismiss();
                }
            });

            dialog.show();
        }
    }

    private void setDialog(CustomDialog dialog, View contentView) {
        dialog.setContentView(contentView);
        dialog.setCanceledOnTouchOutside(true);
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        lp.width = (int) (width * 0.7);
        lp.height = (int) (height * 0.65);
        dialogWindow.setAttributes(lp);
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