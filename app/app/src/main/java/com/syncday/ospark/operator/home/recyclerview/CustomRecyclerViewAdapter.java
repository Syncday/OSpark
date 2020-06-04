package com.syncday.ospark.operator.home.recyclerview;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.syncday.ospark.R;
import com.syncday.ospark.bean.CarBean;

import java.util.List;

public class CustomRecyclerViewAdapter extends RecyclerView.Adapter<CustomRecyclerViewAdapter.HomeHolder> {

    private Context mContext;
    private List<CarBean.Car> mList;

    public CustomRecyclerViewAdapter(Context context, List<CarBean.Car> list) {
        this.mContext = context;
        this.mList = list;
    }



    ExpandableViewHoldersUtil.KeepOneH<HomeHolder> keepOne = new ExpandableViewHoldersUtil.KeepOneH<>();

    //点击事件的回调
    private OnItemClickListener<CarBean.Car> onItemClickListener;

    //设置回调监听
    public void setOnItemClickListener(OnItemClickListener<CarBean.Car> listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public HomeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HomeHolder((ViewGroup) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_op_home_recycler_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final HomeHolder holder, final int position) {
        final CarBean.Car car = mList.get(position);
        holder.bind(position, car);
        holder.initItemListener(position,car);
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    public class HomeHolder extends RecyclerView.ViewHolder implements View.OnClickListener, ExpandableViewHoldersUtil.Expandable {

        private HomeHolder mHolder;
        public final TextView mTitle;
        public final TextView mTime;

        public TextView detail;
        public TextView message;
        public TextView pay;

        public final ImageView mRight;//右侧图标
        public final RelativeLayout mTopLayout; //折叠View
        public final LinearLayout mBottomLayout; //折叠View

        public HomeHolder(@NonNull View itemView) {
            super(itemView);
            mHolder = this;
            mTitle = itemView.findViewById(R.id.home_car_textView);
            mTime = itemView.findViewById(R.id.home_time_textView);

            detail = itemView.findViewById(R.id.recycler_detail);
            message = itemView.findViewById(R.id.recycler_message);
            pay = itemView.findViewById(R.id.recycler_pay);

            mRight = itemView.findViewById(R.id.home_right_imageView);
            mTopLayout = itemView.findViewById(R.id.mTopLayout);
            mBottomLayout = itemView.findViewById(R.id.mBottomLayout);
            mTopLayout.setOnClickListener(this);
        }

        //绑定数据
        public void bind(final int pos, final CarBean.Car car) {
            keepOne.bind(this,pos);
            String text = car.getParking_car();
            //截取时间
            String time = car.getParking_time().substring(11,16);
            //设置第一个字的大小，颜色
            SpannableString span = new SpannableString(text);
            span.setSpan( new ForegroundColorSpan(Color.parseColor("#1296db")), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            span.setSpan(new RelativeSizeSpan(1.5f), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            mTitle.setText(span);
            mTime.setText(time);

        }

        //添加item的监听器
        public void initItemListener(final int pos, final CarBean.Car car){
            mHolder.detail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onItemClickListener != null){
                        onItemClickListener.onItemClick(car, mHolder.detail, pos);
                    }
                }
            });
            mHolder.message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onItemClickListener != null){
                        onItemClickListener.onItemClick(car, mHolder.message, pos);
                    }
                }
            });
            mHolder.pay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onItemClickListener != null){
                        onItemClickListener.onItemClick(car, mHolder.pay, pos);
                    }
                }
            });
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.mTopLayout:
                    keepOne.toggle(mHolder, mRight);
                    break;
            }
        }

        @Override
        public View getExpandView() {
            return mBottomLayout;
        }
    }

}