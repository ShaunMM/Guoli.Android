package zj.com.mc;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.List;
import java.util.Map;

import DBUtils.DBOpenHelper;
import Fragments.DailyWork;
import Fragments.DriverData;
import Fragments.DriverNote;
import Fragments.HomePage;
import Fragments.HomePage2;
import Fragments.SystemTools;
import Fragments.TrainSchedule;
import Fragments.VocationalStudy;


public class MainActivity extends FragmentActivity implements View.OnClickListener{

    private HomePage2 homePage2;//首页指导司机
    private HomePage homePage;//首页动车司机
    private DriverNote driverNote;//司机手账
    private DriverData driverData;//行车资料
    private VocationalStudy vocationalStudy;//业务学习;
    private DailyWork dailyWork;//指导司机
    private TrainSchedule trainSchedule;//列车时刻
    private SystemTools systemTools;//系统工具
    private RadioGroup rg;//菜单布局
    private FragmentManager manager;//fragment管理器
    private FragmentTransaction transaction;//fragment事务
    private DBOpenHelper dbOpenHelper;
    private RadioButton radioButton5,radioButton2;
    private String personID;
    private String isGuiddriver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
            dbOpenHelper= DBOpenHelper.getInstance(getApplicationContext());
        findViewById(R.id.radio3).setOnClickListener(this);
        radioButton5= (RadioButton) findViewById(R.id.radio5);
        radioButton2= (RadioButton) findViewById(R.id.radio2);


