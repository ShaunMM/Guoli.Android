package Adapter;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import DBUtils.DBOpenHelper;
import Utils.DateUtil;
import Utils.MyDatePickerDialog;
import Utils.MyTimePickerDialog;
import zj.com.mc.R;

/**
 * 签点信息Adapter
 */
public class TainGrapAdapter extends BaseAdapter {
    private Context context;
    private List<Map> list, trainFormationList, trainCodeList;
    private ViewHoder viewHoder;
    private String DriveRecordId;
    private String str_arriveTime, str_leaveTime, str_lateinfo, str_cars, str_carpower, str_carleanth;
    private String str_educe, str_expel, str_rush, str_eave;
    private MyTimePickerDialog myTimePickerDialog;
    private MyDatePickerDialog myDatePickerDialog;
    private String timeStr;
    private String StationId;
    private String type;


    public TainGrapAdapter(Context context, String type, String DriveRecordId, List<Map> list, List<Map> trainFormationList,
                           List<Map> trainCodeList) {
        this.context = context;
        this.type = type;
        this.list = list;
        this.trainFormationList = trainFormationList;
        this.DriveRecordId = DriveRecordId;
        this.trainCodeList = trainCodeList;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private List<Map<String, Object>> DriveSignPointList;
    private List<Map<String, Object>> TrainFormationList;

    public List<Map<String, Object>> getTrainFormationList() {
        return TrainFormationList;
    }

    public List<Map<String, Object>> getDriveSignPointList() {
        return DriveSignPointList;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Map map = list.get(position);
        convertView = LayoutInflater.from(context).inflate(R.layout.item_taingrap, null);
        viewHoder = new ViewHoder();
        viewHoder.tv_stationname = (TextView) convertView.findViewById(R.id.tv_stationname);
        viewHoder.tv_uptime = (TextView) convertView.findViewById(R.id.edt_uptime);
        viewHoder.tv_latetime = (TextView) convertView.findViewById(R.id.edt_latetime);
        viewHoder.edt_lateinfo = (EditText) convertView.findViewById(R.id.edt_lateinfo);
        viewHoder.edt_cars = (EditText) convertView.findViewById(R.id.edt_cars);
        viewHoder.edt_carpower = (EditText) convertView.findViewById(R.id.edt_carpower);
        viewHoder.edt_carleanth = (EditText) convertView.findViewById(R.id.edt_carleanth);
        viewHoder.edt_educe = (EditText) convertView.findViewById(R.id.edt_educe);
        viewHoder.edt_expel = (EditText) convertView.findViewById(R.id.edt_expel);
        viewHoder.edt_rush = (EditText) convertView.findViewById(R.id.edt_rush);
        viewHoder.edt_eave = (EditText) convertView.findViewById(R.id.edt_eave);
        convertView.setTag(viewHoder);

        StationId = String.valueOf(map.get("StationId"));
        for (int i = 0; i < trainCodeList.size(); i++) {
            if (StationId.equals(String.valueOf(trainCodeList.get(i).get("TrainStationId")))) {
                viewHoder.tv_stationname.setText(String.valueOf(trainCodeList.get(i).get("StationName")));
                break;
            }
        }

        String arrive = String.valueOf(map.get("ArriveTime"));
        String leave = String.valueOf(map.get("LeaveTime"));
        String reason = String.valueOf(map.get("EarlyOrLateReason"));

        if (TextUtils.isEmpty(arrive) || "null".equals(arrive)) {
            viewHoder.tv_uptime.setText("");
        } else {
            viewHoder.tv_uptime.setText(arrive.substring(arrive.length() - 5, arrive.length()));
        }
        if (TextUtils.isEmpty(leave) || "null".equals(leave)) {
            viewHoder.tv_latetime.setText("");
        } else {
            viewHoder.tv_latetime.setText(leave.substring(leave.length() - 5, leave.length()));
        }
        if (TextUtils.isEmpty(reason) || "null".equals(reason)) {
            viewHoder.edt_lateinfo.setText("");
        } else {
            viewHoder.edt_lateinfo.setText(String.valueOf(map.get("EarlyOrLateReason")));
        }

        for (int i = 0; i < trainFormationList.size(); i++) {
            if (StationId.equals(String.valueOf(trainFormationList.get(i).get("StationId")))) {
                if (String.valueOf(trainFormationList.get(i).get("CarriageCount")).equals("0")) {
                    viewHoder.edt_cars.setText("");
                } else {
                    viewHoder.edt_cars.setText(String.valueOf(trainFormationList.get(i).get("CarriageCount")));
                }
                viewHoder.edt_carpower.setText(String.valueOf(trainFormationList.get(i).get("CarryingCapacity")));
                viewHoder.edt_carleanth.setText(String.valueOf(trainFormationList.get(i).get("Length")));
                viewHoder.edt_educe.setText(String.valueOf(trainFormationList.get(i).get("Decompress")));
                viewHoder.edt_expel.setText(String.valueOf(trainFormationList.get(i).get("RowTime")));
                viewHoder.edt_rush.setText(String.valueOf(trainFormationList.get(i).get("FillingTime")));
                viewHoder.edt_eave.setText(String.valueOf(trainFormationList.get(i).get("Missing")));
                break;
            }
        }

        if (type.equals("TainOperationFragment")) {
            viewHoder.tv_uptime.setEnabled(false);
            viewHoder.tv_latetime.setEnabled(false);
            viewHoder.edt_lateinfo.setEnabled(false);
            viewHoder.edt_cars.setEnabled(false);
            viewHoder.edt_carpower.setEnabled(false);
            viewHoder.edt_carleanth.setEnabled(false);
            viewHoder.edt_educe.setEnabled(false);
            viewHoder.edt_expel.setEnabled(false);
            viewHoder.edt_rush.setEnabled(false);
            viewHoder.edt_eave.setEnabled(false);

        } else {
            viewHoder.tv_uptime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setDate(viewHoder.tv_uptime);
                }
            });

