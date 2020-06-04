package com.syncday.ospark.operator.home.recyclerview;

import android.view.View;

/**
 * 点击事件的回调接口
 */
public interface OnItemClickListener<T> {

    /**
     * Item点击事件
     *
     * @param bean
     * @param view
     * @param position
     */
    void onItemClick(T bean, View view, int position);

}
