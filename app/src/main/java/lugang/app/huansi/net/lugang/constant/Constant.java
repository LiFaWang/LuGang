package lugang.app.huansi.net.lugang.constant;

import android.Manifest;

/**
 * Created by Tony on 2017/9/14.
 * 11:57
 */

public final class Constant {
    public static final String[] PERMISSIONS = new String[]{
//            Manifest.permission.ACCESS_COARSE_LOCATION,
//            Manifest.permission.ACCESS_FINE_LOCATION,
//            Manifest.permission.CAMERA,
//            Manifest.permission.RECORD_AUDIO,
//            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
//            Manifest.permission.READ_CONTACTS
    };

    public static final String LG_DB_NAME="lugang_measure_db";//SQLite的数据库名

    public final static String SPERSON="SPERSON";//客户姓名
    public final static String SEX="sex";//客户姓名
    public final static String SDEPARTMENTNAME="SDEPARTMENTNAME";//客户部门
    public final static String ISDORDERMETERMSTID="ISDORDERMETERMSTID";//订单ID
    public final static String SVALUENAME="SVALUENAME";//款式的ID
    public final static String STATUS="STATUS";//订单的状态
    public final static String SUSERID="SUSERID";//员工的id
    public final static String IORDERTYPE="IORDERTYPE";//区分从哪个界面跳转
    public static final String AREANAME ="AREANAME" ;//区域名字
    public static final String CITYNAME ="CITYNAME" ;//城市名字
    public static final String COUNTYNAME = "COUNTYNAME";//县市名字
    public static final String CUSTOMERNAME = "CUSTOMERNAME";//单位名字
    public static final String DEPARTMENTNAME = "DEPARTMENTNAME";//部门名字


    /**
     * MeasureCustomActivity的常量
     */
    public static class  MeasureCustomActivityConstant{
        public static final int REMARK_INTENT_KEY=1;//跳转到备注界面选择备注
        public static final String REMARK_INTENT_DATA="remark_intent_data";//传入备注界面的数据
        public static final String STYLE_ID_INTENT="style_id_intent";//传入备注的款式ID
        public static final String ORDER_DTL_ID_INTENT="order_dtl_id_intent";//订单明细ID传入备注界面
        public static final String REMARK_RETURN_DATA="remark_return_data";//备注界面返回的数据
        public static final String STYLE_ID_KEY="ISDSTYLETYPEMSTID";
    }
    /**
     * 开启newMeasureCustomActivityConstant的常量
     */
    public static class NewMeasureCustomActivityConstant{
        public static final String TVCUSTOMERSEARCH="tvCustomerSearch";
        public static final String TVCITYSEARCH="tvCitySearch";
        public static final String TVCOUNTYSEARCH="tvCountySearch";
        public static final String TVDEPARTMENTSEARCH="tvDepartmentSearch";
    }
}
