package lugang.app.huansi.net.lugang.activity;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import huansi.net.qianjingapp.base.NotWebBaseActivity;
import huansi.net.qianjingapp.entity.HsWebInfo;
import huansi.net.qianjingapp.entity.WsEntity;
import huansi.net.qianjingapp.imp.SimpleHsWeb;
import huansi.net.qianjingapp.utils.NetUtil;
import huansi.net.qianjingapp.utils.NewRxjavaWebUtils;
import huansi.net.qianjingapp.utils.OthersUtil;
import huansi.net.qianjingapp.view.LoadProgressDialog;
import lugang.app.huansi.net.db.MeasureDataInSQLite;
import lugang.app.huansi.net.db.MeasureOrderInSQLite;
import lugang.app.huansi.net.db.MeasureRemarkDataInSQLite;
import lugang.app.huansi.net.greendao.DaoSession;
import lugang.app.huansi.net.greendao.MeasureDataInSQLiteDao;
import lugang.app.huansi.net.greendao.MeasureOrderInSQLiteDao;
import lugang.app.huansi.net.greendao.MeasureRemarkDataInSQLiteDao;
import lugang.app.huansi.net.lugang.R;
import lugang.app.huansi.net.lugang.bean.MeasureBaseBean;
import lugang.app.huansi.net.lugang.bean.MeasureCustomBean;
import lugang.app.huansi.net.lugang.bean.MeasureDateBean;
import lugang.app.huansi.net.lugang.bean.RemarkSavedBean;
import lugang.app.huansi.net.lugang.constant.Constant;
import lugang.app.huansi.net.lugang.databinding.ActivityMeasureCustomBinding;
import lugang.app.huansi.net.lugang.event.SecondToFirstActivityEvent;
import lugang.app.huansi.net.util.GreenDaoUtil;
import lugang.app.huansi.net.util.LGSPUtils;
import lugang.app.huansi.net.widget.CustomKeyboardEditText;
import lugang.app.huansi.net.widget.CustomKeyboardEditText.OnEditFocusListener;
import rx.functions.Func1;

import static huansi.net.qianjingapp.utils.NewRxjavaWebUtils.getJsonData;
import static huansi.net.qianjingapp.utils.WebServices.WebServiceType.CUS_SERVICE;
import static lugang.app.huansi.net.lugang.constant.Constant.AREANAME;
import static lugang.app.huansi.net.lugang.constant.Constant.CITYNAME;
import static lugang.app.huansi.net.lugang.constant.Constant.COUNTYNAME;
import static lugang.app.huansi.net.lugang.constant.Constant.CUSTOMERNAME;
import static lugang.app.huansi.net.lugang.constant.Constant.DEPARTMENTNAME;
import static lugang.app.huansi.net.lugang.constant.Constant.MeasureCustomActivityConstant.ORDER_DTL_ID_INTENT;
import static lugang.app.huansi.net.lugang.constant.Constant.MeasureCustomActivityConstant.REMARK_INTENT_DATA;
import static lugang.app.huansi.net.lugang.constant.Constant.MeasureCustomActivityConstant.REMARK_INTENT_KEY;
import static lugang.app.huansi.net.lugang.constant.Constant.MeasureCustomActivityConstant.REMARK_RETURN_DATA;
import static lugang.app.huansi.net.lugang.constant.Constant.MeasureCustomActivityConstant.STYLE_ID_INTENT;
import static lugang.app.huansi.net.lugang.constant.Constant.MeasureCustomActivityConstant.STYLE_ID_KEY;
import static lugang.app.huansi.net.lugang.constant.Constant.SEX;
import static lugang.app.huansi.net.lugang.constant.Constant.SPERSON;
import static lugang.app.huansi.net.util.LGSPUtils.USER_GUID;

/**
 * 量体定制详情页
 */

public class MeasureCustomActivity extends NotWebBaseActivity {
    private ActivityMeasureCustomBinding mActivityMeasureCustomBinding;
    private List<List<MeasureDataInSQLite>> mMeasureCustomLists;
    private List<List<MeasureRemarkDataInSQLite>> remarkAllList;//备注列表
    private LoadProgressDialog dialog;
    long mLastTime = 0;
    long mCurTime = 0;

    private String person = "";//被量体人
    private String orderId = "";//订单ID
    private int orderType = 0;//0待量体 1已量体 2返修
    private String sex = "男";//性别
    private String mAreaname;
    private String mCityname;
    private String mCountyname;
    private String mCustomername;
    private String mDepartmentname;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_measure_custom;
    }

    @Override
    public void init() {
        OthersUtil.hideInputFirst(this);//默认自动跳出软键盘
        mActivityMeasureCustomBinding = (ActivityMeasureCustomBinding) viewDataBinding;
        OthersUtil.registerEvent(this);
        dialog = new LoadProgressDialog(this);
        remarkAllList = new ArrayList<>();
        mMeasureCustomLists = new ArrayList<>();
        Intent intent = getIntent();
        person = intent.getStringExtra(SPERSON);
        sex = intent.getStringExtra(SEX);
        mAreaname = intent.getStringExtra(AREANAME);
        mCityname = intent.getStringExtra(CITYNAME);
        mCountyname = intent.getStringExtra(COUNTYNAME);
        mCustomername = intent.getStringExtra(CUSTOMERNAME);
        mDepartmentname = intent.getStringExtra(DEPARTMENTNAME);
//        if(TextUtils.isEmpty(sex)) sex="男";
        String departmentName = intent.getStringExtra(Constant.SDEPARTMENTNAME);
        orderId = intent.getStringExtra(Constant.ISDORDERMETERMSTID);//订单头表id
        orderType = intent.getIntExtra(Constant.IORDERTYPE, 0);
        if (orderType==2){
            mActivityMeasureCustomBinding.tvHeight.setVisibility(View.INVISIBLE);
            mActivityMeasureCustomBinding.tvWeight.setVisibility(View.INVISIBLE);
            mActivityMeasureCustomBinding.etWeight.setVisibility(View.INVISIBLE);
            mActivityMeasureCustomBinding.etHeight.setVisibility(View.INVISIBLE);
            mActivityMeasureCustomBinding.llBaseCloth.setVisibility(View.INVISIBLE);
            mActivityMeasureCustomBinding.btnSure.setVisibility(View.INVISIBLE);
        }
        mActivityMeasureCustomBinding.customName.setText(departmentName + ": " + person + "   性别:" + sex);
        mActivityMeasureCustomBinding.btnSaveMeasure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                clearAllInputFocus(v);
                new AlertDialog.Builder(MeasureCustomActivity.this)
                        .setMessage("是否确定要保存")
                        .setNegativeButton("返回", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("是的", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (NetUtil.isNetworkAvailable(getApplicationContext())) {
                                    submitMeasureData();
                                } else {
                                    saveToSQLite();
                                }
                            }
                        }).show();
//                AlertDialog.Builder builder = new AlertDialog.Builder(MeasureCustomActivity.this);
//                builder.setTitle("提示");
//                builder.setMessage("是否确定信息无误并同步到服务器");
//                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                    }
//                });
//                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        saveMeasure(userGUID, orderDtlId);//保存录入信息
//                    }
//                });
//                AlertDialog dialog = builder.create();
//                dialog.show();


//                new AlertDialog.Builder(MeasureCustomActivity.this)
//                        .setItems(new String[]{"保存数据", "本地保存"}, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                switch (which) {
//                                    case 0:
//
//                                        break;
//                                    case 1:
//
//                                        break;
//                                }
//                            }
//                        })
//                        .show();


            }
        });
