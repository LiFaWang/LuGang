package lugang.app.huansi.net.lugang.activity;

import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import huansi.net.qianjingapp.base.NotWebBaseActivity;
import huansi.net.qianjingapp.entity.HsWebInfo;
import huansi.net.qianjingapp.entity.WsEntity;
import huansi.net.qianjingapp.imp.SimpleHsWeb;
import huansi.net.qianjingapp.utils.NewRxjavaWebUtils;
import huansi.net.qianjingapp.utils.OthersUtil;
import lugang.app.huansi.net.lugang.R;
import lugang.app.huansi.net.lugang.adapter.ClothTypeAdapter;
import lugang.app.huansi.net.lugang.bean.MeasureCustomBean;
import lugang.app.huansi.net.lugang.databinding.ActivityMeasureCustomBinding;
import rx.functions.Func1;

import static huansi.net.qianjingapp.utils.WebServices.WebServiceType.CUS_SERVICE;

/**
 * 量体定制详情页
 */

public class MeasureCustomActivity extends NotWebBaseActivity {

    private ActivityMeasureCustomBinding mActivityMeasureCustomBinding;
//    private MeasureCustomViewPagerAdapter measureCustomViewPagerAdapter;
    private List<View> mViewList;
    private ClothTypeAdapter lvClothTypeAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_measure_custom;
    }
    @Override
    public void init() {
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);//默认自动跳出软键盘
        mActivityMeasureCustomBinding = (ActivityMeasureCustomBinding) viewDataBinding;
        Intent intent = getIntent();
        String sperson = intent.getStringExtra("SPERSON");
        String sdepartmentname = intent.getStringExtra("SDEPARTMENTNAME");
        mActivityMeasureCustomBinding.customName.setText(sdepartmentname+sperson);
        getClothStyle();
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
                        List<MeasureCustomBean> measureCustomBeanList=new ArrayList<>();
                        for (int i = 0; i < listwsdata.size(); i++) {
                            MeasureCustomBean measureCustomBean = (MeasureCustomBean) listwsdata.get(i);

                            measureCustomBeanList.add(measureCustomBean);
                        }
                        for (MeasureCustomBean measureCustomBean : measureCustomBeanList) {

                            setMeasureCustomItemDate(measureCustomBean);//每个款式要测量的数据
                        }




                    }
                });



    }

    /**
     * 每个款式要测量的数据
     * @param measureCustomBean
     */
    private void setMeasureCustomItemDate(MeasureCustomBean measureCustomBean) {
        String isdStyleTypeMstId = measureCustomBean.ISDSTYLETYPEMSTID;//款式的id
        String svaluegroup = measureCustomBean.SVALUEGROUP;//衣服名称
        String smetername = measureCustomBean.SMETERNAME;//衣服对应的测量细节名称
        LayoutInflater layoutInflater = getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.activity_measure_detial, null);
        TextView tvClothStyle = (TextView) view.findViewById(R.id.tvClothStyle);
       ListView lvClothTypeList = (ListView) view.findViewById(R.id.lvClothTypeList);
        tvClothStyle.setText(svaluegroup);
        List<String> stringList=new ArrayList<>();
        stringList.add(smetername);
        lvClothTypeAdapter=new ClothTypeAdapter(stringList,this);
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
                startActivity(intent);
            }
        });



    }
//   private class MeasureCustomViewPagerAdapter extends PagerAdapter {
//
//        @Override
//        public int getCount() {
//            return mViewList.size();
//        }
//
//        @Override
//        public boolean isViewFromObject(View view, Object object) {
//            return view==object;
//        }
//
//       @Override
//       public void destroyItem(ViewGroup container, int position, Object object) {
//           container.removeView(mViewList.get(position));
//
//       }
//
//       @Override
//        public Object instantiateItem(ViewGroup container, int position) {
//           container.addView(mViewList.get(position));
//            return mViewList.get(position);
//        }
//    }

}
