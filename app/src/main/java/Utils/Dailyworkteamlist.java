package Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import zj.com.mc.R;


/**
 * Created by dell on 2016/8/1.
 */
public class Dailyworkteamlist extends BaseAdapter {

    private Context context;
    private List<Map> lsit;

    public Dailyworkteamlist(Context context, List<Map> lsit) {
        this.context = context;
        this.lsit = lsit;
    }

    @Override
    public int getCount() {
        return lsit.size();
    }

    @Override
    public Object getItem(int i) {
        return lsit.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View convertView, ViewGroup viewGroup) {
        viewHoldervp vh=null;
        HashMap map= (HashMap) lsit.get(i);
        if (convertView==null){
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.dailylistinglistitem, null);
            vh=new viewHoldervp();
            vh.tv1= (TextView) convertView.findViewById(R.id.daily_Listing_itemmessage);
            vh.tv2= (TextView) convertView.findViewById(R.id.daily_Listing_itemtype);
            vh.tv3= (TextView) convertView.findViewById(R.id.daily_Listing_itemnumber);
            vh.tv4= (TextView) convertView.findViewById(R.id.daily_Listing_itemtime);
            vh.tv5= (TextView) convertView.findViewById(R.id.daily_Listing_itemdo);

            convertView.setTag(vh);
        }else {
            vh= (viewHoldervp) convertView.getTag();

        }
        vh.tv1.setText((Integer) map.get("TrainCode"));
        vh.tv2.setText((Integer) map.get("DriverId"));
        vh.tv3.setText(map.get("TakeSection")+"");
        vh.tv4.setText(map.get("TakeDate")+"");
        vh.tv5.setText((Integer) map.get("IsUploaded"));


        //判断操作条件
//        if (lsit.get(i).get(5).equals("上传")){
//            vh.tv5.setBackgroundColor(Color.GRAY);
//            vh.tv5.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Toast.makeText(context,"diansi"+i,Toast.LENGTH_SHORT).show();
                   //操作数据库使其修改为已上传





//
//
//                }
//            });
//        }else
//        {
//            vh.tv5.setBackgroundColor(Color.WHITE);//已上传
//        }


        return null;
    }

        class viewHoldervp{
            TextView tv1,tv2,tv3,tv4,tv5;

        }
}
