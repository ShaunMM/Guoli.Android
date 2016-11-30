package Fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.rest.CacheMode;
import com.yolanda.nohttp.rest.OnResponseListener;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

import DBUtils.DBOpenHelper;
import Utils.HttpUtils;
import Utils.NetUtils;
import Utils.SDCardHelper;
import zj.com.mc.AddRemoveKeyperson;
import zj.com.mc.Myapplilcation;
import zj.com.mc.R;
import zj.com.mc.UtilisClass;

/**
 * Created by dell on 2016/8/1.
 */
public class HomePage2 extends Fragment implements View.OnClickListener{
    //首页指导司机
    private TextView tv5, tv6, tv7, tv8, tv9, tv10, tv11, tv12, tv13, tv14, tv15, tv16, tv17, tv18, tv19, tv20, tv21, home2_conn14;
    private TextView home2_linkde1, home2_linkde2;
    private TextView home2_monthadd1_all, home2_monthadd2_all, home2_monthadd3_all, home2_monthadd4_all, home2_connadd1, home2_connadd2, home2_connadd3, home2_connadd4;
    private TextView home2_text1, home2_text2, home2_text3, home2_text4; //完成指标数
    private TextView home2_removerelation;//接触关键人
    private ImageView wifi_im;
    private TextView uploaded_time;
    private ImageView home2_iv;
    private DBOpenHelper dbOpenHelper;
    private List<Map> loginpersoninfo;
    private List<Map> Loginpersoninfo2;
    private String personinfo;
    private int AddMoveKeyPerson;
    private ArrayList<ScanResult> wifilist;
    private String workNo;
    private WifiManager wifiManager;
    private int WIFISTATION;
    private String loginpersonId;
    private SharedPreferences sharedPreferences;
    private String personId;
    private int searchid;
    private LinearLayout key_addlayout;
    private String UpDataId;
    private ProgressDialog updatadiolag;
    private ExecutorService upLoadPool;
    private List<Map<String, Object>> mapList;
    private List<String> promptlist;

    Intent serviceIntent;

    String string1, string2;
    private static int WHAT = 0;
    String responsejson;
    String urls;
    private WifiReceiver wifireceiver;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int n = msg.arg1;
            if (n == 1) {
                String uploadedtime=UtilisClass.getStringDate1();
                uploaded_time.setText("上次更新:"+uploadedtime);

                String toaststring= (String) msg.obj;
                System.out.println("????????????"+toaststring);
                TextView tv=new TextView(getActivity());
                tv.setPadding(40,20,0,0);


                try {
                    onrefresh();
                    updatadiolag.cancel();
                } catch (UnsupportedOperationException u) {
                    u.printStackTrace();
                }
                if (!toaststring.equals("")) {
                    tv.setText(toaststring +"数据有更新！");
                    new AlertDialog.Builder(getActivity())
                            .setTitle(R.string.diolagtitle)
                            .setView(tv)
                            .setNegativeButton(R.string.sure,null)
                            .show();
                }else {
                    UtilisClass.showToast(getActivity(),"数据更新完成！");
                }

            } else if (n == 2) {
                UtilisClass.showToast(getActivity(),"数据上传完成！");
                onrefresh();
                updatadiolag.cancel();
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.homepage2, container, false);


        wifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
        WIFISTATION = wifiManager.getWifiState();
        dbOpenHelper = DBOpenHelper.getInstance(getActivity().getApplication());
        mapList = new ArrayList<>();
        upLoadPool = Myapplilcation.getExecutorService();
        AddMoveKeyPerson = 1;//添加关键人
        initView(view);
        setdialog();
//        setUpDatas();
        getwifiinfo();



        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        wifireceiver=new WifiReceiver();
        getActivity().registerReceiver(wifireceiver, filter);


