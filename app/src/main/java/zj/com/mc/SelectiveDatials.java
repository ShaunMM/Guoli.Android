package zj.com.mc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

/**
 * Created by dell on 2016/8/1.
 */
public class SelectiveDatials extends Activity implements View.OnClickListener{
    //1日期，4开始时间，5结束时间，6问题个数，7抽查内容，8发现问题，9处理意见
    private int SAVECONTER=1;
    private int intentid;
    private List<TextView>viewList;
    private List<EditText> edList;
    private RelativeLayout selected_popuwindow;
    //抽查类型
    private Spinner selectdetails_LocomotiveType;
    EditText daily_selectdetails_aim,daily_selectdetails_problem,daily_selectdetails_dopinion,selectdetails_promcount,selected_key;
    private TextView select_title2,selectdetails_takedate,selectdetails_TrainCode,
            selectdetails_time1,selectdetails_time2,whichtv;//标题
    private String key,spinnertext;
    private ListView selected_poplist;
    private DBOpenHelper dbOpenHelper;
    private List<Map> listsearch,list;
    private String[] mItems = {"测查", "坐岗", "其他"};;
    private String personID;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dailyselectlistingitem);

        initview();
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("InstructorCheck");
        intentid = bundle.getInt("listitemId");

        sharedPreferences=getSharedPreferences("PersonInfo",MODE_PRIVATE);
        personID=sharedPreferences.getString("PersonId",null);
        initviewspinner();

        if (intentid == -1) {
//            Toast.makeText(this, "空的", Toast.LENGTH_SHORT).show();
        } else {

//            Toast.makeText(this, "有参数", Toast.LENGTH_SHORT).show();
            //设置title
            String sql = "select * from InstructorCheck" +
                    " where Id  =? ";
            String[] selectionArgs = new String[]{intentid + ""};
            list = dbOpenHelper.queryListMap(sql, selectionArgs);
            SAVECONTER = (Integer) list.get(0).get("IsUploaded");


            //执行控件赋值
            initviewtext();
        }

    }

    private void initviewtext() {


        selectdetails_takedate.setText((list.get(0).get("StartTime").toString()).split(" ")[0]);
        selectdetails_TrainCode.setText(list.get(0).get("Location").toString());
        selectdetails_time1.setText((list.get(0).get("StartTime").toString()).split(" ")[1]);
        selectdetails_time2.setText((list.get(0).get("EndTime").toString()).split(" ")[1]);
        selectdetails_promcount .setText(list.get(0).get("ProblemCount").toString());
        daily_selectdetails_aim .setText(list.get(0).get("CheckContent").toString());
        daily_selectdetails_problem.setText(list.get(0).get("Problems").toString());
        daily_selectdetails_dopinion .setText(list.get(0).get("Suggests").toString());
            String str=list.get(0).get("CheckType").toString();
            for (int i=0; i<mItems.length;i++){
                if (str.equals(mItems[i])){
                    selectdetails_LocomotiveType.setSelection(i,true);
                }
            }
    }

    private void initviewspinner() {


        //建立数据源

        // 建立Adapter并且绑定数据源
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, mItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //绑定 Adapter到控件
        selectdetails_LocomotiveType .setAdapter(adapter);
        selectdetails_LocomotiveType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                spinnertext=mItems[pos];
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    private void initview() {
        dbOpenHelper= DBOpenHelper.getInstance(getApplicationContext());
        selectdetails_LocomotiveType= (Spinner) findViewById(R.id.selectdetails_LocomotiveType);
        selected_popuwindow= (RelativeLayout) findViewById(R.id.selected_popuwindow);
        selected_popuwindow.setOnClickListener(this);
        selected_popuwindow.setVisibility(View.GONE);


        select_title2= (TextView) findViewById(R.id.select_title2);
        select_title2.setText("抽查详细单");
        selectdetails_takedate= (TextView) findViewById(R.id.selectdetails_takedate);
        selectdetails_takedate.setOnClickListener(this);//日期
        selectdetails_TrainCode= (TextView) findViewById(R.id.selectdetails_address);
        selectdetails_TrainCode.setOnClickListener(this);
        selectdetails_time1= (TextView) findViewById(R.id.selectdetails_time1);
        selectdetails_time1.setOnClickListener(this);
        selectdetails_time2= (TextView) findViewById(R.id.selectdetails_time2);
        selectdetails_time2.setOnClickListener(this);
        selectdetails_promcount= (EditText) findViewById(R.id.selectdetails_promcount);


        daily_selectdetails_aim= (EditText) findViewById(R.id.daily_selectdetails_aim);
        daily_selectdetails_problem= (EditText) findViewById(R.id.daily_selectdetails_problem);
        daily_selectdetails_dopinion= (EditText) findViewById(R.id.daily_selectdetails_dopinion);
        selected_key= (EditText) findViewById(R.id.selected_key);
        selected_poplist= (ListView) findViewById(R.id.selected_poplist);


        findViewById(R.id.daily_selectdetails_save).setOnClickListener(this);
        findViewById(R.id.daily_selectdetails_saveup).setOnClickListener(this);
        findViewById(R.id.daily_selectdetails_cancle).setOnClickListener(this);
        findViewById(R.id.selected_popuwindow_yes).setOnClickListener(this);
        viewList=new ArrayList<TextView>();
        viewList.add(selectdetails_takedate);
        viewList.add(selectdetails_TrainCode);
        viewList.add(selectdetails_time1);
        viewList.add(selectdetails_time2);
        edList=new ArrayList<EditText>();
        edList.add(selectdetails_promcount);
        findViewById(R.id.daily_selected_titleback).setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {

    if (SAVECONTER==1) {
        switch (view.getId()) {
            case R.id.daily_selected_titleback:
                //返回
                UtilisClass.hidInputMethodManager(SelectiveDatials.this,selectdetails_promcount);

                finish();
                break;

            case R.id.selectdetails_takedate:
//日期
                UtilisClass.getdatepicker(SelectiveDatials.this, selectdetails_takedate);
                break;
            case R.id.selectdetails_address:
//抽查地点

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

            case R.id.daily_selectdetails_save:
//保存到本地

                    getisempty(viewList, edList, 1);
                UtilisClass.hidInputMethodManager(SelectiveDatials.this,selectdetails_promcount);


                break;

            case R.id.daily_selectdetails_saveup:
//保存并上传
                    getisempty2(viewList, edList, 0);
                UtilisClass.hidInputMethodManager(SelectiveDatials.this,selectdetails_promcount);

                break;

            case R.id.daily_selectdetails_cancle:
//取消
//                showToast("取消");
                UtilisClass.hidInputMethodManager(SelectiveDatials.this,selectdetails_promcount);

                finish();
                break;

        }
    }else {
        switch (view.getId()) {
            case R.id.daily_selected_titleback:
                //返回
                UtilisClass.hidInputMethodManager(SelectiveDatials.this,selectdetails_promcount);

                finish();
                break;
            case R.id.daily_selectdetails_cancle:
//取消
//                showToast("取消");
                UtilisClass.hidInputMethodManager(SelectiveDatials.this,selectdetails_promcount);

                finish();
                break;
        }
        }

    }

    private void getsearch(final TextView ed, final String tablename){
        selected_key.setText("");
        selected_key.setFocusable(true);
        selected_key.setFocusableInTouchMode(true);
        selected_key.requestFocus();
        InputMethodManager inputManager =
                (InputMethodManager)selected_key.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(selected_key, 0);
        whichtv=ed;
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

                                    selected_key.setText(listsearch.get(0).get("StationName").toString());
                                selected_popuwindow.setVisibility(View.GONE);
                                whichtv.setText(key);
                            }
                        });

                    } else {
//                    listView.setAdapter(null);
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
    private List<Map> getDBpersonname(String tablename, String coads){

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
        if (selected_popuwindow.getVisibility()!= View.GONE){
            selected_popuwindow.setVisibility(View.GONE);
        }else {
            super.onBackPressed();
        }
    }
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
                                     if (intentid== -1) {

                                    dbOpenHelper.insert("InstructorCheck", new String[]{"InstructorId","StartTime","EndTime",
                                                    "Location","CheckType","ProblemCount","CheckContent","Problems","Suggests","IsUploaded","RecirdTime"},
                                            new Object[]{personID,selectdetails_takedate.getText().toString()+" "+selectdetails_time1.getText().toString(),
                                                    selectdetails_takedate.getText().toString()+" "+selectdetails_time2.getText().toString(),selectdetails_TrainCode.getText().toString(),
                                                    spinnertext,selectdetails_promcount.getText().toString(),daily_selectdetails_aim.getText().toString(),
                                                    daily_selectdetails_problem.getText().toString(), daily_selectdetails_dopinion.getText().toString(),uploadedid,1});
                                    Isrecodetime();
                                } else {
                                    dbOpenHelper.update("InstructorCheck",
                                            new String[]{"InstructorId","StartTime","EndTime",
                                                    "Location","CheckType","ProblemCount","CheckContent","Problems","Suggests","IsUploaded"},
                                            new Object[]{personID,selectdetails_takedate.getText().toString()+" "+selectdetails_time1.getText().toString(),
                                                    selectdetails_takedate.getText().toString()+" "+selectdetails_time2.getText().toString(),selectdetails_TrainCode.getText().toString(),
                                                    spinnertext,selectdetails_promcount.getText().toString(),daily_selectdetails_aim.getText().toString(),
                                                    daily_selectdetails_problem.getText().toString(), daily_selectdetails_dopinion.getText().toString(),uploadedid},
                                            new String[]{"Id"}, new String[]{intentid + ""});
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
                                     if (intentid== -1) {
                                    dbOpenHelper.insert("InstructorCheck", new String[]{"InstructorId","StartTime","EndTime",
                                                    "Location","CheckType","ProblemCount","CheckContent","Problems","Suggests","IsUploaded","RecirdTime"},
                                            new Object[]{personID,selectdetails_takedate.getText().toString()+" "+selectdetails_time1.getText().toString(),
                                                    selectdetails_takedate.getText().toString()+" "+selectdetails_time2.getText().toString(),selectdetails_TrainCode.getText().toString(),
                                                    spinnertext,selectdetails_promcount.getText().toString(),daily_selectdetails_aim.getText().toString(),
                                                    daily_selectdetails_problem.getText().toString(), daily_selectdetails_dopinion.getText().toString(),uploadedid,1});
                                    Isrecodetime();
                                    List<Map> mapListid=dbOpenHelper.queryListMap("select * from InstructorCheck where InstructorId=?",new String[]{personID});
                                    if (mapListid.size()!=0){
                                        final String upid=mapListid.get(mapListid.size()-1).get("Id")+"";

                                        Myapplilcation.getExecutorService().execute(new Runnable() {
                                            @Override
                                            public void run() {
                                                NetUtils.updataarguments3dingle(dbOpenHelper,"InstructorCheck",upid);
                                            }
                                        });
                                    }else {}

                                } else {
                                    dbOpenHelper.update("InstructorCheck",
                                            new String[]{"InstructorId","StartTime","EndTime",
                                                    "Location","CheckType","ProblemCount","CheckContent","Problems","Suggests","IsUploaded"},
                                            new Object[]{personID,selectdetails_takedate.getText().toString()+" "+selectdetails_time1.getText().toString(),
                                                    selectdetails_takedate.getText().toString()+" "+selectdetails_time2.getText().toString(),selectdetails_TrainCode.getText().toString(),
                                                    spinnertext,selectdetails_promcount.getText().toString(),daily_selectdetails_aim.getText().toString(),
                                                    daily_selectdetails_problem.getText().toString(), daily_selectdetails_dopinion.getText().toString(),uploadedid},
                                            new String[]{"Id"}, new String[]{intentid + ""});

                                                    UtilisClass.showToast(SelectiveDatials.this,">>>>>>>>>>>>>>>>>>>>");
                                        Myapplilcation.getExecutorService().execute(new Runnable() {
                                            @Override
                                            public void run() {
                                                NetUtils.updataarguments3dingle(dbOpenHelper,"InstructorCheck",intentid+"");
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
    private void Isrecodetime(){

        String data= UtilisClass.getStringDate2();
        String data2= UtilisClass.getStringDate();

        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH)+1;

        if (intentid==-1) {
            //第一次添加
            int quotaid= UtilisClass.getMONTHSELECTIMEID();
            List<Map> list = dbOpenHelper.queryListMap("select * from InstructorQuotaRecord where InstructorId=? and QuotaId=? and UpdateTime like ?", new String[]{personID,quotaid+"",data + "%"});
            if (list.size()==0) {
                dbOpenHelper.insert("InstructorQuotaRecord", new String[]{
                        "InstructorId","QuotaId","FinishedAmmount","UpdateTime","Year","Month"
                }, new Object[]{personID, quotaid, 1, data2,year,month});
            }else {
                       int num= Integer.parseInt(list.get(0).get("FinishedAmmount")+"");
                        num=num+1;
                String id=list.get(0).get("Id")+"";
                dbOpenHelper.update("InstructorQuotaRecord",new String[]{"FinishedAmmount","UpdateTime","IsUploaded"
                },new Object[]{num,data2,"1"},new String[]{"Id"},new String[]{id});
            }
        }else {
        }
    }
}
