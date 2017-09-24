package lugang.app.huansi.net.lugang.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
import lugang.app.huansi.net.lugang.bean.RepairRegisterBean;
import lugang.app.huansi.net.lugang.constant.Constant;
import lugang.app.huansi.net.lugang.databinding.RepairRegisterFragmentBinding;
import rx.functions.Func1;

import static huansi.net.qianjingapp.utils.SPHelper.USER_GUID;
import static huansi.net.qianjingapp.utils.WebServices.WebServiceType.CUS_SERVICE;

/**
 * Created by Tony on 2017/9/9.
 * 11:30
 */

public class RepairRegisterFragment extends BaseFragment {
    protected LoadProgressDialog mDialog;
    private RepairRegisterFragmentBinding mRegisterFragmentBinding;

    @Override
    public int getLayout() {
        return R.layout.repair_register_fragment;
    }
    @Override
    public void init() {
        mDialog = new LoadProgressDialog(getActivity());
        mRegisterFragmentBinding = (RepairRegisterFragmentBinding) viewDataBinding;
        Intent intent = getActivity().getIntent();
        String suserid = intent.getStringExtra(Constant.SUSERID);
        final String userGUID= SPHelper.getLocalData(getContext(),USER_GUID,String.class.getName(),"").toString();

        setRepairMeasure(userGUID);
    }
    /**
     * 联网获取量体人返修数据
     */
    private void setRepairMeasure(final String userGUID) {
        OthersUtil.showLoadDialog(mDialog);
        NewRxjavaWebUtils.getUIThread(NewRxjavaWebUtils.getObservable(this, "")
                        .map(new Func1<String, HsWebInfo>() {
                            @Override
                            public HsWebInfo call(String s) {
                                //待量体
                                return NewRxjavaWebUtils.getJsonData(getContext(), CUS_SERVICE,
                                        "spappMeasureOrderList"
                                        , "iIndex=2" + ",uUserGUID=" + userGUID,
                                        RepairRegisterBean.class.getName(),
                                        true, "");
                            }
                        })
                , getContext(), mDialog, new SimpleHsWeb() {
                    @Override
                    public void success(HsWebInfo hsWebInfo) {
                        List<WsEntity> listWsdata = hsWebInfo.wsData.LISTWSDATA;
                        for (int i = 0; i < listWsdata.size(); i++) {
                            final RepairRegisterBean repairRegisterBean = (RepairRegisterBean) listWsdata.get(i);
                            setMeasureData(repairRegisterBean);
                        }
                    }
                });
    }

    private void setMeasureData(final RepairRegisterBean repairRegisterBean) {
        View view = View.inflate(getActivity(), R.layout.repair_register_item, null);
        TextView customerName = (TextView) view.findViewById(R.id.customerName);
        TextView areaName = (TextView) view.findViewById(R.id.areaName);
        TextView cityName = (TextView) view.findViewById(R.id.cityName);
        TextView countyName = (TextView) view.findViewById(R.id.countyName);
        TextView departmentName = (TextView) view.findViewById(R.id.departmentName);
        TextView person = (TextView) view.findViewById(R.id.person);
        Button btnMeasure = (Button) view.findViewById(R.id.btnMeasure);
        customerName.setText(repairRegisterBean.SCUSTOMERNAME);
        areaName.setText(repairRegisterBean.SAREANAME);
        cityName.setText(repairRegisterBean.SCITYNAME);
        countyName.setText(repairRegisterBean.SCOUNTYNAME);
        departmentName.setText(repairRegisterBean.SDEPARTMENTNAME);
        person.setText(repairRegisterBean.SPERSON);
        btnMeasure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MeasureCustomActivity.class);
                intent.putExtra(Constant.SPERSON, repairRegisterBean.SPERSON);
                intent.putExtra(Constant.SDEPARTMENTNAME, repairRegisterBean.SDEPARTMENTNAME);
                intent.putExtra(Constant.IID,repairRegisterBean.IID);
                intent.putExtra(Constant.SVALUENAME,repairRegisterBean.SVALUENAME);
                intent.putExtra(Constant.STATUS,repairRegisterBean.STATUS);
                startActivity(intent);
            }
        });
        mRegisterFragmentBinding.llCustomer.addView(view);

    }


}
