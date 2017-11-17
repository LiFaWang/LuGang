package lugang.app.huansi.net.lugang.fragment;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
import lugang.app.huansi.net.lugang.adapter.FinishAdapter;
import lugang.app.huansi.net.lugang.adapter.LinkageSearchAdapter;
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

public class FinishMeasureFragment extends BaseFragment {
    protected LoadProgressDialog mDialog;
    private FinishMeasureFragmentBinding mFinishMeasureFragmentBinding;
    private FinishAdapter mFinishAdapter;
    private List<String> mCustomerStringList;//单位名称集合
    private List<String> mDepartmentStringList;//部门名称集合
    private List<MeasureOrderInSQLite> measureOrderInSQLiteList;

    private boolean isCustomerCreate = true;
    private boolean isDepartmentCreate = true;
    private ArrayAdapter<String> customerAdapter;//单位的筛选adapter
    private ArrayAdapter<String> spElementAdapter;//部门的筛选的adapter
    private LinkageSearchAdapter mCustomerSearchAdapter;//单位下拉选择
    private LinkageSearchAdapter mLinkageSearchAdapter;//地市下拉选择
    private LinkageSearchAdapter mCountySearchAdapter;//县市下拉选择
    private LinkageSearchAdapter mDepartmentSearchAdapter;//部门下拉选择
    private List<String> mSCustomerNameLists;
    private List<String> mSCityNameLists;
    private List<String> mSCountyNameLists;
    private List<String> mSDepartmentNameLists;

    private List<MeasureOrderInSQLite> mCityList;//根据单位选择的地市集合
    private List<MeasureOrderInSQLite> mCountyList;//根据地市选择的县市集合
    private List<MeasureOrderInSQLite> mDepartmentList;//根据县市选择的部门集合
    private List<MeasureOrderInSQLite> mPersonList;//根据部门选择的姓名的集合

    @Override
    public int getLayout() {
        return R.layout.finish_measure_fragment;
    }

    @Override
    public void init() {
        if (!EventBus.getDefault().isRegistered(this)) EventBus.getDefault().register(this);
        mFinishMeasureFragmentBinding = (FinishMeasureFragmentBinding) viewDataBinding;
        measureOrderInSQLiteList = new ArrayList<>();
        mDialog = new LoadProgressDialog(getActivity());
        mCustomerStringList = new ArrayList<>();
        mSCityNameLists = new ArrayList<>();
        mSCountyNameLists = new ArrayList<>();
        mSDepartmentNameLists=new ArrayList<>();
        mDepartmentStringList = new ArrayList<>();
        mFinishAdapter = new FinishAdapter(measureOrderInSQLiteList, getContext());
        mFinishMeasureFragmentBinding.lvCustomer.setAdapter(mFinishAdapter);

        customerAdapter = new ArrayAdapter<>(getActivity(), R.layout.depart_string_item, R.id.tvDepartString, mCustomerStringList);
        mFinishMeasureFragmentBinding.spCustomer.setAdapter(customerAdapter);

        spElementAdapter = new ArrayAdapter<>(getActivity(), R.layout.element_string_item, R.id.tvElementString, mDepartmentStringList);
        mFinishMeasureFragmentBinding.spDepartment.setAdapter(spElementAdapter);
        mFinishMeasureFragmentBinding.spDepartment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isCustomerCreate) {
                    isCustomerCreate = false;
                    return;
                }
                loadFinishMeasureData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mFinishMeasureFragmentBinding.spCustomer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isDepartmentCreate) {
                    isDepartmentCreate = false;
                    return;
                }
                loadFinishMeasureData();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        /**
         * 筛选查询
         */
        mFinishMeasureFragmentBinding.btnSearch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                List<MeasureOrderInSQLite> personList=new ArrayList<>();
