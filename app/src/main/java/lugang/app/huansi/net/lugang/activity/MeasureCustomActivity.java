package lugang.app.huansi.net.lugang.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import lugang.app.huansi.net.lugang.bean.MeasureCustomBean;
import lugang.app.huansi.net.lugang.bean.MeasureDateBean;
import lugang.app.huansi.net.lugang.bean.RemarkSavedBean;
import lugang.app.huansi.net.lugang.constant.Constant;
import lugang.app.huansi.net.lugang.databinding.ActivityMeasureCustomBinding;
import lugang.app.huansi.net.lugang.event.SecondToFirstActivityEvent;
import lugang.app.huansi.net.util.GreenDaoUtil;
import lugang.app.huansi.net.util.LGSPUtils;
import rx.functions.Func1;

import static huansi.net.qianjingapp.utils.NewRxjavaWebUtils.getJsonData;
import static huansi.net.qianjingapp.utils.WebServices.WebServiceType.CUS_SERVICE;
import static lugang.app.huansi.net.lugang.constant.Constant.MeasureCustomActivityConstant.ORDER_DTL_ID_INTENT;
import static lugang.app.huansi.net.lugang.constant.Constant.MeasureCustomActivityConstant.REMARK_INTENT_DATA;
import static lugang.app.huansi.net.lugang.constant.Constant.MeasureCustomActivityConstant.REMARK_INTENT_KEY;
import static lugang.app.huansi.net.lugang.constant.Constant.MeasureCustomActivityConstant.REMARK_RETURN_DATA;
import static lugang.app.huansi.net.lugang.constant.Constant.MeasureCustomActivityConstant.STYLE_ID_INTENT;
import static lugang.app.huansi.net.lugang.constant.Constant.MeasureCustomActivityConstant.STYLE_ID_KEY;
import static lugang.app.huansi.net.util.LGSPUtils.USER_GUID;

/**
 * 量体定制详情页
 */

public class MeasureCustomActivity extends NotWebBaseActivity {
    private ActivityMeasureCustomBinding mActivityMeasureCustomBinding;
    private List<List<MeasureDataInSQLite>> mMeasureCustomLists;
    private List<List<MeasureRemarkDataInSQLite>> remarkAllList;//备注列表
    private LoadProgressDialog dialog;

    private String person = "";//被量体人
    private String orderId = "";//订单ID
    private int orderType = 0;//0待量体 1已量体 2返修

    @Override
    protected int getLayoutId() {
        return R.layout.activity_measure_custom;
    }

