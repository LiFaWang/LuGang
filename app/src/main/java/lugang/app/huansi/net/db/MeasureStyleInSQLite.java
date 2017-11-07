//package lugang.app.huansi.net.db;
//
//import org.greenrobot.greendao.annotation.Entity;
//import org.greenrobot.greendao.annotation.Id;
//import org.greenrobot.greendao.annotation.Generated;
//
///**
// * Created by 年 on 2017/11/1.
// */
//
//@Entity
//public class MeasureStyleInSQLite {
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
//    private String sBillNo;//单号
//    private String sValueCode;//
//    private String sValueGroup;//
//    private String sdStyleTypeItemDtlID;//
//    private String sMeterCode;//
//    private String sMeterName;//
//    private String iSeq;//序号
//
//    @Generated(hash = 1836076955)
//    public MeasureStyleInSQLite(Long id, String userGUID, int type,
//            String iSdOrderMeterMstId, String sPerson, String isdordermeterDtlId,
//            String isdStyleTypeMstid, String sBillNo, String sValueCode,
//            String sValueGroup, String sdStyleTypeItemDtlID, String sMeterCode,
//            String sMeterName, String iSeq) {
//        this.id = id;
//        this.userGUID = userGUID;
//        this.type = type;
//        this.iSdOrderMeterMstId = iSdOrderMeterMstId;
//        this.sPerson = sPerson;
//        this.isdordermeterDtlId = isdordermeterDtlId;
//        this.isdStyleTypeMstid = isdStyleTypeMstid;
//        this.sBillNo = sBillNo;
//        this.sValueCode = sValueCode;
//        this.sValueGroup = sValueGroup;
//        this.sdStyleTypeItemDtlID = sdStyleTypeItemDtlID;
//        this.sMeterCode = sMeterCode;
//        this.sMeterName = sMeterName;
//        this.iSeq = iSeq;
//    }
//
//    @Generated(hash = 1434815742)
//    public MeasureStyleInSQLite() {
//    }
//
//    /*****************服务器数据***结束**************************/
//    public String getISeq() {
//        return this.iSeq;
//    }
//
//    public void setISeq(String iSeq) {
//        this.iSeq = iSeq;
//    }
//
//    public String getSMeterName() {
//        return this.sMeterName;
//    }
//
//    public void setSMeterName(String sMeterName) {
//        this.sMeterName = sMeterName;
//    }
//
//    public String getSMeterCode() {
//        return this.sMeterCode;
//    }
//
//    public void setSMeterCode(String sMeterCode) {
//        this.sMeterCode = sMeterCode;
//    }
//
//    public String getSdStyleTypeItemDtlID() {
//        return this.sdStyleTypeItemDtlID;
//    }
//
//    public void setSdStyleTypeItemDtlID(String sdStyleTypeItemDtlID) {
//        this.sdStyleTypeItemDtlID = sdStyleTypeItemDtlID;
//    }
//
//    public String getSValueGroup() {
//        return this.sValueGroup;
//    }
//
//    public void setSValueGroup(String sValueGroup) {
//        this.sValueGroup = sValueGroup;
//    }
//
//    public String getSValueCode() {
//        return this.sValueCode;
//    }
//
//    public void setSValueCode(String sValueCode) {
//        this.sValueCode = sValueCode;
//    }
//
//    public String getSBillNo() {
//        return this.sBillNo;
//    }
//
//    public void setSBillNo(String sBillNo) {
//        this.sBillNo = sBillNo;
//    }
//
//    public String getIsdStyleTypeMstid() {
//        return this.isdStyleTypeMstid;
//    }
//
//    public void setIsdStyleTypeMstid(String isdStyleTypeMstid) {
//        this.isdStyleTypeMstid = isdStyleTypeMstid;
//    }
//
//    public String getIsdordermeterDtlId() {
//        return this.isdordermeterDtlId;
//    }
//
//    public void setIsdordermeterDtlId(String isdordermeterDtlId) {
//        this.isdordermeterDtlId = isdordermeterDtlId;
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
//    public Long getId() {
//        return this.id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public String getSPerson() {
//        return this.sPerson;
//    }
//
//    public void setSPerson(String sPerson) {
//        this.sPerson = sPerson;
//    }
//
//    public String getISdOrderMeterMstId() {
//        return this.iSdOrderMeterMstId;
//    }
//
//    public void setISdOrderMeterMstId(String iSdOrderMeterMstId) {
//        this.iSdOrderMeterMstId = iSdOrderMeterMstId;
//    }
//
//    public int getType() {
//        return this.type;
//    }
//
//    public void setType(int type) {
//        this.type = type;
//    }
//}
