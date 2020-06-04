package com.syncday.ospark.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.syncday.ospark.R;
import com.syncday.ospark.bean.ChatBean;
import com.syncday.ospark.bean.ChatListBean;

import java.util.List;

public class ChatRecyclerViewAdapter extends RecyclerView.Adapter<ChatRecyclerViewAdapter.mHolder> {

    private Context mContext;
    private List<ChatBean> mList;

    public ChatRecyclerViewAdapter(Context context, List<ChatBean> list) {
        this.mContext = context;
        this.mList = list;
    }

    @NonNull
    @Override
    public mHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //创建ViewHolder，返回每一项的布局
        return new mHolder((ViewGroup) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_recycler_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final mHolder holder, final int position) {
        //将数据和控件绑定
        final ChatBean chatBean = mList.get(position);
        holder.bind(position, chatBean);
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    //内部类，绑定控件
    class mHolder extends RecyclerView.ViewHolder{

        private mHolder mHolder;
        private LinearLayout leftLayout,rightLayout;
        private TextView chat_content_right,chat_time_right;
        private TextView chat_content_left,chat_time_left;

        public mHolder(@NonNull View itemView) {
            super(itemView);
            mHolder = this;
            leftLayout = itemView.findViewById(R.id.chat_content_left_layout);
            rightLayout = itemView.findViewById(R.id.chat_content_right_layout);
            chat_content_left = itemView.findViewById(R.id.chat_content_left);
            chat_content_right = itemView.findViewById(R.id.chat_content_right);
            chat_time_left = itemView.findViewById(R.id.chat_time_left);
            chat_time_right = itemView.findViewById(R.id.chat_time_right);

        }

        //绑定数据
        public void bind(final int pos, final ChatBean chatBean) {

            String content = chatBean.getContent();
            String time = chatBean.getTime();
            String from = chatBean.getFrom();

            if(from != null){//左边赋值
                chat_content_left.setText(content);
                chat_time_left.setText(time);
                rightLayout.setVisibility(View.GONE);
                leftLayout.setVisibility(View.VISIBLE);
            }else {//右边赋值
                chat_content_right.setText(content);
                chat_time_right.setText(time);
                leftLayout.setVisibility(View.GONE);
                rightLayout.setVisibility(View.VISIBLE);
            }

        }

    }

}