package lugang.app.huansi.net.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by 年 on 2017/11/2.
 */

@Entity
public class RemarkDetailDataInSQLite{
    @Id(autoincrement = true)
    private Long id;

//    private int type;//0待量体 1已量体 2返修
//    private String person;//被量体人
//    private String userGUID;//登陆人的GUID

//    private String orderId;//订单头表ID

    private String remarkCategoryId;//备注大类的ID

//    private String iOrderDtlId;//订单明细id

    private String iId;
    private String sMeterMarkCode;
    private String sMeterMarkName;

    @Transient
    public boolean isChoose=false;
    @Transient
    public boolean isAdd=false;//是否添加到已选内容
    @Generated(hash = 149074243)
    public RemarkDetailDataInSQLite(Long id, String remarkCategoryId, String iId,
            String sMeterMarkCode, String sMeterMarkName) {
        this.id = id;
        this.remarkCategoryId = remarkCategoryId;
        this.iId = iId;
        this.sMeterMarkCode = sMeterMarkCode;
        this.sMeterMarkName = sMeterMarkName;
    }
    @Generated(hash = 1754894294)
    public RemarkDetailDataInSQLite() {
    }
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
//    public String getIOrderDtlId() {
//        return this.iOrderDtlId;
//    }
//    public void setIOrderDtlId(String iOrderDtlId) {
//        this.iOrderDtlId = iOrderDtlId;
//    }
    public String getRemarkCategoryId() {
        return this.remarkCategoryId;
    }
    public void setRemarkCategoryId(String remarkCategoryId) {
        this.remarkCategoryId = remarkCategoryId;
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
 


   
}
