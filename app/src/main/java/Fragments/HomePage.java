package Fragments;

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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
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
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

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

//import org.apache.commons.io.FilenameUtils;/
import org.json.JSONException;
import org.json.JSONObject;
import org.kymjs.kjframe.KJHttp;
import org.kymjs.kjframe.http.HttpCallBack;
import org.kymjs.kjframe.http.HttpParams;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

import Adapter.CompleteFilesAdapter;
import DBUtils.DBOpenHelper;
import Utils.CommonAdapter;
import Utils.CustomDialog;
import Utils.HttpUtils;
import Utils.NetUtils;
import Utils.SDCardHelper;
import Utils.ViewHolder;
import config.ISystemConfig;
import config.SystemConfigFactory;
import zj.com.mc.BridgeActivity;
import zj.com.mc.HtmlWebView;
import zj.com.mc.MainActivity;
import zj.com.mc.Myapplilcation;
import zj.com.mc.NoticeDetails;
import zj.com.mc.R;
import zj.com.mc.UtilisClass;

public class HomePage extends Fragment implements View.OnClickListener {

    private DBOpenHelper dbOpenHelper;
    private ViewPager traindriver_viewpager;
    private RadioGroup traindrivergroup;
    private ImageView wifiicon, driverPhtot;
    private TextView driver_wifiname, driverworkname, drivername, driverdepartment, driverpostname, uploaded_time1;
    private TextView driver_linked;
    private WifiManager wifiManager;
    private TextView driver_loaded, driver_uploaded;
    private String personID;
    private static int WHAT = 0;
    private List<Map> driverInfolist;
    private List<View> pagerlist;
    private int[] bmList;
    private ListView driver_listview;
    private ListView warn_listview;
    private ListView accessory_listview;
    private ListView count_listview;

    private ProgressDialog upDatadialog;
    private List<Map<String, Object>> mapList;
    private List<String> promptlist;
    private WifiReceiver2 wifiReceiver2;
    private ISystemConfig systemConfig;
    private RequestQueue registerQueue;
    private DownloadRequest downloadRequest;
    private String fileType;
    private CustomDialog downloaddialog;
    private CustomDialog completddialog;
    private CompleteFilesAdapter completeFilesAdapter;
    private List<Map> comletefiles;
    private List<Map<String, List>> needfiles;
    private MainActivity mainActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();

        dbOpenHelper = DBOpenHelper.getInstance(getActivity().getApplicationContext());
        systemConfig = SystemConfigFactory.getInstance(getActivity()).getSystemConfig();
        personID = systemConfig.getUserId();//登录人id
        driverInfolist = new ArrayList<>();
        mapList = new ArrayList<>();
        promptlist = new ArrayList<>();
        bmList = new int[]{R.drawable.vp1, R.drawable.vp2, R.drawable.vp3, R.drawable.vp1, R.drawable.vp2, R.drawable.vp3};

        //注册WiFi广播接收器
        IntentFilter filter2 = new IntentFilter();
        filter2.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter2.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter2.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        wifiReceiver2 = new WifiReceiver2();
        getActivity().registerReceiver(wifiReceiver2, filter2);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.homepage, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {

        driverInfolist = dbOpenHelper.queryListMap("select * from ViewPersonInfo where Id=?", new String[]{personID});
        systemConfig.setUserPostId(driverInfolist.get(0).get("PostId").toString());
        wifiicon = (ImageView) view.findViewById(R.id.driver_wifiicon);
        driver_wifiname = (TextView) view.findViewById(R.id.driver_wifiname);
        driver_loaded = (TextView) view.findViewById(R.id.driver_loaded);
        driver_loaded.setOnClickListener(this);
        driver_uploaded = (TextView) view.findViewById(R.id.driver_uploaded);
        driver_uploaded.setOnClickListener(this);
        driver_listview = (ListView) view.findViewById(R.id.driver_listview);
        warn_listview = (ListView) view.findViewById(R.id.warn_listview);
        accessory_listview = (ListView) view.findViewById(R.id.accessory_listview);
        count_listview = (ListView) view.findViewById(R.id.count_listview);
        driverPhtot = (ImageView) view.findViewById(R.id.driver_photo);//头像
        driver_linked = (TextView) view.findViewById(R.id.driver_linked);//wifi设置
        wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        driverworkname = (TextView) view.findViewById(R.id.driver_worknumber);//登录人工号
        drivername = (TextView) view.findViewById(R.id.driver_name);//登录人姓名
        driverdepartment = (TextView) view.findViewById(R.id.driver_department);//登录人部门
        driverpostname = (TextView) view.findViewById(R.id.driver_postname);//登录人岗位
        traindriver_viewpager = (ViewPager) view.findViewById(R.id.traindriver_viewpager);//首页轮播图
        traindrivergroup = (RadioGroup) view.findViewById(R.id.traindriver_radiogroup);//首页轮播，跟新记录 radiogroup
        uploaded_time1 = (TextView) view.findViewById(R.id.uploaded_time1);//数据更新时间

        setphotoImage();
        setTraindriverInfo();
        initwifishow();
        setViewpager();
        traindriver_viewpager.setCurrentItem(Integer.MAX_VALUE / 2 - Integer.MAX_VALUE / 2 % pagerlist.size());
        homeHandler.sendEmptyMessageDelayed(3, 3000);
        setTrainRadiogroup();
    }

