package zj.com.mc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import DBUtils.DBOpenHelper;
import Utils.CustomDialog;

/**
 * Created by dell on 2016/8/2.
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
                if (hour==0){
                    h="00";
                }else
                h = "0" + hour;
            } else {
                h=hour+"";
            }
            if (min < 10) {
                if (min==0){
                    m="00";
                }else
                m = "0" + min;
            } else {
            }
            if (currenttime % 60 < 10) {
                if (currenttime % 60==0){
                    c="00";
                }else
                c = "0" + currenttime % 60;
            } else {
                c=currenttime % 60+"";
            }

            if (currenttime < maxTimes) {
                normaltesttime.setText(h + ":" + m + ":" + c);

            } else {
                normaltesttime.setText("00" + ":" + "00" + ":" + "00");
                timer1.cancel();
                setdialog();
                dialognormal.show();
            }
        }
    };


    private TextView normaltesttitle, normaltesttime, testnumber;
    private DBOpenHelper dbOpenHelper;
    private Button downbutton;
    private LinearLayout vp, test_l1, test_l2, test_l3, test_l4;
    private long currenttime,maxTimes;
    private int currentnum, maxcount;//当前页面编号，总题目编号；
    private Timer timer1;
    private CustomDialog dialognormal;//
    private String title, typename;
    private TextView answer_a, answer_b, answer_c, answer_d, answer_ia, answer_ib, answer_ic, answer_id, test_leixing, test_leixing2;
    private TextView  chose_a,chose_b,chose_c,chose_d;
    private String filetypeid;//题目类型
    private List<Map> listmaptype, testlistdata;//单种类实体总共集合，考试随机20道题目；
    private Map<String,List> answermap;//答题答案
    private Map<String,List> answererrormap;//答题答错
    private Map<String,List> systemAnswers;//正确答案
    private List<Map<String,List>> errorlist;//错题
    private List<TextView> listabcd,listchoseanswers,listansweric,listline;//abcd集合，abcd选项集合，对勾集合；
    private  List<LinearLayout> listlayoutchose;
    private List<String> listKeyId;//key  即使Id
    private List<String> erroranswerIds;//错题id
    private String str;//查询语句
    private TextView b_line,a_line,c_line,d_line;
    private int t;//考试类型
    private SharedPreferences s;
    private String personId;
    private String notifyId;
    private int passscore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_exams_single);

        s= getSharedPreferences("PersonInfo", Context.MODE_PRIVATE);
        personId=s.getString("PersonId",null);

        Intent intentget = getIntent();
        Bundle bundleget = intentget.getBundleExtra("bundle");
        title = (String) bundleget.get("title");
        int t = bundleget.getInt("type");
        filetypeid =bundleget.getString("Id");
        notifyId=bundleget.getString("notifyId");

        if (t==-1) {
            maxcount=50;
            initnorlistmaptype(50);//总共题目数及50到考试题
            maxTimes=20*60;
            passscore=80;
        }else {
            maxTimes=Integer.parseInt(bundleget.get("timelilmit")+"")*60;
            maxcount= Integer.parseInt(bundleget.get("allcount")+"");
            Log.i("sssssssssssssssssss",  +maxcount+"   ");
            initnorlistmaptype(maxcount);//总共题目数及40到考试题
            bundleget.getString("notifyId");
//            initnorlistmaptype(50);//总共题目数及40到考试题
            passscore=Integer.parseInt(bundleget.getString("PassScore"));
        }

        initnormaltest();//初始化view
        settimer();//开启定时
        setchose();


    }

    //正式考试总共题目数   及    练习考试题目数
    private void initnorlistmaptype(int t) {
        dbOpenHelper = DBOpenHelper.getInstance(getApplicationContext());


        str = "select * from ExamQuestion where ExamFileId=?";
        listmaptype = dbOpenHelper.queryListMap(str, new String[]{filetypeid});

        testlistdata = gettestitemlist(listmaptype, t);
        listKeyId=new ArrayList<String>();
        for (int i=0; i<testlistdata.size();i++) {
            listKeyId.add(testlistdata.get(i).get("Id")+"");
        }
        //正确答案
        systemAnswers=new HashMap<String,List>();
        for (int i=0; i<listKeyId.size(); i++){
            List<Map> list=dbOpenHelper.queryListMap("select * from ExamAnswers where QuestionId=? and IsRight=?",new String[]{listKeyId.get(i)+"","1"});
            List listchose=new ArrayList();
            for (int j=0; j<list.size();j++ ){
                listchose.add(list.get(j).get("Id"));
            }
            systemAnswers.put(listKeyId.get(i)+"",listchose);
        }
//        Log.i("sssssssssslistKeyId", systemAnswers.size()+"  "
//                +String.valueOf(systemAnswers)+"   ");
    }

    //随机抽取题
    private List<Map> gettestitemlist(List<Map> list,int allcount) {
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

    //初始化view
    private void initnormaltest() {
        currentnum = 0;
        currenttime = 0;
        answermap=new HashMap();
        normaltesttitle = (TextView) findViewById(R.id.daily_normaltest_title);//标题
        normaltesttitle.setText(title);
        test_leixing = (TextView) findViewById(R.id.test_leixing);//类型
        test_leixing2 = (TextView) findViewById(R.id.test_leixing2);
        normaltesttime = (TextView) findViewById(R.id.normaltest_time);
        testnumber = (TextView) findViewById(R.id.normaltest_number);
        testnumber.setText(1+"/"+maxcount);
        findViewById(R.id.normaltest_upbutton).setOnClickListener(this);//上一题
        downbutton = (Button) findViewById(R.id.normaltest_downbutton);//下一题
        downbutton.setOnClickListener(this);//下一题
        vp = (LinearLayout) findViewById(R.id.viewpager_layout);

        a_line= (TextView) findViewById(R.id.a_line);
        b_line= (TextView) findViewById(R.id.b_line);
        c_line= (TextView) findViewById(R.id.c_line);
        d_line= (TextView) findViewById(R.id.d_line);

        listline=new ArrayList<>();
        listline.add(a_line);
        listline.add(b_line);
        listline.add(c_line);
        listline.add(d_line);


        test_l1 = (LinearLayout) findViewById(R.id.test_l1);//第一题layout
        test_l1.setOnClickListener(this);
        test_l2 = (LinearLayout) findViewById(R.id.test_l2);//第二题layout
        test_l2.setOnClickListener(this);
        test_l3 = (LinearLayout) findViewById(R.id.test_l3);//第三题layout
        test_l3.setOnClickListener(this);
        test_l4 = (LinearLayout) findViewById(R.id.test_l4);//第四题layout
        test_l4.setOnClickListener(this);
        listlayoutchose=new ArrayList<LinearLayout>();
        listlayoutchose.add(test_l1);
        listlayoutchose.add(test_l2);
        listlayoutchose.add(test_l3);
        listlayoutchose.add(test_l4);

        answer_a = (TextView) findViewById(R.id.answer_a);//选项A的内容
        chose_a= (TextView) findViewById(R.id.chose_a);
        answer_b = (TextView) findViewById(R.id.answer_b);//选项B
        chose_b= (TextView) findViewById(R.id.chose_b);
        answer_c = (TextView) findViewById(R.id.answer_c);//选项C
        chose_c= (TextView) findViewById(R.id.chose_c);
        answer_d = (TextView) findViewById(R.id.answer_d);//选项D
        chose_d= (TextView) findViewById(R.id.chose_d);
        listabcd=new ArrayList<TextView>();
        listabcd.add(chose_a);
        listabcd.add(chose_b);
        listabcd.add(chose_c);
        listabcd.add(chose_d);
        listchoseanswers=new ArrayList<TextView>();
        listchoseanswers.add(answer_a);
        listchoseanswers.add(answer_b);
        listchoseanswers.add(answer_c);
        listchoseanswers.add(answer_d);

        answer_ia = (TextView) findViewById(R.id.answer_ia);//选项A对勾
        answer_ib = (TextView) findViewById(R.id.answer_ib);//选项B
        answer_ic = (TextView) findViewById(R.id.answer_ic);//选项C
        answer_id = (TextView) findViewById(R.id.answer_id);//选项D
        listansweric=new ArrayList<TextView>();
        listansweric.add(answer_ia);
        listansweric.add(answer_ib);
        listansweric.add(answer_ic);
        listansweric.add(answer_id);
    }

//设置返回键失效

    @Override
    public void onBackPressed() {
//        if (t==-1) {
            super.onBackPressed();
            timer1.cancel();
//        }else {
//        }
    }

    //设置点击事件
    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.normaltest_upbutton:
                //上一页

                if (currentnum == 0) {
                    UtilisClass.showToast(NormalTestPage.this, "已是第一页");

                } else {
                    //初始化选项
                    addanswertolist();
//进行换页
                    currentnum = currentnum - 1;
                    testnumber.setText(currentnum+1+"/"+maxcount);
                    getupanswer(answermap,listKeyId);
                }
                break;
            case R.id.normaltest_downbutton:
                //下一页
                if (currentnum==maxcount-1){
                    addanswertolist();
                    //交卷
                    getPersonChoses(systemAnswers,answermap,listKeyId);

                    setdialog();//
                    savaError(erroranswerIds);
                    dialognormal.show();
//                    finish();
                }else {
                    addanswertolist();//保存到集合
//进行换页
                    getupanswer(answermap,listKeyId);
                    currentnum = currentnum + 1;
                    testnumber.setText(currentnum+1+"/"+maxcount);
                    setchose();
                        downbutton.setText("下一题");
                }
                break;



            case R.id.test_l1:
//A选项
                setChoseType(chose_a,answer_a,answer_ia,a_line);
//                settesttag(chose_a,answer_a,answer_ia,a_line);

                break;
            case R.id.test_l2:
//B选项
                setChoseType(chose_b,answer_b,answer_ib,b_line);
//                settesttag(chose_b,answer_b,answer_ib,b_line);
                break;
            case R.id.test_l3:
//C选项
                setChoseType(chose_c,answer_c,answer_ic,c_line);
//                settesttag(chose_c,answer_c,answer_ic,c_line);

                break;
            case R.id.test_l4:
//D选项
//                settesttag(chose_d,answer_d,answer_id,d_line);
                setChoseType(chose_d,answer_d,answer_id,d_line);

                break;

        }





        if (currentnum == maxcount-1) {
            downbutton.setText("交卷");
        }else {
            downbutton.setText("下一题");
        }
    }


    //设置选择方式即单选还是多选
    private void setChoseType(TextView chosetag,TextView choseContext,TextView textView,TextView line){
        String singlequestId = testlistdata.get(currentnum ).get("Id") + "";
        String chosetype = testlistdata.get(currentnum ).get("AnswerType") + "";
        List<Map> choseslist = dbOpenHelper.queryListMap("select * from ExamAnswers where QuestionId=?", new String[]{singlequestId});
        if (chosetype.equals("2")){
            settesttag( chosetag, choseContext, textView, line);
        }else {

            for (int i=0; i<4;i++ ) {
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
    }

    //设置按钮对勾显示
    private void settesttag(TextView chosetag,TextView choseContext,TextView textView,TextView line) {

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
    private void addanswertolist(){
        List list=new ArrayList();
        String singlequestId = testlistdata.get(currentnum ).get("Id") + "";
        List<Map> choseslist = dbOpenHelper.queryListMap("select * from ExamAnswers where QuestionId=?", new String[]{singlequestId});


//        System.out.println("<<<><>>>"+String.valueOf(choseslist));
        for (int i=0; i<4; i++){
            if (listansweric.get(i).getVisibility()==View.VISIBLE){
                list.add(choseslist.get(i).get("Id"));
            }else {}
        }
        answermap.put(listKeyId.get(currentnum)+"",list);
//        System.out.println("<<<><>>>"+String.valueOf(answermap));
    }


//设置选项内容
private void setchose() {
        String chosetype = testlistdata.get(currentnum ).get("AnswerType") + "";
        String questbody = testlistdata.get(currentnum ).get("Question") + "";
        String singlequestId = testlistdata.get(currentnum ).get("Id") + "";
        Log.i("testlistdata", String.valueOf(testlistdata));
        if (chosetype.equals("1")){
        test_leixing.setText("【"+"单项选择"+"】");
        }else if (chosetype.equals("2")){
            test_leixing.setText("【"+"多项选择"+"】");

        }else if (chosetype.equals("3")){
            test_leixing.setText("【"+"判断"+"】");

        }

        test_leixing2.setText(questbody);
        List<Map> choseslist = dbOpenHelper.queryListMap("select * from ExamAnswers where QuestionId=?", new String[]{singlequestId});

        Log.i("choseslist", String.valueOf(choseslist));



        for (int i=0; i<4;i++ ) {
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
            answer_a.setText(choseslist.get(0).get("Answer")+"");

        }else if (choseslist.size()==2){
            test_l1.setVisibility(View.VISIBLE);
            test_l2.setVisibility(View.VISIBLE);
            test_l3.setVisibility(View.GONE);
            test_l4.setVisibility(View.GONE);
            a_line.setVisibility(View.VISIBLE);
            b_line.setVisibility(View.VISIBLE);
            c_line.setVisibility(View.GONE);
            d_line.setVisibility(View.GONE);
            answer_a.setText(choseslist.get(0).get("Answer")+"");
            answer_b.setText(choseslist.get(1).get("Answer")+"");
        }else if (choseslist.size()==3){
            test_l1.setVisibility(View.VISIBLE);
            test_l2.setVisibility(View.VISIBLE);
            test_l3.setVisibility(View.VISIBLE);
            test_l4.setVisibility(View.GONE);
            a_line.setVisibility(View.VISIBLE);
            b_line.setVisibility(View.VISIBLE);
            c_line.setVisibility(View.VISIBLE);
            d_line.setVisibility(View.GONE);
            answer_a.setText(choseslist.get(0).get("Answer")+"");
            answer_b.setText(choseslist.get(1).get("Answer")+"");
            answer_c.setText(choseslist.get(2).get("Answer")+"");
        }else {
            test_l1.setVisibility(View.VISIBLE);
            test_l2.setVisibility(View.VISIBLE);
            test_l3.setVisibility(View.VISIBLE);
            test_l4.setVisibility(View.VISIBLE);
            a_line.setVisibility(View.VISIBLE);
            b_line.setVisibility(View.VISIBLE);
            c_line.setVisibility(View.VISIBLE);
            d_line.setVisibility(View.VISIBLE);
            answer_a.setText(choseslist.get(0).get("Answer")+"");
            answer_b.setText(choseslist.get(1).get("Answer")+"");
            answer_c.setText(choseslist.get(2).get("Answer")+"");
            answer_d.setText(choseslist.get(3).get("Answer")+"");
        }
    }




    //    定时器
    TimerTask task = new TimerTask() {
        public void run() {
            Message message = new Message();
            message.what = 1;
            handler.sendMessage(message);
        }
    };

//设置定时器
private void settimer() {
        timer1 = new Timer(true);
        timer1.schedule(task, 1000, 1000);
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

//        lp.x = (int) (100); // 新位置X坐标
//        lp.y = (int) (dm.heightPixels); // 新位置Y坐标
        lp.width = (int) (1000); // 宽度
        lp.height = (int) (1000 * 0.6); // 高度
//lp.alpha = 0.7f; // 透明度

        dialogWindow.setAttributes(lp);


        TextView stoptest_title = (TextView) contentView.findViewById(R.id.stoptest_title);//考试结束
        TextView qustrall = (TextView) contentView.findViewById(R.id.qustrall);//总答题数
        TextView right_count = (TextView) contentView.findViewById(R.id.right_count);//答对题数
        TextView problem_count = (TextView) contentView.findViewById(R.id.problem_count);//答错题述
        TextView answerok = (TextView) contentView.findViewById(R.id.accuracys);//正确率


        TextView teststop_content = (TextView) contentView.findViewById(R.id.teststop_content);//正确操作

        stoptest_title.setText("考试结束");


        qustrall.setText(listKeyId.size()+"");
        String errortestnum = "";
        String goodtestnum="";

        if (listKeyId.size()!=0&&erroranswerIds.size()!=0){
        right_count.setText((listKeyId.size()-erroranswerIds.size())+"");
        problem_count.setText(erroranswerIds.size()+"");
            goodtestnum=erroranswerIds.size()+"";
            errortestnum=(listKeyId.size()-erroranswerIds.size()+"");
            int accuracys=listKeyId.size()-erroranswerIds.size();
            if (accuracys!=0){

                answerok.setText(accuracys*100/listKeyId.size()+"");
                if (accuracys*100/listKeyId.size()>passscore) {
                    teststop_content.setText("你已经对此类精通了");
                }else {
                    teststop_content.setText("很遗憾，未能通过考试");
                }
            }else {
                answerok.setText("0");
                teststop_content.setText("很遗憾，未能通过考试");
            }
    }else {
        }
        if (t!=-1) {
        String data= UtilisClass.getStringDate3();
        String usetime=currenttime/60+"";
            String getscore="";
            int accuracys=listKeyId.size()-erroranswerIds.size();
            if (accuracys!=0){
                getscore=accuracys*100/listKeyId.size()+"";
            }else {
                getscore="0";
            }
                dbOpenHelper.insert("ExamRecords", new String[]{"ExamNotifyId", "PersionId", "RightCount", "WrongCount", "ExamTime", "TimeSpends","Score","IsUploaded"
                }, new Object[]{notifyId, personId, errortestnum, goodtestnum, data, usetime,getscore,1});
        }else {}
                final Button button = (Button) contentView.findViewById(R.id.stoptest_ok);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        NormalTestPage.this.finish();
                        dialognormal.dismiss();
                        timer1.cancel();
                    }
                });

                return;
            }

/**
 * @param keylist mapkey键
 * @param m2 person答案
 * @param m1 争确答案
 * */
