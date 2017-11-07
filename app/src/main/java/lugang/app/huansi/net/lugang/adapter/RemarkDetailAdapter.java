package lugang.app.huansi.net.lugang.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import java.util.List;

import huansi.net.qianjingapp.adapter.HsBaseAdapter;
import huansi.net.qianjingapp.utils.ViewHolder;
import lugang.app.huansi.net.db.RemarkDetailDataInSQLite;
import lugang.app.huansi.net.lugang.R;

/**
 * Created by Tony on 2017/9/22.
 * 17:08
 */

public class RemarkDetailAdapter extends HsBaseAdapter<RemarkDetailDataInSQLite> {

    public RemarkDetailAdapter(List<RemarkDetailDataInSQLite> list, Context context) {
        super(list, context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null) convertView=mInflater.inflate(R.layout.remark_detail_item,parent,false);
        CheckBox cbRemarkDetail= ViewHolder.get(convertView,R.id.cbRemarkDtail);
        RemarkDetailDataInSQLite remarkDetailDataInSQLite=mList.get(position);
        cbRemarkDetail.setText(remarkDetailDataInSQLite.getSMeterMarkName());
        cbRemarkDetail.setTextSize(18);
        cbRemarkDetail.setChecked(remarkDetailDataInSQLite.isChoose);
        cbRemarkDetail.setEnabled(!remarkDetailDataInSQLite.isAdd);
        return convertView;
    }
}
