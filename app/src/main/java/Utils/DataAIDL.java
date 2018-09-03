package Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by dell on 2016/8/1.
 */
public class DataAIDL {

    /**
     * 所有数据库的键KEY
     * */
    public static String[] AnnounceCommandsNameList = {"Id","CommandNo","CommandInterval","Direction","SpeedLimitLocation","StartEndTime","LoseEffectTime","LimitedSpeed","AddTime","IsActive"};
    public static String[] AnnouncementNameList = {"Id","Title","Content","SystemUserId","PubTime"};
    public static String[] BaseLineNameList = {"Id","LineName","FirstStationId","FirstStation","LastStationId","LastStation","LineLength","Spell","UpdateTime","IsDelete"};
    public static String[] BaseStationNameList = {"Id","StationName","SN","Spell","IsDelete"};
    public static String[] datetimeNameList = {"Id","DepartmentName","ParentId","IsDelete","AddTime"};
    public static String[] DbUpdateLogNameList = {"Id","TableName","UpdateType","TargetId","UpdateTime"};
    public static String[] DepartInfoNameList = {"Id","DepartmentName","ParentId","IsDelete","AddTime"};
    public static String[] DrivePlanNameList = {"Id","LineName","TrainCode","LocoType","DriverNo","DriverName","ViceDriverNo","ViceDriverName","StudentNo","StudentName","AttendTime","StartTime"};
    public static String[] DriveRecordsNameList = {"Id","ViceDriverId","StudentDriverId","OtherDriverId","LocomotiveType","AttendTime","GetTrainTime","AttendForecast","GiveTrainTime","RecordEndTime","OperateConsume","StopConsume","RecieveEnergy","LeftEnergy","EngineOil","AirCompressorOil","TurbineOil","GearOil","GovernorOil","OtherOil","Staple","MultiLocoDepot","MultiLocoType","MultiLocoSection","EndSummary","AddTime","IsDelete"};
    public static String[] DriveSignPointNameList = {"Id","DriveRecordId","StationId","ArriveTime","LeaveTime","EarlyMinutes","LateMinutes","EarlyOrLateReason"};
    public static String[] DriveTrainNoAndLineNameList = {"Id","DriveRecordId","TrainNoId","LineId","AddTime","IsDelete"};
    public static String[] ExamAnswersNameList = {"Id","QuestionId","Answer","IsRight"};
    public static String[] ExamErrorQuestionsNameList = {"Id","PersionId","QuestionId","ErrorCount","HasRemembered"};
    public static String[] ExamFilesNameList = {"Id","ExamTypeId","FileName","FileDesc","FilePath","AddTime","IsDelete"};
    public static String[] ExamNotifyNameList = {"Id","PostId","ExamTypeId","QuestionCount","PassScore","ResitCount","EndTime"};
    public static String[] ExamQuestionNameList = {"Id","ExamFileId","Question","AnswerType"};
    public static String[] ExamRecordsNameList = {"Id","ExamNotifyId","PersionId","RightCount","WrongCount","Score","TimeSpends","UploadTime","ExamTime"};
    public static String[] ExamTypeNameList = {"Id","TypeName"};
    public static String[] FeedbackNameList = {"Id","Content","PersonId","AddTime"};
    public static String[] InstructorAnalysisNameList = {"Id","InstructorId","TrainCode","LocomotiveType","RunDate","RunSection","DriverId","ViceDriverId","AnalysisStart","AnalysisEnd","Problems","Suggests","UploadTime","IsDelete"};
    public static String[] InstructorCheckNameList = {"Id","InstructorId","StartTime","EndTime","Location","CheckType","ProblemCount","CheckContent","Problems","Suggests","UploadTime","IsDelete"};
    public static String[] InstructorGoodJobNameList = {"Id","InstructorId","WriteDate","DriverId","GoodJobType","GeneralSituation","Suggests","UploadTime","IsDelete"};
    public static String[] InstructorKeyPersonNameList = {"Id","InstructorId","ConfirmDate","KeyPersonId","ExpectRemoveTime","ActualRemoveTime","KeyLocation","PersonConfirmReason","HelpMethod","PersonRemoveSuggests","LocationConfirmReason","ControlMethod","ActualControl","LocationRemoveSuggests","UploadTime","IsDelete"};
    public static String[] InstructorLocoQualityNameList = {"Id","InstructorId","RegistDate","TrainCode","LocomotiveType","DriverId","RepairClass","MaintenanceStatus","FaultLocation","GeneralSituation","UploadTime","IsDelete","Score"};
    public static String[] InstructorodJobNameList = {"Id","InstructorId","WriteDate","DriverId","odJobType","GeneralSituation","Suggests","UploadTime","IsDelete"};
    public static String[] InstructorPeccancyNameList = {"Id","InstructorId","WriteDate","DriverId","PeccancyType","GeneralSituation","Analysis","Suggests","UploadTime","IsDelete"};
    public static String[] InstructorPlanNameList = {"Id","InstructorId","WriteDate","WorkSummary","Problems","WorkPlans","UploadTime","IsDelete"};
    public static String[] InstructorQuotaNameList = {"Id","QuotaName","QuataAmmount","AddTime","IsDelete"};
    public static String[] InstructorQuotaRecordNameList = {"Id","InstructorId","QuotaId","FinishedAmmount","UpdateTime","Year","Month","IsDelete"};
    public static String[] InstructorRepairNameList = {"Id","InstructorId","HappenTime","Location","TrainCode","LocomotiveType","DriverId","ViceDriverId","StudentId","FaultLocation","FaultReason","Responsibility","UploadTime","IsDelete"};
    public static String[] InstructorTeachNameList = {"Id","InstructorId","TeachPlace","JoinCount","TeachStart","TeachEnd","TeachContent","UploadTime","IsDelete"};
    public static String[] InstructorTempTakeNameList = {"Id","InstructorId","TakeDate","TrainCode","LocomotiveType","DriverId","ViceDriverId","StudentId","CarCount","WholeWeight","Length","TakeSection","RunStart","RunEnd","OperateSection","OperateStart","OperateEnd","AttendTime","EndAttendTime","TakeAims","Problems","Suggests","UploadTime","IsDelete"};
    public static String[] LineStationsNameList = {"Id","LineId","StationName","StationId","Sort"};
    public static String[] PersonInfoNameList = {"Id","PersonId","WorkNo","DepotId","DepartmentId","Name","Spell","BirthDate","Sex","PostId","PhotoPath","IdentityNo","Password","IsDelete"};
    public static String[] PostsNameList = {"Id","PostName","IsDelete"};
    public static String[] ReceiveDataNameList = {"tableName","code","msg"};
    public static String[] StationFilesNameList = {"Id","StationId","FileType","FilePath","AddTime","IsDelete","name"};
    public static String[] StationFiles2NameList = {"Id","StationId","FileType","FilePath","AddTime","IsDelete"};
    public static String[] TraficFilesNameList = {"Id","TypeId","FileName","FileExtension","FileSize","FilePath","AddTime","IsDelete"};
    public static String[] TraficFileTypeNameList = {"Id","TypeName","ParentId"};
    public static String[] TraficKeywordsNameList = {"Id","Keywords","SearchCount","AddTime"};
    public static String[] TraficSearchRecordNameList = {"Id","PersonId","Keywords","SearchCount"};
    public static String[] TraficSearchResultNameList = {"Id","KeywordsId","SearchResult"};
    public static String[] TraficSearchTextNameList = {"Id","FileId","Chapter","Item","Content","UpdateTime","IsDelete"};
    public static String[] TrainFormationNameList = {"Id","DriveRecordId","StationId","CarriageCount","CarryingCapacity","Length","NoteTime"};
    public static String[] TrainMomentNameList = {"Id","TrainNoLineId","TrainStationId","ArriveTime","DepartTime","StopMinutes","IntervalKms","SuggestSpeed","Sort"};
    public static String[] TrainNoNameList = {"Id","FullName","Code","Number","Direction","RunType","FirstStation","LastStation","UpdateTime","IsDelete"};
    public static String[] TrainNoLineNameList = {"Id","TrainNoId","LineId","Sort","UpdateTime","IsDelete"};

    /**
     *   将json 数组转换为Map 对象
     * @param jsonString
     * @return
     */
    public static Map<String, Object> getMap(String jsonString)
    {
        JSONObject jsonObject;
        try
        {
        jsonObject = new JSONObject(jsonString);   @SuppressWarnings("unchecked")
        Iterator<String> keyIter = jsonObject.keys();
            String key;
            Object value;
            Map<String, Object> valueMap = new HashMap<String, Object>();
            while (keyIter.hasNext())
            {
                key = (String) keyIter.next();
                value = jsonObject.get(key);
                valueMap.put(key, value);
            }
            return valueMap;
        }

        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 把json 转换为ArrayList 形式
     * @return
     */
    public static List<Map<String, Object>> getList(String jsonString)
    {
        List<Map<String, Object>> list = null;
        try
        {
            JSONArray jsonArray = new JSONArray(jsonString);
            JSONObject jsonObject;
            list = new ArrayList<Map<String, Object>>();
            for (int i = 0; i < jsonArray.length(); i++)
            {
                jsonObject = jsonArray.getJSONObject(i);

                list.add(getMap(jsonObject.toString()));

            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return list;

    }
}
