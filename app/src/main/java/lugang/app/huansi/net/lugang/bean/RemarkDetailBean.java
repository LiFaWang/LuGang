package lugang.app.huansi.net.lugang.bean;

import huansi.net.qianjingapp.entity.WsData;

/**
 * Created by Tony on 2017/9/15.
 * 9:29
 * {
 "STATUS": "0",
 "DATA": [
 {
 "IID": "1003",
 "SMETERMARKCODE": "0011101",
 "SMETERMARKNAME": "前衣加长1"
 },
 {
 "IID": "1004",
 "SMETERMARKCODE": "0011102",
 "SMETERMARKNAME": "前衣加长2"
 }
 ]
 }
 */

public class RemarkDetailBean extends WsData {
    public String iOrderDtlId="";//订单明细id


    public String IID="";
    public String SMETERMARKCODE="";
    public String SMETERMARKNAME="";

    public boolean isChoose=false;
    public boolean isAdd=false;//是否添加到已选内容
}
