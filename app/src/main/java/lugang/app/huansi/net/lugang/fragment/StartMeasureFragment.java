package lugang.app.huansi.net.lugang.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import huansi.net.qianjingapp.entity.HsWebInfo;
import huansi.net.qianjingapp.entity.WsEntity;
import huansi.net.qianjingapp.fragment.BaseFragment;
import huansi.net.qianjingapp.imp.SimpleHsWeb;
import huansi.net.qianjingapp.utils.NewRxjavaWebUtils;
import huansi.net.qianjingapp.utils.OthersUtil;
import huansi.net.qianjingapp.utils.SPHelper;
import huansi.net.qianjingapp.view.LoadProgressDialog;
import lugang.app.huansi.net.lugang.R;
import lugang.app.huansi.net.lugang.activity.NewMeasureCustomActivity;
import lugang.app.huansi.net.lugang.adapter.StartAdapter;
import lugang.app.huansi.net.lugang.bean.StartMeasureBean;
import lugang.app.huansi.net.lugang.constant.Constant;
import lugang.app.huansi.net.lugang.databinding.StartMeasureFragmentBinding;
import rx.functions.Func1;

import static huansi.net.qianjingapp.utils.SPHelper.USER_GUID;
import static huansi.net.qianjingapp.utils.WebServices.WebServiceType.CUS_SERVICE;

/**
 * Created by Tony on 2017/9/12.
 * 测量分类页-未测量
 * 15:19
 */

public class StartMeasureFragment extends BaseFragment {
    protected LoadProgressDialog mDialog;

    private StartMeasureFragmentBinding mStartMeasureFragmentBinding;
    private String mDepartment;//单位名称
    private List<StartMeasureBean> mStartMeasureBeanList;
//    private List<StartMeasureBean> mBeanList;
    private StartAdapter mAdapter;
    private List<String> mDepartStringList;//单位名称集合
    private List<String> mElementStringList;//部门名称集合
    private String mUserGUID;
    private String mElement;

//    public void setStartMeasureBeanList(List<StartMeasureBean> startMeasureBeanList) {
////        mStartMeasureFragmentBinding.lvCustomer.removeAllViews();
////        mStartMeasureBeanList.clear();
////        mStartMeasureBeanList.addAll(startMeasureBeanList);
//
//        mAdapter.setList(startMeasureBeanList);
////        for (StartMeasureBean startMeasureBean : startMeasureBeanList) {
//////            setMeasureData(startMeasureBean);
////            mStartMeasureBeanList.add(startMeasureBean);
////        }
//        mAdapter.notifyDataSetChanged();
//    }

//    public List<StartMeasureBean> getStartMeasureBeanList() {
//        return mStartMeasureBeanList;
//    }

    @Override
    public int getLayout() {
        return R.layout.start_measure_fragment;
    }


    @Override
    public void init() {
        mDialog = new LoadProgressDialog(getActivity());
        mStartMeasureBeanList = new ArrayList<>();
        mDepartStringList = new ArrayList<>();
        mElementStringList = new ArrayList<>();

        mUserGUID = SPHelper.getLocalData(getContext(), USER_GUID, String.class.getName(), "").toString();

        mStartMeasureFragmentBinding = (StartMeasureFragmentBinding) viewDataBinding;
        initSearchDate(mUserGUID);//初始化查询筛选条件


    }

    @Override
    public void onResume() {
        super.onResume();
        Intent intent = getActivity().getIntent();
        String suserid = intent.getStringExtra(Constant.SUSERID);

        setStartMeasure(mUserGUID, "", "", "");

    }


