package zj.com.mc;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.mozilla.universalchardet.UniversalDetector;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.zip.ZipException;

import DBUtils.DBOpenHelper;
import Utils.ViewHolder;

/**
 * Created by dell on 2016/7/29.
 */
public class UtilisClass {

    //添乘信息单
    static int ADDPERSONHOURSID=1;//添乘小时数
    static int DAYADDPERSONHOURSID=2;//白天添乘小时数
    static int NIGHTADDPERSONHOURSID=3;//晚上添乘小时数
    static int MONTHADDTIME=4;//月添乘趟数
    static int KEYPERSONADDTIME=5;//关键人添乘
    static int SHOWCANCLEALLHOURS=6;//示范操纵累计小时数

    //监控分析单
    static int MMONTHTRAINTIME=7;// '月分析列数列数
    static int LASTMONTHTRAIN=8;//上旬分析列数
    static int MIDDLEMONTHTRAIN=9;//中旬分析列数
    static int NEXTMONTHTRAIN=10;//下旬分析//添乘信息单

    //抽查信息单
    static int MONTHSELECTIMEID=11;//月检查次数

    public static int getADDPERSONHOURSID(){
        return ADDPERSONHOURSID;
    }
    public static int getDAYADDPERSONHOURSID(){
            return DAYADDPERSONHOURSID;
        }
    public static int getNIGHTADDPERSONHOURSID(){
            return NIGHTADDPERSONHOURSID;
        }
    public static int getMONTHADDTIME(){
            return MONTHADDTIME;
        }
    public static int getKEYPERSONADDTIME(){
            return KEYPERSONADDTIME;
        }
    public static int getSHOWCANCLEALLHOURS(){
            return SHOWCANCLEALLHOURS;
        }
    public static int getMMONTHTRAINTIME(){
            return MMONTHTRAINTIME;
        }
    public static int getLASTMONTHTRAIN(){
            return LASTMONTHTRAIN;
        }
    public static int getMIDDLEMONTHTRAIN(){
            return MIDDLEMONTHTRAIN;
        }
    public static int getNEXTMONTHTRAIN(){
            return NEXTMONTHTRAIN;
        }
    public static int getMONTHSELECTIMEID(){
            return MONTHSELECTIMEID;
        }

    static String DAYNIGHT="22:00";//白天
    static String NIGHTDAY="6:00";
    public static String getDAYNIGHT(){
        return DAYNIGHT;
    }
    public static String getNIGHTDAY(){
            return NIGHTDAY;
        }




    /**
     * @时间获取器
     * */
    public static void gettimepicker(final Context context, final TextView ed) {
        Calendar c2 = Calendar.getInstance();
        new TimePickerDialog(context, 0, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                if (minute<10){
                    ed.setText(+hourOfDay + ":" +"0"+ minute);
                    showToast(context,"你选择的是：" + hourOfDay + "时" +"0"+ minute + "分");
                }else {
                    ed.setText(+hourOfDay + ":"+minute);
                    showToast(context,"你选择的是：" + hourOfDay + "时" + minute + "分");

                }

            }
        }, c2.get(Calendar.HOUR_OF_DAY), c2.get(Calendar.MINUTE), true).show();

    }

    public static void showToast(Context context,String s) {

        Toast.makeText(context,s,Toast.LENGTH_SHORT).show();
    }

    /**
     * @日期获取器
     */
    public static void getdatepicker(final Context context, final TextView ed) {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(context, 0, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                showToast(context,"您选择的是：" + year + "年" + (monthOfYear + 1) + "月" + dayOfMonth + "日");
                ed.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
            }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();

    }

    /**
     * @日期获取器年月
     */
    public static void getdatepicker2(final Context context, final TextView ed) {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(context, 0, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                showToast(context,"您选择的是：" + year + "年" + (monthOfYear + 1) + "月");
                ed.setText(year + "-" + (monthOfYear + 1) );
            }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();

    }

/**
 * @模糊搜索3个参数
 * */
public static List<Map> getDBpersonname(DBOpenHelper db, String tablename, String[] strs, String coads){

    String sql= "select * from "+tablename+
            " where "+strs[0]+" like ? or "+strs[1]+" like ? or "+strs[2]+" like ? ";
    String [] selectionArgs  = new String[]{"%" + coads + "%",
            "%" + coads + "%",
            "%" + coads + "%"};

    List<Map> listsearch = db.queryListMap(sql, selectionArgs);

    return  listsearch;
};

/**
 * @模糊搜索1个参数
 * */
    public static List<Map> getDBsingargs(DBOpenHelper db, String tablename, String strs, String coads){

        String sql= "select * from "+tablename+
                " where "+strs+" like ? ";
        String [] selectionArgs  = new String[]{"%" + coads + "%"};

        List<Map> listsearch = db.queryListMap(sql, selectionArgs);

        return  listsearch;
    };
