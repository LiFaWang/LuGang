package lugang.app.huansi.net.lugang.bean;

import huansi.net.qianjingapp.entity.WsData;

/**
 * Created by Tony on 2017/9/15.
 * 10:37
 * {
 "STATUS": "0",
 "DATA": [
 {
 "ISDSTYLETYPEMSTID": "1006",
 "SBILLNO": "001",
 "SVALUECODE": "",
 "SVALUEGROUP": "西服",
 "SDSTYLETYPEITEMDTLID": "1001",
 "SMETERCODE": "00101",
 "SMETERNAME": "衣长",
 "ISEQ": "1"
 },
 {
 "ISDSTYLETYPEMSTID": "1006",
 "SBILLNO": "001",
 "SVALUECODE": "",
 "SVALUEGROUP": "西服",
 "SDSTYLETYPEITEMDTLID": "1002",
 "SMETERCODE": "00102",
 "SMETERNAME": "肩宽",
 "ISEQ": "2"
 },
 {
 "ISDSTYLETYPEMSTID": "1006",//分组
 "SBILLNO": "001",
 "SVALUECODE": "",
 "SVALUEGROUP": "西服",
 "SDSTYLETYPEITEMDTLID": "1003",
 "SMETERCODE": "00103",
 "SMETERNAME": "袖长",
 "ISEQ": "3"
 },
 {
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
    public String ISDSTYLETYPEMSTID="";
    public String SBILLNO="";
    public String SVALUECODE="";
    public String SVALUEGROUP="";
    public String SDSTYLETYPEITEMDTLID="";
    public String SMETERCODE="";
    public String SMETERNAME="";
    public String ISEQ="";
}
