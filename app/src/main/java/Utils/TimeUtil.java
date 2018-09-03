package Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by BYJ on 2017/1/5.
 */

public class TimeUtil {

    private static Long transferStringDateToLong(String formatDate,String date) throws ParseException {
        SimpleDateFormat sdf= new SimpleDateFormat(formatDate);
        Date dt = sdf.parse(date);
        return dt.getTime();    }
}
