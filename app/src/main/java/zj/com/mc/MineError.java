package zj.com.mc;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import DBUtils.DBOpenHelper;


/**
 * Created by dell on 2016/8/3.
 */
public class MineError extends Activity implements View.OnClickListener{
//我的错题
    private Button downbutton,exams_remove;
    private TextView normaltesttitle, normaltesttime, testnumber;
    private LinearLayout vp, test_l1, test_l2, test_l3, test_l4;
    private TextView answer_a, answer_b, answer_c, answer_d, answer_ia, answer_ib, answer_ic, answer_id, test_leixing, test_leixing2,a_line,b_line,c_line,d_line;
    private TextView  chose_a,chose_b,chose_c,chose_d;
    private TextView error_number;
    private int currentnum;
    private int maxcount;
    private DBOpenHelper dbOpenHelper;
    private List<Map> errorlist;
    private List<TextView> listabcd2,listchoseanswers,listansweric,listline;//abcd集合，abcd选项集合，对勾集合,下划线；
    private List<List> questionchose;//所有选项
    private List<String> listId;//
    private LinearLayout linlayouttime,mineerror_errorlayout;
    private String personID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_exams_single);
        initnormaltest();
        if (errorlist.size()!=0){
        setchose();
        }else {
        }
    }

    private void initnormaltest() {
        currentnum = 0;
        maxcount=0;
        dbOpenHelper= DBOpenHelper.getInstance(getApplicationContext());
        normaltesttitle = (TextView) findViewById(R.id.daily_normaltest_title);//标题
        normaltesttitle.setText("我的错题");
        linlayouttime= (LinearLayout) findViewById(R.id.linlayouttime);
        linlayouttime.setVisibility(View.GONE);
        mineerror_errorlayout= (LinearLayout) findViewById(R.id.mineerror_errorlayout);
        mineerror_errorlayout.setVisibility(View.VISIBLE);

        test_leixing = (TextView) findViewById(R.id.test_leixing);//类型
        test_leixing2 = (TextView) findViewById(R.id.test_leixing2);
        normaltesttime = (TextView) findViewById(R.id.normaltest_time);
        testnumber = (TextView) findViewById(R.id.normaltest_number);
        findViewById(R.id.normaltest_upbutton).setOnClickListener(this);//上一题
        downbutton = (Button) findViewById(R.id.normaltest_downbutton);//下一题
        downbutton.setOnClickListener(this);//下一题
        exams_remove= (Button) findViewById(R.id.exams_remove);
        exams_remove.setOnClickListener(this);//移除
        exams_remove.setVisibility(View.GONE);
        error_number= (TextView) findViewById(R.id.error_number);
        vp = (LinearLayout) findViewById(R.id.viewpager_layout);
        test_l1 = (LinearLayout) findViewById(R.id.test_l1);//第一题
        test_l1.setOnClickListener(this);
        test_l2 = (LinearLayout) findViewById(R.id.test_l2);//第二题
        test_l2.setOnClickListener(this);
        test_l3 = (LinearLayout) findViewById(R.id.test_l3);//第三题
        test_l3.setOnClickListener(this);
        test_l4 = (LinearLayout) findViewById(R.id.test_l4);//第四题
        test_l4.setOnClickListener(this);

        answer_a = (TextView) findViewById(R.id.answer_a);//选项A的内容
        chose_a= (TextView) findViewById(R.id.chose_a);
        answer_b = (TextView) findViewById(R.id.answer_b);//选项B
        chose_b= (TextView) findViewById(R.id.chose_b);
        answer_c = (TextView) findViewById(R.id.answer_c);//选项C
        chose_c= (TextView) findViewById(R.id.chose_c);
        answer_d = (TextView) findViewById(R.id.answer_d);//选项D
        chose_d= (TextView) findViewById(R.id.chose_d);


        answer_ia = (TextView) findViewById(R.id.answer_ia);//选项A对勾
        answer_ib = (TextView) findViewById(R.id.answer_ib);//选项B
        answer_ic = (TextView) findViewById(R.id.answer_ic);//选项C
        answer_id = (TextView) findViewById(R.id.answer_id);//选项D
        listabcd2=new ArrayList<TextView>();
        listabcd2.add(chose_a);
        listabcd2.add(chose_b);
        listabcd2.add(chose_c);
        listabcd2.add(chose_d);
        listchoseanswers=new ArrayList<TextView>();
        listchoseanswers.add(answer_a);
        listchoseanswers.add(answer_b);
        listchoseanswers.add(answer_c);
        listchoseanswers.add(answer_d);
        listansweric=new ArrayList<TextView>();
        listansweric.add(answer_ia);
        listansweric.add(answer_ib);
        listansweric.add(answer_ic);
        listansweric.add(answer_id);

        listline=new ArrayList<>();
        a_line= (TextView) findViewById(R.id.a_line);
        b_line= (TextView) findViewById(R.id.b_line);
        c_line= (TextView) findViewById(R.id.c_line);
        d_line= (TextView) findViewById(R.id.d_line);
        listline.add(a_line);
        listline.add(b_line);
        listline.add(c_line);
        listline.add(d_line);

        SharedPreferences sharedPreferences=getSharedPreferences("PersonInfo",MODE_PRIVATE);
        personID=sharedPreferences.getString("PersonId",null);

        errorlist=dbOpenHelper.queryListMap("select * from ExamErrorQuestions where PersionId=?",new String[]{personID});
        System.out.println(String.valueOf(errorlist));
        listId=new ArrayList<String>();
        if (errorlist.size()!=0) {
            for (int i = 0; i < errorlist.size(); i++) {
                listId.add(errorlist.get(i).get("QuestionId") + "");
            }
        }
        System.out.println("ssssssssss"+String.valueOf(listId));
        questionchose=new ArrayList<>();
        if (listId.size()!=0) {
            for (int i = 0; i < listId.size(); i++) {
                List<Map> list=dbOpenHelper.queryListMap("select * from ExamQuestion where Id=?",new String[]{listId.get(i)});
                System.out.println("ssssssssss"+String.valueOf(list));
                questionchose.add(list);

            }
        }
        maxcount=errorlist.size();
            error_number.setText((currentnum+1)+"/"+maxcount);

    }


    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.normaltest_upbutton:
                //上一题
                if (errorlist.size()!=0) {
                if (currentnum!=0) {

                    currentnum=currentnum-1;
                    setchose();
                    error_number.setText((currentnum+1)+"/"+maxcount);

                }else {
                    UtilisClass.showToast(MineError.this,"已经是第一题了");
                }
                }else {}
                 break;
            case R.id.normaltest_downbutton:
            //下一题
                if (errorlist.size()!=0) {

                    if (currentnum != maxcount - 1) {

                        currentnum = currentnum + 1;
                        setchose();
                        error_number.setText((currentnum + 1) + "/" + maxcount);


                    } else {
                        UtilisClass.showToast(MineError.this, "已经是最后一题");

                    }
                }else {}

                break;



        }

    }

    private void setchose() {

        List<Map> singlechose= questionchose.get(currentnum);
        String chosetype = singlechose.get(0 ).get("AnswerType") + "";
        String questbody = singlechose.get(0 ).get("Question") + "";
        String singlequestId = errorlist.get(currentnum ).get("QuestionId") + "";

        if (chosetype.equals("1")){

            test_leixing.setText("【"+"单项选择"+"】");
        }else if (chosetype.equals("2")){
            test_leixing.setText("【"+"多项选择"+"】");

        }else if (chosetype.equals("3")){
            test_leixing.setText("【"+"判断"+"】");

        }
        test_leixing2.setText(questbody);
        List<Map> choseslist = dbOpenHelper.queryListMap("select * from ExamAnswers where QuestionId=?", new String[]{singlequestId});
        for (int i=0; i<4;i++ ) {
            listchoseanswers.get(i).setTextColor(this.getResources().getColor(R.color.sblack));
            listabcd2.get(i).setTextColor(this.getResources().getColor(R.color.sblack));
            listline.get(i).setBackgroundColor(this.getResources().getColor(R.color.llblack));
            listansweric.get(i).setVisibility(View.GONE);
        }
        for (int j=0; j<choseslist.size(); j++){
            if (choseslist.get(j).get("IsRight").equals("1")) {

                listchoseanswers.get(j).setTextColor(this.getResources().getColor(R.color.colorblue2));
                listabcd2.get(j).setTextColor(this.getResources().getColor(R.color.colorblue2));
//                listline.get(j).setTextColor(this.getResources().getColor(R.color.colorblue2));
                listline.get(j).setBackgroundColor(this.getResources().getColor(R.color.colorblue2));

                listansweric.get(j).setVisibility(View.VISIBLE);

            }
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
        }else if (choseslist.size()==4){
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
        }else {}
    }
}
