package lugang.app.huansi.net.lugang.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import huansi.net.qianjingapp.base.NotWebBaseActivity;
import huansi.net.qianjingapp.entity.HsWebInfo;
import huansi.net.qianjingapp.entity.WsEntity;
import huansi.net.qianjingapp.imp.SimpleHsWeb;
import huansi.net.qianjingapp.utils.NewRxjavaWebUtils;
import huansi.net.qianjingapp.utils.OthersUtil;
import lugang.app.huansi.net.greendao.db.RemarkDetail;
import lugang.app.huansi.net.lugang.R;
import lugang.app.huansi.net.lugang.bean.RemarkCategoryBean;
import lugang.app.huansi.net.lugang.bean.RemarkDetailBean;
import lugang.app.huansi.net.lugang.databinding.ActivityRemarkDetailBinding;
import lugang.app.huansi.net.lugang.manager.DBManager;
import rx.functions.Func1;

import static huansi.net.qianjingapp.utils.WebServices.WebServiceType.CUS_SERVICE;

/**
 * 备注详情页面
 */
public class RemarkDetailActivity extends NotWebBaseActivity {

    private ActivityRemarkDetailBinding mActivityRemarkDetailBinding;
    private List<RemarkDetailBean> mRemarkDetailBeanList;//备注详情选中集合
    DBManager dbManager = DBManager.getInstance(this);

    @Override
    protected int getLayoutId() {
        return R.layout.activity_remark_detail;
    }

