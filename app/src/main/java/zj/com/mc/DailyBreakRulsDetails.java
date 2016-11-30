package zj.com.mc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import DBUtils.DBOpenHelper;
import Utils.CommonAdapter;
import Utils.NetUtils;
import Utils.ViewHolder;

/**
 * Created by dell on 2016/8/11.
 */
public class DailyBreakRulsDetails extends Activity implements View.OnClickListener{


    private DBOpenHelper dbOpenHelper;
    private List<Map> listsearch1;
    private List<Map> list;
    private List<TextView>viewList;
    private List<EditText> edList;
    private ListView listView;
    private int intentid;//判断跳转码
    private TextView whichtv;//中转
    private String key2;//弹窗内edittext中的文字
    private String personid,personID;
    private RelativeLayout breakruls_popuwindow;
    private int SAVECONTER=1;
    private TextView breakruls_takedate,breakruls_TrainCode;
    private  EditText breakruls_aim,breakruls_problem,breakruls_dopinion,brealruls_LocomotiveType,txtkey2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dailybreakrulsadditem1);
        dbOpenHelper= DBOpenHelper.getInstance(getApplicationContext());
        personID=getSharedPreferences("PersonInfo",MODE_PRIVATE).getString("PersonId",null);
        initview();
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("InstructorPeccancy");
        intentid = bundle.getInt("listitemId");

        if (intentid == -1) {
//            Toast.makeText(this, "空的", Toast.LENGTH_SHORT).show();
        } else {

//            Toast.makeText(this, "有参数", Toast.LENGTH_SHORT).show();
            //设置title
            String sql = "select * from InstructorPeccancy" +
                    " where Id  =? ";
            String[] selectionArgs = new String[]{intentid + ""};
            list = dbOpenHelper.queryListMap(sql, selectionArgs);
            SAVECONTER = (Integer) list.get(0).get("IsUploaded");
            //执行控件赋值

            initviewtext();

        }
    }

    private void initviewtext() {


        personid=list.get(0).get("DriverId")+"";
        breakruls_TrainCode.setText(UtilisClass.getName(dbOpenHelper,personid));
        breakruls_takedate.setText(list.get(0).get("WriteDate").toString());
//                breakruls_TrainCode.setText(list.get(0).get("DriverId").toString());
        brealruls_LocomotiveType.setText(list.get(0).get("PeccancyType").toString());
                breakruls_aim.setText(list.get(0).get("GeneralSituation").toString());
        breakruls_problem.setText(list.get(0).get("Analysis").toString());
                breakruls_dopinion.setText(list.get(0).get("Suggests").toString());


    }

    private void initview() {

        breakruls_popuwindow= (RelativeLayout) findViewById(R.id.breakruls_popuwindow);
        breakruls_popuwindow.setVisibility(View.GONE);



        breakruls_takedate= (TextView) findViewById(R.id.breakruls_takedate);//日期
        breakruls_takedate.setOnClickListener(this);
        breakruls_TrainCode= (TextView) findViewById(R.id.breakruls_TrainCode);//违章司机
        breakruls_TrainCode.setOnClickListener(this);
        listView= (ListView) findViewById(R.id.lstv_all);
        brealruls_LocomotiveType= (EditText) findViewById(R.id.brealruls_LocomotiveType);//性质：
        breakruls_aim= (EditText) findViewById(R.id.breakruls_aim);//概况
        breakruls_problem= (EditText) findViewById(R.id.breakruls_problem);//分析情况
        breakruls_dopinion= (EditText) findViewById(R.id.breakruls_dopinion);//处理意见
        txtkey2= (EditText) findViewById(R.id.breakruls_edit_key2);


        findViewById(R.id.breakruls_save).setOnClickListener(this);//保存
        findViewById(R.id.breakruls_saveup).setOnClickListener(this);//保存并上传
        findViewById(R.id.breakruls_cancle).setOnClickListener(this);//取消
        findViewById(R.id.daily_breakruls_titleback).setOnClickListener(this);//返回键


        findViewById(R.id.breakruls_popuwindow_yes).setOnClickListener(this);//弹窗确定键

        viewList=new ArrayList<TextView>();
        viewList.add(breakruls_takedate);
        viewList.add(breakruls_TrainCode);
        edList=new ArrayList<EditText>();
        edList.add(brealruls_LocomotiveType);
        edList.add(breakruls_aim);
        edList.add(breakruls_problem);
        edList.add(breakruls_dopinion);


    }


    //吐司
    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    //搜索view表中的数据
    private List<Map> getDBpersonname(String tablename, String coads){

        String sql= "select * from " +tablename+
                " where WorkNo like ? or Name like ? or Spell like ? ";
        String [] selectionArgs  = new String[]{"%" + coads + "%",
                "%" + coads + "%",
                "%" + coads + "%"};
        List<Map> listsearch = dbOpenHelper.queryListMap(sql, selectionArgs);
        Log.i("listsearch", String.valueOf(listsearch));
        return  listsearch;

    };

    //设置输入框
    @Override
    public void onBackPressed() {
        if (breakruls_popuwindow.getVisibility()!= View.GONE){
            breakruls_popuwindow.setVisibility(View.GONE);
        }else {
            super.onBackPressed();
        }
    }
