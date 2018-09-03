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
 * Created by BYJ on 2017/7/29.
 */

public class DriverDataAdapter extends BaseAdapter {

    private Context context;
    private List<String> filesTypeNames;
    private LayoutInflater inflater = null;

    public DriverDataAdapter(Context context, List<String> filesTypeNames) {
        this.context = context;
        this.filesTypeNames = filesTypeNames;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData(List<String> filesTypeNames) {
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
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FunctionHolder functionHolder = null;
        if (convertView == null) {
            functionHolder = new FunctionHolder();
            convertView = inflater.inflate(R.layout.item_driverdata, null);
            functionHolder.gv_itemtext = (TextView) convertView.findViewById(R.id.gv_itemtext);
            convertView.setTag(functionHolder);
        } else {
            functionHolder = (FunctionHolder) convertView.getTag();
        }

        functionHolder.gv_itemtext.setText(filesTypeNames.get(position));
        return convertView;
    }
}