        return view;
    }






    @Override
    public void onResume() {
        super.onResume();
//获取完成指标
        initPersonFinish();
//获取量化指标
        initAllQualiteCount();
//重要提示
        initImportantNotice();
//最新公告
        initLatestAnnouncement();
//设置wifi显示设置
        initwifishow();
//设置关键人信息
        initkeyperson();
//司机头像设置
        setphotoImage();
    }

    private void initView(View v) {
        promptlist=new ArrayList<>();
        home2_monthadd1_all = (TextView) v.findViewById(R.id.home2_monthadd1_all);//总月添乘趟数
        home2_monthadd2_all = (TextView) v.findViewById(R.id.home2_monthadd2_all);//总月添乘小时
        home2_monthadd3_all = (TextView) v.findViewById(R.id.home2_monthadd3_all);//总分析列数
        home2_monthadd4_all = (TextView) v.findViewById(R.id.home2_monthadd4_all);//总检查次数
        home2_text1 = (TextView) v.findViewById(R.id.home2_monthadd1);
        home2_text2 = (TextView) v.findViewById(R.id.home2_monthadd2);
        home2_text3 = (TextView) v.findViewById(R.id.home2_monthadd3);
        home2_text4 = (TextView) v.findViewById(R.id.home2_monthadd4);

        tv5 = (TextView) v.findViewById(R.id.home2_finish1);//   月添乘趟数   完成
        tv19 = (TextView) v.findViewById(R.id.home2_finish2);//月添乘小时  完成
        tv20 = (TextView) v.findViewById(R.id.home2_finish3);// 分析列数 完成
        tv21 = (TextView) v.findViewById(R.id.home2_finish4);//检查次数  完成


        home2_iv = (ImageView) v.findViewById(R.id.home2_iv);//指导司机头像


        wifi_im = (ImageView) v.findViewById(R.id.wifi_im);//wifi图标
        sharedPreferences = getActivity().getSharedPreferences("PersonInfo", Context.MODE_PRIVATE);

        workNo = sharedPreferences.getString("WorkNo", null);
        personId = sharedPreferences.getString("PersonId", null);
//重要提示
        tv6 = (TextView) v.findViewById(R.id.home2_conn1);
        tv7 = (TextView) v.findViewById(R.id.home2_conn2);
        tv8 = (TextView) v.findViewById(R.id.home2_conn3);
        home2_connadd1 = (TextView) v.findViewById(R.id.home2_connadd1);
        home2_connadd2 = (TextView) v.findViewById(R.id.home2_connadd2);

        tv6.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);//设置下划线
        tv7.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);//设置下划线
        tv8.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);//设置下划线
        home2_connadd1.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);//设置下划线
        home2_connadd2.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);//设置下划线


//最新公告
        tv9 = (TextView) v.findViewById(R.id.home2_conn4);
        tv10 = (TextView) v.findViewById(R.id.home2_conn5);
        tv11 = (TextView) v.findViewById(R.id.home2_conn6);
        home2_connadd3 = (TextView) v.findViewById(R.id.home2_connadd3);
        home2_connadd4 = (TextView) v.findViewById(R.id.home2_connadd4);

        tv9 .getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);//设置下划线
        tv10.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);//设置下划线
        tv11.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);//设置下划线
        home2_connadd3.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);//设置下划线
        home2_connadd4.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);//设置下划线

        key_addlayout = (LinearLayout) v.findViewById(R.id.key_addlayout);//关键人信息
        tv12 = (TextView) v.findViewById(R.id.home2_conn7);//关键人姓名
        tv13 = (TextView) v.findViewById(R.id.home2_conn8);//关键人id
        tv14 = (TextView) v.findViewById(R.id.home2_conn9);//添乘趟数
        tv15 = (TextView) v.findViewById(R.id.home2_conn10);//分析列数
        tv16 = (TextView) v.findViewById(R.id.home2_conn11);//工号
        tv17 = (TextView) v.findViewById(R.id.home2_conn12);//姓名
        tv18 = (TextView) v.findViewById(R.id.home2_conn13);//wifi网络名称
        home2_conn14 = (TextView) v.findViewById(R.id.home2_conn14);//wifi列表网络名称2
        home2_removerelation = (TextView) v.findViewById(R.id.home2_removerelation);
        home2_removerelation.setOnClickListener(this);//解除关键人
        v.findViewById(R.id.home2_updata).setOnClickListener(this);//资料更新
        v.findViewById(R.id.home2_upload).setOnClickListener(this);//资料上传
        home2_linkde1 = (TextView) v.findViewById(R.id.home2_linkde1);

        //可以对每个时间域单独修改
        String data = UtilisClass.getStringDate2();
        dbOpenHelper = DBOpenHelper.getInstance(getActivity().getApplicationContext());
