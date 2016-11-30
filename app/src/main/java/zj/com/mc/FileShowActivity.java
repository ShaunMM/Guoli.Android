package zj.com.mc;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import DBUtils.DBOpenHelper;
import Utils.CommonAdapter;
import Utils.ViewHolder;
import WPSutils.Wpsutils;

/**
 * Created by dell on 2016/9/19.
 */
public class FileShowActivity extends Activity implements View.OnClickListener{

    private ListView filesShow;
    private TextView pathtextView;
    private DBOpenHelper dbOpenHelper;
    private String path;
    private String basefleid;
    private List<String> everyfilepath;
    private List<String> evetyfilefatherId;
    private List<Map> filelist;
    private List<String> filenames  ;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fileshowactivitylayout);
        filesShow= (ListView) findViewById(R.id.search_showfiles);
        pathtextView= (TextView) findViewById(R.id.fileshow_filepath);
        title= (TextView) findViewById(R.id.fileshow_title);
        dbOpenHelper=DBOpenHelper.getInstance(getApplicationContext());
        findViewById(R.id.search_file_back).setOnClickListener(this);
        everyfilepath=new ArrayList<>();
        evetyfilefatherId=new ArrayList<>();
        Intent intent=getIntent();
        Bundle bundle=intent.getBundleExtra("bundle");
        basefleid=bundle.getString("Id");
        System.out.println("2222222220>>>"+basefleid);
        String fileName=bundle.getString("filename");
        path=fileName;
        title.setText(path);
        pathtextView.setText(path);
        filelist    =new ArrayList<>();
        filenames      =new ArrayList<>();
        setFilesShow(basefleid);
        everyfilepath.add(path);
        evetyfilefatherId.add(basefleid);
    }
    private void setFilesShow(String fatherfileid){

        filelist.clear();
        filenames.clear();

         List<Map> filelist1=new ArrayList<>();

                filelist1=UtilisClass.getFilelist(dbOpenHelper,fatherfileid);//行车资料文件表     展示的文件夹
            List<String> filenames1=new ArrayList<>();//行车资料文件夹名称列表
        if (filelist1.size()!=0) {

            for (int i = 0; i < filelist1.size(); i++) {
                    String filename=filelist1.get(i).get("TypeName")+"";
                filenames1.add(filename);
            }
        }else {
        }
        List<Map> filelist2=new ArrayList<>();
        filelist2=UtilisClass.getFilelist2(dbOpenHelper,fatherfileid);
        List<String> filenames2=new ArrayList<>();//行车资料文件名称列表
        if (filelist2.size()!=0) {
            for (int i = 0; i < filelist2.size(); i++) {
                String filename=filelist2.get(i).get("FileName")+"";
                System.out.println("LLLLLLLLLLLL"+filelist2.toString());
//                String filename2=filename.substring(0,filename.length()-4)+"";
//                filenames2.add(filename2);
                filenames2.add(filename);
            }
        }else {
        }
        filelist.addAll(filelist1);
        filelist.addAll(filelist2);
        filenames.addAll(filenames1);
        filenames.addAll(filenames2);
//        UtilisClass.showToast(FileShowActivity.this,"点击了"+filelist.size()+"????"+filenames.size());
        filesShow.setAdapter(new CommonAdapter<String>(FileShowActivity.this,filenames,R.layout.fileshowlistview) {
            @Override
            protected void convertlistener(ViewHolder holder, String s) {
            }

            @Override
            public void convert(ViewHolder holder, String s) {
               int dex;
                String htm=".htm";
                String zip=".zip";

                if ((dex=s.indexOf(htm))!=-1){

                    holder.getView(R.id.fileshow_fileicn).setBackground(getResources().getDrawable(R.drawable.htm));
                    String filename2=s.substring(0,s.length()-4)+"";
                    holder.setText(R.id.fileshow_listviewitem,filename2);
                }else if ((dex=s.indexOf(zip))!=-1){
                    holder.getView(R.id.fileshow_fileicn).setBackground(getResources().getDrawable(R.drawable.htm));
                    String filename2=s.substring(0,s.length()-4)+"";
                    holder.setText(R.id.fileshow_listviewitem,filename2);
                }else {
                    holder.setText(R.id.fileshow_listviewitem,s);
                }
            }
        });

        final List<Map> finalFilelist = filelist1;
        final List<Map> finalFilelist1 = filelist2;
        filesShow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (i< finalFilelist.size()){
                    String id= finalFilelist.get(i).get("Id")+"";
                    String path= finalFilelist.get(i).get("TypeName")+"";
                    everyfilepath.add(path);
                    pathtextView.setText(setfilepathshow(everyfilepath));
//                    System.out.println(">>>>>>>>>>>>>>"+path);
                    evetyfilefatherId.add(id);
//                    System.out.println(">>>>>>>>>>>>>>"+id);
                    setFilesShow(id);
                }else {
//                    String fileExtension=filelist2.get(i+1-filelist1.size()).get("FileExtension")+"";//获取文件类型
                    String fileExtension=filelist.get(i).get("FileExtension")+"";//获取文件类型
                    fileExtension.toLowerCase();
                    if (!fileExtension.equals("")){
                        if (fileExtension.equals(".zip")||fileExtension.equals(".htm")) {
                            String filepath = finalFilelist1.get(i - finalFilelist.size()).get("LocaPath") + "";
                            if (filepath!=null&&!filepath.equals("")) {
                                Intent htmlintent = new Intent(FileShowActivity.this, HtmlWebView.class);
//                                UtilisClass.showToast(FileShowActivity.this, "点击了" + filepath);
                                htmlintent.putExtra("FilePath", filepath);
                                startActivity(htmlintent);
                            }else {
                                UtilisClass.showToast(FileShowActivity.this,"未找到该文件！");
                            }
                        }
                        else if (fileExtension.equals(".mp4")){

                            String filepath = finalFilelist1.get(i - finalFilelist.size()).get("LocaPath") + "";

                            if (filepath!=null&&!filepath.equals("")) {

                                Intent videointent=new Intent(Intent.ACTION_VIEW);
                                videointent.setDataAndType(Uri.parse(filepath),"video/mp4");
                                startActivity(videointent);
//                                WordPptExcelUtils.getVideoFileIntent(filepath);

                            }else {
                                UtilisClass.showToast(FileShowActivity.this,"未找到该文件！");
                            }
                        }else if(fileExtension.equals(".doc")||fileExtension.equals(".ppt")||fileExtension.equals(".xls")){

                            String filepath = finalFilelist1.get(i - finalFilelist.size()).get("LocaPath") + "";

                            if (filepath!=null&&!filepath.equals("")) {
                                Wpsutils.wpsOpenFile(filepath,FileShowActivity.this);
                            }else {
                                UtilisClass.showToast(FileShowActivity.this,"未找到该文件！");
                            }

                        }else if (fileExtension.equals(".png")||fileExtension.equals(".jpg")){
                            String filepath = finalFilelist1.get(i - finalFilelist.size()).get("LocaPath") + "";

                            if (filepath!=null&&!filepath.equals("")) {
                                Intent imageintent = new Intent(Intent.ACTION_VIEW);
                                imageintent.setDataAndType(Uri.parse(filepath),"image/*");
                                startActivity(imageintent);

                            }else {
                                UtilisClass.showToast(FileShowActivity.this,"未找到该文件！");
                            }

                        }else {
                            UtilisClass.showToast(FileShowActivity.this,"未找到该文件！");
                        }
                    }else {}
                }


            }
        });

    }



    @Override
    public void onBackPressed() {
        if (everyfilepath.size()==1){
            super.onBackPressed();
            finish();
        }else {
            evetyfilefatherId.remove(evetyfilefatherId.size()-1);
            everyfilepath.remove(everyfilepath.size()-1);
//            System.out.println(">>>>>>>>>>>>>>"+everyfilepath.toString());
            pathtextView.setText(setfilepathshow(everyfilepath));
            setFilesShow(evetyfilefatherId.get(evetyfilefatherId.size()-1));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.search_file_back:
                if (everyfilepath.size()==1){
                    super.onBackPressed();
                    finish();
                }else {
                    evetyfilefatherId.remove(evetyfilefatherId.size()-1);
                    everyfilepath.remove(everyfilepath.size()-1);
                    pathtextView.setText(setfilepathshow(everyfilepath));
                    setFilesShow(evetyfilefatherId.get(evetyfilefatherId.size()-1));





                }
                break;

        }
    }

    private String setfilepathshow(List<String> list){
        StringBuffer s=new StringBuffer();
        for (int i=0;i<list.size(); i++){
            s.append(list.get(i));
            s.append("〉");
        }
        s.deleteCharAt(s.lastIndexOf("〉"));
        String showpath=s.toString();

        return showpath;
    }
}
