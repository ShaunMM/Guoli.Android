package Utils;

import android.net.Uri;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import DBUtils.DBOpenHelper;
import config.ISystemConfig;
import zj.com.mc.UtilisClass;

/**
 * 网络地址及网络操作
 */
public class NetUtils {

    public static String defultTime = "1970-01-01";
    public static String APPKEYQUOTA = "/App/QuotaRecordUpload?";

    public static String SIGNATURE = "signature=bcad117ce31ac75fcfa347acefc8d198";

    public static String APPINDEX1 = "/App/Index?";
    public static String APPINDEX = "/App/Index";
    public static String APPUPGRADE = "/App/Upgrade?";

    public static String DbUpdateLog6 = "/App/Index?signature=bcad117ce31ac75fcfa347acefc8d198&TableName=DbUpdateLog&Operate=6&StartId=";
    //分页请求数据
    public static String DbUpdateLog7 = "signature=bcad117ce31ac75fcfa347acefc8d198&TableName=DbUpdateLog&Operate=7&StartId=";
    public static String PageSize = "&PageSize=10000";
    public static String PageIndex = "&PageIndex=";


    public static String SYSTEMFOLDER = "JWYYGKXT";
    public static String TRAFFICDATA = "JWYYGKXT/TrafficData";
    public static String PHOTOPATH = "JWYYGKXT/Photo";
    public static String AUDIOPATH = "JWYYGKXT/Audio";
    public static String VIDEOPATH = "JWYYGKXT/Video";
    public static String APKPATH = "JWYYGKXT/APK";
    public static String RUNLOG = "JWYYGKXT/RunLog";
    public static String APPVERIFY = "guoli.app.upgradedat:";


    //需要上传的数据表单（判断是否更新数据）
    //InstructorPeccancy违章记录
    public static String[] dailyWorkUpTableNames = {"InstructorTempTake", "InstructorTeach",
            "InstructorRepair", "InstructorPlan", "InstructorPeccancy", "InstructorLocoQuality",
            "InstructorKeyPerson", "InstructorGoodJob", "InstructorCheck", "InstructorWifiRecord", "InstructorAnalysis", "Feedback",
            "ExamRecords", "ExceptionLog"};
    public static String[] driverNoteUpTableNames = {"InstructorWifiRecord", "Feedback",
            "ExamRecords", "ExceptionLog"};
    public static String[] appWifiTableNames = {"AppOperateLog"};

    // 将list<Map> 转换成 json字符串
    public static StringBuffer getjson(List<Map> list) {
        StringBuffer sb = new StringBuffer();
        sb.append("{");
        for (Map<String, Object> map : list) {
            for (String key : map.keySet()) {
                sb.append("\"").append(key).append("\":\"").append(map.get(key))
                        .append("\"").append(",");
            }
        }
        sb.deleteCharAt(sb.lastIndexOf(","));
        sb.append("}");
        return sb;
    }

    public static StringBuffer getjsonlist(List<Map> list) {
        StringBuffer sb = new StringBuffer();
        sb.append("[");
        for (int i = 0; i < list.size(); i++) {
            sb.append("{");
            for (Object key : list.get(i).keySet()) {
                sb.append("\"").append(key).append("\":\"").append(list.get(i).get(key))
                        .append("\"").append(",");
            }
            sb.deleteCharAt(sb.lastIndexOf(","));
            sb.append("}");
            sb.append(",");
        }
        if (list.size() != 0) {
            sb.deleteCharAt(sb.lastIndexOf(","));
        }
        sb.append("]");
        return sb;
    }

