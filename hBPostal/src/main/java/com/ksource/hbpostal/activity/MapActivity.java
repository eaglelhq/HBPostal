package com.ksource.hbpostal.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.district.DistrictResult;
import com.baidu.mapapi.search.district.DistrictSearch;
import com.baidu.mapapi.search.district.DistrictSearchOption;
import com.baidu.mapapi.search.district.OnGetDistricSearchResultListener;
import com.baidu.navisdk.adapter.BNCommonSettingParam;
import com.baidu.navisdk.adapter.BNOuterTTSPlayerCallback;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BNaviSettingManager;
import com.baidu.navisdk.adapter.BaiduNaviManager;
import com.blankj.utilcode.utils.ConvertUtils;
import com.blankj.utilcode.utils.LogUtils;
import com.blankj.utilcode.utils.PhoneUtils;
import com.blankj.utilcode.utils.ScreenUtils;
import com.blankj.utilcode.utils.ToastUtils;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.ksource.hbpostal.R;
import com.ksource.hbpostal.adapter.DefaultBaseAdapter;
import com.ksource.hbpostal.bean.AreaResaultBean;
import com.ksource.hbpostal.bean.MapDataBean;
import com.ksource.hbpostal.bean.PiontInfoBean;
import com.ksource.hbpostal.config.ConstantValues;
import com.ksource.hbpostal.util.DataUtil;
import com.yinglan.scrolllayout.ScrollLayout;
import com.yinglan.scrolllayout.content.ContentListView;
import com.yitao.util.ConvertUtil;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.jeesoft.widget.pickerview.CharacterPickerView;
import cn.jeesoft.widget.pickerview.OnOptionChangedListener;
import okhttp3.Call;

import static com.baidu.navisdk.adapter.PackageUtil.getSdcardDir;

/**
 * 服务导航-地图页面
 */
