package Utils;

import android.content.Context;
import android.app.TimePickerDialog;

import java.util.Calendar;

/**
 * 自定义时间选择器
 * 解决onTimeSet调用两次的问题
 */
public class MyTimePickerDialog extends TimePickerDialog {

    public MyTimePickerDialog(Context context, OnTimeSetListener callBack,
                              int hourOfDay, int minute, boolean is24HourView) {
        super(context, callBack, hourOfDay, minute, is24HourView);
    }

    public MyTimePickerDialog(Context context, OnTimeSetListener callBack, Calendar c) {
        super(context, callBack, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true);
    }

    @Override
    protected void onStop() {
        //super.onStop();
    }
}
