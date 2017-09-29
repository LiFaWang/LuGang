package lugang.app.huansi.net.factory;

import android.support.v4.app.Fragment;
import android.util.SparseArray;

import huansi.net.qianjingapp.fragment.BaseFragment;
import lugang.app.huansi.net.lugang.fragment.FinishMeasureFragment;
import lugang.app.huansi.net.lugang.fragment.StartMeasureFragment;

/**
 * Created by Tony on 2017/9/29.
 * 21:21
 */

public class FragmentFactory {

    private static SparseArray<BaseFragment> sFragmentSparseArray=new SparseArray<>();
    public static Fragment createFragment(int pos){
        BaseFragment baseFragment = sFragmentSparseArray.get(pos);
        if (baseFragment==null){
            switch (pos) {
                case 0:
                    baseFragment=new StartMeasureFragment();
                    break;
                case 1:
                    baseFragment=new FinishMeasureFragment();
                    break;

                default:
                    break;
            }
            sFragmentSparseArray.put(pos, baseFragment);
        }
        return baseFragment;
    }
}
