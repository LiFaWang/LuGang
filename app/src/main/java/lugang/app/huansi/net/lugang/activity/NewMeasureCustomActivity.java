package lugang.app.huansi.net.lugang.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import huansi.net.qianjingapp.base.NotWebBaseActivity;
import huansi.net.qianjingapp.entity.HsWebInfo;
import huansi.net.qianjingapp.entity.WsEntity;
import huansi.net.qianjingapp.imp.SimpleHsWeb;
import huansi.net.qianjingapp.listener.WebListener;
import huansi.net.qianjingapp.utils.NetUtil;
import huansi.net.qianjingapp.utils.NewRxjavaWebUtils;
import huansi.net.qianjingapp.utils.OthersUtil;
import lugang.app.huansi.net.db.MeasureDataInSQLite;
import lugang.app.huansi.net.db.MeasureOrderDtlStyleBaseDataInSQLite;
import lugang.app.huansi.net.db.MeasureOrderInSQLite;
import lugang.app.huansi.net.greendao.DaoSession;
import lugang.app.huansi.net.greendao.MeasureDataInSQLiteDao;
import lugang.app.huansi.net.greendao.MeasureOrderDtlStyleBaseDataInSQLiteDao;
import lugang.app.huansi.net.greendao.MeasureOrderInSQLiteDao;
import lugang.app.huansi.net.lugang.R;
import lugang.app.huansi.net.lugang.adapter.HsArrayAdapter;
import lugang.app.huansi.net.lugang.adapter.NewMeasureCustomAdapter;
import lugang.app.huansi.net.lugang.bean.NewMeasureBean;
import lugang.app.huansi.net.lugang.bean.ObtainNewMeasureOrderNoBean;
import lugang.app.huansi.net.lugang.databinding.ActivityNewMeasureCustomBinding;
import lugang.app.huansi.net.util.GreenDaoUtil;
import lugang.app.huansi.net.util.LGSPUtils;
import rx.functions.Func1;

import static huansi.net.qianjingapp.utils.WebServices.WebServiceType.CUS_SERVICE;
import static lugang.app.huansi.net.lugang.constant.Constant.NewMeasureCustomActivityConstant.TVCITYSEARCH;
import static lugang.app.huansi.net.lugang.constant.Constant.NewMeasureCustomActivityConstant.TVCOUNTYSEARCH;
import static lugang.app.huansi.net.lugang.constant.Constant.NewMeasureCustomActivityConstant.TVCUSTOMERSEARCH;
import static lugang.app.huansi.net.lugang.constant.Constant.NewMeasureCustomActivityConstant.TVDEPARTMENTSEARCH;
import static lugang.app.huansi.net.util.LGSPUtils.USER_GUID;

public class NewMeasureCustomActivity extends NotWebBaseActivity {
    private ActivityNewMeasureCustomBinding mActivityNewMeasureCustomBinding;
    //    private List<String> mClothStyleStringList;//衣服款式集合
//    private List<String> mClothStyleidStringList;//衣服款式id集合
//    private Map<String, String> mClothStyleMap;//衣服款式集合
//    private EditText mEtPerson;
//    private String mSpSex;
//    private EditText mEtCount;
    private List<ObtainNewMeasureOrderNoBean> mObtainNewMeasureOrderNoBeanList;//清单号的集合

    private boolean isFirstClick = false;

//    private String mMeasureNoSelected;

//    private TextView mTvCustomerName;//单位名称
//    private String mIsdstyletypemstid;//款式的id
//    private String mIsdordermetermstid;//订单的id
//    private  boolean isFirstClick;//是否第一次点击

//    private AutoCompleteTextView mActAreaName;//地区的名称
//    private AutoCompleteTextView mActCityName;//城市的名称
//    private AutoCompleteTextView mActCountyName;//县城的名称
//    private AutoCompleteTextView mActDepartmentName;//部门的名称
//    private AutoCompleteTextView mActJobName;//岗位的名称


