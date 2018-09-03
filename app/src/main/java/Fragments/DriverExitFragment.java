package Fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import zj.com.mc.AttentInfoActivity;
import zj.com.mc.MainActivity;
import zj.com.mc.R;

/**
 * 近期手账记录 -- 退勤信息Fragment
 */
public class DriverExitFragment extends Fragment implements View.OnClickListener {

    private TextView tv_ArriveDepotTime1, tv_RecordEndTime, tv_GiveTrainTime, tv_ArriveDepotTime2;
    private EditText edt_OperateConsume, edt_EndSummary, edt_RecieveEnergy, edt_LeftEnergy, edt_MultiLocoType, edt_MultiLocoSection, edt_EngineOil, edt_AirCompressorOil, edt_TurbineOil, edt_GearOil, edt_GovernorOil, edt_OtherOil, edt_Staple, edt_MultiLocoDepot, tv_StopConsume;
    private View view;
    private DBOpenHelper openHelper;
    private AttentInfoActivity mActivity;
    private String id;
    private String selectSql = "select * from ViewDriveRecord where Id=?";
    private String timeStr;
    private MyTimePickerDialog myTimePickerDialog;
    private MyDatePickerDialog myDatePickerDialog;
    private List<Map> resultList;
    private Map resultMap;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_driverexit, null);
        inintView(view);
        inintData();
        return view;
    }

    private void inintView(View view) {
        tv_ArriveDepotTime1 = (TextView) view.findViewById(R.id.tv_ArriveDepotTime1);
        tv_ArriveDepotTime2 = (TextView) view.findViewById(R.id.tv_ArriveDepotTime2);
        tv_GiveTrainTime = (TextView) view.findViewById(R.id.tv_GiveTrainTime);
        tv_RecordEndTime = (TextView) view.findViewById(R.id.tv_RecordEndTime);

        tv_StopConsume = (EditText) view.findViewById(R.id.tv_StopConsume);
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
        mActivity = (AttentInfoActivity) getActivity();
        id = getArguments().getString("id");
        openHelper = DBOpenHelper.getInstance(mActivity);

        resultMap = openHelper.queryItemMap(selectSql, new String[]{id});
        if (resultMap != null) {
            upDateUI(resultMap);
        }

        mActivity.setOnSavebtnClickLisner(new AttentInfoActivity.onSavebtnClick() {
            @Override
            public void onSaveClick(View v) {
                //更新数据
                String ArriveDepotTime1 = tv_ArriveDepotTime1.getText().toString();
                String ArriveDepotTime2 = tv_ArriveDepotTime2.getText().toString();
                String GiveTrainTime = tv_GiveTrainTime.getText().toString();
                String RecordEndTime = tv_RecordEndTime.getText().toString();
                String StopConsume = tv_StopConsume.getText().toString();
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
                boolean isok = upDate(openHelper, id,
                        ArriveDepotTime1,
                        ArriveDepotTime2,
                        GiveTrainTime,
                        RecordEndTime,
                        OperateConsume,
                        StopConsume,
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
                    Toast.makeText(mActivity, "保存成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mActivity, "保存失败", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void upDateUI(Map resultMap) {
        String a1 = String.valueOf(resultMap.get("ArriveDepotTime1"));
        String a2 = String.valueOf(resultMap.get("ArriveDepotTime2"));
        String g = String.valueOf(resultMap.get("GiveTrainTime"));
        String r = String.valueOf(resultMap.get("RecordEndTime"));
        tv_ArriveDepotTime1.setText((a1.equals("null") || a1.equals("1970-01-01")) ? "" : a1);
        tv_ArriveDepotTime2.setText((a2.equals("null") || a2.equals("1970-01-01")) ? "" : a2);
        tv_GiveTrainTime.setText((g.equals("null") || g.equals("1970-01-01")) ? "" : g);
        tv_RecordEndTime.setText((r.equals("null") || r.equals("1970-01-01")) ? "" : r);

        tv_ArriveDepotTime1.setOnClickListener(this);
        tv_ArriveDepotTime2.setOnClickListener(this);
        tv_GiveTrainTime.setOnClickListener(this);
        tv_RecordEndTime.setOnClickListener(this);
        //tv_StopConsume.setOnClickListener(this);

        tv_StopConsume.setText(String.valueOf(resultMap.get("StopConsume")).equals("") || String.valueOf(resultMap.get("StopConsume")).equals("null") ? "" : String.valueOf(resultMap.get("StopConsume")));
        edt_OperateConsume.setText(String.valueOf(resultMap.get("OperateConsume")).equals("") || String.valueOf(resultMap.get("OperateConsume")).equals("null") ? "" : String.valueOf(resultMap.get("OperateConsume")));
        edt_RecieveEnergy.setText(String.valueOf(resultMap.get("RecieveEnergy")).equals("") || String.valueOf(resultMap.get("RecieveEnergy")).equals("null") ? "" : String.valueOf(resultMap.get("RecieveEnergy")));
        edt_LeftEnergy.setText(String.valueOf(resultMap.get("LeftEnergy")).equals("") || String.valueOf(resultMap.get("LeftEnergy")).equals("null") ? "" : String.valueOf(resultMap.get("LeftEnergy")));
        edt_MultiLocoDepot.setText(String.valueOf(resultMap.get("MultiLocoDepot")).equals("") || String.valueOf(resultMap.get("MultiLocoDepot")).equals("null") ? "" : String.valueOf(resultMap.get("MultiLocoDepot")));
        edt_MultiLocoType.setText(String.valueOf(resultMap.get("MultiLocoType")).equals("") || String.valueOf(resultMap.get("MultiLocoType")).equals("null") ? "" : String.valueOf(resultMap.get("MultiLocoType")));
        edt_MultiLocoSection.setText(String.valueOf(resultMap.get("MultiLocoSection")).equals("") || String.valueOf(resultMap.get("MultiLocoSection")).equals("null") ? "" : String.valueOf(resultMap.get("MultiLocoSection")));
        edt_EndSummary.setText(String.valueOf(resultMap.get("EndSummary")).equals("") || String.valueOf(resultMap.get("EndSummary")).equals("null") ? "" : String.valueOf(resultMap.get("EndSummary")));
        edt_EngineOil.setText(String.valueOf(resultMap.get("EngineOil")).equals("") || String.valueOf(resultMap.get("EngineOil")).equals("null") ? "" : String.valueOf(resultMap.get("EngineOil")));
        edt_AirCompressorOil.setText(String.valueOf(resultMap.get("AirCompressorOil")).equals("") || String.valueOf(resultMap.get("AirCompressorOil")).equals("null") ? "" : String.valueOf(resultMap.get("AirCompressorOil")));
        edt_TurbineOil.setText(String.valueOf(resultMap.get("TurbineOil")).equals("") || String.valueOf(resultMap.get("TurbineOil")).equals("null") ? "" : String.valueOf(resultMap.get("TurbineOil")));
        edt_GearOil.setText(String.valueOf(resultMap.get("GearOil")).equals("") || String.valueOf(resultMap.get("GearOil")).equals("null") ? "" : String.valueOf(resultMap.get("GearOil")));
        edt_GovernorOil.setText(String.valueOf(resultMap.get("GovernorOil")).equals("") || String.valueOf(resultMap.get("GovernorOil")).equals("null") ? "" : String.valueOf(resultMap.get("GovernorOil")));
        edt_OtherOil.setText(String.valueOf(resultMap.get("OtherOil")).equals("") || String.valueOf(resultMap.get("OtherOil")).equals("null") ? "" : String.valueOf(resultMap.get("OtherOil")));
        edt_Staple.setText(String.valueOf(resultMap.get("Staple")).equals("") || String.valueOf(resultMap.get("Staple")).equals("null") ? "" : String.valueOf(resultMap.get("Staple")));

    }

    public boolean upDate(DBOpenHelper openHelper, String lastid,
                          String ArriveDepotTime1,
                          String ArriveDepotTime2,
                          String GiveTrainTime,
                          String RecordEndTime,
                          String OperateConsume,
                          String StopConsume,
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
        boolean isok = openHelper.update("DriveRecords", new String[]{"ArriveDepotTime1",
                        "ArriveDepotTime2", "GiveTrainTime", "RecordEndTime", "OperateConsume",
                        "StopConsume", "RecieveEnergy", "LeftEnergy", "EngineOil", "AirCompressorOil",
                        "TurbineOil", "GearOil", "GovernorOil", "OtherOil", "Staple",
                        "MultiLocoDepot", "MultiLocoType", "MultiLocoSection", "EndSummary", "AddTime", "IsDelete"},
                new Object[]{ArriveDepotTime1,
                        ArriveDepotTime2,
                        GiveTrainTime, RecordEndTime, OperateConsume, StopConsume,
                        RecieveEnergy, LeftEnergy, EngineOil, AirCompressorOil, TurbineOil,
                        GearOil, GovernorOil, OtherOil, Staple, MultiLocoDepot, MultiLocoType,
                        MultiLocoSection, EndSummary, time, "false"
                }, new String[]{"Id"}, new String[]{lastid});
        if (isok) {
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_ArriveDepotTime1:
                setDate(tv_ArriveDepotTime1);
                break;
            case R.id.tv_ArriveDepotTime2:
                setDate(tv_ArriveDepotTime2);
                break;
            case R.id.tv_GiveTrainTime:
                setDate(tv_GiveTrainTime);
                break;
            case R.id.tv_RecordEndTime:
                setDate(tv_RecordEndTime);
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