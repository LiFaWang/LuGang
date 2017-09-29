package lugang.app.huansi.net.lugang.activity;

import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import huansi.net.qianjingapp.base.NotWebBaseActivity;
import huansi.net.qianjingapp.entity.HsWebInfo;
import huansi.net.qianjingapp.entity.WsEntity;
import huansi.net.qianjingapp.imp.SimpleHsWeb;
import huansi.net.qianjingapp.utils.NewRxjavaWebUtils;
import huansi.net.qianjingapp.utils.OthersUtil;
import huansi.net.qianjingapp.utils.SPHelper;
import lugang.app.huansi.net.lugang.R;
import lugang.app.huansi.net.lugang.bean.NewMeasureBean;
import lugang.app.huansi.net.lugang.databinding.ActivityNewMeasureCustomBinding;
import rx.functions.Func1;

import static huansi.net.qianjingapp.utils.SPHelper.USER_GUID;
import static huansi.net.qianjingapp.utils.WebServices.WebServiceType.CUS_SERVICE;

public class NewMeasureCustomActivity extends NotWebBaseActivity {


    private ActivityNewMeasureCustomBinding mActivityNewMeasureCustomBinding;
    private List<NewMeasureBean> mNewMeasureBeangList;//单位名称集合
    private List<String> mClothStyleStringList;//衣服款式集合
    private List<String> mClothStyleidStringList;//衣服款式id集合
    private Map<String, String> mClothStyleMap;//衣服款式集合
    private EditText mEtAreaName;
    private EditText mEtCityName;
    private EditText mEtCountyName;
    private EditText mEtDepartmentName;
    private EditText mEtJobName;
    private EditText mEtPerson;
    private EditText mEtSex;
    private EditText mEtCount;
    private NewMeasureBean mBean;
    private List<WsEntity> mCustomerNameList;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_new_measure_custom;
    }

    @Override
    public void init() {
        mActivityNewMeasureCustomBinding = (ActivityNewMeasureCustomBinding) viewDataBinding;
        mNewMeasureBeangList = new ArrayList<>();
        mClothStyleStringList = new ArrayList<>();
        mClothStyleidStringList = new ArrayList<>();
        mClothStyleMap = new HashMap<>();

        //新增待量体清单人员条目界面
        mActivityNewMeasureCustomBinding.btnNewMeasure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String sOrderBillNo = mActivityNewMeasureCustomBinding.etOrderSearch.getText().toString();
                if (TextUtils.isEmpty(sOrderBillNo)){
                    OthersUtil.ToastMsg(NewMeasureCustomActivity.this,"请先填写量体清单单号");
                }else {
                    addBaseData(sOrderBillNo);

                }
            }
        });
        final String userGUID = SPHelper.getLocalData(getApplicationContext(), USER_GUID, String.class.getName(), "").toString();
        //上传服务器
        mActivityNewMeasureCustomBinding.btnSaveMeasure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCustomerNameList==null||mCustomerNameList.size()==0)return;
                for (int i = 0; i < mCustomerNameList.size(); i++) {
                    mBean.ETAREANAME = mEtAreaName.getText().toString();
                    mBean.ETCITYNAME = mEtCityName.getText().toString();
                    mBean.ETCOUNTYNAME = mEtCountyName.getText().toString();
                    mBean.ETDEPARTMENTNAME = mEtDepartmentName.getText().toString();
                    mBean.ETJOBNAME = mEtJobName.getText().toString();
                    mBean.ETPERSON = mEtPerson.getText().toString();
                    mBean.ETSEX = mEtSex.getText().toString();
                    mBean.ETCOUNT = mEtCount.getText().toString();
                    mNewMeasureBeangList.add(mBean);
                }

                upDateNewBaseData(userGUID);
            }
        });
    }

    /**
     * 上传服务器新增的待量体人员清单
     */
    private void upDateNewBaseData(final String sUserGUID) {
        OthersUtil.showLoadDialog(mDialog);
        NewRxjavaWebUtils.getUIThread(NewRxjavaWebUtils.getObservable(this, "")
                        .map(new Func1<String, HsWebInfo>() {
                            @Override
                            public HsWebInfo call(String s) {
                                StringBuilder sbStr = new StringBuilder();
                                for (int i = 0; i < mNewMeasureBeangList.size(); i++) {
                                    NewMeasureBean bean = mNewMeasureBeangList.get(i);
                                    sbStr.append("EXEC spappAddOneMeasureDtl ")
                                         .append("@isdOrderMeterMstid=").append(bean.ISDORDERMETERMSTID)
                                         .append(",@sAreaName=").append(bean.ETAREANAME)
                                         .append(",@sCustomerName=").append(bean.SCUSTOMERNAME)
                                         .append(",@sCityName=").append(bean.ETCITYNAME)
                                         .append(",@sCountyName=").append(bean.ETCOUNTYNAME)
                                         .append(",@sDepartmentName=").append(bean.ETDEPARTMENTNAME)
                                         .append(",@sJobName=").append(bean.ETJOBNAME)
                                         .append(",@sName=").append(bean.ETPERSON)
                                         .append(",@sSex=").append(bean.ETSEX)
                                         .append(",@sQty=").append(bean.ETCOUNT)
                                         .append(",@sUserGUID='").append(sUserGUID).append("'")
                                         .append(",@isdStyleTypeMstId=").append(bean.ISDSTYLETYPEMSTID)
                                         .append(";");

                                }
                                return NewRxjavaWebUtils.getJsonData(getApplicationContext(), CUS_SERVICE
                                        , sbStr.toString(), "",
                                        NewMeasureBean.class.getName(),
                                        true, "");
//                                return NewRxjavaWebUtils.getJsonData(getApplicationContext(),CUS_SERVICE
//                                ,"spappAddOneMeasureDtl","isdOrderMeterMstid="+isdOrderMeterMstid
//                                +",sAreaName="+sAreaName+",sCityName="+sCityName
//                                +",sCountyName="+sCountyName+",sDepartmentName="+sDepartmentName
//                                +",sJobName="+sJobName+",sName="+sName+",sSex="+sSex+",sQty="+sQty
//                                +",sUserGUID="+sUserGUID+",isdStyleTypeMstId="+isdStyleTypeMstId,
//                                NewMeasureBean.class.getName(),
//                                true, "");
                            }
                        }), this, mDialog, new SimpleHsWeb() {
                    @Override
                    public void success(HsWebInfo hsWebInfo) {
                        OthersUtil.ToastMsg(NewMeasureCustomActivity.this, "上传成功！！");
                        finish();

                    }
                }
        );

    }

    /**
     * 添加新的量体订单
     */

    @SuppressWarnings("unchecked")
    private void addBaseData(final String sOrderBillNo) {
        mNewMeasureBeangList.clear();
        mClothStyleStringList.clear();
        mClothStyleMap.clear();
        OthersUtil.showLoadDialog(mDialog);
        NewRxjavaWebUtils.getUIThread(NewRxjavaWebUtils.getObservable(this, "")
                //单位名称
                .map(new Func1<String, HsWebInfo>() {
                    @Override
                    public HsWebInfo call(String s) {
                        HsWebInfo hsWebInfo = NewRxjavaWebUtils.getJsonData(getApplicationContext(), CUS_SERVICE,
                                "spappMeasureAddBaseData"
                                , "iIndex=0" + ",sOrderBillNo=" + sOrderBillNo,
                                NewMeasureBean.class.getName(),
                                true, "");
                        if (!hsWebInfo.success) return hsWebInfo;
                        Map<String, Object> map = new HashMap<>();
                        map.put("customerName", hsWebInfo.wsData.LISTWSDATA);
                        hsWebInfo.object = map;
                        return hsWebInfo;
                    }
                })
                //服装类型
                .map(new Func1<HsWebInfo, HsWebInfo>() {
                    @Override
                    public HsWebInfo call(HsWebInfo hsWebInfo) {
                        Map<String, Object> map = (Map<String, Object>) hsWebInfo.object;
                        hsWebInfo = NewRxjavaWebUtils.getJsonData(getApplicationContext(), CUS_SERVICE,
                                "spappMeasureAddBaseData"
                                , "iIndex=1" + ",sOrderBillNo=" + sOrderBillNo,
                                NewMeasureBean.class.getName(),
                                true, ""
                        );
                        map.put("clothStyle", hsWebInfo.wsData.LISTWSDATA);
                        hsWebInfo.object = map;
                        return hsWebInfo;

                    }
                }), this, mDialog, new SimpleHsWeb() {
            @Override
            public void success(HsWebInfo hsWebInfo) {
                Map<String, Object> map = (Map<String, Object>) hsWebInfo.object;
                mCustomerNameList = (List<WsEntity>) map.get("customerName");
                List<WsEntity> clothStyleList = (List<WsEntity>) map.get("clothStyle");
//                //添加服装类型
//                addClothStyle(clothStyleList);
//                //添加单位名称
//                addCustomerName(customerNameList,mClothStyleStringList);
                //新增一行表单
                addMeasurItem(mCustomerNameList, clothStyleList);

            }


        });
    }

    /**
     * 新增一行表单
     *
     * @param
     * @param clothStyleStringList
     */
    private void addMeasurItem(List<WsEntity> customerNameList, final List<WsEntity> clothStyleStringList) {


        for (int i = 0; i < customerNameList.size(); i++) {
            mBean = (NewMeasureBean) customerNameList.get(0);
            addClothStyle(clothStyleStringList);
            View view = View.inflate(this, R.layout.new_measure_item, null);
            TextView tvCustomerName = (TextView) view.findViewById(R.id.tvCustomerName);
            tvCustomerName.setText(mBean.SCUSTOMERNAME);
            Spinner spClothStyle = (Spinner) view.findViewById(R.id.spClothStyle);
            final ArrayAdapter<String> spAdapter = new ArrayAdapter<>(this, R.layout.string_item,
                    R.id.tvString, mClothStyleStringList);
            spClothStyle.setAdapter(spAdapter);
            spClothStyle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    mBean.ISDSTYLETYPEMSTID = mClothStyleidStringList.get(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            mEtAreaName = (EditText) view.findViewById(R.id.etAreaName);
            mEtCityName = (EditText) view.findViewById(R.id.etCityName);
            mEtCountyName = (EditText) view.findViewById(R.id.etCountyName);
            mEtDepartmentName = (EditText) view.findViewById(R.id.etDepartmentName);
            mEtJobName = (EditText) view.findViewById(R.id.etJobName);
            mEtPerson = (EditText) view.findViewById(R.id.etPerson);
            mEtSex = (EditText) view.findViewById(R.id.etSex);
            mEtCount = (EditText) view.findViewById(R.id.etCount);
            mActivityNewMeasureCustomBinding.llNewCustom.addView(view);
        }

    }

    /**
     * 添加服装类型
     *
     * @param clothStyleList
     */
    private void addClothStyle(List<WsEntity> clothStyleList) {

        for (int i = 0; i < clothStyleList.size(); i++) {
            NewMeasureBean bean = (NewMeasureBean) clothStyleList.get(i);

            String isdstyletypemstid = bean.ISDSTYLETYPEMSTID;
            String svaluegroup = bean.SVALUEGROUP;

            mClothStyleMap.put("isdstyletypemstid", isdstyletypemstid);
            mClothStyleMap.put("svaluegroup", svaluegroup);
            mClothStyleStringList.add(mClothStyleMap.get("svaluegroup"));
            mClothStyleidStringList.add(mClothStyleMap.get("isdstyletypemstid"));

        }


    }

    /**
     * 添加单位名称
     *
     * @param customerNameList
     * @param clothStyleStringList
     */
//    private void addCustomerName(List<WsEntity> customerNameList, final List<String> clothStyleStringList) {
//        for (int i = 0; i < customerNameList.size(); i++) {
//            final NewMeasureBean bean = (NewMeasureBean) customerNameList.get(i);
//            View view = View.inflate(this, R.layout.new_measure_item, null);
//            TextView tvCustomerName = (TextView) view.findViewById(R.id.tvCustomerName);
//            tvCustomerName.setText(bean.SCUSTOMERNAME);
//            Spinner spClothStyle = (Spinner) view.findViewById(R.id.spClothStyle);
//            final ArrayAdapter<String> spAdapter = new ArrayAdapter<>(this, R.layout.string_item,
//                    R.id.tvString, clothStyleStringList);
//            spClothStyle.setAdapter(spAdapter);
//            spClothStyle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                @Override
//                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                    bean.ISDSTYLETYPEMSTID = view.toString();
//                }
//
//                @Override
//                public void onNothingSelected(AdapterView<?> parent) {
//
//                }
//            });
////            EditText etAreaName = (EditText) view.findViewById(etAreaName);
////            EditText etCityName = (EditText) view.findViewById(etCityName);
////            EditText etCountyName = (EditText) view.findViewById(etCountyName);
////            EditText etDepartmentName = (EditText) view.findViewById(etDepartmentName);
////            EditText etJobName = (EditText) view.findViewById(etJobName);
////            EditText etPerson = (EditText) view.findViewById(etPerson);
////            EditText etSex = (EditText) view.findViewById(etSex);
////            EditText etCount = (EditText) view.findViewById(etCount);
////            bean.ETAREANAME = etAreaName.getText().toString();
////            bean.ETCITYNAME = etCityName.getText().toString();
////            bean.ETCOUNTYNAME = etCountyName.getText().toString();
////            bean.ETDEPARTMENTNAME = etDepartmentName.getText().toString();
////            bean.ETJOBNAME = etJobName.getText().toString();
////            bean.ETPERSON = etPerson.getText().toString();
////            bean.ETSEX = etSex.getText().toString();
////            bean.ETCOUNT = etCount.getText().toString();
//            mNewMeasureBeangList.add(bean);
//            mActivityNewMeasureCustomBinding.llNewCustom.addView(view);
//        }
//
//    }
}
