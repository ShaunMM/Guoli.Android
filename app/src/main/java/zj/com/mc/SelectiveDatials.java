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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
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
 * 抽查信息单 ---详情
 */
public class SelectiveDatials extends Activity implements View.OnClickListener {
    //1日期，4开始时间，5结束时间，6问题个数，7抽查内容，8发现问题，9处理意见
    private int SAVECONTER = 0;
    private int intentid;
    private List<TextView> viewList;
    private List<EditText> edList;
    private RelativeLayout selected_popuwindow;
    private Spinner selectdetails_LocomotiveType;

    private EditText daily_selectdetails_aim, daily_selectdetails_problem, daily_selectdetails_dopinion, selectdetails_promcount, selected_key;
    private TextView select_title2, selectdetails_takedate, selectdetails_TrainCode,
            selectdetails_time1, selectdetails_time2, whichtv;//标题
    private String key, spinnertext;
    private ListView selected_poplist;
    private DBOpenHelper dbOpenHelper;
    private List<Map> listsearch, list;
    private String[] mItems = {"测查", "坐岗", "其他"};
    private String personID;
    private String isUploaded = "2";
    private ISystemConfig systemConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dailyselectlistingitem);
        Myapplilcation.addActivity(this);

        initview();
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("InstructorCheck");
        intentid = bundle.getInt("listitemId");
        listsearch = new ArrayList<>();
        list = new ArrayList<>();
        if (intentid != -1) {
            isUploaded = bundle.getString("IsUploaded");
            String sql = "select * from InstructorCheck where Id  =? ";
            list = dbOpenHelper.queryListMap(sql, new String[]{intentid + ""});
            SAVECONTER = (Integer) list.get(0).get("IsUploaded");
            initviewtext();
        }

        initviewspinner();
        if (isUploaded.equals("1")) {
            daily_selectdetails_aim.setEnabled(false);
            daily_selectdetails_problem.setEnabled(false);
            daily_selectdetails_dopinion.setEnabled(false);
            selectdetails_promcount.setEnabled(false);
            selected_key.setEnabled(false);
        }
    }

    private void initviewtext() {
        Map map = list.get(0);
        if (map.get("StartTime").toString().length() > 11) {
            selectdetails_takedate.setText(map.get("StartTime").toString().split(" ")[0]);
            selectdetails_time1.setText((map.get("StartTime").toString().split(" ")[1]));
            selectdetails_time2.setText((map.get("EndTime").toString().toString().split(" ")[1]));
        } else if (map.get("StartTime").toString().length() > 1 && map.get("StartTime").toString().length() <= 11) {
            selectdetails_takedate.setText((map.get("StartTime").toString()));
            selectdetails_time1.setText("");
            selectdetails_time2.setText("");
        } else {
            selectdetails_takedate.setText("");
            selectdetails_time1.setText("");
            selectdetails_time2.setText("");
        }
        selectdetails_TrainCode.setText(map.get("Location").toString());
        selectdetails_promcount.setText(map.get("ProblemCount").toString());
        daily_selectdetails_aim.setText(map.get("CheckContent").toString());
        daily_selectdetails_problem.setText(map.get("Problems").toString());
        daily_selectdetails_dopinion.setText(map.get("Suggests").toString());
        String str = list.get(0).get("CheckType").toString();
        for (int i = 0; i < mItems.length; i++) {
            if (str.equals(mItems[i])) {
                selectdetails_LocomotiveType.setSelection(i, true);
            }
        }
    }

    private void initviewspinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectdetails_LocomotiveType.setAdapter(adapter);
        selectdetails_LocomotiveType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                spinnertext = mItems[pos];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void initview() {
        dbOpenHelper = DBOpenHelper.getInstance(getApplicationContext());
        systemConfig = SystemConfigFactory.getInstance(this).getSystemConfig();
        personID = systemConfig.getUserId();

        selectdetails_LocomotiveType = (Spinner) findViewById(R.id.selectdetails_LocomotiveType);
        selected_popuwindow = (RelativeLayout) findViewById(R.id.selected_popuwindow);
        selected_popuwindow.setOnClickListener(this);
        selected_popuwindow.setVisibility(View.GONE);

        select_title2 = (TextView) findViewById(R.id.select_title2);
        select_title2.setText("抽查详细单");

        selectdetails_takedate = (TextView) findViewById(R.id.selectdetails_takedate);
        selectdetails_takedate.setOnClickListener(this);//日期
        selectdetails_TrainCode = (TextView) findViewById(R.id.selectdetails_address);//抽查地点
        selectdetails_TrainCode.setOnClickListener(this);
        selectdetails_time1 = (TextView) findViewById(R.id.selectdetails_time1);//抽查时间（起）
        selectdetails_time1.setOnClickListener(this);
        selectdetails_time2 = (TextView) findViewById(R.id.selectdetails_time2);//抽查时间（止）
        selectdetails_time2.setOnClickListener(this);
        selectdetails_promcount = (EditText) findViewById(R.id.selectdetails_promcount);//发现问题个数

        daily_selectdetails_aim = (EditText) findViewById(R.id.daily_selectdetails_aim);//抽查内容
        daily_selectdetails_problem = (EditText) findViewById(R.id.daily_selectdetails_problem);//发现问题
        daily_selectdetails_dopinion = (EditText) findViewById(R.id.daily_selectdetails_dopinion);//处理意见
        selected_key = (EditText) findViewById(R.id.selected_key);
        selected_poplist = (ListView) findViewById(R.id.selected_poplist);

        selectdetails_promcount.setOnClickListener(this);
        daily_selectdetails_aim.setOnClickListener(this);
        daily_selectdetails_problem.setOnClickListener(this);
        daily_selectdetails_dopinion.setOnClickListener(this);

        findViewById(R.id.daily_selectdetails_save).setOnClickListener(this);
        findViewById(R.id.daily_selectdetails_saveup).setOnClickListener(this);
        findViewById(R.id.daily_selectdetails_cancle).setOnClickListener(this);
        findViewById(R.id.selected_popuwindow_yes).setOnClickListener(this);

        viewList = new ArrayList<TextView>();
        viewList.add(selectdetails_takedate);
        viewList.add(selectdetails_TrainCode);
        viewList.add(selectdetails_time1);
        viewList.add(selectdetails_time2);
        viewList.add(selectdetails_promcount);
        viewList.add(daily_selectdetails_aim);
        viewList.add(daily_selectdetails_problem);
        viewList.add(daily_selectdetails_dopinion);

        edList = new ArrayList<EditText>();
        edList.add(selectdetails_promcount);
        findViewById(R.id.daily_selected_titleback).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (SAVECONTER == 0) {
            switch (view.getId()) {
                case R.id.daily_selected_titleback:
                    UtilisClass.hidInputMethodManager(SelectiveDatials.this, selectdetails_promcount);
                    finish();
                    break;
                case R.id.selectdetails_takedate:
                    UtilisClass.getdatepicker(SelectiveDatials.this, selectdetails_takedate);
                    break;
                case R.id.selectdetails_address:
                    selected_popuwindow.setVisibility(View.VISIBLE);
                    getsearch(selectdetails_TrainCode, "BaseStation");
                    break;
                case R.id.selectdetails_time1:
                    //开始时间
                    UtilisClass.gettimepicker(SelectiveDatials.this, selectdetails_time1);
                    break;
                case R.id.selectdetails_time2:
                    //结束时间
                    UtilisClass.gettimepicker(SelectiveDatials.this, selectdetails_time2);
                    break;
                case R.id.selected_popuwindow_yes:
                    //popwindow确定
                    selected_popuwindow.setVisibility(View.GONE);
                    whichtv.setText(key);
                    break;
                case R.id.selected_popuwindow:
                    selected_popuwindow.setVisibility(View.GONE);
                    break;
                case R.id.daily_selectdetails_save:
                    //保存到本地
                    getisempty(0);
                    UtilisClass.hidInputMethodManager(SelectiveDatials.this, selectdetails_promcount);
                    UtilisClass.showToast(this, "保存成功！");
                    finish();
                    break;
                case R.id.daily_selectdetails_saveup:
                    //保存并上传
                    getisempty2(viewList, edList, 0);
                    UtilisClass.hidInputMethodManager(SelectiveDatials.this, selectdetails_promcount);
                    break;
                case R.id.daily_selectdetails_cancle:
                    UtilisClass.hidInputMethodManager(SelectiveDatials.this, selectdetails_promcount);
                    finish();
                    break;
            }
        } else {
            switch (view.getId()) {
                case R.id.daily_selected_titleback:
                    UtilisClass.hidInputMethodManager(SelectiveDatials.this, selectdetails_promcount);
                    finish();
                    break;
                case R.id.daily_selectdetails_cancle:
                    UtilisClass.hidInputMethodManager(SelectiveDatials.this, selectdetails_promcount);
                    finish();
                    break;
            }
        }
    }

    private void getsearch(final TextView ed, final String tablename) {
        selected_key.setText("");
        selected_key.setFocusable(true);
        selected_key.setFocusableInTouchMode(true);
        selected_key.requestFocus();
        InputMethodManager inputManager =
                (InputMethodManager) selected_key.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(selected_key, 0);
        whichtv = ed;
        selected_key.setHint("支持站名，拼音");

        selected_key.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                key = selected_key.getText().toString();
                if (!key.equals("")) {
                    //获取数据
                    listsearch = getDBpersonname("BaseStation", key);
                    if (listsearch.size() != 0) {
                        selected_poplist.setAdapter(new CommonAdapter<Map>(SelectiveDatials.this, listsearch, R.layout.editlist1) {

                            @Override
                            protected void convertlistener(ViewHolder holder, final Map map) {
                            }

                            @Override
                            public void convert(ViewHolder holder, Map map) {
                                holder.setText(R.id.editlist_item1, map.get("StationName").toString());
                                holder.setText(R.id.editlist_item2, map.get("Spell").toString());
                            }
                        });

                        selected_poplist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                if (listsearch.size() >= i) {
                                    selected_key.setText(listsearch.get(i).get("StationName").toString());
                                    selected_popuwindow.setVisibility(View.GONE);
                                    whichtv.setText(key);
                                }
                            }
                        });

                    } else {
                        showToast("没有相关数据");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    private List<Map> getDBpersonname(String tablename, String coads) {

        String sql = "select * from BaseStation" +
                " where StationName like ? or Spell like ?";
        String[] selectionArgs = new String[]{"%" + coads + "%",
                "%" + coads + "%"};
        List<Map> listsearch = dbOpenHelper.queryListMap(sql, selectionArgs);

        return listsearch;
    }

    @Override
    public void onBackPressed() {
        if (selected_popuwindow.getVisibility() != View.GONE) {
            selected_popuwindow.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }

    private void getisempty(int uploadedid) {
        if (intentid == -1) {
            dbOpenHelper.insert("InstructorCheck", new String[]{"InstructorId", "StartTime", "EndTime",
                            "Location", "CheckType", "ProblemCount", "CheckContent", "Problems", "Suggests", "IsUploaded", "RecirdTime"},
                    new Object[]{personID, selectdetails_takedate.getText().toString() + " " + selectdetails_time1.getText().toString(),
                            selectdetails_takedate.getText().toString() + " " + selectdetails_time2.getText().toString(), selectdetails_TrainCode.getText().toString(),
                            spinnertext, selectdetails_promcount.getText().toString(), daily_selectdetails_aim.getText().toString(),
                            daily_selectdetails_problem.getText().toString(), daily_selectdetails_dopinion.getText().toString(), uploadedid, 0});
            Isrecodetime();
        } else {
            dbOpenHelper.update("InstructorCheck",
                    new String[]{"InstructorId", "StartTime", "EndTime",
                            "Location", "CheckType", "ProblemCount", "CheckContent", "Problems", "Suggests", "IsUploaded"},
                    new Object[]{personID, selectdetails_takedate.getText().toString() + " " + selectdetails_time1.getText().toString(),
                            selectdetails_takedate.getText().toString() + " " + selectdetails_time2.getText().toString(), selectdetails_TrainCode.getText().toString(),
                            spinnertext, selectdetails_promcount.getText().toString(), daily_selectdetails_aim.getText().toString(),
                            daily_selectdetails_problem.getText().toString(), daily_selectdetails_dopinion.getText().toString(), uploadedid},
                    new String[]{"Id"}, new String[]{intentid + ""});
        }
    }

    private void getisempty2(List<TextView> viewList, List<EditText> edList, int uploadedid) {
        aa:
        for (int i = 0; i < viewList.size(); i++) {

            if (viewList.get(i).getText().toString().equals("")) {
                UtilisClass.showToast(SelectiveDatials.this, getResources().getString(R.string.perfectInformation));
                break aa;
            } else {
                if (i == viewList.size() - 1) {
                    for (int j = 0; j < edList.size(); j++) {
                        if (edList.get(j).getText().toString().equals("")) {
                            UtilisClass.showToast(SelectiveDatials.this, getResources().getString(R.string.perfectInformation));
                            break aa;
                        } else {
                            if (j == edList.size() - 1) {
                                if (intentid == -1) {
                                    dbOpenHelper.insert("InstructorCheck", new String[]{"InstructorId", "StartTime", "EndTime",
                                                    "Location", "CheckType", "ProblemCount", "CheckContent", "Problems", "Suggests", "IsUploaded", "RecirdTime"},
                                            new Object[]{personID, selectdetails_takedate.getText().toString() + " " + selectdetails_time1.getText().toString(),
                                                    selectdetails_takedate.getText().toString() + " " + selectdetails_time2.getText().toString(), selectdetails_TrainCode.getText().toString(),
                                                    spinnertext, selectdetails_promcount.getText().toString(), daily_selectdetails_aim.getText().toString(),
                                                    daily_selectdetails_problem.getText().toString(), daily_selectdetails_dopinion.getText().toString(), uploadedid, 1});
                                    Isrecodetime();
                                    List<Map> mapListid = dbOpenHelper.queryListMap("select * from InstructorCheck where InstructorId=?", new String[]{personID});
                                    if (mapListid.size() != 0) {
                                        final String upid = mapListid.get(mapListid.size() - 1).get("Id") + "";

                                        Myapplilcation.getExecutorService().execute(new Runnable() {
                                            @Override
                                            public void run() {
                                                NetUtils.updataarguments3dingle(dbOpenHelper, systemConfig, "InstructorCheck", upid);
                                            }
                                        });
                                    }

                                } else {
                                    dbOpenHelper.update("InstructorCheck",
                                            new String[]{"InstructorId", "StartTime", "EndTime",
                                                    "Location", "CheckType", "ProblemCount", "CheckContent", "Problems", "Suggests", "IsUploaded"},
                                            new Object[]{personID, selectdetails_takedate.getText().toString() + " " + selectdetails_time1.getText().toString(),
                                                    selectdetails_takedate.getText().toString() + " " + selectdetails_time2.getText().toString(), selectdetails_TrainCode.getText().toString(),
                                                    spinnertext, selectdetails_promcount.getText().toString(), daily_selectdetails_aim.getText().toString(),
                                                    daily_selectdetails_problem.getText().toString(), daily_selectdetails_dopinion.getText().toString(), uploadedid},
                                            new String[]{"Id"}, new String[]{intentid + ""});

                                    Myapplilcation.getExecutorService().execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            NetUtils.updataarguments3dingle(dbOpenHelper, systemConfig, "InstructorCheck", intentid + "");
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

    //检测次数
    private void Isrecodetime() {

        String data = UtilisClass.getStringDate2();
        String data2 = UtilisClass.getStringDate();

        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;

        if (intentid == -1) {
            //第一次添加
            int quotaid = UtilisClass.getMONTHSELECTIMEID();
            List<Map> list = dbOpenHelper.queryListMap("select * from InstructorQuotaRecord where InstructorId=? and QuotaId=? and UpdateTime like ?", new String[]{personID, quotaid + "", data + "%"});
            if (list.size() == 0) {
                dbOpenHelper.insert("InstructorQuotaRecord", new String[]{
                        "InstructorId", "QuotaId", "FinishedAmmount", "UpdateTime", "Year", "Month"
                }, new Object[]{personID, quotaid, 1, data2, year, month});
            } else {
                int num = Integer.parseInt(list.get(0).get("FinishedAmmount") + "");
                num = num + 1;
                String id = list.get(0).get("Id") + "";
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