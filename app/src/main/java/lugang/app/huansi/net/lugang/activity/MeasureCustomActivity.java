package lugang.app.huansi.net.lugang.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
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
import lugang.app.huansi.net.lugang.bean.MeasureCustomBean;
import lugang.app.huansi.net.lugang.bean.MeasureDateBean;
import lugang.app.huansi.net.lugang.bean.RemarkDetailBean;
import lugang.app.huansi.net.lugang.bean.RemarkSavedBean;
import lugang.app.huansi.net.lugang.constant.Constant;
import lugang.app.huansi.net.lugang.databinding.ActivityMeasureCustomBinding;
import lugang.app.huansi.net.lugang.event.SecondToFirstActivityEvent;
import rx.functions.Func1;

import static huansi.net.qianjingapp.utils.NewRxjavaWebUtils.getJsonData;
import static huansi.net.qianjingapp.utils.SPHelper.USER_GUID;
import static huansi.net.qianjingapp.utils.WebServices.WebServiceType.CUS_SERVICE;
import static lugang.app.huansi.net.lugang.constant.Constant.MeasureCustomActivityConstant.ORDER_DTL_ID_INTENT;
import static lugang.app.huansi.net.lugang.constant.Constant.MeasureCustomActivityConstant.REMARK_INTENT_DATA;
import static lugang.app.huansi.net.lugang.constant.Constant.MeasureCustomActivityConstant.REMARK_INTENT_KEY;
import static lugang.app.huansi.net.lugang.constant.Constant.MeasureCustomActivityConstant.REMARK_RETURN_DATA;
import static lugang.app.huansi.net.lugang.constant.Constant.MeasureCustomActivityConstant.STYLE_ID_INTENT;
import static lugang.app.huansi.net.lugang.constant.Constant.MeasureCustomActivityConstant.STYLE_ID_KEY;

/**
 * 量体定制详情页
 */

public class MeasureCustomActivity extends NotWebBaseActivity {

