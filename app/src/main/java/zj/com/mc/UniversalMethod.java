package zj.com.mc;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import DBUtils.DBOpenHelper;
import config.ISystemConfig;

/**
 * Created by BYJ on 2017/10/25.
 */

public class UniversalMethod {

    public static void recordOperateLog(DBOpenHelper dbOpenHelper, ISystemConfig systemConfig,int LogType, String LogContent) {
        Date dt = new Date();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String nowTime = df.format(dt);
        dbOpenHelper.insert("AppOperateLog", new String[]{"LogType", "LogContent", "OperatorId", "DeviceId", "AddTime"},
                new Object[]{LogType, LogContent, systemConfig.getOperatorId(), 0, nowTime});
    }

}
