package lugang.app.huansi.net.lugang.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import huansi.net.qianjingapp.adapter.HsBaseAdapter;
import lugang.app.huansi.net.db.MeasureDataInSQLite;

/**
 * Created by 年 on 2017/11/15.
 */

public class MeasureCustomAdapter extends HsBaseAdapter<List<MeasureDataInSQLite>> {
    public MeasureCustomAdapter(List<List<MeasureDataInSQLite>> list, Context context) {
        super(list, context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){

        }

        return convertView;
    }
}
