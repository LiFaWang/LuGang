package lugang.app.huansi.net.lugang.fragment;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
import lugang.app.huansi.net.lugang.adapter.LinkageSearchAdapter;
import lugang.app.huansi.net.lugang.adapter.RepairAdapter;
import lugang.app.huansi.net.lugang.bean.RepairRegisterBean;
import lugang.app.huansi.net.lugang.databinding.RepairFragmentBinding;
import lugang.app.huansi.net.lugang.event.NetConnectionEvent;
import lugang.app.huansi.net.util.GreenDaoUtil;
import lugang.app.huansi.net.util.LGSPUtils;
import rx.functions.Func1;

import static huansi.net.qianjingapp.utils.WebServices.WebServiceType.CUS_SERVICE;

/**
 * Created by Tony on 2017/9/9.
 * 11:30
 * 返修
 */

public class RepairRegisterFragment extends BaseFragment {
    protected LoadProgressDialog mDialog;
    private List<MeasureOrderInSQLite> mRepairMeasureInSQLiteList;
    private RepairFragmentBinding mRepairFragmentBinding;
    private List<String> mSCustomerNameLists;
    private List<String> mSCityNameLists;
    private List<String> mSCountyNameLists;
    private List<String> mSDepartmentNameLists;
    private LinkageSearchAdapter mCustomerSearchAdapter;//单位下拉选择
    private LinkageSearchAdapter mLinkageSearchAdapter;//地市下拉选择
    private LinkageSearchAdapter mCountySearchAdapter;//县市下拉选择
    private LinkageSearchAdapter mDepartmentSearchAdapter;//部门下拉选择

    private List<MeasureOrderInSQLite> mCityList;//根据单位选择的地市集合
    private List<MeasureOrderInSQLite> mCountyList;//根据地市选择的县市集合
    private List<MeasureOrderInSQLite> mDepartmentList;//根据县市选择的部门集合
    private List<MeasureOrderInSQLite> mPersonList;//根据部门选择的姓名的集合
    private RepairAdapter mRepairAdapter;