    @Override
    public void init() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);//默认自动跳出软键盘
        mActivityMeasureCustomBinding = (ActivityMeasureCustomBinding) viewDataBinding;
        OthersUtil.registerEvent(this);
        dialog=new LoadProgressDialog(this);
        remarkAllList = new ArrayList<>();
        mMeasureCustomLists = new ArrayList<>();
        Intent intent = getIntent();
        person = intent.getStringExtra(Constant.SPERSON);
        String departmentName = intent.getStringExtra(Constant.SDEPARTMENTNAME);
        orderId = intent.getStringExtra(Constant.ISDORDERMETERMSTID);//订单头表id
        orderType= intent.getIntExtra(Constant.IORDERTYPE,0);
        mActivityMeasureCustomBinding.customName.setText(departmentName + ": " + person);
        mActivityMeasureCustomBinding.btnSaveMeasure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(NetUtil.isNetworkAvailable(getApplicationContext())){
                    submitMeasureData();
                }else {
                    saveToSQLite();
                }
                
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
//        initData(orderDtlId);
        if(NetUtil.isNetworkAvailable(getApplicationContext())){
            initDataFromInternet();
        }else {
            initDataFromSQLite();
        }

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
    private void saveToSQLite(){
        OthersUtil.showLoadDialog(dialog);
        //查询输入框的数据，并保存到数组中
        for(int i=0;i<mMeasureCustomLists.size();i++){
            List<MeasureDataInSQLite> subList=mMeasureCustomLists.get(i);
            View item=mActivityMeasureCustomBinding.llCloth.getChildAt(i);
            LinearLayout linearLayout = (LinearLayout) item.findViewById(R.id.llClothTypeList);
            for(int j=0;j<subList.size();j++){
                MeasureDataInSQLite measureDataInSQLite=subList.get(j);
                View subItem=linearLayout.getChildAt(j);
                EditText editText = (EditText) subItem.findViewById(R.id.etParameter);
                String size=editText.getText().toString().trim();
                if(size.isEmpty()) size="0";
                measureDataInSQLite.setISMeterSize(size);
                subList.set(j,measureDataInSQLite);
            }
            mMeasureCustomLists.set(i,subList);
        }

        NewRxjavaWebUtils.getUIThread(NewRxjavaWebUtils.getObservable(this, "")
                .map(new Func1<String, HsWebInfo>() {
                    @Override
                    public HsWebInfo call(String s) {
                        DaoSession daoSession= GreenDaoUtil.getGreenDaoSession(getApplicationContext());
                        String userGUID=LGSPUtils.getLocalData(getApplicationContext(),USER_GUID,String.class.getName(),"").toString();
                        //判断这个里面是否写过数据 如果没有输入值或者添加过备注，则为待量体 否则为已量体 返修的单独算
                        int saveType=0;//0待量体 1已量体 2返修
                        if(orderType==2) saveType=2;
                        else {
                            for (List<MeasureDataInSQLite> subList : mMeasureCustomLists) {
                                if (subList.isEmpty()) continue;
                                for(MeasureDataInSQLite measureDataInSQLite:subList){
                                    String size=measureDataInSQLite.getISMeterSize();
                                    if(size==null||size.isEmpty()||size.equalsIgnoreCase("0"))continue;
                                    saveType=1;
                                    break;
                                }
                                if(saveType==1) break;
                            }
                            if(saveType!=1){
                                for (List<MeasureRemarkDataInSQLite> subList : remarkAllList) {
                                    if (subList.isEmpty()) continue;
                                    saveType=1;
                                    break;
                                }
                            }
                        }

                        //先保存量体数据
                        MeasureDataInSQLiteDao measureDataInSQLiteDao=daoSession.getMeasureDataInSQLiteDao();

                        for(int i=0;i<mMeasureCustomLists.size();i++){
                            List<MeasureDataInSQLite> subList=mMeasureCustomLists.get(i);
                            if(subList.isEmpty())continue;
                            for(int j=0;j<subList.size();j++){
                                MeasureDataInSQLite measureDataInSQLite=subList.get(j);
                                measureDataInSQLite.setType(saveType);
                                subList.set(j,measureDataInSQLite);
                            }
                            measureDataInSQLiteDao.insertOrReplaceInTx(subList);
                        }

                        //其次保存备注数据
                        MeasureRemarkDataInSQLiteDao measureRemarkDataInSQLiteDao=daoSession.getMeasureRemarkDataInSQLiteDao();
                        List<MeasureRemarkDataInSQLite> beforeRemarkDataInList=null;
                        try {
                            beforeRemarkDataInList=measureRemarkDataInSQLiteDao.queryBuilder()
                                    .where(MeasureRemarkDataInSQLiteDao.Properties.Type.eq(orderType))
                                    .where(MeasureRemarkDataInSQLiteDao.Properties.UserGUID.eq(userGUID))
                                    .where(MeasureRemarkDataInSQLiteDao.Properties.Person.eq(person))
                                    .where(MeasureRemarkDataInSQLiteDao.Properties.OrderId.eq(orderId))
                                    .list();
                        }catch (Exception e){}
                        if(beforeRemarkDataInList==null) beforeRemarkDataInList=new ArrayList<>();
                        measureRemarkDataInSQLiteDao.deleteInTx(beforeRemarkDataInList);

                        for(List<MeasureRemarkDataInSQLite> subList:remarkAllList){
                            if(subList.isEmpty())continue;
                            for(int j=0;j<subList.size();j++){
                                MeasureRemarkDataInSQLite measureRemarkDataInSQLite=subList.get(j);
                                measureRemarkDataInSQLite.setType(saveType);
                                measureRemarkDataInSQLite.setId(null);
                                subList.set(j,measureRemarkDataInSQLite);
                            }
                            measureRemarkDataInSQLiteDao.insertOrReplaceInTx(subList);
                        }
                        //说明已量体->待量体 or 待量体->已量体 更改之前界面的状态
                        if(saveType!=orderType){
                            MeasureOrderInSQLite measureOrderInSQLite=null;
                            MeasureOrderInSQLiteDao measureOrderInSQLiteDao=daoSession.getMeasureOrderInSQLiteDao();

                            try {
                                List<MeasureOrderInSQLite> measureOrderInSQLiteList=measureOrderInSQLiteDao
                                        .queryBuilder()
                                        .where(MeasureOrderInSQLiteDao.Properties.OrderType.eq(orderType))
                                        .where(MeasureOrderInSQLiteDao.Properties.UserGUID.eq(userGUID))
                                        .where(MeasureOrderInSQLiteDao.Properties.ISdOrderMeterMstId.eq(orderId))
                                        .where(MeasureOrderInSQLiteDao.Properties.SPerson.eq(person))
                                        .list();
                                measureOrderInSQLite=measureOrderInSQLiteList.get(0);
                            }catch (Exception e){}
                            if(measureOrderInSQLite!=null){
                                measureOrderInSQLite.setOrderType(saveType);
                                measureOrderInSQLiteDao.insertOrReplace(measureOrderInSQLite);
                            }
                        }
                        return new HsWebInfo();
                    }
                }), getApplicationContext(), dialog, new SimpleHsWeb() {
            @Override
            public void success(HsWebInfo hsWebInfo) {
                OthersUtil.ToastMsg(getApplicationContext(),"保存成功");
                finish();
            }

            @Override
            public void error(HsWebInfo hsWebInfo, Context context) {
                OthersUtil.ToastMsg(getApplicationContext(),"保存失败");
            }
        });
    }




    //保存录入信息
    private void submitMeasureData() {
        OthersUtil.showLoadDialog(mDialog);
        //查询输入框的数据，并保存到数组中
        for(int i=0;i<mMeasureCustomLists.size();i++){
            List<MeasureDataInSQLite> subList=mMeasureCustomLists.get(i);
            View item=mActivityMeasureCustomBinding.llCloth.getChildAt(i);
            LinearLayout linearLayout = (LinearLayout) item.findViewById(R.id.llClothTypeList);
            for(int j=0;j<subList.size();j++){
                MeasureDataInSQLite measureDataInSQLite=subList.get(j);
                View subItem=linearLayout.getChildAt(j);
                EditText editText = (EditText) subItem.findViewById(R.id.etParameter);
                String size=editText.getText().toString().trim();
                if(size.isEmpty()) size="0";
                measureDataInSQLite.setISMeterSize(size);
                subList.set(j,measureDataInSQLite);
            }
            mMeasureCustomLists.set(i,subList);
        }
        NewRxjavaWebUtils.getUIThread(NewRxjavaWebUtils.getObservable(this, "")
                        .map(new Func1<String, HsWebInfo>() {
                            @Override
                            public HsWebInfo call(String s) {
                                String userGUID=LGSPUtils.getLocalData(getApplicationContext(),USER_GUID,String.class.getName(),"").toString();
                                StringBuilder sbStr = new StringBuilder();
                                for (int i = 0; i < mMeasureCustomLists.size(); i++) {
                                    List<MeasureDataInSQLite> subList = mMeasureCustomLists.get(i);
                                    for (int j = 0; j < subList.size(); j++) {
                                        MeasureDataInSQLite measureDataInSQLite = subList.get(j);
                                        sbStr.append("EXEC spappMeasureSaveMeasureData ")
                                                .append("@uHrEmployeeGUID='").append(userGUID ).append("'")
                                                .append(",@isdOrderMeterDtlid=").append(measureDataInSQLite.getISdOrderMeterDtlId())
                                                .append(",@isMeterSize=").append(measureDataInSQLite.getISMeterSize())
                                                .append(",@isdStyleTypeItemDtlid=").append(measureDataInSQLite.getSdStyleTypeItemDtlId())
                                                .append(";");
                                    }
                                }

                                for (int i = 0; i < remarkAllList.size(); i++) {
                                    StringBuilder sbRemarkId=new StringBuilder();
                                    List<MeasureRemarkDataInSQLite> subList = remarkAllList.get(i);
                                    if (subList == null) subList = new ArrayList<>();
                                    String orderDtlId="";
                                    for (int j=0;j<subList.size();j++) {
                                        MeasureRemarkDataInSQLite measureRemarkDataInSQLite=subList.get(j);
                                        orderDtlId=measureRemarkDataInSQLite.getIOrderDtlId();
                                        sbRemarkId.append(measureRemarkDataInSQLite.getIId());
                                        if(j!=subList.size()-1) sbRemarkId.append("@");
                                    }
                                    if(!orderDtlId.isEmpty())
                                        sbStr.append("EXEC spappMeasureSaveMeasureRemark ")
                                                .append("@sSdMeterMarkDtlid='").append(sbRemarkId.toString()).append("'")
                                                .append(",@isdOrderMeterDtlid=").append(orderDtlId)
                                                .append("; ");
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
                        String userGUID= LGSPUtils.getLocalData(getApplicationContext(), USER_GUID,String.class.getName(),"").toString();
                        DaoSession daoSession = GreenDaoUtil.getGreenDaoSession(getApplicationContext());
                        //先从数据库中获取量体数据
                        MeasureDataInSQLiteDao measureDataInSQLiteDao = daoSession.getMeasureDataInSQLiteDao();
                        List<MeasureDataInSQLite> measureDataInSQLiteList = measureDataInSQLiteDao.queryBuilder()
                                .where(MeasureDataInSQLiteDao.Properties.Person.eq(person))
                                .where(MeasureDataInSQLiteDao.Properties.OrderId.eq(orderId))
                                .where(MeasureDataInSQLiteDao.Properties.UserGUID.eq(userGUID))
                                .where(MeasureDataInSQLiteDao.Properties.Type.eq(orderType))
                                .list();
                        if(measureDataInSQLiteList==null) measureDataInSQLiteList=new ArrayList<>();

                        MeasureRemarkDataInSQLiteDao measureRemarkDataInSQLiteDao=daoSession.getMeasureRemarkDataInSQLiteDao();
                        List<MeasureRemarkDataInSQLite> measureRemarkDataInSQLiteList = measureRemarkDataInSQLiteDao.queryBuilder()
                                .where(MeasureRemarkDataInSQLiteDao.Properties.Person.eq(person))
                                .where(MeasureRemarkDataInSQLiteDao.Properties.OrderId.eq(orderId))
                                .where(MeasureRemarkDataInSQLiteDao.Properties.UserGUID.eq(userGUID))
                                .where(MeasureRemarkDataInSQLiteDao.Properties.Type.eq(orderType))
                                .list();
                        if(measureRemarkDataInSQLiteList==null) measureRemarkDataInSQLiteList=new ArrayList<>();
                        HsWebInfo info=new HsWebInfo();
                        Map<String,Object> map=new HashMap<>();
                        map.put("measureDataInSQLiteList",measureDataInSQLiteList);
                        map.put("measureRemarkDataInSQLiteList",measureRemarkDataInSQLiteList);
                        info.object=map;
                        return info;
                    }
                }), getApplicationContext(), dialog, new SimpleHsWeb() {
            @Override
            public void success(HsWebInfo hsWebInfo) {
                Map<String,Object> map= (Map<String, Object>) hsWebInfo.object;
                //量体数据
                List<MeasureDataInSQLite> measureDataInSQLiteList= (List<MeasureDataInSQLite>) map.get("measureDataInSQLiteList");
                //备注数据
                List<MeasureRemarkDataInSQLite> measureRemarkDataInSQLiteList= (List<MeasureRemarkDataInSQLite>) map.get("measureRemarkDataInSQLiteList");

                //整理量体数据
                Map<String,List<MeasureDataInSQLite>> filterMap=new HashMap<>();
                for(MeasureDataInSQLite measureDataInSQLite:measureDataInSQLiteList){
                    String key=measureDataInSQLite.getISdStyleTypeMstId();
                    List<MeasureDataInSQLite> subList=filterMap.get(key);
                    if(subList==null) subList=new ArrayList<>();
                    subList.add(measureDataInSQLite);
                    filterMap.put(key,subList);
                }
                Iterator<Entry<String,List<MeasureDataInSQLite>>> itData=filterMap.entrySet().iterator();
                while (itData.hasNext()){
                    Entry<String,List<MeasureDataInSQLite>> entry=itData.next();
                    mMeasureCustomLists.add(entry.getValue());
                }

                Map<String,List<MeasureRemarkDataInSQLite>> remarkMap=new HashMap<>();
                for(MeasureRemarkDataInSQLite measureRemarkDataInSQLite:measureRemarkDataInSQLiteList){
                    List<MeasureRemarkDataInSQLite> subList=remarkMap.get(measureRemarkDataInSQLite.getStyleId());
                    if(subList==null) subList=new ArrayList<>();
                    subList.add(measureRemarkDataInSQLite);
                    remarkMap.put(measureRemarkDataInSQLite.getStyleId(),subList);
                }


                //整理备注数据
                for(List<MeasureDataInSQLite> subList:mMeasureCustomLists){
                    String styleId=subList.get(0).getISdStyleTypeMstId();
                    List<MeasureRemarkDataInSQLite> remarkSubList=remarkMap.get(styleId);
                    if(remarkSubList==null) remarkSubList=new ArrayList<>();
                    remarkAllList.add(remarkSubList);
                }
                showData();
            }
        });

    }

    /**
     * 进行对数据的显示作用
     */
    private void showData(){
        LayoutInflater layoutInflater = getLayoutInflater();
        //添加View 即每个款式
        for (int i = 0; i < mMeasureCustomLists.size(); i++) {
            View view = layoutInflater.inflate(R.layout.activity_measure_detial, null);
            TextView tvClothStyle = (TextView) view.findViewById(R.id.tvClothStyle);
            tvClothStyle.setText(mMeasureCustomLists.get(i).get(0).getSValueGroup());
            List<MeasureDataInSQLite> measureDataInSQLiteList = mMeasureCustomLists.get(i);
            //添加咩咯款式每条量体信息
            LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.llClothTypeList);
            for (MeasureDataInSQLite measureDataInSQLite : measureDataInSQLiteList) {
                View convertView = LinearLayout.inflate(getApplicationContext(), R.layout.ll_parameter, null);
                TextView tvParameter = (TextView) convertView.findViewById(R.id.tvParameter);
                tvParameter.setText(measureDataInSQLite.getSMeterName());
//                MeasureDateBean measureDateBean=measureDataMap.get(measureCustom.SDSTYLETYPEITEMDTLID+"_"+measureCustom.ISDORDERMETERDTLID);
//                measureCustom.ISMETERSIZE=measureDateBean==null?"":measureDateBean.ISMETERSIZE;
                EditText editText = (EditText) convertView.findViewById(R.id.etParameter);
                editText.setText(measureDataInSQLite.getISMeterSize());
                linearLayout.addView(convertView);
            }
            mActivityMeasureCustomBinding.llCloth.setGravity(Gravity.CENTER_HORIZONTAL);
            WindowManager wm =getWindowManager();
            int width = wm.getDefaultDisplay().getWidth();
            int height = wm.getDefaultDisplay().getHeight();
            LinearLayout remarkLayout= (LinearLayout) view.findViewById(R.id.remarkLayout);

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
                cbRemark.setChecked(remarkAllList.get(finalI)!=null&&!remarkAllList.get(finalI).isEmpty());
            }catch (Exception e){
                cbRemark.setChecked(false);
            }
            mActivityMeasureCustomBinding.llCloth.addView(view, width / 4, height - 110);
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
                                                +",iOrderType="+ orderType+
                                                ",iSdOrderMeterMstId=" + orderId+
                                                ",sPerson="+person,
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
                                                ",iOrderType="+ orderType+
                                                ",iSdOrderMeterMstId=" + orderId+
                                                ",sPerson="+person,
                                        MeasureDateBean.class.getName(),
                                        true, "已量体款式信息未获取到，请重试！");
                                map.put("measureStyleData", !hsInfo.success ? new ArrayList<WsEntity>() : hsInfo.wsData.LISTWSDATA);
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
                        //整理填充的量体数字
                        Map<String,MeasureDateBean> measureDataMap=new HashMap<>();
                        for (int i = 0; i <measureDateList.size() ; i++) {
                            MeasureDateBean measureDateBean = (MeasureDateBean) measureDateList.get(i);
                            String key=measureDateBean.ISDSTYLETYPEMSTID+"_"+measureDateBean.ISDSTYLETYPEITEMDTLID;
                            measureDataMap.put(key,measureDateBean);
                        }

                        for (int i = 0; i < mMeasureCustomLists.size(); i++) {
                            List<MeasureDataInSQLite> measureDataInSQLiteList = mMeasureCustomLists.get(i);
                            //添加咩咯款式每条量体信息
                            for (int j=0;j<measureDataInSQLiteList.size();j++) {
                                MeasureDataInSQLite measureDataInSQLite =measureDataInSQLiteList.get(j);
                                String key=measureDataInSQLite.getISdStyleTypeMstId()
                                        + "_" + measureDataInSQLite.getSdStyleTypeItemDtlId();

                                MeasureDateBean measureDateBean = measureDataMap.get(key);

                                measureDataInSQLite.setISMeterSize(measureDateBean == null ? "" : measureDateBean.ISMETERSIZE);
                                measureDataInSQLiteList.set(j,measureDataInSQLite);
                            }
                            mMeasureCustomLists.set(i,measureDataInSQLiteList);
                        }
                        initRemarkSaved(measureDateList);

                    }
                });
    }

    /**
     * 查询已保存备注的信息
     */
    private void initRemarkSaved(List<WsEntity> measureDateList){
        OthersUtil.showLoadDialog(mDialog);
        NewRxjavaWebUtils.getUIThread(NewRxjavaWebUtils.getObservable(this, measureDateList)
                .map(new Func1<List<WsEntity>, HsWebInfo>() {
                    @Override
                    public HsWebInfo call(List<WsEntity> measureDateList) {
                        String userGUID=LGSPUtils.getLocalData(getApplicationContext(),USER_GUID,String.class.getName(),"").toString();
                        for(List<MeasureDataInSQLite> subList:mMeasureCustomLists) {
                            if (subList == null || subList.isEmpty()) {
                                remarkAllList.add(new ArrayList<MeasureRemarkDataInSQLite>());
                                continue;
                            }
                            try {
                                MeasureDataInSQLite measureDataInSQLite=subList.get(0);
                                HsWebInfo info = NewRxjavaWebUtils.getJsonData(getApplicationContext(), CUS_SERVICE,
                                        "spappMeasureStyleTypeList",
                                        "iIndex=2" +",iOrderType="+ orderType+
                                                ",iSdOrderMeterMstId="+orderId+
                                                ",iSdOrderMeterDtlId=" + measureDataInSQLite.getISdOrderMeterDtlId() +
                                                ",sPerson="+person+
                                                ",isdStyleTypeMstId=" + measureDataInSQLite.getISdStyleTypeMstId(),
                                        RemarkSavedBean.class.getName(),
                                        true,
                                        "");
                                if(!info.success)remarkAllList.add(new ArrayList<MeasureRemarkDataInSQLite>());
                                else {
                                    List<MeasureRemarkDataInSQLite> remarkList=new ArrayList<>();
                                    List<WsEntity> entities=info.wsData.LISTWSDATA;
                                    for(WsEntity entity:entities){
                                        RemarkSavedBean bean= (RemarkSavedBean) entity;
                                        MeasureRemarkDataInSQLite measureRemarkDataInSQLite=new MeasureRemarkDataInSQLite();
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
                            }catch (Exception e){
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
        String userGUID=LGSPUtils.getLocalData(getApplicationContext(),USER_GUID,String.class.getName(),"").toString();
        Map<String, List<MeasureDataInSQLite>> map = new HashMap<>();
        for (int i = 0; i < measureStyleList.size(); i++) {
            MeasureCustomBean bean = (MeasureCustomBean) measureStyleList.get(i);
            MeasureDataInSQLite measureDataInSQLite=new MeasureDataInSQLite();
            measureDataInSQLite.setOrderId(orderId);
            measureDataInSQLite.setISMeterSize(bean.ISMETERSIZE);
            measureDataInSQLite.setISdOrderMeterDtlId(bean.ISDORDERMETERDTLID);
            measureDataInSQLite.setISdStyleTypeMstId(bean.ISDSTYLETYPEMSTID);
            measureDataInSQLite.setISeq(bean.ISEQ);
            measureDataInSQLite.setPerson(person);
            measureDataInSQLite.setSBillNo(bean.SBILLNO);
            measureDataInSQLite.setSdStyleTypeItemDtlId(bean.SDSTYLETYPEITEMDTLID);
            measureDataInSQLite.setSMeterCode(bean.SMETERCODE);
            measureDataInSQLite.setSMeterName(bean.SMETERNAME);
            measureDataInSQLite.setSValueCode(bean.SVALUECODE);
            measureDataInSQLite.setSValueGroup(bean.SVALUEGROUP);
            measureDataInSQLite.setType(orderType);
            measureDataInSQLite.setUserGUID(userGUID);

            List<MeasureDataInSQLite> measureDataInSQLiteList = map.get(measureDataInSQLite.getISdStyleTypeMstId());
            if (measureDataInSQLiteList == null) measureDataInSQLiteList = new ArrayList<>();
            measureDataInSQLiteList.add(measureDataInSQLite);
            map.put(measureDataInSQLite.getISdStyleTypeMstId(), measureDataInSQLiteList);
        }
        Iterator<Entry<String, List<MeasureDataInSQLite>>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, List<MeasureDataInSQLite>> entry = it.next();
            List<MeasureDataInSQLite> subList = entry.getValue();
            Collections.sort(subList, new Comparator<MeasureDataInSQLite>() {
                @Override
                public int compare(MeasureDataInSQLite o1, MeasureDataInSQLite o2) {
                    try {
                        return Integer.parseInt(o1.getISeq())-Integer.parseInt(o2.getISeq());
                    } catch (Exception e) {
                        return 0;
                    }
                }
            });
            mMeasureCustomLists.add(subList);
        }
    }

    /**
     * 显示传上去的量体数据
     */
//    private void showMeasureDataSaved(List<WsEntity> measureDateList) {
//        Map<String,MeasureDateBean> measureDataMap=new HashMap<>();
//        for (int i = 0; i <measureDateList.size() ; i++) {
//            MeasureDateBean measureDateBean = (MeasureDateBean) measureDateList.get(i);
//            measureDataMap.put(measureDateBean.ISDSTYLETYPEITEMDTLID+"_"+measureDateBean.ISDORDERMETERDTLID,measureDateBean);
//        }
//        LayoutInflater layoutInflater = getLayoutInflater();
//        //添加View 即每个款式
//        for (int i = 0; i < mMeasureCustomLists.size(); i++) {
//            View view = layoutInflater.inflate(R.layout.activity_measure_detial, null);
//            TextView tvClothStyle = (TextView) view.findViewById(R.id.tvClothStyle);
//            tvClothStyle.setText(mMeasureCustomLists.get(i).get(0).SVALUEGROUP);
//            List<MeasureCustomBean> measureBeanList = mMeasureCustomLists.get(i);
//            //添加咩咯款式每条量体信息
//            LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.llClothTypeList);
//            for (MeasureCustomBean measureCustom : measureBeanList) {
//                View convertView = LinearLayout.inflate(getApplicationContext(), R.layout.ll_parameter, null);
//                TextView tvParameter = (TextView) convertView.findViewById(R.id.tvParameter);
//                tvParameter.setText(measureCustom.SMETERNAME);
//                MeasureDateBean measureDateBean=measureDataMap.get(measureCustom.SDSTYLETYPEITEMDTLID+"_"+measureCustom.ISDORDERMETERDTLID);
//                measureCustom.ISMETERSIZE=measureDateBean==null?"":measureDateBean.ISMETERSIZE;
//                final EditText editText = (EditText) convertView.findViewById(R.id.etParameter);
//                editText.setText(measureCustom.ISMETERSIZE);
//                linearLayout.addView(convertView);
//            }
//            mActivityMeasureCustomBinding.llCloth.setGravity(Gravity.CENTER_HORIZONTAL);
//            WindowManager wm =getWindowManager();
//            int width = wm.getDefaultDisplay().getWidth();
//            int height = wm.getDefaultDisplay().getHeight();
//            LinearLayout remarkLayout= (LinearLayout) view.findViewById(R.id.remarkLayout);
//
//            //跳转到备注界面
//            final int finalI = i;
//            final int finalI1 = i;
//            remarkLayout.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(MeasureCustomActivity.this, RemarkDetailActivity.class);
//                    intent.putExtra(STYLE_ID_INTENT, mMeasureCustomLists.get(finalI1).get(0).ISDSTYLETYPEMSTID);
//                    intent.putExtra(ORDER_DTL_ID_INTENT, mMeasureCustomLists.get(finalI1).get(0).ISDORDERMETERDTLID);
//                    intent.putExtra(REMARK_INTENT_DATA, (Serializable) remarkAllList.get(finalI));
//                    startActivity(intent);
//                }
//            });
//
//            final CheckBox cbRemark = (CheckBox) view.findViewById(R.id.cbRemark);
//            try {
//                cbRemark.setChecked(remarkAllList.get(finalI)!=null&&!remarkAllList.get(finalI).isEmpty());
//            }catch (Exception e){
//                cbRemark.setChecked(false);
//            }
//            mActivityMeasureCustomBinding.llCloth.addView(view, width / 5, height - 110);
//        }
//    }


//    @SuppressWarnings("unchecked")
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(resultCode!=RESULT_OK) return;
//        if(data==null) return;
//        switch (requestCode) {
//            //备注界面
//            case REMARK_INTENT_KEY:
//        }
//    }

    /**
     * 来自备注界面的数据
     * @param event
     */
    @SuppressWarnings("unchecked")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveData(SecondToFirstActivityEvent event){
        if(event.secondClass!=RemarkDetailActivity.class|| event.firstClass!=MeasureCustomActivity.class) return;
        switch (event.index) {
            //备注界面
            case REMARK_INTENT_KEY:
                String userGUID=LGSPUtils.getLocalData(getApplicationContext(),USER_GUID,String.class.getName(),"").toString();
                Map<String,Object> map= (Map<String, Object>) event.object;
                if(map==null) return;
                String styleId=map.get(STYLE_ID_KEY).toString();//款式ID
                int position=-1;
                for(int i=0;i<mMeasureCustomLists.size();i++){
                    if(styleId.equalsIgnoreCase(mMeasureCustomLists.get(i).get(0).getISdStyleTypeMstId())){
                        position=i;
                        break;
                    }
                }
                if(position==-1) return;
                List<MeasureRemarkDataInSQLite> remarkList= (List<MeasureRemarkDataInSQLite>) map.get(REMARK_RETURN_DATA);
                for(int i=0;i<remarkList.size();i++){
                    MeasureRemarkDataInSQLite measureRemarkDataInSQLite=remarkList.get(i);
                    measureRemarkDataInSQLite.setType(orderType);
                    measureRemarkDataInSQLite.setPerson(person);
                    measureRemarkDataInSQLite.setUserGUID(userGUID);
                    measureRemarkDataInSQLite.setOrderId(orderId);
                    measureRemarkDataInSQLite.setStyleId(mMeasureCustomLists.get(position).get(0).getISdStyleTypeMstId());
                    remarkList.set(i,measureRemarkDataInSQLite);
                }
                remarkAllList.set(position,remarkList);
                View view=mActivityMeasureCustomBinding.llCloth.getChildAt(position);
                final CheckBox cbRemark = (CheckBox) view.findViewById(R.id.cbRemark);
                try {
                    cbRemark.setChecked(remarkAllList.get(position)!=null&&!remarkAllList.get(position).isEmpty());
                }catch (Exception e){
                    cbRemark.setChecked(false);
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MeasureCustomActivity.this);
        builder.setTitle("提示");
        builder.setMessage("当前页面数据未保存保存，是否要确认退出");
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
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
}
