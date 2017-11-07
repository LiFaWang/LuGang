package lugang.app.huansi.net.lugang.activity;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import huansi.net.qianjingapp.base.NotWebBaseActivity;
import huansi.net.qianjingapp.entity.HsWebInfo;
import huansi.net.qianjingapp.entity.WsEntity;
import huansi.net.qianjingapp.imp.SimpleHsWeb;
import huansi.net.qianjingapp.utils.NetUtil;
import huansi.net.qianjingapp.utils.NewRxjavaWebUtils;
import huansi.net.qianjingapp.utils.OthersUtil;
import lugang.app.huansi.net.db.MeasureRemarkDataInSQLite;
import lugang.app.huansi.net.db.RemarkCategoryDataInSQLite;
import lugang.app.huansi.net.db.RemarkDetailDataInSQLite;
import lugang.app.huansi.net.greendao.RemarkCategoryDataInSQLiteDao;
import lugang.app.huansi.net.greendao.RemarkDetailDataInSQLiteDao;
import lugang.app.huansi.net.lugang.R;
import lugang.app.huansi.net.lugang.adapter.RemarkAddAdapter;
import lugang.app.huansi.net.lugang.adapter.RemarkCategoryAdapter;
import lugang.app.huansi.net.lugang.adapter.RemarkDetailAdapter;
import lugang.app.huansi.net.lugang.bean.RemarkCategoryBean;
import lugang.app.huansi.net.lugang.bean.RemarkDetailBean;
import lugang.app.huansi.net.lugang.databinding.ActivityRemarkDetailBinding;
import lugang.app.huansi.net.lugang.event.SecondToFirstActivityEvent;
import lugang.app.huansi.net.util.GreenDaoUtil;
import lugang.app.huansi.net.util.LGSPUtils;
import rx.functions.Func1;

import static huansi.net.qianjingapp.utils.NewRxjavaWebUtils.getJsonData;
import static huansi.net.qianjingapp.utils.WebServices.WebServiceType.CUS_SERVICE;
import static lugang.app.huansi.net.lugang.constant.Constant.MeasureCustomActivityConstant.ORDER_DTL_ID_INTENT;
import static lugang.app.huansi.net.lugang.constant.Constant.MeasureCustomActivityConstant.REMARK_INTENT_DATA;
import static lugang.app.huansi.net.lugang.constant.Constant.MeasureCustomActivityConstant.REMARK_INTENT_KEY;
import static lugang.app.huansi.net.lugang.constant.Constant.MeasureCustomActivityConstant.REMARK_RETURN_DATA;
import static lugang.app.huansi.net.lugang.constant.Constant.MeasureCustomActivityConstant.STYLE_ID_INTENT;
import static lugang.app.huansi.net.lugang.constant.Constant.MeasureCustomActivityConstant.STYLE_ID_KEY;
import static lugang.app.huansi.net.util.LGSPUtils.USER_GUID;

/**
 * 备注详情页面
 */
@SuppressWarnings("unchecked")
public class RemarkDetailActivity extends NotWebBaseActivity {

    private ActivityRemarkDetailBinding mActivityRemarkDetailBinding;
    private List<RemarkDetailDataInSQLite> remarkDetailList;
    private List<RemarkCategoryDataInSQLite> remarkCategoryList;
    private List<MeasureRemarkDataInSQLite> remarkAddList;//选中的list

    private RemarkCategoryAdapter mRemarkCategoryAdapter;//备注大类的adapter
    private RemarkDetailAdapter mRemarkDetailAdapter;//备注明细的adapter
    private RemarkAddAdapter mRemarkAddAdapter;//已选的adapter

    private  String orderDtlID;//订单明细ID
    private String styleId;//款式ID

    @Override
    protected int getLayoutId() {
        return R.layout.activity_remark_detail;
    }

