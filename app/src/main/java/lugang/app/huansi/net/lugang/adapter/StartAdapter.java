package lugang.app.huansi.net.lugang.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import huansi.net.qianjingapp.adapter.HsBaseAdapter;
import lugang.app.huansi.net.lugang.R;
import lugang.app.huansi.net.lugang.activity.MeasureCustomActivity;
import lugang.app.huansi.net.lugang.bean.StartMeasureBean;
import lugang.app.huansi.net.lugang.constant.Constant;

/**
 * Created by Tony on 2017/9/30.
 * 13:48
 */

public class StartAdapter extends HsBaseAdapter<StartMeasureBean> {

    public StartAdapter(List<StartMeasureBean> list, Context context) {
        super(list, context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = View.inflate(mContext, R.layout.start_measure_item, null);
        final StartMeasureBean startMeasureBean = mList.get(position);
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
                Intent intent = new Intent(mContext, MeasureCustomActivity.class);
                intent.putExtra(Constant.SPERSON, startMeasureBean.SPERSON);
                intent.putExtra(Constant.SDEPARTMENTNAME, startMeasureBean.SDEPARTMENTNAME);
                intent.putExtra(Constant.ISDORDERMETERMSTID,startMeasureBean.ISDORDERMETERMSTID);
                intent.putExtra(Constant.SVALUENAME,startMeasureBean.SVALUENAME);
                intent.putExtra(Constant.IORDERTYPE,startMeasureBean.IORDERTYPE);
               mContext.startActivity(intent);
            }
        });
        return view;
    }
}
