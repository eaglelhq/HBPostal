package com.ksource.hbpostal.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ksource.hbpostal.MyApplication;
import com.ksource.hbpostal.config.ConstantValues;
/**
 * 首页对应的fragment基类
 * @author wangdh
 *
 */
public abstract class BaseFragment extends Fragment {
    public Context context;
    public View view;
    public MyApplication mApplication;
    public SharedPreferences sp;
    public Activity mActivity;
    
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
        sp = context.getSharedPreferences(ConstantValues.SP_NAME, Context.MODE_PRIVATE);
        view = initView();
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
