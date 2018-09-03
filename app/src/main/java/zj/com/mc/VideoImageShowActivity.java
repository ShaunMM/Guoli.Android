package zj.com.mc;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Adapter.VideoImageAdapter;
import Utils.NetUtils;
import Utils.SDCardHelper;

/**
 * Created by BYJ on 2017/8/23.
 */

public class VideoImageShowActivity extends Activity implements View.OnClickListener {

    private ListView folderShow;
    private ListView filesShow;
    private TextView pathtextView;
    private TextView title;
    private VideoImageAdapter videoImageAdapter;
    private List<Map> fileClass;
    private List<Map> photoFiles = new ArrayList<>();
    private List<Map> videoFiles = new ArrayList<>();
    private List<Map> audioFiles = new ArrayList<>();
    private String folderType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videoimageshow);
        Myapplilcation.addActivity(this);
        initView();
        initData();
    }

    private void initView() {
        folderShow = (ListView) findViewById(R.id.videoimageshow_folder);
        filesShow = (ListView) findViewById(R.id.videoimageshow_files);
        pathtextView = (TextView) findViewById(R.id.videoimageshow_path);
        title = (TextView) findViewById(R.id.videoimageshow_title);
        title.setText("音视频照片文件浏览");
        pathtextView.setVisibility(View.GONE);
        findViewById(R.id.videoimageshow_back).setOnClickListener(this);
    }

    private void initData() {
        getPhotoFileName(SDCardHelper.fileSdkPath(NetUtils.PHOTOPATH));
        getAudioFileName(SDCardHelper.fileSdkPath(NetUtils.AUDIOPATH));
        getVideoFileName(SDCardHelper.fileSdkPath(NetUtils.VIDEOPATH));

        fileClass = new ArrayList<>();
        Map map = new HashMap();
        map.put("Extension", "folder");
        map.put("Name", "音频文件 (" + audioFiles.size() + ")");
        Map map1 = new HashMap();
        map1.put("Extension", "folder");
        map1.put("Name", "视频文件 (" + videoFiles.size() + ")");
        Map map2 = new HashMap();
        map2.put("Extension", "folder");
        map2.put("Name", "图片文件 (" + photoFiles.size() + ")");

        fileClass.add(map);
        fileClass.add(map1);
        fileClass.add(map2);

        if (fileClass.size() != 0) {
            if (videoImageAdapter == null) {
                videoImageAdapter = new VideoImageAdapter(this, fileClass);
                folderShow.setAdapter(videoImageAdapter);
            } else {
                videoImageAdapter.setData(fileClass);
                folderShow.setAdapter(videoImageAdapter);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.videoimageshow_back:
                if (filesShow.isShown()) {
                    filesShow.setVisibility(View.GONE);
                    folderShow.setVisibility(View.VISIBLE);
                    if (fileClass.size() != 0) {
                        if (videoImageAdapter == null) {
                            videoImageAdapter = new VideoImageAdapter(this, fileClass);
                            folderShow.setAdapter(videoImageAdapter);
                        } else {
                            videoImageAdapter.setData(fileClass);
                            folderShow.setAdapter(videoImageAdapter);
                        }
                    }

                } else {
                    super.onBackPressed();
                    finish();
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        openFloder();
        openFiles();
    }

    private void openFloder() {
        folderShow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                folderShow.setVisibility(View.GONE);
                if (filesShow.getVisibility() == View.GONE) {
                    filesShow.setVisibility(View.VISIBLE);
                }
                if (i == 0) {
                    folderType = "audio";
                    if (audioFiles.size() != 0) {
                        if (videoImageAdapter == null) {
                            videoImageAdapter = new VideoImageAdapter(getApplicationContext(), audioFiles);
                            filesShow.setAdapter(videoImageAdapter);
                        } else {
                            videoImageAdapter.setData(audioFiles);
                            filesShow.setAdapter(videoImageAdapter);
                        }
                    }
                } else if (i == 1) {
                    folderType = "video";
                    if (videoFiles.size() != 0) {
                        if (videoImageAdapter == null) {
                            videoImageAdapter = new VideoImageAdapter(getApplicationContext(), videoFiles);
                            filesShow.setAdapter(videoImageAdapter);
                        } else {
                            videoImageAdapter.setData(videoFiles);
                            filesShow.setAdapter(videoImageAdapter);
                        }
                    }
                } else if (i == 2) {
                    folderType = "photo";
                    if (photoFiles.size() != 0) {
                        if (videoImageAdapter == null) {
                            videoImageAdapter = new VideoImageAdapter(getApplicationContext(), photoFiles);
                            filesShow.setAdapter(videoImageAdapter);
                        } else {
                            videoImageAdapter.setData(photoFiles);
                            filesShow.setAdapter(videoImageAdapter);
                        }
                    }
                }

            }
        });
    }

    private void openFiles() {

        filesShow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (folderType.equals("audio")) {
                    if (audioFiles.size() >= i + 1) {
                        Intent audiointent = new Intent(Intent.ACTION_VIEW);
                        audiointent.setDataAndType(Uri.parse("file://" + audioFiles.get(i).get("Filepath").toString()), "video/*");
                        startActivity(audiointent);
                    }

                } else if (folderType.equals("video")) {
                    if (videoFiles.size() >= i + 1) {
                        Intent videointent = new Intent(Intent.ACTION_VIEW);
                        videointent.setDataAndType(Uri.parse("file://" + videoFiles.get(i).get("Filepath").toString()), "video/*");
                        startActivity(videointent);
                    }

                } else if (folderType.equals("photo")) {
                    if (photoFiles.size() >= i + 1) {
                        Intent imageintent = new Intent();
                        imageintent.setAction(android.content.Intent.ACTION_VIEW);
                        imageintent.setDataAndType(Uri.parse("file://" + photoFiles.get(i).get("Filepath").toString()), "image/*");
                        startActivity(imageintent);
                    }
                }
            }
        });
    }

    // 获取当前目录下所有的.jpg文件
    private List<Map> getPhotoFileName(String fileAbsolutePath) {

        File file = new File(fileAbsolutePath);
        File[] subFile = file.listFiles();
        if (subFile.length > 0) {
            for (int iFileLength = 0; iFileLength < subFile.length; iFileLength++) {
                // 判断是否为文件夹
                if (!subFile[iFileLength].isDirectory()) {
                    String filename = subFile[iFileLength].getName();
                    String filepath = subFile[iFileLength].getAbsolutePath();
                    Map map = new HashMap();
                    map.put("Extension", ".jpg");
                    map.put("Name", filename);
                    map.put("Filepath", filepath);
                    photoFiles.add(map);
                }
            }
        }
        return photoFiles;
    }

    // 获取当前目录下所有的.3gpp文件
    private List<Map> getAudioFileName(String fileAbsolutePath) {

        File file = new File(fileAbsolutePath);
        File[] subFile = file.listFiles();
        if (subFile.length > 0) {
            for (int iFileLength = 0; iFileLength < subFile.length; iFileLength++) {
                // 判断是否为文件夹
                if (!subFile[iFileLength].isDirectory()) {
                    String filename = subFile[iFileLength].getName();
                    String filepath = subFile[iFileLength].getAbsolutePath();
                    Map map = new HashMap();
                    map.put("Extension", ".3gpp");
                    map.put("Name", filename);
                    map.put("Filepath", filepath);
                    audioFiles.add(map);
                }
            }
        }
        return audioFiles;
    }

    // 获取当前目录下所有的.mp4文件
    private List<Map> getVideoFileName(String fileAbsolutePath) {

        File file = new File(fileAbsolutePath);
        File[] subFile = file.listFiles();
        if (subFile.length > 0) {
            for (int iFileLength = 0; iFileLength < subFile.length; iFileLength++) {
                // 判断是否为文件夹
                if (!subFile[iFileLength].isDirectory()) {
                    String filename = subFile[iFileLength].getName();
                    String filepath = subFile[iFileLength].getAbsolutePath();
                    Map map = new HashMap();
                    map.put("Extension", ".mp4");
                    map.put("Name", filename);
                    map.put("Filepath", filepath);
                    videoFiles.add(map);
                }
            }
        }
        return videoFiles;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Myapplilcation.removeActivity(this);
    }
}
