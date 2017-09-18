package com.ksource.hbpostal.fragment;

import com.ksource.hbpostal.R;
import com.yitao.util.ToastUtil;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

public class GoldActiveFragment extends BaseFragment implements OnClickListener {

	
	private LinearLayout ll_dazhuanpan,ll_guaguale,ll_yaoyaole;
	
	@Override
	public View initView() {
		return View.inflate(context, R.layout.fragment_gold_active, null);
	}

	@Override
	public void initData() {
		ll_dazhuanpan = (LinearLayout) view.findViewById(R.id.ll_dazhuanpan);
		ll_guaguale = (LinearLayout) view.findViewById(R.id.ll_guaguale);
		ll_yaoyaole = (LinearLayout) view.findViewById(R.id.ll_yaoyaole);

		ll_dazhuanpan.setOnClickListener(this);
		ll_guaguale.setOnClickListener(this);
		ll_yaoyaole.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_dazhuanpan:
			ToastUtil.showTextToast(context, "游戏还在开发中！");
			break;
		case R.id.ll_guaguale:
			ToastUtil.showTextToast(context, "游戏还在开发中！");
			
			break;
		case R.id.ll_yaoyaole:
			ToastUtil.showTextToast(context, "游戏还在开发中！");
			
			break;

		default:
			break;
		}
	}

}
