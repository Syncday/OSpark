package com.syncday.ospark.operator.home.recyclerview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Build;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.syncday.ospark.R;

/**
 * 作者：created by Jarchie
 * 时间：2019-11-24 11:21:03
 * 邮箱：jarchie520@gmail.com
 * 说明：折叠透明度动画工具类
 */
public class ExpandableViewHoldersUtil {
    //自定义处理列表中右侧图标，这里是一个旋转动画
    public static void rotateExpandIcon(final ImageView mImage, float from, float to) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(from, to);//属性动画
            valueAnimator.setDuration(500);
            valueAnimator.setInterpolator(new DecelerateInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    mImage.setRotation((Float) valueAnimator.getAnimatedValue());
                }
            });
            valueAnimator.start();
        }
    }

    //参数介绍：1、holder对象 2、展开部分的View，由holder.getExpandView()方法获取 3、animate参数为true，则有动画效果
    public static void openH(final RecyclerView.ViewHolder holder, final View expandView, final boolean animate) {
        if (animate) {
            expandView.setVisibility(View.VISIBLE);
            //改变高度的动画
            final Animator animator = ViewHolderAnimator.ofItemViewHeight(holder);
            //扩展的动画，结束后透明度动画开始
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    final ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(expandView, View.ALPHA, 1);
                    alphaAnimator.addListener(new ViewHolderAnimator.ViewHolderAnimatorListener(holder));
                    alphaAnimator.start();
                }
            });
            animator.start();
        } else {
            expandView.setVisibility(View.VISIBLE);
            expandView.setAlpha(1);
        }
    }

    //类似于打开的方法
    public static void closeH(final RecyclerView.ViewHolder holder, final View expandView, final boolean animate) {
        if (animate) {
            expandView.setVisibility(View.GONE);
            final Animator animator = ViewHolderAnimator.ofItemViewHeight(holder);
            expandView.setVisibility(View.VISIBLE);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    expandView.setVisibility(View.GONE);
                    expandView.setAlpha(0);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    expandView.setVisibility(View.GONE);
                    expandView.setAlpha(0);
                }
            });
            animator.start();
        } else {
            expandView.setVisibility(View.GONE);
            expandView.setAlpha(0);
        }
    }
    //获取展开部分的View
    public interface Expandable {
        View getExpandView();
    }

    public static class KeepOneH<VH extends RecyclerView.ViewHolder & Expandable> {
        //-1表示所有item是关闭状态，opend为pos值的表示pos位置的item为展开的状态
        private int _opened = -1;

        /**
         * 此方法是在Adapter的onBindViewHolder()方法中调用
         *
         * @param holder holder对象
         * @param pos    下标
         */
        public void bind(VH holder, int pos) {
            if (pos == _opened)
                ExpandableViewHoldersUtil.openH(holder, holder.getExpandView(), false);
            else
                ExpandableViewHoldersUtil.closeH(holder, holder.getExpandView(), false);
        }

        /**
         * 响应ViewHolder的点击事件
         *
         * @param holder    holder对象
         * @param imageView 这里我传入了一个ImageView对象，为了处理图片旋转的动画，为了处理内部业务
         */
        @SuppressWarnings({"unchecked", "deprecation"})
        public void toggle(VH holder, ImageView imageView) {
            if (_opened == holder.getPosition()) {
                _opened = -1;
                ExpandableViewHoldersUtil.rotateExpandIcon(imageView, 90, 0);
                ExpandableViewHoldersUtil.closeH(holder, holder.getExpandView(), true);
            } else {//点击的是本来关闭的Item，则把opend值换成当前pos，把之前打开的Item给关掉
                int previous = _opened;
                _opened = holder.getPosition();
                ExpandableViewHoldersUtil.rotateExpandIcon(imageView, 0, 90);
                ExpandableViewHoldersUtil.openH(holder, holder.getExpandView(), true);
                //动画关闭之前打开的Item
                final VH oldHolder = (VH) ((RecyclerView) holder.itemView.getParent()).findViewHolderForPosition(previous);
                if (oldHolder != null) {
                    //列表的展开图标
                    ImageView oldImageView = oldHolder.itemView.findViewById(R.id.home_right_imageView);
                    ExpandableViewHoldersUtil.rotateExpandIcon(oldImageView, 90, 0);
                    ExpandableViewHoldersUtil.closeH(oldHolder, oldHolder.getExpandView(), true);
                }
            }
        }
    }
}