//        获取登陆人关键人信息
        loginpersoninfo = dbOpenHelper.queryListMap("select * from PersonInfo where WorkNo=?", new String[]{workNo});
        loginpersonId = personId;

        uploaded_time= (TextView) v.findViewById(R.id.uploaded_time);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(wifireceiver);
    }

    private void initkeyperson() {
        if (loginpersoninfo.size() != 0) {
            //获取关键人
            List<Map> person1 = dbOpenHelper.queryListMap("select * from InstructorKeyPerson where InstructorId=? and IsRemoved=?", new String[]{loginpersonId, "false"});
            if (person1.size() != 0) {
                String person2Id = person1.get(0).get("KeyPersonId") + "";//关键人ID
                if (!person2Id.equals("")) {
                    Loginpersoninfo2 = dbOpenHelper.queryListMap("select * from PersonInfo where Id=?", new String[]{person2Id});
                    String keyWorkNo = Loginpersoninfo2.get(0).get("WorkNo") + "";
                    tv13.setText(keyWorkNo);//关键人id
                    if (person1.size() != 0) {
                        key_addlayout.setVisibility(View.VISIBLE);
                        home2_removerelation.setVisibility(View.VISIBLE);
                        tv12.setText(Loginpersoninfo2.get(0).get("Name") + "");//关键人姓名
                        List<Map> keypersonaddhistory = dbOpenHelper.queryListMap("select * from InstructorTempTake where InstructorId=? and TakeDate like? and DriverId=?", new String[]{
                                loginpersonId, UtilisClass.getStringDate2() + "%", person2Id});
                        if (keypersonaddhistory.size() != 0) {
                            int keyaddtime = keypersonaddhistory.size();
                            tv14.setText(keyaddtime + "");//添乘趟数
                        } else {
                            tv14.setText(0 + "");//添乘趟数
                        }
                    } else {
                    }
                } else {
                }
            } else {

                AddMoveKeyPerson = 0;
                tv12.setText("暂无");
                tv13.setText("");
                tv16.setText(workNo);///工号
                key_addlayout.setVisibility(View.INVISIBLE);
                home2_removerelation.setVisibility(View.INVISIBLE);
            }
            tv16.setText(workNo);///工号
            tv17.setText(loginpersoninfo.get(0).get("Name") + "");//姓名

        } else {

        }

    }

    //设置wifi显示状态
    private void initwifishow() {
        home2_linkde1.setOnClickListener(this);//wifi断开

        if (UtilisClass.isWifi(getActivity())) {
            home2_linkde1.setText("已连接");
            tv18.setText(getConnectWifiSsid().substring(1, getConnectWifiSsid().length() - 1));
            wifi_im.setImageResource(R.mipmap.i2_8);
        } else {
            home2_linkde1.setText("已断开");
            wifi_im.setImageResource(R.mipmap.i2_7);
            tv18.setText("");
        }
    }

    //设置完成指标板块指标数
    private void initAllQualiteCount() {

        List<Map> allmonthadd = dbOpenHelper.queryListMap("select * from InstructorQuota ORDER BY Id ASC", null);
        int monthaddtime = 10;
        double addpersoonhoursid =10;
        int mmonthraintime =10;
        int monthselectimeid = 10;

        if (allmonthadd.size() != 0) {
            int MONTHADDTIME = UtilisClass.getMONTHADDTIME();//总月添乘趟数
            String monthaddtimes=allmonthadd.get(MONTHADDTIME-1).get("QuataAmmount")+"";
            if (!monthaddtimes.equals("")){
            monthaddtime =Integer.parseInt(monthaddtimes);
            }else {
                monthaddtime =0;
            }


            int ADDPERSONHOURSID = UtilisClass.getADDPERSONHOURSID();//总月添乘小时
            String addpersoonhoursids=allmonthadd.get(ADDPERSONHOURSID-1).get("QuataAmmount")+"";
            if (!addpersoonhoursids.equals("")){
                addpersoonhoursid =Double.parseDouble(addpersoonhoursids);
            }else {
                addpersoonhoursid=0.0;
            }


            int MMONTHTRAINTIME = UtilisClass.getMMONTHTRAINTIME();//总分析列数
            String mmonthraintimes=allmonthadd.get(MMONTHTRAINTIME-1).get("QuataAmmount")+"";
            if (!mmonthraintimes.equals("")){
                mmonthraintime =Integer.parseInt(mmonthraintimes);
            }else {
                    mmonthraintime=0;
            }


            int MONTHSELECTIMEID = UtilisClass.getMONTHSELECTIMEID();//月检查次数
            String monthselectimeids=allmonthadd.get(MONTHSELECTIMEID-1).get("QuataAmmount")+"";
            if (!monthselectimeids.equals("")){
                monthselectimeid =Integer.parseInt(monthselectimeids);
            }else {
                monthselectimeid=0;
            }

        } else {
        }
        home2_monthadd1_all.setText("/" + monthaddtime + "");
        home2_monthadd2_all.setText("/" + addpersoonhoursid + "");
        home2_monthadd3_all.setText("/" + mmonthraintime + "");
        home2_monthadd4_all.setText("/" + monthselectimeid + "");
        //判断完成指标情况

        if (Integer.parseInt(home2_text1.getText() + "") < monthaddtime) {
            tv5.setText("未完成");
            tv5.setTextColor(Color.RED);
        } else {
            tv5.setText("完成");
//            tv5.setTextColor(Color.GREEN);
            tv5.setTextColor(getResources().getColor(R.color.colorblue2));

        }
        if (Double.parseDouble(home2_text2.getText() + "") < addpersoonhoursid) {
            tv19.setText("未完成");
            tv19.setTextColor(Color.RED);
        } else {
            tv19.setText("完成");
//            tv19.setTextColor(Color.GREEN);
            tv19.setTextColor(getResources().getColor(R.color.colorblue2));
        }
        if (Integer.parseInt(home2_text3.getText() + "") < mmonthraintime) {
            tv20.setText("未完成");
            tv20.setTextColor(Color.RED);
        } else {
            tv20.setText("完成");
//            tv20.setTextColor(Color.GREEN);
            tv20.setTextColor(getResources().getColor(R.color.colorblue2));
        }
        if (Integer.parseInt(home2_text4.getText() + "") < monthselectimeid) {
            tv21.setText("未完成");
            tv21.setTextColor(Color.RED);
        } else {
            tv21.setText("完成");
//            tv21.setTextColor(Color.GREEN);
            tv21.setTextColor(getResources().getColor(R.color.colorblue2));
        }

    }

    //设置完成指标数
    private void initPersonFinish() {

        String currentyearmoth = UtilisClass.getStringDate2();
        List<Map> personmonthadd = dbOpenHelper.queryListMap("select * from InstructorQuotaRecord where InstructorId=? and QuotaId=? and UpdateTime like ?",
                new String[]{personId, UtilisClass.getMONTHADDTIME() + "", currentyearmoth + "%"});
        if (personmonthadd.size() != 0) {
            home2_text1.setText(personmonthadd.get(0).get("FinishedAmmount") + "");//月添乘趟数
        } else {
            home2_text1.setText("0");
        }
        List<Map> personadd = dbOpenHelper.queryListMap("select * from InstructorQuotaRecord where InstructorId=? and QuotaId=? and UpdateTime like ?",
                new String[]{personId, UtilisClass.getADDPERSONHOURSID() + "", currentyearmoth + "%"});
        if (personadd.size() != 0) {
            home2_text2.setText(personadd.get(0).get("FinishedAmmount") + ""); //添乘小时数
        } else {
            home2_text2.setText("0");
        }
        List<Map> monthtrai = dbOpenHelper.queryListMap("select * from InstructorQuotaRecord where InstructorId=? and QuotaId=? and UpdateTime like ?",
                new String[]{personId, UtilisClass.getMMONTHTRAINTIME() + "", currentyearmoth + "%"});
        if (monthtrai.size() != 0) {
            home2_text3.setText(monthtrai.get(0).get("FinishedAmmount") + "");// '月分析列数列数
        } else {
            home2_text3.setText("0");
        }
        List<Map> monthsele = dbOpenHelper.queryListMap("select * from InstructorQuotaRecord where InstructorId=? and QuotaId=? and UpdateTime like ?",
                new String[]{personId, UtilisClass.getMONTHSELECTIMEID() + "", currentyearmoth + "%"});
        if (monthsele.size() != 0) {
            home2_text4.setText(monthsele.get(0).get("FinishedAmmount") + ""); //月检查次数
        } else {
            home2_text4.setText("0");
        }
    }

    //设置最新公告
    private void initLatestAnnouncement() {
        List<Map> importantnoticelist = dbOpenHelper.queryListMap("select * from Announcement", null);
        if (importantnoticelist.size() > 0) {
            tv9.setText(importantnoticelist.get(importantnoticelist.size() - 1).get("Title") + "");
        }
        if (importantnoticelist.size() > 1) {
            tv10.setText(importantnoticelist.get(importantnoticelist.size() - 2).get("Title") + "");
        }
        if (importantnoticelist.size() > 2) {
            tv11.setText(importantnoticelist.get(importantnoticelist.size() - 3).get("Title") + "");
        }
        if (importantnoticelist.size() > 3) {
            home2_connadd3.setText(importantnoticelist.get(importantnoticelist.size() - 4).get("Title") + "");
        }
        if (importantnoticelist.size() > 4) {
            home2_connadd4.setText(importantnoticelist.get(importantnoticelist.size() - 5).get("Title") + "");
        }
    }

    //设置重要提示
    private void initImportantNotice() {
        List<Map> importantnoticelist = dbOpenHelper.queryListMap("select * from ExamNotify", null);
        if (importantnoticelist.size() > 0) {
            tv6.setText(importantnoticelist.get(importantnoticelist.size() - 1).get("ExamName") + "");
        }
        if (importantnoticelist.size() > 1) {
            tv7.setText(importantnoticelist.get(importantnoticelist.size() - 2).get("ExamName") + "");
        }
        if (importantnoticelist.size() > 2) {
            tv8.setText(importantnoticelist.get(importantnoticelist.size() - 3).get("ExamName") + "");
        }
        if (importantnoticelist.size() > 3) {
            home2_connadd1.setText(importantnoticelist.get(importantnoticelist.size() - 4).get("ExamName") + "");
        }
        if (importantnoticelist.size() > 4) {
            home2_connadd2.setText(importantnoticelist.get(importantnoticelist.size() - 5).get("ExamName") + "");
        }
    }

    //设置资料更新
    private void setUpDatas(){
        if (HttpUtils.isNetWorkConn(getActivity())) {

            updatadiolag.show();
            searchid = 0;
            upLoadPool.execute(new Runnable() {
                @Override
                public void run() {
                    upData();
//                    aa:for (;;){
//                        if (mapList.size()!=0){
//                            upData();
//                        }else {
//                            break aa;
//                        }
//                    }


                }
            });
        } else {
            UtilisClass.showToast(getActivity(), "更新失败！，网络未联通");
        }
    }


    //设置点击事件
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.home2_removerelation:
                Intent addremoveintent = new Intent(getActivity(), AddRemoveKeyperson.class);
                Bundle addremovebundle = new Bundle();
                addremovebundle.putInt("haveorno", 1);
                addremovebundle.putInt("listitemId", -1);
                addremoveintent.putExtra("InstructorKeyPerson", addremovebundle);
                startActivity(addremoveintent);
                break;
            case R.id.home2_updata:
