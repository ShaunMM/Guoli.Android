package Utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;

/**
 * 日期工具类
 *
 * @author zhou
 */
@SuppressLint("SimpleDateFormat")
public class DateUtil {
    private static String TAG = DateUtil.class.getSimpleName();
    public static final String Y_M = "yyyy 年 MM 月";
    public static final String Y_M_D = "yyyy-MM-dd";
    public static final String M_D_Y = "MM/dd/yyyy";
    public static final String M_D_Y_H_M_S = "MM/dd/yyyy HH:mm:ss";
    public static final String M_D = "MM-dd";
    public static final String M_Ds = "MM月dd日";
    public static final String Y_M_D_H_M_S = "yyyy-MM-dd HH:mm:ss";
    public static final String Y_M_D_H_M = "yyyy-MM-dd HH:mm";
    public static final String Y_M_D_H = "yyyy-MM-dd HH";
    public static final String M_D_H_M_S = "MM-dd HH:mm:ss";
    public static final String M_D_H_M = "MM-dd HH: mm";
    public static final String H_M_S = "HH:mm:ss";
    public static final String H_M = "HH:mm";
    public static final String Y_M_Ds = "yyyy年MM月dd日";
    public static final String H_Ms = "HH时mm分";
    public static final String Y_M_D_H_Ms = "yyyy年MM月dd日HH时mm分";
    public static final String YMDHMMS = "yyyyMMddHHmmss";
    public static final String Y_M_D_H_M_Ss = "yyyy年MM月dd日HH时mm分ss秒";
    public static final String Y_M_D_H_MS = "yyyy年MM月dd日  HH:mm";

    /**
     * 一分钟的秒值，用于判断上次的更新时间
     */
    public static final long ONE_MINUTE = 60 * 1000;

    /**
     * 一小时的秒值，用于判断上次的更新时间
     */
    public static final long ONE_HOUR = 60 * ONE_MINUTE;

    /**
     * 一天的秒值，用于判断上次的更新时间
     */
    public static final long ONE_DAY = 24 * ONE_HOUR;
    /**
     * 一月的秒值，用于判断上次的更新时间
     */
    public static final long ONE_MONTH = 30 * ONE_DAY;

    /**
     * 一年的秒值，用于判断上次的更新时间
     */
    public static final long ONE_YEAR = 12 * ONE_MONTH;

    /**
     * 根据给定时间来获得距离现在时间间隔的描述
     */
    public static String getTheDaytimeInterval(long time) {
        long currentTime = System.currentTimeMillis();
        long timePassed = currentTime - time;
        long timeIntoFormat;
        Log.d(TAG, "timeIntoFormat=    " + time);
        // if (timePassed <= ONE_MINUTE) {
        // return "刚刚";
        // } else if (timePassed < ONE_HOUR) {
        // timeIntoFormat = timePassed / ONE_MINUTE;
        // return timeIntoFormat + "分钟前";
        // } else if (timePassed < ONE_DAY) {
        // timeIntoFormat = timePassed / ONE_HOUR;
        // return timeIntoFormat + "小时前";
        // } else if (timePassed < ONE_MONTH) {
        // timeIntoFormat = timePassed / ONE_DAY;
        // return timeIntoFormat + "天前";
        // } else if (timePassed < ONE_YEAR) {
        // timeIntoFormat = timePassed / ONE_MONTH;
        // return timeIntoFormat + "个月前";
        // } else {
        // timeIntoFormat = timePassed / ONE_YEAR;
        // return timeIntoFormat + "年前";
        // // return null;
        // }
        if (timePassed <= ONE_MINUTE) {
            return "刚刚";
        } else if (timePassed < ONE_HOUR) {
            timeIntoFormat = timePassed / ONE_MINUTE;
            return timeIntoFormat + "分钟前";
        } else if (timePassed < ONE_DAY) {
            timeIntoFormat = timePassed / ONE_HOUR;
            return timeIntoFormat + "小时前";
        } else {
            return getCurrentYear(time);
            // return null;
        }
    }

