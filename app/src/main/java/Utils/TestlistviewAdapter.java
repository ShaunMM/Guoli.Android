package Utils;

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
 * Created by dell on 2016/8/2.
 */
public class TestlistviewAdapter extends BaseAdapter {
    private Context context;
    private List<Map> list;

    public TestlistviewAdapter(Context context, List<Map> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    public void refresh(List<Map> list) {
        list = list;
        notifyDataSetChanged();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder vh;
        Map map=list.get(i);

        if (convertView==null) {
                convertView= LayoutInflater.from(context).inflate(R.layout.vocational_list_item,null);
            vh=new ViewHolder();
            vh.tvitem1= (TextView) convertView.findViewById(R.id.volist_item1);
            vh.tvitem2= (TextView) convertView.findViewById(R.id.volist_item2);
            vh.tvitte3= (TextView) convertView.findViewById(R.id.volist_item3);
            vh.tvitem4= (TextView) convertView.findViewById(R.id.volist_item4);
            vh.tvitem5= (TextView) convertView.findViewById(R.id.volist_item5);
            vh.tvitem6= (TextView) convertView.findViewById(R.id.volist_item6);
            convertView.setTag(vh);
        }else {
            vh= (ViewHolder) convertView.getTag();
        }
            vh.tvitem1.setText("a");
            vh.tvitem2.setText("b");
            vh.tvitte3.setText("c");
            vh.tvitem4.setText("d");
            vh.tvitem5.setText("e");
            vh.tvitem6.setText("f");

        return convertView;
    }


    class ViewHolder{
        TextView tvitem1,tvitem2,tvitte3,tvitem4,tvitem5,tvitem6;

    }

}
