package bean;

/**
 * Created by mao on 2016/12/4.
 */
public class DriveSignPoint {
    private int DriveRecordId;
    private int StationId;
    private String ArriveTime;
    private String LeaveTime;
    private String EarlyMinutes;
    private String LateMinutes;
    private String EarlyOrLateReason;

    public DriveSignPoint() { }

    public DriveSignPoint(int driveRecordId, int stationId, String arriveTime, String leaveTime, String earlyMinutes, String lateMinutes, String earlyOrLateReason) {

        DriveRecordId = driveRecordId;
        StationId = stationId;
        ArriveTime = arriveTime;
        LeaveTime = leaveTime;
        EarlyMinutes = earlyMinutes;
        LateMinutes = lateMinutes;
        EarlyOrLateReason = earlyOrLateReason;
    }

    public int getDriveRecordId() {
        return DriveRecordId;
    }

    public void setDriveRecordId(int driveRecordId) {
        DriveRecordId = driveRecordId;
    }

    public int getStationId() {
        return StationId;
    }

    public void setStationId(int stationId) {
        StationId = stationId;
    }

    public String getArriveTime() {
        return ArriveTime;
    }

    public void setArriveTime(String arriveTime) {
        ArriveTime = arriveTime;
    }

    public String getLeaveTime() {
        return LeaveTime;
    }

    public void setLeaveTime(String leaveTime) {
        LeaveTime = leaveTime;
    }

    public String getEarlyMinutes() {
        return EarlyMinutes;
    }

    public void setEarlyMinutes(String earlyMinutes) {
        EarlyMinutes = earlyMinutes;
    }

    public String getLateMinutes() {
        return LateMinutes;
    }

    public void setLateMinutes(String lateMinutes) {
        LateMinutes = lateMinutes;
    }

    public String getEarlyOrLateReason() {
        return EarlyOrLateReason;
    }

    public void setEarlyOrLateReason(String earlyOrLateReason) {
        EarlyOrLateReason = earlyOrLateReason;
    }
}
