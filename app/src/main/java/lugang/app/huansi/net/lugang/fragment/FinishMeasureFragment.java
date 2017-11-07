package lugang.app.huansi.net.lugang.fragment;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import huansi.net.qianjingapp.entity.HsWebInfo;
import huansi.net.qianjingapp.entity.WsEntity;
import huansi.net.qianjingapp.fragment.BaseFragment;
import huansi.net.qianjingapp.imp.SimpleHsWeb;
import huansi.net.qianjingapp.utils.NetUtil;
import huansi.net.qianjingapp.utils.NewRxjavaWebUtils;
import huansi.net.qianjingapp.utils.OthersUtil;
import huansi.net.qianjingapp.view.LoadProgressDialog;
import lugang.app.huansi.net.db.MeasureOrderInSQLite;
import lugang.app.huansi.net.greendao.MeasureOrderInSQLiteDao;
import lugang.app.huansi.net.lugang.R;
import lugang.app.huansi.net.lugang.activity.NewMeasureCustomActivity;
import lugang.app.huansi.net.lugang.adapter.FinishAdapter;
import lugang.app.huansi.net.lugang.adapter.StartAdapter;
import lugang.app.huansi.net.lugang.bean.FinishMeasureBean;
import lugang.app.huansi.net.lugang.bean.MeasureOrderCustomerBean;
import lugang.app.huansi.net.lugang.bean.MeasureOrderDepartmentBean;
import lugang.app.huansi.net.lugang.databinding.FinishMeasureFragmentBinding;
import lugang.app.huansi.net.lugang.event.NetConnectionEvent;
import lugang.app.huansi.net.util.GreenDaoUtil;
import lugang.app.huansi.net.util.LGSPUtils;
import rx.functions.Func1;

import static huansi.net.qianjingapp.utils.WebServices.WebServiceType.CUS_SERVICE;
import static lugang.app.huansi.net.util.LGSPUtils.USER_GUID;

/**
 * Created by Tony on 2017/9/12.
 * 测量分类页-已测量
 * 15:19
 */

public class FinishMeasureFragment extends BaseFragment{
    protected LoadProgressDialog mDialog;
    private FinishMeasureFragmentBinding mFinishMeasureFragmentBinding;
    private FinishAdapter mFinishAdapter;
    private List<String> mCustomerStringList;//单位名称集合
    private List<String> mDepartmentStringList;//部门名称集合
    private List<MeasureOrderInSQLite> measureOrderInSQLiteList;

    private boolean isCustomerCreate=true;
    private boolean isDepartmentCreate=true;
    private ArrayAdapter<String> customerAdapter;//单位的筛选adapter
    private ArrayAdapter<String> spElementAdapter;//部门的筛选的adapter

