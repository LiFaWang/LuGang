package lugang.app.huansi.net.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by 年 on 2017/11/2.
 */

@Entity
public class MeasureDataInSQLite {
    @Id(autoincrement = true)
    private Long id;

    private int type;//0待量体 1已量体 2返修
    private String person;//被量体人

    private String orderId;//订单头表ID
    private String userGUID;//登陆人的GUID

    private String iSdOrderMeterDtlId;//订单明细id  离线的情况下，新增的明细是没有订单明细ID
    private String iSdStyleTypeMstId ;//款式ID
    private String sBillNo;
    private String sValueCode;
    private String sValueGroup;//衣服名字
    private String sdStyleTypeItemDtlId;//款式明细ID
    private String sMeterCode;
    private String sMeterName;
    private String iSeq;
    private String iStyleseq;
    private String iSMeterSize = "";//测量的数据

    /************************输入框的校验**开始**********************************/
    private String sFemaleMinLenth=""; //女最小值 空就不判断
    private String sFemaleMaxLenth=""; //女最大值 空就不判断
    private String sMaleMinLenth="";//男最小值 空就不判断
    private String sMaleMaxLenth="";//男最大值 空就不判断
    public String bEvenNo="";//0是偶数 1是奇数  空就不判断
    private String bPoint="";//0是允许小数 1不允许小数 空就不判断

    /*************************输入框的校验**结束*****************************/


    private boolean bupdated ;//是不是修改过
    private String sex;//性别
    private int count;//数量

    private boolean isAdd=false;

    @Generated(hash = 73587977)
    public MeasureDataInSQLite(Long id, int type, String person, String orderId,
            String userGUID, String iSdOrderMeterDtlId, String iSdStyleTypeMstId,
            String sBillNo, String sValueCode, String sValueGroup,
            String sdStyleTypeItemDtlId, String sMeterCode, String sMeterName,
            String iSeq, String iStyleseq, String iSMeterSize,
            String sFemaleMinLenth, String sFemaleMaxLenth, String sMaleMinLenth,
            String sMaleMaxLenth, String bEvenNo, String bPoint, boolean bupdated,
            String sex, int count, boolean isAdd) {
        this.id = id;
        this.type = type;
        this.person = person;
        this.orderId = orderId;
        this.userGUID = userGUID;
        this.iSdOrderMeterDtlId = iSdOrderMeterDtlId;
        this.iSdStyleTypeMstId = iSdStyleTypeMstId;
        this.sBillNo = sBillNo;
        this.sValueCode = sValueCode;
        this.sValueGroup = sValueGroup;
        this.sdStyleTypeItemDtlId = sdStyleTypeItemDtlId;
        this.sMeterCode = sMeterCode;
        this.sMeterName = sMeterName;
        this.iSeq = iSeq;
        this.iStyleseq = iStyleseq;
        this.iSMeterSize = iSMeterSize;
        this.sFemaleMinLenth = sFemaleMinLenth;
        this.sFemaleMaxLenth = sFemaleMaxLenth;
        this.sMaleMinLenth = sMaleMinLenth;
        this.sMaleMaxLenth = sMaleMaxLenth;
        this.bEvenNo = bEvenNo;
        this.bPoint = bPoint;
        this.bupdated = bupdated;
        this.sex = sex;
        this.count = count;
        this.isAdd = isAdd;
    }

    @Generated(hash = 1689375826)
    public MeasureDataInSQLite() {
    }

    public boolean getIsAdd() {
        return this.isAdd;
    }

    public void setIsAdd(boolean isAdd) {
        this.isAdd = isAdd;
    }

    public int getCount() {
        return this.count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getSex() {
        return this.sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getISMeterSize() {
        return this.iSMeterSize;
    }

    public void setISMeterSize(String iSMeterSize) {
        this.iSMeterSize = iSMeterSize;
    }

    public String getISeq() {
        return this.iSeq;
    }

    public void setISeq(String iSeq) {
        this.iSeq = iSeq;
    }

    public String getSMeterName() {
        return this.sMeterName;
    }

    public void setSMeterName(String sMeterName) {
        this.sMeterName = sMeterName;
    }

    public String getSMeterCode() {
        return this.sMeterCode;
    }

    public void setSMeterCode(String sMeterCode) {
        this.sMeterCode = sMeterCode;
    }

    public String getSdStyleTypeItemDtlId() {
        return this.sdStyleTypeItemDtlId;
    }

    public void setSdStyleTypeItemDtlId(String sdStyleTypeItemDtlId) {
        this.sdStyleTypeItemDtlId = sdStyleTypeItemDtlId;
    }

    public String getSValueGroup() {
        return this.sValueGroup;
    }

    public void setSValueGroup(String sValueGroup) {
        this.sValueGroup = sValueGroup;
    }

    public String getSValueCode() {
        return this.sValueCode;
    }

    public void setSValueCode(String sValueCode) {
        this.sValueCode = sValueCode;
    }

    public String getSBillNo() {
        return this.sBillNo;
    }

    public void setSBillNo(String sBillNo) {
        this.sBillNo = sBillNo;
    }

    public String getISdStyleTypeMstId() {
        return this.iSdStyleTypeMstId;
    }

    public void setISdStyleTypeMstId(String iSdStyleTypeMstId) {
        this.iSdStyleTypeMstId = iSdStyleTypeMstId;
    }

    public String getISdOrderMeterDtlId() {
        return this.iSdOrderMeterDtlId;
    }

    public void setISdOrderMeterDtlId(String iSdOrderMeterDtlId) {
        this.iSdOrderMeterDtlId = iSdOrderMeterDtlId;
    }

    public String getUserGUID() {
        return this.userGUID;
    }

    public void setUserGUID(String userGUID) {
        this.userGUID = userGUID;
    }

    public String getOrderId() {
        return this.orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPerson() {
        return this.person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBPoint() {
        return this.bPoint;
    }

    public void setBPoint(String bPoint) {
        this.bPoint = bPoint;
    }

    public String getBEvenNo() {
        return this.bEvenNo;
    }

    public void setBEvenNo(String bEvenNo) {
        this.bEvenNo = bEvenNo;
    }

    public String getSMaleMaxLenth() {
        return this.sMaleMaxLenth;
    }

    public void setSMaleMaxLenth(String sMaleMaxLenth) {
        this.sMaleMaxLenth = sMaleMaxLenth;
    }

    public String getSMaleMinLenth() {
        return this.sMaleMinLenth;
    }

    public void setSMaleMinLenth(String sMaleMinLenth) {
        this.sMaleMinLenth = sMaleMinLenth;
    }

    public String getSFemaleMaxLenth() {
        return this.sFemaleMaxLenth;
    }

    public void setSFemaleMaxLenth(String sFemaleMaxLenth) {
        this.sFemaleMaxLenth = sFemaleMaxLenth;
    }

    public String getSFemaleMinLenth() {
        return this.sFemaleMinLenth;
    }

    public void setSFemaleMinLenth(String sFemaleMinLenth) {
        this.sFemaleMinLenth = sFemaleMinLenth;
    }

    public String getIStyleseq() {
        return this.iStyleseq;
    }

    public void setIStyleseq(String iStyleseq) {
        this.iStyleseq = iStyleseq;
    }

    public boolean getBupdated() {
        return this.bupdated;
    }

    public void setBupdated(boolean bupdated) {
        this.bupdated = bupdated;
    }

   
}