package lugang.app.huansi.net.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Tony on 2017/11/7.
 */

public class FileUtils {
    /**
     * 文件存储:
     *   一：内部存储：试用于lib库,SharedPreferences
     *                 具体路径：/data/data/app包名/
     *                 手机硬件设备之一，空间有限，尽量不要使用，特别是大的数据量存储
     *       操作方式：Context 提供了一系列的操作方式inside...
     *   二：外部存储：外部存储卡，如SD卡等，适合数据量相对较大的数据缓存，比如图片等...
     *                 具体路径：/mnt/sdcard
     *                 获取路径：Environment.getExternalStorageDirectory()
     *                 外部可扩展的存储硬件设备
     *       操作方式：outSide...等方法
     *       <!-- 在SDCard中创建与删除文件权限 -->
     *       <uses-permission android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS"/>
     *       <!-- 往SDCard写入数据权限 -->
     *       <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
     *   Bitmap的存储缓存可参考：工具类phoneUtils中saveBitmapFile（）方法
     */

    /**
     * 文件存储类型：fileType
     * 0:私有覆盖
     * 1:私有续写
     * 2:非私有读写
     * 3:非私有写入
     */
    private final static int MY_MODE_PRIVATE=0;
    private final static int MY_MODE_APPEND=1;
    private final static int MY_MODE_WORLD_READABLE=2;
    private final static int MY_MODE_WORLD_WRITEABLE=3;

    public static int getFileType(Context context, int fileType){
        switch (fileType){
            case MY_MODE_PRIVATE:
                return context.MODE_PRIVATE;
            case MY_MODE_APPEND:
                return context.MODE_APPEND;
            case MY_MODE_WORLD_READABLE:
                return context.MODE_WORLD_READABLE;
            case MY_MODE_WORLD_WRITEABLE:
                return context.MODE_WORLD_WRITEABLE;
        }
        return context.MODE_PRIVATE;
    }

    /**
     *  内部存储：将String存储于手机内存：
     *  创建一个文件，并打开成一个文件输出流，需要提供一个String，作为文件名,以及一个读取模式：
     *         context.MODE_PRIVATE=0:私有模式，文件只可以被本身app读取，写入会覆盖，如果不存在创建文件。
     *         context.MODE_APPEND=32768：私有模式，写入文件如果存在追加写入，如果不存在创建文件。
     *         context.MODE_WORLD_READABLE=1:非私有模式：当前文件可以被其他程序读取
     *         context.MODE_WORLD_WRITEABLE=2:非私有模式：当前文件只可被其他程序写入
     */
    public static  void insideAddFile(Context context,String fileName,String json,int fileType){
        FileOutputStream outputStream=null;
        try {
            outputStream=context.openFileOutput(fileName, getFileType(context,fileType));
            outputStream.write(json.getBytes());
            outputStream.close();
        } catch (FileNotFoundException e) {

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 内部存储：将存储的String数据进行读取
     */
    public static String insideSelectFile(Context context,String fileName){
        FileInputStream inputStream=null;
        try {
            inputStream=context.openFileInput(fileName);
            if (inputStream==null){
                return null;
            }
            byte[] buf = new byte[1024];
            StringBuffer sb=new StringBuffer();
            while((inputStream.read(buf))!=-1){
                sb.append(new String(buf));
                buf=new byte[1024];//重新生成，避免和上次读取的数据重复
            }
            inputStream.close();
            return sb.toString().trim();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 内部存储：删除一个文件
     */
    public static boolean insideDeleteFile(Context context,String fileName){
        if (context.deleteFile(fileName)) {
            return true;
        }
        return false;
    }

    /**
     * 内部存储：创建一个新的目录
     */
    public static String insideCreadeDir(Context context,String dirName){
        File myDir = context.getDir(dirName, context.MODE_PRIVATE);
        if (myDir.getAbsolutePath()!=null){
            return myDir.getAbsolutePath();
        }
        return null;
    }

    /**
     * 内部存储：查找一个文件存储路径
     */
    public static String insideSelectDir(Context context,String fileName){
        String path=null;
        File fileDir = context.getDir(fileName,0);
        if (fileDir!=null){
            return fileDir.getAbsolutePath().trim();
        }
        return path;
    }


    /**
     * 外部存储:检查外部存储是否可用;
     */
    public static boolean outSideCheck(){
        String state = Environment.getExternalStorageState();
        if(state.equals(android.os.Environment.MEDIA_MOUNTED)){
            return true;
        }
        return false;
    }

    /**
     * 外部存储：在指定路径下保存一个文件
     * fileDir:  路径名(不需要加/，除非多级目录中间分级目录加/)
     * fileName: abc.text (不需要加/） 格式自定义
     * isAppend:是否追加
     * data:要保存String数据
     */
    public static String outSideAddFile(String fileDir,String fileName,boolean isAppend,String json){
        String myDir="/"+fileDir;
        String myName="/"+fileName;
        boolean isUse=outSideCheck();
        if (!isUse){
            //外部存储设备未安装
            return "";
        }
        try {
            File file=new File(Environment.getExternalStorageDirectory()+myDir);
            if(!file.exists()){
                file.mkdirs();
            }
            FileOutputStream outputStream=new FileOutputStream(new File(file.getAbsolutePath()+myName),isAppend);
            outputStream.write(json.getBytes());
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Environment.getExternalStorageDirectory()+myDir+myName;
    }

    /**
     * 外部存储，读取一个文件内容
     * fileDir:文件完整的存储路径
     */
    public static String outSideSelectFile(String fileDir){
        String json=null;
        FileInputStream inputStream=null;
        try {
            inputStream=new FileInputStream(fileDir);
            if (inputStream==null){
                return null;
            }
            byte[] buf = new byte[1024];
            StringBuffer sb=new StringBuffer();
            while((inputStream.read(buf))!=-1){
                sb.append(new String(buf));
                buf=new byte[1024];//重新生成，避免和上次读取的数据重复
            }
            json=sb.toString().trim();
            inputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * SharedPreferences 存储示例  以单个String存储为例
     */
    public static void SharedPreferencesSave(Context cotext,String fileName,String key,String value){
        SharedPreferences sharedPreferences=cotext.getSharedPreferences(fileName,cotext.MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(key,value);
        edit.commit();
    }

    /**
     * SharedPreferences 读取示例 以单个String读取为例
     */
    public static String SharedPreferencesSelect(Context cotext,String fileName,String key){
        SharedPreferences sharedPreferences = cotext.getSharedPreferences(fileName, cotext.MODE_PRIVATE);
        return sharedPreferences.getString(key,"默认数据");
    }

}