private void getPersonChoses(Map<String,List> m1,Map<String,List> m2,List keylist){
//
                erroranswerIds=new ArrayList<String>();
                answererrormap=new HashMap();
                for (int num=0; num < keylist.size(); num++) {
                    List<Object> list1=systemAnswers.get(keylist.get(num)+"");//
                    List<Object> list2=answermap.get(keylist.get(num)+"");
                    int count=0;
                    for (int i = 0; i < list1.size(); i++) {
                        String systemanswer=list1.get(i)+"";
                        for (int j = 0; j < list2.size(); j++) {
//                            System.out.println("<<<><>>>"+String.valueOf(list1)+"vvvvvvvvv"+String.valueOf(list2));
                            String personanswer=list2.get(j)+"";
                            if (systemanswer.equals(personanswer)) {
                                count++;
                            } else {


                            }
                        }
                    }
                    if (count!=list1.size()){
                        erroranswerIds.add(keylist.get(num)+"");
                        answererrormap.put(keylist.get(num)+"",list2);
                        //加入错题集合
                    }
                }
   }

    //上一题设置选项
    private void getupanswer(Map answermap,List keylist) {
    //answermap  person选择集合      keylist 当前题目key键集合


        List list = new ArrayList();
        list = (List) answermap.get(keylist.get(currentnum));

        if (list.size() != 0) {
            setchose();
            for (int i = 0; i < list.size(); i++) {
                for (int j = 0; j < 4; j++) {

                    if (list.get(i).equals(listchoseanswers.get(j).getText() + "")) {
                        //      listabcd,listchoseanswers,listansweric;//abcd集合，abcd选项集合，对勾集合；
                        listabcd.get(j).setTextColor(this.getResources().getColor(R.color.colorblue2));
                        listchoseanswers.get(j).setTextColor(this.getResources().getColor(R.color.colorblue2));
                        listansweric.get(j).setVisibility(View.VISIBLE);
                    }
                }
            }
        }else {setchose();}

    }


    //错题上传;
    private void savaError(List<String> list){
        List<Map> questionidlist=new ArrayList<Map>();
        for (int i=0; i<list.size();i++) {
            String questionid=list.get(i)+"";


            questionidlist=dbOpenHelper.queryListMap("select * from ExamErrorQuestions where QuestionId=? and PersionId=?",
                    new String[]{questionid,personId});
                if (questionidlist.size()==0) {
                    dbOpenHelper.insert("ExamErrorQuestions", new String[]{ "QuestionId", "ErrorCount","PersionId"},
                            new Object[]{Integer.parseInt(list.get(i)),"1",personId});

                }else {
                    String examId=questionidlist.get(0).get("Id")+"";
                        int errorcount= Integer.parseInt(questionidlist.get(0).get("ErrorCount")+"");
                            errorcount=errorcount+1;
                     dbOpenHelper.update("ExamErrorQuestions",new String[]{"ErrorCount"},
                                new Object[]{errorcount},new String[]{"Id"},new String[]{examId});
                }
        }
    }
}
