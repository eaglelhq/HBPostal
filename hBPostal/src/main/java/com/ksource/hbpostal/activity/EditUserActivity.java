package com.ksource.hbpostal.activity;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.utils.ToastUtils;
import com.google.gson.Gson;
import com.ksource.hbpostal.R;
import com.ksource.hbpostal.bean.BaseResultBean;
import com.ksource.hbpostal.bean.HBAreaResaultBean;
import com.ksource.hbpostal.bean.HBAreaResaultBean.AreaListBean;
import com.ksource.hbpostal.bean.HomeInfoResultBean;
import com.ksource.hbpostal.bean.HomeResultBean;
import com.ksource.hbpostal.bean.HomeResultBean.HomeListBean;
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

public class EditUserActivity extends BaseActivity {

	private TextView tv_title, tv_right;
	private ImageView iv_back;
	private TextView tv_home, tv_area;
	private EditText et_name, et_addr;
	private Button btn_save, btn_del;

	private String areaId = "";
	private String areaName = "";
	private String address = "";
	private String cityId = "";
	private String cityName = "";
	private String provinceName = "";
	private String provinceId = "";
//	private String areaStr = "";
	private String currArea = "";

	private List<AreaListBean> provinceList;
	private List<AreaListBean> cityList;
	private List<AreaListBean> areaList;

	private String[] mHomeDatas;
	private String[] mProvinceDatas;
	private String[] mCityDatas;
	private String[] mAreaDatas;

	String token;

	// 可用家庭列表
	private List<HomeListBean> homeList;
	private String homeId = "";
	private String homeName = "";
//	private String typeKey = "";
//	private String typeName = "";

	private LoadDialog mLoadDialog;

	@Override
	public int getLayoutResId() {
		return R.layout.activity_edit_user;
	}

	@Override
	public void initView() {
		token = sp.getString(ConstantValues.TOKEN, "");
		homeId = getIntent().getStringExtra("homeId");
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_right = (TextView) findViewById(R.id.btn_right);
		iv_back = (ImageView) findViewById(R.id.iv_back);
		tv_home = (TextView) findViewById(R.id.tv_home);
		tv_area = (TextView) findViewById(R.id.tv_area);
		et_name = (EditText) findViewById(R.id.et_name);
		et_addr = (EditText) findViewById(R.id.et_addr);
		btn_del = (Button) findViewById(R.id.btn_del);
		btn_save = (Button) findViewById(R.id.btn_save);

	}

	@Override
	public void initListener() {
		iv_back.setOnClickListener(this);
		tv_home.setOnClickListener(this);
		tv_right.setOnClickListener(this);
		tv_area.setOnClickListener(this);
		btn_del.setOnClickListener(this);
		btn_save.setOnClickListener(this);
	}

	@Override
	public void initData() {
		boolean canAdd = getIntent().getBooleanExtra("canAdd",false);
		if (canAdd){
			tv_right.setVisibility(View.VISIBLE);
			tv_right.setText("新增家庭");
		}else{
			tv_right.setVisibility(View.GONE);
		}
		tv_title.setText("家庭管理");
		getHomeInfo();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			finish();
			break;
		case R.id.btn_right:
			startActivity(new Intent(context, AddUserActivity.class));
			break;
		case R.id.tv_home:
			getHomeList();
			break;
		case R.id.tv_area:
//			currArea = "";
			getArea("", 1);
			break;
		case R.id.btn_save:
			if (TextUtils.isEmpty(currArea)) {
				ToastUtil.showTextToast(context, "请先选择所在地区");
				return;
			}
			address = et_addr.getText().toString().trim();
			if (TextUtils.isEmpty(address)) {
				ToastUtil.showTextToast(context, "请输入详细地址！");
				et_addr.requestFocus();
				return;
			}
			saveHome();
			break;
		case R.id.btn_del:
			SweetAlertDialog dialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE);
			dialog.setTitleText("确定要删除家庭？")
			.showCancelButton(true)
			.setCancelText("取消")
			.setConfirmText("删除")
			.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
				
