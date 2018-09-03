package zj.com.mc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
 * 标准化验收---详情
 */
public class StandardizedActivity extends Activity implements View.OnClickListener {

    private int SAVECONTER = 0;
    private int intentid;
    private ListView accept_poplist;
    private List<TextView> viewList;
    private List<EditText> edList;
    private TextView accept_takedate,
            accept_DriverId, accept_ViceDriverId, accept_StudentId;
    private DBOpenHelper dbopenhelper;
    private RelativeLayout accept_popuwindow;
    EditText acceptedit_key, accept_LocomotiveType, daily_accept_aim, daily_accept_problem,
            accept_EndAttendTime;
    private TextView whichtv;//中转
    private String personID;
    private String key;
    private String personid1, personid2;//记录员工ID
    private List<Map> listsearch, list;
    private String isUploaded = "2";
    private ISystemConfig systemConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.standardizedactivity);
        Myapplilcation.addActivity(this);
        initview();
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("InstructorAccept");
        intentid = bundle.getInt("listitemId");

        if (intentid != -1) {
            isUploaded = bundle.getString("IsUploaded");
            String sql = "select * from InstructorAccept" + " where Id  =? ";
            String[] selectionArgs = new String[]{intentid + ""};
            list = dbopenhelper.queryListMap(sql, selectionArgs);
            System.out.println(String.valueOf(list));
            SAVECONTER = (int) list.get(0).get("IsUploaded");
            initviewtext();
        }

        if (isUploaded.equals("1")) {
            acceptedit_key.setEnabled(false);
            accept_LocomotiveType.setEnabled(false);
            daily_accept_aim.setEnabled(false);
            daily_accept_problem.setEnabled(false);
            accept_EndAttendTime.setEnabled(false);
        }
    }

    private void initviewtext() {
        accept_takedate.setText(list.get(0).get("AcceptDate").toString());

        personid1 = list.get(0).get("DriverId").toString();
        List<Map> maindriverinfo = dbopenhelper.queryListMap("select * from PersonInfo where Id=?", new String[]{personid1});
        if (maindriverinfo.size() != 0) {
            accept_DriverId.setText(maindriverinfo.get(0).get("Name").toString());
        }
        personid2 = list.get(0).get("ViceDriverId").toString();
        List<Map> auxdriverinfo = dbopenhelper.queryListMap("select * from PersonInfo where Id= ?", new String[]{personid2});
        if (auxdriverinfo.size() != 0) {
            accept_ViceDriverId.setText(auxdriverinfo.get(0).get("Name").toString());
        }
        List<Map> studriverinfo = dbopenhelper.queryListMap("select * from PersonInfo where Id= ?", new String[]{personID});
        if (studriverinfo.size() != 0) {
            accept_StudentId.setText(studriverinfo.get(0).get("Name").toString());
        }
        accept_LocomotiveType.setText(list.get(0).get("DriverScore").toString());
        accept_EndAttendTime.setText(list.get(0).get("ViceDriverScore").toString());
        daily_accept_aim.setText(list.get(0).get("Problems").toString());
        daily_accept_problem.setText(list.get(0).get("Suggests").toString());
    }

    private void initview() {
        dbopenhelper = DBOpenHelper.getInstance(getApplicationContext());
        systemConfig = SystemConfigFactory.getInstance(this).getSystemConfig();
        personID = systemConfig.getUserId();

        findViewById(R.id.accept_titleback).setOnClickListener(this);//返回
        accept_popuwindow = (RelativeLayout) findViewById(R.id.accept_popuwindow);//popwindow
        accept_popuwindow.setOnClickListener(this);

        accept_takedate = (TextView) findViewById(R.id.accept_takedate);//发生日期
        accept_takedate.setOnClickListener(this);

        accept_DriverId = (TextView) findViewById(R.id.accept_DriverId);//主班司机
        accept_DriverId.setOnClickListener(this);
        accept_ViceDriverId = (TextView) findViewById(R.id.accept_ViceDriverId);//副班司机
        accept_ViceDriverId.setOnClickListener(this);
        accept_StudentId = (TextView) findViewById(R.id.accept_StudentId);//验收人

        accept_EndAttendTime = (EditText) findViewById(R.id.accept_EndAttendTime);//副司机得分
        accept_LocomotiveType = (EditText) findViewById(R.id.accept_LocomotiveType);//司机得分
        daily_accept_aim = (EditText) findViewById(R.id.daily_accept_aim);//主要原因
        daily_accept_problem = (EditText) findViewById(R.id.daily_accept_problem);//指导意见
        acceptedit_key = (EditText) findViewById(R.id.acceptedit_key);//popwindow  editext

        findViewById(R.id.accept_popuwindow_yes).setOnClickListener(this);//popwindow 确定键

        accept_poplist = (ListView) findViewById(R.id.accept_poplist);//pop listview
        findViewById(R.id.daily_accept_save).setOnClickListener(this);
        findViewById(R.id.daily_accept_saveup).setOnClickListener(this);
        findViewById(R.id.daily_accept_cancle).setOnClickListener(this);

        viewList = new ArrayList<TextView>();
        viewList.add(accept_takedate);
        viewList.add(accept_DriverId);
        viewList.add(accept_ViceDriverId);
        edList = new ArrayList<EditText>();
        edList.add(accept_LocomotiveType);
        edList.add(accept_EndAttendTime);

        List<Map> maindriverinfo = dbopenhelper.queryListMap("select * from PersonInfo where Id=?", new String[]{personID});
        if (maindriverinfo.size() != 0) {
            accept_StudentId.setText(maindriverinfo.get(0).get("Name").toString());
        }
    }

    @Override
    public void onClick(View view) {
        if (SAVECONTER == 0) {
            switch (view.getId()) {
                case R.id.accept_titleback:
                    UtilisClass.hidInputMethodManager(StandardizedActivity.this, accept_LocomotiveType);
                    finish();
                    break;
                case R.id.accept_takedate:
                    UtilisClass.getdatepicker(StandardizedActivity.this, accept_takedate);
                    break;
                case R.id.accept_popuwindow:
                    accept_popuwindow.setVisibility(View.GONE);
                    break;
                case R.id.accept_popuwindow_yes:
                    //popwindow确定
                    whichtv.setText(acceptedit_key.getText().toString());
                    accept_popuwindow.setVisibility(View.GONE);
                    UtilisClass.setDriverid(StandardizedActivity.this, whichtv, accept_DriverId, personid1);
                    UtilisClass.setDriverid(StandardizedActivity.this, whichtv, accept_ViceDriverId, personid2);
                    break;
                case R.id.accept_DriverId:
                    //主班司机
                    accept_popuwindow.setVisibility(View.VISIBLE);
                    getsearch(accept_DriverId, "ViewPersonInfo");
                    break;
                case R.id.accept_ViceDriverId:
                    //副班司机
                    accept_popuwindow.setVisibility(View.VISIBLE);
                    getsearch(accept_ViceDriverId, "ViewPersonInfo");
                    break;
                case R.id.daily_accept_save:
                    //保存到本地
                    if (SAVECONTER == 0) {
                        getisempty(0);
                        UtilisClass.showToast(StandardizedActivity.this, "保存成功！");
                        finish();
                    } else {
                        UtilisClass.showToast(StandardizedActivity.this, "保存失败！");
                    }
                    break;
                case R.id.daily_accept_saveup:
                    //保存并上传
                    if (SAVECONTER == 1) {
                        getisempty2(viewList, edList, 1);
                        //执行上传代码
                        UtilisClass.hidInputMethodManager(StandardizedActivity.this, accept_LocomotiveType);
                        SAVECONTER = 0;
                    } else {
                        UtilisClass.showToast(StandardizedActivity.this, "保存失败，文件已上传！");
                    }
                    break;
                case R.id.daily_accept_cancle:
                    UtilisClass.hidInputMethodManager(StandardizedActivity.this, accept_LocomotiveType);
                    finish();
                    break;
            }
        } else {
            switch (view.getId()) {
                case R.id.accept_titleback:
                    UtilisClass.hidInputMethodManager(StandardizedActivity.this, accept_LocomotiveType);
                    finish();
                    break;
                case R.id.daily_accept_cancle:
                    UtilisClass.hidInputMethodManager(StandardizedActivity.this, accept_LocomotiveType);
                    finish();
                    break;
            }
        }
    }

    private void getsearch(final TextView ed, final String tablename) {

        acceptedit_key.setText("");
        acceptedit_key.setFocusable(true);
        acceptedit_key.setFocusableInTouchMode(true);
        acceptedit_key.requestFocus();
        InputMethodManager inputManager =
                (InputMethodManager) acceptedit_key.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(acceptedit_key, 0);

        whichtv = ed;
        acceptedit_key.setHint("支持工号，姓名，拼音简称搜索");
        acceptedit_key.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                UtilisClass.setdriverid2(whichtv, accept_DriverId, personid1);
                UtilisClass.setdriverid2(whichtv, accept_ViceDriverId, personid2);

                key = acceptedit_key.getText().toString();
                if (key != null && !"".equals(key.trim())) {
                    listsearch = getDBpersonname("ViewPersonInfo", key);
                    accept_poplist.setAdapter(new CommonAdapter<Map>(StandardizedActivity.this, listsearch, R.layout.editlist1) {

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
                                holder.setText(R.id.editlist_item1, map.get("StationName").toString());
                                holder.setText(R.id.editlist_item2, map.get("Spell").toString());
                            }
                        }
                    });
                    accept_poplist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            if (tablename.equals("ViewPersonInfo")) {
                                acceptedit_key.setText(listsearch.get(i).get("Name").toString());
                                if (ed == accept_DriverId) {
                                    personid1 = listsearch.get(0).get("Id") + "";
                                    System.out.println(personid1);
                                } else if (ed == accept_ViceDriverId) {
                                    personid2 = listsearch.get(0).get("Id") + "";
                                    System.out.println(personid2);
                                }
                            } else {
                                acceptedit_key.setText(listsearch.get(i).get("StationName").toString());
                            }

                            whichtv.setText(acceptedit_key.getText().toString());
                            accept_popuwindow.setVisibility(View.GONE);
                        }
                    });
                } else {
                    accept_poplist.setAdapter(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private List<Map> getDBpersonname(String tablename, String coads) {

        String sql = "select * from " + tablename +
                " where WorkNo like ? or Name like ? or Spell like ? ";
        String[] selectionArgs = new String[]{"%" + coads + "%",
                "%" + coads + "%",
                "%" + coads + "%"};
        List<Map> listsearch = dbopenhelper.queryListMap(sql, selectionArgs);

        return listsearch;
    }

    private void getisempty(int uploadedid) {

        if (intentid == -1) {
            dbopenhelper.insert("InstructorAccept", new String[]{"InstructorId", "AcceptDate", "DriverId", "DriverScore",
                            "ViceDriverId", "ViceDriverScore", "Problems", "Suggests", "IsUploaded"},
                    new Object[]{personID, accept_takedate.getText().toString(),
                            personid1, accept_LocomotiveType.getText().toString(), personid2,
                            accept_EndAttendTime.getText().toString(), daily_accept_aim.getText().toString(),
                            daily_accept_problem.getText().toString(),
                            uploadedid});
        } else {
            dbopenhelper.update("InstructorAccept",
                    new String[]{"InstructorId", "AcceptDate", "DriverId", "DriverScore",
                            "ViceDriverId", "ViceDriverScore", "Problems", "Suggests", "IsUploaded"},
                    new Object[]{personID, accept_takedate.getText().toString(),
                            personid1, accept_LocomotiveType.getText().toString(), personid2,
                            accept_EndAttendTime.getText().toString(), daily_accept_aim.getText().toString(),
                            daily_accept_problem.getText().toString(),
                            uploadedid}, new String[]{"Id"}, new String[]{intentid + ""});
        }
    }

    private void getisempty2(List<TextView> viewList, List<EditText> edList, int uploadedid) {

        aa:
        for (int i = 0; i < viewList.size(); i++) {

            if (viewList.get(i).getText().toString().equals("")) {
                UtilisClass.showToast(StandardizedActivity.this, getResources().getString(R.string.perfectInformation));
                break aa;
            } else {
                if (i == viewList.size() - 1) {
                    for (int j = 0; j < edList.size(); j++) {
                        if (edList.get(j).getText().toString().equals("")) {
                            UtilisClass.showToast(StandardizedActivity.this, getResources().getString(R.string.perfectInformation));
                            break aa;
                        } else {
                            if (j == edList.size() - 1) {
                                if (intentid == -1) {
                                    dbopenhelper.insert("InstructorAccept", new String[]{"InstructorId", "AcceptDate", "DriverId", "DriverScore",
                                                    "ViceDriverId", "ViceDriverScore", "Problems", "Suggests", "IsUploaded"},
                                            new Object[]{personID, accept_takedate.getText().toString(),
                                                    personid1, accept_LocomotiveType.getText().toString(), personid2,
                                                    accept_EndAttendTime.getText().toString(), daily_accept_aim.getText().toString(),
                                                    daily_accept_problem.getText().toString(),
                                                    uploadedid});
                                    List<Map> mapListid = dbopenhelper.queryListMap("select * from InstructorAccept where InstructorId=?", new String[]{personID});
                                    if (mapListid.size() != 0) {
                                        final String upid = mapListid.get(mapListid.size() - 1).get("Id") + "";

                                        Myapplilcation.getExecutorService().execute(new Runnable() {
                                            @Override
                                            public void run() {
                                                NetUtils.updataarguments3dingle(dbopenhelper, systemConfig, "InstructorAccept", upid);
                                            }
                                        });
                                    }

                                } else {
                                    dbopenhelper.update("InstructorAccept",
                                            new String[]{"InstructorId", "AcceptDate", "DriverId", "DriverScore",
                                                    "ViceDriverId", "ViceDriverScore", "Problems", "Suggests", "IsUploaded"},
                                            new Object[]{personID, accept_takedate.getText().toString(),
                                                    personid1, accept_LocomotiveType.getText().toString(), personid2,
                                                    accept_EndAttendTime.getText().toString(), daily_accept_aim.getText().toString(),
                                                    daily_accept_problem.getText().toString(),
                                                    uploadedid}, new String[]{"Id"}, new String[]{intentid + ""});

                                    Myapplilcation.getExecutorService().execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            NetUtils.updataarguments3dingle(dbopenhelper, systemConfig, "InstructorAccept", intentid + "");
                                        }
                                    });
                                }
                            }
                        }

                    }
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (accept_popuwindow.getVisibility() != View.GONE) {
            accept_popuwindow.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Myapplilcation.removeActivity(this);
    }
}