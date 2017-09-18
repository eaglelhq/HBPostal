package com.ksource.hbStore.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blankj.utilcode.utils.SPUtils;
import com.ksource.hbStore.MyApplication;
import com.ksource.hbStore.config.ConstantValues;


/**
 * 首页对应的fragment基类
 * @author wangdh
 *
 */
public abstract class BaseFragment extends Fragment {
    public Context context;
    public View view;
    public MyApplication mApplication;
    public Activity mActivity;
    public SPUtils spUtils;

    @SuppressWarnings("deprecation")
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
 
        mActivity=activity;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        mApplication = MyApplication.getInstance();
        view = initView();
        spUtils = new SPUtils(ConstantValues.SP_NAME);
        initData();
        return view;
    }

   
    /**
     * 初始化界面
     * @return
     */
    public abstract View initView() ;
    
    /**
     * 初始化数据
     */
    public abstract void initData() ;
    
}
