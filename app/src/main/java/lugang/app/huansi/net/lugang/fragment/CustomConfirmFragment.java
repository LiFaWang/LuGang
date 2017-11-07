package lugang.app.huansi.net.lugang.fragment;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.List;

import huansi.net.qianjingapp.entity.HsWebInfo;
import huansi.net.qianjingapp.entity.WsEntity;
import huansi.net.qianjingapp.fragment.BaseFragment;
import huansi.net.qianjingapp.imp.SimpleHsWeb;
import huansi.net.qianjingapp.utils.NewRxjavaWebUtils;
import huansi.net.qianjingapp.view.LoadProgressDialog;
import lugang.app.huansi.net.lugang.R;
import lugang.app.huansi.net.lugang.activity.CustomConfirmActivity;
import lugang.app.huansi.net.lugang.adapter.ConfirmListAdapter;
import lugang.app.huansi.net.lugang.bean.ConfirmPictureBean;
import lugang.app.huansi.net.lugang.databinding.ConfirmFragmentBinding;
import lugang.app.huansi.net.util.LGSPUtils;
import rx.functions.Func1;

import static huansi.net.qianjingapp.utils.WebServices.WebServiceType.CUS_SERVICE;
import static lugang.app.huansi.net.util.LGSPUtils.USER_GUID;

/**
 * Created by Tony on 2017/9/9.
 * 11:31
 */

public class CustomConfirmFragment extends BaseFragment {

    private ConfirmFragmentBinding mConfirmFragmentBinding;
    protected LoadProgressDialog mDialog;
    private List<ConfirmPictureBean> mConfirmPictureBeanList;//确认表单列表数据集合
    private ConfirmListAdapter mConfirmListAdapter;//确认表单列表数据集合的adapter

    @Override
    public int getLayout() {
        return R.layout.confirm_fragment;
    }
    @Override
    public void init() {
        mConfirmFragmentBinding = (ConfirmFragmentBinding) viewDataBinding;
        mDialog = new LoadProgressDialog(getActivity());
        mConfirmPictureBeanList=new ArrayList<>();

        mConfirmListAdapter = new ConfirmListAdapter(mConfirmPictureBeanList,getContext());
        mConfirmFragmentBinding.lvConfirm.setAdapter(mConfirmListAdapter);
        mConfirmFragmentBinding.lvConfirm.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent =new Intent(getActivity(),CustomConfirmActivity.class);
                String iordermetermstid = mConfirmPictureBeanList.get(position).IORDERMETERMSTID;
                String gpicture = mConfirmPictureBeanList.get(position).SPICTURE;
                String scustomername = mConfirmPictureBeanList.get(position).SCUSTOMERNAME;
                intent.putExtra("iordermetermstid",iordermetermstid);
                intent.putExtra("gpicture",gpicture);
                intent.putExtra("scustomername",scustomername);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        requestConfirmPicture();
    }

    /**
     * 联网请求图片
     */
    private void requestConfirmPicture() {
        mConfirmPictureBeanList.clear();
        NewRxjavaWebUtils.getUIThread(NewRxjavaWebUtils.getObservable(this, "")
                        .map(new Func1<String, HsWebInfo>() {
                            @Override
                            public HsWebInfo call(String s) {
                                String userGUID= LGSPUtils.getLocalData(getContext(),USER_GUID,String.class.getName(),"").toString();
                                return NewRxjavaWebUtils.getJsonData(getContext(), CUS_SERVICE,
                                        "spappMeasureConfirmationList"
                                        ,  "uUserGUID=" + userGUID,
                                        ConfirmPictureBean.class.getName(),
                                        true, "没有取得客户确认函");
                            }
                        })
                , getContext(), mDialog, new SimpleHsWeb() {
                    @Override
                    public void success(HsWebInfo hsWebInfo) {
                        List<WsEntity> listWsdata = hsWebInfo.wsData.LISTWSDATA;
                        for (int i = 0; i < listWsdata.size(); i++) {
                            ConfirmPictureBean pictureBean = (ConfirmPictureBean) listWsdata.get(i);
                            mConfirmPictureBeanList.add(pictureBean);
                        }
                        mConfirmListAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void error(HsWebInfo hsWebInfo, Context context) {
                    }
                });

    }
}
