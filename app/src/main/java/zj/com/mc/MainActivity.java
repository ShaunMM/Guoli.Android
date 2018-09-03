package zj.com.mc;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.yanzhenjie.nohttp.Headers;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.download.DownloadListener;
import com.yanzhenjie.nohttp.download.DownloadRequest;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;

import org.json.JSONException;
import org.json.JSONObject;
import org.kymjs.kjframe.KJHttp;
import org.kymjs.kjframe.http.HttpCallBack;
import org.kymjs.kjframe.http.HttpParams;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import DBUtils.DBOpenHelper;
import Fragments.DailyWork;
import Fragments.DriverData;
import Fragments.DriverNote;
import Fragments.HomePage;
import Fragments.HomePage2;
import Fragments.SystemTools;
import Fragments.TrainSchedule;
import Fragments.VocationalStudy;
import Utils.HttpUtils;
import Utils.NetUtils;
import Utils.SDCardHelper;
import config.ISystemConfig;
import config.SystemConfigFactory;

public class MainActivity extends FragmentActivity implements View.OnClickListener {

    public static final String F_DriverNote = "DriverNote";
    public static final String F_Attendance = "Attendance";
    public static final String F_TainOperation = "TainOperation";
    public static final String F_Background = "Background";

    private HomePage homePage;
    private HomePage2 homePage2;
    private DriverNote driverNote;
    private DriverData driverData;
    private VocationalStudy vocationalStudy;
    private DailyWork dailyWork;
    private TrainSchedule trainSchedule;
    private SystemTools systemTools;

    private RadioGroup rg;
    private FragmentManager manager;
    private FragmentTransaction transaction;
    private DBOpenHelper dbOpenHelper;
    private RadioButton radioButton5, radioButton2;
    private String Post = "trainMan";
    private ISystemConfig systemConfig;
    private DownloadRequest downloadRequest;
    private RequestQueue registerQueue;
    private String fileType;
    private Map<String, Object> appMap = new HashMap<>();
    public static final int FLAG_HOMEKEY_DISPATCHED = 0x80000000; //需要自己定义标志
    private ProgressDialog upDatadialog;
    private Timer timer;
    private int totalSize;

