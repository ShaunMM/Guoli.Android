package Utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import zj.com.mc.BridgeActivity;
import zj.com.mc.HtmlWebView;
import zj.com.mc.R;

import zj.com.mc.UtilisClass;


/**
 * Created by BYJ on 2017/2/10.
 * 本地查询Adapert
 */

public class SerchLocalFileAdapert extends BaseAdapter {

    private Context context;
    private List<Map> fileList;
    private LayoutInflater inflater = null;

    public SerchLocalFileAdapert(Context context, List<Map> fileList) {
        this.context = context;
        this.fileList = fileList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData(List<Map> fileList) {
        this.fileList = fileList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return fileList.size();
    }

    @Override
    public Object getItem(int position) {
        if (fileList != null) {
            return fileList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        SerchHolder serchHolder = null;
        if (convertView == null) {
            serchHolder = new SerchHolder();
            convertView = inflater.inflate(R.layout.driverdatasearch3, null);
            serchHolder.search_result_filename = (TextView) convertView.findViewById(R.id.search_result_filename);
            serchHolder.result_fileaddtime = (TextView) convertView.findViewById(R.id.result_fileaddtime);
//            serchHolder.local_result_click = (TextView) convertView.findViewById(R.id.local_result_click);
            convertView.setTag(serchHolder);
        } else {
            serchHolder = (SerchHolder) convertView.getTag();
        }

        serchHolder.search_result_filename.setText(fileList.get(position).get("FileName").toString());
        serchHolder.result_fileaddtime.setText(fileList.get(position).get("AddTime").toString());
//        serchHolder.local_result_click.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                String fileExtension = fileList.get(position).get("FileExtension") + "";
//                fileExtension.toLowerCase();
//
//                if (!fileExtension.equals("")) {
//                    if (fileExtension.equals(".zip") || fileExtension.equals(".htm") || fileExtension.equals(".html")) {
//                        String filepath = fileList.get(position).get("LocaPath") + "";
//                        if (filepath != null && !filepath.equals("") && !filepath.equals("null")) {
//                            Intent htmlintent = new Intent(context, HtmlWebView.class);
//                            htmlintent.putExtra("FilePath", filepath);
//                            context.startActivity(htmlintent);
//                        } else {
//                            UtilisClass.showToast(context, "未找到该文件！");
//                        }
//                    } else if (fileExtension.equals(".mp4") || fileExtension.equals(".avi")
//                            || fileExtension.equals(".mpg") || fileExtension.equals(".wmv")) {
//                        String filepath = fileList.get(position).get("LocaPath") + "";
//                        if (filepath != null && !filepath.equals("") && !filepath.equals("null")) {
//                            Intent videointent = new Intent(Intent.ACTION_VIEW);
//                            videointent.setDataAndType(Uri.parse(filepath), "video/*");
//                            context.startActivity(videointent);
//                        } else {
//                            UtilisClass.showToast(context, "未找到该文件！");
//                        }
//                    } else if (fileExtension.equals(".swf") || fileExtension.equals(".exe") || fileExtension.equals(".mov")
//                            || fileExtension.equals(".vsd") || fileExtension.equals(".rmvb")) {
//                        UtilisClass.showToast(context, "暂不支持该类型文件打开！");
//                    } else if (fileExtension.equals(".docx") || fileExtension.equals(".pptx") || fileExtension.equals(".pdf")
//                            || fileExtension.equals(".doc") || fileExtension.equals(".ppt") || fileExtension.equals(".xls")
//                            || fileExtension.equals(".xlsx") || fileExtension.equals(".ppsx") || fileExtension.equals(".wps")) {
//                        String filepath = fileList.get(position).get("LocaPath") + "";
//                        if (filepath != null && !filepath.equals("") && !filepath.equals("null")) {
//                            Intent bridgeintent = new Intent(context, BridgeActivity.class);
//                            Bundle bundle = new Bundle();
//                            bundle.putString("FilePath", filepath);
//                            bridgeintent.putExtras(bundle);
//                            context.startActivity(bridgeintent);
//                        } else {
//                            UtilisClass.showToast(context, "未找到该文件！");
//                        }
//                    } else if (fileExtension.equals(".png") || fileExtension.equals(".jpg")
//                            || fileExtension.equals(".bmp") || fileExtension.equals(".jpeg")) {
//                        String filepath = fileList.get(position).get("LocaPath") + "";
//                        if (filepath != null && !filepath.equals("") && !filepath.equals("null")) {
//                            Intent imageintent = new Intent();
//                            imageintent.setAction(android.content.Intent.ACTION_VIEW);
//                            imageintent.setDataAndType(Uri.parse("file://" + filepath), "image/*");
//                            context.startActivity(imageintent);
//                        } else {
//                            UtilisClass.showToast(context, "未找到该文件！");
//                        }
//                    } else if (fileExtension.equals(".swf") || fileExtension.equals(".exe") || fileExtension.equals(".mov")
//                            || fileExtension.equals(".vsd") || fileExtension.equals(".rmvb")) {
//                        UtilisClass.showToast(context, "暂不支持该类型文件打开！");
//                    }
//                }
//            }
//        });

        return convertView;
    }

    public class SerchHolder {
        public TextView result_fileaddtime;
        public TextView search_result_filename;
//        public TextView local_result_click;
    }
}



