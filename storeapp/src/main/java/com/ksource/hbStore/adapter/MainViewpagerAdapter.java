package com.ksource.hbStore.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 详情列表的viewpager的适配器
 * @author Administrator
 *
 */
 
public class MainViewpagerAdapter extends FragmentPagerAdapter {
    //数据源
    public List<Fragment> datas = new ArrayList<Fragment>();
    
    public MainViewpagerAdapter(FragmentManager fm,List<Fragment> datas) {
        super(fm);
        this.datas = datas;
    }
    /**
     * 根据当前position返回对应的fragment
     */
    @Override
    public Fragment getItem(int position) {
        return datas.get(position);
    }
    
    @Override
    public int getCount() {
        return datas.size();
    }
    
}
