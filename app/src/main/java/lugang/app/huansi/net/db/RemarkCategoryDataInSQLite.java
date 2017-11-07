package lugang.app.huansi.net.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Transient;

/**
 * Created by 年 on 2017/11/2.
 */
@Entity
public class RemarkCategoryDataInSQLite {
    @Id(autoincrement = true)
    private Long id;

    private String styleId;//款式ID

    private String iId;//备注大类的ID
    private String sBillNo;//备注类别编号
    private String sMeterMarkName;//备注类别名称

    @Transient
    public boolean isChoose=false;

    @Generated(hash = 248215503)
    public RemarkCategoryDataInSQLite(Long id, String styleId, String iId,
            String sBillNo, String sMeterMarkName) {
        this.id = id;
        this.styleId = styleId;
        this.iId = iId;
        this.sBillNo = sBillNo;
        this.sMeterMarkName = sMeterMarkName;
    }
    @Generated(hash = 629280758)
    public RemarkCategoryDataInSQLite() {
    }

    public String getSMeterMarkName() {
        return this.sMeterMarkName;
    }
    public void setSMeterMarkName(String sMeterMarkName) {
        this.sMeterMarkName = sMeterMarkName;
    }
    public String getSBillNo() {
        return this.sBillNo;
    }
    public void setSBillNo(String sBillNo) {
        this.sBillNo = sBillNo;
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
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }





    




}