    @Override
    public int getLayout() {
        return R.layout.finish_measure_fragment;
    }
    @Override
    public void init() {
        if(!EventBus.getDefault().isRegistered(this)) EventBus.getDefault().register(this);
        mFinishMeasureFragmentBinding= (FinishMeasureFragmentBinding) viewDataBinding;
        measureOrderInSQLiteList = new ArrayList<>();
        mDialog = new LoadProgressDialog(getActivity());
        mCustomerStringList = new ArrayList<>();
        mDepartmentStringList = new ArrayList<>();
        mFinishAdapter=new FinishAdapter(measureOrderInSQLiteList,getContext());
        mFinishMeasureFragmentBinding.lvCustomer.setAdapter(mFinishAdapter);

        customerAdapter= new ArrayAdapter<>(getActivity(), R.layout.depart_string_item, R.id.tvDepartString, mCustomerStringList);
        mFinishMeasureFragmentBinding.spCustomer.setAdapter(customerAdapter);

        spElementAdapter = new ArrayAdapter<>(getActivity(), R.layout.element_string_item, R.id.tvElementString, mDepartmentStringList);
        mFinishMeasureFragmentBinding.spDepartment.setAdapter(spElementAdapter);
        mFinishMeasureFragmentBinding.spDepartment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(isCustomerCreate){
                    isCustomerCreate=false;
                    return;
                }
                loadFinishMeasureData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        mFinishMeasureFragmentBinding.spCustomer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(isDepartmentCreate){
                    isDepartmentCreate=false;
                    return;
                }
                loadFinishMeasureData();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        /**
         * 筛选查询
         */
        mFinishMeasureFragmentBinding.btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFinishMeasureData();
            }
        });

        mFinishMeasureFragmentBinding.srlFinish.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initSearchData();
                loadFinishMeasureData();
            }
        });

    }
    @Override
    public void onResume() {
        super.onResume();
        if(!mFinishMeasureFragmentBinding.srlFinish.isRefreshing())
            mFinishMeasureFragmentBinding.srlFinish.post(new Runnable() {
                @Override
                public void run() {
                    mFinishMeasureFragmentBinding.srlFinish.setRefreshing(true);
                    initSearchData();
                    loadFinishMeasureData();
                }
            });
    }

    /**
     * 网络切换
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void netChanged(NetConnectionEvent event){
        try {
            if (!mFinishMeasureFragmentBinding.srlFinish.isRefreshing())
                mFinishMeasureFragmentBinding.srlFinish.setRefreshing(true);
            initSearchData();
            loadFinishMeasureData();
        }catch (Exception e){}
    }


    /**
     * 初始化查询筛选条件
     *
     */
    @SuppressWarnings("unchecked")
    private void initSearchData() {
        mCustomerStringList.clear();
        mDepartmentStringList.clear();
        NewRxjavaWebUtils.getUIThread(NewRxjavaWebUtils.getObservable(this, "")
                //单位信息
                .map(new Func1<String, HsWebInfo>() {
                    @Override
                    public HsWebInfo call(String s) {
                        String userGUID = LGSPUtils.getLocalData(getContext(), USER_GUID, String.class.getName(), "").toString();
                        HsWebInfo hsWebInfo = null;
                        List<String> list = new ArrayList<>();
                        if (NetUtil.isNetworkAvailable(getContext())) {
                            hsWebInfo = NewRxjavaWebUtils.getJsonData(getActivity(), CUS_SERVICE,
                                    "spappOrderBaseData"
                                    , "uUserGUID=" + userGUID + ",iIndex=0" + ",iOrderType=1",
                                    MeasureOrderCustomerBean.class.getName(),
                                    true,
                                    ""

                            );
                            if (!hsWebInfo.success) return hsWebInfo;
                            for (WsEntity entity : hsWebInfo.wsData.LISTWSDATA) {
                                MeasureOrderCustomerBean bean = (MeasureOrderCustomerBean) entity;
                                list.add(bean.SCUSTOMERNAME);
                            }
                        } else {
                            List<MeasureOrderInSQLite> measureOrderInSQLiteList = null;
                            try {
                                measureOrderInSQLiteList = GreenDaoUtil.getGreenDaoSession(getContext()).getMeasureOrderInSQLiteDao()
                                        .queryBuilder()
                                        .where(MeasureOrderInSQLiteDao.Properties.OrderType.eq(1))
                                        .where(MeasureOrderInSQLiteDao.Properties.UserGUID.eq(userGUID))
                                        .list();
                            } catch (Exception e) {
                            }
                            if (measureOrderInSQLiteList == null) measureOrderInSQLiteList = new ArrayList<>();
                            Set<String> customerNameSet = new HashSet<>();
                            for (MeasureOrderInSQLite measureOrderInSQLite : measureOrderInSQLiteList) {
                                customerNameSet.add(measureOrderInSQLite.getSCustomerName());
                            }
                            list.addAll(customerNameSet);
                        }
                        if (hsWebInfo == null) hsWebInfo = new HsWebInfo();
                        Map<String, Object> map = new HashMap<>();
                        map.put("depart", list);
                        hsWebInfo.object = map;
                        return hsWebInfo;

                    }
                })
                .map(new Func1<HsWebInfo, HsWebInfo>() {
                    @Override
                    public HsWebInfo call(HsWebInfo info) {
                        //部门信息
                        HashMap<String, Object> map = (HashMap<String, Object>) info.object;
                        String userGUID = LGSPUtils.getLocalData(getContext(), USER_GUID, String.class.getName(), "").toString();
                        List<String> list = new ArrayList<>();
                        if (NetUtil.isNetworkAvailable(getContext())) {
                            info = NewRxjavaWebUtils.getJsonData(getActivity(), CUS_SERVICE,
                                    "spappOrderBaseData"
                                    , "uUserGUID=" + userGUID +
                                            ",iIndex=1" +
                                            ",iOrderType=1",
                                    MeasureOrderDepartmentBean.class.getName(),
                                    true, ""

                            );
                            if (!info.success) return info;
                            for (WsEntity entity : info.wsData.LISTWSDATA) {
                                MeasureOrderDepartmentBean bean = (MeasureOrderDepartmentBean) entity;
                                list.add(bean.SDEPARTMENTNAME);
                            }
                        } else {
                            List<MeasureOrderInSQLite> measureOrderInSQLiteList = null;
                            try {
                                measureOrderInSQLiteList = GreenDaoUtil.getGreenDaoSession(getContext()).getMeasureOrderInSQLiteDao()
                                        .queryBuilder()
                                        .where(MeasureOrderInSQLiteDao.Properties.OrderType.eq(1))
                                        .where(MeasureOrderInSQLiteDao.Properties.UserGUID.eq(userGUID))
                                        .list();
                            } catch (Exception e) {
                            }
                            if (measureOrderInSQLiteList == null) measureOrderInSQLiteList = new ArrayList<>();
                            Set<String> departmentSet = new HashSet<>();
                            for (MeasureOrderInSQLite measureOrderInSQLite : measureOrderInSQLiteList) {
                                departmentSet.add(measureOrderInSQLite.getSDepartmentName());
                            }
                            list.addAll(departmentSet);
                        }
                        info = new HsWebInfo();
                        map.put("element", list);
                        info.object = map;
                        return info;
                    }
                }), getActivity(), mDialog, new SimpleHsWeb() {
            @Override
            public void success(HsWebInfo hsWebInfo) {

                HashMap<String, Object> map = (HashMap<String, Object>) hsWebInfo.object;
                //获得单位名称
                List<String> customerList = (List<String>) map.get("depart");
                receiverDepartName(customerList);
                //获得部门名称
                List<String> departmentList = (List<String>) map.get("element");
                receiverElementName(departmentList);

            }

            @Override
            public void error(HsWebInfo hsWebInfo, Context context) {
                super.error(hsWebInfo, context);
                mCustomerStringList.clear();
                mDepartmentStringList.clear();
                customerAdapter.notifyDataSetChanged();
                spElementAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * 获得部门名称
     */
    private void receiverElementName(List<String> departmentList) {
        mDepartmentStringList.add("所有部门");
        mDepartmentStringList.addAll(departmentList);
        spElementAdapter.notifyDataSetChanged();
    }

    /**
     * 获得单位名称
     *
     */
    private void receiverDepartName(List<String> customerList) {
        mCustomerStringList.add("所有单位");
        mCustomerStringList.addAll(customerList);
        customerAdapter.notifyDataSetChanged();
    }
//    /**
//     * 获得部门名称
//     *
//     */
//    private void receiverElementName(List<String> departmentList) {
//        mDepartmentStringList.add("所有部门");
//        mDepartmentStringList.addAll(departmentList);
//        final ArrayAdapter<String> spElementAdapter = new ArrayAdapter<>(getActivity(), R.layout.element_string_item,
//                R.id.tvElementString, mDepartmentStringList);
//        mFinishMeasureFragmentBinding.spDepartment.setAdapter(spElementAdapter);
//        mFinishMeasureFragmentBinding.spDepartment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                loadFinishMeasureData();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
//    }
//
//    /**
//     * 获得单位名称
//     *
//     */
//    private void receiverDepartName(List<String> customerList) {
//        mCustomerStringList.add("所有单位");
//        mCustomerStringList.addAll(customerList);
//        final ArrayAdapter<String> customerAdapter = new ArrayAdapter<>(getActivity(), R.layout.depart_string_item,
//                R.id.tvDepartString, mCustomerStringList);
//        mFinishMeasureFragmentBinding.spCustomer.setAdapter(customerAdapter);
//        mFinishMeasureFragmentBinding.spCustomer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//
//
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                loadFinishMeasureData();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//            }
//        });
//        /**
//         * 筛选查询
//         */
//        mFinishMeasureFragmentBinding.btnSearch.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                loadFinishMeasureData();
//            }
//        });
//
//    }



    /**
     * 联网获取已量体人数据
     */
    private synchronized void loadFinishMeasureData() {

        String sCustomerName="";
        String sDepartmentName="";
        try {
            sCustomerName=mCustomerStringList.get(mFinishMeasureFragmentBinding.spCustomer.getSelectedItemPosition());
            sDepartmentName=mDepartmentStringList.get(mFinishMeasureFragmentBinding.spDepartment.getSelectedItemPosition());
        }catch (Exception e){}
        final String sCustomerNameFinal=sCustomerName;
        final String sDepartmentNameFinal=sDepartmentName;
        final String sSearch=mFinishMeasureFragmentBinding.orderSearch.getText().toString();

        NewRxjavaWebUtils.getUIThread(NewRxjavaWebUtils.getObservable(this, "")
                        .map(new Func1<String, HsWebInfo>() {
                            @Override
                            public HsWebInfo call(String s) {
                                HsWebInfo info=null;
                                List<MeasureOrderInSQLite> list=new ArrayList<>();
                                String userGUID= LGSPUtils.getLocalData(getContext(), LGSPUtils.USER_GUID,String.class.getName(),"").toString();
                                //在线查询
                                if(NetUtil.isNetworkAvailable(getContext())) {

                                    info=NewRxjavaWebUtils.getJsonData(getContext(), CUS_SERVICE,
                                            "spappMeasureOrderList"
                                            , "iIndex=1" +
                                                    ",uUserGUID=" + userGUID +
                                                    ",sCustomerName=" + (sCustomerNameFinal.equalsIgnoreCase("所有单位")?"":sCustomerNameFinal) +
                                                    ",sDepartmentName=" + (sDepartmentNameFinal.equalsIgnoreCase("所有部门")?"":sDepartmentNameFinal) +
                                                    ",sSearch=" + sSearch,
                                            FinishMeasureBean.class.getName(),
                                            true, "");
                                    if(!info.success) return info;
                                    for(WsEntity entity:info.wsData.LISTWSDATA){
                                        FinishMeasureBean bean= (FinishMeasureBean) entity;
                                        MeasureOrderInSQLite measureOrderInSQLite=new MeasureOrderInSQLite();
                                        measureOrderInSQLite.setISdOrderMeterMstId(bean.ISDORDERMETERMSTID);
                                        measureOrderInSQLite.setOrderType(1);
                                        measureOrderInSQLite.setSAreaName(bean.SAREANAME);
                                        measureOrderInSQLite.setSBillNo(bean.SBILLNO);
                                        measureOrderInSQLite.setSCityName(bean.SCITYNAME);
                                        measureOrderInSQLite.setSCountyName(bean.SCOUNTYNAME);
                                        measureOrderInSQLite.setSCustomerName(bean.SCUSTOMERNAME);
                                        measureOrderInSQLite.setSDepartmentName(bean.SDEPARTMENTNAME);
                                        measureOrderInSQLite.setSPerson(bean.SPERSON);
                                        measureOrderInSQLite.setUserGUID(userGUID);
                                        list.add(measureOrderInSQLite);
                                    }
                                    //离线查询
                                }else {
                                    MeasureOrderInSQLiteDao dao = GreenDaoUtil.getGreenDaoSession(getContext()).getMeasureOrderInSQLiteDao();
//
                                    QueryBuilder<MeasureOrderInSQLite> queryBuilder = dao.queryBuilder()
                                            .where(MeasureOrderInSQLiteDao.Properties.UserGUID.eq(userGUID))
                                            .where(MeasureOrderInSQLiteDao.Properties.OrderType.eq(1));
                                    if (!sCustomerNameFinal.equalsIgnoreCase("所有单位")&&!sCustomerNameFinal.isEmpty()) {
                                        queryBuilder.where(MeasureOrderInSQLiteDao.Properties.SCustomerName.eq(sCustomerNameFinal));
                                    }

                                    if (!sDepartmentNameFinal.equalsIgnoreCase("所有部门")&&!sDepartmentNameFinal.isEmpty()) {
                                        queryBuilder.where(MeasureOrderInSQLiteDao.Properties.SDepartmentName.eq(sDepartmentNameFinal));
                                    }
                                    String likeSearch="%"+sSearch+"%";
                                    queryBuilder.whereOr(MeasureOrderInSQLiteDao.Properties.SAreaName.like(likeSearch),
                                            MeasureOrderInSQLiteDao.Properties.SCityName.like(likeSearch),
                                            MeasureOrderInSQLiteDao.Properties.SCountyName.like(likeSearch),
                                            MeasureOrderInSQLiteDao.Properties.SBillNo.like(likeSearch),
                                            MeasureOrderInSQLiteDao.Properties.SPerson.like(likeSearch));
                                    List<MeasureOrderInSQLite> measureOrderInSQLiteList=queryBuilder.list();
                                    if (measureOrderInSQLiteList == null) measureOrderInSQLiteList = new ArrayList<>();
                                    list.addAll(measureOrderInSQLiteList);
                                }
                                if(info==null) info=new HsWebInfo();
                                info.object=list;
                                return info;


                            }
                        })
                , getContext(), mDialog, new SimpleHsWeb() {
                    @Override
                    @SuppressWarnings("unchecked")
                    public void success(HsWebInfo hsWebInfo) {
                        measureOrderInSQLiteList.clear();
                        List<MeasureOrderInSQLite> orderInSQLiteList= (List<MeasureOrderInSQLite>) hsWebInfo.object;
                        measureOrderInSQLiteList.addAll(orderInSQLiteList);
                        mFinishAdapter.notifyDataSetChanged();
                        mFinishMeasureFragmentBinding.srlFinish.setRefreshing(false);
                    }

                    @Override
                    public void error(HsWebInfo hsWebInfo, Context context) {
                        measureOrderInSQLiteList.clear();
                        mFinishAdapter.notifyDataSetChanged();
                        mFinishMeasureFragmentBinding.srlFinish.setRefreshing(false);
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(EventBus.getDefault().isRegistered(this)) EventBus.getDefault().unregister(this);
    }
}
