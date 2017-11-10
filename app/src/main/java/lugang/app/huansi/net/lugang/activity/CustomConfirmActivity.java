package lugang.app.huansi.net.lugang.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.LinearLayout;

import com.github.gcacace.signaturepad.views.SignaturePad;

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
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE" };

    @Override
    protected int getLayoutId() {
        return R.layout.custom_confirm_activity;
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CustomConfirmActivity.this);
        builder.setTitle("提示");
        builder.setMessage("当前页面数据未上传保存，是否要确认退出");
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
    public static boolean isGrantExternalRW(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            activity.requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1);

            return false;
        }

        return true;
    }
    @Override
    public void init() {

            try {
                //检测是否有写的权限
                int permission = ActivityCompat.checkSelfPermission(CustomConfirmActivity.this,
                        "android.permission.WRITE_EXTERNAL_STORAGE");
                if (permission != PackageManager.PERMISSION_GRANTED) {
                    // 没有写的权限，去申请写的权限，会弹出对话框
                    ActivityCompat.requestPermissions(CustomConfirmActivity.this, PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        mCustomConfirmActivityBinding = (CustomConfirmActivityBinding) viewDataBinding;
        final Intent intent = getIntent();
        final String orderId = intent.getStringExtra("iordermetermstid");
        final String gpicture = intent.getStringExtra("gpicture");
        final String scustomername = intent.getStringExtra("scustomername");
        mCustomConfirmActivityBinding.tvDepartment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                requestDepartName(orderId);
            }
        });
//        String rootPath = getExternalFilesDir("screenshot").getAbsoluteFile()
//                + "/" + scustomername+"_"+orderId + ".png";
        String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures/"
                + "/" + scustomername+"_"+orderId + ".png";
        Bitmap diskBitmap = getDiskBitmap(rootPath);
        mCustomConfirmActivityBinding.ivConfirm.setImageBitmap(diskBitmap);

        requestConfirmTable(orderId, "");
//        String a = gpicture.replace("@", "=");
//        Bitmap bitmap = Base64BitmapUtils.base64ToBitmap(a);
////        加载保存的图片
//        mCustomConfirmActivityBinding.ivConfirm.setImageBitmap(bitmap);
        mCustomConfirmActivityBinding.signaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
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
//                mCustomConfirmActivityBinding.btnSavePng.setVisibility(View.INVISIBLE);
//                mCustomConfirmActivityBinding.llEcho.setVisibility(View.GONE);
                //截屏
                screenShot(orderId, scustomername);
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
                        mCustomConfirmActivityBinding.etMaleAddCount.setText(confirmTableBean.IMALEADDMEASUREQTY);
                        mCustomConfirmActivityBinding.etFemaleAddCount.setText(confirmTableBean.IFEMALEADDMEASUREQTY);
                        mCustomConfirmActivityBinding.etAddCount.setText(confirmTableBean.ITOTALADDMEASUREQTY);
                        // TODO Auto-generated method stub
                        int etOriginalTotal = Integer.parseInt(mCustomConfirmActivityBinding.measuredCount.getText().toString()) + Integer.parseInt(mCustomConfirmActivityBinding.waitMeasureCount.getText().toString());
                        int addCount=Integer.parseInt(mCustomConfirmActivityBinding.etAddCount.getText().toString());
                        int total = etOriginalTotal + addCount;
                        mCustomConfirmActivityBinding.etOriginalToatal.setText(String.valueOf(etOriginalTotal));
                        mCustomConfirmActivityBinding.etToatalCount.setText(String.valueOf(total));
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
     * @param orderId
     * @param scustomername
     */
    private void screenShot(String orderId, String scustomername) {

        mLayout = (LinearLayout) findViewById(R.id.llConfimBody);
        //打开图像缓存
        mLayout.setDrawingCacheEnabled(true);
        //测量大小
        mLayout.buildDrawingCache();
        //获取可视组件的截图
        Bitmap cacheBitmap = mLayout.getDrawingCache();
        FileOutputStream fos = null;
        //获得sd卡路径
        String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures/"
                 + scustomername+"_"+orderId + ".png";
//        String rootPath = getExternalFilesDir("screenshot")
//                + "/" + scustomername+"_"+orderId + ".png";
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

        showImage(orderId,scustomername);
//            String pictureData = Base64BitmapUtils.bitmapToBase64(cacheBitmap);
//        //上传数据库
//        System.out.println(pictureData);
//        upConfirmPicture("", orderId);
    }


    private void showImage(String orderId, String scustomername) {
        String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures/"
                + "/" + scustomername+"_"+orderId + ".png";
//        String rootPath = getExternalFilesDir("screenshot").getAbsoluteFile()
//                + "/" + scustomername+"_"+orderId + ".png";
        Bitmap diskBitmap = getDiskBitmap(rootPath);
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
