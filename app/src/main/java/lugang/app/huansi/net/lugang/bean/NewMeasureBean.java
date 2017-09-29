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
 */

public class NewMeasureBean extends WsData {
    public String SCUSTOMERNAME="";//A银行
    public String SCUSTOMERCODE="";//0001
    public String ISDORDERMETERMSTID="";//
    public String SVALUEGROUP="";//春秋上衣
    public String ISDSTYLETYPEMSTID="";//1008
    public boolean isSelected;
    public String ETAREANAME="";
    public String ETCITYNAME="";
    public String ETCOUNTYNAME="";
    public String ETDEPARTMENTNAME="";
    public String ETJOBNAME="";
    public String ETPERSON="";
    public String ETSEX="";
    public String ETCOUNT="";


}
