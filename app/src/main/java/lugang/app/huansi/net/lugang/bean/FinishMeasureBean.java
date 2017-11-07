package lugang.app.huansi.net.lugang.bean;

import huansi.net.qianjingapp.entity.WsData;

/**
 * Created by Tony on 2017/9/13.
 * 17:38
 * * {
 "STATUS": "0",
 "DATA": [
 {
 "IID": "1001",
 "ILISTNO": "1",
 "SAREANAME": "江苏省",
 "SCITYNAME": "苏州市",
 "SCOUNTYNAME": "吴中区",
 "SCUSTOMERNAME": "A银行",
 "SDEPARTMENTNAME": "业务部",
 "SPERSON": "张三"
 },
 {
 "IID": "1002",
 "ILISTNO": "2",
 "SAREANAME": "江苏省",
 "SCITYNAME": "盐城市",
 "SCOUNTYNAME": "建湖县",
 "SCUSTOMERNAME": "A银行",
 "SDEPARTMENTNAME": "业务部",
 "SPERSON": "王五"
 },
 {
 "IID": "1003",
 "ILISTNO": "3",
 "SAREANAME": "江苏省",
 "SCITYNAME": "无锡市",
 "SCOUNTYNAME": "XX区",
 "SCUSTOMERNAME": "A银行",
 "SDEPARTMENTNAME": "业务部",
 "SPERSON": "赵六"
 },
 {
 "IID": "1004",
 "ILISTNO": "4",
 "SAREANAME": "浙江省",
 "SCITYNAME": "杭州市",
 "SCOUNTYNAME": "ABC区",
 "SCUSTOMERNAME": "B银行",
 "SDEPARTMENTNAME": "业务部",
 "SPERSON": "赵三"
 },
 {
 "IID": "1005",
 "ILISTNO": "5",
 "SAREANAME": "浙江省",
 "SCITYNAME": "杭州市",
 "SCOUNTYNAME": "ABC区",
 "SCUSTOMERNAME": "B银行",
 "SDEPARTMENTNAME": "业务部",
 "SPERSON": "李七"
 }
 ]
 }
 */

public class FinishMeasureBean extends WsData {
    /*
     "ISDORDERMETERMSTID": "1001",
     "ILISTNO": "1",
     "SAREANAME": "江苏省",
     "SCITYNAME": "苏州市",
     "SCOUNTYNAME": "吴中区",
     "SCUSTOMERNAME": "A银行",
     "SDEPARTMENTNAME": "业务部",
     "SPERSON": "张三"
     "SVALUENAME":
     */
    public String ISDORDERMETERMSTID="";
    public String ILISTNO="";
    public String SAREANAME="";
    public String SCITYNAME="";
    public String SCOUNTYNAME="";
    public String SCUSTOMERNAME="";
    public String SCUSTOMERCODE="";
    public String SDEPARTMENTNAME="";
    public String SJOBNAME="";
    public String SPERSON="";
    public String SVALUENAME="";//款式的ID
    public String STATUS="FINISH";//跳转的状态
    public String SBILLNO="";//清单的ID
    public String IORDERTYPE="1";//区分是从哪个界面跳转的

}
