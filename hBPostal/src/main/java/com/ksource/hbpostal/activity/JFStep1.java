package com.ksource.hbpostal.activity;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ksource.hbpostal.R;
import com.ksource.hbpostal.bean.AccountResultBean;
import com.ksource.hbpostal.bean.BaseResultBean;
import com.ksource.hbpostal.bean.CompanyResultBean;
import com.ksource.hbpostal.bean.CompanyResultBean.CompanyListBean;
import com.ksource.hbpostal.config.ConstantValues;
import com.ksource.hbpostal.util.DataUtil;
import com.yitao.dialog.LoadDialog;
import com.yitao.util.DialogUtil;
import com.yitao.util.ToastUtil;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * 生活缴费，未绑定账户，添加账户
 */
public class JFStep1 extends BaseActivity {

    private TextView tv_title;
    private ImageView iv_back;
    private TextView tv_jf_name, tv_home, tv_company, tv_xieyi, tv_hint,
            tv_num_name, tv_company_name;
    private EditText et_user_number;
    private ImageView iv_jf_icon;
    private CheckBox cb_agree;
    private Button btn_next;

    private boolean isAgree = true;
    private LoadDialog mLoadDialog;
    private int accountType;
    private String token;
    private String homeId = "";
    private String homeName = "";
    // private String typeKey = "";
    // private String typeName = "";
    private String payUnitKey = "";
    private String payUnitName = "";
    private String accountNumber = "";
    // private String accountName = "";

    // private int type;