//                loadFinishMeasureData();
            String sSearch = mFinishMeasureFragmentBinding.orderSearch.getText().toString();
                String s = mFinishMeasureFragmentBinding.tvCustomerSearch.getText().toString();
                if (s.equals("单位")){
                    OthersUtil.ToastMsg(getContext(),"请先选择要查询的人员所在单位");
                }else {
                    for (int i = 0; i < mCityList.size(); i++) {
                        if( mCityList.get(i).getSPerson().equals(sSearch)){
                            personList.add(mCityList.get(i));
                        }
                    }
                    if (personList.isEmpty()||personList.size()==0){
                        OthersUtil.ToastMsg(getContext(),"该单位没有您要查找到"+sSearch);
                    }
                    mFinishAdapter.setList(personList);
                    mFinishAdapter.notifyDataSetChanged();
                }



            }
        });

        mFinishMeasureFragmentBinding.srlFinish.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mFinishMeasureFragmentBinding.tvCustomerSearch.setText("单位");
                mFinishMeasureFragmentBinding.tvCitySearch.setText("地市");
                mFinishMeasureFragmentBinding.tvCountySearch.setText("县市");
                mFinishMeasureFragmentBinding.tvDepartmentSearch.setText("部门");
                mFinishAdapter.setList(measureOrderInSQLiteList);
                initSearchData();
                loadFinishMeasureData();
            }
        });

        mFinishMeasureFragmentBinding.tvCustomerSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCustomerSearchAdapter = new LinkageSearchAdapter(mSCustomerNameLists, getContext());
                showPop(v);
            }
        });
        mFinishMeasureFragmentBinding.tvCitySearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLinkageSearchAdapter = new LinkageSearchAdapter(mSCityNameLists, getContext());
                showPop(v);

            }
        });
        mFinishMeasureFragmentBinding.tvCountySearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCountySearchAdapter = new LinkageSearchAdapter(mSCountyNameLists, getContext());
                showPop(v);
            }
        });
        mFinishMeasureFragmentBinding.tvDepartmentSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDepartmentSearchAdapter =new LinkageSearchAdapter(mSDepartmentNameLists,getContext());
                showPop(v);
            }
        });


    }


    /**
     * popWindow
     *
     * @param view
     */
    private void showPop(final View view) {
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.pop_list, null);
        final PopupWindow popupWindow = new PopupWindow(contentView, view.getMeasuredWidth(), 200, true);
        final ListView popListView = (ListView) contentView.findViewById(R.id.lv_pop_clothlist);
        switch (view.getId()) {
            case R.id.tvCustomerSearch:
                popListView.setAdapter(mCustomerSearchAdapter);
                break;
            case R.id.tvCitySearch:
                popListView.setAdapter(mLinkageSearchAdapter);
                break;
            case R.id.tvCountySearch:
                popListView.setAdapter(mCountySearchAdapter);
                break;
            case R.id.tvDepartmentSearch:
                popListView.setAdapter(mDepartmentSearchAdapter);
                break;
        }
        popupWindow.setTouchable(true);
        ColorDrawable dw = new ColorDrawable(0x000);
        popupWindow.setBackgroundDrawable(dw);
        popupWindow.showAsDropDown(view);
