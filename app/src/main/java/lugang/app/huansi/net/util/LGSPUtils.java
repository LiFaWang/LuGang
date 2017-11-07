package lugang.app.huansi.net.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Administrator on 2017/4/19 0019.
 */

public class LGSPUtils {
//    public static void saveMacId(Context context, String macId){
//        SharedPreferences sp = context.getSharedPreferences("MacId",context.MODE_PRIVATE);
//        SharedPreferences.Editor edit = sp.edit();
//        edit.putString("ID",macId);
//        edit.commit();
//    }
//    public static String readMacId(Context context){
//        SharedPreferences sp = context.getSharedPreferences("MacId",context.MODE_PRIVATE);
//        String id = sp.getString("ID", "101");
//        return id;
//    }
//    public static void saveMacIp(Context context, String macIp){
//        SharedPreferences sp = context.getSharedPreferences("MacIp",context.MODE_PRIVATE);
//        SharedPreferences.Editor edit = sp.edit();
//        edit.putString("IP",macIp);
//        edit.commit();
//    }
//    public static String readMacIp(Context context){
//        SharedPreferences sp = context.getSharedPreferences("MacIp",context.MODE_PRIVATE);
//        String id = sp.getString("IP", "192.168.10.9:8011");
//        return id;
//    }
//    /**
//     * 保存数据
//     * @param context
//     * @param key 键
//     * @param value 值
//     */
//    public static void saveSpData(Context context, String key, String value){
//        SharedPreferences sp = context.getSharedPreferences("spDatas",context.MODE_PRIVATE);
//        SharedPreferences.Editor edit = sp.edit();
//        edit.putString(key,value);
//        edit.commit();
//    }
//
//    /**
//     * 通过key获取值
//     * @param context
//     * @param key   键
//     * @param defaultValue 默认值
//     * @return
//     */
//    public static String getSpData(Context context, String key, String defaultValue){
//        SharedPreferences sp = context.getSharedPreferences("spDatas",context.MODE_PRIVATE);
//        String value = sp.getString(key, defaultValue);
//        return value;
//    }

    private static final String SP_NAME="lg_sp";

    public static final String USER_NAME="name";//用户名
    public static final String USER_GUID="ugu_id";//用户GUID
    public static final String USER_ID="user_id";//用户ID
//    public static final String IP="user_ip";//IP
    public static final String USER_PWD="user_psw";//密码





    public static void saveLocalData(Context context, String key, Object value,String  className) {
        SharedPreferences preferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        if (className.equalsIgnoreCase(Integer.class.getName())) {
            editor.putInt(key, (Integer) value);
        } else if (className.equalsIgnoreCase(String.class.getName())) {
            editor.putString(key, value.toString());
        } else if (className.equalsIgnoreCase(Boolean.class.getName())) {
            editor.putBoolean(key, (Boolean) value);
        } else if (className.equalsIgnoreCase(Float.class.getName())) {
            editor.putFloat(key, (Float) value);
        } else if (className.equalsIgnoreCase(Long.class.getName())) {
            editor.putLong(key, (Long) value);
        }
        editor.apply();
    }

    public static Object getLocalData(Context context, String key,String  className,Object defaultValue) {
        SharedPreferences preferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        if(className.equalsIgnoreCase(Integer.class.getName())) return preferences.getInt(key, (Integer) defaultValue);
        if(className.equalsIgnoreCase(String.class.getName())) return preferences.getString(key, defaultValue.toString());
        if(className.equalsIgnoreCase(Boolean.class.getName())) return preferences.getBoolean(key, (Boolean) defaultValue);
        if(className.equalsIgnoreCase(Float.class.getName())) return preferences.getFloat(key, (Float) defaultValue);
        if(className.equalsIgnoreCase(Long.class.getName())) return preferences.getLong(key, (Long) defaultValue);
        return "";
    }

}