    /**
     * @param date   string类型的日期 例如:1970-01-01 23:23:23
     * @param format 传入类型的格式 例如:yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String DateToString(Date date, String format) {
        if (date == null) {
            return null;
        } else if (TextUtils.isEmpty(format)) {
            return new SimpleDateFormat(Y_M_D_H_M_S).format(date);
        } else {
            return new SimpleDateFormat(format).format(date);
        }
    }

    /**
     * @param date   string类型的日期 例如:1970-01-01 23:23:23
     * @param format 传入类型的格式 例如:yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static Date StringToDate(String date, String format) {
        try {
            if (TextUtils.isEmpty(date)) {
                return null;
            } else if (TextUtils.isEmpty(format)) {
                return new SimpleDateFormat(Y_M_D_H_M_S).parse(date);
            } else {

                return new SimpleDateFormat(format).parse(date);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get one date type in a fixed format.
     *
     * @param pattern
     * @return
     */
    public static DateFormat getCnDateFormat(String pattern) {
        return new SimpleDateFormat(pattern);
    }

    /**
     * 今天，过去，未来
     *
     * @return 1-过去 2-今天 3—未来
     */
    public static int isToday(String mills) {
        // 当前时间
        Calendar curCalendar = Calendar.getInstance();

        curCalendar.add(Calendar.DATE, 0);

        int year = curCalendar.get(Calendar.YEAR);
        int month = curCalendar.get(Calendar.MONTH);
        int day = curCalendar.get(Calendar.DAY_OF_MONTH);

        // 把时间移动到当天的00:00
        curCalendar.set(year, month, day, 0, 0, 0);

        // 得到00:00的毫秒值
        long curMills = curCalendar.getTimeInMillis();

        // 比较二个毫秒值
        long time = Long.parseLong(mills);

        // 如果传进来的时间，比当天的00:00的毫秒值要小
        if (time < curMills) {
            return 1;
        } else if (curMills <= time && time < (curMills + 24 * 3600 * 1000)) {
            return 2;
        } else if (time >= (curMills + 24 * 3600 * 1000)) {
            return 3;
        }
        return 0;
    }

    /**
     * 拿到今天偏移后的某天的00:00的毫秒值
     *
     * @param value 0代表当前 -1往前一天
     * @return
     */
    public static long getTimeInMillisForStart(int value) {

        // 当前时间
        Calendar curCalendar = Calendar.getInstance();

        // 移动时间
        curCalendar.add(Calendar.DATE, value);

        int year = curCalendar.get(Calendar.YEAR);
        int month = curCalendar.get(Calendar.MONTH);
        int day = curCalendar.get(Calendar.DAY_OF_MONTH);

        // 把时间移动到当天的00:00
        curCalendar.set(year, month, day, 0, 0, 0);

        // 得到00:00的毫秒值
        long curMills = curCalendar.getTimeInMillis();

        return curMills;
    }

    /**
     * 拿到今天偏移后的某天的23:59的毫秒值
     *
     * @param value 0代表当前 -1往前一天
     * @return
     */
    public static long getTimeInMillisForEnd(int value) {

        // 当前时间
        Calendar curCalendar = Calendar.getInstance();

        // 移动时间
        curCalendar.add(Calendar.DATE, value);

        int year = curCalendar.get(Calendar.YEAR);
        int month = curCalendar.get(Calendar.MONTH);
        int day = curCalendar.get(Calendar.DAY_OF_MONTH);

        // 把时间移动到当天的00:00
        curCalendar.set(year, month, day, 23, 59, 59);

        // 得到00:00的毫秒值
        long curMills = curCalendar.getTimeInMillis();

        return curMills;
    }

    public static String getTimeFomat(String year, String month, String day,
                                      String hour, String minute, String format) {
        Calendar curCalendar = Calendar.getInstance();
        int year_ = 0;
        if (TextUtils.isEmpty(year)) {
            year_ = curCalendar.get(Calendar.YEAR);
        } else {
            year_ = Integer.parseInt(year);
        }
        int month_ = 0;
        if (TextUtils.isEmpty(month)) {
            month_ = curCalendar.get(Calendar.MONTH);
        } else {
            month_ = Integer.parseInt(month);
        }
        int day_ = 0;
        if (TextUtils.isEmpty(day)) {
            day_ = curCalendar.get(Calendar.DAY_OF_MONTH);
        } else {
            day_ = Integer.parseInt(day);
        }
        int hour_ = 0;
        if ("0".equals(hour)) {
            hour_ = 0;
        } else {
            hour_ = Integer.parseInt(hour);
        }
        int minute_ = 0;
        if ("0".equals(minute)) {
            minute_ = 0;
        } else {
            minute_ = Integer.parseInt(minute);
        }
        int second = curCalendar.get(Calendar.SECOND);
        curCalendar.set(year_, month_, day_, hour_, minute_, second);

        return DateToString(new Date(curCalendar.getTimeInMillis()), format);

    }


