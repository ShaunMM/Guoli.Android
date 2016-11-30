package Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import DBUtils.DBOpenHelper;
import Utils.CommonAdapter;
import Utils.NetUtils;
import Utils.ViewHolder;
import zj.com.mc.FileShowActivity;
import zj.com.mc.R;
import zj.com.mc.SearchWebview;
import zj.com.mc.UtilisClass;


/**
 * Created by dell on 2016/7/29.
 */
public class DriverData extends Fragment implements View.OnClickListener {
    private View mView;
    private LinearLayout linearLayout1, linearLayout2,search_back;
    private AutoCompleteTextView editText;
    private GridView gridView,history;
    private TextView textView;
    private ListView listView1, listView2;
    private DBOpenHelper dbOpenHelper;
    private List<String> searchrlistfilename;
    private List<String> searchrlistfilenamecount;
    private String searchcode;
    private List<String> searchhistorycode;
    private String personID;
    private Button searchbutton;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.driverdata, container, false);
        setmViewView();
        return mView;
    }

    //初始化View
    private void setmViewView() {
        personID=getActivity().getSharedPreferences("PersonInfo", Context.MODE_PRIVATE).getString("PersonId",null);
        editText = (AutoCompleteTextView) mView.findViewById(R.id.search_edittext);
        searchbutton= (Button) mView.findViewById(R.id.search_buttonsearch);
        searchbutton.setOnClickListener(this);
        gridView = (GridView) mView.findViewById(R.id.search_gridview);
        textView = (TextView) mView.findViewById(R.id.search_result_textcount);
        linearLayout1 = (LinearLayout) mView.findViewById(R.id.search_layout1);
        linearLayout2 = (LinearLayout) mView.findViewById(R.id.search_layout2);
        listView1 = (ListView) mView.findViewById(R.id.search_type);
        listView2 = (ListView) mView.findViewById(R.id.search_details);
        search_back= (LinearLayout) mView.findViewById(R.id.search_result_back);
        search_back.setOnClickListener(this);
        dbOpenHelper = DBOpenHelper.getInstance(getActivity().getApplicationContext());
        history= (GridView) mView.findViewById(R.id.search_keywords_history);

        searchhistorycode=new ArrayList<>();
        getpershosearchhistory();
        setsearchedittext();
        setGridView();
    }
    //获取数据库关键字搜索历史
    private void getpershosearchhistory(){
        List<Map> searchhistory=dbOpenHelper.queryListMap("select * from TraficSearchRecord where PersonId=?",
                new String[]{personID});

        if (searchhistory.size()!=0){
            String code="";
            for (int i=0; i<searchhistory.size(); i++){
                code=searchhistory.get(i).get("Keywords")+"";
                searchhistorycode.add(code);
            }
        }

        setHistoryGridview();
    }
    //设置搜索历史数据
    private void setHistoryGridview(){
        searchhistorycode=NetUtils.removeDuplicate(searchhistorycode);//去重
        if (searchhistorycode.size()>5) {
            searchhistorycode = searchhistorycode.subList(0, 4);
        }else {}
        history.setAdapter(new CommonAdapter<String>(getActivity(),searchhistorycode,R.layout.trainsche_historylist) {
            @Override
            protected void convertlistener(ViewHolder holder, String s) {
            }
            @Override
            public void convert(ViewHolder holder, String s) {
                holder.setText(R.id.grid1_item2, s);
            }
        });

        history.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
               String code= searchhistorycode.get(i);
                editText.setText(code);
                editText.setSelection(code.length());
                setListView1();
            }
        });
    }

    //设置各分类数量
    private void setListView1() {
        searchcode = editText.getText() + "";
        if (!searchcode.equals("")) {
            List<Map> searchcodelist = dbOpenHelper.queryListMap("select * from TraficKeywords where Keywords =? ",
                    new String[]{searchcode});


            if (searchcodelist.size() != 0) {
                searchhistorycode.add(searchcode);
                setHistoryGridview();
                List<Map> codes=dbOpenHelper.queryListMap("select * from TraficSearchRecord where PersonId=? and Keywords=?"
                ,new String[]{personID,searchcode});
                if (codes.size()==0){
                    dbOpenHelper.insert("TraficSearchRecord",new String[]{"PersonId","Keywords","SearchCount","IsUploaded"},new Object[]{personID,searchcode,1,1});
                }else {

                    String id=codes.get(0).get("Id")+"";
                    String searchcount=codes.get(0).get("SearchCount")+"";
                    int searchCount=Integer.parseInt(searchcount);
                    searchCount=searchCount+1;
                    dbOpenHelper.update("TraficSearchRecord",new String[]{"SearchCount"},new Object[]{searchCount},
                            new String[]{"Id"},new String[]{id});
                }


                linearLayout1.setVisibility(View.GONE);
                linearLayout2.setVisibility(View.VISIBLE);
                search_back.setVisibility(View.VISIBLE);
                String keywordsid = searchcodelist.get(0).get("Id") + "";//关键字Id
                //关键字对应的文件结果
                List<Map> searchresultlist = dbOpenHelper.queryListMap("select * from TraficSearchResult where KeywordsId=?",
                        new String[]{keywordsid});
                List<String> listfileresultId = new ArrayList<>();
                if (searchresultlist.size() != 0) {
                    //文件Id
                    for (int j = 0; j < searchresultlist.size(); j++) {
                        String fileid = searchresultlist.get(j).get("TraficFileId") + "";
                        listfileresultId.add(fileid);
                    }
                    //父文件夹id
                    List<String> filetypeid=new ArrayList<>();
                    //获取第一个listview即各个文件文件夹文件夹名称
                    searchrlistfilename = new ArrayList<>();
                    searchrlistfilenamecount = new ArrayList<>();
                    for (int i = 0; i < listfileresultId.size(); i++) {
                        List<Map> list = dbOpenHelper.queryListMap("select * from TraficFiles where Id=?", new String[]{listfileresultId.get(i)});
                        String filetypeids = list.get(0).get("TypeId") + "";
                        filetypeid.add(filetypeids);
                        List<Map> list1 = dbOpenHelper.queryListMap("select * from TraficFileType where Id=?",
                                new String[]{filetypeids});
                        String fileName = list1.get(0).get("TypeName") + "";
                        searchrlistfilename.add(fileName);
                    }
                    //进行数据的分类   属于哪个文件夹下
                    if (searchrlistfilename.size()>1){
                        searchrlistfilename=NetUtils.removeDuplicate(searchrlistfilename);
                    }
                    //父文件夹id进行数据的分类   属于哪个文件夹下
                    if (filetypeid.size()>1){
                        filetypeid=NetUtils.removeDuplicate(filetypeid);
                    }


                    //进行关键字数据跟距文件夹分类
                    final List<List> baselistfile=new ArrayList<>();
                    for (int j=0; j<filetypeid.size(); j++) {
                        List<Map> childfile=new ArrayList<>();
                        for (int i = 0; i < listfileresultId.size(); i++) {
                            List<Map> list = dbOpenHelper.queryListMap("select * from TraficFiles where Id=?", new String[]{listfileresultId.get(i)});
                            String filetypeids = list.get(0).get("TypeId") + "";

                            if (filetypeids.equals(filetypeid.get(j))){
                                childfile.add(searchresultlist.get(i));
                            }


                        }
                        String filenamecount=searchrlistfilename.get(j)+"("+childfile.size()+")";
                        searchrlistfilenamecount.add(filenamecount);
                        baselistfile.add(childfile);
                    }
                    listView1.setAdapter(new CommonAdapter<String>(getActivity(), searchrlistfilenamecount, R.layout.search_result_list1) {
                        @Override
                        protected void convertlistener(ViewHolder holder, String s) {

                        }
                        @Override
                        public void convert(ViewHolder holder, String s) {
//                            int count=baselistfile.get(i).size();
//                            String typecount=s+"("+""+")";
                            holder.setText(R.id.search_result_list1item, s);
                        }
                    });
//文件分类点击事件
                    listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            setListView2(baselistfile,i);

                        }
                    });
                    textView.setText("共搜索到"+searchresultlist.size()+"个结果");
                    setListView2(baselistfile,0);

                }


            }
        }else {
            UtilisClass.showToast(getActivity(),"未找到相关内容！");
        }

    }

    //设置分类详情
    private void setListView2(List<List> list,int i) {

        final List<Map> mapList=list.get(i);


        listView2.setAdapter(new CommonAdapter<Map>(getActivity(),mapList,R.layout.driverdatasearch2) {
            @Override
            protected void convertlistener(ViewHolder holder, Map map) {
            }

            @Override
            public void convert(ViewHolder holder, final Map map) {

                List<Map> list = dbOpenHelper.queryListMap("select * from TraficFiles where Id=?", new String[]{map.get("TraficFileId")+""});
                String filename = list.get(0).get("FileName") + "";
                    filename=filename.substring(0,filename.length()-4);
                holder.setText(R.id.search_result_title,filename);

                WebView webView=holder.getView(R.id.search_result_webview);
                String htmlData=map.get("SearchResult")+"";
                webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
                webView.loadDataWithBaseURL(null, htmlData, "text/html", "utf-8", null);

                holder.getView(R.id.search_result_click).setOnClickListener(new View.OnClickListener() {
//                holder.getView(R.id.search_result_layout).setOnClickListener(new View.OnClickListener() {
//                webView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String fileId=map.get("TraficFileId")+"";
                        List<Map> fileInfo=dbOpenHelper.queryListMap("select * from TraficFiles where Id=?"
                                ,new String[]{fileId});
                        String filelocapath=fileInfo.get(0).get("LocaPath")+"";
                        String position=map.get("Position")+"";
//                        String keyword=searchcode;
                        String keywordmaddress=filelocapath+"?"+"id="+position+"&"+"keywords="+searchcode;
//                        System.out.println("MMMMMMMM"+keywordmaddress);
                        Intent seachitemintent=new Intent(getActivity(), SearchWebview.class);
                        Bundle seachitembundle=new Bundle();
                        seachitembundle.putString("id",position);
                        seachitembundle.putString("keywords",searchcode);
                        seachitembundle.putString("LocaPath",filelocapath);
                        seachitemintent.putExtra("bundle",seachitembundle);

                        startActivity(seachitemintent);
                    }
                });
            }
        });
    }

    //设置文件夹分类
    private void setGridView() {
    //展示文件父类Id为0的文件夹
        final List<Map> fileslist = dbOpenHelper.queryListMap("select * from TraficFileType where ParentId=?", new String[]{"0"});
        final List<String> filesTypeNames = new ArrayList<>();

        //文件夹分类名称
        if (fileslist.size() != 0) {
            for (int i = 0; i < fileslist.size(); i++) {
                filesTypeNames.add(fileslist.get(i).get("TypeName") + "");
            }
        }
        if (fileslist.size() != 0) {
            gridView.setAdapter(new CommonAdapter<String>(getActivity(), filesTypeNames, R.layout.search_result_list1) {

                @Override
                protected void convertlistener(ViewHolder holder, String s) {

                }

                @Override
                public void convert(ViewHolder holder, String s) {
                    holder.setText(R.id.search_result_list1item, s);
                }
            });
        }

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent searchGridview = new Intent(getActivity(), FileShowActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("filename", filesTypeNames.get(i));
                bundle.putString("Id", fileslist.get(i).get("Id") + "");
                searchGridview.putExtra("bundle", bundle);
                startActivity(searchGridview);

            }
        });
    }

    private void setsearchedittext() {
        List<Map> searchcodelist = dbOpenHelper.queryListMap("select * from TraficKeywords", null);
        List<String> listkeycodes = new ArrayList<>();
        if (searchcodelist.size() != 0) {
            for (int i = 0; i < searchcodelist.size(); i++) {
                String keycode = searchcodelist.get(i).get("Keywords") + "";
                listkeycodes.add(keycode);
            }
            editText.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, listkeycodes));
            editText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    TextView edtextview= (TextView) view;
                    String code=edtextview.getText()+"";
                    editText.setText(code);
                    setListView1();

                }
            });

        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.search_buttonsearch:
                setListView1();
                break;
              case R.id.search_result_back:

                  editText.setText("");
                  linearLayout1.setVisibility(View.VISIBLE);
                  linearLayout2.setVisibility(View.GONE);
                  search_back.setVisibility(View.INVISIBLE);
                break;




        }
    }




}
