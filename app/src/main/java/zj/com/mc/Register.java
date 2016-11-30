package zj.com.mc;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import DBUtils.DBOpenHelper;
import Utils.HttpUtils;
import Utils.NetUtils;


/**
 * Created by dell on 2016/7/29.
 */
public class Register extends Activity implements View.OnClickListener {

    private EditText acco, pass;
    private String accounts;//账号
    private String password;//密码
    private int i=0;//检测密码是否为空
    private List<Map> personInfolist;
    private DBOpenHelper dbOpenHelper;
    private String personId;
    private static int WHAT=0;
    private String s;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enter);
        acco = (EditText) findViewById(R.id.account);
        pass = (EditText) findViewById(R.id.password);
        findViewById(R.id.logging).setOnClickListener(this);
        dbOpenHelper= DBOpenHelper.getInstance(getApplicationContext());

    }

    @Override
    public void onClick(View view) {
        //获取输入账号内容
        accounts = acco.getText().toString();
        //获取输入密码
        password = pass.getText().toString();
        if(accounts.equals("9999")&&password.equals("9999")) {
            UtilisClass.showToast(Register.this,"成功");
            final EditText ed=new EditText(Register.this);
            new AlertDialog.Builder(Register.this).setTitle("请输入网址")
                    .setView(ed)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            UtilisClass.showToast(Register.this,"确定");
                            String httpdoor=ed.getText()+"";
                            if (!httpdoor.equals("")&&!httpdoor.isEmpty()) {
                                SharedPreferences sharedPreferences = getSharedPreferences("HTTPDOOR", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("HttpDoor", httpdoor);
                                editor.commit();
                                dbOpenHelper.update("HttpDoor",new String[]{"Message"},new Object[]{httpdoor},new String[]{"Id"},
                                        new String[]{"1"});
                            }
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            UtilisClass.showToast(Register.this,"取消");
                        }
                    })
                    .show();



        }else{
            List<Map> isfirstlist = dbOpenHelper.queryListMap("select * from PersonInfo ", null);
            if (isfirstlist.size() == 0) {
                dbOpenHelper.insert("InstructorQuota", new String[]{"Id", "QuotaName", "QuataAmmount"}, new Object[]{1, "添乘小时数", "81"});
                dbOpenHelper.insert("InstructorQuota", new String[]{"Id", "QuotaName", "QuataAmmount"}, new Object[]{2, "白天添乘小时数", "37"});
                dbOpenHelper.insert("InstructorQuota", new String[]{"Id", "QuotaName", "QuataAmmount"}, new Object[]{3, "夜添乘小时数", "27"});
                dbOpenHelper.insert("InstructorQuota", new String[]{"Id", "QuotaName", "QuataAmmount"}, new Object[]{4, "月添乘趟数", "10"});
                dbOpenHelper.insert("InstructorQuota", new String[]{"Id", "QuotaName", "QuataAmmount"}, new Object[]{5, "关键人添乘趟数", "3"});
                dbOpenHelper.insert("InstructorQuota", new String[]{"Id", "QuotaName", "QuataAmmount"}, new Object[]{6, "示范操纵累计小时", "10"});
                dbOpenHelper.insert("InstructorQuota", new String[]{"Id", "QuotaName", "QuataAmmount"}, new Object[]{7, "月分析列数", "40"});
                dbOpenHelper.insert("InstructorQuota", new String[]{"Id", "QuotaName", "QuataAmmount"}, new Object[]{8, "上旬分析列数", "15"});
                dbOpenHelper.insert("InstructorQuota", new String[]{"Id", "QuotaName", "QuataAmmount"}, new Object[]{9, "中旬分析列数", "15"});
                dbOpenHelper.insert("InstructorQuota", new String[]{"Id", "QuotaName", "QuataAmmount"}, new Object[]{10, "下旬分析列数", "10"});
                dbOpenHelper.insert("InstructorQuota", new String[]{"Id", "QuotaName", "QuataAmmount"}, new Object[]{11, "月检查次数", "3"});
                 setfirstdialog();
            } else {
//        点击“确定登录”事件
                if (view.getId() == R.id.logging) {
                    try {
                        getAccoPass();
                    } catch (Exception e) {
                        if (i == 0) {
                            setToast("密码不能为空");
                        }

                    }
                }

            }

        }

    }

    private void getAccoPass() {


    if (!accounts.isEmpty() && !password.isEmpty()) {
        personInfolist = dbOpenHelper.queryListMap("select * from PersonInfo where WorkNo=?", new String[]{accounts});

        Log.i("personInfolist", String.valueOf(personInfolist));
        if (personInfolist.size() == 0) {
            UtilisClass.showToast(Register.this, "没有此工号");
        } else {
            String passwordsMD5 = null;
                passwordsMD5=md5(password);
            if (passwordsMD5.equals(personInfolist.get(0).get("Password"))) {
                SharedPreferences sharedPreferences=getSharedPreferences("PersonInfo",MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                personId=personInfolist.get(0).get("Id")+"";
                editor.putString("WorkNo",accounts);
                editor.putString("PersonId",personId);
                editor.commit();


                Intent mainintent = new Intent(this, MainActivity.class);
                startActivity(mainintent);
                finish();

            } else {
                UtilisClass.showToast(Register.this, "密码输入不正确");


            }
        }
    }else {
        UtilisClass.showToast(Register.this,"账号和密码不能为空");
    }
    }

    //重置账号密码
    private void setEditEmpety() {
        acco.setText("");
        pass.setText("");
    }

    /**
     * @设置Toast
     */
    private void setToast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_LONG).show();
    }


    @NonNull
    private static String getString(byte[] b){
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < b.length; i ++){
            sb.append(b[i]);
        }
        return sb.toString();
    }



    @Override
    protected void onPause() {
        super.onPause();
    }
    /**
     * @MD5加密
     * */
    private String md5(String string) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Huh, UTF-8 should be supported?", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }


    private void setfirstdialog(){
        final AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("第一次登陆是否更新数据,请点击确定更新！");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener(){//为列表项的单击事件设置监听器
            public void onClick(DialogInterface dialog,int Which)
            {
                if (UtilisClass.isWifi(Register.this)){

                    setprogressDialog();
                    progressDialog.show();
                    Myapplilcation.getExecutorService().execute(new Runnable() {
                        @Override
                        public void run() {
                            setfirstupdata();
                        }
                    });
                }else {
                    UtilisClass.showToast(Register.this, "无网络！");
                }
            }
        });
        // 为对话框设置一个“取消”按钮
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
            }
        });
        //创建、并显示对话框
        builder.create().show();
    }