            viewHoder.tv_latetime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setDate(viewHoder.tv_latetime);
                }
            });

            viewHoder.edt_lateinfo.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    setString(position);
                    str_lateinfo = charSequence.toString();
                }

                @Override
                public void afterTextChanged(Editable editable) {}
            });

            viewHoder.edt_cars.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    setString(position);
                    str_cars = charSequence.toString();
                }

                @Override
                public void afterTextChanged(Editable editable) {}
            });

            viewHoder.edt_carpower.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    setString(position);
                    str_carpower = charSequence.toString();
                }

                @Override
                public void afterTextChanged(Editable editable) {}
            });

            viewHoder.edt_carleanth.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    setString(position);
                    str_carleanth = charSequence.toString();
                }

                @Override
                public void afterTextChanged(Editable editable) {}
            });

            viewHoder.edt_educe.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    setString(position);
                    str_educe = charSequence.toString();
                }

                @Override
                public void afterTextChanged(Editable editable) {}
            });

            viewHoder.edt_expel.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    setString(position);
                    str_expel = charSequence.toString();
                }

                @Override
                public void afterTextChanged(Editable editable) {}
            });

            viewHoder.edt_rush.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    setString(position);
                    str_rush = charSequence.toString();}

                @Override
                public void afterTextChanged(Editable editable) {}
            });

            viewHoder.edt_eave.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    setString(position);
                    str_eave = charSequence.toString();
                }

                @Override
                public void afterTextChanged(Editable editable) {}
            });
        }
        return convertView;
    }

    public void setString(int position) {
        if (list.size() > position) {
            StationId = String.valueOf(list.get(position).get("StationId"));
            str_arriveTime = String.valueOf(list.get(position).get("ArriveTime"));
            str_leaveTime = String.valueOf(list.get(position).get("LeaveTime"));
            str_lateinfo = String.valueOf(list.get(position).get("EarlyOrLateReason"));
        }

        if (trainFormationList.size() > position) {
            str_cars = String.valueOf(trainFormationList.get(position).get("CarriageCount"));
            str_carpower = String.valueOf(trainFormationList.get(position).get("CarryingCapacity"));
            str_carleanth = String.valueOf(trainFormationList.get(position).get("Length"));

            str_educe = String.valueOf(trainFormationList.get(position).get("Decompress"));
            str_expel = String.valueOf(trainFormationList.get(position).get("RowTime"));
            str_rush = String.valueOf(trainFormationList.get(position).get("FillingTime"));
            str_eave = String.valueOf(trainFormationList.get(position).get("Missing"));
        }
    }

    private void setDate(final TextView tv) {
        final Calendar calender = Calendar.getInstance();
        myDatePickerDialog = new MyDatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, final int year, final int monthOfYear, final int dayOfMonth) {
                timeStr = year + "-" + monthOfYear + "-" + dayOfMonth;
                myDatePickerDialog.dismiss();
                myTimePickerDialog = new MyTimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        timeStr = DateUtil.getY_M_D_H_M(year, monthOfYear, dayOfMonth, hourOfDay, minute);
                        tv.setText(timeStr.substring(timeStr.length() - 5, timeStr.length()));
                        myTimePickerDialog.dismiss();
                    }
                }, calender.get(Calendar.HOUR_OF_DAY), calender.get(Calendar.MINUTE), true);

                myTimePickerDialog.show();
            }
        }, calender.get(Calendar.YEAR), calender.get(Calendar.MONTH), calender.get(Calendar.DAY_OF_MONTH));
        myDatePickerDialog.show();

    }

    static class ViewHoder {
        TextView tv_stationname, tv_uptime, tv_latetime;
        EditText edt_lateinfo, edt_cars, edt_carpower, edt_carleanth;
        EditText edt_educe, edt_expel, edt_rush, edt_eave;//试风记录
    }

    public boolean upRign(DBOpenHelper openHelper) {
        boolean isok = false;
        isok = openHelper.update("DriveSignPoint", new String[]{"ArriveTime", "LeaveTime", "EarlyOrLateReason"},
                new Object[]{str_arriveTime, str_leaveTime, str_lateinfo}, new String[]{"DriveRecordId", "StationId"}, new String[]{DriveRecordId, StationId});
        return isok;
    }


    public boolean uporInsetGrap(DBOpenHelper openHelper) {
        boolean isok = false;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String time = format.format(new Date());
        List<Map> list = openHelper.queryListMap("select * from TrainFormation where DriveRecordId=? and StationId=?", new String[]{DriveRecordId, StationId});
        if (list != null) {
            if (list.size() > 0) {
                isok = openHelper.update("TrainFormation", new String[]{"CarriageCount", "CarryingCapacity", "Length", "Decompress", "RowTime", "FillingTime", "Missing", "NoteTime"},
                        new Object[]{str_cars, str_carpower, str_carleanth, str_educe, str_expel, str_rush, str_eave, time}, new String[]{"DriveRecordId", "StationId"}, new String[]{DriveRecordId, StationId});
            } else {
                isok = openHelper.insert("TrainFormation", new String[]{"DriveRecordId", "StationId", "CarriageCount", "CarryingCapacity", "Length", "Decompress", "RowTime", "FillingTime", "Missing", "NoteTime"},
                        new Object[]{DriveRecordId, StationId, str_cars, str_carpower, str_carleanth, str_educe, str_expel, str_rush, str_eave, time});
            }
        }
        return isok;
    }
}