    // 可用家庭列表
//	private List<HomeListBean> homeList;
//	private String[] mHomeDatas;
    private List<CompanyListBean> companyList;
    private String[] mCompanyDatas;
    private boolean isEdit;
    private String accountId;
    private boolean isChioce = false;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_jf_step1;
    }

    @Override
    public void initView() {

        // 头部
        tv_title = (TextView) findViewById(R.id.tv_title);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        // 内容
        tv_jf_name = (TextView) findViewById(R.id.tv_jf_name);
        tv_company = (TextView) findViewById(R.id.tv_company);
        tv_home = (TextView) findViewById(R.id.tv_home);
        tv_xieyi = (TextView) findViewById(R.id.tv_xieyi);
        tv_company_name = (TextView) findViewById(R.id.tv_company_name);
        tv_num_name = (TextView) findViewById(R.id.tv_num_name);
        tv_hint = (TextView) findViewById(R.id.tv_hint);
        et_user_number = (EditText) findViewById(R.id.et_user_number);
        iv_jf_icon = (ImageView) findViewById(R.id.iv_jf_icon);
        cb_agree = (CheckBox) findViewById(R.id.cb_agree);
        btn_next = (Button) findViewById(R.id.btn_next);

    }

    @Override
    public void initListener() {
        iv_back.setOnClickListener(this);
        btn_next.setOnClickListener(this);
        // tv_home.setOnClickListener(this);
        tv_company.setOnClickListener(this);
        tv_xieyi.setOnClickListener(this);
        cb_agree.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                isAgree = isChecked;
                btn_next.setEnabled(isAgree);
            }
        });
    }

    @Override
    public void initData() {
        token = sp.getString(ConstantValues.TOKEN, null);
        Intent intent = getIntent();
        if (intent.hasExtra("accountType")) {
            accountType = intent.getIntExtra("accountType", 1);
            switch (accountType) {
                case 1:
                    iv_jf_icon.setImageResource(R.drawable.life_icon_water_bg);
                    tv_jf_name.setText("水费");
                    break;
                case 2:
                    iv_jf_icon.setImageResource(R.drawable.life_icon_ele_bg);
                    tv_jf_name.setText("电费");
                    break;
                case 3:
                    iv_jf_icon.setImageResource(R.drawable.life_icon_gas_bg);
                    tv_jf_name.setText("燃气费");
                    break;
                case 4:
                    iv_jf_icon.setImageResource(R.drawable.life_icon_tv_bg);
                    payUnitKey = "21A";
                    payUnitName = "身份证";
                    tv_jf_name.setText("有线电视");
                    tv_company_name.setText("证件类型");
                    tv_num_name.setText("身份证号码");
                    tv_company.setText(payUnitName);
                    break;
                case 5:
                    iv_jf_icon.setImageResource(R.drawable.life_icon_broad_bg);
                    tv_jf_name.setText("固话宽带");
                    tv_num_name.setText("固话号码");
                    break;

                default:
                    break;
            }
            if (intent.hasExtra("numName")) {
                tv_num_name.setText(intent.getStringExtra("numName"));
            }
            if (intent.hasExtra("hint")) {
                tv_hint.setText(intent.getStringExtra("hint"));
                tv_hint.setVisibility(View.VISIBLE);
            } else {
                tv_hint.setVisibility(View.GONE);
            }
            isEdit = intent.hasExtra("bindNum");
            if (isEdit) {
                isChioce = true;
                accountNumber = intent.getStringExtra("bindNum");
                payUnitKey = intent.getStringExtra("payUnitKey");
                payUnitName = intent.getStringExtra("payUnitName");
//			if (payUnitKey.equals("ZGDX")) {
//				payUnitName = "中国电信";
//			} else if (payUnitKey.equals("ZGLT")) {
//				payUnitName = "中国联通";
//			}
                payUnitName = payUnitName == null ? "" : payUnitName;
                accountId = intent.getStringExtra("accountId");
                tv_title.setText("修改缴费账户");
                tv_company.setText(payUnitName);
            } else {
                tv_title.setText("新增缴费账户");
                if (accountType != 4)
                    getCompanyList();
            }

        }
        if (intent.hasExtra("homeId")) {
            homeId = intent.getStringExtra("homeId");
        }
        if (intent.hasExtra("homeName")) {
            homeName = intent.getStringExtra("homeName");
            tv_home.setText(homeName);
        }
        if (!TextUtils.isEmpty(accountNumber))
            et_user_number.setText(accountNumber);
        // tv_company.setText("鹤壁燃气公司");
        cb_agree.setChecked(isAgree);
        btn_next.setEnabled(isAgree);

    }


    // 获取缴费账户信息
    private void getCompanyList() {
        mLoadDialog = DialogUtil.getInstance().showLoadDialog(context, "");
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", token);
        params.put("accountType", accountType + "");
        StringCallback callback = new StringCallback() {

            @Override
            public void onResponse(String arg0, int arg1) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                CompanyResultBean ResultBean = null;
                Gson gson = new Gson();
                try {
                    ResultBean = gson.fromJson(arg0,
                            CompanyResultBean.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (ResultBean == null) {
                    ToastUtil.showTextToast(context, "获取信息失败！");
                } else {
                    if (ResultBean.success) {
                        companyList = ResultBean.companyList;
                        if (companyList != null
                                && companyList.size() > 0) {
                            mCompanyDatas = new String[companyList
                                    .size()];
                            for (int i = 0; i < companyList.size(); i++) {
                                mCompanyDatas[i] = companyList.get(i).DATAVALUE;
                            }
                        }
                        if (isChioce) {
                            chioceCompany(mCompanyDatas);
                        } else {
                            isChioce = true;
                            payUnitKey = companyList.get(0).DATAKEY;
                            payUnitName = companyList.get(0).DATAVALUE;
                            tv_company.setText(payUnitName);
                        }
                    } else {
                        if (ResultBean.flag == 10) {
                            mApplication.login();
                        } else {
                            ToastUtil.showTextToast(context, "获取家庭失败！");
                        }
                    }
                }
            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                ToastUtil.showTextToast(context, "获取信息失败！");
            }
        };
        DataUtil.doPostAESData(mLoadDialog,context,ConstantValues.GET_COMPANY_LIST_URL, params, callback);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_home:
                // getHomeList();
                break;
            case R.id.tv_xieyi:
                Intent intent = new Intent(context, BaseHtmlActivity.class);
                intent.putExtra("title", "自助缴费服务协议");
                intent.putExtra("url", ConstantValues.JF_XIEYI_HTML_URL);
                startActivity(intent);
                break;
            case R.id.tv_company:
                if (accountType == 4) {
                    String[] itemDatas = {"身份证", "军官证", "企业执照", "护照", "组织机构代码证"};
                    chioceCompany(itemDatas);
                } else {
                    getCompanyList();
                }
                break;
            case R.id.btn_next:
                if (TextUtils.isEmpty(tv_home.getText().toString().trim())) {
                    ToastUtil.showTextToast(context, "请选择家庭");
                    return;
                }
                if (TextUtils.isEmpty(payUnitName)) {
                    ToastUtil.showTextToast(context, "请选择公司");
                    getCompanyList();
                    return;
                }
                if (TextUtils.isEmpty(et_user_number.getText().toString().trim())) {
                    ToastUtil.showTextToast(context, "请输入户号");
                    et_user_number.requestFocus();
                    return;
                }
                accountNumber = et_user_number.getText().toString().trim();
                // accountName = "hehe";
                // payUnitKey = "0";
                // payUnitName = "鹤壁市自来水公司";
                if (isEdit) {
                    updateHome();
                } else {
                    addHome();
                }
                // Intent intent = new Intent(context, JFStep2.class);
                // startActivity(intent);
                break;

            default:
                break;
        }
    }

    // 添加缴费账户
    private void addHome() {
        mLoadDialog = DialogUtil.getInstance().showLoadDialog(context, "");
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", token);
        params.put("accountType", accountType + "");
        params.put("homeId", homeId);
        if (accountType == 4) {
            params.put("gd_papertype", payUnitKey);
            params.put("gd_paperno", accountNumber);
            params.put("payUnitKey", payUnitKey);
            params.put("payUnitName", payUnitName);
        } else {
            params.put("payUnitKey", payUnitKey);
            params.put("payUnitName", payUnitName);
        }
        params.put("accountNumber", accountNumber);
        // params.put("accountName", accountName);
        // if (!TextUtils.isEmpty(accountNumber)) {
        // params.put("id", accountNumber);
        // }
        StringCallback callback = new StringCallback() {

            @Override
            public void onResponse(String arg0, int arg1) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);

                AccountResultBean accountResultBean = null;
                Gson gson = new Gson();
                try {
                    accountResultBean = gson.fromJson(arg0,
                            AccountResultBean.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (accountResultBean == null) {
                    ToastUtil.showTextToast(context, "添加缴费账户失败！");
                } else {
                    if (accountResultBean.success) {
                        accountId = accountResultBean.accountId;
                        if (accountType == 5) {
                            Intent intent = new Intent(context, SJCZActivity.class);
                            if (payUnitKey.equals("ZGDX")) {
                                intent.putExtra("operator", 5);
                            } else if (payUnitKey.equals("ZGLT")) {
                                intent.putExtra("operator", 4);
                            }
                            intent.putExtra("accountNumber", accountNumber);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(
                                    context,
                                    JFStep2.class);
                            intent.putExtra(
                                    "accountType",
                                    accountType);
                            intent.putExtra(
                                    "accountId",
                                    accountId);
                            startActivity(intent);
                        }
                        finish();
                    } else {
                        if (accountResultBean.flag == 10) {
                            mApplication.login();
                        } else {
                            ToastUtil.showTextToast(context,
                                    accountResultBean.msg);
                        }
                    }
                }

            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                ToastUtil.showTextToast(context, "添加缴费账户失败！");
            }
        };
        DataUtil.doPostAESData(mLoadDialog,context,ConstantValues.ADD_JF_ACCOUNT_URL, params, callback);
    }

    // 修改缴费账户
    private void updateHome() {
        mLoadDialog = DialogUtil.getInstance().showLoadDialog(context, "");
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", token);
        params.put("accountType", accountType + "");
        params.put("homeId", homeId);
        if (accountType == 4) {
            params.put("gd_papertype", payUnitKey);
            params.put("gd_paperno", accountNumber);
            params.put("payUnitKey", payUnitKey);
            params.put("payUnitName", payUnitName);
        } else {
            params.put("payUnitKey", payUnitKey);
            params.put("payUnitName", payUnitName);
        }
        params.put("accountNumber", accountNumber);
        // params.put("accountName", accountName);
        params.put("id", accountId);
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
                    ToastUtil.showTextToast(context, "修改缴费账户失败！");
                } else {
                    if (homeResultBean.success) {
                        ToastUtil.showTextToast(context, "修改缴费账户成功！");
                        finish();
                    } else {
                        if (homeResultBean.flag == 10) {
                            mApplication.login();
                        } else {
                            ToastUtil.showTextToast(context,
                                    homeResultBean.msg);
                        }
                    }
                }


            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
                DialogUtil.getInstance().dialogDismiss(mLoadDialog);
                ToastUtil.showTextToast(context, "修改缴费账户失败！");
            }
        };
        DataUtil.doPostAESData(mLoadDialog,context,ConstantValues.UPDATE_JF_ACCOUNT_URL, params, callback);
    }


    // 选择公司
    private void chioceCompany(final String[] itemDatas) {

        Builder builder = new Builder(this);
        if (accountType == 4) {
            builder.setTitle("请选证件类型");
        } else {
            builder.setTitle("请选缴费单位");
        }
        builder.setItems(itemDatas, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (accountType == 4) {
                    payUnitName = itemDatas[which];
                    switch (which) {
                        case 0:
                            payUnitKey = "21A";
                            break;
                        case 1:
                            payUnitKey = "21C";
                            break;
                        case 2:
                            payUnitKey = "21D";
                            break;
                        case 3:
                            payUnitKey = "21E";
                            break;
                        case 4:
                            payUnitKey = "21F";
                            break;
                    }
                    tv_num_name.setText(payUnitName + "号码");
                } else {
                    payUnitKey = companyList.get(which).DATAKEY;
                    payUnitName = companyList.get(which).DATAVALUE;
                }
                tv_company.setText(payUnitName);
            }
        });
        builder.show();

    }

    // 选择家庭
