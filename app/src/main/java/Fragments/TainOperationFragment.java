package Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import Adapter.AnnounceCommandsAdpter;
import Adapter.TainGrapAdapter;
import Adapter.TainmomentAdapter;
import DBUtils.DBOpenHelper;
import Dialog.TianGrapDialog;
import Utils.CustomDialog;
import Utils.NetUtils;
import Utils.SDCardHelper;
import WPSutils.Wpsutils;
import config.ISystemConfig;
import config.SystemConfigFactory;
import zj.com.mc.MainActivity;
import zj.com.mc.R;
import zj.com.mc.TainCommentActivity;
import zj.com.mc.TainGrapActivity;
import zj.com.mc.TainMomentActivity;
import zj.com.mc.UtilisClass;

/**
 * 行车操作过程详细记录
 */
public class TainOperationFragment extends Fragment implements View.OnClickListener {

    private TextView tv_tain_arrive, tv_title, tv_nextarrive, tv_thestation, tv_nextstation, tv_tain_go,
            tv_see, tv_change, tv_stationInfo, tv_cargrap, tv_stationAllInfo, tv_node, tv_tip, tv_img, tv_moretime, tv_carend;
    private MainActivity mActivity;
    private String lastId, line, code;
    private ListView lv_tainmoment, lv_AnnounceCommands;
    private String tainMomentsql, commandssql, tainInfosql;
    private DBOpenHelper dbOpenHelper;
    private TainmomentAdapter tainmomentAdapter;
    private AnnounceCommandsAdpter commandsAdpter;
    private LinearLayout ll_comment;
    private TianGrapDialog tianGrapDialog;
    private String currentSite;//为记事本传递站点名
    private boolean isok = false;//签点
    private List<Map> list;//签点信息Map
    private List<Map> commandslist;
    private Map TainNo;//车次信息

