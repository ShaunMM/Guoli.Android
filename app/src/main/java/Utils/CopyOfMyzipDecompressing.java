package Utils;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 * Created by dell on 2016/9/8.
 */
public class CopyOfMyzipDecompressing {


    //zip文件解压方法
        public static void getfileDecompress(String presszipfilepath,String outdepresszipfilepath){

            long startTime=System.currentTimeMillis();
            try {
                ZipInputStream Zin=new ZipInputStream(new FileInputStream(presszipfilepath));//输入源zip路径
                System.out.println("ssssssssssssssssss"+Zin.toString());
                BufferedInputStream Bin=new BufferedInputStream(Zin);
                String Parent=outdepresszipfilepath; //输出路径（文件夹目录）
                File Fout=null;
                ZipEntry entry;
                try {
                    while((entry = Zin.getNextEntry())!=null && !entry.isDirectory()){
                        Fout=new File(Parent,entry.getName());
                        if(!Fout.exists()){
                            (new File(Fout.getParent())).mkdirs();
                        }

                        FileOutputStream out=new FileOutputStream(Fout);
                        BufferedOutputStream Bout=new BufferedOutputStream(out);
                        int b;
                        while((b=Bin.read())!=-1){
                            Bout.write(b);
                        }
                        Bout.close();
                        out.close();
                        System.out.println(Fout+"解压成功");
                    }
                    Bin.close();
                    Zin.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            long endTime=System.currentTimeMillis();
            System.out.println("耗费时间： "+(endTime-startTime)+" ms");


}

    public static void getfileDecompress2(String presszipfilepath,String outdepresszipfilepath){
        ZipInputStream zin; // 创建ZipInputStream对象
        try { // try语句捕获可能发生的异常
            zin = new ZipInputStream(new FileInputStream(presszipfilepath));
// 实例化对象，指明要进行解压的文件
            ZipEntry entry = zin.getNextEntry(); // 获取下一个ZipEntry
            while (((entry = zin.getNextEntry()) != null)
                    && !entry.isDirectory()) {
// 如果entry不为空，并不在同一目录下
                File file = new File(outdepresszipfilepath); // 获取文件目录
                        System.out.println(file);
                if (!file.exists()) { // 如果该文件不存在
                    file.mkdirs();// 创建文件所在文件夹
                    file.createNewFile(); // 创建文件
                }
                zin.closeEntry(); // 关闭当前entry
                System.out.println(entry.getName() + "解压成功");
            }
            zin.close(); // 关闭流
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

/**
 2     * 解压缩功能.
 3     * 将zipFile文件解压到folderPath目录下.
 4     * @throws Exception
 5 */
   public static int upZipFile(File zipFile, String folderPath)throws ZipException,IOException {
       //public static void upZipFile() throws Exception{
           ZipFile zfile=new ZipFile(zipFile);
           Enumeration zList=zfile.entries();
            ZipEntry ze=null;
            byte[] buf=new byte[1024];
            while(zList.hasMoreElements()){
     ze=(ZipEntry)zList.nextElement();
            if(ze.isDirectory()){
                    Log.d("upZipFile", "ze.getName() = "+ze.getName());
                    String dirstr = folderPath + ze.getName();
                    //dirstr.trim();
                    dirstr = new String(dirstr.getBytes("8859_1"), "GB2312");
                    Log.d("upZipFile", "str = "+dirstr);
                    File f=new File(dirstr);
                    f.mkdir();
                    continue;
                }
            Log.d("upZipFile", "ze.getName() = "+ze.getName());
            OutputStream os=new BufferedOutputStream(new FileOutputStream(getRealFileName(folderPath, ze.getName())));
            InputStream is=new BufferedInputStream(zfile.getInputStream(ze));
            int readLen=0;
            while ((readLen=is.read(buf, 0, 1024))!=-1) {
                    os.write(buf, 0, readLen);
                }
            is.close();
            os.close();
        }
    zfile.close();
    Log.d("upZipFile", "finishssssssssssssssssssss");
             return 0;
         }

    /**
        * 给定根目录，返回一个相对路径所对应的实际文件名.
        * @param baseDir 指定根目录
        * @param absFileName 相对路径名，来自于ZipEntry中的name
        * @return java.io.File 实际的文件
    */

    public static File getRealFileName(String baseDir, String absFileName){
       String[] dirs=absFileName.split("/");
       File ret=new File(baseDir);
       String substr = null;
       if(dirs.length>1){
               for (int i = 0; i < dirs.length-1;i++) {
                       substr = dirs[i];
    try {
            //substr.trim();
            substr = new String(substr.getBytes("8859_1"), "GB2312");

        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                ret=new File(ret, substr);

            }
        Log.d("upZipFile", "1ret = "+ret);
        if(!ret.exists())
                ret.mkdirs();
        substr = dirs[dirs.length-1];
        try {
                //substr.trim();
                substr = new String(substr.getBytes("8859_1"), "GB2312");
                Log.d("upZipFile", "substr = "+substr);
            } catch (UnsupportedEncodingException e) {
                        // TODO Auto-generated catch block
                         e.printStackTrace();
                     }

                 ret=new File(ret, substr);
                 Log.d("upZipFile", "2ret = "+ret);
                 return ret;
             }
           return ret;
         }

}
