package com.ksource.hbpostal.activity;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.blankj.utilcode.utils.ToastUtils;
import com.google.gson.Gson;
import com.ksource.hbpostal.R;
import com.ksource.hbpostal.bean.AreaResaultBean;
import com.ksource.hbpostal.bean.AreaResaultBean.AreaListBean;
import com.ksource.hbpostal.bean.BaseResultBean;
import com.ksource.hbpostal.config.ConstantValues;
import com.ksource.hbpostal.util.DataUtil;
import com.yitao.dialog.LoadDialog;
import com.yitao.util.DialogUtil;
import com.yitao.util.ToastUtil;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;

/**
 * 添加地址页面
 */
public class AddAddrActivity extends BaseActivity {

	private TextView tv_title, tv_right;
	private ImageView iv_back;
	private TextView tv_area,tv_user_name;
	private EditText et_user_name, et_phone, et_addr;
	private ToggleButton tb_defaultl;
	private String userName = "";
	private String userPhone = "";
	private String userAddr = "";
	private String userAddrId = "";
	// private String curArea = "";
	// private String curAreaId = "";
	// private String curCity = "";
	// private String curCityId = "";
	// private String curProvince = "";
	// private String curProvinceId = "";
	private boolean isDefault;
	private LoadDialog mLoadDialog;

	private List<AreaListBean> provinceList;
	private List<AreaListBean> cityList;
	private List<AreaListBean> areaList;

	private String[] mProvinceDatas;
	private String[] mCityDatas;
	private String[] mAreaDatas;

	private String curProvince = "";
	private String curCity = "";
	private String curArea = "";

	private String curProvinceId = "";
	private String curCityId = "";
	private String curAreaId = "";
	private String userAreaStr = "";
	private String msg = "";
	private boolean isLoading;
	private int type;
	private String typeStr;

	@Override
	public int getLayoutResId() {
		return R.layout.activity_edit_addr;
	}

	@Override
	public void initView() {
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_right = (TextView) findViewById(R.id.btn_right);
		iv_back = (ImageView) findViewById(R.id.iv_back);

		tv_area = (TextView) findViewById(R.id.tv_area);
		tv_user_name = (TextView) findViewById(R.id.tv_user_name);
		et_user_name = (EditText) findViewById(R.id.et_user_name);
		et_phone = (EditText) findViewById(R.id.et_phone);
		et_addr = (EditText) findViewById(R.id.et_addr);
		tb_defaultl = (ToggleButton) findViewById(R.id.tb_default);

	}