    private List<NewMeasureBean> clothStyleList = new ArrayList<>();//服装类型 筛选数据
    //    private List<String> departmentNameList=new ArrayList<>();//部门的筛选数据
//    private List<String> areaList=new ArrayList<>();//地区的筛选数据
//    private List<String> cityList=new ArrayList<>();//城市的筛选数据
//    private List<String> countryList=new ArrayList<>();//县城的筛选数据
//    private List<String> jobList=new ArrayList<>();//职位的筛选数据
    private String[] sexArr = {"男", "女"};
    private RecyclerView mRecyclerView;
    private List<String> mClothList;
    private Map<String, String> mNewMeasureClothTypeMap;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_new_measure_custom;
    }

    @Override
    public void init() {
        mActivityNewMeasureCustomBinding = (ActivityNewMeasureCustomBinding) viewDataBinding;
//        mClothStyleStringList = new ArrayList<>();
//        mClothStyleidStringList = new ArrayList<>();
        mObtainNewMeasureOrderNoBeanList = new ArrayList<>();
//        mClothStyleMap = new HashMap<>();
        mNewMeasureClothTypeMap = new HashMap<>();
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        NewMeasureCustomAdapter adapter = new NewMeasureCustomAdapter();
        mRecyclerView.setAdapter(adapter);
        clothStyleList = new ArrayList<>();
        mClothList = new ArrayList<>();
        //获取量体清单编号
        initBaseData();



        //上传服务器
        mActivityNewMeasureCustomBinding.btnSaveMeasure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upDateNewBaseData();
            }
        });
    }

    /**
     * 初始化数据
     */
    private void initBaseData() {
        OthersUtil.showLoadDialog(mDialog);
        NewRxjavaWebUtils.getUIThread(NewRxjavaWebUtils.getObservable(this, "")
                        .map(new Func1<String, HsWebInfo>() {
                            @Override
                            public HsWebInfo call(String s) {
                                Map<String, Object> map = new HashMap<>();
                                String userGUID = LGSPUtils.getLocalData(getApplicationContext(), USER_GUID, String.class.getName(), "").toString();
                                HsWebInfo info = null;
                                //在线
                                if (NetUtil.isNetworkAvailable(getApplicationContext())) {
                                    info = NewRxjavaWebUtils.getJsonData(getApplicationContext(), CUS_SERVICE,
                                            "spappMeasureAddPremiseData", "uUserGUID=" + userGUID,
                                            ObtainNewMeasureOrderNoBean.class.getName(),
                                            true, "");
                                    if (!info.success) return info;
                                    map.put("orderAndCustomer", info.wsData.LISTWSDATA);
                                } else {
                                    List<WsEntity> entities = new ArrayList<>();
                                    DaoSession daoSession = GreenDaoUtil.getGreenDaoSession(getApplicationContext());
                                    MeasureOrderInSQLiteDao measureOrderInSQLiteDao = daoSession.getMeasureOrderInSQLiteDao();
                                    List<MeasureOrderInSQLite> measureOrderInSQLiteList = measureOrderInSQLiteDao.queryBuilder()
                                            .where(MeasureOrderInSQLiteDao.Properties.UserGUID.eq(userGUID))
                                            .list();
                                    Map<String, MeasureOrderInSQLite> measureOrderInSQLiteMap = new HashMap<>();
                                    if (measureOrderInSQLiteList == null)
                                        measureOrderInSQLiteList = new ArrayList<>();
                                    for (MeasureOrderInSQLite measureOrderInSQLite : measureOrderInSQLiteList) {
                                        measureOrderInSQLiteMap.put(measureOrderInSQLite.getISdOrderMeterMstId(), measureOrderInSQLite);
                                    }
                                    Iterator<Map.Entry<String, MeasureOrderInSQLite>> it = measureOrderInSQLiteMap.entrySet().iterator();
                                    while (it.hasNext()) {
                                        MeasureOrderInSQLite measureOrderInSQLite = it.next().getValue();
                                        ObtainNewMeasureOrderNoBean bean = new ObtainNewMeasureOrderNoBean();
                                        bean.ISDORDERMETERMSTID = measureOrderInSQLite.getISdOrderMeterMstId();
                                        bean.SBILLNO = measureOrderInSQLite.getSBillNo();
                                        bean.SCUSTOMERCODE = measureOrderInSQLite.getSCustomerCode();
                                        bean.SCUSTOMERNAME = measureOrderInSQLite.getSCustomerName();
                                        entities.add(bean);
                                    }
                                    info = new HsWebInfo();
                                    map.put("orderAndCustomer", entities);
                                }
                                if (!info.success) return info;
                                info.object = map;
                                return info;
                            }
                        })


                , this, mDialog, new WebListener() {
                    @Override
                    @SuppressWarnings("unchecked")
                    public void success(HsWebInfo hsWebInfo) {
                        Map<String, Object> map = (Map<String, Object>) hsWebInfo.object;
                        if (map == null) map = new HashMap<>();
                        List<WsEntity> orderAndCustomerList = (List<WsEntity>) map.get("orderAndCustomer");
                        for (int i = 0; i < orderAndCustomerList.size(); i++) {
                            ObtainNewMeasureOrderNoBean obtainNewMeasureOrderNoBean = (ObtainNewMeasureOrderNoBean) orderAndCustomerList.get(i);
                            mObtainNewMeasureOrderNoBeanList.add(obtainNewMeasureOrderNoBean);
                        }
                        initClothStyle();
                        //选取清单编号
                        selectorNewMeasureOrderNo();
                    }

                    @Override
                    public void error(HsWebInfo hsWebInfo, Context context) {
                    }
                }
        );

    }

    /**
     * 选取清单编号
     */
    private void selectorNewMeasureOrderNo() {
        final List<String> measureNoList = new ArrayList<>();
        measureNoList.add("清单编号");
        for (int i = 0; i < mObtainNewMeasureOrderNoBeanList.size(); i++) {
            String measureNo = mObtainNewMeasureOrderNoBeanList.get(i).SBILLNO;
            measureNoList.add(measureNo);
        }
        ArrayAdapter<String> measureNoAdapter = new ArrayAdapter<>(this, R.layout.element_string_item, R.id.tvElementString, measureNoList);
        mActivityNewMeasureCustomBinding.spMeasureNo.setAdapter(measureNoAdapter);
        mActivityNewMeasureCustomBinding.spMeasureNo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    mActivityNewMeasureCustomBinding.tvCustomerName.setText("单位名称");
                    mActivityNewMeasureCustomBinding.tvCustomerCode.setText("单位编号");
                } else {
                    mActivityNewMeasureCustomBinding.tvCustomerName.setText(mObtainNewMeasureOrderNoBeanList.get(position - 1).SCUSTOMERNAME);
                    mActivityNewMeasureCustomBinding.tvCustomerCode.setText(mObtainNewMeasureOrderNoBeanList.get(position - 1).SCUSTOMERCODE);
                }


