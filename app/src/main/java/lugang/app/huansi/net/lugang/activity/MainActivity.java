package lugang.app.huansi.net.lugang.activity;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.IdRes;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import huansi.net.qianjingapp.base.NotWebBaseActivity;
import huansi.net.qianjingapp.entity.HsWebInfo;
import huansi.net.qianjingapp.entity.WsData;
import huansi.net.qianjingapp.entity.WsEntity;
import huansi.net.qianjingapp.imp.SimpleHsWeb;
import huansi.net.qianjingapp.utils.NewRxjavaWebUtils;
import huansi.net.qianjingapp.utils.OthersUtil;
import huansi.net.qianjingapp.view.LoadProgressDialog;
import lugang.app.huansi.net.db.MeasureDataInSQLite;
import lugang.app.huansi.net.db.MeasureOrderDtlRemarkBaseInSQLite;
import lugang.app.huansi.net.db.MeasureOrderDtlStyleBaseDataInSQLite;
import lugang.app.huansi.net.db.MeasureOrderInSQLite;
import lugang.app.huansi.net.db.MeasureRemarkDataInSQLite;
import lugang.app.huansi.net.db.RemarkCategoryDataInSQLite;
import lugang.app.huansi.net.db.RemarkDetailDataInSQLite;
import lugang.app.huansi.net.greendao.DaoSession;
import lugang.app.huansi.net.greendao.MeasureDataInSQLiteDao;
import lugang.app.huansi.net.greendao.MeasureOrderDtlRemarkBaseInSQLiteDao;
import lugang.app.huansi.net.greendao.MeasureOrderDtlStyleBaseDataInSQLiteDao;
import lugang.app.huansi.net.greendao.MeasureOrderInSQLiteDao;
import lugang.app.huansi.net.greendao.MeasureRemarkDataInSQLiteDao;
import lugang.app.huansi.net.greendao.RemarkCategoryDataInSQLiteDao;
import lugang.app.huansi.net.greendao.RemarkDetailDataInSQLiteDao;
import lugang.app.huansi.net.lugang.R;
import lugang.app.huansi.net.lugang.bean.AddOrderDtlBean;
import lugang.app.huansi.net.lugang.bean.FinishMeasureBean;
import lugang.app.huansi.net.lugang.bean.MeasureCustomBean;
import lugang.app.huansi.net.lugang.bean.MeasureDateBean;
import lugang.app.huansi.net.lugang.bean.MeasureOrderDtlRemarkBaseBean;
import lugang.app.huansi.net.lugang.bean.MeasureOrderDtlStyleBaseBean;
import lugang.app.huansi.net.lugang.bean.RemarkCategoryBaseDataBean;
import lugang.app.huansi.net.lugang.bean.RemarkDetailBaseDataBean;
import lugang.app.huansi.net.lugang.bean.RemarkSavedBean;
import lugang.app.huansi.net.lugang.bean.RepairRegisterBean;
import lugang.app.huansi.net.lugang.bean.StartMeasureBean;
import lugang.app.huansi.net.lugang.databinding.ActivityMainBinding;
import lugang.app.huansi.net.lugang.fragment.CustomConfirmFragment;
import lugang.app.huansi.net.lugang.fragment.MeasureCustomFragment;
import lugang.app.huansi.net.lugang.fragment.RepairRegisterFragment;
import lugang.app.huansi.net.lugang.fragment.TaskShowFragment;
import lugang.app.huansi.net.util.GreenDaoUtil;
import lugang.app.huansi.net.util.LGSPUtils;
import rx.functions.Func1;

import static huansi.net.qianjingapp.utils.NewRxjavaWebUtils.getJsonData;
import static huansi.net.qianjingapp.utils.WebServices.WebServiceType.CUS_SERVICE;
import static lugang.app.huansi.net.util.LGSPUtils.USER_GUID;

public class MainActivity  extends NotWebBaseActivity  {

    private ActivityMainBinding mActivityMainBinding;
    private MeasureCustomFragment mMeasureCustomFragment;
    private TaskShowFragment mTaskShowFragment;
    private RepairRegisterFragment mRepairRegisterFragment;
    private CustomConfirmFragment mCustomConfirmFragment;
    private LoadProgressDialog dialog;


