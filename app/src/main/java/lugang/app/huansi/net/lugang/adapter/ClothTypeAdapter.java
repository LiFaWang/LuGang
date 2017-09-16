package lugang.app.huansi.net.lugang.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import huansi.net.qianjingapp.adapter.HsBaseAdapter;
import lugang.app.huansi.net.lugang.R;
import lugang.app.huansi.net.lugang.bean.MeasureCustomBean;

/**
 * Created by Tony on 2017/9/7.
 * 14:19
 */

public class ClothTypeAdapter extends HsBaseAdapter<List<MeasureCustomBean>> {
    public ClothTypeAdapter(List<List<MeasureCustomBean>> list, Context context) {
        super(list, context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView==null)convertView=mInflater.inflate(R.layout.ll_parameter,null);

        List<MeasureCustomBean> measureCustomBeanList = mList.get(position);
        for (MeasureCustomBean measureCustomBean : measureCustomBeanList) {
            TextView tvParameter = (TextView) convertView.findViewById(R.id.tvParameter);
            String smetername = measureCustomBean.SMETERNAME;
            tvParameter.setText(smetername);
            EditText editText = (EditText) convertView.findViewById(R.id.etParameter);

        }
        return convertView;
    }
}
