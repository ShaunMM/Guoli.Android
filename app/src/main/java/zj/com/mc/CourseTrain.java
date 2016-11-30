package zj.com.mc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import DBUtils.DBOpenHelper;
import Utils.NetUtils;

/**
 * Created by dell on 2016/8/1.
 */
public class CourseTrain extends Activity implements View.OnClickListener{

    private int SAVECONTER=1;
    private int intentid;
    private ListView course_poplist;
    private List<TextView> viewList;
    private List<EditText> edList;
    private EditText address,personcount,daily_course_aim,courseedit_key;
    private TextView coursedate,startt,endt;
    private String personId;


    private DBOpenHelper dbopenhelper;
    private RelativeLayout course_popuwindow;
    private LinearLayout    course_titleback;
    private TextView whichtv;//中转

    private String key;
    private List<Map> listcourse,list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coursetrain);
        initview();
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("InstructorTeach");
        intentid = bundle.getInt("listitemId");
        personId=getSharedPreferences("PersonInfo",MODE_PRIVATE).getString("PersonId",null);
        if (intentid == -1) {
//            UtilisClass.showToast(CourseTrain.this,"空的");
        } else {

//            UtilisClass.showToast(CourseTrain.this, "有参数");
            //设置title
            String sql = "select * from InstructorTeach" +
                    " where Id  =? ";
            String[] selectionArgs = new String[]{intentid + ""};
            list = dbopenhelper.queryListMap(sql, selectionArgs);
            SAVECONTER = (Integer) list.get(0).get("IsUploaded");
            //执行控件赋值
            initviewtext();
        }
    }

    private void initview() {
        dbopenhelper= DBOpenHelper.getInstance(getApplicationContext());
        address= (EditText) findViewById(R.id.course_takedate);//授课地点
        personcount= (EditText) findViewById(R.id.course_personcount);//参与人数
        daily_course_aim= (EditText) findViewById(R.id.daily_course_aim);//授课内容

        coursedate= (TextView) findViewById(R.id.course_date);//授课日期
        coursedate.setOnClickListener(this);
        startt= (TextView) findViewById(R.id.course_time1);//授课开始时间
        startt.setOnClickListener(this);
        endt= (TextView) findViewById(R.id.course_time2);//授课结束时间
        endt.setOnClickListener(this);

        course_popuwindow= (RelativeLayout) findViewById(R.id.course_popuwindow);//弹窗
        course_popuwindow.setOnClickListener(this);
        course_titleback= (LinearLayout) findViewById(R.id.course_titleback);//title 返回键
        course_titleback.setOnClickListener(this);
        course_poplist= (ListView) findViewById(R.id.course_poplist);

        findViewById(R.id.daily_course_save).setOnClickListener(this);
        findViewById(R.id.daily_course_saveup).setOnClickListener(this);
        findViewById(R.id.daily_course_cancle).setOnClickListener(this);

        viewList=new ArrayList<TextView>();
        viewList.add(coursedate);
        viewList.add(startt);
        viewList.add(endt);
        edList=new ArrayList<EditText>();
        edList.add(address);
        edList.add(personcount);

    }

    private void initviewtext() {

        address.setText(list.get(0).get("TeachPlace").toString());
        personcount.setText(list.get(0).get("JoinCount").toString());
        coursedate.setText(list.get(0).get("TeachStart").toString().split(" ")[0]);
        startt.setText(list.get(0).get("TeachStart").toString().split(" ")[1]);
        endt.setText(list.get(0).get("TeachEnd").toString().split(" ")[1]);
        daily_course_aim.setText(list.get(0).get("TeachContent").toString());

    }

    @Override
    public void onClick(View view) {

        if (SAVECONTER==1){
            switch (view.getId()){
                case R.id.course_titleback:
//返回
                    UtilisClass.hidInputMethodManager(CourseTrain.this,address);

                    finish();
                    break;
                case R.id.course_date:
//日期
                    UtilisClass.getdatepicker(CourseTrain.this,coursedate);

                    break;
                case R.id.course_popuwindow:
//popwindow
                    course_popuwindow.setVisibility(View.VISIBLE);
                    break;
                case R.id.course_popuwindow_yes:
//popwindow确定
                    whichtv.setText(courseedit_key.getText().toString());
                    course_popuwindow.setVisibility(View.GONE);
                    break;

                case R.id.course_time1 :
//开始时间startt
                    UtilisClass.gettimepicker(CourseTrain.this,startt);
                        
                    break;
                case  R.id.course_time2:
//结束时间
                    UtilisClass.gettimepicker(CourseTrain.this,endt);

                    break;
                case  R.id.daily_course_save:
//保存到本地
                    if (SAVECONTER == 1) {

                        getisempty(viewList, edList,1);
                        UtilisClass.hidInputMethodManager(CourseTrain.this,address);

//                        finish();
                    }else {
                        UtilisClass.showToast(CourseTrain.this,"保存失败，文件已上传！");
                    }

                    break;
                case  R.id.daily_course_saveup:
//保存并上传
                    if (SAVECONTER == 1) {

                        getisempty2(viewList, edList,0);
                        UtilisClass.hidInputMethodManager(CourseTrain.this,address);

//                        UtilisClass.UpLoadeddata("InstructorTeach");
//                        UtilisClass.showToast(CourseTrain.this,"上传成功");
                        //执行上传代码

                        SAVECONTER=0;
                    }else {
                        UtilisClass.showToast(CourseTrain.this,"保存失败，文件已上传！");
                    }
                    break;
                case  R.id.daily_course_cancle:
//取消
                    UtilisClass.hidInputMethodManager(CourseTrain.this,address);

                    finish();
                    break;

            }

        }else {
            switch (view.getId()) {
                case R.id.course_titleback:
//返回
                    UtilisClass.hidInputMethodManager(CourseTrain.this,address);

                    finish();
                    break;
                case  R.id.daily_course_cancle:
//取消
                    UtilisClass.hidInputMethodManager(CourseTrain.this,address);

                    finish();
                    break;
            }
        }
        
        
    }

   

    //判断view中的内容
    private void getisempty(List<TextView> viewList,List<EditText> edList,int uploadedid) {


        aa:for (int i=0; i<viewList.size();i++) {

            if (viewList.get(i).getText().toString().equals("")){
                UtilisClass.showToast(CourseTrain.this,"不能为空");

                break aa;
            }else {
                if (i==viewList.size()-1){
                    for (int j=0; j<edList.size(); j++){
                        if (edList.get(j).getText().toString().equals("")){

                            UtilisClass.showToast(CourseTrain.this,"不能为空"+edList.get(j).toString());
                            break aa;
                        }else {
                            if (j == edList.size() - 1) {
                            if (intentid== -1) {
                                    dbopenhelper.insert("InstructorTeach", new String[]{"InstructorId", "TeachPlace", "JoinCount",
                                                    "TeachStart", "TeachEnd", "TeachContent", "IsUploaded"},
                                            new Object[]{personId,address.getText().toString(),
                                                    personcount.getText().toString(),coursedate.getText().toString()+" "+startt.getText().toString(),
                                                    coursedate.getText().toString()+" "+endt.getText().toString(),daily_course_aim.getText().toString()
                                                    ,uploadedid});


                                } else {
                                    dbopenhelper.update("InstructorTeach",
                                            new String[]{"InstructorId", "TeachPlace", "JoinCount",
                                                    "TeachStart", "TeachEnd", "TeachContent", "IsUploaded"},
                                            new Object[]{personId,address.getText().toString(),
                                                    personcount.getText().toString(),coursedate.getText().toString()+" "+startt.getText().toString(),
                                                    coursedate.getText().toString()+" "+endt.getText().toString(),daily_course_aim.getText().toString()
                                                    ,uploadedid}, new String[]{"Id"}, new String[]{intentid + ""});




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
                UtilisClass.showToast(CourseTrain.this,"不能为空");

                break aa;
            }else {
                if (i==viewList.size()-1){
                    for (int j=0; j<edList.size(); j++){
                        if (edList.get(j).getText().toString().equals("")){

                            UtilisClass.showToast(CourseTrain.this,"不能为空"+edList.get(j).toString());
                            break aa;
                        }else {
                            if (j == edList.size() - 1) {
                            if (intentid== -1) {

                                    dbopenhelper.insert("InstructorTeach", new String[]{"InstructorId", "TeachPlace", "JoinCount",
                                                    "TeachStart", "TeachEnd", "TeachContent", "IsUploaded"},
                                            new Object[]{personId,address.getText().toString(),
                                                    personcount.getText().toString(),coursedate.getText().toString()+" "+startt.getText().toString(),
                                                    coursedate.getText().toString()+" "+endt.getText().toString(),daily_course_aim.getText().toString()
                                                    ,uploadedid});
                                    List<Map> mapListid=dbopenhelper.queryListMap("select * from InstructorTeach where InstructorId=?",new String[]{personId});
                                    if (mapListid.size()!=0){
                                        final String upid=mapListid.get(mapListid.size()-1).get("Id")+"";

                                        Myapplilcation.getExecutorService().execute(new Runnable() {
                                            @Override
                                            public void run() {
                                                NetUtils.updataarguments3dingle(dbopenhelper,"InstructorTeach",upid);
                                            }
                                        });
                                    }else {}

                                } else {
                                    dbopenhelper.update("InstructorTeach",
                                            new String[]{"InstructorId", "TeachPlace", "JoinCount",
                                                    "TeachStart", "TeachEnd", "TeachContent", "IsUploaded"},
                                            new Object[]{personId,address.getText().toString(),
                                                    personcount.getText().toString(),coursedate.getText().toString()+" "+startt.getText().toString(),
                                                    coursedate.getText().toString()+" "+endt.getText().toString(),daily_course_aim.getText().toString()
                                                    ,uploadedid}, new String[]{"Id"}, new String[]{intentid + ""});

                                    Myapplilcation.getExecutorService().execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            NetUtils.updataarguments3dingle(dbopenhelper,"InstructorTeach",intentid+"");
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
        if (course_popuwindow.getVisibility()!=View.GONE){
            course_popuwindow.setVisibility(View.GONE);
        }else {
            super.onBackPressed();
        }
    }
    
}
