package Fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import zj.com.mc.R;

/**
 * 基类Fragment
 */
public abstract class BaseFragment extends Fragment implements View.OnClickListener {

    protected View mView;
    private TextView daily_tiancheng_spmonth;//时间选择
    private FrameLayout fl_base;
    private NumberPicker yearPicker;
    private NumberPicker monthPicker;
    protected TextView title;
    private TextView adddata;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_base, container, false);
        initUI(view);
        mView = initView(inflater, container, savedInstanceState);
        fl_base.addView(mView);
        return view;
    }

    protected void initUI(View view) {
        view.findViewById(R.id.back).setOnClickListener(this);
        title = (TextView) view.findViewById(R.id.daily_tiancheng_title);
        view.findViewById(R.id.daily_tiancheng_databutton).setOnClickListener(this);
        daily_tiancheng_spmonth = (TextView) view.findViewById(R.id.daily_tiancheng_spmonth);
        view.findViewById(R.id.tainjia_shuju).setOnClickListener(this);
        setDailyMonth();
        fl_base = (FrameLayout) view.findViewById(R.id.fl_base);
        adddata = (TextView) view.findViewById(R.id.daily_tiancheng_databutton);
        showAddButton(adddata);

    }

    protected void setDailyMonth() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        daily_tiancheng_spmonth.setText(format.format(new Date()));
        daily_tiancheng_spmonth.setOnClickListener(this);
    }

    abstract View initView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState);

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                Intent intent = new Intent();
                intent.setAction("CLOSE_FRAG");
                getActivity().sendBroadcast(intent);
                break;
            case R.id.daily_tiancheng_spmonth:

                String s = daily_tiancheng_spmonth.getText().toString();
                String[] split = s.split("-");
                String year = split[0];
                String month = split[1];
                View dialogView = View.inflate(getActivity(), R.layout.dailog_spmonth, null);
                //屏蔽软键盘自动弹出
                TextView textView = (TextView) dialogView.findViewById(R.id.text_notuse);
                textView.requestFocus();

                yearPicker = (NumberPicker) dialogView.findViewById(R.id.year);
                monthPicker = (NumberPicker) dialogView.findViewById(R.id.month);

                yearPicker.setMinValue(1990);
                yearPicker.setMaxValue(2100);
                yearPicker.setValue(Integer.valueOf(year));
                monthPicker.setMinValue(1);
                monthPicker.setMaxValue(12);
                monthPicker.setValue(Integer.valueOf(month));
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                final AlertDialog dialog = builder.setTitle("选择日期")
                        .setView(dialogView)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                int checkYear = yearPicker.getValue();
                                int checkMonth = monthPicker.getValue();
                                daily_tiancheng_spmonth.setText(checkYear + "-" + checkMonth);

                                setDateChanged(checkYear + "-" + checkMonth);

                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();

                            }
                        })
                        .create();

                dialog.show();
                break;
            case R.id.daily_tiancheng_databutton:
                setDataButton();
                break;
            case R.id.tainjia_shuju:
                setTestButton();
                break;

        }
    }

    protected abstract void setDateChanged(String date);

    protected abstract void setDataButton();

    protected abstract void setTestButton();

    protected abstract void showAddButton(TextView textView);

    @Override
    public void onResume() {
        super.onResume();
    }
}
