package lugang.app.huansi.net.lugang.bean;

import huansi.net.qianjingapp.entity.WsData;

/**
 * Created by Tony on 2017/9/15.
 * 10:37
 * {
 "STATUS": "0",
 "DATA": [
 "ISDORDERMETERDTLID": "1001",订单明细id
 "ISDSTYLETYPEMSTID": "1006",
 "SBILLNO": "001",
 "SVALUECODE": "",
 "SVALUEGROUP": "西服",
 "SDSTYLETYPEITEMDTLID": "1004",
 "SMETERCODE": "00104",
 "SMETERNAME": "臀围",
 "ISEQ": "4"
 }
 ]
 }
 */

public class MeasureCustomBean extends WsData {
    public String ISDORDERMETERDTLID="";//订单明细id
    public String ISDSTYLETYPEMSTID="";//款式ID
    public String SBILLNO="";
    public String SVALUECODE="";
    public String SVALUEGROUP="";//衣服名字
    public String SDSTYLETYPEITEMDTLID="";//款式明细ID
    public String SMETERCODE="";
    public String SMETERNAME="";
    public String ISEQ="0";

    public String ISMETERSIZE="0";//测量的数据


}
