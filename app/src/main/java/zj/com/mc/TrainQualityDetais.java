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
import android.widget.LinearLayout;
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

/**
 * Created by dell on 2016/8/9.
 */
public class TrainQualityDetais extends Activity implements View.OnClickListener{
    private int SAVECONTER=1;
    private int intentid;

    private ListView qualitys_poplist;
    private List<TextView> viewList;
    private List<EditText> edList;

    private TextView  qualitys_takedate,qualitys_TrainCode,qualitys_DriverId,qualitys_title2;
    private EditText qualitys_LocomotiveType,qualitys_ViceDriverId,qualitys_StudentId,
            qualitys_AttendTime,qualitys_EndAttendTime,daily_qualitys_aim,qualitysedit_key;

    private DBOpenHelper dbopenhelper;
    private RelativeLayout qualitys_popuwindow;
    private LinearLayout qualitys_titleback;
    private TextView whichtv;//中转
    private String personID;
    private String key;
    private String personid1;//记录员工ID
    private List<Map> listsearch,list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trainqualitys);
        initview();
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("InstructorLocoQuality");
        intentid = bundle.getInt("listitemId");
        personID=getSharedPreferences("PersonInfo",MODE_PRIVATE).getString("PersonId",null);
        if (intentid == -1) {
//            UtilisClass.showToast(TrainQualityDetais.this,"空的");
        } else {

//            UtilisClass.showToast(TrainQualityDetais.this, "有参数");
            //设置title
            String sql = "select * from InstructorLocoQuality" +
                    " where Id  =? ";
            String[] selectionArgs = new String[]{intentid + ""};
            list = dbopenhelper.queryListMap(sql, selectionArgs);
            SAVECONTER = (Integer) list.get(0).get("IsUploaded");
            //执行控件赋值
            initviewtext();
        }
    }

    private void initviewtext() {


        qualitys_takedate.setText(list.get(0).get("RegistDate").toString());
        qualitys_TrainCode.setText(list.get(0).get("TrainCode").toString());
        qualitys_LocomotiveType.setText(list.get(0).get("LocomotiveType").toString());
        personid1=list.get(0).get("DriverId").toString();
        List<Map> maindriverinfo=dbopenhelper.queryListMap("select * from PersonInfo where Id=?",new String[]{personid1});
                  if (maindriverinfo.size()!=0){
            qualitys_DriverId.setText(maindriverinfo.get(0).get("Name").toString());}


        qualitys_ViceDriverId.setText(list.get(0).get("RepairClass").toString());
        qualitys_StudentId.setText(list.get(0).get("MaintenanceStatus").toString());
        qualitys_AttendTime.setText(list.get(0).get("Score").toString());
        qualitys_EndAttendTime.setText(list.get(0).get("FaultLocation").toString());
        daily_qualitys_aim.setText(list.get(0).get("GeneralSituation").toString());
    }

    private void initview() {
        dbopenhelper= DBOpenHelper.getInstance(getApplicationContext());
        findViewById(R.id.qualitys_titleback).setOnClickListener(this);//返回
        qualitys_popuwindow= (RelativeLayout) findViewById(R.id.qualitys_popuwindow);//popwindow
        qualitys_popuwindow.setOnClickListener(this);
        qualitys_takedate= (TextView) findViewById(R.id.qualitys_takedate);//发生日期
        qualitys_takedate.setOnClickListener(this);
        qualitys_TrainCode= (TextView) findViewById(R.id.qualitys_TrainCode);//车次
        qualitys_TrainCode.setOnClickListener(this);
        qualitys_DriverId= (TextView) findViewById(R.id.qualitys_DriverId);//主班司机
        qualitys_DriverId.setOnClickListener(this);
        qualitys_LocomotiveType= (EditText) findViewById(R.id.qualitys_LocomotiveType);//机车型号
        qualitys_ViceDriverId= (EditText) findViewById(R.id.qualitys_ViceDriverId);//修程
        qualitys_StudentId= (EditText) findViewById(R.id.qualitys_StudentId);//保养状态
        qualitys_AttendTime= (EditText) findViewById(R.id.qualitys_AttendTime);//评定成绩
        qualitys_EndAttendTime= (EditText) findViewById(R.id.qualitys_EndAttendTime);//故障处理
        daily_qualitys_aim= (EditText) findViewById(R.id.daily_qualitys_aim);//概况
        qualitysedit_key= (EditText) findViewById(R.id.qualitysedit_key);//popwindow  editext
        findViewById(R.id.qualitys_popuwindow_yes).setOnClickListener(this);//popwindow 确定键
        qualitys_poplist= (ListView) findViewById(R.id.qualitys_poplist);//pop listview

        findViewById(R.id.daily_qualitys_save).setOnClickListener(this);
        findViewById(R.id.daily_qualitys_saveup).setOnClickListener(this);
        findViewById(R.id.daily_qualitys_cancle).setOnClickListener(this);

        viewList=new ArrayList<TextView>();
        viewList.add(qualitys_takedate);
        viewList.add(qualitys_TrainCode);
        viewList.add(qualitys_DriverId);

        edList=new ArrayList<EditText>();
        viewList.add(qualitys_ViceDriverId);
        viewList.add(qualitys_StudentId);
        viewList.add(qualitys_AttendTime);
        edList.add(qualitys_LocomotiveType);
        edList.add(qualitys_EndAttendTime);


    }

    @Override
    public void onClick(View view) {
        if (SAVECONTER==1){
            switch (view.getId()){
                case R.id.qualitys_titleback:
//返回
                    UtilisClass.hidInputMethodManager(TrainQualityDetais.this,qualitys_ViceDriverId);

                    finish();
                    break;
                case R.id.qualitys_takedate:
//日期
                UtilisClass.getdatepicker(TrainQualityDetais.this,qualitys_takedate);

                    break;
                case R.id.qualitys_popuwindow:
//popwindow
                    qualitys_popuwindow.setVisibility(View.VISIBLE);
                break;
                case R.id.qualitys_popuwindow_yes:
//popwindow确定
                    whichtv.setText(qualitysedit_key.getText().toString());
                    qualitys_popuwindow.setVisibility(View.GONE);

                    UtilisClass.setDriverid(TrainQualityDetais.this,whichtv,qualitys_DriverId,personid1);


                    break;

                case R.id.qualitys_TrainCode :
//车次
                    qualitys_popuwindow.setVisibility(View.VISIBLE);
                    getsearch(qualitys_TrainCode,"BaseStation");

                break;
                case  R.id.qualitys_DriverId:
//主班司机
                    qualitys_popuwindow.setVisibility(View.VISIBLE);
                    getsearch(qualitys_DriverId,"ViewPersonInfo");

                break;
                case R.id.qualitys_AttendTime :
//发生地点
                    qualitys_popuwindow.setVisibility(View.VISIBLE);
                    getsearch(qualitys_AttendTime,"BaseStation");

                break;
                case  R.id.daily_qualitys_save:
//保存到本地
                    if (SAVECONTER == 1) {

                        getisempty(viewList, edList,1);
                        UtilisClass.hidInputMethodManager(TrainQualityDetais.this,qualitys_ViceDriverId);

//                        finish();
                    }else {
                        UtilisClass.showToast(TrainQualityDetais.this,"保存失败，文件已上传！");
                    }


                    UtilisClass.showToast(TrainQualityDetais.this,qualitys_takedate.getText().toString());
                break;
            case  R.id.daily_qualitys_saveup:
//保存并上传
    if (SAVECONTER == 1) {

        getisempty2(viewList, edList,0);
        UtilisClass.hidInputMethodManager(TrainQualityDetais.this,qualitys_ViceDriverId);

        //执行上传代码

        SAVECONTER=0;
    }else {
        UtilisClass.showToast(TrainQualityDetais.this,"保存失败，文件已上传！");
    }
                break;
        case  R.id.daily_qualitys_cancle:
//取消
            UtilisClass.hidInputMethodManager(TrainQualityDetais.this,qualitys_ViceDriverId);

            finish();
                break;

            }

        }else {}
            switch (view.getId()) {
                case R.id.qualitys_titleback:
//返回
                    UtilisClass.hidInputMethodManager(TrainQualityDetais.this,qualitys_ViceDriverId);

                    finish();
                    break;

        }
    }





