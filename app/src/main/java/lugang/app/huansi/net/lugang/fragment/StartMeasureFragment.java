package lugang.app.huansi.net.lugang.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
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
import lugang.app.huansi.net.lugang.activity.NewMeasureCustomActivity;
import lugang.app.huansi.net.lugang.adapter.LinkageSearchAdapter;
import lugang.app.huansi.net.lugang.adapter.StartAdapter;
import lugang.app.huansi.net.lugang.bean.MeasureOrderCustomerBean;
import lugang.app.huansi.net.lugang.bean.MeasureOrderDepartmentBean;
import lugang.app.huansi.net.lugang.bean.StartMeasureBean;
import lugang.app.huansi.net.lugang.databinding.StartMeasureFragmentBinding;
import lugang.app.huansi.net.lugang.event.NetConnectionEvent;
import lugang.app.huansi.net.util.GreenDaoUtil;
import lugang.app.huansi.net.util.LGSPUtils;
import rx.functions.Func1;

import static huansi.net.qianjingapp.utils.WebServices.WebServiceType.CUS_SERVICE;
import static lugang.app.huansi.net.lugang.constant.Constant.NewMeasureCustomActivityConstant.TVCITYSEARCH;
import static lugang.app.huansi.net.lugang.constant.Constant.NewMeasureCustomActivityConstant.TVCOUNTYSEARCH;
import static lugang.app.huansi.net.lugang.constant.Constant.NewMeasureCustomActivityConstant.TVCUSTOMERSEARCH;
import static lugang.app.huansi.net.lugang.constant.Constant.NewMeasureCustomActivityConstant.TVDEPARTMENTSEARCH;
import static lugang.app.huansi.net.util.LGSPUtils.USER_GUID;

/**
 * Created by Tony on 2017/9/12.
 * 测量分类页-未测量
 * 15:19
 */

public class StartMeasureFragment extends BaseFragment {
    protected LoadProgressDialog mDialog;

    private StartMeasureFragmentBinding mStartMeasureFragmentBinding;
    private List<MeasureOrderInSQLite> measureOrderInSQLiteList;

    private StartAdapter mStartAdapter;
    private List<String> mCustomerStringList;//单位名称集合
    private List<String> mDepartmentStringList;//部门名称集合
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
    private List<MeasureOrderInSQLite> mShortlisted;//筛选后的所有数据集合

    @Override
    public int getLayout() {
        return R.layout.start_measure_fragment;
    }