/**
 * @commadapter的converlistener设置
 * */

    /**
     * @commadapter的conver设置
     * */
    public static  void setconvert(ViewHolder holder, Map map, String code1, String cod2, String code3, String code4) {
        holder.setText(R.id.daily_Listing_itemmessage, String.valueOf(map.get(code1)));
        holder.setText(R.id.daily_Listing_itemtype, String.valueOf(map.get(cod2)));
        holder.setText(R.id.daily_Listing_itemnumber, map.get(code3) + "");
        holder.setText(R.id.daily_Listing_itemtime, map.get(code4) + "");
        if (map.get("IsUploaded").equals(1)){
            holder.setText(R.id.daily_Listing_itemdo,"上传");
        }else {
            holder.setText(R.id.daily_Listing_itemdo,"已上传");
        }
    }

//年月日时分秒
    public static String getStringDate(){
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH)+1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour=c.get(Calendar.HOUR_OF_DAY);
        int min=c.get(Calendar.MINUTE);
        int sec=c.get(Calendar.SECOND);
        String smin="";
        String ssec="";
        if (min<10){
            smin="0"+min;
        }else {
            smin=min+"";
        }
        if (sec<10){
            ssec="0"+sec;
        }else {
            ssec=sec+"";
        }

        String date=year+"-"+month+"-"+day+" "+hour+":"+smin+":"+ssec;

        return date;
    }
    //月日时分
    public static String getStringDate1(){
        Calendar c = Calendar.getInstance();
        int month = c.get(Calendar.MONTH)+1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour=c.get(Calendar.HOUR_OF_DAY);
        int min=c.get(Calendar.MINUTE);
        String smin="";
        String ssec="";
        if (min<10){
            smin="0"+min;
        }else {
            smin=min+"";
        }

        String date=month+"-"+day+" "+hour+":"+smin;

        return date;
    }
//年月日
    public static String getStringDate3(){
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH)+1;
        int day = c.get(Calendar.DAY_OF_MONTH);

        String date=year+"-"+month+"-"+day;
        return date;
    }

    //年月
    public static String getStringDate2(){
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH)+1;

        String date=year+"-"+month;
        return date;
    }



    /**
     * make true current connect service is wifi
     * @param mContext
     * @return
     */
    public static boolean isWifi(Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null
                && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;


    }
//设置progressdialog
    public static   ProgressDialog setprogressDialog(Context context){
     ProgressDialog   progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("等待");
        progressDialog.setMessage("正在加载....");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
//        progressDialog.show();
        return progressDialog;

    }

    /**
     * date2比date1多的天数
     * @param date1
     * @param date2
     * @return
     */
    public static int differentDays(Date date1, Date date2)
    {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        int day1= cal1.get(Calendar.DAY_OF_YEAR);
        int day2 = cal2.get(Calendar.DAY_OF_YEAR);

        int year1 = cal1.get(Calendar.YEAR);
        int year2 = cal2.get(Calendar.YEAR);
        if(year1 != year2) //同一年
        {
            int timeDistance = 0 ;
            for(int i = year1 ; i < year2 ; i ++)
            {
                if(i%4==0 && i%100!=0 || i%400==0) //闰年
                {
                    timeDistance += 366;
                }
                else //不是闰年
                {
                    timeDistance += 365;
                }
            }

            return timeDistance + (day2-day1) ;
        }
        else //不同年
        {
            System.out.println("判断day2 - day1 : " + (day2-day1));
            return day2-day1;
        }
    }
