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
import lugang.app.huansi.net.lugang.adapter.FininshAdapter;
import lugang.app.huansi.net.lugang.bean.FinishMeasureBean;
import lugang.app.huansi.net.lugang.bean.StartMeasureBean;
import lugang.app.huansi.net.lugang.constant.Constant;
import lugang.app.huansi.net.lugang.databinding.FinishMeasureFragmentBinding;
import rx.functions.Func1;

import static huansi.net.qianjingapp.utils.SPHelper.USER_GUID;
import static huansi.net.qianjingapp.utils.WebServices.WebServiceType.CUS_SERVICE;

/**
 * Created by Tony on 2017/9/12.
 * 测量分类页-已测量
 * 15:19
 */

public class FinishMeasureFragment extends BaseFragment{
    protected LoadProgressDialog mDialog;
    private FinishMeasureFragmentBinding mFinishMeasureFragmentBinding;
    private FininshAdapter mFininshAdapter;
    private String mDepartment;//单位名称
    private String mElement;
    private String mUserGUID;
    private List<String> mDepartStringList;//单位名称集合
    private List<String> mElementStringList;//部门名称集合
    private List<FinishMeasureBean> mFinishMeasureBeanList;

    @Override
    public int getLayout() {
        return R.layout.finish_measure_fragment;
    }
    @Override
    public void init() {
        mFinishMeasureFragmentBinding= (FinishMeasureFragmentBinding) viewDataBinding;
        mUserGUID = SPHelper.getLocalData(getContext(),USER_GUID,String.class.getName(),"").toString();
        mFinishMeasureBeanList = new ArrayList<>();
        mDialog = new LoadProgressDialog(getActivity());
        mDepartStringList = new ArrayList<>();
        mElementStringList = new ArrayList<>();

        initSearchDate(mUserGUID);//初始化查询筛选条件
    }
    @Override
    public void onResume() {
        super.onResume();
        Intent intent = getActivity().getIntent();
        String suserid = intent.getStringExtra(Constant.SUSERID);

        setFinishMeasure(mUserGUID,"", "", "");
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
        mElementStringList.add("所有部门");
        mDepartStringList.add("所有单位");
        NewRxjavaWebUtils.getUIThread(NewRxjavaWebUtils.getObservable(this, "")
                .map(new Func1<String, HsWebInfo>() {
                    @Override
                    public HsWebInfo call(String s) {
                        HsWebInfo hsWebInfo = NewRxjavaWebUtils.getJsonData(getActivity(), CUS_SERVICE,
                                "spappOrderBaseData"
                                , "uUserGUID=" + userGUID + ",iIndex=0" + ",iOrderType=1",
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
                                , "uUserGUID=" + userGUID + ",iIndex=1" + ",iOrderType=1",
                                StartMeasureBean.class.getName(),
                                true, ""
                        );
                        if (!info.success) return info;
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
        mFinishMeasureFragmentBinding.spElement.setAdapter(spElementAdapter);
        mFinishMeasureFragmentBinding.spElement.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mElement = (String) mFinishMeasureFragmentBinding.spElement.getSelectedItem();
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
        mFinishMeasureFragmentBinding.spDepartment.setAdapter(spDepartmentAdapter);
        mFinishMeasureFragmentBinding.spDepartment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mDepartment = (String) mFinishMeasureFragmentBinding.spDepartment.getSelectedItem();
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
                //根据分类筛选显示数据
                if(mDepartment.equals("所有单位"))mDepartment="";
                if(mElement.equals("所有部门"))mElement="";
                String sSearch = mFinishMeasureFragmentBinding.orderSearch.getText().toString();
//                if (TextUtils.isEmpty(sSearch)) {
//                    OthersUtil.ToastMsg(getActivity(), "请输入要查询的清单号");
//                    return;
//                }
                setFinishMeasure(mUserGUID, mDepartment, mElement, sSearch);
            }
        });

    }



    /**
     * 联网获取已量体人数据
     */
    private void setFinishMeasure(final String userGUID, final String sCustomerName, final String sDepartmentName, final String sSearch) {
        mFinishMeasureBeanList.clear();
        OthersUtil.showLoadDialog(mDialog);
        NewRxjavaWebUtils.getUIThread(NewRxjavaWebUtils.getObservable(this, "")
                        .map(new Func1<String, HsWebInfo>() {
                            @Override
                            public HsWebInfo call(String s) {
                                //已量体
                                return NewRxjavaWebUtils.getJsonData(getContext(), CUS_SERVICE,
                                        "spappMeasureOrderList"
                                        , "iIndex=1" + ",uUserGUID=" + userGUID +
                                                ",sCustomerName=" + sCustomerName +
                                                ",sDepartmentName=" + sDepartmentName +
                                                ",sSearch=" + sSearch,
                                        FinishMeasureBean.class.getName(),
                                        true, "");
                            }
                        })
                , getContext(), mDialog, new SimpleHsWeb() {
                    @Override
                    public void success(HsWebInfo hsWebInfo) {
                        List<WsEntity> listwsdata = hsWebInfo.wsData.LISTWSDATA;

                        for (int i = 0; i < listwsdata.size(); i++) {
                            FinishMeasureBean finishMeasureBean = (FinishMeasureBean) listwsdata.get(i);
                            mFinishMeasureBeanList.add(finishMeasureBean);
//                          setMeasureData(finishMeasureBean);
                        }
                       if (mFininshAdapter==null)mFininshAdapter = new FininshAdapter(mFinishMeasureBeanList,getActivity());
                        mFinishMeasureFragmentBinding.lvCustomer.setAdapter(mFininshAdapter);

                    }
                });
    }
//    private void setMeasureData(final FinishMeasureBean finishMeasureBean) {
//        View view = View.inflate(getActivity(), R.layout.finish_measure_item, null);
//        TextView customerName = (TextView) view.findViewById(R.id.customerName);
//        TextView areaName = (TextView) view.findViewById(R.id.areaName);
//        TextView cityName = (TextView) view.findViewById(R.id.cityName);
//        TextView countyName = (TextView) view.findViewById(R.id.countyName);
//        TextView departmentName = (TextView) view.findViewById(R.id.departmentName);
//        TextView person = (TextView) view.findViewById(R.id.person);
//        Button btnMeasure = (Button) view.findViewById(R.id.btnMeasure);
//        customerName.setText(finishMeasureBean.SCUSTOMERNAME);
//        areaName.setText(finishMeasureBean.SAREANAME);
//        cityName.setText(finishMeasureBean.SCITYNAME);
//        countyName.setText(finishMeasureBean.SCOUNTYNAME);
//        departmentName.setText(finishMeasureBean.SDEPARTMENTNAME);
//        person.setText(finishMeasureBean.SPERSON);
//        btnMeasure.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), MeasureCustomActivity.class);
//                intent.putExtra(Constant.SPERSON, finishMeasureBean.SPERSON);
//                intent.putExtra(Constant.SDEPARTMENTNAME, finishMeasureBean.SDEPARTMENTNAME);
//                intent.putExtra(Constant.ISDORDERMETERMSTID,finishMeasureBean.ISDORDERMETERMSTID);
//                intent.putExtra(Constant.SVALUENAME,finishMeasureBean.SVALUENAME);
//                intent.putExtra(Constant.STATUS,finishMeasureBean.STATUS);
//                intent.putExtra(Constant.IORDERTYPE,finishMeasureBean.IORDERTYPE);
//                startActivity(intent);
//            }
//        });
////        mFinishMeasureFragmentBinding.llCustomer.addView(view);
//    }
}
