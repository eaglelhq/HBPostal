package com.yitao.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yitao.library_tao.R;
import com.yitao.util.ImageLoaderUtil;


/**
 * Created by Administrator on 2016/6/27 0027.
 */
public class RollViewPager extends ViewPager {

    //传递过来的图片数组，这个必须更换，真实项目中有可能是一个集合
    private int[] imageUrls;
    private String[] imagesUrl;
    private static final int NEXT = 99;//切换下一张图片的标志
    private boolean isRunning = false;//是否自动轮播的标志，默认不自动轮播

    private ImageLoader mImageLoader;
    private boolean shouldRunning;

    //    private BitmapUtils bitmapUtils;
    public RollViewPager(Context context) {
        super(context);
    }

    public RollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
//        initImageLoader(context);
    }

    public void setImageUrls(int[] imageUrls) {
        this.imageUrls = imageUrls;
        setAdapter(new MyRollViePagerAdatper());
    }

    public void setImageUrls(String[] imageUrls) {
        this.imagesUrl = imageUrls;
        setAdapter(new MyRollViePagerAdatper());
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {

                case NEXT:
                    if (isRunning == true) {
                        //设置当前item+1；相当于设置下一个item，然后余图片数量；
                        setCurrentItem(getCurrentItem() + 1);
                        //然后发送空消息延时2秒
                        handler.sendEmptyMessageDelayed(NEXT, 3000);
                    }
                    break;
            }
        }
    };

    //开始轮播
    public void startRoll() {
        //开启轮播
        isRunning = true;
        shouldRunning = true;
        //发送handler延时2秒
        handler.sendEmptyMessageDelayed(NEXT, 3000);
    }

    //停止轮播
    public void stopRoll() {
        //开启轮播
        isRunning = false;
        shouldRunning = false;
        //发送handler延时2秒
        handler.removeMessages(NEXT);
    }

//    public void initImageLoader(Context context) {
//		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
//				context).threadPriority(Thread.NORM_PRIORITY - 2)
//				.denyCacheImageMultipleSizesInMemory()
//				.discCacheFileNameGenerator(new Md5FileNameGenerator())
//				.tasksProcessingOrder(QueueProcessingType.LIFO)
//				.writeDebugLogs().build();
//		ImageLoader.getInstance().init(config);
//		mImageLoader = ImageLoader.getInstance();
//	}

    class MyRollViePagerAdatper extends PagerAdapter {

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            //这个必须取余数，不然会下标越界
            ImageView imageView = (ImageView) View.inflate(getContext(), R.layout.viewpager_item, null);
            imageView.setScaleType(ScaleType.CENTER_CROP);
//        	imageView.setBackgroundResource(R.drawable.loading);
            if (imageUrls != null && imageUrls.length != 0) {
                position = position % imageUrls.length;
                imageView.setImageResource(imageUrls[position]);
            }
            if (imagesUrl != null && imagesUrl.length != 0) {
                position = position % imagesUrl.length;
                try {
                    ImageLoaderUtil.loadNetPic(imagesUrl[position], imageView);
                } catch (Exception e) {
//					imageView.setBackgroundResource(R.drawable.loading);
                    e.printStackTrace();
                }
            }
            //先获取网络中图片的url
//           在真实项目中使用谷歌官方提供的Glide加载图片
//            Glide.with(context).load(new File(path)).diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().into(vh.imageView);
            container.addView(imageView);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    private int downTime = 0;//按下时间
    //按下的XY坐标
    private int downX = 0;
    private int downY = 0;

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        switch (ev.getAction()) {

            case MotionEvent.ACTION_DOWN:
                downX = (int) ev.getX();
                downY = (int) ev.getY();
                downTime = (int) System.currentTimeMillis();
                //停止轮播
                isRunning = false;
                handler.removeMessages(NEXT);
                break;

            case MotionEvent.ACTION_UP:
                int upX = (int) ev.getX();
                int upY = (int) ev.getY();
                int disX = Math.abs(upX - downX);
                int disY = Math.abs(upY - downY);
                int upTime = (int) System.currentTimeMillis();
                if (upTime - downTime < 500 && disX - disY < 5) {
                    if (onItemClickListener != null) {
                        if (imageUrls != null && imageUrls.length != 0) {
                            //当前位置就是显示的条目
                            onItemClickListener.onItemClick(getCurrentItem()
                                    % imageUrls.length);
                        }
                        if (imagesUrl != null && imagesUrl.length != 0) {
                            //当前位置就是显示的条目
                            onItemClickListener.onItemClick(getCurrentItem()
                                    % imagesUrl.length);
                        }
                    }
                }
                //开启轮播
                if (shouldRunning)
                    startRoll();
                break;

            case MotionEvent.ACTION_CANCEL:
                if (shouldRunning)
                    startRoll();
                break;

            default:
                break;
        }

        return super.onTouchEvent(ev);
    }

    @Override
    //当控件挂载到页面上会调用此方法
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    //当控件从页面上移除的时候会调用此方法
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isRunning = false;
        handler.removeMessages(NEXT);
    }

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        public void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

}
