package Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import zj.com.mc.R;


/**
 * Created by dell on 2016/7/31.
 */
public class GridlistAdapter extends BaseAdapter{

    private Context context;
    private String[]str;

    public GridlistAdapter(Context context, String[] str) {
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
                    R.layout.trainsche_result_grivaitem, null);
                vh=new Viewholder();
            vh.tv= (TextView) convertView.findViewById(R.id.grid1_item);
            convertView.setTag(vh);
        }else {
           vh= (Viewholder) convertView.getTag();

        }
        vh.tv.setText(str[i]);
        return convertView;
    }

    class Viewholder{
        TextView tv;
    }

}
