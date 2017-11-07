package lugang.app.huansi.net.lugang.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import huansi.net.qianjingapp.base.NotWebBaseActivity;
import huansi.net.qianjingapp.base.PermissionsActivity;
import huansi.net.qianjingapp.entity.HsWebInfo;
import huansi.net.qianjingapp.imp.SimpleHsWeb;
import huansi.net.qianjingapp.utils.NewRxjavaWebUtils;
import huansi.net.qianjingapp.utils.OthersUtil;
import huansi.net.qianjingapp.utils.PermissionsChecker;
import huansi.net.qianjingapp.utils.SPUtils;
import lugang.app.huansi.net.lugang.R;
import lugang.app.huansi.net.lugang.bean.LoginBean;
import lugang.app.huansi.net.lugang.constant.Constant;
import lugang.app.huansi.net.lugang.databinding.ActivityLoginBinding;
import lugang.app.huansi.net.util.LGSPUtils;
import rx.functions.Func1;

import static huansi.net.qianjingapp.utils.WebServices.WebServiceType.CUS_SERVICE;
import static lugang.app.huansi.net.lugang.constant.Constant.PERMISSIONS;
import static lugang.app.huansi.net.util.LGSPUtils.USER_GUID;
import static lugang.app.huansi.net.util.LGSPUtils.USER_ID;
import static lugang.app.huansi.net.util.LGSPUtils.USER_NAME;
import static lugang.app.huansi.net.util.LGSPUtils.USER_PWD;

public class LoginActivity extends NotWebBaseActivity {

    private ActivityLoginBinding mActivityLoginBinding;
    private PermissionsChecker mChecker;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public void init() {

        mActivityLoginBinding = (ActivityLoginBinding) viewDataBinding;
        mChecker = new PermissionsChecker(this);
//        LoginBean loginBean = readUser();
//        if (!TextUtils.isEmpty(loginBean.toString())){
//            mActivityLoginBinding.etIpNumber.setText(loginBean.SUSERIP);
//            mActivityLoginBinding.etLoginNumber.setText(loginBean.SUSERID);
//            mActivityLoginBinding.etLoginPwd.setText(loginBean.SUSERPSW);
//        }


        mActivityLoginBinding.etIpNumber.setText(SPUtils.readMacIp(getApplicationContext()));
        mActivityLoginBinding.etLoginNumber.setText(LGSPUtils.getLocalData(getApplicationContext(),USER_ID,String.class.getName(),"").toString());
        mActivityLoginBinding.etLoginPwd.setText(LGSPUtils.getLocalData(getApplicationContext(),USER_PWD,String.class.getName(),"").toString());



        mActivityLoginBinding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ip = mActivityLoginBinding.etIpNumber.getText().toString().trim();
                String number = mActivityLoginBinding.etLoginNumber.getText().toString().trim();
                String pwd = mActivityLoginBinding.etLoginPwd.getText().toString().trim();
                if (TextUtils.isEmpty(ip) ) {
                    OthersUtil.ToastMsg(LoginActivity.this, "IP不能为空");
                    return;
                }
                if (TextUtils.isEmpty(number) ) {
                    OthersUtil.ToastMsg(LoginActivity.this, "工号不能为空");
                    return;
                }
                if(TextUtils.isEmpty(pwd)){
                    OthersUtil.ToastMsg(LoginActivity.this, "密码不能为空");
                    return;
                }
                SPUtils.saveMacIp(getApplicationContext(),ip);
                login(number, pwd);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mChecker.lacksPermissions(PERMISSIONS)) {
            PermissionsActivity.startActivityForResult(this, 0, PERMISSIONS);
        }
        String userGUID=LGSPUtils.getLocalData(getApplicationContext(),USER_GUID,String.class.getName(),"").toString();
        if(!userGUID.isEmpty()){
            toMain();
        }
    }

    /**
     * 登录
     */
    private void login(final String sUserID, final String sPassword) {
        OthersUtil.showLoadDialog(mDialog);
        NewRxjavaWebUtils.getUIThread(NewRxjavaWebUtils.getObservable(this, "")
                        .map(new Func1<String, HsWebInfo>() {
                            @Override
                            public HsWebInfo call(String s) {
                                //登录
                                return NewRxjavaWebUtils.getJsonData(getApplicationContext(), CUS_SERVICE,
                                        "spappMeasureUserLogin",
                                        "sUserID=" + sUserID + ",sPassword=" + sPassword,
                                        LoginBean.class.getName(),
                                        true, "请检查IP输入是否正确或者没有返回数据");
                            }
                        })
                , this, mDialog, new SimpleHsWeb() {
                    @Override
                    public void success(HsWebInfo hsWebInfo) {
                        LoginBean loginBean = (LoginBean) hsWebInfo.wsData.LISTWSDATA.get(0);
                        String userId = loginBean.SUSERID;
                        saveUser(loginBean);
                        toMain();
                    }
                });
    }

    private void toMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//        intent.putExtra(Constant.SUSERID, userId);
        startActivity(intent);
        finish();
    }


    //保存用户信息
    public void saveUser(LoginBean user) {
//        SharedPreferences sp = this.getSharedPreferences("user_info", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sp.edit();
//        editor.putString("name", user.SUSERNAME);
//        editor.putString("ugu_id", user.UGUID);
//        editor.putString("user_id", user.SUSERID);
//        editor.putString("user_ip", user.SUSERIP);
//        editor.putString("user_psw", user.SUSERPSW);
//        editor.apply();//必须提交，否则保存不成功

        LGSPUtils.saveLocalData(getApplicationContext(), USER_NAME,user.SUSERNAME,String.class.getName());
        LGSPUtils.saveLocalData(getApplicationContext(), USER_GUID,user.UGUID,String.class.getName());
        LGSPUtils.saveLocalData(getApplicationContext(), USER_ID,user.SUSERID,String.class.getName());



//        LGSPUtils.saveLocalData(getApplicationContext(), IP,mActivityLoginBinding.etIpNumber.getText().toString(),String.class.getName());
        LGSPUtils.saveLocalData(getApplicationContext(), USER_PWD,mActivityLoginBinding.etLoginPwd.getText().toString(),String.class.getName());

    }
//    //读取用户信息
//    public LoginBean readUser() {
//        SharedPreferences sp = this.getSharedPreferences("user_info", Context.MODE_PRIVATE);
//        LoginBean loginBean=new LoginBean();
//        loginBean.SUSERID=sp.getString("user_id","");
//        loginBean.SUSERNAME=sp.getString("name", "");
//        loginBean.UGUID=sp.getString("ugu_id", "");
//        loginBean.SUSERIP=sp.getString("user_ip", "");
//        loginBean.SUSERPSW=sp.getString("user_psw", "");
//        return loginBean;
//    }
}