//                //新增待量体清单人员条目界面
//                mActivityNewMeasureCustomBinding.btnNewMeasure.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        int choosePosition=mActivityNewMeasureCustomBinding.spMeasureNo.getSelectedItemPosition();
//                        if (choosePosition==0) {
//                            OthersUtil.ToastMsg(NewMeasureCustomActivity.this, "请先选取量体清单单号");
//                            return;
//                        } /*else if( mActivityNewMeasureCustomBinding.llNewCustom.getChildCount()>0){
//                            OthersUtil.ToastMsg(NewMeasureCustomActivity.this, "请先上传服务器");
//                        }else {
//                            addBaseData();
//                        }*/
//                        addBaseData();
//                    }
//                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


    /**
     * 上传服务器新增的待量体人员清单 或者是本地保存
     */
    private void upDateNewBaseData() {
        JSONArray array = new JSONArray();
        for (int i = 0; i < mActivityNewMeasureCustomBinding.llNewCustom.getChildCount(); i++) {
            View view = mActivityNewMeasureCustomBinding.llNewCustom.getChildAt(i);
            if (view == null) continue;
            TextView tvCustomerName = (TextView) view.findViewById(R.id.tvCustomerName);
//            AutoCompleteTextView actAreaName= (AutoCompleteTextView) view.findViewById(R.id.actAreaName);
            TextView actCityName = (TextView) view.findViewById(R.id.actCityName);
            TextView actCountyName = (TextView) view.findViewById(R.id.actCountyName);
            TextView actDepartmentName = (TextView) view.findViewById(R.id.actDepartmentName);
            TextView actJobName= (TextView) view.findViewById(R.id.etJobName);
            EditText etJobName = (EditText) view.findViewById(R.id.etJobName);
            TextView actClothStyle = (TextView) view.findViewById(R.id.actClothStyle);
            EditText etPerson = (EditText) view.findViewById(R.id.etPerson);
            EditText etNewSex = (EditText) view.findViewById(R.id.etNewSex);
//            Spinner spSex = (Spinner) view.findViewById(R.id.spSex);
//            Spinner spClothStyle= (Spinner) view.findViewById(R.id.spClothStyle);
            EditText etCount = (EditText) view.findViewById(R.id.etCount);
            String clothId = mNewMeasureClothTypeMap.get(actClothStyle.getText().toString());
            JSONObject item = new JSONObject();

            try {
                item.put("customer", tvCustomerName.getText().toString());
                item.put("area","");
                item.put("city", actCityName.getText().toString());
                item.put("country", actCountyName.getText().toString());
                item.put("department", actDepartmentName.getText().toString());
                item.put("job",actJobName.getText().toString());
//                item.put("job", etJobName.getText().toString());
                item.put("person", etPerson.getText().toString());
//                item.put("sex", sexArr[spSex.getSelectedItemPosition()]);
                item.put("sex", etNewSex.getText().toString());
//                item.put("clothStyleID",clothStyleList.get(spClothStyle.getSelectedItemPosition()).ISDSTYLETYPEMSTID);
                item.put("clothStyleID", clothId);
                item.put("count", etCount.getText().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            array.put(item);
        }

//        JSONObject jsonObject=new JSONObject();
//        jsonObject.put()


        OthersUtil.showLoadDialog(mDialog);
        NewRxjavaWebUtils.getUIThread(NewRxjavaWebUtils.getObservable(this, array)
                        .map(new Func1<JSONArray, HsWebInfo>() {
                            @Override
                            public HsWebInfo call(JSONArray array) {
                                Map<String, ObtainNewMeasureOrderNoBean> map = new HashMap<>();
                                String userGUID = LGSPUtils.getLocalData(getApplicationContext(), USER_GUID, String.class.getName(), "").toString();
                                for (ObtainNewMeasureOrderNoBean bean : mObtainNewMeasureOrderNoBeanList) {
                                    map.put(bean.SCUSTOMERNAME, bean);
                                }
                                if (NetUtil.isNetworkAvailable(getApplicationContext())) {
                                    StringBuilder sbStr = new StringBuilder();
                                    try {
                                        for (int i = 0; i < array.length(); i++) {
                                            JSONObject item = array.getJSONObject(i);
                                            String customer = item.getString("customer");
                                            String area = item.getString("area");
                                            String city = item.getString("city");
                                            String country = item.getString("country");
                                            String department = item.getString("department");
                                            String job = item.getString("job");
                                            String person = item.getString("person");
                                            String sex = item.getString("sex");
                                            String clothStyleID = item.getString("clothStyleID");
                                            String count = item.getString("count");
                                            ObtainNewMeasureOrderNoBean bean = map.get(customer);

                                            sbStr.append("EXEC spappAddOneMeasureDtl ")
                                                  .append("@isdOrderMeterMstid=").append(bean.ISDORDERMETERMSTID)
                                                  .append(",@sAreaName='").append(area).append("'")
                                                  .append(",@sCustomerName='").append(customer).append("'")
                                                  .append(",@sCityName='").append(city).append("'")
                                                    .append(",@sCountyName='").append(country).append("'")
                                                    .append(",@sDepartmentName='").append(department).append("'")
                                                    .append(",@sJobName='").append(job).append("'")
                                                    .append(",@sName='").append(person).append("'")
                                                    .append(",@sSex='").append(sex).append("'")
                                                    .append(",@sQty='").append(count).append("'")
                                                    .append(",@sUserGUID='").append(userGUID).append("'")
                                                    .append(",@isdStyleTypeMstId=").append(clothStyleID)
                                                    .append(";");

                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    return NewRxjavaWebUtils.getJsonData(getApplicationContext(), CUS_SERVICE
                                            , sbStr.toString(), "",
                                            NewMeasureBean.class.getName(),
                                            true, "");
                                } else {
                                    HsWebInfo info = new HsWebInfo();
                                    DaoSession daoSession = GreenDaoUtil.getGreenDaoSession(getApplicationContext());
                                    MeasureOrderInSQLiteDao measureOrderInSQLiteDao = daoSession.getMeasureOrderInSQLiteDao();
                                    MeasureDataInSQLiteDao measureDataInSQLiteDao = daoSession.getMeasureDataInSQLiteDao();
                                    Map<String, MeasureOrderInSQLite> hdrMap = new HashMap<>();//用于保存同一个人同一个单
                                    List<MeasureOrderInSQLite> addHdrList = new ArrayList<>();//订单头表数据
                                    List<MeasureDataInSQLite> addDtlList = new ArrayList<>();//款式(订单明细)数据
                                    try {
                                        for (int i = 0; i < array.length(); i++) {
                                            JSONObject item = array.getJSONObject(i);
                                            String customer = item.getString("customer");
                                            String area = item.getString("area");
                                            String city = item.getString("city");
                                            String country = item.getString("country");
                                            String department = item.getString("department");
                                            String job = item.getString("job");
                                            String person = item.getString("person");
                                            String sex = item.getString("sex");
                                            String clothStyleID = item.getString("clothStyleID");
                                            String count = item.getString("count");
                                            ObtainNewMeasureOrderNoBean bean = map.get(customer);

                                            //查询这个订单下的这个人本地有没有单子
                                            MeasureOrderInSQLite beforeOrder = null;
                                            try {
                                                beforeOrder = measureOrderInSQLiteDao.queryBuilder()
                                                        .where(MeasureOrderInSQLiteDao.Properties.ISdOrderMeterMstId.eq(bean.ISDORDERMETERMSTID))
                                                        .where(MeasureOrderInSQLiteDao.Properties.SPerson.eq(person))
                                                        .limit(1)
                                                        .list().get(0);
                                            } catch (Exception e) {
                                            }
                                            if (beforeOrder == null) {
                                                //同一个订单，同一个人的单子保存一份
                                                if (!hdrMap.containsKey(bean.ISDORDERMETERMSTID + "_" + person)) {
                                                    MeasureOrderInSQLite measureOrderInSQLite = new MeasureOrderInSQLite();
                                                    measureOrderInSQLite.setSCustomerCode(bean.SCUSTOMERCODE);
                                                    measureOrderInSQLite.setOrderType(0);
                                                    measureOrderInSQLite.setISdOrderMeterMstId(bean.ISDORDERMETERMSTID);
                                                    measureOrderInSQLite.setSAreaName(area);
                                                    measureOrderInSQLite.setSBillNo(bean.SBILLNO);
                                                    measureOrderInSQLite.setSCityName(city);
                                                    measureOrderInSQLite.setSCountyName(country);
                                                    measureOrderInSQLite.setSCustomerName(customer);
                                                    measureOrderInSQLite.setSDepartmentName(department);
                                                    measureOrderInSQLite.setSPerson(person);
                                                    measureOrderInSQLite.setUserGUID(userGUID);
                                                    measureOrderInSQLite.setSJobName(job);
                                                    addHdrList.add(measureOrderInSQLite);
                                                    hdrMap.put(bean.ISDORDERMETERMSTID + "_" + person, measureOrderInSQLite);
                                                }
                                            }

                                            //保存明细单
                                            MeasureOrderDtlStyleBaseDataInSQLiteDao orderDtlStyleBaseDataInSQLiteDao = daoSession.getMeasureOrderDtlStyleBaseDataInSQLiteDao();
                                            List<MeasureOrderDtlStyleBaseDataInSQLite> orderDtlStyleBaseDataInSQLiteList = null;
                                            try {
                                                orderDtlStyleBaseDataInSQLiteList = orderDtlStyleBaseDataInSQLiteDao.queryBuilder()
                                                        .where(MeasureOrderDtlStyleBaseDataInSQLiteDao.Properties.ISdStyleTypeMstId.eq(clothStyleID))
                                                        .list();
                                            } catch (Exception e) {
                                            }
                                            if (orderDtlStyleBaseDataInSQLiteList == null || orderDtlStyleBaseDataInSQLiteList.isEmpty()) {
                                                info.success = false;
                                                info.error.error = "保存失败";
                                                return info;
                                            }
                                            //订单明细款式的基础数据保存到measureDataInSQLite中
                                            for (MeasureOrderDtlStyleBaseDataInSQLite orderDtlStyleBaseDataInSQLite : orderDtlStyleBaseDataInSQLiteList) {
                                                MeasureDataInSQLite measureDataInSQLite = new MeasureDataInSQLite();
                                                measureDataInSQLite.setType(0);
                                                measureDataInSQLite.setOrderId(bean.ISDORDERMETERMSTID);
                                                measureDataInSQLite.setISdOrderMeterDtlId(null);
                                                measureDataInSQLite.setISdStyleTypeMstId(clothStyleID);
                                                measureDataInSQLite.setISeq(orderDtlStyleBaseDataInSQLite.getISeq());
                                                measureDataInSQLite.setPerson(person);
                                                measureDataInSQLite.setSBillNo(orderDtlStyleBaseDataInSQLite.getSBillNo());
                                                measureDataInSQLite.setSdStyleTypeItemDtlId(orderDtlStyleBaseDataInSQLite.getISdStyleTypeItemDtlId());
                                                measureDataInSQLite.setSMeterCode(orderDtlStyleBaseDataInSQLite.getSMeterCode());
                                                measureDataInSQLite.setSMeterName(orderDtlStyleBaseDataInSQLite.getSMeterName());
                                                measureDataInSQLite.setSValueCode(orderDtlStyleBaseDataInSQLite.getSValueCode());
                                                measureDataInSQLite.setSValueGroup(orderDtlStyleBaseDataInSQLite.getSValueGroup());
                                                measureDataInSQLite.setUserGUID(userGUID);
                                                measureDataInSQLite.setISMeterSize("0");
                                                measureDataInSQLite.setIsAdd(true);
                                                measureDataInSQLite.setCount(Integer.parseInt(count));
                                                measureDataInSQLite.setSex(sex);
                                                addDtlList.add(measureDataInSQLite);
                                            }
                                        }
//                                        Set<String> personSet=new HashSet<>();
//                                        for(int i=0;i<addHdrList.size();i++){
//                                            MeasureOrderInSQLite measureOrderInSQLite=addHdrList.get(i);
//                                            String key=measureOrderInSQLite.getSPerson()+"_"+measureOrderInSQLite.getISdOrderMeterMstId();
//                                            if(personSet.contains(key)){
//                                                addHdrList.remove(i);
//                                                i--;
//                                                continue;
//                                            }
//                                            personSet.add(key);
//                                        }
                                        measureOrderInSQLiteDao.insertOrReplaceInTx(addHdrList);
                                        measureDataInSQLiteDao.insertOrReplaceInTx(addDtlList);
                                        return new HsWebInfo();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        info.success = false;
                                        info.error.error = "保存失败";
                                        return info;
                                    }
                                }
                            }
                        }), this, mDialog, new SimpleHsWeb() {
                    @Override
                    public void success(HsWebInfo hsWebInfo) {
                        OthersUtil.ToastMsg(NewMeasureCustomActivity.this, "保存成功！！");
                        mActivityNewMeasureCustomBinding.llNewCustom.removeAllViews();
                    }
                }
        );
    }


    /**
     * 初始化量体款式
     */
    private void initClothStyle(){
        OthersUtil.showLoadDialog(mDialog);
        final String billNo = "1";
        NewRxjavaWebUtils.getUIThread(NewRxjavaWebUtils.getObservable(this,"")
                .map(new Func1<String, HsWebInfo>() {
            @Override
            public HsWebInfo call(String s) {
                Map<String, Object> map = new HashMap<>();
                HsWebInfo hsWebInfo = null;
                if (NetUtil.isNetworkAvailable(getApplicationContext())) {
                    hsWebInfo = NewRxjavaWebUtils.getJsonData(getApplicationContext(), CUS_SERVICE,
                            "spappMeasureAddBaseData",
                            "iIndex=1" +
                                    ",sOrderBillNo=" + billNo,
                            NewMeasureBean.class.getName(),
                            true, "服装类型有错误");
                    if (!hsWebInfo.success) return hsWebInfo;
                    map.put("clothStyle", hsWebInfo.wsData.LISTWSDATA);
                } else {
                    MeasureOrderDtlStyleBaseDataInSQLiteDao measureStyleBaseDataInSQLiteDao = GreenDaoUtil
                            .getGreenDaoSession(getApplicationContext()).getMeasureOrderDtlStyleBaseDataInSQLiteDao();
                    List<MeasureOrderDtlStyleBaseDataInSQLite> orderDtlStyleBaseDataInSQLiteList = measureStyleBaseDataInSQLiteDao.queryBuilder()
                            .list();
                    List<WsEntity> beanList = new ArrayList<>();
//                                    Map<String,MeasureStyleBaseDataInSQLite> measureStyleBaseDataInSQLiteMap=new HashMap<>();
                    Set<String> styleIdAdded = new HashSet<>();
                    for (MeasureOrderDtlStyleBaseDataInSQLite orderDtlStyleBaseDataInSQLite : orderDtlStyleBaseDataInSQLiteList) {
                        if (styleIdAdded.contains(orderDtlStyleBaseDataInSQLite.getISdStyleTypeMstId()))
                            continue;
                        NewMeasureBean bean = new NewMeasureBean();
                        bean.SVALUEGROUP = orderDtlStyleBaseDataInSQLite.getSValueGroup();
                        bean.ISDSTYLETYPEMSTID = orderDtlStyleBaseDataInSQLite.getISdStyleTypeMstId();
                        beanList.add(bean);
                        styleIdAdded.add(bean.ISDSTYLETYPEMSTID);
                    }
                    map.put("clothStyle", beanList);
                }
                if (hsWebInfo == null) hsWebInfo = new HsWebInfo();
                hsWebInfo.object = map;
                return hsWebInfo;
            }
        }), this, mDialog, new SimpleHsWeb() {
            @Override
            public void success(HsWebInfo hsWebInfo) {

                //新增一行表单
                Map<String, Object> map = (Map<String, Object>) hsWebInfo.object;

                final List<WsEntity> clothStyleLists = (List<WsEntity>) map.get("clothStyle");
                for (int i = 0; i < clothStyleLists.size(); i++) {
                    NewMeasureBean clothStyleBean = (NewMeasureBean) clothStyleLists.get(i);
                    clothStyleList.add(clothStyleBean);
                }
                //新增待量体名单
                mActivityNewMeasureCustomBinding.btnNewMeasure.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        mClothList.clear();
                        mActivityNewMeasureCustomBinding.llNewCustom.removeAllViews();

                        List<String> totalClothTypeList=new ArrayList<>();
                        for (int i = 0; i <clothStyleList.size() ; i++) {
                            String svaluegroup = clothStyleList.get(i).SVALUEGROUP;
                            totalClothTypeList.add(svaluegroup);
                            String isdstyletypemstid = clothStyleList.get(i).ISDSTYLETYPEMSTID;
                            mNewMeasureClothTypeMap.put(svaluegroup,isdstyletypemstid);
                        }
                        final String[] clothTypeArray =  totalClothTypeList.toArray(new String[totalClothTypeList.size()]);

//                final String[] clothTypeArray = {"春秋上衣", "春秋裤子",  "冬装上衣", "冬装裤子",
//                        "夏装上衣", "夏装裤子", "大衣", "冬装裤子", "马甲",
//                        "女裙", "长袖衬衫", "短袖衬衫", "毛衣", "背心", "T恤", "夹克","工装上衣",
//                        "工装裤子", "防寒棉衣"};
                       new AlertDialog.Builder(NewMeasureCustomActivity.this)
                                .setTitle("请选择衣服款式")
                                .setMultiChoiceItems(clothTypeArray, null, new DialogInterface.OnMultiChoiceClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                                        if (isChecked) {
                                            mClothList.add(clothTypeArray[which]);
                                        }else {
                                            mClothList.remove(clothTypeArray[which]);
                                        }

                                    }
                                })

                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        addNewItem();
                                    }
                                })
                                .setNegativeButton("取消", null)
                                .show();


                    }
                });

            }
        });

    }

    /**
     * 添加新的量体订单
     */
    @SuppressWarnings("unchecked")
    private void addBaseData() {
//        final String billNo = mObtainNewMeasureOrderNoBeanList.get(mActivityNewMeasureCustomBinding.spMeasureNo.getSelectedItemPosition() - 1).SBILLNO;
        final String billNo = "1";
        NewRxjavaWebUtils.getUIThread(NewRxjavaWebUtils.getObservable(this, "")
                        //服装类型
                        .map(new Func1<String, HsWebInfo>() {
                            @Override
                            public HsWebInfo call(String s) {
                                Map<String, Object> map = new HashMap<>();
                                HsWebInfo hsWebInfo = null;
                                if (NetUtil.isNetworkAvailable(getApplicationContext())) {
                                    hsWebInfo = NewRxjavaWebUtils.getJsonData(getApplicationContext(), CUS_SERVICE,
                                            "spappMeasureAddBaseData",
                                            "iIndex=1" +
                                                    ",sOrderBillNo=" + billNo,
                                            NewMeasureBean.class.getName(),
                                            true, "服装类型有错误");
                                    if (!hsWebInfo.success) return hsWebInfo;
                                    map.put("clothStyle", hsWebInfo.wsData.LISTWSDATA);
                                } else {
                                    MeasureOrderDtlStyleBaseDataInSQLiteDao measureStyleBaseDataInSQLiteDao = GreenDaoUtil
                                            .getGreenDaoSession(getApplicationContext()).getMeasureOrderDtlStyleBaseDataInSQLiteDao();
                                    List<MeasureOrderDtlStyleBaseDataInSQLite> orderDtlStyleBaseDataInSQLiteList = measureStyleBaseDataInSQLiteDao.queryBuilder()
                                            .list();
                                    List<WsEntity> beanList = new ArrayList<>();
                                    Set<String> styleIdAdded = new HashSet<>();
                                    for (MeasureOrderDtlStyleBaseDataInSQLite orderDtlStyleBaseDataInSQLite : orderDtlStyleBaseDataInSQLiteList) {
                                        if (styleIdAdded.contains(orderDtlStyleBaseDataInSQLite.getISdStyleTypeMstId()))
                                            continue;
                                        NewMeasureBean bean = new NewMeasureBean();
                                        bean.SVALUEGROUP = orderDtlStyleBaseDataInSQLite.getSValueGroup();
                                        bean.ISDSTYLETYPEMSTID = orderDtlStyleBaseDataInSQLite.getISdStyleTypeMstId();
                                        beanList.add(bean);
                                        styleIdAdded.add(bean.ISDSTYLETYPEMSTID);
                                    }
                                    map.put("clothStyle", beanList);
                                }
                                if (hsWebInfo == null) hsWebInfo = new HsWebInfo();
                                hsWebInfo.object = map;
                                return hsWebInfo;
                            }
                        })
                        //部门名称
                        .map(new Func1<HsWebInfo, HsWebInfo>() {
                            @Override
                            public HsWebInfo call(HsWebInfo hsWebInfo) {
                                Map<String, Object> map = (Map<String, Object>) hsWebInfo.object;
                                String userGUID = LGSPUtils.getLocalData(getApplicationContext(), USER_GUID, String.class.getName(), "").toString();
                                if (NetUtil.isNetworkAvailable(getApplicationContext())) {
                                    hsWebInfo = NewRxjavaWebUtils.getJsonData(getApplicationContext(), CUS_SERVICE,
                                            "spappMeasureAddBaseData"
                                            , "iIndex=2" +
                                                    ",sOrderBillNo=" + billNo,
                                            NewMeasureBean.class.getName(),
                                            true, "部门名称有错误"
                                    );
                                    if (!hsWebInfo.success) return hsWebInfo;
                                    map.put("department", hsWebInfo.wsData.LISTWSDATA);
                                } else {
                                    MeasureOrderInSQLiteDao measureOrderInSQLiteDao = GreenDaoUtil
                                            .getGreenDaoSession(getApplicationContext()).getMeasureOrderInSQLiteDao();
                                    List<MeasureOrderInSQLite> list = null;
                                    try {
                                        list = measureOrderInSQLiteDao.queryBuilder()
                                                .where(MeasureOrderInSQLiteDao.Properties.UserGUID.eq(userGUID))
                                                .where(MeasureOrderInSQLiteDao.Properties.SBillNo.eq(billNo))
                                                .list();
                                    } catch (Exception e) {
                                    }
                                    if (list == null) list = new ArrayList<>();
                                    List<WsEntity> entities = new ArrayList<>();
                                    Set<String> set = new HashSet<>();
                                    for (MeasureOrderInSQLite measureOrderInSQLite : list) {
                                        set.add(measureOrderInSQLite.getSDepartmentName());
                                    }
                                    for (String s : set) {
                                        NewMeasureBean bean = new NewMeasureBean();
                                        bean.SDEPARTMENTNAME = s;
                                        entities.add(bean);
                                    }
                                    map.put("department", entities);
                                    map.put("measureOrderInSQLiteList", list);
                                }
                                hsWebInfo.object = map;
                                return hsWebInfo;

                            }
                        })
                        //地区名称
                        .map(new Func1<HsWebInfo, HsWebInfo>() {
                            @Override
                            public HsWebInfo call(HsWebInfo hsWebInfo) {
                                Map<String, Object> map = (Map<String, Object>) hsWebInfo.object;
                                if (NetUtil.isNetworkAvailable(getApplicationContext())) {
                                    hsWebInfo = NewRxjavaWebUtils.getJsonData(getApplicationContext(), CUS_SERVICE,
                                            "spappMeasureAddBaseData"
                                            , "iIndex=3" + ",sOrderBillNo=" + billNo,
                                            NewMeasureBean.class.getName(),
                                            true, "地区名称有错误"
                                    );
                                    if (!hsWebInfo.success) return hsWebInfo;
                                    map.put("area", hsWebInfo.wsData.LISTWSDATA);
                                } else {
                                    List<MeasureOrderInSQLite> list = (List<MeasureOrderInSQLite>) map.get("measureOrderInSQLiteList");

//                                    String userGUID=LGSPUtils.getLocalData(getApplicationContext(),USER_GUID,String.class.getName(),"").toString();
//                                    MeasureOrderInSQLiteDao measureOrderInSQLiteDao = GreenDaoUtil
//                                            .getGreenDaoSession(getApplicationContext()).getMeasureOrderInSQLiteDao();
//                                     = null;
//                                    try {
//                                        list = measureOrderInSQLiteDao.queryBuilder()
//                                                .where(MeasureOrderInSQLiteDao.Properties.UserGUID.eq(userGUID))
//                                                .where(MeasureOrderInSQLiteDao.Properties.SBillNo.eq(billNo))
//                                                .list();
//                                    } catch (Exception e) {}
//                                    if (list == null) list = new ArrayList<>();
//                                    List<WsEntity> entities = new ArrayList<>();
                                    Set<String> set = new HashSet<>();
                                    for (MeasureOrderInSQLite measureOrderInSQLite : list) {
                                        set.add(measureOrderInSQLite.getSAreaName());
                                    }
                                    List<WsEntity> entities = new ArrayList<>();
                                    for (String s : set) {
                                        NewMeasureBean bean = new NewMeasureBean();
                                        bean.SAREANAME = s;
                                        entities.add(bean);
                                    }
                                    map.put("area", entities);
                                }
                                hsWebInfo.object = map;
                                return hsWebInfo;

                            }
                        })
                        //城市名称
                        .map(new Func1<HsWebInfo, HsWebInfo>() {
                            @Override
                            public HsWebInfo call(HsWebInfo hsWebInfo) {
                                Map<String, Object> map = (Map<String, Object>) hsWebInfo.object;
                                if (NetUtil.isNetworkAvailable(getApplicationContext())) {
                                    hsWebInfo = NewRxjavaWebUtils.getJsonData(getApplicationContext(), CUS_SERVICE,
                                            "spappMeasureAddBaseData"
                                            , "iIndex=4" + ",sOrderBillNo=" + billNo,
                                            NewMeasureBean.class.getName(),
                                            true, "城市名称有错误"
                                    );
                                    if (!hsWebInfo.success) return hsWebInfo;
                                    map.put("city", hsWebInfo.wsData.LISTWSDATA);
                                } else {
                                    List<MeasureOrderInSQLite> list = (List<MeasureOrderInSQLite>) map.get("measureOrderInSQLiteList");
                                    Set<String> set = new HashSet<>();
                                    for (MeasureOrderInSQLite measureOrderInSQLite : list) {
                                        set.add(measureOrderInSQLite.getSCityName());
                                    }
                                    List<WsEntity> entities = new ArrayList<>();
                                    for (String s : set) {
                                        NewMeasureBean bean = new NewMeasureBean();
                                        bean.SCITYNAME = s;
                                        entities.add(bean);
                                    }
                                    map.put("city", entities);
                                }

                                hsWebInfo.object = map;
                                return hsWebInfo;

                            }
                        })
                        //县城名称
                        .map(new Func1<HsWebInfo, HsWebInfo>() {
                            @Override
                            public HsWebInfo call(HsWebInfo hsWebInfo) {
                                Map<String, Object> map = (Map<String, Object>) hsWebInfo.object;
                                if (NetUtil.isNetworkAvailable(getApplicationContext())) {
                                    hsWebInfo = NewRxjavaWebUtils.getJsonData(getApplicationContext(), CUS_SERVICE,
                                            "spappMeasureAddBaseData"
                                            , "iIndex=5" + ",sOrderBillNo=" + billNo,
                                            NewMeasureBean.class.getName(),
                                            true, "县城名称有错误"
                                    );
                                    if (!hsWebInfo.success) return hsWebInfo;
                                    map.put("county", hsWebInfo.wsData.LISTWSDATA);
                                } else {
                                    List<MeasureOrderInSQLite> list = (List<MeasureOrderInSQLite>) map.get("measureOrderInSQLiteList");
                                    Set<String> set = new HashSet<>();
                                    for (MeasureOrderInSQLite measureOrderInSQLite : list) {
                                        set.add(measureOrderInSQLite.getSCountyName());
                                    }
                                    List<WsEntity> entities = new ArrayList<>();
                                    for (String s : set) {
                                        NewMeasureBean bean = new NewMeasureBean();
                                        bean.SCOUNTYNAME = s;
                                        entities.add(bean);
                                    }
                                    map.put("county", entities);
                                }
                                hsWebInfo.object = map;
                                return hsWebInfo;

                            }
                        })
                        // 岗位名称
                        .map(new Func1<HsWebInfo, HsWebInfo>() {
                            @Override
                            public HsWebInfo call(HsWebInfo hsWebInfo) {
                                Map<String, Object> map = (Map<String, Object>) hsWebInfo.object;
                                if (NetUtil.isNetworkAvailable(getApplicationContext())) {
                                    hsWebInfo = NewRxjavaWebUtils.getJsonData(getApplicationContext(), CUS_SERVICE,
                                            "spappMeasureAddBaseData"
                                            , "iIndex=6" + ",sOrderBillNo=" + billNo,
                                            NewMeasureBean.class.getName(),
                                            true, ""
                                    );
                                    if (!hsWebInfo.success) return hsWebInfo;
                                    map.put("job", hsWebInfo.wsData.LISTWSDATA);
                                } else {
                                    List<MeasureOrderInSQLite> list = (List<MeasureOrderInSQLite>) map.get("measureOrderInSQLiteList");
                                    Set<String> set = new HashSet<>();
                                    for (MeasureOrderInSQLite measureOrderInSQLite : list) {
                                        set.add(measureOrderInSQLite.getSJobName());
                                    }
                                    List<WsEntity> entities = new ArrayList<>();
                                    for (String s : set) {
                                        NewMeasureBean bean = new NewMeasureBean();
                                        bean.SJOBNAME = s;
                                        entities.add(bean);
                                    }
                                    map.put("job", entities);
                                }


                                hsWebInfo.object = map;
                                return hsWebInfo;
                            }
                        })

                , this, mDialog, new SimpleHsWeb() {
                    @Override
                    public void success(HsWebInfo hsWebInfo) {
//                List<NewMeasureBean> clothStyleBeanList=new ArrayList<>();
//                List<NewMeasureBean> departmentBeanList=new ArrayList<>();
//                List<NewMeasureBean> areaBeanList=new ArrayList<>();
//                List<NewMeasureBean> cityBeanList=new ArrayList<>();
//                List<NewMeasureBean> countyBeanList=new ArrayList<>();
//                List<NewMeasureBean> jobBeanList=new ArrayList<>();

                        List<String> departmentNameStrList = new ArrayList<>();//部门的筛选数据
                        List<String> areaStrList = new ArrayList<>();//地区的筛选数据
                        List<String> cityStrList = new ArrayList<>();//城市的筛选数据
                        List<String> countryStrList = new ArrayList<>();//县城的筛选数据
                        List<String> jobStrList = new ArrayList<>();//职位的筛选数据


                        //新增一行表单
                        Map<String, Object> map = (Map<String, Object>) hsWebInfo.object;

                        List<WsEntity> clothStyleList = (List<WsEntity>) map.get("clothStyle");
                        for (int i = 0; i < clothStyleList.size(); i++) {
                            NewMeasureBean clothStyleBean = (NewMeasureBean) clothStyleList.get(i);
                            NewMeasureCustomActivity.this.clothStyleList.add(clothStyleBean);
                        }
                        List<WsEntity> departmentList = (List<WsEntity>) map.get("department");
                        for (int i = 0; i < departmentList.size(); i++) {
                            NewMeasureBean departmentBean = (NewMeasureBean) departmentList.get(i);
                            departmentNameStrList.add(departmentBean.SDEPARTMENTNAME);
                        }
                        List<WsEntity> areaList = (List<WsEntity>) map.get("area");
                        for (int i = 0; i < areaList.size(); i++) {
                            NewMeasureBean areaBean = (NewMeasureBean) areaList.get(i);
                            areaStrList.add(areaBean.SAREANAME);
                        }
                        List<WsEntity> cityList = (List<WsEntity>) map.get("city");
                        for (int i = 0; i < cityList.size(); i++) {
                            NewMeasureBean cityBean = (NewMeasureBean) cityList.get(i);
                            cityStrList.add(cityBean.SCITYNAME);
                        }
                        List<WsEntity> countyList = (List<WsEntity>) map.get("county");
                        for (int i = 0; i < countyList.size(); i++) {
                            NewMeasureBean countyBean = (NewMeasureBean) countyList.get(i);
                            countryStrList.add(countyBean.SCOUNTYNAME);
                        }
                        List<WsEntity> jobList = (List<WsEntity>) map.get("job");
                        for (int i = 0; i < jobList.size(); i++) {
                            NewMeasureBean jobBean = (NewMeasureBean) jobList.get(i);
                            jobStrList.add(jobBean.SJOBNAME);
                        }
                        Map<String, Object> filterStrMap = new HashMap<>();
                        filterStrMap.put("departmentNameStrList", departmentNameStrList);
                        filterStrMap.put("areaStrList", areaStrList);
                        filterStrMap.put("cityStrList", cityStrList);
                        filterStrMap.put("countryStrList", countryStrList);
                        filterStrMap.put("jobStrList", jobStrList);


//                        addNewMeasureItem(filterStrMap);
                    }
                });
    }

    /**
     * 新增一个
     */
    private void addNewItem() {
        Intent intent = getIntent();
        String tvCustomerSearch = intent.getStringExtra(TVCUSTOMERSEARCH);
        String tvCitySearch = intent.getStringExtra(TVCITYSEARCH);
        String tvCountySearch = intent.getStringExtra(TVCOUNTYSEARCH);
        String tvDepartmentSearch = intent.getStringExtra(TVDEPARTMENTSEARCH);
        String etJob = mActivityNewMeasureCustomBinding.etJob.getText().toString();
        String etName = mActivityNewMeasureCustomBinding.etName.getText().toString();
        String etSex = mActivityNewMeasureCustomBinding.etSex.getText().toString();
        if (etJob.isEmpty()||etName.isEmpty()||etSex.isEmpty()){
            OthersUtil.ToastMsg(NewMeasureCustomActivity.this,"请先将岗位，姓名，性别填写完整");
            return;
        }


        for (int i = 0; i < mClothList.size(); i++) {
            final View view = View.inflate(this, R.layout.new_measure_item, null);
            final int finalI = i;
            //长按删除条目
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(final View v) {
                    new AlertDialog.Builder(NewMeasureCustomActivity.this)
                            .setTitle("确定删除此款衣服吗")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mClothList.remove(finalI);
                                    mActivityNewMeasureCustomBinding.llNewCustom.removeView(v);
                                    mActivityNewMeasureCustomBinding.llNewCustom.invalidate();

                                }
                            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {


                        }
                    }).show();

                    return true;
                }
            });
            TextView tvCustomerName = (TextView) view.findViewById(R.id.tvCustomerName);
