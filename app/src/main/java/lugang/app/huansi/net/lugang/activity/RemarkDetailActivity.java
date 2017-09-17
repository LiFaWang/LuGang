package lugang.app.huansi.net.lugang.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.IdRes;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.List;

import huansi.net.qianjingapp.base.NotWebBaseActivity;
import huansi.net.qianjingapp.entity.HsWebInfo;
import huansi.net.qianjingapp.entity.WsEntity;
import huansi.net.qianjingapp.imp.SimpleHsWeb;
import huansi.net.qianjingapp.utils.NewRxjavaWebUtils;
import huansi.net.qianjingapp.utils.OthersUtil;
import lugang.app.huansi.net.greendao.db.RemarkDetailBeanDB;
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
    private List<RemarkDetailBeanDB> mRemarkDetailListFromServer;
    private List<RemarkDetailBeanDB> mRemarkDetailSelectedListFromServer;//备注详情选中集合
    private List<RemarkDetailBeanDB> mRemarkDetailListFromDB;//本地备注详情数据库的集合
    private List<RemarkDetailBeanDB> mRemarkDetailSelectedListFromDB;//本地备注详情数据库的集合
    DBManager dbManager = DBManager.getInstance(this);
    private List<CheckBox> mRemarkDetailChecks;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_remark_detail;
    }

    @Override
    public void init() {
        mActivityRemarkDetailBinding = (ActivityRemarkDetailBinding) viewDataBinding;
        mRemarkDetailListFromServer = new ArrayList<>();
        mRemarkDetailListFromDB = new ArrayList<>();
        mRemarkDetailSelectedListFromDB = new ArrayList<>();
        mRemarkDetailSelectedListFromServer = new ArrayList<>();
        mRemarkDetailChecks = new ArrayList<>();

        Intent intent = getIntent();
        final String isdstyletypemstid = intent.getStringExtra("ISDSTYLETYPEMSTID");

        setRemarkCategoryDate(isdstyletypemstid);

        //点击按钮添加
        mActivityRemarkDetailBinding.butAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRemarkDetailSelectedListFromServer.isEmpty()) return;
                insertSelectedItemToDB();
                mRemarkDetailListFromDB=dbManager.queryRemarkDetailList();
                refreshSelectedContent();
                for (CheckBox remarkDetailCheck : mRemarkDetailChecks) {
                    if (remarkDetailCheck.isChecked()) {
                        remarkDetailCheck.setChecked(false);
                        remarkDetailCheck.setEnabled(false);
                    }
                }
                mRemarkDetailSelectedListFromServer.clear();
                mRemarkDetailSelectedListFromDB.clear();
            }
        });
        ///点击按钮删除
        mActivityRemarkDetailBinding.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRemarkDetailSelectedListFromDB.isEmpty()) return;
                deleteSelectedRemarkFromDB();
                mRemarkDetailListFromDB=dbManager.queryRemarkDetailList();
                refreshSelectedContent();
//                for (CheckBox remarkDetailCheck : mRemarkDetailChecks) {
//                    if (remarkDetailCheck.isChecked()) {
//                        remarkDetailCheck.setChecked(false);
//                        remarkDetailCheck.setEnabled(false);
//                    }
//                }
//                deleteSelectorRemark(mRemarkDetailListFromServer);
            }
        });
        ///点击保存的到本地db
//        mActivityRemarkDetailBinding.btnSaveRemark.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                for (int i = 0; i < mRemarkDetailListFromServer.size(); i++) {
//                    insertSelectedItemToDB(i);
//                }
//            }
//        });
        //取本地已经存在数据
        mRemarkDetailListFromDB = dbManager.queryRemarkDetailList();
        refreshSelectedContent();


    }

    private void deleteSelectedRemarkFromDB() {
        for (RemarkDetailBeanDB remarkDetailBeanDB : mRemarkDetailSelectedListFromDB) {
            dbManager.deleteRemarkDetail(remarkDetailBeanDB);
        }
    }

    private void refreshSelectedContent() {
        mActivityRemarkDetailBinding.llSelectedRemark.removeAllViews();
        if (mRemarkDetailListFromDB.isEmpty()) return;
        for (final RemarkDetailBeanDB remarkDetailBeanDB : mRemarkDetailListFromDB) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(remarkDetailBeanDB.getSmetermarkname());
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mRemarkDetailSelectedListFromDB.add(remarkDetailBeanDB);
                }
            });
            mActivityRemarkDetailBinding.llSelectedRemark.addView(checkBox);
        }
    }

    private void addSelectedRemarks(List<RemarkDetailBeanDB> remarkDetailSelectedListFromServer) {
        for (RemarkDetailBeanDB remarkDetailBeanDB : remarkDetailSelectedListFromServer) {

        }
    }

    private void insertSelectedItemToDB() {
        for (RemarkDetailBeanDB remarkDetailBeanDB : mRemarkDetailSelectedListFromServer) {
            dbManager.insertRemarkDetail(remarkDetailBeanDB);
        }
    }

