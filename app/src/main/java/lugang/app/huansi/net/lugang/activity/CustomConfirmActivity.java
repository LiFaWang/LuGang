package lugang.app.huansi.net.lugang.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import huansi.net.qianjingapp.base.NotWebBaseActivity;
import huansi.net.qianjingapp.entity.HsWebInfo;
import huansi.net.qianjingapp.entity.WsEntity;
import huansi.net.qianjingapp.imp.SimpleHsWeb;
import huansi.net.qianjingapp.utils.NewRxjavaWebUtils;
import huansi.net.qianjingapp.utils.OthersUtil;
import lugang.app.huansi.net.lugang.R;
import lugang.app.huansi.net.lugang.bean.ConfirmDepartBean;
import lugang.app.huansi.net.lugang.bean.ConfirmTableBean;
import lugang.app.huansi.net.lugang.bean.UpPictureBean;
import lugang.app.huansi.net.lugang.databinding.CustomConfirmActivityBinding;
import lugang.app.huansi.net.util.Base64BitmapUtils;
import rx.functions.Func1;

import static huansi.net.qianjingapp.utils.WebServices.WebServiceType.CUS_SERVICE;

public class CustomConfirmActivity extends NotWebBaseActivity {
    private CustomConfirmActivityBinding mCustomConfirmActivityBinding;
    private LinearLayout mLayout;

    @Override
    protected int getLayoutId() {
        return R.layout.custom_confirm_activity;
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CustomConfirmActivity.this);
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
        mCustomConfirmActivityBinding = (CustomConfirmActivityBinding) viewDataBinding;
        final Intent intent = getIntent();
        final String orderId = intent.getStringExtra("iordermetermstid");
        final String gpicture = intent.getStringExtra("gpicture");
        mCustomConfirmActivityBinding.tvDepartment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                requestDepartName(orderId);
            }
        });
        String absolutePath = getExternalFilesDir("screenshot").getAbsoluteFile()
                + "/" + orderId + ".png";
        Bitmap diskBitmap = getDiskBitmap(absolutePath);
        // mCustomConfirmActivityBinding.ivConfirm.setImageBitmap(diskBitmap);
        mCustomConfirmActivityBinding.ivConfirm.setImageBitmap(diskBitmap);

        requestConfirmTable(orderId, "");
//        String a = gpicture.replace("@", "=");
//        Bitmap bitmap = Base64BitmapUtils.base64ToBitmap(a);
////        加载保存的图片
//        mCustomConfirmActivityBinding.ivConfirm.setImageBitmap(bitmap);

        mCustomConfirmActivityBinding.signaturePad.setMinWidth((float) 0.5);

        mCustomConfirmActivityBinding.signaturePad.setMaxWidth(3);
//        清除手写
        mCustomConfirmActivityBinding.tvReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCustomConfirmActivityBinding.signaturePad.clear();
            }
        });
        //截屏并保存为png
        mCustomConfirmActivityBinding.btnSavePng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String string = mCustomConfirmActivityBinding.etMaleAddCount.getText().toString();
                String string1 = mCustomConfirmActivityBinding.etFemaleAddCount.getText().toString();
                String originalToatalMeasure = mCustomConfirmActivityBinding.etOriginalToatal.getText().toString();
                String addMeasure = mCustomConfirmActivityBinding.etAddCount.getText().toString();
                int toatal = Integer.parseInt(originalToatalMeasure) + Integer.parseInt(addMeasure);
                if (TextUtils.isEmpty(toatal+"")) {
                    OthersUtil.ToastMsg(CustomConfirmActivity.this, "原总人数或者添加人数不能为空");
                }else {
                    mCustomConfirmActivityBinding.etToatalCount.setText(String.valueOf(toatal));
                }




//            //截屏
                screenShot(orderId);
