package com.syncday.ospark.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.syncday.ospark.R;
import com.syncday.ospark.bean.PriceListBean;

import java.util.List;

public class PriceListRecyclerViewAdapter extends RecyclerView.Adapter<PriceListRecyclerViewAdapter.mHolder> {

    private Context mContext;
    private List<PriceListBean.priceList> mList;

    public PriceListRecyclerViewAdapter(Context context, List<PriceListBean.priceList> list) {
        this.mContext = context;
        this.mList = list;
    }
    @NonNull
    @Override
    public mHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new mHolder((ViewGroup) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_price_list_recycler_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PriceListRecyclerViewAdapter.mHolder holder, int position) {
        //将数据和控件绑定
        final PriceListBean.priceList priceList = mList.get(position);
        holder.bind(position, priceList);
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    //内部类，绑定控件
    class mHolder extends RecyclerView.ViewHolder {

        private mHolder mHolder;
        private LinearLayout linearLayout;
        private TextView time_tv,price_tv;

        public mHolder(@NonNull View itemView) {
            super(itemView);
            mHolder = this;
            time_tv = itemView.findViewById(R.id.price_list_item_time);
            price_tv = itemView.findViewById(R.id.price_list_item_price);

        }

        //绑定数据
        public void bind(final int pos, final PriceListBean.priceList priceList) {
            SpannableString time = new SpannableString("时间(分钟):"+priceList.getTimeLine());
            time.setSpan( new ForegroundColorSpan(Color.parseColor("#1296db")), 7,7+priceList.getTimeLine().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            SpannableString price = new SpannableString("价格(元)"+priceList.getPrice());
            price.setSpan( new ForegroundColorSpan(Color.parseColor("#F8AE51")), 5,5+priceList.getPrice().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            time_tv.setText(time);
            price_tv.setText(price);
        }
    }
}
