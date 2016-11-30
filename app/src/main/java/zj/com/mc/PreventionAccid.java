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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import DBUtils.DBOpenHelper;
import Utils.CommonAdapter;
import Utils.NetUtils;
import Utils.ViewHolder;

/**
 * Created by dell on 2016/8/10.
 */
public class PreventionAccid extends Activity implements View.OnClickListener{
//防止事故及好人好事

    private DBOpenHelper dbOpenHelper;
    private List<Map> listsearch;
    private List<Map> list;
    private List<TextView>viewList;
    private List<EditText> edList;
    private ListView listView;
    private int intentid;//判断跳转码
    private TextView whichtv;//中转
    private String key;//弹窗内edittext中的文字
    private String personid;
    private RelativeLayout goodp_popuwindow;
    private int SAVECONTER=1;
    private TextView goodp_takedate,goodp_TrainCode;
    private  EditText goodp_aim,goodp_dopinion,goodp_LocomotiveType,txtkey;
    private String personID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preventionaccid1);
        dbOpenHelper= DBOpenHelper.getInstance(getApplicationContext());

        initview();
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("InstructorGoodJob");
        intentid = bundle.getInt("listitemId");
        personID=getSharedPreferences("PersonInfo",MODE_PRIVATE).getString("PersonId",null);

        if (intentid == -1) {
//            Toast.makeText(this, "空的", Toast.LENGTH_SHORT).show();
        } else {

//            Toast.makeText(this, "有参数", Toast.LENGTH_SHORT).show();
            //设置title
            String sql = "select * from InstructorGoodJob" +
                    " where Id  =? ";
            String[] selectionArgs = new String[]{intentid + ""};
            list = dbOpenHelper.queryListMap(sql, selectionArgs);
            SAVECONTER = (Integer) list.get(0).get("IsUploaded");
            //执行控件赋值
            initviewtext();

        }
    }

    private void initviewtext() {
        goodp_takedate.setText(list.get(0).get("WriteDate").toString());
        personid=list.get(0).get("DriverId").toString();
        List<Map> maindriverinfo=dbOpenHelper.queryListMap("select * from PersonInfo where Id =?",new String[]{personid});
        if (maindriverinfo.size()!=0){
            goodp_TrainCode.setText(maindriverinfo.get(0).get("Name").toString());}
        goodp_LocomotiveType.setText(list.get(0).get("GoodJobType").toString());
        goodp_aim.setText(list.get(0).get("GeneralSituation").toString());
        goodp_dopinion.setText(list.get(0).get("Suggests").toString());


    }

    private void initview() {

        goodp_popuwindow= (RelativeLayout) findViewById(R.id.goodp_popuwindow);
        goodp_popuwindow.setVisibility(View.GONE);


        listView= (ListView) findViewById(R.id.goodp_listv);
        goodp_takedate= (TextView) findViewById(R.id.goodp_takedate);//日期
        goodp_takedate.setOnClickListener(this);
        goodp_TrainCode= (TextView) findViewById(R.id.goodp_TrainCode);//责任司机
        goodp_TrainCode.setOnClickListener(this);

        goodp_LocomotiveType= (EditText) findViewById(R.id.goodp_LocomotiveType);//性质：
        goodp_aim= (EditText) findViewById(R.id.goodp_aim);//概况
        goodp_dopinion= (EditText) findViewById(R.id.goodp_dopinion);//处理意见
        txtkey= (EditText) findViewById(R.id.goodp_edit_key);


        findViewById(R.id.goodp_save).setOnClickListener(this);//保存
        findViewById(R.id.goodp_saveup).setOnClickListener(this);//保存并上传
        findViewById(R.id.goodp_cancle).setOnClickListener(this);//取消
        findViewById(R.id.daily_goodp_titleback).setOnClickListener(this);//返回键


        findViewById(R.id.goodp_popuwindow_yes).setOnClickListener(this);//弹窗确定键

        viewList=new ArrayList<TextView>();
        viewList.add(goodp_takedate);
        viewList.add(goodp_TrainCode);
        edList=new ArrayList<EditText>();
        edList.add(goodp_LocomotiveType);
        edList.add(goodp_aim);
        edList.add(goodp_dopinion);


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

    //设置输入框
    @Override
    public void onBackPressed() {
        if (goodp_popuwindow.getVisibility()!= View.GONE){
            goodp_popuwindow.setVisibility(View.GONE);
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
                            if (intentid== -1) {
                                if (j == edList.size() - 1) {
                                    dbOpenHelper.insert("InstructorGoodJob", new String[]{"InstructorId","WriteDate","DriverId",
                                                    "GoodJobType","GeneralSituation","Suggests","IsUploaded"},
                                            new Object[]{personID,goodp_takedate.getText().toString(),
                                                    personid,goodp_LocomotiveType.getText().toString(),
                                                    goodp_aim.getText().toString(),
                                                    goodp_dopinion.getText().toString(), uploadedid});

                                } else {
                                    dbOpenHelper.update("InstructorGoodJob",
                                            new String[]{"InstructorId","WriteDate","DriverId",
                                                    "GoodJobType","GeneralSituation","Suggests","IsUploaded"},
                                            new Object[]{personID,goodp_takedate.getText().toString(),
                                                    personid,goodp_LocomotiveType.getText().toString(),
                                                    goodp_aim.getText().toString(),
                                                    goodp_dopinion.getText().toString(), uploadedid},
                                            new String[]{"Id"}, new String[]{intentid + ""});
                                }
                            }
                            finish();

                        }

                    }
                }
            }
        }
    }
    //    判断view中的内容  保存调动
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
                                    dbOpenHelper.insert("InstructorGoodJob", new String[]{"InstructorId","WriteDate","DriverId",
                                                    "GoodJobType","GeneralSituation","Suggests","IsUploaded"},
                                            new Object[]{personID,goodp_takedate.getText().toString(),
                                                    personid,goodp_LocomotiveType.getText().toString(),
                                                    goodp_aim.getText().toString(),
                                                    goodp_dopinion.getText().toString(), uploadedid});
                                    List<Map> mapListid=dbOpenHelper.queryListMap("select * from InstructorGoodJob where InstructorId=?",new String[]{personID});
                                    if (mapListid.size()!=0){
                                        final String upid=mapListid.get(mapListid.size()-1).get("Id")+"";

                                        Myapplilcation.getExecutorService().execute(new Runnable() {
                                            @Override
                                            public void run() {
                                                NetUtils.updataarguments3dingle(dbOpenHelper,"InstructorGoodJob",upid);
                                            }
                                        });
                                    }else {}

                                } else {
                                    dbOpenHelper.update("InstructorGoodJob",
                                            new String[]{"InstructorId","WriteDate","DriverId",
                                                    "GoodJobType","GeneralSituation","Suggests","IsUploaded"},
                                            new Object[]{personID,goodp_takedate.getText().toString(),
                                                    personid,goodp_LocomotiveType.getText().toString(),
                                                    goodp_aim.getText().toString(),
                                                    goodp_dopinion.getText().toString(), uploadedid},
                                            new String[]{"Id"}, new String[]{intentid + ""});


                                        Myapplilcation.getExecutorService().execute(new Runnable() {
                                            @Override
                                            public void run() {
                                                NetUtils.updataarguments3dingle(dbOpenHelper,"InstructorGoodJob",intentid+"");
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
    public void onClick(View view) {
        if (SAVECONTER==1) {
            switch (view.getId()) {
                case R.id.daily_goodp_titleback:
                    //返回

                    UtilisClass.hidInputMethodManager(PreventionAccid.this,goodp_LocomotiveType);

                    finish();

                    break;
                case R.id.goodp_takedate:

//日期
                    UtilisClass.getdatepicker(PreventionAccid.this, goodp_takedate);

                    break;
                case R.id.goodp_TrainCode:
//违章司机

                    goodp_popuwindow.setVisibility(View.VISIBLE);
                    getsearch(goodp_TrainCode, "ViewPersonInfo");

                    break;
                case R.id.goodp_save:
                    //保存
                    if (SAVECONTER == 1) {

                        getisempty(viewList, edList, 1);
                        showToast("保存成功");
                        UtilisClass.hidInputMethodManager(PreventionAccid.this,goodp_LocomotiveType);

//                        finish();
                    } else {
                        showToast("保存失败，文件已上传！");
                    }

                    break;
                case R.id.goodp_saveup:
                    //保存并上传
                    if (SAVECONTER == 1) {

                        getisempty2(viewList, edList, 0);
                        showToast("保存成功");
                        //执行上传代码
                        UtilisClass.hidInputMethodManager(PreventionAccid.this,goodp_LocomotiveType);

                        SAVECONTER=0;
                    } else {
                        showToast("保存失败，文件已上传！");
                    }
                    break;
                case R.id.goodp_cancle:
                    //取消
                    UtilisClass.hidInputMethodManager(PreventionAccid.this,goodp_LocomotiveType);

                    finish();
                    break;

                case R.id.goodp_popuwindow_yes:


                    goodp_popuwindow.setVisibility(View.GONE);
                    whichtv.setText(key);
                    UtilisClass.setDriverid(PreventionAccid.this,whichtv,goodp_TrainCode,personid);
                    break;
            }
        }else {
            switch (view.getId()) {
                case R.id.daily_goodp_titleback:
                    //返回
                    UtilisClass.hidInputMethodManager(PreventionAccid.this,goodp_LocomotiveType);

                    finish();

                    break;
                case R.id.goodp_cancle:
                    //取消
                    UtilisClass.hidInputMethodManager(PreventionAccid.this,goodp_LocomotiveType);

                    finish();
                    break;
            }
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
        txtkey.setHint("支持工号，姓名，拼音简称搜索");

        txtkey.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                UtilisClass.setdriverid2(whichtv,goodp_TrainCode,personid);


                key=  txtkey.getText().toString();
                if(!key.equals("")){
                    //获取数据
                    listsearch= getDBpersonname(tablename, key);

                    if (listsearch.size()!=0){
                        listView.setAdapter(new CommonAdapter<Map>(PreventionAccid.this,listsearch, R.layout.editlist1) {

                            @Override
                            protected void convertlistener(ViewHolder holder, final Map map) {
                            }

                            @Override
                            public void convert(ViewHolder holder, Map map) {
                                holder.setText(R.id.editlist_item1,map.get("WorkNo").toString());
                                holder.setText(R.id.editlist_item2,map.get("Name").toString());
                                holder.setText(R.id.editlist_item3,map.get("Spell").toString());
                            }
                        });

                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                if (tablename.equals("ViewPersonInfo")) {
                                txtkey.setText(listsearch.get(i).get("Name").toString());
                                personid= listsearch.get(0).get("Id")+"";
                            }
                                goodp_popuwindow.setVisibility(View.GONE);
                                whichtv.setText(key);
                            }

                        });

                    }else{
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
}
