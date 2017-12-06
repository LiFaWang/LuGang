package lugang.app.huansi.net.lugang.bean;

import huansi.net.qianjingapp.entity.WsData;

/**
 * Created by Tony on 2017/9/19.
 * 16:07
 * {
 "STATUS": "0",
 {
 "ISDORDERMETERDTLID": "271",
 "ISDSTYLETYPEMSTID": "1006",
 "ISMETERSIZE": "48",
 "ISDSTYLETYPEITEMDTLID": "1002",
 "ISDORDERMETERDTLDETAILID": "1002",
 "BUPDATED": "False"
 }

 {
 "ISDORDERMETERDTLID": "271",
 "ISDSTYLETYPEMSTID": "1006",
 "ISMETERSIZE": "77.0",
 "IREPAIR": "",
 "BREPAIR": "False",
 "ISDSTYLETYPEITEMDTLID": "1001",
 "ISDORDERMETERDTLDETAILID": "1001",
 "BUPDATED": "False"
 }
 */

public class MeasureDateBean extends WsData {
    public String ISDORDERMETERDTLID="";//订单明细id
    public String ISDSTYLETYPEMSTID="";//款式ID
    public String ISMETERSIZE="";//尺寸
    public String ISDSTYLETYPEITEMDTLID="";//款式明细ID
    public String ISDORDERMETERDTLDETAILID=""; //订单款式明细中的ID
    public String IREPAIR=""; //订单款式明细中返修后的值
    public String BREPAIR=""; //订单款式明细中数据是否返修过
    public String BUPDATED=""; //订单款式明细中数据是否修改过
}
