package lugang.app.huansi.net.lugang.bean;

import huansi.net.qianjingapp.entity.WsData;

/**
 * Created by Tony on 2017/9/13.
 * 17:35
 *  {
 "STATUS": "0",
 "DATA": [
 {
 "UGUID": "b414b2f4-c7af-4e2a-8f8a-93ec77a6392c",
 "SUSERID": "cttsoft",
 "SUSERNAME": "cttsoft"
 }
 ]
 }
 */

public class LoginBean extends WsData {
    public String UGUID="";
    public String SUSERID="";//员工的id
    public String SUSERNAME="";//员工的名字
    public String SUSERIP="";//员工的ip
    public String SUSERPSW="";//员工的密码

}
