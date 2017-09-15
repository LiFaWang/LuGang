package lugang.app.huansi.net.lugang.activity;

import android.support.annotation.IdRes;
import android.support.v4.app.FragmentTransaction;
import android.widget.RadioGroup;

import huansi.net.qianjingapp.base.NotWebBaseActivity;
import huansi.net.qianjingapp.utils.OthersUtil;
import lugang.app.huansi.net.lugang.R;
import lugang.app.huansi.net.lugang.databinding.ActivityMainBinding;
import lugang.app.huansi.net.lugang.fragment.CustomConfirmFragment;
import lugang.app.huansi.net.lugang.fragment.MeasureCustomFragment;
import lugang.app.huansi.net.lugang.fragment.RepairRegisterFragment;
import lugang.app.huansi.net.lugang.fragment.TaskShowFragment;

public class MainActivity  extends NotWebBaseActivity  {

    private ActivityMainBinding mActivityMainBinding;
    private MeasureCustomFragment mMeasureCustomFragment;
    private TaskShowFragment mTaskShowFragment;
    private RepairRegisterFragment mRepairRegisterFragment;
    private CustomConfirmFragment mCustomConfirmFragment;

    @Override
    public void init() {
        mActivityMainBinding = (ActivityMainBinding) viewDataBinding;
        initFragment();
        mActivityMainBinding.rgBase.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                FragmentTransaction fs = getSupportFragmentManager().beginTransaction();
                switch (checkedId) {
                    case R.id.measureCustom://量体定制
                        hideFragment();
                        fs.show(mMeasureCustomFragment);
                        OthersUtil.ToastMsg(MainActivity.this,"mMeasureCustomFragment");
                        fs.commitAllowingStateLoss();
                        break;
                    case R.id.taskShow://任务看板
                        hideFragment();
                        fs.show(mTaskShowFragment);
                        OthersUtil.ToastMsg(MainActivity.this,"mTaskShowFragment");
                        fs.commitAllowingStateLoss();
                        break;
                    case R.id.repairRegister://返修登记
                        hideFragment();
                        fs.show(mRepairRegisterFragment);
                        OthersUtil.ToastMsg(MainActivity.this,"mRepairRegisterFragment");
                        fs.commitAllowingStateLoss();
                        break;
                    case R.id.customerConfirm://用户确认
                        hideFragment();
                        fs.show(mCustomConfirmFragment);
                        OthersUtil.ToastMsg(MainActivity.this,"mCustomConfirmFragment");
                        fs.commitAllowingStateLoss();
                        break;
                    default:
                        break;

                }
            }
        });

        mActivityMainBinding.rgBase.check(R.id.taskShow);

    }
    private void initFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        mMeasureCustomFragment = new MeasureCustomFragment();
        mTaskShowFragment=new TaskShowFragment();
        mRepairRegisterFragment=new RepairRegisterFragment();
        mCustomConfirmFragment=new CustomConfirmFragment();
        transaction.add(R.id.flContent, mMeasureCustomFragment);
        transaction.add(R.id.flContent, mTaskShowFragment);
        transaction.add(R.id.flContent, mRepairRegisterFragment);
        transaction.add(R.id.flContent, mCustomConfirmFragment);
        transaction.hide(mTaskShowFragment);
        transaction.hide(mMeasureCustomFragment);
        transaction.hide(mRepairRegisterFragment);
        transaction.hide(mCustomConfirmFragment);
        transaction.commitAllowingStateLoss();

    }
    private void hideFragment(){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.hide(mTaskShowFragment);
        transaction.hide(mMeasureCustomFragment);
        transaction.hide(mRepairRegisterFragment);
        transaction.hide(mCustomConfirmFragment);
        transaction.commitAllowingStateLoss();
    }





    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

}
