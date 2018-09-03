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
 * 近期手账记录Adapter
 */
public class CarKeepAdapter extends BaseAdapter {
    private List<?> list;
    private Context context;
    private onbtnclick click;

    public CarKeepAdapter(List<?> list, Context context) {
        this.list = list;
        this.context = context;
    }

    public void setOnbtnClick(onbtnclick click) {
        this.click = click;
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

    public void setList(List<?> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Map map = (Map) list.get(position);
        ViewHoder viewHoder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_carkeep, null);
            viewHoder = new ViewHoder();
            viewHoder.tv_up = (TextView) convertView.findViewById(R.id.tv_up);
            viewHoder.tv_modify = (TextView) convertView.findViewById(R.id.tv_modify);
            viewHoder.tv_upsucsses = (TextView) convertView.findViewById(R.id.tv_upsucsses);
            viewHoder.tv_carline = (TextView) convertView.findViewById(R.id.tv_carline);
            viewHoder.tv_carcode = (TextView) convertView.findViewById(R.id.tv_carcode);
            viewHoder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            viewHoder.v = convertView.findViewById(R.id.view_line);
            convertView.setTag(viewHoder);
        } else {
            viewHoder = (ViewHoder) convertView.getTag();
        }

        viewHoder.tv_carline.setText(String.valueOf(map.get("LineName")));
        viewHoder.tv_carcode.setText(String.valueOf(map.get("TrainCode")));
        viewHoder.tv_time.setText(String.valueOf(map.get("AttendTime")));
        String IsUploaded = String.valueOf(map.get("IsUploaded"));

        if ("0".equals(IsUploaded)) {
            viewHoder.tv_upsucsses.setVisibility(View.GONE);
            viewHoder.v.setVisibility(View.INVISIBLE);
            viewHoder.tv_modify.setVisibility(View.VISIBLE);
            viewHoder.tv_up.setVisibility(View.VISIBLE);
        } else {
            viewHoder.v.setVisibility(View.VISIBLE);
            viewHoder.tv_upsucsses.setVisibility(View.VISIBLE);
            viewHoder.tv_modify.setVisibility(View.GONE);
            viewHoder.tv_up.setVisibility(View.GONE);
        }

        //上传行车记录
        viewHoder.tv_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click.onUpbtnClick(v, position);
            }
        });

        //修改行车记录
        viewHoder.tv_modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click.onModifybtnClick(v, position);
            }
        });

        return convertView;
    }

    static class ViewHoder {
        TextView tv_up, tv_modify, tv_upsucsses, tv_time, tv_carline, tv_carcode;
        View v;
    }

    public interface onbtnclick {
        public void onUpbtnClick(View v, int position);

        public void onModifybtnClick(View v, int position);
    }
}
