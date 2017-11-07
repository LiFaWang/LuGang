package lugang.app.huansi.net.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by 年 on 2017/11/5.
 */

@Entity
public class MeasureOrderDtlStyleBaseDataInSQLite {
    @Id(autoincrement = true)
    private Long id;
    private String iSdStyleTypeMstId;//款式ID
    private String sBillNo;//单号
    private String sValueCode;//款式Code
    private String sValueGroup;//款式名称
    private String iSdStyleTypeItemDtlId;//款式明细ID
    private String sMeterCode;//款式明细Code
    private String sMeterName;//款式明细Name
    private String iSeq;//序号
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
    public String getISdStyleTypeItemDtlId() {
        return this.iSdStyleTypeItemDtlId;
    }
    public void setISdStyleTypeItemDtlId(String iSdStyleTypeItemDtlId) {
        this.iSdStyleTypeItemDtlId = iSdStyleTypeItemDtlId;
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
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    @Generated(hash = 2029568512)
    public MeasureOrderDtlStyleBaseDataInSQLite(Long id, String iSdStyleTypeMstId,
            String sBillNo, String sValueCode, String sValueGroup,
            String iSdStyleTypeItemDtlId, String sMeterCode, String sMeterName,
            String iSeq) {
        this.id = id;
        this.iSdStyleTypeMstId = iSdStyleTypeMstId;
        this.sBillNo = sBillNo;
        this.sValueCode = sValueCode;
        this.sValueGroup = sValueGroup;
        this.iSdStyleTypeItemDtlId = iSdStyleTypeItemDtlId;
        this.sMeterCode = sMeterCode;
        this.sMeterName = sMeterName;
        this.iSeq = iSeq;
    }
    @Generated(hash = 2010966684)
    public MeasureOrderDtlStyleBaseDataInSQLite() {
    }


}
