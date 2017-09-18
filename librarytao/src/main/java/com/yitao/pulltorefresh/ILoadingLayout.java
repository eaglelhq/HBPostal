package com.yitao.pulltorefresh;

/**
 * 下拉刷新和上拉加载更多的界面接口
 */
public interface ILoadingLayout {
    /**
     * 当前的状态
     */
    public enum State {
        
        /**
         * Initial state
         */
        NONE,
        
        /**
         * When the UI is in a state which means that user is not interacting
         * with the Pull-to-Refresh function.
         */
        RESET,
        
        /**
         * When the UI is being pulled by the user, but has not been pulled far
         * enough so that it refreshes when released.
         */
        PULL_TO_REFRESH,
        
        /**
         * When the UI is being pulled by the user, and <strong>has</strong>
         * been pulled far enough so that it will refresh when released.
         */
        RELEASE_TO_REFRESH,
        
        /**
         * When the UI is currently refreshing, caused by a pull gesture.
         */
        REFRESHING,
        
        /**
         * When the UI is currently refreshing, caused by a pull gesture.
         */
        @Deprecated
        LOADING,
        
        /**
         * No more data
         */
        NO_MORE_DATA,
        /**
         * No data
         */
        NO_DATA,
    }

    /**
     * 
     * @param state 状态
     */
    public void setState(State state);
    
    /**
     *  
     * @return 状态
     */
    public State getState();
    
    /**
     * 
     * @return 高度
     */
    public int getContentSize();
    
    /**
     * 
     * @param scale 拉动的比例
     */
    public void onPull(float scale);
}
