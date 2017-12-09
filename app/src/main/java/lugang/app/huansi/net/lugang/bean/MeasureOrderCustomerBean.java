package lugang.app.huansi.net.lugang.bean;

import huansi.net.qianjingapp.entity.WsData;

/**
 * Created by 年 on 2017/11/1.
 * 筛选的单位
 *
 *
 *
 * {
 "IMEASUREDQTY": "47",
 "INOTMEASUREDQTY": "640"
 }
 */

public class MeasureOrderCustomerBean extends WsData {
    public String SCUSTOMERNAME="";//单位信息
    public String IMEASUREDQTY="";//已量体人数
    public String INOTMEASUREDQTY="";//待量体人数
}
