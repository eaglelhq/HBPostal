package com.handmark.pulltorefresh.library.internal;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Orientation;
import com.yitao.library_tao.R;

/**
 * @author 祁连山
 * @desc 帧动画加载布局
 * @date 2015/1/8
 */
public class FrameAnimationLayout extends LoadingLayout {

    private AnimationDrawable mAnimationDrawable;

    public FrameAnimationLayout(Context context, Mode mode, Orientation scrollDirection, TypedArray attrs) {
        super(context, mode, scrollDirection, attrs);
        mHeaderImage.setImageResource(R.drawable.progress_circle_loading);
        mAnimationDrawable = (AnimationDrawable) mHeaderImage.getDrawable();
    }

    @Override
    protected int getDefaultDrawableResId() {
        return R.drawable.ic_launcher;
    }

    @Override
    protected void onLoadingDrawableSet(Drawable imageDrawable) {
    }

    @Override
    protected void onPullImpl(float scaleOfLayout) {
    }

    @Override
    protected void pullToRefreshImpl() {
    }

    @Override
    protected void refreshingImpl() {
        mAnimationDrawable.start();
    }

    @Override
    protected void releaseToRefreshImpl() {
    }

    @Override
    protected void resetImpl() {
    	mHeaderImage.setVisibility(View.VISIBLE);  
        mHeaderImage.clearAnimation(); 
    }
}
