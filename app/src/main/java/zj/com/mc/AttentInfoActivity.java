package zj.com.mc;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import Fragments.DriverAttensFragment;
import Fragments.DriverExitFragment;
import Fragments.RignFragment;

/**
 * 近期手账记录
 */
public class AttentInfoActivity extends FragmentActivity {

    private LinearLayout search_result_back;
    private FrameLayout fl_temp;
    private RadioGroup rp_btn;
    private FragmentTransaction beginTransaction;
    private DriverAttensFragment driverAttensFragment;//出勤信息
    private RignFragment rignFragment;//签点信息
    private DriverExitFragment exitFragment;//退勤信息
    private String id, modify;
    private TextView tv_save;//保存

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attentinfo);
        Myapplilcation.addActivity(this);

        search_result_back = (LinearLayout) findViewById(R.id.search_result_back);
        rp_btn = (RadioGroup) findViewById(R.id.rp_btn);
        fl_temp = (FrameLayout) findViewById(R.id.fl_temp);
        tv_save = (TextView) findViewById(R.id.tv_save);

        id = getIntent().getStringExtra("id");
        modify = getIntent().getStringExtra("modify");

        if ("0".equals(modify)) {
            tv_save.setVisibility(View.INVISIBLE);
        } else {
            tv_save.setVisibility(View.VISIBLE);
        }

        driverAttensFragment = new DriverAttensFragment();
        rignFragment = new RignFragment();
        exitFragment = new DriverExitFragment();
        beginTransaction = getSupportFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        driverAttensFragment.setArguments(bundle);
        beginTransaction.replace(R.id.fl_temp, driverAttensFragment);
        beginTransaction.commit();

        rp_btn.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                beginTransaction = getSupportFragmentManager().beginTransaction();
                switch (checkedId) {
                    case R.id.rb_attentinfo:
                        Bundle bundle = new Bundle();
                        bundle.putString("id", id);
                        driverAttensFragment.setArguments(bundle);
                        beginTransaction.replace(R.id.fl_temp, driverAttensFragment);
                        beginTransaction.commit();
                        break;
                    case R.id.rb_rign:
                        Bundle bundle1 = new Bundle();
                        bundle1.putString("id", id);
                        rignFragment.setArguments(bundle1);
                        beginTransaction.replace(R.id.fl_temp, rignFragment);
                        beginTransaction.commit();
                        break;
                    case R.id.rb_exitattent:
                        Bundle bundle2 = new Bundle();
                        bundle2.putString("id", id);
                        exitFragment.setArguments(bundle2);
                        beginTransaction.replace(R.id.fl_temp, exitFragment);
                        beginTransaction.commit();
                        break;
                }
            }
        });

        search_result_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click.onSaveClick(v);
            }
        });
    }

    private onSavebtnClick click;

    public void setOnSavebtnClickLisner(onSavebtnClick clickLisner) {
        this.click = clickLisner;
    }

    public interface onSavebtnClick {
        public void onSaveClick(View v);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Myapplilcation.removeActivity(this);
    }
}