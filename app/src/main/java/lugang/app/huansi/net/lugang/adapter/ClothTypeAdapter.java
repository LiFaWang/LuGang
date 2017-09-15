package lugang.app.huansi.net.lugang.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import huansi.net.qianjingapp.adapter.HsBaseAdapter;
import lugang.app.huansi.net.lugang.R;

/**
 * Created by Tony on 2017/9/7.
 * 14:19
 */

public class ClothTypeAdapter extends HsBaseAdapter<String> {
    public ClothTypeAdapter(List<String> list, Context context) {
        super(list, context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView==null)convertView=mInflater.inflate(R.layout.ll_parameter,null);
        TextView tvParameter = (TextView) convertView.findViewById(R.id.tvParameter);
        String s = mList.get(position);
        tvParameter.setText(s);
        return convertView;
    }
}
