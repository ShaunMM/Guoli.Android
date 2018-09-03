package bean;

/**
 * Created by mao on 2016/12/4.
 */
public class TrainFormation {
    private int DriveRecordId;
    private int StationId;
    private String CarriageCount;
    private String CarryingCapacity;
    private String Length;

    public TrainFormation() { }

    public TrainFormation(int driveRecordId, int stationId, String carriageCount, String carryingCapacity, String length) {
        DriveRecordId = driveRecordId;
        StationId = stationId;
        CarriageCount = carriageCount;
        CarryingCapacity = carryingCapacity;
        Length = length;
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

    public String getCarriageCount() {
        return CarriageCount;
    }

    public void setCarriageCount(String carriageCount) {
        CarriageCount = carriageCount;
    }

    public String getCarryingCapacity() {
        return CarryingCapacity;
    }

    public void setCarryingCapacity(String carryingCapacity) {
        CarryingCapacity = carryingCapacity;
    }

    public String getLength() {
        return Length;
    }

    public void setLength(String length) {
        Length = length;
    }
}
