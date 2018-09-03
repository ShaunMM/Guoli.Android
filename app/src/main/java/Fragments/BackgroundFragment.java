package Fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import DBUtils.DBOpenHelper;
import Utils.DateUtil;
import Utils.MyDatePickerDialog;
import Utils.MyTimePickerDialog;
import Utils.NetUtils;
import zj.com.mc.MainActivity;
import zj.com.mc.R;

/**
 * 行车记录结束----->退勤
 */
public class BackgroundFragment extends Fragment implements View.OnClickListener {

    private TextView tv_tain_back, tv_ArriveDepotTime1, tv_RecordEndTime,
            tv_GiveTrainTime, tv_ArriveDepotTime2;
    private MainActivity mActivity;
    private EditText edt_OperateConsume, edt_EndSummary, edt_RecieveEnergy, edt_LeftEnergy, edt_MultiLocoType, edt_MultiLocoSection, edt_EngineOil, edt_AirCompressorOil, edt_TurbineOil, edt_GearOil, edt_GovernorOil, edt_OtherOil, edt_Staple, edt_MultiLocoDepot, tv_StopConsume;
    private EditText edt_lenth, edt_power, edt_CarNum;
    private String lastid, line, code;
    private DBOpenHelper openHelper;
    private String grap = "select * from TrainFormation where DriveRecordId=?";
    private String timeStr;
    private MyTimePickerDialog myTimePickerDialog;
    private MyDatePickerDialog myDatePickerDialog;
    private String isShowDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_background, null);
        inintView(view);
        inintData();
        return view;
    }

    private void inintView(View view) {

        tv_tain_back = (TextView) view.findViewById(R.id.tv_tain_back);
        tv_ArriveDepotTime1 = (TextView) view.findViewById(R.id.tv_ArriveDepotTime1);
        tv_ArriveDepotTime2 = (TextView) view.findViewById(R.id.tv_ArriveDepotTime2);
        tv_GiveTrainTime = (TextView) view.findViewById(R.id.tv_GiveTrainTime);
        tv_RecordEndTime = (TextView) view.findViewById(R.id.tv_RecordEndTime);
        tv_StopConsume = (EditText) view.findViewById(R.id.tv_StopConsume);

        edt_CarNum = (EditText) view.findViewById(R.id.edt_CarNum);
        edt_power = (EditText) view.findViewById(R.id.edt_power);
        edt_lenth = (EditText) view.findViewById(R.id.edt_lenth);

        edt_OperateConsume = (EditText) view.findViewById(R.id.edt_OperateConsume);
        edt_RecieveEnergy = (EditText) view.findViewById(R.id.edt_RecieveEnergy);
        edt_LeftEnergy = (EditText) view.findViewById(R.id.edt_LeftEnergy);
        edt_MultiLocoType = (EditText) view.findViewById(R.id.edt_MultiLocoType);
        edt_MultiLocoSection = (EditText) view.findViewById(R.id.edt_MultiLocoSection);
        edt_EngineOil = (EditText) view.findViewById(R.id.edt_EngineOil);
        edt_AirCompressorOil = (EditText) view.findViewById(R.id.edt_AirCompressorOil);
        edt_TurbineOil = (EditText) view.findViewById(R.id.edt_TurbineOil);
        edt_GearOil = (EditText) view.findViewById(R.id.edt_GearOil);
        edt_GovernorOil = (EditText) view.findViewById(R.id.edt_GovernorOil);
        edt_OtherOil = (EditText) view.findViewById(R.id.edt_OtherOil);
        edt_Staple = (EditText) view.findViewById(R.id.edt_Staple);
        edt_EndSummary = (EditText) view.findViewById(R.id.edt_EndSummary);
        edt_MultiLocoDepot = (EditText) view.findViewById(R.id.edt_MultiLocoDepot);
    }

    private void inintData() {
        mActivity = (MainActivity) getActivity();
        lastid = getArguments().getString("id");
        line = getArguments().getString("line");
        code = getArguments().getString("code");
        isShowDialog = getArguments().getString("isShowDialog");
        openHelper = DBOpenHelper.getInstance(mActivity);

        tv_tain_back.setOnClickListener(this);
        tv_ArriveDepotTime1.setOnClickListener(this);
        tv_ArriveDepotTime2.setOnClickListener(this);
        tv_GiveTrainTime.setOnClickListener(this);
        tv_RecordEndTime.setOnClickListener(this);
        //tv_StopConsume.setOnClickListener(this);

        List<Map> list = openHelper.queryListMap(grap, new String[]{lastid});
        if (list != null) {
            if (list.size() > 0) {
                Map map = list.get(list.size() - 1);
                edt_CarNum.setText(String.valueOf(map.get("CarriageCount")));
                edt_power.setText(String.valueOf(map.get("CarryingCapacity")));
                edt_lenth.setText(String.valueOf(map.get("Length")));
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_tain_back:
                String ArriveDepotTime1 = tv_ArriveDepotTime1.getText().toString();
                String ArriveDepotTime2 = tv_ArriveDepotTime2.getText().toString();
                String GiveTrainTime = tv_GiveTrainTime.getText().toString();
                String RecordEndTime = tv_RecordEndTime.getText().toString();
                String StopConsume = tv_StopConsume.getText().toString();
                //列车编组
                String CarriageCount = edt_CarNum.getText().toString();
                String CarryingCapacity = edt_power.getText().toString();
                String Length = edt_lenth.getText().toString();

                String OperateConsume = edt_OperateConsume.getText().toString();
                String RecieveEnergy = edt_RecieveEnergy.getText().toString();
                String LeftEnergy = edt_LeftEnergy.getText().toString();
                String MultiLocoDepot = edt_MultiLocoDepot.getText().toString();
                String MultiLocoType = edt_MultiLocoType.getText().toString();
                String MultiLocoSection = edt_MultiLocoSection.getText().toString();
                String EngineOil = edt_EngineOil.getText().toString();
                String AirCompressorOil = edt_AirCompressorOil.getText().toString();
                String TurbineOil = edt_TurbineOil.getText().toString();
                String GearOil = edt_GearOil.getText().toString();
                String GovernorOil = edt_GovernorOil.getText().toString();
                String OtherOil = edt_OtherOil.getText().toString();
                String Staple = edt_Staple.getText().toString();
                String EndSummary = edt_EndSummary.getText().toString();
                boolean isok = upDate(openHelper, lastid,
                        ArriveDepotTime1,
                        ArriveDepotTime2,
                        GiveTrainTime,
                        RecordEndTime,
                        OperateConsume,
                        StopConsume,
                        CarriageCount,
                        CarryingCapacity,
                        Length,
                        RecieveEnergy,
                        LeftEnergy,
                        EngineOil,
                        AirCompressorOil,
                        TurbineOil,
                        GearOil,
                        GovernorOil,
                        OtherOil,
                        Staple,
                        MultiLocoDepot,
                        MultiLocoType,
                        MultiLocoSection,
                        EndSummary);
                if (isok) {
                    Toast.makeText(mActivity, "退勤成功", Toast.LENGTH_SHORT).show();
                    mActivity.changgeFragment(MainActivity.F_Background, new DriverNote(), MainActivity.F_DriverNote);
                    mActivity.ableRadioGroup();

                } else {
                    Toast.makeText(mActivity, "退勤失败", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.tv_ArriveDepotTime1:
                if (!isShowDialog.equals(false)) {
                    setDateFirst(tv_ArriveDepotTime1);
                } else {
                    setDate(tv_ArriveDepotTime1);
                }
                tv_ArriveDepotTime2.setEnabled(false);
                break;
            case R.id.tv_ArriveDepotTime2:
                if (!isShowDialog.equals(false)) {
                    setDateFirst(tv_ArriveDepotTime2);
                } else {
                    setDate(tv_ArriveDepotTime2);
                }
                tv_ArriveDepotTime1.setEnabled(false);
                break;
            case R.id.tv_GiveTrainTime:
                if (!isShowDialog.equals(false)) {
                    setDateFirst(tv_GiveTrainTime);
                } else {
                    setDate(tv_GiveTrainTime);
                }
                break;
            case R.id.tv_RecordEndTime:
                if (!isShowDialog.equals(false)) {
                    setDateFirst(tv_RecordEndTime);
                } else {
                    setDate(tv_RecordEndTime);
                }
                break;
        }
    }

    public boolean upDate(DBOpenHelper openHelper, String lastid,
                          String ArriveDepotTime1,
                          String ArriveDepotTime2,
                          String GiveTrainTime,
                          String RecordEndTime,
                          String OperateConsume,
                          String StopConsume,
                          String CarriageCount,
                          String CarryingCapacity,
                          String Length,
                          String RecieveEnergy,
                          String LeftEnergy,
                          String EngineOil,
                          String AirCompressorOil,
                          String TurbineOil,
                          String GearOil,
                          String GovernorOil,
                          String OtherOil,
                          String Staple,
                          String MultiLocoDepot,
                          String MultiLocoType,
                          String MultiLocoSection,
                          String EndSummary) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String time = format.format(new Date());
        if (TextUtils.isEmpty(ArriveDepotTime1)) {
            ArriveDepotTime1 = NetUtils.defultTime;
        }
        if (TextUtils.isEmpty(ArriveDepotTime2)) {
            ArriveDepotTime2 = NetUtils.defultTime;
        }
        if (TextUtils.isEmpty(GiveTrainTime)) {
            GiveTrainTime = NetUtils.defultTime;
        }
        if (TextUtils.isEmpty(RecordEndTime)) {
            RecordEndTime = NetUtils.defultTime;
        }


        boolean isok = openHelper.update("DriveRecords", new String[]{"ArriveDepotTime1",
                        "ArriveDepotTime2", "GiveTrainTime", "RecordEndTime", "OperateConsume",
                        "StopConsume", "CarriageCount", "CarryingCapacity", "Length", "RecieveEnergy", "LeftEnergy", "EngineOil", "AirCompressorOil",
                        "TurbineOil", "GearOil", "GovernorOil", "OtherOil", "Staple",
                        "MultiLocoDepot", "MultiLocoType", "MultiLocoSection", "EndSummary", "AddTime", "IsDelete"},
                new Object[]{ArriveDepotTime1,
                        ArriveDepotTime2,
                        GiveTrainTime, RecordEndTime, OperateConsume, StopConsume, CarriageCount, CarryingCapacity, Length,
                        RecieveEnergy, LeftEnergy, EngineOil, AirCompressorOil, TurbineOil,
                        GearOil, GovernorOil, OtherOil, Staple, MultiLocoDepot, MultiLocoType,
                        MultiLocoSection, EndSummary, time, "false"
                }, new String[]{"Id"}, new String[]{lastid});
        if (isok) {
            return true;
        }
        return false;
    }

    private void setDateFirst(final TextView tv) {
        final Calendar calender = Calendar.getInstance();
        timeStr = DateUtil.getY_M_D_H_M(calender.get(Calendar.YEAR), calender.get(Calendar.MONTH), calender.get(Calendar.DAY_OF_MONTH), calender.get(Calendar.HOUR_OF_DAY), calender.get(Calendar.MINUTE));
        tv.setText(timeStr);

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
