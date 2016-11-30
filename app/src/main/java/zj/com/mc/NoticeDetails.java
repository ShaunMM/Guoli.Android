package zj.com.mc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import DBUtils.DBOpenHelper;

/**
 * Created by dell on 2016/9/6.
 */
//公告详情
public class NoticeDetails extends Activity implements View.OnClickListener{

    private TextView notice_title,notice_date,notice_context;
    private List<Map>noticeList;
    private DBOpenHelper dbOpenHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.noticedetails);
        notice_title= (TextView) findViewById(R.id.notice_title);
        notice_date= (TextView) findViewById(R.id.notice_date);
        notice_context= (TextView) findViewById(R.id.notice_context);
        findViewById(R.id.noticedetails_titleback).setOnClickListener(this);
        dbOpenHelper= DBOpenHelper.getInstance(this);

        initviewnoticedata();
//        noticeList=dbOpenHelper.queryListMap("select * from Announcement",null);
//        UtilisClass.showToast(NoticeDetails.this, String.valueOf(noticeList));
    }

    private void initviewnoticedata() {
        Intent intentnotice=getIntent();
        String id=intentnotice.getStringExtra("Id");

        noticeList=dbOpenHelper.queryListMap("select * from Announcement where Id=?",new String[]{id+""});
//        UtilisClass.showToast(NoticeDetails.this, String.valueOf(id));



        if (noticeList.size()!=0){
            notice_title.setText(noticeList.get(0).get("Title")+"");
            notice_date.setText(noticeList.get(0).get("PubTime")+"");
            notice_context.setText(noticeList.get(0).get("Content")+"");
        }
    }


    @Override
    public void onClick(View view) {
        finish();
    }
}
