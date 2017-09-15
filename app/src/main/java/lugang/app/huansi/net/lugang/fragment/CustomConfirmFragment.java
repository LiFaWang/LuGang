package lugang.app.huansi.net.lugang.fragment;

import android.content.Intent;
import android.view.View;

import com.androidyuan.lib.screenshot.ScreenShotActivity;
import com.github.gcacace.signaturepad.views.SignaturePad;

import huansi.net.qianjingapp.fragment.BaseFragment;
import lugang.app.huansi.net.lugang.R;
import lugang.app.huansi.net.lugang.databinding.CustomConfirmFragmentBinding;

/**
 * Created by Tony on 2017/9/9.
 * 11:31
 */

public class CustomConfirmFragment extends BaseFragment {

    private CustomConfirmFragmentBinding mConfirmFragmentBinding;

    @Override
    public int getLayout() {
        return R.layout.custom_confirm_fragment;
    }
    @Override
    public void init() {
        mConfirmFragmentBinding = (CustomConfirmFragmentBinding) viewDataBinding;
        mConfirmFragmentBinding.signaturePad.setMinWidth((float) 0.5);
        mConfirmFragmentBinding.signaturePad.setMaxWidth(3);
        mConfirmFragmentBinding.signaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {

            }

            @Override
            public void onSigned() {

            }

            @Override
            public void onClear() {


            }
        });
        //清除手写
        mConfirmFragmentBinding.tvReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mConfirmFragmentBinding.signaturePad.clear();
            }
        });
        //截屏并保存为PDF
        mConfirmFragmentBinding.btnSavePDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //截屏
                startActivity(new Intent(getActivity(), ScreenShotActivity.class));
                //保存pdf


            }
        });


    }


}