//关键字词搜索
private void getsearch(final TextView ed, final String tablename){
        qualitysedit_key.setText("");
        qualitysedit_key.setFocusable(true);
        qualitysedit_key.setFocusableInTouchMode(true);
        qualitysedit_key.requestFocus();
        InputMethodManager inputManager =
                (InputMethodManager)qualitysedit_key.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(qualitysedit_key, 0);
        whichtv=ed;
        if (tablename.equals("ViewPersonInfo")) {
            qualitysedit_key.setHint("支持工号，姓名，拼音简称搜索");
        }else {
            qualitysedit_key.setHint("请输入要查询的内容");
        }


        qualitysedit_key.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                UtilisClass.setdriverid2(whichtv,qualitys_DriverId,personid1);

                key=  qualitysedit_key.getText().toString();
                if(key!=null && !"".equals(key.trim())){
                    //获取数据


                    if (tablename.equals("ViewPersonInfo")) {
                        listsearch = getDBpersonname("ViewPersonInfo",key);

                    }else {
                        listsearch =getDBpersonname2(key);
                    }

                    qualitys_poplist.setAdapter(new CommonAdapter<Map>(TrainQualityDetais.this,listsearch, R.layout.editlist1) {

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
                                holder.setText(R.id.editlist_item1,map.get("FullName").toString());
                            }
                        }
                    });
                    qualitys_poplist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            if (tablename.equals("ViewPersonInfo")) {
                                qualitysedit_key.setText(listsearch.get(i).get("Name").toString());
                                personid1 =  listsearch.get(0).get("Id")+"";
                            }else {
                                qualitysedit_key.setText(listsearch.get(i).get("FullName").toString());
                            }
                            whichtv.setText(qualitysedit_key.getText().toString());
                            qualitys_popuwindow.setVisibility(View.GONE);
                        }
                    });
                }else{
                    qualitys_poplist.setAdapter(null);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }
    //操纵区段查询
    private List<Map> getDBpersonname2(String coads){

        String sql= "select * from TrainNo"+
                " where FullName like ? ";
        String [] selectionArgs  = new String[]{
                "%" + coads + "%"};
        List<Map> listsearch = dbopenhelper.queryListMap(sql, selectionArgs);

        return  listsearch;
    };
    //搜索view表中的数据
    private List<Map> getDBpersonname(String tablename, String coads){

        String sql= "select * from " +tablename+
                " where WorkNo like ? or Name like ? or Spell like ? ";
        String [] selectionArgs  = new String[]{"%" + coads + "%",
                "%" + coads + "%",
                "%" + coads + "%"};
        List<Map> listsearch = dbopenhelper.queryListMap(sql, selectionArgs);

        return  listsearch;
    };

    //判断view中的内容
    private void getisempty(List<TextView> viewList,List<EditText> edList,int uploadedid) {


        aa:for (int i=0; i<viewList.size();i++) {

            if (viewList.get(i).getText().toString().equals("")){
                UtilisClass.showToast(TrainQualityDetais.this,"不能为空");

                break aa;
            }else {
                if (i==viewList.size()-1){
                    for (int j=0; j<edList.size(); j++){
                        if (edList.get(j).getText().toString().equals("")){

                            UtilisClass.showToast(TrainQualityDetais.this,"不能为空"+edList.get(j).toString());
                            break aa;
                        }else {
                            if (j == edList.size() - 1) {
                            if (intentid== -1) {
                                    dbopenhelper.insert("InstructorLocoQuality", new String[]{"InstructorId", "RegistDate", "TrainCode", "LocomotiveType",
                                                    "DriverId", "RepairClass", "MaintenanceStatus", "Score", "FaultLocation","GeneralSituation","IsUploaded"},
                                            new Object[]{personID,qualitys_takedate.getText().toString(),
                                                    qualitys_TrainCode.getText().toString(),qualitys_LocomotiveType.getText().toString(),
                                                    personid1,qualitys_ViceDriverId.getText().toString(),qualitys_StudentId.getText().toString(),
                                                    qualitys_AttendTime.getText().toString(),
                                                    qualitys_EndAttendTime.getText().toString(),daily_qualitys_aim.getText().toString(),
                                                    uploadedid});


                                } else {
                                    dbopenhelper.update("InstructorLocoQuality",
                                            new String[]{"InstructorId", "RegistDate", "TrainCode", "LocomotiveType",
                                                    "DriverId", "RepairClass", "MaintenanceStatus", "Score", "FaultLocation","GeneralSituation","IsUploaded"},
                                            new Object[]{personID,qualitys_takedate.getText().toString(),
                                                    qualitys_TrainCode.getText().toString(),qualitys_LocomotiveType.getText().toString(),
                                                    personid1,qualitys_ViceDriverId.getText().toString(),qualitys_StudentId.getText().toString(),
                                                    qualitys_AttendTime.getText().toString(),
                                                    qualitys_EndAttendTime.getText().toString(),daily_qualitys_aim.getText().toString(),
                                                    uploadedid}, new String[]{"Id"}, new String[]{intentid + ""});
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
    private void getisempty2(List<TextView> viewList,List<EditText> edList,int uploadedid) {


        aa:for (int i=0; i<viewList.size();i++) {

            if (viewList.get(i).getText().toString().equals("")){
                UtilisClass.showToast(TrainQualityDetais.this,"不能为空");

                break aa;
            }else {
                if (i==viewList.size()-1){
                    for (int j=0; j<edList.size(); j++){
                        if (edList.get(j).getText().toString().equals("")){

                            UtilisClass.showToast(TrainQualityDetais.this,"不能为空"+edList.get(j).toString());
                            break aa;
                        }else {
                            if (j == edList.size() - 1) {
                                 if (intentid== -1) {
                                    dbopenhelper.insert("InstructorLocoQuality", new String[]{"InstructorId", "RegistDate", "TrainCode", "LocomotiveType",
                                                    "DriverId", "RepairClass", "MaintenanceStatus", "Score", "FaultLocation","GeneralSituation","IsUploaded"},
                                            new Object[]{personID,qualitys_takedate.getText().toString(),
                                                    qualitys_TrainCode.getText().toString(),qualitys_LocomotiveType.getText().toString(),
                                                    personid1,qualitys_ViceDriverId.getText().toString(),qualitys_StudentId.getText().toString(),
                                                    qualitys_AttendTime.getText().toString(),
                                                    qualitys_EndAttendTime.getText().toString(),daily_qualitys_aim.getText().toString(),
                                                    uploadedid});
                                    List<Map> mapListid=dbopenhelper.queryListMap("select * from InstructorLocoQuality where InstructorId=?",new String[]{personID});
                                    if (mapListid.size()!=0){
                                        final String upid=mapListid.get(mapListid.size()-1).get("Id")+"";
                                        System.out.println(String.valueOf(mapListid)+"trainqualitydetais??????????????");
                                        Myapplilcation.getExecutorService().execute(new Runnable() {
                                            @Override
                                            public void run() {
                                                NetUtils.updataarguments3dingle(dbopenhelper,"InstructorLocoQuality",upid);
                                            }
                                        });
                                    }else {}

                                } else {
                                    dbopenhelper.update("InstructorLocoQuality",
                                            new String[]{"InstructorId", "RegistDate", "TrainCode", "LocomotiveType",
                                                    "DriverId", "RepairClass", "MaintenanceStatus", "Score", "FaultLocation","GeneralSituation","IsUploaded"},
                                            new Object[]{personID,qualitys_takedate.getText().toString(),
                                                    qualitys_TrainCode.getText().toString(),qualitys_LocomotiveType.getText().toString(),
                                                    personid1,qualitys_ViceDriverId.getText().toString(),qualitys_StudentId.getText().toString(),
                                                    qualitys_AttendTime.getText().toString(),
                                                    qualitys_EndAttendTime.getText().toString(),daily_qualitys_aim.getText().toString(),
                                                    uploadedid}, new String[]{"Id"}, new String[]{intentid + ""});

                                        Myapplilcation.getExecutorService().execute(new Runnable() {
                                            @Override
                                            public void run() {
                                                NetUtils.updataarguments3dingle(dbopenhelper,"InstructorLocoQuality",intentid+"");
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
        if (qualitys_popuwindow.getVisibility()!=View.GONE){
            qualitys_popuwindow.setVisibility(View.GONE);
        }else {
            super.onBackPressed();
        }
    }

}