	@Override
	public void initListener() {
		iv_back.setOnClickListener(this);
		tv_right.setOnClickListener(this);
		tv_area.setOnClickListener(this);
		tb_defaultl.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {

				isDefault = isChecked;
				// if (isChecked) {
				// ToastUtil.showTextToast(context, "设为默认");
				// } else {
				// ToastUtil.showTextToast(context, "取消默认");
				// }
			}
		});
	}

	@Override
	public void initData() {
		type = getIntent().getIntExtra("type",1);
		typeStr = type==1?"收货":"寄件";
		tv_right.setText("保存");
		tv_right.setVisibility(View.VISIBLE);
		Intent intent = getIntent();
		isDefault = intent.getBooleanExtra("null", false);
		userAddrId = intent.getStringExtra("userAddrId");
		tv_user_name.setText(typeStr+"人");
		et_user_name.setHint("请输入"+typeStr+"人姓名");
		if (TextUtils.isEmpty(userAddrId)) {
			tv_title.setText("添加"+typeStr+"地址");
			tb_defaultl.setChecked(isDefault);
			msg = "添加"+typeStr+"地址";
		} else {
			userName = intent.getStringExtra("userName");
			userPhone = intent.getStringExtra("userPhone");
			curArea = intent.getStringExtra("userArea");
			curAreaId = intent.getStringExtra("userAreaId");
			curCity = intent.getStringExtra("userCity");
			curCityId = intent.getStringExtra("userCityId");
			curProvince = intent.getStringExtra("userProvince");
			curProvinceId = intent.getStringExtra("userProvinceId");
			userAddr = intent.getStringExtra("userAddr");
			isDefault = "2".equals(intent.getStringExtra("isDefault"));
			userAreaStr = curProvince + curCity + curArea;
			setText();
			tv_title.setText("编辑"+typeStr+"地址");
			msg = "编辑"+typeStr+"地址";
		}

	}

	private void setText() {
		et_user_name.setText(userName);
		et_phone.setText(userPhone);
		et_addr.setText(userAddr);
		tv_area.setText(userAreaStr);
		tb_defaultl.setChecked(isDefault);
	}

	private void getText() {
		userName = et_user_name.getText().toString().trim();
		userPhone = et_phone.getText().toString().trim();
		userAddr = et_addr.getText().toString().trim();
		userAreaStr = tv_area.getText().toString().trim();

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			finish();
			break;
		case R.id.btn_right:
			// 保存 上传
			String token = sp.getString(ConstantValues.TOKEN, null);
			if (TextUtils.isEmpty(token)) {
				// finish();
				startActivity(new Intent(context, LoginActivity.class));
			}
			getText();

			if (TextUtils.isEmpty(userPhone) || userPhone.length() > 12) {
				ToastUtils.showLongToast("请输入正确的联系电话！");
				et_phone.requestFocus();
				return;
			}
			if (TextUtils.isEmpty(userName)) {
				ToastUtils.showLongToast("请输入收货人姓名！");
				et_user_name.requestFocus();
				return;
			}
			if (TextUtils.isEmpty(userAreaStr)) {
				ToastUtils.showLongToast("请先完善您的地址信息！");
//				userAreaStr = "";
				getArea("", 1);
				return;
			}
			if (TextUtils.isEmpty(userAddr)) {
				ToastUtils.showLongToast("请先完善您的地址信息！");
				et_addr.requestFocus();
				return;
			}
			submitData();
			break;
		case R.id.tv_area:
			if (!isLoading) {
				getArea("", 1);
			}
			break;

		default:
			break;
		}
	}

	// 获取地区
	private void getArea(final String upId, final int type) {
		mLoadDialog = DialogUtil.getInstance().showLoadDialog(context);
		isLoading = true;
		Map<String, String> params = new HashMap<>();
		String token = sp.getString(ConstantValues.TOKEN, null);
		params.put("token", token);
		params.put("up_id", upId);
		params.put("type", type+"");

		StringCallback callback = new StringCallback() {
			
			@Override
			public void onResponse(String arg0, int arg1) {
				isLoading = false;
				DialogUtil.getInstance().dialogDismiss(mLoadDialog);
				Gson gson = new Gson();
				AreaResaultBean areaResult = null;
				try {
					areaResult = gson.fromJson(arg0,
							AreaResaultBean.class);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (areaResult == null) {
					ToastUtil.showTextToast(context, "获取地区失败！");
					return;
				}
				if (areaResult.success) {
					switch (type) {
					case 1:
						provinceList = areaResult.areaList;
						if (areaResult.areaList != null
								&& areaResult.areaList.size() > 0) {
							mProvinceDatas = new String[provinceList
									.size()];
							for (int i = 0; i < provinceList.size(); i++) {
								mProvinceDatas[i] = provinceList.get(i).address_name;
							}
							chioceItem("省份", mProvinceDatas, 1);
						}

						break;
					case 2:
						cityList = areaResult.areaList;
						if (areaResult.areaList != null
								&& areaResult.areaList.size() > 0) {
							mCityDatas = new String[cityList.size()];
							for (int i = 0; i < cityList.size(); i++) {
								mCityDatas[i] = cityList.get(i).address_name;
							}
							chioceItem("市", mCityDatas, 2);
						}

						break;
					case 3:
						areaList = areaResult.areaList;
						if (areaResult.areaList != null
								&& areaResult.areaList.size() > 0) {
							mAreaDatas = new String[areaList.size()];
							for (int i = 0; i < areaList.size(); i++) {
								mAreaDatas[i] = areaList.get(i).address_name;
							}
							chioceItem("县区", mAreaDatas, 3);
						}

						break;

					default:
						break;
					}
				} else {
					ToastUtil.showTextToast(context, areaResult.msg);
				}
			}
			
			@Override
			public void onError(Call arg0, Exception arg1, int arg2) {
				isLoading = false;
				DialogUtil.getInstance().dialogDismiss(mLoadDialog);
			}
		};
		DataUtil.doPostAESData(mLoadDialog,context,ConstantValues.GET_AREA_URL, params, callback);
	}

	// 选择地区
	private void chioceItem(String title, final String[] itemDatas,
			final int type) {

		Builder builder = new Builder(this);
		builder.setTitle("请选择" + title);
		builder.setItems(itemDatas, new DialogInterface.OnClickListener() {
			/*
			 * 第一个参数代表对话框对象 第二个参数是点击对象的索引
			 */
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (type == 1) {
					userAreaStr = "";
					curProvinceId = "";
					curCityId = "";
					curAreaId = "";
					curProvince = "";
					curCity = "";
					curArea = "";
				}
				userAreaStr += itemDatas[which];
				tv_area.setText(userAreaStr);
				
				switch (type) {
				case 1:
					curProvince = itemDatas[which];
					curProvinceId = provinceList.get(which).id;
					getArea(curProvinceId, 2);
					break;
				case 2:
					curCity = itemDatas[which];
					curCityId = cityList.get(which).id;
					getArea(curCityId, 3);
					break;
				case 3:
					curArea = itemDatas[which];
					curAreaId = areaList.get(which).id;
					break;

				default:
					break;
				}
			}
		});
		builder.show();

	}

	// 提交地址
	private void submitData() {
		mLoadDialog = DialogUtil.getInstance().showLoadDialog(context,
				"提交"+typeStr+"地址...");
		Map<String, String> params = new HashMap<>();
		String token = sp.getString(ConstantValues.TOKEN, "");
		params.put("token", token);
		params.put("name", userName);
		params.put("type", type+"");
		params.put("mobile", userPhone);
		params.put("province_id", curProvinceId);
		params.put("province_name", curProvince);
		params.put("city_id", curCityId);
		params.put("city_name", curCity);
		params.put("area_id", curAreaId);
		params.put("area_name", curArea);
		params.put("address", userAddr);
		params.put("addressId", userAddrId);
		params.put("is_default_address", isDefault ? "2" : "1");
		
		StringCallback callback = new StringCallback() {
			
			@Override
			public void onResponse(String arg0, int arg1) {
				DialogUtil.getInstance().dialogDismiss(mLoadDialog);
				Gson gson = new Gson();
				BaseResultBean baseResult = null;
				try {
					baseResult = gson.fromJson(arg0,
							BaseResultBean.class);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (baseResult == null) {
					new SweetAlertDialog(context,
							SweetAlertDialog.ERROR_TYPE)
							.setTitleText("失败！")
							.setContentText(msg+"失败！").show();
					return;
				}
				if (baseResult.success) {
					SweetAlertDialog dialog = new SweetAlertDialog(
							context, SweetAlertDialog.SUCCESS_TYPE);
					dialog.setTitleText("成功！")
							.setContentText(msg+"成功！")
							.setConfirmClickListener(
									new SweetAlertDialog.OnSweetClickListener() {
										@Override
										public void onClick(
												SweetAlertDialog sDialog) {
											sDialog.dismiss();
											finish();
										}
									}).show();

				} else {
					if (baseResult.flag == 10) {
						mApplication.login();
					}
					new SweetAlertDialog(context,
							SweetAlertDialog.ERROR_TYPE)
							.setTitleText("失败！")
							.setContentText(baseResult.msg).show();
				}

			}
			
			@Override
			public void onError(Call arg0, Exception arg1, int arg2) {
				DialogUtil.getInstance().dialogDismiss(mLoadDialog);
				ToastUtils.showShortToast(msg+"失败！");
			}
		};
		DataUtil.doPostAESData(mLoadDialog,context,ConstantValues.SUBMIT_ADDR_URL, params, callback);
	}

}
