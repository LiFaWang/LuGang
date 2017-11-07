//package lugang.app.huansi.net.db;
//
//import org.greenrobot.greendao.annotation.Entity;
//import org.greenrobot.greendao.annotation.Generated;
//import org.greenrobot.greendao.annotation.Id;
//
///**
// * Created by 年 on 2017/11/1.
// */
//
//@Entity
//public class MeasureStyleDataInSQLite {
//    @Id(autoincrement = true)
//    private Long id;
//    private String userGUID;
//    private int type;//0待量体 1已量体 2返修
//
//
//    private String iSdOrderMeterMstId;//服务器中订单头表
//    private String sPerson;//被量体者
//
//
//    /*****************服务器数据***开始**************************/
//    private String isdordermeterDtlId;//订单明细ID
//    private String isdStyleTypeMstid;//款式ID
//    private String iSmeterSize;//尺寸
//    private String isdStyleTypeItemDtlid;//款式明细ID
//    private String isdOrderMeterDtlDetailId;//款式明细单的ID
//    public String getIsdOrderMeterDtlDetailId() {
//        return this.isdOrderMeterDtlDetailId;
//    }
//    public void setIsdOrderMeterDtlDetailId(String isdOrderMeterDtlDetailId) {
//        this.isdOrderMeterDtlDetailId = isdOrderMeterDtlDetailId;
//    }
//    public String getIsdStyleTypeItemDtlid() {
//        return this.isdStyleTypeItemDtlid;
//    }
//    public void setIsdStyleTypeItemDtlid(String isdStyleTypeItemDtlid) {
//        this.isdStyleTypeItemDtlid = isdStyleTypeItemDtlid;
//    }
//    public String getISmeterSize() {
//        return this.iSmeterSize;
//    }
//    public void setISmeterSize(String iSmeterSize) {
//        this.iSmeterSize = iSmeterSize;
//    }
//    public String getIsdStyleTypeMstid() {
//        return this.isdStyleTypeMstid;
//    }
//    public void setIsdStyleTypeMstid(String isdStyleTypeMstid) {
//        this.isdStyleTypeMstid = isdStyleTypeMstid;
//    }
//    public String getIsdordermeterDtlId() {
//        return this.isdordermeterDtlId;
//    }
//    public void setIsdordermeterDtlId(String isdordermeterDtlId) {
//        this.isdordermeterDtlId = isdordermeterDtlId;
//    }
//    public String getSPerson() {
//        return this.sPerson;
//    }
//    public void setSPerson(String sPerson) {
//        this.sPerson = sPerson;
//    }
//    public String getISdOrderMeterMstId() {
//        return this.iSdOrderMeterMstId;
//    }
//    public void setISdOrderMeterMstId(String iSdOrderMeterMstId) {
//        this.iSdOrderMeterMstId = iSdOrderMeterMstId;
//    }
//    public int getType() {
//        return this.type;
//    }
//    public void setType(int type) {
//        this.type = type;
//    }
//    public String getUserGUID() {
//        return this.userGUID;
//    }
//    public void setUserGUID(String userGUID) {
//        this.userGUID = userGUID;
//    }
//    public Long getId() {
//        return this.id;
//    }
//    public void setId(Long id) {
//        this.id = id;
//    }
//    @Generated(hash = 922234517)
//    public MeasureStyleDataInSQLite(Long id, String userGUID, int type,
//            String iSdOrderMeterMstId, String sPerson, String isdordermeterDtlId,
//            String isdStyleTypeMstid, String iSmeterSize,
//            String isdStyleTypeItemDtlid, String isdOrderMeterDtlDetailId) {
//        this.id = id;
//        this.userGUID = userGUID;
//        this.type = type;
//        this.iSdOrderMeterMstId = iSdOrderMeterMstId;
//        this.sPerson = sPerson;
//        this.isdordermeterDtlId = isdordermeterDtlId;
//        this.isdStyleTypeMstid = isdStyleTypeMstid;
//        this.iSmeterSize = iSmeterSize;
//        this.isdStyleTypeItemDtlid = isdStyleTypeItemDtlid;
//        this.isdOrderMeterDtlDetailId = isdOrderMeterDtlDetailId;
//    }
//    @Generated(hash = 569138408)
//    public MeasureStyleDataInSQLite() {
//    }
//
//
//
//
//}
