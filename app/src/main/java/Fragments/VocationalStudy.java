package Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import DBUtils.DBOpenHelper;
import Utils.CommonAdapter;
import Utils.CustomDialog;
import Utils.ViewHolder;
import zj.com.mc.MineError;
import zj.com.mc.NormalTestPage;
import zj.com.mc.R;

/**
 * Created by dell on 2016/7/29.
 */
public class VocationalStudy extends Fragment implements View.OnClickListener{
    private ListView testlistview;
    private List<Map> testlistviewdata,exantypelist;//通知考试，题库分类
    private CommonAdapter<Map> vocationaladapter,vocationgridadapter2;
    private DBOpenHelper dbOpenHelper;
    private TextView tokedata;
    private String personId;
    private SharedPreferences s;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.activity_main1,container,false);

        initviewvocation(view);//初始化view
        s= getActivity().getSharedPreferences("PersonInfo", Context.MODE_PRIVATE);
        personId=s.getString("PersonId",null);
        dbOpenHelper= DBOpenHelper.getInstance(getActivity().getApplicationContext());
        return view;
    }
//初始化view
    private void initviewvocation(View view) {
        view.findViewById(R.id.test_simulat).setOnClickListener(this);
        view.findViewById(R.id.test_simulat2).setOnClickListener(this);
        view.findViewById(R.id.test_simulat3).setOnClickListener(this);
        view.findViewById(R.id.test_simulat4).setOnClickListener(this);
        view.findViewById(R.id.test_simulat5).setOnClickListener(this);
        tokedata= (TextView) view.findViewById(R.id.tokedata);
        testlistview= (ListView) view.findViewById(R.id.vocational_list);
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.test_simulat:
//业务学习
                setdialogvocationfenlei();

                break;
            case R.id.test_simulat2:
//我的错题
                Intent intenterror=new Intent(getActivity(), MineError.class);
                startActivity(intenterror);
                break;
            case R.id.test_simulat3:
//模拟考试
                break;
            case R.id.test_simulat4:
//考试练习
                break;
            case R.id.test_simulat5:
//考试统计
                break;

        }

    }

    //马上参加考试
    public void setdialog(String texttitle, final String timeLimit, final String passScore, final String questionCount, final String testclass, String testTimes, String score, final String notifyId){

        final CustomDialog dialog= new CustomDialog(getActivity(), R.style.mydialog);
        View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.formal_exams, null);
        dialog.setContentView(contentView);
        dialog.setCanceledOnTouchOutside(true);

        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        DisplayMetrics dm=new DisplayMetrics();

//        lp.x = (int) (235 ); // 新位置X坐标
//        lp.y = (int) (dm.heightPixels); // 新位置Y坐标
        lp.width = (int) (1000); // 宽度
        lp.height = (int) (1000 * 0.6); // 高度
//lp.alpha = 0.7f; // 透明度

        dialogWindow.setAttributes(lp);


        TextView pop_context= (TextView) contentView.findViewById(R.id.pop_context);//考试内容
        TextView pop_time= (TextView) contentView.findViewById(R.id.pop_time);//考试时限
        TextView pop_score= (TextView) contentView.findViewById(R.id.pop_score);//通过分数
        TextView pop_contextscore= (TextView) contentView.findViewById(R.id.pop_contextscore);//总题数
        TextView pop_testtime= (TextView) contentView.findViewById(R.id.pop_testtime);//已考次数
        TextView pop_hightst_score= (TextView) contentView.findViewById(R.id.pop_hightst_score);//最高分数
        pop_context.setText(texttitle);//考试内容
        pop_time.setText(timeLimit);//考试时限
        pop_score.setText(passScore);//通过分数
        pop_contextscore.setText(questionCount);//总题数
                pop_testtime.setText(testTimes);//已考次数
        pop_hightst_score.setText(score);//最高分数

        final Button button= (Button) contentView.findViewById(R.id.pop_start);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //调到二级界面
                Intent intentvocation=new Intent(getActivity(), NormalTestPage.class);
                Bundle bundlevocation=new Bundle();

                bundlevocation.putString("title","正式考试");
                bundlevocation.putInt("type",1);
                bundlevocation.putString("notifyId",notifyId);
                bundlevocation.putString("timelilmit",timeLimit);
                bundlevocation.putString("allcount",questionCount);
                bundlevocation.putString("PassScore",passScore);
                bundlevocation.putString("Id",testclass);
                intentvocation.putExtra("bundle",bundlevocation);
                startActivity(intentvocation);
                dialog.dismiss();
            }
        });


        dialog.show();

    }
    //分类
    public void setdialogvocationfenlei(){
        exantypelist=dbOpenHelper.queryListMap("select * from ExamFiles",null);
        final CustomDialog dialog= new CustomDialog(getActivity(), R.style.mydialog);
        View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.test_classifica, null);
        dialog.setContentView(contentView);
        dialog.setCanceledOnTouchOutside(true);

        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.LEFT | Gravity.TOP);
        DisplayMetrics dm=new DisplayMetrics();

        lp.x = (int) (235 ); // 新位置X坐标
        lp.y = (int) (dm.heightPixels+100); // 新位置Y坐标
        lp.width = (int) (800); // 宽度
        lp.height = (int) (800 * 0.6); // 高度
