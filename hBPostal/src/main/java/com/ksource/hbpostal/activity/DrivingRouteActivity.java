package com.ksource.hbpostal.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BaiduNaviManager;
import com.blankj.utilcode.utils.ToastUtils;
import com.ksource.hbpostal.R;
import com.ksource.hbpostal.overlayutil.DrivingRouteOverlay;
import com.yitao.util.ConvertUtil;
import com.yitao.util.NumberUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 路线规划--> 驾车
 */
public class DrivingRouteActivity extends Activity implements View.OnClickListener{

    private String mSDCardPath = null;
    private static final String APP_FOLDER_NAME = "BNSDK";
    public static final String ROUTE_PLAN_NODE = "routePlanNode";
    private static final String[] authBaseArr = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION};
    private static final String[] authComArr = {Manifest.permission.READ_PHONE_STATE};
    private static final int authBaseRequestCode = 1;
    private static final int authComRequestCode = 2;

    private boolean hasInitSuccess = false;
    private boolean hasRequestComAuth = false;

    private RoutePlanSearch mRoutePlanSearch;
    private LatLng mCurrentPos;
    private LatLng mPos4;

    private TextView tv_1,tv_2,tv_3,tv_light_count;
    private Button btn_go;
    private Context context;

    // 地图显示控件
    protected MapView mMapView;
    // 地图操作控件
    protected BaiduMap mBaiduMap;
    private List<DrivingRouteLine> routeLines;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_route);
        context = this;
        mMapView = (MapView) findViewById(R.id.map_view);
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView tv_title = (TextView)findViewById(R.id.tv_title);
        tv_title.setText("路线规划");
        tv_1 = (TextView)findViewById(R.id.tv_1);
        tv_2 = (TextView)findViewById(R.id.tv_2);
        tv_3 = (TextView)findViewById(R.id.tv_3);
        tv_light_count = (TextView)findViewById(R.id.tv_light_count);
        btn_go = (Button) findViewById(R.id.btn_go);
        tv_1.setOnClickListener(this);
        tv_2.setOnClickListener(this);
        tv_3.setOnClickListener(this);
        tv_light_count.setOnClickListener(this);
        btn_go.setOnClickListener(this);

        Intent intent = getIntent();
        double currLat = intent.getDoubleExtra("currLat",0);
        double currLng = intent.getDoubleExtra("currLng",0);
        double toLat = intent.getDoubleExtra("toLat",0);
        double toLng = intent.getDoubleExtra("toLng",0);
//        mCurrentPos = new LatLng(currLng,currLat);
//        mPos4 = new LatLng(toLng,toLat);
        mCurrentPos = new LatLng(currLat,currLng);
        mPos4 = new LatLng(toLat,toLng);
