package zj.com.mc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import DBUtils.DBOpenHelper;
import Utils.CustomDialog;
import Utils.NetUtils;
import config.ISystemConfig;
import config.SystemConfigFactory;

/**
 * 考试界面
 */
public class NormalTestPage extends Activity implements View.OnClickListener {
    //定时交卷
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String h = null, m = null, c = null;
            super.handleMessage(msg);
            currenttime = msg.what + currenttime;

            int hour = (int) (currenttime / 3600);
            int min = (int) ((currenttime % 3600) / 60);
            if (hour < 10) {
                if (hour == 0) {
                    h = "00";
                } else
                    h = "0" + hour;
            } else {
                h = hour + "";
            }
            if (min < 10) {
                if (min == 0) {
                    m = "00";
                } else
                    m = "0" + min;
            }

            if (currenttime % 60 < 10) {
                if (currenttime % 60 == 0) {
                    c = "00";
                } else
                    c = "0" + currenttime % 60;
            } else {
                c = currenttime % 60 + "";
            }

            if (currenttime < maxTimes) {
                normaltesttime.setText(h + ":" + m + ":" + c);
            } else {
                normaltesttime.setText("00" + ":" + "00" + ":" + "00");
                timer1.cancel();
                if (currentnum < maxcount) {
                    for (int i = currentnum; i < maxcount; i++) {
                        List list = new ArrayList();
                        list.add("-1");
                        answermap.put(listKeyId.get(i) + "", list);
                        wrongAnswermap.put(listKeyId.get(i) + "", "未选择");
                    }
                }
                getPersonChoses(systemAnswers, answermap, listKeyId);
                savaError(erroranswerIds);
                setdialog();
                dialognormal.show();
            }
        }
    };

    private TextView normaltesttitle, normaltesttime, testnumber;
    private DBOpenHelper dbOpenHelper;
    private Button downbutton;
    private LinearLayout vp, test_l1, test_l2, test_l3, test_l4, search_file_back;
    private long currenttime, maxTimes;
    private int currentnum, maxcount;//当前页面编号，总题目编号；
    private Timer timer1;
    private CustomDialog dialognormal;//
    private String title;
    private TextView answer_a, answer_b, answer_c, answer_d, answer_ia, answer_ib, answer_ic, answer_id, test_leixing, test_leixing2;
    private TextView chose_a, chose_b, chose_c, chose_d;
    private String filetypeid;//题目类型
    private List<Map> listmaptype, testlistdata;
    private Map<String, List> answermap;//答题答案
    private Map<String, List> systemAnswers;//正确答案
    private List<TextView> listabcd, listchoseanswers, listansweric, listline;//abcd集合，abcd选项集合，对勾集合；
    private List<LinearLayout> listlayoutchose;
    private List<String> listKeyId;//key  题目Id
    private List<String> erroranswerIds;//错题id
    private String str;//查询语句
    private TextView b_line, a_line, c_line, d_line;
    private int type;//考试类型
    private String personId;
    private String notifyId;//ExamTypeId 业务学传的值
    private String examTypeId;
    private int passscore;
    private List<Map> examFilesList;//考试文件
    private String examTableNames;
    private StringBuilder answerSB;
    private Map<String, String> wrongAnswermap = new HashMap<>();//错题答案
    private boolean isSelect = false;
    private ISystemConfig systemConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_exams_single);

        systemConfig = SystemConfigFactory.getInstance(this).getSystemConfig();
        personId = systemConfig.getUserId();
        Myapplilcation.addActivity(this);

        Intent intentget = getIntent();
        Bundle bundleget = intentget.getBundleExtra("bundle");
        title = (String) bundleget.get("title");
        type = bundleget.getInt("type");
        filetypeid = bundleget.getString("Id");
        notifyId = bundleget.getString("notifyId");
        examTypeId = bundleget.getString("ExamTypeId");

        if (type == 1) {//正式考试
            maxTimes = Integer.parseInt(bundleget.get("timelilmit") + "") * 60;
            maxcount = Integer.parseInt(bundleget.get("allcount") + "");
            initnorlistmaptype(maxcount);
            bundleget.getString("notifyId");
            passscore = Integer.parseInt(bundleget.getString("PassScore"));
        } else if (type == 2) {//岗前答题
            maxcount = bundleget.getInt("QuestionNum");
            initnorlistmaptype(maxcount);
            maxTimes = maxcount * 60;
            passscore = 80;
            recordOperateLog(6, "岗前答题: " + title + " 时长" + maxTimes + "秒");
        } else if (type == 3) {//模拟练习
            maxcount = bundleget.getInt("QuestionNum");
            initnorlistmaptype(maxcount);
            maxTimes = maxcount * 60;
            passscore = 80;
            recordOperateLog(7, "模拟练习: " + title + " 时长" + maxTimes + "秒");
        }
        initnormaltest();
        settimer();
        setchose();
    }

    private void recordOperateLog(int LogType, String LogContent) {
        Date dt = new Date();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String nowTime = df.format(dt);
        dbOpenHelper.insert("AppOperateLog", new String[]{"LogType", "LogContent", "OperatorId", "DeviceId", "AddTime"},
                new Object[]{LogType, LogContent, systemConfig.getOperatorId(), 0, nowTime});
    }

    private void initnormaltest() {
        currentnum = 0;
        currenttime = 0;
        answermap = new HashMap();
        normaltesttitle = (TextView) findViewById(R.id.daily_normaltest_title);//标题
        normaltesttitle.setText(title);
        test_leixing = (TextView) findViewById(R.id.test_leixing);//类型
        test_leixing2 = (TextView) findViewById(R.id.test_leixing2);
        normaltesttime = (TextView) findViewById(R.id.normaltest_time);
        testnumber = (TextView) findViewById(R.id.normaltest_number);
        testnumber.setText(1 + "/" + maxcount);
        findViewById(R.id.normaltest_upbutton).setOnClickListener(this);//上一题
        downbutton = (Button) findViewById(R.id.normaltest_downbutton);//下一题
        downbutton.setOnClickListener(this);//下一题
        vp = (LinearLayout) findViewById(R.id.viewpager_layout);

        a_line = (TextView) findViewById(R.id.a_line);
        b_line = (TextView) findViewById(R.id.b_line);
        c_line = (TextView) findViewById(R.id.c_line);
        d_line = (TextView) findViewById(R.id.d_line);

        listline = new ArrayList<>();
        listline.add(a_line);
        listline.add(b_line);
        listline.add(c_line);
        listline.add(d_line);

        search_file_back = (LinearLayout) findViewById(R.id.search_file_back);
        search_file_back.setOnClickListener(this);
        test_l1 = (LinearLayout) findViewById(R.id.test_l1);//第一题layout
        test_l1.setOnClickListener(this);
        test_l2 = (LinearLayout) findViewById(R.id.test_l2);//第二题layout
        test_l2.setOnClickListener(this);
        test_l3 = (LinearLayout) findViewById(R.id.test_l3);//第三题layout
        test_l3.setOnClickListener(this);
        test_l4 = (LinearLayout) findViewById(R.id.test_l4);//第四题layout
        test_l4.setOnClickListener(this);
        listlayoutchose = new ArrayList<LinearLayout>();
        listlayoutchose.add(test_l1);
        listlayoutchose.add(test_l2);
        listlayoutchose.add(test_l3);
        listlayoutchose.add(test_l4);

        answer_a = (TextView) findViewById(R.id.answer_a);//选项A的内容
        chose_a = (TextView) findViewById(R.id.chose_a);
        answer_b = (TextView) findViewById(R.id.answer_b);//选项B
        chose_b = (TextView) findViewById(R.id.chose_b);
        answer_c = (TextView) findViewById(R.id.answer_c);//选项C
        chose_c = (TextView) findViewById(R.id.chose_c);
        answer_d = (TextView) findViewById(R.id.answer_d);//选项D
        chose_d = (TextView) findViewById(R.id.chose_d);
        listabcd = new ArrayList<TextView>();
        listabcd.add(chose_a);
        listabcd.add(chose_b);
        listabcd.add(chose_c);
        listabcd.add(chose_d);
        listchoseanswers = new ArrayList<TextView>();
        listchoseanswers.add(answer_a);
        listchoseanswers.add(answer_b);
        listchoseanswers.add(answer_c);
        listchoseanswers.add(answer_d);

        answer_ia = (TextView) findViewById(R.id.answer_ia);//选项A对勾
        answer_ib = (TextView) findViewById(R.id.answer_ib);//选项B
        answer_ic = (TextView) findViewById(R.id.answer_ic);//选项C
        answer_id = (TextView) findViewById(R.id.answer_id);//选项D
        listansweric = new ArrayList<TextView>();
        listansweric.add(answer_ia);
        listansweric.add(answer_ib);
        listansweric.add(answer_ic);
        listansweric.add(answer_id);

        if (type == 1) {
            search_file_back.setVisibility(View.GONE);
        }
    }

    TimerTask task = new TimerTask() {
        public void run() {
            Message message = new Message();
            message.what = 1;
            handler.sendMessage(message);
        }
    };

    private void settimer() {
        timer1 = new Timer(true);
        timer1.schedule(task, 1000, 1000);
    }

    private void setchose() {
        String chosetype = testlistdata.get(currentnum).get("AnswerType") + "";
        String questbody = testlistdata.get(currentnum).get("Question") + "";
        String singlequestId = testlistdata.get(currentnum).get("Id") + "";

        if (chosetype.equals("1")) {
            test_leixing.setText("【" + "单项选择" + "】");
        } else if (chosetype.equals("2")) {
            test_leixing.setText("【" + "多项选择" + "】");
        } else if (chosetype.equals("3")) {
            test_leixing.setText("【" + "判断" + "】");
        }

        test_leixing2.setText(questbody);
        List<Map> choseslist = dbOpenHelper.queryListMap("select * from ExamAnswers where QuestionId=?", new String[]{singlequestId});

        for (int i = 0; i < 4; i++) {
            listabcd.get(i).setTextColor(this.getResources().getColor(R.color.sblack));
            listchoseanswers.get(i).setTextColor(this.getResources().getColor(R.color.sblack));
            listline.get(i).setBackgroundColor(this.getResources().getColor(R.color.llblack));
            listansweric.get(i).setVisibility(View.GONE);
        }

        if (choseslist.size() == 1) {
            test_l1.setVisibility(View.VISIBLE);
            test_l2.setVisibility(View.GONE);
            test_l3.setVisibility(View.GONE);
            test_l4.setVisibility(View.GONE);

            a_line.setVisibility(View.VISIBLE);
            b_line.setVisibility(View.GONE);
            c_line.setVisibility(View.GONE);
            d_line.setVisibility(View.GONE);
            answer_a.setText(choseslist.get(0).get("Answer") + "");

        } else if (choseslist.size() == 2) {
            test_l1.setVisibility(View.VISIBLE);
            test_l2.setVisibility(View.VISIBLE);
            test_l3.setVisibility(View.GONE);
            test_l4.setVisibility(View.GONE);
            a_line.setVisibility(View.VISIBLE);
            b_line.setVisibility(View.VISIBLE);
            c_line.setVisibility(View.GONE);
            d_line.setVisibility(View.GONE);
            answer_a.setText(choseslist.get(0).get("Answer") + "");
            answer_b.setText(choseslist.get(1).get("Answer") + "");
        } else if (choseslist.size() == 3) {
            test_l1.setVisibility(View.VISIBLE);
            test_l2.setVisibility(View.VISIBLE);
            test_l3.setVisibility(View.VISIBLE);
            test_l4.setVisibility(View.GONE);
            a_line.setVisibility(View.VISIBLE);
            b_line.setVisibility(View.VISIBLE);
            c_line.setVisibility(View.VISIBLE);
            d_line.setVisibility(View.GONE);
            answer_a.setText(choseslist.get(0).get("Answer") + "");
            answer_b.setText(choseslist.get(1).get("Answer") + "");
            answer_c.setText(choseslist.get(2).get("Answer") + "");
        } else if (choseslist.size() == 4) {
            test_l1.setVisibility(View.VISIBLE);
            test_l2.setVisibility(View.VISIBLE);
            test_l3.setVisibility(View.VISIBLE);
            test_l4.setVisibility(View.VISIBLE);
            a_line.setVisibility(View.VISIBLE);
            b_line.setVisibility(View.VISIBLE);
            c_line.setVisibility(View.VISIBLE);
            d_line.setVisibility(View.VISIBLE);
            answer_a.setText(choseslist.get(0).get("Answer") + "");
            answer_b.setText(choseslist.get(1).get("Answer") + "");
            answer_c.setText(choseslist.get(2).get("Answer") + "");
            answer_d.setText(choseslist.get(3).get("Answer") + "");
        }
    }

    private void initnorlistmaptype(int t) {
        dbOpenHelper = DBOpenHelper.getInstance(getApplicationContext());
        str = "select * from ExamQuestion where ExamFileId=?";
        examFilesList = new ArrayList<>();
        listmaptype = new ArrayList<>();
        testlistdata = new ArrayList<>();
        listKeyId = new ArrayList<String>();
        systemAnswers = new HashMap<String, List>();

        listmaptype = dbOpenHelper.queryListMap(str, new String[]{filetypeid});
        examFilesList = dbOpenHelper.queryListMap("select * from ExamFiles where Id=?", new String[]{filetypeid});
        if (examFilesList.size() != 0) {
            examTableNames = examFilesList.get(0).get("FileName").toString();
        }
        testlistdata = gettestitemlist(listmaptype, t);

        for (int i = 0; i < testlistdata.size(); i++) {
            listKeyId.add(testlistdata.get(i).get("Id") + "");
        }

        for (int i = 0; i < listKeyId.size(); i++) {//list中的Answer-->
            List<Map> list = dbOpenHelper.queryListMap("select * from ExamAnswers where QuestionId=? and IsRight=?", new String[]{listKeyId.get(i) + "", "1"});
            List listchose = new ArrayList();
            for (int j = 0; j < list.size(); j++) {
                listchose.add(list.get(j).get("Id"));//listchose只存答案的ID
            }
            systemAnswers.put(listKeyId.get(i) + "", listchose);//
        }
    }

    //随机抽取题
    private List<Map> gettestitemlist(List<Map> list, int allcount) {
        // 用于存放所取的值的数组
        List testdata = new ArrayList<Map>();
        int number = list.size();// 控制随机数产生的范围
        for (int j = 0; j < allcount && (j < list.size()); j++) {
            int index = (int) (Math.random() * number);// 产生一个随机数作为索引
            testdata.add(list.get(index));
            list.remove(index);// 移除已经取过的元素
            number--;// 将随机数范围缩小1
        }
        return testdata;
    }

    //设置返回键失效
    @Override
    public void onBackPressed() {
        if (type == 1) {
            //super.onBackPressed();
            timer1.cancel();
        } else {
            super.onBackPressed();
            //savaError(erroranswerIds);
            timer1.cancel();
            finish();
        }
    }

    //设置点击事件
    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.normaltest_upbutton:
                //上一题
                if (currentnum == 0) {
                    UtilisClass.showToast(NormalTestPage.this, "已是第一页");
                } else {
                    //初始化选项
                    addanswertolist();
                    //进行换页
                    currentnum = currentnum - 1;
                    testnumber.setText(currentnum + 1 + "/" + maxcount);
                    getupanswer(answermap, listKeyId);
                }
                break;
            case R.id.normaltest_downbutton:
                //下一题
                if (currentnum == maxcount - 1) {
                    addanswertolist();
                    //交卷    systemAnswers正确答案  answermap 答题答案 listKeyId只存了题目的ID
                    getPersonChoses(systemAnswers, answermap, listKeyId);
                    setdialog();
                    savaError(erroranswerIds);
                    dialognormal.show();

                } else {
                    addanswertolist();//保存到集合
                    //进行换页
                    getupanswer(answermap, listKeyId);
                    currentnum = currentnum + 1;
                    testnumber.setText(currentnum + 1 + "/" + maxcount);
                    setchose();
                    downbutton.setText("下一题");
                }
                break;
            case R.id.test_l1:
                //A选项
                isSelect = true;
                setChoseType(chose_a, answer_a, answer_ia, a_line);
                break;
            case R.id.test_l2:
                //B选项
                isSelect = true;
                setChoseType(chose_b, answer_b, answer_ib, b_line);
                break;
            case R.id.test_l3:
                //C选项
                isSelect = true;
                setChoseType(chose_c, answer_c, answer_ic, c_line);
                break;
            case R.id.test_l4:
                //D选项
                isSelect = true;
                setChoseType(chose_d, answer_d, answer_id, d_line);
                break;
            case R.id.search_file_back:
                //savaError(erroranswerIds);
                finish();
                break;
        }
        if (currentnum == maxcount - 1) {
            downbutton.setText("交卷");
        } else {
            downbutton.setText("下一题");
        }
    }

    //设置选择方式即单选还是多选
    private void setChoseType(TextView chosetag, TextView choseContext, TextView textView, TextView line) {
        answerSB = new StringBuilder();
        String singlequestId = testlistdata.get(currentnum).get("Id") + "";
        String chosetype = testlistdata.get(currentnum).get("AnswerType") + "";
        List<Map> choseslist = dbOpenHelper.queryListMap("select * from ExamAnswers where QuestionId=?", new String[]{singlequestId});
        if (chosetype.equals("2")) {
            settesttag(chosetag, choseContext, textView, line);
        } else {
            for (int i = 0; i < 4; i++) {
                listabcd.get(i).setTextColor(this.getResources().getColor(R.color.sblack));
                listchoseanswers.get(i).setTextColor(this.getResources().getColor(R.color.sblack));
                listline.get(i).setBackgroundColor(this.getResources().getColor(R.color.llblack));
                listansweric.get(i).setVisibility(View.GONE);
            }
            chosetag.setTextColor(this.getResources().getColor(R.color.colorblue2));
            choseContext.setTextColor(this.getResources().getColor(R.color.colorblue2));
            line.setBackgroundColor(this.getResources().getColor(R.color.colorblue2));
            textView.setVisibility(View.VISIBLE);
        }
        //选择答案记录
        answerSB.append(chosetag.getText().toString());
    }

    //设置按钮对勾显示
    private void settesttag(TextView chosetag, TextView choseContext, TextView textView, TextView line) {
        if (textView.getVisibility() == View.GONE) {
            chosetag.setTextColor(this.getResources().getColor(R.color.colorblue2));
            choseContext.setTextColor(this.getResources().getColor(R.color.colorblue2));
            line.setBackgroundColor(this.getResources().getColor(R.color.colorblue2));
            textView.setVisibility(View.VISIBLE);
        } else {
            chosetag.setTextColor(this.getResources().getColor(R.color.sblack));
            choseContext.setTextColor(this.getResources().getColor(R.color.sblack));
            line.setBackgroundColor(this.getResources().getColor(R.color.llblack));
            textView.setVisibility(View.GONE);
        }
    }

    //加入集合 person答案
    private void addanswertolist() {

        List list = new ArrayList();
        String singlequestId = testlistdata.get(currentnum).get("Id") + "";    //题号
        //获取的答案选项
        List<Map> choseslist = dbOpenHelper.queryListMap("select * from ExamAnswers where QuestionId=?", new String[]{singlequestId});
        for (int i = 0; i < 4; i++) {
            if (listansweric.get(i).getVisibility() == View.VISIBLE) {
                list.add(choseslist.get(i).get("Id"));
            }
        }

        answermap.put(listKeyId.get(currentnum) + "", list);

        //把用户选择的ABCD记录
        if (answerSB == null || !isSelect) {
            wrongAnswermap.put(listKeyId.get(currentnum) + "", "未选择");
        } else {
            wrongAnswermap.put(listKeyId.get(currentnum) + "", answerSB.toString());
            isSelect = false;
        }
    }

    //答题结束时弹出
    private void setdialog() {
        dialognormal = new CustomDialog(NormalTestPage.this, R.style.mydialog);
        View contentView = LayoutInflater.from(NormalTestPage.this).inflate(R.layout.formal_exams_result, null);
        dialognormal.setContentView(contentView);
        dialognormal.setCanceledOnTouchOutside(false);

        Window dialogWindow = dialognormal.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        lp.width = (int) (width * 0.5);
        lp.height = (int) (height * 0.6);
        dialogWindow.setAttributes(lp);

        TextView qustrall = (TextView) contentView.findViewById(R.id.qustrall);//总答题数
        TextView right_count = (TextView) contentView.findViewById(R.id.right_count);//答对题数
        TextView problem_count = (TextView) contentView.findViewById(R.id.problem_count);//答错题述
        TextView answerok = (TextView) contentView.findViewById(R.id.accuracys);//正确率
        TextView teststop_content = (TextView) contentView.findViewById(R.id.teststop_content);//正确操作
        qustrall.setText(listKeyId.size() + "");
        String errortestnum = "";
        String goodtestnum = "";

        if (listKeyId.size() != 0 && erroranswerIds.size() != 0) {
            right_count.setText((listKeyId.size() - erroranswerIds.size()) + "道");
            problem_count.setText(erroranswerIds.size() + "道");
            goodtestnum = erroranswerIds.size() + "道";
            errortestnum = (listKeyId.size() - erroranswerIds.size() + "");
            int accuracys = listKeyId.size() - erroranswerIds.size();
            if (accuracys != 0) {
                answerok.setText(accuracys * 100 / listKeyId.size() + "分");
                if (type != 1) {
                    teststop_content.setText("");
                } else {
                    if (accuracys * 100 / listKeyId.size() > passscore) {
                        teststop_content.setText("通过考试");
                    } else {
                        teststop_content.setText("未能通过考试");
                    }
                }
            } else {
                answerok.setText("0");
                if (type != 1) {
                    teststop_content.setText("");
                } else {
                    teststop_content.setText("未能通过考试");
                }
            }
        }

        if (type == 1) {
            String data = UtilisClass.getStringDate3();
            String usetime = currenttime / 60 + "";
            String getscore = "";
            int accuracys = listKeyId.size() - erroranswerIds.size();
            if (accuracys != 0) {
                getscore = accuracys * 100 / listKeyId.size() + "";
            } else {
                getscore = "0";
            }
            dbOpenHelper.insert("ExamRecords", new String[]{"ExamNotifyId", "PersionId", "RightCount", "WrongCount", "ExamTime", "TimeSpends", "Score", "IsUploaded"
            }, new Object[]{notifyId, personId, errortestnum, goodtestnum, data, usetime, getscore, 0});

        }

        final Button button = (Button) contentView.findViewById(R.id.stoptest_ok);
        final Button upload = (Button) contentView.findViewById(R.id.upload_result);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NormalTestPage.this.finish();
                dialognormal.dismiss();
                timer1.cancel();
            }
        });
        if (type != 1) {
            upload.setVisibility(View.GONE);
        } else {
            upload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Myapplilcation.getExecutorService().execute(new Runnable() {
                        @Override
                        public void run() {
                            String sql = "select * from ExamRecords where Id = (select max(Id) from ExamRecords)";
                            List<Map> list = dbOpenHelper.queryListMap(sql, null);
                            NetUtils.updataarguments3dingle(dbOpenHelper, systemConfig, "ExamRecords", list.get(0).get("Id").toString());
                        }
                    });

                    NormalTestPage.this.finish();
                    dialognormal.dismiss();
                    timer1.cancel();
                }
            });
        }
        return;
    }

    /**
     * @param keylist mapkey键
     * @param m2      person答案
     * @param m1      正确答案
     */
    //systemAnswers正确答案  answermap 答题答案 listKeyId只存了题目的ID
    private void getPersonChoses(Map<String, List> m1, Map<String, List> m2, List keylist) {
        erroranswerIds = new ArrayList<String>();//错题ID
        for (int num = 0; num < keylist.size(); num++) {
            List<Object> list1 = m1.get(keylist.get(num) + "");
            List<Object> list2 = m2.get(keylist.get(num) + "");
            int count = 0;
            for (int i = 0; i < list1.size(); i++) {
                String systemanswer = list1.get(i) + "";
                for (int j = 0; j < list2.size(); j++) {
                    String personanswer = list2.get(j) + "";
                    if (systemanswer.equals(personanswer)) {
                        count++;
                        wrongAnswermap.remove(keylist.get(num) + "");//错题集合
                    }
                }
            }
            if (count != list1.size()) {
                erroranswerIds.add(keylist.get(num) + "");
            }
        }
    }

    //上一题设置选项  keylist题目ID集合    answermap 键为 题目ID 值选择的 答案
    private void getupanswer(Map answermap, List keylist) {
        //answermap  person选择集合      keylist 当前题目key键集合
        List list = new ArrayList();
        list = (List) answermap.get(keylist.get(currentnum));
        if (list.size() != 0) {
            setchose();
            for (int i = 0; i < list.size(); i++) {
                for (int j = 0; j < 4; j++) {
                    if (list.get(i).equals(listchoseanswers.get(j).getText() + "")) {
                        listabcd.get(j).setTextColor(this.getResources().getColor(R.color.colorblue2));
                        listchoseanswers.get(j).setTextColor(this.getResources().getColor(R.color.colorblue2));
                        listansweric.get(j).setVisibility(View.VISIBLE);
                    }
                }
            }
        } else {
            setchose();
        }
    }

    private void savaError(List<String> list) {
        List<Map> questionidlist = new ArrayList<Map>();
        for (int i = 0; i < list.size(); i++) {
            String questionid = list.get(i) + "";
            questionidlist = dbOpenHelper.queryListMap("select * from ExamErrorQuestions where QuestionId=? and PersionId=?",
                    new String[]{questionid, personId});
            String wrongstr;
            if (wrongAnswermap.get(questionid) != null) {
                wrongstr = wrongAnswermap.get(questionid).toString();
            } else {
                wrongstr = "未选择";
            }
            if (questionidlist.size() == 0) {
                //HasRemembered字段 当成记录错误选项
                dbOpenHelper.insert("ExamErrorQuestions", new String[]{"QuestionId", "ErrorCount", "PersionId", "HasRemembered", "TableNamessss"},
                        new Object[]{Integer.parseInt(list.get(i)), "1", personId, wrongstr, examTableNames});
                if (dialognormal != null) {
                    if (dialognormal.isShowing()) {
                        dialognormal.dismiss();
                    }
                }
            } else {
                int errorcount = Integer.parseInt(questionidlist.get(0).get("ErrorCount") + "");
                errorcount = errorcount + 1;
                dbOpenHelper.update("ExamErrorQuestions", new String[]{"ErrorCount", "HasRemembered"},
                        new Object[]{errorcount, wrongstr}, new String[]{"Id"}, new String[]{questionid});
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Myapplilcation.removeActivity(this);
    }
}