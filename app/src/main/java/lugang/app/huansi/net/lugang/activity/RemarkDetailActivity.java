package lugang.app.huansi.net.lugang.activity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import huansi.net.qianjingapp.base.NotWebBaseActivity;
import huansi.net.qianjingapp.entity.HsWebInfo;
import huansi.net.qianjingapp.entity.WsEntity;
import huansi.net.qianjingapp.imp.SimpleHsWeb;
import huansi.net.qianjingapp.utils.NewRxjavaWebUtils;
import huansi.net.qianjingapp.utils.OthersUtil;
import lugang.app.huansi.net.lugang.R;
import lugang.app.huansi.net.lugang.bean.RemarkCategoryBean;
import lugang.app.huansi.net.lugang.bean.RemarkDetailBean;
import lugang.app.huansi.net.lugang.databinding.ActivityRemarkDetailBinding;
import rx.functions.Func1;

import static huansi.net.qianjingapp.utils.WebServices.WebServiceType.CUS_SERVICE;

/**
 * 备注详情页面
 */
public class RemarkDetailActivity extends NotWebBaseActivity {

    private ActivityRemarkDetailBinding mActivityRemarkDetailBinding;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_remark_detail;
    }

    @Override
    public void init() {
        mActivityRemarkDetailBinding = (ActivityRemarkDetailBinding) viewDataBinding;
        setRemarkCategoryDate();
//        addview( mActivityRemarkDetailBinding.gadiogroup);

    }

    private void setRemarkDetailDate() {
        //备注详情
        NewRxjavaWebUtils.getUIThread(NewRxjavaWebUtils.getObservable(this, "")
                .map(new Func1<String, HsWebInfo>() {
                    @Override
                    public HsWebInfo call(String s) {
                        return NewRxjavaWebUtils.getJsonData(getApplicationContext(), CUS_SERVICE,
                                "spappMeasureRemarkList"
                                , "isdMeterMarkMstid=-1",
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
    private void setRemarkDetailItem(RemarkDetailBean remarkDetailBean) {
        CheckBox checkBox=new CheckBox(this);
        checkBox.setText(remarkDetailBean.SMESSAGE);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                OthersUtil.ToastMsg(getApplicationContext(),buttonView.toString());
            }
        });

    }

    private void setRemarkCategoryDate() {
//        备注大类
        NewRxjavaWebUtils.getUIThread(NewRxjavaWebUtils.getObservable(this, "")
                .map(new Func1<String, HsWebInfo>() {
                    @Override
                    public HsWebInfo call(String s) {
                        return NewRxjavaWebUtils.getJsonData(getApplicationContext(), CUS_SERVICE,
                                "spappMeasureRemarkList"
                                , "isdMeterMarkMstid=-1",
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

        setRaidBtnAttribute(button, remarkCategoryBean.SMETERMARKNAME);

        mActivityRemarkDetailBinding.gadiogroup.addView(button, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) button
                .getLayoutParams();
        layoutParams.setMargins(10, 10, 10, 10);//4个参数按顺序分别是左上右下
        button.setLayoutParams(layoutParams);
    }

    public List<String> getListSize() {
        List<String> list = new ArrayList<String>();
        list.add("前衣长");
        list.add("后衣长");
        list.add("斜肩");
        list.add("胸省");
        list.add("驼背");
        list.add("袖肥");
        return list;
    }

    //动态添加视图
    public void addview(RadioGroup radiogroup) {

        int index = 0;
        for (final String ss : getListSize()) {

            final RadioButton button = new RadioButton(this);
            button.setPadding(10, 10, 10, 10);

            setRaidBtnAttribute(button, ss);

            radiogroup.addView(button, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) button
                    .getLayoutParams();
            layoutParams.setMargins(10, 10, 10, 10);//4个参数按顺序分别是左上右下


            button.setLayoutParams(layoutParams);
            index++;
        }


    }

    private void setRaidBtnAttribute(final RadioButton codeBtn, String btnContent) {
        if (null == codeBtn) {
            return;
        }
        codeBtn.setBackgroundResource(R.drawable.radio_group_selector);
//        codeBtn.setTextColor(this.getResources().getColorStateList(R.drawable.color_radiobutton));
        codeBtn.setButtonDrawable(new ColorDrawable(Color.TRANSPARENT));
//        codeBtn.setTextSize( ( textSize > 16 )?textSize:24 );

        codeBtn.setText(btnContent);
        codeBtn.setTextSize(18);
        codeBtn.setPadding(5, 5, 5, 5);

        codeBtn.setGravity(Gravity.CENTER);
        codeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRemarkDetailDate();

                Toast.makeText(RemarkDetailActivity.this, codeBtn.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });
        //DensityUtilHelps.Dp2Px(this,40)
        LinearLayout.LayoutParams rlp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 25);

        codeBtn.setLayoutParams(rlp);
    }


}
