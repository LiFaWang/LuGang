package lugang.app.huansi.net.lugang.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.View;

import huansi.net.qianjingapp.base.NotWebBaseActivity;
import huansi.net.qianjingapp.base.PermissionsActivity;
import huansi.net.qianjingapp.entity.HsWebInfo;
import huansi.net.qianjingapp.imp.SimpleHsWeb;
import huansi.net.qianjingapp.utils.NewRxjavaWebUtils;
import huansi.net.qianjingapp.utils.OthersUtil;
import huansi.net.qianjingapp.utils.PermissionsChecker;
import lugang.app.huansi.net.lugang.R;
import lugang.app.huansi.net.lugang.bean.LoginBean;
import lugang.app.huansi.net.lugang.constant.Constant;
import lugang.app.huansi.net.lugang.databinding.ActivityLoginBinding;
import lugang.app.huansi.net.util.SPUtils;
import rx.functions.Func1;

import static huansi.net.qianjingapp.utils.WebServices.WebServiceType.CUS_SERVICE;
import static lugang.app.huansi.net.lugang.constant.Constant.PERMISSIONS;

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
        LoginBean loginBean = readUser();
        if (!TextUtils.isEmpty(loginBean.toString())){
            mActivityLoginBinding.etIpNumber.setText(loginBean.SUSERIP);
            mActivityLoginBinding.etLoginNumber.setText(loginBean.SUSERID);
            mActivityLoginBinding.etLoginPwd.setText(loginBean.SUSERPSW);
        }


        mActivityLoginBinding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ip = mActivityLoginBinding.etIpNumber.getText().toString().trim();
                String number = mActivityLoginBinding.etLoginNumber.getText().toString().trim();
                String pwd = mActivityLoginBinding.etLoginPwd.getText().toString().trim();
                SPUtils.saveMacIp(LoginActivity.this,ip);
                if (TextUtils.isEmpty(number) || TextUtils.isEmpty(pwd)) {
                    OthersUtil.ToastMsg(LoginActivity.this, "工号或密码不能为空");
                }
                login(number, pwd,ip);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mChecker.lacksPermissions(PERMISSIONS)) {
            PermissionsActivity.startActivityForResult(this, 0, PERMISSIONS);
        }
    }

    /**
     * 登录
     */
    private void login(final String sUserID, final String sPassword, final String ip) {
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
                        String mSuserid = loginBean.SUSERID;
                        loginBean.SUSERIP=ip;
                        loginBean.SUSERPSW=sPassword;
                        saveUser(loginBean);
                        jumpToMain(mSuserid);
                    }
                });
    }

    private void jumpToMain(String mSuserid) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra(Constant.SUSERID, mSuserid);
        startActivity(intent);
        finish();
    }


    //保存用户信息
    public void saveUser(LoginBean user) {
        SharedPreferences sp = this.getSharedPreferences("user_info", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("name", user.SUSERNAME);
        editor.putString("ugu_id", user.UGUID);
        editor.putString("user_id", user.SUSERID);
        editor.putString("user_ip", user.SUSERIP);
        editor.putString("user_psw", user.SUSERPSW);
        editor.apply();//必须提交，否则保存不成功
    }
    //读取用户信息
    public LoginBean readUser() {
        SharedPreferences sp = this.getSharedPreferences("user_info", Context.MODE_PRIVATE);
        LoginBean loginBean=new LoginBean();
        loginBean.SUSERID=sp.getString("user_id","");
        loginBean.SUSERNAME=sp.getString("name", "");
        loginBean.UGUID=sp.getString("ugu_id", "");
        loginBean.SUSERIP=sp.getString("user_ip", "");
        loginBean.SUSERPSW=sp.getString("user_psw", "");
        return loginBean;
    }
}
