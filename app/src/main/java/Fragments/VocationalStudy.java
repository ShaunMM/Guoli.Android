package Fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.DigitsKeyListener;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import DBUtils.DBOpenHelper;
import Utils.CommonAdapter;
import Utils.CustomDialog;
import Utils.ViewHolder;
import config.ISystemConfig;
import config.SystemConfigFactory;
import zj.com.mc.MineError;
import zj.com.mc.NormalTestPage;
import zj.com.mc.R;

/**
 * 初进业务学习界面
 */
public class VocationalStudy extends Fragment implements View.OnClickListener {

    private ListView testlistview;
    private List<Map> testlistviewdata, exantypelist;//通知考试，题库分类
    private List<Map> mineErrorClassList;//错题分类
    private CommonAdapter<Map> vocationaladapter, vocationgridadapter2;
    private DBOpenHelper dbOpenHelper;
    private ISystemConfig systemConfig;
    private String personId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main1, container, false);
        initView(view);
        dbOpenHelper = DBOpenHelper.getInstance(getActivity().getApplicationContext());
        systemConfig = SystemConfigFactory.getInstance(getActivity()).getSystemConfig();
        personId = systemConfig.getUserId();
        testlistviewdata = new ArrayList<>();
        exantypelist = new ArrayList<>();
        mineErrorClassList = new ArrayList<>();
        return view;
    }

    private void initView(View view) {
        view.findViewById(R.id.test_simulat).setOnClickListener(this);
        view.findViewById(R.id.test_simulat2).setOnClickListener(this);
        view.findViewById(R.id.test_simulat6).setOnClickListener(this);
        testlistview = (ListView) view.findViewById(R.id.vocational_list);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //岗前答题
            case R.id.test_simulat:
                setAttendanceAnswer();
                break;
            //我的错题
            case R.id.test_simulat2:
                mineErrorClass();
                break;
            //模拟练习
            case R.id.test_simulat6:
                setShamExam();
                break;
        }
    }

    public void setdialog(String texttitle, final String timeLimit, final String passScore,
                          final String questionCount, final String testclass, String testTimes, String score,
                          final String notifyId, final String examTypeId) {

        final CustomDialog dialog = new CustomDialog(getActivity(), R.style.mydialog);
        View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.formal_exams, null);

        TextView pop_context = (TextView) contentView.findViewById(R.id.pop_context);//考试内容
        TextView pop_time = (TextView) contentView.findViewById(R.id.pop_time);//考试时限
        TextView pop_score = (TextView) contentView.findViewById(R.id.pop_score);//通过分数
        TextView pop_contextscore = (TextView) contentView.findViewById(R.id.pop_contextscore);//总题数
        TextView pop_testtime = (TextView) contentView.findViewById(R.id.pop_testtime);//已考次数
        TextView pop_hightst_score = (TextView) contentView.findViewById(R.id.pop_hightst_score);//最高分数
        pop_context.setText(texttitle);//考试内容
        pop_time.setText(timeLimit+"分钟");//考试时限
        pop_score.setText(passScore+"分");//通过分数
        pop_contextscore.setText(questionCount+"道");//总题数
        pop_testtime.setText(testTimes+"次");//已考次数
        pop_hightst_score.setText(score+"分");//最高分数

        final Button button = (Button) contentView.findViewById(R.id.pop_start);
        setDialog(dialog, contentView);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //跳到考试界面
                Intent intentvocation = new Intent(getActivity(), NormalTestPage.class);
                Bundle bundlevocation = new Bundle();

                bundlevocation.putString("title", "正式考试");
                bundlevocation.putInt("type", 1);
                bundlevocation.putString("notifyId", notifyId);
                bundlevocation.putString("timelilmit", timeLimit);
                bundlevocation.putString("allcount", questionCount);
                bundlevocation.putString("PassScore", passScore);
                bundlevocation.putString("Id", testclass);
                bundlevocation.putString("ExamTypeId", examTypeId);
                intentvocation.putExtra("bundle", bundlevocation);
                startActivity(intentvocation);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void setAttendanceAnswer() {
        exantypelist = dbOpenHelper.queryListMap("select * from ExamFiles", null);
        if (exantypelist.size() != 0) {
            final CustomDialog dialog = new CustomDialog(getActivity(), R.style.mydialog);
            View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.test_classifica, null);
            TextView tv_studyname = (TextView) contentView.findViewById(R.id.tv_studyname);
            tv_studyname.setText("岗 前 答 题");
            EditText tv_testnum = (EditText) contentView.findViewById(R.id.tv_testnum);
            tv_testnum.setVisibility(View.GONE);
            setDialog(dialog, contentView);

            vocationgridadapter2 = new CommonAdapter<Map>(getActivity(), exantypelist, R.layout.trainsche_result_grivaitem) {
                @Override
                protected void convertlistener(ViewHolder holder, Map map) {

                }

                @Override
                public void convert(ViewHolder holder, Map map) {
                    holder.setText(R.id.grid1_item, map.get("FileName").toString());
                }
            };

            GridView gridView = (GridView) contentView.findViewById(R.id.test_class_gridv);//分类gridview
            gridView.setAdapter(vocationgridadapter2);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    //跳到答题界面
                    Intent intentvocation = new Intent(getActivity(), NormalTestPage.class);
                    Bundle bundlevocation = new Bundle();

                    bundlevocation.putString("Id", exantypelist.get(i).get("Id").toString());
                    bundlevocation.putInt("type", 2);
                    bundlevocation.putString("ExamTypeId", exantypelist.get(i).get("ExamTypeId") + "");
                    bundlevocation.putString("title", "岗前答题" + "-" + exantypelist.get(i).get("FileName").toString());
                    bundlevocation.putInt("QuestionNum", 5);
                    intentvocation.putExtra("bundle", bundlevocation);
                    startActivity(intentvocation);
                    dialog.dismiss();

                }
            });
            dialog.show();
        }
    }

    public void setShamExam() {
        exantypelist = dbOpenHelper.queryListMap("select * from ExamFiles", null);
        if (exantypelist.size() != 0) {
            List<Map> examsort = new ArrayList<>();
            for (int i = 0; i < exantypelist.size(); i++) {
                Map map = new HashMap();
                Map examcount = dbOpenHelper.queryItemMap("select count(1) from ExamQuestion where ExamFileId = ?", new String[]{exantypelist.get(i).get("Id").toString()});
                String fileName = exantypelist.get(i).get("FileName").toString() + "(" + examcount.get("count(1)") + ")";
                map.put("FileName", fileName);
                examsort.add(map);
            }
            final CustomDialog dialog = new CustomDialog(getActivity(), R.style.mydialog);
            View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.test_classifica, null);
            final EditText tv_testnum = (EditText) contentView.findViewById(R.id.tv_testnum);
            tv_testnum.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
            TextView tv_studyname = (TextView) contentView.findViewById(R.id.tv_studyname);
            tv_studyname.setText("模 拟 练 习");
            tv_testnum.setOnClickListener(this);
            setDialog(dialog, contentView);

            vocationgridadapter2 = new CommonAdapter<Map>(getActivity(), examsort, R.layout.trainsche_result_grivaitem) {
                @Override
                protected void convertlistener(ViewHolder holder, Map map) {
                }

                @Override
                public void convert(ViewHolder holder, Map map) {
                    holder.setText(R.id.grid1_item, map.get("FileName").toString());
                }
            };

            GridView gridView = (GridView) contentView.findViewById(R.id.test_class_gridv);//分类gridview
            gridView.setAdapter(vocationgridadapter2);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if (tv_testnum.getText().toString().equals("")) {
                        Toast.makeText(getActivity(), "请先输入模拟考试题目", Toast.LENGTH_SHORT).show();
                    } else {
                        Map examcount = dbOpenHelper.queryItemMap("select count(1) from ExamQuestion where ExamFileId = ?", new String[]{exantypelist.get(i).get("Id").toString()});
                        Intent intentvocation = new Intent(getActivity(), NormalTestPage.class);
                        Bundle bundlevocation = new Bundle();
                        bundlevocation.putString("Id", exantypelist.get(i).get("Id").toString());
                        bundlevocation.putInt("type", 3);//区分正式考试（1）岗前答题（2）模拟练习（3）
                        bundlevocation.putString("ExamTypeId", exantypelist.get(i).get("ExamTypeId") + "");
                        bundlevocation.putString("title", "模拟练习" + "-" + exantypelist.get(i).get("FileName").toString());

                        if (Integer.parseInt(examcount.get("count(1)").toString()) >= Integer.parseInt(tv_testnum.getText().toString())) {
                            bundlevocation.putInt("QuestionNum", Integer.parseInt(tv_testnum.getText().toString()));
                        } else {
                            bundlevocation.putInt("QuestionNum", Integer.parseInt(examcount.get("count(1)").toString()));
                        }
                        intentvocation.putExtra("bundle", bundlevocation);
                        startActivity(intentvocation);
                        dialog.dismiss();
                    }
                }
            });
            dialog.show();
        }
    }

    public void mineErrorClass() {
        final List<Map> errorClassList = new ArrayList<Map>();
        boolean istrue = false;
        mineErrorClassList = dbOpenHelper.queryListMap("select * from ExamErrorQuestions", null);

        if (mineErrorClassList.size() <= 0) {
            Toast.makeText(getContext(), "暂时还没有错题收集", Toast.LENGTH_LONG).show();
            return;
        }

        if (errorClassList.size() == 0) {
            errorClassList.add(0, mineErrorClassList.get(0));
        }

        for (int i = 0; i < mineErrorClassList.size(); i++) {
            if (errorClassList.size() > 0) {
                for (int j = 0; j < errorClassList.size(); j++) {
                    if (mineErrorClassList.get(i).get("TableNamessss").toString().equals(errorClassList.get(j).get("TableNamessss").toString())) {
                        istrue = false;
                        break;
                    } else {
                        istrue = true;
                    }
                }
                if (istrue) {
                    errorClassList.add(errorClassList.size(), mineErrorClassList.get(i));
                }
            }
        }

        final CustomDialog dialog = new CustomDialog(getActivity(), R.style.mydialog);
        View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.test_classifica, null);
        TextView tv_studyname = (TextView) contentView.findViewById(R.id.tv_studyname);
        TextView tv_testnum = (TextView) contentView.findViewById(R.id.tv_testnum);
        tv_testnum.setVisibility(View.GONE);
        tv_studyname.setText("我 的 错 题");
        setDialog(dialog, contentView);
        vocationgridadapter2 = new CommonAdapter<Map>(getActivity(), errorClassList, R.layout.trainsche_result_grivaitem) {
            @Override
            protected void convertlistener(ViewHolder holder, Map map) {
            }

            @Override
            public void convert(ViewHolder holder, Map map) {
                holder.setText(R.id.grid1_item, map.get("TableNamessss").toString());//"FileName"替换成表名
            }
        };

        GridView gridView = (GridView) contentView.findViewById(R.id.test_class_gridv);//分类gridview
        gridView.setAdapter(vocationgridadapter2);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intenterror = new Intent(getActivity(), MineError.class);
                Bundle bundleerror = new Bundle();
                bundleerror.putString("TableNamessss", errorClassList.get(i).get("TableNamessss").toString());//把表名传过去
                intenterror.putExtra("bundle", bundleerror);
                startActivity(intenterror);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void setDialog(CustomDialog dialog, View contentView) {
        dialog.setContentView(contentView);
        dialog.setCanceledOnTouchOutside(true);
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        lp.width = (int) (width * 0.7);
        lp.height = (int) (height * 0.65);
        dialogWindow.setAttributes(lp);
    }

    private void setlistviewdata() {
        if (systemConfig.getPostId().equals("130") || systemConfig.getPostId().equals("134")) {
            testlistviewdata = dbOpenHelper.queryListMap("select * from ExamNotify", null);
        } else {
            testlistviewdata = dbOpenHelper.queryListMap("select * from ExamNotify where PostId = ? ", new String[]{systemConfig.getUserPostId()});
        }
        if (testlistviewdata.size() != 0) {
            Collections.reverse(testlistviewdata);
            vocationaladapter = new CommonAdapter<Map>(getActivity(), testlistviewdata, R.layout.vocational_list_item) {
                @Override
                protected void convertlistener(final ViewHolder holder, final Map map) {
                    int passscore = Integer.parseInt(map.get("PassScore") + "");
                    int testtimes = Integer.parseInt(map.get("ResitCount") + "") + 1;
                    int score = 0;
                    List<Map> testHistory = dbOpenHelper.queryListMap("select * from ExamRecords where ExamNotifyId=? and PersionId=? order by Score desc",
                            new String[]{map.get("Id") + "", personId});

                    //考试通知已过期的不能参加考试
                    final long currenttime = System.currentTimeMillis();
                    final long timelimit = Long.valueOf(dateToStamp(map.get("EndTime").toString()));
                    if (testHistory.size() != 0) {
                        holder.setText(R.id.volist_item5, testHistory.size() + "");
                        if (testHistory.size() == 1) {
                            score = Integer.parseInt(testHistory.get(0).get("Score") + "");
                            holder.setText(R.id.volist_item3, testHistory.get(0).get("Score") + "");
                            if (passscore > score && testHistory.size() < testtimes) {
                                if (currenttime > timelimit) {
                                    holder.setText(R.id.volist_item6, "已过截止日期");
                                    holder.getView(R.id.volist_item6).setEnabled(false);
                                } else {
                                    holder.setText(R.id.volist_item6, "马上参加");
                                    holder.getView(R.id.volist_item6).setEnabled(true);
                                    holder.getView(R.id.volist_item6).setOnClickListener(new View.OnClickListener() {

                                        @Override
                                        public void onClick(View view) {
                                            //设置dialog要传递的考试分类
                                            String texttitle = map.get("ExamName") + "";//考试名称
                                            String timeLimit = map.get("TimeLimit") + "";//考试时长
                                            String passScore = map.get("PassScore") + "";//考试通过分数
                                            String questionCount = map.get("QuestionCount") + "";//总题数
                                            String testtimes = (String) holder.getTextview(R.id.volist_item5).getText();//已考次数
                                            String historyHightestscore = (String) holder.getTextview(R.id.volist_item3).getText();//最高分数
                                            String NotifyId = map.get("Id") + "";
                                            String examTypeId = map.get("ExamTypeId") + "";
                                            setdialog(texttitle, timeLimit, passScore, questionCount, map.get("ExamFilesId") + "", testtimes, historyHightestscore, NotifyId, examTypeId);
                                        }
                                    });
                                }
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
                            holder.setText(R.id.volist_item3, testHistory.get(0).get("Score") + "");
                            if (passscore > score && testHistory.size() < testtimes) {
                                //考试通知已过期的不能参加考
                                if (currenttime > timelimit) {
                                    holder.setText(R.id.volist_item6, "已过截止日期");
                                    holder.getView(R.id.volist_item6).setEnabled(false);
                                    return;
                                } else {
                                    holder.setText(R.id.volist_item6, "马上参加");
                                    holder.getView(R.id.volist_item6).setEnabled(true);
                                    holder.getView(R.id.volist_item6).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            //设置dialog要传递的考试分类
                                            String texttitle = map.get("ExamName") + "";//考试名称
                                            String timeLimit = map.get("TimeLimit") + "";//考试时长
                                            String passScore = map.get("PassScore") + "";//考试通过分数
                                            String questionCount = map.get("QuestionCount") + "";//总题数
                                            String testtimes = (String) holder.getTextview(R.id.volist_item5).getText();//已考次数
                                            String historyHightestscore = (String) holder.getTextview(R.id.volist_item3).getText();//最高分数
                                            String NotifyId = map.get("Id") + "";
                                            String examTypeId = map.get("ExamTypeId") + "";
                                            setdialog(texttitle, timeLimit, passScore, questionCount, map.get("ExamFilesId") + "", testtimes, historyHightestscore, NotifyId, examTypeId);
                                        }
                                    });
                                }
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
                        //第一次参加考试
                        holder.setText(R.id.volist_item3, 0 + "");
                        holder.setText(R.id.volist_item5, 0 + "");
                        if (currenttime > timelimit) {
                            holder.setText(R.id.volist_item6, "已过截止日期");
                            holder.getView(R.id.volist_item6).setEnabled(false);
                        } else {
                            holder.setText(R.id.volist_item6, "马上参加");
                            holder.getView(R.id.volist_item6).setEnabled(true);
                            holder.getView(R.id.volist_item6).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    //设置dialog要传递的考试分类
                                    String texttitle = map.get("ExamName") + "";//考试名称
                                    String timeLimit = map.get("TimeLimit") + "";//考试时长
                                    String passScore = map.get("PassScore") + "";//考试通过分数
                                    String questionCount = map.get("QuestionCount") + "";//总题数
                                    String testtimes = (String) holder.getTextview(R.id.volist_item5).getText();//已考次数
                                    String historyHightestscore = (String) holder.getTextview(R.id.volist_item3).getText();//最高分数
                                    String NotifyId = map.get("Id") + "";
                                    String examTypeId = map.get("ExamTypeId") + "";
                                    setdialog(texttitle, timeLimit, passScore, questionCount, map.get("ExamFilesId") + "", testtimes, historyHightestscore, NotifyId, examTypeId);
                                }
                            });
                        }
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

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            if (testlistviewdata.size() > 0) {
                testlistviewdata.clear();
                setlistviewdata();
            }
        }
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
}