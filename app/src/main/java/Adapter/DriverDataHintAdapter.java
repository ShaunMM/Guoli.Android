package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;
import zj.com.mc.R;

/**
 * Created by BYJ on 2017/3/20.
 * 行车资料搜索提示Adapter
 */
public class DriverDataHintAdapter extends BaseAdapter {
    private Context context;
    private List<String> hintList;
    private LayoutInflater inflater = null;

    public DriverDataHintAdapter(Context context, List<String> hintList) {
        this.context = context;
        this.hintList = hintList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData(List<String> hintList) {
        this.hintList = hintList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return hintList.size();
    }

    @Override
    public Object getItem(int position) {
        if (hintList != null) {
            return hintList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HintHolder hintHolder = null;
        if (convertView == null) {
            hintHolder = new HintHolder();
            convertView = inflater.inflate(R.layout.adapter_ddhintitem, null);
            hintHolder.tv__hint = (TextView) convertView.findViewById(R.id.tv__hint);
            convertView.setTag(hintHolder);
        } else {
            hintHolder = (HintHolder) convertView.getTag();
        }
        hintHolder.tv__hint.setText(hintList.get(position).toString());
        return convertView;
    }

    public class HintHolder {
        public TextView tv__hint;
    }

}
