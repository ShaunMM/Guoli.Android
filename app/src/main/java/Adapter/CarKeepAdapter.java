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
 * Created by mao on 2016/11/30.
 */

public class CarKeepAdapter extends BaseAdapter{
    private List<?> list;
    private Context context;
    private onbtnclick click;
    public CarKeepAdapter(List<?> list, Context context){
        this.list=list;
        this.context=context;
    }
    public void setOnbtnClick(onbtnclick click){
        this.click=click;
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
        ViewHoder viewHoder;
        if (convertView==null){
            convertView= LayoutInflater.from(context).inflate(R.layout.item_carkeep,null);
            viewHoder=new ViewHoder();
            viewHoder.tv_up=(TextView) convertView.findViewById(R.id.tv_up);
            viewHoder.tv_modify=(TextView) convertView.findViewById(R.id.tv_modify);
            viewHoder.tv_upsucsses=(TextView) convertView.findViewById(R.id.tv_upsucsses);
            convertView.setTag(viewHoder);
        }else {
            viewHoder=(ViewHoder) convertView.getTag();
        }
        viewHoder.tv_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click.onUpbtnClick(v);
            }
        });
        viewHoder.tv_modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click.onModifybtnClick(v);
            }
        });
        return convertView;
    }

    static  class  ViewHoder{
        TextView tv_up,tv_modify,tv_upsucsses;
    }

    public interface onbtnclick{
        public void onUpbtnClick(View v);
        public void onModifybtnClick(View v);
    }
}
