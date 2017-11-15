package lugang.app.huansi.net.lugang.bean;

import huansi.net.qianjingapp.entity.WsData;

/**
 * Created by Tony on 2017/9/13.
 * 17:37
 *

 */

public class StartMeasureBean extends WsData {
/*
 {
            "ISDORDERMETERMSTID": "1026",
            "ILISTNO": "0",
            "SAREANAME": "a",
            "SCITYNAME": "d",
            "SCOUNTYNAME": "f",
            "SCUSTOMERNAME": "A银行",
            "SDEPARTMENTNAME": "gb",
            "SPERSON": "j",
            "SVALUENAME": "1006",
            "SBILLNO": "A0001"
        }
 */
    public String ISDORDERMETERMSTID="";//订单头表
    public String ILISTNO="";
    public String SAREANAME="";
    public String SCITYNAME="";
    public String SCOUNTYNAME="";
    public String SCUSTOMERCODE="";
    public String SCUSTOMERNAME="";
    public String SDEPARTMENTNAME="";
    public String SPERSON="";
    public String SJOBNAME="";

    public String SVALUENAME="";//款式的ID
    public String SBILLNO="";//清单的ID
    public String SSEX="男";


    public String IORDERTYPE="0";//区分是从哪个界面跳转的
}