    /**
     * 初始化查询筛选条件
     *
     * @param userGUID
     */
    @SuppressWarnings("unchecked")
    private void initSearchDate(final String userGUID) {
        OthersUtil.showLoadDialog(mDialog);
        mDepartStringList.clear();
        mElementStringList.clear();
        NewRxjavaWebUtils.getUIThread(NewRxjavaWebUtils.getObservable(this, "")
                .map(new Func1<String, HsWebInfo>() {
                    @Override
                    public HsWebInfo call(String s) {
                        HsWebInfo hsWebInfo = NewRxjavaWebUtils.getJsonData(getActivity(), CUS_SERVICE,
                                "spappOrderBaseData"
                                , "uUserGUID=" + userGUID + ",iIndex=0" + ",iOrderType=0",
                                StartMeasureBean.class.getName(),
                                true, ""

                        );
                        if (!hsWebInfo.success) return hsWebInfo;
                        Map<String, Object> map = new HashMap<>();
                        map.put("depart", hsWebInfo.wsData.LISTWSDATA);
                        hsWebInfo.object = map;
                        return hsWebInfo;

                    }
                })
                .map(new Func1<HsWebInfo, HsWebInfo>() {
                    @Override
                    public HsWebInfo call(HsWebInfo info) {
                        HashMap<String, Object> map = (HashMap<String, Object>) info.object;
                        info = NewRxjavaWebUtils.getJsonData(getActivity(), CUS_SERVICE,
                                "spappOrderBaseData"
                                , "uUserGUID=" + userGUID + ",iIndex=1" + ",iOrderType=0",
                                StartMeasureBean.class.getName(),
                                true, ""
                        );
                        map.put("element", info.wsData.LISTWSDATA);
                        info.object = map;
                        return info;

                    }
                }), getActivity(), mDialog, new SimpleHsWeb() {
            @Override
            public void success(HsWebInfo hsWebInfo) {

                HashMap<String, Object> map = (HashMap<String, Object>) hsWebInfo.object;
                //获得单位名称
                List<WsEntity> departDataList = (List<WsEntity>) map.get("depart");
                receiverDepartName(departDataList);
                //获得部门名称
                List<WsEntity> elementDataList = (List<WsEntity>) map.get("element");
                receiverElementName(elementDataList);

            }
        });
    }

    /**
     * 获得部门名称
     *
     * @param elementDataList
     */
    private void receiverElementName(List<WsEntity> elementDataList) {
        mElementStringList.add("所有部门");
        for (int i = 0; i < elementDataList.size(); i++) {
            StartMeasureBean entity = (StartMeasureBean) elementDataList.get(i);
            String sdepartmentname = entity.SDEPARTMENTNAME;
            mElementStringList.add(sdepartmentname);
        }
        final ArrayAdapter<String> spElementAdapter = new ArrayAdapter<>(getActivity(), R.layout.element_string_item,
                R.id.tvElementString, mElementStringList);
        mStartMeasureFragmentBinding.spElement.setAdapter(spElementAdapter);
        mStartMeasureFragmentBinding.spElement.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mElement = (String) mStartMeasureFragmentBinding.spElement.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    /**
     * 获得单位名称
     *
     * @param departDataList
     */
    private void receiverDepartName(List<WsEntity> departDataList) {
        mDepartStringList.add("所有单位");
        for (int i = 0; i < departDataList.size(); i++) {
            StartMeasureBean entity = (StartMeasureBean) departDataList.get(i);
            String scustomername = entity.SCUSTOMERNAME;
            mDepartStringList.add(scustomername);
        }

        final ArrayAdapter<String> spDepartmentAdapter = new ArrayAdapter<>(getActivity(), R.layout.depart_string_item,
                R.id.tvDepartString, mDepartStringList);
        mStartMeasureFragmentBinding.spDepartment.setAdapter(spDepartmentAdapter);
        mStartMeasureFragmentBinding.spDepartment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mDepartment = (String) mStartMeasureFragmentBinding.spDepartment.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        /**
         * 筛选查询
         */
        mStartMeasureFragmentBinding.btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //根据分类筛选显示数据
                if(mDepartment.equals("所有单位"))mDepartment="";
                if(mElement.equals("所有部门"))mElement="";
                String sSearch = mStartMeasureFragmentBinding.orderSearch.getText().toString();
//                if (TextUtils.isEmpty(sSearch)) {
//                    OthersUtil.ToastMsg(getActivity(), "请输入要查询的清单号");
//                    return;
//                }
                setStartMeasure(mUserGUID, mDepartment, mElement, sSearch);
            }
        });
        /**
         * 新增测量人员的界面
         */
        mStartMeasureFragmentBinding.btnNewMeasure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NewMeasureCustomActivity.class);
                startActivity(intent);
            }
        });

    }


    /**
     * 联网获取待量体人数据
     */
    private void setStartMeasure(final String userGUID, final String sCustomerName, final String sDepartmentName, final String sSearch) {
        OthersUtil.showLoadDialog(mDialog);
        mStartMeasureBeanList.clear();
//        mStartMeasureFragmentBinding.llCustomer.removeAllViews();
        NewRxjavaWebUtils.getUIThread(NewRxjavaWebUtils.getObservable(this, "")
                        .map(new Func1<String, HsWebInfo>() {
                            @Override
                            public HsWebInfo call(String s) {
                                //待量体
                                return NewRxjavaWebUtils.getJsonData(getContext(), CUS_SERVICE,
                                        "spappMeasureOrderList"
                                        , "iIndex=0" + ",uUserGUID=" + userGUID +
                                                ",sCustomerName=" + sCustomerName +
                                                ",sDepartmentName=" + sDepartmentName +
                                                ",sSearch=" + sSearch,

                                        StartMeasureBean.class.getName(),
                                        true, "");
                            }
                        })
                , getContext(), mDialog, new SimpleHsWeb() {
                    @Override
                    public void success(HsWebInfo hsWebInfo) {
                        List<WsEntity> listWsdata = hsWebInfo.wsData.LISTWSDATA;

                        for (int i = 0; i < listWsdata.size(); i++) {
                            final StartMeasureBean startMeasureBean = (StartMeasureBean) listWsdata.get(i);
                            mStartMeasureBeanList.add(startMeasureBean);
//                            setMeasureData(startMeasureBean);
                        }
                        mAdapter = new StartAdapter(mStartMeasureBeanList, getActivity());
                        mStartMeasureFragmentBinding.lvCustomer.setAdapter(mAdapter);
                    }
                });

    }

    /**
     * 根据量体清单单号查询
     *
     * @param orderNO
     */
