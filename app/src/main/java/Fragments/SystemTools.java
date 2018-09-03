package Fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import DBUtils.DBOpenHelper;
import Utils.CustomDialog;
import Utils.NetUtils;
import Utils.SDCardHelper;
import config.ISystemConfig;
import config.SystemConfigFactory;
import zj.com.mc.MainActivity;
import zj.com.mc.NoteActivity;
import zj.com.mc.R;
import zj.com.mc.Register;
import zj.com.mc.UtilisClass;
import zj.com.mc.VideoImageShowActivity;

/**
 * Created by dell on 2016/7/29.
 * 系统工具
 */
public class SystemTools extends Fragment implements View.OnClickListener {
    private String personId;
    private FragmentManager fm;
    private Intent intentsystem;
    private DBOpenHelper dbOpenHelper;
    private LatestAnnouncement latestAnnouncement;
    private FrameLayout system_fragments;
    private ISystemConfig systemConfig;
    private String fileName;
    private String photopath;
    private String audiopath;
    private String videopath;
    private MainActivity mainActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity=(MainActivity) getActivity();
        fm = getActivity().getSupportFragmentManager();
        dbOpenHelper = DBOpenHelper.getInstance(getActivity());
        systemConfig = SystemConfigFactory.getInstance(getActivity()).getSystemConfig();
        personId = systemConfig.getUserId();
        latestAnnouncement = new LatestAnnouncement();
        photopath = NetUtils.PHOTOPATH;
        audiopath = NetUtils.AUDIOPATH;
        videopath = NetUtils.VIDEOPATH;
        if (systemConfig.isSystemTools()) {
            SDCardHelper.saveFileToSDCardCustomDirss(photopath);
            SDCardHelper.saveFileToSDCardCustomDirss(audiopath);
            SDCardHelper.saveFileToSDCardCustomDirss(videopath);
            systemConfig.setSystemTools(false);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.systemtools, container, false);
        view.findViewById(R.id.system_tools1).setOnClickListener(this);//日历
        view.findViewById(R.id.system_tools2).setOnClickListener(this);//计算器
        view.findViewById(R.id.system_tools3).setOnClickListener(this);//记事本
        view.findViewById(R.id.system_tools4).setOnClickListener(this);//拍照
        view.findViewById(R.id.system_tools5).setOnClickListener(this);//录音
        view.findViewById(R.id.system_tools6).setOnClickListener(this);//录像
        view.findViewById(R.id.system_tools7).setOnClickListener(this);//最新公告
        view.findViewById(R.id.system_tools8).setOnClickListener(this);//意见反馈
        view.findViewById(R.id.system_tools9).setOnClickListener(this);//退出系统
        view.findViewById(R.id.system_tools10).setOnClickListener(this);//退出系统
        system_fragments = (FrameLayout) view.findViewById(R.id.system_fragments);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        Date dt = new Date();//如果不需要格式,可直接用dt,dt就是当前系统时间
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置显示格式
        String nowTime = df.format(dt);
        intentsystem = new Intent();
        switch (view.getId()) {
            case R.id.system_tools1:
                //日历
                intentsystem.setData(CalendarContract.Events.CONTENT_URI);
//                intentsystem.setClassName("com.android.calendar","com.android.calendar.LaunchActivity");
                startActivity(intentsystem);
                break;
            case R.id.system_tools2:
                //计算
                intentsystem.setClassName("com.android.calculator2", "com.android.calculator2.Calculator");
                startActivity(intentsystem);
                break;
            case R.id.system_tools3:
                //记事本
                intentsystem = new Intent(getActivity(), NoteActivity.class);
                startActivity(intentsystem);
                break;
            case R.id.system_tools4:
                //拍照
                fileName = SDCardHelper.fileSdkPath(photopath) + File.separator + nowTime + ".jpg";
                intentsystem.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                intentsystem.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(fileName)));
                startActivityForResult(intentsystem, Activity.DEFAULT_KEYS_DIALER);
                break;
            case R.id.system_tools5:
                //录音
                intentsystem = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
                startActivityForResult(intentsystem, 6);
                break;
            case R.id.system_tools6:
                //录制视频
                fileName = SDCardHelper.fileSdkPath(videopath) + File.separator + nowTime + ".mp4";
                intentsystem.setAction(MediaStore.ACTION_VIDEO_CAPTURE);
                intentsystem.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(fileName)));                //指定要保存的位置。
                intentsystem.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 100);
                intentsystem.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 60);
                startActivityForResult(intentsystem, 5);
                break;
            case R.id.system_tools7:
                //最新公告
                addFrag(latestAnnouncement);
                break;
            case R.id.system_tools8:
                //意见反馈
                intentsystem = new Intent(getActivity(), VideoImageShowActivity.class);
                startActivity(intentsystem);
                break;
            case R.id.system_tools9:
                //退出系统
                intentsystem = new Intent(getActivity(), Register.class);
                getActivity().finish();
                startActivity(intentsystem);
                recordOperateLog(2,"退出终端登录");
                break;
            case R.id.system_tools10:
                //系统升级
                mainActivity.updateApk();
                recordOperateLog(3,"终端系统升级");
                break;
        }

    }

    private void recordOperateLog(int LogType, String LogContent) {
        Date dt = new Date();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String nowTime = df.format(dt);
        dbOpenHelper.insert("AppOperateLog", new String[]{"LogType", "LogContent", "OperatorId", "DeviceId", "AddTime"},
                new Object[]{LogType, LogContent, systemConfig.getOperatorId(), 0, nowTime});
    }

    public void setdialogvocationfenlei() {

        final CustomDialog dialog = new CustomDialog(getActivity(), R.style.mydialog);
        View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.feedback, null);
        dialog.setContentView(contentView);
        dialog.setCanceledOnTouchOutside(false);

        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        DisplayMetrics dm = new DisplayMetrics();

        dialogWindow.setAttributes(lp);
        final Button feedback_back = (Button) contentView.findViewById(R.id.feedback_back);
        Button feedback_ok = (Button) contentView.findViewById(R.id.feedback_ok);
        final EditText feedback_ed = (EditText) contentView.findViewById(R.id.feedback_ed);
        feedback_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        feedback_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = feedback_ed.getText().toString();
                if (!s.equals("")) {
                    String date = UtilisClass.getStringDate();
                    dbOpenHelper.insert("Feedback", new String[]{"Content", "PersonId", "AddTime"},
                            new Object[]{s, personId, date});
                    UtilisClass.showToast(getActivity(), s + date + personId);
                    feedback_ed.setText("");
                    dialog.dismiss();
                } else {
                    UtilisClass.showToast(getActivity(), "输入内容不能为空！");
                }
            }
        });
        dialog.show();
    }

    private void addFrag(Fragment f) {
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.add(R.id.system_fragments, f);
        transaction.show(f).commit();
        system_fragments.setVisibility(View.VISIBLE);
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
                    if (data != null) {
                        Uri uri = data.getData();
                        String filePath = getAudioFilePathFromUri(uri);
                        fileName = SDCardHelper.fileSdkPath(audiopath) + File.separator + nowTime + ".3gpp";
                        copyFile(filePath, fileName);
                    }
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
}
