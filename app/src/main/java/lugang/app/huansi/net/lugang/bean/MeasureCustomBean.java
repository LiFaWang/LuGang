package lugang.app.huansi.net.lugang.bean;

import huansi.net.qianjingapp.entity.WsData;

/**
 * Created by Tony on 2017/9/15.
 * 10:37

 {
 "ISDORDERMETERDTLID": "1106",
 "ISDSTYLETYPEMSTID": "1006",
 "SBILLNO": "001",
 "SVALUECODE": "",
 "SVALUEGROUP": "春秋上衣",
 "SDSTYLETYPEITEMDTLID": "1001",
 "SMETERCODE": "00101",
 "SMETERNAME": "衣长",
 "ISEQ": "3",
 "SFEMALEMINLENTH": "50.0",
 "SFEMALEMAXLENTH": "80.0",
 "SMALEMINLENTH": "65.0",
 "SMALEMAXLENTH": "90.0",
 "BEVENNO": "True",
 "BPOINT": ""
 },
 */

/*B.sFemaleMinLenth,--女最小值 空就不判断
		B.sFemaleMaxLenth,--女最大值 空就不判断
		B.sMaleMinLenth,--男最小值 空就不判断
		B.sMaleMaxLenth,--男最大值 空就不判断
		B.bEvenNo,--0是偶数 1是奇数  空就不判断
		B.bPoint--0是允许 1不允许 空就不判断*/

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
    public String ISTYLESEQ="0";
    public String ISMETERSIZE="0";//测量的数据
    public String BUPDATED=""; //订单款式明细中数据是否有修改

    /************************输入框的校验**开始**********************************/
    public String SFEMALEMINLENTH; //女最小值 空就不判断
    public String SFEMALEMAXLENTH; //女最大值 空就不判断
    public String SMALEMINLENTH;//男最小值 空就不判断
    public String SMALEMAXLENTH;//男最大值 空就不判断
    public String BEVENNO;//0是偶数 1是奇数  空就不判断
    public String BPOINT;//0是允许小数 1不允许小数 空就不判断

    /*************************输入框的校验**结束*****************************/


}
