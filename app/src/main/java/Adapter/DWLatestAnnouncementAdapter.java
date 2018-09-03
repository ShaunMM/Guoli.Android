package Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import zj.com.mc.R;

/**
 * Created by BYJ on 2017/3/13.
 * 指导司机首页 最新公告Adapter
 */
public class DWLatestAnnouncementAdapter extends BaseAdapter {
    private Context context;
    private List<Map> fileList;
    private LayoutInflater inflater = null;

    public DWLatestAnnouncementAdapter(Context context, List<Map> fileList) {
        this.context = context;
        this.fileList = fileList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setList(List<Map> fileList) {
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
    public View getView(int position, View convertView, ViewGroup parent) {
        FileInfoHolder fileInfoHolder = null;
        fileInfoHolder = new FileInfoHolder();
        convertView = inflater.inflate(R.layout.adapter_dailyworkfileinfo, null);
        fileInfoHolder.tv_infoName = (TextView) convertView.findViewById(R.id.tv_infoName);
        fileInfoHolder.tv_infoTime = (TextView) convertView.findViewById(R.id.tv_infoTime);
        convertView.setTag(fileInfoHolder);

        if (fileList.get(position).get("IsRead").toString().equals("0")) {
            fileInfoHolder.tv_infoName.setTextColor(Color.RED);
        }
        if (fileList.get(position).get("FileName").toString().length() == 0) {
            fileInfoHolder.tv_infoName.setText(fileList.get(position).get("Title").toString());
        } else {
            fileInfoHolder.tv_infoName.setText(fileList.get(position).get("FileName").toString());
        }
        fileInfoHolder.tv_infoTime.setText(fileList.get(position).get("PubTime").toString());

        return convertView;
    }

    public class FileInfoHolder {
        public TextView tv_infoName;
        public TextView tv_infoTime;
    }
}
