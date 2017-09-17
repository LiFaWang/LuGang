package lugang.app.huansi.net.greendao.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by Tony on 2017/9/16.
 * 10:28
 */
@Entity
public class RemarkDetail {
    @Id
    private Long id;
    private String smetermarkname;//备注详情的内容
    private String isdstyletypemstid;//备注详情的id
    public String getIsdstyletypemstid() {
        return this.isdstyletypemstid;
    }
    public void setIsdstyletypemstid(String isdstyletypemstid) {
        this.isdstyletypemstid = isdstyletypemstid;
    }
    public String getSmetermarkname() {
        return this.smetermarkname;
    }
    public void setSmetermarkname(String smetermarkname) {
        this.smetermarkname = smetermarkname;
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    @Generated(hash = 968128628)
    public RemarkDetail(Long id, String smetermarkname, String isdstyletypemstid) {
        this.id = id;
        this.smetermarkname = smetermarkname;
        this.isdstyletypemstid = isdstyletypemstid;
    }
    @Generated(hash = 1619841701)
    public RemarkDetail() {
    }
}
