package lugang.app.huansi.net.lugang.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import huansi.net.qianjingapp.adapter.HsBaseAdapter;
import huansi.net.qianjingapp.utils.ViewHolder;
import lugang.app.huansi.net.lugang.R;

/**
 * Created by Tony on 2017/11/11.
 */

public class LinkageSearchAdapter extends HsBaseAdapter<String> {


    public LinkageSearchAdapter(List<String> list, Context context) {
        super(list, context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) convertView = mInflater.inflate(R.layout.element_string_item, null);
        String lites = mList.get(position);
        TextView view = ViewHolder.get(convertView, R.id.tvElementString);
        view.setText(lites);
        return convertView;
    }
}
