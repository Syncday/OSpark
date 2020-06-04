package com.syncday.ospark.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.syncday.ospark.R;
import com.syncday.ospark.bean.BillBean;

import java.util.List;

public class BillRecyclerViewAdapter  extends RecyclerView.Adapter<BillRecyclerViewAdapter.mHolder>  {


    private Context mContext;
    private List<BillBean.Bill> mList;

    public BillRecyclerViewAdapter(Context context, List<BillBean.Bill> list) {
        this.mContext = context;
        this.mList = list;
    }

    //列表项事件的回调
    private OnItemEventListener<BillBean.Bill> onItemEventListener;
    //设置回调监听
    public void setOnItemEventListener(OnItemEventListener<BillBean.Bill> listener) {
        this.onItemEventListener = listener;
    }


    @NonNull
    @Override
    public mHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //创建ViewHolder，返回每一项的布局
        return new mHolder((ViewGroup) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bill_recycler_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final mHolder holder, final int position) {
        //将数据和控件绑定
        final BillBean.Bill bill = mList.get(position);
        holder.bind(position, bill);
        holder.initListener(position,bill);
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    //内部类，绑定控件
    class mHolder extends RecyclerView.ViewHolder{

        private mHolder mHolder;
        private LinearLayout linearLayout;
        private TextView car_tv,price_tv,start_time_tv,stop_time_tv,address_tv;


        public mHolder(@NonNull View itemView) {
            super(itemView);
            linearLayout =itemView.findViewById(R.id.bill_layout);
            car_tv = itemView.findViewById(R.id.bill_car);
            address_tv = itemView.findViewById(R.id.bill_address);
            price_tv = itemView.findViewById(R.id.bill_price);
            start_time_tv = itemView.findViewById(R.id.bill_start_time);
            stop_time_tv = itemView.findViewById(R.id.bill_stop_time);
        }

        //绑定数据
        public void bind(final int pos, final BillBean.Bill bill) {

            SpannableString car = new SpannableString("车牌："+bill.getBill_car());
            car.setSpan( new ForegroundColorSpan(Color.parseColor("#FF527DA3")), 3, 3+bill.getBill_car().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            SpannableString price = new SpannableString("金额："+bill.getBill_price());
            price.setSpan( new ForegroundColorSpan(Color.parseColor("#FB8C00")), 3, 3+bill.getBill_price().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            SpannableString startTime = new SpannableString("结束："+bill.getBill_create_time());
            startTime.setSpan( new ForegroundColorSpan(Color.parseColor("#039BE5")), 3, 3+bill.getBill_create_time().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            SpannableString  stopTime = new SpannableString("结束："+bill.getBill_finish_time());
            stopTime.setSpan( new ForegroundColorSpan(Color.parseColor("#00897B")), 3, 3+bill.getBill_finish_time().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            car_tv.setText(car);
            price_tv.setText(price);
            start_time_tv.setText(startTime);
            stop_time_tv.setText(stopTime);
            address_tv.setText("地点："+bill.getBill_address());

        }
        //设置监听器
        public void initListener(final int position, final BillBean.Bill bill){
            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onItemEventListener != null){
                        onItemEventListener.onItemClick(bill, linearLayout, position);
                    }
                }
            });
        }

    }


}
