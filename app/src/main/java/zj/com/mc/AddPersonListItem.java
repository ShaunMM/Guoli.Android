package zj.com.mc;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import DBUtils.DBOpenHelper;
import Utils.CommonAdapter;
import Utils.NetUtils;
import Utils.ViewHolder;
import config.ISystemConfig;
import config.SystemConfigFactory;

/**
 * 添乘信息单 --- 详情
 */
public class AddPersonListItem extends Activity implements View.OnClickListener {

    private int SAVECONTER = 0;//判断保存上传状态
    private DBOpenHelper dbOpenHelper;
    private List<Map> list;
    private List<TextView> viewList;
    private List<EditText> edList;
    private ListView listView;
    private int intentid;//判断跳转码
    private TextView whichtv;//中转
    private TextView takedate, runstart, runend, operatestart, operatesend, attendtime, endattendtime;
    private TextView tiancheng_TrainCode, tiancheng_OperateSection1, tiancheng_OperateSection2;
    private TextView tiancheng_TakeSection1, tiancheng_TakeSection2;
    private TextView taincheng_DriverId, tiancheng_ViceDriverId, tiancheng_StudentId, daily_tiancheng_title;
    private TextView tiancheng_LocomotiveType;
    private TextView carcount;
    private TextView wholeweight;
    private TextView length;

    private EditText daily_tiancheng_aim, daily_tiancheng_problem, daily_tiancheng_dopinion;

    RelativeLayout tiancheng_popuwindow;
    private LinearLayout daily_tiancheng_titleback;
    String key;//弹窗内edittext中的文字
    private String clouns = "TrainCode";//关键字和列明
    String table1 = "ViewPersonInfo";//需要查询的表名
    private EditText txtkey;
    private List<Map> listsearch;
    private String personID;
    private String maindriverid, auxdriverid, studriverid;
    private String isUploaded = "2";

