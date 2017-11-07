package lugang.app.huansi.net.lugang.bean;

import huansi.net.qianjingapp.entity.WsData;

/**
 * Created by Tony on 2017/9/13.
 * 17:39
 * {
 "STATUS": "0",
 "DATA": [
 {
 "IID": "1006",
 "ILISTNO": "6",
 "SAREANAME": "浙江省",
 "SCITYNAME": "杭州市",
 "SCOUNTYNAME": "ABC区",
 "SCUSTOMERNAME": "B银行",
 "SDEPARTMENTNAME": "业务部",
 "SPERSON": "李七"
 },
 {
 "ISDORDERMETERMSTID": "1007",
 "ILISTNO": "7",
 "SAREANAME": "浙江省",
 "SCITYNAME": "杭州市",
 "SCOUNTYNAME": "ABC区",
 "SCUSTOMERNAME": "B银行",
 "SDEPARTMENTNAME": "业务部",
 "SPERSON": "李七"
 "SVALUENAME":
 }
 ]
 }
 */

public class RepairRegisterBean extends WsData {
    public String ISDORDERMETERMSTID="";//订单id
    public String ILISTNO="";
    public String SAREANAME="";
    public String SCITYNAME="";
    public String SCOUNTYNAME="";
    public String SCUSTOMERNAME="";//单位
    public String SCUSTOMERCODE="";
    public String SDEPARTMENTNAME="";//部门
    public String SJOBNAME="";
    public String SPERSON="";//姓名
    public String SVALUENAME="";//款式的ID
    public String STATUS="REPAIR";//跳转的状态
    public String SBILLNO="";//清单的ID
    public String IORDERTYPE="2";//区分是从哪个界面跳转的


}
