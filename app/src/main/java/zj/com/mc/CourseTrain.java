package zj.com.mc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import DBUtils.DBOpenHelper;
import Utils.NetUtils;
import config.ISystemConfig;
import config.SystemConfigFactory;

/**
 * 授课培训纪律---详情
 */
public class CourseTrain extends Activity implements View.OnClickListener {

    private int SAVECONTER = 0;
    private int intentid;
    private ListView course_poplist;
    private List<TextView> viewList;
    private List<EditText> edList;
    private EditText address, personcount, daily_course_aim, courseedit_key;
    private TextView coursedate, startt, endt;
    private String personId;


    private DBOpenHelper dbopenhelper;
    private RelativeLayout course_popuwindow;
    private LinearLayout course_titleback;
    private TextView whichtv;

    private String key;
    private List<Map> listcourse, list;
    private String isUploaded = "is";
    private ISystemConfig systemConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coursetrain);
        Myapplilcation.addActivity(this);
        initview();
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("InstructorTeach");
        intentid = bundle.getInt("listitemId");
        personId = systemConfig.getUserId();
        if (intentid != -1) {
            isUploaded = bundle.getString("IsUploaded");
            String sql = "select * from InstructorTeach" + " where Id  =? ";
            String[] selectionArgs = new String[]{intentid + ""};
            list = dbopenhelper.queryListMap(sql, selectionArgs);
            SAVECONTER = (Integer) list.get(0).get("IsUploaded");
            initviewtext();
        }

        if (isUploaded.equals("1")) {
            address.setEnabled(false);
            personcount.setEnabled(false);
            daily_course_aim.setEnabled(false);
        }
    }

    private void initview() {
        dbopenhelper = DBOpenHelper.getInstance(getApplicationContext());
        systemConfig = SystemConfigFactory.getInstance(this).getSystemConfig();
        address = (EditText) findViewById(R.id.course_takedate);//授课地点
        personcount = (EditText) findViewById(R.id.course_personcount);//参与人数
        daily_course_aim = (EditText) findViewById(R.id.daily_course_aim);//授课内容

        coursedate = (TextView) findViewById(R.id.course_date);//授课日期
        coursedate.setOnClickListener(this);
        startt = (TextView) findViewById(R.id.course_time1);//授课开始时间
        startt.setOnClickListener(this);
        endt = (TextView) findViewById(R.id.course_time2);//授课结束时间
        endt.setOnClickListener(this);

        course_popuwindow = (RelativeLayout) findViewById(R.id.course_popuwindow);//弹窗
        course_popuwindow.setOnClickListener(this);
        course_titleback = (LinearLayout) findViewById(R.id.course_titleback);//title 返回键
        course_titleback.setOnClickListener(this);
        course_poplist = (ListView) findViewById(R.id.course_poplist);

        findViewById(R.id.daily_course_save).setOnClickListener(this);
        findViewById(R.id.daily_course_saveup).setOnClickListener(this);
        findViewById(R.id.daily_course_cancle).setOnClickListener(this);

        viewList = new ArrayList<TextView>();
        viewList.add(coursedate);
        viewList.add(startt);
        viewList.add(endt);
        edList = new ArrayList<EditText>();
        edList.add(address);
        edList.add(personcount);
    }

    private void initviewtext() {
        Map map = list.get(0);
        address.setText(map.get("TeachPlace").toString());
        personcount.setText(map.get("JoinCount").toString());
        if (map.get("TeachStart").toString().length() > 2 && map.get("TeachStart").toString().length() < 12) {
            coursedate.setText(map.get("TeachStart").toString().split(" ")[0]);
        } else if (map.get("TeachStart").toString().length() > 11) {
            coursedate.setText(map.get("TeachStart").toString().split(" ")[0]);
            startt.setText(map.get("TeachStart").toString().split(" ")[1]);
            endt.setText(map.get("TeachEnd").toString().split(" ")[1]);
        } else {
            coursedate.setText("");
            startt.setText("");
            endt.setText("");
        }
        daily_course_aim.setText(map.get("TeachContent").toString());
    }

    @Override
    public void onClick(View view) {

        if (SAVECONTER == 0) {
            switch (view.getId()) {
                case R.id.course_titleback:
                    UtilisClass.hidInputMethodManager(CourseTrain.this, address);
                    finish();
                    break;
                case R.id.course_date:
                    UtilisClass.getdatepicker(CourseTrain.this, coursedate);
                    break;
                case R.id.course_popuwindow:
                    course_popuwindow.setVisibility(View.GONE);
                    break;
                case R.id.course_popuwindow_yes:
                    whichtv.setText(courseedit_key.getText().toString());
                    course_popuwindow.setVisibility(View.GONE);
                    break;
                case R.id.course_time1:
                    UtilisClass.gettimepicker(CourseTrain.this, startt);
                    break;
                case R.id.course_time2:
                    UtilisClass.gettimepicker(CourseTrain.this, endt);
                    break;
                case R.id.daily_course_save:
                    //保存到本地
                    if (SAVECONTER == 0) {
                        getisempty(0);
                        UtilisClass.hidInputMethodManager(CourseTrain.this, address);
                        finish();
                    } else {
                        UtilisClass.showToast(CourseTrain.this, "文件保存失败！");
                    }
                    break;
                case R.id.daily_course_saveup:
                    //保存并上传
                    if (SAVECONTER == 1) {
                        getisempty2(viewList, edList, 1);
                        UtilisClass.hidInputMethodManager(CourseTrain.this, address);
                        //UtilisClass.UpLoadeddata("InstructorTeach");
                        //UtilisClass.showToast(CourseTrain.this,"上传成功");
                        //执行上传代码
                        SAVECONTER = 0;
                    } else {
                        UtilisClass.showToast(CourseTrain.this, "保存失败，文件已上传！");
                    }
                    break;
                case R.id.daily_course_cancle:
                    UtilisClass.hidInputMethodManager(CourseTrain.this, address);
                    finish();
                    break;
            }
        } else {
            switch (view.getId()) {
                case R.id.course_titleback:
                    UtilisClass.hidInputMethodManager(CourseTrain.this, address);
                    finish();
                    break;
                case R.id.daily_course_cancle:
                    UtilisClass.hidInputMethodManager(CourseTrain.this, address);
                    finish();
                    break;
            }
        }
    }

    private void getisempty(int uploadedid) {
        if (intentid == -1) {
            dbopenhelper.insert("InstructorTeach", new String[]{"InstructorId", "TeachPlace", "JoinCount",
                            "TeachStart", "TeachEnd", "TeachContent", "IsUploaded"},
                    new Object[]{personId, address.getText().toString(),
                            personcount.getText().toString(), coursedate.getText().toString() + " " + startt.getText().toString(),
                            coursedate.getText().toString() + " " + endt.getText().toString(), daily_course_aim.getText().toString()
                            , uploadedid});
        } else {
            dbopenhelper.update("InstructorTeach",
                    new String[]{"InstructorId", "TeachPlace", "JoinCount",
                            "TeachStart", "TeachEnd", "TeachContent", "IsUploaded"},
                    new Object[]{personId, address.getText().toString(),
                            personcount.getText().toString(), coursedate.getText().toString() + " " + startt.getText().toString(),
                            coursedate.getText().toString() + " " + endt.getText().toString(), daily_course_aim.getText().toString()
                            , uploadedid}, new String[]{"Id"}, new String[]{intentid + ""});
        }
    }

    private void getisempty2(List<TextView> viewList, List<EditText> edList, int uploadedid) {
        aa:
        for (int i = 0; i < viewList.size(); i++) {
            if (viewList.get(i).getText().toString().equals("")) {
                UtilisClass.showToast(CourseTrain.this, getResources().getString(R.string.perfectInformation));
                break aa;
            } else {
                if (i == viewList.size() - 1) {
                    for (int j = 0; j < edList.size(); j++) {
                        if (edList.get(j).getText().toString().equals("")) {
                            UtilisClass.showToast(CourseTrain.this, getResources().getString(R.string.perfectInformation));
                            break aa;
                        } else {
                            if (j == edList.size() - 1) {
                                if (intentid == -1) {
                                    dbopenhelper.insert("InstructorTeach", new String[]{"InstructorId", "TeachPlace", "JoinCount",
                                                    "TeachStart", "TeachEnd", "TeachContent", "IsUploaded"},
                                            new Object[]{personId, address.getText().toString(),
                                                    personcount.getText().toString(), coursedate.getText().toString() + " " + startt.getText().toString(),
                                                    coursedate.getText().toString() + " " + endt.getText().toString(), daily_course_aim.getText().toString()
                                                    , uploadedid});
                                    List<Map> mapListid = dbopenhelper.queryListMap("select * from InstructorTeach where InstructorId=?", new String[]{personId});
                                    if (mapListid.size() != 0) {
                                        final String upid = mapListid.get(mapListid.size() - 1).get("Id") + "";

                                        Myapplilcation.getExecutorService().execute(new Runnable() {
                                            @Override
                                            public void run() {
                                                NetUtils.updataarguments3dingle(dbopenhelper, systemConfig, "InstructorTeach", upid);
                                            }
                                        });
                                    }

                                } else {
                                    dbopenhelper.update("InstructorTeach",
                                            new String[]{"InstructorId", "TeachPlace", "JoinCount",
                                                    "TeachStart", "TeachEnd", "TeachContent", "IsUploaded"},
                                            new Object[]{personId, address.getText().toString(),
                                                    personcount.getText().toString(), coursedate.getText().toString() + " " + startt.getText().toString(),
                                                    coursedate.getText().toString() + " " + endt.getText().toString(), daily_course_aim.getText().toString()
                                                    , uploadedid}, new String[]{"Id"}, new String[]{intentid + ""});

                                    Myapplilcation.getExecutorService().execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            NetUtils.updataarguments3dingle(dbopenhelper, systemConfig, "InstructorTeach", intentid + "");
                                        }
                                    });
                                }
                                finish();
                            }
                        }

                    }
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (course_popuwindow.getVisibility() != View.GONE) {
            course_popuwindow.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Myapplilcation.removeActivity(this);
    }
}
