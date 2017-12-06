package lugang.app.huansi.net.lugang.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import lugang.app.huansi.net.lugang.R;

/**
 * Created by Tony on 2017/12/2.
 * 16:13
 */

public class NewMeasureCustomAdapter extends RecyclerView.Adapter<NewMeasureCustomAdapter.MyViewHolder> {
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder=new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.new_measure_item,null));
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {


        public MyViewHolder(View itemView) {
            super(itemView);
            TextView tvCustomerName = (TextView) itemView.findViewById(R.id.tvCustomerName);
            AutoCompleteTextView actAreaName = (AutoCompleteTextView) itemView.findViewById(R.id.actAreaName);
            AutoCompleteTextView actCityName = (AutoCompleteTextView) itemView.findViewById(R.id.actCityName);
            AutoCompleteTextView actCountyName = (AutoCompleteTextView) itemView.findViewById(R.id.actCountyName);
            AutoCompleteTextView actDepartmentName = (AutoCompleteTextView) itemView.findViewById(R.id.actDepartmentName);
            AutoCompleteTextView actJobName = (AutoCompleteTextView) itemView.findViewById(R.id.actJobName);
            AutoCompleteTextView spSex = (AutoCompleteTextView) itemView.findViewById(R.id.spSex);
            AutoCompleteTextView spClothStyle = (AutoCompleteTextView) itemView.findViewById(R.id.spClothStyle);
            AutoCompleteTextView etCount = (AutoCompleteTextView) itemView.findViewById(R.id.etCount);
        }
    }

}
