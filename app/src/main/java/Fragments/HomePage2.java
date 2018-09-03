package Fragments;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.yanzhenjie.nohttp.Headers;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.download.DownloadListener;
import com.yanzhenjie.nohttp.download.DownloadRequest;
import com.yanzhenjie.nohttp.rest.CacheMode;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;

//import org.apache.commons.io.FilenameUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import Adapter.CompleteFilesAdapter;
import Adapter.DWImportantNoteAdapter;
import DBUtils.DBOpenHelper;
import Utils.CommonAdapter;
import Utils.CustomDialog;
import Utils.HttpUtils;
import Utils.NetUtils;
import Utils.SDCardHelper;
import Utils.ViewHolder;
import config.ISystemConfig;
import config.SystemConfigFactory;
import zj.com.mc.AddRemoveKeyperson;
import zj.com.mc.BridgeActivity;
import zj.com.mc.HtmlWebView;
import zj.com.mc.MainActivity;
import zj.com.mc.Myapplilcation;
import zj.com.mc.NoticeDetails;
import zj.com.mc.R;
import zj.com.mc.UtilisClass;

/**
 * 指导司机首页
 */
public class HomePage2 extends Fragment implements View.OnClickListener {

    private TextView tv5, tv12, tv13, tv14, tv15, tv16, tv17, tv18, tv19, tv20, tv21, home2_conn14;
    private TextView home2_linkde1;
    private TextView home2_monthadd1_all, home2_monthadd2_all, home2_monthadd3_all, home2_monthadd4_all;
    private TextView home2_text1, home2_text2, home2_text3, home2_text4; //完成指标数
    private TextView home2_removerelation;//接触关键人

    private RadioGroup rg_classify;
    private ListView lv_notice;
    private ListView lv_warn;
    private ListView lv_accessory;
    private ListView lv_examnotice;

    private ImageView wifi_im;
    private TextView uploaded_time;
    private ImageView home2_iv;
    private DBOpenHelper dbOpenHelper;
    private List<Map> loginpersoninfo;
    private List<Map> Loginpersoninfo2;
    private String workNo;
    private WifiManager wifiManager;
    private String loginpersonId;
    private String personId;
    private LinearLayout key_addlayout;
    private ProgressDialog updatadiolag;
    private List<Map<String, Object>> mapList;
    private List<String> promptlist;

    private DWImportantNoteAdapter dwImportantNoteAdapter;

