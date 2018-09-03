package bean;

import java.util.Date;

public class DrivePlan {

    public static final String D_Id = "Id";
    public static final String D_LineName = "LineName";
    public static final String D_TrainCode = "TrainCode";
    public static final String D_LocoType = "LocoType";
    public static final String D_DriverNo = "DriverNo";
    public static final String D_DriverName = "DriverName";
    public static final String D_ViceDriverNo = "ViceDriverNo";
    public static final String D_ViceDriverName = "ViceDriverName";
    public static final String D_StudentNo = "StudentNo";
    public static final String D_StudentName = "StudentName";
    public static final String D_OtherNo1 = "OtherNo1";
    public static final String D_OtherName1 = "OtherName1";
    public static final String D_OtherNo2 = "OtherNo2";
    public static final String D_AttendTime = "AttendTime";
    public static final String D_OtherName2 = "OtherName2";
    public static final String D_StartTime = "StartTime";
    private int id;
    private String LineName;
    private String TrainCode;
    private String LocoType;
    private String DriverNo;
    private String DriverName;
    private String ViceDriverNo;
    private String ViceDriverName;
    private String StudentNo;
    private String StudentName;
    private String OtherNo1;
    private String OtherName1;
    private String OtherNo2;
    private String OtherName2;
    private Date AttendTime;
    private Date StartTime;

    public DrivePlan() { }

    public DrivePlan(int id, String lineName, String trainCode, String locoType, String driverNo, String driverName, String viceDriverNo, String viceDriverName, String studentNo, String studentName, String otherNo1, String otherName1, String otherNo2, String otherName2, Date attendTime, Date startTime) {
        this.id = id;
        LineName = lineName;
        TrainCode = trainCode;
        LocoType = locoType;
        DriverNo = driverNo;
        DriverName = driverName;
        ViceDriverNo = viceDriverNo;
        ViceDriverName = viceDriverName;
        StudentNo = studentNo;
        StudentName = studentName;
        OtherNo1 = otherNo1;
        OtherName1 = otherName1;
        OtherNo2 = otherNo2;
        OtherName2 = otherName2;
        AttendTime = attendTime;
        StartTime = startTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLineName() {
        return LineName;
    }

    public void setLineName(String lineName) {
        LineName = lineName;
    }

    public String getTrainCode() {
        return TrainCode;
    }

    public void setTrainCode(String trainCode) {
        TrainCode = trainCode;
    }

    public String getLocoType() {
        return LocoType;
    }

    public void setLocoType(String locoType) {
        LocoType = locoType;
    }

    public String getDriverNo() {
        return DriverNo;
    }

    public void setDriverNo(String driverNo) {
        DriverNo = driverNo;
    }

    public String getDriverName() {
        return DriverName;
    }

    public void setDriverName(String driverName) {
        DriverName = driverName;
    }

    public String getViceDriverNo() {
        return ViceDriverNo;
    }

    public void setViceDriverNo(String viceDriverNo) {
        ViceDriverNo = viceDriverNo;
    }

    public String getViceDriverName() {
        return ViceDriverName;
    }

    public void setViceDriverName(String viceDriverName) {
        ViceDriverName = viceDriverName;
    }

    public String getStudentNo() {
        return StudentNo;
    }

    public void setStudentNo(String studentNo) {
        StudentNo = studentNo;
    }

    public String getStudentName() {
        return StudentName;
    }

    public void setStudentName(String studentName) {
        StudentName = studentName;
    }

    public String getOtherNo1() {
        return OtherNo1;
    }

    public void setOtherNo1(String otherNo1) {
        OtherNo1 = otherNo1;
    }

    public String getOtherName1() {
        return OtherName1;
    }

    public void setOtherName1(String otherName1) {
        OtherName1 = otherName1;
    }

    public String getOtherNo2() {
        return OtherNo2;
    }

    public void setOtherNo2(String otherNo2) {
        OtherNo2 = otherNo2;
    }

    public String getOtherName2() {
        return OtherName2;
    }

    public void setOtherName2(String otherName2) {
        OtherName2 = otherName2;
    }

    public Date getAttendTime() {
        return AttendTime;
    }

    public void setAttendTime(Date attendTime) {
        AttendTime = attendTime;
    }

    public Date getStartTime() {
        return StartTime;
    }

    public void setStartTime(Date startTime) {
        StartTime = startTime;
    }
}