//        三级联动
        popListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view2, int position, long id) {

                switch (view.getId()) {
                    case R.id.tvCustomerSearch:
                        String itemCountry = (String) parent.getItemAtPosition(position);
                        mFinishMeasureFragmentBinding.tvCustomerSearch.setText(itemCountry);
                        //单位查询地市
                        mCityList = new ArrayList<>();
                        mCityList.clear();
                        mFinishMeasureFragmentBinding.tvCitySearch.setText("地市");
                        mFinishMeasureFragmentBinding.tvCountySearch.setText("县市");
                        mFinishMeasureFragmentBinding.tvDepartmentSearch.setText("部门");

                        if (mFinishMeasureFragmentBinding.tvCustomerSearch.getText().toString().equals("单位")) {
                            mCityList.addAll(measureOrderInSQLiteList);
                        } else {
                            List<String> sCityString = new ArrayList<>();
                            for (int i = 0; i < measureOrderInSQLiteList.size(); i++) {
                                MeasureOrderInSQLite measureOrderInSQLite = measureOrderInSQLiteList.get(i);
                                if (itemCountry.equals(measureOrderInSQLite.getSCustomerName())) {
                                    mCityList.add(measureOrderInSQLite);
                                }
                            }
                            for (int j = 0; j < mCityList.size(); j++) {
                                String sCityName = mCityList.get(j).getSCityName();
                                sCityString.add(sCityName);
                            }
                            mSCityNameLists = getSingle(sCityString);
                        }
                        mFinishAdapter.setList(mCityList);
                        mFinishAdapter.notifyDataSetChanged();
                        break;
                    case R.id.tvCitySearch:
                        //地市查询县市
                        mCountyList = new ArrayList<>();
                        List<String> sCountyString = new ArrayList<>();
                        String itemCity = (String) parent.getItemAtPosition(position);
                        mFinishMeasureFragmentBinding.tvCitySearch.setText(itemCity);
                        if (mFinishMeasureFragmentBinding.tvCitySearch.getText().toString().equals("地市")&&
                                mFinishMeasureFragmentBinding.tvCustomerSearch.getText().toString().equals("单位")){
//                            mCountyList.clear();
//                            mCountyList.addAll(mCityList);
                            mFinishMeasureFragmentBinding.tvCountySearch.setText("县市");
                            mFinishMeasureFragmentBinding.tvDepartmentSearch.setText("部门");
                        }else {
                            for (int i = 0; i < mCityList.size(); i++) {
                                MeasureOrderInSQLite measureOrderInSQLite = mCityList.get(i);
                                if (itemCity.equals(measureOrderInSQLite.getSCityName())) {
                                    mCountyList.add(measureOrderInSQLite);
                                }
                            }
                            for (int i = 0; i < mCountyList.size(); i++) {
                                String sCountyName = mCountyList.get(i).getSCountyName();
                                sCountyString.add(sCountyName);
                            }
                            mSCountyNameLists=getSingle(sCountyString);
                            mFinishAdapter.setList(mCountyList);
                            mFinishAdapter.notifyDataSetChanged();
                        }


                        break;
                    case R.id.tvCountySearch:
                        //县市查询部门
                        mDepartmentList = new ArrayList<>();
                        List<String> sDepartmentString = new ArrayList<>();
                        String itemCounty = (String) parent.getItemAtPosition(position);
                        mFinishMeasureFragmentBinding.tvCountySearch.setText(itemCounty);
                        for (int i = 0; i < mCountyList.size(); i++) {
                            MeasureOrderInSQLite measureOrderInSQLite = mCountyList.get(i);
                            if (itemCounty.equals(measureOrderInSQLite.getSCountyName())) {
                                mDepartmentList.add(measureOrderInSQLite);
                            }
                        }
                        for (int i = 0; i < mDepartmentList.size(); i++) {
                            String sDepartmentName = mDepartmentList.get(i).getSDepartmentName();
                            sDepartmentString.add(sDepartmentName);
                        }
                        mSDepartmentNameLists=getSingle(sDepartmentString);
                        mFinishAdapter.setList(mDepartmentList);
                        mFinishAdapter.notifyDataSetChanged();
                        break;
                    case R.id.tvDepartmentSearch:
                        //部门查询姓名
                        mPersonList = new ArrayList<>();
                        String itemDepartment = (String) parent.getItemAtPosition(position);
                        mFinishMeasureFragmentBinding.tvDepartmentSearch.setText(itemDepartment);
                        for (int i = 0; i <mDepartmentList.size() ; i++) {
                            MeasureOrderInSQLite measureOrderInSQLite = mDepartmentList.get(i);
                            if (itemDepartment.equals(measureOrderInSQLite.getSDepartmentName())){
                                mPersonList.add(measureOrderInSQLite);
                            }
                        }
                        mFinishAdapter.setList(mPersonList);
                        mFinishAdapter.notifyDataSetChanged();
                        break;

                }

                popupWindow.dismiss();
            }
        });

    }

    public static List<String> getSingle(List<String> sCustomerNameList) {
        List newList = new ArrayList();     //创建新集合
        Iterator it = sCustomerNameList.iterator();   //根据传入的集合(旧集合)获取迭代器
        while (it.hasNext()) {          //遍历老集合
            Object obj = it.next();       //记录每一个元素
            if (!newList.contains(obj)) {      //如果新集合中不包含旧集合中的元素
                newList.add(obj);       //将元素添加
            }
        }
        return newList;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mFinishMeasureFragmentBinding.srlFinish.isRefreshing())
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
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void netChanged(NetConnectionEvent event) {
        try {
            if (!mFinishMeasureFragmentBinding.srlFinish.isRefreshing())
                mFinishMeasureFragmentBinding.srlFinish.setRefreshing(true);
            initSearchData();
            loadFinishMeasureData();
        } catch (Exception e) {
        }
    }


    /**
     * 初始化查询筛选条件
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
                            if (measureOrderInSQLiteList == null)
                                measureOrderInSQLiteList = new ArrayList<>();
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
                            if (measureOrderInSQLiteList == null)
                                measureOrderInSQLiteList = new ArrayList<>();
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

        String sCustomerName = "";
        String sDepartmentName = "";
        try {
            sCustomerName = mCustomerStringList.get(mFinishMeasureFragmentBinding.spCustomer.getSelectedItemPosition());
            sDepartmentName = mDepartmentStringList.get(mFinishMeasureFragmentBinding.spDepartment.getSelectedItemPosition());
        } catch (Exception e) {
        }
        final String sCustomerNameFinal = sCustomerName;
        final String sDepartmentNameFinal = sDepartmentName;
        final String sSearch = mFinishMeasureFragmentBinding.orderSearch.getText().toString();

        NewRxjavaWebUtils.getUIThread(NewRxjavaWebUtils.getObservable(this, "")
                        .map(new Func1<String, HsWebInfo>() {
                            @Override
                            public HsWebInfo call(String s) {
                                HsWebInfo info = null;
                                List<MeasureOrderInSQLite> list = new ArrayList<>();
                                String userGUID = LGSPUtils.getLocalData(getContext(), LGSPUtils.USER_GUID, String.class.getName(), "").toString();
                                //在线查询
                                if (NetUtil.isNetworkAvailable(getContext())) {

                                    info = NewRxjavaWebUtils.getJsonData(getContext(), CUS_SERVICE,
                                            "spappMeasureOrderList"
                                            , "iIndex=1" +
                                                    ",uUserGUID=" + userGUID +
                                                    ",sCustomerName=" + (sCustomerNameFinal.equalsIgnoreCase("所有单位") ? "" : sCustomerNameFinal) +
                                                    ",sDepartmentName=" + (sDepartmentNameFinal.equalsIgnoreCase("所有部门") ? "" : sDepartmentNameFinal) +
                                                    ",sSearch=" + sSearch,
                                            FinishMeasureBean.class.getName(),
                                            true, "");
                                    if (!info.success) return info;
                                    for (WsEntity entity : info.wsData.LISTWSDATA) {
                                        FinishMeasureBean bean = (FinishMeasureBean) entity;
                                        MeasureOrderInSQLite measureOrderInSQLite = new MeasureOrderInSQLite();
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
                                        measureOrderInSQLite.setSex(bean.SSEX);
                                        list.add(measureOrderInSQLite);
                                    }
                                    //离线查询
                                } else {
                                    MeasureOrderInSQLiteDao dao = GreenDaoUtil.getGreenDaoSession(getContext()).getMeasureOrderInSQLiteDao();
                                    QueryBuilder<MeasureOrderInSQLite> queryBuilder = dao.queryBuilder()
                                            .where(MeasureOrderInSQLiteDao.Properties.UserGUID.eq(userGUID))
                                            .where(MeasureOrderInSQLiteDao.Properties.OrderType.eq(1));
                                    if (!sCustomerNameFinal.equalsIgnoreCase("所有单位") && !sCustomerNameFinal.isEmpty()) {
                                        queryBuilder.where(MeasureOrderInSQLiteDao.Properties.SCustomerName.eq(sCustomerNameFinal));
                                    }

                                    if (!sDepartmentNameFinal.equalsIgnoreCase("所有部门") && !sDepartmentNameFinal.isEmpty()) {
                                        queryBuilder.where(MeasureOrderInSQLiteDao.Properties.SDepartmentName.eq(sDepartmentNameFinal));
                                    }
                                    String likeSearch = "%" + sSearch + "%";
                                    queryBuilder.whereOr(MeasureOrderInSQLiteDao.Properties.SAreaName.like(likeSearch),
                                            MeasureOrderInSQLiteDao.Properties.SCityName.like(likeSearch),
                                            MeasureOrderInSQLiteDao.Properties.SCountyName.like(likeSearch),
                                            MeasureOrderInSQLiteDao.Properties.SBillNo.like(likeSearch),
                                            MeasureOrderInSQLiteDao.Properties.SPerson.like(likeSearch));
                                    List<MeasureOrderInSQLite> measureOrderInSQLiteList = queryBuilder.list();
                                    if (measureOrderInSQLiteList == null)
                                        measureOrderInSQLiteList = new ArrayList<>();
                                    list.addAll(measureOrderInSQLiteList);
                                }
                                if (info == null) info = new HsWebInfo();
                                info.object = list;
                                return info;
                            }
                        })
                , getContext(), mDialog, new SimpleHsWeb() {
                    @Override
                    @SuppressWarnings("unchecked")
                    public void success(HsWebInfo hsWebInfo) {
                        measureOrderInSQLiteList.clear();
                        List<MeasureOrderInSQLite> orderInSQLiteList = (List<MeasureOrderInSQLite>) hsWebInfo.object;
                        measureOrderInSQLiteList.addAll(orderInSQLiteList);
                        mFinishAdapter.notifyDataSetChanged();
                        mFinishMeasureFragmentBinding.srlFinish.setRefreshing(false);
                        List<String> mSCustomerString = new ArrayList<>();
                        mSCustomerString.add("单位");
                        for (int i = 0; i < measureOrderInSQLiteList.size(); i++) {
                            String sCustomerName = measureOrderInSQLiteList.get(i).getSCustomerName();
                            mSCustomerString.add(sCustomerName);
                        }
                        mSCustomerNameLists = getSingle(mSCustomerString);
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
        if (EventBus.getDefault().isRegistered(this)) EventBus.getDefault().unregister(this);
    }
}
