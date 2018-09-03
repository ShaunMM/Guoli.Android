package zj.com.mc;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;

import bean.MyAdapter;
import bean.Notes;

/**
 * 系统工具-->记事本
 */
public class NoteActivity extends Activity {

    private Button bt;
    private ListView lv;
    private LinearLayout note_linearLayout;
    private LayoutInflater inflater;
    private ArrayList<Notes> array;
    private MyDataBase mdb;
    private String currentSite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Myapplilcation.addActivity(this);
        initView();
        inflater = getLayoutInflater();
        mdb = new MyDataBase(this);
        array = mdb.getArray();

        if (array.size() == 0) {
            note_linearLayout.setVisibility(View.GONE);
        }
        MyAdapter adapter = new MyAdapter(inflater, array);
        lv.setAdapter(adapter);

        Intent intent = getIntent();
        currentSite = intent.getStringExtra("currentSite");
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NotesDetailActivity.class);
                intent.putExtra("currentSite", currentSite);
                startActivity(intent);
                NoteActivity.this.finish();
            }
        });
    }

    public void initView() {
        note_linearLayout = (LinearLayout) findViewById(R.id.note_linearLayout);
        lv = (ListView) findViewById(R.id.listView1);
        bt = (Button) findViewById(R.id.button1);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(getApplicationContext(), NotesDetailActivity.class);
                intent.putExtra("ids", array.get(position).getIds());
                intent.putExtra("currentSite", currentSite);
                startActivity(intent);
                NoteActivity.this.finish();
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           final int position, long id) {
                //AlertDialog,来判断是否删除日记。
                new AlertDialog.Builder(NoteActivity.this)
                        .setTitle("删除")
                        .setMessage("是否删除笔记")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mdb.toDelete(array.get(position).getIds());
                                array = mdb.getArray();
                                if (array.size() == 0) {
                                    MyAdapter adapter = new MyAdapter(inflater, array);
                                    lv.setAdapter(adapter);
                                    note_linearLayout.setVisibility(View.GONE);
                                } else {
                                    MyAdapter adapter = new MyAdapter(inflater, array);
                                    lv.setAdapter(adapter);
                                }
                            }
                        })
                        .create().show();
                return true;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item1:
                Intent intent = new Intent(getApplicationContext(), NotesDetailActivity.class);
                startActivity(intent);
                this.finish();
                break;
            case R.id.item2:
                this.finish();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Myapplilcation.removeActivity(this);
    }
}
