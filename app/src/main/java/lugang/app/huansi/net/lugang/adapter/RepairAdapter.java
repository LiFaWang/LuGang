package lugang.app.huansi.net.lugang.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import huansi.net.qianjingapp.adapter.HsBaseAdapter;
import huansi.net.qianjingapp.utils.ViewHolder;
import lugang.app.huansi.net.db.MeasureOrderInSQLite;
import lugang.app.huansi.net.lugang.R;
import lugang.app.huansi.net.lugang.activity.MeasureCustomActivity;
import lugang.app.huansi.net.lugang.constant.Constant;

/**
 * Created by Tony on 2017/12/5.
 * 18:16
 */

public class RepairAdapter extends HsBaseAdapter<MeasureOrderInSQLite> {
    public RepairAdapter(List<MeasureOrderInSQLite> list, Context context) {
        super(list, context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView==null)
            convertView= View.inflate(mContext, R.layout.repair_register_item,null);

            final MeasureOrderInSQLite measureOrderInSQLite = mList.get(position);
            TextView customerName = ViewHolder.get(convertView,R.id.customerName);
            TextView cityName = ViewHolder.get(convertView,R.id.cityName);
            TextView countyName = ViewHolder.get(convertView,R.id.countyName);
            TextView departmentName =ViewHolder.get(convertView,R.id.departmentName);
            TextView person =ViewHolder.get(convertView,R.id.person);
            TextView sex =ViewHolder.get(convertView,R.id.sex);
            Button btnMeasure = ViewHolder.get(convertView,R.id.btnMeasure);
            customerName.setText(measureOrderInSQLite.getSCustomerName());
            cityName.setText(measureOrderInSQLite.getSCityName());
            countyName.setText(measureOrderInSQLite.getSCountyName());
            departmentName.setText(measureOrderInSQLite.getSDepartmentName());
            person.setText(measureOrderInSQLite.getSPerson());
            sex.setText(measureOrderInSQLite.getSex());
            btnMeasure.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, MeasureCustomActivity.class);
                    intent.putExtra(Constant.SPERSON, measureOrderInSQLite.getSPerson());
                    intent.putExtra(Constant.SEX, measureOrderInSQLite.getSex());
                    intent.putExtra(Constant.SDEPARTMENTNAME, measureOrderInSQLite.getSDepartmentName());
                    intent.putExtra(Constant.ISDORDERMETERMSTID,measureOrderInSQLite.getISdOrderMeterMstId());
                    intent.putExtra(Constant.AREANAME,measureOrderInSQLite.getSAreaName());
                    intent.putExtra(Constant.CITYNAME,measureOrderInSQLite.getSCityName());
                    intent.putExtra(Constant.COUNTYNAME,measureOrderInSQLite.getSCountyName());
                    intent.putExtra(Constant.CUSTOMERNAME,measureOrderInSQLite.getSCustomerName());
                    intent.putExtra(Constant.DEPARTMENTNAME,measureOrderInSQLite.getSDepartmentName());
//              intent.putExtra(Constant.SVALUENAME,measureOrderInSQLite.get.SVALUENAME);
                    intent.putExtra(Constant.IORDERTYPE,measureOrderInSQLite.getOrderType());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                }
            });
            return convertView;


    }
}