    public static StringBuffer getdevicelist(List<Map> list) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < list.size(); i++) {
            sb.append("{");
            for (Object key : list.get(i).keySet()) {
                sb.append("\"").append(key).append("\":\"").append(list.get(i).get(key))
                        .append("\"").append(",");
            }
            sb.deleteCharAt(sb.lastIndexOf(","));
            sb.append("}");
            sb.append(",");
        }
        if (list.size() != 0) {
            sb.deleteCharAt(sb.lastIndexOf(","));
        }
        return sb;
    }

    //向服务器post未上传的数据
    public static void updataarguments3dingle(DBOpenHelper dbOpenHelper, ISystemConfig systemConfig, String tablename, String id) {
        List<Map> uplist = dbOpenHelper.queryListMap("select * from " + tablename + " where " +
                "Id=?", new String[]{id});
        String currenttime = UtilisClass.getStringDate();
        uplist.get(0).put("UploadTime", currenttime);
        //uplist.get(0).put("IsRemoved","false");
        if (uplist.size() != 0) {
            try {
                String json = NetUtils.getjsonlist(uplist).toString();
                //差一个post地址
                json = URLEncoder.encode(json, "UTF-8");
                String path = NetUtils.SIGNATURE + "&" + "TableName=" + tablename + "&" + "Operate=2" + "&" + "Data=" + json;

                String responsepath = systemConfig.getHost() + APPINDEX1 + path;
                String responsejson = HttpUtils.getJsonContent(responsepath);

                JSONObject object = new JSONObject(responsejson);
                if (object.get("code").equals(107)) {
                    String tableitemId = uplist.get(0).get("Id") + "";
                    dbOpenHelper.update(tablename, new String[]{"IsUploaded"}, new Object[]{"1"}, new String[]{"Id"}, new String[]{tableitemId});
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //向服务器post未上传的数据
    public static void updataarguments3dinglehome(List<Map> uplist, DBOpenHelper dbOpenHelper, ISystemConfig systemConfig, String tablename) {
        String currenttime = UtilisClass.getStringDate();
        uplist.get(0).put("UploadTime", currenttime);
        if (uplist.size() != 0) {
            try {
                String json = NetUtils.getjsonlist(uplist).toString();
                //差一个post地址
                json = URLEncoder.encode(json, "UTF-8");
                String path = NetUtils.SIGNATURE + "&" + "TableName=" + tablename + "&" + "Operate=2" + "&" + "Data=" + json;
                //HttpRequestUtil.sendPostRequest(NetUtils.APPDOOR, map, null);
                String responsepath = systemConfig.getHost() + APPINDEX1 + path;
                String responsejson = HttpUtils.getJsonContent(responsepath);
                System.out.println("aaaaaaaaaaaaaaa" + responsejson);
                JSONObject object = new JSONObject(responsejson);
                if (object.get("code").equals(107)) {
                    String tableitemId = uplist.get(0).get("Id") + "";
                    dbOpenHelper.update(tablename, new String[]{"IsUploaded"}, new Object[]{"1"}, new String[]{"Id"}, new String[]{tableitemId});
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //向服务器post未上传的数据
    public static void updataAppWifi(List<Map> uplist, List<Map> device, DBOpenHelper dbOpenHelper, ISystemConfig systemConfig, String tablename) {
        String currenttime = UtilisClass.getStringDate();
        uplist.get(0).put("UploadTime", currenttime);
        if (uplist.size() != 0) {
            try {
                String json = NetUtils.getjsonlist(uplist).toString();
                String devicejson = NetUtils.getdevicelist(device).toString();
                String path2 = NetUtils.SIGNATURE + "&" + "Data=" + json + "&" + "device=" + devicejson;
                String responsepath2 = systemConfig.getHost() + "/App/OperateLog?" + path2;
                json = URLEncoder.encode(json, "UTF-8");
                devicejson = URLEncoder.encode(devicejson, "UTF-8");
                String path = NetUtils.SIGNATURE + "&" + "Data=" + json + "&" + "device=" + devicejson;
                String responsepath = systemConfig.getHost() + "/App/OperateLog?" + path;
                String responsejson = HttpUtils.getJsonContent(responsepath);
                JSONObject object = new JSONObject(responsejson);
                if (object.get("code").equals(100)) {
                    String tableitemId = uplist.get(0).get("Id") + "";
                    dbOpenHelper.update(tablename, new String[]{"IsUploaded"}, new Object[]{"0"}, new String[]{"Id"}, new String[]{tableitemId});
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //向服务器post未上传的数据//指标完成情况
    public static void updateInstructorQuotaRecord(List<Map> uplist, DBOpenHelper dbOpenHelper, ISystemConfig systemConfig, String tablename) {
        String currenttime = UtilisClass.getStringDate();
        uplist.get(0).put("UploadTime", currenttime);
        //uplist.get(0).put("IsRemoved","false");
        if (uplist.size() != 0) {
            try {
                String json = NetUtils.getjsonlist(uplist).toString();
                //差一个post地址
                json = URLEncoder.encode(json, "UTF-8");
                String path = NetUtils.SIGNATURE + "&" + "list=" + json;
                //HttpRequestUtil.sendPostRequest(NetUtils.APPDOOR, map, null);
                //String responsepath = NetUtils.APPKEYQUOTA + path;
                String responsepath = systemConfig.getHost() + NetUtils.APPKEYQUOTA + path;
                String responsejson = HttpUtils.getJsonContent(responsepath);
                System.out.println("aaaaaaaaaaaaaaa" + responsejson);
                JSONObject object = new JSONObject(responsejson);
                if (object.get("code").equals(100)) {
                    String tableitemId = uplist.get(0).get("Id") + "";
                    dbOpenHelper.update(tablename, new String[]{"IsUploaded"}, new Object[]{"1"}, new String[]{"Id"}, new String[]{tableitemId});
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 根据字段进行提取List《string》集合
     */
    public static List<String> getTableNamelist(List<Map<String, Object>> mapList, String code) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < mapList.size(); i++) {
            list.add(mapList.get(i).get(code) + "");
        }
        list = removeDuplicate(list);
        return list;
    }

    //list<String>去重
    public static List<String> removeDuplicate(List<String> list) {
        HashSet<String> h = new HashSet<String>(list);
        list.clear();
        list.addAll(h);
        return list;
    }

    //list<String>去重
    public static List<Map> reDuplicate(List<Map> list) {
        HashSet<Map> h = new HashSet<Map>(list);
        list.clear();
        list.addAll(h);
        return list;
    }    //list<String>去重

    public static List<List<Map>> Duplicate(List<List<Map>> list) {
        HashSet<List<Map>> h = new HashSet<List<Map>>(list);
        list.clear();
        list.addAll(h);
        return list;
    }

    /**
     * 写个去除数组中重复数据的方法
     */
    public static String[] array_unique(String[] a) {
        // array_unique
        List<String> list = new LinkedList<String>();
        for (int i = 0; i < a.length; i++) {
            if (!list.contains(a[i])) {
                list.add(a[i]);
            }
        }
        return (String[]) list.toArray(new String[list.size()]);
    }

    /**
     * 根据code筛选出list集合去重
     * 添加第一个
     */
    public static List<Map<String, Object>> singleTableUpdataList(List<Map<String, Object>> list, String code) {

        List<Map<String, Object>> tmpList = new ArrayList<Map<String, Object>>();
        Set<String> keysSet = new HashSet<String>();
        for (Map<String, Object> map : list) {
            String keys = map.get(code) + "";
            int beforeSize = keysSet.size();
            keysSet.add(keys);
            int afterSize = keysSet.size();
            if (afterSize == beforeSize + 1) {
                tmpList.add(map);
            }
        }
        return tmpList;
    }

    /**
     * 根据字段code类中的内容筛选
     */
    public static List<Map<String, Object>> getSingleTableNameList(List<Map<String, Object>> maps, String code, Object s) {
        List<Map<String, Object>> list = new ArrayList<>();

        for (Map<String, Object> map : maps) {
            if (map.get(code).equals(s)) {
                list.add(map);
            }
        }
        return list;
    }

    /**
     * 单张表的URL获取
     *
     * @param Ids        想要获取数去的id集合
     * @param tableNames 表名
     */
    public static String getaddurl(List<String> Ids, String tableNames, ISystemConfig systemConfig) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("(");
        for (int i = 0; i < Ids.size(); i++) {
            stringBuffer.append(Ids.get(i));
            stringBuffer.append(",");
        }
        stringBuffer.deleteCharAt(stringBuffer.lastIndexOf(","));
        stringBuffer.append(")");
        // Uri.encode("Comdition=Id In");
        String addurl = systemConfig.getHost() + APPINDEX1 + Uri.encode("Comdition=Id In") + stringBuffer.toString() + "&TableName=" + tableNames + "&Operate=6&" + NetUtils.SIGNATURE;
        return addurl;
    }

    public static Map<String, String> getcontention(List<String> Idscomditionid, String tablename) {

        Map<String, String> m = new HashMap<>();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("Id In(");

        for (int i = 0; i < Idscomditionid.size(); i++) {
            stringBuffer.append(Idscomditionid.get(i));
            stringBuffer.append(",");
        }
        stringBuffer.deleteCharAt(stringBuffer.lastIndexOf(","));
        stringBuffer.append(")");
        String s = stringBuffer.toString();

        m.put("Condition", s);
        m.put("TableName", tablename);
        m.put("Operate", "6");
        m.put("signature", "bcad117ce31ac75fcfa347acefc8d198");
        return m;
    }


    public static void Dosingletableinsert(List<Map<String, Object>> addTableDatalist, String tableNames, DBOpenHelper dbOpenHelper, ISystemConfig systemConfig) {

        HttpURLConnection conn = null;
        if (addTableDatalist.size() != 0) {
            List<String> Ids = NetUtils.getTableNamelist(addTableDatalist, "TargetId");
            try {
                Map map = getcontention(Ids, tableNames);
                String str = map.toString();
                //conn = (HttpURLConnection) HttpRequestUtil.sendPostRequest(APPKEY2, getcontention(Ids, tableNames), null);
                conn = (HttpURLConnection) HttpRequestUtil.sendPostRequest(systemConfig.getHost() + APPINDEX, getcontention(Ids, tableNames), null);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String addjson = getresponse(conn);
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(addjson);
                String jsonArray = jsonObject.getString("data");
                Log.i("tttttttttttttttttt", jsonArray);
                List<Map<String, Object>> jsonarraulist = StringListUtils.getList(jsonArray);
                for (int i = 0; i < jsonarraulist.size(); i++) {
                    dbOpenHelper.insert(tableNames, jsonarraulist.get(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static void DosingletableUpdata(List<Map<String, Object>> addTableDatalist, String tableNames, DBOpenHelper dbOpenHelper, ISystemConfig systemConfig) {
        HttpURLConnection conn = null;
        if (addTableDatalist.size() != 0) {
            List<String> Ids = NetUtils.getTableNamelist(addTableDatalist, "TargetId");
            try {
                //conn = (HttpURLConnection) HttpRequestUtil.sendPostRequest("http://192.168.43.97:8080/App/Index", getcontention(Ids,tableNames), null);
                //conn = (HttpURLConnection) HttpRequestUtil.sendPostRequest(APPKEY2, getcontention(Ids, tableNames), null);
                conn = (HttpURLConnection) HttpRequestUtil.sendPostRequest(systemConfig.getHost() + APPINDEX, getcontention(Ids, tableNames), null);
            } catch (Exception e) {
                e.printStackTrace();
            }

            String addjson = getresponse(conn);
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(addjson);
                String jsonArray = jsonObject.getString("data");
                List<Map<String, Object>> jsonarraulist = StringListUtils.getList(jsonArray);
                Log.i("更新操作jsonarraulist", String.valueOf(jsonarraulist));
                Map<String, String> map = new HashMap<>();
                for (int i = 0; i < jsonarraulist.size(); i++) {
                    map.put("Id", jsonarraulist.get(i).get("Id") + "");
                    Log.i("更新操作map", String.valueOf(map));
                    if (dbOpenHelper.queryItemMap("select * from " + tableNames + " where Id=?", new String[]{jsonarraulist.get(i).get("Id") + ""}).size() != 0) {
                        dbOpenHelper.update(tableNames, jsonarraulist.get(i), map);
                        Log.i("更新操作数据后", String.valueOf(dbOpenHelper.queryItemMap("select * from  PersonInfo " + " where Id=?", new String[]{jsonarraulist.get(i).get("Id") + ""})));
                    } else {
                        dbOpenHelper.insert(tableNames, jsonarraulist.get(i));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ClassCastException a) {
                String str = a.toString();
                a.printStackTrace();
            }
        }
    }

    public static void DosingletableDelete(List<Map<String, Object>> addTableDatalist, String tableNames, DBOpenHelper dbOpenHelper) throws ExecutionException {
        if (tableNames.equals("TraficFiles")) {
            if (addTableDatalist.size() != 0) {
                for (int i = 0; i < addTableDatalist.size(); i++) {
                    Map map = dbOpenHelper.queryItemMap("select LocaPath from TraficFiles where Id =? ",
                            new String[]{addTableDatalist.get(i).get("TargetId").toString()});
                    if (map.get("LocaPath") != null) {
                        String localPath = map.get("LocaPath").toString();
                        File file = new File(localPath);
                        deleteFile(file);
                    }
                }
            }
        } else if (tableNames.equals("Announcement")) {
            if (addTableDatalist.size() != 0) {
                for (int i = 0; i < addTableDatalist.size(); i++) {
                    Map map = dbOpenHelper.queryItemMap("select LocaPath from Announcement where Id =? and AnnounceType = 2 ",
                            new String[]{addTableDatalist.get(i).get("TargetId").toString()});
                    if (map.get("LocaPath") != null) {
                        String localPath = map.get("LocaPath").toString();
                        File file = new File(localPath);
                        deleteFile(file);
                    }
                }
            }
        }

        if (addTableDatalist.size() != 0) {
            List<String> Ids = NetUtils.getTableNamelist(addTableDatalist, "TargetId");
            Map<String, String> map = new HashMap<>();
            for (int i = 0; i < Ids.size(); i++) {
                map.put("Id", Ids.get(i));
                dbOpenHelper.delete(tableNames, map);
            }
        }
    }

    private static void deleteFile(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }
        if (file.isDirectory()) {
            File[] childFile = file.listFiles();
            if (childFile == null || childFile.length == 0) {
                file.delete();
                return;
            }
            for (File f : childFile) {
                deleteFile(f);
            }
            file.delete();
        }
    }

    public static String getresponse(URLConnection connection) {
        String s = "";
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br;
        try {
            is = connection.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            isr = new InputStreamReader(is, "utf-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        br = new BufferedReader(isr);
        String line = "";
        try {
            while ((line = br.readLine()) != null) {
                s += line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (isr != null) {
            try {
                isr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
    }
}