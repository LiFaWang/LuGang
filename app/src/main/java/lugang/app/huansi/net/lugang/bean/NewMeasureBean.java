package lugang.app.huansi.net.lugang.bean;

import huansi.net.qianjingapp.entity.WsData;

/**
 * Created by Tony on 2017/9/28.
 * 10:38
 * {
 "STATUS": "0",
 "DATA": [
 {
 "SCUSTOMERNAME": "A银行",
 "SCUSTOMERCODE": "0001"
 "ISDORDERMETERMSTID":"1101"
 }
 ]
 }

 {
 "STATUS": "0",
 "DATA": [
 {
 "SVALUEGROUP": "西服",
 "ISDSTYLETYPEMSTID": "1006"

 },
 {
 "SVALUEGROUP": "春秋上衣",
 "ISDSTYLETYPEMSTID": "1007"
 },
 {
 "SVALUEGROUP": "春秋裤子",
 "ISDSTYLETYPEMSTID": "1008"
 }
 ]
 }
 [
 {
 "SDEPARTMENTNAME": "技术部"
 "SAREANAME": "山东"
 "SCITYNAME": "青岛"
 "SCOUNTYNAME": "城阳"
 "SJOBNAME": "业务员"
 {
 "SVALUEGROUP": "西服",
 "ISDSTYLETYPEMSTID": "1006"
 },

 */

public class NewMeasureBean extends WsData {

//    public String SVALUEGROUP="";//春秋上衣
//    public String ISDSTYLETYPEMSTID="";//1008
//    public boolean isSelected;
//    public String ETAREANAME="";
//    public String ETCITYNAME="";
//    public String ETCOUNTYNAME="";
//    public String ETDEPARTMENTNAME="";
//    public String ETJOBNAME="";
//    public String ETPERSON="";
//    public String ETSEX="";
//    public String ETCOUNT="";



    public String SCUSTOMERNAME="";//单位
    public String SCUSTOMERCODE="";//单位Code
    public String ISDORDERMETERMSTID="";//订单主表ID
    public String SDEPARTMENTNAME="";//部门名字
    public String SAREANAME="";//地区名字
    public String SCITYNAME="";//城市名字
    public String SCOUNTYNAME="";//县城名字
    public String SJOBNAME="";//岗位名字
    public String SVALUEGROUP="";//春秋上衣
    public String ISDSTYLETYPEMSTID="";//款式ID


}
