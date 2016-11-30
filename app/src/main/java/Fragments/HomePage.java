package Fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import DBUtils.DBOpenHelper;
import Utils.CommonAdapter;
import Utils.HttpUtils;
import Utils.NetUtils;
import Utils.SDCardHelper;
import Utils.ViewHolder;
import WPSutils.Wpsutils;
import zj.com.mc.Myapplilcation;
import zj.com.mc.NoticeDetails;
import zj.com.mc.R;
import zj.com.mc.UtilisClass;


/**
 * Created by dell on 2016/7/29.
 */
public class HomePage extends Fragment implements View.OnClickListener {
    //首页乘务员

    private DBOpenHelper dbOpenHelper;
    private ViewPager traindriver_viewpager;
    private RadioGroup traindrivergroup;
    private ImageView wifiicon,driverPhtot;
    private TextView driver_wifiname,driverworkname,drivername,driverdepartment,driverpostname,uploaded_time1;
    private TextView driver_linked;
    private WifiManager wifiManager;
    private TextView driver_loaded,driver_uploaded;
    private String personID;
    private static int WHAT = 0;
    private List<Map> driverInfolist;
    private List<View> pagerlist;
    private int[] bmList;
    private ListView driver_listview;
    private List<Map> newtoastlsit;
    private ProgressDialog upDatadialog;
    private int uploadid;
    private String loadedpath;
    private List<Map<String, Object>> mapList;
    private List<String> promptlist;
    private WifiReceiver2 wifiReceiver2;



    Handler homeHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            if (msg.what==3) {
                traindriver_viewpager.setCurrentItem(traindriver_viewpager.getCurrentItem()+1);
                homeHandler.sendEmptyMessageDelayed(3, 5000);
            }
            if (msg.arg1==5){
//                UtilisClass.showToast(getActivity(), "数据更新完成！");

                String uploadedtime=UtilisClass.getStringDate1();
                uploaded_time1.setText("上次更新:"+uploadedtime);
                upDatadialog.cancel();

                String toaststring= (String) msg.obj;
                TextView tv=new TextView(getActivity());
                tv.setPadding(40,20,0,0);
                if (!toaststring.equals("")) {
                    tv.setText(toaststring + "数据有更新！");
                    new AlertDialog.Builder(getActivity())
                            .setTitle("提示信息")
                            .setView(tv)
                            .setNegativeButton("确定",null)
                            .show();
                }else {
                    UtilisClass.showToast(getActivity(), "数据更新完成！");
                }
            }
            if (msg.arg1 == 2) {
                UtilisClass.showToast(getActivity(), "数据上传完成！");
                upDatadialog.cancel();
            }

        }
    };

    @Override
    public void onResume() {
        super.onResume();

        initwifishow();//设置wifi
        setphotoImage();

    }

