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

public class RemarkDetailAdapter extends HsBaseAdapter<RemarkDetailBean> {

    public RemarkDetailAdapter(List<RemarkDetailBean> list, Context context) {
        super(list, context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null) convertView=mInflater.inflate(R.layout.remark_detail_item,parent,false);
        CheckBox cbRemarkDtail= ViewHolder.get(convertView,R.id.cbRemarkDtail);
        RemarkDetailBean bean=mList.get(position);
        cbRemarkDtail.setText(bean.SMETERMARKNAME);
        cbRemarkDtail.setChecked(bean.isChoose);
        cbRemarkDtail.setEnabled(!bean.isAdd);
        return convertView;
    }
}
