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
 * Created by Dell on 2017/8/23.
 */

public class VideoImageAdapter extends BaseAdapter {

    private Context context;
    private List<Map> filesTypeNames;
    private LayoutInflater inflater = null;

    public VideoImageAdapter(Context context, List<Map> filesTypeNames) {
        this.context = context;
        this.filesTypeNames = filesTypeNames;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        FunctionHolder functionHolder = null;
        if (convertView == null) {
            functionHolder = new FunctionHolder();
            convertView = inflater.inflate(R.layout.fileshowlistview, null);
            functionHolder.gv_itemtext = (TextView) convertView.findViewById(R.id.fileshow_listviewitem);
            functionHolder.fileicn = (TextView) convertView.findViewById(R.id.fileshow_fileicn);
            convertView.setTag(functionHolder);
        } else {
            functionHolder = (FunctionHolder) convertView.getTag();
        }

        functionHolder.gv_itemtext.setText(map.get("Name").toString());

        if (fileExtension.equals(".mp4")) {
            functionHolder.fileicn.setBackgroundResource(R.mipmap.video);
        } else if (fileExtension.equals(".3gpp")) {
            functionHolder.fileicn.setBackgroundResource(R.mipmap.audio);
        } else if (fileExtension.equals(".png") || fileExtension.equals(".jpg")) {
            functionHolder.fileicn.setBackgroundResource(R.mipmap.pic);
        } else {
            functionHolder.fileicn.setBackgroundResource(R.mipmap.folder);
        }

        return convertView;
    }
}
