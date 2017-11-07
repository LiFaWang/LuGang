package lugang.app.huansi.net.lugang.bean;

import huansi.net.qianjingapp.entity.WsData;

/**
 * Created by 年 on 2017/11/5.
 * 订单明细对应的款式基本信息
 */

public class MeasureOrderDtlStyleBaseBean extends WsData{
    /*A.iid AS iSdStyleTypeMstId,
		A.sBillNo,
		A.sValueCode,
		A.sValueGroup,
		B.iid AS iSdStyleTypeItemDtlId,
		B.sMeterCode,
		B.sMeterName,
		B.iSeq*/
    public String ISDSTYLETYPEMSTID="";//款式ID
    public String SBILLNO="";//单号
    public String SVALUECODE="";//款式Code
    public String SVALUEGROUP="";//款式名称
    public String ISDSTYLETYPEITEMDTLID="";//款式明细ID
    public String SMETERCODE="";//款式明细Code
    public String SMETERNAME="";//款式明细Name
    public String ISEQ="0";//序号
}
