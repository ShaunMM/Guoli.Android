package zj.com.mc;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import DBUtils.DBOpenHelper;
import Utils.NetUtils;

/**
 * Created by dell on 2016/8/1.
 */
public class MonthSummarize extends Activity implements View.OnClickListener{
//本月计划总结
    private int SAVECONTER=1;
    private int intentid;
    private List<TextView> viewList;
    private List<EditText> edList;
    private EditText daily_monthdetails_aim,daily_monthdetails_problem,daily_monthdetails_dopinion,monthdetails_promcount;
    private TextView  monthdetails_takedate,monthdetails_TrainCode;//标题
    private DBOpenHelper dbOpenHelper;
    private List<Map>  listsearch;
    private SharedPreferences sharedmonthsum;
    private String personId;
    private String listitemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lastmonth);
        sharedmonthsum=getSharedPreferences("PersonInfo",MODE_PRIVATE);
        personId= sharedmonthsum.getString("PersonId",personId);

        initview();


        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bundle");
        intentid = bundle.getInt("month");
        //判断加载内容

        if(intentid == 1) {
                String code = UtilisClass.getStringDate2();
                String sql = "select * from InstructorPlan where WriteDate like ?";
                String[] selectionArgs = new String[]{code + "%"};
                listsearch = dbOpenHelper.queryListMap(sql, selectionArgs);

        }else {
                Calendar c = Calendar.getInstance();
                int  year=c.get(Calendar.YEAR);
                int month=c.get(Calendar.MONTH);
                String code = year + "-" + month;
                System.out.println(">>>>>>>"+month);
                String sql = "select * from InstructorPlan where WriteDate like ?";
                String[] selectionArgs = new String[]{code+"%"};
                listsearch = dbOpenHelper.queryListMap(sql, selectionArgs);
        }

        if (listsearch.size()==0) {

//            Toast.makeText(this, "空的", Toast.LENGTH_SHORT).show();
        } else {
//            Toast.makeText(this, "有参数", Toast.LENGTH_SHORT).show();
            //设置title
            SAVECONTER = (Integer) listsearch.get(0).get("IsUploaded");
            listitemId=listsearch.get(0).get("Id")+"";
            //执行控件赋值
            initviewtext();
        }

    }

    private void initviewtext() {

        monthdetails_takedate.setText(listsearch.get(0).get("WriteDate").toString());
        daily_monthdetails_aim.setText(listsearch.get(0).get("WorkSummary").toString());
        daily_monthdetails_problem.setText(listsearch.get(0).get("Problems").toString());
        daily_monthdetails_dopinion.setText(listsearch.get(0).get("WorkPlans").toString());

    }
    

    private void initview() {
        dbOpenHelper= DBOpenHelper.getInstance(MonthSummarize.this);
      



        monthdetails_takedate= (TextView) findViewById(R.id.monthdetails_takedate);
        monthdetails_takedate.setOnClickListener(this);//日期
        monthdetails_TrainCode= (TextView) findViewById(R.id.monthdetails_address);//总结人
        String name="";
        List<Map> list=dbOpenHelper.queryListMap("select * from PersonInfo where Id=?",new String[]{personId});
        if (list.size()!=0) {
            name = list.get(0).get("Name") + "";
        }
        monthdetails_TrainCode.setText(name);


        daily_monthdetails_aim= (EditText) findViewById(R.id.daily_monthdetails_aim);//完成本月工作
        daily_monthdetails_problem= (EditText) findViewById(R.id.daily_monthdetails_problem);//存在问题分析
        daily_monthdetails_dopinion= (EditText) findViewById(R.id.daily_monthdetails_dopinion);//下月计划工作

        findViewById(R.id.daily_month_titleback).setOnClickListener(this);
        findViewById(R.id.daily_monthdetails_save).setOnClickListener(this);
        findViewById(R.id.daily_monthdetails_saveup).setOnClickListener(this);
        findViewById(R.id.daily_monthdetails_cancle).setOnClickListener(this);

        viewList=new ArrayList<TextView>();
        viewList.add(monthdetails_takedate);
        edList=new ArrayList<EditText>();
        edList.add(daily_monthdetails_aim);
        edList.add(daily_monthdetails_problem);
        edList.add(daily_monthdetails_dopinion);

    }


    @Override
    public void onClick(View view) {
        if (SAVECONTER==1) {
            switch (view.getId()) {
                case R.id.daily_month_titleback:
                    //返回
                    UtilisClass.hidInputMethodManager(MonthSummarize.this,daily_monthdetails_aim);
                    finish();

//                    listsearch=dbOpenHelper.queryListMap("select * from InstructorPlan",null);
//                    daily_monthdetails_aim.setText(String.valueOf(listsearch));

                    break;
                case R.id.monthdetails_takedate:
//日期
                    UtilisClass.getdatepicker(MonthSummarize.this, monthdetails_takedate);
                    break;
                case R.id.daily_monthdetails_save:
//保存到本地
                    if (SAVECONTER == 1) {
//
                        getisempty(viewList, edList, 1);
                        UtilisClass.hidInputMethodManager(MonthSummarize.this,daily_monthdetails_aim);

                        finish();
                    } else {
                        showToast("保存失败，文件已上传！");
                    }
                    break;

                case R.id.daily_monthdetails_saveup:
//保存并上传
//                    showToast("保存失败，文件已上传！");
                    if (SAVECONTER == 1) {
                        getisempty2(viewList, edList, 0);
                        //执行上传代码
                        UtilisClass.hidInputMethodManager(MonthSummarize.this,daily_monthdetails_aim);

                    } else {
                        showToast("保存失败，文件已上传！");
                    }
                    break;

                case R.id.daily_monthdetails_cancle:
//取消
//                    showToast("取消");
                    UtilisClass.hidInputMethodManager(MonthSummarize.this,daily_monthdetails_aim);

                    finish();
                    break;

            }
        }else {

            switch (view.getId()) {
                case R.id.daily_month_titleback:
//返回
                    UtilisClass.hidInputMethodManager(MonthSummarize.this,daily_monthdetails_aim);

                    finish();

                    break;
                case R.id.daily_monthdetails_cancle:
//取消
//                    showToast("取消");
                    UtilisClass.hidInputMethodManager(MonthSummarize.this,daily_monthdetails_aim);

                    finish();
                    break;
            }
            }
    }
    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    //设置输入框
    @Override
    public void onBackPressed() {
            super.onBackPressed();
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
                            if (j == (edList.size() - 1)) {
                                if (listsearch.size()==0) {
                                    dbOpenHelper.insert("InstructorPlan", new String[]{"InstructorId","WriteDate","WorkSummary",
                                                    "Problems","WorkPlans","IsUploaded"},
                                            new Object[]{personId,monthdetails_takedate.getText().toString(),
                                                    personId,daily_monthdetails_problem.getText().toString(),
                                                    daily_monthdetails_dopinion.getText().toString(),uploadedid});
                                    UtilisClass.showToast(MonthSummarize.this,"baocun");
                                } else {
                                    dbOpenHelper.update("InstructorPlan",
                                            new String[]{"InstructorId","WriteDate","WorkSummary",
                                                    "Problems","WorkPlans","IsUploaded"},
                                            new Object[]{personId,monthdetails_takedate.getText().toString(),
                                                    personId,daily_monthdetails_problem.getText().toString(),
                                                    daily_monthdetails_dopinion.getText().toString(),uploadedid},
                                            new String[]{"Id"}, new String[]{listitemId});
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
                                if (j == (edList.size() - 1)) {
                                    if (listsearch.size()==0) {
                                    dbOpenHelper.insert("InstructorPlan", new String[]{"InstructorId","WriteDate","WorkSummary",
                                                    "Problems","WorkPlans","IsUploaded"},
                                            new Object[]{personId,monthdetails_takedate.getText().toString(),
                                                    personId,daily_monthdetails_problem.getText().toString(),
                                                    daily_monthdetails_dopinion.getText().toString(),uploadedid});
//                                    UtilisClass.showToast(MonthSummarize.this,"baocun");

                                    List<Map> mapListid=dbOpenHelper.queryListMap("select * from InstructorPlan where InstructorId=?",null);
                                    if (mapListid.size()!=0){
                                        final String upid=mapListid.get(mapListid.size()-1).get("Id")+"";

                                        Myapplilcation.getExecutorService().execute(new Runnable() {
                                            @Override
                                            public void run() {
                                                NetUtils.updataarguments3dingle(dbOpenHelper,"InstructorPlan",upid);
                                            }
                                        });
                                    }else {}

                                } else {
                                    dbOpenHelper.update("InstructorPlan",
                                            new String[]{"InstructorId","WriteDate","WorkSummary",
                                                    "Problems","WorkPlans","IsUploaded"},
                                            new Object[]{personId,monthdetails_takedate.getText().toString(),
                                                    personId,daily_monthdetails_problem.getText().toString(),
                                                    daily_monthdetails_dopinion.getText().toString(),uploadedid},
                                            new String[]{"Id"}, new String[]{listitemId});


                                        Myapplilcation.getExecutorService().execute(new Runnable() {
                                            @Override
                                            public void run() {
                                                NetUtils.updataarguments3dingle(dbOpenHelper,"InstructorPlan",listitemId);
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



}
