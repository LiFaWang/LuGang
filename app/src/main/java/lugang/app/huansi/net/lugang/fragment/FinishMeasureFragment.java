package lugang.app.huansi.net.lugang.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import huansi.net.qianjingapp.entity.HsWebInfo;
import huansi.net.qianjingapp.entity.WsEntity;
import huansi.net.qianjingapp.fragment.BaseFragment;
import huansi.net.qianjingapp.imp.SimpleHsWeb;
import huansi.net.qianjingapp.utils.NewRxjavaWebUtils;
import huansi.net.qianjingapp.utils.OthersUtil;
import huansi.net.qianjingapp.utils.SPHelper;
import huansi.net.qianjingapp.view.LoadProgressDialog;
import lugang.app.huansi.net.lugang.R;
import lugang.app.huansi.net.lugang.activity.MeasureCustomActivity;
import lugang.app.huansi.net.lugang.adapter.FininshAdapter;
import lugang.app.huansi.net.lugang.bean.FinishMeasureBean;
import lugang.app.huansi.net.lugang.constant.Constant;
import lugang.app.huansi.net.lugang.databinding.FinishMeasureFragmentBinding;
import rx.functions.Func1;

import static huansi.net.qianjingapp.utils.SPHelper.USER_GUID;
import static huansi.net.qianjingapp.utils.WebServices.WebServiceType.CUS_SERVICE;

/**
 * Created by Tony on 2017/9/12.
 * 测量分类页-已测量
 * 15:19
 */

public class FinishMeasureFragment extends BaseFragment{
    protected LoadProgressDialog mDialog;
    private FinishMeasureFragmentBinding mFinishMeasureFragmentBinding;
    private FininshAdapter mFininshAdapter;

    @Override
    public int getLayout() {
        return R.layout.finish_measure_fragment;
    }
    @Override
    public void init() {
        mFinishMeasureFragmentBinding= (FinishMeasureFragmentBinding) viewDataBinding;
//
        mDialog = new LoadProgressDialog(getActivity());

    }

    @Override
    public void onResume() {
        super.onResume();
        Intent intent = getActivity().getIntent();
        String suserid = intent.getStringExtra(Constant.SUSERID);
        final String userGUID= SPHelper.getLocalData(getContext(),USER_GUID,String.class.getName(),"").toString();

        setFinishMeasure(userGUID);
    }

    /**
     * 联网获取已量体人数据
     */
    private void setFinishMeasure(final String userGUID) {
        OthersUtil.showLoadDialog(mDialog);
        NewRxjavaWebUtils.getUIThread(NewRxjavaWebUtils.getObservable(this, "")
                        .map(new Func1<String, HsWebInfo>() {
                            @Override
                            public HsWebInfo call(String s) {
                                //已量体
                                return NewRxjavaWebUtils.getJsonData(getContext(), CUS_SERVICE,
                                        "spappMeasureOrderList"
                                        , "iIndex=1" + ",uUserGUID=" + userGUID,
                                        FinishMeasureBean.class.getName(),
                                        true, "");
                            }
                        })
                , getContext(), mDialog, new SimpleHsWeb() {
                    @Override
                    public void success(HsWebInfo hsWebInfo) {
                        List<WsEntity> listwsdata = hsWebInfo.wsData.LISTWSDATA;
                        List<FinishMeasureBean>finishMeasureBeanList=new ArrayList<>();
                        for (int i = 0; i < listwsdata.size(); i++) {
                            FinishMeasureBean finishMeasureBean = (FinishMeasureBean) listwsdata.get(i);
                            finishMeasureBeanList.add(finishMeasureBean);
//                          setMeasureData(finishMeasureBean);
                        }
                       if (mFininshAdapter==null)mFininshAdapter = new FininshAdapter(finishMeasureBeanList,getActivity());
                        mFinishMeasureFragmentBinding.lvCustomer.setAdapter(mFininshAdapter);

                    }
                });
    }
    private void setMeasureData(final FinishMeasureBean finishMeasureBean) {
        View view = View.inflate(getActivity(), R.layout.finish_measure_item, null);
        TextView customerName = (TextView) view.findViewById(R.id.customerName);
        TextView areaName = (TextView) view.findViewById(R.id.areaName);
        TextView cityName = (TextView) view.findViewById(R.id.cityName);
        TextView countyName = (TextView) view.findViewById(R.id.countyName);
        TextView departmentName = (TextView) view.findViewById(R.id.departmentName);
        TextView person = (TextView) view.findViewById(R.id.person);
        Button btnMeasure = (Button) view.findViewById(R.id.btnMeasure);
        customerName.setText(finishMeasureBean.SCUSTOMERNAME);
        areaName.setText(finishMeasureBean.SAREANAME);
        cityName.setText(finishMeasureBean.SCITYNAME);
        countyName.setText(finishMeasureBean.SCOUNTYNAME);
        departmentName.setText(finishMeasureBean.SDEPARTMENTNAME);
        person.setText(finishMeasureBean.SPERSON);
        btnMeasure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MeasureCustomActivity.class);
                intent.putExtra(Constant.SPERSON, finishMeasureBean.SPERSON);
                intent.putExtra(Constant.SDEPARTMENTNAME, finishMeasureBean.SDEPARTMENTNAME);
                intent.putExtra(Constant.IID,finishMeasureBean.IID);
                intent.putExtra(Constant.SVALUENAME,finishMeasureBean.SVALUENAME);
                intent.putExtra(Constant.STATUS,finishMeasureBean.STATUS);
                startActivity(intent);
            }
        });
//        mFinishMeasureFragmentBinding.llCustomer.addView(view);
    }
}