public class MapActivity extends BaseActivity implements OnGetDistricSearchResultListener,
        OnClickListener {


    DrawerLayout dl;
    LinearLayout rlRight;
    //鹤壁市邮政大厦  114.290683,35.756217
    protected float lat = 35.756217f;
    protected float lon = 114.290683f;
    protected LatLng mCurrentPos = new LatLng(lat, lon);

    LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    //    private LocationClientOption.LocationMode mCurrentMode;
    boolean isFirstLoc = false; // 是否首次定位
    private LatLng locLL;//定位坐标
    private String mSDCardPath = null;

    private ImageView iv_back, iv_search;
    private ImageView btn_loc;
    private EditText et_search;
    private TextView tv_title;
    private TextView tv_filter;
    private RadioGroup rg_check;
    private Button btn_reset, btn_finish;
    private CheckBox cb_show_zj, cb_show_wd;
    private RelativeLayout rl_pop;
    private ContentListView listView, listView2, listView3;
    //private TextView tv_show_list;
    private ScrollLayout mScrollLayout;

    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private LinearLayout cpv_area, cpv_org;
    private CharacterPickerView cpv_area1, cpv_area2, cpv_area3, cpv_org1, cpv_org2;

    private static final String APP_FOLDER_NAME = "BNSDK";
    public static final String ROUTE_PLAN_NODE = "routePlanNode";
    private static final String[] authBaseArr = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION};
    private static final String[] authComArr = {Manifest.permission.READ_PHONE_STATE};
    private static final int authBaseRequestCode = 1;
    private static final int authComRequestCode = 2;

    private boolean hasInitSuccess = false;
    private boolean hasRequestComAuth = false;
    //    private CustomPopWindow mListPopWindow;
    private List<MapDataBean.DatasBean> mapList;
    private boolean isZj, isWd;
    private List<AreaResaultBean.DatasBean> areaList1, areaList2, areaList3;
    private List<AreaResaultBean.DatasBean> orgList1, orgList2;
    private ArrayList<String> areaStrList1, areaStrList2, areaStrList3;
    private ArrayList<String> orgStrList1, orgStrList2;
    private String area1Id, area2Id, area3Id;
    private String org1Id, org2Id;
    private InfoWindow mInfoWindow;
    private DistrictSearch mDistrictSearch;
    private InputMethodManager imm;
    private TextView tv_pt_name;
    private TextView tv_name;
    private TextView tv_phone;
    private TextView tv_addr;
    private ImageView iv_goto;
    private Button btn_navi;
    private LinearLayout ll_tt;
    private TextView tv_shadow, tv_back;
    private MyAdapter adapter;
    private int state = 0;//网点显示状态  0-仅一级机构 1-一二级机构 2-二级机构和二级机构下网点  3-仅网点
    private Animation animation;
    private OrgAdapter orgAdapter;
    private OrgAdapter orgAdapter1;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_map;
    }

    @Override
    public void initView() {
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mDistrictSearch = DistrictSearch.newInstance();
        mDistrictSearch.setOnDistrictSearchListener(this);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_search = (ImageView) findViewById(R.id.iv_search);
        et_search = (EditText) findViewById(R.id.et_search);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_filter = (TextView) findViewById(R.id.tv_filter);
        btn_reset = (Button) findViewById(R.id.btn_reset);
        btn_finish = (Button) findViewById(R.id.btn_finish);
        mMapView = (MapView) findViewById(R.id.map_view);
        mBaiduMap = mMapView.getMap();
        dl = (DrawerLayout) findViewById(R.id.drawerlayout);
        rlRight = (LinearLayout) findViewById(R.id.right);
        rg_check = (RadioGroup) findViewById(R.id.rg_check);
        cpv_area = (LinearLayout) findViewById(R.id.cpv_area);
        cpv_org = (LinearLayout) findViewById(R.id.cpv_org);
        cpv_area1 = (CharacterPickerView) findViewById(R.id.cpv_area1);
        cpv_area2 = (CharacterPickerView) findViewById(R.id.cpv_area2);
        cpv_area3 = (CharacterPickerView) findViewById(R.id.cpv_area3);
        cpv_org1 = (CharacterPickerView) findViewById(R.id.cpv_org1);
        cpv_org2 = (CharacterPickerView) findViewById(R.id.cpv_org2);
        cb_show_zj = (CheckBox) findViewById(R.id.cb_show_zj);
        cb_show_wd = (CheckBox) findViewById(R.id.cb_show_wd);
        rl_pop = (RelativeLayout) findViewById(R.id.rl_pop);
        btn_loc = (ImageView) findViewById(R.id.btn_loc);
        listView = (ContentListView) findViewById(R.id.lv_addr);
        listView2 = (ContentListView) findViewById(R.id.lv_addr2);
        listView3 = (ContentListView) findViewById(R.id.lv_addr3);
//        tv_show_list = (TextView) findViewById(R.id.tv_show_list);
        tv_shadow = (TextView) findViewById(R.id.tv_show_list2);
        tv_back = (TextView) findViewById(R.id.tv_back);
        mScrollLayout = (ScrollLayout) findViewById(R.id.scroll_down_layout);
        mScrollLayout.setToExit();

        tv_pt_name = (TextView) findViewById(R.id.tv_pt_name);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_phone = (TextView) findViewById(R.id.tv_phone);
        tv_addr = (TextView) findViewById(R.id.tv_addr);
        iv_goto = (ImageView) findViewById(R.id.iv_goto);
        btn_navi = (Button) findViewById(R.id.btn_navi);
//        imm.showSoftInput(et_search,InputMethodManager.SHOW_FORCED);
//        View view = LayoutInflater.from(this).inflate(R.layout.lv_head1, null);
//        View view2 = LayoutInflater.from(this).inflate(R.layout.lv_head2, null);
//        ll_tt = (LinearLayout) view2.findViewById(R.id.ll_tt);
        // 设置head1的高度
//        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams();
//        tv = (TextView) view2.findViewById(R.id.tv_tt);
//        listView.addHeaderView(view);
//        listView.addHeaderView(view2);

    }

    @Override
    public void initListener() {
        tv_filter.setOnClickListener(this);
        iv_search.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        btn_reset.setOnClickListener(this);
        btn_finish.setOnClickListener(this);
        btn_loc.setOnClickListener(this);
        rl_pop.setOnClickListener(this);
        tv_shadow.setOnClickListener(this);
        tv_back.setOnClickListener(this);
//        tv_show_list.setOnClickListener(this);
        listView.setDivider(null);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                rg_check.check(R.id.rb_org);
                area1Id = "";
                area2Id = "";
                area3Id = "";
                switch (state) {
                    case 0:
                        org1Id = orgList1.get(position).ID;
                        state = 1;
                        listView2.setVisibility(View.VISIBLE);
                        getOrg(org1Id, 2);
//                        if (orgList1 == null || orgList1.size() == 0) {
//                            ToastUtils.showShortToast("未查询到相关网点信息！");
//                        } else {
////                            orgAdapter = new OrgAdapter(orgList1);
////                            listView.setAdapter(orgAdapter);
//                        }
                        orgAdapter.notifyDataSetChanged();
                        break;
                    case 1:
                        org1Id = orgList1.get(position).ID;
//                        org2Id = orgList2.get(position).ID;
                        state = 1;
                        getOrg(org1Id, 2);
//                        listView.setVisibility(View.GONE);
//                        listView3.setVisibility(View.VISIBLE);
//                        if (orgList1 == null || orgList1.size() == 0) {
//                            ToastUtils.showShortToast("未查询到相关网点信息！");
//                        } else {
//                            listView.setAdapter(new OrgAdapter(orgList1));
//                            listView2.setAdapter(new OrgAdapter(orgList2));
//                        }
                        orgAdapter.notifyDataSetChanged();
                        break;
                    case 2:

                        break;
                    case 3:

                        break;
                }
            }
        });
        listView2.setDivider(null);
        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                rg_check.check(R.id.rb_org);
                area1Id = "";
                area2Id = "";
                area3Id = "";
                switch (state) {
                    case 0:
//                        org1Id = orgList1.get(position).ID;
//                        state = 1;
//                        getOrg(org1Id, 2);
//                        if (orgList1 == null || orgList1.size() == 0) {
//                            ToastUtils.showShortToast("未查询到相关网点信息！");
//                        } else {
//                            listView.setAdapter(new OrgAdapter(orgList1));
//                        }
                        break;
                    case 1:
                        org2Id = orgList2.get(position).ID;
                        state = 2;
                        listView.setVisibility(View.GONE);
//                        listView2.setAnimation(animation);
                        listView3.setVisibility(View.VISIBLE);
                        tv_back.setVisibility(View.VISIBLE);
                        searchPoint();
                        orgAdapter1.notifyDataSetChanged();
                        break;
                    case 2:
                        org2Id = orgList2.get(position).ID;
                        state = 2;
//                        getOrg(org1Id, 2);
                        listView.setVisibility(View.GONE);
                        searchPoint();
                        orgAdapter1.notifyDataSetChanged();
                        break;
                    case 3:

                        break;
                }
            }
        });
        listView3.setDivider(null);
        listView3.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                mScrollLayout.getBackground().setAlpha(0);
                mScrollLayout.setToExit();
                TextView button = new TextView(getApplicationContext());
                button.setBackgroundResource(R.drawable.popup);
                button.setText("  " + mapList.get(position).NAME + "  ");
                button.setTextColor(getResources().getColor(R.color.lightgreen));
                mInfoWindow = new InfoWindow(button, new LatLng(mapList.get(position).WD, mapList.get(position).JD), -47);
                mBaiduMap.showInfoWindow(mInfoWindow);
                getDetail(position + "");
            }
        });

        cb_show_zj.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isZj = isChecked;
            }
        });
        cb_show_wd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isWd = isChecked;
            }
        });

        rg_check.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.rb_area:
//                        ToastUtils.showShortToast("选择地区");
//                        OptionsWindowHelper.setThreePickerData(cpv_area, ArrayDataDemo.getAll());
                        cpv_org.setVisibility(View.GONE);
                        cpv_area.setVisibility(View.VISIBLE);
                        area1Id = "";
                        area2Id = "";
                        area3Id = "";
                        org1Id = "";
                        org2Id = "";
                        getAddr("", 1);
                        break;

                    case R.id.rb_org:
