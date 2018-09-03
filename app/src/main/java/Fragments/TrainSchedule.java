package Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import Adapter.TrainScheduleAdapterTwo;
import DBUtils.DBOpenHelper;
import zj.com.mc.R;

/**
 * Created by BYJ on 2017/8/3.
 */

public class TrainSchedule extends Fragment implements View.OnClickListener {

    private EditText et_startstation;
    private EditText et_endstation;
    private Button bt_exchange;
    private RadioGroup rg_options;
    private Button bt_querycode;
    private ExpandableListView el_searchresult;
    private DBOpenHelper dbOpenHelper;
    private List<Map> trains;
    private List<List<Map>> trainMoments;
    private String firststation = "";
    private String laststation = "";
    private String code = "";
    private String type = "FullName";
    private TrainScheduleAdapterTwo trainScheduleAdapterTwo;
    private String queryType;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        trainMoments = new ArrayList<>();
        dbOpenHelper = DBOpenHelper.getInstance(getActivity().getApplicationContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trainschedule, container, false);
        initView(view);
        queryType = "all";
        new MyTask(getActivity()).execute("all");
        getRadioGroupKey();
        queryCondition();
        return view;
    }

    private void queryCondition() {

        et_startstation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                firststation = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        et_endstation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                laststation = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private void initView(View view) {
        et_startstation = (EditText) view.findViewById(R.id.et_startstation);
        et_endstation = (EditText) view.findViewById(R.id.et_endstation);
        bt_exchange = (Button) view.findViewById(R.id.bt_exchange);
        rg_options = (RadioGroup) view.findViewById(R.id.rg_options);
        bt_querycode = (Button) view.findViewById(R.id.bt_querycode);
        el_searchresult = (ExpandableListView) view.findViewById(R.id.el_searchresult);

        bt_exchange.setOnClickListener(this);
        bt_querycode.setOnClickListener(this);
    }

    private void initData() {
        trains = dbOpenHelper.queryListMap("select * from TrainNo", null);
        if (trains.size() > 0) {
            List<Map> trainMomentMap;
            for (int i = 0; i < trains.size(); i++) {
                trainMomentMap = new ArrayList<>();
                String trainNoLineId = trains.get(i).get("Id").toString();
                trainMomentMap = dbOpenHelper.queryListMap("select * from ViewTrainMoment where TrainNoId = ?",
                        new String[]{trainNoLineId});
                trainMoments.add(trainMomentMap);
            }
        }
    }

    public void getRadioGroupKey() {
        rg_options.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rb_allcodes:
                        type = "FullName";
                        code = "";
                        break;
                    case R.id.rb_gdccodes:
                        type = "FullName";
                        code = "D";
                        break;
                    case R.id.rb_zcodes:
                        type = "FullName";
                        code = "Z";
                        break;
                    case R.id.rb_kcodes:
                        type = "FullName";
                        code = "K";
                        break;
                    case R.id.rb_ycodes:
                        type = "FullName";
                        code = "Y";
                        break;
                    case R.id.rb_othercodes:
                        type = "Code";
                        code = "";
                        break;
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_exchange:
                firststation = et_endstation.getText().toString();
                laststation = et_startstation.getText().toString();
                et_startstation.setText(firststation);
                et_endstation.setText(laststation);
                break;
            case R.id.bt_querycode:
                firststation = et_startstation.getText().toString();
                laststation = et_endstation.getText().toString();
                queryType = "term";
                new MyTask(getActivity()).execute("term");
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setAdapter();
    }

    private void queryTrainInfo(String firststation, String laststation, String code, String type) {
        if (trains.size() > 0) {
            trains.clear();
        }
        if (type.equals("Code")) {
            trains = dbOpenHelper.queryListMap("select * from TrainNo where FirstStation like ? and LastStation like ? and Code = ? limit 100",
                    new String[]{"%" + firststation + "%", "%" + laststation + "%", code});
        } else {
            trains = dbOpenHelper.queryListMap("select * from TrainNo where FirstStation like ? and LastStation like ? and FullName like ? limit 100",
                    new String[]{"%" + firststation + "%", "%" + laststation + "%", "%" + code + "%"});
        }
        setTrainMoments();
    }

    public void setTrainMoments() {
        if (trainMoments.size() > 0) {
            trainMoments.clear();
        }
        if (trains.size() > 0) {
            List<Map> trainMomentMap;
            for (int i = 0; i < trains.size(); i++) {
                trainMomentMap = new ArrayList<>();
                String trainNoLineId = trains.get(i).get("Id").toString();
                trainMomentMap = dbOpenHelper.queryListMap("select * from ViewTrainMoment where TrainNoId = ?",
                        new String[]{trainNoLineId});
                trainMoments.add(trainMomentMap);
            }
        }
    }

    public void setAdapter() {
        if (trains != null && trainMoments != null) {
            if (trainScheduleAdapterTwo == null) {
                trainScheduleAdapterTwo = new TrainScheduleAdapterTwo(getActivity(), trains, trainMoments);
                el_searchresult.setAdapter(trainScheduleAdapterTwo);
            } else {
                trainScheduleAdapterTwo = null;
                trainScheduleAdapterTwo = new TrainScheduleAdapterTwo(getActivity(), trains, trainMoments);
                el_searchresult.setAdapter(trainScheduleAdapterTwo);
            }
        }
    }

    class MyTask extends AsyncTask<String, Void, String> {
        private ProgressDialog pDialog;
        private Context context = null;

        public MyTask(Context context) {
            this.context = context;
            pDialog = new ProgressDialog(context);
            pDialog.setTitle("提示：");
            pDialog.setMessage("数据加载中...");
        }

        @Override
        protected void onPreExecute() {
            if (queryType.equals("term")) {
                pDialog.show();
            }
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            String str = null;
            if (strings[0].equals("all")) {
                initData();
                str = "all";
            } else if (strings[0].equals("term")) {
                queryTrainInfo(firststation, laststation, code, type);
                str = "term";
            }
            return str;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                setAdapter();
            }

            if (pDialog != null && result.equals("term")) {
                pDialog.dismiss();
            }
        }
    }
}