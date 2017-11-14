package lugang.app.huansi.net.lugang.fragment;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import huansi.net.qianjingapp.fragment.BaseFragment;
import lugang.app.huansi.net.factory.FragmentFactory;
import lugang.app.huansi.net.lugang.R;
import lugang.app.huansi.net.lugang.databinding.MeasureCustomFragmentBinding;

/**
 *
 * Created by Tony on 2017/9/6.
 * 15:45
 */

public class MeasureCustomFragment extends BaseFragment {


    private MeasureCustomFragmentBinding mMeasureCustomFragmentBinding;

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
        mMeasureCustomFragmentBinding.vpMeasureCustom.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
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
    }

}