    @Override
    public void init() {
        mActivityRemarkDetailBinding = (ActivityRemarkDetailBinding) viewDataBinding;
        mRemarkDetailBeanList = new ArrayList<>();

        Intent intent = getIntent();
        final String isdstyletypemstid = intent.getStringExtra("ISDSTYLETYPEMSTID");

        setRemarkCategoryDate(isdstyletypemstid);
        //点击按钮添加
        mActivityRemarkDetailBinding.butAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivityRemarkDetailBinding.llSelectorRemark.removeAllViews();
                showSelectorRemark(mRemarkDetailBeanList);

            }
        });
        ///点击按钮删除
        mActivityRemarkDetailBinding.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteSelectorRemark(mRemarkDetailBeanList);
            }
        });
        ///点击保存的到本地db
        mActivityRemarkDetailBinding.btnSaveRemark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < mRemarkDetailBeanList.size(); i++) {
                    String smetermarkname = mRemarkDetailBeanList.get(i).SMETERMARKNAME;
                    RemarkDetail remarkDetail=new RemarkDetail(null,smetermarkname,isdstyletypemstid);
                    dbManager.insertRemarkDetail(remarkDetail);
                    OthersUtil.ToastMsg(RemarkDetailActivity.this,"保存");


                }

            }
        });
        mActivityRemarkDetailBinding.llSelectorRemark.removeAllViews();
        List<RemarkDetail> remarkDetails = dbManager.queryRemarkDetailList();
        for (RemarkDetail detail : remarkDetails) {
            String smetermarkname1 = detail.getSmetermarkname();
            CheckBox checkBox=new CheckBox(RemarkDetailActivity.this);
            checkBox.setText(smetermarkname1);
            mActivityRemarkDetailBinding.llSelectorRemark.addView(checkBox);
        }


    }

    /**
     *
     * @param remarkDetailBeanList
     */

    private void deleteSelectorRemark(List<RemarkDetailBean> remarkDetailBeanList) {
        for (int i = 0; i < remarkDetailBeanList.size(); i++) {
            CheckBox box = (CheckBox) mActivityRemarkDetailBinding.llSelectorRemark.getChildAt(i);
            if (box.isChecked()){
                remarkDetailBeanList.remove(i);
                mActivityRemarkDetailBinding.llSelectorRemark.removeView(box);
            }else {
                OthersUtil.ToastMsg(this,"请先选择要删除的备注 ");
            }



        }
        List<RemarkDetail> remarkDetails = dbManager.queryRemarkDetailList();
            for (int i = 0; i <remarkDetails.size() ; i++) {
                remarkDetails.remove(i);

                CheckBox box = (CheckBox) mActivityRemarkDetailBinding.llSelectorRemark.getChildAt(i);
                mActivityRemarkDetailBinding.llSelectorRemark.removeView(box);


            }




    }

    /**
     *
     *
     * @param remarkDetailBeanList
     */
    private void showSelectorRemark(List<RemarkDetailBean> remarkDetailBeanList) {

        for (int i = 0; i < mRemarkDetailBeanList.size(); i++) {
                            CheckBox box=new CheckBox(this);
            box.setText(remarkDetailBeanList.get(i).SMETERMARKNAME);
            mActivityRemarkDetailBinding.llSelectorRemark.addView(box);

        }

    }

    private void setRemarkDetailDate(final int id) {
        //备注详情
        NewRxjavaWebUtils.getUIThread(NewRxjavaWebUtils.getObservable(this, "")
                .map(new Func1<String, HsWebInfo>() {
                    @Override
                    public HsWebInfo call(String s) {
                        return NewRxjavaWebUtils.getJsonData(getApplicationContext(), CUS_SERVICE,
                                "spappMeasureRemarkList"
                                , "isdMeterMarkMstid="+id,
                                RemarkDetailBean.class.getName(),
                                true, "");
                    }
                }), this, mDialog, new SimpleHsWeb() {
            @Override
            public void success(HsWebInfo hsWebInfo) {
                List<WsEntity> listwsdata = hsWebInfo.wsData.LISTWSDATA;
                List<RemarkDetailBean> remarkDetailBeanList = new ArrayList<>();
                for (int i = 0; i < listwsdata.size(); i++) {
                    RemarkDetailBean remarkDetailBean = (RemarkDetailBean) listwsdata.get(i);
                    remarkDetailBeanList.add(remarkDetailBean);
                }
                for (RemarkDetailBean remarkDetailBean : remarkDetailBeanList) {
                    setRemarkDetailItem(remarkDetailBean);//添加备注详情的列表数据
                }


            }


        });
    }

    /**
     * 添加备注详情的列表数据
     * @param remarkDetailBean
     */
    private void setRemarkDetailItem(final RemarkDetailBean remarkDetailBean) {
        final CheckBox checkBox=new CheckBox(this);
        checkBox.setText(remarkDetailBean.SMETERMARKNAME);
        //点击备注详情的checkBox，
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String text = checkBox.getText().toString();
                OthersUtil.ToastMsg(getApplicationContext(),text);
                if(isChecked){
                    mRemarkDetailBeanList.add(remarkDetailBean);
                }
//                buttonView.setChecked(false);


            }
        });
        mActivityRemarkDetailBinding.llRemarkDetailItem.addView(checkBox);

    }

    private void setRemarkCategoryDate(final String isdstyletypemstid) {
//        备注大类
        NewRxjavaWebUtils.getUIThread(NewRxjavaWebUtils.getObservable(this, "")
                .map(new Func1<String, HsWebInfo>() {
                    @Override
                    public HsWebInfo call(String s) {
                        return NewRxjavaWebUtils.getJsonData(getApplicationContext(), CUS_SERVICE,
                                "spappMeasureRemarkList"
                                , "isdStyleTypeMstid="+isdstyletypemstid,
                                RemarkCategoryBean.class.getName(),
                                true, "");
                    }
                }), this, mDialog, new SimpleHsWeb() {
            @Override
            public void success(HsWebInfo hsWebInfo) {
                List<WsEntity> listwsdata = hsWebInfo.wsData.LISTWSDATA;
                List<RemarkCategoryBean> remarkCategoryBeanList = new ArrayList<>();
                for (int i = 0; i < listwsdata.size(); i++) {
                    RemarkCategoryBean remarkCategoryBean = (RemarkCategoryBean) listwsdata.get(i);
                    remarkCategoryBeanList.add(remarkCategoryBean);
                }

                for (RemarkCategoryBean remarkCategoryBean : remarkCategoryBeanList) {
                    setRemarkCategoryItem(remarkCategoryBean);//添加备注大类列表数据
                }

            }
        });
    }

    /**
     * 动态添加备注大类的item数据
     *
     * @param remarkCategoryBean
     */
    private void setRemarkCategoryItem(RemarkCategoryBean remarkCategoryBean) {

        final RadioButton button = new RadioButton(this);
        button.setPadding(10, 10, 10, 10);

        setRaidBtnAttribute(button, remarkCategoryBean.SMETERMARKNAME,remarkCategoryBean.IID);

        mActivityRemarkDetailBinding.gadiogroup.addView(button, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) button
                .getLayoutParams();
        layoutParams.setMargins(10, 10, 10, 10);//4个参数按顺序分别是左上右下
        button.setLayoutParams(layoutParams);
    }



    private void setRaidBtnAttribute(final RadioButton radioButton, String btnContent, String id) {
        if (null == radioButton) {
            return;
        }
        radioButton.setBackgroundResource(R.drawable.radio_group_selector);
//        radioButton.setTextColor(this.getResources().getColorStateList(R.drawable.color_radiobutton));
        radioButton.setButtonDrawable(new ColorDrawable(Color.TRANSPARENT));
//        radioButton.setTextSize( ( textSize > 16 )?textSize:24 );

        radioButton.setText(btnContent);
        radioButton.setId(Integer.parseInt(id));
        radioButton.setTextSize(18);
        radioButton.setPadding(5, 5, 5, 5);

        radioButton.setGravity(Gravity.CENTER);
        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivityRemarkDetailBinding.llRemarkDetailItem.removeAllViews();

                setRemarkDetailDate(radioButton.getId());

                Toast.makeText(RemarkDetailActivity.this, radioButton.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });
        //DensityUtilHelps.Dp2Px(this,40)
        LinearLayout.LayoutParams rlp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 25);

        radioButton.setLayoutParams(rlp);
    }


}
