package zj.com.mc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import DBUtils.DBOpenHelper;
import Utils.CommonAdapter;
import Utils.NetUtils;
import Utils.ViewHolder;
import config.ISystemConfig;
import config.SystemConfigFactory;

/**
 * 机破维修记录---详情
 */
public class MachineDetails extends Activity implements View.OnClickListener {

    private int SAVECONTER = 0;
    private int intentid;
    private ListView mach_poplist;
    private List<TextView> viewList;
    private List<EditText> edList;
    private TextView mach_title2, machine_takedate, machine_TrainCode,
            machine_DriverId, machine_ViceDriverId, machine_StudentId,
            machine_AttendTime;
    private DBOpenHelper dbOpenhelper;
    private RelativeLayout mach_popuwindow;
    EditText machedit_key, machine_LocomotiveType, daily_machine_aim, daily_machine_problem,
            machine_EndAttendTime;
    private TextView whichtv;//中转
    private String maindriverid, auxdriverid, studriverid;
    private String key;
    private List<Map> listsearch, list;
    private String instrutorId;
    private String isUploaded = "2";
    private GoogleApiClient client;
    private ISystemConfig systemConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.machdetails);
        Myapplilcation.addActivity(this);
        initview();
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("InstructorRepair");
        intentid = bundle.getInt("listitemId");
        if (intentid != -1) {
            isUploaded = bundle.getString("IsUploaded");
            String sql = "select * from InstructorRepair" + " where Id  =? ";
            String[] selectionArgs = new String[]{intentid + ""};
            list = dbOpenhelper.queryListMap(sql, selectionArgs);
            SAVECONTER = (Integer) list.get(0).get("IsUploaded");
            initviewtext();
        }

        if (isUploaded.equals("1")) {
            machedit_key.setEnabled(false);
            machine_LocomotiveType.setEnabled(false);
            daily_machine_aim.setEnabled(false);
            daily_machine_problem.setEnabled(false);
            machine_EndAttendTime.setEnabled(false);
        }

        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void initview() {
        dbOpenhelper = dbOpenhelper.getInstance(getApplicationContext());
        systemConfig = SystemConfigFactory.getInstance(this).getSystemConfig();
        instrutorId = systemConfig.getUserId();
        findViewById(R.id.mach_titleback).setOnClickListener(this);//返回
        mach_popuwindow = (RelativeLayout) findViewById(R.id.mach_popuwindow);//popwindow
        mach_popuwindow.setOnClickListener(this);

        machine_takedate = (TextView) findViewById(R.id.machine_takedate);//发生日期
        machine_takedate.setOnClickListener(this);

        machine_TrainCode = (TextView) findViewById(R.id.machine_TrainCode);//车次
        machine_TrainCode.setOnClickListener(this);
        machine_DriverId = (TextView) findViewById(R.id.machine_DriverId);//主班司机
        machine_DriverId.setOnClickListener(this);
        machine_ViceDriverId = (TextView) findViewById(R.id.machine_ViceDriverId);//副班司机
        machine_ViceDriverId.setOnClickListener(this);
        machine_StudentId = (TextView) findViewById(R.id.machine_StudentId);//学习司机
        machine_StudentId.setOnClickListener(this);
        machine_AttendTime = (TextView) findViewById(R.id.machine_AttendTime);//发生地点
        machine_AttendTime.setOnClickListener(this);


        machine_EndAttendTime = (EditText) findViewById(R.id.machine_EndAttendTime);//故障处所
        machine_LocomotiveType = (EditText) findViewById(R.id.machine_LocomotiveType);//机车型号
        daily_machine_aim = (EditText) findViewById(R.id.daily_machine_aim);//故障原因
        daily_machine_problem = (EditText) findViewById(R.id.daily_machine_problem);//责任

        machedit_key = (EditText) findViewById(R.id.machedit_key);
        mach_poplist = (ListView) findViewById(R.id.mach_poplist);
        findViewById(R.id.mach_popuwindow_yes).setOnClickListener(this);


        findViewById(R.id.daily_machine_save).setOnClickListener(this);
        findViewById(R.id.daily_machine_saveup).setOnClickListener(this);
        findViewById(R.id.daily_machine_cancle).setOnClickListener(this);

        viewList = new ArrayList<TextView>();
        viewList.add(machine_takedate);
        viewList.add(machine_TrainCode);
        viewList.add(machine_DriverId);
        viewList.add(machine_ViceDriverId);
        viewList.add(machine_StudentId);
        viewList.add(machine_AttendTime);
        edList = new ArrayList<EditText>();
        edList.add(machine_LocomotiveType);
        edList.add(machine_EndAttendTime);
    }

    private void initviewtext() {

        machine_takedate.setText(list.get(0).get("HappenTime").toString());
        machine_TrainCode.setText(list.get(0).get("TrainCode").toString());

        maindriverid = list.get(0).get("DriverId").toString();
        List<Map> maindriverinfo = dbOpenhelper.queryListMap("select * from PersonInfo where Id=?", new String[]{maindriverid});
        if (maindriverinfo.size() != 0) {
            machine_DriverId.setText(maindriverinfo.get(0).get("Name").toString());
        }

        auxdriverid = list.get(0).get("ViceDriverId").toString();
        List<Map> auxdriverinfo = dbOpenhelper.queryListMap("select * from PersonInfo where Id=?", new String[]{auxdriverid});
        if (auxdriverinfo.size() != 0) {
            machine_ViceDriverId.setText(auxdriverinfo.get(0).get("Name").toString());
        }
        studriverid = list.get(0).get("StudentId").toString();
        List<Map> studriverinfo = dbOpenhelper.queryListMap("select * from PersonInfo where  Id=?", new String[]{studriverid});
        if (studriverinfo.size() != 0) {
            machine_StudentId.setText(studriverinfo.get(0).get("Name").toString());
        }

        machine_LocomotiveType.setText(list.get(0).get("LocomotiveType").toString());
        machine_AttendTime.setText(list.get(0).get("Location").toString());
        machine_EndAttendTime.setText(list.get(0).get("FaultLocation").toString());
        daily_machine_aim.setText(list.get(0).get("FaultReason").toString());
        daily_machine_problem.setText(list.get(0).get("Responsibility").toString());
    }

    @Override
    public void onClick(View view) {
        if (SAVECONTER == 0) {
            switch (view.getId()) {
                case R.id.mach_titleback:
                    UtilisClass.hidInputMethodManager(MachineDetails.this, machine_LocomotiveType);
                    finish();
                    break;
                case R.id.machine_takedate:
                    UtilisClass.getdatepicker(MachineDetails.this, machine_takedate);
                    break;
                case R.id.mach_popuwindow:
                    mach_popuwindow.setVisibility(View.GONE);
                    break;
                case R.id.mach_popuwindow_yes:
                    whichtv.setText(machedit_key.getText().toString());
                    mach_popuwindow.setVisibility(View.GONE);
                    UtilisClass.setDriverid(MachineDetails.this, whichtv, machine_DriverId, maindriverid);
                    UtilisClass.setDriverid(MachineDetails.this, whichtv, machine_ViceDriverId, auxdriverid);
                    UtilisClass.setDriverid(MachineDetails.this, whichtv, machine_StudentId, studriverid);
                    break;
                case R.id.machine_TrainCode:
                    mach_popuwindow.setVisibility(View.VISIBLE);
                    getsearch(machine_TrainCode, "TrainNo");
                    break;
                case R.id.machine_DriverId:
                    //主班司机
                    mach_popuwindow.setVisibility(View.VISIBLE);
                    getsearch(machine_DriverId, "ViewPersonInfo");
                    break;
                case R.id.machine_ViceDriverId:
                    //副班司机
                    mach_popuwindow.setVisibility(View.VISIBLE);
                    getsearch(machine_ViceDriverId, "ViewPersonInfo");
                    break;
                case R.id.machine_StudentId:
                    //学习司机
                    mach_popuwindow.setVisibility(View.VISIBLE);
                    getsearch(machine_StudentId, "ViewPersonInfo");
                    break;
                case R.id.machine_AttendTime:
                    //发生地点
                    mach_popuwindow.setVisibility(View.VISIBLE);
                    getsearch(machine_AttendTime, "BaseStation");
                    break;
                case R.id.daily_machine_save:
                    //保存到本地
                    if (SAVECONTER == 0) {
                        getisempty(0);
                        UtilisClass.hidInputMethodManager(MachineDetails.this, machine_LocomotiveType);
                        UtilisClass.showToast(MachineDetails.this, "保存成功！");
                        finish();
                    } else {
                        UtilisClass.showToast(MachineDetails.this, "保存失败！");
                    }
                    break;
                case R.id.daily_machine_saveup:
                    //保存并上传
                    if (SAVECONTER == 1) {
                        getisempty2(viewList, edList, 1);
                        UtilisClass.hidInputMethodManager(MachineDetails.this, machine_LocomotiveType);
                        SAVECONTER = 0;
                    } else {
                        UtilisClass.showToast(MachineDetails.this, "保存失败，文件已上传！");
                    }
                    break;
                case R.id.daily_machine_cancle:
                    UtilisClass.hidInputMethodManager(MachineDetails.this, machine_LocomotiveType);
                    finish();
                    break;
            }

        } else {
            switch (view.getId()) {
                case R.id.mach_titleback:
                    UtilisClass.hidInputMethodManager(MachineDetails.this, machine_LocomotiveType);
                    finish();
                    break;
                case R.id.daily_machine_cancle:
                    UtilisClass.hidInputMethodManager(MachineDetails.this, machine_LocomotiveType);
                    finish();
                    break;
            }
        }
    }

    private void getsearch(final TextView ed, final String tablename) {
        machedit_key.setText("");
        machedit_key.setFocusable(true);
        machedit_key.setFocusableInTouchMode(true);
        machedit_key.requestFocus();
        InputMethodManager inputManager =
                (InputMethodManager) machedit_key.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(machedit_key, 0);
        whichtv = ed;
        if (tablename.equals("ViewPersonInfo")) {
            machedit_key.setHint("支持工号，姓名，拼音简称搜索");
        } else {
            machedit_key.setHint("请输入要查询的内容");
        }

        machedit_key.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                UtilisClass.setdriverid2(whichtv, machine_DriverId, maindriverid);
                UtilisClass.setdriverid2(whichtv, machine_ViceDriverId, auxdriverid);
                UtilisClass.setdriverid2(whichtv, machine_StudentId, studriverid);

                key = machedit_key.getText().toString();
                if (key != null && !"".equals(key.trim())) {
                    if (tablename.equals("ViewPersonInfo")) {
                        listsearch = getDBpersonname("ViewPersonInfo", key);

                    } else if (tablename.equals("TrainNo")) {

                        listsearch = getDBpersonname3("TrainNo", key);
                    } else {
                        listsearch = getDBpersonname2(key);
                    }

                    mach_poplist.setAdapter(new CommonAdapter<Map>(MachineDetails.this, listsearch, R.layout.editlist1) {

                        @Override
                        protected void convertlistener(ViewHolder holder, final Map map) {
                        }

                        @Override
                        public void convert(ViewHolder holder, Map map) {
                            if (tablename.equals("ViewPersonInfo")) {
                                holder.setText(R.id.editlist_item1, map.get("WorkNo").toString());
                                holder.setText(R.id.editlist_item2, map.get("Name").toString());
                                holder.setText(R.id.editlist_item3, map.get("Spell").toString());
                            } else if (tablename.equals("TrainNo")) {
                                holder.setText(R.id.editlist_item1, map.get("FullName").toString());
                            } else {
                                holder.setText(R.id.editlist_item1, map.get("StationName").toString());
                                holder.setText(R.id.editlist_item2, map.get("Spell").toString());
                            }
                        }
                    });

                    mach_poplist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            if (tablename.equals("ViewPersonInfo")) {
                                machedit_key.setText(listsearch.get(i).get("Name").toString());
                                if (ed == machine_DriverId) {
                                    maindriverid = listsearch.get(0).get("Id") + "";
                                } else if (ed == machine_ViceDriverId) {
                                    auxdriverid = listsearch.get(0).get("Id") + "";
                                } else if (ed == machine_StudentId) {
                                    studriverid = listsearch.get(0).get("Id") + "";
                                }
                            } else if (tablename.equals("TrainNo")) {
                                machedit_key.setText(listsearch.get(i).get("FullName").toString());
                            } else {
                                machedit_key.setText(listsearch.get(i).get("StationName").toString());
                            }
                            whichtv.setText(machedit_key.getText().toString());
                            mach_popuwindow.setVisibility(View.GONE);
                        }

                    });
                } else {
                    mach_poplist.setAdapter(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    //操纵区段查询
    private List<Map> getDBpersonname2(String coads) {

        String sql = "select * from BaseStation" +
                " where StationName like ? or Spell like ?";
        String[] selectionArgs = new String[]{"%" + coads + "%",
                "%" + coads + "%"};
        List<Map> listsearch = dbOpenhelper.queryListMap(sql, selectionArgs);

        return listsearch;
    }

    //搜索view表中的数据
    private List<Map> getDBpersonname(String tablename, String coads) {

        String sql = "select * from " + tablename +
                " where WorkNo like ? or Name like ? or Spell like ? ";
        String[] selectionArgs = new String[]{"%" + coads + "%",
                "%" + coads + "%",
                "%" + coads + "%"};
        List<Map> listsearch = dbOpenhelper.queryListMap(sql, selectionArgs);
        Log.i("listsearch", String.valueOf(listsearch));
        return listsearch;
    }

    //搜索checi表中的数据
    private List<Map> getDBpersonname3(String tablename, String coads) {
        String sql = "select * from " + tablename +
                " where FullName like ? ";
        String[] selectionArgs = new String[]{"%" + coads + "%"};
        List<Map> listsearch = dbOpenhelper.queryListMap(sql, selectionArgs);
        Log.i("listsearch", String.valueOf(listsearch));
        return listsearch;
    }

    //判断view中的内容
    private void getisempty(int uploadedid) {

        if (intentid == -1) {
            dbOpenhelper.insert("InstructorRepair", new String[]{"InstructorId", "HappenTime", "TrainCode", "LocomotiveType",
                            "DriverId", "ViceDriverId", "StudentId", "Location", "FaultLocation", "FaultReason", "Responsibility", "IsUploaded"},
                    new Object[]{instrutorId, machine_takedate.getText().toString(),
                            machine_TrainCode.getText().toString(), machine_LocomotiveType.getText().toString(),
                            maindriverid, auxdriverid, studriverid, machine_AttendTime.getText().toString(),
                            machine_EndAttendTime.getText().toString(), daily_machine_aim.getText().toString(),
                            daily_machine_problem.getText().toString(), uploadedid});
        } else {
            dbOpenhelper.update("InstructorRepair",
                    new String[]{"InstructorId", "HappenTime", "TrainCode", "LocomotiveType",
                            "DriverId", "ViceDriverId", "StudentId", "Location", "FaultLocation", "FaultReason", "Responsibility", "IsUploaded"},
                    new Object[]{instrutorId, machine_takedate.getText().toString(),
                            machine_TrainCode.getText().toString(), machine_LocomotiveType.getText().toString(),
                            maindriverid, auxdriverid, studriverid, machine_AttendTime.getText().toString(),
                            machine_EndAttendTime.getText().toString(), daily_machine_aim.getText().toString(),
                            daily_machine_problem.getText().toString(), uploadedid}, new String[]{"Id"}, new String[]{intentid + ""});
        }
    }

    //判断view中的内容
    private void getisempty2(List<TextView> viewList, List<EditText> edList, int uploadedid) {

        aa:
        for (int i = 0; i < viewList.size(); i++) {

            if (viewList.get(i).getText().toString().equals("")) {
                UtilisClass.showToast(MachineDetails.this, getResources().getString(R.string.perfectInformation));
                break aa;
            } else {
                if (i == viewList.size() - 1) {
                    for (int j = 0; j < edList.size(); j++) {
                        if (edList.get(j).getText().toString().equals("")) {
                            UtilisClass.showToast(MachineDetails.this, getResources().getString(R.string.perfectInformation));
                            break aa;
                        } else {
                            if (j == edList.size() - 1) {
                                if (intentid == -1) {
                                    dbOpenhelper.insert("InstructorRepair", new String[]{"InstructorId", "HappenTime", "TrainCode", "LocomotiveType",
                                                    "DriverId", "ViceDriverId", "StudentId", "Location", "FaultLocation", "FaultReason", "Responsibility", "IsUploaded"},
                                            new Object[]{instrutorId, machine_takedate.getText().toString(),
                                                    machine_TrainCode.getText().toString(), machine_LocomotiveType.getText().toString(),
                                                    maindriverid, auxdriverid, studriverid, machine_AttendTime.getText().toString(),
                                                    machine_EndAttendTime.getText().toString(), daily_machine_aim.getText().toString(),
                                                    daily_machine_problem.getText().toString(), uploadedid});

                                    List<Map> mapListid = dbOpenhelper.queryListMap("select * from InstructorRepair where InstructorId=?", new String[]{instrutorId});
                                    if (mapListid.size() != 0) {
                                        final String upid = mapListid.get(mapListid.size() - 1).get("Id") + "";

                                        Myapplilcation.getExecutorService().execute(new Runnable() {
                                            @Override
                                            public void run() {
                                                NetUtils.updataarguments3dingle(dbOpenhelper, systemConfig, "InstructorRepair", upid);
                                            }
                                        });
                                    }
                                } else {
                                    dbOpenhelper.update("InstructorRepair",
                                            new String[]{"InstructorId", "HappenTime", "TrainCode", "LocomotiveType",
                                                    "DriverId", "ViceDriverId", "StudentId", "Location", "FaultLocation", "FaultReason", "Responsibility", "IsUploaded"},
                                            new Object[]{instrutorId, machine_takedate.getText().toString(),
                                                    machine_TrainCode.getText().toString(), machine_LocomotiveType.getText().toString(),
                                                    maindriverid, auxdriverid, studriverid, machine_AttendTime.getText().toString(),
                                                    machine_EndAttendTime.getText().toString(), daily_machine_aim.getText().toString(),
                                                    daily_machine_problem.getText().toString(), uploadedid}, new String[]{"Id"}, new String[]{intentid + ""});

                                    Myapplilcation.getExecutorService().execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            NetUtils.updataarguments3dingle(dbOpenhelper, systemConfig, "InstructorRepair", intentid + "");
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

    //设置输入框
    @Override
    public void onBackPressed() {
        if (mach_popuwindow.getVisibility() != View.GONE) {
            mach_popuwindow.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }

    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("MachineDetails Page")
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Myapplilcation.removeActivity(this);
    }
}
