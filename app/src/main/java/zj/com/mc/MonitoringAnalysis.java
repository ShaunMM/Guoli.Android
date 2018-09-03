package zj.com.mc;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
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
 * 监控分析单 --- 详情
 */
public class MonitoringAnalysis extends Activity implements View.OnClickListener {

    private int SAVECONTER = 0;//判断保存上传状态
    private String personid1, personid2; //记录员工id
    private DBOpenHelper dbOpenHelper;
    private List<Map> list;
    private List<TextView> viewList;
    private List<EditText> edList;
    private ListView listView;
    private int intentid;//判断跳转码
    private TextView whichtv;//中转
    private TextView takedate, runstart, daily_anlysis_title;
    private TextView anlysis_TrainCode, anlysis_OperateSection1, anlysis_OperateSection2;
    private TextView anlysis_TakeSection1, anlysis_TakeSection2;
    private TextView anlysis_DriverId, anlysis_ViceDriverId;
    private EditText anlysis_LocomotiveType;
    private EditText daily_anlysis_problem, daily_anlysis_dopinion;
    private EditText txtkey;
    private RelativeLayout anlysis_popuwindow;
    private LinearLayout daily_anlysis_titleback;
    private String key;//弹窗内edittext中的文字
    private String clouns = "TrainCode";//关键字和列明
    private String table1 = "ViewPersonInfo";//需要查询的表名
    private List<Map> listsearch;
    private String personID;
    private String isUploaded = "2";
    private ISystemConfig systemConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.monitoringanalysiss);
        Myapplilcation.addActivity(this);
        initview();
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("InstructorAnalysis");
        intentid = bundle.getInt("listitemId");

        if (intentid != -1) {
            isUploaded = bundle.getString("IsUploaded");
            String sql = "select * from InstructorAnalysis" + " where Id  =? ";
            String[] selectionArgs = new String[]{intentid + ""};
            list = dbOpenHelper.queryListMap(sql, selectionArgs);
            Map map = list.get(0);
            SAVECONTER = (Integer) map.get("IsUploaded");

            takedate.setText(map.get("RunDate").toString());
            anlysis_TrainCode.setText(map.get("TrainCode").toString());
            anlysis_LocomotiveType.setText(map.get("LocomotiveType").toString());
            personid1 = map.get("DriverId").toString();
            List<Map> maindriverinfo = dbOpenHelper.queryListMap("select * from PersonInfo where Id=?", new String[]{personid1});
            if (maindriverinfo.size() != 0) {
                anlysis_DriverId.setText(maindriverinfo.get(0).get("Name").toString());
            }
            personid2 = map.get("ViceDriverId").toString();
            List<Map> auxdriverinfo = dbOpenHelper.queryListMap("select * from PersonInfo where Id= ?", new String[]{personid2});
            if (maindriverinfo.size() != 0) {
                anlysis_ViceDriverId.setText(auxdriverinfo.get(0).get("Name").toString());
            }
            if (map.get("RunSection").toString().equals("-")) {
                anlysis_OperateSection1.setText("");
                anlysis_OperateSection2.setText("");
            } else {
                anlysis_OperateSection1.setText(map.get("RunSection").toString().split("-")[0]);
                anlysis_OperateSection2.setText(map.get("RunSection").toString().split("-")[1]);
            }
            if (map.get("AnalysisStart").toString().length() > 1) {
                runstart.setText(map.get("AnalysisStart").toString().split(" ")[0]);
                anlysis_TakeSection1.setText(map.get("AnalysisStart").toString().split(" ")[1]);
                anlysis_TakeSection2.setText(map.get("AnalysisEnd").toString().split(" ")[1]);
            } else {
                runstart.setText("");
                anlysis_TakeSection1.setText("");
                anlysis_TakeSection2.setText("");
            }
            daily_anlysis_problem.setText(map.get("Problems").toString());
            daily_anlysis_dopinion.setText(map.get("Suggests").toString());
        }

        if (isUploaded.equals("1")) {
            anlysis_LocomotiveType.setEnabled(false);
            daily_anlysis_problem.setEnabled(false);
            daily_anlysis_dopinion.setEnabled(false);
            txtkey.setEnabled(false);
        }
    }

    private void initview() {
        takedate = (TextView) findViewById(R.id.anlysis_takedate);
        takedate.setOnClickListener(this);

        runstart = (TextView) findViewById(R.id.anlysis_RunStart);
        runstart.setOnClickListener(this);
        anlysis_LocomotiveType = (EditText) findViewById(R.id.anlysis_LocomotiveType);
        anlysis_TrainCode = (TextView) findViewById(R.id.anlysis_TrainCode);
        anlysis_TrainCode.setOnClickListener(this);
        daily_anlysis_title = (TextView) findViewById(R.id.daily_anlysis_title2);

        daily_anlysis_titleback = (LinearLayout) findViewById(R.id.daily_anlysis_titleback);
        daily_anlysis_titleback.setOnClickListener(this);

        daily_anlysis_problem = (EditText) findViewById(R.id.daily_anlysis_problem);
        daily_anlysis_dopinion = (EditText) findViewById(R.id.daily_anlysis_dopinion);

        anlysis_ViceDriverId = (TextView) findViewById(R.id.anlysis_ViceDriverId);
        anlysis_ViceDriverId.setOnClickListener(this);
        anlysis_DriverId = (TextView) findViewById(R.id.anlysis_DriverId);
        anlysis_DriverId.setOnClickListener(this);
        anlysis_OperateSection1 = (TextView) findViewById(R.id.anlysis_OperateSection1);
        anlysis_OperateSection2 = (TextView) findViewById(R.id.anlysis_OperateSection2);
        anlysis_OperateSection1.setOnClickListener(this);
        anlysis_OperateSection2.setOnClickListener(this);

        anlysis_TakeSection1 = (TextView) findViewById(R.id.anlysis_TakeSection1);
        anlysis_TakeSection1.setOnClickListener(this);
        anlysis_TakeSection2 = (TextView) findViewById(R.id.anlysis_TakeSection2);
        anlysis_TakeSection2.setOnClickListener(this);

        anlysis_popuwindow = (RelativeLayout) findViewById(R.id.anlysis_popuwindow);
        anlysis_popuwindow.setOnClickListener(this);
        anlysis_popuwindow.setVisibility(View.GONE);
        findViewById(R.id.anlysis_popuwindow_yes).setOnClickListener(this);

        findViewById(R.id.daily_anlysis_save).setOnClickListener(this);
        findViewById(R.id.daily_anlysis_saveup).setOnClickListener(this);
        findViewById(R.id.daily_anlysis_cancle).setOnClickListener(this);

        dbOpenHelper = DBOpenHelper.getInstance(getApplicationContext());
        systemConfig = SystemConfigFactory.getInstance(this).getSystemConfig();
        personID = systemConfig.getUserId();

        txtkey = (EditText) findViewById(R.id.anlysis_key);
        listView = (ListView) findViewById(R.id.anlysis_listv);

        viewList = new ArrayList<TextView>();
        viewList.add(takedate);
        viewList.add(runstart);
        viewList.add(anlysis_TrainCode);
        viewList.add(anlysis_OperateSection1);
        viewList.add(anlysis_OperateSection2);

        viewList.add(anlysis_TakeSection1);
        viewList.add(anlysis_TakeSection2);
        viewList.add(anlysis_DriverId);
        viewList.add(anlysis_ViceDriverId);

        edList = new ArrayList<EditText>();
        edList.add(anlysis_LocomotiveType);
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
        } else {
            txtkey.setHint("请输入要搜索的内容");
        }

        txtkey.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                UtilisClass.setdriverid2(whichtv, anlysis_DriverId, personid1);
                UtilisClass.setdriverid2(whichtv, anlysis_ViceDriverId, personid2);

                key = txtkey.getText().toString();
                if (key != null && !"".equals(key.trim())) {
                    if (tablename.equals("ViewPersonInfo")) {
                        listsearch = getDBpersonname(tablename, key);
                    } else {
                        if (tablename.equals("TrainNo")) {
                            listsearch = getDBpersonname3(key);
                        } else {
                            listsearch = getDBpersonname2(key);
                        }
                    }

                    listView.setAdapter(new CommonAdapter<Map>(MonitoringAnalysis.this, listsearch, R.layout.editlist1) {

                        @Override
                        protected void convertlistener(ViewHolder holder, final Map map) {
                        }

                        @Override
                        public void convert(ViewHolder holder, Map map) {
                            if (tablename.equals("ViewPersonInfo")) {
                                holder.setText(R.id.editlist_item1, map.get("WorkNo").toString());
                                holder.setText(R.id.editlist_item2, map.get("Name").toString());
                                holder.setText(R.id.editlist_item3, map.get("Spell").toString());
                            } else {
                                if (tablename.equals("TrainNo")) {
                                    holder.setText(R.id.editlist_item1, map.get("FullName").toString());
                                } else {
                                    holder.setText(R.id.editlist_item2, map.get("Spell").toString());
                                    holder.setText(R.id.editlist_item1, map.get("StationName").toString());
                                }
                            }
                        }
                    });

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            if (tablename.equals("ViewPersonInfo")) {
                                txtkey.setText(listsearch.get(i).get("Name").toString());
                                if (ed == anlysis_DriverId) {
                                    personid1 = listsearch.get(0).get("Id") + "";
                                } else if (ed == anlysis_ViceDriverId) {
                                    personid2 = listsearch.get(0).get("Id") + "";
                                }
                            } else {
                                if (tablename.equals("TrainNo")) {
                                    txtkey.setText(listsearch.get(i).get("FullName").toString());
                                } else {
                                    txtkey.setText(listsearch.get(i).get("StationName").toString());
                                }
                            }
                            whichtv.setText(txtkey.getText().toString());
                            anlysis_popuwindow.setVisibility(View.GONE);
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
                case R.id.daily_anlysis_titleback:
                    finish();
                    UtilisClass.hidInputMethodManager(MonitoringAnalysis.this, anlysis_LocomotiveType);
                    break;
                case R.id.anlysis_takedate:
                    getdatepicker(takedate);
                    break;
                case R.id.anlysis_TrainCode:
                    anlysis_popuwindow.setVisibility(View.VISIBLE);
                    getsearch(anlysis_TrainCode, "TrainNo");
                    break;
                case R.id.anlysis_LocomotiveType:
                    //机车型号
                    break;
                case R.id.anlysis_TakeSection1:
                    //开车时间1
                    gettimepicker(anlysis_TakeSection1);
                    break;
                case R.id.anlysis_TakeSection2:
                    //开始时间2
                    gettimepicker(anlysis_TakeSection2);
                    break;
                case R.id.anlysis_RunStart:
                    //分析日期
                    getdatepicker(runstart);
                    break;
                case R.id.anlysis_ViceDriverId:
                    //副班司机
                    anlysis_popuwindow.setVisibility(View.VISIBLE);
                    getsearch(anlysis_ViceDriverId, table1);
                    break;
                case R.id.anlysis_DriverId:
                    //主班司机
                    anlysis_popuwindow.setVisibility(View.VISIBLE);
                    getsearch(anlysis_DriverId, table1);
                    break;
                case R.id.anlysis_OperateSection1:
                    //运行区段1
                    anlysis_popuwindow.setVisibility(View.VISIBLE);
                    getsearch(anlysis_OperateSection1, "BaseStation");
                    break;
                case R.id.anlysis_OperateSection2:
                    //运行区段2
                    anlysis_popuwindow.setVisibility(View.VISIBLE);
                    getsearch(anlysis_OperateSection2, "BaseStation");
                    break;
                case R.id.anlysis_popuwindow_yes:
                    //姓名弹窗确定键
                    whichtv.setText(txtkey.getText().toString());
                    anlysis_popuwindow.setVisibility(View.GONE);
                    UtilisClass.setDriverid(MonitoringAnalysis.this, whichtv, anlysis_DriverId, personid1);
                    UtilisClass.setDriverid(MonitoringAnalysis.this, whichtv, anlysis_ViceDriverId, personid2);
                    break;
                case R.id.anlysis_popuwindow:
                    //设置点击边上弹窗消失
                    anlysis_popuwindow.setVisibility(View.GONE);
                    break;
                case R.id.daily_anlysis_save:
                    //保存到本地
                    if (SAVECONTER == 0) {
                        getisempty(0);
                        UtilisClass.hidInputMethodManager(MonitoringAnalysis.this, anlysis_LocomotiveType);
                        showToast("保存成功！");
                        finish();
                    } else {
                        showToast("保存失败！");
                    }
                    break;
                case R.id.daily_anlysis_saveup:
                    //保存并上传
                    if (SAVECONTER == 1) {
                        getisempty2(viewList, edList, 1);
                        UtilisClass.hidInputMethodManager(MonitoringAnalysis.this, anlysis_LocomotiveType);
                    } else {
                        showToast("上传失败，文件已上传！");
                    }
                    break;
                case R.id.daily_anlysis_cancle:
                    UtilisClass.hidInputMethodManager(MonitoringAnalysis.this, anlysis_LocomotiveType);
                    finish();
                    break;
            }
        } else {
            switch (view.getId()) {
                case R.id.daily_anlysis_titleback:
                    UtilisClass.hidInputMethodManager(MonitoringAnalysis.this, anlysis_LocomotiveType);
                    finish();
                    break;
                case R.id.daily_anlysis_cancle:
                    UtilisClass.hidInputMethodManager(MonitoringAnalysis.this, anlysis_LocomotiveType);
                    finish();
                    break;
            }
        }
    }

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

    private void gettimepicker(final TextView ed) {
        Calendar c2 = Calendar.getInstance();
        new TimePickerDialog(this, 0, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                showToast("你选择的是：" + hourOfDay + "时" + minute + "分");
                if (minute > 10) {
                    ed.setText(+hourOfDay + ":" + minute);
                } else {
                    ed.setText(+hourOfDay + ":" + "0" + minute);

                }
            }
        }, c2.get(Calendar.HOUR_OF_DAY), c2.get(Calendar.MINUTE), true).show();
    }

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

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

    private List<Map> getDBpersonname2(String coads) {

        String sql = "select * from BaseStation" +
                " where StationName like ? or Spell like ?";
        String[] selectionArgs = new String[]{"%" + coads + "%",
                "%" + coads + "%"};
        List<Map> listsearch = dbOpenHelper.queryListMap(sql, selectionArgs);

        return listsearch;
    }

    private List<Map> getDBpersonname3(String coads) {

        String sql = "select * from TrainNo" +
                " where FullName like ?";
        String[] selectionArgs = new String[]{
                "%" + coads + "%"};
        List<Map> listsearch = dbOpenHelper.queryListMap(sql, selectionArgs);

        return listsearch;
    }

    @Override
    public void onBackPressed() {
        if (anlysis_popuwindow.getVisibility() != View.GONE) {
            anlysis_popuwindow.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }

    private void getisempty(int uploadedid) {
        if (intentid == -1) {
            dbOpenHelper.insert("InstructorAnalysis", new String[]{"InstructorId",
                            "RunDate", "TrainCode",
                            "LocomotiveType", "DriverId", "ViceDriverId", "RunSection",
                            "AnalysisStart", "AnalysisEnd", "Problems", "Suggests", "IsUploaded", "RecirdTime"},
                    new Object[]{personID, takedate.getText().toString(), anlysis_TrainCode.getText().toString(),
                            anlysis_LocomotiveType.getText().toString(), personid1, personid2, anlysis_OperateSection1.getText().toString() + "-" +
                            anlysis_OperateSection2.getText().toString(), runstart.getText().toString() + " " + anlysis_TakeSection1.getText().toString(),
                            runstart.getText().toString() + " " + anlysis_TakeSection1.getText().toString(),
                            daily_anlysis_problem.getText().toString(), daily_anlysis_dopinion.getText().toString(), uploadedid, 1});
            Isrecodetime();
        } else {
            dbOpenHelper.update("InstructorAnalysis",
                    new String[]{"InstructorId",
                            "RunDate", "TrainCode",
                            "LocomotiveType", "DriverId", "ViceDriverId", "RunSection",
                            "AnalysisStart", "AnalysisEnd", "Problems", "Suggests", "IsUploaded"},
                    new Object[]{personID, takedate.getText().toString(), anlysis_TrainCode.getText().toString(),
                            anlysis_LocomotiveType.getText().toString(), personid1, personid2, anlysis_OperateSection1.getText().toString() + "-" +
                            anlysis_OperateSection2.getText().toString(), runstart.getText().toString() + " " + anlysis_TakeSection1.getText().toString(),
                            runstart.getText().toString() + " " + anlysis_TakeSection1.getText().toString(),
                            daily_anlysis_problem.getText().toString(), daily_anlysis_dopinion.getText().toString(), uploadedid},
                    new String[]{"Id"}, new String[]{intentid + ""});
        }
    }

    private void getisempty2(List<TextView> viewList, List<EditText> edList, int uploadedid) {
        aa:
        for (int i = 0; i < viewList.size(); i++) {

            if (viewList.get(i).getText().toString().equals("")) {
                UtilisClass.showToast(MonitoringAnalysis.this, getResources().getString(R.string.perfectInformation));
                break aa;
            } else {
                if (i == viewList.size() - 1) {
                    for (int j = 0; j < edList.size(); j++) {
                        if (edList.get(j).getText().toString().equals("")) {
                            UtilisClass.showToast(MonitoringAnalysis.this, getResources().getString(R.string.perfectInformation));
                            break aa;
                        } else {
                            if (j == edList.size() - 1) {
                                if (intentid == -1) {

                                    dbOpenHelper.insert("InstructorAnalysis", new String[]{"InstructorId",
                                                    "RunDate", "TrainCode",
                                                    "LocomotiveType", "DriverId", "ViceDriverId", "RunSection",
                                                    "AnalysisStart", "AnalysisEnd", "Problems", "Suggests", "IsUploaded", "RecirdTime"},
                                            new Object[]{personID, takedate.getText().toString(), anlysis_TrainCode.getText().toString(),
                                                    anlysis_LocomotiveType.getText().toString(), personid1, personid2, anlysis_OperateSection1.getText().toString() + "-" +
                                                    anlysis_OperateSection2.getText().toString(), runstart.getText().toString() + " " + anlysis_TakeSection1.getText().toString(),
                                                    runstart.getText().toString() + " " + anlysis_TakeSection1.getText().toString(),
                                                    daily_anlysis_problem.getText().toString(), daily_anlysis_dopinion.getText().toString(), uploadedid, 1});
                                    Isrecodetime();
                                    List<Map> mapListid = dbOpenHelper.queryListMap("select * from InstructorAnalysis where InstructorId=?", new String[]{personID});
                                    if (mapListid.size() != 0) {
                                        final String upid = mapListid.get(mapListid.size() - 1).get("Id") + "";

                                        Myapplilcation.getExecutorService().execute(new Runnable() {
                                            @Override
                                            public void run() {
                                                NetUtils.updataarguments3dingle(dbOpenHelper, systemConfig, "InstructorAnalysis", upid);
                                            }
                                        });
                                    }

                                } else {
                                    dbOpenHelper.update("InstructorAnalysis",
                                            new String[]{"InstructorId",
                                                    "RunDate", "TrainCode",
                                                    "LocomotiveType", "DriverId", "ViceDriverId", "RunSection",
                                                    "AnalysisStart", "AnalysisEnd", "Problems", "Suggests", "IsUploaded"},
                                            new Object[]{personID, takedate.getText().toString(), anlysis_TrainCode.getText().toString(),
                                                    anlysis_LocomotiveType.getText().toString(), personid1, personid2, anlysis_OperateSection1.getText().toString() + "-" +
                                                    anlysis_OperateSection2.getText().toString(), runstart.getText().toString() + " " + anlysis_TakeSection1.getText().toString(),
                                                    runstart.getText().toString() + " " + anlysis_TakeSection1.getText().toString(),
                                                    daily_anlysis_problem.getText().toString(), daily_anlysis_dopinion.getText().toString(), uploadedid},
                                            new String[]{"Id"}, new String[]{intentid + ""});
                                    Myapplilcation.getExecutorService().execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            NetUtils.updataarguments3dingle(dbOpenHelper, systemConfig, "InstructorAnalysis", intentid + "");
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

    private void Isrecodetime() {
        String adddata = takedate.getText().toString();
        String[] datas = adddata.split("-");

        int year = Integer.parseInt(datas[0]);
        int month = Integer.parseInt(datas[1]);
        String data = year + "-" + month;
        int day = Integer.parseInt(datas[2]);

        String data2 = UtilisClass.getStringDate();
        if (intentid == -1) {
            //第一次添加
            int quotaid;
            if (day < 11) {
                quotaid = UtilisClass.getLASTMONTHTRAIN();//上旬
            } else if (10 < day && day < 21) {
                quotaid = UtilisClass.getMIDDLEMONTHTRAIN();//中旬
            } else {
                quotaid = UtilisClass.getNEXTMONTHTRAIN();//下旬

            }

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


            int alltrain = UtilisClass.getMMONTHTRAINTIME();
            List<Map> listall = dbOpenHelper.queryListMap("select * from InstructorQuotaRecord where InstructorId=? and QuotaId=? and UpdateTime like ?", new String[]{personID, alltrain + "", data + "%"});

            if (listall.size() == 0) {
                dbOpenHelper.insert("InstructorQuotaRecord", new String[]{
                        "InstructorId", "QuotaId", "FinishedAmmount", "UpdateTime", "Year", "Month"
                }, new Object[]{personID, alltrain, 1, data2, year, month});
            } else {
                int num = Integer.parseInt(listall.get(0).get("FinishedAmmount") + "");
                num = num + 1;
                String id = listall.get(0).get("Id") + "";
                dbOpenHelper.update("InstructorQuotaRecord", new String[]{"FinishedAmmount", "UpdateTime", "IsUploaded"
                }, new Object[]{num, data2, "1"}, new String[]{"Id"}, new String[]{id});
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Myapplilcation.removeActivity(this);
    }
}