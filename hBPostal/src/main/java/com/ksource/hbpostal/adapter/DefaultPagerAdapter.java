package com.ksource.hbpostal.adapter;

import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

/**
 * 自定义的viewpager的PagerAdapter
 * @author Administrator
 *
 * @param <T>
 */
 
public abstract class DefaultPagerAdapter<T> extends PagerAdapter {
    //数据源
    public List<T> datas;
    public DefaultPagerAdapter(List<T> datas){
        this.datas = datas;
    }
    
    @Override
    public int getCount() {
        return datas.size();
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
    
    @Override
    public abstract Object instantiateItem(ViewGroup container, int position) ;
    
    
}