    @Override
    public void init() {
        if (!EventBus.getDefault().isRegistered(this)) EventBus.getDefault().register(this);
        mStartMeasureFragmentBinding = (StartMeasureFragmentBinding) viewDataBinding;
        mDialog = new LoadProgressDialog(getActivity());
        measureOrderInSQLiteList = new ArrayList<>();
        mCustomerStringList = new ArrayList<>();
        mDepartmentStringList = new ArrayList<>();
        mCustomerStringList = new ArrayList<>();
        mSCityNameLists = new ArrayList<>();
        mSCountyNameLists = new ArrayList<>();
        mSDepartmentNameLists = new ArrayList<>();
        mShortlisted = new ArrayList<>();

        mStartAdapter = new StartAdapter(measureOrderInSQLiteList, getContext());
        mStartMeasureFragmentBinding.lvCustomer.setAdapter(mStartAdapter);

        customerAdapter = new ArrayAdapter<>(getActivity(), R.layout.depart_string_item, R.id.tvDepartString, mCustomerStringList);
        mStartMeasureFragmentBinding.spCustomer.setAdapter(customerAdapter);

        spElementAdapter = new ArrayAdapter<>(getActivity(), R.layout.element_string_item, R.id.tvElementString, mDepartmentStringList);
        mStartMeasureFragmentBinding.spDepartment.setAdapter(spElementAdapter);
        mStartMeasureFragmentBinding.spDepartment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isCustomerCreate) {
                    isCustomerCreate = false;
                    return;
                }
                loadMeasureData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mStartMeasureFragmentBinding.spCustomer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isDepartmentCreate) {
                    isDepartmentCreate = false;
                    return;
                }
                loadMeasureData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        /**
         * 模糊查询
         */
        mStartMeasureFragmentBinding.btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                loadMeasureData();
                String s = mStartMeasureFragmentBinding.tvCustomerSearch.getText().toString();
                if (s.equals("单位")) {
                    OthersUtil.ToastMsg(getContext(), "请先选择要查询的人员所在单位");
                    return;
                }
                List<MeasureOrderInSQLite> personList = new ArrayList<>();
                String sSearch = mStartMeasureFragmentBinding.orderSearch.getText().toString();
                if (!sSearch.isEmpty()){

                    for (int i = 0; i < mCityList.size(); i++) {
                        if (mCityList.get(i).getSPerson().contains(sSearch)) {
                            personList.add(mCityList.get(i));
                        }
                    }

                    if (personList.isEmpty() || personList.size() == 0) {
                        OthersUtil.ToastMsg(getContext(), "该单位没有您要查找到" + sSearch);
                    }
                    mStartAdapter.setList(personList);
                    mStartAdapter.notifyDataSetChanged();

                    mStartMeasureFragmentBinding.orderSearch.setText("");
                }

            }
        });
        /**
         * 新增测量人员的界面
         */
        mStartMeasureFragmentBinding.btnNewMeasure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), NewMeasureCustomActivity.class);
                String tvCustomerSearch = mStartMeasureFragmentBinding.tvCustomerSearch.getText().toString();
                String tvCitySearch = mStartMeasureFragmentBinding.tvCitySearch.getText().toString();
                String tvCountySearch = mStartMeasureFragmentBinding.tvCountySearch.getText().toString();
                String tvDepartmentSearch = mStartMeasureFragmentBinding.tvDepartmentSearch.getText().toString();
                if (tvCustomerSearch.equals("单位") || tvCitySearch.equals("地市")
                        || tvCountySearch.equals("县市")
                        || tvDepartmentSearch.equals("部门")) {
                    OthersUtil.ToastMsg(getContext(), "请先把筛选条件精确到部门");
                } else {
                    intent.putExtra(TVCUSTOMERSEARCH, tvCustomerSearch);
                    intent.putExtra(TVCITYSEARCH, tvCitySearch);
                    intent.putExtra(TVCOUNTYSEARCH, tvCountySearch);
                    intent.putExtra(TVDEPARTMENTSEARCH, tvDepartmentSearch);

                    startActivity(intent);
                }
            }
        });
