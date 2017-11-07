//package lugang.app.huansi.net.db;
//
//import org.greenrobot.greendao.annotation.Entity;
//import org.greenrobot.greendao.annotation.Generated;
//import org.greenrobot.greendao.annotation.Id;
//
///**
// * Created by 年 on 2017/11/1.
// * 筛选部门的时候
// */
//@Entity
//public class MeasureOrderDepartmentInSQLite {
//    @Id(autoincrement =true)
//    private Long id;
//    private int type;//0待量体 1已量体 2返修
//    private String userGUID;
//
//    private String  sDepartmentName;//部门名称
//
//    public String getSDepartmentName() {
//        return this.sDepartmentName;
//    }
//
//    public void setSDepartmentName(String sDepartmentName) {
//        this.sDepartmentName = sDepartmentName;
//    }
//
//    public String getUserGUID() {
//        return this.userGUID;
//    }
//
//    public void setUserGUID(String userGUID) {
//        this.userGUID = userGUID;
//    }
//
//    public int getType() {
//        return this.type;
//    }
//
//    public void setType(int type) {
//        this.type = type;
//    }
//
//    public Long getId() {
//        return this.id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    @Generated(hash = 2121827643)
//    public MeasureOrderDepartmentInSQLite(Long id, int type, String userGUID,
//            String sDepartmentName) {
//        this.id = id;
//        this.type = type;
//        this.userGUID = userGUID;
//        this.sDepartmentName = sDepartmentName;
//    }
//
//    @Generated(hash = 37315396)
//    public MeasureOrderDepartmentInSQLite() {
//    }
//
//}
