package lugang.app.huansi.net.lugang.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import huansi.net.qianjingapp.adapter.HsBaseAdapter;
import lugang.app.huansi.net.lugang.R;
import lugang.app.huansi.net.lugang.bean.ConfirmPictureBean;

/**
 * Created by Tony on 2017/9/23.
 * 15:02
 */

public class ConfirmListAdapter extends HsBaseAdapter<ConfirmPictureBean> {

    public ConfirmListAdapter(List<ConfirmPictureBean> list, Context context) {
        super(list, context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) convertView = mInflater.inflate(R.layout.ll_confirmitem, null);
        ConfirmPictureBean pictureBean = mList.get(position);

        TextView tvConfirmOrderNo = (TextView) convertView.findViewById(R.id.tvConfirmOrderNo);
        TextView tvConfirmUnitsName = (TextView) convertView.findViewById(R.id.tvConfirmUnitsName);
        TextView tvConfirmDate = (TextView) convertView.findViewById(R.id.tvConfirmDate);

        tvConfirmOrderNo.setText(pictureBean.SBILLNO);
        tvConfirmUnitsName.setText(pictureBean.SCUSTOMERNAME);
        tvConfirmDate.setText(pictureBean.DCREATEDATE);

        return convertView;
    }
}