    public void setdialog() {
        upDatadialog = UtilisClass.setprogressDialog(getActivity(), "update");
    }

    private void setphotoImage() {
        if (driverInfolist.size() != 0) {
            if (driverInfolist.get(0).get("PhotoPath").toString().length() != 0) {
                String netPhotoPath = driverInfolist.get(0).get("PhotoPath") + "";
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
                            driverPhtot.setImageBitmap(bm);
                            UtilisClass.saveimg(getActivity(), picName, bm);
                            String locaPath = getActivity().getExternalFilesDir(null).getPath() + "/" + picName + ".png";
                            dbOpenHelper.update("PersonInfo", new String[]{"LocaPhotoPath"}, new Object[]{locaPath}, new String[]{"Id"}, new String[]{personID});
                        }

                        @Override
                        public void onFailed(int i, Response<Bitmap> response) {
                            List<Map> mapList1 = dbOpenHelper.queryListMap("select * from PersonInfo where Id=?", new String[]{personID});
                            String locaphotopath = mapList1.get(0).get("LocaPhotoPath") + "";
                            if (locaphotopath.length() != 0) {
                                Uri uri = Uri.parse(locaphotopath);
                                driverPhtot.setImageURI(uri);
                            }
                        }

                        @Override
                        public void onFinish(int i) {
                        }
                    });
                }
            } else {
//                driverPhtot.setImageResource(R.mipmap.reader);
            }
        } else {
            List<Map> mapList1 = dbOpenHelper.queryListMap("select * from PersonInfo where Id=?", new String[]{personID});
            String locaphotopath = mapList1.get(0).get("LocaPhotoPath") + "";
            if (locaphotopath.length() != 0) {
                Uri uri = Uri.parse(locaphotopath);
                driverPhtot.setImageURI(uri);
            }
        }
    }

    private void setTraindriverInfo() {
        String driverWorkNo = "";
        String dirverName = "";
        String driverDeparnmentName = "";
        String driverPostName = "";
        if (driverInfolist.size() != 0) {
            driverWorkNo = driverInfolist.get(0).get("WorkNo") + "";
            dirverName = driverInfolist.get(0).get("Name") + "";
            driverDeparnmentName = driverInfolist.get(0).get("DepartmentName") + "";
            driverPostName = driverInfolist.get(0).get("PostName") + "";
        }
        driverworkname.setText(driverWorkNo);
        drivername.setText(dirverName);
        driverdepartment.setText(driverDeparnmentName);
        driverpostname.setText(driverPostName);
    }

    private void initwifishow() {
        driver_linked.setOnClickListener(this);//wifi断开
        if (UtilisClass.isWifi(getActivity())) {
            driver_linked.setText("已连接");
            driver_wifiname.setText(getConnectWifiSsid().substring(1, getConnectWifiSsid().length() - 1));
            wifiicon.setImageResource(R.mipmap.i2_8);
        } else {
            driver_linked.setText("已断开");
            wifiicon.setImageResource(R.mipmap.i2_7);
            driver_wifiname.setText("");
        }
    }

    private String getConnectWifiSsid() {
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        return wifiInfo.getSSID();
    }

    private void setViewpager() {
        pagerlist = new ArrayList<>();
        for (int i = 0; i < bmList.length; i++) {
            View view = new ImageView(getActivity());
            view.setBackground(getActivity().getResources().getDrawable(bmList[i]));
            pagerlist.add(view);
        }

        PagerAdapter pagerAdapter = new PagerAdapter() {
            @Override
            public int getCount() {
                return Integer.MAX_VALUE;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(pagerlist.get(position % pagerlist.size()));
                return pagerlist.get(position % pagerlist.size());
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(pagerlist.get(position % pagerlist.size()));
            }
        };
        traindriver_viewpager.setAdapter(pagerAdapter);
    }

    private void setTrainRadiogroup() {
        traindrivergroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.traindriver_radio1:
                        traindriver_viewpager.setVisibility(View.VISIBLE);
                        driver_listview.setVisibility(View.GONE);
                        warn_listview.setVisibility(View.GONE);
                        accessory_listview.setVisibility(View.GONE);
                        count_listview.setVisibility(View.GONE);
                        break;
                    case R.id.traindriver_radio2:
                        driver_listview.setVisibility(View.VISIBLE);
                        traindriver_viewpager.setVisibility(View.GONE);
                        warn_listview.setVisibility(View.GONE);
                        accessory_listview.setVisibility(View.GONE);
                        count_listview.setVisibility(View.GONE);
                        setNewtoast();
                        break;
                    case R.id.traindriver_radio3:
                        warn_listview.setVisibility(View.VISIBLE);
                        traindriver_viewpager.setVisibility(View.GONE);
                        driver_listview.setVisibility(View.GONE);
                        accessory_listview.setVisibility(View.GONE);
                        count_listview.setVisibility(View.GONE);
                        setWarning();
                        break;
                    case R.id.traindriver_radio4:
                        accessory_listview.setVisibility(View.VISIBLE);
                        traindriver_viewpager.setVisibility(View.GONE);
                        driver_listview.setVisibility(View.GONE);
                        warn_listview.setVisibility(View.GONE);
                        count_listview.setVisibility(View.GONE);
                        setAccessory();
                        break;
                    case R.id.traindriver_radio5:
                        count_listview.setVisibility(View.VISIBLE);
                        traindriver_viewpager.setVisibility(View.GONE);
                        driver_listview.setVisibility(View.GONE);
                        warn_listview.setVisibility(View.GONE);
                        accessory_listview.setVisibility(View.GONE);
                        gettrafficStatistics();
                        break;
                }
            }
        });
    }

    private void setNewtoast() {
        final List<Map> notices = dbOpenHelper.queryListMap("select Id,Title,AnnounceType,LocaPath,IsRead from Announcement where BusinessType = 1", null);
        if (notices.size() > 0) {
            Collections.reverse(notices);
            setAdapter(driver_listview, notices);

            driver_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    openNotice(notices, i);
                }
            });
        }
    }

    private void setWarning() {
//        final List<Map> warnings = dbOpenHelper.queryListMap("select Id,Title,AnnounceType,LocaPath,IsRead from Announcement where BusinessType = 2", null);
        final List<Map> warnings = dbOpenHelper.queryListMap("select * from Announcement where BusinessType = 2", null);
        if (warnings.size() > 0) {
            Collections.reverse(warnings);
            setAdapter(warn_listview, warnings);
            warn_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    openNotice(warnings, i);
                }
            });
        }
    }

    private void setAccessory() {
        List<Map> accessorys = new ArrayList<>();
        if (systemConfig.getPostId().equals("130") || systemConfig.getPostId().equals("134")) {
            accessorys = dbOpenHelper.queryListMap("select Id,Title,AnnounceType,LocaPath,IsRead from Announcement where BusinessType = 3", null);
        } else {
            accessorys = dbOpenHelper.queryListMap("select Id,Title,AnnounceType,LocaPath,IsRead from Announcement where DepartmentId = ? and BusinessType = 3", new String[]{systemConfig.getDepartmentId()});
        }
        if (accessorys.size() > 0) {
            Collections.reverse(accessorys);
            setAdapter(accessory_listview, accessorys);
            final List<Map> finalAccessorys = accessorys;
            accessory_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    openNotice(finalAccessorys, i);
                }
            });
        }
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

    private void recordOperateLog(int LogType, String LogContent) {
        Date dt = new Date();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String nowTime = df.format(dt);
        dbOpenHelper.insert("AppOperateLog", new String[]{"LogType", "LogContent", "OperatorId", "DeviceId", "AddTime"},
                new Object[]{LogType, LogContent, systemConfig.getOperatorId(), 0, nowTime});
    }

    private void gettrafficStatistics() {
        List<Map> countlist = dbOpenHelper.queryListMap("select * from DriveRecords where IsUploaded = 1 order by Id desc", null);
        if (countlist.size() != 0) {
            Collections.reverse(countlist);
        }
        count_listview.setAdapter(new CommonAdapter<Map>(getActivity(), countlist, R.layout.fileshowlistview) {
            @Override
            protected void convertlistener(ViewHolder holder, Map map) {
            }

            @Override
            public void convert(ViewHolder holder, Map map) {
                holder.getView(R.id.fileshow_fileicn).setVisibility(View.GONE);
                holder.setText(R.id.fileshow_listviewitem, spliceTrafficStatistics(map));
            }
        });
    }

    Handler homeHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 3) {
                traindriver_viewpager.setCurrentItem(traindriver_viewpager.getCurrentItem() + 1);
                homeHandler.sendEmptyMessageDelayed(3, 5000);
            }
            if (msg.arg1 == 5) {
                String uploadedtime = UtilisClass.getStringDate1();
                uploaded_time1.setText("更新时间:" + uploadedtime);
                upDatadialog.cancel();
                mainActivity.upAppOperateLog();
                mainActivity.upWifiRecord();
                setNewtoast();
                setWarning();
                setAccessory();
                gettrafficStatistics();
                String toaststring = (String) msg.obj;
                TextView tv = new TextView(getActivity());
                tv.setPadding(40, 20, 0, 0);
                tv.setTextSize(20);
                if (!toaststring.equals("")) {
                    tv.setText(toaststring + " 数据有更新！");
                    dataUpdatePrompt(tv);
                } else {
                    UtilisClass.showToast(getActivity(), "数据更新完成！");
                }
            }
            if (msg.arg1 == 2) {
                UtilisClass.showToast(getActivity(), "数据上传完成！");
                upDatadialog.cancel();
            }
            if (msg.arg1 == 1) {
                UtilisClass.showToast(getActivity(), "资料更失败！");
                upDatadialog.cancel();
            }
            if (msg.arg1 == 6) {
                UtilisClass.showToast(getActivity(), "暂无数据更新！");
                upDatadialog.cancel();
                mainActivity.upAppOperateLog();
                mainActivity.upWifiRecord();
                needFiles();
            }
            if (msg.arg1 == 7) {
                Log.i("文件下载", "--->完成提示");
                if (upDatadialog != null) {
                    upDatadialog.dismiss();
                }
                comleteFiles(needfiles);
                mainActivity.upAppOperateLog();
                mainActivity.upWifiRecord();
            }
        }
    };

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

    private void dialog() {
        if (downloaddialog != null) {
            downloaddialog.dismiss();
            upDatadialog.show();
        }
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

    @Override
    public void onResume() {
        super.onResume();
        initwifishow();
        setphotoImage();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.driver_linked:
                if (android.os.Build.VERSION.SDK_INT > 10) {
                    startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
                } else {
                    startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                }
                break;
            case R.id.driver_loaded:
                setdialog();
                setupdataloaded();
                break;
            case R.id.driver_uploaded:
                if (HttpUtils.isNetWorkConn(getActivity())) {
                    upDatadialog.show();
                    Myapplilcation.getExecutorService().execute(new Runnable() {
                        @Override
                        public void run() {
                            updataarguments();
                        }
                    });
                } else {
                    UtilisClass.showToast(getActivity(), "资料上传失败！网络未联通");
                }
                break;
        }
    }

    private void setupdataloaded() {
        if (HttpUtils.isNetWorkConn(getActivity())) {
            upDatadialog.show();
            updateData();
        } else {
            UtilisClass.showToast(getActivity(), "WIFI未连接，无法更新数据！");
        }
    }

    private void updataarguments() {
        String[] tablename = NetUtils.driverNoteUpTableNames;
        for (int i = 0; i < tablename.length; i++) {
            final String tableName = tablename[i];
            final List<Map> uplist = dbOpenHelper.queryListMap("select * from " + tableName + " where " +
                    "IsUploaded=?", new String[]{"0"});
            if (uplist.size() != 0) {
                for (int j = 0; j < uplist.size(); j++) {
                    List<Map> list = new ArrayList<>();
                    list.add(uplist.get(j));
                    NetUtils.updataarguments3dinglehome(list, dbOpenHelper, systemConfig, tableName);
                }
            }
        }

        List<Map> driveRecordLists = dbOpenHelper.queryListMap("select * from DriveRecords where " +
                "IsUploaded=?", new String[]{"0"});
        if (driveRecordLists.size() != 0) {
            for (int j = 0; j < driveRecordLists.size(); j++) {
                List<Map> list = new ArrayList<>();
                Map map = driveRecordLists.get(j);
                list.add(map);
                List<Map> signPointList = dbOpenHelper.queryListMap("select * from DriveSignPoint where DriveRecordId=?", new String[]{String.valueOf(map.get("Id"))});
                List<Map> trainformationList = dbOpenHelper.queryListMap("select * from TrainFormation where DriveRecordId=?", new String[]{String.valueOf(map.get("Id"))});
                List<Map> noandlineList = dbOpenHelper.queryListMap("select * from DriveTrainNoAndLine where DriveRecordId=?", new String[]{String.valueOf(map.get("Id"))});
                boolean up = upDate(list, noandlineList, signPointList, trainformationList, map);
            }
        }
        Message message1 = new Message();
        message1.arg1 = 2;
        homeHandler.sendMessage(message1);
    }

    boolean up = false;

    private boolean upDate(final List<Map> RecordsList, List<Map> DriveTrainNoAndLineList, List<Map> DriveSignPoint, List<Map> TrainFormation, final Map map) {
        KJHttp kjhttp = new KJHttp();
        HttpParams params = new HttpParams();
        params.put("signature", "bcad117ce31ac75fcfa347acefc8d198");

        StringBuffer recordsList = NetUtils.getjson(RecordsList);
        StringBuffer signPoint = NetUtils.getjsonlist(DriveSignPoint);
        StringBuffer trainFormation = NetUtils.getjsonlist(TrainFormation);
        StringBuffer noandline = NetUtils.getjsonlist(DriveTrainNoAndLineList);

        String json = "[{";
        json += "\"DriveRecords\":" + recordsList + ",";
        json += "\"DriveSignPoints\":" + signPoint + ",";
        json += "\"TrainFormations\":" + trainFormation + ",";
        json += "\"DriveTrainNoAndLines\":" + noandline + "}]";

        // \"代表一个双引号字符
        // \\代表一个反斜线字符\
        // json.replace去掉
        json = json.replace("\\\"", "\"")
                .replace("\"null\"", "\"\"")
                .replace("\"ArriveTime\":\"\"", "\"ArriveTime\":\"1970-01-01\"")
                .replace("\"LeaveTime\":\"\"", "\"LeaveTime\":\"1970-01-01\"")
                .replace("\"AttendTime\":\"\"", "\"AttendTime\":\"1970-01-01\"")
                .replace("\"GetTrainTime\":\"\"", "\"GetTrainTime\":\"1970-01-01\"")
                .replace("\"LeaveDepotTime1\":\"\"", "\"LeaveDepotTime1\":\"1970-01-01\"")
                .replace("\"LeaveDepotTime2\":\"\"", "\"LeaveDepotTime2\":\"1970-01-01\"")
                .replace("\"ArriveDepotTime1\":\"\"", "\"ArriveDepotTime1\":\"1970-01-01\"")
                .replace("\"ArriveDepotTime2\":\"\"", "\"ArriveDepotTime2\":\"1970-01-01\"")
                .replace("\"GiveTrainTime\":\"\"", "\"GiveTrainTime\":\"1970-01-01\"")
                .replace("\"RecordEndTime\":\"\"", "\"RecordEndTime\":\"1970-01-01\"");
        try {
            json = URLEncoder.encode(json, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        params.put("json", json);
        kjhttp.post(systemConfig.getHost() + "/App/DrivePlanUpload", params, new HttpCallBack() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                Toast.makeText(getContext(), "上传成功" + t, Toast.LENGTH_SHORT).show();
                up = true;
                try {
                    boolean ok = dbOpenHelper.update("DriveRecords", new String[]{"IsUploaded"}, new Object[]{1}, new String[]{"Id"}, new String[]{String.valueOf(map.get("Id"))});
                    if (ok) {
                        List<Map> list = dbOpenHelper.queryListMap("select * from DriveRecords order by Id desc", new String[]{});
                    }
                } catch (Exception e) {
                    Log.e("上传手账记录", "数据库上传状态更改异常");
                }
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                Toast.makeText(getContext(), "上传失败" + errorNo + ":" + strMsg, Toast.LENGTH_SHORT).show();
                up = false;
            }
        });
        return up;
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
                Log.i("tagg", response.get().toString());
                String str = response.get().toString();
                Myapplilcation.getExecutorService().execute(new Runnable() {
                    @Override
                    public void run() {
                        writeDatabase(response.get().toString());
                    }
                });
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                String str = response.toString();
                UtilisClass.showToast(getActivity(), "获取数据失败！"+response.toString());
                if (upDatadialog != null) {
                    upDatadialog.cancel();
                }
            }

            @Override
            public void onFinish(int what) {
            }
        });
    }

    private void writeDatabase(String json) {
        Message message = new Message();
        try {
            JSONObject jsonObject = new JSONObject(json);
            if (jsonObject.get("code").equals(304) || jsonObject.get("code").equals("304")) {
                message.arg1 = 1;
                homeHandler.sendMessage(message);
            } else {
                String jsonArray = jsonObject.getString("data");
                mapList = Utils.StringListUtils.getList(jsonArray);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (mapList.size() != 0) {
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

                String stringprompt = toast.toString();
                message.obj = stringprompt;
                message.arg1 = 5;
                homeHandler.sendMessage(message);
            }
        } else {
            message.arg1 = 6;
            homeHandler.sendMessage(message);
        }
    }

    private void needDownloadFile() {
        announcement();
        traficFiles();
        stationFiles();
    }

    private void needFiles() {
        List<Map> fileList1 = dbOpenHelper.queryListMap("select * from Announcement where AnnounceType=1 and LocaPath is null", null);
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
                        Log.e("文件更新下载进度", "----->" + i + "====>" + i1 + "---->" + l + "====>" + l1);
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
                            messagefirst.arg1 = 7;
                            homeHandler.sendMessage(messagefirst);
                        }
                    }

                    @Override
                    public void onCancel(int what) {
                    }
                });
            }
        }
    }

    private class WifiReceiver2 extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {// 这个监听wifi的打开与关闭，与wifi的连接无关
                int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
                Log.e("H3c", "wifiState" + wifiState);
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
                    boolean isConnected = state == NetworkInfo.State.CONNECTED;// 当然，这边可以更精确的确定状态
                    Log.e("H3c", "isConnected" + isConnected);
                    String currentdate = "";
                    String routerid2 = "";

                    if (isConnected) {
                        driver_linked.setText("已连接");
                        driver_wifiname.setText(getConnectWifiSsid().substring(1, getConnectWifiSsid().length() - 1));
                        wifiicon.setImageResource(R.mipmap.i2_8);
                        WifiInfo mWifiInfo = wifiManager.getConnectionInfo();
                        String routerBssid = mWifiInfo.getBSSID();

                        List<Map> routerlist = dbOpenHelper.queryListMap("select * from InstructorRouterPosition  where BssId like ?"
                                , new String[]{"%" + routerBssid + "%"});

                        if (routerlist.size() != 0) {
                            currentdate = UtilisClass.getStringDate();
                            String routerid = routerlist.get(0).get("Id") + "";
                            dbOpenHelper.insert("InstructorWifiRecord", new String[]{"InstructorId", "RouterPositionId", "ConnectTime", "ConnectFlag"},
                                    new Object[]{personID, routerid, currentdate, "1"});
                        }
                    } else {
                        driver_linked.setText("已断开");
                        wifiicon.setImageResource(R.mipmap.i2_7);
                        driver_wifiname.setText("");
                        currentdate = UtilisClass.getStringDate();
                        dbOpenHelper.insert("InstructorWifiRecord", new String[]{"InstructorId", "RouterPositionId", "ConnectTime", "ConnectFlag"},
                                new Object[]{personID, routerid2, currentdate, "2"});

                        if (registerQueue != null) {
                            registerQueue.cancelAll();
                            registerQueue.stop();
                        }
                        if (downloadRequest != null) {
                            downloadRequest.removeAll();
                        }
                    }
                }
            }
        }
    }

    public String spliceTrafficStatistics(Map map) {
        long getTrainTime = Long.valueOf(dateToStamp(map.get("AttendTime").toString() + ":00"));
        long addTime = Long.valueOf(dateToStamp(map.get("AddTime").toString() + ":00"));
        StringBuilder sb = new StringBuilder(map.get("AttendTime").toString()).append("         ")
                .append(map.get("TrainCode").toString())
                .append("         ").append(map.get("LineName")).append("      ")
                .append(stampToDate(String.valueOf(addTime - getTrainTime)));
        return sb.toString();
    }

    public static String dateToStamp(String s) {
        String res = null;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = simpleDateFormat.parse(s);
            long ts = date.getTime();
            res = String.valueOf(ts);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public static String stampToDate(String s) {
        String res;
        long lt = new Long(s);
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        res = formatter.format(lt);
        return res;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            setNewtoast();
            setWarning();
            setAccessory();
            gettrafficStatistics();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(wifiReceiver2);
    }
}