//        mPos4 = new LatLng(currLat-0.1,currLng-0.1);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(mCurrentPos));
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(15));

        initRoutePlanSearch();
        drivingSearch();
    }

    private void initRoutePlanSearch() {
    	//初始化搜索对象
        mRoutePlanSearch = RoutePlanSearch.newInstance();
        // 设置监听器
        mRoutePlanSearch.setOnGetRoutePlanResultListener(new
                OnGetRoutePlanResultListener(){

            @Override
            public void onGetTransitRouteResult(TransitRouteResult result) {
            }
            @Override
            public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {

            }
            @Override
            public void onGetWalkingRouteResult(WalkingRouteResult result) {
            }

            @Override// 接收搜索到的结果
            public void onGetDrivingRouteResult(DrivingRouteResult result) {
                if (result == null
                        || SearchResult.ERRORNO.RESULT_NOT_FOUND == result.error) {
                    ToastUtils.showShortToast("未搜索到结果");
                    tv_1.setVisibility(View.GONE);
                    tv_2.setVisibility(View.GONE);
                    tv_3.setVisibility(View.GONE);
                    tv_light_count.setText("未搜索到线路！");
                    return;
                }

                routeLines = result.getRouteLines();
                ToastUtils.showShortToast("共搜索到" + routeLines.size() + " 条路线");
                tv_light_count.setText("总路程"+NumberUtil.div(ConvertUtil.obj2Double(routeLines.get(0).getDistance()),ConvertUtil.obj2Double(1000),1)+"公里，途径红绿灯"+routeLines.get(0).getLightNum()+"个");
//                routeLines.get(0).describeContents();
                if (routeLines.size() == 3){
                    tv_1.setVisibility(View.VISIBLE);
                    tv_2.setVisibility(View.VISIBLE);
                    tv_3.setVisibility(View.VISIBLE);
                }else if (routeLines.size() == 2){
                    tv_1.setVisibility(View.VISIBLE);
                    tv_2.setVisibility(View.VISIBLE);
                    tv_3.setVisibility(View.GONE);
                }else if (routeLines.size() == 1){
                    tv_1.setVisibility(View.VISIBLE);
                    tv_2.setVisibility(View.GONE);
                    tv_3.setVisibility(View.GONE);
                }
                showRouteLineInMap(routeLines.get(0));
            }
            @Override
            public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

            }
            @Override
            public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

            }
        });
    }

    private void drivingSearch() {
        // 创建搜索参数
        DrivingRoutePlanOption drivingRoutePlanOption = new DrivingRoutePlanOption();

        PlanNode from = PlanNode.withLocation(mCurrentPos);// 起点
        drivingRoutePlanOption.from(from);
        PlanNode to = PlanNode.withLocation(mPos4 );
        drivingRoutePlanOption.to(to); // 终点

//		List<PlanNode> nodes = new ArrayList<PlanNode>();
//		nodes.add(PlanNode.withCityNameAndPlaceName("郑州", "高新区"));
//		drivingRoutePlanOption.passBy(nodes); // 设置线程的经过的位置

		// 设置线程的策略，距离最短
//        drivingRoutePlanOption.policy(DrivingRoutePlanOption.DrivingPolicy.ECAR_DIS_FIRST);
        // 开始搜索
        mRoutePlanSearch.drivingSearch(drivingRoutePlanOption);
    }

    /** 通过覆盖物在地图上绘制路线 */
    private void showRouteLineInMap(DrivingRouteLine routeLine) {
        DrivingRouteOverlay overlay = new DrivingRouteOverlay(mBaiduMap);
        mBaiduMap.setOnMarkerClickListener(overlay);// 把事件传递给overlay
        overlay.setData(routeLine);// 设置线路为第一条
        overlay.addToMap();
        overlay.zoomToSpan();
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        // 界面退出后，销毁搜索对象
        if (mRoutePlanSearch != null) {
            mRoutePlanSearch.destroy();
        }
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

    private void routeplanToNavi() {
//        if (!hasInitSuccess) {
//            Toast.makeText(context, "还未初始化!", Toast.LENGTH_SHORT).show();
//        }
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
        BNRoutePlanNode sNode = new BNRoutePlanNode(mCurrentPos.longitude, mCurrentPos.latitude, "", null, BNRoutePlanNode.CoordinateType.BD09LL);
        BNRoutePlanNode eNode = new BNRoutePlanNode(mPos4.longitude, mPos4.latitude, "", null, BNRoutePlanNode.CoordinateType.BD09LL);

        List<BNRoutePlanNode> list = new ArrayList<BNRoutePlanNode>();
        list.add(sNode);
//			list.add(new BNRoutePlanNode(116.41355525193937, 39.915144800132085, "北京", null, coType));
        list.add(eNode);
        BaiduNaviManager.getInstance().launchNavigator(this, list, 1, true, new DemoRoutePlanListener(sNode));
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
            intent.putExtra("locLatitude", mCurrentPos.latitude);
            intent.putExtra("locLongitude", mCurrentPos.longitude);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_1:
                mBaiduMap.clear();
                tv_1.setBackgroundResource(R.drawable.rect_green);
                tv_2.setBackgroundResource(R.drawable.rect_green_bk);
                tv_3.setBackgroundResource(R.drawable.rect_green_bk);
                tv_1.setTextColor(Color.WHITE);
                tv_2.setTextColor(Color.BLACK);
                tv_3.setTextColor(Color.BLACK);
                showRouteLineInMap(routeLines.get(0));
                tv_light_count.setText("总路程"+NumberUtil.div(ConvertUtil.obj2Double(routeLines.get(0).getDistance()),ConvertUtil.obj2Double(1000),1)+"公里，途径红绿灯"+routeLines.get(0).getLightNum()+"个");
//                ToastUtils.showShortToast("getDistance:"+routeLines.get(0).getDistance()+",getCongestionDistance:"+routeLines.get(0).getCongestionDistance());
                break;
            case R.id.tv_2:
                mBaiduMap.clear();
                tv_2.setBackgroundResource(R.drawable.rect_green);
                tv_1.setBackgroundResource(R.drawable.rect_green_bk);
                tv_3.setBackgroundResource(R.drawable.rect_green_bk);
                tv_2.setTextColor(Color.WHITE);
                tv_1.setTextColor(Color.BLACK);
                tv_3.setTextColor(Color.BLACK);
                showRouteLineInMap(routeLines.get(1));
                tv_light_count.setText("总路程"+NumberUtil.div(ConvertUtil.obj2Double(routeLines.get(1).getDistance()),ConvertUtil.obj2Double(1000),1)+"公里，途径红绿灯"+routeLines.get(1).getLightNum()+"个");
//                ToastUtils.showShortToast("getDistance:"+routeLines.get(1).getDistance()+",getCongestionDistance:"+routeLines.get(1).getCongestionDistance());
                break;
            case R.id.tv_3:
                mBaiduMap.clear();
                tv_3.setBackgroundResource(R.drawable.rect_green);
                tv_2.setBackgroundResource(R.drawable.rect_green_bk);
                tv_1.setBackgroundResource(R.drawable.rect_green_bk);
                tv_3.setTextColor(Color.WHITE);
                tv_2.setTextColor(Color.BLACK);
                tv_1.setTextColor(Color.BLACK);
                showRouteLineInMap(routeLines.get(2));
                tv_light_count.setText("总路程"+NumberUtil.div(ConvertUtil.obj2Double(routeLines.get(2).getDistance()),ConvertUtil.obj2Double(1000),1)+"公里，途径红绿灯"+routeLines.get(2).getLightNum()+"个");
//                ToastUtils.showShortToast("getDistance:"+routeLines.get(2).getDistance()+",getCongestionDistance:"+routeLines.get(2).getCongestionDistance());
                break;
            case R.id.btn_go:
                ToastUtils.showShortToast("开启导航...");
                routeplanToNavi();
                break;
        }
    }
}