//		private void chioceHome(final String[] itemDatas) {
//
//			Builder builder = new Builder(this);
//			builder.setTitle("请选家庭");
//			builder.setItems(itemDatas, new DialogInterface.OnClickListener() {
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					homeName = homeList.get(which).HOME_NAME;
//					// typeKey = homeList.get(which).TYPE_KEY;
//					tv_home.setText(homeName);
//				}
//			});
//			builder.show();
//
//		}

    // 获取缴费账户信息
//	private void getHomeList() {
//		mLoadDialog = DialogUtil.getInstance().showLoadDialog(context, "");
//		new Thread() {
//			public void run() {
//				Map<String, String> params = new HashMap<String, String>();
//				params.put("token", token);
//				params.put("accountType", accountType + "");
//				final String homeResult = HttpUtil.doPost(
//						ConstantValues.GET_HOME_INFO_URL, params,
//						HttpCharset.UTF8);
//				DialogUtil.getInstance().dialogDismiss(mLoadDialog);
//				runOnUiThread(new Runnable() {
//					@Override
//					public void run() {
//						HomeResultBean homeResultBean = null;
//						Gson gson = new Gson();
//						try {
//							homeResultBean = gson.fromJson(homeResult,
//									HomeResultBean.class);
//						} catch (Exception e) {
//							e.printStackTrace();
//						}
//						if (homeResultBean == null) {
//							ToastUtil.showTextToast(context, "获取信息失败！");
//						} else {
//							if (homeResultBean.success) {
//								homeList = homeResultBean.homeList;
//								if (homeList != null && homeList.size() > 0) {
//									mHomeDatas = new String[homeList.size()];
//									for (int i = 0; i < homeList.size(); i++) {
//										mHomeDatas[i] = homeList.get(i).HOME_NAME;
//									}
//								}
//								chioceHome(mHomeDatas);
//							} else {
//								if (homeResultBean.flag == 10) {
//									mApplication.login();
//								} else {
//									ToastUtil.showTextToast(context, "获取家庭失败！");
//								}
//							}
//						}
//					}
//				});
//			};
//		}.start();
//
//	}

}
