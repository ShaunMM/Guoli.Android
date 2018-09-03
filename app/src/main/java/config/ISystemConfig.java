package config;

/**
 * Created by dell on 2017/3/23.
 */

public interface ISystemConfig {

    public void setUserName(String username);

    public String getUserName();

    public void setUserAccount(String useraccount);

    public String getUserAccount();

    public String getUserId();

    public void setUserId(String userid);

    public String getDepartmentId();

    public void setDepartmentId(String departmentid);

    public String getPostId();

    public void setPostId(String postid);

    public boolean isFirstUse();

    public void setFirstUse(boolean isFirstUse);

    public boolean isFirstSync();

    public void setFirstSync(boolean isFirstSync);

    public String getHost();

    public void setHost(String host);

    public String getTrainScheduleCode();

    public void setTrainScheduleCode(String trainschedulecode);

    //文件 上次检查更新所保存的最大id值
    public String getNewFileMaxId();

    public void setNewFileMaxId(String newfilemaxid);

    public boolean isSystemTools();

    public void setSystemTools(boolean isSystemTools);

    public boolean isGetPermission();

    public void setPermission(boolean isGetPermission);

    public String getUserPostId();

    public void setUserPostId(String userPostId);

    public void setVersionCode(String versionCode);

    public String getVersionCode();

    public void setIMEI(String imei);

    public String getIMEI();

    public boolean isTabletInfor();

    public void setTabletInfor(boolean isTabletInfor);

    public void setOperatorId(String operatorId);

    public String getOperatorId();

    public boolean isAddData();

    public void setAddData(boolean isAddData);
}
