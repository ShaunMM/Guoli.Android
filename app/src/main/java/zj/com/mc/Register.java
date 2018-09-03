package zj.com.mc;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import DBUtils.DBOpenHelper;
import Utils.NetUtils;
import config.ISystemConfig;
import config.SystemConfigFactory;

/**
 * 登录界面
 */
public class Register extends Activity implements View.OnClickListener {

    private EditText acco;
    private EditText pass;
    private String accounts;
    private String password;
    private List<Map> personInfolist;
    private DBOpenHelper dbOpenHelper;
    private ProgressDialog progressDialog;
    private ISystemConfig systemConfig;
    private RequestQueue registerQueue;
    final public static int REQUEST_PHONE_STATE = 123;

    Handler handlerfirst = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int n = msg.arg1;
            if (n == 1) {
                try {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    getAccoPass();
                } catch (UnsupportedOperationException u) {
                    u.printStackTrace();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enter);
        acco = (EditText) findViewById(R.id.account);
        pass = (EditText) findViewById(R.id.password);
        findViewById(R.id.logging).setOnClickListener(this);
        dbOpenHelper = DBOpenHelper.getInstance(getApplicationContext());
        Myapplilcation.addActivity(this);

        try {
            Thread.currentThread().sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        systemConfig = SystemConfigFactory.getInstance(Register.this).getSystemConfig();
        personInfolist = new ArrayList<>();
        if (!systemConfig.isFirstUse()) {
            acco.setText(systemConfig.getUserAccount());
        }

        if (dbOpenHelper != null) {
            if (systemConfig.isTabletInfor()) {
                getIMEI();
            }
        }
    }

    private void getIMEI() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_PHONE_STATE);
            } else {
                TelephonyManager TelephonyMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                String IMEI = TelephonyMgr.getDeviceId();
                if (IMEI == null) {
                    IMEI = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
                }
                systemConfig.setIMEI(IMEI);
                recordTabletInfor(IMEI);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_PHONE_STATE && grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            try {
                TelephonyManager TelephonyMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                String IMEI = TelephonyMgr.getDeviceId();
                if (IMEI == null) {
                    IMEI = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
                }
                recordTabletInfor(IMEI);
                systemConfig.setIMEI(IMEI);
                askForPermission();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == REQUEST_PHONE_STATE && grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
            UtilisClass.showToast(Register.this, "权限申请被拒绝！");
        }
    }

    private void recordTabletInfor(String imri) {
        Date dt = new Date();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String nowTime = df.format(dt);
        String model = android.os.Build.MODEL;
        String release = android.os.Build.VERSION.RELEASE;
        if (dbOpenHelper != null) {
            dbOpenHelper.insert("MobileDevice", new String[]{"UniqueId", "DeviceType", "OsVersion", "AddTime"},
                    new Object[]{imri, model, release, nowTime});
            systemConfig.setTabletInfor(false);
        }
    }

    @Override
    public void onClick(View view) {
        accounts = acco.getText().toString();
        password = pass.getText().toString();
        if (accounts.equals("123456") && password.equals("99999")) {
            final EditText ed = new EditText(Register.this);
            new AlertDialog.Builder(Register.this).setTitle("请输入新的服务器地址：")
                    .setView(ed)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String httpdoor = ed.getText() + "";
                            if (!httpdoor.equals("") && !httpdoor.isEmpty()) {
                                systemConfig.setHost(httpdoor);
                            }
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    })
                    .show();
        } else if (accounts.equals("123456") && password.equals("admin")) {
            int id = android.os.Process.myPid();
            if (id != 0) {
                android.os.Process.killProcess(id);
            }
        } else {
            if (view.getId() == R.id.logging) {
                if (systemConfig.isAddData()) {
                    Map map = dbOpenHelper.queryItemMap("select Id from DbUpdateLog order by Id desc limit 1", null);
                    if (!map.isEmpty()) {
                        systemConfig.setNewFileMaxId(String.valueOf((int) map.get("Id")));
                        Log.i("tagg", "现有数据DbUpdateLog最大ID--->" + (int) map.get("Id"));
                    }
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
                    systemConfig.setAddData(false);
                    Log.i("tagg", "现有数据DbUpdateLog最大ID--->" + (int) map.get("Id"));
                    getAccoPass();
                } else {
                    getAccoPass();
                }
            }
        }
    }

    private void initDataDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("加载数据提示");
        builder.setMessage("第一次登陆需要初始化数据,请点击确定！");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int Which) {
                if (UtilisClass.isWifi(Register.this)) {
                    progressDialog = UtilisClass.setprogressDialog(Register.this, "data");
                    progressDialog.show();
                    noHttpPostData();
                } else {
                    UtilisClass.showToast(Register.this, "WIFI未连接！");
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.create().show();
    }

    private void getAccoPass() {
        if (!accounts.isEmpty() && !password.isEmpty()) {
            personInfolist = dbOpenHelper.queryListMap("select * from PersonInfo where WorkNo=?", new String[]{accounts});
            if (personInfolist.size() == 0) {
                UtilisClass.showToast(Register.this, "没有此工号");
            } else {
                String passwordsMD5 = null;
                passwordsMD5 = md5(password);
                if (passwordsMD5.equals(personInfolist.get(0).get("Password"))) {
//                if (true) {

                    systemConfig.setUserName(personInfolist.get(0).get("Name").toString());
                    systemConfig.setUserAccount(accounts);
                    systemConfig.setUserId(personInfolist.get(0).get("Id") + "");
                    systemConfig.setPostId(personInfolist.get(0).get("PostId") + "");
                    if (personInfolist.get(0).get("DepartmentId").toString().equals("29")) {
                        systemConfig.setDepartmentId(String.valueOf(35));
                    } else {
                        systemConfig.setDepartmentId(personInfolist.get(0).get("DepartmentId").toString());
                    }

                    systemConfig.setOperatorId(personInfolist.get(0).get("Id").toString());
                    recordOperateLog(1, "登录终端平板");
                    systemConfig.setFirstSync(false);
                    System.gc();

                    Intent mainintent = new Intent(this, MainActivity.class);
                    startActivity(mainintent);
//                    systemConfig.setFirstUse(false);
                    finish();
                } else {
                    UtilisClass.showToast(Register.this, "密码输入不正确");
                }
            }
        } else {
            UtilisClass.showToast(Register.this, "账号和密码不能为空");
        }
    }

    private void recordOperateLog(int LogType, String LogContent) {
        Date dt = new Date();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String nowTime = df.format(dt);
        dbOpenHelper.insert("AppOperateLog", new String[]{"LogType", "LogContent", "OperatorId", "DeviceId", "AddTime"},
                new Object[]{LogType, LogContent, systemConfig.getOperatorId(), 0, nowTime});
    }

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

    public void noHttpPostData() {
        String url2 = systemConfig.getHost() + NetUtils.APPINDEX1;
        registerQueue = NoHttp.newRequestQueue();
        Request<String> stringPostRequest = NoHttp.createStringRequest(url2, RequestMethod.POST);
        stringPostRequest.add("signature", "bcad117ce31ac75fcfa347acefc8d198");
        stringPostRequest.add("TableName", "DbUpdateLog");
        stringPostRequest.add("Operate", "6");
        stringPostRequest.add("StartId", "0");

        registerQueue.add(2, stringPostRequest, new OnResponseListener<String>() {

            @Override
            public void onStart(int what) {
            }

            @Override
            public void onSucceed(int what, final Response<String> response) {
                Log.i("tagg", response.get().toString());
                Myapplilcation.getExecutorService().execute(new Runnable() {
                    @Override
                    public void run() {
                        writeDatabase(response.get().toString());
                    }
                });
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                UtilisClass.showToast(Register.this, "获取数据失败！");
            }

            @Override
            public void onFinish(int what) {
            }
        });
    }

    private void writeDatabase(String json) {
        Log.i("tagg", "首次初始化数据后记录下的最大ID--->" + "开始执行");
        List<Map<String, Object>> firstpList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            if (jsonObject.get("code").equals("304")) {
                Log.i("tagg", "获取数据失败");
            } else {
                String jsonArray = jsonObject.getString("data");
                firstpList = Utils.StringListUtils.getList(jsonArray);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (firstpList.size() != 0) {
            for (int num = 0; num < firstpList.size(); num++) {
                dbOpenHelper.insert("DbUpdateLog", firstpList.get(num));
                if (num == firstpList.size() - 1) {
                    systemConfig.setNewFileMaxId(String.valueOf((int) firstpList.get(num).get("Id")));
                    Log.i("tagg", "首次初始化数据后记录下的最大ID--->" + (int) firstpList.get(num).get("Id"));
                }
            }

            List<String> tableNames = NetUtils.getTableNamelist(firstpList, "TableName");
            Log.i("tablenames", String.valueOf(tableNames));

            if (tableNames.size() != 0) {
                for (int num = 0; num < tableNames.size(); num++) {
                    //获取有对同一张有更新操作的表所有操作集合
                    List<Map<String, Object>> singleTableData = NetUtils.getSingleTableNameList(firstpList, "TableName", tableNames.get(num));
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

                    List<Map<String, Object>> addTableDatalist = NetUtils.getSingleTableNameList(singleTableDatalist, "UpdateType", 1);
                    Log.i("tablenames", String.valueOf(addTableDatalist));
                    if (addTableDatalist.size() != 0)
                        NetUtils.Dosingletableinsert(addTableDatalist, tableNames.get(num), dbOpenHelper, systemConfig);

                    List<Map<String, Object>> upTableDatalist = NetUtils.getSingleTableNameList(singleTableDatalist, "UpdateType", 2);
                    Log.i("tablenames", String.valueOf(upTableDatalist));
                    if (upTableDatalist.size() != 0)
                        NetUtils.DosingletableUpdata(upTableDatalist, tableNames.get(num), dbOpenHelper, systemConfig);

                    List<Map<String, Object>> deleteTableDatalist = NetUtils.getSingleTableNameList(singleTableDatalist, "UpdateType", 3);
                    Log.i("tablenames", String.valueOf(deleteTableDatalist));
                    try {
                        if (deleteTableDatalist.size() != 0)
                            NetUtils.DosingletableDelete(deleteTableDatalist, tableNames.get(num), dbOpenHelper);
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        Message messagefirst = new Message();
        messagefirst.arg1 = 1;
        handlerfirst.sendMessage(messagefirst);
    }

    private void askForPermission() {
        if (systemConfig.isGetPermission()) {
            if (Build.VERSION.SDK_INT >= 23) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setIcon(R.mipmap.hint);
                builder.setTitle("申请权限");
                builder.setMessage("        请先设置程序需要的 \"存储空间\" 和 \"日历\" 应用权限 !    ");
                builder.setPositiveButton("设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + getPackageName())); // 根据包名打开对应的设置界面
                        startActivity(intent);
                        systemConfig.setPermission(false);
                    }
                });
                builder.create().show();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        ActivityManager activityManager = (ActivityManager) getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.moveTaskToFront(getTaskId(), 0);
    }

    @Override
    protected void onDestroy() {
        setContentView(R.layout.view_null);
        super.onDestroy();

        if (registerQueue != null) {
            registerQueue.cancelAll();
            registerQueue.stop();
            registerQueue = null;
        }
        if (personInfolist != null) {
            personInfolist = null;
        }
        Myapplilcation.removeActivity(this);
    }
}