package config;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by dell on 2017/3/23.
 */
public class SystemConfig implements ISystemConfig {

    private final static String CONFIG_FILENAME = "appbase";
    private final static String ISFIRSTUSE = "isfirstuse";
    private final static String ISFIRSTSYNC = "isfirstsync";
    private final static String USERNAME = "username";
    private final static String USERACCOUNT = "useraccount";
    private final static String USERID = "userid";
    private final static String DEPARTMENTID = "departmentid";
    private final static String HOST = "host";
    private final static String NEWFILEMAXID = "newfilemaxid";//获取最新添加的文件
    private final static String TRAINSCHEDULECODE = "Trainschedulecode";//列车时刻查询code
    private final static String SYSTEMTOOLS = "SystemTools";//照相录音视频文件夹创建
    private final static String GETPERMISSION = "GetPermission";//照相录音视频文件夹创建
    private final static String USERPOSTID = "UserPostId";//照相录音视频文件夹创建
    private final static String VERSIONCODE = "versionCode";//版本号
    private final static String IMEI = "imei";//版本号
    private final static String TABLETNFOR = "tabletinfor";//版本号
    private final static String OPERATORID = "operatorId";//OperatorId为当前登录人的Id（PersonInfo表的主键）
    private final static String POSTID = "postid";
    private final static String ISADDDATA = "isadddata";
    private Context context;
    private SharedPreferences systemConfigSharedPreferences;

    public SystemConfig(Context ctx) {
        this.context = ctx;
        SharedPreferences sp = this.context.getSharedPreferences(CONFIG_FILENAME, Context.MODE_WORLD_READABLE);
        this.setSystemConfigSharedPreferences(sp);
    }

    public SharedPreferences getSystemConfigSharedPreferences() {
        return systemConfigSharedPreferences;
    }

    public void setSystemConfigSharedPreferences(SharedPreferences systemConfigSharedPreferences) {
        this.systemConfigSharedPreferences = systemConfigSharedPreferences;
    }

    @Override
    public void setUserName(String username) {
        this.getSystemConfigSharedPreferences().edit().putString(USERNAME, username).commit();

    }

    @Override
    public String getUserName() {
        return this.getSystemConfigSharedPreferences().getString(USERNAME, "");
    }

    @Override
    public String getUserId() {
        return this.getSystemConfigSharedPreferences().getString(USERID, String.valueOf(0));
    }

    @Override
    public void setUserId(String userid) {
        this.getSystemConfigSharedPreferences().edit().putString(
                USERID, userid).commit();
    }

    @Override
    public void setUserAccount(String useraccount) {
        this.getSystemConfigSharedPreferences().edit().putString(USERACCOUNT, useraccount).commit();

    }

    @Override
    public String getUserAccount() {
        return this.getSystemConfigSharedPreferences().getString(USERACCOUNT, "");
    }

    @Override
    public String getDepartmentId() {
        return this.getSystemConfigSharedPreferences().getString(DEPARTMENTID, "");
    }

    @Override
    public void setDepartmentId(String departmentid) {
        this.getSystemConfigSharedPreferences().edit().putString(DEPARTMENTID, departmentid).commit();
    }

    @Override
    public String getPostId() {
        return this.getSystemConfigSharedPreferences().getString(POSTID, "");
    }

    @Override
    public void setPostId(String postid) {
        this.getSystemConfigSharedPreferences().edit().putString(POSTID, postid).commit();
    }

    @Override
    public String getHost() {
//        return this.getSystemConfigSharedPreferences().getString(HOST, "http://10.95.65.12:8002");
        return this.getSystemConfigSharedPreferences().getString(HOST, "http://192.168.0.103:8003");
//        return this.getSystemConfigSharedPreferences().getString(HOST, "http://172.20.10.2:8002");
    }

    @Override
    public void setHost(String host) {
        this.getSystemConfigSharedPreferences().edit().putString(HOST, host).commit();
    }