//    private void searchMeasureOrder(String orderNO) {
////        StartMeasureFragment item = ((StartMeasureFragment) ((FragmentPagerAdapter) mMeasureCustomFragmentBinding.vpMeasureCustom.getAdapter()).getItem(0));
////        List<StartMeasureBean> mStartMeasureBeanList = item.getStartMeasureBeanList();
//        if (TextUtils.isEmpty(orderNO)) {
////            item.setStartMeasureBeanList(mStartMeasureBeanList);
//            OthersUtil.ToastMsg(getActivity(), "请输入要查询的清单号");
//            return;
//
//        }
//
//        mBeanList = new ArrayList<>();
//        for (int i = 0; i < mStartMeasureBeanList.size(); i++) {
//            StartMeasureBean sbillno = mStartMeasureBeanList.get(i);
//            if (sbillno.SBILLNO.equals(orderNO)) {
//                mBeanList.add(sbillno);
//            }
//
//        }
//        if (mBeanList != null && mBeanList.size() > 0) {
////            item.setStartMeasureBeanList(mBeanList);
//            mAdapter.setList(mBeanList);
//            mAdapter.notifyDataSetChanged();
//        } else {
//            OthersUtil.ToastMsg(getActivity(), "没有找到清单号哦");
//        }
//
//
//    }

//    private void setMeasureData(final StartMeasureBean startMeasureBean) {
//        if (getStartMeasureBeanList().isEmpty()) return;
//        View view = View.inflate(getActivity(), R.layout.start_measure_item, null);
//        TextView customerName = (TextView) view.findViewById(R.id.customerName);
//        TextView areaName = (TextView) view.findViewById(R.id.areaName);
//        TextView cityName = (TextView) view.findViewById(R.id.cityName);
//        TextView countyName = (TextView) view.findViewById(R.id.countyName);
//        TextView departmentName = (TextView) view.findViewById(R.id.departmentName);
//        TextView person = (TextView) view.findViewById(R.id.person);
//        Button btnMeasure = (Button) view.findViewById(R.id.btnMeasure);
//        customerName.setText(startMeasureBean.SCUSTOMERNAME);
//        areaName.setText(startMeasureBean.SAREANAME);
//        cityName.setText(startMeasureBean.SCITYNAME);
//        countyName.setText(startMeasureBean.SCOUNTYNAME);
//        departmentName.setText(startMeasureBean.SDEPARTMENTNAME);
//        person.setText(startMeasureBean.SPERSON);
//        btnMeasure.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), MeasureCustomActivity.class);
//                intent.putExtra(Constant.SPERSON, startMeasureBean.SPERSON);
//                intent.putExtra(Constant.SDEPARTMENTNAME, startMeasureBean.SDEPARTMENTNAME);
//                intent.putExtra(Constant.ISDORDERMETERMSTID,startMeasureBean.ISDORDERMETERMSTID);
//                intent.putExtra(Constant.SVALUENAME,startMeasureBean.SVALUENAME);
//                startActivity(intent);
//            }
//        });

//        mStartMeasureFragmentBinding.llCustomer.addView(view);

//    }
}