//            Intent intent=new Intent(CustomConfirmActivity.this, ScreenShotActivity.class);
//            intent.putExtra("orderId",orderId);
//            startActivity(intent);
//            Bitmap signatureBitmap = customConfirmActivityBinding.signaturePad.getSignatureBitmap();
//            String pictureData = Base64BitmapUtils.bitmapToBase64(signatureBitmap);
//            //上传数据库
//            upConfirmPicture(pictureData, orderId);
//            showImage(orderId);
            }
        });
    }


    /**
     * 获取部门的信息
     *
     * @param orderId
     */
    private void requestDepartName(final String orderId) {
        OthersUtil.showLoadDialog(mDialog);
        NewRxjavaWebUtils.getUIThread(NewRxjavaWebUtils.getObservable(this, "")
                .map(new Func1<String, HsWebInfo>() {
                    @Override
                    public HsWebInfo call(String s) {
                        return NewRxjavaWebUtils.getJsonData(CustomConfirmActivity.this,
                                CUS_SERVICE, "spappQueryBaseInfo",
                                "iIndexx=0", ConfirmDepartBean.class.getName(), true,
                                "没有取得确认函表单");

                    }
                }), this, mDialog, new SimpleHsWeb() {
            @Override
            public void success(HsWebInfo hsWebInfo) {
                List<String> confirmDepartBeanList = new ArrayList<>();
                List<WsEntity> listwsdata = hsWebInfo.wsData.LISTWSDATA;
                for (int i = 0; i < listwsdata.size(); i++) {
                    ConfirmDepartBean confirmDepartBean = (ConfirmDepartBean) listwsdata.get(i);
                    String sdepartmentname = confirmDepartBean.SDEPARTMENTNAME;
                    confirmDepartBeanList.add(sdepartmentname);
                }
                //显示复选框
                showMultiDialog(orderId,confirmDepartBeanList);
//              ConfirmDepartBean confirmDepartBean = (ConfirmDepartBean) hsWebInfo.wsData.LISTWSDATA.get(6);
//              mCustomConfirmActivityBinding.tvDepartment.setText(confirmDepartBean.SDEPARTMENTNAME);

            }
        });


    }

    /**
     * 显示复选框
     *  @param orderId
     * @param confirmDepartBeanList
     */
    private void showMultiDialog(final String orderId, List<String> confirmDepartBeanList) {
        //定义复选框选项
        final String[] multiChoiceItems =  confirmDepartBeanList.toArray(new String[0]);

        //复选框默认值：false=未选;true=选中 ,各自对应items[i]
        //根据
        final boolean[] defaultSelectedStatus=new boolean[confirmDepartBeanList.size()];
        for (int i = 0; i < confirmDepartBeanList.size(); i++) {
            defaultSelectedStatus[i]=false;
        }

        final StringBuilder sb = new StringBuilder();
        final StringBuilder sb1 = new StringBuilder();
        //创建对话框
        new AlertDialog.Builder(this)
                .setTitle("请选择参与量体的部门")//设置对话框标题
                .setMultiChoiceItems(multiChoiceItems, defaultSelectedStatus, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which,
                                        boolean isChecked) {
                        //来回重复选择取消，得相应去改变item对应的bool值，点击确定时，根据这个bool[],得到选择的内容
                        defaultSelectedStatus[which] = isChecked;
                    }
                })  //设置对话框[肯定]按钮
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (int i = 0; i < defaultSelectedStatus.length; i++) {
                            if (defaultSelectedStatus[i]) {
                                sb.append(multiChoiceItems[i]+"@");
                                sb1.append(multiChoiceItems[i]+" ");
                            }
                        }
                        // TODO Auto-generated method stub
