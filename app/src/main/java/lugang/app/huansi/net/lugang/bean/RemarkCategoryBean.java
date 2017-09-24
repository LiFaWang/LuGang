package lugang.app.huansi.net.lugang.bean;

import huansi.net.qianjingapp.entity.WsData;

/**
 * Created by Tony on 2017/9/15.
 *
 * 9:27
 {
 "STATUS": "0",
 "DATA": [
 {
 "IID": "1001",
 "SBILLNO": "00111",
 "SMETERMARKNAME": "前衣长"
 }
 ]
 }
 */

public class RemarkCategoryBean extends WsData{
     public String IID="";
     public String SBILLNO="";
     public String SMETERMARKNAME="";

     public boolean isChoose=false;
}
