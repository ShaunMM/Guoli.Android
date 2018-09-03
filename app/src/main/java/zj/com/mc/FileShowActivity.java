package zj.com.mc;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Adapter.ShowFilesAdapter;
import DBUtils.DBOpenHelper;
import WPSutils.Wpsutils;
import config.ISystemConfig;
import config.SystemConfigFactory;

/**
 * 设置文件夹分类  行车资料、学习园地、动车资料
 */
public class FileShowActivity extends Activity implements View.OnClickListener {

    private ListView filesShow;
    private TextView pathtextView;
    private DBOpenHelper dbOpenHelper;
    private String path;
    private String basefleid;
    private List<String> everyfilepath;
    private List<String> evetyfilefatherId;
    private List<Map> filelist;
    private List<String> filenames;
    private TextView title;
    private ShowFilesAdapter showFilesAdapter;
    private List<Map> fileslists;
    private ISystemConfig systemConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fileshowactivitylayout);
        Myapplilcation.addActivity(this);

        systemConfig = SystemConfigFactory.getInstance(this).getSystemConfig();

        filesShow = (ListView) findViewById(R.id.search_showfiles);
        pathtextView = (TextView) findViewById(R.id.fileshow_filepath);
        title = (TextView) findViewById(R.id.fileshow_title);
        dbOpenHelper = DBOpenHelper.getInstance(getApplicationContext());
        findViewById(R.id.search_file_back).setOnClickListener(this);

        everyfilepath = new ArrayList<>();
        evetyfilefatherId = new ArrayList<>();
        fileslists = new ArrayList<>();

        //点击传过来的值
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bundle");
        basefleid = bundle.getString("Id");//顶级目录的Id
        String fileName = bundle.getString("filename");
        path = fileName;

        title.setText(path);
        pathtextView.setText(path);
        filelist = new ArrayList<>();
        filenames = new ArrayList<>();

        everyfilepath.add(path);
        evetyfilefatherId.add(basefleid);
        setFilesShow(basefleid);
    }

    private void setFilesShow(String fatherfileid) {
        filelist.clear();
        filenames.clear();
        fileslists.clear();

        List<Map> filelist1 = new ArrayList<>();
        List<Map> folder = new ArrayList<>();
        //通过行车资料分类表的Id去行行车资料分类表找typeid找到文件名称(结果还是文件夹)
        filelist1 = UtilisClass.getFilelist(dbOpenHelper, fatherfileid);//行车资料文件表     展示的文件夹
        List<String> filenames1 = new ArrayList<>();//行车资料 --->文件夹名称列表
        if (filelist1.size() != 0) {
            Map<String, String> map;
            for (int i = 0; i < filelist1.size(); i++) {
                map = new HashMap<>();
                String filename = filelist1.get(i).get("TypeName") + "";
                filenames1.add(filename);
                map.put("Name", filename);
                map.put("Extension", "folder");
                folder.add(map);
            }
        }

        List<Map> filelist2 = new ArrayList<>();
        List<Map> files = new ArrayList<>();
        //通过行车资料分类表的Id去行车资料文件管理表找typeid找到文件名称
        filelist2 = UtilisClass.getFilelist2(dbOpenHelper, fatherfileid);
        List<String> filenames2 = new ArrayList<>();//行车资料--->文 件名称列表
        if (filelist2.size() != 0) {
            Map<String, String> map;
            for (int i = 0; i < filelist2.size(); i++) {
                map = new HashMap<>();
                String filename = filelist2.get(i).get("FileName") + "";
                String origin = filelist2.get(i).get("OriginFilePath").toString().split("/")[4];
                String str =filename.substring(0,filename.lastIndexOf("."));
                String str1 = origin.substring(origin.lastIndexOf('.'),origin.length());
                filename = str+str1;
                filenames2.add(filename);
                map.put("Name", filename);
                map.put("Extension", str1);
                files.add(map);
            }
        }

        filelist.addAll(filelist1);
        filelist.addAll(filelist2);
        filenames.addAll(filenames1);
        filenames.addAll(filenames2);
        fileslists.addAll(folder);
        fileslists.addAll(files);

        if (showFilesAdapter == null) {
            showFilesAdapter = new ShowFilesAdapter(this, fileslists);
            filesShow.setAdapter(showFilesAdapter);
        } else {
            showFilesAdapter.setData(fileslists);
            filesShow.setAdapter(showFilesAdapter);
        }
        final List<Map> finalFilelist = filelist1;
        final List<Map> finalFilelist1 = filelist2;
        filesShow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i < finalFilelist.size()) {
                    String id = finalFilelist.get(i).get("Id") + "";
                    String path = finalFilelist.get(i).get("TypeName") + "";
                    everyfilepath.add(path);
                    pathtextView.setText(setfilepathshow(everyfilepath));
                    evetyfilefatherId.add(id);
                    setFilesShow(id);
                } else {
                    String fileExtension = filelist.get(i).get("FileExtension") + "";//获取文件类型
                    fileExtension.toLowerCase();
                    if (!fileExtension.equals("")) {
                        if (fileExtension.equals(".zip") || fileExtension.equals(".htm") || fileExtension.equals(".html")) {
                            String filepath = finalFilelist1.get(i - finalFilelist.size()).get("LocaPath") + "";
                            if (filepath != null && !filepath.equals("") && !filepath.equals("null")) {
                                Intent htmlintent = new Intent(FileShowActivity.this, HtmlWebView.class);
                                htmlintent.putExtra("FilePath", filepath);
                                startActivity(htmlintent);
                                recordOperateLog(9, "查看：" + finalFilelist1.get(i - finalFilelist.size()).get("FileName"));
                            } else {
                                UtilisClass.showToast(FileShowActivity.this, "未找到该文件！");
                            }
                        } else if (fileExtension.equals(".mp4") || fileExtension.equals(".avi")
                                || fileExtension.equals(".mpg") || fileExtension.equals(".wmv")) {
                            String filepath = finalFilelist1.get(i - finalFilelist.size()).get("LocaPath") + "";
                            if (filepath != null && !filepath.equals("") && !filepath.equals("null")) {
                                Intent videointent = new Intent(Intent.ACTION_VIEW);
                                videointent.setDataAndType(Uri.parse(filepath), "video/*");
                                startActivity(videointent);
                                recordOperateLog(9, "查看：" + finalFilelist1.get(i - finalFilelist.size()).get("FileName"));
                            } else {
                                UtilisClass.showToast(FileShowActivity.this, "未找到该文件！");
                            }
                        } else if (fileExtension.equals(".swf") || fileExtension.equals(".exe") || fileExtension.equals(".mov")
                                || fileExtension.equals(".vsd") || fileExtension.equals(".rmvb")) {
                            UtilisClass.showToast(FileShowActivity.this, "暂不支持该类型文件打开！");
                        } else if (fileExtension.equals(".docx") || fileExtension.equals(".pptx") || fileExtension.equals(".pdf")
                                || fileExtension.equals(".doc") || fileExtension.equals(".ppt") || fileExtension.equals(".xls")
                                || fileExtension.equals(".xlsx") || fileExtension.equals(".ppsx") || fileExtension.equals(".wps")) {
                            String filepath = finalFilelist1.get(i - finalFilelist.size()).get("LocaPath") + "";
                            if (filepath != null && !filepath.equals("") && !filepath.equals("null")) {
                                recordOperateLog(9, "查看：" + finalFilelist1.get(i - finalFilelist.size()).get("FileName"));
                                Wpsutils.wpsOpenFile(filepath, FileShowActivity.this);
                            } else {
                                UtilisClass.showToast(FileShowActivity.this, "未找到该文件！");
                            }
                        } else if (fileExtension.equals(".png") || fileExtension.equals(".jpg")
                                || fileExtension.equals(".bmp") || fileExtension.equals(".jpeg")) {
                            String filepath = finalFilelist1.get(i - finalFilelist.size()).get("LocaPath") + "";
                            if (filepath != null && !filepath.equals("") && !filepath.equals("null")) {
                                Intent imageintent = new Intent();
                                imageintent.setAction(android.content.Intent.ACTION_VIEW);
                                imageintent.setDataAndType(Uri.parse("file://" + filepath), "image/*");
                                startActivity(imageintent);
                                recordOperateLog(9, "查看：" + finalFilelist1.get(i - finalFilelist.size()).get("FileName"));
                            } else {
                                UtilisClass.showToast(FileShowActivity.this, "未找到该文件！");
                            }
                        } else {
                            UtilisClass.showToast(FileShowActivity.this, "未找到该文件！");
                        }
                    }
                }
            }
        });
    }

    private void recordOperateLog(int LogType, String LogContent) {
        Date dt = new Date();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String nowTime = df.format(dt);
        dbOpenHelper.insert("AppOperateLog", new String[]{"LogType", "LogContent", "OperatorId", "DeviceId", "AddTime"},
                new Object[]{LogType, LogContent, systemConfig.getOperatorId(), 0, nowTime});
    }

    @Override
    public void onBackPressed() {
        if (everyfilepath.size() == 1) {
            super.onBackPressed();
            finish();
        } else {
            evetyfilefatherId.remove(evetyfilefatherId.size() - 1);
            everyfilepath.remove(everyfilepath.size() - 1);
            pathtextView.setText(setfilepathshow(everyfilepath));
            setFilesShow(evetyfilefatherId.get(evetyfilefatherId.size() - 1));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.search_file_back:
                if (everyfilepath.size() == 1) {
                    super.onBackPressed();
                    finish();
                } else {
                    evetyfilefatherId.remove(evetyfilefatherId.size() - 1);
                    everyfilepath.remove(everyfilepath.size() - 1);
                    pathtextView.setText(setfilepathshow(everyfilepath));
                    setFilesShow(evetyfilefatherId.get(evetyfilefatherId.size() - 1));
                }
                break;
        }
    }

    private String setfilepathshow(List<String> list) {
        StringBuffer s = new StringBuffer();
        for (int i = 0; i < list.size(); i++) {
            s.append(list.get(i));
            s.append("〉");
        }
        s.deleteCharAt(s.lastIndexOf("〉"));
        String showpath = s.toString();
        return showpath;
    }

    @Override
    protected void onDestroy() {
        setContentView(R.layout.view_null);
        super.onDestroy();
        Myapplilcation.removeActivity(this);
    }
}