//    判断view中的内容  保存调动
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
                                    dbOpenHelper.insert("InstructorPeccancy", new String[]{"InstructorId","WriteDate","DriverId",
                                            "PeccancyType","GeneralSituation","Analysis","Suggests","IsUploaded"},
                                            new Object[]{personID,breakruls_takedate.getText().toString(),
                                                    personid,brealruls_LocomotiveType.getText().toString(),
                                                    breakruls_aim.getText().toString(), breakruls_problem.getText().toString(),
                                                    breakruls_dopinion.getText().toString(), uploadedid});

                                } else {
                                    dbOpenHelper.update("InstructorPeccancy",
                                            new String[]{"InstructorId","WriteDate","DriverId",
                                                    "PeccancyType","GeneralSituation","Analysis","Suggests","IsUploaded"},
                                            new Object[]{personID,breakruls_takedate.getText().toString(),
                                                    personid,brealruls_LocomotiveType.getText().toString(),
                                                    breakruls_aim.getText().toString(), breakruls_problem.getText().toString(),
                                                    breakruls_dopinion.getText().toString(), uploadedid},
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

                                    dbOpenHelper.insert("InstructorPeccancy", new String[]{"InstructorId","WriteDate","DriverId",
                                            "PeccancyType","GeneralSituation","Analysis","Suggests","IsUploaded"},
                                            new Object[]{personID,breakruls_takedate.getText().toString(),
                                                    personid,brealruls_LocomotiveType.getText().toString(),
                                                    breakruls_aim.getText().toString(), breakruls_problem.getText().toString(),
                                                    breakruls_dopinion.getText().toString(), uploadedid});
                                    List<Map> mapListid=dbOpenHelper.queryListMap("select * from InstructorPeccancy where InstructorId=?",new String[]{getSharedPreferences("PersonInfo",MODE_PRIVATE).getString("PersonId",null)});
                                    if (mapListid.size()!=0){
                                        final String upid=mapListid.get(mapListid.size()-1).get("Id")+"";

                                        Myapplilcation.getExecutorService().execute(new Runnable() {
                                            @Override
                                            public void run() {
                                                NetUtils.updataarguments3dingle(dbOpenHelper,"InstructorPeccancy",upid);
                                            }
                                        });
                                    }else {}


                                } else {
                                    dbOpenHelper.update("InstructorPeccancy",
                                            new String[]{"InstructorId","WriteDate","DriverId",
                                                    "PeccancyType","GeneralSituation","Analysis","Suggests","IsUploaded"},
                                            new Object[]{personID,breakruls_takedate.getText().toString(),
                                                    personid,brealruls_LocomotiveType.getText().toString(),
                                                    breakruls_aim.getText().toString(), breakruls_problem.getText().toString(),
                                                    breakruls_dopinion.getText().toString(), uploadedid},
                                            new String[]{"Id"}, new String[]{intentid + ""});

                                        Myapplilcation.getExecutorService().execute(new Runnable() {
                                            @Override
                                            public void run() {
                                                NetUtils.updataarguments3dingle(dbOpenHelper,"InstructorPeccancy",intentid+"");
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

    @Override
    public void onClick(View view) {
        if (SAVECONTER == 1){
        switch (view.getId()) {
            case R.id.daily_breakruls_titleback:
                //返回

                UtilisClass.hidInputMethodManager(DailyBreakRulsDetails.this,brealruls_LocomotiveType);

                finish();

                break;
            case R.id.breakruls_takedate:

//日期
                UtilisClass.getdatepicker(DailyBreakRulsDetails.this, breakruls_takedate);

                break;
            case R.id.breakruls_TrainCode:
//违章司机

                breakruls_popuwindow.setVisibility(View.VISIBLE);
                getsearch(breakruls_TrainCode, "ViewPersonInfo");

                break;
            case R.id.breakruls_save:
//保存
                if (SAVECONTER == 1) {

                    getisempty(viewList, edList, 1);
                    UtilisClass.hidInputMethodManager(DailyBreakRulsDetails.this,brealruls_LocomotiveType);

//                    showToast("保存");
//                    finish();
                } else {
                    showToast("保存失败，文件已上传！");
                }
                UtilisClass.showToast(DailyBreakRulsDetails.this,personid);
                break;
            case R.id.breakruls_saveup:
                //保存并上传
                if (SAVECONTER == 1) {

                    getisempty2(viewList, edList, 0);
                    UtilisClass.hidInputMethodManager(DailyBreakRulsDetails.this,brealruls_LocomotiveType);

                    showToast("保存成功");
                    //执行上传代码
                    SAVECONTER = 0;
                } else {
                    showToast("保存失败，文件已上传！");
                }
                break;
            case R.id.breakruls_cancle:
                //取消
                UtilisClass.hidInputMethodManager(DailyBreakRulsDetails.this,brealruls_LocomotiveType);

                finish();
                break;

            case R.id.breakruls_popuwindow_yes:


                breakruls_popuwindow.setVisibility(View.GONE);
                whichtv.setText(key2);
                UtilisClass.setDriverid(DailyBreakRulsDetails.this,whichtv,breakruls_TrainCode,personid);

                break;
        }
        }else {
            switch (view.getId()) {
                case R.id.daily_breakruls_titleback:
                    //返回
                    UtilisClass.hidInputMethodManager(DailyBreakRulsDetails.this,brealruls_LocomotiveType);

                    finish();

                    break;
                case R.id.breakruls_cancle:
                    //取消
                    UtilisClass.hidInputMethodManager(DailyBreakRulsDetails.this,brealruls_LocomotiveType);

                    finish();
                    break;

            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UtilisClass.hidInputMethodManager(DailyBreakRulsDetails.this,brealruls_LocomotiveType);

    }

    private void getsearch(final TextView ed, final String tablename){
    txtkey2.setText("");
    txtkey2.setFocusable(true);
    txtkey2.setFocusableInTouchMode(true);
    txtkey2.requestFocus();
    InputMethodManager inputManager =
            (InputMethodManager)txtkey2.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
    inputManager.showSoftInput(txtkey2, 0);


    whichtv=ed;
    if (tablename.equals("ViewPersonInfo")) {
        txtkey2.setHint("支持工号，姓名，拼音简称搜索");
    }else {}


    txtkey2.addTextChangedListener(new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            UtilisClass.setdriverid2(whichtv,breakruls_TrainCode,personid);


            key2=  txtkey2.getText().toString();
            if(key2!=null && !"".equals(key2.trim())){
                //获取数据

                    listsearch1 = getDBpersonname("ViewPersonInfo", key2);

                listView.setAdapter(new CommonAdapter<Map>(DailyBreakRulsDetails.this,listsearch1, R.layout.editlist1) {

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
                        }
                    }
                });


                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        if (tablename.equals("ViewPersonInfo")) {
                            txtkey2.setText(listsearch1.get(i).get("Name").toString());
                            personid=listsearch1.get(0).get("Id")+"";
                                UtilisClass.showToast(DailyBreakRulsDetails.this,personid);
                        }else {
                        }
                        breakruls_popuwindow.setVisibility(View.GONE);
                        whichtv.setText(key2);
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


}