    @Override
    public int getLayout() {
        return R.layout.repair_fragment;
    }
    @Override
    public void init() {
        mRepairMeasureInSQLiteList = new ArrayList<>();
        if(!EventBus.getDefault().isRegistered(this)) EventBus.getDefault().register(this);
        mDialog = new LoadProgressDialog(getActivity());

        mRepairFragmentBinding = (RepairFragmentBinding) viewDataBinding;
        mSCityNameLists = new ArrayList<>();
        mSCountyNameLists = new ArrayList<>();
        mSDepartmentNameLists=new ArrayList<>();

//        mRegisterFragmentBinding.srlRepair.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                loadRepairMeasureData();
//            }
//        });
        mRepairAdapter = new RepairAdapter(mRepairMeasureInSQLiteList,getContext());
        mRepairFragmentBinding.lvCustomer.setAdapter(mRepairAdapter);
        /**
         * 筛选查询
         */
        mRepairFragmentBinding.btnSearch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                List<MeasureOrderInSQLite> personList=new ArrayList<>();
//                loadFinishMeasureData();
                String sSearch = mRepairFragmentBinding.orderSearch.getText().toString();
                String s = mRepairFragmentBinding.tvCustomerSearch.getText().toString();
                if (s.equals("单位")){
                    OthersUtil.ToastMsg(getContext(),"请先选择要查询的人员所在单位");
                }else {
                    for (int i = 0; i < mCityList.size(); i++) {
                        if( mCityList.get(i).getSPerson().contains(sSearch)){
                            personList.add(mCityList.get(i));
                        }
                    }
                    if (personList.isEmpty()||personList.size()==0){
                        OthersUtil.ToastMsg(getContext(),"该单位没有您要查找到"+sSearch);
                    }
                    mRepairAdapter.setList(personList);
                    mRepairAdapter.notifyDataSetChanged();
                }

                mRepairFragmentBinding.orderSearch.setText("");

            }
        });
        mRepairFragmentBinding.tvCustomerSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCustomerSearchAdapter = new LinkageSearchAdapter(mSCustomerNameLists, getContext());
                showPop(v);
            }
        });
        mRepairFragmentBinding.tvCitySearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLinkageSearchAdapter = new LinkageSearchAdapter(mSCityNameLists, getContext());
                showPop(v);

            }
        });
        mRepairFragmentBinding.tvCountySearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCountySearchAdapter = new LinkageSearchAdapter(mSCountyNameLists, getContext());
                showPop(v);
            }
        });
        mRepairFragmentBinding.tvDepartmentSearch.setOnClickListener(new View.OnClickListener() {
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
                        mRepairFragmentBinding.tvCustomerSearch.setText(itemCountry);
                        //单位查询地市
                        mCityList = new ArrayList<>();
                        mCityList.clear();
                        mRepairFragmentBinding.tvCitySearch.setText("地市");
                        mRepairFragmentBinding.tvCountySearch.setText("县市");
                        mRepairFragmentBinding.tvDepartmentSearch.setText("部门");

                        if (mRepairFragmentBinding.tvCustomerSearch.getText().toString().equals("单位")) {
                            mCityList.addAll(mRepairMeasureInSQLiteList);
                        } else {
                            List<String> sCityString = new ArrayList<>();
                            for (int i = 0; i < mRepairMeasureInSQLiteList.size(); i++) {
                                MeasureOrderInSQLite measureOrderInSQLite = mRepairMeasureInSQLiteList.get(i);
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
                        mRepairAdapter.setList(mCityList);
                        mRepairAdapter.notifyDataSetChanged();
                        break;
                    case R.id.tvCitySearch:
                        //地市查询县市
                        mCountyList = new ArrayList<>();
                        List<String> sCountyString = new ArrayList<>();
                        String itemCity = (String) parent.getItemAtPosition(position);
                        mRepairFragmentBinding.tvCitySearch.setText(itemCity);
                        if (mRepairFragmentBinding.tvCitySearch.getText().toString().equals("地市")&&
                                mRepairFragmentBinding.tvCustomerSearch.getText().toString().equals("单位")){
//                            mCountyList.clear();
//                            mCountyList.addAll(mCityList);
                            mRepairFragmentBinding.tvCountySearch.setText("县市");
                            mRepairFragmentBinding.tvDepartmentSearch.setText("部门");
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
                            mRepairAdapter.setList(mCountyList);
                            mRepairAdapter.notifyDataSetChanged();
                        }


                        break;
                    case R.id.tvCountySearch:
                        //县市查询部门
                        mDepartmentList = new ArrayList<>();
                        List<String> sDepartmentString = new ArrayList<>();
                        String itemCounty = (String) parent.getItemAtPosition(position);
                        mRepairFragmentBinding.tvCountySearch.setText(itemCounty);
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
                        mRepairAdapter.setList(mDepartmentList);
                        mRepairAdapter.notifyDataSetChanged();
                        break;
                    case R.id.tvDepartmentSearch:
                        //部门查询姓名
                        mPersonList = new ArrayList<>();
                        String itemDepartment = (String) parent.getItemAtPosition(position);
                        mRepairFragmentBinding.tvDepartmentSearch.setText(itemDepartment);
                        for (int i = 0; i <mDepartmentList.size() ; i++) {
                            MeasureOrderInSQLite measureOrderInSQLite = mDepartmentList.get(i);
                            if (itemDepartment.equals(measureOrderInSQLite.getSDepartmentName())){
                                mPersonList.add(measureOrderInSQLite);
                            }
                        }
                        mRepairAdapter.setList(mPersonList);
                        mRepairAdapter.notifyDataSetChanged();
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
//        if(!mRegisterFragmentBinding.srlRepair.isRefreshing())
//            mRegisterFragmentBinding.srlRepair.post(new Runnable() {
//                @Override
//                public void run() {
//                    mRegisterFragmentBinding.srlRepair.setRefreshing(true);
//                    loadRepairMeasureData();
//                }
//            });
                    loadRepairMeasureData();
    }

    /**
     * 网络切换
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void netChanged(NetConnectionEvent event){
        try {
//            if(!mRegisterFragmentBinding.srlRepair.isRefreshing()) mRegisterFragmentBinding.srlRepair.setRefreshing(true);
            loadRepairMeasureData();
        }catch (Exception e){}

    }

    /**
     * 联网获取量体人返修数据
     */
    private void loadRepairMeasureData() {
        OthersUtil.showLoadDialog(mDialog);
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
                                            , "iIndex=2" +
                                                    ",uUserGUID=" + userGUID +
                                                    ",sCustomerName=" + "" +
                                                    ",sDepartmentName=" + "" +
                                                    ",sSearch=" + "",
                                            RepairRegisterBean.class.getName(),
                                            true, "");
                                    if(!info.success) return info;
                                    for(WsEntity entity:info.wsData.LISTWSDATA){
                                        RepairRegisterBean bean= (RepairRegisterBean) entity;
                                        MeasureOrderInSQLite measureOrderInSQLite=new MeasureOrderInSQLite();
                                        measureOrderInSQLite.setISdOrderMeterMstId(bean.ISDORDERMETERMSTID);
                                        measureOrderInSQLite.setOrderType(2);
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
                                }else {
                                    MeasureOrderInSQLiteDao dao= GreenDaoUtil.getGreenDaoSession(getContext()).getMeasureOrderInSQLiteDao();
                                    List<MeasureOrderInSQLite> measureOrderInSQLiteList=dao.queryBuilder()
                                            .where(MeasureOrderInSQLiteDao.Properties.UserGUID.eq(userGUID))
                                            .where(MeasureOrderInSQLiteDao.Properties.OrderType.eq(2))
                                            .build()
                                            .list();
                                    if(measureOrderInSQLiteList==null) measureOrderInSQLiteList=new ArrayList<>();
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
//                        mRegisterFragmentBinding.srlRepair.setRefreshing(false);
                        OthersUtil.dismissLoadDialog(mDialog);
                        List<MeasureOrderInSQLite> orderInSQLiteList= (List<MeasureOrderInSQLite>) hsWebInfo.object;
                        mRepairMeasureInSQLiteList.clear();
                        mRepairMeasureInSQLiteList.addAll(orderInSQLiteList);
                        mRepairAdapter.notifyDataSetChanged();
                        //  mFinishMeasureFragmentBinding.srlFinish.setRefreshing(false);
                        List<String> mSCustomerString = new ArrayList<>();
                        mSCustomerString.add("单位");
                        for (int i = 0; i < mRepairMeasureInSQLiteList.size(); i++) {
                            String sCustomerName = mRepairMeasureInSQLiteList.get(i).getSCustomerName();
                            mSCustomerString.add(sCustomerName);
                        }
                        mSCustomerNameLists = getSingle(mSCustomerString);
                    }

                    @Override
                    public void error(HsWebInfo hsWebInfo, Context context) {
//                        mRegisterFragmentBinding.srlRepair.setRefreshing(false);
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(EventBus.getDefault().isRegistered(this)) EventBus.getDefault().unregister(this);
    }

}
