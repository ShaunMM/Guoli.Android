package zj.com.mc;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
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

/**
 * Created by dell on 2016/8/3.
 */
public class AddRemoveKeyperson extends Activity implements View.OnClickListener{
//解除关键人
    private String personid2;//记录员工id
    private DBOpenHelper dbOpenHelper;
    private List<TextView>viewList;
    private List<EditText> edList;
    private ListView listView;
    private int intentid;//判断跳转码
    private TextView whichtv;//中转
    private TextView takedate,attendtime,endattendtime,removedata;
    private TextView addremovekp_TrainCode;
    private TextView daily_addremovekp_title;
    private EditText carcount,wholeweight,length,addremovekp_LocomotiveType,daily_addremovekp_problem1,daily_addremovekp_dopinion1;
    private EditText daily_addremovekp_aim,daily_addremovekp_problem,daily_addremovekp_dopinion,daily_addremovekp_aim1,daily_addremovekp_dopinion3,daily_addremovekp_dopinion4;
    private RelativeLayout addremovekp_popuwindow;
    private LinearLayout daily_addremovekp_titleback;
    private String key;//弹窗内edittext中的文字
    private String  clouns="TrainCode";//关键字和列明
    private String table1="ViewPersonInfo";//需要查询的表名
    private EditText txtkey;
    private List<Map> listsearch;
    private List<Map>addremovelist,MineInfo;
    private String mineworkno;
    private String logInpersonId;
    private Button bsave,remove,bsaveUp,bcancle;
    private String Tableid;
    private int keylisthistoryid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addremovekeyperson);
        dbOpenHelper= DBOpenHelper.getInstance(getApplicationContext());
        initview();
        SharedPreferences  sharedPreferences=getSharedPreferences("PersonInfo",MODE_PRIVATE);
        mineworkno=sharedPreferences.getString("WorkNo",null);
        logInpersonId=sharedPreferences.getString("PersonId",null);

        MineInfo=new ArrayList<Map>();
        MineInfo=dbOpenHelper.queryListMap("select * from PersonInfo where WorkNo=?",new String[]{mineworkno});
        if (MineInfo.size()!=0) {
            addremovekp_LocomotiveType.setText(MineInfo.get(0).get("Name") + "");
        }
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("InstructorKeyPerson");
        keylisthistoryid=bundle.getInt("listitemId");

        if (keylisthistoryid==-1) {

            addremovelist = dbOpenHelper.queryListMap("select * from InstructorKeyPerson where InstructorId=? and IsRemoved=?", new String[]{logInpersonId, "false"});
            if (addremovelist.size() == 0) {
                intentid = 1;
            } else {
                intentid = 0;
            }

            if (intentid == 1) {
                Toast.makeText(this, "空的", Toast.LENGTH_SHORT).show();
            } else {
                Tableid = addremovelist.get(0).get("Id") + "";
//                UtilisClass.showToast(AddRemoveKeyperson.this, "有参数的");

                initlistdata(addremovelist);


            }
        }else {
            addremovelist = dbOpenHelper.queryListMap("select * from InstructorKeyPerson where Id=?", new String[]{keylisthistoryid+""});
//            addremovelist = dbOpenHelper.queryListMap("select * from InstructorKeyPerson ",null);
            System.out.println(">>>>>>>>>>"+addremovelist.toString());
            initlistdata(addremovelist);
        }
    }

    private void initlistdata(List<Map> list){
        if (list.size() != 0) {
            String keyperson = "";
            String personname = "";
            personid2=list.get(0).get("KeyPersonId") + "";
            keyperson = UtilisClass.getName(dbOpenHelper, personid2);
            String removeda = list.get(0).get("ActualRemoveTime") + "";
            if (!removeda.equals("null")) {
                removedata.setText(list.get(0).get("ActualRemoveTime") + "");
            } else {
                removedata.setText("");
            }
            takedate.setText(list.get(0).get("ConfirmDate") + "");
            attendtime.setText(list.get(0).get("ExpectRemoveTime") + "");
            endattendtime.setText(list.get(0).get("KeyLocation") + "");
            addremovekp_TrainCode.setText(keyperson);
            daily_addremovekp_problem1.setText(list.get(0).get("HelpMethod") + "");
            daily_addremovekp_dopinion1.setText(list.get(0).get("Changes") + "");
            daily_addremovekp_aim1.setText(list.get(0).get("LocationConfirmReason") + "");
            daily_addremovekp_aim.setText(list.get(0).get("PersonRemoveSuggests") + "");
            daily_addremovekp_problem.setText(list.get(0).get("LocationConfirmReason") + "");
            daily_addremovekp_dopinion.setText(list.get(0).get("ControlMethod") + "");
            daily_addremovekp_dopinion3.setText(list.get(0).get("ActualControl") + "");
            daily_addremovekp_dopinion4.setText(list.get(0).get("LocationRemoveSuggests") + "");

        } else {
        }
    }

    private void initview() {
        daily_addremovekp_title= (TextView) findViewById(R.id.daily_addremovekp_title2);//title
        takedate = (TextView) findViewById(R.id.addremovekp_takedate);//确定日期
        attendtime= (TextView) findViewById(R.id.addremovekp_AttendTime);//预计解除日期
        endattendtime= (TextView) findViewById(R.id.addremovekp_EndAttendTime);//关键点
        addremovekp_LocomotiveType= (EditText) findViewById(R.id.addremovekp_LocomotiveType);//包保人
        addremovekp_TrainCode= (TextView) findViewById(R.id.addremovekp_TrainCode);//关键人
        takedate.setOnClickListener(this);
        attendtime.setOnClickListener(this);
        endattendtime.setOnClickListener(this);
        addremovekp_LocomotiveType.setOnClickListener(this);
        addremovekp_TrainCode.setOnClickListener(this);
        removedata= (TextView) findViewById(R.id.daily_addremovekp_removedata);
        removedata.setOnClickListener(this);

        daily_addremovekp_titleback= (LinearLayout) findViewById(R.id.daily_addremovekp_titleback);//返回按钮
        daily_addremovekp_titleback.setOnClickListener(this);

        daily_addremovekp_problem1= (EditText) findViewById(R.id.daily_addremovekp_problem1);//帮救措施
        daily_addremovekp_dopinion1= (EditText) findViewById(R.id.daily_addremovekp_dopinion1);//转变情况
        daily_addremovekp_aim1= (EditText) findViewById(R.id.daily_addremovekp_aim1);///关键人确定原因
        daily_addremovekp_aim= (EditText) findViewById(R.id.daily_addremovekp_aim);//关键人解除意见
        daily_addremovekp_problem= (EditText) findViewById(R.id.daily_addremovekp_problem);//关键点确定原因
        daily_addremovekp_dopinion= (EditText) findViewById(R.id.daily_addremovekp_dopinion);//盯控措施
        daily_addremovekp_dopinion3= (EditText) findViewById(R.id.daily_addremovekp_dopinion3);//落实措施
        daily_addremovekp_dopinion4= (EditText) findViewById(R.id.daily_addremovekp_dopinion4);//关键点解除意见


        addremovekp_popuwindow= (RelativeLayout) findViewById(R.id.addremovekp_popuwindow);
        addremovekp_popuwindow.setOnClickListener(this);
        addremovekp_popuwindow.setVisibility(View.GONE);
        findViewById(R.id.addremovekp_popuwindow_yes).setOnClickListener(this);//popwindow确定键


        bsave       = (Button) findViewById(R.id.daily_addremovekp_save);
        remove      =(Button) findViewById(R.id.daily_addremovekp_remove);
        bsaveUp     = (Button) findViewById(R.id.daily_addremovekp_saveup);
        bcancle     = (Button) findViewById(R.id.daily_addremovekp_cancle);
         bsave.setOnClickListener(this);
         remove.setOnClickListener(this);
         bsaveUp.setOnClickListener(this);
         bcancle.setOnClickListener(this);




        txtkey= (EditText) findViewById(R.id.edit_key);
        listView= (ListView) findViewById(R.id.lstv_all);
        if (intentid==0) {
            viewList = new ArrayList<TextView>();
            viewList.add(takedate);
            viewList.add(attendtime);
            viewList.add(endattendtime);
            viewList.add(addremovekp_LocomotiveType);
            viewList.add(addremovekp_TrainCode);

            edList = new ArrayList<EditText>();
            edList.add(daily_addremovekp_problem1);
            edList.add(daily_addremovekp_dopinion1);
            edList.add(daily_addremovekp_aim1);
            edList.add(daily_addremovekp_aim);
            edList.add(daily_addremovekp_problem);
            edList.add(daily_addremovekp_dopinion);
            edList.add(daily_addremovekp_dopinion3);
            edList.add(daily_addremovekp_dopinion4);
        }else if (intentid==1){
            viewList = new ArrayList<TextView>();
            viewList.add(takedate);
            viewList.add(attendtime);
            viewList.add(endattendtime);
            viewList.add(addremovekp_LocomotiveType);
            viewList.add(addremovekp_TrainCode);


            edList = new ArrayList<EditText>();
            daily_addremovekp_problem= (EditText) findViewById(R.id.daily_addremovekp_problem);//关键点确定原因
            daily_addremovekp_problem1= (EditText) findViewById(R.id.daily_addremovekp_problem1);//帮救措施
        }
    }


    private void getsearch(final TextView ed, final String tablename){
        txtkey.setText("");
        txtkey.setFocusable(true);
        txtkey.setFocusableInTouchMode(true);
        txtkey.requestFocus();
        InputMethodManager inputManager =
                (InputMethodManager)txtkey.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(txtkey, 0);


        whichtv=ed;
        if (tablename.equals("ViewPersonInfo")) {
            txtkey.setHint("支持工号，姓名，拼音简称搜索");
        }else {
            txtkey.setHint("支持站名和拼音简称搜索");

        }


        txtkey.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                UtilisClass.setdriverid2(whichtv,addremovekp_TrainCode,personid2);


                key=  txtkey.getText().toString();
                if(key!=null && !"".equals(key.trim())){
                    //获取数据

                    if (tablename.equals("ViewPersonInfo")) {
                        listsearch = getDBpersonname(tablename, key);

                    }else {
                        listsearch = getDBpersonname2(key);

                    }

                    listView.setAdapter(new CommonAdapter<Map>(AddRemoveKeyperson.this,listsearch, R.layout.editlist1) {

                        @Override
                        protected void convertlistener(ViewHolder holder, final Map map) {
                        }

                        @Override
                        public void convert(ViewHolder holder, Map map) {
                            if (tablename.equals("ViewPersonInfo")) {
                                holder.setText(R.id.editlist_item1,map.get("WorkNo").toString());
                                holder.setText(R.id.editlist_item2,map.get("Name").toString());
                                holder.setText(R.id.editlist_item3,map.get("Spell").toString());

                            }else {
                                holder.setText(R.id.editlist_item1,map.get("StationName").toString());
                                holder.setText(R.id.editlist_item2,map.get("Spell").toString());
                            }
                        }
                    });

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                                txtkey.setText(listsearch.get(i).get("Name").toString());
//                                personid=listsearch.get(0).get("Id")+"";

                            if (tablename.equals("ViewPersonInfo")) {
                                txtkey.setText(listsearch.get(i).get("Name").toString());
                                personid2=listsearch.get(0).get("Id")+"";

                            }else {
                                txtkey.setText(listsearch.get(i).get("StationName").toString());
                            }

                            ed.setText(txtkey.getText().toString());
                            addremovekp_popuwindow.setVisibility(View.GONE);

                        }
                    });



                }else{
                    listView.setAdapter(null);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    @Override
    protected void onDestroy() {
        UtilisClass.hidInputMethodManager(AddRemoveKeyperson.this,daily_addremovekp_problem1);

        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        if (keylisthistoryid==-1) {
            switch (view.getId()) {

                case R.id.daily_addremovekp_removedata:

                    getdatepicker(removedata);

                    break;
                case R.id.daily_addremovekp_remove:
 //移除
                    dbOpenHelper.update("InstructorKeyPerson",new String[]{"IsRemoved"},new Object[]{"true"},new String[]{"Id"},new String[]{Tableid});
                    UtilisClass.hidInputMethodManager(AddRemoveKeyperson.this,daily_addremovekp_problem1);

                    finish();
                break;
                case R.id.daily_addremovekp_titleback:
//返回
                    UtilisClass.hidInputMethodManager(AddRemoveKeyperson.this,daily_addremovekp_problem1);

                    finish();
                    break;


                case R.id.addremovekp_takedate:
//添乘日期
                    getdatepicker(takedate);

                    break;
                case R.id.addremovekp_TrainCode:
//关键人

                    addremovekp_popuwindow.setVisibility(View.VISIBLE);
                    getsearch(addremovekp_TrainCode, table1);
                    break;

                case R.id.addremovekp_AttendTime:
//预计借出日期
                    UtilisClass.getdatepicker(AddRemoveKeyperson.this, attendtime);
                    break;


                case R.id.addremovekp_EndAttendTime:
//关键点
                    addremovekp_popuwindow.setVisibility(View.VISIBLE);
                    getsearch(endattendtime, clouns);
                    break;

                case R.id.addremovekp_popuwindow_yes:
//姓名弹窗确定键

                    whichtv.setText(txtkey.getText().toString());
                    addremovekp_popuwindow.setVisibility(View.GONE);
                    UtilisClass.setDriverid(AddRemoveKeyperson.this,whichtv,addremovekp_TrainCode,personid2);
                    break;
                case R.id.addremovekp_popuwindow:
//设置点击边上弹窗消失
                    addremovekp_popuwindow.setVisibility(View.GONE);
                    break;

                case R.id.daily_addremovekp_save:

//保存
                    if (removedata.getText().equals("")){
                        getisempty(viewList, edList, 1);
                        UtilisClass.hidInputMethodManager(AddRemoveKeyperson.this,daily_addremovekp_problem1);

                    }else {
                        setpromptdialog();
//                        getisempty3(viewList, edList, 1);
                    }
                    break;

                case R.id.daily_addremovekp_saveup:
//解除关键人
                        getisempty2(viewList, edList, 1);
                    UtilisClass.hidInputMethodManager(AddRemoveKeyperson.this,daily_addremovekp_problem1);

                    break;

                case R.id.daily_addremovekp_cancle:
//取消
                    UtilisClass.hidInputMethodManager(AddRemoveKeyperson.this,daily_addremovekp_problem1);

//                    showToast("取消");
                    finish();
                    break;
            }

        }else {

            switch (view.getId()) {

                case R.id.daily_addremovekp_titleback:
                    finish();
//返回
                    UtilisClass.hidInputMethodManager(AddRemoveKeyperson.this,daily_addremovekp_problem1);

                    break;

                case R.id.daily_addremovekp_cancle:
//取消
//                    showToast("取消");
                    UtilisClass.hidInputMethodManager(AddRemoveKeyperson.this,daily_addremovekp_problem1);

                    finish();
                    break;
            }
        }
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
    //吐司
    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    //搜索view表中的数据
    public List<Map> getDBpersonname(String tablename, String coads){

        String sql= "select * from " +tablename+
                " where WorkNo like ? or Name like ? or Spell like ? ";
        String [] selectionArgs  = new String[]{"%" + coads + "%",
                "%" + coads + "%",
                "%" + coads + "%"};
        List<Map> listsearch = dbOpenHelper.queryListMap(sql, selectionArgs);

        return  listsearch;
    };
    //操纵区段查询
    public List<Map> getDBpersonname2(String coads){

        String sql= "select * from BaseStation"+
                " where StationName like ? or Spell like ?";
        String [] selectionArgs  = new String[]{"%" + coads + "%",
                "%" + coads + "%"};
        List<Map> listsearch = dbOpenHelper.queryListMap(sql, selectionArgs);

        return  listsearch;
    };
    //设置输入框
    @Override
    public void onBackPressed() {
        if (addremovekp_popuwindow.getVisibility()!=View.GONE){
            addremovekp_popuwindow.setVisibility(View.GONE);
        }else {
            super.onBackPressed();
        }
    }
    //判断view中的内容
    private void getisempty(List<TextView> viewList,List<EditText> edList,int uploadedid) {


        aa:for (int i=0; i<viewList.size();i++) {

            if (viewList.get(i).getText().toString().equals("")){
                showToast("不能为空");

                break aa;
            }else {
                if (i==viewList.size()-1){
                    for (int j=0; j<edList.size(); j++){
                        if (edList.get(j).getText().toString().equals("")){

                            showToast("不能为空"+edList.get(j).toString());
                            break aa;
                        }else {
                            if (j == edList.size() - 1) {
                                if (intentid == 1) {
                                    dbOpenHelper.insert("InstructorKeyPerson", new String[]{"InstructorId", "ConfirmDate", "KeyPersonId", "ExpectRemoveTime",
                                                    "KeyLocation", "PersonConfirmReason", "HelpMethod", "Changes", "PersonRemoveSuggests", "LocationConfirmReason", "ControlMethod",
                                                    "ActualControl",
                                                    "LocationRemoveSuggests", "IsUploaded"},
                                            new Object[]{logInpersonId, takedate.getText().toString(), personid2,
                                                    attendtime.getText().toString(), endattendtime.getText().toString(),
                                                    daily_addremovekp_aim1.getText().toString(), daily_addremovekp_problem1.getText().toString(),
                                                    daily_addremovekp_dopinion1.getText().toString(), daily_addremovekp_aim.getText().toString(), daily_addremovekp_aim1.getText().toString(),
                                                    daily_addremovekp_dopinion.getText().toString(), daily_addremovekp_dopinion3.getText().toString(), daily_addremovekp_dopinion4.getText().toString()
                                                    , uploadedid});


                                } else {

                                    dbOpenHelper.update("InstructorKeyPerson",
                                            new String[]{"InstructorId", "ConfirmDate", "KeyPersonId", "ExpectRemoveTime",
                                                    "KeyLocation", "PersonConfirmReason", "HelpMethod", "Changes", "PersonRemoveSuggests", "LocationConfirmReason", "ControlMethod",
                                                    "ActualControl", "LocationRemoveSuggests", "IsUploaded"},
                                            new Object[]{logInpersonId, takedate.getText().toString(), personid2,
                                                    attendtime.getText().toString(), endattendtime.getText().toString(),
                                                    daily_addremovekp_aim1.getText().toString(), daily_addremovekp_problem1.getText().toString(),
                                                    daily_addremovekp_dopinion1.getText().toString(), daily_addremovekp_aim.getText().toString(), daily_addremovekp_aim1.getText().toString(),
                                                    daily_addremovekp_dopinion.getText().toString(), daily_addremovekp_dopinion3.getText().toString(), daily_addremovekp_dopinion4.getText().toString()
                                                    , uploadedid
                                            }, new String[]{"Id"}, new String[]{Tableid + ""});
                                }
                                finish();
                            }
                            }
                        }

                    }
                }
            }



    }

    //判断view中的内容
    private void getisempty3(List<TextView> viewList,List<EditText> edList,int uploadedid) {


        aa:for (int i=0; i<viewList.size();i++) {

            if (viewList.get(i).getText().toString().equals("")){
                showToast("不能为空");

                break aa;
            }else {
                if (i==viewList.size()-1){
                    for (int j=0; j<edList.size(); j++){
                        if (edList.get(j).getText().toString().equals("")){

                            showToast("不能为空"+edList.get(j).toString());
                            break aa;
                        }else {
                            if (j == edList.size() - 1) {
                                if (intentid == 1) {
                                    dbOpenHelper.insert("InstructorKeyPerson", new String[]{"InstructorId", "ConfirmDate", "KeyPersonId", "ExpectRemoveTime",
                                                    "KeyLocation", "PersonConfirmReason", "HelpMethod", "Changes", "PersonRemoveSuggests", "LocationConfirmReason", "ControlMethod",
                                                    "ActualControl",
                                                    "LocationRemoveSuggests", "IsUploaded","IsRemoved","ActualRemoveTime"},
                                            new Object[]{logInpersonId, takedate.getText().toString(), personid2,
                                                    attendtime.getText().toString(), endattendtime.getText().toString(),
                                                    daily_addremovekp_aim1.getText().toString(), daily_addremovekp_problem1.getText().toString(),
                                                    daily_addremovekp_dopinion1.getText().toString(), daily_addremovekp_aim.getText().toString(), daily_addremovekp_aim1.getText().toString(),
                                                    daily_addremovekp_dopinion.getText().toString(), daily_addremovekp_dopinion3.getText().toString(), daily_addremovekp_dopinion4.getText().toString()
                                                    , uploadedid,"true",removedata.getText().toString()});


                                } else {

                                    dbOpenHelper.update("InstructorKeyPerson",
                                            new String[]{"InstructorId", "ConfirmDate", "KeyPersonId", "ExpectRemoveTime",
                                                    "KeyLocation", "PersonConfirmReason", "HelpMethod", "Changes", "PersonRemoveSuggests", "LocationConfirmReason", "ControlMethod",
                                                    "ActualControl", "LocationRemoveSuggests", "IsUploaded","IsRemoved","ActualRemoveTime"},
                                            new Object[]{logInpersonId, takedate.getText().toString(), personid2,
                                                    attendtime.getText().toString(), endattendtime.getText().toString(),
                                                    daily_addremovekp_aim1.getText().toString(), daily_addremovekp_problem1.getText().toString(),
                                                    daily_addremovekp_dopinion1.getText().toString(), daily_addremovekp_aim.getText().toString(), daily_addremovekp_aim1.getText().toString(),
                                                    daily_addremovekp_dopinion.getText().toString(), daily_addremovekp_dopinion3.getText().toString(), daily_addremovekp_dopinion4.getText().toString()
                                                    , uploadedid,"true",removedata.getText().toString()}, new String[]{"Id"}, new String[]{Tableid + ""});
                                }
                                finish();
                            }
                        }
                    }

                }
            }
        }



    }


    private void getisempty2(List<TextView> viewList,List<EditText> edList,int uploadedid) {


    aa:
    for (int i = 0; i < viewList.size(); i++) {

        if (viewList.get(i).getText().toString().equals("")) {
            showToast("不能为空");

            break aa;
        } else {
            if (i == viewList.size() - 1) {
                for (int j = 0; j < edList.size(); j++) {
                    if (edList.get(j).getText().toString().equals("")) {

                        showToast("不能为空" + edList.get(j).toString());
                        break aa;
                    } else {

                        if (j == edList.size() - 1) {
                            if (intentid == 1) {
                                dbOpenHelper.insert("InstructorKeyPerson", new String[]{"InstructorId", "ConfirmDate", "KeyPersonId", "ExpectRemoveTime",
                                                "ActualRemoveTime", "KeyLocation", "PersonConfirmReason", "HelpMethod", "Changes", "PersonRemoveSuggests", "LocationConfirmReason", "ControlMethod",
                                                "ActualControl",
                                                "LocationRemoveSuggests", "IsUploaded"},
                                        new Object[]{logInpersonId, takedate.getText().toString(), personid2,
                                                attendtime.getText().toString(), "", endattendtime.getText().toString(),
                                                daily_addremovekp_aim1.getText().toString(), daily_addremovekp_problem1.getText().toString(),
                                                daily_addremovekp_dopinion1.getText().toString(), daily_addremovekp_aim.getText().toString(), daily_addremovekp_aim1.getText().toString(),
                                                daily_addremovekp_dopinion.getText().toString(), daily_addremovekp_dopinion3.getText().toString(), daily_addremovekp_dopinion4.getText().toString()
                                                , uploadedid});

                            } else {
                                dbOpenHelper.update("InstructorKeyPerson",
                                        new String[]{"InstructorId", "ConfirmDate", "KeyPersonId", "ExpectRemoveTime",
                                                "ActualRemoveTime", "KeyLocation", "PersonConfirmReason", "HelpMethod", "Changes", "PersonRemoveSuggests", "LocationConfirmReason", "ControlMethod",
                                                "ActualControl", "LocationRemoveSuggests", "IsUploaded"},
                                        new Object[]{logInpersonId, takedate.getText().toString(), personid2,
                                                attendtime.getText().toString(), "", endattendtime.getText().toString(),
                                                daily_addremovekp_aim1.getText().toString(), daily_addremovekp_problem1.getText().toString(),
                                                daily_addremovekp_dopinion1.getText().toString(), daily_addremovekp_aim.getText().toString(), daily_addremovekp_aim1.getText().toString(),
                                                daily_addremovekp_dopinion.getText().toString(), daily_addremovekp_dopinion3.getText().toString(), daily_addremovekp_dopinion4.getText().toString()
                                                , uploadedid
                                        }, new String[]{"Id"}, new String[]{Tableid + ""});
                                dbOpenHelper.update("InstructorKeyPerson", new String[]{"IsRemoved"}, new Object[]{"true"}, new String[]{"Id"}, new String[]{Tableid});
                                Myapplilcation.getExecutorService().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        NetUtils.updataarguments3dingle(dbOpenHelper, "InstructorKeyPerson", Tableid + "");
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

    private void setpromptdialog(){

        AlertDialog.Builder builder=new AlertDialog.Builder(AddRemoveKeyperson.this);
        builder.setTitle("提示");
        builder.setMessage("填写了实际解除日期会解除当前关键人！点击确定解除关键人");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                getisempty3(viewList, edList, 1);

                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        builder.show();
    }

}
