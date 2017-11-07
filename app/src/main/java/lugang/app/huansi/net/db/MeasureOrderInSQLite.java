package lugang.app.huansi.net.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by 年 on 2017/11/1.
 *待量体、已量体、返修的实体类 //-离线的数据
 */
@Entity
public class MeasureOrderInSQLite {
    @Id(autoincrement = true)
    private Long id;
    private int orderType;//0待量体 1已量体 2返修
    private String userGUID;//登录用户的GUID

//    private boolean isAdd=false;//新增的用户


    /*********************服务器数据隐射开始*************************/
    private String sJobName;//岗位
    private String iSdOrderMeterMstId;//订单头表ID
    private String sAreaName;//地区
    private String sCityName;//城市
    private String sCountyName;//县
    private String sCustomerName;//客户(单位名称)
    private String sCustomerCode;//客户（单位Code）
    private String sDepartmentName;//部门
    private String sPerson;//姓名
    private String sBillNo;//单号
    public String getSBillNo() {
        return this.sBillNo;
    }
    public void setSBillNo(String sBillNo) {
        this.sBillNo = sBillNo;
    }
    public String getSPerson() {
        return this.sPerson;
    }
    public void setSPerson(String sPerson) {
        this.sPerson = sPerson;
    }
    public String getSDepartmentName() {
        return this.sDepartmentName;
    }
    public void setSDepartmentName(String sDepartmentName) {
        this.sDepartmentName = sDepartmentName;
    }
    public String getSCustomerCode() {
        return this.sCustomerCode;
    }
    public void setSCustomerCode(String sCustomerCode) {
        this.sCustomerCode = sCustomerCode;
    }
    public String getSCustomerName() {
        return this.sCustomerName;
    }
    public void setSCustomerName(String sCustomerName) {
        this.sCustomerName = sCustomerName;
    }
    public String getSCountyName() {
        return this.sCountyName;
    }
    public void setSCountyName(String sCountyName) {
        this.sCountyName = sCountyName;
    }
    public String getSCityName() {
        return this.sCityName;
    }
    public void setSCityName(String sCityName) {
        this.sCityName = sCityName;
    }
    public String getSAreaName() {
        return this.sAreaName;
    }
    public void setSAreaName(String sAreaName) {
        this.sAreaName = sAreaName;
    }
    public String getISdOrderMeterMstId() {
        return this.iSdOrderMeterMstId;
    }
    public void setISdOrderMeterMstId(String iSdOrderMeterMstId) {
        this.iSdOrderMeterMstId = iSdOrderMeterMstId;
    }
    public String getSJobName() {
        return this.sJobName;
    }
    public void setSJobName(String sJobName) {
        this.sJobName = sJobName;
    }
    public String getUserGUID() {
        return this.userGUID;
    }
    public void setUserGUID(String userGUID) {
        this.userGUID = userGUID;
    }
    public int getOrderType() {
        return this.orderType;
    }
    public void setOrderType(int orderType) {
        this.orderType = orderType;
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    @Generated(hash = 756719233)
    public MeasureOrderInSQLite(Long id, int orderType, String userGUID,
            String sJobName, String iSdOrderMeterMstId, String sAreaName,
            String sCityName, String sCountyName, String sCustomerName,
            String sCustomerCode, String sDepartmentName, String sPerson,
            String sBillNo) {
        this.id = id;
        this.orderType = orderType;
        this.userGUID = userGUID;
        this.sJobName = sJobName;
        this.iSdOrderMeterMstId = iSdOrderMeterMstId;
        this.sAreaName = sAreaName;
        this.sCityName = sCityName;
        this.sCountyName = sCountyName;
        this.sCustomerName = sCustomerName;
        this.sCustomerCode = sCustomerCode;
        this.sDepartmentName = sDepartmentName;
        this.sPerson = sPerson;
        this.sBillNo = sBillNo;
    }
    @Generated(hash = 1798843719)
    public MeasureOrderInSQLite() {
    }
}