//          TextView actAreaName = (TextView) view.findViewById(R.id.actAreaName);
            TextView actCityName = (TextView) view.findViewById(R.id.actCityName);
            TextView actCountyName = (TextView) view.findViewById(R.id.actCountyName);
            TextView actDepartmentName = (TextView) view.findViewById(R.id.actDepartmentName);
            TextView actClothStyle = (TextView) view.findViewById(R.id.actClothStyle);
            TextView etJobName = (TextView) view.findViewById(R.id.etJobName);
            TextView etPerson = (TextView) view.findViewById(R.id.etPerson);
            TextView etNewSex = (TextView) view.findViewById(R.id.etNewSex);
            etJobName.setText(etJob);
            etPerson.setText(etName);
            etNewSex.setText(etSex);
            Spinner spSex = (Spinner) view.findViewById(R.id.spSex);
            ArrayAdapter<String> sexAdapter = new ArrayAdapter<>(this, R.layout.element_string_item, R.id.tvElementString, sexArr);
            spSex.setAdapter(sexAdapter);
            spSex.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            tvCustomerName.setText(tvCustomerSearch);
            actCityName.setText(tvCitySearch);
            actCountyName.setText(tvCountySearch);
            actDepartmentName.setText(tvDepartmentSearch);
            actClothStyle.setText(mClothList.get(i));
