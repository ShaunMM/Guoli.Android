package Fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import DBUtils.DBOpenHelper;
import Utils.DateUtil;
import Utils.MyDatePickerDialog;
import Utils.MyTimePickerDialog;
import zj.com.mc.AttentInfoActivity;
import zj.com.mc.R;

/**
 * 出勤信息Fragment
 */
public class DriverAttensFragment extends Fragment implements View.OnClickListener {

    private View view;
    private TextView tv_time_out, tv_time_this, tv_time_attendance, tv_time_getcar, tv_carcode, tv_carline, text_notuse;
    private LinearLayout ll_time_out, ll_time_this, ll_time_attendance, ll_time_getcar;
    private String code, line;
    private EditText edt_AttendForecast, edt_student, edt_Driver2, edt_drive1, edt_cartype;
    private DBOpenHelper dbOpenHelper;
    private String sql;
    private AttentInfoActivity mActivity;
    private String id = "1";
    private Map map;
    private String timeStr;
    private MyTimePickerDialog myTimePickerDialog;
    private MyDatePickerDialog myDatePickerDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_driverattendance, null);
        sql = "select * from ViewDriveRecord where Id=?";
        inintView(view);
        inintData();
        return view;
    }

    private void inintView(View view) {
        tv_time_out = (TextView) view.findViewById(R.id.tv_time_out);//出外段时间
        tv_time_this = (TextView) view.findViewById(R.id.tv_time_this);//出本段时间
        ll_time_this = (LinearLayout) view.findViewById(R.id.ll_time_this);
        tv_time_getcar = (TextView) view.findViewById(R.id.tv_time_getcar);//接车时间
        ll_time_getcar = (LinearLayout) view.findViewById(R.id.ll_time_getcar);
        tv_time_attendance = (TextView) view.findViewById(R.id.tv_time_attendance);//出勤时间
        ll_time_attendance = (LinearLayout) view.findViewById(R.id.ll_time_attendance);
        ll_time_out = (LinearLayout) view.findViewById(R.id.ll_time_out);//出外段时间

        tv_carcode = (TextView) view.findViewById(R.id.tv_carcode);//车次
        tv_carline = (TextView) view.findViewById(R.id.tv_carline);//线路
        edt_cartype = (EditText) view.findViewById(R.id.edt_cartype);//机车型号
        edt_drive1 = (EditText) view.findViewById(R.id.edt_drive1);//司机
        edt_Driver2 = (EditText) view.findViewById(R.id.edt_Driver2);//副司机
        edt_student = (EditText) view.findViewById(R.id.edt_student);//学员
        edt_AttendForecast = (EditText) view.findViewById(R.id.edt_AttendForecast);//出勤会
        text_notuse = (TextView) view.findViewById(R.id.text_notuse);

        ll_time_attendance.setOnClickListener(this);
        ll_time_getcar.setOnClickListener(this);
        ll_time_out.setOnClickListener(this);
        ll_time_this.setOnClickListener(this);
        //阻止软件盘弹出（用一个么用的TextView）
        text_notuse.requestFocus();
    }

    private void inintData() {
        mActivity = (AttentInfoActivity) getActivity();
        dbOpenHelper = DBOpenHelper.getInstance(mActivity);
        id = getArguments().getString("id");
        map = dbOpenHelper.queryItemMap(sql, new String[]{id});
        if (map != null) {
            String viceDriverSql = "select * from PersonInfo where Id=?";
            Map viceDriver = dbOpenHelper.queryItemMap(viceDriverSql, new String[]{String.valueOf(map.get("ViceDriverId"))});
            Map stuDriver = dbOpenHelper.queryItemMap(viceDriverSql, new String[]{String.valueOf(map.get("StudentDriverId"))});

            tv_carcode.setText(String.valueOf(map.get("FullName")));
            tv_carline.setText(String.valueOf(map.get("LineName")));
            edt_drive1.setText(String.valueOf(map.get("DriverName")));

            if (viceDriver.get("Name") == null) {
                edt_Driver2.setText("");
            } else {
                edt_Driver2.setText(String.valueOf(viceDriver.get("Name")).equals("null") ? "" : String.valueOf(viceDriver.get("Name")));
            }

            if (stuDriver.size() == 1) {
                if (stuDriver.get("Name") == null) {
                    edt_student.setText("");
                } else {
                    edt_student.setText(String.valueOf(stuDriver.get("Name")).equals("null") ? "" : String.valueOf(stuDriver.get("Name")));
                }
            } else {
                edt_student.setText("无");
            }

            edt_cartype.setText(String.valueOf(map.get("LocomotiveType")));
            edt_AttendForecast.setText(String.valueOf(map.get("AttendForecast")));

            String attenttime = String.valueOf(map.get("AttendTime"));
            String getTraintime = String.valueOf(map.get("GetTrainTime"));
            String leavetime1 = String.valueOf(map.get("LeaveDepotTime1"));
            String leavetime2 = String.valueOf(map.get("LeaveDepotTime2"));

            if (TextUtils.isEmpty(attenttime) || "null".equals(attenttime)) {
                tv_time_attendance.setText("");
            } else {
                tv_time_attendance.setText(attenttime);
            }
            if (TextUtils.isEmpty(getTraintime) || "1970-01-01".equals(getTraintime)) {
                tv_time_getcar.setText("");
            } else {
                tv_time_getcar.setText(getTraintime);
            }
            if (TextUtils.isEmpty(leavetime1) || "1970-01-01".equals(leavetime1)) {
                tv_time_this.setText("");
            } else {
                tv_time_this.setText(leavetime1);
            }
            if (TextUtils.isEmpty(leavetime2) || "1970-01-01".equals(leavetime2)) {
                tv_time_out.setText("");
            } else {
                tv_time_out.setText(leavetime2);
            }
        }

        mActivity.setOnSavebtnClickLisner(new AttentInfoActivity.onSavebtnClick() {
            @Override
            public void onSaveClick(View v) {
                insertData();
            }
        });
    }

    private void insertData() {
        String drivername = edt_drive1.getText().toString();
        String driver2name = edt_Driver2.getText().toString();
        String studentname = edt_student.getText().toString();
        String cartype = edt_cartype.getText().toString();
        String attendForecast = edt_AttendForecast.getText().toString();
        String AttendTime = tv_time_attendance.getText().toString();
        String GetTrainTime = tv_time_getcar.getText().toString();
        String LeaveDepotTime1 = tv_time_this.getText().toString();
        String LeaveDepotTime2 = tv_time_out.getText().toString();
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
        boolean isok = dbOpenHelper.update("DriveRecords", new String[]{"TrainCode", "LineName", "DriverId1",
                        "ViceDriverId", "StudentDriverId", "LocomotiveType", "AttendForecast", "AttendTime", "GetTrainTime",
                        "LeaveDepotTime1", "LeaveDepotTime2", "IsDelete", "IsUploaded"},
                new Object[]{code, line, Integer.parseInt(driver1No), Integer.parseInt(driver2No), Integer.parseInt(studentNo)
                        , cartype, attendForecast, AttendTime, GetTrainTime, LeaveDepotTime1, LeaveDepotTime2, "false", 0}, new String[]{"Id"}, new String[]{id});
        if (isok) {
            Toast.makeText(mActivity, "信息保存成功...", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mActivity, "信息保存失败...", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_time_out:
                setDate(tv_time_out);
                break;
            case R.id.ll_time_this:
                setDate(tv_time_this);
                break;
            case R.id.ll_time_attendance:
                setDate(tv_time_attendance);
                break;
            case R.id.ll_time_getcar:
                setDate(tv_time_getcar);
                break;
        }
    }

    private void setDate(final TextView tv) {
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

    }
}