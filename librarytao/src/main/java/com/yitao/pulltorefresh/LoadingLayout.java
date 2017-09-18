package com.yitao.pulltorefresh;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 */
public abstract class LoadingLayout extends FrameLayout implements ILoadingLayout {
    
    private View mContainer;
    private State mCurState = State.NONE;
    private State mPreState = State.NONE;
    
    /**
     * 
     * @param context context
     */
    public LoadingLayout(Context context) {
        this(context, null);
    }
    
    /**
     * 
     * @param context context
     * @param attrs attrs
     */
    public LoadingLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    
    /**
     * 
     * @param context context
     * @param attrs attrs
     * @param defStyle defStyle
     */
    public LoadingLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        
        init(context, attrs);
    }
    
    /**
     * 
     * @param context context
     * @param attrs attrs
     */
    protected void init(Context context, AttributeSet attrs) {
        mContainer = createLoadingView(context, attrs);
        if (null == mContainer) {
            throw new NullPointerException("Loading view can not be null.");
        }
        
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, 
                LayoutParams.WRAP_CONTENT);
        addView(mContainer, params);
    }

    /**
     * 
     * @param show flag
     */
    public void show(boolean show) {
        // If is showing, do nothing.
        if (show == (View.VISIBLE == getVisibility())) {
            return;
        }
        
        ViewGroup.LayoutParams params = mContainer.getLayoutParams();
        if (null != params) {
            if (show) {
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            } else {
                params.height = 0;
            }
            requestLayout(); 
            setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        }
    }
    
    /**
     * 
     * @param label 文本
     */
    public void setLastUpdatedLabel(CharSequence label) {
        
    }
    
    /**
     * 
     * @param drawable 图片
     */
    public void setLoadingDrawable(Drawable drawable) {
        
    }

    /**
     * 
     * @param pullLabel 拉动的文本
     */
    public void setPullLabel(CharSequence pullLabel) {
        
    }

    /**
     * 
     * @param refreshingLabel 刷新文本
     */
    public void setRefreshingLabel(CharSequence refreshingLabel) {
        
    }

    /**
     * 
     * @param releaseLabel 释放文本
     */
    public void setReleaseLabel(CharSequence releaseLabel) {
        
    }

    @Override
    public void setState(State state) {
        if (mCurState != state) {
            mPreState = mCurState;
            mCurState = state;
            onStateChanged(state, mPreState);
        }
    }
    
    @Override
    public State getState() {
        return mCurState;
    }

    @Override
    public void onPull(float scale) {
        
    }
    
    /**
     * 得到前一个状态
     * 
     * @return 状态
     */
    protected State getPreState() {
        return mPreState;
    }
    
    /**
     * 当状态改变时调用
     * 
     * @param curState 当前状态
     * @param oldState 老的状态
     */
    protected void onStateChanged(State curState, State oldState) {
        switch (curState) {
        case RESET:
            onReset();
            break;
            
        case RELEASE_TO_REFRESH:
            onReleaseToRefresh();
            break;
            
        case PULL_TO_REFRESH:
            onPullToRefresh();
            break;
            
        case REFRESHING:
            onRefreshing();
            break;
            
        case NO_MORE_DATA:
            onNoMoreData();
            break;
        case NO_DATA:
            onNoData();
            break;
        default:
            break;
        }
    }
    
    /**
     * 当状态设置为{@link State#RESET}时调用
     */
    protected void onReset() {
        
    }
    
    /**
     * 当状态设置为{@link State#PULL_TO_REFRESH}时调用
     */
    protected void onPullToRefresh() {
        
    }
    
    /**
     * 当状态设置为{@link State#RELEASE_TO_REFRESH}时调用
     */
    protected void onReleaseToRefresh() {
        
    }
    
    /**
     * 当状态设置为{@link State#REFRESHING}时调用
     */
    protected void onRefreshing() {
        
    }
    
    /**
     * 当状态设置为{@link State#NO_MORE_DATA}时调用
     */
    protected void onNoMoreData() {
        
    }
    
    /**
     * 当状态设置为{@link State#NO_DATA}时调用
     */
    protected void onNoData(){
    	
    }
    
    /**
     * 得到当前Layout的内容大小，它将作为一个刷新的临界点
     * 
     * @return 高度
     */
    public abstract int getContentSize();
    
    /**
     * 创建Loading的View
     * 
     * @param context context
     * @param attrs attrs
     * @return Loading的View
     */
    protected abstract View createLoadingView(Context context, AttributeSet attrs);
}