//                        Toast.makeText(CustomConfirmActivity.this, sb.toString(), Toast.LENGTH_LONG).show();
                        requestConfirmTable(orderId, sb.toString());
                        mCustomConfirmActivityBinding.tvDepartment.setText(sb1.toString());

                    }
                })
                .setNegativeButton("取消", null)//设置对话框[否定]按钮
                .show();
    }





    /**
     * 获取确认函的数据
     *
     * @param orderId
     */
    private void requestConfirmTable(final String orderId, final String sDepartmentName ) {
        OthersUtil.showLoadDialog(mDialog);
        NewRxjavaWebUtils.getUIThread(NewRxjavaWebUtils.getObservable(this, "")
                        .map(new Func1<String, HsWebInfo>() {
                            @Override
                            public HsWebInfo call(String s) {
                                return NewRxjavaWebUtils.getJsonData(CustomConfirmActivity.this,
                                        CUS_SERVICE, "spappMeasureConfirmationInfo",
                                        "iOrderId=" + orderId+",sDepartmentName="+sDepartmentName, ConfirmTableBean.class.getName(), true,
                                        "没有取得确认函表单");

                            }
                        }), this, mDialog, new SimpleHsWeb() {
                    @Override
                    public void success(HsWebInfo hsWebInfo) {
                        List<WsEntity> listwsdata = hsWebInfo.wsData.LISTWSDATA;
                        ConfirmTableBean confirmTableBean = (ConfirmTableBean) listwsdata.get(0);
                        mCustomConfirmActivityBinding.tvMeasureCompany.setText(confirmTableBean.SCUSTOMERNAME);
                        SharedPreferences sp = CustomConfirmActivity.this.getSharedPreferences("user_info", Context.MODE_PRIVATE);
                        confirmTableBean.SPERSON=sp.getString("name", "");
                        mCustomConfirmActivityBinding.tvMeasurePerson.setText(confirmTableBean.SPERSON);
                        mCustomConfirmActivityBinding.maleMeasuredCount.setText(confirmTableBean.IMALEMEASUREDQTY);
                        mCustomConfirmActivityBinding.maleWaitMeasureCount.setText(confirmTableBean.INOTMALEMEASUREDQTY);
                        mCustomConfirmActivityBinding.femaleMeasuredCount.setText(confirmTableBean.IFEMALEMEASUREDQTY);
                        mCustomConfirmActivityBinding.femaleWaitMeasureCount.setText(confirmTableBean.INOTFEMALEMEASUREDQTY);
                        mCustomConfirmActivityBinding.measuredCount.setText(confirmTableBean.IMEASUREDQTY);
                        mCustomConfirmActivityBinding.waitMeasureCount.setText(confirmTableBean.INOTMEASUREDQTY);

                        int etOriginalToatal = Integer.parseInt(mCustomConfirmActivityBinding.measuredCount.getText().toString()) + Integer.parseInt(mCustomConfirmActivityBinding.waitMeasureCount.getText().toString());
                        mCustomConfirmActivityBinding.etOriginalToatal.setText(String.valueOf(etOriginalToatal));
                        mCustomConfirmActivityBinding.tvContact.setText("量体服务联系人：" + confirmTableBean.SCUSTOMSERVICECONTACTS);
                        mCustomConfirmActivityBinding.tvPhone.setText("电话：" + confirmTableBean.SCONTACTNUMBER);
                        mCustomConfirmActivityBinding.tvEmail.setText("电话：" + confirmTableBean.SEMAIL);
                        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
                        String date=sdf.format(new java.util.Date());
                        mCustomConfirmActivityBinding.tvTime.setText(date);
                    }
                }
        );

    }

    /**
     * 截屏
     *
     * @param orderId
     */
    private void screenShot(String orderId) {
        mLayout = (LinearLayout) findViewById(R.id.llConfimBody);
        //打开图像缓存
        mLayout.setDrawingCacheEnabled(true);
        //测量大小
        mLayout.buildDrawingCache();
        //获取可视组件的截图
        Bitmap cacheBitmap = mLayout.getDrawingCache();
        FileOutputStream fos = null;
        //获得sd卡路径
        String rootPath = getExternalFilesDir("screenshot").getAbsoluteFile()
                + "/" + orderId + ".png";
        File file = new File(rootPath);
        try {
            fos = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //把bitmap压缩成png格式，并通过fos写入到目标文件
        cacheBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        try {
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        showImage(orderId);
//            String pictureData = Base64BitmapUtils.bitmapToBase64(cacheBitmap);
//        //上传数据库
//        System.out.println(pictureData);
//        upConfirmPicture("", orderId);
    }


    private void showImage(String orderId) {

        String absolutePath = getExternalFilesDir("screenshot").getAbsoluteFile()
                + "/" + orderId + ".png";
        Bitmap diskBitmap = getDiskBitmap(absolutePath);
        // mCustomConfirmActivityBinding.ivConfirm.setImageBitmap(diskBitmap);
        String pictureData = Base64BitmapUtils.bitmapToBase64(diskBitmap);

        //上传数据库
        upConfirmPicture(pictureData, orderId);
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
        final String pictureBase64 = pictureData.replace("=", "@");
        OthersUtil.showLoadDialog(mDialog);
        NewRxjavaWebUtils.getUIThread(NewRxjavaWebUtils.getObservable(this, "")
                        .map(new Func1<String, HsWebInfo>() {
                            @Override
                            public HsWebInfo call(String s) {
                                return NewRxjavaWebUtils.getJsonData(getApplicationContext(), CUS_SERVICE,
                                        "spappMeasureSaveConfirmation"
                                        , "sPictureData=" + pictureBase64
                                        + ",iOrderMeterMstId=" + orderId,
                                        UpPictureBean.class.getName(),
                                        true, "上传确认函失败");
                            }
                        })
                , getApplicationContext(), mDialog, new SimpleHsWeb() {
                    @Override
                    public void success(HsWebInfo hsWebInfo) {
                        OthersUtil.ToastMsg(CustomConfirmActivity.this, "上传成功");
                    }

                });
    }

}