//lp.alpha = 0.7f; // 透明度
        dialogWindow.setAttributes(lp);

        vocationgridadapter2=new CommonAdapter<Map>(getActivity(),exantypelist, R.layout.trainsche_result_grivaitem) {
            @Override
            protected void convertlistener(ViewHolder holder, Map map) {

            }

            @Override
            public void convert(ViewHolder holder, Map map) {
                holder.setText(R.id.grid1_item,map.get("FileName").toString());
            }
        };


        GridView gridView= (GridView) contentView.findViewById(R.id.test_class_gridv);//分类gridview
        gridView.setAdapter(vocationgridadapter2);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //调到二级界面
                Intent intentvocation=new Intent(getActivity(), NormalTestPage.class);
                Bundle bundlevocation=new Bundle();

                bundlevocation.putString("Id",exantypelist.get(i).get("Id").toString());
                bundlevocation.putInt("type",-1);
                bundlevocation.putString("ExamTypeId",exantypelist.get(i).get("ExamTypeId")+"");
                bundlevocation.putString("title","模拟考试"+"-"+exantypelist.get(i).get("FileName").toString());
                intentvocation.putExtra("bundle",bundlevocation);
                startActivity(intentvocation);
                dialog.dismiss();
            }
        });
        dialog.show();

    }
