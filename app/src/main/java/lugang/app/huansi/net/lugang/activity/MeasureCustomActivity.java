package lugang.app.huansi.net.lugang.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
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
import lugang.app.huansi.net.greendao.db.RemarkDetail;
import lugang.app.huansi.net.lugang.R;
import lugang.app.huansi.net.lugang.adapter.ClothTypeAdapter;
import lugang.app.huansi.net.lugang.bean.MeasureCustomBean;
import lugang.app.huansi.net.lugang.databinding.ActivityMeasureCustomBinding;
import lugang.app.huansi.net.lugang.manager.DBManager;
import rx.functions.Func1;

import static huansi.net.qianjingapp.utils.WebServices.WebServiceType.CUS_SERVICE;

/**
 * 量体定制详情页
 */

public class MeasureCustomActivity extends NotWebBaseActivity {

    private ActivityMeasureCustomBinding mActivityMeasureCustomBinding;
//    private MeasureCustomViewPagerAdapter measureCustomViewPagerAdapter;
    private ClothTypeAdapter lvClothTypeAdapter;
    private List<List<MeasureCustomBean>> mMeasureCustomLists;
    DBManager dbManager = DBManager.getInstance(this);

    @Override
    protected int getLayoutId() {
        return R.layout.activity_measure_custom;
    }
    @Override
    public void init() {
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);//默认自动跳出软键盘
        mActivityMeasureCustomBinding = (ActivityMeasureCustomBinding) viewDataBinding;
        SharedPreferences sp = this.getSharedPreferences("user_info", Context.MODE_PRIVATE);
        final String ugu_id = sp.getString("ugu_id", "");
        Intent intent = getIntent();
         final String sperson = intent.getStringExtra("SPERSON");
         String sdepartmentname = intent.getStringExtra("SDEPARTMENTNAME");
        final String iid = intent.getStringExtra("IID");
        mActivityMeasureCustomBinding.customName.setText(sdepartmentname+sperson);
        getClothStyle();
        List<RemarkDetail> remarkDetails = dbManager.queryRemarkDetailList();
        final String isdstyletypemstid = remarkDetails.get(0).getIsdstyletypemstid();
        mActivityMeasureCustomBinding.btnSaveMeasure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMeasure(ugu_id,iid,isdstyletypemstid);//保存录入信息
            }
        });
    }
    //保存录入信息
    private void saveMeasure(final String uHrEmployeeGUID, final String isdOrderMeterDtlid, final String isdstyletypemstid) {
       OthersUtil.showLoadDialog(mDialog);
        NewRxjavaWebUtils.getUIThread(NewRxjavaWebUtils.getObservable(this, "")
                        .map(new Func1<String, HsWebInfo>() {
                            @Override
                            public HsWebInfo call(String s) {
                                StringBuilder sbStr=new StringBuilder();
                                for(int i=0;i<mMeasureCustomLists.size();i++){
                                    List<MeasureCustomBean> subList=mMeasureCustomLists.get(i);

                                    for(int j=0;j<subList.size();j++){
                                        MeasureCustomBean bean=subList.get(j);
                                        sbStr.append("EXEC spappMeasureSaveData ")
                                              .append("@iIndex=0,@uHrEmployeeGUID='").append(uHrEmployeeGUID+"'")
                                              .append(",@isdOrderMeterDtlid=").append(isdOrderMeterDtlid)
                                              .append(",@isMeterSize=").append(bean.SIZE)
                                              .append(",@isdStyleTypeItemDtlid=").append(bean.SDSTYLETYPEITEMDTLID)
                                              .append(";");
                                    }
                                }
                                sbStr.append("EXEC spappMeasureSaveData ")
                                     .append("@iIndex=0,@uHrEmployeeGUID='").append(uHrEmployeeGUID+"'")
                                     .append(",@isdOrderMeterDtlid=").append(isdOrderMeterDtlid)
                                     .append(",@isdMeterMarkDtlid=").append(isdstyletypemstid)
                                     .append("; ");
                                return NewRxjavaWebUtils.getJsonData(getApplicationContext(),CUS_SERVICE,
                                        sbStr.toString(),"", MeasureCustomBean.class.getName(),true,
                                        "保存出错");

                            }
                        })
                , this, mDialog, new SimpleHsWeb() {
                    @Override
                    public void success(HsWebInfo hsWebInfo) {
                        finish();
                        OthersUtil.ToastMsg(MeasureCustomActivity.this,"上传服务器成功");

                    }
                });

    }

    /**
     * 联网获得服装的款式
     */
    private void getClothStyle() {
        OthersUtil.showLoadDialog(mDialog);
        NewRxjavaWebUtils.getUIThread(NewRxjavaWebUtils.getObservable(this, "")
                        .map(new Func1<String, HsWebInfo>() {
                            @Override
                            public HsWebInfo call(String s) {
                                //登录
                                return NewRxjavaWebUtils.getJsonData(getApplicationContext(),CUS_SERVICE,
                                        "spappMeasureStyleTypeList",
                                        "",
                                        MeasureCustomBean.class.getName(),
                                        true,"没有找到");
                            }
                        })
                , this, mDialog, new SimpleHsWeb() {


                    @Override
                    public void success(HsWebInfo hsWebInfo) {
                        List<WsEntity> listwsdata = hsWebInfo.wsData.LISTWSDATA;
                        HashMap<String,List<MeasureCustomBean>> hashMap=new HashMap<>();
                        for (int i = 0; i < listwsdata.size(); i++) {
                            MeasureCustomBean measureCustomBean = (MeasureCustomBean) listwsdata.get(i);
                            String sdstyletypeitemdtlid = measureCustomBean.SDSTYLETYPEITEMDTLID;
                            List<MeasureCustomBean> measureCustomBeanList = hashMap.get(sdstyletypeitemdtlid);
                            if (measureCustomBeanList==null)measureCustomBeanList=new ArrayList<>();
                            measureCustomBeanList.add(measureCustomBean);
                            hashMap.put(sdstyletypeitemdtlid,measureCustomBeanList);
                        }
                        mMeasureCustomLists = new ArrayList<>();
                        Iterator<Map.Entry<String, List<MeasureCustomBean>>> it = hashMap.entrySet().iterator();
                        while (it.hasNext()) {
                            Map.Entry<String, List<MeasureCustomBean>> entry = it.next();
                            List<MeasureCustomBean> subList = entry.getValue();
                            mMeasureCustomLists.add( subList);
                        }
                        setMeasureCustomItemDate(mMeasureCustomLists);//每个款式要测量的数据
                        lvClothTypeAdapter.notifyDataSetChanged();



                    }
                });



    }

    /**
     * 每个款式要测量的数据
     * @param measureCustomBean
     */
    private void setMeasureCustomItemDate(final List<List<MeasureCustomBean>> measureCustomBean) {

        LayoutInflater layoutInflater = getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.activity_measure_detial, null);
        TextView tvClothStyle = (TextView) view.findViewById(R.id.tvClothStyle);
       ListView lvClothTypeList = (ListView) view.findViewById(R.id.lvClothTypeList);
        tvClothStyle.setText(measureCustomBean.get(0).get(0).SVALUEGROUP);
        lvClothTypeAdapter=new ClothTypeAdapter(measureCustomBean,this);
        lvClothTypeList.setAdapter(lvClothTypeAdapter);

        mActivityMeasureCustomBinding.llCloth.setGravity(Gravity.CENTER_HORIZONTAL);
        WindowManager wm = this.getWindowManager();
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();
        mActivityMeasureCustomBinding.llCloth.addView(view,
                width/5 ,height-110);
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.cbRemark);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Intent intent=new Intent(MeasureCustomActivity.this,RemarkDetailActivity.class);
                intent.putExtra("ISDSTYLETYPEMSTID", measureCustomBean.get(0).get(0).ISDSTYLETYPEMSTID);
                startActivity(intent);

            }
        });



    }

}