				@Override
				public void onClick(SweetAlertDialog sweetAlertDialog) {
					sweetAlertDialog.dismiss();
					delHome();
				}
			})
			.show();
			break;

		default:
			break;
		}
	}

	// 获取家庭信息
	private void getHomeInfo() {
		mLoadDialog = DialogUtil.getInstance().showLoadDialog(context, "");
		Map<String, String> params = new HashMap<>();
		params.put("token", token);
		params.put("id", homeId);
		StringCallback callback = new StringCallback() {
			
			@Override
			public void onResponse(String arg0, int arg1) {
				DialogUtil.getInstance().dialogDismiss(mLoadDialog);

				HomeInfoResultBean homeResultBean = null;
				Gson gson = new Gson();
				try {
					homeResultBean = gson.fromJson(arg0,
							HomeInfoResultBean.class);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (homeResultBean == null) {
					ToastUtil.showTextToast(context, "获取家庭失败！");
				} else {
					if (homeResultBean.success) {
						if (!TextUtils.isEmpty(homeResultBean.info.ADDRESS)) {
							address = homeResultBean.info.ADDRESS;
						}
						if (!TextUtils.isEmpty(homeResultBean.info.AREA_ID)) {
							areaId = homeResultBean.info.AREA_ID;
						}
						if (!TextUtils.isEmpty(homeResultBean.info.AREA_NAME)) {
							areaName = homeResultBean.info.AREA_NAME;
						}
						if (!TextUtils.isEmpty(homeResultBean.info.CITY_ID)) {
							cityId = homeResultBean.info.CITY_ID;
						}
						if (!TextUtils.isEmpty(homeResultBean.info.CITY_NAME)) {
							cityName = homeResultBean.info.CITY_NAME;
						}
						if (!TextUtils.isEmpty(homeResultBean.info.PROVINCE_ID)) {
							provinceId = homeResultBean.info.PROVINCE_ID;
						}
						if (!TextUtils.isEmpty(homeResultBean.info.PROVINCE_NAME)) {
							provinceName = homeResultBean.info.PROVINCE_NAME;
						}
						if (!TextUtils.isEmpty(homeResultBean.info.HOME_NAME)) {
							homeName = homeResultBean.info.HOME_NAME;
						}
						if (!TextUtils.isEmpty(homeResultBean.info.ID)) {
							homeId = homeResultBean.info.ID;
						}
						currArea = provinceName + cityName + areaName;
						// 设置信息
						tv_home.setText(homeName);
//						if (!TextUtils.isEmpty(homeName)) {
//							tv_home.setText(homeName);
//						}
						tv_area.setText(currArea);
						if (!TextUtils.isEmpty(address)) {
							et_addr.setText(address);
						}
					} else {
						if (homeResultBean.flag == 10) {
							mApplication.login();
						} else {
							ToastUtil.showTextToast(context, homeResultBean.msg);
						}
					}
				}

			
			}
			
			@Override
			public void onError(Call arg0, Exception arg1, int arg2) {
				DialogUtil.getInstance().dialogDismiss(mLoadDialog);
			}
		};
		DataUtil.doPostAESData(mLoadDialog,context,ConstantValues.GET_HOME_DETAIL_URL, params, callback);

	}

	// 获取家庭集合
	private void getHomeList() {
		mLoadDialog = DialogUtil.getInstance().showLoadDialog(context, "");
		Map<String, String> params = new HashMap<>();
		params.put("token", token);
		StringCallback callback = new StringCallback() {
			
			@Override
			public void onResponse(String arg0, int arg1) {
				DialogUtil.getInstance().dialogDismiss(mLoadDialog);

				HomeResultBean homeResultBean = null;
				Gson gson = new Gson();
				try {
					homeResultBean = gson.fromJson(arg0,
							HomeResultBean.class);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (homeResultBean == null) {
					ToastUtil.showTextToast(context, "获取家庭失败！");
				} else {
					if (homeResultBean.success) {
						homeList = homeResultBean.homeList;
						if (homeList != null && homeList.size() > 0) {
							mHomeDatas = new String[homeList.size()];
							for (int i = 0; i < homeList.size(); i++) {
								mHomeDatas[i] = homeList.get(i).HOME_NAME;
							}
							chioceHome(mHomeDatas);
						}else{
							ToastUtils.showShortToast("无其他可用家庭！");
						}
					} else {
						if (homeResultBean.flag == 10) {
							mApplication.login();
						} else {
							ToastUtil.showTextToast(context, homeResultBean.msg);
						}
					}
				}
			}
			
			@Override
			public void onError(Call arg0, Exception arg1, int arg2) {
				DialogUtil.getInstance().dialogDismiss(mLoadDialog);
				ToastUtil.showTextToast(context, "获取家庭失败！");
			}
		};
		DataUtil.doPostAESData(mLoadDialog,context,ConstantValues.GET_ADD_HOME_URL, params, callback);
	}

	// 获取地区
	private void getArea(final String upId, final int type) {
		mLoadDialog = DialogUtil.getInstance().showLoadDialog(context, "");
		Map<String, String> params = new HashMap<>();
		params.put("pId", upId);
		StringCallback callback = new StringCallback() {
			
			@Override
			public void onResponse(String arg0, int arg1) {
				DialogUtil.getInstance().dialogDismiss(mLoadDialog);

				Gson gson = new Gson();
				HBAreaResaultBean areaResult = null;
				try {
					areaResult = gson.fromJson(
							arg0, HBAreaResaultBean.class);
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
						provinceList = areaResult.araList;
						if (provinceList != null
								&& provinceList.size() > 0) {
							mProvinceDatas = new String[provinceList
									.size()];
							for (int i = 0; i < provinceList.size(); i++) {
								mProvinceDatas[i] = provinceList.get(i).NAME;
							}
							chioceItem("一级区域", mProvinceDatas, 1);
						}

						break;
					case 2:
						cityList = areaResult.araList;
						if (areaResult.araList != null
								&& areaResult.araList.size() > 0) {
							mCityDatas = new String[cityList.size()];
							for (int i = 0; i < cityList.size(); i++) {
								mCityDatas[i] = cityList.get(i).NAME;
							}
							chioceItem("二级区域", mCityDatas, 2);
						}

						break;
					case 3:
						areaList = areaResult.araList;
						if (areaResult.araList != null
								&& areaResult.araList.size() > 0) {
							mAreaDatas = new String[areaList.size()];
							for (int i = 0; i < areaList.size(); i++) {
								mAreaDatas[i] = areaList.get(i).NAME;
							}
							chioceItem("三级区域", mAreaDatas, 3);
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
				DialogUtil.getInstance().dialogDismiss(mLoadDialog);
				
			}
		};
		DataUtil.doPostAESData(mLoadDialog,context,ConstantValues.GET_HB_AREA_URL, params, callback);
	}

	// 保存家庭
	private void saveHome() {
		mLoadDialog = DialogUtil.getInstance().showLoadDialog(context, "");
		Map<String, String> params = new HashMap<>();
		params.put("token", token);
		// params.put("typeKey", typeKey);
		params.put("typeName", homeName);
		params.put("id", homeId);
		params.put("homeName", homeName);
		params.put("provinceId", provinceId);
		params.put("provinceName", provinceName);
		params.put("cityId", cityId);
		params.put("cityName", cityName);
		params.put("areaId", areaId);
		params.put("areaName", areaName);
		params.put("address", address);
		StringCallback callback = new StringCallback() {
			
			@Override
			public void onResponse(String arg0, int arg1) {
				DialogUtil.getInstance().dialogDismiss(mLoadDialog);
				BaseResultBean homeResultBean = null;
				Gson gson = new Gson();
				try {
					homeResultBean = gson.fromJson(arg0,
							BaseResultBean.class);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (homeResultBean == null) {
					ToastUtil.showTextToast(context, "保存家庭失败！");
				} else {
					if (homeResultBean.success) {
						final SweetAlertDialog dialog = new SweetAlertDialog(
								context, SweetAlertDialog.SUCCESS_TYPE);
						dialog.setTitleText("保存家庭成功！")
						.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
							
							@Override
							public void onClick(SweetAlertDialog sweetAlertDialog) {
								dialog.dismiss();
								finish();
								mApplication.finishActivity("SHJFActivity");
								startActivity(new Intent(context, SHJFActivity.class));
							}
						})
						.show();
					} else {
						if (homeResultBean.flag == 10) {
							mApplication.login();
						} else {
							ToastUtil.showTextToast(context, homeResultBean.msg);
						}
					}
				}
			}
			
			@Override
			public void onError(Call arg0, Exception arg1, int arg2) {
				DialogUtil.getInstance().dialogDismiss(mLoadDialog);
				ToastUtil.showTextToast(context, "保存家庭失败！");
			}
		};
		DataUtil.doPostAESData(mLoadDialog,context,ConstantValues.UPDATE_HOME_URL, params, callback);
	}

	// 删除家庭
	private void delHome() {
		mLoadDialog = DialogUtil.getInstance().showLoadDialog(context, "");
		Map<String, String> params = new HashMap<>();
		params.put("token", token);
		params.put("id", homeId);
		StringCallback callback = new StringCallback() {
			
			@Override
			public void onResponse(String arg0, int arg1) {
				DialogUtil.getInstance().dialogDismiss(mLoadDialog);
				BaseResultBean homeResultBean = null;
				Gson gson = new Gson();
				try {
					homeResultBean = gson.fromJson(arg0,
							BaseResultBean.class);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (homeResultBean == null) {
					ToastUtil.showTextToast(context, "删除家庭失败！");
				} else {
					if (homeResultBean.success) {
						SweetAlertDialog dialog = new SweetAlertDialog(
								context, SweetAlertDialog.SUCCESS_TYPE);
						dialog.setTitleText("删除成功！")
								.setConfirmClickListener(
										new SweetAlertDialog.OnSweetClickListener() {

											@Override
											public void onClick(
													SweetAlertDialog sweetAlertDialog) {
												sweetAlertDialog.dismiss();
												finish();
												mApplication.finishActivity("SHJFActivity");
												startActivity(new Intent(context, SHJFActivity.class));
											}
										}).show();
					} else {
						if (homeResultBean.flag == 10) {
							mApplication.login();
						} else {
							ToastUtil.showTextToast(context, homeResultBean.msg);
						}
					}
				}
			}
			
			@Override
			public void onError(Call arg0, Exception arg1, int arg2) {
				DialogUtil.getInstance().dialogDismiss(mLoadDialog);
				ToastUtil.showTextToast(context, "删除家庭失败！");
			}
		};
		DataUtil.doPostAESData(mLoadDialog,context,ConstantValues.DELETE_HOME_URL, params, callback);
	}

	// 选择家庭
	private void chioceHome(final String[] itemDatas) {

		Builder builder = new Builder(this);
		builder.setTitle("请选家庭");
		builder.setItems(itemDatas, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				homeName = homeList.get(which).HOME_NAME;
//				typeKey = homeList.get(which).TYPE_KEY;
				tv_home.setText(homeName);
			}
		});
		builder.show();

	}

	// 选择地区
	private void chioceItem(String title, final String[] itemDatas,
			final int type) {

		Builder builder = new Builder(this);
		builder.setTitle("请选" + title);
		builder.setItems(itemDatas, new DialogInterface.OnClickListener() {

			/*
			 * 第一个参数代表对话框对象 第二个参数是点击对象的索引
			 */
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (type == 1) {
					currArea = "";
					provinceId = "";
					cityId = "";
					areaId = "";
				}
				currArea += itemDatas[which];
				tv_area.setText(currArea);
				switch (type) {
				case 1:
					provinceName = itemDatas[which];
					provinceId = provinceList.get(which).ID;
					getArea(provinceId, 2);
					break;
				case 2:
					cityName = itemDatas[which];
					cityId = cityList.get(which).ID;
					getArea(cityId, 3);
					break;
				case 3:
					areaName = itemDatas[which];
					areaId = areaList.get(which).ID;
					// if (currArea.equals(areaStr)) {
					//
					// // areaChange = false;
					// } else {
					//
					// // areaChange = true;
					// }
					break;

				default:
					break;
				}
			}
		});
		builder.show();

	}

}