//                        ToastUtils.showShortToast("选择机构");
                        cpv_org.setVisibility(View.VISIBLE);
                        cpv_area.setVisibility(View.GONE);
                        area1Id = "";
                        area2Id = "";
                        area3Id = "";
                        org1Id = "";
                        org2Id = "";
                        getOrg("", 1);
                        break;
                }
            }
        });

        //设置监听事件
        cpv_org1.setOnOptionChangedListener(new OnOptionChangedListener() {
            @Override
            public void onOptionChanged(int option1, int option2, int option3) {
                if (orgList1 != null && orgList1.size() > 0) {
                    org1Id = orgList1.get(option1).ID;
                    getOrg(org1Id, 2);
                } else {
                    org1Id = "";
                }
            }
        });
        cpv_org2.setOnOptionChangedListener(new OnOptionChangedListener() {
            @Override
            public void onOptionChanged(int option1, int option2, int option3) {
                if (orgList2 != null && orgList2.size() > 0) {
                    org2Id = orgList2.get(option1).ID;
                } else {
                    org2Id = "";
                }
            }
        });
        cpv_area1.setOnOptionChangedListener(new OnOptionChangedListener() {
            @Override
            public void onOptionChanged(int option1, int option2, int option3) {
                if (areaList1 != null && areaList1.size() > 0) {
                    area1Id = areaList1.get(option1).ID;
                    getAddr(area1Id, 2);
                } else {
                    area1Id = "";
                }
            }
        });
        cpv_area2.setOnOptionChangedListener(new OnOptionChangedListener() {
            @Override
            public void onOptionChanged(int option1, int option2, int option3) {
                if (areaList2 != null && areaList2.size() > 0) {
                    area2Id = areaList2.get(option1).ID;
                    getAddr(area2Id, 3);
                } else {
                    area2Id = "";
                }
            }
        });
        cpv_area3.setOnOptionChangedListener(new OnOptionChangedListener() {
            @Override
            public void onOptionChanged(int option1, int option2, int option3) {
                if (areaList3 != null && areaList3.size() > 0) {
                    area3Id = areaList3.get(option1).ID;
                } else {
                    area3Id = "";
                }
            }
        });

        //侧滑菜单监听
        dl.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                drawerView.setClickable(true);
                // 打开手势滑动
//                dl.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                // 关闭手势滑动
                dl.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        //添加marker点击监听
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker clickMarker) {
//                mBaiduMap.showInfoWindow(new InfoWindow());
//                routeplanToNavi();
//                showPointInfo(clickMarker.getTitle());
                int j = Integer.parseInt(clickMarker.getTitle());
                TextView button = new TextView(getApplicationContext());
                button.setBackgroundResource(R.drawable.popup);
                button.setText("  " + mapList.get(j).NAME + "  ");
                button.setTextColor(getResources().getColor(R.color.lightgreen));
                mInfoWindow = new InfoWindow(button, new LatLng(mapList.get(j).WD, mapList.get(j).JD), -47);
                mBaiduMap.showInfoWindow(mInfoWindow);
                getDetail(clickMarker.getTitle());
                return false;
            }
        });
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                rl_pop.setVisibility(View.GONE);
                mBaiduMap.hideInfoWindow();
