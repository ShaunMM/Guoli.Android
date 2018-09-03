package Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import zj.com.mc.R;

/**
 * 选择出勤车次
 */
public class CarCodeNewAdapter extends BaseAdapter {
    private Context context;
    private List<?> list;
    private ViewHolder viewHolder;
    private int selectIndex = -1;

    public CarCodeNewAdapter(Context context, List<?> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public String getItem(int position) {
        if (selectIndex != -1) {
            Map map = (Map) list.get(selectIndex);
            return String.valueOf(map.get("TrainCode"));
        }

        return "";
    }

    public String getCode() {
        if (selectIndex != -1) {
            Map map = (Map) list.get(selectIndex);
            return String.valueOf(map.get("TrainCode"));
        }

        return "";
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setSelectIndex(int selectIndex) {
        this.selectIndex = selectIndex;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Map map = (Map) list.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_carcode_textview, null);
            viewHolder = new ViewHolder();
            viewHolder.rb = (TextView) convertView.findViewById(R.id.tv_code);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.rb.setText(String.valueOf(map.get("TrainCode")));

        if (position == selectIndex) {
            viewHolder.rb.setTextColor(Color.parseColor("#ffffff"));
            viewHolder.rb.setBackground(context.getResources().getDrawable(R.drawable.shap_rbtn_code_blue));
        } else {
            viewHolder.rb.setTextColor(Color.parseColor("#000000"));
            viewHolder.rb.setBackground(context.getResources().getDrawable(R.drawable.shap_rbtn_code));
        }
        return convertView;
    }

    static class ViewHolder {
        TextView rb;
    }
}