//          clothStyleData(view);//衣服款式数据
            mActivityNewMeasureCustomBinding.llNewCustom.addView(view);

        }

    }

    /**
     * 新增一行表单
     */
    @SuppressWarnings("unchecked")
    private void addNewMeasureItem(Map<String, Object> filterStrMap) {
        View view = View.inflate(this, R.layout.new_measure_item, null);
        TextView tvCustomerName = (TextView) view.findViewById(R.id.tvCustomerName);
        tvCustomerName.setText(mActivityNewMeasureCustomBinding.tvCustomerName.getText());
//        mEtPerson = (EditText) view.findViewById(R.id.etPerson);
        Spinner spSex = (Spinner) view.findViewById(R.id.spSex);
        ArrayAdapter<String> sexAdapter = new ArrayAdapter<>(this, R.layout.element_string_item, R.id.tvElementString, sexArr);
        spSex.setAdapter(sexAdapter);
//        spSex.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
//        EditText mEtCount = (EditText) view.findViewById(R.id.etCount);
        List<String> departmentNameStrList = (List<String>) filterStrMap.get("departmentNameStrList");
        List<String> areaStrList = (List<String>) filterStrMap.get("areaStrList");
        List<String> cityStrList = (List<String>) filterStrMap.get("cityStrList");
        List<String> countryStrList = (List<String>) filterStrMap.get("countryStrList");
        List<String> jobStrList = (List<String>) filterStrMap.get("jobStrList");
        clothStyleData(view);//衣服款式数据
        areaData(view, areaStrList);//区域数据
        cityData(view, cityStrList);//城市数据
        countyData(view, countryStrList);//县城数据
        departmentData(view, departmentNameStrList);//部门数据
        jobData(view, jobStrList);//岗位数据


        mActivityNewMeasureCustomBinding.llNewCustom.addView(view);
    }

    private void clothStyleData(View view) {
        List<String> clothStyleStringList = new ArrayList<>();
        for (int i = 0; i < clothStyleList.size(); i++) {
            clothStyleStringList.add(clothStyleList.get(i).SVALUEGROUP);
        }
//        Spinner spClothStyle = (Spinner) view.findViewById(R.id.spClothStyle);
        ArrayAdapter<String> spAdapter = new ArrayAdapter<>(this, R.layout.string_item, R.id.tvString, clothStyleStringList);
//        spClothStyle.setAdapter(spAdapter);
//        addClothStyle(clothStyleBeanList);
//        spClothStyle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                mIsdstyletypemstid = mClothStyleidStringList.get(position);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
    }


    /**
     * 岗位数据
     *
     * @param view
     */
    private void jobData(View view, List<String> jobList) {
//        List<String> sJobNameList =new ArrayList<>();
//        for (int i = 0; i < jobBeanList.size(); i++) {
//            String sjobname = jobBeanList.get(i).SJOBNAME;
//            sJobNameList.add(sjobname);
//        }
//        final AutoCompleteTextView actJobName = (AutoCompleteTextView) view.findViewById(R.id.actJobName);
//        HsArrayAdapter<String> adapter=new HsArrayAdapter<>(this,android.R.layout.simple_list_item_1,jobList);
//        actJobName.setAdapter(adapter);
//        actJobName.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (isFirstClick){
//                    actJobName.setFocusable(true);
//                    actJobName.setFocusableInTouchMode(true);
//                    actJobName.showDropDown();
//                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//                    imm.hideSoftInputFromWindow(actJobName.getWindowToken(), 0);
//                }else {
//                    actJobName.setFocusable(true);
//                    actJobName.setFocusableInTouchMode(true);
//                    actJobName.showDropDown();
//                }
//
//                isFirstClick=!isFirstClick;
//            }
//        });

    }

    /**
     * 部门数据
     *
     * @param view
     */
    private void departmentData(View view, List<String> departmentNameList) {
//        List<String> sDepartmentNameList =new ArrayList<>();
//        for (int i = 0; i < departmentBeanList.size(); i++) {
//            String sdepartmentname = departmentBeanList.get(i).SDEPARTMENTNAME;
//            sDepartmentNameList.add(sdepartmentname);
//        }
        final AutoCompleteTextView actDepartmentName = (AutoCompleteTextView) view.findViewById(R.id.actDepartmentName);
        HsArrayAdapter<String> adapter = new HsArrayAdapter<>(this, android.R.layout.simple_list_item_1, departmentNameList);
        actDepartmentName.setAdapter(adapter);
        actDepartmentName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFirstClick) {
                    actDepartmentName.setFocusable(true);
                    actDepartmentName.setFocusableInTouchMode(true);
                    actDepartmentName.showDropDown();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(actDepartmentName.getWindowToken(), 0);
                } else {
                    actDepartmentName.setFocusable(true);
                    actDepartmentName.setFocusableInTouchMode(true);
                    actDepartmentName.showDropDown();
                }
                isFirstClick = !isFirstClick;
            }
        });

    }

    /**
     * 县城数据
     *
     * @param view
     */
    private void countyData(View view, List<String> countryList) {
//        List<String> sCountyNameList =new ArrayList<>();
//        for (int i = 0; i < countyBeanList.size(); i++) {
//            String scountyname = countyBeanList.get(i).SCOUNTYNAME;
//            sCountyNameList.add(scountyname);
//        }
        final AutoCompleteTextView actCountyName = (AutoCompleteTextView) view.findViewById(R.id.actCountyName);
        HsArrayAdapter<String> adapter = new HsArrayAdapter<>(this, android.R.layout.simple_list_item_1, countryList);
        actCountyName.setAdapter(adapter);
        actCountyName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFirstClick) {
                    actCountyName.setFocusable(true);
                    actCountyName.setFocusableInTouchMode(true);
                    actCountyName.showDropDown();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(actCountyName.getWindowToken(), 0);
                } else {
                    actCountyName.setFocusable(true);
                    actCountyName.setFocusableInTouchMode(true);
                    actCountyName.showDropDown();
                }
                isFirstClick = !isFirstClick;
            }
        });
    }

    /**
     * 城市数据
     *
     * @param view
     */
    private void cityData(View view, List<String> cityList) {
//        List<String> sCityNameList =new ArrayList<>();
//        for (int i = 0; i < cityBeanList.size(); i++) {
//            String scityname = cityBeanList.get(i).SCITYNAME;
//            sCityNameList.add(scityname);
//        }
        final AutoCompleteTextView actCityName = (AutoCompleteTextView) view.findViewById(R.id.actCityName);
        HsArrayAdapter<String> adapter = new HsArrayAdapter<>(this, android.R.layout.simple_list_item_1, cityList);
        actCityName.setAdapter(adapter);
        actCityName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFirstClick) {
                    actCityName.setFocusable(true);
                    actCityName.setFocusableInTouchMode(true);
                    actCityName.showDropDown();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(actCityName.getWindowToken(), 0);
                } else {
                    actCityName.setFocusable(true);
                    actCityName.setFocusableInTouchMode(true);
                    actCityName.showDropDown();
                }
                isFirstClick = !isFirstClick;
            }
        });
    }

    /**
     * 区域数据
     *
     * @param view
     */
    private void areaData(View view, List<String> areaList) {
//        List<String> sAreaNameList =new ArrayList<>();
//        for (int i = 0; i < areaBeanList.size(); i++) {
//            String sareaname = areaBeanList.get(i).SAREANAME;
//            sAreaNameList.add(sareaname);
//        }
//        final AutoCompleteTextView actAreaName = (AutoCompleteTextView) view.findViewById(R.id.actAreaName);
        HsArrayAdapter<String> adapter = new HsArrayAdapter<>(this, android.R.layout.simple_list_item_1, areaList);
//        actAreaName.setAdapter(adapter);

//        actAreaName.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//               if (isFirstClick){
//                   actAreaName.setFocusable(true);
//                   actAreaName.setFocusableInTouchMode(true);
//                   actAreaName.showDropDown();
//                   InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//                   imm.hideSoftInputFromWindow(actAreaName.getWindowToken(), 0);
//               }else {
//                   actAreaName.setFocusable(true);
//                   actAreaName.setFocusableInTouchMode(true);
//                   actAreaName.showDropDown();
//               }
        isFirstClick = !isFirstClick;
//            }
//        });

    }


//    /**
//     * 添加服装类型
//     *
//     * @param clothStyleList
//     */
//    private void addClothStyle(List<NewMeasureBean> clothStyleList) {
//
//        for (int i = 0; i < clothStyleList.size(); i++) {
//            NewMeasureBean bean =  clothStyleList.get(i);
//
//            String isdstyletypemstid = bean.ISDSTYLETYPEMSTID;
//            String svaluegroup = bean.SVALUEGROUP;
//            mClothStyleMap.put("isdstyletypemstid", isdstyletypemstid);
//            mClothStyleMap.put("svaluegroup", svaluegroup);
//            mClothStyleStringList.add(mClothStyleMap.get("svaluegroup"));
//            mClothStyleidStringList.add(mClothStyleMap.get("isdstyletypemstid"));
//        }
//    }

}