//根据personInfo获取姓名
    public static String getName(DBOpenHelper dbOpenHelper, String personid){
        String name="";
        List<Map> list=dbOpenHelper.queryListMap("select * from PersonInfo where Id=?",new String[]{personid});
        if (list.size()!=0) {
            name = list.get(0).get("Name") + "";
        }
        return name;
    }

    //根据personInfo获取姓名
    public static String getWorkNo(DBOpenHelper dbOpenHelper, String personid){
        String workno="";
        List<Map> list=dbOpenHelper.queryListMap("select * from PersonInfo where Id=?",new String[]{personid});
        if (list.size()!=0) {
            workno = list.get(0).get("WorkNo") + "";
        }
        return workno;
    }


    //保存图片到本地

    public  static void  saveimg(Context context,String imgName,Bitmap bitmap){

        File file = new File(context.getExternalFilesDir(null).getPath()+"/", imgName + ".png");
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
            Log.i("", "成功");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
//通过行车资料分类表的Id去行车资料文件管理表找typeid找到文件名称
    public static String getFileName(DBOpenHelper dbOpenHelper,String FileTypeId){
        String fileName="";
        List<Map> fileInfo=dbOpenHelper.queryListMap("select * from TraficFiles where TypeId=? and IsDelete=?",new String[]{FileTypeId,0+""});

        if (fileInfo.size()!=0){
            fileName=fileInfo.get(0).get("FileName")+"";
        }
        return fileName;
    }

    //通过行车资料分类表的Id去行行车资料分类表找typeid找到文件名称
    public static List<Map> getFilelist(DBOpenHelper dbOpenHelper, String fatherFileTypeId){
        List<Map> fileInfo=dbOpenHelper.queryListMap("select * from TraficFileType where ParentId=?",new String[]{fatherFileTypeId});
        return fileInfo;
    }
    //通过行车资料分类表的Id去行车资料文件管理表找typeid找到文件名称
    public static List<Map> getFilelist2(DBOpenHelper dbOpenHelper, String fatherFileTypeId){
        List<Map> fileInfo=dbOpenHelper.queryListMap("select * from TraficFiles where TypeId=? and IsDelete=?",new String[]{fatherFileTypeId,0+""});
        return fileInfo;
    }



//解压带中文的zip文件

    public static void    unZipFile(String archive, String decompressDir)throws IOException, FileNotFoundException, ZipException
    {
        BufferedInputStream bi;
        ZipFile zf = new ZipFile(archive, "GBK");
        Enumeration e = zf.getEntries();
        while (e.hasMoreElements())
        {
            ZipEntry ze2 = (ZipEntry) e.nextElement();
            String entryName = ze2.getName();
            String path = decompressDir + "/" + entryName;
            if (ze2.isDirectory())
            {
                System.out.println("正在创建解压目录 - " + entryName);
                File decompressDirFile = new File(path);
                if (!decompressDirFile.exists())
                {
                    decompressDirFile.mkdirs();
                }
            } else
            {
                System.out.println("正在创建解压文件 - " + entryName);
                String fileDir = path.substring(0, path.lastIndexOf("/"));
                File fileDirFile = new File(fileDir);
                if (!fileDirFile.exists())
                {
                    fileDirFile.mkdirs();
                }
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(decompressDir + "/" + entryName));
                bi = new BufferedInputStream(zf.getInputStream(ze2));
                byte[] readContent = new byte[1024];
                int readCount = bi.read(readContent);
                while (readCount != -1)
                {
                    bos.write(readContent, 0, readCount);
                    readCount = bi.read(readContent);
                }
                bos.close();
            }
        }
        zf.close();
        //bIsUnzipFinsh = true;
    }
//获得guid
public static String getuuid(){

    String guid= UUID.randomUUID()+"";

    return guid;
}
//遍历指定文件夹获取。htm文件

    public static String getFileDir(String filePath) {
        String filepath="";
        try{
            File f = new File(filePath);
            File[] files = f.listFiles();// 列出所有文件
            if(files != null){
                int count = files.length;// 文件个数
                for (int i = 0; i < count; i++) {
                    File file = files[i];
                    if (!file.getName().endsWith(".files")){
                        if(file.getName().endsWith(".htm")){
//                            filepath=file.getName().substring(0,file.getName().lastIndexOf("."));
                            filepath=file.getPath();
                        }else{
                        }

                    }

                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return filepath;
    }


//获取文件的解码字体
    public static String getFileIncode(File file) {

        if (!file.exists()) {
            System.err.println("getFileIncode: file not exists!");
            return null;
        }

        byte[] buf = new byte[4096];
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            // (1)
            UniversalDetector detector = new UniversalDetector(null);

            // (2)
            int nread;
            while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
                detector.handleData(buf, 0, nread);
            }
            // (3)
            detector.dataEnd();

            // (4)
            String encoding = detector.getDetectedCharset();
            if (encoding != null) {
                System.out.println("Detected encoding = " + encoding);
            } else {
                System.out.println("No encoding detected.");
            }

            // (5)
            detector.reset();
            fis.close();
            return encoding;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }





    //设置司机不能为空，并且id不能为空！
    public static void setDriverid(Context context, TextView textView, TextView tv, String driverid){

        if(textView==tv){
            if (driverid!=""&&driverid!=null){
                 }else {
                  tv.setText("");
            UtilisClass.showToast(context,"未找到相关人，请选择列表中的名字！");
            }
        }
    }

    //设置数据变动会改变driverid
    public static void  setdriverid2(TextView textView,TextView tv,String driverid){
        if(textView==tv){
            driverid="";
        }else {
        }
    }

//输入法隐藏
    public static void hidInputMethodManager(Context context,View edit){
        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edit.getWindowToken(), 0);
    }



}