    /**
     * 拿到某天的00:00的毫秒值
     *
     * @param date
     * @return
     */
    public static long getTimeInMillisForStart(Date date) {

        // 当前时间
        Calendar curCalendar = Calendar.getInstance();

        // 移动时间
        curCalendar.setTime(date);

        int year = curCalendar.get(Calendar.YEAR);
        int month = curCalendar.get(Calendar.MONTH);
        int day = curCalendar.get(Calendar.DAY_OF_MONTH);

        // 把时间移动到当天的00:00
        curCalendar.set(year, month, day, 0, 0, 0);

        // 得到00:00的毫秒值
        long curMills = curCalendar.getTimeInMillis();

        return curMills;
    }

    /**
     * 拿到某天的23:59的毫秒值
     *
     * @param date
     * @return
     */
    public static long getTimeInMillisForEnd(Date date) {

        // 当前时间
        Calendar curCalendar = Calendar.getInstance();

        // 移动时间
        curCalendar.setTime(date);

        int year = curCalendar.get(Calendar.YEAR);
        int month = curCalendar.get(Calendar.MONTH);
        int day = curCalendar.get(Calendar.DAY_OF_MONTH);

        // 把时间移动到当天的00:00
        curCalendar.set(year, month, day, 23, 59, 59);

        // 得到00:00的毫秒值
        long curMills = curCalendar.getTimeInMillis();

        return curMills;
    }

