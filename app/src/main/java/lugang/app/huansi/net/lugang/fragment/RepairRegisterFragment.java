package lugang.app.huansi.net.lugang.fragment;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
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
import lugang.app.huansi.net.lugang.activity.MeasureCustomActivity;
import lugang.app.huansi.net.lugang.bean.RepairRegisterBean;
import lugang.app.huansi.net.lugang.constant.Constant;
import lugang.app.huansi.net.lugang.databinding.RepairRegisterFragmentBinding;
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
    private RepairRegisterFragmentBinding mRegisterFragmentBinding;

    @Override
    public int getLayout() {
        return R.layout.repair_register_fragment;
    }
    @Override
    public void init() {
        if(!EventBus.getDefault().isRegistered(this)) EventBus.getDefault().register(this);
        mDialog = new LoadProgressDialog(getActivity());
        mRegisterFragmentBinding = (RepairRegisterFragmentBinding) viewDataBinding;
        mRegisterFragmentBinding.srlRepair.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadRepairMeasureData();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!mRegisterFragmentBinding.srlRepair.isRefreshing())
            mRegisterFragmentBinding.srlRepair.post(new Runnable() {
                @Override
                public void run() {
                    mRegisterFragmentBinding.srlRepair.setRefreshing(true);
                    loadRepairMeasureData();
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
            if(!mRegisterFragmentBinding.srlRepair.isRefreshing()) mRegisterFragmentBinding.srlRepair.setRefreshing(true);
            loadRepairMeasureData();
        }catch (Exception e){}

    }

    /**
     * 联网获取量体人返修数据
     */
    private void loadRepairMeasureData() {
        mRegisterFragmentBinding.llCustomer.removeAllViews();
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
                        mRegisterFragmentBinding.srlRepair.setRefreshing(false);
                        List<MeasureOrderInSQLite> measureOrderInSQLiteList= (List<MeasureOrderInSQLite>) hsWebInfo.object;
                        for (int i = 0; i < measureOrderInSQLiteList.size(); i++) {
                            MeasureOrderInSQLite measureOrderInSQLite=measureOrderInSQLiteList.get(i);
                            setMeasureData(measureOrderInSQLite);
                        }
                    }

                    @Override
                    public void error(HsWebInfo hsWebInfo, Context context) {
                        mRegisterFragmentBinding.srlRepair.setRefreshing(false);
                    }
                });
    }

    /**
     *
     * @param measureOrderInSQLite
     */
    private void setMeasureData(final MeasureOrderInSQLite measureOrderInSQLite) {
        View view = View.inflate(getActivity(), R.layout.repair_register_item, null);
        TextView customerName = (TextView) view.findViewById(R.id.customerName);
        TextView areaName = (TextView) view.findViewById(R.id.areaName);
        TextView cityName = (TextView) view.findViewById(R.id.cityName);
        TextView countyName = (TextView) view.findViewById(R.id.countyName);
        TextView departmentName = (TextView) view.findViewById(R.id.departmentName);
        TextView person = (TextView) view.findViewById(R.id.person);
        Button btnMeasure = (Button) view.findViewById(R.id.btnMeasure);
        customerName.setText(measureOrderInSQLite.getSCustomerName());
        areaName.setText(measureOrderInSQLite.getSAreaName());
        cityName.setText(measureOrderInSQLite.getSCityName());
        countyName.setText(measureOrderInSQLite.getSCountyName());
        departmentName.setText(measureOrderInSQLite.getSDepartmentName());
        person.setText(measureOrderInSQLite.getSPerson());
        btnMeasure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MeasureCustomActivity.class);
                intent.putExtra(Constant.SPERSON,measureOrderInSQLite.getSPerson());
                intent.putExtra(Constant.SEX, measureOrderInSQLite.getSex());
                intent.putExtra(Constant.SDEPARTMENTNAME,measureOrderInSQLite.getSDepartmentName());
                intent.putExtra(Constant.ISDORDERMETERMSTID,measureOrderInSQLite.getISdOrderMeterMstId());
                intent.putExtra(Constant.IORDERTYPE,2);
                startActivity(intent);
            }
        });
        mRegisterFragmentBinding.llCustomer.addView(view);

    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(EventBus.getDefault().isRegistered(this)) EventBus.getDefault().unregister(this);
    }

}