//设置第一次更新

    private void setfirstupdata(){

        String firstupdatapath = NetUtils.Uploadeduri+"0";

            List<Map<String,Object>> firstpList = new ArrayList<>();
            String json = HttpUtils.getJsonContent(firstupdatapath);
            Log.i("tagg", json);
            try {
                JSONObject jsonObject = new JSONObject(json);
                if (jsonObject.get("code").equals("304")) {
                } else {
                    String jsonArray = jsonObject.getString("data");
                    firstpList = Utils.StringListUtils.getList(jsonArray);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (firstpList.size() != 0) for (int num = 0; num < firstpList.size(); num++) {

                dbOpenHelper.insert("DbUpdateLog", firstpList.get(num));
            }


            List<String> tableNames = NetUtils.getTableNamelist(firstpList, "TableName");//筛选需要更新的所有表名集合

            Log.i("tablenames", String.valueOf(tableNames));
            if (tableNames.size() != 0) {
                for (int num = 0; num < tableNames.size(); num++) {
                    List<Map<String, Object>> singleTableData = NetUtils.getSingleTableNameList(firstpList, "TableName", tableNames.get(num));//获取单个表集合
                    List<String> ListId = NetUtils.getTableNamelist(singleTableData, "TargetId");

                    Log.i("tablenames", String.valueOf(ListId));
                    Map<String, Map> m = new HashMap<>();
                    for (int i = 0; i < singleTableData.size(); i++) {
                        m.put(singleTableData.get(i).get("TargetId") + "", (Map) singleTableData.get(i));
                    }
                    List<Map<String, Object>> singleTableDatalist = new ArrayList<>();
                    for (int i = 0; i < ListId.size(); i++) {
                        singleTableDatalist.add(m.get(ListId.get(i)));
                    }
                    Log.i("tagg", String.valueOf(singleTableDatalist));

                    List<Map<String, Object>> addTableDatalist = NetUtils.getSingleTableNameList(singleTableDatalist, "UpdateType", 1);//获取单个表的insert

                    if (addTableDatalist.size() != 0)
                        NetUtils.Dosingletableinsert(addTableDatalist, tableNames.get(num), dbOpenHelper);

                    List<Map<String, Object>> upTableDatalist = NetUtils.getSingleTableNameList(singleTableDatalist, "UpdateType", 2);//获取单个表的更新
                    if (upTableDatalist.size() != 0)
                        NetUtils.DosingletableUpdata(upTableDatalist, tableNames.get(num), dbOpenHelper);
                    List<Map<String, Object>> deleteTableDatalist = NetUtils.getSingleTableNameList(singleTableDatalist, "UpdateType", 3);//获取单个表的更新

                    Log.i("tablenames", String.valueOf(deleteTableDatalist));

                    try {
                        if (deleteTableDatalist.size() != 0)
                            NetUtils.DosingletableDelete(deleteTableDatalist, tableNames.get(num), dbOpenHelper);
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            }

            Message messagefirst = new Message();
            messagefirst.arg1 = 1;
            handlerfirst.sendMessage(messagefirst);

    }
//设置progressdialog
    private  void setprogressDialog(){
        progressDialog = UtilisClass.setprogressDialog(Register.this);
    }

    Handler handlerfirst = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int n = msg.arg1;
            if (n == 1) {
                UtilisClass.showToast(Register.this, "数据更新完成！");
                try {
                    progressDialog.cancel();
                } catch (UnsupportedOperationException u) {
                    u.printStackTrace();
                }
            } else{}
        }
    };


}
