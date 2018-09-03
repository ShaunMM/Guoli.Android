package Adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;


import zj.com.mc.R;

/**
 * Created by BYJ on 2017/8/3.
 */

public class TrainScheduleAdapterTwo extends BaseExpandableListAdapter {

    private Context context;
    private List<Map> groupMap;
    private List<List<Map>> childMap;
    private Handler handler;

    public TrainScheduleAdapterTwo(Context context, List<Map> groupMap, List<List<Map>> childMap) {
        this.context = context;
        this.groupMap = groupMap;
        this.childMap = childMap;
    }

    public TrainScheduleAdapterTwo(Context context) {
        this.context = context;
    }

    public TrainScheduleAdapterTwo(List<Map> groupMap, List<List<Map>> childMap) {
        this.groupMap = groupMap;
        this.childMap = childMap;
        handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                notifyDataSetChanged();
                super.handleMessage(msg);
            }
        };
    }

    public void refresh() {
        handler.sendMessage(new Message());
    }

    @Override
    public int getGroupCount() {
        if (groupMap != null) {
            return groupMap.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupMap.get(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        Map parentmap = groupMap.get(groupPosition);
        ParentViewHolder parentViewHolder;
        if (convertView == null) {
            parentViewHolder = new ParentViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_parent, null);
            parentViewHolder.tv_fullname = (TextView) convertView.findViewById(R.id.tv_fullname);
            parentViewHolder.tv_beginstation = (TextView) convertView.findViewById(R.id.tv_beginstation);
            parentViewHolder.tv_overstation = (TextView) convertView.findViewById(R.id.tv_overstation);
            parentViewHolder.tv_direction = (TextView) convertView.findViewById(R.id.tv_direction);
            convertView.setTag(parentViewHolder);
        } else {
            parentViewHolder = (ParentViewHolder) convertView.getTag();
        }

        parentViewHolder.tv_fullname.setText(parentmap.get("FullName").toString());
        parentViewHolder.tv_beginstation.setText(parentmap.get("FirstStation").toString());
        parentViewHolder.tv_overstation.setText(parentmap.get("LastStation").toString());
        parentViewHolder.tv_direction.setText(parentmap.get("Direction").toString());

        return convertView;
    }

    public class ParentViewHolder {
        public TextView tv_fullname;
        public TextView tv_beginstation;
        public TextView tv_overstation;
        public TextView tv_direction;
    }

    //子目录Adapter
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childMap.get(groupPosition).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public int getChildrenCount(int groupPosition) {

        return childMap.get(groupPosition).size();
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
                             ViewGroup parent) {
        Map childmap = childMap.get(groupPosition).get(childPosition);
        ChildViewHolder childViewHolder;

        if (convertView == null) {
            childViewHolder = new ChildViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_children, null);
            childViewHolder.tv_stationorder = (TextView) convertView.findViewById(R.id.tv_stationorder);
            childViewHolder.tv_station = (TextView) convertView.findViewById(R.id.tv_station);
            childViewHolder.tv_arrivetime = (TextView) convertView.findViewById(R.id.tv_arrivetime);
            childViewHolder.tv_departtime = (TextView) convertView.findViewById(R.id.tv_departtime);
            childViewHolder.tv_stoptime = (TextView) convertView.findViewById(R.id.tv_stoptime);
            childViewHolder.tv_distance = (TextView) convertView.findViewById(R.id.tv_distance);
            childViewHolder.tv_kmh = (TextView) convertView.findViewById(R.id.tv_kmh);
            convertView.setTag(childViewHolder);
        } else {
            childViewHolder = (ChildViewHolder) convertView.getTag();
        }

        childViewHolder.tv_stationorder.setText(childmap.get("TrainStationId").toString());
        childViewHolder.tv_station.setText(childmap.get("StationName").toString());
        childViewHolder.tv_arrivetime.setText(childmap.get("ArriveTime").toString());
        childViewHolder.tv_departtime.setText(childmap.get("DepartTime").toString());
        childViewHolder.tv_stoptime.setText(childmap.get("StopMinutes").toString());
        childViewHolder.tv_distance.setText(childmap.get("IntervalKms").toString());
        childViewHolder.tv_kmh.setText(childmap.get("SuggestSpeed").toString());

        return convertView;
    }

    public class ChildViewHolder {
        public TextView tv_stationorder;
        public TextView tv_station;
        public TextView tv_arrivetime;
        public TextView tv_departtime;
        public TextView tv_stoptime;
        public TextView tv_distance;
        public TextView tv_kmh;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }
}
