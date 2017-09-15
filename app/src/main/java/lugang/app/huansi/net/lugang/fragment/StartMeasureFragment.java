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
import huansi.net.qianjingapp.view.LoadProgressDialog;
import lugang.app.huansi.net.lugang.R;
import lugang.app.huansi.net.lugang.activity.MeasureCustomActivity;
import lugang.app.huansi.net.lugang.bean.StartMeasureBean;
import lugang.app.huansi.net.lugang.databinding.StartMeasureFragmentBinding;
import rx.functions.Func1;

import static huansi.net.qianjingapp.utils.WebServices.WebServiceType.CUS_SERVICE;

/**
 * Created by Tony on 2017/9/12.
 * 测量分类页-未测量
 * 15:19
 */

public class StartMeasureFragment extends BaseFragment {
    protected LoadProgressDialog mDialog;

    private StartMeasureFragmentBinding mStartMeasureFragmentBinding;

    @Override
    public int getLayout() {
        return R.layout.start_measure_fragment;
    }

    @Override
    public void init() {
        mDialog = new LoadProgressDialog(getActivity());
        mStartMeasureFragmentBinding = (StartMeasureFragmentBinding) viewDataBinding;

        Intent intent = getActivity().getIntent();
        String suserid = intent.getStringExtra("SUSERID");
        setStartMeasure(suserid);
    }

    /**
     * 联网获取待量体人数据
     */
    private void setStartMeasure(final String userNo) {
        OthersUtil.showLoadDialog(mDialog);
        NewRxjavaWebUtils.getUIThread(NewRxjavaWebUtils.getObservable(this, "")
                        .map(new Func1<String, HsWebInfo>() {
                            @Override
                            public HsWebInfo call(String s) {
                                //待量体
                                return NewRxjavaWebUtils.getJsonData(getContext(), CUS_SERVICE,
                                        "spappMeasureOrderList"
                                        , "iIndex=0" + ",sUserNo=" + userNo,
                                        StartMeasureBean.class.getName(),
                                        true, "");
                            }
                        })
                , getContext(), mDialog, new SimpleHsWeb() {
                    @Override
                    public void success(HsWebInfo hsWebInfo) {
                        List<WsEntity> listWsdata = hsWebInfo.wsData.LISTWSDATA;
                        List<StartMeasureBean> startMeasureBeanList = new ArrayList<>();
                        for (int i = 0; i < listWsdata.size(); i++) {
                            final StartMeasureBean startMeasureBean = (StartMeasureBean) listWsdata.get(i);
                            startMeasureBeanList.add(startMeasureBean);

                        }
                        for (StartMeasureBean bean : startMeasureBeanList) {
                            setMeasureData(bean);
                        }


                    }
                });

    }

    private void setMeasureData(final StartMeasureBean startMeasureBean) {
        View view = View.inflate(getActivity(), R.layout.start_measure_item, null);
        TextView customerName = (TextView) view.findViewById(R.id.customerName);
        TextView areaName = (TextView) view.findViewById(R.id.areaName);
        TextView cityName = (TextView) view.findViewById(R.id.cityName);
        TextView countyName = (TextView) view.findViewById(R.id.countyName);
        TextView departmentName = (TextView) view.findViewById(R.id.departmentName);
        TextView person = (TextView) view.findViewById(R.id.person);
        Button btnMeasure = (Button) view.findViewById(R.id.btnMeasure);
        customerName.setText(startMeasureBean.SCUSTOMERNAME);
        areaName.setText(startMeasureBean.SAREANAME);
        cityName.setText(startMeasureBean.SCITYNAME);
        countyName.setText(startMeasureBean.SCOUNTYNAME);
        departmentName.setText(startMeasureBean.SDEPARTMENTNAME);
        person.setText(startMeasureBean.SPERSON);
       btnMeasure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MeasureCustomActivity.class);
                intent.putExtra("SPERSON", startMeasureBean.SPERSON);
                intent.putExtra("SDEPARTMENTNAME", startMeasureBean.SDEPARTMENTNAME);
                startActivity(intent);
            }
        });
        mStartMeasureFragmentBinding.llCustomer.addView(view);

    }

}
