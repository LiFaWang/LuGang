package lugang.app.huansi.net.lugang.bean;

import huansi.net.qianjingapp.entity.WsData;

/**
 * Created by Tony on 2017/9/23.
 * 13:46
 {
 "STATUS": "0",
 "DATA": [
 {
 "IORDERMETERMSTID": "1001",
 "SBILLNO": "A0001",
 "SCUSTOMERCODE": "0001",
 "SCUSTOMERNAME": "A银行",
 "SPICTURE": "",
 "DCREATEDATE": "2017-09-09"
 },
 {
 "IORDERMETERMSTID": "1002",
 "SBILLNO": "A0002",
 "SCUSTOMERCODE": "0002",
 "SCUSTOMERNAME": "B银行",
 "SPICTURE": "",
 "DCREATEDATE": "2017-09-09"
 },
 {
 "IORDERMETERMSTID": "1004",
 "SBILLNO": "A1004",
 "SCUSTOMERCODE": "0004",
 "SCUSTOMERNAME": "D银行",
 "SPICTURE": "",
 "DCREATEDATE": "2017-09-09"
 }
 ]
 }
 */

public class ConfirmPictureBean extends WsData {
    public String IORDERMETERMSTID="";
    public String SBILLNO="";
    public String SCUSTOMERCODE="";
    public String SCUSTOMERNAME="";
    public String SPICTURE="";
    public String DCREATEDATE="";

}
