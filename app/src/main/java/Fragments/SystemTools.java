package Fragments;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
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

import DBUtils.DBOpenHelper;
import Utils.CustomDialog;
import zj.com.mc.NoteActivity;
import zj.com.mc.R;
import zj.com.mc.Register;
import zj.com.mc.UtilisClass;


/**
 * Created by dell on 2016/7/29.
 */
public class SystemTools extends Fragment implements View.OnClickListener{
    private String personId;
    private FragmentManager fm;
    private Intent intentsystem;
    private DBOpenHelper dbOpenHelper;
    private LatestAnnouncement latestAnnouncement;
    private LinearLayout system_layout;
    private FrameLayout system_fragments;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.systemtools,container,false);
        view.findViewById(R.id.system_tools1).setOnClickListener(this);
        view.findViewById(R.id.system_tools2).setOnClickListener(this);
        view.findViewById(R.id.system_tools3).setOnClickListener(this);
        view.findViewById(R.id.system_tools4).setOnClickListener(this);
        view.findViewById(R.id.system_tools5).setOnClickListener(this);
        view.findViewById(R.id.system_tools6).setOnClickListener(this);
        view.findViewById(R.id.system_tools7).setOnClickListener(this);
        view.findViewById(R.id.system_tools8).setOnClickListener(this);
        view.findViewById(R.id.system_tools9).setOnClickListener(this);
        system_fragments=(FrameLayout) view.findViewById(R.id.system_fragments);
        system_layout= (LinearLayout) view.findViewById(R.id.system_layout);
        fm=getActivity().getSupportFragmentManager();
        SharedPreferences sharedPreferences=getActivity().getSharedPreferences("PersonInfo", Context.MODE_PRIVATE);
        personId = sharedPreferences.getString("PersonId", null);
        dbOpenHelper= DBOpenHelper.getInstance(getActivity());
        latestAnnouncement=new LatestAnnouncement();


        return view;
    }

    @Override
    public void onClick(View view) {


            intentsystem = new Intent();
            switch (view.getId()) {
                case R.id.system_tools1:
//日历

                    intentsystem.setComponent(new ComponentName("com.android.calendar", "com.android.calendar.LaunchActivity"));
                    startActivity(intentsystem);
                    break;
                case R.id.system_tools2:
//计算机
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
                    intentsystem = new Intent("android.media.action.IMAGE_CAPTURE");
                    startActivity(intentsystem);
                    break;
                case R.id.system_tools5:
//录音
                    intentsystem = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
                    startActivity(intentsystem);
                    break;
                case R.id.system_tools6:
//录像
                    intentsystem.setAction("android.media.action.VIDEO_CAPTURE");
                    intentsystem.addCategory("android.intent.category.DEFAULT");
                    startActivity(intentsystem);
                    break;
                case R.id.system_tools7:
//最新公告
//                    UtilisClass.showToast(getActivity(),"暂无公告");

                    addFrag(latestAnnouncement);


                    break;
                case R.id.system_tools8:
//意见反馈
//                    UtilisClass.showToast(getActivity(),"暂时无数据");
                    setdialogvocationfenlei();
                    break;
                case R.id.system_tools9:
//退出系统
                    intentsystem=new Intent(getActivity(), Register.class);
                    getActivity().finish();
                    startActivity(intentsystem);
                    break;

            }

        }



    public void setdialogvocationfenlei(){


        final CustomDialog dialog= new CustomDialog(getActivity(), R.style.mydialog);
        View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.feedback, null);
        dialog.setContentView(contentView);
        dialog.setCanceledOnTouchOutside(false);

        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        DisplayMetrics dm=new DisplayMetrics();


        dialogWindow.setAttributes(lp);
        final Button feedback_back= (Button) contentView.findViewById(R.id.feedback_back);
        Button feedback_ok= (Button) contentView.findViewById(R.id.feedback_ok);
        final EditText feedback_ed= (EditText) contentView.findViewById(R.id.feedback_ed);
        feedback_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

            }
        });
        feedback_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s=feedback_ed.getText().toString();

                if (!s.equals("")){
                    String date= UtilisClass.getStringDate();
                    dbOpenHelper.insert("Feedback",new String[]{"Content","PersonId","AddTime"},
                            new Object[]{s,personId,date});

                    UtilisClass.showToast(getActivity(),s+date+personId);
                    feedback_ed.setText("");
                    dialog.dismiss();

                }else {
                    UtilisClass.showToast(getActivity(),"输入内容不能为空！");

                }
            }
        });


        dialog.show();

    }

    private void addFrag(Fragment f) {

        FragmentTransaction transaction = fm.beginTransaction();
//        transaction.replace(R.id.system_fragments, f).addToBackStack(null).commit();
        transaction.add(R.id.system_fragments,f);
            transaction.show(f).commit();
        system_fragments.setVisibility(View.VISIBLE);
//        system_layout.setVisibility(View.GONE);


    }

}