    private ActivityMeasureCustomBinding mActivityMeasureCustomBinding;
    private List<List<MeasureCustomBean>> mMeasureCustomLists;
    private List<List<RemarkDetailBean>> remarkAllList;//备注列表
    @Override
    protected int getLayoutId() {
        return R.layout.activity_measure_custom;
    }
    @Override
    public void init() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);//默认自动跳出软键盘
        mActivityMeasureCustomBinding = (ActivityMeasureCustomBinding) viewDataBinding;
        OthersUtil.registerEvent(this);
        remarkAllList=new ArrayList<>();
        mMeasureCustomLists=new ArrayList<>();
        final String userGUID= SPHelper.getLocalData(getApplicationContext(),USER_GUID,String.class.getName(),"").toString();
        Intent intent = getIntent();
        final String sperson = intent.getStringExtra(Constant.SPERSON);
        String departmentName = intent.getStringExtra(Constant.SDEPARTMENTNAME);
        final String orderId = intent.getStringExtra(Constant.ISDORDERMETERMSTID);//订单头表id
        final String iOrderType = intent.getStringExtra(Constant.IORDERTYPE);//区分从哪个界面跳转
        mActivityMeasureCustomBinding.customName.setText(departmentName+": " + sperson);
        mActivityMeasureCustomBinding.btnSaveMeasure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MeasureCustomActivity.this);
                builder.setTitle("提示");
                builder.setMessage("是否确定信息无误并同步到服务器");
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveMeasure(userGUID);//保存录入信息
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        initData(orderId,iOrderType,sperson);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MeasureCustomActivity.this);
        builder.setTitle("提示");
        builder.setMessage("确定要退出当前页面吗");
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    //保存录入信息
    private void saveMeasure(final String userGUID) {
        OthersUtil.showLoadDialog(mDialog);
        //查询输入框的数据，并保存到数组中
        for(int i=0;i<mMeasureCustomLists.size();i++){
            List<MeasureCustomBean> subList=mMeasureCustomLists.get(i);
            View item=mActivityMeasureCustomBinding.llCloth.getChildAt(i);
            LinearLayout linearLayout = (LinearLayout) item.findViewById(R.id.llClothTypeList);
            for(int j=0;j<subList.size();j++){
                MeasureCustomBean bean=subList.get(j);
                View subItem=linearLayout.getChildAt(j);
                EditText editText = (EditText) subItem.findViewById(R.id.etParameter);
                String size=editText.getText().toString().trim();
                if(size.isEmpty()) size="0";
                bean.ISMETERSIZE=size;
                subList.set(j,bean);
            }
            mMeasureCustomLists.set(i,subList);
        }
        NewRxjavaWebUtils.getUIThread(NewRxjavaWebUtils.getObservable(this, "")
                        .map(new Func1<String, HsWebInfo>() {

//                            private String mIsdordermeterdtlid;

                            @Override
                            public HsWebInfo call(String s) {
                                StringBuilder sbStr = new StringBuilder();
                                for (int i = 0; i < mMeasureCustomLists.size(); i++) {
                                    List<MeasureCustomBean> subList = mMeasureCustomLists.get(i);
                                    for (int j = 0; j < subList.size(); j++) {
                                        MeasureCustomBean bean = subList.get(j);
                                        sbStr.append("EXEC spappMeasureSaveMeasureData ")
                                             .append("@uHrEmployeeGUID='").append(userGUID ).append("'")
                                             .append(",@isdOrderMeterDtlid=").append(bean.ISDORDERMETERDTLID)
                                             .append(",@isMeterSize=").append(bean.ISMETERSIZE)
                                             .append(",@isdStyleTypeItemDtlid=").append(bean.SDSTYLETYPEITEMDTLID)
                                             .append(";");
                                    }
                                }

                                for (int i = 0; i < remarkAllList.size(); i++) {
                                    StringBuilder sbRemarkId=new StringBuilder();
                                    List<RemarkDetailBean> subList = remarkAllList.get(i);
                                    if (subList == null) subList = new ArrayList<>();
                                    String orderDtlId="";
                                    for (int j=0;j<subList.size();j++) {
                                        RemarkDetailBean remarkDetailBean=subList.get(j);
                                        orderDtlId=remarkDetailBean.iOrderDtlId;
                                        sbRemarkId.append(remarkDetailBean.IID);
                                        if(j!=subList.size()-1) sbRemarkId.append("@");
                                    }
                                    if(!orderDtlId.isEmpty())
                                        sbStr.append("EXEC spappMeasureSaveMeasureRemark ")
                                        .append("@sSdMeterMarkDtlid='").append(sbRemarkId.toString()).append("'")
                                        .append(",@isdOrderMeterDtlid=").append(orderDtlId)
                                        .append("; ");
                                }
                                return getJsonData(getApplicationContext(), CUS_SERVICE,
                                        sbStr.toString(), "", MeasureCustomBean.class.getName(), true,
                                        "上传失败！！");
                            }
                        })
                , this, mDialog, new SimpleHsWeb() {
                    @Override
                    public void success(HsWebInfo hsWebInfo) {
                        OthersUtil.ToastMsg(MeasureCustomActivity.this, "上传成功！！");
                        finish();
                    }
                });
    }

    /**
     * 初始化数据
     */
    @SuppressWarnings("unchecked")
    private void initData(final String orderId,final String iOrderType,final String sPerson) {
        OthersUtil.showLoadDialog(mDialog);
        mMeasureCustomLists.clear();
        NewRxjavaWebUtils.getUIThread(NewRxjavaWebUtils.getObservable(this, "")
                        .map(new Func1<String, HsWebInfo>() {
                            @Override
                            public HsWebInfo call(String s) {
                                HsWebInfo hsWebInfo = NewRxjavaWebUtils.getJsonData(getApplicationContext(), CUS_SERVICE,
                                        "spappMeasureStyleTypeList",
                                        "iIndex=0" +",iOrderType="+ iOrderType+",iSdOrderMeterMstId=" + orderId+
                                        ",sPerson="+sPerson,
                                        MeasureCustomBean.class.getName(),
                                        true, "待量体款式信息未获取到，请重试！");
                                Map<String, Object> map = new HashMap<>();
                                if (!hsWebInfo.success) return hsWebInfo;
                                map.put("measureStyle", hsWebInfo.wsData.LISTWSDATA);
                                hsWebInfo.object = map;
                                return hsWebInfo;
                            }
                        })
                        .map(new Func1<HsWebInfo, HsWebInfo>() {
                            @Override
                            public HsWebInfo call(HsWebInfo hsWebInfo) {
                                Map<String, Object> map = (Map<String, Object>) hsWebInfo.object;
                                HsWebInfo hsInfo = NewRxjavaWebUtils.getJsonData(getApplicationContext(), CUS_SERVICE,
                                        "spappMeasureStyleTypeList",
                                        "iIndex=1" +",iOrderType="+ iOrderType+ ",iSdOrderMeterMstId=" + orderId+
                                                ",sPerson="+sPerson,
                                        MeasureDateBean.class.getName(),
                                        true, "已量体款式信息未获取到，请重试！");
                                map.put("measureStyleData", !hsInfo.success ? new ArrayList<WsEntity>() : hsInfo.wsData.LISTWSDATA);
                                hsWebInfo.object = map;
                                return hsWebInfo;
                            }
                        })
                , this, mDialog, new SimpleHsWeb() {
                    @Override
                    public void success(HsWebInfo hsWebInfo) {
                        Map<String, Object> map = (Map<String, Object>) hsWebInfo.object;
                        //获取量体款式的名字
                        List<WsEntity> measureStyleList = (List<WsEntity>) map.get("measureStyle");
                        //获取量体款式的数据
                        List<WsEntity> measureDateList = (List<WsEntity>) map.get("measureStyleData");

                        showMeasureType(measureStyleList);
                        initRemarkSaved(orderId,iOrderType,measureDateList,sPerson);

                    }
                });
    }

    /**
     * 查询已上传备注的信息
     */
    private void initRemarkSaved(final String orderId,final String iOrderType, final List<WsEntity> measureDateList,final String sPerson){
        OthersUtil.showLoadDialog(mDialog);
        remarkAllList.clear();

        NewRxjavaWebUtils.getUIThread(NewRxjavaWebUtils.getObservable(this, orderId)
                .map(new Func1<String, HsWebInfo>() {
                    @Override
                    public HsWebInfo call(String orderId) {
                        for(List<MeasureCustomBean> subList:mMeasureCustomLists) {
                            if (subList == null || subList.isEmpty()) {
                                remarkAllList.add(new ArrayList<RemarkDetailBean>());
                                continue;
                            }
                            try {
                                MeasureCustomBean measureCustomBean=subList.get(0);
                                HsWebInfo info = NewRxjavaWebUtils.getJsonData(getApplicationContext(), CUS_SERVICE,
                                        "spappMeasureStyleTypeList",
                                        "iIndex=2" +",iOrderType="+ iOrderType+
                                        ",iSdOrderMeterMstId="+orderId+
                                        ",iSdOrderMeterDtlId=" + measureCustomBean.ISDORDERMETERDTLID +
                                                ",sPerson="+sPerson+
                                        ",isdStyleTypeMstId=" + measureCustomBean.ISDSTYLETYPEMSTID,
                                        RemarkSavedBean.class.getName(),
                                        true,
                                        "");
                                if(!info.success)remarkAllList.add(new ArrayList<RemarkDetailBean>());
                                else {
                                    List<RemarkDetailBean> remarkList=new ArrayList<>();
                                    List<WsEntity> entities=info.wsData.LISTWSDATA;
                                    for(WsEntity entity:entities){
                                        RemarkSavedBean remarkSavedBean= (RemarkSavedBean) entity;
                                        RemarkDetailBean detailBean=new RemarkDetailBean();
                                        detailBean.IID=remarkSavedBean.ISMETERMARKDTLID;
                                        detailBean.SMETERMARKCODE=remarkSavedBean.SMETERMARKCODE;
                                        detailBean.SMETERMARKNAME=remarkSavedBean.SMETERMARKNAME;
                                        detailBean.iOrderDtlId=remarkSavedBean.ISDORDERMETERDTLID;
                                        remarkList.add(detailBean);
                                    }
                                    remarkAllList.add(remarkList);
                                }
                            }catch (Exception e){
                                remarkAllList.add(new ArrayList<RemarkDetailBean>());
                            }
                        }
                        return new HsWebInfo();
                    }
                }), getApplicationContext(), mDialog, new SimpleHsWeb() {
            @Override
            public void success(HsWebInfo hsWebInfo) {
                //显示量体数据
                showMeasureDataSaved(measureDateList);
            }
        });
    }

    /**
     *    获取量体款式的名字
     */
    private void showMeasureType(List<WsEntity> measureStyleList) {
        Map<String, List<MeasureCustomBean>> map = new HashMap<>();
        for (int i = 0; i < measureStyleList.size(); i++) {
            MeasureCustomBean measureCustomBean = (MeasureCustomBean) measureStyleList.get(i);
            List<MeasureCustomBean> measureCustomBeanList = map.get(measureCustomBean.ISDSTYLETYPEMSTID);
            if (measureCustomBeanList == null) measureCustomBeanList = new ArrayList<>();
            measureCustomBeanList.add(measureCustomBean);
            map.put(measureCustomBean.ISDSTYLETYPEMSTID, measureCustomBeanList);
        }
        Iterator<Map.Entry<String, List<MeasureCustomBean>>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, List<MeasureCustomBean>> entry = it.next();
            List<MeasureCustomBean> subList = entry.getValue();
            Collections.sort(subList, new Comparator<MeasureCustomBean>() {
                @Override
                public int compare(MeasureCustomBean o1, MeasureCustomBean o2) {
                    try {
                        return Integer.parseInt(o1.ISEQ)-Integer.parseInt(o2.ISEQ);
                    } catch (Exception e) {
                        return 0;
                    }
                }
            });
            mMeasureCustomLists.add(subList);
        }
    }

    /**
     * 显示传上去的量体数据
     */
    private void showMeasureDataSaved(List<WsEntity> measureDateList) {
        Map<String,MeasureDateBean> measureDataMap=new HashMap<>();
        for (int i = 0; i <measureDateList.size() ; i++) {
            MeasureDateBean measureDateBean = (MeasureDateBean) measureDateList.get(i);
            measureDataMap.put(measureDateBean.ISDSTYLETYPEITEMDTLID+"_"+measureDateBean.ISDORDERMETERDTLID,measureDateBean);
        }
        LayoutInflater layoutInflater = getLayoutInflater();
        //添加View 即每个款式
        for (int i = 0; i < mMeasureCustomLists.size(); i++) {
            View view = layoutInflater.inflate(R.layout.activity_measure_detial, null);
            TextView tvClothStyle = (TextView) view.findViewById(R.id.tvClothStyle);
            tvClothStyle.setText(mMeasureCustomLists.get(i).get(0).SVALUEGROUP);
            List<MeasureCustomBean> measureBeanList = mMeasureCustomLists.get(i);
            //添加咩咯款式每条量体信息
            LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.llClothTypeList);
            for (MeasureCustomBean measureCustom : measureBeanList) {
                View convertView = LinearLayout.inflate(getApplicationContext(), R.layout.ll_parameter, null);
                TextView tvParameter = (TextView) convertView.findViewById(R.id.tvParameter);
                tvParameter.setText(measureCustom.SMETERNAME);
                MeasureDateBean measureDateBean=measureDataMap.get(measureCustom.SDSTYLETYPEITEMDTLID+"_"+measureCustom.ISDORDERMETERDTLID);
                measureCustom.ISMETERSIZE=measureDateBean==null?"":measureDateBean.ISMETERSIZE;
                final EditText editText = (EditText) convertView.findViewById(R.id.etParameter);
                editText.setText(measureCustom.ISMETERSIZE);
                linearLayout.addView(convertView);
            }
            mActivityMeasureCustomBinding.llCloth.setGravity(Gravity.CENTER_HORIZONTAL);
            WindowManager wm =getWindowManager();
            int width = wm.getDefaultDisplay().getWidth();
            int height = wm.getDefaultDisplay().getHeight();
            LinearLayout remarkLayout= (LinearLayout) view.findViewById(R.id.remarkLayout);

            //跳转到备注界面
            final int finalI = i;
            final int finalI1 = i;
            remarkLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MeasureCustomActivity.this, RemarkDetailActivity.class);
                    intent.putExtra(STYLE_ID_INTENT, mMeasureCustomLists.get(finalI1).get(0).ISDSTYLETYPEMSTID);
                    intent.putExtra(ORDER_DTL_ID_INTENT, mMeasureCustomLists.get(finalI1).get(0).ISDORDERMETERDTLID);
                    intent.putExtra(REMARK_INTENT_DATA, (Serializable) remarkAllList.get(finalI));
                    startActivity(intent);
                }
            });

            final CheckBox cbRemark = (CheckBox) view.findViewById(R.id.cbRemark);
            try {
                cbRemark.setChecked(remarkAllList.get(finalI)!=null&&!remarkAllList.get(finalI).isEmpty());
            }catch (Exception e){
                cbRemark.setChecked(false);
            }
            mActivityMeasureCustomBinding.llCloth.addView(view, width / 5, height - 110);
        }
    }


    @SuppressWarnings("unchecked")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode!=RESULT_OK) return;
        if(data==null) return;
        switch (requestCode) {
            //备注界面
            case REMARK_INTENT_KEY:
        }
    }

    /**
     * 来自备注界面的数据
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveData(SecondToFirstActivityEvent event){
        if(event.secondClass!=RemarkDetailActivity.class||
                event.firstClass!=MeasureCustomActivity.class) return;
        switch (event.index) {
            //备注界面
            case REMARK_INTENT_KEY:
                Map<String,Object> map= (Map<String, Object>) event.object;
                if(map==null) return;
                String styleId=map.get(STYLE_ID_KEY).toString();//款式ID
                int position=-1;
                for(int i=0;i<mMeasureCustomLists.size();i++){
                    if(styleId.equalsIgnoreCase(mMeasureCustomLists.get(i).get(0).ISDSTYLETYPEMSTID)){
                        position=i;
                        break;
                    }
                }
                if(position==-1) return;
                List<RemarkDetailBean> remarkList= (List<RemarkDetailBean>) map.get(REMARK_RETURN_DATA);
                remarkAllList.set(position,remarkList);
                View view=mActivityMeasureCustomBinding.llCloth.getChildAt(position);
                final CheckBox cbRemark = (CheckBox) view.findViewById(R.id.cbRemark);
                try {
                    cbRemark.setChecked(remarkAllList.get(position)!=null&&!remarkAllList.get(position).isEmpty());
                }catch (Exception e){
                    cbRemark.setChecked(false);
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OthersUtil.unregisterEvent(this);
    }
}