//资料更新

                setUpDatas();
                break;
            case R.id.home2_upload:
//资料上传

                if (HttpUtils.isNetWorkConn(getActivity())) {

                    updatadiolag.show();
                    upLoadPool.execute(new Runnable() {
                        @Override
                        public void run() {
                            updataarguments();
                        }
                    });

                } else {
                    UtilisClass.showToast(getActivity(), "更新失败！，网络未联通");
                }




                break;
            case R.id.home2_linkde1:
//wifi断开
//                if (android.os.Build.VERSION.SDK_INT > 10) {
//                    // 3.0以上打开设置界面，也可以直接用ACTION_WIRELESS_SETTINGS打开到wifi界面
//                    startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
//                } else {
//                    startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
//
//                }
                List<Map> list=dbOpenHelper.queryListMap("select * from Announcement",null);
                System.out.println(list.toString());


                break;
            case R.id.home2_linkde2:
                break;


        }
    }


    //获取wifi名字
    private String getConnectWifiSsid() {
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        Log.d("wifiInfo", wifiInfo.toString());
        Log.d("SSID", wifiInfo.getSSID());
        return wifiInfo.getSSID();
    }

    //重新加载activity
    private void onrefresh() {
        onCreate(null);
    }


    //progress 弹窗
    public void setdialog() {
        updatadiolag = UtilisClass.setprogressDialog(getActivity());
    }


    //设置资料更新
    //向服务器post未上传的数据
    private void updataarguments() {

        String[] tablename = NetUtils.uptablenames;

        System.out.println(tablename.toString());
        for (int i = 0; i < tablename.length; i++) {
            final String tableName = tablename[i];
            final List<Map> uplist = dbOpenHelper.queryListMap("select * from " + tableName + " where " +
                    "IsUploaded=?", new String[]{"1"});
            if (uplist.size() != 0) {
                String currenttime = UtilisClass.getStringDate();
                for (int j = 0; j < uplist.size(); j++) {
                    if (tableName.equals("InstructorKeyPerson")) {
                        String isremoved = uplist.get(j).get("IsRemoved") + "";
                        if (isremoved.equals("false")) {

                            uplist.get(j).put("ActualRemoveTime", "1990-1-1");
                        }
                    }
                }


                for (int j=0; j<uplist.size(); j++){
                    List<Map> list=new ArrayList<>();
                    list.add(uplist.get(j));
                    NetUtils.updataarguments3dinglehome(list,dbOpenHelper,tableName);
                }

            }
        }

        List<Map> InstructorQuotaRecordlist=dbOpenHelper.queryListMap("select * from InstructorQuotaRecord where " +
                "IsUploaded=?", new String[]{"1"});

//        UtilisClass.showToast(getActivity(),"运行了！");
        if (InstructorQuotaRecordlist.size()!=0) {
            for (int j=0; j<InstructorQuotaRecordlist.size(); j++){
                List<Map> list=new ArrayList<>();
                list.add(InstructorQuotaRecordlist.get(j));
                NetUtils.updateInstructorQuotaRecord(list,dbOpenHelper,"InstructorQuotaRecord");
                    System.out.println(list.toString());
            }
        }



        Message message1 = new Message();
        message1.arg1 = 2;
        handler.sendMessage(message1);

    }

    //线程执行的任务
    public void upData() {
        List<Map> lastIdlist = dbOpenHelper.queryListMap("select * from DbUpdateLog", null);
        if (lastIdlist.size() != 0) {
            searchid = (int) lastIdlist.get(lastIdlist.size() - 1).get("Id");
            lastIdlist.clear();
        }
        urls = NetUtils.Uploadeduri + searchid;



        String json = HttpUtils.getJsonContent(urls);
        Log.i("tagg", json);
        Log.i("tagg?????????????????", urls);

        if (!json.isEmpty()&&!json.equals("")) {

            try {
                JSONObject jsonObject = new JSONObject(json);
                if (jsonObject.get("code").equals(304) || jsonObject.get("code").equals("304")) {
                    UtilisClass.showToast(getActivity(), "任务失败！");
                } else
//            if (jsonObject.get("code").equals("108"))
                {
                    String jsonArray = jsonObject.getString("data");
                    mapList = Utils.StringListUtils.getList(jsonArray);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (mapList.size() != 0) for (int num = 0; num < mapList.size(); num++) {

                dbOpenHelper.insert("DbUpdateLog", mapList.get(num));
            }

            List<String> tableNames = NetUtils.getTableNamelist(mapList, "TableName");//筛选需要更新的所有表名集合

//        Log.i("tablenames", String.valueOf(tableNames));

            if (tableNames.size() != 0) {
                promptlist.clear();
                for (int num = 0; num < tableNames.size(); num++) {
                    String singletablename = tableNames.get(num);
                    List<Map<String, Object>> singleTableData = NetUtils.getSingleTableNameList(mapList, "TableName", singletablename);//获取单个表集合
                    List<String> ListId = NetUtils.getTableNamelist(singleTableData, "TargetId");

                    if (singletablename.equals("ExamNotify")) {
                        promptlist.add("ExamNotify");
                    } else if (singletablename.equals("Announcement")) {
                        promptlist.add("Announcement");
                    } else if (singletablename.equals("TraficFiles")) {
                        promptlist.add("TraficFiles");
                    }


//                Log.i("tablenames", String.valueOf(ListId));
                    Map<String, Map> m = new HashMap<>();
                    for (int i = 0; i < singleTableData.size(); i++) {
                        m.put(singleTableData.get(i).get("TargetId") + "", (Map) singleTableData.get(i));
                    }
                    List<Map<String, Object>> singleTableDatalist = new ArrayList<>();


                    for (int i = 0; i < ListId.size(); i++) {
                        singleTableDatalist.add(m.get(ListId.get(i)));
                    }
//                Log.i("tagg", String.valueOf(singleTableDatalist));
                    List<Map<String, Object>> addTableDatalist = NetUtils.getSingleTableNameList(singleTableDatalist, "UpdateType", 1);//获取单个表的insert
                    if (addTableDatalist.size() != 0) {
                        NetUtils.Dosingletableinsert(addTableDatalist, singletablename, dbOpenHelper);

                    } else {
                    }
                    List<Map<String, Object>> upTableDatalist = NetUtils.getSingleTableNameList(singleTableDatalist, "UpdateType", 2);//获取单个表的更新
                    if (upTableDatalist.size() != 0) {
                        NetUtils.DosingletableUpdata(upTableDatalist, singletablename, dbOpenHelper);
                    } else {
                    }

                    List<Map<String, Object>> deleteTableDatalist = NetUtils.getSingleTableNameList(singleTableDatalist, "UpdateType", 3);//获取单个表的更新

//                Log.i("tablenames", String.valueOf(deleteTableDatalist));

                    try {
                        if (deleteTableDatalist.size() != 0) {
                            NetUtils.DosingletableDelete(deleteTableDatalist, singletablename, dbOpenHelper);
                        } else {
                        }
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            }

            List<Map> fileList = dbOpenHelper.queryListMap("select * from TraficFiles where IsDelete=? and IsLoaded=?", new String[]{"0", "false"});

            System.out.println("<<<<<<<<<<<<<>>>>>>>>>>>>" + fileList.toString());
            if (fileList.size() != 0) {
                try {
                    getsearchfileSaved(1, fileList);
                } catch (UnsupportedEncodingException e) {

                }
            }

            List<Map> fileList2 = dbOpenHelper.queryListMap("select * from Announcement where AnnounceType=? and IsLoaded=?", new String[]{"1", "false"});

//        System.out.println("<<>><<>>"+fileList2.toString());

            if (fileList2.size() != 0) {
                try {
                    getsearchfileSaved(2, fileList2);
                } catch (UnsupportedEncodingException e) {

                }

            }
            Message message = new Message();
            StringBuffer toast = new StringBuffer();
            if (promptlist.size() != 0) {
                for (int i = 0; i < promptlist.size(); i++) {
                    String tablename = promptlist.get(i);
                    if (tablename.equals("ExamNotify")) {
                        toast.append("重要提示");
                    } else if (tablename.equals("Announcement")) {
                        toast.append("最新公告");
                    } else if (tablename.equals("TraficFiles")) {
                        toast.append("行车资料");
                    }
                    toast.append("、");
                }
                toast.deleteCharAt(toast.lastIndexOf("、"));
            }
            String stringprompt = toast.toString();
            message.obj = stringprompt;
            message.arg1 = 1;
            handler.sendMessage(message);
        }else {
            UtilisClass.showToast(getActivity(),"获取数据失败！");
        }
    }

    //图片请求
    private void setphotoImage() {
        if (loginpersoninfo.size() != 0) {
            String netPhotoPath = loginpersoninfo.get(0).get("PhotoPath") + "";
            String[] path = netPhotoPath.split("/");
            String picNames = path[path.length - 1];
            final String picName = picNames.substring(0, picNames.length() - 4);

            if (UtilisClass.isWifi(getActivity())) {
                String urlphotopath = NetUtils.APPDOOR2 + netPhotoPath;
                Request<Bitmap> bitmapRequest = NoHttp.createImageRequest(urlphotopath);
                bitmapRequest.setCacheMode(CacheMode.ONLY_REQUEST_NETWORK);
                Myapplilcation.queue.add(WHAT, bitmapRequest, new OnResponseListener<Bitmap>() {
                    @Override
                    public void onStart(int i) {
                    }

                    @Override
                    public void onSucceed(int i, Response<Bitmap> response) {
                        Bitmap bm = response.get();
                        home2_iv.setImageBitmap(bm);
//                        UtilisClass.showToast(getActivity(), "请求成功！");
                        UtilisClass.saveimg(getActivity(), picName, bm);
                        String locaPath = getActivity().getExternalFilesDir(null).getPath() + "/" + picName + ".png";
                        dbOpenHelper.update("PersonInfo", new String[]{"LocaPhotoPath"}, new Object[]{locaPath}, new String[]{"Id"}, new String[]{personId});
                    }

                    @Override
                    public void onFailed(int i, Response<Bitmap> response) {
                        List<Map> mapList1 = dbOpenHelper.queryListMap("select * from PersonInfo where Id=?", new String[]{personId});
                        String locaphotopath = mapList1.get(0).get("LocaPhotoPath") + "";
                        if (locaphotopath.length() != 0) {
                            Uri uri = Uri.parse(locaphotopath);
                            home2_iv.setImageURI(uri);
                        } else {
                        }
                    }

                    @Override
                    public void onFinish(int i) {
                    }
                });
            } else {
                List<Map> mapList1 = dbOpenHelper.queryListMap("select * from PersonInfo where Id=?", new String[]{personId});
                String locaphotopath = mapList1.get(0).get("LocaPhotoPath") + "";
                if (locaphotopath.length() != 0) {
                    Uri uri = Uri.parse(locaphotopath);
                    home2_iv.setImageURI(uri);
                } else {

                }
            }
        }
    }


    private String HandleUrlEncode(String path) throws UnsupportedEncodingException {
        int lastIndex = path.lastIndexOf('/');
        String fileName = path.substring(lastIndex + 1);
        String serverPath = path.substring(0, lastIndex + 1);
        String encodeFileName = URLEncoder.encode(fileName, "UTF-8");
        return serverPath + encodeFileName;
    }

//文件下载保存

    private void getsearchfileSaved(int a,List<Map> listitem) throws UnsupportedEncodingException {
//        FileUtil fileUtil = new FileUtil();

        for (int i = 0; i < listitem.size(); i++) {
            String filepath = listitem.get(i).get("FilePath") + "";
            String loadfilepath = NetUtils.APPDOOR2 + filepath;//网络请求路径
            String fileName ="";
                fileName = filepath.split("/")[filepath.split("/").length - 1];
                String unzipfilename = listitem.get(i).get("FileName") + "";
            String urlfilename = HandleUrlEncode(loadfilepath);
//            System.out.println(urlfilename);
            String fileSavePath = "FileData/" + UtilisClass.getuuid();//文件保存路径
            if (a==1){
            if (listitem.get(i).get("FileExtension").equals(".zip")) {
                //获得压缩文件
                byte[] bytes = HttpUtils.getByteContent(urlfilename);
                if (bytes != null && bytes.length != 0) {
                    SDCardHelper.saveFileToSDCardPrivateDir(bytes, null, fileName, getActivity());
                }
                try {
                    String filepathzip = getActivity().getExternalFilesDir(null).getPath() + "/" + fileName;
                    String unzipfilepath = getActivity().getExternalFilesDir(null).getPath() + "/" + fileSavePath;
                    UtilisClass.unZipFile(filepathzip, unzipfilepath);
//                    String htmfilepath = unzipfilepath + "/" + unzipfilename;

                    String filetestpath = UtilisClass.getFileDir(unzipfilepath);
                    System.out.println(">>>>>>>>>>>>>>>>>>>" + filetestpath);
                    dbOpenHelper.update("TraficFiles", new String[]{"LocaPath", "IsLoaded"}, new Object[]{filetestpath, "true"}, new String[]{"Id"}, new String[]{listitem.get(i).get("Id") + ""});

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else {

                byte[] bytes = HttpUtils.getByteContent(urlfilename);
                if (bytes != null && bytes.length != 0) {
                    SDCardHelper.saveFileToSDCardPrivateDir(bytes, null, fileName, getActivity());
                    String loachpath=getActivity().getExternalFilesDir(null).getPath() + "/" + fileName;
                    dbOpenHelper.update("TraficFiles", new String[]{"LocaPath", "IsLoaded"}, new Object[]{loachpath, "true"}, new String[]{"Id"}, new String[]{listitem.get(i).get("Id") + ""});
                }
            }
            } else {
//获得文件
                byte[] bytes = HttpUtils.getByteContent(urlfilename);
                System.out.println("1111111111111"+urlfilename);
                if (bytes != null && bytes.length != 0) {
                    SDCardHelper.saveFileToSDCardPrivateDir(bytes, null, fileName, getActivity());
                    String loachpath=getActivity().getExternalFilesDir(null).getPath() + "/" + fileName;
                    dbOpenHelper.update("Announcement", new String[]{"LocaPath", "IsLoaded"}, new Object[]{loachpath, "true"}, new String[]{"Id"}, new String[]{listitem.get(i).get("Id") + ""});
                }
            }
        }
    }

    //获得文件绝对路径无文件名
    private String getstringpath(Map map) {
        List<Map> TraficFileTypes = dbOpenHelper.queryListMap("select * from TraficFileType where Id=?",
                new String[]{map.get("TypeId") + ""});
        String parentId = TraficFileTypes.get(0).get("Id") + "";
        String filepath = getTraficFileTypeParentfilename(parentId, "");
        return filepath;
    }


    private String getTraficFileTypeParentfilename(String traficFileTypeid, String fileName) {
        if (!traficFileTypeid.equals("0")) {
            List<Map> list = dbOpenHelper.queryListMap("select * from TraficFileType where Id=?", new String[]{traficFileTypeid});

            String TraficFileTypeid = list.get(0).get("ParentId") + "";
            String dir = list.get(0).get("TypeName") + "";
            fileName = dir + "/" + fileName;
            return getTraficFileTypeParentfilename(TraficFileTypeid, fileName);
        } else {
            return "/" + fileName;
        }
    }


    private void getwifiinfo(){
       WifiInfo mWifiInfo = wifiManager.getConnectionInfo();
        if (mWifiInfo != null) {
            System.out.println(">>>>>"+mWifiInfo.toString());
            System.out.println(">>>>>"+mWifiInfo.getIpAddress());

        }
    }


    private class WifiReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {// 这个监听wifi的打开与关闭，与wifi的连接无关
                int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
                Log.e("H3c", "wifiState" + wifiState);
                switch (wifiState) {
                    case WifiManager.WIFI_STATE_DISABLED:
                        //WIFI网卡不可用
                        UtilisClass.showToast(context,"WIFI网卡不可用");
                        break;
                    case WifiManager.WIFI_STATE_DISABLING:
                        UtilisClass.showToast(context,"WIFI正在关闭");
//WIFI正在关闭

                        break;
                    //
                }
            }
            // 这个监听wifi的连接状态即是否连上了一个有效无线路由，当上边广播的状态是WifiManager.WIFI_STATE_DISABLING，和WIFI_STATE_DISABLED的时候，根本不会接到这个广播。
            // 在上边广播接到广播是WifiManager.WIFI_STATE_ENABLED状态的同时也会接到这个广播，当然刚打开wifi肯定还没有连接到有效的无线
            if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                Parcelable parcelableExtra = intent
                        .getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (null != parcelableExtra) {
                    NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
                    NetworkInfo.State state = networkInfo.getState();
                    boolean isConnected = state == NetworkInfo.State.CONNECTED;// 当然，这边可以更精确的确定状态
                    Log.e("H3c", "isConnected" + isConnected);



                    if (isConnected) {
//如果连接
                        home2_linkde1.setText("已连接");
                        tv18.setText(getConnectWifiSsid().substring(1, getConnectWifiSsid().length() - 1));
                        wifi_im.setImageResource(R.mipmap.i2_8);
//                        try {
//                            Thread.currentThread().sleep(1000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }

                        WifiInfo mWifiInfo = wifiManager.getConnectionInfo();
                        String routerBssid=mWifiInfo.getBSSID();
                        String locamac=mWifiInfo.getMacAddress();

                        Log.e("H3c", "routerBssid" + routerBssid);
                        Log.e("H3c", "locamac" + locamac);

                        List<Map> routerlist=dbOpenHelper.queryListMap("select * from InstructorRouterPosition  where BssId like ?"
                                ,new String[]{"%"+routerBssid+"%"});

//                        System.out.println("???????????"+routerlist.toString()+"???");


                        if (routerlist.size()!=0) {
                           String currentdate = UtilisClass.getStringDate();
                            String routerid=routerlist.get(0).get("Id")+"";

                            dbOpenHelper.insert("InstructorWifiRecord", new String[]{"InstructorId", "RouterPositionId", "ConnectTime","ConnectFlag"},
                                    new Object[]{personId,routerid, currentdate,"1"});
                            try {
                                Thread.currentThread().sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            setUpDatas();
                        }else {
                        }
                    } else {
//未连接
                        home2_linkde1.setText("已断开");
                        wifi_im.setImageResource(R.mipmap.i2_7);
                        tv18.setText("");

                    }
                }
            }

        }
    }




}