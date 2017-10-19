package lugang.app.huansi.net.lugang.bean;

import huansi.net.qianjingapp.entity.WsData;

/**
 * Created by Tony on 2017/9/19.
 * 16:07
 * {
 "STATUS": "0",
 "DATA": [
 {
 "ISDORDERMETERDTLID":1001
 "ISDSTYLETYPEMSTID": "1006",
 "ISMETERSIZE": "55",
 "ISDSTYLETYPEITEMDTLID": "1004",
 "ISDORDERMETERDTLDETAILID": "1004"
 }
 ]
 }
 */

public class MeasureDateBean extends WsData {
    public String ISDORDERMETERDTLID="";//订单明细id
    public String ISDSTYLETYPEMSTID="";//款式ID
    public String ISMETERSIZE="";//尺寸
    public String ISDSTYLETYPEITEMDTLID="";//款式明细ID
    public String ISDORDERMETERDTLDETAILID=""; //订单款式明细中的ID
}