    @Override
    public void init() {
        mActivityRemarkDetailBinding = (ActivityRemarkDetailBinding) viewDataBinding;

        styleId = getIntent().getStringExtra(STYLE_ID_INTENT);//款式ID
        orderDtlID= getIntent().getStringExtra(ORDER_DTL_ID_INTENT);
        remarkAddList= (List<MeasureRemarkDataInSQLite>) getIntent().getSerializableExtra(REMARK_INTENT_DATA);
        if(remarkAddList==null) remarkAddList=new ArrayList<>();

        remarkDetailList=new ArrayList<>();
        remarkCategoryList=new ArrayList<>();
        mRemarkCategoryAdapter=new RemarkCategoryAdapter(remarkCategoryList,getApplicationContext());
        mActivityRemarkDetailBinding.lvRemarkCategory.setAdapter(mRemarkCategoryAdapter);
        mRemarkDetailAdapter=new RemarkDetailAdapter(remarkDetailList,getApplicationContext());
        mActivityRemarkDetailBinding.lvRemarkDetail.setAdapter(mRemarkDetailAdapter);

        mRemarkAddAdapter=new RemarkAddAdapter(remarkAddList,getApplicationContext());
        mActivityRemarkDetailBinding.lvRemarkAdd.setAdapter(mRemarkAddAdapter);

        //点击按钮添加
        mActivityRemarkDetailBinding.butAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<MeasureRemarkDataInSQLite> chooseList=new ArrayList<>();
                String userGUID= LGSPUtils.getLocalData(getApplicationContext(), USER_GUID,String.class.getName(),"").toString();
                for(int i=0;i<remarkDetailList.size();i++){
                    RemarkDetailDataInSQLite remarkDetailDataInSQLite=remarkDetailList.get(i);
                    if(!remarkDetailDataInSQLite.isAdd&&remarkDetailDataInSQLite.isChoose) {
//                        try {
                        MeasureRemarkDataInSQLite measureRemarkDataInSQLite = new MeasureRemarkDataInSQLite();
//                            RemarkDetailDataInSQLite addRemarkDetailData= (RemarkDetailDataInSQLite) remarkDetailDataInSQLite.clone();
                        measureRemarkDataInSQLite.setIOrderDtlId(orderDtlID);
                        measureRemarkDataInSQLite.setSMeterMarkName(remarkDetailDataInSQLite.getSMeterMarkName());
                        measureRemarkDataInSQLite.setSMeterMarkCode(remarkDetailDataInSQLite.getSMeterMarkCode());
                        measureRemarkDataInSQLite.setUserGUID(userGUID);
                        measureRemarkDataInSQLite.setIId(remarkDetailDataInSQLite.getIId());
//                            measureRemarkDataInSQLite.setType();
//                            measureRemarkDataInSQLite.setPerson();
//                            measureRemarkDataInSQLite.set
//                            RemarkDetailBean addBean=new RemarkDetailBean();
//                            addBean.IID=bean.IID;
//                            addBean.SMETERMARKCODE=bean.SMETERMARKCODE;
//                            addBean.SMETERMARKNAME=bean.SMETERMARKNAME;
//                            addBean.iOrderDtlId=orderDtlID;
                        measureRemarkDataInSQLite.isChoose = false;
                        chooseList.add(measureRemarkDataInSQLite);

//                        } catch (CloneNotSupportedException e) {
//                            e.printStackTrace();
//                        }
                    }
                }
                if (chooseList.isEmpty()) {
                    OthersUtil.ToastMsg(RemarkDetailActivity.this, "请先选择要添加的备注 ");
                    return;
                }
                remarkAddList.addAll(chooseList);
                mRemarkAddAdapter.notifyDataSetChanged();
                showDetailData();
            }
        });
        ///点击按钮删除
        mActivityRemarkDetailBinding.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<MeasureRemarkDataInSQLite> chooseList=new ArrayList<>();

                for(int i=0;i<remarkAddList.size();i++){
                    MeasureRemarkDataInSQLite measureRemarkDataInSQLite=remarkAddList.get(i);
                    if(measureRemarkDataInSQLite.isChoose){
                        chooseList.add(measureRemarkDataInSQLite);
                    }
                }
                if(chooseList.isEmpty()){
                    OthersUtil.ToastMsg(RemarkDetailActivity.this,"请先选择要删除的备注 ");
                    return;
                }
                remarkAddList.removeAll(chooseList);
                mRemarkAddAdapter.notifyDataSetChanged();
                showDetailData();
            }
        });


        mActivityRemarkDetailBinding.lvRemarkCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(remarkCategoryList.get(position).isChoose)return;
                for(int i=0;i<remarkCategoryList.size();i++){
                    remarkCategoryList.get(i).isChoose=false;
                }
                remarkCategoryList.get(position).isChoose=true;
                mRemarkCategoryAdapter.notifyDataSetChanged();
                RemarkCategoryDataInSQLite remarkCategoryDataInSQLite=remarkCategoryList.get(position);
                requestRemarkDetailDate(remarkCategoryDataInSQLite.getIId());
            }
        });

        mActivityRemarkDetailBinding.lvRemarkDetail.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(remarkDetailList.get(position).isAdd) return;
                remarkDetailList.get(position).isChoose=!remarkDetailList.get(position).isChoose;
                mRemarkDetailAdapter.notifyDataSetChanged();
            }
        });

        mActivityRemarkDetailBinding.lvRemarkAdd.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                remarkAddList.get(position).isChoose=!remarkAddList.get(position).isChoose;
                mRemarkAddAdapter.notifyDataSetChanged();
            }
        });

        initData();

    }

    /**
     * 获取网络返回的数据
     */
    @SuppressWarnings("unchecked")
    private void initData() {
        OthersUtil.showLoadDialog(mDialog);
        remarkCategoryList.clear();
        NewRxjavaWebUtils.getUIThread(NewRxjavaWebUtils.getObservable(this, "")
                //获取备注大类
                .map(new Func1<String, HsWebInfo>() {
                    @Override
                    public HsWebInfo call(String s) {
                        HsWebInfo info=null;
                        List<RemarkCategoryDataInSQLite> list=new ArrayList<>();
                        if(NetUtil.isNetworkAvailable(getApplicationContext())){
                            info= getJsonData(getApplicationContext(), CUS_SERVICE,
                                    "spappMeasureRemarkList",
                                    "isdStyleTypeMstId="+styleId,
                                    RemarkCategoryBean.class.getName(),
                                    true, "没有查询到备注大类");
                            if(!info.success) return info;
                            List<WsEntity> remarkHdrList=info.wsData.LISTWSDATA;
                            for(WsEntity entity:remarkHdrList){
                                RemarkCategoryBean bean= (RemarkCategoryBean) entity;
                                RemarkCategoryDataInSQLite categoryDataInSQLite=new RemarkCategoryDataInSQLite();
                                categoryDataInSQLite.setIId(bean.IID);
                                categoryDataInSQLite.setSBillNo(bean.SBILLNO);
                                categoryDataInSQLite.setSMeterMarkName(bean.SMETERMARKNAME);
                                categoryDataInSQLite.setStyleId(styleId);
                                list.add(categoryDataInSQLite);
                            }
                        }else {

                            RemarkCategoryDataInSQLiteDao dao= GreenDaoUtil.getGreenDaoSession(getApplicationContext()).getRemarkCategoryDataInSQLiteDao();
                            List<RemarkCategoryDataInSQLite> categoryDataInSQLiteList=null;
                            try {
                                categoryDataInSQLiteList=dao.queryBuilder()
                                        .where(RemarkCategoryDataInSQLiteDao.Properties.StyleId.eq(styleId))
                                        .list();
                            }catch (Exception e){}
                            if(categoryDataInSQLiteList==null) categoryDataInSQLiteList=new ArrayList<>();
                            list.addAll(categoryDataInSQLiteList);
                        }
                        Map<String,Object> map=new HashMap<>();
                        map.put("remarkHdrList",list);
                        if(info==null) info=new HsWebInfo();
                        info.object=map;
                        return info;
                    }
                })
                , this, mDialog, new SimpleHsWeb() {
            @Override
            public void success(HsWebInfo hsWebInfo) {
                Map<String,Object> map= (Map<String, Object>) hsWebInfo.object;
                List<RemarkCategoryDataInSQLite> remarkHdrList= (List<RemarkCategoryDataInSQLite>) map.get("remarkHdrList");
//                for(WsEntity entity:remarkHdrList){
//                    remarkCategoryList.add((RemarkCategoryBean) entity);
//                }
                remarkCategoryList.addAll(remarkHdrList);
                if(!remarkCategoryList.isEmpty()) remarkCategoryList.get(0).isChoose=true;
                mRemarkCategoryAdapter.notifyDataSetChanged();
                if(!remarkCategoryList.isEmpty()) requestRemarkDetailDate(remarkCategoryList.get(0).getIId());
            }
        });
    }


    /**
     * 获取备注的详情
     */
    private void requestRemarkDetailDate( String remarkCategoryId) {
        OthersUtil.showLoadDialog(mDialog);
        remarkDetailList.clear();

        NewRxjavaWebUtils.getUIThread(NewRxjavaWebUtils.getObservable(this, remarkCategoryId)
                .map(new Func1<String, HsWebInfo>() {
                    @Override
                    public HsWebInfo call(String remarkCategoryId) {
                        HsWebInfo info=null;
                        List<RemarkDetailDataInSQLite> list=new ArrayList<>();
                        if(NetUtil.isNetworkAvailable(getApplicationContext())){
                            info= getJsonData(getApplicationContext(), CUS_SERVICE,
                                    "spappMeasureRemarkList"
                                    ,"isdMeterMarkMstid="+remarkCategoryId,
                                    RemarkDetailBean.class.getName(),
                                    true, "");
                            if(!info.success) return info;
                            for(WsEntity entity:info.wsData.LISTWSDATA){
                                RemarkDetailBean bean= (RemarkDetailBean) entity;
                                RemarkDetailDataInSQLite remarkDetailDataInSQLite=new RemarkDetailDataInSQLite();
                                remarkDetailDataInSQLite.setIId(bean.IID);
//                                remarkDetailDataInSQLite.setIOrderDtlId(orderDtlID);
                                remarkDetailDataInSQLite.setRemarkCategoryId(remarkCategoryId);
                                remarkDetailDataInSQLite.setSMeterMarkCode(bean.SMETERMARKCODE);
                                remarkDetailDataInSQLite.setSMeterMarkName(bean.SMETERMARKNAME);
                                list.add(remarkDetailDataInSQLite);
                            }
                        }else {
                            RemarkDetailDataInSQLiteDao dao=GreenDaoUtil.getGreenDaoSession(getApplicationContext()).getRemarkDetailDataInSQLiteDao();
                            List<RemarkDetailDataInSQLite> detailDataInSQLiteList=null;
                            try {
                                detailDataInSQLiteList=dao.queryBuilder()
                                        .where(RemarkDetailDataInSQLiteDao.Properties.RemarkCategoryId.eq(remarkCategoryId))
                                        .list();
                            }catch (Exception e){}
                            if(detailDataInSQLiteList==null) detailDataInSQLiteList=new ArrayList<>();
                            list.addAll(detailDataInSQLiteList);
                        }
                        if(info==null) info=new HsWebInfo();
                        info.object=list;
                        return info;
                    }
                }), this, mDialog, new SimpleHsWeb() {
            @Override
            public void success(HsWebInfo hsWebInfo) {
//                for(WsEntity entity:hsWebInfo.wsData.LISTWSDATA){
//                    remarkDetailList.add((RemarkDetailBean) entity);
//                }
                remarkDetailList.addAll((List<RemarkDetailDataInSQLite>) hsWebInfo.object);
                showDetailData();
            }

            @Override
            public void error(HsWebInfo hsWebInfo, Context context) {
                super.error(hsWebInfo, context);
                showDetailData();
            }
        });
    }

    /**
     * 显示明细数据
     */
    private void showDetailData(){
        Map<String,String> map=new HashMap<>();
        for(int i=0;i<remarkAddList.size();i++){
            MeasureRemarkDataInSQLite measureRemarkDataInSQLite=remarkAddList.get(i);
            map.put(measureRemarkDataInSQLite.getIId(),measureRemarkDataInSQLite.getIId());
        }
        for(int i=0;i<remarkDetailList.size();i++){
            RemarkDetailDataInSQLite remarkDetailDataInSQLite=remarkDetailList.get(i);
            remarkDetailDataInSQLite.isAdd=map.get(remarkDetailDataInSQLite.getIId())!=null;
            remarkDetailDataInSQLite.isChoose=false;
            remarkDetailList.set(i,remarkDetailDataInSQLite);
        }
        mRemarkDetailAdapter.notifyDataSetChanged();
    }


    @Override
    protected void onDestroy() {
        Map<String,Object>map=new HashMap<>();
        map.put(STYLE_ID_KEY,getIntent().getStringExtra(STYLE_ID_INTENT));
        map.put(REMARK_RETURN_DATA,remarkAddList);
        EventBus.getDefault().post(new SecondToFirstActivityEvent(REMARK_INTENT_KEY,
                MeasureCustomActivity.class,RemarkDetailActivity.class,map));
        super.onDestroy();
    }

}
