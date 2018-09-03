package Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import DBUtils.DBOpenHelper;
import Utils.CommonAdapter;
import Utils.ListViewForScrollView;
import Utils.ViewHolder;
import config.ISystemConfig;
import config.SystemConfigFactory;
import zj.com.mc.AddRemoveKeyperson;
import zj.com.mc.MonthSummarize;
import zj.com.mc.R;
import zj.com.mc.UtilisClass;

/**
 * 指导司机
 */
public class DailyWork extends Fragment implements View.OnClickListener {

    int i = 0;

    private LinearLayout rootView;
    private FragmentManager fm;
    private View content;
    private FrameLayout fl_container;
    private ArrayList<BaseFragment> fragments;
    private MyBroadcastReciver reciver;
    private TextView daily_add1, daily_add2, daily_add3, daily_add4, daily_add5;//白天，夜间添乘，月添乘趟数，关键人趟数，示范操纵时间
    private TextView daily_personname, daily_personid;
    private TextView daily_add6, daily_add7, daily_add8, daily_add9, daily_add10, listshow;
    private Button daily_tc5;
    private TextView daily_tangshu, daily_lines;
    private String personId;
    private String currentyearmoth;
    private ListViewForScrollView dailywork_teamperson;
    private int HAVEORNOKEYPERSON;
    private ISystemConfig systemConfig;
    //选择布局
    // 1添乘，2检查，3 培训，4班组，5班组内容
    private List<ArrayList<String>> listdata;
    private DBOpenHelper dbOpenHelper;
    private String[] bz = {"工号", "姓名", "职务", "上次添乘日期", "距今天数"};//班组管理

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        //加载需要刷新的数据
        setListviewData();//班组管理
        initViewData();//日常工作指导司机指标完成情况
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dailywork, container, false);
        rootView = (LinearLayout) view.findViewById(R.id.daily_wroklayout);
        content = view.findViewById(R.id.ll_content);
        fl_container = (FrameLayout) view.findViewById(R.id.fl_container);

        fm = getActivity().getSupportFragmentManager();
        currentyearmoth = UtilisClass.getStringDate2();
        dbOpenHelper = DBOpenHelper.getInstance(getActivity().getApplicationContext());
        systemConfig = SystemConfigFactory.getInstance(getActivity()).getSystemConfig();
        personId = systemConfig.getUserId();

        initOnclick(view);
        initList();
        createFragments();
        IntentFilter intentFilter = new IntentFilter("CLOSE_FRAG");
        reciver = new MyBroadcastReciver();
        getActivity().registerReceiver(reciver, intentFilter);
        return view;
    }

    private void initList() {
        listdata = new ArrayList<ArrayList<String>>();
    }

    private void createFragments() {
        fragments = new ArrayList<>();

        DailyAddPersonlist addPersonlist = new DailyAddPersonlist();//(0)添乘信息单
        DailyBreakRuls dailyBreakRuls = new DailyBreakRuls();//(1) 违章违纪记录
        DailyTrainQuality dailyTrainQuality = new DailyTrainQuality();//(2)机车质量登记
        DailyPreventionAccid dailyPreventionAccid = new DailyPreventionAccid();//(3)防止事故及好人好事

        Dailyselectlisting dailyselectlisting = new Dailyselectlisting();//(4)抽查信息单
        DailyMachinerepair dailyMachinerepair = new DailyMachinerepair();//(5)机破临修记录
        DailyMonitoringAnalysis dailyMonitoringAnalysis = new DailyMonitoringAnalysis();//(6)监控分析单

        DailyCourseTrain dailyCourseTrain = new DailyCourseTrain();//(7)授课培训记录
        StandardizedAcceptance standardizedAcceptance = new StandardizedAcceptance();//(8)标准化验收单
        DailyKeyPersonlist dailyKeyPersonlist = new DailyKeyPersonlist();//(9)关键人历史记录
        //AddRemoveKeyperson.class      关键人管理
        //MonthSummarize.class          上月/本月总结计划
        fragments.add(addPersonlist);
        fragments.add(dailyBreakRuls);
        fragments.add(dailyTrainQuality);
        fragments.add(dailyPreventionAccid);
        fragments.add(dailyselectlisting);
        fragments.add(dailyMachinerepair);
        fragments.add(dailyMonitoringAnalysis);
        fragments.add(dailyCourseTrain);
        fragments.add(standardizedAcceptance);
        fragments.add(dailyKeyPersonlist);
    }

    private void initOnclick(View view) {
        // 1添乘信息单，2违章违纪记录，3 机车质量登机，4 防止事故及好人好事，5关键人管理
        view.findViewById(R.id.daily_tc1).setOnClickListener(this);
        view.findViewById(R.id.daily_tc2).setOnClickListener(this);
        view.findViewById(R.id.daily_tc3).setOnClickListener(this);
        view.findViewById(R.id.daily_tc4).setOnClickListener(this);
        view.findViewById(R.id.daily_tc5).setOnClickListener(this);
        //检查1, 抽查信息单，2机破临修记录，3监控分析单
        view.findViewById(R.id.daily_jc1).setOnClickListener(this);
        view.findViewById(R.id.daily_jc2).setOnClickListener(this);
        view.findViewById(R.id.daily_jc3).setOnClickListener(this);
        //培训1授课培训记录，2标准化验收，3上月总结计划，本月总结计划
        view.findViewById(R.id.daily_px1).setOnClickListener(this);
        view.findViewById(R.id.daily_px2).setOnClickListener(this);
        view.findViewById(R.id.daily_px3).setOnClickListener(this);
        view.findViewById(R.id.daily_px4).setOnClickListener(this);

        view.findViewById(R.id.daily_keypersonlistory).setOnClickListener(this);//关键人历史记录

        daily_add1 = (TextView) view.findViewById(R.id.daily_add1);
        daily_add2 = (TextView) view.findViewById(R.id.daily_add2);
        daily_add3 = (TextView) view.findViewById(R.id.daily_add3);
        daily_add4 = (TextView) view.findViewById(R.id.daily_add4);
        daily_add5 = (TextView) view.findViewById(R.id.daily_add5);

        daily_add6 = (TextView) view.findViewById(R.id.daily_add6);
        daily_add7 = (TextView) view.findViewById(R.id.daily_add7);
        daily_add8 = (TextView) view.findViewById(R.id.daily_add8);
        daily_add9 = (TextView) view.findViewById(R.id.daily_add9);
        daily_add10 = (TextView) view.findViewById(R.id.daily_add10);

        daily_personname = (TextView) view.findViewById(R.id.daily_personname);
        daily_personid = (TextView) view.findViewById(R.id.daily_personid);

        daily_tangshu = (TextView) view.findViewById(R.id.daily_tangshu);
        daily_lines = (TextView) view.findViewById(R.id.daily_lines);

        daily_tc5 = (Button) view.findViewById(R.id.daily_tc5);
        listshow = (TextView) view.findViewById(R.id.dailywork_listshow);
        listshow.setOnClickListener(this);
        dailywork_teamperson = (ListViewForScrollView) view.findViewById(R.id.dailywork_teamperson);
    }


    //班组管理
    private void setListviewData() {
        List<Map> personInfo = dbOpenHelper.queryListMap("select * from ViewPersonInfo where Id=?", new String[]{personId});

        if (personInfo.size() != 0) {
            String departmentId = personInfo.get(0).get("DepartmentId") + "";//部门Id
            List<Map> classlist = dbOpenHelper.queryListMap("select * from ViewPersonInfo where DepartmentId=? limit 0,10", new String[]{departmentId});
            CommonAdapter<Map> adapter = new CommonAdapter<Map>(getActivity(), classlist, R.layout.classtiems) {
                @Override
                protected void convertlistener(ViewHolder holder, Map map) {
                }

                @Override
                public void convert(ViewHolder holder, Map map) {
                    holder.setText(R.id.daily_Listing_itemmessage1, String.valueOf(map.get("WorkNo")));
                    holder.setText(R.id.daily_Listing_itemtype1, String.valueOf(map.get("Name")));
                    holder.setText(R.id.daily_Listing_itemnumber1, map.get("PostName") + "");
                    List<Map> personaddinfo = dbOpenHelper.queryListMap("select * from InstructorTempTake where DriverId=? and InstructorId=?", new String[]{map.get("Id") + "", personId});
                    if (personaddinfo.size() != 0) {
                        String lastdate = personaddinfo.get(personaddinfo.size() - 1).get("TakeDate") + "";
                        holder.setText(R.id.daily_Listing_itemtime1, lastdate);
                        String currentdate = UtilisClass.getStringDate3();
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        try {
                            Date date1 = format.parse(lastdate);
                            Date date2 = format.parse(currentdate);
                            holder.setText(R.id.daily_Listing_itemdo1, UtilisClass.differentDays(date1, date2) + "");
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    } else {
                        holder.setText(R.id.daily_Listing_itemtime1, "");
                        holder.setText(R.id.daily_Listing_itemdo1, "");
                    }
                }
            };
            dailywork_teamperson.setAdapter(adapter);
        }
    }

    //日常工作指导司机指标完成情况
    private void initViewData() {
        List<Map> qualityQuota = dbOpenHelper.queryListMap("select * from InstructorQuota ORDER BY Id ASC", null);//规定限定量

        String addpersontimes = "";
        String adyaddpersontimes = "";
        String neightaddpersontimes = "";
        String monthaddpersontimes = "";
        String keyaddpersontimes = "";
        String shwocanclehourss = "";
        String monthtrains = "";
        String upmonthtrain = "";
        String middletrain = "";
        String nextmothtrain = "";
        String monthselecttime = "";

        if (qualityQuota.size() != 0) {
            addpersontimes = "/" + qualityQuota.get(UtilisClass.getADDPERSONHOURSID() - 1).get("QuataAmmount");//添乘小时数
            adyaddpersontimes = "/" + qualityQuota.get(UtilisClass.getDAYADDPERSONHOURSID() - 1).get("QuataAmmount");//白天添乘小时数
            neightaddpersontimes = "/" + qualityQuota.get(UtilisClass.getNIGHTADDPERSONHOURSID() - 1).get("QuataAmmount");        //晚上添乘小时数
            monthaddpersontimes = "/" + qualityQuota.get(UtilisClass.getMONTHADDTIME() - 1).get("QuataAmmount"); //月添乘趟数
            keyaddpersontimes = "/" + qualityQuota.get(UtilisClass.getKEYPERSONADDTIME() - 1).get("QuataAmmount");//关键人添乘
            shwocanclehourss = "/" + qualityQuota.get(UtilisClass.getSHOWCANCLEALLHOURS() - 1).get("QuataAmmount");//示范操纵累计小时数
            monthtrains = "/" + qualityQuota.get(UtilisClass.getMMONTHTRAINTIME() - 1).get("QuataAmmount"); // '月分析列数列数
            upmonthtrain = "/" + qualityQuota.get(UtilisClass.getLASTMONTHTRAIN() - 1).get("QuataAmmount"); //上旬分析列数
            middletrain = "/" + qualityQuota.get(UtilisClass.getMIDDLEMONTHTRAIN() - 1).get("QuataAmmount");//中旬分析列数
            nextmothtrain = "/" + qualityQuota.get(UtilisClass.getNEXTMONTHTRAIN() - 1).get("QuataAmmount"); //下旬分析//添乘信息单
            monthselecttime = "/" + qualityQuota.get(UtilisClass.getMONTHSELECTIMEID() - 1).get("QuataAmmount");//月检查次数
        } else {
            addpersontimes = "/0";
            adyaddpersontimes = "/0";
            neightaddpersontimes = "/0";
            monthaddpersontimes = "/0";
            keyaddpersontimes = "/0";
            shwocanclehourss = "/0";
            monthtrains = "/0";
            upmonthtrain = "/0";
            middletrain = "/0";
            nextmothtrain = "/0";
            monthselecttime = "/0";
        }
        String addperson = "0";
        String adyaddper = "0";
        String neightadd = "0";
        String monthaddp = "0";
        String keyaddper = "0";
        String shwocancl = "0";
        String monthtrai = "0";
        String upmonthtr = "0";
        String middletra = "0";
        String nextmotht = "0";
        String monthsele = "0";

        List<Map> personQuota = dbOpenHelper.queryListMap("select * from InstructorQuotaRecord where InstructorId=? and UpdateTime like ?",
                new String[]{personId, currentyearmoth + "%"});//指导司机完成
        System.out.println(currentyearmoth + String.valueOf(personQuota));

        if (personQuota.size() != 0) {
            addperson = getFinishedAmmount(UtilisClass.getADDPERSONHOURSID());//添乘小时数
            adyaddper = getFinishedAmmount(UtilisClass.getDAYADDPERSONHOURSID());//白天添乘小时数
            neightadd = getFinishedAmmount(UtilisClass.getNIGHTADDPERSONHOURSID());//晚上添乘小时数
            monthaddp = getFinishedAmmount(UtilisClass.getMONTHADDTIME());//月添乘趟数
            keyaddper = getFinishedAmmount(UtilisClass.getKEYPERSONADDTIME());//关键人添乘
            shwocancl = getFinishedAmmount(UtilisClass.getSHOWCANCLEALLHOURS());//示范操纵累计小时数
            monthtrai = getFinishedAmmount(UtilisClass.getMMONTHTRAINTIME());//月分析列数列数
            upmonthtr = getFinishedAmmount(UtilisClass.getLASTMONTHTRAIN());//上旬分析列数
            middletra = getFinishedAmmount(UtilisClass.getMIDDLEMONTHTRAIN());//中旬分析列数
            nextmotht = getFinishedAmmount(UtilisClass.getNEXTMONTHTRAIN());//下旬分析//添乘信息单
            monthsele = getFinishedAmmount(UtilisClass.getMONTHSELECTIMEID());//月检查次数
        }

        List<Map> currentkeyperson = dbOpenHelper.queryListMap("select * from InstructorKeyPerson where InstructorId=? and IsRemoved=?",
                new String[]{personId, "false"});//指导司机指标完成情况

        if (currentkeyperson.size() == 0) {
            HAVEORNOKEYPERSON = 1;
            daily_add4.setText("0" + keyaddpersontimes);
            //  关键人添乘数
            daily_tangshu.setText(0 + "");//添乘趟数
            daily_lines.setText(0 + "");//分析列数
            daily_personname.setText("");
            daily_personid.setText("");
        } else {//关键人添成信息
            HAVEORNOKEYPERSON = 0;
            String keypersonId = currentkeyperson.get(0).get("KeyPersonId") + "";
            if (!keypersonId.equals("")) {
                List<Map> keypersonInfo = dbOpenHelper.queryListMap("select * from PersonInfo where Id=?",
                        new String[]{keypersonId});//关键人乘务信息
                String keyname = UtilisClass.getName(dbOpenHelper, keypersonId);
                daily_personname.setText(keyname);
                daily_personid.setText(keypersonInfo.get(0).get("WorkNo") + "");
                List<Map> keypersonaddhistory = dbOpenHelper.queryListMap("select * from InstructorTempTake where InstructorId=? and TakeDate like? and DriverId=?", new String[]{
                        personId, UtilisClass.getStringDate2() + "%", keypersonId});
                if (keypersonaddhistory.size() != 0) {
                    int keyaddtime = keypersonaddhistory.size();
                    daily_tangshu.setText(keyaddtime + "");//添乘趟数
                    daily_add4.setText(keyaddtime + keyaddpersontimes);//添乘趟数
                }
            }
        }

        daily_add1.setText(adyaddper + adyaddpersontimes);
        daily_add2.setText(neightadd + neightaddpersontimes);
        daily_add3.setText(monthaddp + monthaddpersontimes);

        daily_add5.setText(shwocancl + shwocanclehourss);
        daily_add6.setText(monthtrai + monthtrains);
        daily_add7.setText(middletra + middletrain);
        daily_add8.setText(monthsele + monthselecttime);
        daily_add9.setText(upmonthtr + upmonthtrain);
        daily_add10.setText(nextmotht + nextmothtrain);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.dailywork_listshow:
                //展开收起-->对班组管理信息的展开收起
                if (dailywork_teamperson.getVisibility() == View.GONE) {
                    dailywork_teamperson.setVisibility(View.VISIBLE);
                    listshow.setText("收起︿");
                } else {
                    listshow.setText("展开﹀");
                    dailywork_teamperson.setVisibility(View.GONE);
                }
                break;
            case R.id.daily_tc1:
                //添乘信息单
                addFrag(fragments.get(0));
                break;
            case R.id.daily_tc2:
                //违章违纪记录
                addFrag(fragments.get(1));
                break;
            case R.id.daily_tc3:
                //机车质量登记
                addFrag(fragments.get(2));
                break;
            case R.id.daily_tc4:
                //防止事故及好人好事
                addFrag(fragments.get(3));
                break;
            case R.id.daily_tc5:
                //关键人管理
                List<Map> currentkeyperson = dbOpenHelper.queryListMap("select * from InstructorKeyPerson where InstructorId=? and IsRemoved=?",
                        new String[]{personId, "false"});//指导司机指标完成情况
                Intent addremoveintent = new Intent(getActivity(), AddRemoveKeyperson.class);
                Bundle addremovebundle = new Bundle();
                addremovebundle.putInt("listitemId", -1);
                addremovebundle.putInt("haveorno", HAVEORNOKEYPERSON);
                addremoveintent.putExtra("InstructorKeyPerson", addremovebundle);
                startActivity(addremoveintent);
                break;
            case R.id.daily_jc1:
                //检查信息单
                addFrag(fragments.get(4));
                break;
            case R.id.daily_jc2:
                //机破临修记录
                addFrag(fragments.get(5));
                break;
            case R.id.daily_jc3:
                //监控分析单
                addFrag(fragments.get(6));
                break;
            case R.id.daily_px1:
                //授课培训记录
                addFrag(fragments.get(7));
                break;
            case R.id.daily_px2:
                //标准化验收单
                addFrag(fragments.get(8));
                break;
            case R.id.daily_keypersonlistory:
                //查看关键人历史
                addFrag(fragments.get(9));
                break;
            case R.id.daily_px3:
                Intent intentpx3 = new Intent(getActivity(), MonthSummarize.class);
                Bundle bundlepx3 = new Bundle();
                bundlepx3.putInt("month", -1);
                intentpx3.putExtra("bundle", bundlepx3);
                startActivity(intentpx3);
                break;
            case R.id.daily_px4:
                Intent intentpx4 = new Intent(getActivity(), MonthSummarize.class);
                Bundle bundlepx4 = new Bundle();
                bundlepx4.putInt("month", 1);
                intentpx4.putExtra("bundle", bundlepx4);
                startActivity(intentpx4);
                break;
        }
    }

    private void addFrag(Fragment f) {
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.fl_container, f).addToBackStack(null).commit();
        fl_container.setVisibility(View.VISIBLE);
        content.setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(reciver);
    }

    //获得完成数量
    private String getFinishedAmmount(int i) {
        String s = "";
        String sql = "select * from InstructorQuotaRecord where InstructorId=? and QuotaId=? and UpdateTime like ?";
        String[] values = {personId, i + "", currentyearmoth + "%"};
        List<Map> list = dbOpenHelper.queryListMap(sql, values);
        if (list.size() != 0) {
            s = list.get(0).get("FinishedAmmount") + "";
        } else {
            s = 0 + "";
        }
        return s;
    }

    class MyBroadcastReciver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            fl_container.setVisibility(View.GONE);
            content.setVisibility(View.VISIBLE);
        }
    }
}