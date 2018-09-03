package Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import Adapter.DriverDataAdapter;
import Adapter.DriverDataHintAdapter;
import DBUtils.DBOpenHelper;
import Utils.CommonAdapter;
import Utils.SerchLocalFileAdapert;
import Utils.NetUtils;
import Utils.ViewHolder;
import config.ISystemConfig;
import config.SystemConfigFactory;
import zj.com.mc.BridgeActivity;
import zj.com.mc.FileShowActivity;
import zj.com.mc.HtmlWebView;
import zj.com.mc.MainActivity;
import zj.com.mc.R;
import zj.com.mc.SearchWebview;
import zj.com.mc.UtilisClass;

/**
 * 行车资料
 */
public class DriverData extends Fragment implements View.OnClickListener {

    private View mView;
    private LinearLayout linearLayout1, linearLayout2, linearLayout3, search_back;
    private EditText editText;
    private ListView ls_hint;
    private GridView gridView, history;
    private TextView textView;
    private ListView listView1, listView2;
    private ListView localFilesListView;
    private DBOpenHelper dbOpenHelper;
    private List<String> searchrlistfilename;
    private List<String> searchrlistfilenamecount;
    private List<String> searchhistorycode;
    private Button searchbutton;
    private List<Map> localFileMaps = new ArrayList<>();
    private SerchLocalFileAdapert serchLocalFileAdapert;
    private DriverDataAdapter driverDataAdapter;
    private ISystemConfig systemConfig;
    private String isLocal = "NO";
    private MainActivity mainActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.driverdata, container, false);
        dbOpenHelper = DBOpenHelper.getInstance(getActivity().getApplicationContext());
        systemConfig = SystemConfigFactory.getInstance(getActivity()).getSystemConfig();
        mainActivity = (MainActivity) getActivity();
        inintView();
        return mView;
    }

    private void inintView() {
        editText = (EditText) mView.findViewById(R.id.search_edittext);//输入关键字搜索
        ls_hint = (ListView) mView.findViewById(R.id.ls_hint);//输入关键字搜索
        searchbutton = (Button) mView.findViewById(R.id.search_buttonsearch);//搜索按钮
        searchbutton.setOnClickListener(this);
        gridView = (GridView) mView.findViewById(R.id.search_gridview);//资料分类
        textView = (TextView) mView.findViewById(R.id.search_result_textcount);//搜索到结果数目
        linearLayout1 = (LinearLayout) mView.findViewById(R.id.search_layout1);//搜索的关键字历史记录
        linearLayout2 = (LinearLayout) mView.findViewById(R.id.search_layout2);//搜索结果的显示
        linearLayout3 = (LinearLayout) mView.findViewById(R.id.search_layout3);//显示根据本地文件名模糊搜素
        listView1 = (ListView) mView.findViewById(R.id.search_type);//搜索结果分类
        listView2 = (ListView) mView.findViewById(R.id.search_details);//搜索结果详细列表
        localFilesListView = (ListView) mView.findViewById(R.id.search_local_details);//本地文件模糊查询结果详细列表
        search_back = (LinearLayout) mView.findViewById(R.id.search_result_back);//返回
        search_back.setOnClickListener(this);
        history = (GridView) mView.findViewById(R.id.search_keywords_history);

        searchhistorycode = new ArrayList<>();
        getSearchHistory();
        setSearched();
        setGridView();
    }

    //获取数据库关键字搜索历史
    private void getSearchHistory() {
        List<Map> searchhistory = dbOpenHelper.queryListMap("select * from TraficSearchRecord where PersonId=?",
                new String[]{systemConfig.getUserId()});

        if (searchhistory != null) {
            if (searchhistory.size() != 0) {
                String code = "";
                for (int i = 0; i < searchhistory.size(); i++) {
                    code = searchhistory.get(i).get("Keywords") + "";
                    searchhistorycode.add(code);
                }
                setHistoryGridview();
            }
        }
    }

    //设置搜索历史数据
    private void setHistoryGridview() {
        searchhistorycode = NetUtils.removeDuplicate(searchhistorycode);
        if (searchhistorycode.size() > 5) {
            searchhistorycode = searchhistorycode.subList(0, 4);
        }

        history.setAdapter(new CommonAdapter<String>(getActivity(), searchhistorycode, R.layout.trainsche_historylist) {
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
                String code = searchhistorycode.get(i);
                editText.setText(code);
                editText.setSelection(code.length());
                setListView1(code);
                if (isLocal.equals("YES")) {
                    localFileMaps = UtilisClass.getFilelist3(dbOpenHelper, code);
                    setListView3(localFileMaps);
                    isLocal = "NO";
                }
            }
        });
    }

    private void setSearched() {
        editText.addTextChangedListener(new TextWatcher() {
            DriverDataHintAdapter driverDataHintAdapter = null;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    ls_hint.setVisibility(View.GONE);
                    linearLayout1.setVisibility(View.VISIBLE);
                    linearLayout2.setVisibility(View.GONE);
                    linearLayout3.setVisibility(View.GONE);
                    search_back.setVisibility(View.INVISIBLE);
                    getSearchHistory();
                    setGridView();
                } else {
                    List<Map> searchcodelist = dbOpenHelper.queryListMap("select * from TraficKeywords where Keywords like ?", new String[]{
                            "%" + s.toString() + "%"});
                    List<String> listkeycodes = new ArrayList<>();
                    if (searchcodelist.size() > 0) {
                        for (int i = 0; i < searchcodelist.size(); i++) {
                            String keycode = searchcodelist.get(i).get("Keywords") + "";
                            listkeycodes.add(keycode);
                        }
                        listkeycodes = NetUtils.removeDuplicate(listkeycodes);
                        if (listkeycodes.size() > 0) {
                            ls_hint.setVisibility(View.VISIBLE);
                            if (driverDataHintAdapter != null) {
                                driverDataHintAdapter.setData(listkeycodes);
                            } else {
                                driverDataHintAdapter = new DriverDataHintAdapter(getActivity(), listkeycodes);
                            }
                            ls_hint.setAdapter(driverDataHintAdapter);
                            final List<String> finalListkeycodes = listkeycodes;
                            ls_hint.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    String code = finalListkeycodes.get(i).toString();
                                    editText.setText(code);
                                    editText.setSelection(code.length());
                                    setListView1(code);
                                    if (isLocal.equals("YES")) {
                                        localFileMaps = UtilisClass.getFilelist3(dbOpenHelper, code);
                                        setListView3(localFileMaps);
                                        isLocal = "NO";
                                    }
                                }
                            });
                        }
                    } else {
                        String str = editText.getText().toString();
                        localFileMaps = UtilisClass.getFilelist3(dbOpenHelper, str);
                        setListView3(localFileMaps);
                        isLocal = "NO";
                    }
                }
            }
        });
    }

    private void setListView1(final String searchcode) {
        if (ls_hint.getVisibility() == View.VISIBLE) {
            ls_hint.setVisibility(View.GONE);
        }
        if (searchresultlist == null) {
            searchresultlist = new ArrayList<>();
        }
        if (searchresultlist.size() > 0) {
            searchresultlist.clear();
        }
        if (!searchcode.equals("")) {
            List<Map> searchcodelist = dbOpenHelper.queryListMap("select * from TraficKeywords where Keywords =? ",
                    new String[]{searchcode});

            if (searchcodelist.size() > 0) {
                this.searchcode = searchcode;
                searchhistorycode.add(searchcode);
                setHistoryGridview();
                List<Map> codes = dbOpenHelper.queryListMap("select * from TraficSearchRecord where PersonId=? and Keywords=?"
                        , new String[]{systemConfig.getUserId(), searchcode});
                if (codes.size() == 0) {
                    dbOpenHelper.insert("TraficSearchRecord", new String[]{"PersonId", "Keywords", "SearchCount", "IsUploaded"}, new Object[]{systemConfig.getUserId(), searchcode, 1, 1});
                } else {
                    String id = codes.get(0).get("Id") + "";
                    String searchcount = codes.get(0).get("SearchCount") + "";
                    int searchCount = Integer.parseInt(searchcount);
                    searchCount = searchCount + 1;
                    dbOpenHelper.update("TraficSearchRecord", new String[]{"SearchCount"}, new Object[]{searchCount},
                            new String[]{"Id"}, new String[]{id});
                }

                linearLayout1.setVisibility(View.GONE);
                linearLayout2.setVisibility(View.VISIBLE);
                search_back.setVisibility(View.VISIBLE);
                String keywordsid = searchcodelist.get(0).get("Id") + "";
                new MyTask(getActivity()).execute(keywordsid);
            }
        } else {
            textView.setText("共搜索到" + 0 + "个结果");
            UtilisClass.showToast(getActivity(), "未找到相关内容！");
        }
    }


    //设置分类详情
    private void setListView2(List<List> list, int i, final String searchcode) {
        final List<Map> mapList = list.get(i);
        listView2.setAdapter(new CommonAdapter<Map>(getActivity(), mapList, R.layout.driverdatasearch2) {
            @Override
            protected void convertlistener(ViewHolder holder, Map map) {
            }

            @Override
            public void convert(ViewHolder holder, final Map map) {

                List<Map> list = dbOpenHelper.queryListMap("select * from TraficFiles where Id=?", new String[]{map.get("TraficFileId") + ""});
                String filename = list.get(0).get("FileName") + "";
                filename = filename.substring(0, filename.length() - 4);
                holder.setText(R.id.search_result_title, filename);

                WebView webView = holder.getView(R.id.search_result_webview);
                String htmlData = map.get("SearchResult") + "";
                webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
                webView.loadDataWithBaseURL(null, htmlData, "text/html", "utf-8", null);

                holder.getView(R.id.search_result_click).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String fileId = map.get("TraficFileId") + "";
                        List<Map> fileInfo = dbOpenHelper.queryListMap("select * from TraficFiles where Id=?"
                                , new String[]{fileId});
                        String filelocapath = fileInfo.get(0).get("LocaPath") + "";
                        String position = map.get("Position") + "";
                        String keywordmaddress = filelocapath + "?" + "id=" + position + "&" + "keywords=" + searchcode;
                        Intent seachitemintent = new Intent(getActivity(), SearchWebview.class);
                        Bundle seachitembundle = new Bundle();
                        seachitembundle.putString("id", position);
                        seachitembundle.putString("keywords", searchcode);
                        seachitembundle.putString("LocaPath", filelocapath);
                        seachitemintent.putExtra("bundle", seachitembundle);
                        startActivity(seachitemintent);
                    }
                });
            }
        });
    }

    //本地文件模糊查询
    private void setListView3(final List<Map> list) {
        //((InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE)).
        // hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        if (ls_hint.getVisibility() == View.VISIBLE) {
            ls_hint.setVisibility(View.GONE);
        }

        linearLayout1.setVisibility(View.GONE);
        linearLayout2.setVisibility(View.GONE);
        linearLayout3.setVisibility(View.VISIBLE);
        search_back.setVisibility(View.VISIBLE);
        textView.setText("共搜索到" + list.size() + "个结果");
        if (serchLocalFileAdapert == null) {
            serchLocalFileAdapert = new SerchLocalFileAdapert(getActivity(), list);
            localFilesListView.setAdapter(serchLocalFileAdapert);
        } else {
            serchLocalFileAdapert.setData(list);
            localFilesListView.setAdapter(serchLocalFileAdapert);
        }

        localFilesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String fileExtension = list.get(position).get("FileExtension") + "";
                fileExtension.toLowerCase();

                if (!fileExtension.equals("")) {
                    if (fileExtension.equals(".zip") || fileExtension.equals(".htm") || fileExtension.equals(".html")) {
                        String filepath = list.get(position).get("LocaPath") + "";
                        if (filepath != null && !filepath.equals("") && !filepath.equals("null")) {
                            Intent htmlintent = new Intent(getActivity(), HtmlWebView.class);
                            htmlintent.putExtra("FilePath", filepath);
                            startActivity(htmlintent);
                        } else {
                            UtilisClass.showToast(getActivity(), "未找到该文件！");
                        }
                    } else if (fileExtension.equals(".mp4") || fileExtension.equals(".avi")
                            || fileExtension.equals(".mpg") || fileExtension.equals(".wmv")) {
                        String filepath = list.get(position).get("LocaPath") + "";
                        if (filepath != null && !filepath.equals("") && !filepath.equals("null")) {
                            Intent videointent = new Intent(Intent.ACTION_VIEW);
                            videointent.setDataAndType(Uri.parse(filepath), "video/*");
                            startActivity(videointent);
                        } else {
                            UtilisClass.showToast(getActivity(), "未找到该文件！");
                        }
                    } else if (fileExtension.equals(".swf") || fileExtension.equals(".exe") || fileExtension.equals(".mov")
                            || fileExtension.equals(".vsd") || fileExtension.equals(".rmvb")) {
                        UtilisClass.showToast(getActivity(), "暂不支持该类型文件打开！");
                    } else if (fileExtension.equals(".docx") || fileExtension.equals(".pptx") || fileExtension.equals(".pdf")
                            || fileExtension.equals(".doc") || fileExtension.equals(".ppt") || fileExtension.equals(".xls")
                            || fileExtension.equals(".xlsx") || fileExtension.equals(".ppsx") || fileExtension.equals(".wps")
                            || fileExtension.equals(".txt")) {
                        String filepath = list.get(position).get("LocaPath") + "";
                        if (filepath != null && !filepath.equals("") && !filepath.equals("null")) {
                            Intent bridgeintent = new Intent(mainActivity, BridgeActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("FilePath", filepath);
                            bridgeintent.putExtras(bundle);
                            startActivity(bridgeintent);
                        } else {
                            UtilisClass.showToast(getActivity(), "未找到该文件！");
                        }
                    } else if (fileExtension.equals(".png") || fileExtension.equals(".jpg")
                            || fileExtension.equals(".bmp") || fileExtension.equals(".jpeg")) {
                        String filepath = list.get(position).get("LocaPath") + "";
                        if (filepath != null && !filepath.equals("") && !filepath.equals("null")) {
                            Intent imageintent = new Intent();
                            imageintent.setAction(android.content.Intent.ACTION_VIEW);
                            imageintent.setDataAndType(Uri.parse("file://" + filepath), "image/*");
                            startActivity(imageintent);
                        } else {
                            UtilisClass.showToast(getActivity(), "未找到该文件！");
                        }
                    } else if (fileExtension.equals(".swf") || fileExtension.equals(".exe") || fileExtension.equals(".mov")
                            || fileExtension.equals(".vsd") || fileExtension.equals(".rmvb")) {
                        UtilisClass.showToast(getActivity(), "暂不支持该类型文件打开！");
                    }
                }
            }
        });
    }

    private void setGridView() {
        List<Map> folders;
        final List<Map> fileslist = new ArrayList<>();
        if (systemConfig.getPostId().equals("134") || systemConfig.getPostId().equals("130") || systemConfig.getPostId().equals("193")) {
            folders = dbOpenHelper.queryListMap("select * from TraficFileType where ParentId=? and IsDelete = 0 ", new String[]{"0"});
        } else {
            folders = dbOpenHelper.queryListMap("select * from TraficFileType where ParentId = 0 and Hidden = 0 and IsDelete = 0 or DepartmentId =?",
                    new String[]{systemConfig.getDepartmentId()});
        }

        if (folders.size() > 0) {
            for (int i = 0; i < folders.size(); i++) {
                if (folders.get(i).get("ParentId").toString().equals("0")) {
                    fileslist.add(folders.get(i));
                }
            }

            List<String> filesTypeNames = new ArrayList<>();
            //文件夹分类名称
            if (fileslist.size() != 0) {
                for (int i = 0; i < fileslist.size(); i++) {
                    filesTypeNames.add(fileslist.get(i).get("TypeName") + "");
                }
            }
            final List<String> finalFilesTypeNames = filesTypeNames;
            if (fileslist.size() != 0) {
                if (driverDataAdapter == null) {
                    driverDataAdapter = new DriverDataAdapter(getActivity(), finalFilesTypeNames);
                    gridView.setAdapter(driverDataAdapter);
                } else {
                    driverDataAdapter.setData(finalFilesTypeNames);
                    gridView.setAdapter(driverDataAdapter);
                }
            }

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    int index = 0;
                    for (int num = 0; num < fileslist.size(); num++) {
                        if (fileslist.get(num).get("TypeName").toString().equals(finalFilesTypeNames.get(i))) {
                            index = num;
                            break;
                        }
                    }
                    Intent searchGridview = new Intent(getActivity(), FileShowActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("filename", fileslist.get(index).get("TypeName").toString());
                    bundle.putString("Id", fileslist.get(index).get("Id") + "");
                    searchGridview.putExtra("bundle", bundle);
                    startActivity(searchGridview);
                }
            });
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.search_buttonsearch:
                List<Map> localFileMaps = new ArrayList<>();
                String str = editText.getText().toString();
                localFileMaps = UtilisClass.getFilelist3(dbOpenHelper, str);
                setListView3(localFileMaps);
                break;
            case R.id.search_result_back:
                editText.setText("");
                linearLayout1.setVisibility(View.VISIBLE);
                linearLayout2.setVisibility(View.GONE);
                search_back.setVisibility(View.INVISIBLE);
                break;
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            getSearchHistory();
            setGridView();
        }
    }

    private List<Map> searchresultlist;
    private String searchcode;

    class MyTask extends AsyncTask<String, Void, String> {
        private ProgressDialog pDialog;
        private Context context = null;

        public MyTask(Context context) {
            this.context = context;
            pDialog = new ProgressDialog(context);
            pDialog.setTitle("提示：");
            pDialog.setMessage("正在努力查询中...");
        }

        @Override
        protected void onPreExecute() {
            pDialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            String str = null;
            searchresultlist = dbOpenHelper.queryListMap("select Id,KeywordsId,SearchResult,TraficFileId,Position from TraficSearchResult where KeywordsId=?  limit 200",
                    new String[]{strings[0]});
            str = "all";
            return str;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                analyzeData();
            }
            if (pDialog != null && result.equals("all")) {
                pDialog.dismiss();
            }
        }
    }

    private void analyzeData() {
        List<String> listfileresultId = new ArrayList<>();

        if (searchresultlist.size() > 0) {
            for (int j = 0; j < searchresultlist.size(); j++) {
                String fileid = searchresultlist.get(j).get("TraficFileId") + "";
                listfileresultId.add(fileid);
            }
            //父文件夹id
            List<String> filetypeid = new ArrayList<>();
            //获取第一个listview即各个文件文件夹文件夹名称
            searchrlistfilename = new ArrayList<>();
            searchrlistfilenamecount = new ArrayList<>();
            for (int i = 0; i < listfileresultId.size(); i++) {
                List<Map> list = dbOpenHelper.queryListMap("select * from TraficFiles where Id=?", new String[]{listfileresultId.get(i)});
                String filetypeids = list.get(0).get("TypeId") + "";
                if (list.size() > 0) {
                    filetypeid.add(filetypeids);
                    List<Map> list1 = dbOpenHelper.queryListMap("select * from TraficFileType where Id=?",
                            new String[]{filetypeids});
                    if (list1.size() > 0) {
                        String fileName = list1.get(0).get("TypeName") + "";
                        searchrlistfilename.add(fileName);
                    } else {
                        searchrlistfilename.add("");
                    }
                }
            }
            //进行数据的分类   属于哪个文件夹下
            if (searchrlistfilename.size() > 1) {
                searchrlistfilename = NetUtils.removeDuplicate(searchrlistfilename);
            }
            //父文件夹id进行数据的分类   属于哪个文件夹下
            if (filetypeid.size() > 1) {
                filetypeid = NetUtils.removeDuplicate(filetypeid);
            }
            //进行关键字数据跟距文件夹分类
            final List<List> baselistfile = new ArrayList<>();
            for (int j = 0; j < filetypeid.size(); j++) {
                List<Map> childfile = new ArrayList<>();
                for (int i = 0; i < listfileresultId.size(); i++) {
                    List<Map> list = dbOpenHelper.queryListMap("select * from TraficFiles where Id=?", new String[]{listfileresultId.get(i)});
                    String filetypeids = list.get(0).get("TypeId") + "";

                    if (filetypeids.equals(filetypeid.get(j))) {
                        childfile.add(searchresultlist.get(i));
                    }
                }
                String filenamecount = searchrlistfilename.get(j) + "(" + childfile.size() + ")";
                searchrlistfilenamecount.add(filenamecount);
                baselistfile.add(childfile);
            }

            listView1.setAdapter(new CommonAdapter<String>(getActivity(), searchrlistfilenamecount, R.layout.search_result_list1) {
                @Override
                protected void convertlistener(ViewHolder holder, String s) {
                }

                @Override
                public void convert(ViewHolder holder, String s) {
                    holder.setText(R.id.search_result_list1item, s);
                }
            });

            listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    setListView2(baselistfile, i, searchcode);
                }
            });

            textView.setText("共搜索到" + searchresultlist.size() + "个结果");
            setListView2(baselistfile, 0, searchcode);
        }
    }
}