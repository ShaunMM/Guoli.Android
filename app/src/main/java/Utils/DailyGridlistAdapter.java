package Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import zj.com.mc.R;


/**
 * Created by dell on 2016/7/31.
 */
public class DailyGridlistAdapter extends BaseAdapter{

    private Context context;
    private String[]str;

    public DailyGridlistAdapter(Context context, String[] str) {
        this.context = context;
        this.str = str;
    }

    @Override
    public int getCount() {
        return str.length;
    }

    @Override
    public Object getItem(int i) {
        return str[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        Viewholder vh=null;
        if (convertView==null){
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.dailyworkgriditem, null);
                vh=new Viewholder();
            vh.bt= (Button) convertView.findViewById(R.id.daily_griditem1);
            convertView.setTag(vh);
        }else {
           vh= (Viewholder) convertView.getTag();

        }
        vh.bt.setText(str[i]);
        return convertView;
    }

    class Viewholder{
        Button bt;

    }

}