//                tv_show_list.setVisibility(View.VISIBLE);
                mScrollLayout.setVisibility(View.VISIBLE);
                mScrollLayout.setToExit();
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });

    }

    @Override
    public void initData() {
        tv_title.setText("便民服务站信息");
        mapList = new ArrayList<>();
        adapter = new MyAdapter(mapList);
//        listView3.setAdapter(adapter);

        orgList1 = new ArrayList<>();
        orgAdapter = new OrgAdapter(orgList1);
//        listView.setAdapter(orgAdapter);

        orgList2 = new ArrayList<>();
        orgAdapter1 = new OrgAdapter(orgList2);
//        listView2.setAdapter(orgAdapter1);
        // 关闭手势滑动
        dl.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        mDistrictSearch.searchDistrict(new DistrictSearchOption().cityName("鹤壁市"));
//        // 设置地图显示的中心位置
//        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(mCurrentPos));
//        // 设置缩放级别 3~20
//        mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(16));

        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 定位初始化
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();
        cb_show_zj.setChecked(true);
        cb_show_wd.setChecked(true);
//        initListener();
        if (initDirs()) {
            initNavi();
        }
        searchPoint();
        getOrg("", 1);
        /**设置 setting*/
        mScrollLayout.setMinOffset(0);
        mScrollLayout.setMaxOffset((int) (ScreenUtils.getScreenHeight() * 0.5));
        mScrollLayout.setExitOffset(ConvertUtils.dp2px(40));
        mScrollLayout.setIsSupportExit(true);
        mScrollLayout.setAllowHorizontalScroll(true);
        mScrollLayout.setOnScrollChangedListener(mOnScrollChangedListener);

        mScrollLayout.getBackground().setAlpha(0);
        mScrollLayout.setToExit();
        initAnim();
    }

    private void initAnim() {
        animation = new TranslateAnimation(0f, (float) (-ScreenUtils.getScreenWidth() * 0.5), 0f, 0f);
        animation.setDuration(200);
        animation.setFillAfter(true);
    }

    private boolean hasBasePhoneAuth() {
        PackageManager pm = this.getPackageManager();
        for (String auth : authBaseArr) {
            if (pm.checkPermission(auth, this.getPackageName()) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private boolean hasCompletePhoneAuth() {
        PackageManager pm = this.getPackageManager();
        for (String auth : authComArr) {
            if (pm.checkPermission(auth, this.getPackageName()) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    String authinfo = null;

    private void initNavi() {

        BNOuterTTSPlayerCallback ttsCallback = null;

        // 申请权限
        if (android.os.Build.VERSION.SDK_INT >= 23) {

            if (!hasBasePhoneAuth()) {

                this.requestPermissions(authBaseArr, authBaseRequestCode);
                return;

            }
        }

        BaiduNaviManager.getInstance().init(this, mSDCardPath, APP_FOLDER_NAME, new BaiduNaviManager.NaviInitListener() {
            @Override
            public void onAuthResult(int status, String msg) {
                if (0 == status) {
                    authinfo = "key校验成功!";
                } else {
                    authinfo = "key校验失败, " + msg;
                }
                LogUtils.e("BaiduNavi", authinfo);
            }

            public void initSuccess() {
//                Toast.makeText(context, "百度导航引擎初始化成功", Toast.LENGTH_SHORT).show();
                LogUtils.e("BaiduNavi", "百度导航引擎初始化成功");
                hasInitSuccess = true;
                initSetting();
            }

            public void initStart() {
//                Toast.makeText(context, "百度导航引擎初始化开始", Toast.LENGTH_SHORT).show();
                LogUtils.e("BaiduNavi", "百度导航引擎初始化开始");
            }

            public void initFailed() {
                LogUtils.e("BaiduNavi", "百度导航引擎初始化失败");
//                Toast.makeText(context, "百度导航引擎初始化失败", Toast.LENGTH_SHORT).show();
            }

        }, null, null, null);//ttsHandler, ttsPlayStateListener

    }

    private boolean initDirs() {
        mSDCardPath = getSdcardDir();
        if (mSDCardPath == null) {
            return false;
        }
        File f = new File(mSDCardPath, APP_FOLDER_NAME);
        if (!f.exists()) {
            try {
                f.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    private void initSetting() {
        // BNaviSettingManager.setDayNightMode(BNaviSettingManager.DayNightMode.DAY_NIGHT_MODE_DAY);
        BNaviSettingManager
                .setShowTotalRoadConditionBar(BNaviSettingManager.PreViewRoadCondition.ROAD_CONDITION_BAR_SHOW_ON);
        BNaviSettingManager.setVoiceMode(BNaviSettingManager.VoiceMode.Veteran);
        // BNaviSettingManager.setPowerSaveMode(BNaviSettingManager.PowerSaveMode.DISABLE_MODE);
        BNaviSettingManager.setRealRoadCondition(BNaviSettingManager.RealRoadCondition.NAVI_ITS_ON);
        Bundle bundle = new Bundle();
        // 必须设置APPID，否则会静音
        bundle.putString(BNCommonSettingParam.TTS_APP_ID, ConstantValues.TTS_APP_ID);
        BNaviSettingManager.setNaviSdkParam(bundle);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_search:
                state = 3;
//                listView.setVisibility(View.GONE);
//                listView2.setVisibility(View.GONE);
//                listView3.setVisibility(View.VISIBLE);
                tv_back.setVisibility(View.VISIBLE);
                mScrollLayout.setVisibility(View.VISIBLE);
                searchPoint();
                break;
            case R.id.tv_filter:
                if (!dl.isDrawerOpen(rlRight)) {
                    dl.openDrawer(rlRight);
                }
                break;
            case R.id.btn_reset:
                if (dl.isDrawerOpen(rlRight)) {
                    dl.closeDrawer(rlRight);
                }
                et_search.setText("");
                rg_check.clearCheck();
                cb_show_zj.setChecked(true);
                cb_show_wd.setChecked(true);
                cpv_area.setVisibility(View.GONE);
                cpv_org.setVisibility(View.GONE);
                state = 0;
                area1Id = "";
                area2Id = "";
                area3Id = "";
                org1Id = "";
                org2Id = "";
                if (orgList1 != null) {
                    orgList1.clear();
                }
                if (orgList2 != null) {
                    orgList2.clear();
                }
                orgAdapter.notifyDataSetChanged();
                orgAdapter1.notifyDataSetChanged();
                listView.setVisibility(View.VISIBLE);
                listView2.setVisibility(View.VISIBLE);
                listView3.setVisibility(View.GONE);
                tv_back.setVisibility(View.GONE);
                mScrollLayout.setVisibility(View.VISIBLE);
                getOrg("", 1);
                searchPoint();
                break;
            case R.id.btn_finish:
                if (dl.isDrawerOpen(rlRight)) {
                    dl.closeDrawer(rlRight);
                }
                state = 3;
                listView.setVisibility(View.GONE);
                listView2.setVisibility(View.GONE);
                listView3.setVisibility(View.VISIBLE);
                tv_back.setVisibility(View.VISIBLE);
                mScrollLayout.setVisibility(View.VISIBLE);
                searchPoint();
                break;
            case R.id.btn_loc:
                isFirstLoc = true;
                mLocClient.start();
                break;
            case R.id.tv_back:
                if (state == 2 || state == 3) {
                    state = 1;
                    listView.setVisibility(View.VISIBLE);
                    listView2.setVisibility(View.VISIBLE);
                    listView3.setVisibility(View.GONE);
                    tv_back.setVisibility(View.GONE);
                }
                break;
            case R.id.tv_show_list2:
                switch (state) {
                    case 0:
                        listView.setVisibility(View.VISIBLE);
                        listView3.setVisibility(View.GONE);
                        tv_back.setVisibility(View.GONE);
                        if (orgList1 == null || orgList1.size() == 0) {
                            getOrg("", 1);
//                            ToastUtils.showShortToast("未查询到相关网点信息！");
                        } else {
                            listView.setAdapter(orgAdapter);
                            orgAdapter.notifyDataSetChanged();
                            mScrollLayout.setToOpen();
                        }
                        break;
                    case 1:
                        listView2.setVisibility(View.VISIBLE);
                        tv_back.setVisibility(View.GONE);
                        if (orgList1 == null || orgList1.size() == 0) {
                            getOrg("", 1);
//                            ToastUtils.showShortToast("未查询到相关网点信息！");
                        } else {
                            listView.setAdapter(orgAdapter);
                            orgAdapter.notifyDataSetChanged();
                            mScrollLayout.setToOpen();
                        }
                        if (orgList2 == null || orgList2.size() == 0) {
                            getOrg(org1Id, 2);
//                            ToastUtils.showShortToast("未查询到相关网点信息！");
                        } else {
                            listView2.setAdapter(orgAdapter1);
                            orgAdapter1.notifyDataSetChanged();
                            mScrollLayout.setToOpen();
                        }
                        break;
                    case 2:
                        listView.setVisibility(View.GONE);
                        listView2.setVisibility(View.VISIBLE);
                        listView3.setVisibility(View.VISIBLE);
                        tv_back.setVisibility(View.VISIBLE);
                        if (orgList2 == null || orgList2.size() == 0) {
                            getOrg(org1Id, 2);
//                            ToastUtils.showShortToast("未查询到相关网点信息！");
                        } else {
                            listView2.setAdapter(orgAdapter1);
                            orgAdapter1.notifyDataSetChanged();
                            mScrollLayout.setToOpen();
                        }
                        if (mapList == null || mapList.size() == 0) {
//                            ToastUtils.showShortToast("未查询到相关网点信息！");
                            searchPoint();
                        } else {
                            listView3.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            mScrollLayout.setToOpen();
                        }
                        break;
                    case 3:
                        listView.setVisibility(View.GONE);
                        listView2.setVisibility(View.GONE);
                        tv_back.setVisibility(View.VISIBLE);
                        if (mapList == null || mapList.size() == 0) {
                            searchPoint();
//                            ToastUtils.showShortToast("未查询到相关网点信息！");
                        } else {
                            listView3.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            mScrollLayout.setToOpen();
                        }
                        break;
                }

                break;
            default:
                break;
        }
    }

    //获取地区信息
    private void getAddr(String upId, final int item) {
        final Map<String, String> params = new HashMap<>();
        params.put("upId", upId);
        final StringCallback callback = new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int i) {
                ToastUtils.showShortToast("获取地区信息失败！");
            }

            @Override
            public void onResponse(String s, int i) {
//                ToastUtils.showShortToast(s);
                Gson gson = new Gson();
                AreaResaultBean dataBean = null;
                try {
                    dataBean = gson.fromJson(s, AreaResaultBean.class);
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
                if (dataBean == null) {
                    ToastUtils.showShortToast("获取地区信息失败！");
                    return;
                }
                if (dataBean.success) {
                    if (dataBean.datas == null) {
                        return;
                    }
                    if (item == 1) {
                        areaList1 = dataBean.datas;
                        areaStrList1 = new ArrayList<String>();
                        for (AreaResaultBean.DatasBean bean : areaList1) {
                            areaStrList1.add(bean.NAME);
                        }
                        cpv_area1.setPicker(areaStrList1);
                    } else if (item == 2) {
                        areaList2 = dataBean.datas;
                        areaStrList2 = new ArrayList<String>();
                        for (AreaResaultBean.DatasBean bean : areaList2) {
                            areaStrList2.add(bean.NAME);
                        }
                        cpv_area2.setPicker(areaStrList2);
                    } else if (item == 3) {
                        areaList3 = dataBean.datas;
                        areaStrList3 = new ArrayList<String>();
                        for (AreaResaultBean.DatasBean bean : areaList3) {
                            areaStrList3.add(bean.NAME);
                        }
                        cpv_area3.setPicker(areaStrList3);
                    }
                } else {
                    ToastUtils.showShortToast("获取地区信息失败！");
                }

            }
        };
        DataUtil.doPostAESData(null, context, ConstantValues.MAP_AREA_INFO, params, callback);
    }

    //获取机构信息
    private void getOrg(String upId, final int item) {
        Map<String, String> params = new HashMap<>();
        params.put("upId", upId);
        StringCallback callback = new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int i) {
                ToastUtils.showShortToast("获取机构信息失败！");
            }

            @Override
            public void onResponse(String s, int i) {
//                ToastUtils.showShortToast(s);
                Gson gson = new Gson();
                AreaResaultBean dataBean = null;
                try {
                    dataBean = gson.fromJson(s, AreaResaultBean.class);
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
                if (dataBean == null) {
                    ToastUtils.showShortToast("获取机构信息失败！");
                    return;
                }
                if (dataBean.success) {
                    if (dataBean.datas == null) {
                        return;
                    }
                    if (item == 1) {
                        orgList1 = dataBean.datas;
                        orgStrList1 = new ArrayList<>();
                        for (AreaResaultBean.DatasBean bean : orgList1) {
                            orgStrList1.add(bean.NAME);
                        }
                        if (state == 0) {
                            orgAdapter = new OrgAdapter(orgList1);
                            listView.setAdapter(orgAdapter);
                            orgAdapter.notifyDataSetChanged();
                        }
                        cpv_org1.setPicker(orgStrList1);
                    } else if (item == 2) {
                        orgList2 = dataBean.datas;
                        orgStrList2 = new ArrayList<>();
                        for (AreaResaultBean.DatasBean bean : orgList2) {
                            orgStrList2.add(bean.NAME);
                        }
                        if (state == 1) {
                            orgAdapter1 = new OrgAdapter(orgList2);
                            listView2.setAdapter(orgAdapter1);
                            orgAdapter1.notifyDataSetChanged();
                        }
                        cpv_org2.setPicker(orgStrList2);
                    }
                } else {
                    ToastUtils.showShortToast("获取机构信息失败！");
                }

            }
        };
        DataUtil.doPostAESData(null, context, ConstantValues.MAP_ORG_INFO, params, callback);
    }

    //获取网点详情信息
    private void getDetail(String title) {
        int index = Integer.parseInt(title);
        Map<String, String> params = new HashMap<>();
        params.put("id", mapList.get(index).ID);
        params.put("t", "" + mapList.get(index).TYPE);
        StringCallback callback = new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int i) {
                ToastUtils.showShortToast("获取网点信息失败！" + e.getMessage());
            }

            @Override
            public void onResponse(String s, int i) {
//                ToastUtils.showShortToast(s);
                Gson gson = new Gson();
                PiontInfoBean mapDataBean = null;
                try {
                    mapDataBean = gson.fromJson(s, PiontInfoBean.class);
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
                if (mapDataBean == null) {
                    ToastUtils.showShortToast("未查询到相关网点信息！");
                    return;
                }
                if (mapDataBean.success) {
                    PiontInfoBean.DataBean piontInfo = mapDataBean.data;
                    showPointInfo(piontInfo);
                } else {
                    ToastUtils.showShortToast("未查询到相关网点信息！");
                }

            }
        };
        DataUtil.doPostAESData(null, context, ConstantValues.MAP_POINT_DETAIL, params, callback);
    }

    //搜索网点
    private void searchPoint() {
        rl_pop.setVisibility(View.GONE);
        imm.hideSoftInputFromWindow(et_search.getWindowToken(), 0); //强制隐藏键盘
        mBaiduMap.clear();
        mapList.clear();
        mDistrictSearch.searchDistrict(new DistrictSearchOption().cityName("鹤壁市"));
        Map<String, String> params = new HashMap<>();
        String key = et_search.getText().toString().trim();
        params.put("key", key);
        params.put("cityId", area1Id);
        params.put("areaId", area2Id);
        params.put("cunId", area3Id);
        params.put("xqId", org1Id);
        params.put("zjId", org2Id);
        params.put("isZj", isZj ? "1" : "");
        params.put("isFwz", isWd ? "1" : "");
        StringCallback callback = new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int i) {
                adapter.notifyDataSetChanged();
                ToastUtils.showShortToast("获取网点信息失败！");
            }

            @Override
            public void onResponse(String s, int i) {
//                ToastUtils.showShortToast(s);
//                adapter.notifyDataSetChanged();
                Gson gson = new Gson();
                MapDataBean mapDataBean = null;
                try {
                    mapDataBean = gson.fromJson(s, MapDataBean.class);
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
                if (mapDataBean == null) {
                    ToastUtils.showShortToast("获取网点信息失败！");
                    return;
                }
                if (mapDataBean.datas == null || mapDataBean.datas.size() == 0) {
                    ToastUtils.showShortToast("未查询到相关网点！");
                    return;
                }
                if (mapDataBean.success) {
//                    adapter.notifyDataSetChanged();
//                    listView3.setAdapter(new MyAdapter(mapList));
                    mapList = mapDataBean.datas;
                    adapter = new MyAdapter(mapList);
                    listView3.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
//                    mScrollLayout.setToOpen();

//                    adapter.notifyDataSetChanged();
                    if (state == 2) {
                        listView2.setVisibility(View.VISIBLE);
                        listView3.setVisibility(View.VISIBLE);
                    } else if (state == 3) {
                        listView2.setVisibility(View.GONE);
                        listView.setVisibility(View.GONE);
                        listView3.setVisibility(View.VISIBLE);
                    }
                    MarkerOptions markerOptions = new MarkerOptions();
                    BitmapDescriptor zjBitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_add);
                    BitmapDescriptor wdBitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_youju);
                    if (mapList == null || mapList.size() == 0) {
                        return;
                    }

                    if (mapList.get(0).JD != 0 && mapList.get(0).WD != 0) {
                        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(new LatLng(mapList.get(0).WD, mapList.get(0).JD)));
                        mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(16));
                    }
                    for (int j = 0; j < mapList.size(); j++) {
                        if (mapList.get(j).JD != 0 && mapList.get(j).WD != 0) {
                            if (mapList.get(j).TYPE == 1) {
                                markerOptions = new MarkerOptions().title("" + j).position(new LatLng(mapList.get(j).WD, mapList.get(j).JD))
                                        .icon(zjBitmap);
                            } else if (mapList.get(j).TYPE == 2) {
                                markerOptions = new MarkerOptions().title("" + j).position(new LatLng(mapList.get(j).WD, mapList.get(j).JD))
                                        .icon(wdBitmap);
                            }
                            mBaiduMap.addOverlay(markerOptions);
                        }

                    }

                } else {
                    adapter.notifyDataSetChanged();
                    ToastUtils.showShortToast("获取网点信息失败！");
                }

            }
        };
        DataUtil.doPostAESData(null, context, ConstantValues.MAP_DATA, params, callback);
//        mDistrictSearch.searchDistrict(new DistrictSearchOption().cityName("鹤壁市"));
    }


    @Override
    public void onBackPressed() {
        if (dl.isDrawerOpen(rlRight)) {
            dl.closeDrawer(rlRight);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        // 退出时销毁定位
        mLocClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }


//    private BNRoutePlanNode.CoordinateType mCoordinateType = null;

    private void routeplanToNavi(String JD, String WD) {
        if (!hasInitSuccess) {
            Toast.makeText(context, "还未初始化!", Toast.LENGTH_SHORT).show();
        }
        // 权限申请
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            // 保证导航功能完备
            if (!hasCompletePhoneAuth()) {
                if (!hasRequestComAuth) {
                    hasRequestComAuth = true;
                    this.requestPermissions(authComArr, authComRequestCode);
                    return;
                } else {
                    Toast.makeText(context, "请设置该应用的权限!", Toast.LENGTH_SHORT).show();
                }
            }

        }
        BNRoutePlanNode sNode = new BNRoutePlanNode(locLL.longitude, locLL.latitude, "", null, BNRoutePlanNode.CoordinateType.BD09LL);
        BNRoutePlanNode eNode = new BNRoutePlanNode(ConvertUtil.obj2Double(JD), ConvertUtil.obj2Double(WD), "", null, BNRoutePlanNode.CoordinateType.BD09LL);

        List<BNRoutePlanNode> list = new ArrayList<BNRoutePlanNode>();
        list.add(sNode);
//			list.add(new BNRoutePlanNode(116.41355525193937, 39.915144800132085, "北京", null, coType));
        list.add(eNode);
        BaiduNaviManager.getInstance().launchNavigator(this, list, 1, true, new DemoRoutePlanListener(sNode));
    }

    @Override
    public void onGetDistrictResult(DistrictResult districtResult) {
        if (districtResult == null) {
            return;
        }
        if (districtResult.error == SearchResult.ERRORNO.NO_ERROR) {
            List<List<LatLng>> polyLines = districtResult.getPolylines();
            if (polyLines == null) {
                return;
            }
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (List<LatLng> polyline : polyLines) {
                OverlayOptions ooPolyline11 = new PolylineOptions().width(10)
                        .points(polyline).dottedLine(true).color(0xAA00AA00);
                mBaiduMap.addOverlay(ooPolyline11);
//                OverlayOptions ooPolygon = new PolygonOptions().points(polyline)
//                        .stroke(new Stroke(5, 0xAA00FF88)).fillColor(0xAAFFFF00);
//                mBaiduMap.addOverlay(ooPolygon);
                for (LatLng latLng : polyline) {
                    builder.include(latLng);
                }
            }
            mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLngBounds(builder.build()));
            // 以获取的坐标列表的第一个为中心
            if (mapList != null && mapList.size() > 0 && mapList.get(0).JD != 0 && mapList.get(0).WD != 0) {
                mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(new LatLng(mapList.get(0).WD, mapList.get(0).JD)));
                mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(16));
            }

        }
    }

    public class DemoRoutePlanListener implements BaiduNaviManager.RoutePlanListener {

        private BNRoutePlanNode mBNRoutePlanNode = null;

        public DemoRoutePlanListener(BNRoutePlanNode node) {
            mBNRoutePlanNode = node;
        }

        @Override
        public void onJumpToNavigator() {
            /*
             * 设置途径点以及resetEndNode会回调该接口
             */
            Intent intent = new Intent(context, BNGuideActivity.class);
            intent.putExtra("locLatitude", locLL.latitude);
            intent.putExtra("locLongitude", locLL.longitude);
            Bundle bundle = new Bundle();
            bundle.putSerializable(ROUTE_PLAN_NODE, (BNRoutePlanNode) mBNRoutePlanNode);
            intent.putExtras(bundle);
            startActivity(intent);

        }

        @Override
        public void onRoutePlanFailed() {
            Toast.makeText(context, "算路失败", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 内部TTS播报状态回传handler
     */
    private Handler ttsHandler = new Handler() {
        public void handleMessage(Message msg) {
            int type = msg.what;
            switch (type) {
                case BaiduNaviManager.TTSPlayMsgType.PLAY_START_MSG: {
//                     showToastMsg("Handler : TTS play start");
                    break;
                }
                case BaiduNaviManager.TTSPlayMsgType.PLAY_END_MSG: {
                    // showToastMsg("Handler : TTS play end");
                    break;
                }
                default:
                    break;
            }
        }
    };

    /**
     * 内部TTS播报状态回调接口
     */
    private BaiduNaviManager.TTSPlayStateListener ttsPlayStateListener = new BaiduNaviManager.TTSPlayStateListener() {

        @Override
        public void playEnd() {
            // showToastMsg("TTSPlayStateListener : TTS play end");
        }

        @Override
        public void playStart() {
            // showToastMsg("TTSPlayStateListener : TTS play start");
        }
    };

    private void showPointInfo(PiontInfoBean.DataBean pointInfo) {
        //处理popWindow 显示内容
        rl_pop.setVisibility(View.VISIBLE);
        mScrollLayout.setVisibility(View.GONE);
        mScrollLayout.setToExit();
        handleContent(pointInfo);

//        View contentView = LayoutInflater.from(this).inflate(R.layout.point_info, null);
        //创建并显示popWindow
//        mListPopWindow = new CustomPopWindow.PopupWindowBuilder(this)
//                .setView(contentView)
//                .size(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)//显示大小
//                .create()
//                .showAtLocation(mMapView, Gravity.BOTTOM, 0, 0);
//                .showAsDropDown(btn_clearing,0,0);
//                .showAsDropDown(btn_clearing, 0, -btn_clearing.getHeight());
        // 设置背景颜色变暗
//        WindowManager.LayoutParams lp = getWindow().getAttributes();
//        lp.alpha = 0.7f;
//        getWindow().setAttributes(lp);
    }

    //处理popWindow 显示内容
    private void handleContent(final PiontInfoBean.DataBean pointInfo) {

        if (pointInfo.t == 1) {
            tv_pt_name.setText(pointInfo.NAME);
            tv_name.setVisibility(View.GONE);
            tv_phone.setVisibility(View.GONE);
        } else {
            tv_pt_name.setText(pointInfo.SHOP_NAME);
            tv_name.setVisibility(View.VISIBLE);
            tv_phone.setVisibility(View.VISIBLE);
            tv_name.setText(pointInfo.SELLER_NAME);
            tv_phone.setText(pointInfo.MOBILE);
        }
        String address, privince, city, area, addr;
        if (!TextUtils.isEmpty(pointInfo.PROVINCE_NAME))
            privince = pointInfo.PROVINCE_NAME;
        else
            privince = "";
        if (!TextUtils.isEmpty(pointInfo.CITY_NAME))
            city = pointInfo.CITY_NAME;
        else
            city = "";
        if (!TextUtils.isEmpty(pointInfo.CUN_NAME))
            area = pointInfo.CUN_NAME;
        else
            area = "";
        if (!TextUtils.isEmpty(pointInfo.AREA_NAME))
            area = pointInfo.AREA_NAME;
        else
            area = area + "";
        if (!TextUtils.isEmpty(pointInfo.ADDRESS))
            addr = pointInfo.ADDRESS;
        else
            addr = "";
        address = privince + city + area + addr;
        if (TextUtils.isEmpty(address))
            address = "地址不详";

        tv_addr.setText(address);
        iv_goto.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DrivingRouteActivity.class);
                intent.putExtra("currLat", locLL.latitude);
                intent.putExtra("currLng", locLL.longitude);
                intent.putExtra("toLat", ConvertUtil.obj2Double(pointInfo.WD));
                intent.putExtra("toLng", ConvertUtil.obj2Double(pointInfo.JD));
                startActivity(intent);
            }
        });
        tv_phone.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(pointInfo.MOBILE)) {
//                    callorSms(pointInfo.MOBILE);
                    PhoneUtils.dial(pointInfo.MOBILE);
                }
            }
        });
        btn_navi.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showShortToast("开启导航...");
                routeplanToNavi(pointInfo.JD, pointInfo.WD);
            }
        });
    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {  //|| mMapView == null
                return;
            }
            locLL = new LatLng(location.getLatitude(),
                    location.getLongitude());
            if (isFirstLoc) {
                MyLocationData locData = new MyLocationData.Builder()
                        .accuracy(location.getRadius())
                        // 此处设置开发者获取到的方向信息，顺时针0-360
                        .direction(100).latitude(location.getLatitude())
                        .longitude(location.getLongitude()).build();
                mBaiduMap.setMyLocationData(locData);
                isFirstLoc = false;
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(locLL).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }

