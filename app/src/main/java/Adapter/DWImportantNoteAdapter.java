package Adapter;

import android.content.Context;
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
 * 指导司机首页 重要提示Adapter
 */
public class DWImportantNoteAdapter extends BaseAdapter {

    private Context context;
    private List<Map> fileList;
    private LayoutInflater inflater = null;

    public DWImportantNoteAdapter(Context context, List<Map> fileList) {
        this.context = context;
        this.fileList = fileList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        if (convertView == null) {
            fileInfoHolder = new FileInfoHolder();
            convertView = inflater.inflate(R.layout.adapter_dailyworkfileinfo, null);
            fileInfoHolder.tv_infoName = (TextView) convertView.findViewById(R.id.tv_infoName);
            fileInfoHolder.tv_infoTime = (TextView) convertView.findViewById(R.id.tv_infoTime);
            convertView.setTag(fileInfoHolder);
        } else {
            fileInfoHolder = (FileInfoHolder) convertView.getTag();
        }

        fileInfoHolder.tv_infoName.setText(fileList.get(position).get("ExamName").toString());
        fileInfoHolder.tv_infoTime.setText(fileList.get(position).get("AddTime").toString());

        return convertView;
    }

    public class FileInfoHolder {
        public TextView tv_infoName;
        public TextView tv_infoTime;
    }
}