    //运行及编组信息
    private ListView lv_signpointinfor;
    private String signSql, TrainFormationSql, ViewTrainSql;
    private List<Map> signSqlList, TrainFormationList, ViewTrainmentList;
    private TainGrapAdapter tainGrapAdapter;
    private TextView tv_photograph;
    private TextView tv_video;
    private TextView tv_aideo;
    private String fileName;
    private String photopath;
    private String videopath;
    private String audiopath;
    private Intent intentsystem;
    private ISystemConfig systemConfig;
    private int startPoint;
    private boolean arriveClick = false, nextClick = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tainoperation, null);
        tainMomentsql = "select * from ViewTrainMoment where FullName=?";
        commandssql = "select * from ViewAnnounceCommands where FullName=?";
        tainInfosql = "select * from TrainNo where FullName=?";
        signSql = "select * from DriveSignPoint where DriveRecordId=?";
        TrainFormationSql = "select * from TrainFormation where DriveRecordId=?";
        ViewTrainSql = "select * from ViewTrainMoment where FullName=?";
        inintView(view);
        inintData();
        inintLinsner();
        photopath = NetUtils.PHOTOPATH;
        videopath = NetUtils.VIDEOPATH;
        audiopath = NetUtils.AUDIOPATH;

        if (systemConfig.isSystemTools()) {
            SDCardHelper.saveFileToSDCardCustomDirss(photopath);
            SDCardHelper.saveFileToSDCardCustomDirss(audiopath);
            SDCardHelper.saveFileToSDCardCustomDirss(videopath);
            systemConfig.setSystemTools(false);
        }
        return view;
    }

    private void inintView(View view) {

        tv_carend = (TextView) view.findViewById(R.id.tv_carend);//行车结束
        tv_title = (TextView) view.findViewById(R.id.tv_title);//出勤信息

        tv_nextarrive = (TextView) view.findViewById(R.id.tv_nextarrive);//即将到达
        tv_thestation = (TextView) view.findViewById(R.id.tv_thestation);//即将到达站名
        tv_nextstation = (TextView) view.findViewById(R.id.tv_nextstation);//下一站站名
        tv_tain_arrive = (TextView) view.findViewById(R.id.tv_tain_arrive);//到达签点
        tv_tain_go = (TextView) view.findViewById(R.id.tv_tain_go);//发车、通过签点
        tv_see = (TextView) view.findViewById(R.id.tv_see);//查看签点
        tv_change = (TextView) view.findViewById(R.id.tv_change);//更换车次

        tv_stationInfo = (TextView) view.findViewById(R.id.tv_stationInfo);//当前站点
        tv_cargrap = (TextView) view.findViewById(R.id.tv_cargrap);//列车编组/试风
        tv_stationAllInfo = (TextView) view.findViewById(R.id.tv_stationAllInfo);
        tv_node = (TextView) view.findViewById(R.id.tv_node);//记事
        tv_tip = (TextView) view.findViewById(R.id.tv_tip);
        tv_img = (TextView) view.findViewById(R.id.tv_img);
        tv_moretime = (TextView) view.findViewById(R.id.tv_moretime);//更多时刻
        ll_comment = (LinearLayout) view.findViewById(R.id.ll_comment);//揭示命令
        tv_photograph = (TextView) view.findViewById(R.id.tv_photograph);//行车拍照
        tv_video = (TextView) view.findViewById(R.id.tv_video);//行车录像
        tv_aideo = (TextView) view.findViewById(R.id.tv_aideo);//行车录像

        lv_AnnounceCommands = (ListView) view.findViewById(R.id.lv_AnnounceCommands);
        lv_tainmoment = (ListView) view.findViewById(R.id.lv_tainmoment);
        lv_signpointinfor = (ListView) view.findViewById(R.id.lv_signpointinfor);

        tv_tain_go.setOnClickListener(this);
        tv_see.setOnClickListener(this);
        tv_change.setOnClickListener(this);
        tv_cargrap.setOnClickListener(this);
        tv_stationAllInfo.setOnClickListener(this);
        tv_node.setOnClickListener(this);
        tv_tip.setOnClickListener(this);
        tv_img.setOnClickListener(this);
        tv_moretime.setOnClickListener(this);
        ll_comment.setOnClickListener(this);
        tv_photograph.setOnClickListener(this);
        tv_video.setOnClickListener(this);
        tv_aideo.setOnClickListener(this);
    }

    private void inintData() {
        mActivity = (MainActivity) getActivity();
        lastId = getArguments().getString("id");
        line = getArguments().getString("line");
        code = getArguments().getString("code");
        startPoint = getArguments().getInt("startPoint");

        tv_title.setText(code + "(" + line + ")");
        tv_tain_arrive.setOnClickListener(this);
        tv_carend.setOnClickListener(this);

        dbOpenHelper = DBOpenHelper.getInstance(mActivity);
        systemConfig = SystemConfigFactory.getInstance(getActivity()).getSystemConfig();
        list = dbOpenHelper.queryListMap(tainMomentsql, new String[]{code});
        commandslist = dbOpenHelper.queryListMap(commandssql, new String[]{code});
        tainmomentAdapter = new TainmomentAdapter(mActivity, list);
        commandsAdpter = new AnnounceCommandsAdpter(mActivity, commandslist);
        lv_tainmoment.setAdapter(tainmomentAdapter);
        lv_AnnounceCommands.setAdapter(commandsAdpter);

        tv_nextarrive.setText("始发地：");
        tv_tain_arrive.setBackgroundColor(getResources().getColor(R.color.llblack));

        if (list.size() > 0) {
            tv_thestation.setText(String.valueOf(list.get(startPoint).get("StationName")));//即将到达车站名
            tv_nextstation.setText(String.valueOf(list.get(startPoint+1).get("StationName")));//下一站车站名
            tv_stationInfo.setText(String.valueOf(list.get(startPoint).get("StationName")) + "站的：");//当前站信息
            currentSite = String.valueOf(list.get(startPoint).get("StationName")) + "站";//记事本传递站名
            //储存手账车次线路表
            TainNo = dbOpenHelper.queryItemMap(tainInfosql, new String[]{code});
            DriveTrainNoAndLine(dbOpenHelper, lastId, String.valueOf(TainNo.get("Id")), String.valueOf(list.get(startPoint).get("LineId")));
        }
        listChange(startPoint);
    }

    private void initSignpointinfor() {
        signSqlList = dbOpenHelper.queryListMap(signSql, new String[]{lastId});
        TrainFormationList = dbOpenHelper.queryListMap(TrainFormationSql, new String[]{lastId});
        ViewTrainmentList = dbOpenHelper.queryListMap(ViewTrainSql, new String[]{code});
//        Collections.reverse(signSqlList);
//        Collections.reverse(TrainFormationList);
//        Collections.reverse(ViewTrainmentList);
        tainGrapAdapter = new TainGrapAdapter(getActivity(), "TainOperationFragment", lastId, signSqlList, TrainFormationList, ViewTrainmentList);
        lv_signpointinfor.setAdapter(tainGrapAdapter);
    }


    private void inintLinsner() {
        lv_AnnounceCommands.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent1 = new Intent(mActivity, TainCommentActivity.class);
                intent1.putExtra("code", code);
                mActivity.startActivity(intent1);
            }
        });
    }

    @Override
    public void onClick(View v) {
        Date dt = new Date();//如果不需要格式,可直接用dt,dt就是当前系统时间
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置显示格式
        String nowTime = df.format(dt);
        intentsystem = new Intent();

        switch (v.getId()) {
            //到达签点
            case R.id.tv_tain_arrive:
                if (arriveClick) {
                    tv_nextarrive.setText("已经到达：");
                    tv_tain_go.setText("发车/通过签点");
                    if (startPoint < list.size() - 2) {
                        boolean isok = setTarinMomment();
                        if (isok) {
                            initSignpointinfor();
                        }
                        tv_thestation.setText(String.valueOf(list.get(startPoint).get("StationName")));
                        tv_stationInfo.setText(String.valueOf(list.get(startPoint).get("StationName")) + "站的：");
                        currentSite = String.valueOf(list.get(startPoint).get("StationName")) + "站";
                    } else if (startPoint >= list.size() - 2) {
                        tv_thestation.setText(String.valueOf(list.get(startPoint + 1).get("StationName")));
                        tv_stationInfo.setText(String.valueOf(list.get(startPoint + 1).get("StationName")) + "站的：");
                        currentSite = String.valueOf(list.get(startPoint).get("StationName")) + "站";
                        tv_nextarrive.setText("终点站：");
                        tv_nextstation.setText("");
                        nextClick = false;
                        tv_tain_go.setBackgroundColor(Color.parseColor("#33000000"));
                        boolean isok = setTarinMomment();
                        if (isok) {
                            initSignpointinfor();
                            showNext();
                        }
                    }
                    arriveClick = false;
                    tv_tain_arrive.setBackgroundColor(Color.parseColor("#33000000"));
                    SimpleDateFormat s = new SimpleDateFormat("HH:mm");
                    tv_tain_arrive.setText(s.format(new Date()));
                }
                break;

            //发车/通车签点
            case R.id.tv_tain_go:
                if (nextClick) {
                    tv_nextarrive.setText("即将到达：");
                    tv_tain_arrive.setText("到达签点");
                    arriveClick = true;
                    tv_tain_arrive.setBackground(getResources().getDrawable(R.drawable.buttonclickcolor));
                    if (startPoint < list.size() - 2) {    //list  ----> 列车时刻
                        final boolean ok = setTarinGoMomment();
                        //线程3秒后执行
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (ok) {
                                    startPoint++; // 1
                                    listChange(startPoint);//列车时刻
                                    initSignpointinfor();
                                    tv_thestation.setText(String.valueOf(list.get(startPoint).get("StationName")));//即将到达站名
                                    tv_stationInfo.setText(String.valueOf(list.get(startPoint).get("StationName")) + "站的：");//当前站点
                                    currentSite = String.valueOf(list.get(startPoint).get("StationName")) + "站";
                                    if (startPoint >= list.size() - 1) {
                                        if (setTarinGoMomment()) {
                                            tv_nextarrive.setText("终点站：");
                                            tv_nextstation.setText("");
                                            nextClick = false;
                                            tv_tain_go.setBackgroundColor(Color.parseColor("#33000000"));

                                        }
                                    } else {
                                        tv_nextstation.setText(String.valueOf(list.get(startPoint + 1).get("StationName")));//下一站站点
                                        nextClick = true;
                                        tv_tain_go.setText("发车/通过签点");
                                    }
                                }
                            }
                        }, 4000);

                    } else if (startPoint == list.size() - 2) {
                        if (setTarinGoMomment()) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    listChange(startPoint + 1);
                                    tv_thestation.setText(String.valueOf(list.get(startPoint + 1).get("StationName")));
                                    tv_stationInfo.setText(String.valueOf(list.get(startPoint + 1).get("StationName")) + "站的：");
                                    currentSite = String.valueOf(list.get(startPoint).get("StationName")) + "站";
                                    tv_nextstation.setText("");
                                    tv_nextarrive.setText("终点站：");
                                    nextClick = false;
                                    tv_tain_go.setBackgroundColor(Color.parseColor("#33000000"));//灰色

                                }
                            }, 4000);
                            initSignpointinfor();
                        }
                    }
                }
                break;

            //更多时刻
            case R.id.tv_moretime:
                Intent intent = new Intent(mActivity, TainMomentActivity.class);
                intent.putExtra("code", code);
                mActivity.startActivity(intent);
                break;
            //揭示命令
            case R.id.ll_comment:
                Intent intent1 = new Intent(mActivity, TainCommentActivity.class);
                intent1.putExtra("code", code);
                mActivity.startActivity(intent1);
                break;
            //列车编组
            case R.id.tv_cargrap:
                showDialog(mActivity, dbOpenHelper, lastId, String.valueOf(list.get(startPoint).get("TrainStationId")));
                break;
            //查看全部揭示命令
            case R.id.tv_see:
                Intent intent2 = new Intent(mActivity, TainGrapActivity.class);
                intent2.putExtra("id", lastId);
                intent2.putExtra("code", code);
                startActivity(intent2);

                break;
            case R.id.tv_node:
                writeNotes(currentSite, lastId, String.valueOf(list.get(startPoint).get("TrainStationId")));
                break;
            case R.id.tv_stationAllInfo:
                //站段明细
                List<Map> file = dbOpenHelper.queryListMap("select * from StationFiles where StationId=?", new String[]{String.valueOf(list.get(startPoint).get("StationId"))});
                if (file.size() > 0) {
                    for (int j = 0; j < file.size(); j++) {
                        String fileExtension = file.get(j).get("FileExtension") + "";//获取文件类型
                        fileExtension.toLowerCase();
                        if (fileExtension.equals(".doc") || fileExtension.equals(".ppt") || fileExtension.equals(".xls")) {
                            String filepath = file.get(j).get("LocaPath") + "";
                            if (filepath != null && !filepath.equals("")) {
                                Wpsutils.wpsOpenFile(filepath, mActivity);
                                break;
                            } else {
                                UtilisClass.showToast(mActivity, "未找到该文件！");
                            }
                        }
                    }
                } else {
                    UtilisClass.showToast(mActivity, "未找到该站段关于站段明细的相关文件！");
                }
                break;
            case R.id.tv_tip:
                //操作提示卡
                List<Map> file1 = dbOpenHelper.queryListMap("select * from StationFiles where StationId=?", new String[]{String.valueOf(list.get(startPoint).get("StationId"))});
                if (file1.size() > 0) {
                    for (int j = 0; j < file1.size(); j++) {
                        String fileExtension = file1.get(j).get("FileExtension") + "";//获取文件类型
                        fileExtension.toLowerCase();
                        if (fileExtension.equals(".doc") || fileExtension.equals(".ppt") || fileExtension.equals(".xls")) {
                            String filepath = file1.get(j).get("LocaPath") + "";
                            if (filepath != null && !filepath.equals("")) {
                                Wpsutils.wpsOpenFile(filepath, mActivity);
                                break;
                            } else {
                                UtilisClass.showToast(mActivity, "未找到该文件！");
                            }
                        }
                    }
                } else {
                    UtilisClass.showToast(mActivity, "未找到该站段关于操作提示的相关文件！");
                }
                break;
            case R.id.tv_img:
                //站场示意图
                List<Map> file2 = dbOpenHelper.queryListMap("select * from StationFiles where StationId=?", new String[]{String.valueOf(list.get(startPoint).get("StationId"))});
                if (file2.size() > 0) {
                    for (int j = 0; j < file2.size(); j++) {
                        String fileExtension = file2.get(j).get("FileExtension") + "";//获取文件类型
                        fileExtension.toLowerCase();
                        if (fileExtension.equals(".doc") || fileExtension.equals(".ppt") || fileExtension.equals(".xls")) {
                            String filepath = file2.get(j).get("LocaPath") + "";
                            if (filepath != null && !filepath.equals("")) {
                                Wpsutils.wpsOpenFile(filepath, mActivity);
                                break;
                            } else {
                                UtilisClass.showToast(mActivity, "未找到该文件！");
                            }
                        } else if (fileExtension.equals(".png") || fileExtension.equals(".jpg")) {
                            String filepath = file2.get(j).get("LocaPath") + "";
                            if (filepath != null && !filepath.equals("")) {
                                Intent imageintent = new Intent(Intent.ACTION_VIEW);
                                imageintent.setDataAndType(Uri.parse(filepath), "image/*");
                                startActivity(imageintent);
                                break;
                            } else {
                                UtilisClass.showToast(mActivity, "未找到该文件！");
                            }
                        }
                    }
                } else {
                    UtilisClass.showToast(mActivity, "未找到该站段关于站场示意图的相关文件！");
                }
                break;
            case R.id.tv_carend:
                //行车结束
                new AlertDialog.Builder(mActivity).setTitle("提示：")
                        .setMessage("是否结束当前流程")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                showNext();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).show();
                break;
            case R.id.tv_photograph:
                //行车拍照
                fileName = SDCardHelper.fileSdkPath(photopath) + File.separator + nowTime + ".jpg";
                intentsystem.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                intentsystem.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(fileName)));
                startActivityForResult(intentsystem, Activity.DEFAULT_KEYS_DIALER);
                break;
            case R.id.tv_video:
                //行车录像
                fileName = SDCardHelper.fileSdkPath(videopath) + File.separator + nowTime + ".mp4";
                intentsystem.setAction(MediaStore.ACTION_VIDEO_CAPTURE);
                intentsystem.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(fileName)));                //指定要保存的位置。
                intentsystem.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 100);
                intentsystem.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 60);
                startActivityForResult(intentsystem, 5);
                break;
            case R.id.tv_aideo:
                //行车录音
                intentsystem = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
                startActivityForResult(intentsystem, 6);
                break;

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Activity.DEFAULT_KEYS_DIALER: {
                File file = new File(fileName);
                Log.e("mTag", file.length() / 1024 + "");
                break;
            }
            case 5: {
                File file = new File(fileName);
                Log.e("mTag", file.length() / 1024 + "");
                break;
            }
            case 6: {
                Date dt = new Date();//如果不需要格式,可直接用dt,dt就是当前系统时间
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置显示格式
                String nowTime = df.format(dt);

                try {
                    Uri uri = data.getData();
                    String filePath = getAudioFilePathFromUri(uri);
                    fileName = SDCardHelper.fileSdkPath(audiopath) + File.separator + nowTime + ".3gpp";
                    copyFile(filePath, fileName);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                break;
            }
        }
    }

    private String getAudioFilePathFromUri(Uri uri) {
        Cursor cursor = getActivity().getContentResolver()
                .query(uri, null, null, null, null);
        cursor.moveToFirst();
        int index = cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA);
        return cursor.getString(index);
    }

    //复制单个文件
    //@param oldPath String 原文件路径 如：c:/fqf.txt
    //@param newPath String 复制后路径 如：f:/fqf.txt
    //@return boolean
    public void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) {                  //文件存在时
                InputStream inStream = new FileInputStream(oldPath);      //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1024];
                int length;
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread;            //字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        } catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();
        }
    }

    //到达签点
    public boolean setTarinMomment() {
        SimpleDateFormat f1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date nDate = new Date();
        String time = f1.format(nDate);
        if (startPoint == list.size() - 2) {
            isok = DriverSign(dbOpenHelper, lastId, String.valueOf(list.get(startPoint + 1).get("TrainStationId")),
                    time);
            Log.i("到达签点", "---->i的值"  +startPoint + "-->站点名" + String.valueOf(list.get(startPoint + 1).get("TrainStationId")) + "-->到达签点时间" + time);
        } else {
            isok = DriverSign(dbOpenHelper, lastId, String.valueOf(list.get(startPoint).get("TrainStationId")),
                    time);
            Log.i("到达签点", "---->i的值" + startPoint + "-->站点名" + String.valueOf(list.get(startPoint).get("TrainStationId")) + "-->到达签点时间" + time);
        }
        if (isok) {
            Toast.makeText(mActivity, "到达签点成功", Toast.LENGTH_SHORT).show();
        }
        return isok;
    }


    boolean isGook = true;

    //发车/通过签点
    public boolean setTarinGoMomment() {
        String DepartTime = String.valueOf(list.get(startPoint).get("DepartTime"));
        SimpleDateFormat f1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SimpleDateFormat f = new SimpleDateFormat("HH:mm");
        Date nDate = new Date();
        long between = 0;
        StringBuffer timeLog = new StringBuffer();
        tv_tain_go.setText(f.format(nDate));
        if ((DepartTime.length() == 2) || (DepartTime.length() == 1)) {
            DepartTime = DepartTime + ":00";
        }

        //通过判断DepartTime有无--->（有）早点晚点正点，（无）通过签点
        if (!TextUtils.isEmpty(DepartTime)) {
            SimpleDateFormat f2 = new SimpleDateFormat("yyyy-MM-dd");
            try {
                String date = f2.format(nDate);
                Date arriveDate = f1.parse(date + " " + DepartTime);
                long atime = arriveDate.getTime();
                long ntime = nDate.getTime();
                if (atime > ntime) {
                    //早点
                    between = (atime - ntime) / 1000 / 60;
                    if (between > 0 && between <= 60) {
                        timeLog.append(between);
                    } else if (between > 60) {
                        timeLog.append(between / 60).append("小时").append(between % 60);
                    }
                    final long minute1;
                    minute1 = between;

                    final String time = f1.format(nDate);
                    isGook = DriverGoSign(dbOpenHelper, lastId, String.valueOf(list.get(startPoint).get("TrainStationId")),
                            time, minute1 + "", "0", "");
                    Log.i("发车/通过签点", "---->i的值" + startPoint + "-->站点名" + String.valueOf(list.get(startPoint).get("TrainStationId")) + "-->发车/通过签点时间" + time);
                    if (isok) {
                        Toast.makeText(mActivity, "通过签点成功", Toast.LENGTH_SHORT).show();
                    }
                } else if (ntime > atime) {

                    //晚点
                    between = (ntime - atime) / 1000 / 60;
                    if (between > 0 && between <= 60) {
                        timeLog.append(between);
                    } else if (between > 60) {
                        timeLog.append(between / 60).append("小时").append(between % 60);
                    }
                    final long minute1;
                    minute1 = between;

                    final String time = f1.format(ntime);
                    isGook = DriverGoSign(dbOpenHelper, lastId, String.valueOf(list.get(startPoint).get("TrainStationId")),
                            time, "0", minute1 + "", "");
                    Log.i("发车/通过签点", "---->i的值" + startPoint + "-->站点名" + String.valueOf(list.get(startPoint).get("TrainStationId")) + "-->发车/通过签点时间" + time);
                    if (isok) {
                        Toast.makeText(mActivity, "通过签点成功", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    //准时
                    String time = f1.format(nDate);
                    isGook = DriverGoSign(dbOpenHelper, lastId, String.valueOf(list.get(startPoint).get("TrainStationId")),
                            time, "0", "0", "");
                    Log.i("发车/通过签点", "---->i的值" + startPoint + "-->站点名" + String.valueOf(list.get(startPoint).get("TrainStationId")) + "-->发车/通过签点时间" + time);
                    if (isok) {
                        Toast.makeText(mActivity, "通过签点成功", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            //准时
            String time = f1.format(nDate);
            isGook = DriverGoSign(dbOpenHelper, lastId, String.valueOf(list.get(startPoint).get("TrainStationId")),
                    time, "0", "0", "");
            Log.i("发车/通过签点", "---->i的值" + startPoint + "-->站点名" + String.valueOf(list.get(startPoint).get("TrainStationId")) + "-->发车/通过签点时间" + time);
            if (isok) {
                Toast.makeText(mActivity, "通过签点成功", Toast.LENGTH_SHORT).show();
            }
        }
        return isGook;
    }

    public void listChange(int i) {
        tainmomentAdapter.setSelectIndex(i);
        lv_tainmoment.setSmoothScrollbarEnabled(true);
        lv_tainmoment.setSelection(i);
    }

    //准时签点
    public boolean DriverSign(DBOpenHelper openHelper, String DriveRecordId, String StationId, String ArriveTime) {
        String sql = "select * from DriveSignPoint where StationId=? and DriveRecordId=?";
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String time = f.format(new Date());
        boolean isok = false;
        List<Map> list = openHelper.queryListMap(sql, new String[]{StationId, DriveRecordId});
        if (list.size() > 0) {
            isok = openHelper.update("DriveSignPoint", new String[]{"DriveRecordId", "StationId", "ArriveTime"}, new Object[]{Integer.parseInt(DriveRecordId),
                    Integer.parseInt(StationId), ArriveTime}, new String[]{"StationId", "DriveRecordId"}, new String[]{StationId, DriveRecordId});
        } else {
            isok = openHelper.insert("DriveSignPoint", new String[]{"DriveRecordId", "StationId", "ArriveTime"}, new Object[]{Integer.parseInt(DriveRecordId),
                    Integer.parseInt(StationId), ArriveTime});
        }
        return isok;
    }

    //早点或晚点签点
    public boolean DriverGoSign(DBOpenHelper openHelper, String DriveRecordId, String StationId, String
            LeaveTime, String EarlyMinutes, String LateMinutes, String EarlyOrLateReason) {
        String sql = "select * from DriveSignPoint where StationId=? and DriveRecordId=?";
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String time = f.format(new Date());
        boolean isok = false;
        List<Map> list = openHelper.queryListMap(sql, new String[]{StationId, DriveRecordId});
        if (list.size() > 0) {
            isok = openHelper.update("DriveSignPoint", new String[]{"DriveRecordId", "StationId", "LeaveTime"
                    , "EarlyMinutes", "LateMinutes"}, new Object[]{Integer.parseInt(DriveRecordId),
                    Integer.parseInt(StationId), LeaveTime, Integer.parseInt(EarlyMinutes),
                    Integer.parseInt(LateMinutes)}, new String[]{"StationId", "DriveRecordId"}, new String[]{StationId, DriveRecordId});
        } else {
            isok = openHelper.insert("DriveSignPoint", new String[]{"DriveRecordId", "StationId", "LeaveTime"
                    , "EarlyMinutes", "LateMinutes"}, new Object[]{Integer.parseInt(DriveRecordId),
                    Integer.parseInt(StationId), LeaveTime, Integer.parseInt(EarlyMinutes),
                    Integer.parseInt(LateMinutes)});
        }
        return isok;
    }

    //手账车次线路表
    public boolean DriveTrainNoAndLine(DBOpenHelper openHelper, String DriveRecordId, String TrainNoId, String LineId) {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String time = f.format(new Date());
        boolean isok = openHelper.insert("DriveTrainNoAndLine", new String[]{"DriveRecordId", "TrainNoId", "LineId", "AddTime", "IsDelete"},
                new Object[]{Integer.parseInt(DriveRecordId), Integer.parseInt(TrainNoId), Integer.parseInt(LineId), time, "false"});
        return isok;
    }

    public void showDialog(Context context, final DBOpenHelper openHelper, final String DriveRecordId, final String StationId) {

        tianGrapDialog = new TianGrapDialog(context, new TianGrapDialog.OntvOkClick() {
            @Override
            public void getStr(String CarriageCount, String CarryingCapacity, String Length, String Decompress, String RowTime, String FillingTime, String Missing) {
                boolean isok = TrainFormation(openHelper, DriveRecordId, StationId, CarriageCount, CarryingCapacity, Length, Decompress, RowTime, FillingTime, Missing);
                if (isok) {
                    Toast.makeText(mActivity, "添加成功", Toast.LENGTH_SHORT).show();
                    if (tianGrapDialog != null) {
                        tianGrapDialog.dismiss();
                    }
                } else {
                    Toast.makeText(mActivity, "添加成功", Toast.LENGTH_SHORT).show();
                }
            }
        });
        tianGrapDialog.show();
    }

    //列车编组 试风记录
    public boolean TrainFormation(DBOpenHelper openHelper, String DriveRecordId, String StationId,
                                  String CarriageCount, String CarryingCapacity, String Length,
                                  String Decompress, String RowTime, String FillingTime, String Missing) {
        String sql = "select * from TrainFormation where StationId=? and DriveRecordId=?";
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String time = f.format(new Date());
        boolean isok = false;
        List<Map> list = openHelper.queryListMap(sql, new String[]{StationId, DriveRecordId});
        if (CarriageCount.equals("")){
            CarriageCount = "0";
        }
        if (list.size() > 0) {
            isok = openHelper.update("TrainFormation", new String[]{"DriveRecordId", "StationId", "CarriageCount", "CarryingCapacity",
                            "Length", "Decompress", "RowTime", "FillingTime", "Missing", "NoteTime"}, new Object[]{Integer.parseInt(DriveRecordId), Integer.parseInt(StationId),
                            Integer.parseInt(CarriageCount), CarryingCapacity, Length, Decompress, RowTime, FillingTime, Missing, time},
                    new String[]{"StationId", "DriveRecordId"}, new String[]{StationId, DriveRecordId});
        } else {
            isok = openHelper.insert("TrainFormation", new String[]{"DriveRecordId", "StationId", "CarriageCount", "CarryingCapacity",
                    "Length", "Decompress", "RowTime", "FillingTime", "Missing", "NoteTime"}, new Object[]{Integer.parseInt(DriveRecordId), Integer.parseInt(StationId),
                    Integer.parseInt(CarriageCount), CarryingCapacity, Length, Decompress, RowTime, FillingTime, Missing, time});
        }
        return isok;
    }

    public void writeNotes(String currentSite, final String DriveRecordId, final String StationId) {

        final CustomDialog dialog = new CustomDialog(getActivity(), R.style.mydialog);
        View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.dailog_siteevent, null);
        dialog.setContentView(contentView);
        dialog.setCanceledOnTouchOutside(false);

        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        DisplayMetrics dm = new DisplayMetrics();
        dialogWindow.setAttributes(lp);


        TextView tv_eventsitename = (TextView) contentView.findViewById(R.id.tv_eventsitename);
        Button bt_eventcancel = (Button) contentView.findViewById(R.id.bt_eventcancel);
        final EditText et_record = (EditText) contentView.findViewById(R.id.et_record);
        Button bt_eventsubmit = (Button) contentView.findViewById(R.id.bt_eventsubmit);
        tv_eventsitename.setText(currentSite);

        bt_eventcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        bt_eventsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = et_record.getText().toString();
                if (!s.equals("")) {
                    addSiteEvent(dbOpenHelper, DriveRecordId, StationId, s);
                    et_record.setText("");
                    dialog.dismiss();
                } else {
                    UtilisClass.showToast(getActivity(), "输入内容不能为空！");
                }
            }
        });

        dialog.show();
    }

    //添加站点记事
    public boolean addSiteEvent(DBOpenHelper openHelper, String DriveRecordId, String StationId, String EarlyOrLateReason) {

        String sql = "select * from DriveSignPoint where StationId=? and DriveRecordId=?";
        boolean isok = false;
        List<Map> list = openHelper.queryListMap(sql, new String[]{StationId, DriveRecordId});
        if (list.size() > 0) {
            isok = openHelper.update("DriveSignPoint", new String[]{"DriveRecordId", "StationId", "EarlyOrLateReason"}, new Object[]{Integer.parseInt(DriveRecordId),
                    Integer.parseInt(StationId), EarlyOrLateReason}, new String[]{"StationId", "DriveRecordId"}, new String[]{StationId, DriveRecordId});
        } else {
            isok = openHelper.insert("DriveSignPoint", new String[]{"DriveRecordId", "StationId", "EarlyOrLateReason"}, new Object[]{Integer.parseInt(DriveRecordId),
                    Integer.parseInt(StationId), EarlyOrLateReason});
        }
        return isok;
    }

    //行车结束 ---> 填写退勤信息Fragment
    private void showNext() {
        FragmentManager manager = mActivity.getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        BackgroundFragment backgroundFragment = new BackgroundFragment();
        Bundle b = new Bundle();
        b.putString("id", lastId);
        b.putString("line", line);
        b.putString("code", code);
        b.putString("isShowDialog", "false");
        backgroundFragment.setArguments(b);
        mActivity.changgeFragment(MainActivity.F_TainOperation, backgroundFragment, MainActivity.F_Background);
        transaction.commit();
        mActivity.setShowTag(MainActivity.F_Background);
    }

}