    Handler handlerfirst = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.arg1 == 7) {
                UtilisClass.showToast(MainActivity.this, "App暂无更新！");
            } else if (msg.arg1 == 3) {
                if (upDatadialog != null) {
                    upDatadialog.dismiss();
                }
            } else if (msg.arg1 == 1) {

                if (upDatadialog != null) {
                    upDatadialog.dismiss();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(FLAG_HOMEKEY_DISPATCHED, FLAG_HOMEKEY_DISPATCHED);//关键代码
        setContentView(R.layout.activity_main);
        findViewById(R.id.radio3).setOnClickListener(this);
        radioButton5 = (RadioButton) findViewById(R.id.radio5);
        radioButton2 = (RadioButton) findViewById(R.id.radio2);

        dbOpenHelper = DBOpenHelper.getInstance(getApplicationContext());
        systemConfig = SystemConfigFactory.getInstance(this).getSystemConfig();

        if (systemConfig.isFirstSync()) {
            SDCardHelper.saveFileToSDCardCustomDirss(NetUtils.TRAFFICDATA);
            SDCardHelper.saveFileToSDCardCustomDirss(NetUtils.APKPATH);
        }

        Myapplilcation.addActivity(this);
        initView();
        initFragments();
        setradiogroup();

//        //定位
//        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//        double Longitude = location.getLongitude();
//        double Latitude = location.getLatitude();

    }

    private void initView() {
        if (systemConfig.getPostId().equals("192") || systemConfig.getPostId().equals("94")) {
            radioButton5.setVisibility(View.VISIBLE);
            radioButton2.setVisibility(View.GONE);
            Post = "guideDriver";
        } else if (systemConfig.getPostId().equals("168") || systemConfig.getPostId().equals("175")) {
            radioButton5.setVisibility(View.GONE);
            radioButton2.setVisibility(View.VISIBLE);
            Post = "trainMan";
        } else if (systemConfig.getPostId().equals("130") || systemConfig.getPostId().equals("134")) {
            radioButton5.setVisibility(View.VISIBLE);
            radioButton2.setVisibility(View.VISIBLE);
            Post = "leader";
        } else if (systemConfig.getPostId().equals("") || systemConfig.getPostId() == null) {
            radioButton5.setVisibility(View.GONE);
            radioButton2.setVisibility(View.VISIBLE);
            Post = "trainMan";
        }
    }

    public void initFragments() {
        manager = getSupportFragmentManager();
        transaction = manager.beginTransaction();

        if (Post.equals("guideDriver")) {
            homePage2 = new HomePage2();
            driverData = new DriverData();
            vocationalStudy = new VocationalStudy();
            dailyWork = new DailyWork();
            trainSchedule = new TrainSchedule();
            systemTools = new SystemTools();

            transaction.add(R.id.frg_conn, homePage2);
            transaction.add(R.id.frg_conn, driverData);
            transaction.add(R.id.frg_conn, vocationalStudy);
            transaction.add(R.id.frg_conn, dailyWork);
            transaction.add(R.id.frg_conn, trainSchedule, "TrainSchedule");
            transaction.add(R.id.frg_conn, systemTools);

            transaction.show(homePage2);
            transaction.hide(driverData);
            transaction.hide(vocationalStudy);
            transaction.hide(dailyWork);
            transaction.hide(trainSchedule);
            transaction.hide(systemTools);
            transaction.commit();
        } else if (Post.equals("trainMan")) {
            homePage = new HomePage();
            driverNote = new DriverNote();
            driverData = new DriverData();
            vocationalStudy = new VocationalStudy();
            trainSchedule = new TrainSchedule();
            systemTools = new SystemTools();

            transaction.add(R.id.frg_conn, homePage);
            transaction.add(R.id.frg_conn, driverNote, "DriverNote");
            transaction.add(R.id.frg_conn, driverData);
            transaction.add(R.id.frg_conn, vocationalStudy);
            transaction.add(R.id.frg_conn, trainSchedule, "TrainScheduleTwo");
            transaction.add(R.id.frg_conn, systemTools);

            transaction.show(homePage);
            transaction.hide(driverNote);
            transaction.hide(driverData);
            transaction.hide(vocationalStudy);
            transaction.hide(trainSchedule);
            transaction.hide(systemTools);
            transaction.commit();
        } else if (Post.equals("leader")) {
            homePage = new HomePage();
            driverNote = new DriverNote();
            driverData = new DriverData();
            vocationalStudy = new VocationalStudy();
            dailyWork = new DailyWork();
            trainSchedule = new TrainSchedule();
            systemTools = new SystemTools();

            transaction.add(R.id.frg_conn, homePage);
            transaction.add(R.id.frg_conn, driverNote, "DriverNote");
            transaction.add(R.id.frg_conn, driverData);
            transaction.add(R.id.frg_conn, vocationalStudy);
            transaction.add(R.id.frg_conn, dailyWork);
            transaction.add(R.id.frg_conn, trainSchedule, "TrainSchedule");
            transaction.add(R.id.frg_conn, systemTools);

            transaction.show(homePage);
            transaction.hide(driverNote);
            transaction.hide(driverData);
            transaction.hide(vocationalStudy);
            transaction.hide(dailyWork);
            transaction.hide(trainSchedule);
            transaction.hide(systemTools);
            transaction.commit();
        }
    }

    public void setradiogroup() {
        rg = (RadioGroup) findViewById(R.id.list_menu);
        if (Post.equals("guideDriver")) {
            guideDriver();
        } else if (Post.equals("trainMan")) {
            trainMan();
        } else if (Post.equals("leader")) {
            leader();
        }
    }

    private void guideDriver() {
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                transaction = manager.beginTransaction();
                Fragment f = manager.findFragmentByTag(showTag);
                if (f != null) {
                    manager.beginTransaction().hide(f).commit();
                }
                switch (i) {
                    case R.id.radio1:
                        setGuideFragment(homePage2, driverData, vocationalStudy, dailyWork, trainSchedule, systemTools);
                        break;
                    case R.id.radio3:
                        setGuideFragment(driverData, homePage2, vocationalStudy, dailyWork, trainSchedule, systemTools);
                        break;
                    case R.id.radio4:
                        setGuideFragment(vocationalStudy, driverData, homePage2, dailyWork, trainSchedule, systemTools);
                        break;
                    case R.id.radio5:
                        setGuideFragment(dailyWork, driverData, vocationalStudy, homePage2, trainSchedule, systemTools);
                        break;
                    case R.id.radio6:
                        setGuideFragment(trainSchedule, driverData, vocationalStudy, dailyWork, homePage2, systemTools);
                        break;
                    case R.id.radio7:
                        setGuideFragment(systemTools, driverData, vocationalStudy, dailyWork, trainSchedule, homePage2);
                        break;
                }
                transaction.commit();
            }
        });
    }

    public void trainMan() {
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                transaction = manager.beginTransaction();
                Fragment f = manager.findFragmentByTag(showTag);
                if (f != null) {
                    switch (i) {
                        case R.id.radio1:
                            setTrainFragment(homePage, f, driverData, vocationalStudy, trainSchedule, systemTools);
                            break;
                        case R.id.radio2:
                            setTrainFragment(f, homePage, driverData, vocationalStudy, trainSchedule, systemTools);
                            break;
                        case R.id.radio3:
                            setTrainFragment(driverData, homePage, f, vocationalStudy, trainSchedule, systemTools);
                            break;
                        case R.id.radio4:
                            setTrainFragment(vocationalStudy, f, driverData, homePage, trainSchedule, systemTools);
                            break;
                        case R.id.radio6:
                            setTrainFragment(trainSchedule, f, driverData, vocationalStudy, homePage, systemTools);
                            break;
                        case R.id.radio7:
                            setTrainFragment(systemTools, f, driverData, vocationalStudy, trainSchedule, homePage);
                            break;
                    }
                } else {
                    switch (i) {
                        case R.id.radio1:
                            setTrainFragment(homePage, driverNote, driverData, vocationalStudy, trainSchedule, systemTools);
                            break;
                        case R.id.radio2:
                            setTrainFragment(driverNote, homePage, driverData, vocationalStudy, trainSchedule, systemTools);
                            break;
                        case R.id.radio3:
                            setTrainFragment(driverData, homePage, driverNote, vocationalStudy, trainSchedule, systemTools);
                            break;
                        case R.id.radio4:
                            setTrainFragment(vocationalStudy, driverNote, driverData, homePage, trainSchedule, systemTools);
                            break;
                        case R.id.radio6:
                            setTrainFragment(trainSchedule, driverNote, driverData, vocationalStudy, homePage, systemTools);
                            break;
                        case R.id.radio7:
                            setTrainFragment(systemTools, driverNote, driverData, vocationalStudy, trainSchedule, homePage);
                            break;
                    }
                }
                transaction.commit();
            }
        });
    }

    private void leader() {
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                transaction = manager.beginTransaction();
                Fragment f = manager.findFragmentByTag(showTag);
                if (f != null) {
                    switch (i) {
                        case R.id.radio1:
                            setLeaderFragment(homePage, f, driverData, vocationalStudy, dailyWork, trainSchedule, systemTools);
                            break;
                        case R.id.radio2:
                            setLeaderFragment(f, homePage, driverData, vocationalStudy, dailyWork, trainSchedule, systemTools);
                            break;
                        case R.id.radio3:
                            setLeaderFragment(driverData, homePage, f, vocationalStudy, dailyWork, trainSchedule, systemTools);
                            break;
                        case R.id.radio4:
                            setLeaderFragment(vocationalStudy, f, driverData, homePage, dailyWork, trainSchedule, systemTools);
                            break;
                        case R.id.radio5:
                            setLeaderFragment(dailyWork, f, driverData, vocationalStudy, homePage, trainSchedule, systemTools);
                            break;
                        case R.id.radio6:
                            setLeaderFragment(trainSchedule, f, driverData, vocationalStudy, dailyWork, homePage, systemTools);
                            break;
                        case R.id.radio7:
                            setLeaderFragment(systemTools, f, driverData, vocationalStudy, dailyWork, trainSchedule, homePage);
                            break;
                    }
                } else {
                    switch (i) {
                        case R.id.radio1:
                            setLeaderFragment(homePage, driverNote, driverData, vocationalStudy, dailyWork, trainSchedule, systemTools);
                            break;
                        case R.id.radio2:
                            setLeaderFragment(driverNote, homePage, driverData, vocationalStudy, dailyWork, trainSchedule, systemTools);
                            break;
                        case R.id.radio3:
                            setLeaderFragment(driverData, homePage, driverNote, vocationalStudy, dailyWork, trainSchedule, systemTools);
                            break;
                        case R.id.radio4:
                            setLeaderFragment(vocationalStudy, driverNote, driverData, homePage, dailyWork, trainSchedule, systemTools);
                            break;
                        case R.id.radio5:
                            setLeaderFragment(dailyWork, driverNote, driverData, vocationalStudy, homePage, trainSchedule, systemTools);
                            break;
                        case R.id.radio6:
                            setLeaderFragment(trainSchedule, driverNote, driverData, vocationalStudy, dailyWork, homePage, systemTools);
                            break;
                        case R.id.radio7:
                            setLeaderFragment(systemTools, driverNote, driverData, vocationalStudy, dailyWork, trainSchedule, homePage);
                            break;
                    }
                }
                transaction.commit();
            }
        });
    }

    public void changgeFragment(String hindtag, Fragment f, String showtag) {
        this.showTag = showtag;
        manager = getSupportFragmentManager();
        transaction = manager.beginTransaction();
        transaction.hide(manager.findFragmentByTag(hindtag));
        transaction.add(R.id.frg_conn, f, showtag);
        transaction.commit();
    }

    private void setGuideFragment(Fragment f1, Fragment f3, Fragment f4, Fragment f5, Fragment f6, Fragment f7) {
        transaction.show(f1);
        transaction.hide(f3);
        transaction.hide(f4);
        transaction.hide(f5);
        transaction.hide(f6);
        transaction.hide(f7);
    }

    private void setLeaderFragment(Fragment f1, Fragment f2, Fragment f3, Fragment f4, Fragment f5, Fragment f6, Fragment f7) {
        transaction.show(f1);
        transaction.hide(f2);
        transaction.hide(f3);
        transaction.hide(f4);
        transaction.hide(f5);
        transaction.hide(f6);
        transaction.hide(f7);
    }


    private void setTrainFragment(Fragment f1, Fragment f2, Fragment f3, Fragment f4, Fragment f5, Fragment f6) {
        transaction.show(f1);
        transaction.hide(f2);
        transaction.hide(f3);
        transaction.hide(f4);
        transaction.hide(f5);
        transaction.hide(f6);
    }

    private String showTag = "";

    public void setShowTag(String showTag) {
        this.showTag = showTag;
    }

    public void rgSetChecked() {
        ((RadioButton) rg.getChildAt(3)).setChecked(true);
    }

    public void ableRadioGroup() {
        for (int i = 0; i < rg.getChildCount(); i++) {
            ((RadioButton) rg.getChildAt(i)).setEnabled(true);
        }
        ((RadioButton) rg.getChildAt(3)).setChecked(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (HttpUtils.isNetWorkConn(this)) {
//            registerQueue = NoHttp.newRequestQueue();
            if (systemConfig.isFirstUse()) {
                systemConfig.setFirstUse(false);
                UtilisClass.showToast(this, "第一次使用...");
//
                timer = new Timer();
//                String str = downloadInfor();
//                AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                builder.setTitle("提示");
//                builder.setMessage("即将在后台为您开始下载文件..." + str);
//                builder.setPositiveButton("开始下载", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int Which) {
//                        systemConfig.setFirstUse(false);
//                        dialog.dismiss();
//                        upDatadialog = UtilisClass.setprogressDialog(MainActivity.this, "file");
//                        upDatadialog.show();
////                        downloadFile();
//                    }
//                });
//                builder.setNegativeButton("暂不下载", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                });
//                builder.create().show();

                openTimer();
            }
        } else {
            UtilisClass.showToast(this, "WIFI未连接");
        }
    }

    private void openTimer() {
        int time = 2;
        if (totalSize > (1024 * 1024 * 3)) {
            time = 6;
        } else if ((totalSize < (1024 * 1024 * 3)) && (totalSize > (1024 * 1024))) {
            time = 5;
        } else if ((totalSize > (1024 * 500)) && (totalSize < 1024 * 1024)) {
            time = 4;
        }

        timer.schedule(new TimerTask() {
            public void run() {
                addLocalPath();
                upAppOperateLog();
                upWifiRecord();
                this.cancel();
            }
        }, 1000 * 60 * time);

    }

//    private void downloadFile() {
//
//        List<Map> fileList2 = dbOpenHelper.queryListMap("select * from Announcement where AnnounceType=? and IsLoaded=?", new String[]{"1", "0"});
//        if (fileList2.size() != 0) {
//            fileType = "Announcement";
//            downloadAndSave(fileType, fileList2);
//        }
//        Log.i("tagg", "--------->" + "MainActivity 开始执行downloadFile()函数");
//
//        List<Map> fileList = dbOpenHelper.queryListMap("select * from TraficFiles where IsDelete=? and IsLoaded=?", new String[]{"0", "false"});
//        if (fileList.size() != 0) {
//            fileType = "TraficFiles";
//            downloadAndSave(fileType, fileList);
//        }
//
//        List<Map> stationFiles = dbOpenHelper.queryListMap("select * from StationFiles where IsDelete=? and IsLoaded=?", new String[]{"false", "false"});
//        if (stationFiles.size() != 0) {
//            fileType = "StationFiles";
//            downloadAndSave(fileType, stationFiles);
//        }
//        if (stationFiles.size() == 0 && fileList2.size() == 0 && fileList.size() == 0) {
//            systemConfig.setFirstSync(false);
//            UtilisClass.showToast(MainActivity.this, "服务器端暂无文件资料！");
//        } else {
//            if (upDatadialog != null) {
//                upDatadialog.dismiss();
//            }
//        }
//    }
//
//    private String downloadInfor() {
//        List<Map> fileList = dbOpenHelper.queryListMap("select * from TraficFiles where IsDelete=? and IsLoaded=?", new String[]{"0", "false"});
//        int size = 0;
//        if (fileList.size() > 0) {
//            for (int i = 0; i < fileList.size(); i++) {
//                String str = fileList.get(i).get("FileSize").toString();
//                if (!str.isEmpty()) {
//                    size = size + Integer.parseInt(str);
//                }
//            }
//        }
//        totalSize = size;
//        return fileList.size() + "个文件" + ",大约" + size + "Kb";
//    }

//    private void downloadAndSave(final String fileType, final List<Map> fileinfor) {
//
//        for (int j = 0; j < fileinfor.size(); j++) {
//            final int num = j;
//            final Map filemap = fileinfor.get(j);
//            String filepath = filemap.get("FilePath") + "";
//            String url = systemConfig.getHost() + filepath;
//            final String fileName = filemap.get("FilePath").toString().split("/")[4];
//
//            downloadRequest = NoHttp.createDownloadRequest(url,
//                    SDCardHelper.fileSdkPath(NetUtils.TRAFFICDATA),
//                    fileName,
//                    true,
//                    false);
//
//            String loachpath = SDCardHelper.fileSdkPath(NetUtils.TRAFFICDATA) + File.separator + fileName;
//            Log.i("文件本地路径", "----->" + loachpath);
//
//            recordOperateLog(4, "资料更新：" + fileName);
//
//            if (fileType.equals("TraficFiles")) {
//                if (!filemap.get("FileExtension").equals(".zip")) {
//                    dbOpenHelper.update("TraficFiles", new String[]{"LocaPath", "IsLoaded"},
//                            new Object[]{loachpath, "true"}, new String[]{"Id"}, new String[]{filemap.get("Id") + ""});
//                }
//            } else if (fileType.equals("Announcement")) {
//                dbOpenHelper.update("Announcement", new String[]{"LocaPath", "IsLoaded"},
//                        new Object[]{loachpath, "true"}, new String[]{"Id"}, new String[]{filemap.get("Id") + ""});
//
//            } else if (fileType.equals("StationFiles")) {
//                if (!filemap.get("FileExtension").equals(".zip")) {
//                    dbOpenHelper.update("StationFiles", new String[]{"LocaPath", "IsLoaded"},
//                            new Object[]{loachpath, "true"}, new String[]{"Id"}, new String[]{filemap.get("Id") + ""});
//                }
//            }
//
//            Myapplilcation.downloadQueue.add(0, downloadRequest, new DownloadListener() {
//                @Override
//                public void onDownloadError(int i, Exception e) {
//                    Message messagefirst = new Message();
//                    messagefirst.arg1 = 3;
//                    handlerfirst.sendMessage(messagefirst);
//                    Log.i("文件下载", "--->异常");
//                }
//
//                @Override
//                public void onStart(int i, boolean b, long l, Headers headers, long l1) {
//                }
//
//                @Override
//                public void onProgress(int i, int i1, long l, long l1) {
//                    Log.e("文件下载进度", "----->" + i + "====>" + i1 + "---->" + l + "====>" + l1);
//                }
//
//                @Override
//                public void onFinish(int i, String s) {
//                    if (num == fileinfor.size() - 1) {
//                        Message messagefirst = new Message();
//                        messagefirst.arg1 = 1;
//                        handlerfirst.sendMessage(messagefirst);
//                        Log.i("文件下载", "--->完成提示" + fileName);
//                    }
//                }
//
//                @Override
//                public void onCancel(int what) {
//                    Message messagefirst = new Message();
//                    messagefirst.arg1 = 3;
//                    handlerfirst.sendMessage(messagefirst);
//                    Log.i("文件下载", "--->下载取消");
//                }
//            });
//        }
//    }

    private void addLocalPath() {
        List<Map> fileList2 = dbOpenHelper.queryListMap("select * from Announcement where AnnounceType=1 and LocaPath is null", new String[]{"1", "null"});
        if (fileList2.size() != 0) {
            fileType = "Announcement";
            fileIsExist(fileType, fileList2);
        }

        List<Map> fileList = dbOpenHelper.queryListMap("select * from TraficFiles where IsDelete=0 and LocaPath is null", null);
        if (fileList.size() != 0) {
            fileType = "TraficFiles";
            fileIsExist(fileType, fileList);
        }

        List<Map> stationFiles = dbOpenHelper.queryListMap("select * from StationFiles where IsDelete= 'false' and LocaPath is null", null);
        if (stationFiles.size() != 0) {
            fileType = "StationFiles";
            fileIsExist(fileType, stationFiles);
        }
    }

    private void fileIsExist(String filetype, List<Map> list) {
        for (int i = 0; i < list.size(); i++) {
            Map filemap = list.get(i);
            //         /Upload/DriveFiles/2017-10-25/1627be7e-e2a9-4598-a03d-829ec5dc006c.pdf
            String fileName = filemap.get("FilePath").toString().split("/")[4];
            String loachpath = SDCardHelper.fileSdkPath(NetUtils.TRAFFICDATA) + File.separator + fileName;
            File file = new File(loachpath);
            if (file.exists()) {
                //final String fileSavePath = UtilisClass.getuuid();//解压文件保存路径
                final String fileSavePath = fileName;//解压文件保存路径
                //下载行车资料
                if (filetype.equals("TraficFiles")) {
                    if (filemap.get("FileExtension").equals(".zip")) {
                        try {
                            String unzipfilepath = loachpath + fileSavePath;
                            UtilisClass.unZipFile(loachpath, unzipfilepath);
                            String filetestpath = UtilisClass.getFileDir(unzipfilepath);
                            dbOpenHelper.update("TraficFiles", new String[]{"LocaPath", "IsLoaded"},
                                    new Object[]{filetestpath, "true"}, new String[]{"Id"}, new String[]{filemap.get("Id") + ""});
                            Log.i("添加文件本地路径zip", "---->" + loachpath);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.i("添加文件本地路径非zip", "---->" + loachpath);
                        dbOpenHelper.update("TraficFiles", new String[]{"LocaPath", "IsLoaded"},
                                new Object[]{loachpath, "true"}, new String[]{"Id"}, new String[]{filemap.get("Id") + ""});
                    }
                } else if (filetype.equals("Announcement")) {
                    dbOpenHelper.update("Announcement", new String[]{"LocaPath", "IsLoaded"},
                            new Object[]{loachpath, "true"}, new String[]{"Id"}, new String[]{filemap.get("Id") + ""});

                } else if (filetype.equals("StationFiles")) {
                    if (filemap.get("FileExtension").equals(".zip")) {
                        //获得压缩文件
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
            }
        }
    }

    public String getDate(String str) {
        String pattern = "[1-9]\\d*";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(str);
        String result = null;
        if (m.find()) {
            result = m.group();
        }
        return result;
    }

    private String getMd5(String string) {
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

    public void updateApk() {
        if (HttpUtils.isNetWorkConn(this)) {
            if (registerQueue == null) {
                registerQueue = NoHttp.newRequestQueue();
            }
            String url = systemConfig.getHost() + NetUtils.APPUPGRADE;
            Request<String> stringPostRequest = NoHttp.createStringRequest(url, RequestMethod.POST);
            stringPostRequest.add("signature", "bcad117ce31ac75fcfa347acefc8d198");
            String str = systemConfig.getVersionCode();
            stringPostRequest.add("version", systemConfig.getVersionCode());

            registerQueue.add(3, stringPostRequest, new OnResponseListener<String>() {
                @Override
                public void onStart(int what) {
                }

                @Override
                public void onSucceed(int what, final Response<String> response) {
                    Myapplilcation.getExecutorService().execute(new Runnable() {
                        @Override
                        public void run() {
                            writeApkData(response.get().toString());
                        }
                    });
                }

                @Override
                public void onFailed(int what, Response<String> response) {
                    UtilisClass.showToast(MainActivity.this, "APK获取数据失败！");
                }

                @Override
                public void onFinish(int what) {
                }
            });
        } else {
            UtilisClass.showToast(this, "WIFI未连接,Apk无法更新！");
        }
    }

    private void writeApkData(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            if (jsonObject.get("code").equals(122) || jsonObject.get("code").equals("122")) {
                Message message = new Message();
                message.arg1 = 7;
                handlerfirst.sendMessage(message);
            } else {
                String jsonArray = jsonObject.getString("data");
                appMap = Utils.StringListUtils.getApkMap(jsonArray);
                List<Map> appList = dbOpenHelper.queryListMap("select * from AppUpdate where Id =?", new String[]{appMap.get("Id").toString()});
                if (appList.size() != 1) {
                    systemConfig.setVersionCode(appMap.get("Version").toString());
                    dbOpenHelper.insert("AppUpdate", appMap);
                    List<Map> appList2 = dbOpenHelper.queryListMap("select * from AppUpdate order by id desc limit 0,1", null);
                    String addTime = appList2.get(0).get("AddTime").toString();
                    addTime = getDate(addTime);
                    long lt = new Long(addTime);
                    SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmss");
                    addTime = fmt.format(lt);
                    String str = getMd5(NetUtils.APPVERIFY + addTime);
                    if (str.equals(appList2.get(0).get("Token").toString())) {
                        UtilisClass.showToast(this, "即将为您开始下载APK更新软件系统！");
                        downloadApk(appList2, addTime);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void downloadApk(List<Map> appList, String addtime) {
        for (int i = 0; i < appList.size(); i++) {
            final Map filemap = appList.get(i);
            String filepath = filemap.get("Url") + "";
            String url = systemConfig.getHost() + filepath;
            final String appname = "机务运用管控系统" + addtime + ".apk";
            recordOperateLog(3, "APK更新版本号：" + filemap.get("Version"));

            downloadRequest = NoHttp.createDownloadRequest(url,
                    SDCardHelper.fileSdkPath(NetUtils.APKPATH),
                    appname,
                    true,
                    false);

            Myapplilcation.downloadQueue.add(4, downloadRequest, new DownloadListener() {
                @Override
                public void onDownloadError(int i, Exception e) {
                    Log.i("下载文件异常", "----->" + e.toString());
                }

                @Override
                public void onStart(int i, boolean b, long l, Headers headers, long l1) {
                }

                @Override
                public void onProgress(int i, int i1, long l, long l1) {
                }

                @Override
                public void onFinish(int i, String s) {
                    installApk(SDCardHelper.fileSdkPath(NetUtils.APKPATH) + "/" + appname);
                }

                @Override
                public void onCancel(int what) {
                }
            });
        }
    }

    private void installApk(String apkname) {
        Uri uri = Uri.fromFile(new File(apkname));
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    private void recordOperateLog(int LogType, String LogContent) {
        Date dt = new Date();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String nowTime = df.format(dt);
        dbOpenHelper.insert("AppOperateLog", new String[]{"LogType", "LogContent", "OperatorId", "DeviceId", "AddTime"},
                new Object[]{LogType, LogContent, systemConfig.getOperatorId(), 0, nowTime});
    }

    public void upAppOperateLog() {
        List<Map> device = dbOpenHelper.queryListMap("select * from MobileDevice", null);
        if (device.size() > 0) {
            KJHttp kjhttp = new KJHttp();
            HttpParams params = new HttpParams();
            params.put("signature", "bcad117ce31ac75fcfa347acefc8d198");
            String devicejson = NetUtils.getjson(device).toString();
            String operatejson = null;

            final List<Map> logLists = dbOpenHelper.queryListMap("select * from AppOperateLog where IsUploaded=?", new String[]{"0"});
            if (logLists.size() != 0) {
                operatejson = NetUtils.getjsonlist(logLists).toString();
                try {
                    devicejson = URLEncoder.encode(devicejson, "UTF-8");
                    operatejson = URLEncoder.encode(operatejson, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                params.put("data", operatejson);
                params.put("device", devicejson);
                kjhttp.post(systemConfig.getHost() + "/App/OperateLog", params, new HttpCallBack() {
                    @Override
                    public void onSuccess(String t) {
                        super.onSuccess(t);
                        try {

                            JSONObject object = new JSONObject(t);
                            if (object.get("code").equals(100)) {
                                for (int i = 0; i < logLists.size(); i++) {
                                    String id = logLists.get(i).get("Id") + "";
                                    dbOpenHelper.update("AppOperateLog", new String[]{"IsUploaded"}, new Object[]{1}, new String[]{"Id"}, new String[]{id});
                                }
                            }
                        } catch (Exception e) {
                            Log.e("上传App操作记录", "数据库上传状态更改异常");
                        }
                    }

                    @Override
                    public void onFailure(int errorNo, String strMsg) {
                        super.onFailure(errorNo, strMsg);
                        Toast.makeText(MainActivity.this, "上传失败" + errorNo + ":" + strMsg, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    public void upWifiRecord() {
        List<Map> device = dbOpenHelper.queryListMap("select * from MobileDevice", null);
        if (device.size() > 0) {
            KJHttp kjhttp = new KJHttp();
            HttpParams params = new HttpParams();
            params.put("signature", "bcad117ce31ac75fcfa347acefc8d198");
            String devicejson = NetUtils.getjson(device).toString();
            String operatejson = null;
            final List<Map> wifiLists = dbOpenHelper.queryListMap("select * from InstructorWifiRecord where IsUploaded=?", new String[]{"false"});
            if (wifiLists.size() != 0) {
                operatejson = NetUtils.getjsonlist(wifiLists).toString();
                try {
                    devicejson = URLEncoder.encode(devicejson, "UTF-8");
                    operatejson = URLEncoder.encode(operatejson, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                params.put("data", operatejson);
                params.put("device", devicejson);
                kjhttp.post(systemConfig.getHost() + "/App/WifiRecord", params, new HttpCallBack() {
                    @Override
                    public void onSuccess(String t) {
                        super.onSuccess(t);
                        try {
                            JSONObject object = new JSONObject(t);
                            if (object.get("code").equals(100)) {
                                for (int i = 0; i < wifiLists.size(); i++) {
                                    String id = wifiLists.get(i).get("Id") + "";
                                    dbOpenHelper.update("InstructorWifiRecord", new String[]{"IsUploaded"}, new Object[]{true}, new String[]{"Id"}, new String[]{id});
                                }

                            }
                        } catch (Exception e) {
                            Log.e("上传WiFi连接记录", "数据库上传状态更改异常");
                        }
                    }

                    @Override
                    public void onFailure(int errorNo, String strMsg) {
                        super.onFailure(errorNo, strMsg);
                        Toast.makeText(MainActivity.this, "上传失败" + errorNo + ":" + strMsg, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK)
            return true;
        if (keyCode == event.KEYCODE_HOME) {
            return true;
        }
        if (keyCode == event.KEYCODE_APP_SWITCH) {
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }

    @Override
    public void onBackPressed() {
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
        super.onDestroy();
        if (registerQueue != null) {
            registerQueue.cancelAll();
            registerQueue.stop();
        }
        if (downloadRequest != null) {
            downloadRequest.removeAll();
        }
    }

    @Override
    public void onClick(View v) {
    }
}