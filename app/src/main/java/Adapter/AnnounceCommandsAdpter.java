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
 * Created by mao on 2016/12/2.
 */
public class AnnounceCommandsAdpter extends BaseAdapter {
    private Context context;
    private List<?> list;
    private ViewHoder viewHoder;
    private onPassbtnClick click;

    public AnnounceCommandsAdpter(Context context, List<?> list) {
        this.context = context;
        this.list = list;
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

    public void setOnPassbtnClick(onPassbtnClick click) {
        this.click = click;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Map map = (Map) list.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_announcecommands, null);
            viewHoder = new ViewHoder();
            viewHoder.tv_CommandNo = (TextView) convertView.findViewById(R.id.tv_CommandNo);
            viewHoder.tv_CommandInterval = (TextView) convertView.findViewById(R.id.tv_CommandInterval);
            viewHoder.tv_Direction = (TextView) convertView.findViewById(R.id.tv_Direction);
            viewHoder.tv_SpeedLimitLocation = (TextView) convertView.findViewById(R.id.tv_SpeedLimitLocation);
            viewHoder.tv_StartEndTime = (TextView) convertView.findViewById(R.id.tv_StartEndTime);
            viewHoder.tv_LimitedSpeed = (TextView) convertView.findViewById(R.id.tv_LimitedSpeed);
            viewHoder.tv_IsPassed = (TextView) convertView.findViewById(R.id.tv_IsPassed);
            convertView.setTag(viewHoder);
        } else {
            viewHoder = (ViewHoder) convertView.getTag();
        }
        viewHoder.tv_CommandNo.setText(String.valueOf(map.get("CommandNo")));
        viewHoder.tv_CommandInterval.setText(String.valueOf(map.get("CommandInterval")));
        viewHoder.tv_Direction.setText(String.valueOf(map.get("Direction")));
        viewHoder.tv_SpeedLimitLocation.setText(String.valueOf(map.get("SpeedLimitLocation")));
        viewHoder.tv_StartEndTime.setText(String.valueOf(map.get("StartEndTime")));
        viewHoder.tv_LimitedSpeed.setText(String.valueOf(map.get("LimitedSpeed")));

        viewHoder.tv_IsPassed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click.onClick(v, position);
            }
        });

        return convertView;
    }

    static class ViewHoder {
        TextView tv_CommandNo, tv_CommandInterval, tv_Direction,
                tv_SpeedLimitLocation, tv_StartEndTime, tv_LimitedSpeed, tv_IsPassed;
    }

    public interface onPassbtnClick {
        public void onClick(View v, int position);
    }
}