    /**
     * 将date格式字符串转换为时间
     *
     * @param date   时间
     * @param format 格式
     * @return
     */
    public static String getStringDate(Date date, String format) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(date);
    }

    public static String getStringDate(Date date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat formatter = new SimpleDateFormat(Y_M_D_H_M_S);
        return formatter.format(date);
    }

    /**
     * 将字符串转为date
     *
     * @param user_time
     * @param format
     * @return
     */
    public static Date getString2Date(String user_time, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date d = null;
        try {
            d = sdf.parse(user_time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return d;
    }

    /**
     * 获取当前时间
     *
     * @param format 格式
     * @return
     */
    public static String getStringDate(String format) {
        long timeNow = System.currentTimeMillis();
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        String dateString = formatter.format(timeNow);
        return dateString;
    }

    /**
     * 判断选择的日期是不是过去时间
     */
    public static boolean dateIsPast(String date) {
        long timeNow = System.currentTimeMillis();
        Date timeSelect = StringToDate(date + ":00:00", null);
        if (timeSelect.getTime() <= timeNow) {
            return true;
        } else {
            return false;
        }
    }

    /**
     *  当前日期 mm/dd/yyyy  
     */
    public static String getCurrentDate() {
        Date date = new Date();
        int year = Integer.parseInt(new SimpleDateFormat("yyyy").format(date));
        int month = Integer.parseInt(new SimpleDateFormat("MM").format(date));
        int day = Integer.parseInt(new SimpleDateFormat("dd").format(date));
        return month + "/" + day + "/" + year;

    }

    /**
     *  * 从当前日期算起，获取N天前的日期（当前日不算在内），日期格式为yyyy-MM-dd  *  * @param daily 天数  * @return
     *  
     */
    public static String getDateByDay(int daily) {
        Date date = new Date();
        int year = Integer.parseInt(new SimpleDateFormat("yyyy").format(date));
        int month = Integer.parseInt(new SimpleDateFormat("MM").format(date));
        int day = Integer.parseInt(new SimpleDateFormat("dd").format(date))
                - daily;
        if (day < 1) {
            month -= 1;
            if (month == 0) {
                year -= 1;
                month = 12;
            }
            if (month == 4 || month == 6 || month == 9 || month == 11) {
                day = 30 + day;
            } else if (month == 1 || month == 3 || month == 5 || month == 7
                    || month == 8 || month == 10 || month == 12) {
                day = 31 + day;
            } else if (month == 2) {
                if (year % 400 == 0 || (year % 4 == 0 && year % 100 != 0)) {
                    day = 29 + day;
                } else {
                    day = 28 + day;
                }
            }

        }
        String y = year + "";
        String m = "";
        String d = "";
        m = month + "";
        d = day + "";
        return m + "/" + d + "/" + y;
    }

    /**
     *  * 从当前日期算起，获取N天前的日期（当前日不算在内），日期格式为yyyy-MM-dd   *  * @param daily 天数  * @return
     *  
     */
    public static String getDateByDay(String time) {
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        Date d;
        Calendar calendar = Calendar.getInstance();
        try {
            d = sdf.parse(time);
            long l = 0;
            l = d.getTime();
            calendar.setTimeInMillis(l);
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return df.format(calendar.getTime());
    }

    /**
     * 将字符串转为时间戳
     *
     * @param user_time
     * @param format
     * @return
     */
    public static long getTime(String user_time, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date d;
        long l = 0;
        try {
            d = sdf.parse(user_time);
            l = d.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return l;
    }

    /**
     * 将时间戳转为字符串
     *
     * @param user_time
     * @param format
     * @return
     */
    public static String getStrTime(long user_time, String format) {
        String re_time = null;
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        re_time = sdf.format(new Date(user_time));
        return re_time;
    }

    /**
     * 判斷是否在一天之內
     *
     * @param savetime
     * @return
     */
    public static boolean isNew(long savetime) {
        long currentTime = System.currentTimeMillis();
        long timePassed = currentTime - savetime;
        if (timePassed > ONE_DAY) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 获取时间字符串，如果是去年就显示年份，不是去年就去掉年份 11-10(2014-11-10)
     *
     * @param time
     * @return
     */
    public static String isCurrentYear(long time) {
        String str = "";
        Calendar curCalendar = Calendar.getInstance();
        int year = curCalendar.get(Calendar.YEAR);
        Date date = new Date(time);
        Calendar getCalendar = Calendar.getInstance();
        getCalendar.setTime(date);
        int getyear = curCalendar.get(Calendar.YEAR);
        if (year > getyear) {
            str = getStrTime(time, Y_M_D);
        } else {
            str = getStrTime(time, M_D);
        }
        return str;
    }

    /**
     * 获取时间字符串，如果是去年就显示年份，不是去年就去掉年份 11-10 10:30(2014-11-10 10:30)
     *
     * @param time
     * @return
     */
    public static String getCurrentYear(long time) {
        String str = "";
        Calendar curCalendar = Calendar.getInstance();
        int year = curCalendar.get(Calendar.YEAR);
        Date date = new Date(time);
        Calendar getCalendar = Calendar.getInstance();
        getCalendar.setTime(date);
        int getyear = getCalendar.get(Calendar.YEAR);
        if (year > getyear) {
            str = getStrTime(time, Y_M_D_H_M);
        } else {
            str = getStrTime(time, M_D_H_M);
        }
        return str;
    }

    public static String isMonthDayhm(long time) {
        String str = "";
        str = getStrTime(time, M_D_H_M);
        return str;
    }

    public static String isYearMonthDay(long time) {
        String str = "";
        str = getStrTime(time, Y_M_D);
        return str;
    }

    public static boolean isSameDay(Date date) {
        if (date == null) {
            return false;

        }
        Calendar nowCalendar = Calendar.getInstance();
        Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.setTime(date);
        if (nowCalendar.get(Calendar.YEAR) == dateCalendar.get(Calendar.YEAR)

                && nowCalendar.get(Calendar.MONTH) == dateCalendar.get(Calendar.MONTH)

                && nowCalendar.get(Calendar.DATE) == dateCalendar.get(Calendar.DATE)) {
            return true;

        }
        return false;
    }

    public static String getAmAndPm(String startdatetime, String enddatetime) {
        StringBuffer time = new StringBuffer();
        Date startDate = getString2Date(startdatetime, Y_M_D_H_M_S);
        Date endDate = getString2Date(enddatetime, Y_M_D_H_M_S);
        Calendar instance = Calendar.getInstance();
        instance.setTime(startDate);
        switch (instance.get(Calendar.AM_PM)) {
            case 0:
                time.append("上午" + DateToString(startDate, H_M));
                break;
            case 1:
                time.append("下午" + DateToString(startDate, H_M));
                break;
            default:
                break;
        }
        instance.setTime(endDate);
        switch (instance.get(Calendar.AM_PM)) {
            case 0:
                time.append(" - 上午" + DateToString(endDate, H_M));
                break;
            case 1:
                time.append(" - 下午" + DateToString(endDate, H_M));
                break;

            default:
                break;
        }
        return time.toString();
    }

    public static String getY_M_D_H_M_S(int year, int monthOfYear,
                                        int dayOfMonth, int hourOfDay, int minute, int second) {
        String month = "";
        String day = "";
        String hour = "";
        String min = "";
        String sec = "";
        if (monthOfYear + 1 < 10) {
            month = "0" + (monthOfYear + 1);
        } else {
            month = (monthOfYear + 1) + "";
        }

        if (dayOfMonth < 10) {
            day = "0" + dayOfMonth;
        } else {
            day = dayOfMonth + "";
        }

        if (hourOfDay < 10) {
            hour = "0" + hourOfDay;
        } else {
            hour = hourOfDay + "";
        }

        if (minute < 10) {
            min = "0" + minute;
        } else {
            min = minute + "";
        }
        if (second < 10) {
            sec = "0" + second;
        } else {
            sec = second + "";
        }

        String time = year + "-" + month + "-" + day + " " + hour + ":" + min
                + ":" + sec;
        return time;
    }

    public static String getY_M_D_H_M(int year, int monthOfYear,
                                      int dayOfMonth, int hourOfDay, int minute) {
        String month = "";
        String day = "";
        String hour = "";
        String min = "";
        if (monthOfYear + 1 < 10) {
            month = "0" + (monthOfYear + 1);
        } else {
            month = (monthOfYear + 1) + "";
        }

        if (dayOfMonth < 10) {
            day = "0" + dayOfMonth;
        } else {
            day = dayOfMonth + "";
        }

        if (hourOfDay < 10) {
            hour = "0" + hourOfDay;
        } else {
            hour = hourOfDay + "";
        }

        if (minute < 10) {
            min = "0" + minute;
        } else {
            min = minute + "";
        }

        String time = year + "-" + month + "-" + day + " " + hour + ":" + min;
        return time;
    }

    public static String getY_M_D(int year, int monthOfYear, int dayOfMonth) {
        String month = "";
        String day = "";

        if (monthOfYear + 1 < 10) {
            month = "0" + (monthOfYear + 1);
        } else {
            month = (monthOfYear + 1) + "";
        }

        if (dayOfMonth < 10) {
            day = "0" + dayOfMonth;
        } else {
            day = dayOfMonth + "";
        }
        String time = year + "-" + month + "-" + day;
        return time;
    }

    public static boolean isSameDay(long oldtime, long newtime) {
        if (oldtime == 0 || newtime == 0) {
            return false;
        }
        Calendar nowCalendar = Calendar.getInstance();
        nowCalendar.setTimeInMillis(oldtime);
        Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.setTimeInMillis(newtime);

        if (nowCalendar.get(Calendar.YEAR) == dateCalendar.get(Calendar.YEAR)
                && nowCalendar.get(Calendar.MONTH) == dateCalendar
                .get(Calendar.MONTH)
                && nowCalendar.get(Calendar.DATE) == dateCalendar
                .get(Calendar.DATE)) {
            return true;
        }
        return false;
    }

    /***
     * 将有秒的时间字符转成无秒的
     *
     * @param dataStr
     * @return
     */
    public static String fomortDateString(String dataStr) {
        return dataStr.substring(0, dataStr.length() - 3);
    }

    /**
     * 判断start是否小于end
     *
     * @param start
     * @param end
     * @return
     */
    public static boolean checkTime(String currentTime, String time) {
        System.out.println("currentTime=" + currentTime);
        System.out.println("time=" + time);
        Date currentTimeDate = getString2Date(currentTime, DateUtil.Y_M_D_H_M);
        Date timeDate = getString2Date(time, DateUtil.Y_M_D_H_M);
        if (currentTimeDate.getTime() <= timeDate.getTime()) {
            return true;
        }
        return false;
    }

    public static boolean checkdate(String currentDate, String Date) {
        System.out.println("currentTime=" + currentDate);
        System.out.println("time=" + Date);
        Date currentTimeDate = getString2Date(currentDate, DateUtil.Y_M_D);
        Date timeDate = getString2Date(Date, DateUtil.Y_M_D);
        if (currentTimeDate.getTime() <= timeDate.getTime()) {
            return true;
        }
        return false;
    }
}
