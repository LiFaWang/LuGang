package lugang.app.huansi.net.lugang.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import huansi.net.qianjingapp.adapter.HsBaseAdapter;
import huansi.net.qianjingapp.utils.ViewHolder;
import lugang.app.huansi.net.db.RemarkCategoryDataInSQLite;
import lugang.app.huansi.net.lugang.R;

/**
 * Created by Tony on 2017/9/22.
 * 17:08
 */

public class RemarkCategoryAdapter extends HsBaseAdapter<RemarkCategoryDataInSQLite> {

    public RemarkCategoryAdapter(List<RemarkCategoryDataInSQLite> list, Context context) {
        super(list, context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null) convertView=mInflater.inflate(R.layout.remark_category_item,parent,false);
        TextView tvRemarkCategory= ViewHolder.get(convertView,R.id.tvRemarkCategory);
        RemarkCategoryDataInSQLite remarkCategoryDataInSQLite=mList.get(position);
        tvRemarkCategory.setText(remarkCategoryDataInSQLite.getSMeterMarkName());
        tvRemarkCategory.setTextSize(18);
        tvRemarkCategory.setTextColor(Color.WHITE);
        if(remarkCategoryDataInSQLite.isChoose) tvRemarkCategory.setBackground(mContext.getResources().getDrawable(R.drawable.remark_category_select));
        else tvRemarkCategory.setBackground(mContext.getResources().getDrawable(R.drawable.remark_category_normal));
        return convertView;
    }
}