//        mActivityMeasureCustomBinding.measureCustomTopLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                clearAllInputFocus(v);
//            }
//        });

        if (NetUtil.isNetworkAvailable(getApplicationContext())) {
            initDataFromInternet();
        } else {
            initDataFromSQLite();
        }
        //尺寸录入
        fillInMeasureDate();

    }

    /**
     * 尺寸录入
     */
    private void fillInMeasureDate() {
        mActivityMeasureCustomBinding.btnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLastTime = mCurTime;
                mCurTime = System.currentTimeMillis();
                if (mCurTime - mLastTime < 300) {//双击事件
                    mCurTime = 0;
                    mLastTime = 0;

                    //获得净胸围的值
                    String etChest = mActivityMeasureCustomBinding.etChest.getText().toString();
                    if (Integer.parseInt(etChest) % 2 != 0) {
                        etChest = String.valueOf(Integer.parseInt(etChest) + 1);
                    }
                    //获得净腰围的值
                    String etWaistline = mActivityMeasureCustomBinding.etWaistline.getText().toString();
                    //获得净臀围的值
                    String etHips = mActivityMeasureCustomBinding.etHips.getText().toString();
                    //获得春秋装衣长的值
                    String etClothLength = mActivityMeasureCustomBinding.etClothLength.getText().toString();

                    //获得春秋装肩宽的值
                    String etShoulder = mActivityMeasureCustomBinding.etShoulder.getText().toString();
                    //获得袖长的值
                    String etSleeve = mActivityMeasureCustomBinding.etSleeve.getText().toString();
                    //获得春秋裤长的值
                    String etKuchang = mActivityMeasureCustomBinding.etKuchang.getText().toString();
                    //获得春秋腰围的值
                    String etYaowei = mActivityMeasureCustomBinding.etYaowei.getText().toString();
                    //获得号的值
                    String etNO = mActivityMeasureCustomBinding.etNO.getText().toString();
                    //获得型的值
                    String etStyle = mActivityMeasureCustomBinding.etStyle.getText().toString();
                    if (TextUtils.isEmpty(etChest) || TextUtils.isEmpty(etHips) || TextUtils.isEmpty(etClothLength) ||
                            TextUtils.isEmpty(etShoulder) || TextUtils.isEmpty(etSleeve) || TextUtils.isEmpty(etKuchang) ||
                            TextUtils.isEmpty(etYaowei) || TextUtils.isEmpty(etNO) || TextUtils.isEmpty(etStyle)) {
                        OthersUtil.ToastMsg(MeasureCustomActivity.this, "请将输入信息填写完整");
                        return;
                    }
                    int clothLength = Integer.valueOf(etClothLength);
                    if (sex.equals("男")) {

                        if (clothLength % 2 == 0) {
                            clothLength = clothLength + 1;
                        }

                    } else if (sex.equals("女")) {
                        if (clothLength % 2 != 0) {
                            clothLength = clothLength + 1;
                        }
                    }
                    for (int i = 0; i < mMeasureCustomLists.size(); i++) {
                        List<MeasureDataInSQLite> measureDataInSQLiteList = mMeasureCustomLists.get(i);
                        //添加款式每条量体信息
                        for (int j = 0; j < measureDataInSQLiteList.size(); j++) {
                            MeasureDataInSQLite measureDataInSQLite = measureDataInSQLiteList.get(j);

                            if (measureDataInSQLite.getSdStyleTypeItemDtlId().equals("1072")) {//春秋上衣胸围
                                if (sex.equals("女"))
                                    measureDataInSQLite.setISMeterSize(String.valueOf(Integer.parseInt(String.valueOf(etChest)) + 8));
                                else
                                    measureDataInSQLite.setISMeterSize(String.valueOf(Integer.parseInt(String.valueOf(etChest)) + 14));

                            }
                            if (measureDataInSQLite.getSdStyleTypeItemDtlId().equals("1079")) {//大衣胸围
                                if (sex.equals("女")) {

                                    measureDataInSQLite.setISMeterSize(String.valueOf(Integer.parseInt(String.valueOf(etChest)) + 10));
                                } else
                                    measureDataInSQLite.setISMeterSize(String.valueOf(Integer.parseInt(String.valueOf(etChest)) + 16));

                            }
                            if (measureDataInSQLite.getSdStyleTypeItemDtlId().equals("1056")) {//夏装上衣胸围
                                measureDataInSQLite.setISMeterSize(String.valueOf(Integer.parseInt(String.valueOf(etChest)) + 6));
                            }
                            if (measureDataInSQLite.getSdStyleTypeItemDtlId().equals("1045")) {//冬装上衣胸围
                                measureDataInSQLite.setISMeterSize(String.valueOf(Integer.parseInt(String.valueOf(etChest)) + 10));
                            }
                            if (measureDataInSQLite.getSdStyleTypeItemDtlId().equals("1019")) {//夹克胸围
                                if (sex.equals("女"))
                                    measureDataInSQLite.setISMeterSize(String.valueOf(Integer.parseInt(String.valueOf(etChest)) + 8));
                                else
                                    measureDataInSQLite.setISMeterSize(String.valueOf(Integer.parseInt(String.valueOf(etChest)) + 18));
                            }
                            if (measureDataInSQLite.getSdStyleTypeItemDtlId().equals("1023")) {//长袖衬衫胸围
                                measureDataInSQLite.setISMeterSize(String.valueOf(Integer.parseInt(String.valueOf(etChest)) + 8));
                            }
                            if (measureDataInSQLite.getSdStyleTypeItemDtlId().equals("1030")) {//短袖衬衫胸围
                                measureDataInSQLite.setISMeterSize(String.valueOf(Integer.parseInt(String.valueOf(etChest)) + 8));
                            }
                            if (measureDataInSQLite.getSdStyleTypeItemDtlId().equals("1016")) {//马甲胸围
                                if (sex.equals("女")) {
                                    measureDataInSQLite.setISMeterSize(String.valueOf(Integer.parseInt(String.valueOf(etChest)) + 4));
                                } else
                                    measureDataInSQLite.setISMeterSize(String.valueOf(Integer.parseInt(String.valueOf(etChest)) + 10));
                            }
                            if (measureDataInSQLite.getSdStyleTypeItemDtlId().equals("1074")) {//春秋上衣中腰
                                if (sex.equals("女")) {
                                    measureDataInSQLite.setISMeterSize(String.valueOf(Integer.parseInt(String.valueOf(etChest)) - 5));
                                } else
                                    measureDataInSQLite.setISMeterSize(String.valueOf(Integer.parseInt(String.valueOf(etChest)) + 4));
                            }
                            if (measureDataInSQLite.getSdStyleTypeItemDtlId().equals("1058")) {//夏装上衣中腰
                                if (sex.equals("女")) {

                                    measureDataInSQLite.setISMeterSize(String.valueOf(Integer.parseInt(String.valueOf(etChest)) - 7));
                                } else
                                    measureDataInSQLite.setISMeterSize(String.valueOf(Integer.parseInt(String.valueOf(etChest)) + 2));
                            }
                            if (measureDataInSQLite.getSdStyleTypeItemDtlId().equals("1047")) {//冬装上衣中腰
                                if (sex.equals("女")) {

                                    measureDataInSQLite.setISMeterSize(String.valueOf(Integer.parseInt(String.valueOf(etChest)) - 3));
                                } else
                                    measureDataInSQLite.setISMeterSize(String.valueOf(Integer.parseInt(String.valueOf(etChest)) + 6));
                            }
                            if (measureDataInSQLite.getSdStyleTypeItemDtlId().equals("1040")) {//春秋裤子臀围
                                if (sex.equals("女")) {
                                    measureDataInSQLite.setISMeterSize(String.valueOf(Integer.parseInt(String.valueOf(etHips)) + 5));
                                } else
                                    measureDataInSQLite.setISMeterSize(String.valueOf(Integer.parseInt(String.valueOf(etHips)) + 12));
                            }
                            if (measureDataInSQLite.getSdStyleTypeItemDtlId().equals("1062")) {//夏装裤子臀围
                                if (sex.equals("女")) {

                                    measureDataInSQLite.setISMeterSize(String.valueOf(Integer.parseInt(String.valueOf(etHips)) + 3));
                                } else
                                    measureDataInSQLite.setISMeterSize(String.valueOf(Integer.parseInt(String.valueOf(etHips)) + 10));
                            }
                            if (measureDataInSQLite.getSdStyleTypeItemDtlId().equals("1051")) {//冬装裤子臀围
                                if (sex.equals("女")) {

                                    measureDataInSQLite.setISMeterSize(String.valueOf(Integer.parseInt(String.valueOf(etHips)) + 7));
                                } else
                                    measureDataInSQLite.setISMeterSize(String.valueOf(Integer.parseInt(String.valueOf(etHips)) + 14));
                            }
                            if (measureDataInSQLite.getSdStyleTypeItemDtlId().equals("1071")) {//女裙臀围
                                measureDataInSQLite.setISMeterSize(String.valueOf(Integer.parseInt(String.valueOf(etHips)) + 3));
                            }
                            if (measureDataInSQLite.getSdStyleTypeItemDtlId().equals("1001")) {//春秋上衣长
                                 measureDataInSQLite.setISMeterSize(String.valueOf(clothLength));
                            }
                            if (measureDataInSQLite.getSdStyleTypeItemDtlId().equals("1052")) {//夏装衣长
                                measureDataInSQLite.setISMeterSize(String.valueOf(clothLength - 2));
                            }
                            if (measureDataInSQLite.getSdStyleTypeItemDtlId().equals("1041")) {//冬装衣长
                                measureDataInSQLite.setISMeterSize(String.valueOf(clothLength - 2));
                            }
                            if (measureDataInSQLite.getSdStyleTypeItemDtlId().equals("1015")) {//马甲衣长
                                if (sex.equals("女"))
                                    measureDataInSQLite.setISMeterSize(String.valueOf(clothLength - 6));
                                else
                                    measureDataInSQLite.setISMeterSize(String.valueOf(clothLength - 12));
                            }
                            if (measureDataInSQLite.getSdStyleTypeItemDtlId().equals("1002")) {//春秋上衣肩宽
                                measureDataInSQLite.setISMeterSize(String.valueOf(Float.parseFloat(String.valueOf(etShoulder))));
                            }
                            if (measureDataInSQLite.getSdStyleTypeItemDtlId().equals("1052")) {//夏衣肩宽
                                measureDataInSQLite.setISMeterSize(String.valueOf((Float.parseFloat(String.valueOf(etShoulder)) - 0.5)));
                            }
                            if (measureDataInSQLite.getSdStyleTypeItemDtlId().equals("1042")) {//冬衣肩宽
                                measureDataInSQLite.setISMeterSize(String.valueOf((Float.parseFloat(String.valueOf(etShoulder)) + 0.5)));
                            }
                            if (measureDataInSQLite.getSdStyleTypeItemDtlId().equals("1007")) {//大衣肩宽
                                measureDataInSQLite.setISMeterSize(String.valueOf((Float.parseFloat(String.valueOf(etShoulder)) + 0.5)));
                            }
                            if (measureDataInSQLite.getSdStyleTypeItemDtlId().equals("1003") ||
                                    measureDataInSQLite.getSdStyleTypeItemDtlId().equals("1054") ||
                                    measureDataInSQLite.getSdStyleTypeItemDtlId().equals("1043")) {//春秋衣袖长,夏装袖长，冬装袖长
                                measureDataInSQLite.setISMeterSize(String.valueOf((Float.parseFloat(String.valueOf(etSleeve)))));
                            }
                            if (measureDataInSQLite.getSdStyleTypeItemDtlId().equals("1008")) {//大衣袖长
                                measureDataInSQLite.setISMeterSize(String.valueOf((Float.parseFloat(String.valueOf(etSleeve)) + 1)));
                            }
                            if (measureDataInSQLite.getSdStyleTypeItemDtlId().equals("1035") ||
                                    measureDataInSQLite.getSdStyleTypeItemDtlId().equals("1059") ||
                                    measureDataInSQLite.getSdStyleTypeItemDtlId().equals("1048")) {//春秋裤长,夏裤长，冬裤长
                                measureDataInSQLite.setISMeterSize(String.valueOf(Integer.parseInt(String.valueOf(etKuchang))));
                            }
                            if (measureDataInSQLite.getSdStyleTypeItemDtlId().equals("1036")) {//春秋裤腰围
                                measureDataInSQLite.setISMeterSize(String.valueOf(Integer.parseInt(String.valueOf(etYaowei))));
                            }
                            if (measureDataInSQLite.getSdStyleTypeItemDtlId().equals("1060")) {//夏裤腰围
                                measureDataInSQLite.setISMeterSize(String.valueOf(Integer.parseInt(String.valueOf(etYaowei)) - 1));
                            }
                            if (measureDataInSQLite.getSdStyleTypeItemDtlId().equals("1049")) {//冬裤腰围
                                measureDataInSQLite.setISMeterSize(String.valueOf(Integer.parseInt(String.valueOf(etYaowei)) + 1));
                            }
                            if (measureDataInSQLite.getSdStyleTypeItemDtlId().equals("1088") ||
                                    measureDataInSQLite.getSdStyleTypeItemDtlId().equals("1090") ||
                                    measureDataInSQLite.getSdStyleTypeItemDtlId().equals("1092") ||
                                    measureDataInSQLite.getSdStyleTypeItemDtlId().equals("1094") ||
                                    measureDataInSQLite.getSdStyleTypeItemDtlId().equals("1096") ||
                                    measureDataInSQLite.getSdStyleTypeItemDtlId().equals("1084") ||
                                    measureDataInSQLite.getSdStyleTypeItemDtlId().equals("1100") ||
                                    measureDataInSQLite.getSdStyleTypeItemDtlId().equals("1104") ||
                                    measureDataInSQLite.getSdStyleTypeItemDtlId().equals("1110")
                                    ) {//春秋装号 大衣 夹克 马甲 长袖衬衫 短袖衬衫 冬装上衣 夏装上衣 女裙
                                measureDataInSQLite.setISMeterSize(String.valueOf(Integer.parseInt(String.valueOf(etNO))));
                            }
                            if (measureDataInSQLite.getSdStyleTypeItemDtlId().equals("1089") ||
                                    measureDataInSQLite.getSdStyleTypeItemDtlId().equals("1091") ||
                                    measureDataInSQLite.getSdStyleTypeItemDtlId().equals("1093") ||
                                    measureDataInSQLite.getSdStyleTypeItemDtlId().equals("1095") ||
                                    measureDataInSQLite.getSdStyleTypeItemDtlId().equals("1097") ||
                                    measureDataInSQLite.getSdStyleTypeItemDtlId().equals("1085") ||
                                    measureDataInSQLite.getSdStyleTypeItemDtlId().equals("1101") ||
                                    measureDataInSQLite.getSdStyleTypeItemDtlId().equals("1105") ||
                                    measureDataInSQLite.getSdStyleTypeItemDtlId().equals("1111")
                                    ) {//春秋装型 大衣 夹克 马甲 长袖衬衫 短袖衬衫 冬装上衣 夏装上衣 女裙
                                measureDataInSQLite.setISMeterSize(String.valueOf(Integer.parseInt(String.valueOf(etStyle))));
                            }


                        }
                    }
                    showData();
                }
            }
        });


    }

    //    public void init() {
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);//默认自动跳出软键盘
//        mActivityMeasureCustomBinding = (ActivityMeasureCustomBinding) viewDataBinding;
//        OthersUtil.registerEvent(this);
//        dialog=new LoadProgressDialog(this);
//        remarkAllList=new ArrayList<>();
//        mMeasureCustomLists=new ArrayList<>();
//        Intent intent = getIntent();
//        person= intent.getStringExtra(Constant.SPERSON);
//        String departmentName = intent.getStringExtra(Constant.SDEPARTMENTNAME);
//        orderDtlId = intent.getStringExtra(Constant.ISDORDERMETERMSTID);//订单id
//        mActivityMeasureCustomBinding.customName.setText(departmentName+": " + person);
//        mActivityMeasureCustomBinding.btnSaveMeasure.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                AlertDialog.Builder builder = new AlertDialog.Builder(MeasureCustomActivity.this);
////                builder.setTitle("提示");
////                builder.setMessage("是否确定信息无误并同步到服务器");
////                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
////                    @Override
////                    public void onClick(DialogInterface dialog, int which) {
////                    }
////                });
////                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
////                    @Override
////                    public void onClick(DialogInterface dialog, int which) {
////                        saveMeasure(userGUID, orderDtlId);//保存录入信息
////                    }
////                });
////                AlertDialog dialog = builder.create();
////                dialog.show();
//
//                new AlertDialog.Builder(MeasureCustomActivity.this)
//                        .setItems(new String[]{"保存数据","本地保存"}, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                switch (which){
//                                    case 0:
//                                        submitMeasureData();
//                                        break;
//                                    case 1:
//                                        saveToSQLite();
//                                        break;
//                                }
//                            }
//                        })
//                        .show();
//
//
//            }
//        });
//        initData(orderDtlId);
//    }
//

    /**
     * 保存到本地数据库中
     */
    private void saveToSQLite() {
        OthersUtil.showLoadDialog(dialog);
        //查询输入框的数据，并保存到数组中
        for (int i = 0; i < mMeasureCustomLists.size(); i++) {
            List<MeasureDataInSQLite> subList = mMeasureCustomLists.get(i);
            View item = mActivityMeasureCustomBinding.llCloth.getChildAt(i);
            LinearLayout linearLayout = (LinearLayout) item.findViewById(R.id.llClothTypeList);
            for (int j = 0; j < subList.size(); j++) {
                MeasureDataInSQLite measureDataInSQLite = subList.get(j);
                View subItem = linearLayout.getChildAt(j);
                EditText editText = (EditText) subItem.findViewById(R.id.etParameter);
                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });
                String size = editText.getText().toString().trim();
                if (size.isEmpty()) size = "0";
                measureDataInSQLite.setISMeterSize(size);
                subList.set(j, measureDataInSQLite);
            }
            mMeasureCustomLists.set(i, subList);
        }

        NewRxjavaWebUtils.getUIThread(NewRxjavaWebUtils.getObservable(this, "")
                .map(new Func1<String, HsWebInfo>() {
                    @Override
                    public HsWebInfo call(String s) {
                        DaoSession daoSession = GreenDaoUtil.getGreenDaoSession(getApplicationContext());
                        String userGUID = LGSPUtils.getLocalData(getApplicationContext(), USER_GUID, String.class.getName(), "").toString();
                        //判断这个里面是否写过数据 如果没有输入值或者添加过备注，则为待量体 否则为已量体 返修的单独算
                        int saveType = 0;//0待量体 1已量体 2返修
                        if (orderType == 2) saveType = 2;
                        else {
                            for (List<MeasureDataInSQLite> subList : mMeasureCustomLists) {
                                if (subList.isEmpty()) continue;
                                for (MeasureDataInSQLite measureDataInSQLite : subList) {
                                    String size = measureDataInSQLite.getISMeterSize();
                                    if (size == null || size.isEmpty() || size.equalsIgnoreCase("0"))
                                        continue;
                                    saveType = 1;
                                    break;
                                }
                                if (saveType == 1) break;
                            }
                            if (saveType != 1) {
                                for (List<MeasureRemarkDataInSQLite> subList : remarkAllList) {
                                    if (subList.isEmpty()) continue;
                                    saveType = 1;
                                    break;
                                }
                            }
                        }

                        //先保存量体数据
                        MeasureDataInSQLiteDao measureDataInSQLiteDao = daoSession.getMeasureDataInSQLiteDao();

                        for (int i = 0; i < mMeasureCustomLists.size(); i++) {
                            List<MeasureDataInSQLite> subList = mMeasureCustomLists.get(i);
                            if (subList.isEmpty()) continue;
                            for (int j = 0; j < subList.size(); j++) {
                                MeasureDataInSQLite measureDataInSQLite = subList.get(j);
                                measureDataInSQLite.setType(saveType);
                                subList.set(j, measureDataInSQLite);
                            }
                            measureDataInSQLiteDao.insertOrReplaceInTx(subList);
                        }

                        //其次保存备注数据
                        MeasureRemarkDataInSQLiteDao measureRemarkDataInSQLiteDao = daoSession.getMeasureRemarkDataInSQLiteDao();
                        List<MeasureRemarkDataInSQLite> beforeRemarkDataInList = null;
                        try {
                            beforeRemarkDataInList = measureRemarkDataInSQLiteDao.queryBuilder()
                                    .where(MeasureRemarkDataInSQLiteDao.Properties.Type.eq(orderType))
                                    .where(MeasureRemarkDataInSQLiteDao.Properties.UserGUID.eq(userGUID))
                                    .where(MeasureRemarkDataInSQLiteDao.Properties.Person.eq(person))
                                    .where(MeasureRemarkDataInSQLiteDao.Properties.OrderId.eq(orderId))
                                    .list();
                        } catch (Exception e) {
                        }
                        if (beforeRemarkDataInList == null)
                            beforeRemarkDataInList = new ArrayList<>();
                        measureRemarkDataInSQLiteDao.deleteInTx(beforeRemarkDataInList);

                        for (List<MeasureRemarkDataInSQLite> subList : remarkAllList) {
                            if (subList.isEmpty()) continue;
                            for (int j = 0; j < subList.size(); j++) {
                                MeasureRemarkDataInSQLite measureRemarkDataInSQLite = subList.get(j);
                                measureRemarkDataInSQLite.setType(saveType);
                                measureRemarkDataInSQLite.setId(null);
                                subList.set(j, measureRemarkDataInSQLite);
                            }
                            measureRemarkDataInSQLiteDao.insertOrReplaceInTx(subList);
                        }
                        //说明已量体->待量体 or 待量体->已量体 更改之前界面的状态
                        if (saveType != orderType) {
                            MeasureOrderInSQLite measureOrderInSQLite = null;
                            MeasureOrderInSQLiteDao measureOrderInSQLiteDao = daoSession.getMeasureOrderInSQLiteDao();

                            try {
                                List<MeasureOrderInSQLite> measureOrderInSQLiteList = measureOrderInSQLiteDao
                                        .queryBuilder()
                                        .where(MeasureOrderInSQLiteDao.Properties.OrderType.eq(orderType))
                                        .where(MeasureOrderInSQLiteDao.Properties.UserGUID.eq(userGUID))
                                        .where(MeasureOrderInSQLiteDao.Properties.ISdOrderMeterMstId.eq(orderId))
                                        .where(MeasureOrderInSQLiteDao.Properties.SPerson.eq(person))
                                        .list();
                                measureOrderInSQLite = measureOrderInSQLiteList.get(0);
                            } catch (Exception e) {

                            }
                            if (measureOrderInSQLite != null) {
                                measureOrderInSQLite.setOrderType(saveType);
                                measureOrderInSQLiteDao.insertOrReplace(measureOrderInSQLite);
                            }
                        }
                        return new HsWebInfo();
                    }
                }), getApplicationContext(), dialog, new SimpleHsWeb() {
            @Override
            public void success(HsWebInfo hsWebInfo) {
                OthersUtil.ToastMsg(getApplicationContext(), "保存成功");
                finish();
            }

            @Override
            public void error(HsWebInfo hsWebInfo, Context context) {
                OthersUtil.ToastMsg(getApplicationContext(), "保存失败");
            }
        });
    }


    //保存录入信息
    private void submitMeasureData() {
        //获得身高
        final String etHeight = mActivityMeasureCustomBinding.etHeight.getText().toString();
        //获得体重
        final String etWeight = mActivityMeasureCustomBinding.etWeight.getText().toString();
        //获得净胸围的值
        final String etChest = mActivityMeasureCustomBinding.etChest.getText().toString();
        //获得净腰围的值
        final String etWaistline = mActivityMeasureCustomBinding.etWaistline.getText().toString();
        //获得净臀围的值
        final String etHips = mActivityMeasureCustomBinding.etHips.getText().toString();
        //获得春秋装衣长的值
        final String etClothLength = mActivityMeasureCustomBinding.etClothLength.getText().toString();
        //获得春秋装肩宽的值
        final String etShoulder = mActivityMeasureCustomBinding.etShoulder.getText().toString();
        //获得袖长的值
        final String etSleeve = mActivityMeasureCustomBinding.etSleeve.getText().toString();
        //获得春秋裤长的值
        final String etKuchang = mActivityMeasureCustomBinding.etKuchang.getText().toString();
        //获得春秋腰围的值
        final String etYaowei = mActivityMeasureCustomBinding.etYaowei.getText().toString();
        //获得号的值
        final String etNO = mActivityMeasureCustomBinding.etNO.getText().toString();
        //获得型的值
        final String etStyle = mActivityMeasureCustomBinding.etStyle.getText().toString();
        OthersUtil.showLoadDialog(mDialog);
        //查询输入框的数据，并保存到数组中
        for (int i = 0; i < mMeasureCustomLists.size(); i++) {
            List<MeasureDataInSQLite> subList = mMeasureCustomLists.get(i);
            View item = mActivityMeasureCustomBinding.llCloth.getChildAt(i);
            LinearLayout linearLayout = (LinearLayout) item.findViewById(R.id.llClothTypeList);
            for (int j = 0; j < subList.size(); j++) {
                MeasureDataInSQLite measureDataInSQLite = subList.get(j);
                View subItem = linearLayout.getChildAt(j);
                EditText editText = (EditText) subItem.findViewById(R.id.etParameter);
                TextView tvParameterMeasured = (TextView) subItem.findViewById(R.id.tvParameterMeasured);
                String size = editText.getText().toString().trim();
                String trim = tvParameterMeasured.getText().toString().trim();
                if (orderType!=2){
                    if (size.isEmpty()) size = "0";
                    measureDataInSQLite.setISMeterSize(size);
                }else {
                    measureDataInSQLite.setISMeterSize(trim);
                }

                if (orderType==2&&measureDataInSQLite.getBrepair().equals("True")) measureDataInSQLite.setIrepair(size);

                subList.set(j, measureDataInSQLite);
            }
            mMeasureCustomLists.set(i, subList);
        }
        NewRxjavaWebUtils.getUIThread(NewRxjavaWebUtils.getObservable(this, "")
                        .map(new Func1<String, HsWebInfo>() {
                            @Override
                            public HsWebInfo call(String s) {
                                String userGUID = LGSPUtils.getLocalData(getApplicationContext(), USER_GUID, String.class.getName(), "").toString();
                                StringBuilder sbStr = new StringBuilder();
                                for (int i = 0; i < mMeasureCustomLists.size(); i++) {
                                    List<MeasureDataInSQLite> subList = mMeasureCustomLists.get(i);
                                    for (int j = 0; j < subList.size(); j++) {
                                        MeasureDataInSQLite measureDataInSQLite = subList.get(j);
                                        String brepair = measureDataInSQLite.getBrepair();
                                        sbStr.append("EXEC spappMeasureSaveMeasureData ")
                                                .append("@uHrEmployeeGUID='").append(userGUID).append("'")
                                                .append(",@isdOrderMeterDtlid=").append(measureDataInSQLite.getISdOrderMeterDtlId())
                                                .append(",@isMeterSize=").append(brepair.equals("True")&&orderType==2?measureDataInSQLite.getIrepair():measureDataInSQLite.getISMeterSize())
                                                .append(",@isdStyleTypeItemDtlid=").append(measureDataInSQLite.getSdStyleTypeItemDtlId())
                                                .append(",@bUpdated=").append(measureDataInSQLite.getBupdated())
                                                .append(";");
                                        sbStr.append("EXEC spappMeasureSaveBWHData ")
                                                .append("@iOrderDtlId=").append(measureDataInSQLite.getISdOrderMeterDtlId())
                                                .append(",@iHeight=").append(etHeight)
                                                .append(",@iWeight=").append(etWeight)
                                                .append(",@iPureChest=").append(etChest)
                                                .append(",@iPureWaist=").append(etWaistline)
                                                .append(",@iPureHips=").append(etHips)
                                                .append(",@iClothLenth=").append(etClothLength)
                                                .append(",@sShouderWidth=").append(etShoulder)
                                                .append(",@sSleeveLenth=").append(etSleeve)
                                                .append(",@iTrousersLenth=").append(etKuchang)
                                                .append(",@iWaistLenth=").append(etYaowei)
                                                .append(",@iHaoNo=").append(etNO)
                                                .append(",@iXingNo=").append(etStyle)
                                                .append("; ");
                                    }
                                }

                                for (int i = 0; i < remarkAllList.size(); i++) {
                                    StringBuilder sbRemarkId = new StringBuilder();
                                    List<MeasureRemarkDataInSQLite> subList = remarkAllList.get(i);
                                    if (subList == null) subList = new ArrayList<>();
                                    String orderDtlId = "";
                                    for (int j = 0; j < subList.size(); j++) {
                                        MeasureRemarkDataInSQLite measureRemarkDataInSQLite = subList.get(j);
                                        orderDtlId = measureRemarkDataInSQLite.getIOrderDtlId();
                                        sbRemarkId.append(measureRemarkDataInSQLite.getIId());
                                        if (j != subList.size() - 1) sbRemarkId.append("@");
                                    }
                                    if (!orderDtlId.isEmpty()) {
                                        sbStr.append("EXEC spappMeasureSaveMeasureRemark ")
                                                .append("@sSdMeterMarkDtlid='").append(sbRemarkId.toString()).append("'")
                                                .append(",@isdOrderMeterDtlid=").append(orderDtlId)
                                                .append("; ");

                                    }
                                }
                                return getJsonData(getApplicationContext(), CUS_SERVICE,
                                        sbStr.toString(), "", MeasureCustomBean.class.getName(), true,
                                        "保存失败！！");
                            }
                        })
                , this, mDialog, new SimpleHsWeb() {
                    @Override
                    public void success(HsWebInfo hsWebInfo) {
                        OthersUtil.ToastMsg(MeasureCustomActivity.this, "保存成功！！");
                        finish();
                    }
                });
    }


    /**
     * 从SQLite中初始化数据
     */
    @SuppressWarnings("unchecked")
    private void initDataFromSQLite() {
        mMeasureCustomLists.clear();
        remarkAllList.clear();
        NewRxjavaWebUtils.getUIThread(NewRxjavaWebUtils.getObservable(this, "")
                .map(new Func1<String, HsWebInfo>() {
                    @Override
                    public HsWebInfo call(String s) {
                        String userGUID = LGSPUtils.getLocalData(getApplicationContext(), USER_GUID, String.class.getName(), "").toString();
                        DaoSession daoSession = GreenDaoUtil.getGreenDaoSession(getApplicationContext());
                        //先从数据库中获取量体数据
                        MeasureDataInSQLiteDao measureDataInSQLiteDao = daoSession.getMeasureDataInSQLiteDao();
                        List<MeasureDataInSQLite> measureDataInSQLiteList = measureDataInSQLiteDao.queryBuilder()
                                .where(MeasureDataInSQLiteDao.Properties.Person.eq(person))
                                .where(MeasureDataInSQLiteDao.Properties.OrderId.eq(orderId))
                                .where(MeasureDataInSQLiteDao.Properties.UserGUID.eq(userGUID))
                                .where(MeasureDataInSQLiteDao.Properties.Type.eq(orderType))
                                .list();
                        if (measureDataInSQLiteList == null)
                            measureDataInSQLiteList = new ArrayList<>();

                        MeasureRemarkDataInSQLiteDao measureRemarkDataInSQLiteDao = daoSession.getMeasureRemarkDataInSQLiteDao();
                        List<MeasureRemarkDataInSQLite> measureRemarkDataInSQLiteList = measureRemarkDataInSQLiteDao.queryBuilder()
                                .where(MeasureRemarkDataInSQLiteDao.Properties.Person.eq(person))
                                .where(MeasureRemarkDataInSQLiteDao.Properties.OrderId.eq(orderId))
                                .where(MeasureRemarkDataInSQLiteDao.Properties.UserGUID.eq(userGUID))
                                .where(MeasureRemarkDataInSQLiteDao.Properties.Type.eq(orderType))
                                .list();
                        if (measureRemarkDataInSQLiteList == null)
                            measureRemarkDataInSQLiteList = new ArrayList<>();
                        HsWebInfo info = new HsWebInfo();
                        Map<String, Object> map = new HashMap<>();
                        map.put("measureDataInSQLiteList", measureDataInSQLiteList);
                        map.put("measureRemarkDataInSQLiteList", measureRemarkDataInSQLiteList);
                        info.object = map;
                        return info;
                    }
                }), getApplicationContext(), dialog, new SimpleHsWeb() {
            @Override
            public void success(HsWebInfo hsWebInfo) {
                Map<String, Object> map = (Map<String, Object>) hsWebInfo.object;
                //量体数据
                List<MeasureDataInSQLite> measureDataInSQLiteList = (List<MeasureDataInSQLite>) map.get("measureDataInSQLiteList");
                //备注数据
                List<MeasureRemarkDataInSQLite> measureRemarkDataInSQLiteList = (List<MeasureRemarkDataInSQLite>) map.get("measureRemarkDataInSQLiteList");

                //整理量体数据
                Map<String, List<MeasureDataInSQLite>> filterMap = new HashMap<>();
                for (MeasureDataInSQLite measureDataInSQLite : measureDataInSQLiteList) {
                    String key = measureDataInSQLite.getISdStyleTypeMstId();
                    List<MeasureDataInSQLite> subList = filterMap.get(key);
                    if (subList == null) subList = new ArrayList<>();
                    subList.add(measureDataInSQLite);
                    filterMap.put(key, subList);
                }
                Iterator<Entry<String, List<MeasureDataInSQLite>>> itData = filterMap.entrySet().iterator();
                while (itData.hasNext()) {
                    Entry<String, List<MeasureDataInSQLite>> entry = itData.next();
                    mMeasureCustomLists.add(entry.getValue());
                }

                Map<String, List<MeasureRemarkDataInSQLite>> remarkMap = new HashMap<>();
                for (MeasureRemarkDataInSQLite measureRemarkDataInSQLite : measureRemarkDataInSQLiteList) {
                    List<MeasureRemarkDataInSQLite> subList = remarkMap.get(measureRemarkDataInSQLite.getStyleId());
                    if (subList == null) subList = new ArrayList<>();
                    subList.add(measureRemarkDataInSQLite);
                    remarkMap.put(measureRemarkDataInSQLite.getStyleId(), subList);
                }


                //整理备注数据
                for (List<MeasureDataInSQLite> subList : mMeasureCustomLists) {
                    String styleId = subList.get(0).getISdStyleTypeMstId();
                    List<MeasureRemarkDataInSQLite> remarkSubList = remarkMap.get(styleId);
                    if (remarkSubList == null) remarkSubList = new ArrayList<>();
                    remarkAllList.add(remarkSubList);
                }
                showData();
            }
        });

    }

    /**
     * 进行对数据的显示作用
     */
    private void showData() {
        mActivityMeasureCustomBinding.llCloth.removeAllViews();
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        //添加View 即每个款式
        for (int i = 0; i < mMeasureCustomLists.size(); i++) {
            View view = layoutInflater.inflate(R.layout.activity_measure_detial, null);
            TextView tvClothStyle = (TextView) view.findViewById(R.id.tvClothStyle);
            tvClothStyle.setText(mMeasureCustomLists.get(i).get(0).getSValueGroup());

            List<MeasureDataInSQLite> measureDataInSQLiteList = mMeasureCustomLists.get(i);
            //添加当前款式每条量体信息
            LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.llClothTypeList);
//            LinearLayout llClothTypeDetail= (LinearLayout) view.findViewById(R.id.llClothTypeDetail);
//            CardView cvMeasureDetail= (CardView) view.findViewById(R.id.cvMeasureDetail);
//            ScrollView svClothTypeDetail= (ScrollView) view.findViewById(R.id.svClothTypeDetail);

//            svClothTypeDetail.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    clearAllInputFocus(v);
//                }
//            });

//            linearLayout.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    clearAllInputFocus(v);
//                }
//            });
//            view.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    clearAllInputFocus(v);
//                }
//            });
//            cvMeasureDetail.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    clearAllInputFocus(v);
//                }
//            });
//            llClothTypeDetail.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    clearAllInputFocus(v);
//                }
//            });

            for (final MeasureDataInSQLite measureDataInSQLite : measureDataInSQLiteList) {

                View convertView = layoutInflater.inflate(R.layout.ll_parameter, null);
                TextView tvParameter = (TextView) convertView.findViewById(R.id.tvParameter);
                TextView tvParameterMeasured = (TextView) convertView.findViewById(R.id.tvParameterMeasured);
                if (orderType==2){
                    tvParameterMeasured.setVisibility(View.VISIBLE);
                }else {
                    tvParameterMeasured.setVisibility(View.GONE);
                }
                if (sex.equals("男")) {
                    if (mMeasureCustomLists.get(i).get(0).getSValueGroup().equals("长袖衬衫")
                            || mMeasureCustomLists.get(i).get(0).getSValueGroup().equals("短袖衬衫")) {
                        if (measureDataInSQLite.getSMeterName().equals("领围") || measureDataInSQLite.getSMeterName().equals("号") || measureDataInSQLite.getSMeterName().equals("型")) {
                            tvParameter.setText(measureDataInSQLite.getSMeterName());
                        } else {
                            convertView.setVisibility(View.GONE);
                        }
                    } else {
                        tvParameter.setText(measureDataInSQLite.getSMeterName());
                    }
                } else {
                    tvParameter.setText(measureDataInSQLite.getSMeterName());
                }
//                tvParameter.setText(measureDataInSQLite.getSMeterName());

//                MeasureDateBean measureDateBean=measureDataMap.get(measureCustom.SDSTYLETYPEITEMDTLID+"_"+measureCustom.ISDORDERMETERDTLID);
//                measureCustom.ISMETERSIZE=measureDateBean==null?"":measureDateBean.ISMETERSIZE;
                final CustomKeyboardEditText editText = (CustomKeyboardEditText) convertView.findViewById(R.id.etParameter);

                int evenNo = -1;//奇偶数  1奇数 0偶数 -1不限制
                try {
                    evenNo = Boolean.parseBoolean(measureDataInSQLite.getBEvenNo().toLowerCase()) ? 1 : 0;
                } catch (Exception e) {
                    e.printStackTrace();
                }

                boolean isCanPoint = false;//true 支持小数 false不支持

                try {
                    isCanPoint = Boolean.parseBoolean(measureDataInSQLite.getBPoint().toLowerCase());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (!isCanPoint) {
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER);

                } else {
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
//

                }
                float minLength = -1;//最小的尺寸
                try {
                    switch (sex) {
                        case "女":
                        default:
                            minLength = Float.parseFloat(measureDataInSQLite.getSFemaleMinLenth());
                            break;
                        case "男":
                            minLength = Float.parseFloat(measureDataInSQLite.getSMaleMinLenth());
                            break;

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                float maxLength = -1; //最大的尺寸
                try {
                    switch (sex) {
                        case "女":
                        default:
                            maxLength = Float.parseFloat(measureDataInSQLite.getSFemaleMaxLenth());
                            break;
                        case "男":
                            maxLength = Float.parseFloat(measureDataInSQLite.getSMaleMaxLenth());
                            break;

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                valueList = virtualKeyboardView.getValueList();
                //不允许最大值小于最小值
                if (maxLength < minLength) maxLength = -1;
                final float finalMaxLength = maxLength;
                final float finalMinLength = minLength;
                final boolean finalIsCanPoint = isCanPoint;
                if (measureDataInSQLite.getBupdated().equalsIgnoreCase("True")) {
                    editText.setTextColor(Color.RED);
                    tvParameterMeasured.setTextColor(Color.RED);
                } else {

                    editText.setTextColor(Color.WHITE);
                    tvParameterMeasured.setTextColor(Color.WHITE);
                }
                if (sex.equals("男")) {
                    if (mMeasureCustomLists.get(i).get(0).getSValueGroup().equals("长袖衬衫")
                            || mMeasureCustomLists.get(i).get(0).getSValueGroup().equals("短袖衬衫")) {
                        if (measureDataInSQLite.getSMeterName().equals("领围") || measureDataInSQLite.getSMeterName().equals("号") || measureDataInSQLite.getSMeterName().equals("型")) {
                            editText.setText(orderType==2&&measureDataInSQLite.getBupdated().equals("True")?measureDataInSQLite.getIrepair():measureDataInSQLite.getISMeterSize());
                            tvParameterMeasured.setText(measureDataInSQLite.getISMeterSize());
                        }
                    } else {
                        editText.setText(orderType==2&&measureDataInSQLite.getBupdated().equals("True")?measureDataInSQLite.getIrepair():measureDataInSQLite.getISMeterSize());
                        tvParameterMeasured.setText(measureDataInSQLite.getISMeterSize());
                    }
                } else {

                    editText.setText(orderType==2&&measureDataInSQLite.getBupdated().equals("True")?measureDataInSQLite.getIrepair():measureDataInSQLite.getISMeterSize());
                    tvParameterMeasured.setText(measureDataInSQLite.getISMeterSize());

                }
                //将服务器返回的带小数的字符数转化成int展示
                if (measureDataInSQLite.getSMeterName().equals("肩宽") || measureDataInSQLite.getSMeterName().equals("袖长")) {
                    editText.setText(orderType==2&&measureDataInSQLite.getBupdated().equals("True")?measureDataInSQLite.getIrepair():measureDataInSQLite.getISMeterSize());
                    tvParameterMeasured.setText(measureDataInSQLite.getISMeterSize());

                } else {
                    if (!TextUtils.isEmpty(measureDataInSQLite.getISMeterSize())) {
                        editText.setText(orderType==2&&measureDataInSQLite.getBupdated().equals("True")?measureDataInSQLite.getIrepair():String.valueOf((int) Double.parseDouble(measureDataInSQLite.getISMeterSize())));
                        tvParameterMeasured.setText(String.valueOf((int) Double.parseDouble(measureDataInSQLite.getISMeterSize())));
                    }

                }
                final String beforeChanged = editText.getText().toString();


                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (s.toString().equals(beforeChanged) || TextUtils.isEmpty(beforeChanged)) {

                            editText.setTextColor(Color.WHITE);
                            measureDataInSQLite.setBupdated("False");
                        } else {

                            editText.setTextColor(Color.RED);
                            measureDataInSQLite.setBupdated("True");

                        }
                        if (orderType==2){
                            measureDataInSQLite.setBrepair("True");
                        }


                    }
                });
                final int finalEvenNo = evenNo;
                editText.setOnEditFocusListener(new OnEditFocusListener() {
                    @Override
                    public void onFocus(View v, boolean hasFocus) {
                        if (hasFocus) return;
                        float number = 0;
                        try {
                            number = Float.parseFloat(((EditText) v).getText().toString().trim());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //最小值限制
                        if (finalMinLength > 0 && number < finalMinLength) {
                            ((EditText) v).setText("");
                            OthersUtil.ToastMsg(MeasureCustomActivity.this, "尺寸已经小于该测量项的最小值" + finalMinLength + ",请确认");
                        }
                        //最大值限制
                        if (finalMaxLength > 0 && number > finalMaxLength) {
                            ((EditText) v).setText("");
                            OthersUtil.ToastMsg(MeasureCustomActivity.this, "尺寸已经大于该测量项的最大值" + finalMaxLength + ",请确认");
                        }

//                        ((EditText) v).setText(String.valueOf(finalIsCanPoint?number:(int)number));
                        if (!finalIsCanPoint) {


                            //奇偶数
                            switch (finalEvenNo) {

//                            //奇数
//                            case 1:
//                                if (sex.equals("男")){
//
//                                }
//                                if ((int) number % 2 == 0) number = number + 1;
//                                break;
                                //偶数
                                case 0:
                                    if (sex.equals("男")) {
                                        if (measureDataInSQLite.getSMeterName().equals("胸围") || measureDataInSQLite.getSMeterName().equals("臀围")) {
                                            if ((int) number % 2 != 0) number = number + 1;
                                        } else {

                                            if ((int) number % 2 == 0) number = number + 1;
                                        }
                                    } else {

                                        if (measureDataInSQLite.getSMeterName().equals("胸围")) {
                                            if ((int) number % 2 != 0) number = number + 1;
                                        } else {

                                            if ((int) number % 2 != 0) number = number + 1;
                                        }
                                    }
                                    break;

                            }
                        }
                        if (number < 0) number = 0;
                        if (number == 0) ((EditText) v).setText("");

                        else if (finalIsCanPoint) {

                            String[] split = String.valueOf(number).split("\\.");
                            char c = split[1].charAt(0);
                            if (Float.parseFloat(String.valueOf(c)) > 3 && Float.parseFloat(String.valueOf(c)) < 7) {
                                number = Float.parseFloat(split[0] + ".5");
                            } else if (Float.parseFloat(String.valueOf(c)) < 4) {
                                number = Float.parseFloat(split[0] + ".0");

                            } else {
                                number = Float.parseFloat((Integer.parseInt(split[0]) + 1) + ".0");
                            }
                            ((EditText) v).setText(String.valueOf(number));
                        } else ((EditText) v).setText(String.valueOf((int) number));
                    }
                });


                linearLayout.addView(convertView);
//            }


            }
            mActivityMeasureCustomBinding.llCloth.setGravity(Gravity.CENTER_HORIZONTAL);
            WindowManager wm = getWindowManager();
            int width = wm.getDefaultDisplay().getWidth();
            int height = wm.getDefaultDisplay().getHeight();
            LinearLayout remarkLayout = (LinearLayout) view.findViewById(R.id.remarkLayout);

            //跳转到备注界面
            final int finalI = i;
            remarkLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MeasureCustomActivity.this, RemarkDetailActivity.class);
                    intent.putExtra(STYLE_ID_INTENT, mMeasureCustomLists.get(finalI).get(0).getISdStyleTypeMstId());
                    intent.putExtra(ORDER_DTL_ID_INTENT, mMeasureCustomLists.get(finalI).get(0).getISdOrderMeterDtlId());
                    intent.putExtra(REMARK_INTENT_DATA, (Serializable) remarkAllList.get(finalI));
                    startActivity(intent);
                }
            });

            final CheckBox cbRemark = (CheckBox) view.findViewById(R.id.cbRemark);
            try {
                cbRemark.setChecked(remarkAllList.get(finalI) != null && !remarkAllList.get(finalI).isEmpty());
            } catch (Exception e) {
                cbRemark.setChecked(false);
            }
            mActivityMeasureCustomBinding.llCloth.addView(view, width / 4, height - 180);
        }

    }


    /**
     * 初始化数据(来自服务器数据)
     */
    @SuppressWarnings("unchecked")
    private void initDataFromInternet() {
        OthersUtil.showLoadDialog(mDialog);
        mMeasureCustomLists.clear();
        remarkAllList.clear();
        NewRxjavaWebUtils.getUIThread(NewRxjavaWebUtils.getObservable(this, "")
                        .map(new Func1<String, HsWebInfo>() {
                            @Override
                            public HsWebInfo call(String s) {
                                HsWebInfo hsWebInfo = NewRxjavaWebUtils.getJsonData(getApplicationContext(), CUS_SERVICE,
                                        "spappMeasureStyleTypeList",
                                        "iIndex=0"
                                                + ",iOrderType=" + orderType +
                                                ",iSdOrderMeterMstId=" + orderId +
                                                ",sPerson=" + mAreaname + "@" + mCityname + "@" + mCountyname + "@" + mCustomername + "@" + mDepartmentname + "@" + person,
                                        MeasureCustomBean.class.getName(),
                                        true, "待量体款式信息未获取到，请重试！");
                                Map<String, Object> map = new HashMap<>();
                                if (!hsWebInfo.success) return hsWebInfo;
                                map.put("measureStyle", hsWebInfo.wsData.LISTWSDATA);
                                hsWebInfo.object = map;
                                return hsWebInfo;
                            }
                        })
                        .map(new Func1<HsWebInfo, HsWebInfo>() {
                            @Override
                            public HsWebInfo call(HsWebInfo hsWebInfo) {
                                Map<String, Object> map = (Map<String, Object>) hsWebInfo.object;
                                HsWebInfo hsInfo = NewRxjavaWebUtils.getJsonData(getApplicationContext(), CUS_SERVICE,
                                        "spappMeasureStyleTypeList",
                                        "iIndex=1" +
                                                ",iOrderType=" + orderType +
                                                ",iSdOrderMeterMstId=" + orderId +
                                                ",sPerson=" + mAreaname + "@" + mCityname + "@" + mCountyname + "@" + mCustomername + "@" + mDepartmentname + "@" + person,
                                        MeasureDateBean.class.getName(),
                                        true, "已量体款式信息未获取到，请重试！");
                                map.put("measureStyleData", !hsInfo.success ? new ArrayList<WsEntity>() : hsInfo.wsData.LISTWSDATA);
                                hsWebInfo.object = map;
                                return hsWebInfo;
                            }

                        })
                        .map(new Func1<HsWebInfo, HsWebInfo>() {
                            @Override
                            public HsWebInfo call(HsWebInfo hsWebInfo) {
                                Map<String, Object> map = (Map<String, Object>) hsWebInfo.object;
                                HsWebInfo hsInfo = NewRxjavaWebUtils.getJsonData(getApplicationContext(), CUS_SERVICE,
                                        "spappMeasureStyleTypeList",
                                        "iIndex=3" +
                                                ",iOrderType=" + orderType +
                                                ",iSdOrderMeterMstId=" + orderId +
                                                ",sPerson=" + mAreaname + "@" + mCityname + "@" + mCountyname + "@" + mCustomername + "@" + mDepartmentname + "@" + person,
                                        MeasureBaseBean.class.getName(),
                                        true, "已量体款式信息未获取到，请重试！");
                                map.put("measureBase", !hsInfo.success ? new ArrayList<WsEntity>() : hsInfo.wsData.LISTWSDATA);
                                hsWebInfo.object = map;
                                return hsWebInfo;
                            }

                        })
                , this, mDialog, new SimpleHsWeb() {
                    @Override
                    public void success(HsWebInfo hsWebInfo) {
                        Map<String, Object> map = (Map<String, Object>) hsWebInfo.object;
                        //获取量体款式的名字
                        List<WsEntity> measureStyleList = (List<WsEntity>) map.get("measureStyle");
                        sortMeasureType(measureStyleList);
                        //获取量体款式的数据
                        List<WsEntity> measureDateList = (List<WsEntity>) map.get("measureStyleData");
                        //取量体计算的基础数据
                        List<WsEntity> measureBaseList = (List<WsEntity>) map.get("measureBase");
                        //整理基础量体的数据
                        MeasureBaseBean measureBaseBean = (MeasureBaseBean) measureBaseList.get(0);
                        mActivityMeasureCustomBinding.etHeight.setText(measureBaseBean.IHEIGHT);
                        mActivityMeasureCustomBinding.etWeight.setText(measureBaseBean.IWEIGHT);
                        mActivityMeasureCustomBinding.etChest.setText(measureBaseBean.IPURECHEST);
                        mActivityMeasureCustomBinding.etWaistline.setText(measureBaseBean.IPUREWAIST);
                        mActivityMeasureCustomBinding.etHips.setText(measureBaseBean.IPUREHIPS);
                        mActivityMeasureCustomBinding.etClothLength.setText(measureBaseBean.ICLOTHLENTH);
                        mActivityMeasureCustomBinding.etShoulder.setText(measureBaseBean.SSHOUDERWIDTH);
                        mActivityMeasureCustomBinding.etSleeve.setText(measureBaseBean.SSLEEVELENTH);
                        mActivityMeasureCustomBinding.etKuchang.setText(measureBaseBean.ITROUSERSLENTH);
                        mActivityMeasureCustomBinding.etYaowei.setText(measureBaseBean.IWAISTLENTH);
                        mActivityMeasureCustomBinding.etNO.setText(measureBaseBean.IHAONO);
                        mActivityMeasureCustomBinding.etStyle.setText(measureBaseBean.IXINGNO);
                        //整理填充的量体数字
                        Map<String, MeasureDateBean> measureDataMap = new HashMap<>();
                        for (int i = 0; i < measureDateList.size(); i++) {
                            MeasureDateBean measureDateBean = (MeasureDateBean) measureDateList.get(i);
                            String key = measureDateBean.ISDSTYLETYPEMSTID + "_" + measureDateBean.ISDSTYLETYPEITEMDTLID;
                            measureDataMap.put(key, measureDateBean);
                        }
                        //TODO 为什么没添加任何数据mMeasureCustomLists.size=8
                        for (int i = 0; i < mMeasureCustomLists.size(); i++) {
                            List<MeasureDataInSQLite> measureDataInSQLiteList = mMeasureCustomLists.get(i);
                            //添加款式每条量体信息
                            for (int j = 0; j < measureDataInSQLiteList.size(); j++) {
                                MeasureDataInSQLite measureDataInSQLite = measureDataInSQLiteList.get(j);
                                String key = measureDataInSQLite.getISdStyleTypeMstId()
                                        + "_" + measureDataInSQLite.getSdStyleTypeItemDtlId();
                                MeasureDateBean measureDateBean = measureDataMap.get(key);
                                measureDataInSQLite.setISMeterSize(measureDateBean == null ? "" : measureDateBean.ISMETERSIZE);
                                measureDataInSQLite.setBupdated(measureDateBean == null ? "False" : measureDateBean.BUPDATED);
                                measureDataInSQLite.setIrepair(measureDateBean == null ? "" : measureDateBean.IREPAIR);
                                measureDataInSQLite.setBrepair(measureDateBean == null ? "False" : measureDateBean.BREPAIR);

                                measureDataInSQLiteList.set(j, measureDataInSQLite);
                            }
                            mMeasureCustomLists.set(i, measureDataInSQLiteList);
                        }
                        initRemarkSaved(measureDateList);
                    }
                });
    }

    /**
     * 查询已保存备注的信息
     */
    private void initRemarkSaved(List<WsEntity> measureDateList) {
        OthersUtil.showLoadDialog(mDialog);
        NewRxjavaWebUtils.getUIThread(NewRxjavaWebUtils.getObservable(this, measureDateList)
                .map(new Func1<List<WsEntity>, HsWebInfo>() {
                    @Override
                    public HsWebInfo call(List<WsEntity> measureDateList) {
                        String userGUID = LGSPUtils.getLocalData(getApplicationContext(), USER_GUID, String.class.getName(), "").toString();
                        for (List<MeasureDataInSQLite> subList : mMeasureCustomLists) {
                            if (subList == null || subList.isEmpty()) {
                                remarkAllList.add(new ArrayList<MeasureRemarkDataInSQLite>());
                                continue;
                            }
                            try {
                                MeasureDataInSQLite measureDataInSQLite = subList.get(0);
                                HsWebInfo info = NewRxjavaWebUtils.getJsonData(getApplicationContext(), CUS_SERVICE,
                                        "spappMeasureStyleTypeList",
                                        "iIndex=2" + ",iOrderType=" + orderType +
                                                ",iSdOrderMeterMstId=" + orderId +
                                                ",iSdOrderMeterDtlId=" + measureDataInSQLite.getISdOrderMeterDtlId() +
                                                ",sPerson=" + mAreaname + "@" + mCityname + "@" + mCountyname + "@" + mCustomername + "@" + mDepartmentname + "@" + person +
                                                ",isdStyleTypeMstId=" + measureDataInSQLite.getISdStyleTypeMstId(),
                                        RemarkSavedBean.class.getName(),
                                        true,
                                        "");
                                if (!info.success)
                                    remarkAllList.add(new ArrayList<MeasureRemarkDataInSQLite>());
                                else {
                                    List<MeasureRemarkDataInSQLite> remarkList = new ArrayList<>();
                                    List<WsEntity> entities = info.wsData.LISTWSDATA;
                                    for (WsEntity entity : entities) {
                                        RemarkSavedBean bean = (RemarkSavedBean) entity;
                                        MeasureRemarkDataInSQLite measureRemarkDataInSQLite = new MeasureRemarkDataInSQLite();
                                        measureRemarkDataInSQLite.setPerson(person);
                                        measureRemarkDataInSQLite.setType(orderType);
                                        measureRemarkDataInSQLite.setIId(bean.ISMETERMARKDTLID);
                                        measureRemarkDataInSQLite.setUserGUID(userGUID);
                                        measureRemarkDataInSQLite.setSMeterMarkCode(bean.SMETERMARKCODE);
                                        measureRemarkDataInSQLite.setSMeterMarkName(bean.SMETERMARKNAME);
                                        measureRemarkDataInSQLite.setIOrderDtlId(bean.ISDORDERMETERDTLID);
                                        measureRemarkDataInSQLite.setOrderId(orderId);

//                                      detailBean.IID=remarkSavedBean.ISMETERMARKDTLID;
//                                      detailBean.SMETERMARKCODE=remarkSavedBean.SMETERMARKCODE;
//                                      detailBean.SMETERMARKNAME=remarkSavedBean.SMETERMARKNAME;
//                                      detailBean.iOrderDtlId=remarkSavedBean.ISDORDERMETERDTLID;
                                        remarkList.add(measureRemarkDataInSQLite);
                                    }
                                    remarkAllList.add(remarkList);
                                }
                            } catch (Exception e) {
                                remarkAllList.add(new ArrayList<MeasureRemarkDataInSQLite>());
                            }
                        }
                        return new HsWebInfo();
                    }
                }), getApplicationContext(), mDialog, new SimpleHsWeb() {
            @Override
            public void success(HsWebInfo hsWebInfo) {
                showData();
            }
        });
    }

    /**
     * 整理量体款式以及数据
     */
    private void sortMeasureType(List<WsEntity> measureStyleList) {
        String userGUID = LGSPUtils.getLocalData(getApplicationContext(), USER_GUID, String.class.getName(), "").toString();
        Map<String, List<MeasureDataInSQLite>> map = new HashMap<>();
        for (int i = 0; i < measureStyleList.size(); i++) {
            MeasureCustomBean bean = (MeasureCustomBean) measureStyleList.get(i);
            MeasureDataInSQLite measureDataInSQLite = new MeasureDataInSQLite();
            measureDataInSQLite.setOrderId(orderId);
            measureDataInSQLite.setISMeterSize(bean.ISMETERSIZE);
            measureDataInSQLite.setISdOrderMeterDtlId(bean.ISDORDERMETERDTLID);
            measureDataInSQLite.setISdStyleTypeMstId(bean.ISDSTYLETYPEMSTID);
            measureDataInSQLite.setISeq(bean.ISEQ);
            measureDataInSQLite.setIStyleseq(bean.ISTYLESEQ);
            measureDataInSQLite.setPerson(person);
            measureDataInSQLite.setSBillNo(bean.SBILLNO);
            measureDataInSQLite.setSdStyleTypeItemDtlId(bean.SDSTYLETYPEITEMDTLID);
            measureDataInSQLite.setSMeterCode(bean.SMETERCODE);
            measureDataInSQLite.setSMeterName(bean.SMETERNAME);
            measureDataInSQLite.setSValueCode(bean.SVALUECODE);
            measureDataInSQLite.setSValueGroup(bean.SVALUEGROUP);
            measureDataInSQLite.setType(orderType);
            measureDataInSQLite.setUserGUID(userGUID);
            measureDataInSQLite.setSFemaleMaxLenth(bean.SFEMALEMAXLENTH);
            measureDataInSQLite.setSFemaleMinLenth(bean.SFEMALEMINLENTH);
            measureDataInSQLite.setSMaleMaxLenth(bean.SMALEMAXLENTH);
            measureDataInSQLite.setSMaleMinLenth(bean.SMALEMINLENTH);
            measureDataInSQLite.setBEvenNo(bean.BEVENNO);
            measureDataInSQLite.setBPoint(bean.BPOINT);
//            measureDataInSQLite.setBupdated(bean.BUPDATED);


            List<MeasureDataInSQLite> measureDataInSQLiteList = map.get(measureDataInSQLite.getISdStyleTypeMstId());
            if (measureDataInSQLiteList == null) measureDataInSQLiteList = new ArrayList<>();
            measureDataInSQLiteList.add(measureDataInSQLite);
            map.put(measureDataInSQLite.getISdStyleTypeMstId(), measureDataInSQLiteList);
        }
        Iterator<Entry<String, List<MeasureDataInSQLite>>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, List<MeasureDataInSQLite>> entry = it.next();
            List<MeasureDataInSQLite> subList = entry.getValue();
//            Collections.sort(subList, new Comparator<MeasureDataInSQLite>() {
//                @Override
//                public int compare(MeasureDataInSQLite o1, MeasureDataInSQLite o2) {
//                    try {
//                        return Integer.parseInt(o1.getIStyleseq()) - Integer.parseInt(o2.getIStyleseq());
//                    } catch (Exception e) {
//                        return 0;
//                    }
//                }
//            });
            Collections.sort(subList, new Comparator<MeasureDataInSQLite>() {
                @Override
                public int compare(MeasureDataInSQLite o1, MeasureDataInSQLite o2) {
                    try {
                        return Integer.parseInt(o1.getISeq()) - Integer.parseInt(o2.getISeq());
                    } catch (Exception e) {
                        return 0;
                    }
                }
            });
            mMeasureCustomLists.add(subList);
        }
        Collections.sort(mMeasureCustomLists, new Comparator<List<MeasureDataInSQLite>>() {
            @Override
            public int compare(List<MeasureDataInSQLite> o1, List<MeasureDataInSQLite> o2) {
                try {
                    return Integer.parseInt(o1.get(0).getIStyleseq()) - Integer.parseInt(o2.get(0).getIStyleseq());
                } catch (Exception e) {
                    return 0;
                }
            }
        });

    }

    /**
     * 来自备注界面的数据
     *
     * @param event
     */
    @SuppressWarnings("unchecked")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveData(SecondToFirstActivityEvent event) {
        if (event.secondClass != RemarkDetailActivity.class || event.firstClass != MeasureCustomActivity.class)
            return;
        switch (event.index) {
            //备注界面
            case REMARK_INTENT_KEY:
                String userGUID = LGSPUtils.getLocalData(getApplicationContext(), USER_GUID, String.class.getName(), "").toString();
                Map<String, Object> map = (Map<String, Object>) event.object;
                if (map == null) return;
                String styleId = map.get(STYLE_ID_KEY).toString();//款式ID
                int position = -1;
                for (int i = 0; i < mMeasureCustomLists.size(); i++) {
                    if (styleId.equalsIgnoreCase(mMeasureCustomLists.get(i).get(0).getISdStyleTypeMstId())) {
                        position = i;
                        break;
                    }
                }
                if (position == -1) return;
                List<MeasureRemarkDataInSQLite> remarkList = (List<MeasureRemarkDataInSQLite>) map.get(REMARK_RETURN_DATA);
                for (int i = 0; i < remarkList.size(); i++) {
                    MeasureRemarkDataInSQLite measureRemarkDataInSQLite = remarkList.get(i);
                    measureRemarkDataInSQLite.setType(orderType);
                    measureRemarkDataInSQLite.setPerson(person);
                    measureRemarkDataInSQLite.setUserGUID(userGUID);
                    measureRemarkDataInSQLite.setOrderId(orderId);
                    measureRemarkDataInSQLite.setStyleId(mMeasureCustomLists.get(position).get(0).getISdStyleTypeMstId());
                    remarkList.set(i, measureRemarkDataInSQLite);
                }
                remarkAllList.set(position, remarkList);
                View view = mActivityMeasureCustomBinding.llCloth.getChildAt(position);
                final CheckBox cbRemark = (CheckBox) view.findViewById(R.id.cbRemark);
                try {
                    cbRemark.setChecked(remarkAllList.get(position) != null && !remarkAllList.get(position).isEmpty());
                } catch (Exception e) {
                    cbRemark.setChecked(false);
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Builder builder = new Builder(MeasureCustomActivity.this);
        builder.setTitle("提示");
        builder.setMessage("当前页面数据未保存保存，是否要确认退出");
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
    protected void onDestroy() {
        super.onDestroy();
        OthersUtil.unregisterEvent(this);
    }

//    /**
//     * 清除所有的输入框的焦点
//     */
//    private void clearAllInputFocus(View view){
//        view.setFocusable(true);
//        view.setFocusableInTouchMode(true);
//        view.requestFocus();
//        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
//        if(imm!=null) imm.hideSoftInputFromWindow(mActivityMeasureCustomBinding.btnSaveMeasure.getWindowToken(), 0);
//    }
}
