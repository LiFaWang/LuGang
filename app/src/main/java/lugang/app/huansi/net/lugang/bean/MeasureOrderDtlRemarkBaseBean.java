package lugang.app.huansi.net.lugang.bean;

import huansi.net.qianjingapp.entity.WsData;

/**
 * Created by 年 on 2017/11/5.
 * 订单明细对应的备注基本信息
 */

public class MeasureOrderDtlRemarkBaseBean extends WsData{
    /*  B.iSdStyleTypeMstId,
    B.iid,
    B.sMeterMarkCode,
    B.sMeterMarkName*/
    public String ISDSTYLETYPEMSTID="";//款式ID
    public String IID="";//备注大类ID
    public String SMETERMARKCODE="";//备注大类Code
    public String SMETERMARKNAME="";//备注大类Name

}
