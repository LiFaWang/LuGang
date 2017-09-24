package lugang.app.huansi.net.lugang.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import java.util.List;

import huansi.net.qianjingapp.adapter.HsBaseAdapter;
import huansi.net.qianjingapp.utils.ViewHolder;
import lugang.app.huansi.net.lugang.R;
import lugang.app.huansi.net.lugang.bean.RemarkDetailBean;

/**
 * Created by Tony on 2017/9/22.
 * 17:08
 */

public class RemarkAddAdapter extends HsBaseAdapter<RemarkDetailBean> {

    public RemarkAddAdapter(List<RemarkDetailBean> list, Context context) {
        super(list, context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null) convertView=mInflater.inflate(R.layout.remark_add_item,parent,false);
        CheckBox cbRemarkDetailAdd= ViewHolder.get(convertView, R.id.cbRemarkDetailAdd);
        RemarkDetailBean bean=mList.get(position);
        cbRemarkDetailAdd.setText(bean.SMETERMARKNAME);
        cbRemarkDetailAdd.setChecked(bean.isChoose);
        return convertView;
    }
}
