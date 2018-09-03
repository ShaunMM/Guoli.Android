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
 * 今日行车计划Adapter
 */
public class CarplanAdapter extends BaseAdapter {
    private List<Map> drivePlanList;
    private Context context;
    private ViewHoder viewHoder;

    public CarplanAdapter(List<Map> drivePlanList, Context context) {
        this.drivePlanList = drivePlanList;
        this.context = context;
    }

    public void setData(List<Map> drivePlanList) {
        this.drivePlanList = drivePlanList;
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return drivePlanList.size();
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
        Map map = (Map) drivePlanList.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_carplan, null);
            viewHoder = new ViewHoder();
            viewHoder.tv_driveplan = (TextView) convertView.findViewById(R.id.tv_driveplan);
            viewHoder.tv_traincode = (TextView) convertView.findViewById(R.id.tv_traincode);
            convertView.setTag(viewHoder);
        } else {
            viewHoder = (ViewHoder) convertView.getTag();
        }

        viewHoder.tv_traincode.setText(String.valueOf(map.get("TrainCode")));
        viewHoder.tv_driveplan.setText(String.valueOf(map.get("LineName")));

        return convertView;
    }

    static class ViewHoder {
        TextView tv_driveplan, tv_traincode;
    }
}