    private static int WHAT = 0;
    private WifiReceiver wifireceiver;
    private ISystemConfig systemConfig;
    private RequestQueue registerQueue;
    private DownloadRequest downloadRequest;
    private String fileType;
    private MainActivity mActivity;
    private CustomDialog downloaddialog;
    private CustomDialog completddialog;
    private CompleteFilesAdapter completeFilesAdapter;
    private List<Map> comletefiles;
    private List<Map<String, List>> needfiles;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int n = msg.arg1;
            if (n == 1) {
                String uploadedtime = UtilisClass.getStringDate1();
                uploaded_time.setText("上次更新:" + uploadedtime);
                String toaststring = (String) msg.obj;
                TextView tv = new TextView(getActivity());
                tv.setPadding(40, 20, 0, 0);
                try {
                    onrefresh();
                    setNewtoast();
                    setWarning();
                    setAccessory();
                    initExamNotice();
                    updatadiolag.cancel();

                } catch (UnsupportedOperationException u) {
                    u.printStackTrace();
                }
                if (!toaststring.equals("")) {
                    tv.setText(toaststring + "数据有更新！");
                    tv.setTextSize(20);
                    dataUpdatePrompt(tv);
                } else {
                    UtilisClass.showToast(getActivity(), "数据更新完成！");
                    needFiles();
                }
                mActivity.upAppOperateLog();
                mActivity.upWifiRecord();
            } else if (n == 2) {
                UtilisClass.showToast(getActivity(), "数据上传完成！");
                onrefresh();
                updatadiolag.cancel();
            } else if (n == 3) {
                if (updatadiolag != null) {
                    updatadiolag.dismiss();
                }
                comleteFiles(needfiles);
                mActivity.upAppOperateLog();
                mActivity.upWifiRecord();
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();

        wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        dbOpenHelper = DBOpenHelper.getInstance(getActivity().getApplication());
        systemConfig = SystemConfigFactory.getInstance(getActivity()).getSystemConfig();
        workNo = systemConfig.getUserAccount();
        personId = systemConfig.getUserId();
        mapList = new ArrayList<>();
        promptlist = new ArrayList<>();

        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        wifireceiver = new WifiReceiver();
        getActivity().registerReceiver(wifireceiver, filter);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.homepage2, container, false);
        initView(view);
        return view;
    }

    private void initView(View v) {
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

        rg_classify = (RadioGroup) v.findViewById(R.id.rg_classify);

        lv_notice = (ListView) v.findViewById(R.id.lv_notice);
        lv_warn = (ListView) v.findViewById(R.id.lv_warn);
        lv_accessory = (ListView) v.findViewById(R.id.lv_accessory);
        lv_examnotice = (ListView) v.findViewById(R.id.lv_examnotice);

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

        dbOpenHelper = DBOpenHelper.getInstance(getActivity().getApplicationContext());
        loginpersoninfo = dbOpenHelper.queryListMap("select * from PersonInfo where WorkNo=?", new String[]{workNo});
        systemConfig.setUserPostId(loginpersoninfo.get(0).get("PostId").toString());
        loginpersonId = personId;
        uploaded_time = (TextView) v.findViewById(R.id.uploaded_time);
    }

    @Override
    public void onResume() {
        super.onResume();
        initPersonFinish();
        initAllQualiteCount();
        initwifishow();
        initkeyperson();
        setphotoImage();

        ((RadioButton) rg_classify.getChildAt(0)).setChecked(true);
        setNewtoast();
        selectiveType();
    }

    private void selectiveType() {
        rg_classify.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rb_notice:
                        lv_notice.setVisibility(View.VISIBLE);
                        lv_warn.setVisibility(View.GONE);
                        lv_accessory.setVisibility(View.GONE);
                        lv_examnotice.setVisibility(View.GONE);
                        setNewtoast();
                        break;
                    case R.id.rb_warn:
                        lv_warn.setVisibility(View.VISIBLE);
                        lv_notice.setVisibility(View.GONE);
                        lv_accessory.setVisibility(View.GONE);
                        lv_examnotice.setVisibility(View.GONE);
                        setWarning();
                        break;
                    case R.id.rb_accessory:
                        lv_accessory.setVisibility(View.VISIBLE);
                        lv_notice.setVisibility(View.GONE);
                        lv_warn.setVisibility(View.GONE);
                        lv_examnotice.setVisibility(View.GONE);
                        setAccessory();
                        break;
                    case R.id.rb_examnotice:
                        lv_examnotice.setVisibility(View.VISIBLE);
                        lv_notice.setVisibility(View.GONE);
                        lv_warn.setVisibility(View.GONE);
                        lv_accessory.setVisibility(View.GONE);
                        initExamNotice();
                        break;
                }
            }
        });
    }

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

    private void initAllQualiteCount() {

        List<Map> allmonthadd = dbOpenHelper.queryListMap("select * from InstructorQuota ORDER BY Id ASC", null);
        int monthaddtime = 10;
        double addpersoonhoursid = 10;
        int mmonthraintime = 10;
        int monthselectimeid = 10;

        if (allmonthadd.size() != 0) {
            int MONTHADDTIME = UtilisClass.getMONTHADDTIME();//总月添乘趟数
            String monthaddtimes = allmonthadd.get(MONTHADDTIME - 1).get("QuataAmmount") + "";
            if (!monthaddtimes.equals("")) {
                monthaddtime = Integer.parseInt(monthaddtimes);
            } else {
                monthaddtime = 0;
            }
            int ADDPERSONHOURSID = UtilisClass.getADDPERSONHOURSID();//总月添乘小时
            String addpersoonhoursids = allmonthadd.get(ADDPERSONHOURSID - 1).get("QuataAmmount") + "";
            if (!addpersoonhoursids.equals("")) {
                addpersoonhoursid = Double.parseDouble(addpersoonhoursids);
            } else {
                addpersoonhoursid = 0.0;
            }
            int MMONTHTRAINTIME = UtilisClass.getMMONTHTRAINTIME();//总分析列数
            String mmonthraintimes = allmonthadd.get(MMONTHTRAINTIME - 1).get("QuataAmmount") + "";
            if (!mmonthraintimes.equals("")) {
                mmonthraintime = Integer.parseInt(mmonthraintimes);
            } else {
                mmonthraintime = 0;
            }

            int MONTHSELECTIMEID = UtilisClass.getMONTHSELECTIMEID();//月检查次数
            String monthselectimeids = allmonthadd.get(MONTHSELECTIMEID - 1).get("QuataAmmount") + "";
            if (!monthselectimeids.equals("")) {
                monthselectimeid = Integer.parseInt(monthselectimeids);
            } else {
                monthselectimeid = 0;
            }
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
            tv5.setTextColor(getResources().getColor(R.color.colorblue2));
        }
        if (Double.parseDouble(home2_text2.getText() + "") < addpersoonhoursid) {
            tv19.setText("未完成");
            tv19.setTextColor(Color.RED);
        } else {
            tv19.setText("完成");
            tv19.setTextColor(getResources().getColor(R.color.colorblue2));
        }
        if (Integer.parseInt(home2_text3.getText() + "") < mmonthraintime) {
            tv20.setText("未完成");
            tv20.setTextColor(Color.RED);
        } else {
            tv20.setText("完成");
            tv20.setTextColor(getResources().getColor(R.color.colorblue2));
        }
        if (Integer.parseInt(home2_text4.getText() + "") < monthselectimeid) {
            tv21.setText("未完成");
            tv21.setTextColor(Color.RED);
        } else {
            tv21.setText("完成");
            tv21.setTextColor(getResources().getColor(R.color.colorblue2));
        }
    }

    private void setNewtoast() {
        final List<Map> notices = dbOpenHelper.queryListMap("select Id,Title,AnnounceType,LocaPath,IsRead from Announcement where BusinessType = 1", null);
        if (notices.size() > 0) {
            Collections.reverse(notices);
            setAdapter(lv_notice, notices);

            lv_notice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    openNotice(notices, i);
                }
            });
        }
    }

    private void setWarning() {
        final List<Map> warnings = dbOpenHelper.queryListMap("select Id,Title,AnnounceType,LocaPath,IsRead from Announcement where BusinessType = 2", null);
        if (warnings.size() > 0) {
            Collections.reverse(warnings);
            setAdapter(lv_warn, warnings);
            lv_warn.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    openNotice(warnings, i);
                }
            });
        }
    }

    private void setAccessory() {
        final List<Map> accessorys = dbOpenHelper.queryListMap("select Id,Title,AnnounceType,LocaPath,IsRead from Announcement where DepartmentId = ? and BusinessType = 3", new String[]{systemConfig.getDepartmentId()});
        if (accessorys.size() > 0) {
            Collections.reverse(accessorys);
            setAdapter(lv_accessory, accessorys);
            lv_accessory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    openNotice(accessorys, i);
                }
            });
        }
    }

    private void initExamNotice() {
        List<Map> postmap = dbOpenHelper.queryListMap("select * from Posts where Id=?", new String[]{systemConfig.getUserPostId()});
        List<Map> importantnoticelist;
        if (postmap.get(0).get("PostName").toString().equals("司机")) {
            importantnoticelist = dbOpenHelper.queryListMap("select * from ExamNotify where PostId = ? or PostId = 2 order by AddTime desc",
                    new String[]{systemConfig.getUserPostId()});
        } else {
            importantnoticelist = dbOpenHelper.queryListMap("select * from ExamNotify where PostId = ? order by AddTime desc ",
                    new String[]{systemConfig.getUserPostId()});
        }

        dwImportantNoteAdapter = new DWImportantNoteAdapter(getActivity(), importantnoticelist);
        lv_examnotice.setAdapter(dwImportantNoteAdapter);

        lv_examnotice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mActivity.rgSetChecked();
            }
        });
    }

    private void setAdapter(ListView listView, List<Map> list) {
        listView.setAdapter(new CommonAdapter<Map>(getActivity(), list, R.layout.fileshowlistview) {
            @Override
            protected void convertlistener(ViewHolder holder, Map map) {
            }

            @Override
            public void convert(ViewHolder holder, Map map) {
                holder.getView(R.id.fileshow_fileicn).setVisibility(View.GONE);
                String title = map.get("Title") + "";
                if (map.get("IsRead").toString().equals("0")) {
                    holder.setText(R.id.fileshow_listviewitem, title);
                    ((TextView) holder.getView(R.id.fileshow_listviewitem)).setTextColor(Color.RED);
                } else {
                    holder.setText(R.id.fileshow_listviewitem, title);
                }
            }
        });

    }

    private void openNotice(List<Map> list, int i) {
        String AnnounceType = list.get(i).get("AnnounceType") + "";
        if (AnnounceType.equals("2")) {
            Intent intentLatestA = new Intent(getActivity(), NoticeDetails.class);
            intentLatestA.putExtra("Id", list.get(i).get("Id") + "");
            startActivity(intentLatestA);
        } else {
            String locapath = list.get(i).get("LocaPath") + "";
            if (locapath != null && !locapath.equals("") && !locapath.equals("null")) {
                Intent bridgeintent = new Intent(getActivity(), BridgeActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("FilePath", locapath);
                bridgeintent.putExtras(bundle);
                startActivity(bridgeintent);
            } else {
                UtilisClass.showToast(getActivity(), "未找到该文件！");
            }
        }
        dbOpenHelper.update("Announcement", new String[]{"IsRead"}, new Object[]{1},
                new String[]{"Id"}, new String[]{list.get(i).get("Id") + ""});
        recordOperateLog(5, "打开 " + list.get(i).get("Title"));
    }

    private void initwifishow() {
        home2_linkde1.setOnClickListener(this);
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
                    }
                }
            } else {
                tv12.setText("暂无");
                tv13.setText("");
                tv16.setText(workNo);
                key_addlayout.setVisibility(View.INVISIBLE);
                home2_removerelation.setVisibility(View.INVISIBLE);
            }
            tv16.setText(workNo);
            tv17.setText(loginpersoninfo.get(0).get("Name") + "");//姓名
        }
    }

    private void setphotoImage() {
        if (loginpersoninfo.size() != 0) {
            if (loginpersoninfo.get(0).get("PhotoPath").toString().length() != 0) {
                String netPhotoPath = loginpersoninfo.get(0).get("PhotoPath") + "";
                String[] path = netPhotoPath.split("/");
                String picNames = path[path.length - 1];
                final String picName = picNames.substring(0, picNames.length() - 4);
                if (UtilisClass.isWifi(getActivity())) {
                    String urlphotopath = systemConfig.getHost() + netPhotoPath;
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
                            }
                        }

                        @Override
                        public void onFinish(int i) {
                        }
                    });
                }
            } else {
//                home2_iv.setImageResource(R.mipmap.reader);
            }
        } else {
            List<Map> mapList1 = dbOpenHelper.queryListMap("select * from PersonInfo where Id=?", new String[]{personId});
            String locaphotopath = mapList1.get(0).get("LocaPhotoPath") + "";
            if (locaphotopath.length() != 0) {
                Uri uri = Uri.parse(locaphotopath);
                home2_iv.setImageURI(uri);
            }
        }
    }

    private void recordOperateLog(int LogType, String LogContent) {
        Date dt = new Date();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String nowTime = df.format(dt);
        dbOpenHelper.insert("AppOperateLog", new String[]{"LogType", "LogContent", "OperatorId", "DeviceId", "AddTime"},
                new Object[]{LogType, LogContent, systemConfig.getOperatorId(), 0, nowTime});
    }

    private void setUpDatas() {
        if (HttpUtils.isNetWorkConn(getActivity())) {
            updatadiolag.show();
            updateData();
        } else {
            UtilisClass.showToast(getActivity(), "WIFI未连接，无法更新数据！");
        }
    }

    public void updateData() {
        String url = systemConfig.getHost() + NetUtils.APPINDEX1;
        registerQueue = NoHttp.newRequestQueue();
        Request<String> stringPostRequest = NoHttp.createStringRequest(url, RequestMethod.POST);
        stringPostRequest.add("signature", "bcad117ce31ac75fcfa347acefc8d198");
        stringPostRequest.add("TableName", "DbUpdateLog");
        stringPostRequest.add("Operate", "6");
        stringPostRequest.add("StartId", systemConfig.getNewFileMaxId());

        registerQueue.add(2, stringPostRequest, new OnResponseListener<String>() {

            @Override
            public void onStart(int what) {
            }

            @Override
            public void onSucceed(int what, final Response<String> response) {
                Myapplilcation.getExecutorService().execute(new Runnable() {
                    @Override
                    public void run() {
                        writeDatabase(response.get().toString());
                    }
                });
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                UtilisClass.showToast(getActivity(), "获取数据失败！");
            }

            @Override
            public void onFinish(int what) {
            }
        });
    }

    private void writeDatabase(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            if (jsonObject.get("code").equals(304) || jsonObject.get("code").equals("304")) {
                UtilisClass.showToast(getActivity(), "任务失败！");
            } else {
                String jsonArray = jsonObject.getString("data");
                mapList = Utils.StringListUtils.getList(jsonArray);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (mapList.size() > 0) {
            for (int num = 0; num < mapList.size(); num++) {
                dbOpenHelper.insert("DbUpdateLog", mapList.get(num));
                if (num == mapList.size() - 1) {
                    systemConfig.setNewFileMaxId(String.valueOf((int) mapList.get(num).get("Id")));
                    Log.i("tagg", "首次初始化数据后记录下的最大ID--->" + (int) mapList.get(num).get("Id"));
                }
            }

            List<String> tableNames = NetUtils.getTableNamelist(mapList, "TableName");//筛选需要更新的所有表名集合
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
                    }else if (singletablename.equals("PersonInfo")){
                        promptlist.add("PersonInfo");
                    }else if (singletablename.equals("DepartInfo")){
                        promptlist.add("DepartInfo");
                    }

                    Map<String, Map> m = new HashMap<>();
                    for (int i = 0; i < singleTableData.size(); i++) {
                        m.put(singleTableData.get(i).get("TargetId") + "", (Map) singleTableData.get(i));
                    }
                    List<Map<String, Object>> singleTableDatalist = new ArrayList<>();

                    for (int i = 0; i < ListId.size(); i++) {
                        singleTableDatalist.add(m.get(ListId.get(i)));
                    }
                    List<Map<String, Object>> addTableDatalist = NetUtils.getSingleTableNameList(singleTableDatalist, "UpdateType", 1);//获取单个表的insert
                    if (addTableDatalist.size() != 0) {
                        Log.i("新添加操作", String.valueOf(addTableDatalist));
                        NetUtils.Dosingletableinsert(addTableDatalist, singletablename, dbOpenHelper, systemConfig);
                    }
                    List<Map<String, Object>> upTableDatalist = NetUtils.getSingleTableNameList(singleTableDatalist, "UpdateType", 2);//获取单个表的更新
                    if (upTableDatalist.size() != 0) {
                        Log.i("更新操作", String.valueOf(upTableDatalist));
                        NetUtils.DosingletableUpdata(upTableDatalist, singletablename, dbOpenHelper, systemConfig);
                    }
                    List<Map<String, Object>> deleteTableDatalist = NetUtils.getSingleTableNameList(singleTableDatalist, "UpdateType", 3);//获取单个表的更新
                    try {
                        if (deleteTableDatalist.size() != 0) {
                            Log.i("删除操作", String.valueOf(deleteTableDatalist));
                            NetUtils.DosingletableDelete(deleteTableDatalist, singletablename, dbOpenHelper);
                        }
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        Message message = new Message();
        StringBuffer toast = new StringBuffer();
        if (promptlist.size() != 0) {
            for (int i = 0; i < promptlist.size(); i++) {
                String tablename = promptlist.get(i);
                if (tablename.equals("ExamNotify")) {
                    toast.append("培训考试");
                } else if (tablename.equals("Announcement")) {
                    toast.append("通知公告");
                } else if (tablename.equals("TraficFiles")) {
                    toast.append("行车资料");
                }else if (tablename.equals("PersonInfo")){
                    toast.append("人员信息");
                }else if(tablename.equals("DepartInfo")){
                    toast.append("部门信息");
                }
                toast.append("、");
            }
            toast.deleteCharAt(toast.lastIndexOf("、"));
        }
        String stringprompt = toast.toString();
        message.obj = stringprompt;
        message.arg1 = 1;
        handler.sendMessage(message);
    }

    private void dataUpdatePrompt(TextView tv) {
        new AlertDialog.Builder(getActivity())
                .setTitle("提示信息")
                .setView(tv)
                .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        needFiles();
                    }
                })
                .show();
    }

    private void needFiles() {
        List<Map> fileList1 = dbOpenHelper.queryListMap("select * from Announcement where AnnounceType=? and IsLoaded=?", new String[]{"1", "false"});
        List<Map> fileList2 = dbOpenHelper.queryListMap("select * from StationFiles where IsDelete=? and IsLoaded=?", new String[]{"false", "false"});
        List<Map> fileList3 = dbOpenHelper.queryListMap("select * from TraficFiles where IsDelete=? and IsLoaded=?", new String[]{"0", "false"});
        needfiles = new ArrayList<>();
        if (fileList1.size() > 0) {
            Map map1 = new HashMap();
            map1.put("Announcement", fileList1);
            needfiles.add(map1);
        }

        if (fileList2.size() > 0) {
            Map map2 = new HashMap();
            map2.put("StationFiles", fileList2);
            needfiles.add(map2);
        }

        if (fileList3.size() > 0) {
            Map map3 = new HashMap();
            map3.put("TraficFiles", fileList3);
            needfiles.add(map3);
        }

        if (needfiles.size() > 0) {
            selectDownload(needfiles);
            downloaddialog.show();
        } else {
            UtilisClass.showToast(getActivity(), "无需要下载的文件！");
        }
    }

    private void selectDownload(List<Map<String, List>> needlists) {
        downloaddialog = new CustomDialog(getActivity(), R.style.mydialog);
        View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_selectdownload, null);
        downloaddialog.setContentView(contentView);
        downloaddialog.setCanceledOnTouchOutside(true);

        Window dialogWindow = downloaddialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        lp.width = (int) (width * 0.7);
        lp.height = (int) (height * 0.625);
        dialogWindow.setAttributes(lp);

        Button bt_announcement = (Button) contentView.findViewById(R.id.bt_announcement);//下载文件类型公告
        Button bt_stationfiles = (Button) contentView.findViewById(R.id.bt_stationfiles);//下载站点相关文件
        Button bt_traficfiles = (Button) contentView.findViewById(R.id.bt_traficfiles);//下载站点相关文件
        Button bt_alldownload = (Button) contentView.findViewById(R.id.bt_alldownload);//全部下载
        Button bt_close = (Button) contentView.findViewById(R.id.bt_close);//全部下载

        for (int i = 0; i < needlists.size(); i++) {
            Map map = needlists.get(i);
            java.util.Iterator it = map.entrySet().iterator();
            String mapkey = null;
            while (it.hasNext()) {
                java.util.Map.Entry entry = (java.util.Map.Entry) it.next();
                mapkey = entry.getKey().toString();// 返回与此项对应的键
            }
            if (mapkey.equals("Announcement")) {
                List<Map> list = (List<Map>) map.get("Announcement");
                if (list.size() > 0) {
                    bt_announcement.setText("开始下载最新公告相关文件" + list.size() + "个文件");
                }
            } else if (mapkey.equals("StationFiles")) {
                List<Map> list = (List<Map>) map.get("StationFiles");
                if (list.size() > 0) {
                    int size = clacSize(list);
                    bt_stationfiles.setText("开始下载站点相关文件" + list.size() + "个文件" + "大约" + size + "KB");
                }
            } else if (mapkey.equals("TraficFiles")) {
                List<Map> list = (List<Map>) map.get("TraficFiles");
                if (list.size() > 0) {
                    int size = clacSize(list);
                    bt_traficfiles.setText("开始下载行车资料相关文件" + list.size() + "个文件" + "大约" + size + "KB");
                }

            }
        }

        bt_announcement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog();
                announcement();
            }
        });

        bt_stationfiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog();
                stationFiles();
            }
        });

        bt_traficfiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog();
                traficFiles();
            }
        });

        bt_alldownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog();
                needDownloadFile();
            }
        });
        bt_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (downloaddialog != null) {
                    downloaddialog.dismiss();
                }
            }
        });
    }

    private int clacSize(List<Map> list) {
        int size = 0;
        for (int i = 0; i < list.size(); i++) {
            String str = list.get(i).get("FileSize").toString();
            if (!str.isEmpty()) {
                size = size + Integer.parseInt(str);
            }
        }
        return size;
    }

    private void dialog() {
        if (downloaddialog != null) {
            downloaddialog.dismiss();
        }
    }

    private void announcement() {
        List<Map> fileList2 = dbOpenHelper.queryListMap("select * from Announcement where AnnounceType=? and IsLoaded=?", new String[]{"1", "false"});
        if (fileList2.size() != 0) {
            if (fileList2.size() != 0) {
                fileType = "Announcement";
                downloadAndSave(fileType, fileList2);
            }
        }
    }

    private void stationFiles() {
        List<Map> stationFiles = dbOpenHelper.queryListMap("select * from StationFiles where IsDelete=? and IsLoaded=?", new String[]{"false", "false"});
        if (stationFiles.size() != 0) {
            fileType = "StationFiles";
            downloadAndSave(fileType, stationFiles);
        }
    }

    private void traficFiles() {
        List<Map> fileList = dbOpenHelper.queryListMap("select * from TraficFiles where IsDelete=? and IsLoaded=?", new String[]{"0", "false"});
        if (fileList.size() != 0) {
            if (fileList.size() != 0) {
                fileType = "TraficFiles";
                downloadAndSave(fileType, fileList);
            }
        }
    }

    private void needDownloadFile() {
        announcement();
        traficFiles();
        stationFiles();
    }

    private void downloadAndSave(final String fileType, final List<Map> fileinfor) {
        for (int i = 0; i < fileinfor.size(); i++) {
            final int num = i;
            final Map filemap = fileinfor.get(i);
            if (!filemap.get("FilePath").toString().equals("")) {

                String filepath = filemap.get("FilePath") + "";
                String url = systemConfig.getHost() + filepath;
                final String fileName = filemap.get("FilePath").toString().split("/")[4];
                downloadRequest = NoHttp.createDownloadRequest(url,
                        SDCardHelper.fileSdkPath(NetUtils.TRAFFICDATA),
                        fileName,
                        true,
                        false);

                Myapplilcation.downloadQueue.add(WHAT, downloadRequest, new DownloadListener() {
                    @Override
                    public void onDownloadError(int i, Exception e) {
                    }

                    @Override
                    public void onStart(int i, boolean b, long l, Headers headers, long l1) {
                        Log.i("123456", "----->" + headers.toString() + "-->boolean" + b);
                    }

                    @Override
                    public void onProgress(int i, int i1, long l, long l1) {
                    }

                    @Override
                    public void onFinish(int i, String s) {
                        Log.i("123456", "----->" + s.toString());
                        final String fileSavePath = UtilisClass.getuuid();//文件保存路径
                        String loachpath = SDCardHelper.fileSdkPath(NetUtils.TRAFFICDATA) + File.separator + fileName;
                        if (fileType.equals("TraficFiles"))
                            if (filemap.get("FileExtension").equals(".zip")) {
                                try {
                                    String unzipfilepath = loachpath + fileSavePath;
                                    UtilisClass.unZipFile(loachpath, unzipfilepath);
                                    String filetestpath = UtilisClass.getFileDir(unzipfilepath);
                                    dbOpenHelper.update("TraficFiles", new String[]{"LocaPath", "IsLoaded"},
                                            new Object[]{filetestpath, "true"}, new String[]{"Id"}, new String[]{filemap.get("Id") + ""});

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                dbOpenHelper.update("TraficFiles", new String[]{"LocaPath", "IsLoaded"},
                                        new Object[]{loachpath, "true"}, new String[]{"Id"}, new String[]{filemap.get("Id") + ""});
                            }
                        else if (fileType.equals("Announcement")) {
                            dbOpenHelper.update("Announcement", new String[]{"LocaPath", "IsLoaded"},
                                    new Object[]{loachpath, "true"}, new String[]{"Id"}, new String[]{filemap.get("Id") + ""});

                        } else if (fileType.equals("StationFiles")) {
                            if (filemap.get("FileExtension").equals(".zip")) {
                                try {
                                    String unzipfilepath = loachpath + fileSavePath;
                                    UtilisClass.unZipFile(loachpath, unzipfilepath);
                                    String filetestpath = UtilisClass.getFileDir(unzipfilepath);
                                    dbOpenHelper.update("StationFiles", new String[]{"LocaPath", "IsLoaded"},
                                            new Object[]{filetestpath, "true"}, new String[]{"Id"}, new String[]{filemap.get("Id") + ""});

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                dbOpenHelper.update("StationFiles", new String[]{"LocaPath", "IsLoaded"},
                                        new Object[]{loachpath, "true"}, new String[]{"Id"}, new String[]{filemap.get("Id") + ""});
                            }
                        }

                        if (num == fileinfor.size() - 1) {
                            Message messagefirst = new Message();
                            messagefirst.arg1 = 3;
                            handler.sendMessage(messagefirst);
                        }
                    }

                    @Override
                    public void onCancel(int what) {
                    }
                });
            }
        }
    }

    private void comleteFiles(List<Map<String, List>> needlists) {
        comletefiles = new ArrayList<>();
        for (int i = 0; i < needlists.size(); i++) {
            Map map = needlists.get(i);
            java.util.Iterator it = map.entrySet().iterator();
            String mapkey = null;
            while (it.hasNext()) {
                java.util.Map.Entry entry = (java.util.Map.Entry) it.next();
                mapkey = entry.getKey().toString();// 返回与此项对应的键
            }
            if (mapkey.equals("Announcement")) {
                List<Map> list = (List<Map>) map.get("Announcement");
                if (list.size() > 0) {
                    for (int j = 0; j < list.size(); j++) {
                        String id = list.get(j).get("Id").toString();
                        Map announcement = dbOpenHelper.queryItemMap("select * from Announcement where Id=? and IsLoaded=?", new String[]{id, "true"});
                        if (!announcement.isEmpty()) {
                            comletefiles.add(announcement);
                        }
                    }
                }
            } else if (mapkey.equals("StationFiles")) {
                List<Map> list = (List<Map>) map.get("StationFiles");
                if (list.size() > 0) {
                    for (int j = 0; j < list.size(); j++) {
                        String id = list.get(j).get("Id").toString();
                        Map stationfiles = dbOpenHelper.queryItemMap("select * from StationFiles where Id=? and IsLoaded=?", new String[]{id, "true"});
                        if (!stationfiles.isEmpty()) {
                            comletefiles.add(stationfiles);
                        }
                    }
                }
            } else if (mapkey.equals("TraficFiles")) {
                List<Map> list = (List<Map>) map.get("TraficFiles");
                if (list.size() > 0) {
                    for (int j = 0; j < list.size(); j++) {
                        String id = list.get(j).get("Id").toString();
                        Map traficfiles = dbOpenHelper.queryItemMap("select * from TraficFiles where Id=? and IsLoaded=?", new String[]{id, "true"});
                        if (!traficfiles.isEmpty()) {
                            comletefiles.add(traficfiles);
                        }
                    }
                }
            }
        }

        if (comletefiles.size() > 0) {
            downloadComplet();
            completddialog.show();
        }
    }

    private void downloadComplet() {
        completddialog = new CustomDialog(getActivity(), R.style.mydialog);
        final View contentView1 = LayoutInflater.from(getActivity()).inflate(R.layout.diaog_downloadcomplete, null);
        completddialog.setContentView(contentView1);
        completddialog.setCanceledOnTouchOutside(true);

        Window dialogWindow = completddialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.RIGHT);
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        lp.width = (int) (width * 0.6);
        lp.height = (int) (height * 0.4);
        dialogWindow.setAttributes(lp);

        ListView lv_completefiles = (ListView) contentView1.findViewById(R.id.lv_completefiles);//下载完文件列表
        Button bt_completeclose = (Button) contentView1.findViewById(R.id.bt_completeclose);//全部下载

        if (comletefiles.size() > 0) {
            completeFilesAdapter = new CompleteFilesAdapter(getActivity(), comletefiles);
            if (completeFilesAdapter != null) {
                lv_completefiles.setAdapter(completeFilesAdapter);
            }
        }

        lv_completefiles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String filename = comletefiles.get(position).get("FilePath").toString().split("/")[4];
                String fileExtension = comletefiles.get(position).get("FileExtension") + "";
                fileExtension.toLowerCase();
//                String fileExtension = "." + FilenameUtils.getExtension(filename);
//                fileExtension.toLowerCase();

                if (!fileExtension.equals("")) {
                    if (fileExtension.equals(".zip") || fileExtension.equals(".htm") || fileExtension.equals(".html")) {
                        String filepath = comletefiles.get(position).get("LocaPath") + "";
                        if (filepath != null && !filepath.equals("") && !filepath.equals("null")) {
                            Intent htmlintent = new Intent(getActivity(), HtmlWebView.class);
                            htmlintent.putExtra("FilePath", filepath);
                            startActivity(htmlintent);
                        } else {
                            UtilisClass.showToast(getActivity(), "未找到该文件！");
                        }
                    } else if (fileExtension.equals(".mp4") || fileExtension.equals(".avi")
                            || fileExtension.equals(".mpg") || fileExtension.equals(".wmv")) {
                        String filepath = comletefiles.get(position).get("LocaPath") + "";
                        if (filepath != null && !filepath.equals("") && !filepath.equals("null")) {
                            Intent videointent = new Intent(Intent.ACTION_VIEW);
                            videointent.setDataAndType(Uri.parse(filepath), "video/*");
                            startActivity(videointent);
                        } else {
                            UtilisClass.showToast(getActivity(), "未找到该文件！");
                        }
                    } else if (fileExtension.equals(".swf") || fileExtension.equals(".exe") || fileExtension.equals(".mov")
                            || fileExtension.equals(".vsd") || fileExtension.equals(".rmvb")) {
                        UtilisClass.showToast(getActivity(), "暂不支持该类型文件打开！");
                    } else if (fileExtension.equals(".docx") || fileExtension.equals(".pptx") || fileExtension.equals(".pdf")
                            || fileExtension.equals(".doc") || fileExtension.equals(".ppt") || fileExtension.equals(".xls")
                            || fileExtension.equals(".xlsx") || fileExtension.equals(".ppsx") || fileExtension.equals(".wps")
                            || fileExtension.equals(".txt")) {
                        String filepath = comletefiles.get(position).get("LocaPath") + "";
                        if (filepath != null && !filepath.equals("") && !filepath.equals("null")) {
                            Intent bridgeintent = new Intent(getActivity(), BridgeActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("FilePath", filepath);
                            bridgeintent.putExtras(bundle);
                            startActivity(bridgeintent);
                        } else {
                            UtilisClass.showToast(getActivity(), "未找到该文件！");
                        }
                    } else if (fileExtension.equals(".png") || fileExtension.equals(".jpg")
                            || fileExtension.equals(".bmp") || fileExtension.equals(".jpeg")) {
                        String filepath = comletefiles.get(position).get("LocaPath") + "";
                        if (filepath != null && !filepath.equals("") && !filepath.equals("null")) {
                            Intent imageintent = new Intent();
                            imageintent.setAction(android.content.Intent.ACTION_VIEW);
                            imageintent.setDataAndType(Uri.parse("file://" + filepath), "image/*");
                            startActivity(imageintent);
                        } else {
                            UtilisClass.showToast(getActivity(), "未找到该文件！");
                        }
                    } else if (fileExtension.equals(".swf") || fileExtension.equals(".exe") || fileExtension.equals(".mov")
                            || fileExtension.equals(".vsd") || fileExtension.equals(".rmvb")) {
                        UtilisClass.showToast(getActivity(), "暂不支持该类型文件打开！");
                    }
                }
            }
        });

        bt_completeclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (completddialog != null) {
                    completddialog.dismiss();
                }
            }
        });

    }

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
                setdialog();
                setUpDatas();
                break;
            case R.id.home2_upload:
                if (HttpUtils.isNetWorkConn(getActivity())) {
//                    updatadiolag.show();
//                    upLoadPool.execute(new Runnable() {
//                        @Override
//                        public void run() {
//                            updataarguments();
//                        }
//                    });
                } else {
                    UtilisClass.showToast(getActivity(), "更新失败！，网络未联通");
                }
                break;
            case R.id.home2_linkde1:
                List<Map> list = dbOpenHelper.queryListMap("select * from Announcement", null);
                System.out.println(list.toString());
                break;
        }
    }

    private void onrefresh() {
        onCreate(null);
    }

    public void setdialog() {
        updatadiolag = UtilisClass.setprogressDialog(getActivity(), "update");
    }

    private void updataarguments() {
        String[] tablename = NetUtils.dailyWorkUpTableNames;
        for (int i = 0; i < tablename.length; i++) {
            final String tableName = tablename[i];
            final List<Map> uplist = dbOpenHelper.queryListMap("select * from " + tableName + " where " +
                    "IsUploaded=?", new String[]{"0"});
            if (uplist.size() != 0) {
                String currenttime = UtilisClass.getStringDate();
                for (int j = 0; j < uplist.size(); j++) {
                    if (tableName.equals("InstructorKeyPerson")) {
                        String isremoved = uplist.get(j).get("IsRemoved") + "";
                        if (isremoved.equals("false")) {
                            uplist.get(j).put("ActualRemoveTime", "1990-1-1");
                        }
                    } else {
                        List<Map> list = new ArrayList<>();
                        list.add(uplist.get(j));
                        NetUtils.updataarguments3dinglehome(list, dbOpenHelper, systemConfig, tableName);
                    }
                }

            }
        }

        List<Map> InstructorQuotaRecordlist = dbOpenHelper.queryListMap("select * from InstructorQuotaRecord where " +
                "IsUploaded=?", new String[]{"0"});
        if (InstructorQuotaRecordlist.size() != 0) {
            for (int j = 0; j < InstructorQuotaRecordlist.size(); j++) {
                List<Map> list = new ArrayList<>();
                list.add(InstructorQuotaRecordlist.get(j));
                NetUtils.updateInstructorQuotaRecord(list, dbOpenHelper, systemConfig, "InstructorQuotaRecord");
                System.out.println(list.toString());
            }
        }
        Message message1 = new Message();
        message1.arg1 = 2;
        handler.sendMessage(message1);
    }

    private String getConnectWifiSsid() {
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        return wifiInfo.getSSID();
    }

    private class WifiReceiver extends BroadcastReceiver {
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onReceive(Context context, Intent intent) {
            if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
                switch (wifiState) {
                    case WifiManager.WIFI_STATE_DISABLED:
                        UtilisClass.showToast(context, "WIFI网卡不可用");
                        break;
                    case WifiManager.WIFI_STATE_DISABLING:
                        UtilisClass.showToast(context, "WIFI正在关闭");
                        break;
                }
            }

            if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                Parcelable parcelableExtra = intent
                        .getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (null != parcelableExtra) {
                    NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
                    NetworkInfo.State state = networkInfo.getState();
                    boolean isConnected = state == NetworkInfo.State.CONNECTED;
                    if (isConnected) {
                        home2_linkde1.setText("已连接");
                        tv18.setText(getConnectWifiSsid().substring(1, getConnectWifiSsid().length() - 1));
                        wifi_im.setImageResource(R.mipmap.i2_8);

                        WifiInfo mWifiInfo = wifiManager.getConnectionInfo();
                        String routerBssid = mWifiInfo.getBSSID();
                        if (routerBssid != null) {
                            routerBssid = routerBssid.replaceAll(":", "-");
                            List<Map> routerlist = dbOpenHelper.queryListMap("select * from InstructorRouterPosition  where BssId like ?"
                                    , new String[]{"%" + routerBssid + "%"});

                            if (routerlist.size() != 0) {
                                String currentdate = UtilisClass.getStringDate();
                                String routerid = routerlist.get(0).get("Id") + "";
                                dbOpenHelper.insert("InstructorWifiRecord", new String[]{"InstructorId", "RouterPositionId", "ConnectTime", "ConnectFlag", "DeviceId"},
                                        new Object[]{personId, routerid, currentdate, "1", "0"});
                            }
                        }
                    } else {
                        home2_linkde1.setText("已断开");
                        wifi_im.setImageResource(R.mipmap.i2_7);
                        tv18.setText("");
                    }
                }
            }

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(wifireceiver);
    }
}