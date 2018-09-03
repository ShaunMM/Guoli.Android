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
 * Created by BYJ on 2017/10/24.
 */

public class CompleteFilesAdapter extends BaseAdapter {
    private Context context;
    private List<Map> list;

    public CompleteFilesAdapter(Context context, List<Map> list) {
        this.context = context;
        this.list = list;
    }

    public void setData(List<Map> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CompleteFilesHolder completeFilesHolder = null;
        if (convertView == null) {
            completeFilesHolder = new CompleteFilesHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_completefile, null);
            completeFilesHolder.tv_comfilename = (TextView) convertView.findViewById(R.id.tv_comfilename);

            convertView.setTag(completeFilesHolder);
        } else {
            completeFilesHolder = (CompleteFilesHolder) convertView.getTag();
        }
        completeFilesHolder.tv_comfilename.setText(list.get(position).get("FileName").toString());
        return convertView;
    }

    public class CompleteFilesHolder {
        public TextView tv_comfilename;
    }
}