    @Override
    public void init() {
        mActivityMainBinding = (ActivityMainBinding) viewDataBinding;
        dialog = new LoadProgressDialog(this);
        initFragment();
        mActivityMainBinding.rgBase.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                FragmentTransaction fs = getSupportFragmentManager().beginTransaction();
                switch (checkedId) {
                    case R.id.measureCustom://量体定制
                        hideFragment();
                        fs.show(mMeasureCustomFragment);
                        fs.commitAllowingStateLoss();
                        break;
                    case R.id.taskShow://任务看板
                        hideFragment();
                        fs.show(mTaskShowFragment);
                        fs.commitAllowingStateLoss();
                        break;
                    case R.id.repairRegister://返修登记
                        hideFragment();
                        fs.show(mRepairRegisterFragment);
                        fs.commitAllowingStateLoss();
                        break;
                    case R.id.customerConfirm://用户确认
                        hideFragment();
                        fs.show(mCustomConfirmFragment);
                        fs.commitAllowingStateLoss();
                        break;
                }
            }
        });
        mActivityMainBinding.rgBase.check(R.id.taskShow);
        mActivityMainBinding.btnDownBaseData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downBaseData();
            }
        });
        mActivityMainBinding.btnUploadMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Builder(MainActivity.this)
                        .setMessage("确定上传数据？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                submitData();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setCancelable(false)
                        .show();


            }
        });
    }

    /**
     * 上传数据
     */
    private void submitData() {
        OthersUtil.showLoadDialog(dialog);
        NewRxjavaWebUtils.getUIThread(NewRxjavaWebUtils.getObservable(MainActivity.this, "")
                        //首先上传新增明细单
                        .map(new Func1<String, HsWebInfo>() {
                            @Override
                            public HsWebInfo call(String s) {
                                String userGUID = LGSPUtils.getLocalData(getApplicationContext(), USER_GUID, String.class.getName(), "").toString();

                                DaoSession daoSession = GreenDaoUtil.getGreenDaoSession(getApplicationContext());
                                MeasureOrderInSQLiteDao measureOrderInSQLiteDao = daoSession.getMeasureOrderInSQLiteDao();
                                MeasureDataInSQLiteDao measureDataInSQLiteDao = daoSession.getMeasureDataInSQLiteDao();
                                MeasureRemarkDataInSQLiteDao measureRemarkDataInSQLiteDao = daoSession.getMeasureRemarkDataInSQLiteDao();
                                try {
                                    List<MeasureDataInSQLite> measureDataInSQLiteList = measureDataInSQLiteDao.queryBuilder()
                                            .where(MeasureDataInSQLiteDao.Properties.IsAdd.eq(true))
                                            .where(MeasureDataInSQLiteDao.Properties.UserGUID.eq(userGUID))
                                            .list();
                                    if (measureDataInSQLiteList == null)
                                        measureDataInSQLiteList = new ArrayList<>();
                                    Map<String, String> removeRepeatMap = new HashMap<>();

                                    for (int i = 0; i < measureDataInSQLiteList.size(); i++) {
                                        MeasureDataInSQLite measureDataInSQLite = measureDataInSQLiteList.get(i);
                                        String key = measureDataInSQLite.getISdStyleTypeMstId() + "_" + measureDataInSQLite.getOrderId();
                                        //之前有上传过新增明细单的数据
                                        if (removeRepeatMap.get(key) != null) {
                                            measureDataInSQLite.setISdOrderMeterDtlId(removeRepeatMap.get(key));
                                            measureDataInSQLiteList.set(i, measureDataInSQLite);
                                            continue;
                                        }
                                        List<MeasureOrderInSQLite> addOrderInSQLiteList = measureOrderInSQLiteDao.queryBuilder()
                                                .where(MeasureOrderInSQLiteDao.Properties.UserGUID.eq(userGUID))
                                                .where(MeasureOrderInSQLiteDao.Properties.SPerson.eq(measureDataInSQLite.getPerson()))
                                                .where(MeasureOrderInSQLiteDao.Properties.ISdOrderMeterMstId.eq(measureDataInSQLite.getOrderId()))
                                                .list();
                                        if (addOrderInSQLiteList == null || addOrderInSQLiteList.isEmpty())
                                            continue;
                                        MeasureOrderInSQLite measureOrderInSQLite = addOrderInSQLiteList.get(0);
                                        StringBuilder sbData = new StringBuilder();
                                        sbData.append("EXEC spappAddOneMeasureDtl ")
                                                .append("@isdOrderMeterMstid=").append(measureOrderInSQLite.getISdOrderMeterMstId())
                                                .append(",@sAreaName='").append(measureOrderInSQLite.getSAreaName()).append("'")
                                                .append(",@sCustomerName='").append(measureOrderInSQLite.getSCustomerName()).append("'")
                                                .append(",@sCityName='").append(measureOrderInSQLite.getSCityName()).append("'")
                                                .append(",@sCountyName='").append(measureOrderInSQLite.getSCountyName()).append("'")
                                                .append(",@sDepartmentName='").append(measureOrderInSQLite.getSDepartmentName()).append("'")
                                                .append(",@sJobName='").append(measureOrderInSQLite.getSJobName()).append("'")
                                                .append(",@sName='").append(measureOrderInSQLite.getSPerson()).append("'")
                                                .append(",@sSex='").append(measureDataInSQLite.getSex()).append("'")
                                                .append(",@sQty='").append(measureDataInSQLite.getCount()).append("'")
                                                .append(",@sUserGUID='").append(userGUID).append("'")
                                                .append(",@isdStyleTypeMstId=").append(measureDataInSQLite.getISdStyleTypeMstId())
                                                .append(";");
                                        HsWebInfo info = NewRxjavaWebUtils.getJsonData(getApplicationContext(),
                                                CUS_SERVICE,
                                                sbData.toString(), "",
                                                AddOrderDtlBean.class.getName(),
                                                true, "上传失败！");
                                        if (!info.success) return info;
                                        AddOrderDtlBean addOrderDtlBean = (AddOrderDtlBean) info.wsData.LISTWSDATA.get(0);

                                        removeRepeatMap.put(key, addOrderDtlBean.IIDEN);

                                        //保存本地心增加的明细中添加订单明细ID
                                        List<MeasureRemarkDataInSQLite> measureRemarkDataInSQLiteList = measureRemarkDataInSQLiteDao
                                                .queryBuilder()
                                                .where(MeasureRemarkDataInSQLiteDao.Properties.OrderId.eq(measureDataInSQLite.getOrderId()))
                                                .where(MeasureRemarkDataInSQLiteDao.Properties.StyleId.eq(measureDataInSQLite.getISdStyleTypeMstId()))
                                                .where(MeasureRemarkDataInSQLiteDao.Properties.Person.eq(measureDataInSQLite.getPerson()))
                                                .list();
                                        if (measureRemarkDataInSQLiteList == null)
                                            measureRemarkDataInSQLiteList = new ArrayList<>();
                                        for (int j = 0; j < measureRemarkDataInSQLiteList.size(); j++) {
                                            MeasureRemarkDataInSQLite measureRemarkDataInSQLite = measureRemarkDataInSQLiteList.get(j);
                                            measureRemarkDataInSQLite.setIOrderDtlId(addOrderDtlBean.IIDEN);
                                            measureRemarkDataInSQLiteList.set(j, measureRemarkDataInSQLite);
                                        }

                                        measureRemarkDataInSQLiteDao.insertOrReplaceInTx(measureRemarkDataInSQLiteList);
                                        measureDataInSQLite.setISdOrderMeterDtlId(addOrderDtlBean.IIDEN);
                                        measureDataInSQLiteList.set(i, measureDataInSQLite);
                                    }
                                    measureDataInSQLiteDao.insertOrReplaceInTx(measureDataInSQLiteList);

                                } catch (Exception e) {
                                    HsWebInfo info = new HsWebInfo();
                                    info.success = false;
                                    info.error.error = "上传失败！";
                                    return info;
                                }
                                return new HsWebInfo();
                            }
                        })
                        //其次上传量体数据以及备注信息
                        .map(new Func1<HsWebInfo, HsWebInfo>() {
                            @Override
                            public HsWebInfo call(HsWebInfo info) {
                                if(!info.success) return info;
                                StringBuilder sbData = new StringBuilder();
                                String userGUID = LGSPUtils.getLocalData(getApplicationContext(), USER_GUID, String.class.getName(), "").toString();
                                DaoSession daoSession = GreenDaoUtil.getGreenDaoSession(getApplicationContext());
                                MeasureDataInSQLiteDao measureDataInSQLiteDao = daoSession.getMeasureDataInSQLiteDao();
                                MeasureRemarkDataInSQLiteDao measureRemarkDataInSQLiteDao = daoSession.getMeasureRemarkDataInSQLiteDao();
                                List<MeasureDataInSQLite> measureDataInSQLiteList = measureDataInSQLiteDao.queryBuilder()
                                        .where(MeasureDataInSQLiteDao.Properties.ISMeterSize.isNotNull())
                                        .where(MeasureDataInSQLiteDao.Properties.ISMeterSize.notIn("", "0"))
                                        .list();
                                if (measureDataInSQLiteList == null)
                                    measureDataInSQLiteList = new ArrayList<>();

                                for (int i = 0; i < measureDataInSQLiteList.size(); i++) {
                                    MeasureDataInSQLite measureDataInSQLite = measureDataInSQLiteList.get(i);
                                    sbData.append("EXEC spappMeasureSaveMeasureData ")
                                            .append("@uHrEmployeeGUID='").append(userGUID).append("'")
                                            .append(",@isdOrderMeterDtlid=").append(measureDataInSQLite.getISdOrderMeterDtlId())
                                            .append(",@isMeterSize=").append(measureDataInSQLite.getISMeterSize())
                                            .append(",@isdStyleTypeItemDtlid=").append(measureDataInSQLite.getSdStyleTypeItemDtlId())
                                            .append(";");
                                }

                                List<MeasureRemarkDataInSQLite> measureRemarkDataInSQLiteList = measureRemarkDataInSQLiteDao
                                        .queryBuilder()
                                        .list();
                                if (measureRemarkDataInSQLiteList == null)
                                    measureRemarkDataInSQLiteList = new ArrayList<>();
                                Map<String, List<MeasureRemarkDataInSQLite>> measureRemarkDataInSQLiteMap = new HashMap<>();
                                for (MeasureRemarkDataInSQLite measureRemarkDataInSQLite : measureRemarkDataInSQLiteList) {
                                    String key = measureRemarkDataInSQLite.getStyleId() + "_" + measureRemarkDataInSQLite.getOrderId();
                                    List<MeasureRemarkDataInSQLite> subList = measureRemarkDataInSQLiteMap.get(key);
                                    if (subList == null) subList = new ArrayList<>();
                                    subList.add(measureRemarkDataInSQLite);
                                    measureRemarkDataInSQLiteMap.put(key, subList);
                                }
                                Iterator<Entry<String, List<MeasureRemarkDataInSQLite>>> itRemark = measureRemarkDataInSQLiteMap.entrySet().iterator();
                                while (itRemark.hasNext()) {
                                    List<MeasureRemarkDataInSQLite> subList = itRemark.next().getValue();
                                    String orderDtlId = "";
                                    StringBuilder sbRemarkId = new StringBuilder();
                                    for (int j = 0; j < subList.size(); j++) {
                                        MeasureRemarkDataInSQLite measureRemarkDataInSQLite = subList.get(j);
                                        orderDtlId = measureRemarkDataInSQLite.getIOrderDtlId();
                                        sbRemarkId.append(measureRemarkDataInSQLite.getIId());
                                        if (j != subList.size() - 1) sbRemarkId.append("@");
                                    }
                                    if (!orderDtlId.isEmpty())
                                        sbData.append("EXEC spappMeasureSaveMeasureRemark ")
                                                .append("@sSdMeterMarkDtlid='").append(sbRemarkId.toString()).append("'")
                                                .append(",@isdOrderMeterDtlid=").append(orderDtlId)
                                                .append("; ");
                                }
                                return getJsonData(getApplicationContext(), CUS_SERVICE,
                                        sbData.toString(), "", MeasureCustomBean.class.getName(), true,
                                        "上传失败！！");
                            }
                        })
                //清空本地数据
                .map(new Func1<HsWebInfo, HsWebInfo>() {
                    @Override
                    public HsWebInfo call(HsWebInfo info) {
                        if(!info.success)return info;
                        clearAllInSQLite();
                        return info;
                    }
                })
                , getApplicationContext(), dialog, new SimpleHsWeb() {
                    @Override
                    public void success(HsWebInfo hsWebInfo) {
                        OthersUtil.ToastMsg(getApplicationContext(), "上传成功！");
                    }
                });
    }


    /**
     * 下载数据（用于离线的查询）
     */
    private void downBaseData() {
//        if (!NetUtil.isNetworkAvailable(getApplicationContext())) {
//            OthersUtil.ToastMsg(getApplicationContext(), "请连接网络进行下载");
//            return;
//        }
//        OthersUtil.showLoadDialog(dialog);
//        NewRxjavaWebUtils.getUIThread(NewRxjavaWebUtils.getObservable(this, "")
//                .map(new Func1<String, HsWebInfo>() {
//                    @Override
//                    public HsWebInfo call(String s) {
//                        //检查有没有下载过数据
//                        HsWebInfo info = new HsWebInfo();
//                        List<RemarkCategoryDataInSQLite> remarkCategoryList = null;
//                        DaoSession daoSession = GreenDaoUtil.getGreenDaoSession(getApplicationContext());
//                        RemarkCategoryDataInSQLiteDao remarkCategoryDataInSQLiteDao = daoSession.getRemarkCategoryDataInSQLiteDao();
//                        try {
//                            remarkCategoryList = remarkCategoryDataInSQLiteDao.queryBuilder().list();
//                        } catch (Exception e) {
//                        }
//                        info.object = remarkCategoryList != null && !remarkCategoryList.isEmpty(); //true 表示已下载
//                        return info;
//                    }
//                }), getApplicationContext(), dialog, new SimpleHsWeb() {
//            @Override
//            public void success(HsWebInfo hsWebInfo) {
//                boolean isDowned = (boolean) hsWebInfo.object;
//                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//                if (isNeedDownAgain) {
//                    //未下载
//                    if (!isDowned) {
//                        builder.setMessage("需要下载基础数据(用于无网操作)");
//                    } else {
//                        builder.setMessage("您已下载了基础数据(用于无网操作)，需要覆盖吗？");
//                        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                            }
//                        });
//                    }
//                } else {
//                    if (isDowned) return;
//                    builder.setMessage("需要下载基础数据(用于无网操作)");
//                }
//
//                builder.setPositiveButton("下载", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        subDownBaseData();
//                    }
//                })
//                        .setCancelable(false)
//                        .show();
//            }
//        });

        Builder builder = new Builder(MainActivity.this);
        builder.setMessage("此“下载”会覆盖之前未上传的数据，请确保之前量体的数据已上传到服务器上，需要下载？");
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });

        builder.setPositiveButton("下载", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                subDownBaseData();
            }
        })
                .setCancelable(false)
                .show();

    }

    /**
     * 清空SQLite
     */
    private void clearAllInSQLite(){
        DaoSession daoSession= GreenDaoUtil.getGreenDaoSession(getApplicationContext());
        MeasureOrderInSQLiteDao measureOrderInSQLiteDao =daoSession.getMeasureOrderInSQLiteDao();
        MeasureRemarkDataInSQLiteDao measureRemarkDataInSQLiteDao=daoSession.getMeasureRemarkDataInSQLiteDao();
        MeasureDataInSQLiteDao measureDataInSQLiteDao=daoSession.getMeasureDataInSQLiteDao();
        RemarkCategoryDataInSQLiteDao remarkCategoryDataInSQLiteDao=daoSession.getRemarkCategoryDataInSQLiteDao();
        RemarkDetailDataInSQLiteDao remarkDetailDataInSQLiteDao=daoSession.getRemarkDetailDataInSQLiteDao();
        MeasureOrderDtlStyleBaseDataInSQLiteDao orderDtlStyleBaseDataInSQLiteDao=daoSession.getMeasureOrderDtlStyleBaseDataInSQLiteDao();
        MeasureOrderDtlRemarkBaseInSQLiteDao orderDtlRemarkBaseInSQLiteDao=daoSession.getMeasureOrderDtlRemarkBaseInSQLiteDao();


        measureOrderInSQLiteDao.deleteAll();
        measureRemarkDataInSQLiteDao.deleteAll();
        measureDataInSQLiteDao.deleteAll();
        remarkCategoryDataInSQLiteDao.deleteAll();
        remarkDetailDataInSQLiteDao.deleteAll();
        orderDtlStyleBaseDataInSQLiteDao.deleteAll();
        orderDtlRemarkBaseInSQLiteDao.deleteAll();
    }

    /**
     * 具体的下载动作
     */
    private void subDownBaseData() {
        OthersUtil.showLoadDialog(dialog);
        NewRxjavaWebUtils.getUIThread(NewRxjavaWebUtils.getObservable(this, "")
                        //删除本地数据
                        .map(new Func1<String, String>() {
                            @Override
                            public String call(String s) {
                                clearAllInSQLite();
                                return s;
                            }
                        })
                        //待量体数据
                        .map(new Func1<String, HsWebInfo>() {
                            @Override
                            public HsWebInfo call(String s) {
                                String userGUID = LGSPUtils.getLocalData(getApplicationContext(), USER_GUID, String.class.getName(), "").toString();
                                HsWebInfo info = NewRxjavaWebUtils.getJsonData(getApplicationContext(), CUS_SERVICE,
                                        "spappMeasureOrderList"
                                        , "iIndex=0" +
                                                ",uUserGUID=" + userGUID +
                                                ",sCustomerName=" + "" +
                                                ",sDepartmentName=" + "" +
                                                ",sSearch=" + "",
                                        StartMeasureBean.class.getName(),
                                        true, "");
                                if (!info.success) return new HsWebInfo();
                                MeasureOrderInSQLiteDao dao = GreenDaoUtil.getGreenDaoSession(getApplicationContext()).getMeasureOrderInSQLiteDao();
                                List<MeasureOrderInSQLite> measureOrderInSQLiteList = new ArrayList<>();
                                for (WsEntity entity : info.wsData.LISTWSDATA) {
                                    StartMeasureBean bean = (StartMeasureBean) entity;
                                    measureOrderInSQLiteList.add(orderBeanToSQLite(bean));
                                }
                                dao.insertOrReplaceInTx(measureOrderInSQLiteList);
                                return downMeasureDataRemark(measureOrderInSQLiteList,userGUID);
                            }

                        })
                        //下载已量体数据
                        .map(new Func1<HsWebInfo, HsWebInfo>() {
                            @Override
                            public HsWebInfo call(HsWebInfo info) {
                                if (!info.success) return info;
                                String userGUID = LGSPUtils.getLocalData(getApplicationContext(), USER_GUID, String.class.getName(), "").toString();
                                info = NewRxjavaWebUtils.getJsonData(getApplicationContext(), CUS_SERVICE,
                                        "spappMeasureOrderList"
                                        , "iIndex=1" +
                                                ",uUserGUID=" + userGUID +
                                                ",sCustomerName=" + "" +
                                                ",sDepartmentName=" + "" +
                                                ",sSearch=" + "",
                                        FinishMeasureBean.class.getName(),
                                        true, "");
                                if (!info.success) return new HsWebInfo();
                                MeasureOrderInSQLiteDao dao = GreenDaoUtil.getGreenDaoSession(getApplicationContext()).getMeasureOrderInSQLiteDao();
                                List<MeasureOrderInSQLite> measureOrderInSQLiteList = new ArrayList<>();
                                for (WsEntity entity : info.wsData.LISTWSDATA) {
                                    FinishMeasureBean bean = (FinishMeasureBean) entity;
                                    measureOrderInSQLiteList.add(orderBeanToSQLite(bean));
                                }
                                dao.insertOrReplaceInTx(measureOrderInSQLiteList);
                                return downMeasureDataRemark(measureOrderInSQLiteList,userGUID);
                            }
                        })
                        //下载返修数据
                        .map(new Func1<HsWebInfo, HsWebInfo>() {
                            @Override
                            public HsWebInfo call(HsWebInfo info) {
                                if (!info.success) return info;
                                String userGUID = LGSPUtils.getLocalData(getApplicationContext(), USER_GUID, String.class.getName(), "").toString();
                                info = NewRxjavaWebUtils.getJsonData(getApplicationContext(), CUS_SERVICE,
                                        "spappMeasureOrderList"
                                        , "iIndex=2" +
                                                ",uUserGUID=" + userGUID +
                                                ",sCustomerName=" + "" +
                                                ",sDepartmentName=" + "" +
                                                ",sSearch=" + "",
                                        RepairRegisterBean.class.getName(),
                                        true, "");
                                if (!info.success) return new HsWebInfo();
                                MeasureOrderInSQLiteDao dao = GreenDaoUtil.getGreenDaoSession(getApplicationContext()).getMeasureOrderInSQLiteDao();
                                List<MeasureOrderInSQLite> measureOrderInSQLiteList = new ArrayList<>();
                                for (WsEntity entity : info.wsData.LISTWSDATA) {
                                    RepairRegisterBean bean = (RepairRegisterBean) entity;
                                    measureOrderInSQLiteList.add(orderBeanToSQLite(bean));
                                }
                                dao.insertOrReplaceInTx(measureOrderInSQLiteList);
                                return downMeasureDataRemark(measureOrderInSQLiteList,userGUID);
                            }
                        })
                //下载备注大类以及备注明细
                .map(new Func1<HsWebInfo, HsWebInfo>() {
                    @Override
                    public HsWebInfo call(HsWebInfo info) {
                        if(!info.success) return info;
                        DaoSession daoSession=GreenDaoUtil.getGreenDaoSession(getApplicationContext());
                        //查询备注大类
                        info = NewRxjavaWebUtils.getJsonData(getApplicationContext(), CUS_SERVICE,
                                "spappQueryRemarkBaseData"
                                , "iIndex=0",
                                RemarkCategoryBaseDataBean.class.getName(),
                                true, "");
                        if (!info.success) return info;
                        List<RemarkCategoryDataInSQLite> remarkCategoryDataInSQLiteList=new ArrayList<>();
                        for(WsEntity entity:info.wsData.LISTWSDATA){
                            RemarkCategoryBaseDataBean bean= (RemarkCategoryBaseDataBean) entity;
                            RemarkCategoryDataInSQLite remarkCategoryDataInSQLite=new RemarkCategoryDataInSQLite();
                            remarkCategoryDataInSQLite.setStyleId(bean.ISDSTYLETYPEMSTID);
                            remarkCategoryDataInSQLite.setSMeterMarkName(bean.SMETERMARKNAME);
                            remarkCategoryDataInSQLite.setSBillNo(bean.SBILLNO);
                            remarkCategoryDataInSQLite.setIId(bean.IID);
                            remarkCategoryDataInSQLiteList.add(remarkCategoryDataInSQLite);
                        }
                        RemarkCategoryDataInSQLiteDao remarkCategoryDataInSQLiteDao=daoSession.getRemarkCategoryDataInSQLiteDao();
                        remarkCategoryDataInSQLiteDao.insertOrReplaceInTx(remarkCategoryDataInSQLiteList);

                        //查询备注明细
                        info = NewRxjavaWebUtils.getJsonData(getApplicationContext(), CUS_SERVICE,
                                "spappQueryRemarkBaseData"
                                , "iIndex=1",
                                RemarkDetailBaseDataBean.class.getName(),
                                true, "");
                        if (!info.success) return info;
                        List<RemarkDetailDataInSQLite> remarkDetailDataInSQLiteList=new ArrayList<>();
                        for(WsEntity entity:info.wsData.LISTWSDATA){
                            RemarkDetailBaseDataBean bean= (RemarkDetailBaseDataBean) entity;
                            RemarkDetailDataInSQLite remarkDetailDataInSQLite=new RemarkDetailDataInSQLite();
                            remarkDetailDataInSQLite.setRemarkCategoryId(bean.ISDSTYLETYPEMSTID);
                            remarkDetailDataInSQLite.setSMeterMarkName(bean.SMETERMARKNAME);
                            remarkDetailDataInSQLite.setSMeterMarkCode(bean.SMETERMARKCODE);
                            remarkDetailDataInSQLite.setIId(bean.IID);
                            remarkDetailDataInSQLiteList.add(remarkDetailDataInSQLite);
                        }
                        RemarkDetailDataInSQLiteDao remarkDetailDataInSQLiteDao=daoSession.getRemarkDetailDataInSQLiteDao();
                        remarkDetailDataInSQLiteDao.insertOrReplaceInTx(remarkDetailDataInSQLiteList);
                        return new HsWebInfo();
                    }
                })
                //订单明细对应的款式基本信息
                .map(new Func1<HsWebInfo, HsWebInfo>() {
                    @Override
                    public HsWebInfo call(HsWebInfo info) {
                        if(!info.success) return info;
                        //查询备注明细
                        info = NewRxjavaWebUtils.getJsonData(getApplicationContext(), CUS_SERVICE,
                                "spappQueryStyleBaseData"
                                , "iIndex=0",
                                MeasureOrderDtlStyleBaseBean.class.getName(),
                                true, "");
                        if(!info.success) return info;
                        MeasureOrderDtlStyleBaseDataInSQLiteDao dao=GreenDaoUtil.getGreenDaoSession(getApplicationContext())
                                .getMeasureOrderDtlStyleBaseDataInSQLiteDao();
                        List<MeasureOrderDtlStyleBaseDataInSQLite> orderDtlStyleBaseBeanDataInSQLiteList=new ArrayList<>();
                        for(WsEntity entity:info.wsData.LISTWSDATA){
                            MeasureOrderDtlStyleBaseBean bean= (MeasureOrderDtlStyleBaseBean) entity;
                            MeasureOrderDtlStyleBaseDataInSQLite orderDtlStyleBaseDataInSQLite=new MeasureOrderDtlStyleBaseDataInSQLite();
                            orderDtlStyleBaseDataInSQLite.setISdStyleTypeItemDtlId(bean.ISDSTYLETYPEITEMDTLID);
                            orderDtlStyleBaseDataInSQLite.setISdStyleTypeMstId(bean.ISDSTYLETYPEMSTID);
                            orderDtlStyleBaseDataInSQLite.setISeq(bean.ISEQ);
                            orderDtlStyleBaseDataInSQLite.setSBillNo(bean.SBILLNO);
                            orderDtlStyleBaseDataInSQLite.setSMeterCode(bean.SMETERCODE);
                            orderDtlStyleBaseDataInSQLite.setSMeterName(bean.SMETERNAME);
                            orderDtlStyleBaseDataInSQLite.setSValueCode(bean.SVALUECODE);
                            orderDtlStyleBaseDataInSQLite.setSValueGroup(bean.SVALUEGROUP);
                            orderDtlStyleBaseBeanDataInSQLiteList.add(orderDtlStyleBaseDataInSQLite);
                        }
                        dao.insertOrReplaceInTx(orderDtlStyleBaseBeanDataInSQLiteList);
                        return new HsWebInfo();
                    }
                })
                //订单明细对应的备注基本信息
                .map(new Func1<HsWebInfo, HsWebInfo>() {
                    @Override
                    public HsWebInfo call(HsWebInfo info) {
                        if(!info.success) return info;
                        //查询备注明细
                        info = NewRxjavaWebUtils.getJsonData(getApplicationContext(), CUS_SERVICE,
                                "spappQueryStyleBaseData",
                                 "iIndex=1",
                                MeasureOrderDtlRemarkBaseBean.class.getName(),
                                true, "");
                        if(!info.success) return info;
                        MeasureOrderDtlRemarkBaseInSQLiteDao dao=GreenDaoUtil.getGreenDaoSession(getApplicationContext())
                                .getMeasureOrderDtlRemarkBaseInSQLiteDao();
                        List<MeasureOrderDtlRemarkBaseInSQLite> list=new ArrayList<>();
                        for(WsEntity entity:info.wsData.LISTWSDATA){
                            MeasureOrderDtlRemarkBaseBean bean= (MeasureOrderDtlRemarkBaseBean) entity;
                            MeasureOrderDtlRemarkBaseInSQLite orderDtlRemarkBaseInSQLite=new MeasureOrderDtlRemarkBaseInSQLite();
                            orderDtlRemarkBaseInSQLite.setIId(bean.IID);
                            orderDtlRemarkBaseInSQLite.setISdStyleTypeMstId(bean.ISDSTYLETYPEMSTID);
                            orderDtlRemarkBaseInSQLite.setSMeterMarkCode(bean.SMETERMARKCODE);
                            orderDtlRemarkBaseInSQLite.setSMeterMarkName(bean.SMETERMARKNAME);
                            list.add(orderDtlRemarkBaseInSQLite);
                        }
                        dao.insertOrReplaceInTx(list);
                        return new HsWebInfo();
                    }
                })


                , getApplicationContext(), dialog, new SimpleHsWeb() {
                    @Override
                    public void success(HsWebInfo hsWebInfo) {
                        OthersUtil.ToastMsg(getApplicationContext(), "下载/更新成功");
                    }

                    @Override
                    public void error(HsWebInfo hsWebInfo, Context context) {
                        OthersUtil.ToastMsg(getApplicationContext(), "下载/更新失败");
                    }
                });
    }


    /**
     * 待量体 已量体 返修网络请求的bean转化为SQLite中
     */
    private MeasureOrderInSQLite orderBeanToSQLite(WsData wsData){
        MeasureOrderInSQLite measureOrderInSQLite = new MeasureOrderInSQLite();
        String userGUID=LGSPUtils.getLocalData(getApplicationContext(),USER_GUID,String.class.getName(),"").toString();
        //待量体
        if (wsData instanceof StartMeasureBean){
            measureOrderInSQLite.setUserGUID(userGUID);
            measureOrderInSQLite.setSPerson(((StartMeasureBean) wsData).SPERSON);
            measureOrderInSQLite.setSDepartmentName(((StartMeasureBean) wsData).SDEPARTMENTNAME);
            measureOrderInSQLite.setSCustomerName(((StartMeasureBean) wsData).SCUSTOMERNAME);
            measureOrderInSQLite.setSCountyName(((StartMeasureBean) wsData).SCOUNTYNAME);
            measureOrderInSQLite.setSCityName(((StartMeasureBean) wsData).SCITYNAME);
            measureOrderInSQLite.setSBillNo(((StartMeasureBean) wsData).SBILLNO);
            measureOrderInSQLite.setSAreaName(((StartMeasureBean) wsData).SAREANAME);
            measureOrderInSQLite.setOrderType(0);
            measureOrderInSQLite.setSCustomerCode(((StartMeasureBean) wsData).SCUSTOMERCODE);
            measureOrderInSQLite.setISdOrderMeterMstId(((StartMeasureBean) wsData).ISDORDERMETERMSTID);
            measureOrderInSQLite.setSJobName(((StartMeasureBean) wsData).SJOBNAME);
            measureOrderInSQLite.setSex(((StartMeasureBean) wsData).SSEX);
            //已量体
        }else if (wsData instanceof FinishMeasureBean){
            measureOrderInSQLite.setUserGUID(userGUID);
            measureOrderInSQLite.setSPerson(((FinishMeasureBean) wsData).SPERSON);
            measureOrderInSQLite.setSDepartmentName(((FinishMeasureBean) wsData).SDEPARTMENTNAME);
            measureOrderInSQLite.setSCustomerName(((FinishMeasureBean) wsData).SCUSTOMERNAME);
            measureOrderInSQLite.setSCountyName(((FinishMeasureBean) wsData).SCOUNTYNAME);
            measureOrderInSQLite.setSCityName(((FinishMeasureBean) wsData).SCITYNAME);
            measureOrderInSQLite.setSBillNo(((FinishMeasureBean) wsData).SBILLNO);
            measureOrderInSQLite.setSAreaName(((FinishMeasureBean) wsData).SAREANAME);
            measureOrderInSQLite.setOrderType(1);
            measureOrderInSQLite.setSCustomerCode(((FinishMeasureBean) wsData).SCUSTOMERCODE);
            measureOrderInSQLite.setISdOrderMeterMstId(((FinishMeasureBean) wsData).ISDORDERMETERMSTID);
            measureOrderInSQLite.setSJobName(((FinishMeasureBean) wsData).SJOBNAME);
            measureOrderInSQLite.setSex(((FinishMeasureBean) wsData).SSEX);
            //返修
        }else if(wsData instanceof RepairRegisterBean){
            measureOrderInSQLite.setUserGUID(userGUID);
            measureOrderInSQLite.setSPerson(((RepairRegisterBean) wsData).SPERSON);
            measureOrderInSQLite.setSDepartmentName(((RepairRegisterBean) wsData).SDEPARTMENTNAME);
            measureOrderInSQLite.setSCustomerName(((RepairRegisterBean) wsData).SCUSTOMERNAME);
            measureOrderInSQLite.setSCountyName(((RepairRegisterBean) wsData).SCOUNTYNAME);
            measureOrderInSQLite.setSCityName(((RepairRegisterBean) wsData).SCITYNAME);
            measureOrderInSQLite.setSBillNo(((RepairRegisterBean) wsData).SBILLNO);
            measureOrderInSQLite.setSAreaName(((RepairRegisterBean) wsData).SAREANAME);
            measureOrderInSQLite.setOrderType(2);
            measureOrderInSQLite.setSCustomerCode(((RepairRegisterBean) wsData).SCUSTOMERCODE);
            measureOrderInSQLite.setISdOrderMeterMstId(((RepairRegisterBean) wsData).ISDORDERMETERMSTID);
            measureOrderInSQLite.setSJobName(((RepairRegisterBean) wsData).SJOBNAME);
            measureOrderInSQLite.setSex(((RepairRegisterBean) wsData).SSEX);
        }
        return measureOrderInSQLite;
    }


    /**
     * 下载量体信息以及备注信息
     * @param measureOrderInSQLiteList
     * @param userGUID
     * @return
     */
    private HsWebInfo downMeasureDataRemark(List<MeasureOrderInSQLite> measureOrderInSQLiteList,String userGUID){
        HsWebInfo info=null;
        MeasureRemarkDataInSQLiteDao measureRemarkDataInSQLiteDao=GreenDaoUtil.getGreenDaoSession(getApplicationContext())
                .getMeasureRemarkDataInSQLiteDao();
        MeasureDataInSQLiteDao measureDataInSQLiteDao=GreenDaoUtil.getGreenDaoSession(getApplicationContext())
                .getMeasureDataInSQLiteDao();

        //获取量体款式以及数据、备注信息
        for(MeasureOrderInSQLite measureOrderInSQLite:measureOrderInSQLiteList){
            List<MeasureDataInSQLite> measureDataInSQLiteList=new ArrayList<>();
            //款式
            info= NewRxjavaWebUtils.getJsonData(getApplicationContext(), CUS_SERVICE,
                    "spappMeasureStyleTypeList",
                    "iIndex=0"
                            +",iOrderType="+ measureOrderInSQLite.getOrderType()+
                            ",iSdOrderMeterMstId=" + measureOrderInSQLite.getISdOrderMeterMstId()+
                            ",sPerson="+measureOrderInSQLite.getSPerson(),
                    MeasureCustomBean.class.getName(),
                    true, "1");
            if(!info.success) return info;
            for(WsEntity entity:info.wsData.LISTWSDATA){
                MeasureCustomBean bean= (MeasureCustomBean) entity;
                MeasureDataInSQLite measureDataInSQLite=new MeasureDataInSQLite();
                measureDataInSQLite.setISMeterSize(bean.ISMETERSIZE);
                measureDataInSQLite.setSValueGroup(bean.SVALUEGROUP);
                measureDataInSQLite.setSValueCode(bean.SVALUECODE);
                measureDataInSQLite.setSMeterName(bean.SMETERNAME);
                measureDataInSQLite.setSMeterCode(bean.SMETERCODE);
                measureDataInSQLite.setSdStyleTypeItemDtlId(bean.SDSTYLETYPEITEMDTLID);
                measureDataInSQLite.setSBillNo(bean.SBILLNO);
                measureDataInSQLite.setISeq(bean.ISEQ);
                measureDataInSQLite.setIStyleseq(bean.ISTYLESEQ);
                measureDataInSQLite.setISdStyleTypeMstId(bean.ISDSTYLETYPEMSTID);
                measureDataInSQLite.setISdOrderMeterDtlId(bean.ISDORDERMETERDTLID);

                measureDataInSQLite.setUserGUID(userGUID);
                measureDataInSQLite.setPerson(measureOrderInSQLite.getSPerson());
                measureDataInSQLite.setType(measureOrderInSQLite.getOrderType());
                measureDataInSQLite.setOrderId(measureOrderInSQLite.getISdOrderMeterMstId());
                measureDataInSQLite.setSFemaleMinLenth(bean.SFEMALEMINLENTH);
                measureDataInSQLite.setSFemaleMaxLenth(bean.SFEMALEMAXLENTH);
                measureDataInSQLite.setSMaleMinLenth(bean.SMALEMINLENTH);
                measureDataInSQLite.setSMaleMaxLenth(bean.SMALEMAXLENTH);
                measureDataInSQLite.setBEvenNo(bean.BEVENNO);
                measureDataInSQLite.setBPoint(bean.BPOINT);

                measureDataInSQLiteList.add(measureDataInSQLite);
            }



            //量体数据
            info= NewRxjavaWebUtils.getJsonData(getApplicationContext(), CUS_SERVICE,
                    "spappMeasureStyleTypeList",
                    "iIndex=1"
                            +",iOrderType="+ measureOrderInSQLite.getOrderType()+
                            ",iSdOrderMeterMstId=" + measureOrderInSQLite.getISdOrderMeterMstId()+
                            ",sPerson="+measureOrderInSQLite.getSPerson(),
                    MeasureDateBean.class.getName(),
                    true, "2");
            if(info.success) {
                //整理填充的量体数字
                Map<String, MeasureDateBean> measureDataMap = new HashMap<>();
                for (WsEntity entity : info.wsData.LISTWSDATA) {
                    MeasureDateBean measureDateBean = (MeasureDateBean) entity;
                    measureDataMap.put(measureDateBean.ISDSTYLETYPEMSTID + "_" + measureDateBean.ISDSTYLETYPEITEMDTLID, measureDateBean);
                }
                for (int i = 0; i < measureDataInSQLiteList.size(); i++) {
                    MeasureDataInSQLite measureDataInSQLite = measureDataInSQLiteList.get(i);
                    MeasureDateBean measureDateBean = measureDataMap.get(measureDataInSQLite.getISdStyleTypeMstId()
                            + "_" + measureDataInSQLite.getSdStyleTypeItemDtlId());
                    measureDataInSQLite.setISMeterSize(measureDateBean == null ? "" : measureDateBean.ISMETERSIZE);
                    measureDataInSQLite.setBupdated(measureDateBean == null ? "" :measureDateBean.BUPDATED);
                    measureDataInSQLiteList.set(i, measureDataInSQLite);
                }
            }
            measureDataInSQLiteDao.insertOrReplaceInTx(measureDataInSQLiteList);


            Map<String,List<MeasureDataInSQLite>> measureStyleDataMap=new HashMap<>();
            for(MeasureDataInSQLite measureDataInSQLite:measureDataInSQLiteList){
                List<MeasureDataInSQLite> subList=measureStyleDataMap.get(measureDataInSQLite.getISdOrderMeterDtlId());
                if(subList==null) subList=new ArrayList<>();
                subList.add(measureDataInSQLite);
                measureStyleDataMap.put(measureDataInSQLite.getISdOrderMeterDtlId(),subList);
            }
            Iterator<Entry<String,List<MeasureDataInSQLite>>> it=measureStyleDataMap.entrySet().iterator();
            List<MeasureRemarkDataInSQLite> measureRemarkDataInSQLiteList=new ArrayList<>();
            while (it.hasNext()){
                List<MeasureDataInSQLite> subList=it.next().getValue();
                if(subList.isEmpty()) continue;
                MeasureDataInSQLite measureDataInSQLite=subList.get(0);
                //备注
                info = NewRxjavaWebUtils.getJsonData(getApplicationContext(), CUS_SERVICE,
                        "spappMeasureStyleTypeList",
                        "iIndex=2" +",iOrderType="+ measureOrderInSQLite.getOrderType()+
                                ",iSdOrderMeterMstId="+measureOrderInSQLite.getISdOrderMeterMstId()+
                                ",iSdOrderMeterDtlId=" + measureDataInSQLite.getISdOrderMeterDtlId() +
                                ",sPerson="+measureOrderInSQLite.getSPerson()+
                                ",isdStyleTypeMstId=" + measureDataInSQLite.getISdStyleTypeMstId(),
                        RemarkSavedBean.class.getName(),
                        true,
                        "3");
                if(!info.success) continue;
                for(WsEntity entity:info.wsData.LISTWSDATA){
                    RemarkSavedBean bean= (RemarkSavedBean) entity;
                    MeasureRemarkDataInSQLite measureRemarkDataInSQLite=new MeasureRemarkDataInSQLite();
                    measureRemarkDataInSQLite.setOrderId(measureOrderInSQLite.getISdOrderMeterMstId());
                    measureRemarkDataInSQLite.setUserGUID(userGUID);
                    measureRemarkDataInSQLite.setPerson(measureOrderInSQLite.getSPerson());
                    measureRemarkDataInSQLite.setType(measureOrderInSQLite.getOrderType());
                    measureRemarkDataInSQLite.setSMeterMarkName(bean.SMETERMARKNAME);
                    measureRemarkDataInSQLite.setIOrderDtlId(bean.ISDORDERMETERDTLID);
                    measureRemarkDataInSQLite.setSMeterMarkCode(bean.SMETERMARKCODE);
                    measureRemarkDataInSQLite.setIId(bean.ISMETERMARKDTLID);
                    measureRemarkDataInSQLiteList.add(measureRemarkDataInSQLite);
                }
            }

            measureRemarkDataInSQLiteDao.insertOrReplaceInTx(measureRemarkDataInSQLiteList);
        }
        if(info==null) info=new HsWebInfo();
        info.success=true;
        return info;
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
