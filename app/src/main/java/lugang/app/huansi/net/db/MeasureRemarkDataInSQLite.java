package lugang.app.huansi.net.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;

import java.io.Serializable;

/**
 * Created by 年 on 2017/11/2.
 */

@Entity
@SuppressWarnings("serial")
public class MeasureRemarkDataInSQLite implements Serializable{
    @Id(autoincrement = true)
    private Long id;

    private int type;//0待量体 1已量体 2返修
    private String person;//被量体人
    private String userGUID;//登陆人的GUID

    private String orderId;//订单头表ID

    private String iOrderDtlId;//订单明细id
    private String styleId;//款式ID

    private String iId;//备注ID
    private String sMeterMarkCode;//备注Code
    private String sMeterMarkName;//备注Name

    @Transient
    public boolean isChoose=false;
//    @Transient
//    public boolean isAdd=false;//是否添加到已选内容

    public String getSMeterMarkName() {
        return this.sMeterMarkName;
    }

    public void setSMeterMarkName(String sMeterMarkName) {
        this.sMeterMarkName = sMeterMarkName;
    }

    public String getSMeterMarkCode() {
        return this.sMeterMarkCode;
    }

    public void setSMeterMarkCode(String sMeterMarkCode) {
        this.sMeterMarkCode = sMeterMarkCode;
    }

    public String getIId() {
        return this.iId;
    }

    public void setIId(String iId) {
        this.iId = iId;
    }

    public String getStyleId() {
        return this.styleId;
    }

    public void setStyleId(String styleId) {
        this.styleId = styleId;
    }

    public String getIOrderDtlId() {
        return this.iOrderDtlId;
    }

    public void setIOrderDtlId(String iOrderDtlId) {
        this.iOrderDtlId = iOrderDtlId;
    }

    public String getOrderId() {
        return this.orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUserGUID() {
        return this.userGUID;
    }

    public void setUserGUID(String userGUID) {
        this.userGUID = userGUID;
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

    @Generated(hash = 1361747712)
    public MeasureRemarkDataInSQLite(Long id, int type, String person,
            String userGUID, String orderId, String iOrderDtlId, String styleId,
            String iId, String sMeterMarkCode, String sMeterMarkName) {
        this.id = id;
        this.type = type;
        this.person = person;
        this.userGUID = userGUID;
        this.orderId = orderId;
        this.iOrderDtlId = iOrderDtlId;
        this.styleId = styleId;
        this.iId = iId;
        this.sMeterMarkCode = sMeterMarkCode;
        this.sMeterMarkName = sMeterMarkName;
    }

    @Generated(hash = 1783478609)
    public MeasureRemarkDataInSQLite() {
    }

    
}
