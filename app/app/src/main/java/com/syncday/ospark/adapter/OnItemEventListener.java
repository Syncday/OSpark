package com.syncday.ospark.adapter;

import android.view.View;

public interface OnItemEventListener<T> {
    /**
     * Item点击事件
     *
     * @param bean
     * @param view
     * @param position
     */
    void onItemClick(T bean, View view, int position);

    /**
     * Item长按事件
     *
     * @param bean
     * @param view
     * @param position
     */
    void onItemLongClick(T bean, View view, int position);
}
