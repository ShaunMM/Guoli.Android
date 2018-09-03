package Utils;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;

import java.util.Calendar;

/**
 * Created by dell on 2017/1/18.
 */

public class MyDatePickerDialog extends DatePickerDialog {

    public MyDatePickerDialog(Context context,
                              OnDateSetListener callBack, int year, int monthOfYear,
                              int dayOfMonth) {
        super(context, callBack, year, monthOfYear, dayOfMonth);
    }

    @Override
    protected void onStop() {
        //super.onStop();
    }


}
