package com.yitao.pulltorefresh;

import com.yitao.pulltorefresh.PullToRefreshBase.OnRefreshListener;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;



/**
 * 
 * @param <T>
 */
public interface IPullToRefresh<T extends View> {
    
    /**
     * 带弹性下拉刷新
     * @param pullRefreshEnabled true表示可用，false表示不可用
     */
    public void setPullRefreshEnabled(boolean pullRefreshEnabled);
    
    /**
     * 带弹性上拉加载
     * @param pullLoadEnabled true表示可用，false表示不可用
     */
    public void setPullLoadEnabled(boolean pullLoadEnabled);
    
    /**
     * 滚动到最后一条加载状态条
     * @param scrollLoadEnabled true表示可用，false表示不可用
     */
    public void setScrollLoadEnabled(boolean scrollLoadEnabled);
    
    /**
     * 
     * @return true如果可用，false不可用
     */
    public boolean isPullRefreshEnabled();
    
    /**
     * 
     * @return true可用，false不可用
     */
    public boolean isPullLoadEnabled();
    
    /**
     * 
     * @return true可用，否则不可用
     */
    public boolean isScrollLoadEnabled();
    
    /**
     * 
     * @param refreshListener 监听器对象
     */
    public void setOnRefreshListener(OnRefreshListener<T> refreshListener);
    
    /**
     */
    public void onPullDownRefreshComplete();
    
    /**
     */
    public void onPullUpRefreshComplete();
    
    /**
     * 
     * @return 返回调用{@link #createRefreshableView(Context, AttributeSet)} 方法返回的对象
     */
    public T getRefreshableView();
    
    /**
     * 
     * @return Header布局对象
     */
    public LoadingLayout getHeaderLoadingLayout();
    
    /**
     * 
     * @return Footer布局对象
     */
    public LoadingLayout getFooterLoadingLayout();
    
    /**
     * 
     * @param label 文本
     */
    public void setLastUpdatedLabel(CharSequence label);
}
