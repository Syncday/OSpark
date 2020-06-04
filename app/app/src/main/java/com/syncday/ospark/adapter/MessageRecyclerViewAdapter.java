package com.syncday.ospark.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.syncday.ospark.R;
import com.syncday.ospark.bean.ChatListBean;

import java.util.List;

public class MessageRecyclerViewAdapter extends RecyclerView.Adapter<MessageRecyclerViewAdapter.mHolder> {

    private Context mContext;
    private List<ChatListBean> mList;

    public MessageRecyclerViewAdapter(Context context, List<ChatListBean> list) {
        this.mContext = context;
        this.mList = list;
    }

    //列表项事件的回调
    private OnItemEventListener<ChatListBean> onItemEventListener;
    //设置回调监听
    public void setOnItemEventListener(OnItemEventListener<ChatListBean> listener) {
        this.onItemEventListener = listener;
    }


    @NonNull
    @Override
    public mHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //创建ViewHolder，返回每一项的布局
        return new mHolder((ViewGroup) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_op_message_recycler_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final mHolder holder, final int position) {
        //将数据和控件绑定
        final ChatListBean chatListBean = mList.get(position);
        holder.bind(position, chatListBean);
        holder.initListener(position,chatListBean);
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    //内部类，绑定控件
    class mHolder extends RecyclerView.ViewHolder{

        private mHolder mHolder;
        private LinearLayout linearLayout;
        private TextView item_nickname,item_sender,item_preview,item_time,item_new;

        public mHolder(@NonNull View itemView) {
            super(itemView);
            mHolder = this;
            item_nickname = itemView.findViewById(R.id.item_nickname);
            item_sender = itemView.findViewById(R.id.item_sender);
            item_preview = itemView.findViewById(R.id.item_preView);
            item_time = itemView.findViewById(R.id.item_time);
            item_new = itemView.findViewById(R.id.item_new);

            linearLayout = itemView.findViewById(R.id.item_op_message_recycler_layout);

        }

        //绑定数据
        public void bind(final int pos, final ChatListBean chatListBean) {

            String sender = chatListBean.getSender();
            String preview = chatListBean.getPreview();
            String time = chatListBean.getTime();
            String hasNew = chatListBean.getHasNew();
            String nickname = chatListBean.getNickname();

            item_nickname.setText(nickname);
            item_sender.setText(sender);
            item_preview.setText(preview);
            item_time.setText(time);
            item_new.setText(hasNew);

        }

        //设置监听器
        public void initListener(final int position, final ChatListBean chatListBean){
            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onItemEventListener != null){
                        onItemEventListener.onItemClick(chatListBean, mHolder.linearLayout, position);
                    }
                }
            });

            linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(onItemEventListener != null){
                        onItemEventListener.onItemLongClick(chatListBean, mHolder.linearLayout, position);
                    }
                    return true;
                }
            });
        }
    }

}