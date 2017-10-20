package lugang.app.huansi.net.lugang.bean;

import huansi.net.qianjingapp.entity.WsData;

/**
 * Created by Tony on 2017/9/22.
 * 待量体、已量体保存的备注信息
 * 16:52
 */

public class RemarkSavedBean extends WsData {
//    C.sMeterMarkCode,--备注Code
//    C.sMeterMarkName,--备注名
//    C.iid AS iSmetermarkDtlId --备注明细ID
  //  "ISDORDERMETERDTLID":1001
    public String ISDORDERMETERDTLID="";//订单明细id
    public String SMETERMARKCODE="";
    public String SMETERMARKNAME="";
    public String ISMETERMARKDTLID="";
}
