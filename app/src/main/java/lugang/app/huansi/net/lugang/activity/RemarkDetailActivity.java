package lugang.app.huansi.net.lugang.activity;

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
import huansi.net.qianjingapp.utils.NewRxjavaWebUtils;
import huansi.net.qianjingapp.utils.OthersUtil;
import lugang.app.huansi.net.lugang.R;
import lugang.app.huansi.net.lugang.adapter.RemarkAddAdapter;
import lugang.app.huansi.net.lugang.adapter.RemarkCategoryAdapter;
import lugang.app.huansi.net.lugang.adapter.RemarkDetailAdapter;
import lugang.app.huansi.net.lugang.bean.RemarkCategoryBean;
import lugang.app.huansi.net.lugang.bean.RemarkDetailBean;
import lugang.app.huansi.net.lugang.databinding.ActivityRemarkDetailBinding;
import lugang.app.huansi.net.lugang.event.SecondToFirstActivityEvent;
import rx.functions.Func1;

import static huansi.net.qianjingapp.utils.NewRxjavaWebUtils.getJsonData;
import static huansi.net.qianjingapp.utils.WebServices.WebServiceType.CUS_SERVICE;
import static lugang.app.huansi.net.lugang.constant.Constant.MeasureCustomActivityConstant.ORDER_DTL_ID_INTENT;
import static lugang.app.huansi.net.lugang.constant.Constant.MeasureCustomActivityConstant.REMARK_INTENT_DATA;
import static lugang.app.huansi.net.lugang.constant.Constant.MeasureCustomActivityConstant.REMARK_INTENT_KEY;
import static lugang.app.huansi.net.lugang.constant.Constant.MeasureCustomActivityConstant.REMARK_RETURN_DATA;
import static lugang.app.huansi.net.lugang.constant.Constant.MeasureCustomActivityConstant.STYLE_ID_INTENT;
import static lugang.app.huansi.net.lugang.constant.Constant.MeasureCustomActivityConstant.STYLE_ID_KEY;

/**
 * 备注详情页面
 */
@SuppressWarnings("unchecked")
public class RemarkDetailActivity extends NotWebBaseActivity {

    private ActivityRemarkDetailBinding mActivityRemarkDetailBinding;
    private List<RemarkDetailBean> remarkDetailList;
    private List<RemarkCategoryBean> remarkCategoryList;
    private List<RemarkDetailBean> remarkAddList;//选中的list

    private RemarkCategoryAdapter mRemarkCategoryAdapter;//备注大类的adapter
    private RemarkDetailAdapter mRemarkDetailAdapter;//备注明细的adapter
    private RemarkAddAdapter mRemarkAddAdapter;//已选的adapter

    private  String orderDtlID;//订单明细ID

    @Override
    protected int getLayoutId() {
        return R.layout.activity_remark_detail;
    }

    @Override
    public void init() {
        mActivityRemarkDetailBinding = (ActivityRemarkDetailBinding) viewDataBinding;

        String styleId = getIntent().getStringExtra(STYLE_ID_INTENT);//款式ID
        orderDtlID= getIntent().getStringExtra(ORDER_DTL_ID_INTENT);


        remarkAddList= (List<RemarkDetailBean>) getIntent().getSerializableExtra(REMARK_INTENT_DATA);
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
                List<RemarkDetailBean> chooseList=new ArrayList<>();

                for(int i=0;i<remarkDetailList.size();i++){
                    RemarkDetailBean bean=remarkDetailList.get(i);
                    if(!bean.isAdd&&bean.isChoose){
                        RemarkDetailBean addBean=new RemarkDetailBean();
                        addBean.IID=bean.IID;
                        addBean.SMETERMARKCODE=bean.SMETERMARKCODE;
                        addBean.SMETERMARKNAME=bean.SMETERMARKNAME;
                        addBean.iOrderDtlId=orderDtlID;
                        addBean.isChoose=false;
                        chooseList.add(addBean);
                    }
                }
                if(chooseList.isEmpty()){
                    OthersUtil.ToastMsg(RemarkDetailActivity.this,"请先选择要添加的备注 ");
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

                List<RemarkDetailBean> chooseList=new ArrayList<>();

                for(int i=0;i<remarkAddList.size();i++){
                    RemarkDetailBean bean=remarkAddList.get(i);
                    if(bean.isChoose){
                        chooseList.add(bean);
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
                RemarkCategoryBean categoryBean=remarkCategoryList.get(position);
                requestRemarkDetailDate(categoryBean.IID);
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

        initData(styleId);

    }

    /**
     * 获取网络返回的数据
     */
    @SuppressWarnings("unchecked")
    private void initData(final String styleId) {
        OthersUtil.showLoadDialog(mDialog);
        NewRxjavaWebUtils.getUIThread(NewRxjavaWebUtils.getObservable(this, "")
                //获取备注大类
                .map(new Func1<String, HsWebInfo>() {
                    @Override
                    public HsWebInfo call(String s) {
                        HsWebInfo info= getJsonData(getApplicationContext(), CUS_SERVICE,
                                "spappMeasureRemarkList",
                                "isdStyleTypeMstId="+styleId,
                                RemarkCategoryBean.class.getName(),
                                true, "没有查询到备注大类");
                        if(!info.success) return info;
                        List<WsEntity> remarkHdrList=info.wsData.LISTWSDATA;
                        Map<String,Object> map=new HashMap<>();
                        map.put("remarkHdrList",remarkHdrList);
                        info.object=map;
                        return info;
                    }
                })
                , this, mDialog, new SimpleHsWeb() {
            @Override
            public void success(HsWebInfo hsWebInfo) {
                Map<String,Object> map= (Map<String, Object>) hsWebInfo.object;
                List<WsEntity> remarkHdrList= (List<WsEntity>) map.get("remarkHdrList");
                for(WsEntity entity:remarkHdrList){
                    remarkCategoryList.add((RemarkCategoryBean) entity);
                }
                if(!remarkCategoryList.isEmpty()) remarkCategoryList.get(0).isChoose=true;
                mRemarkCategoryAdapter.notifyDataSetChanged();
                if(!remarkCategoryList.isEmpty())
                    requestRemarkDetailDate(remarkCategoryList.get(0).IID);
            }
        });
    }


    /**
     * 获取备注的详情
     */
    private void requestRemarkDetailDate(final String remarkCategoryId) {
        OthersUtil.showLoadDialog(mDialog);
        remarkDetailList.clear();

        NewRxjavaWebUtils.getUIThread(NewRxjavaWebUtils.getObservable(this, "")
                .map(new Func1<String, HsWebInfo>() {
                    @Override
                    public HsWebInfo call(String s) {
                        return getJsonData(getApplicationContext(), CUS_SERVICE,
                                "spappMeasureRemarkList"
                                ,"isdMeterMarkMstid="+remarkCategoryId,
                                RemarkDetailBean.class.getName(),
                                true, "");
                    }
                }), this, mDialog, new SimpleHsWeb() {
            @Override
            public void success(HsWebInfo hsWebInfo) {
                for(WsEntity entity:hsWebInfo.wsData.LISTWSDATA){
                    remarkDetailList.add((RemarkDetailBean) entity);
                }
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
            RemarkDetailBean bean=remarkAddList.get(i);
            map.put(bean.IID,bean.IID);
        }
        for(int i=0;i<remarkDetailList.size();i++){
            RemarkDetailBean bean=remarkDetailList.get(i);
            bean.isAdd=map.get(bean.IID)!=null;
            bean.isChoose=false;
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