    @Override
    public String getTrainScheduleCode() {
        return this.getSystemConfigSharedPreferences().getString(TRAINSCHEDULECODE, "");
    }

    @Override
    public void setTrainScheduleCode(String trainschedulecode) {
        this.getSystemConfigSharedPreferences().edit().putString(TRAINSCHEDULECODE, trainschedulecode).commit();
    }

    @Override
    public String getNewFileMaxId() {
        return this.getSystemConfigSharedPreferences().getString(NEWFILEMAXID, "0");
    }

    @Override
    public void setNewFileMaxId(String newfilemaxid) {
        this.getSystemConfigSharedPreferences().edit().putString(NEWFILEMAXID, newfilemaxid).commit();
    }

    @Override
    public boolean isSystemTools() {
        return this.getSystemConfigSharedPreferences().getBoolean(SYSTEMTOOLS, true);
    }

    @Override
    public void setSystemTools(boolean isSystemTools) {
        this.getSystemConfigSharedPreferences().edit().putBoolean(SYSTEMTOOLS, isSystemTools).commit();

    }

    @Override
    public boolean isGetPermission() {
        return this.getSystemConfigSharedPreferences().getBoolean(GETPERMISSION, true);
    }

    @Override
    public void setPermission(boolean isGetPermission) {
        this.getSystemConfigSharedPreferences().edit().putBoolean(GETPERMISSION, isGetPermission).commit();
    }

    @Override
    public String getUserPostId() {
        return this.getSystemConfigSharedPreferences().getString(USERPOSTID, "");
    }

    @Override
    public void setUserPostId(String userPostId) {
        this.getSystemConfigSharedPreferences().edit().putString(USERPOSTID, userPostId).commit();
    }

    @Override
    public void setVersionCode(String versionCode) {
        this.getSystemConfigSharedPreferences().edit().putString(VERSIONCODE, versionCode).commit();
    }

    @Override
    public String getVersionCode() {
        return this.getSystemConfigSharedPreferences().getString(VERSIONCODE, "");
    }

    @Override
    public void setIMEI(String imei) {
        this.getSystemConfigSharedPreferences().edit().putString(IMEI, imei).commit();
    }

    @Override
    public String getIMEI() {
        return this.getSystemConfigSharedPreferences().getString(IMEI, "");
    }

    @Override
    public boolean isTabletInfor() {
        return this.getSystemConfigSharedPreferences().getBoolean(TABLETNFOR, true);
    }

    @Override
    public void setTabletInfor(boolean isTabletInfor) {
        this.getSystemConfigSharedPreferences().edit().putBoolean(TABLETNFOR, isTabletInfor).commit();
    }

    @Override
    public void setOperatorId(String operatorId) {
        this.getSystemConfigSharedPreferences().edit().putString(OPERATORID, operatorId).commit();
    }

    @Override
    public String getOperatorId() {
        return this.getSystemConfigSharedPreferences().getString(OPERATORID, "");
    }

    @Override
    public boolean isAddData() {
        return this.getSystemConfigSharedPreferences().getBoolean(ISADDDATA, true);
    }

    @Override
    public void setAddData(boolean isAddData) {
        this.getSystemConfigSharedPreferences().edit().putBoolean(ISADDDATA, isAddData).commit();
    }

    @Override
    public boolean isFirstUse() {
        return this.getSystemConfigSharedPreferences().getBoolean(ISFIRSTUSE, true);
    }

    @Override
    public void setFirstUse(boolean isFirstUse) {
        this.getSystemConfigSharedPreferences().edit().putBoolean(ISFIRSTUSE, isFirstUse).commit();

    }

    @Override
    public boolean isFirstSync() {
        return this.getSystemConfigSharedPreferences().getBoolean(ISFIRSTSYNC, true);
    }

    @Override
    public void setFirstSync(boolean isFirstSync) {
        this.getSystemConfigSharedPreferences().edit().putBoolean(ISFIRSTSYNC, isFirstSync).commit();
    }

}