//		public void onReceivePoi(BDLocation poiLocation) {
//			if (poiLocation == null){
//				return;
//			}
//			String locDesc = poiLocation.getLocationDescribe();
//			String locName = poiLocation.getBuildingName();
//			Toast.makeText(context,"locDesc = "+locDesc+"--locName="+locName,Toast.LENGTH_SHORT).show();
//		}
    }

    private ScrollLayout.OnScrollChangedListener mOnScrollChangedListener = new ScrollLayout.OnScrollChangedListener() {
        @Override
        public void onScrollProgressChanged(float currentProgress) {
            if (currentProgress >= 0) {
                float precent = 255 * currentProgress;
                if (precent > 255) {
                    precent = 255;
                } else if (precent < 0) {
                    precent = 0;
                }
                mScrollLayout.getBackground().setAlpha(255 - (int) precent);
            }
//            if (tv_show_list.getVisibility() == View.VISIBLE)
//                tv_show_list.setVisibility(View.GONE);
        }

        @Override
        public void onScrollFinished(ScrollLayout.Status currentStatus) {
            if (currentStatus.equals(ScrollLayout.Status.EXIT)) {
//                tv_show_list.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onChildScroll(int top) {
        }
    };

    class MyAdapter extends DefaultBaseAdapter<MapDataBean.DatasBean> {

        private ViewHolder holder;

        public MyAdapter(List<MapDataBean.DatasBean> datas) {
            super(datas);
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            holder = null;
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.item_point_list,
                        null);
                holder = new ViewHolder();
//                holder.iv_icon = (ImageView) convertView
//                        .findViewById(R.id.iv_goods);
                holder.tv_name = (TextView) convertView
                        .findViewById(R.id.tv_name);
                holder.tv_point_name = (TextView) convertView
                        .findViewById(R.id.tv_point_name);
                holder.tv_phone = (TextView) convertView
                        .findViewById(R.id.tv_phone);
                holder.tv_addr = (TextView) convertView
                        .findViewById(R.id.tv_addr);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            String pointName = datas.get(position).NAME;
            holder.tv_point_name.setText(pointName);
            String name = datas.get(position).SELLER_NAME;
            if (TextUtils.isEmpty(name)) {
                holder.tv_name.setVisibility(View.GONE);
            } else {
                holder.tv_name.setVisibility(View.VISIBLE);
                holder.tv_name.setText(name);
            }
            String phone = datas.get(position).MOBILE;
            if (TextUtils.isEmpty(phone)) {
                holder.tv_phone.setVisibility(View.GONE);
            } else {
                holder.tv_phone.setVisibility(View.VISIBLE);
                holder.tv_phone.setText(phone);
            }
            String address, privince, city, area, addr;
            if (!TextUtils.isEmpty(datas.get(position).PROVINCE_NAME))
                privince = datas.get(position).PROVINCE_NAME;
            else
                privince = "";
            if (!TextUtils.isEmpty(datas.get(position).CITY_NAME))
                city = datas.get(position).CITY_NAME;
            else
                city = "";
            if (!TextUtils.isEmpty(datas.get(position).CUN_NAME))
                area = datas.get(position).CUN_NAME;
            else
                area = "";
            if (!TextUtils.isEmpty(datas.get(position).AREA_NAME))
                area = datas.get(position).AREA_NAME;
            else
                area = area + "";
            if (!TextUtils.isEmpty(datas.get(position).ADDRESS))
                addr = datas.get(position).ADDRESS;
            else
                addr = "";
            address = privince + city + area + addr;
            if (TextUtils.isEmpty(address))
                address = "地址不详";
            holder.tv_addr.setText(address);
            return convertView;
        }

    }

    class ViewHolder {
        // private TextView tv_card_name;
        private TextView tv_name;
        private TextView tv_point_name;
        private TextView tv_phone;
        private TextView tv_addr;
        private ImageView iv_icon;
    }

    class OrgAdapter extends DefaultBaseAdapter<AreaResaultBean.DatasBean> {

        private OrgHolder holder;

        public OrgAdapter(List<AreaResaultBean.DatasBean> datas) {
            super(datas);
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            holder = null;
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.lv_head2,
                        null);
                holder = new OrgHolder();
//                holder.iv_icon = (ImageView) convertView
//                        .findViewById(R.id.iv_goods);
                holder.tv_tt = (TextView) convertView
                        .findViewById(R.id.tv_tt);

                convertView.setTag(holder);
            } else {
                holder = (OrgHolder) convertView.getTag();
            }

            String name = datas.get(position).NAME;
            holder.tv_tt.setText(name);
//            holder.tv_tt.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    holder.tv_tt.setBackgroundColor(getColor(R.color.lightgrey));
//                }
//            });

            if (datas.get(position).ID.equals(org1Id) || datas.get(position).ID.equals(org2Id)) {
                holder.tv_tt.setBackgroundResource(R.color.lightgray);
            } else {
                holder.tv_tt.setBackgroundResource(R.color.white);
            }
            return convertView;
        }

    }

    class OrgHolder {
        // private TextView tv_card_name;
        private TextView tv_tt;
    }
}
