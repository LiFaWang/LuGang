//package lugang.app.huansi.net.db;
//
//import org.greenrobot.greendao.annotation.Entity;
//import org.greenrobot.greendao.annotation.Id;
//import org.greenrobot.greendao.annotation.Generated;
//
///**
// * Created by 年 on 2017/11/1.
// * 筛选部门的时候
// */
//@Entity
//public class MeasureOrderCustomerInSQLite {
//    @Id(autoincrement =true)
//    private Long id;
//    private int type;//0待量体 1已量体 2返修
//    private String userGUID;
//
//    private String  sCustomerName;//单位名称
//
//    public String getSCustomerName() {
//        return this.sCustomerName;
//    }
//
//    public void setSCustomerName(String sCustomerName) {
//        this.sCustomerName = sCustomerName;
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
//    @Generated(hash = 1115197908)
//    public MeasureOrderCustomerInSQLite(Long id, int type, String userGUID,
//            String sCustomerName) {
//        this.id = id;
//        this.type = type;
//        this.userGUID = userGUID;
//        this.sCustomerName = sCustomerName;
//    }
//
//    @Generated(hash = 1506726811)
//    public MeasureOrderCustomerInSQLite() {
//    }
//
//
//
//
//
//
//
//
//
//
//}