//        mStartMeasureFragmentBinding.srlStart.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                mStartMeasureFragmentBinding.tvCustomerSearch.setText("单位");
//                mStartMeasureFragmentBinding.tvCitySearch.setText("地市");
//                mStartMeasureFragmentBinding.tvCountySearch.setText("县市");
//                mStartMeasureFragmentBinding.tvDepartmentSearch.setText("部门");
//                mStartAdapter.setList(measureOrderInSQLiteList);
//                initSearchData();
//                loadMeasureData();
//            }
//        });
        mStartMeasureFragmentBinding.tvCustomerSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCustomerSearchAdapter = new LinkageSearchAdapter(mSCustomerNameLists, getContext());
                showPop(v);
            }
        });
        mStartMeasureFragmentBinding.tvCitySearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLinkageSearchAdapter = new LinkageSearchAdapter(mSCityNameLists, getContext());
                showPop(v);

            }
        });
        mStartMeasureFragmentBinding.tvCountySearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCountySearchAdapter = new LinkageSearchAdapter(mSCountyNameLists, getContext());
                showPop(v);
            }
        });
        mStartMeasureFragmentBinding.tvDepartmentSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDepartmentSearchAdapter = new LinkageSearchAdapter(mSDepartmentNameLists, getContext());
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
                        mStartMeasureFragmentBinding.tvCustomerSearch.setText(itemCountry);
                        //单位查询地市
                        mCityList = new ArrayList<>();
                        mCityList.clear();
                        mStartMeasureFragmentBinding.tvCitySearch.setText("地市");
                        mStartMeasureFragmentBinding.tvCountySearch.setText("县市");
                        mStartMeasureFragmentBinding.tvDepartmentSearch.setText("部门");

                        if (mStartMeasureFragmentBinding.tvCustomerSearch.getText().toString().equals("单位")) {
                            mCityList.addAll(measureOrderInSQLiteList);
                        } else {
                            List<String> sCityString = new ArrayList<>();
                            for (int i = 0; i < measureOrderInSQLiteList.size(); i++) {
                                MeasureOrderInSQLite measureOrderInSQLite = measureOrderInSQLiteList.get(i);
                                if (itemCountry.equals(measureOrderInSQLite.getSCustomerName())) {
                                    mCityList.add(measureOrderInSQLite);

                                }

                            }
                            mShortlisted.addAll(mCityList);
                            for (int j = 0; j < mCityList.size(); j++) {
                                String sCityName = mCityList.get(j).getSCityName();
                                sCityString.add(sCityName);
                            }
                            mSCityNameLists = getSingle(sCityString);
                        }
                        mStartAdapter.setList(mCityList);
                        mStartAdapter.notifyDataSetChanged();
                        break;
                    case R.id.tvCitySearch:
                        //地市查询县市
                        mCountyList = new ArrayList<>();
                        List<String> sCountyString = new ArrayList<>();
                        String itemCity = (String) parent.getItemAtPosition(position);
                        mStartMeasureFragmentBinding.tvCitySearch.setText(itemCity);
                        if (mStartMeasureFragmentBinding.tvCitySearch.getText().toString().equals("地市") &&
                                mStartMeasureFragmentBinding.tvCustomerSearch.getText().toString().equals("单位")) {
//                            mCountyList.clear();
//                            mCountyList.addAll(mCityList);
                            mStartMeasureFragmentBinding.tvCountySearch.setText("县市");
                            mStartMeasureFragmentBinding.tvDepartmentSearch.setText("部门");
                        } else {
                            for (int i = 0; i < mCityList.size(); i++) {
                                MeasureOrderInSQLite measureOrderInSQLite = mCityList.get(i);
                                if (itemCity.equals(measureOrderInSQLite.getSCityName())) {
                                    mCountyList.add(measureOrderInSQLite);
                                }
                            }
                            mShortlisted.clear();
                            mShortlisted.addAll(mCountyList);
                            for (int i = 0; i < mCountyList.size(); i++) {
                                String sCountyName = mCountyList.get(i).getSCountyName();
                                sCountyString.add(sCountyName);
                            }
                            mSCountyNameLists = getSingle(sCountyString);
                            mStartAdapter.setList(mCountyList);
                            mStartAdapter.notifyDataSetChanged();
                        }


                        break;
                    case R.id.tvCountySearch:
                        //县市查询部门
                        mDepartmentList = new ArrayList<>();
                        List<String> sDepartmentString = new ArrayList<>();
                        String itemCounty = (String) parent.getItemAtPosition(position);
                        mStartMeasureFragmentBinding.tvCountySearch.setText(itemCounty);
                        for (int i = 0; i < mCountyList.size(); i++) {
                            MeasureOrderInSQLite measureOrderInSQLite = mCountyList.get(i);
                            if (itemCounty.equals(measureOrderInSQLite.getSCountyName())) {
                                mDepartmentList.add(measureOrderInSQLite);
                            }
                        }
                        mShortlisted.clear();
                        mShortlisted.addAll(mDepartmentList);
                        for (int i = 0; i < mDepartmentList.size(); i++) {
                            String sDepartmentName = mDepartmentList.get(i).getSDepartmentName();
                            sDepartmentString.add(sDepartmentName);
                        }
                        mSDepartmentNameLists = getSingle(sDepartmentString);
                        mStartAdapter.setList(mDepartmentList);
                        mStartAdapter.notifyDataSetChanged();
                        break;
                    case R.id.tvDepartmentSearch:
                        //部门查询姓名
                        mPersonList = new ArrayList<>();
                        String itemDepartment = (String) parent.getItemAtPosition(position);
                        mStartMeasureFragmentBinding.tvDepartmentSearch.setText(itemDepartment);
                        for (int i = 0; i < mDepartmentList.size(); i++) {
                            MeasureOrderInSQLite measureOrderInSQLite = mDepartmentList.get(i);
                            if (itemDepartment.equals(measureOrderInSQLite.getSDepartmentName())) {
                                mPersonList.add(measureOrderInSQLite);
                            }
                        }
                        mShortlisted.clear();
                        mShortlisted.addAll(mDepartmentList);
                        mStartAdapter.setList(mPersonList);
                        mStartAdapter.notifyDataSetChanged();
                        break;

                }

                popupWindow.dismiss();
            }
        });

    }


    public static List<String> getSingle(List<String> sCustomerNameList) {
        List newList = new ArrayList();     //创建新集合
        Iterator it = sCustomerNameList.iterator();        //根据传入的集合(旧集合)获取迭代器
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
//        if(!mStartMeasureFragmentBinding.srlStart.isRefreshing())
//            mStartMeasureFragmentBinding.srlStart.post(new Runnable() {
//                @Override
//                public void run() {
//                    mStartMeasureFragmentBinding.srlStart.setRefreshing(true);
//                    initSearchData();
//                    loadMeasureData();
//                }
//            });

        loadMeasureData();
//        initSearchData();
        showQuantity();
        redisplay(); //重新获得上次筛选的数据集合


    }

    /**
     * 重新获得上次筛选的数据集合
     */
    private void redisplay() {
//        String tvCustomerSearch = mStartMeasureFragmentBinding.tvCustomerSearch.getText().toString();
//        String tvCitySearch = mStartMeasureFragmentBinding.tvCitySearch.getText().toString();
//        String tvCountySearch = mStartMeasureFragmentBinding.tvCountySearch.getText().toString();
//        String tvDepartmentSearch = mStartMeasureFragmentBinding.tvDepartmentSearch.getText().toString();
//        if(tvCustomerSearch.equals("单位")){
//            mCityList.addAll(measureOrderInSQLiteList);
//            mStartAdapter.setList(mCityList);
//        }else if(tvCitySearch.equals("地市")){
//            for (int i = 0; i < mCityList.size(); i++) {
//                MeasureOrderInSQLite measureOrderInSQLite = mCityList.get(i);
//                if (tvCitySearch.equals(measureOrderInSQLite.getSCityName())) {
//                    mCountyList.add(measureOrderInSQLite);
//                }
//            }
//            mStartAdapter.setList(mCountyList);
//
//        }else if (tvCountySearch.equals("县市")){
//            for (int i = 0; i < mCountyList.size(); i++) {
//                MeasureOrderInSQLite measureOrderInSQLite = mCountyList.get(i);
//                if (tvCountySearch.equals(measureOrderInSQLite.getSCountyName())) {
//                    mDepartmentList.add(measureOrderInSQLite);
//                }
//            }
//            mStartAdapter.setList(mDepartmentList);
//
//
//
//        } else if (tvDepartmentSearch.equals("部门")){
//            for (int i = 0; i < mDepartmentList.size(); i++) {
//                MeasureOrderInSQLite measureOrderInSQLite = mDepartmentList.get(i);
//                if (tvDepartmentSearch.equals(measureOrderInSQLite.getSDepartmentName())) {
//                    mPersonList.add(measureOrderInSQLite);
//                }
//            }
//            mStartAdapter.setList(mPersonList);
//
//
//        }
        if (mShortlisted.size()>0&&mShortlisted!=null){
            mStartAdapter.setList(mShortlisted);
            mStartAdapter.notifyDataSetChanged();
        }

    }

    /**
     * 查询数量（未量体）
     */
    private void showQuantity() {
        OthersUtil.showLoadDialog(mDialog);
        final String userGUID = LGSPUtils.getLocalData(getContext(), USER_GUID, String.class.getName(), "").toString();

        NewRxjavaWebUtils.getUIThread(NewRxjavaWebUtils.getObservable(this, "")
                .map(new Func1<String, HsWebInfo>() {
                    @Override
                    public HsWebInfo call(String s) {
                        return NewRxjavaWebUtils.getJsonData(getContext(),CUS_SERVICE,"spappMeasureQueryQty",
                                "uUserGUID="+userGUID,


                                MeasureOrderCustomerBean.class.getName(),
                                true,
                                "");
                    }
                }), getContext(), mDialog, new SimpleHsWeb() {
            @Override
            public void success(HsWebInfo hsWebInfo) {
                List<WsEntity> listwsdata = hsWebInfo.wsData.LISTWSDATA;
                MeasureOrderCustomerBean bean = (MeasureOrderCustomerBean) listwsdata.get(0);
                mStartMeasureFragmentBinding.tvCount.setText("待量体人数:"+bean.INOTMEASUREDQTY);
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
//            if (!mStartMeasureFragmentBinding.srlStart.isRefreshing())
//                mStartMeasureFragmentBinding.srlStart.setRefreshing(true);
            initSearchData();
            loadMeasureData();
        } catch (Exception e) {

        }
    }


    /**
     * 初始化查询筛选条件
     */
    @SuppressWarnings("unchecked")
    private void initSearchData() {

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
                                    , "uUserGUID=" + userGUID +
                                            ",iIndex=0" +
                                            ",iOrderType=0",
                                    MeasureOrderCustomerBean.class.getName(),
                                    true,
                                    "");
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
                                        .where(MeasureOrderInSQLiteDao.Properties.OrderType.eq(0))
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
                                            ",iOrderType=0",
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
                                        .where(MeasureOrderInSQLiteDao.Properties.OrderType.eq(0))
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
                        map.put("element", list);
                        info.object = map;
                        return info;
                    }
                }), getActivity(), mDialog, new SimpleHsWeb() {
            @Override
            public void success(HsWebInfo hsWebInfo) {
                mCustomerStringList.clear();
                mDepartmentStringList.clear();

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


    /**
     * 联网获取待量体人数据
     */
    private synchronized void loadMeasureData() {
        OthersUtil.showLoadDialog(mDialog);
        String sCustomerName = "";
        String sDepartmentName = "";
        try {
            sCustomerName = mCustomerStringList.get(mStartMeasureFragmentBinding.spCustomer.getSelectedItemPosition());
            sDepartmentName = mDepartmentStringList.get(mStartMeasureFragmentBinding.spDepartment.getSelectedItemPosition());
        } catch (Exception e) {
        }
        final String sCustomerNameFinal = sCustomerName;
        final String sDepartmentNameFinal = sDepartmentName;
        final String sSearch = mStartMeasureFragmentBinding.orderSearch.getText().toString();
        NewRxjavaWebUtils.getUIThread(NewRxjavaWebUtils.getObservable(this, "")
                        .map(new Func1<String, HsWebInfo>() {
                            @Override
                            public HsWebInfo call(String s) {
                                HsWebInfo info = null;
                                List<MeasureOrderInSQLite> list = new ArrayList<>();
                                String userGUID = LGSPUtils.getLocalData(getContext(), USER_GUID, String.class.getName(), "").toString();
                                //在线查询
                                if (NetUtil.isNetworkAvailable(getContext())) {
                                    info = NewRxjavaWebUtils.getJsonData(getContext(), CUS_SERVICE,
                                            "spappMeasureOrderList"
                                            , "iIndex=0" +
                                                    ",uUserGUID=" + userGUID +
                                                    ",sCustomerName=" + (sCustomerNameFinal.equalsIgnoreCase("所有单位") ? "" : sCustomerNameFinal) +
                                                    ",sDepartmentName=" + (sDepartmentNameFinal.equalsIgnoreCase("所有部门") ? "" : sDepartmentNameFinal) +
                                                    ",sSearch=" + sSearch,
                                            StartMeasureBean.class.getName(),
                                            true, "");
                                    if (!info.success) return info;
                                    for (WsEntity entity : info.wsData.LISTWSDATA) {
                                        StartMeasureBean bean = (StartMeasureBean) entity;
                                        MeasureOrderInSQLite measureOrderInSQLite = new MeasureOrderInSQLite();
                                        measureOrderInSQLite.setISdOrderMeterMstId(bean.ISDORDERMETERMSTID);
                                        measureOrderInSQLite.setOrderType(0);
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
//
                                    QueryBuilder<MeasureOrderInSQLite> queryBuilder = dao.queryBuilder()
                                            .where(MeasureOrderInSQLiteDao.Properties.UserGUID.eq(userGUID))
                                            .where(MeasureOrderInSQLiteDao.Properties.OrderType.eq(0));
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
                        OthersUtil.dismissLoadDialog(mDialog);
                        measureOrderInSQLiteList.clear();
                        List<MeasureOrderInSQLite> orderInSQLiteList = (List<MeasureOrderInSQLite>) hsWebInfo.object;
                        measureOrderInSQLiteList.addAll(orderInSQLiteList);
                        mStartAdapter.notifyDataSetChanged();
//                        mStartMeasureFragmentBinding.srlStart.setRefreshing(false);
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
                        mStartAdapter.notifyDataSetChanged();
//                        mStartMeasureFragmentBinding.srlStart.setRefreshing(false);
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (EventBus.getDefault().isRegistered(this)) EventBus.getDefault().unregister(this);
    }
}
