package Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import zj.com.mc.R;


/**
 * Created by dell on 2016/7/31.
 */
public class GridlistAdapter2 extends BaseAdapter{

    private Context context;
    private List<String> gridList;

    public GridlistAdapter2(Context context, List<String> gridList) {
        this.context = context;
        this.gridList = gridList;
    }

    @Override
    public int getCount() {
        return gridList.size();
    }

    @Override
    public Object getItem(int i) {
        return gridList.get(i);
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
        vh.tv.setText(gridList.get(i));
        return convertView;
    }

    class Viewholder{
        TextView tv;

    }

}