    private String[] trainmodels = new String[]{"HXD2-", "SS4G-", "DF8B-", "HXD3C-", "HXD3D-", "DF4DK-", "HXN3B-", "DF7G-", "DF5-", "CRH5-", "CJ5-","X-", "T-"};
    private ISystemConfig systemConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tianchengdetail_sec);
        Myapplilcation.addActivity(this);
        initview();
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("InstructorTempTake");
        intentid = bundle.getInt("listitemId");
        if (intentid != -1) {
            list = new ArrayList<>();
            isUploaded = bundle.getString("IsUploaded");
            String sql = "select * from InstructorTempTake" + " where Id  =? ";
            String[] selectionArgs = new String[]{intentid + ""};
            list = dbOpenHelper.queryListMap(sql, selectionArgs);
            Map map = list.get(0);
            SAVECONTER = (Integer) map.get("IsUploaded");
            //执行控件赋值
            takedate.setText(map.get("TakeDate").toString());
            tiancheng_TrainCode.setText(map.get("TrainCode").toString());
            tiancheng_LocomotiveType.setText(map.get("LocomotiveType").toString());

            maindriverid = map.get("DriverId").toString();

            List<Map> maindriverinfo = dbOpenHelper.queryListMap("select * from PersonInfo where Id=?", new String[]{maindriverid});
            if (maindriverinfo.size() != 0) {
                taincheng_DriverId.setText(maindriverinfo.get(0).get("Name").toString());
            }
            auxdriverid = map.get("ViceDriverId").toString();
            List<Map> auxdriverinfo = dbOpenHelper.queryListMap("select * from PersonInfo where Id= ?", new String[]{auxdriverid});
            if (auxdriverinfo.size() != 0) {
                tiancheng_ViceDriverId.setText(auxdriverinfo.get(0).get("Name").toString());
            }
            studriverid = map.get("StudentId").toString();
            List<Map> studriverinfo = dbOpenHelper.queryListMap("select * from PersonInfo where Id= ?", new String[]{studriverid});
            if (studriverinfo.size() != 0) {
                tiancheng_StudentId.setText(studriverinfo.get(0).get("Name").toString());
            }
            carcount.setText(map.get("CarCount").toString());
            wholeweight.setText(map.get("WholeWeight").toString());
            if (map.get("Length").toString().equals("0")) {
                length.setText("");
            } else {
                length.setText(map.get("Length").toString());
            }
            if (map.get("TakeSection").toString().equals("-")) {
                tiancheng_TakeSection1.setText("");
                tiancheng_TakeSection2.setText("");
            } else {
                tiancheng_TakeSection1.setText(map.get("TakeSection").toString().split("-")[0]);
                tiancheng_TakeSection2.setText(map.get("TakeSection").toString().split("-")[1]);
            }
            runstart.setText(map.get("RunStart").toString());
            runend.setText(map.get("RunEnd").toString());
            if (map.get("OperateSection").toString().equals("-")) {
                tiancheng_OperateSection1.setText("");
                tiancheng_OperateSection2.setText("");
            } else {
                tiancheng_OperateSection1.setText(map.get("OperateSection").toString().split("-")[0]);
                tiancheng_OperateSection2.setText(map.get("OperateSection").toString().split("-")[1]);
            }
            operatestart.setText(map.get("OperateStart").toString());
            operatesend.setText(map.get("OperateEnd").toString());
            endattendtime.setText(map.get("EndAttendTime").toString());
            daily_tiancheng_aim.setText(map.get("TakeAims").toString());
            attendtime.setText(map.get("AttendTime").toString());
            daily_tiancheng_problem.setText(map.get("Problems").toString());
            daily_tiancheng_dopinion.setText(map.get("Suggests").toString());
        }

        if (isUploaded.equals("1")) {
            carcount.setEnabled(false);
            wholeweight.setEnabled(false);
            length.setEnabled(false);
            tiancheng_LocomotiveType.setEnabled(false);
            daily_tiancheng_aim.setEnabled(false);
            daily_tiancheng_problem.setEnabled(false);
            daily_tiancheng_dopinion.setEnabled(false);
        }
    }

    private void initview() {
        takedate = (TextView) findViewById(R.id.tiancheng_takedate);
        takedate.setOnClickListener(this);

        runstart = (TextView) findViewById(R.id.tiancheng_RunStart);
        runstart.setOnClickListener(this);
        runend = (TextView) findViewById(R.id.tiancheng_RunEnd);
        runend.setOnClickListener(this);
        operatestart = (TextView) findViewById(R.id.tiancheng_OperateStart);
        operatestart.setOnClickListener(this);
        operatesend = (TextView) findViewById(R.id.tiancheng_OperateEnd);
        operatesend.setOnClickListener(this);
        attendtime = (TextView) findViewById(R.id.tiancheng_AttendTime);
        attendtime.setOnClickListener(this);
        endattendtime = (TextView) findViewById(R.id.tiancheng_EndAttendTime);
        endattendtime.setOnClickListener(this);
        tiancheng_LocomotiveType = (TextView) findViewById(R.id.tiancheng_LocomotiveType);
        tiancheng_LocomotiveType.setOnClickListener(this);
        tiancheng_TrainCode = (TextView) findViewById(R.id.tiancheng_TrainCode);
        tiancheng_TrainCode.setOnClickListener(this);
        daily_tiancheng_title = (TextView) findViewById(R.id.daily_tiancheng_title2);

        daily_tiancheng_titleback = (LinearLayout) findViewById(R.id.daily_tiancheng_titleback);
        daily_tiancheng_titleback.setOnClickListener(this);

        daily_tiancheng_aim = (EditText) findViewById(R.id.daily_tiancheng_aim);
        daily_tiancheng_problem = (EditText) findViewById(R.id.daily_tiancheng_problem);
        daily_tiancheng_dopinion = (EditText) findViewById(R.id.daily_tiancheng_dopinion);

        tiancheng_ViceDriverId = (TextView) findViewById(R.id.tiancheng_ViceDriverId);
        tiancheng_ViceDriverId.setOnClickListener(this);
        taincheng_DriverId = (TextView) findViewById(R.id.taincheng_DriverId);
        taincheng_DriverId.setOnClickListener(this);
        tiancheng_StudentId = (TextView) findViewById(R.id.tiancheng_StudentId);
        tiancheng_StudentId.setOnClickListener(this);

        tiancheng_OperateSection1 = (TextView) findViewById(R.id.tiancheng_OperateSection1);
        tiancheng_OperateSection2 = (TextView) findViewById(R.id.tiancheng_OperateSection2);
        tiancheng_OperateSection1.setOnClickListener(this);//操纵区段
        tiancheng_OperateSection2.setOnClickListener(this);

        tiancheng_TakeSection1 = (TextView) findViewById(R.id.tiancheng_TakeSection1);
        tiancheng_TakeSection1.setOnClickListener(this);
        tiancheng_TakeSection2 = (TextView) findViewById(R.id.tiancheng_TakeSection2);
        tiancheng_TakeSection2.setOnClickListener(this);

        tiancheng_popuwindow = (RelativeLayout) findViewById(R.id.tiancheng_popuwindow);
        tiancheng_popuwindow.setOnClickListener(this);
        tiancheng_popuwindow.setVisibility(View.GONE);
        findViewById(R.id.tiancheng_popuwindow_yes).setOnClickListener(this);

        findViewById(R.id.daily_tiancheng_save).setOnClickListener(this);
        findViewById(R.id.daily_tiancheng_saveup).setOnClickListener(this);
        findViewById(R.id.daily_tiancheng_cancle).setOnClickListener(this);

        dbOpenHelper = DBOpenHelper.getInstance(getApplicationContext());
        systemConfig = SystemConfigFactory.getInstance(this).getSystemConfig();
        personID = systemConfig.getUserId();

        carcount = (TextView) findViewById(R.id.tiancheng_CarCount);
        carcount.setOnClickListener(this);
        wholeweight = (TextView) findViewById(R.id.tiancheng_WholeWeight);
        wholeweight.setOnClickListener(this);
        length = (TextView) findViewById(R.id.tiancheng_Length);
        length.setOnClickListener(this);

        txtkey = (EditText) findViewById(R.id.edit_key);
        listView = (ListView) findViewById(R.id.lstv_all);

        viewList = new ArrayList<TextView>();
        viewList.add(takedate);
        viewList.add(runstart);
        viewList.add(runend);
        viewList.add(operatestart);
        viewList.add(operatesend);

        viewList.add(attendtime);
        viewList.add(endattendtime);
        viewList.add(tiancheng_TrainCode);
        viewList.add(tiancheng_OperateSection1);
        viewList.add(tiancheng_OperateSection2);

        viewList.add(tiancheng_TakeSection1);
        viewList.add(tiancheng_TakeSection2);
        viewList.add(taincheng_DriverId);
        viewList.add(tiancheng_ViceDriverId);

        edList = new ArrayList<EditText>();
//        edList.add(daily_tiancheng_aim);
//        edList.add(daily_tiancheng_problem);
//        edList.add(daily_tiancheng_dopinion);
//        setLengthText();
    }

    private void getsearch(final TextView ed, final String tablename) {
        txtkey.setText("");
        txtkey.setFocusable(true);
        txtkey.setFocusableInTouchMode(true);
        txtkey.requestFocus();
        InputMethodManager inputManager =
                (InputMethodManager) txtkey.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(txtkey, 0);
        whichtv = ed;

        if (tablename.equals("ViewPersonInfo")) {
            txtkey.setHint("支持工号，姓名，拼音简称搜索");
        } else if (tablename.equals("BaseStation")) {
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

                UtilisClass.setdriverid2(whichtv, taincheng_DriverId, maindriverid);
                UtilisClass.setdriverid2(whichtv, tiancheng_ViceDriverId, auxdriverid);
                UtilisClass.setdriverid2(whichtv, tiancheng_StudentId, studriverid);

                key = txtkey.getText().toString();
                if (key != null && !"".equals(key.trim())) {
                    if (tablename.equals("ViewPersonInfo")) {

                        listsearch = getDBpersonname(tablename, key);
                    } else if (tablename.equals("BaseStation")) {
                        listsearch = getDBpersonname3(key);
                    } else {
                        listsearch = getDBpersonname2(key);
                    }

                    listView.setAdapter(new CommonAdapter<Map>(AddPersonListItem.this, listsearch, R.layout.editlist1) {
                        @Override
                        protected void convertlistener(ViewHolder holder, final Map map) {
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

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            if (tablename.equals("ViewPersonInfo")) {
                                txtkey.setText(listsearch.get(i).get("Name").toString());
                                if (ed == tiancheng_ViceDriverId) {
                                    auxdriverid = listsearch.get(0).get("Id") + "";
                                }
                                if (ed == taincheng_DriverId) {
                                    maindriverid = listsearch.get(0).get("Id") + "";
                                }
                                if (ed == tiancheng_StudentId) {
                                    studriverid = listsearch.get(0).get("Id") + "";
                                }
                            } else if (tablename.equals("BaseStation")) {
                                txtkey.setText(listsearch.get(i).get("StationName").toString());
                            } else {
                                txtkey.setText(listsearch.get(i).get("FullName").toString());
                            }

                            ed.setText(txtkey.getText().toString());
                            tiancheng_popuwindow.setVisibility(View.GONE);
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

    @Override
    public void onClick(View view) {
        if (SAVECONTER == 0) {
            switch (view.getId()) {
                case R.id.daily_tiancheng_titleback:
                    UtilisClass.hidInputMethodManager(AddPersonListItem.this, carcount);
                    finish();
                    break;
                case R.id.tiancheng_takedate:
                    //添乘日期
                    getdatepicker(takedate);
                    break;
                case R.id.tiancheng_TrainCode:
                    //车次
                    tiancheng_popuwindow.setVisibility(View.VISIBLE);
                    getsearch(tiancheng_TrainCode, clouns);
                    break;
                case R.id.tiancheng_LocomotiveType:
                    hintDialog(1);
                    //机车型号
                    break;
                case R.id.taincheng_DriverId:
                    //主班司机
                    tiancheng_popuwindow.setVisibility(View.VISIBLE);
                    getsearch(taincheng_DriverId, table1);
                    break;
                case R.id.tiancheng_ViceDriverId:
                    //副班司机
                    tiancheng_popuwindow.setVisibility(View.VISIBLE);
                    getsearch(tiancheng_ViceDriverId, table1);
                    break;
                case R.id.tiancheng_StudentId:
                    //学习司机
                    tiancheng_popuwindow.setVisibility(View.VISIBLE);
                    getsearch(tiancheng_StudentId, table1);
                    break;
                case R.id.tiancheng_TakeSection1:
                    //添乘区段
                    tiancheng_popuwindow.setVisibility(View.VISIBLE);
                    getsearch(tiancheng_TakeSection1, "BaseStation");
                    break;
                case R.id.tiancheng_TakeSection2:
                    //添乘区段
                    tiancheng_popuwindow.setVisibility(View.VISIBLE);
                    getsearch(tiancheng_TakeSection2, "BaseStation");
                    break;
                case R.id.tiancheng_RunStart:
                    //开车时间
                    gettimepicker(runstart);
                    break;
                case R.id.tiancheng_RunEnd:
                    //到达时间
                    gettimepicker(runend);
                    break;
                case R.id.tiancheng_OperateStart:
                    //操纵开始时间
                    gettimepicker(operatestart);
                    break;
                case R.id.tiancheng_OperateEnd:
                    //操纵结束时间
                    gettimepicker(operatesend);
                    break;
                case R.id.tiancheng_AttendTime:
                    //出勤时间
                    gettimepicker(attendtime);
                    break;
                case R.id.tiancheng_EndAttendTime:
                    //退勤时间
                    gettimepicker(endattendtime);
                    break;
                case R.id.tiancheng_OperateSection1:
                    //操纵区段1
                    tiancheng_popuwindow.setVisibility(View.VISIBLE);
                    getsearch(tiancheng_OperateSection1, "BaseStation");
                    break;
                case R.id.tiancheng_OperateSection2:
                    //操纵区段2
                    tiancheng_popuwindow.setVisibility(View.VISIBLE);
                    getsearch(tiancheng_OperateSection2, "BaseStation");
                    break;
                case R.id.tiancheng_popuwindow_yes:
                    //姓名弹窗确定键
                    whichtv.setText(txtkey.getText().toString());
                    tiancheng_popuwindow.setVisibility(View.GONE);
                    UtilisClass.setDriverid(AddPersonListItem.this, whichtv, taincheng_DriverId, maindriverid);
                    UtilisClass.setDriverid(AddPersonListItem.this, whichtv, tiancheng_ViceDriverId, auxdriverid);
                    UtilisClass.setDriverid(AddPersonListItem.this, whichtv, tiancheng_StudentId, studriverid);
                    break;
                case R.id.tiancheng_popuwindow:
                    //设置点击边上弹窗消失
                    tiancheng_popuwindow.setVisibility(View.GONE);
                    break;
                case R.id.daily_tiancheng_save:
                    //保存到本地
                    if (SAVECONTER == 0) {
                        getisempty(0);
                        UtilisClass.hidInputMethodManager(AddPersonListItem.this, carcount);
                        showToast("保存成功！");
                    } else {
                        showToast("保存失败");
                    }
                    break;
                case R.id.daily_tiancheng_saveup:
                    //保存并上传
                    if (SAVECONTER == 1) {
                        getisempty2(viewList, edList, 1);
                        UtilisClass.hidInputMethodManager(AddPersonListItem.this, carcount);
                        //执行上传代码
                        SAVECONTER = 0;
                    } else {
                        showToast("保存失败，文件已上传！");
                    }
                    break;
                case R.id.daily_tiancheng_cancle:
                    //取消
                    UtilisClass.hidInputMethodManager(AddPersonListItem.this, carcount);
                    finish();
                    break;
            }
        } else {
            switch (view.getId()) {
                case R.id.daily_tiancheng_titleback:
                    UtilisClass.hidInputMethodManager(AddPersonListItem.this, carcount);
                    finish();
                    break;
                case R.id.daily_tiancheng_cancle:
                    UtilisClass.hidInputMethodManager(AddPersonListItem.this, carcount);
                    finish();
                    break;
            }
        }
    }

    private void hintDialog(int sign) {
        AlertDialog dialog = null;
        if (sign == 1) {
            dialog = new AlertDialog.Builder(this).setTitle("机车型号").setItems(trainmodels, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    tiancheng_LocomotiveType.setText(trainmodels[which]);
                    dialog.dismiss();
                }
            }).create();
        }
        dialog.show();
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        params.width = (int) (width * 0.4);
        params.height = (int) (height * 0.6);
        dialog.getWindow().setAttributes(params);
    }

    //日期获取器
    private void getdatepicker(final TextView ed) {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(this, 0, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                showToast("您选择的是：" + year + "年" + (monthOfYear + 1) + "月" + dayOfMonth + "日");
                ed.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
            }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    //时间获取器
    private void gettimepicker(final TextView ed) {
        Calendar c2 = Calendar.getInstance();
        new TimePickerDialog(this, 0, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                if (minute < 10) {
                    ed.setText(+hourOfDay + ":" + "0" + minute);
                    showToast("你选择的是：" + hourOfDay + "时" + "0" + minute + "分");
                } else {
                    ed.setText(+hourOfDay + ":" + minute);
                    showToast("你选择的是：" + hourOfDay + "时" + minute + "分");
                }
            }
        }, c2.get(Calendar.HOUR_OF_DAY), c2.get(Calendar.MINUTE), true).show();
    }

    //搜索view表中的数据
    private List<Map> getDBpersonname(String tablename, String coads) {
        String sql = "select * from " + tablename +
                " where WorkNo like ? or Name like ? or Spell like ? ";
        String[] selectionArgs = new String[]{"%" + coads + "%",
                "%" + coads + "%",
                "%" + coads + "%"};
        List<Map> listsearch = dbOpenHelper.queryListMap(sql, selectionArgs);
        Log.i("listsearch", String.valueOf(listsearch));
        return listsearch;
    }

    //操纵区段查询
    private List<Map> getDBpersonname3(String coads) {
        String sql = "select * from " + "BaseStation where StationName like ? or Spell like ? ";
        String[] selectionArgs = new String[]{"%" + coads + "%", "%" + coads + "%"};
        List<Map> listsearch = dbOpenHelper.queryListMap(sql, selectionArgs);
        return listsearch;
    }

    private List<Map> getDBpersonname2(String coads) {
        String sql = "select * from TrainNo where FullName like ? ";
        String[] selectionArgs = new String[]{"%" + coads + "%"};
        List<Map> listsearch = dbOpenHelper.queryListMap(sql, selectionArgs);
        return listsearch;
    }

    @Override
    public void onBackPressed() {
        //隐藏虚拟键盘
        if (tiancheng_popuwindow.getVisibility() != View.GONE) {
            tiancheng_popuwindow.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }

    private void getisempty(int uploadedid) {
        String Length = length.getText().toString();
        if (!Length.isEmpty() && !Length.equals("")) {
            int leng = Integer.parseInt(Length);
            float fleng = leng;
            fleng = fleng / 10;
            Length = fleng + "";
        } else {
            Length = "0";
        }
        if (intentid == -1) {
            dbOpenHelper.insert("InstructorTempTake", new String[]{"InstructorId", "TakeDate", "TrainCode", "LocomotiveType",
                            "DriverId", "ViceDriverId", "StudentId", "CarCount", "WholeWeight", "Length", "TakeSection", "RunStart", "RunEnd",
                            "OperateSection", "OperateStart", "OperateEnd", "AttendTime", "EndAttendTime", "TakeAims", "Problems", "Suggests", "IsUploaded", "RecirdTime"},
                    new Object[]{personID, takedate.getText().toString(), tiancheng_TrainCode.getText().toString(),
                            tiancheng_LocomotiveType.getText().toString(), maindriverid, auxdriverid,
                            studriverid, carcount.getText().toString(),
                            wholeweight.getText().toString(), Length, tiancheng_TakeSection1.getText().toString() + "-" + tiancheng_TakeSection2.getText().toString(),
                            runstart.getText().toString(), runend.getText().toString(), tiancheng_OperateSection1.getText().toString() + "-" + tiancheng_OperateSection2.getText().toString(),
                            operatestart.getText().toString(), operatesend.getText().toString(), attendtime.getText().toString(), endattendtime.getText().toString(), daily_tiancheng_aim.getText().toString(),
                            daily_tiancheng_problem.getText().toString(), daily_tiancheng_dopinion.getText().toString(), uploadedid, 1});

        } else {
            dbOpenHelper.update("InstructorTempTake",
                    new String[]{"InstructorId", "TakeDate", "TrainCode", "LocomotiveType",
                            "DriverId", "ViceDriverId", "StudentId", "CarCount", "WholeWeight", "Length", "TakeSection", "RunStart", "RunEnd",
                            "OperateSection", "OperateStart", "OperateEnd", "AttendTime", "EndAttendTime", "TakeAims", "Problems", "Suggests", "IsUploaded"},
                    new Object[]{personID, takedate.getText().toString(), tiancheng_TrainCode.getText().toString(),
                            tiancheng_LocomotiveType.getText().toString(), maindriverid, auxdriverid,
                            studriverid, carcount.getText().toString(),
                            wholeweight.getText().toString(), Length, tiancheng_TakeSection1.getText().toString() + "-" + tiancheng_TakeSection2.getText().toString(),
                            runstart.getText().toString(), runend.getText().toString(), tiancheng_OperateSection1.getText().toString() + "-" + tiancheng_OperateSection2.getText().toString(),
                            operatestart.getText().toString(), operatesend.getText().toString(), attendtime.getText().toString(), endattendtime.getText().toString(), daily_tiancheng_aim.getText().toString(),
                            daily_tiancheng_problem.getText().toString(), daily_tiancheng_dopinion.getText().toString(), uploadedid
                    }, new String[]{"Id"}, new String[]{intentid + ""});
        }
        if (!runstart.getText().toString().equals("") && !runend.getText().toString().equals("")) {
            Isadddayhours(7, 21);
        }
        if (!operatestart.getText().toString().equals("") && !operatesend.getText().toString().equals("")) {
            Isshowdoaddhours();
        }
        Isrecodetimemonthadd();
        Isaddkeyperson();
        finish();
    }

    private void getisempty2(List<TextView> viewList, List<EditText> edList, int uploadedid) {
        aa:
        for (int i = 0; i < viewList.size(); i++) {
            if (viewList.get(i).getText().toString().equals("")) {
                UtilisClass.showToast(AddPersonListItem.this, getResources().getString(R.string.perfectInformation));
                break aa;
            } else {
                if (i == viewList.size() - 1) {
                    for (int j = 0; j < edList.size(); j++) {
                        if (edList.get(j).getText().toString().equals("")) {
                            UtilisClass.showToast(AddPersonListItem.this, getResources().getString(R.string.perfectInformation));
                            break aa;
                        } else {
                            if (j == edList.size() - 1) {
                                if (intentid == -1) {
                                    dbOpenHelper.insert("InstructorTempTake", new String[]{"InstructorId", "TakeDate", "TrainCode", "LocomotiveType",
                                                    "DriverId", "ViceDriverId", "StudentId", "CarCount", "WholeWeight", "Length", "TakeSection", "RunStart", "RunEnd",
                                                    "OperateSection", "OperateStart", "OperateEnd", "AttendTime", "EndAttendTime", "TakeAims", "Problems", "Suggests", "IsUploaded", "RecirdTime"},
                                            new Object[]{personID, takedate.getText().toString(), tiancheng_TrainCode.getText().toString(),
                                                    tiancheng_LocomotiveType.getText().toString(), maindriverid, auxdriverid,
                                                    studriverid, carcount.getText().toString(),
                                                    wholeweight.getText().toString(), length.getText().toString(), tiancheng_TakeSection1.getText().toString() + "-" + tiancheng_TakeSection2.getText().toString(),
                                                    runstart.getText().toString(), runend.getText().toString(), tiancheng_OperateSection1.getText().toString() + "-" + tiancheng_OperateSection2.getText().toString(),
                                                    operatestart.getText().toString(), operatesend.getText().toString(), attendtime.getText().toString(), endattendtime.getText().toString(), daily_tiancheng_aim.getText().toString(),
                                                    daily_tiancheng_problem.getText().toString(), daily_tiancheng_dopinion.getText().toString(), uploadedid, 1});
                                    Isadddayhours(7, 21);
                                    Isshowdoaddhours();
                                    Isrecodetimemonthadd();
                                    //saddkeyperson();
                                    List<Map> mapListid = dbOpenHelper.queryListMap("select * from InstructorTempTake where InstructorId=?", new String[]{personID});
                                    if (mapListid.size() != 0) {
                                        final String upid = mapListid.get(mapListid.size() - 1).get("Id") + "";

                                        Myapplilcation.getExecutorService().execute(new Runnable() {
                                            @Override
                                            public void run() {
                                                NetUtils.updataarguments3dingle(dbOpenHelper, systemConfig, "InstructorTempTake", upid);
                                            }
                                        });
                                    }

                                } else {
                                    dbOpenHelper.update("InstructorTempTake",
                                            new String[]{"InstructorId", "TakeDate", "TrainCode", "LocomotiveType",
                                                    "DriverId", "ViceDriverId", "StudentId", "CarCount", "WholeWeight", "Length", "TakeSection", "RunStart", "RunEnd",
                                                    "OperateSection", "OperateStart", "OperateEnd", "AttendTime", "EndAttendTime", "TakeAims", "Problems", "Suggests", "IsUploaded"},
                                            new Object[]{personID, takedate.getText().toString(), tiancheng_TrainCode.getText().toString(),
                                                    tiancheng_LocomotiveType.getText().toString(), maindriverid, auxdriverid,
                                                    studriverid, carcount.getText().toString(),
                                                    wholeweight.getText().toString(), length.getText().toString(), tiancheng_TakeSection1.getText().toString() + "-" + tiancheng_TakeSection2.getText().toString(),
                                                    runstart.getText().toString(), runend.getText().toString(), tiancheng_OperateSection1.getText().toString() + "-" + tiancheng_OperateSection2.getText().toString(),
                                                    operatestart.getText().toString(), operatesend.getText().toString(), attendtime.getText().toString(), endattendtime.getText().toString(), daily_tiancheng_aim.getText().toString(),
                                                    daily_tiancheng_problem.getText().toString(), daily_tiancheng_dopinion.getText().toString(), uploadedid
                                            }, new String[]{"Id"}, new String[]{intentid + ""});
                                    Myapplilcation.getExecutorService().execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            NetUtils.updataarguments3dingle(dbOpenHelper, systemConfig, "InstructorTempTake", intentid + "");
                                        }
                                    });
                                }
                                finish();

                            }
                        }

                    }
                }
            }
        }
    }

    //7,21区分白天和晚上  添乘小时数
    private void Isadddayhours(int day, int nig) {
        String date = UtilisClass.getStringDate2();
        String data2 = UtilisClass.getStringDate();

        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;

        if (intentid == -1) {
            //第一次添加
            int dayaddid = UtilisClass.getDAYADDPERSONHOURSID();//白天添乘数
            int nightaddid = UtilisClass.getNIGHTADDPERSONHOURSID();//晚上添乘数

            String startime = runstart.getText().toString();//开始时间
            int hour = Integer.parseInt(startime.split(":")[0]);
            int mine = Integer.parseInt(startime.split(":")[1]);
            int allmine = hour * 60 + mine;

            String endtime = runend.getText().toString();//结束时间
            int hour2 = Integer.parseInt(endtime.split(":")[0]);
            int mine2 = Integer.parseInt(endtime.split(":")[1]);
            int allmine2 = hour2 * 60 + mine2;
            int whitemin = day * 60;
            int blackmin = nig * 60;
            int alltimewhite = 0;
            int alltimeblack = 0;

            if (-1 < hour && hour < 7) {
                if (-1 < hour2 && hour2 < 7) {
                    if (hour2 > hour) {
                        alltimeblack = allmine2 - allmine;
                    } else {
                        alltimeblack = whitemin - allmine;
                        alltimewhite = 24 * 60 + allmine2 - allmine - alltimeblack;
                    }
                } else if (7 < hour2 && hour2 < 21) {
                    alltimeblack = whitemin - allmine;
                    alltimewhite = allmine2 - whitemin;

                } else if (21 < hour2 && hour2 < 24) {
                    alltimewhite = blackmin - whitemin;
                    alltimeblack = whitemin - allmine + allmine2 - blackmin;
                }
            } else if (7 < hour && hour < 21) {
                if (-1 < hour2 && hour2 < 7) {
                    alltimewhite = blackmin - allmine;
                    alltimeblack = 24 * 60 - blackmin + allmine2;
                } else if (7 < hour2 && hour2 < 21) {
                    if (hour2 > hour) {
                        alltimewhite = allmine2 - allmine;
                    } else {
                        alltimeblack = 24 * 60 - blackmin + whitemin;
                        alltimewhite = 24 * 60 + allmine2 - allmine - alltimeblack;
                    }
                } else if (21 < hour2 && hour2 < 24) {
                    alltimeblack = allmine2 - blackmin;
                    alltimewhite = blackmin - allmine;
                }

            } else if (21 < hour && hour < 24) {
                if (-1 < hour2 && hour2 < 7) {
                    alltimeblack = 24 * 60 - allmine + allmine2;
                } else if (7 < hour2 && hour2 < 21) {
                    alltimeblack = 24 * 60 - allmine + whitemin;
                    alltimewhite = allmine2 - whitemin;
                } else if (21 < hour2 && hour2 < 24) {
                    if (hour2 > hour) {
                        alltimeblack = allmine2 - whitemin;
                    } else {
                        alltimewhite = blackmin - whitemin;
                        alltimeblack = allmine2 + 24 * 60 - allmine - alltimewhite;
                    }
                }
            }

            if (alltimewhite > 0) {
                List<Map> list = dbOpenHelper.queryListMap("select * from InstructorQuotaRecord where InstructorId=? and QuotaId=? and UpdateTime like ?", new String[]{personID, dayaddid + "", date + "%"});
                if (list.size() == 0) {
                    double alltimewhites = alltimewhite / 60;
                    dbOpenHelper.insert("InstructorQuotaRecord", new String[]{
                            "InstructorId", "QuotaId", "FinishedAmmount", "UpdateTime", "Year", "Month"
                    }, new Object[]{personID, dayaddid + "", alltimewhites, data2, year, month});
                } else {
                    String finishwhitetime = list.get(0).get("FinishedAmmount") + "";
                    double finishwhites = Double.parseDouble(finishwhitetime);
                    double alltimewhites = alltimewhite / 60;
                    alltimewhites = alltimewhites + finishwhites;

                    String id = list.get(0).get("Id") + "";
                    dbOpenHelper.update("InstructorQuotaRecord", new String[]{"FinishedAmmount", "UpdateTime", "IsUploaded"
                    }, new Object[]{alltimewhites, data2, "1"}, new String[]{"Id"}, new String[]{id});
                }
            }

            if (alltimeblack > 0) {
                List<Map> list = dbOpenHelper.queryListMap("select * from InstructorQuotaRecord where InstructorId=? and QuotaId=? and UpdateTime like ?", new String[]{personID, nightaddid + "", date + "%"});
                if (list.size() == 0) {
                    double alltimewhites = alltimeblack / 60;
                    dbOpenHelper.insert("InstructorQuotaRecord", new String[]{
                            "InstructorId", "QuotaId", "FinishedAmmount", "UpdateTime", "Year", "Month"
                    }, new Object[]{personID, nightaddid + "", alltimewhites, data2, year, month});
                } else {
                    String finishblacktime = list.get(0).get("FinishedAmmount") + "";
                    double finishblacks = Double.parseDouble(finishblacktime);
                    double alltimewhites = alltimeblack / 60;
                    alltimewhites = alltimewhites + finishblacks;
                    String id = list.get(0).get("Id") + "";
                    dbOpenHelper.update("InstructorQuotaRecord", new String[]{"FinishedAmmount", "UpdateTime", "IsUploaded"
                    }, new Object[]{alltimewhites, data2, "1"}, new String[]{"Id"}, new String[]{id});
                }
            }

            int addpersonhoursid = UtilisClass.getADDPERSONHOURSID();
            List<Map> list = dbOpenHelper.queryListMap("select * from InstructorQuotaRecord where InstructorId=? and QuotaId=? and UpdateTime like ?", new String[]{personID, addpersonhoursid + "", date + "%"});
            if (list.size() == 0) {
                int alltimes = alltimeblack + alltimewhite;
                double alltimemins = 0;
                if (alltimes != 0) {
                    alltimemins = alltimes / 60;
                }
                dbOpenHelper.insert("InstructorQuotaRecord", new String[]{
                        "InstructorId", "QuotaId", "FinishedAmmount", "UpdateTime", "Year", "Month"
                }, new Object[]{personID, addpersonhoursid + "", alltimemins, data2, year, month});
            } else {
                int alltimes = alltimeblack + alltimewhite;
                double alltimemins = 0;
                if (alltimes != 0) {
                    alltimemins = alltimes / 60;
                }
                String finishblacktime = list.get(0).get("FinishedAmmount") + "";
                double finishblacks = Double.parseDouble(finishblacktime);
                double alltimewhites = 0;
                alltimewhites = alltimemins + finishblacks;
                String id = list.get(0).get("Id") + "";
                dbOpenHelper.update("InstructorQuotaRecord", new String[]{"FinishedAmmount", "UpdateTime", "IsUploaded"
                }, new Object[]{alltimewhites, data2, "1"}, new String[]{"Id"}, new String[]{id});
            }
        }
    }

    private void Isrecodetimemonthadd() {
        String data = UtilisClass.getStringDate2();
        String data2 = UtilisClass.getStringDate();
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;

        if (intentid == -1) {
            //第一次添加
            int quotaid = UtilisClass.getMONTHADDTIME();
            List<Map> list = dbOpenHelper.queryListMap("select * from InstructorQuotaRecord where InstructorId=? and QuotaId=? and UpdateTime like ?", new String[]{personID, quotaid + "", data + "%"});
            if (list.size() == 0) {
                dbOpenHelper.insert("InstructorQuotaRecord", new String[]{
                        "InstructorId", "QuotaId", "FinishedAmmount", "UpdateTime", "Year", "Month"
                }, new Object[]{personID, quotaid, 1, data2, year, month});
            } else {
                int num = Integer.parseInt(list.get(0).get("FinishedAmmount") + "");
                num = num + 1;
                String id = list.get(0).get("Id") + "";
                String lastupdata = list.get(0).get("UpdateTime") + "";
                dbOpenHelper.update("InstructorQuotaRecord", new String[]{"FinishedAmmount", "UpdateTime", "IsUploaded"
                }, new Object[]{num, data2, "1"}, new String[]{"Id"}, new String[]{id});
            }
        }
    }

    private void Isshowdoaddhours() {
        String date = UtilisClass.getStringDate2();
        String data2 = UtilisClass.getStringDate();
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        if (intentid == -1) {
            //第一次添加
            int quotaid = UtilisClass.getSHOWCANCLEALLHOURS();
            String startime = operatestart.getText().toString();
            int hour = Integer.parseInt(startime.split(":")[0]);
            int mine = Integer.parseInt(startime.split(":")[1]);
            int allmine = hour * 60 + mine;

            String endtime = operatesend.getText().toString();
            int hour2 = Integer.parseInt(endtime.split(":")[0]);
            int mine2 = Integer.parseInt(endtime.split(":")[1]);
            int allmine2 = hour2 * 60 + mine2;

            int alltime;
            if (allmine2 > allmine) {
                alltime = allmine2 - allmine;
            } else {
                alltime = allmine2 - allmine + 24 * 60;
            }
            double allhour = 0.0;
            if (alltime != 0) {
                allhour = alltime / 60;
            }

            List<Map> list = dbOpenHelper.queryListMap("select * from InstructorQuotaRecord where InstructorId=? and QuotaId=? and UpdateTime like ?", new String[]{personID, quotaid + "", date + "%"});

            if (list.size() == 0) {
                dbOpenHelper.insert("InstructorQuotaRecord", new String[]{
                        "InstructorId", "QuotaId", "FinishedAmmount", "UpdateTime", "Year", "Month"
                }, new Object[]{personID, quotaid, allhour, data2, year, month});
            } else {
                String allfinishtime = list.get(0).get("FinishedAmmount") + "";
                double finishalltime = Double.parseDouble(allfinishtime);
                finishalltime = finishalltime + allhour;
                String id = list.get(0).get("Id") + "";
                String lastupdata = list.get(0).get("UpdateTime") + "";
                dbOpenHelper.update("InstructorQuotaRecord", new String[]{"FinishedAmmount", "UpdateTime", "IsUploaded"
                }, new Object[]{finishalltime, data2, "1"}, new String[]{"Id"}, new String[]{id});
            }
        }
    }

    private void Isaddkeyperson() {

        String data = UtilisClass.getStringDate2();
        String data2 = UtilisClass.getStringDate();

        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;

        List<Map> islist = dbOpenHelper.queryListMap("select * from InstructorKeyPerson where KeyPersonId=? and ConfirmDate like?",
                new String[]{maindriverid, data});

        if (intentid == -1 && islist.size() == 1) {
            //第一次添加
            int quotaid = UtilisClass.getKEYPERSONADDTIME();
            List<Map> list = dbOpenHelper.queryListMap("select * from InstructorQuotaRecord where InstructorId=? and QuotaId=? and UpdateTime like ?", new String[]{personID, quotaid + "", data + "%"});
            if (list.size() == 0) {
                dbOpenHelper.insert("InstructorQuotaRecord", new String[]{
                        "InstructorId", "QuotaId", "FinishedAmmount", "UpdateTime", "Year", "Month"
                }, new Object[]{personID, quotaid, 1, data2, year, month});
            } else {
                int num = Integer.parseInt(list.get(0).get("FinishedAmmount") + "");
                num = num + 1;
                String id = list.get(0).get("Id") + "";
                String lastupdata = list.get(0).get("UpdateTime") + "";
                dbOpenHelper.update("InstructorQuotaRecord", new String[]{"FinishedAmmount", "UpdateTime", "IsUploaded"
                }, new Object[]{num, data2, "1"}, new String[]{"Id"}, new String[]{id});
            }
        }
    }

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Myapplilcation.removeActivity(this);
    }
}