        initdata();
        setradiogroup();
        initFragments();
        //菜单监听

    }

    //初始化fragment
    private void initdata(){
        personID=getSharedPreferences("PersonInfo",MODE_PRIVATE).getString("PersonId",null);
        List<Map>  mapList=dbOpenHelper.queryListMap("select * from PersonInfo",null);
        System.out.println(">>>>>>><<<<<<"+mapList.toString());
        List<Map> postidlist=dbOpenHelper.queryListMap("select * from PersonInfo where Id=?",new String[]{personID});
        if (postidlist.size()!=0) {
            String postid = postidlist.get(0).get("PostId") + "";
            System.out.print("==========="+postid);
            if (postid.equals("1")) {
        //PostName=电力机车司机 Id=1, PostName=指导司机 Id=2,  PostName=车间副主任 Id=3,  PostName=车间主任 Id=4,PostName=副段长 Id=5},PostName=指导司机 Id=6}]
                radioButton5.setVisibility(View.GONE);
                radioButton2.setVisibility(View.VISIBLE);
                isGuiddriver="false";
            }else {
                radioButton5.setVisibility(View.VISIBLE);
                radioButton2.setVisibility(View.GONE);
                isGuiddriver="true";
            }
        }else {}

//        EventBus.getDefault().register(MainActivity.this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        EventBus.getDefault().unregister(MainActivity.this);
    }

    //重写返回按钮，使其无效
    @Override
    public void onBackPressed() {
    }

    //指导司机
    private void traindriver1(){

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                transaction=manager.beginTransaction();
                switch (i) {
                    case R.id.radio1:
                        setFragment(homePage2, driverNote, driverData, vocationalStudy, dailyWork, trainSchedule, systemTools);
                        break;
                    case R.id.radio2:
                        setFragment(driverNote,homePage2,driverData,vocationalStudy,dailyWork,trainSchedule,systemTools);
                        break;

                    case R.id.radio3:
                        setFragment(driverData,homePage2,driverNote,vocationalStudy,dailyWork,trainSchedule,systemTools);
                        break;

                    case R.id.radio4:
                        setFragment(vocationalStudy,driverNote,driverData,homePage2,dailyWork,trainSchedule,systemTools);
                        break;
                    case R.id.radio5:
                        setFragment(dailyWork,driverNote,driverData,vocationalStudy,homePage2,trainSchedule,systemTools);
                        break;
                    case R.id.radio6:
                        setFragment(trainSchedule,driverNote,driverData,vocationalStudy,dailyWork,homePage2,systemTools);
                        break;
                    case R.id.radio7:
                        setFragment(systemTools,driverNote,driverData,vocationalStudy,dailyWork,trainSchedule,homePage2);
                        break;
                }
                transaction.commit();
            }
        });

    }

    //火车司机
    private void traindriver2(){

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                transaction=manager.beginTransaction();
                switch (i) {
                    case R.id.radio1:
                        setFragment2(homePage, driverNote, driverData, vocationalStudy, trainSchedule, systemTools);
                        break;

                    case R.id.radio2:
                        setFragment2(driverNote,homePage,driverData,vocationalStudy,trainSchedule,systemTools);
                        break;

                    case R.id.radio3:
                        setFragment2(driverData,homePage,driverNote,vocationalStudy,trainSchedule,systemTools);
                        break;

                    case R.id.radio4:
                        setFragment2(vocationalStudy,driverNote,driverData,homePage,trainSchedule,systemTools);
                        break;
                    case R.id.radio6:
                        setFragment2(trainSchedule,driverNote,driverData,vocationalStudy,homePage,systemTools);
                        break;
                    case R.id.radio7:
                        setFragment2(systemTools,driverNote,driverData,vocationalStudy,trainSchedule,homePage);
                        break;
                }
                transaction.commit();
            }
        });

    }

    //设置点击事件
    private void setradiogroup(){
        rg = (RadioGroup) findViewById(R.id.list_menu);

        if (isGuiddriver.equals("true")) {
            traindriver1();
        }else {
            traindriver2();
        }
    }

    //生成fragment实例化
    private void initFragments() {
        manager=getSupportFragmentManager();
        transaction=manager.beginTransaction();


        if (isGuiddriver.equals("true")) {
            homePage2 = new HomePage2();//首页
            driverNote = new DriverNote();//司机手账
            driverData = new DriverData();//行车资料
            vocationalStudy = new VocationalStudy();//业务学习;
            dailyWork = new DailyWork();//指导司机
            trainSchedule = new TrainSchedule();//列车时刻
            systemTools = new SystemTools();//系统工具

            transaction.add(R.id.frg_conn,homePage2);
            transaction.add(R.id.frg_conn,driverNote);
            transaction.add(R.id.frg_conn,driverData);
            transaction.add(R.id.frg_conn,vocationalStudy);
            transaction.add(R.id.frg_conn,dailyWork);
            transaction.add(R.id.frg_conn,trainSchedule);
            transaction.add(R.id.frg_conn,systemTools);


            transaction.show(homePage2);
            transaction.hide(driverNote);
            transaction.hide(driverData);
            transaction.hide(vocationalStudy);
            transaction.hide(dailyWork);
            transaction.hide(trainSchedule);
            transaction.hide(systemTools);
            transaction.commit();

        }else {
            homePage = new HomePage();//首页
            driverNote = new DriverNote();//司机手账
            driverData = new DriverData();//行车资料
            vocationalStudy = new VocationalStudy();//业务学习;
            dailyWork = new DailyWork();//指导司机
            trainSchedule = new TrainSchedule();//列车时刻
            systemTools = new SystemTools();//系统工具

            transaction.add(R.id.frg_conn,homePage);
            transaction.add(R.id.frg_conn,driverNote);
            transaction.add(R.id.frg_conn,driverData);
            transaction.add(R.id.frg_conn,vocationalStudy);
//            transaction.add(R.id.frg_conn,dailyWork);
            transaction.add(R.id.frg_conn,trainSchedule);
            transaction.add(R.id.frg_conn,systemTools);

            transaction.show(homePage);
            transaction.hide(driverNote);
            transaction.hide(driverData);
            transaction.hide(vocationalStudy);
//            transaction.hide(dailyWork);
            transaction.hide(trainSchedule);
            transaction.hide(systemTools);
            transaction.commit();

        }
    }

    private void setFragment(Fragment f1, Fragment f2, Fragment f3, Fragment f4, Fragment f5, Fragment f6, Fragment f7){
        transaction.show(f1);
        transaction.hide(f2);
        transaction.hide(f3);
        transaction.hide(f4);
        transaction.hide(f5);
        transaction.hide(f6);
        transaction.hide(f7);
    }
    private void setFragment2(Fragment f1, Fragment f2, Fragment f3, Fragment f4, Fragment f5, Fragment f6){
        transaction.show(f1);
        transaction.hide(f2);
        transaction.hide(f3);
        transaction.hide(f4);
        transaction.hide(f5);
        transaction.hide(f6);
    }
    @Override
    public void onClick(View view) {


//        Intent intent=new Intent(MainActivity.this,TabActivity.class);
//        startActivity(intent);

    }
}
