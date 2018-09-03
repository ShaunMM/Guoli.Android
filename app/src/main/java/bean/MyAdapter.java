package bean;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import zj.com.mc.R;

/*
* 系统工具-->记事本Adapter
* */
public class MyAdapter extends BaseAdapter {

    LayoutInflater inflater;
    ArrayList<Notes> array;

    public MyAdapter(LayoutInflater inf, ArrayList<Notes> arry) {
        this.inflater = inf;
        this.array = arry;
    }

    @Override
    public int getCount() {
        return array.size();
    }

    @Override
    public Object getItem(int position) {
        return array.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.note_item, null);
            viewHolder.tv_site = (TextView) convertView.findViewById(R.id.tv_site);
            viewHolder.tv_notetitle = (TextView) convertView.findViewById(R.id.tv_notetitle);
            viewHolder.tv_notecontent = (TextView) convertView.findViewById(R.id.tv_notecontent);
            viewHolder.tv_notetime = (TextView) convertView.findViewById(R.id.tv_notetime);
            convertView.setTag(viewHolder);
        }
        viewHolder = (ViewHolder) convertView.getTag();
        if (array.get(position).getCurrentSite().equals("null")){
            viewHolder.tv_site.setText("");
        }else{
            viewHolder.tv_site.setText(array.get(position).getCurrentSite());
        }
        viewHolder.tv_notetitle.setText(array.get(position).getTitle());
        viewHolder.tv_notecontent.setText(array.get(position).getContent());
        viewHolder.tv_notetime.setText(array.get(position).getTimes());
        return convertView;
    }

    class ViewHolder {
        TextView tv_site;
        TextView tv_notetitle;
        TextView tv_notecontent;
        TextView tv_notetime;
    }
}
