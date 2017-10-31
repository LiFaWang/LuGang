package lugang.app.huansi.net.lugang.bean;

import huansi.net.qianjingapp.entity.WsData;

/**
 * Created by Tony on 2017/9/26.
 * 14:28
 {
 "STATUS": "0",
 "DATA": [
 {
 "SCUSTOMERNAME": "A银行",
 "IMALEMEASUREDQTY": "3",
 "INOTMALEMEASUREDQTY": "18",
 "IFEMALEMEASUREDQTY": "1",
 "INOTFEMALEMEASUREDQTY": "2",
 "IMEASUREDQTY": "4",
 "INOTMEASUREDQTY": "20",
 "SCUSTOMSERVICECONTACTS": "陆艳红",
 "SCONTACTNUMBER": "0512-58357551",
 "SEMAIL": "347448715@qq.com"
 }
 ]
 }
 {
 "SCUSTOMERNAME": "B银行",
 "IMALEMEASUREDQTY": "1",
 "INOTMALEMEASUREDQTY": "1",
 "IFEMALEMEASUREDQTY": "0",
 "INOTFEMALEMEASUREDQTY": "3",
 "IMEASUREDQTY": "1",
 "INOTMEASUREDQTY": "4",
 "IMALEADDMEASUREQTY": "0",
 "IFEMALEADDMEASUREQTY": "0",
 "ITOTALADDMEASUREQTY": "0",
 "SCUSTOMSERVICECONTACTS": "陆艳红",
 "SCONTACTNUMBER": "0512-58357551",
 "SEMAIL": "347448715@qq.com"
 }
 */

public class ConfirmTableBean extends WsData {
    public String SCUSTOMERNAME="";//A银行
//    public String SDEPARTMENTNAME="";//业务部
    public String SPERSON="";//量体人
    public String IMALEMEASUREDQTY="";//男已量体数
    public String INOTMALEMEASUREDQTY="";//男未量体数
    public String IFEMALEMEASUREDQTY="";//女已量体数
    public String INOTFEMALEMEASUREDQTY="";//女未量体数
    public String IMEASUREDQTY="";//量体总数
    public String INOTMEASUREDQTY="";//未量体总数
    public String IMALEADDMEASUREQTY="";//男添加人数
    public String IFEMALEADDMEASUREQTY="";//女添加人数
    public String ITOTALADDMEASUREQTY="";//添加总人数
    public String SCUSTOMSERVICECONTACTS="";//联系人
    public String SCONTACTNUMBER="";//联系电话
    public String SEMAIL="";//邮箱
}
