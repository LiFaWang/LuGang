package lugang.app.huansi.net.lugang.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import huansi.net.qianjingapp.adapter.HsBaseAdapter;
import lugang.app.huansi.net.lugang.R;
import lugang.app.huansi.net.lugang.bean.NewMeasureBean;

/**
 * Created by Tony on 2017/10/31.
 */

public class JobAdapter extends HsBaseAdapter<NewMeasureBean> {
    public JobAdapter(List<NewMeasureBean> list, Context context) {
        super(list, context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) convertView = mInflater.inflate(R.layout.element_string_item, null);
        NewMeasureBean newMeasureBean = mList.get(position);
        TextView tvElementString = (TextView) convertView.findViewById(R.id.tvElementString);
        tvElementString.setText(newMeasureBean.SJOBNAME);
        return convertView;
    }
}