//设置考试通知
    private void setlistviewdata(){
        testlistviewdata=dbOpenHelper.queryListMap("select * from ExamNotify",null);

        if (testlistviewdata.size()!=0) {
            Collections.reverse(testlistviewdata);
            vocationaladapter = new CommonAdapter<Map>(getActivity(), testlistviewdata, R.layout.vocational_list_item) {
                @Override
                protected void convertlistener(final ViewHolder holder, final Map map) {
                    int passscore = Integer.parseInt(map.get("PassScore") + "");
                    int testtimes = Integer.parseInt(map.get("ResitCount") + "") + 1;
                    int score = 0;
                    List<Map> testHistory = dbOpenHelper.queryListMap("select * from ExamRecords where ExamNotifyId=? and PersionId=?",
                            new String[]{map.get("Id") + "", personId});

                    if (testHistory.size() != 0) {
                        holder.setText(R.id.volist_item5, testHistory.size() + "");
                        if (testHistory.size() == 1) {
                            score = Integer.parseInt(testHistory.get(0).get("Score") + "");

                            holder.setText(R.id.volist_item3, testHistory.get(0).get("Score") + "");
                            if (passscore > score && testHistory.size() < testtimes) {
                                holder.setText(R.id.volist_item6, "马上参加");
                                holder.getView(R.id.volist_item6).setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View view) {
//                        UtilisClass.showToast(getActivity(),"sssaaa");
                                        //设置dialog要传递的考试分类
                                        String texttitle = map.get("ExamName") + "";//考试名称
                                        String timeLimit = map.get("TimeLimit") + "";//考试时长
                                        String passScore = map.get("PassScore") + "";//考试通过分数
                                        String questionCount = map.get("QuestionCount") + "";//总题数
                                        String testtimes = (String) holder.getTextview(R.id.volist_item5).getText();//已考次数
                                        String historyHightestscore = (String) holder.getTextview(R.id.volist_item3).getText();//最高分数
                                        String NotifyId = map.get("Id") + "";
                                        setdialog(texttitle, timeLimit, passScore, questionCount, map.get("ExamFilesId") + "", testtimes, historyHightestscore, NotifyId);
                                    }
                                });
                            } else {
                                if (passscore > score) {
                                    holder.setText(R.id.volist_item6, "已通过");
                                    holder.getTextview(R.id.volist_item6).setBackgroundColor(Color.WHITE);
                                    holder.getTextview(R.id.volist_item6).setTextColor(Color.GRAY);

                                } else {
                                    holder.setText(R.id.volist_item6, "未通过");
                                    holder.getTextview(R.id.volist_item6).setBackgroundColor(Color.WHITE);
                                    holder.getTextview(R.id.volist_item6).setTextColor(Color.GRAY);
                                }
                            }
                        } else {

                            for (int i = 0; i < testHistory.size() - 1; i++) {
                                int lastscore = Integer.parseInt(testHistory.get(i + 1).get("Score") + "");
                                score = score > lastscore ? score : lastscore;
                            }
                            holder.setText(R.id.volist_item3, score + "");

                            if (passscore > score && testHistory.size() < testtimes) {
                                holder.setText(R.id.volist_item6, "马上参加");
                                holder.getView(R.id.volist_item6).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
//                        UtilisClass.showToast(getActivity(),"sssaaa");
                                        //设置dialog要传递的考试分类
                                        String texttitle = map.get("ExamName") + "";//考试名称
                                        String timeLimit = map.get("TimeLimit") + "";//考试时长
                                        String passScore = map.get("PassScore") + "";//考试通过分数
                                        String questionCount = map.get("QuestionCount") + "";//总题数
                                        String testtimes = (String) holder.getTextview(R.id.volist_item5).getText();//已考次数
                                        String historyHightestscore = (String) holder.getTextview(R.id.volist_item3).getText();//最高分数
                                        String NotifyId = map.get("Id") + "";
                                        setdialog(texttitle, timeLimit, passScore, questionCount, map.get("ExamFilesId") + "", testtimes, historyHightestscore, NotifyId);
                                    }
                                });
                            } else {
                                if (passscore > score) {
                                    holder.setText(R.id.volist_item6, "未通过");
                                    holder.getTextview(R.id.volist_item6).setBackgroundColor(Color.WHITE);
                                    holder.getTextview(R.id.volist_item6).setTextColor(Color.GRAY);
                                } else {
                                    holder.setText(R.id.volist_item6, "已通过");
                                    holder.getTextview(R.id.volist_item6).setBackgroundColor(Color.WHITE);
                                    holder.getTextview(R.id.volist_item6).setTextColor(Color.GRAY);
                                }
                            }
                        }
                    } else {
                        holder.setText(R.id.volist_item3, 0 + "");
                        holder.setText(R.id.volist_item5, 0 + "");
                        holder.setText(R.id.volist_item6, "马上参加");
                        holder.getView(R.id.volist_item6).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
//                        UtilisClass.showToast(getActivity(),"sssaaa");
                                //设置dialog要传递的考试分类
                                String texttitle = map.get("ExamName") + "";//考试名称
                                String timeLimit = map.get("TimeLimit") + "";//考试时长
                                String passScore = map.get("PassScore") + "";//考试通过分数
                                String questionCount = map.get("QuestionCount") + "";//总题数
                                String testtimes = (String) holder.getTextview(R.id.volist_item5).getText();//已考次数
                                String historyHightestscore = (String) holder.getTextview(R.id.volist_item3).getText();//最高分数
                                String NotifyId = map.get("Id") + "";
                                setdialog(texttitle, timeLimit, passScore, questionCount, map.get("ExamFilesId") + "", testtimes, historyHightestscore, NotifyId);
                            }
                        });
                    }
                }

                @Override
                public void convert(ViewHolder holder, Map map) {
                    holder.setText(R.id.volist_item1, String.valueOf(map.get("ExamName")));
                    holder.setText(R.id.volist_item2, String.valueOf(map.get("EndTime")).split(" ")[0]);
                    holder.setText(R.id.volist_item4, String.valueOf(map.get("PassScore")));
                }
            };

            testlistview.setAdapter(vocationaladapter);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        setlistviewdata();
    }

}
