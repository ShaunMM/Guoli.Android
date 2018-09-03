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
 * Created by BYJ on 2017/7/30.
 */
public class ShowFilesAdapter extends BaseAdapter {

    private Context context;
    private List<Map> filesTypeNames;

    public ShowFilesAdapter(Context context, List<Map> filesTypeNames) {
        this.context = context;
        this.filesTypeNames = filesTypeNames;
    }

    public void setData(List<Map> filesTypeNames) {
        this.filesTypeNames = filesTypeNames;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return filesTypeNames.size();
    }

    @Override
    public Object getItem(int position) {
        if (filesTypeNames != null) {
            return filesTypeNames.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class FunctionHolder {
        public TextView gv_itemtext;
        public TextView fileicn;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Map map = filesTypeNames.get(position);
        String fileExtension = map.get("Extension").toString();
        fileExtension.toLowerCase();
        FunctionHolder functionHolder = null;
        if (convertView == null) {
            functionHolder = new FunctionHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.fileshowlistview, null);
            functionHolder.gv_itemtext = (TextView) convertView.findViewById(R.id.fileshow_listviewitem);
            functionHolder.fileicn = (TextView) convertView.findViewById(R.id.fileshow_fileicn);
            convertView.setTag(functionHolder);
        } else {
            functionHolder = (FunctionHolder) convertView.getTag();
        }

        functionHolder.gv_itemtext.setText(map.get("Name").toString());
        if (fileExtension.equals(".mp4") || fileExtension.equals(".avi") || fileExtension.equals(".swf")
                || fileExtension.equals(".exe") || fileExtension.equals(".wmv") || fileExtension.equals(".mpg")
                || fileExtension.equals(".rmvb")) {
            functionHolder.fileicn.setBackgroundResource(R.mipmap.video);
        } else if (fileExtension.equals(".docx") || fileExtension.equals(".pptx") || fileExtension.equals(".pdf")
                || fileExtension.equals(".doc") || fileExtension.equals(".ppt") || fileExtension.equals(".xls")
                || fileExtension.equals(".xlsx") || fileExtension.equals(".ppsx") || fileExtension.equals(".wps")
                || fileExtension.equals(".html") || fileExtension.equals(".htm") || fileExtension.equals(".txt")) {
            functionHolder.fileicn.setBackgroundResource(R.mipmap.document);
        } else if (fileExtension.equals(".png") || fileExtension.equals(".jpg")
                || fileExtension.equals(".bmp") || fileExtension.equals(".jpeg") || fileExtension.equals(".vsd")) {
            functionHolder.fileicn.setBackgroundResource(R.mipmap.pic);
        } else if (fileExtension.equals(".zip")) {
            functionHolder.fileicn.setBackgroundResource(R.mipmap.zip);
        } else {
            functionHolder.fileicn.setBackgroundResource(R.mipmap.folder);
        }
        return convertView;
    }
}


