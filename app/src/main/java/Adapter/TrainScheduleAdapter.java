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
 * Created byj Dell on 2017/8/1.
 */

public class TrainScheduleAdapter extends BaseAdapter {
    private Context context;
    private List<Map> trains;
    private LayoutInflater inflater = null;

    public TrainScheduleAdapter(Context context, List<Map> trains) {
        this.context = context;
        this.trains = trains;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData(List<Map> trains) {
        this.trains = trains;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return trains.size();
    }

    @Override
    public Object getItem(int position) {
        if (trains != null) {
            return trains.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        TrainViewHolder trainViewHolder = null;
        if (convertView == null) {
            trainViewHolder = new TrainViewHolder();
            convertView = inflater.inflate(R.layout.item_trainschedule, null);
            trainViewHolder.tv_trainnumber = (TextView) convertView.findViewById(R.id.tv_trainnumber);
            trainViewHolder.tv_trainline = (TextView) convertView.findViewById(R.id.tv_trainline);
            trainViewHolder.tv_firststation = (TextView) convertView.findViewById(R.id.tv_firststation);
            trainViewHolder.tv_finalstation = (TextView) convertView.findViewById(R.id.tv_finalstation);
            trainViewHolder.tv_stations = (TextView) convertView.findViewById(R.id.tv_stations);
            convertView.setTag(trainViewHolder);
        } else {
            trainViewHolder = (TrainViewHolder) convertView.getTag();
        }

        trainViewHolder.tv_trainnumber.setText(trains.get(position).get("FullName").toString());
        trainViewHolder.tv_trainline.setText(trains.get(position).get("LineName").toString());
        trainViewHolder.tv_firststation.setText(trains.get(position).get("FirstStation").toString());
        trainViewHolder.tv_finalstation.setText(trains.get(position).get("LastStation").toString());
        trainViewHolder.tv_stations.setText(trains.get(position).get("LastStationId").toString());

        return convertView;
    }

    public class TrainViewHolder {
        public TextView tv_trainnumber;
        public TextView tv_trainline;
        public TextView tv_firststation;
        public TextView tv_finalstation;
        public TextView tv_stations;

    }
}