//向服务器上传数据
    private void updataarguments() {

        String[] tablename = NetUtils.uptablenames;

        System.out.println(tablename.toString());
        for (int i = 0; i < tablename.length; i++) {
            final String tableName = tablename[i];
            final List<Map> uplist = dbOpenHelper.queryListMap("select * from " + tableName + " where " +
                    "IsUploaded=?", new String[]{"1"});
            if (uplist.size() != 0) {
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
        homeHandler.sendMessage(message1);

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.homepage, container, false);

        initView(view);

        return view;
    }

    private void initView(View view) {
        promptlist=new ArrayList<>();
        bmList= new int[]{R.drawable.vp1, R.drawable.vp2, R.drawable.vp3,R.drawable.vp1, R.drawable.vp2, R.drawable.vp3};
        dbOpenHelper= DBOpenHelper.getInstance(getActivity().getApplicationContext());
        personID=getActivity().getSharedPreferences("PersonInfo",Context.MODE_PRIVATE).getString("PersonId",null);//登录人id
        driverInfolist=dbOpenHelper.queryListMap("select * from ViewPersonInfo where Id=?",new String[]{personID});
        wifiicon= (ImageView) view.findViewById(R.id.driver_wifiicon);//wifi图标
        driver_wifiname= (TextView) view.findViewById(R.id.driver_wifiname);//当前wifi  ssd
        driver_loaded= (TextView) view.findViewById(R.id.driver_loaded);//下载
        driver_loaded.setOnClickListener(this);
        driver_uploaded= (TextView) view.findViewById(R.id.driver_uploaded);//上传
        driver_uploaded.setOnClickListener(this);
        driver_listview= (ListView) view.findViewById(R.id.driver_listview);

        driverPhtot= (ImageView) view.findViewById(R.id.driver_photo);//头像
        driver_linked= (TextView) view.findViewById(R.id.driver_linked);//wifi设置
        wifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
        driverworkname= (TextView) view.findViewById(R.id.driver_worknumber);//登录人工号
        drivername= (TextView) view.findViewById(R.id.driver_name);//登录人姓名
        driverdepartment= (TextView) view.findViewById(R.id.driver_department);//登录人部门
        driverpostname= (TextView) view.findViewById(R.id.driver_postname);//登录人岗位

        traindriver_viewpager= (ViewPager) view.findViewById(R.id.traindriver_viewpager);//首页轮播图
        traindrivergroup= (RadioGroup) view.findViewById(R.id.traindriver_radiogroup);//首页轮播，跟新记录 radiogroup
        mapList=new ArrayList<>();

        uploaded_time1= (TextView) view.findViewById(R.id.uploaded_time1);//数据更新时间

        setdialog();


        setphotoImage();//设置头像
        setTraindriverInfo();//设置driverinfo
        initwifishow();//设置wifi
        setViewpager();

        traindriver_viewpager.setCurrentItem(Integer.MAX_VALUE/2-Integer.MAX_VALUE/2%pagerlist.size());
        homeHandler.sendEmptyMessageDelayed(3, 3000);


        setTrainRadiogroup();
        setNewtoast();

        IntentFilter filter2 = new IntentFilter();
        filter2.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter2.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter2.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        wifiReceiver2=new WifiReceiver2();
        getActivity().registerReceiver(wifiReceiver2, filter2);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        getActivity().unregisterReceiver(wifiReceiver2);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.driver_linked:
                //wifi断开
                if (android.os.Build.VERSION.SDK_INT > 10) {
                    // 3.0以上打开设置界面，也可以直接用ACTION_WIRELESS_SETTINGS打开到wifi界面
                    startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
                } else {
                    startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                }
                break;

            case R.id.driver_loaded:
//下载更新

                uploadid = 0;
                setupdataloaded();
                break;
            case R.id.driver_uploaded:
//上传数据

                if (HttpUtils.isNetWorkConn(getActivity())) {
                    upDatadialog.show();
                    Myapplilcation.getExecutorService().execute(new Runnable() {
                        @Override
                        public void run() {

                            updataarguments();
                        }
                    });
                } else {
                    UtilisClass.showToast(getActivity(), "更新失败！，网络未联通");
                }


                break;

        }
    }

    private void setupdataloaded() {

        uploadid = 0;


        if (HttpUtils.isNetWorkConn(getActivity())) {
            upDatadialog.show();
            uploadid = 0;
            Myapplilcation.getExecutorService().execute(new Runnable() {
                @Override
                public void run() {
                    upData();
                }
            });
        } else {
            UtilisClass.showToast(getActivity(), "更新失败！，网络未联通");
        }


    }

    //设置radiogroup
    private void setTrainRadiogroup(){

        traindrivergroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                switch (i){
                    case R.id.traindriver_radio1:
                        driver_listview.setVisibility(View.GONE);
                        traindriver_viewpager.setVisibility(View.VISIBLE);
                        break;
                    case R.id.traindriver_radio2:
                        driver_listview.setVisibility(View.VISIBLE);
                        traindriver_viewpager.setVisibility(View.GONE);
                        break;


                }


            }
        });
    }


    //设置wifi显示状态
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
    //获取wifi名字
    private String getConnectWifiSsid() {
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        Log.d("wifiInfo", wifiInfo.toString());
        Log.d("SSID", wifiInfo.getSSID());
        return wifiInfo.getSSID();
    }
    //设置司机信息
    private void setTraindriverInfo(){

        String driverWorkNo="";
        String dirverName="";
        String driverDeparnmentName="";
        String driverPostName="";
        if (driverInfolist.size()!=0){
            driverWorkNo=driverInfolist.get(0).get("WorkNo")+"";
            dirverName=driverInfolist.get(0).get("Name")+"";
            driverDeparnmentName=driverInfolist.get(0).get("DepartmentName")+"";
            driverPostName=driverInfolist.get(0).get("PostName")+"";
        }
        driverworkname.setText(driverWorkNo);
        drivername.setText(dirverName);
        driverdepartment.setText(driverDeparnmentName);
        driverpostname.setText(driverPostName);

    }

    //图片请求
    private void setphotoImage() {
        if (driverInfolist.size() != 0) {
            String netPhotoPath = driverInfolist.get(0).get("PhotoPath") + "";
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
                        } else {

                        }
                    }

                    @Override
                    public void onFinish(int i) {
                    }
                });
            } else {
                List<Map> mapList1 = dbOpenHelper.queryListMap("select * from PersonInfo where Id=?", new String[]{personID});
                String locaphotopath = mapList1.get(0).get("LocaPhotoPath") + "";
                if (locaphotopath.length() != 0) {
                    Uri uri = Uri.parse(locaphotopath);
                    driverPhtot.setImageURI(uri);
                } else {

                }
            }
        }
    }

    //设置viewpager
    private void setViewpager(){

        pagerlist=new ArrayList<>();

        for (int i=0; i<bmList.length; i++){
            View view=new ImageView(getActivity());
            view.setBackground(getActivity().getResources().getDrawable(bmList[i]));
            pagerlist.add(view);
        }

        PagerAdapter pagerAdapter=new PagerAdapter() {
            @Override
            public int getCount() {
                return Integer.MAX_VALUE;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {

                return view==object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(pagerlist.get(position%pagerlist.size()));
                return pagerlist.get(position%pagerlist.size());

            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(pagerlist.get(position%pagerlist.size()));
            }
        };

        traindriver_viewpager.setAdapter(pagerAdapter);

    }

    //最新公告list view设置
    private void setNewtoast(){

        //设置最新公告
            newtoastlsit = dbOpenHelper.queryListMap("select * from Announcement", null);
        if (newtoastlsit.size()!=0){
            Collections.reverse(newtoastlsit);
        }

        driver_listview.setAdapter(new CommonAdapter<Map>(getActivity(),newtoastlsit,R.layout.fileshowlistview) {
            @Override
            protected void convertlistener(ViewHolder holder, Map map) {
            }

            @Override
            public void convert(ViewHolder holder, Map map) {
                holder.getView(R.id.fileshow_fileicn).setVisibility(View.GONE);
                String toastTitle=map.get("Title") + "";
                holder.setText(R.id.fileshow_listviewitem,toastTitle);
            }
        });

        driver_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String AnnounceType=newtoastlsit.get(i).get("AnnounceType")+"";

                if (AnnounceType.equals("2")) {

                    Intent intentLatestA = new Intent(getActivity(), NoticeDetails.class);
                    intentLatestA.putExtra("Id", newtoastlsit.get(i).get("Id") + "");
                    startActivity(intentLatestA);
                }else {
                    String locapath=newtoastlsit.get(i).get("LocaPath")+"";
                    Wpsutils.wpsOpenFile(locapath,getActivity());
                }
                dbOpenHelper.update("Announcement",new String[]{"IsRead"},new Object[]{1},
                        new String[]{"Id"},new String[]{newtoastlsit.get(i).get("Id")+""});

            }

//            }
        });
    }

    //progress 弹窗
    public void setdialog() {
        upDatadialog = UtilisClass.setprogressDialog(getActivity());
    }

    //线程执行的任务
    public void upData() {

        List<Map> lastIdlist = dbOpenHelper.queryListMap("select * from DbUpdateLog", null);
        if (lastIdlist.size() != 0) {
            uploadid = (int) lastIdlist.get(lastIdlist.size() - 1).get("Id");
        }
        loadedpath=NetUtils.Uploadeduri+ uploadid;



        String json = HttpUtils.getJsonContent(loadedpath);
        Log.i("tagg", json);
        try {
            JSONObject jsonObject = new JSONObject(json);
            if (jsonObject.get("code").equals(304)||jsonObject.get("code").equals("304")) {
                UtilisClass.showToast(getActivity(), "任务失败！");
            } else {
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

        Log.i("tablenames", String.valueOf(tableNames));
        if (tableNames.size() != 0) {
            promptlist.clear();
            for (int num = 0; num < tableNames.size(); num++) {
                String singletablename=tableNames.get(num);
                List<Map<String, Object>> singleTableData = NetUtils.getSingleTableNameList(mapList, "TableName", singletablename);//获取单个表集合
                List<String> ListId = NetUtils.getTableNamelist(singleTableData, "TargetId");

                Log.i("tablenames", String.valueOf(ListId));

                if (singletablename.equals("ExamNotify")){
                    promptlist.add("ExamNotify");
                }else if (singletablename.equals("Announcement")){
                    promptlist.add("Announcement");
                }else if (singletablename.equals("TraficFiles")){
                    promptlist.add("TraficFiles");
                }



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

                Log.i("tablenames", String.valueOf(deleteTableDatalist));

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

        List<Map> fileList = dbOpenHelper.queryListMap("select * from TraficFiles where IsDelete=? and IsLoaded=?", new String[]{"0","false"});

//        System.out.println("<<<<<<<<<<<<<>>>>>>>>>>>>"+fileList.toString());
        if (fileList.size() != 0) {
            try {
                getsearchfileSaved(1,fileList);
            } catch (UnsupportedEncodingException e) {

            }
        }

        List<Map> fileList2 = dbOpenHelper.queryListMap("select * from Announcement where AnnounceType=? and IsLoaded=?", new String[]{"1", "false"});

//        System.out.println("<<>><<>>"+fileList2.toString());

        if (fileList2.size() != 0) {
            try {
                getsearchfileSaved(2,fileList2);
            } catch (UnsupportedEncodingException e) {

            }

        }
        Message message = new Message();
        StringBuffer toast=new StringBuffer();
        if (promptlist.size()!=0) {
            for (int i=0; i<promptlist.size(); i++){
                String tablename=promptlist.get(i);
                if (tablename.equals("ExamNotify")){
                    toast.append("重要提示");
                }else if (tablename.equals("Announcement")){
                    toast.append("最新公告");
                }else if (tablename.equals("TraficFiles")){
                    toast.append("行车资料");
                }
                toast.append("、");
            }
            toast.deleteCharAt(toast.lastIndexOf("、"));
        }
        String stringprompt=toast.toString();
        message.obj=stringprompt;
        message.arg1 = 5;
        homeHandler.sendMessage(message);
    }


    //文件下载保存
    private void getsearchfileSaved(int a,List<Map> listitem) throws UnsupportedEncodingException {
        FileUtil fileUtil = new FileUtil();
        for (int i = 0; i < listitem.size(); i++) {
            String filepath = listitem.get(i).get("FilePath") + "";
            String loadfilepath = NetUtils.APPDOOR2 + filepath;//网络请求路径
            String fileName = filepath.split("/")[filepath.split("/").length - 1];
            String unzipfilename = listitem.get(i).get("FileName") + "";
//            unzipfilename=unzipfilename.replaceAll(" ","");
            unzipfilename = unzipfilename.substring(0, unzipfilename.length() - 4) + ".htm";

//            System.out.println("<<<<<<<<<<<<<<<<<" + unzipfilename);

//            System.out.println("???????????????" + fileName);
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
//                        System.out.println(">>>>>>>>>>>>>>>>>>>" + filetestpath);
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
//                System.out.println("1111111111111"+urlfilename);
                if (bytes != null && bytes.length != 0) {
                    SDCardHelper.saveFileToSDCardPrivateDir(bytes, null, fileName, getActivity());
                    String loachpath=getActivity().getExternalFilesDir(null).getPath() + "/" + fileName;
                    dbOpenHelper.update("Announcement", new String[]{"LocaPath", "IsLoaded"}, new Object[]{loachpath, "true"}, new String[]{"Id"}, new String[]{listitem.get(i).get("Id") + ""});
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


    private class WifiReceiver2 extends BroadcastReceiver {
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
                    String currentdate = "";
                    String routerid2="";

                    if (isConnected) {
//如果连接

                        driver_linked.setText("已连接");
                        driver_wifiname.setText(getConnectWifiSsid().substring(1, getConnectWifiSsid().length() - 1));
                        wifiicon.setImageResource(R.mipmap.i2_8);

                        WifiInfo mWifiInfo = wifiManager.getConnectionInfo();
                        String routerBssid=mWifiInfo.getBSSID();
                        String locamac=mWifiInfo.getMacAddress();

                        Log.e("H3c", "routerBssid" + routerBssid);
                        Log.e("H3c", "locamac" + locamac);

                        List<Map> routerlist=dbOpenHelper.queryListMap("select * from InstructorRouterPosition  where BssId like ?"
                        ,new String[]{"%"+routerBssid+"%"});

//                        System.out.println("???????????"+routerlist.toString()+"???");


                        if (routerlist.size()!=0) {
                            currentdate = UtilisClass.getStringDate();
                            String routerid=routerlist.get(0).get("Id")+"";
                            routerid2=routerid;


                            dbOpenHelper.insert("InstructorWifiRecord", new String[]{"InstructorId", "RouterPositionId", "ConnectTime","ConnectFlag"},
                                    new Object[]{personID,routerid, currentdate,"1"});



                            try {
                                Thread.currentThread().sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            setupdataloaded();
                        }else {
                        }
                    } else {
                        driver_linked.setText("已断开");
                        wifiicon.setImageResource(R.mipmap.i2_7);
                        driver_wifiname.setText("");
                            currentdate = UtilisClass.getStringDate();
                            dbOpenHelper.insert("InstructorWifiRecord", new String[]{"InstructorId", "RouterPositionId", "ConnectTime","ConnectFlag"},
                                    new Object[]{personID,routerid2, currentdate,"2"});
                    }
                }
            }

        }
    }


}
