package lugang.app.huansi.net.lugang.fragment;


import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import huansi.net.qianjingapp.fragment.BaseFragment;
import lugang.app.huansi.net.factory.FragmentFactory;
import lugang.app.huansi.net.lugang.R;
import lugang.app.huansi.net.lugang.activity.NewMeasureCustomActivity;
import lugang.app.huansi.net.lugang.bean.StartMeasureBean;
import lugang.app.huansi.net.lugang.databinding.MeasureCustomFragmentBinding;

/**
 * 量体定制的fm
 * Created by Tony on 2017/9/6.
 * 15:45
 */

public class MeasureCustomFragment extends BaseFragment {


    private MeasureCustomFragmentBinding mMeasureCustomFragmentBinding;
    private List<StartMeasureBean> mBeanList;

    @Override
    public int getLayout() {
        return R.layout.measure_custom_fragment;
    }
    @Override
    public void init() {
        mMeasureCustomFragmentBinding = (MeasureCustomFragmentBinding) viewDataBinding;
        //切换测量和未测量数据
        mMeasureCustomFragmentBinding.vpMeasureCustom.setAdapter(new FragmentPagerAdapter(getFragmentManager()) {

            @Override
            public Fragment getItem(int position) {
                return FragmentFactory.createFragment(position);
            }

            @Override
            public int getCount() {
                return 2;
            }

        });
        mMeasureCustomFragmentBinding.startMeasure.setChecked(true);
        mMeasureCustomFragmentBinding.vpMeasureCustom.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {

                mMeasureCustomFragmentBinding.startMeasure.setChecked(position == 0);
                mMeasureCustomFragmentBinding.finishMeasure.setChecked(position == 1);
            }
        });
        mMeasureCustomFragmentBinding.startMeasure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMeasureCustomFragmentBinding.vpMeasureCustom.setCurrentItem(0);
            }
        });
        mMeasureCustomFragmentBinding.finishMeasure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            mMeasureCustomFragmentBinding.vpMeasureCustom.setCurrentItem(1);
            }
        });
        mMeasureCustomFragmentBinding.btnNewMeasure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), NewMeasureCustomActivity.class);
                startActivity(intent);
            }
        });
        mMeasureCustomFragmentBinding.btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String orderNO = mMeasureCustomFragmentBinding.orderSearch.getText().toString();
                if (TextUtils.isEmpty(orderNO))return;
                searchMeasureOrder(orderNO);
            }
        });
    }

    /**
     * 根据量体清单单号查询
     * @param orderNO
     */
    private void searchMeasureOrder(String orderNO) {
        StartMeasureFragment item = ((StartMeasureFragment) ((FragmentPagerAdapter) mMeasureCustomFragmentBinding.vpMeasureCustom.getAdapter()).getItem(0));
        List<StartMeasureBean> mStartMeasureBeanList =item.getStartMeasureBeanList();
        mBeanList = new ArrayList<>();
        for (int i = 0; i < mStartMeasureBeanList.size(); i++) {
            StartMeasureBean sbillno = mStartMeasureBeanList.get(i);
            if (sbillno.SBILLNO.equals(orderNO)) {
                mBeanList.add(sbillno);
            }

        }

        item.setStartMeasureBeanList(mBeanList);


    }
}
