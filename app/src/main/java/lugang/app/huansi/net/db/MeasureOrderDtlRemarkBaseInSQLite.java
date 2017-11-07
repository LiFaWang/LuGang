package lugang.app.huansi.net.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import huansi.net.qianjingapp.entity.WsData;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by 年 on 2017/11/5.
 * 订单明细对应的备注基本信息
 */

@Entity
public class MeasureOrderDtlRemarkBaseInSQLite{
    /*  B.iSdStyleTypeMstId,
    B.iid,
    B.sMeterMarkCode,
    B.sMeterMarkName*/
    @Id(autoincrement = true)
    private Long id;
    private String iSdStyleTypeMstId="";//款式ID
    private String iId="";//备注大类ID
    private String sMeterMarkCode="";//备注大类Code
    private String sMeterMarkName="";//备注大类Name
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
    @Generated(hash = 1186686979)
    public MeasureOrderDtlRemarkBaseInSQLite(Long id, String iSdStyleTypeMstId,
            String iId, String sMeterMarkCode, String sMeterMarkName) {
        this.id = id;
        this.iSdStyleTypeMstId = iSdStyleTypeMstId;
        this.iId = iId;
        this.sMeterMarkCode = sMeterMarkCode;
        this.sMeterMarkName = sMeterMarkName;
    }
    @Generated(hash = 2018860603)
    public MeasureOrderDtlRemarkBaseInSQLite() {
    }


}
