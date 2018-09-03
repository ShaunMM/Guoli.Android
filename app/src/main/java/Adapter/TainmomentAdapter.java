package Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;
import java.util.Map;

import zj.com.mc.R;

/**
 * Created by mao on 2016/12/2.
 */

public class TainmomentAdapter extends BaseAdapter{
    private Context context;
    private List<?> list;
    private ViewHoder viewHoder;
    private int selectIndex=0;
    public TainmomentAdapter(Context context,List<?> list){
        this.context=context;
        this.list=list;
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

    public void setSelectIndex(int selectIndex){
        this.selectIndex=selectIndex;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Map map=(Map) list.get(position);
        if (convertView==null){
            convertView= LayoutInflater.from(context).inflate(R.layout.item_tainmoment,null);
            viewHoder=new ViewHoder();
            viewHoder.tv_StationName=(TextView) convertView.findViewById(R.id.tv_StationName);
            viewHoder.tv_ArriveTime=(TextView) convertView.findViewById(R.id.tv_ArriveTime);
            viewHoder.tv_DepartTime=(TextView) convertView.findViewById(R.id.tv_DepartTime);
            viewHoder.tv_SuggestSpeed=(TextView) convertView.findViewById(R.id.tv_SuggestSpeed);
            convertView.setTag(viewHoder);
        }else {
            viewHoder=(ViewHoder) convertView.getTag();
        }
        viewHoder.tv_StationName.setText(String.valueOf(map.get("StationName")));
        viewHoder.tv_ArriveTime.setText(String.valueOf(map.get("ArriveTime")));
        viewHoder.tv_DepartTime.setText(String.valueOf(map.get("DepartTime")));
        viewHoder.tv_SuggestSpeed.setText(String.valueOf(map.get("SuggestSpeed")));
        if (selectIndex==-1){
            viewHoder.tv_StationName.setTextColor(context.getResources().getColor(R.color.black));
            viewHoder.tv_ArriveTime.setTextColor(context.getResources().getColor(R.color.black));
            viewHoder.tv_DepartTime.setTextColor(context.getResources().getColor(R.color.black));
            viewHoder.tv_SuggestSpeed.setTextColor(context.getResources().getColor(R.color.black));
        }else {
            if (position==selectIndex){
                viewHoder.tv_StationName.setTextColor(context.getResources().getColor(R.color.black));
                viewHoder.tv_ArriveTime.setTextColor(context.getResources().getColor(R.color.black));
                viewHoder.tv_DepartTime.setTextColor(context.getResources().getColor(R.color.black));
                viewHoder.tv_SuggestSpeed.setTextColor(context.getResources().getColor(R.color.black));
            }else {
                viewHoder.tv_StationName.setTextColor(Color.parseColor("#999999"));
                viewHoder.tv_ArriveTime.setTextColor(Color.parseColor("#999999"));
                viewHoder.tv_DepartTime.setTextColor(Color.parseColor("#999999"));
                viewHoder.tv_SuggestSpeed.setTextColor(Color.parseColor("#999999"));
            }
        }
        return convertView;
    }

    static class ViewHoder{
        TextView tv_StationName,tv_ArriveTime,tv_DepartTime,tv_SuggestSpeed;
    }
}