//    private void deleteSelectedItemFromDB(int i) {
//        String smetermarkname = mRemarkDetailListFromServer.get(i).SMETERMARKNAME;
//        String smeteriid = mRemarkDetailListFromServer.get(i).IID;
//        RemarkDetailFromDB remarkDetailFromDB =new RemarkDetailFromDB(null,smetermarkname,smeteriid);
//        dbManager.deleteRemarkDetail(remarkDetailFromDB);
//        OthersUtil.ToastMsg(RemarkDetailActivity.this,"保存");
//    }

    /**
     *
     * @param remarkDetailFromServerList
     */

    private void deleteSelectorRemark(List<RemarkDetailBean> remarkDetailFromServerList) {
        for (int i = 0; i < remarkDetailFromServerList.size(); i++) {
            CheckBox box = (CheckBox) mActivityRemarkDetailBinding.llSelectedRemark.getChildAt(i);
            if (box.isChecked()){
                remarkDetailFromServerList.remove(i);
                mActivityRemarkDetailBinding.llSelectedRemark.removeView(box);
            }else {
                OthersUtil.ToastMsg(this,"请先选择要删除的备注 ");
            }



        }
    }

//    /**
//     *
//     *
//     * @param remarkDetailFromServerList
//     */
//    private void showSelectorRemark(List<RemarkDetailBean> remarkDetailFromServerList) {
//
//        for (int i = 0; i < mRemarkDetailListFromServer.size(); i++) {
//                            CheckBox box=new CheckBox(this);
//            box.setText(remarkDetailFromServerList.get(i).SMETERMARKNAME);
//            mActivityRemarkDetailBinding.llSelectorRemark.addView(box);
//
//        }
//
//    }

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
                mRemarkDetailListFromServer.clear();
                for (int i = 0; i < listwsdata.size(); i++) {
                    RemarkDetailBean remarkDetailFromServer = (RemarkDetailBean) listwsdata.get(i);
                    RemarkDetailBeanDB remarkDetailBeanDB = new RemarkDetailBeanDB(null, remarkDetailFromServer.SMETERMARKNAME, remarkDetailFromServer.IID);
                    mRemarkDetailListFromServer.add(remarkDetailBeanDB);
                }
                initRemarkDetailItem();//UI添加备注详情的列表数据
            }


        });
    }

//    /**
//     * 添加备注详情的列表数据
//     * @param remarkDetailFromServer
//     */
//    private void setRemarkDetailItem(final RemarkDetailBean remarkDetailFromServer) {
//        final CheckBox checkBox=new CheckBox(this);
//        checkBox.setText(remarkDetailFromServer.SMETERMARKNAME);
//        //点击备注详情的checkBox，
//        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                String text = checkBox.getText().toString();
//                OthersUtil.ToastMsg(getApplicationContext(),text);
//                if(isChecked){
//                    mRemarkDetailSelectedListFromServer.add(remarkDetailFromServer);
//                    buttonView.setEnabled(false);
//                }
//
//
//            }
//        });
//        mActivityRemarkDetailBinding.llRemarkDetailItem.addView(checkBox);
//
//    }
    /**
     * 添加备注详情的列表数据
     */
    private void initRemarkDetailItem() {
        if (mRemarkDetailChecks.size() == mRemarkDetailListFromServer.size()) return;
        for (final RemarkDetailBeanDB remarkDetailFromServer : mRemarkDetailListFromServer) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(remarkDetailFromServer.getSmetermarkname());
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mRemarkDetailSelectedListFromServer.add(remarkDetailFromServer);
                }
            });
            mRemarkDetailChecks.add(checkBox);
            mActivityRemarkDetailBinding.llRemarkDetailItem.addView(checkBox);
        }
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
//                List<RemarkCategoryBean> remarkCategoryBeanList = new ArrayList<>();
                for (int i = 0; i < listwsdata.size(); i++) {
                    RemarkCategoryBean remarkCategoryBean = (RemarkCategoryBean) listwsdata.get(i);
//                    remarkCategoryBeanList.add(remarkCategoryBean);
                    setRemarkCategoryItem(remarkCategoryBean);//添加备注大类列表数据
                }

//                for (RemarkCategoryBean remarkCategoryBean : remarkCategoryBeanList) {
//                    setRemarkCategoryItem(remarkCategoryBean);//添加备注大类列表数据
//                }

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
        mActivityRemarkDetailBinding.gadiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                setRemarkDetailDate(checkedId);
            }
        });
        mActivityRemarkDetailBinding.gadiogroup.check(Integer.parseInt(remarkCategoryBean.IID));
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
//        radioButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                setRemarkDetailDate(radioButton.getId());
//            }
//        });
        //DensityUtilHelps.Dp2Px(this,40)
        LinearLayout.LayoutParams rlp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 25);

        radioButton.setLayoutParams(rlp);
    }


}
