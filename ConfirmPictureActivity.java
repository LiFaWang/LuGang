package lugang.app.huansi.net.lugang.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;

import com.github.gcacace.signaturepad.views.SignaturePad;

import java.io.File;

import huansi.net.qianjingapp.base.NotWebBaseActivity;
import huansi.net.qianjingapp.entity.HsWebInfo;
import huansi.net.qianjingapp.imp.SimpleHsWeb;
import huansi.net.qianjingapp.utils.NewRxjavaWebUtils;
import huansi.net.qianjingapp.utils.OthersUtil;
import lugang.app.huansi.net.lugang.R;
import lugang.app.huansi.net.lugang.bean.UpPictureBean;
import lugang.app.huansi.net.lugang.databinding.ActivityConfirmPictureBinding;
import lugang.app.huansi.net.util.Base64BitmapUtils;
import rx.functions.Func1;

import static huansi.net.qianjingapp.utils.WebServices.WebServiceType.CUS_SERVICE;

public class ConfirmPictureActivity extends NotWebBaseActivity {


    private ActivityConfirmPictureBinding mActivityConfirmPictureBinding;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_confirm_picture;
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ConfirmPictureActivity.this);
        builder.setTitle("提示");
        builder.setMessage("确定要退出当前页面吗");
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void init() {
        mActivityConfirmPictureBinding = (ActivityConfirmPictureBinding) viewDataBinding;
        final Intent intent = getIntent();
        final String orderId = intent.getStringExtra("iordermetermstid");
        final String gpicture = intent.getStringExtra("gpicture");
        System.out.println("gpicture:" + gpicture);
        Bitmap bitmap = Base64BitmapUtils.base64ToBitmap(gpicture);
        //加载保存的图片
        mActivityConfirmPictureBinding.ivConfirm.setImageBitmap(bitmap);
//        Glide.with(this)
//                .load(Base64BitmapUtils.base64ToBitmap(gpicture))
//                .placeholder(R.drawable.test) //设置占位图
//                .fitCenter()
//                .error(R.drawable.ic_launcher)//设置错误图片
//                .into(mActivityConfirmPictureBinding.ivConfirm);


        mActivityConfirmPictureBinding.signaturePad.setMinWidth((float) 0.5);

        mActivityConfirmPictureBinding.signaturePad.setMaxWidth(3);
        mActivityConfirmPictureBinding.signaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
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
        mActivityConfirmPictureBinding.tvReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivityConfirmPictureBinding.signaturePad.clear();
            }
        });
        //截屏并保存为png
        mActivityConfirmPictureBinding.btnSavePDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//            //截屏
//            Intent intent=new Intent(ConfirmPictureActivity.this, ScreenShotActivity.class);
//            intent.putExtra("orderId",orderId);
//            startActivity(intent);
                Bitmap signatureBitmap = mActivityConfirmPictureBinding.signaturePad.getSignatureBitmap();
                String pictureData = Base64BitmapUtils.bitmapToBase64(signatureBitmap);

                //上传数据库
                upConfirmPicture(pictureData, orderId);
            }
        });
    }


    private void showImage(String orderId) {
        File screenshot = getExternalFilesDir("screenshot").getAbsoluteFile();
        String absolutePath = screenshot.getAbsolutePath() + "/" + orderId + ".png";
        Log.e("TAG", "getFilesDir() : " + absolutePath);
        Bitmap diskBitmap = getDiskBitmap(absolutePath);
        String pictureData = Base64BitmapUtils.bitmapToBase64(diskBitmap);

        //上传数据库
//            upConfirmPicture(pictureData,orderId);
    }

    private Bitmap getDiskBitmap(String pathString) {
        Bitmap bitmap = null;
        try {
            File file = new File(pathString);
            if (file.exists()) {
                bitmap = BitmapFactory.decodeFile(pathString);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }


        return bitmap;
    }

    /**
     * 上传图片
     */
    private void upConfirmPicture(final String pictureData, final String orderId) {
        OthersUtil.showLoadDialog(mDialog);
        NewRxjavaWebUtils.getUIThread(NewRxjavaWebUtils.getObservable(this, "")
                        .map(new Func1<String, HsWebInfo>() {
                            @Override
                            public HsWebInfo call(String s) {
                                return NewRxjavaWebUtils.getJsonData(getApplicationContext(), CUS_SERVICE,
                                        "spappMeasureSaveConfirmation"
                                        , "sPictureData=" + pictureData
                                                + ",iOrderMeterMstId=" + orderId,
                                        UpPictureBean.class.getName(),
                                        true, "上传确认函失败");
                            }
                        })
                , getApplicationContext(), mDialog, new SimpleHsWeb() {
                    @Override
                    public void success(HsWebInfo hsWebInfo) {
                        OthersUtil.ToastMsg(ConfirmPictureActivity.this, "上传成功");
                        finish();
                    }

                });
    }
}
