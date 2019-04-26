package com.emt.fatri.wearbaidusdkdemo.ui;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.emt.fatri.wearbaidusdkdemo.R;
import com.emt.fatri.wearbaidusdkdemo.utils.GlobalConstant;
import com.emt.fatri.wearbaidusdkdemo.utils.LocationUtil;
import com.emt.fatri.wearbaidusdkdemo.utils.MainApplication;
import com.emt.fatri.wearbaidusdkdemo.utils.SharedPreferenceUtil;

import java.util.ArrayList;


/**
 * description:雷达地图View，主要是显示震动点的位置。
 * Created by kingkong on 2018/7/5 0005.
 * changed by kingkong on 2018/7/5 0005.
 */

public class RadarView extends RelativeLayout {
    private static final String TAG= RadarView.class.getSimpleName();
    /**默认系统最大监测半径*/
    private int mAlarmDistance = 100;
    private Context mContext;
    /**报警点闪烁动画*/
    private ValueAnimator anim;
   /**报警点闪烁动画监听器*/
    private AnimationListener mListener;
    /**显示报警点编号的textView*/
    private TextView mTextView;
    /**添加报警点的根view*/
    private ViewGroup mViewGroup;
    /**监控点列表，做多添加20个*/
    private ArrayList<ImageView> mImagesViewList=new ArrayList<>(20);
    private volatile ArrayList<Integer> mErrorIds=new ArrayList<>(20);
    public RadarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext=context;
        LayoutInflater.from(context).inflate(R.layout.compass_layout, this);
        mViewGroup = findViewById(R.id.view_root);
        mTextView=findViewById(R.id.text_view);


    }

    /**
     * 初始化设置，应该放在view被看到前每一次。
     */
    public void initSettings(){

        mAlarmDistance=SharedPreferenceUtil.getInt(MainApplication.getInstance(),
                GlobalConstant.KEY_MAP_SIZE,10);
        if(SharedPreferenceUtil.getInt(MainApplication.getInstance(),
                GlobalConstant.MAP_ID,GlobalConstant.SHAO_BING_ID)==GlobalConstant.SHAO_BING_ID)
        {
            mViewGroup.setBackgroundResource(R.drawable.background);
        } else
        {
            mViewGroup.setBackgroundResource(R.drawable.backgroundp);
        }
        loadPoints();
        initAnimation();
    }


    /**
     * 接口，报警点监听接口
     */
    public interface AnimationListener {
        void onAnimationStart();
        void onAnimationEnd();
    }

    /**
     * 设置报警点动画监听
     * @param al 动画监听器
     */
    public void setAnimationListener(AnimationListener al)
    {
        mListener=al;
    }


    /**
     * 加载定位点
     */
    private void loadPoints() {
        if(GlobalConstant.DEBUG_LOG)Log.v(TAG,"loadPoints() start");
        // 首先获取圆心坐标
        double oy= SharedPreferenceUtil.getDouble(mContext,"originLatitudeKey",1.0);
        double ox= SharedPreferenceUtil.getDouble(mContext,"originLongitudeKey",1.0);
        if(GlobalConstant.DEBUG_LOG)Log.v(TAG,"0latitude="+oy+", "+"0longitude="+ox);

        // 计算每个点的坐标 // 这边现在有隐患 那就是默认点加载。
        // 不同地图存储不同的点
        int mapId=SharedPreferenceUtil.getInt(MainApplication.getInstance(),
                GlobalConstant.MAP_ID,GlobalConstant.SHAO_BING_ID);
        for(int i=0;i<GlobalConstant.MAX_lOCATION_POINT;i++)
        {



            String latitudeKey="sensor"+i+"latitude"+mapId;
            String longitudeKey="sensor"+i+"longitude"+mapId;
            double yi=SharedPreferenceUtil.getDouble(mContext,latitudeKey,1.0);
            double xi=SharedPreferenceUtil.getDouble(mContext,longitudeKey,1.0);

            if(GlobalConstant.DEBUG_LOG)Log.v(TAG,"latitude="+yi);
            if(GlobalConstant.DEBUG_LOG)Log.v(TAG,"longitude="+xi);
            double distance= LocationUtil.getDistance(xi,yi,ox,oy)/ mAlarmDistance *195;
            // 直线与圆交点
            int lx;
            int ly;
            if(xi>ox)
            {
                double k=(yi-oy)/(xi-ox);
                lx=(int)(195+Math.sqrt(distance*distance/(1+k*k)));
                ly=(int)(195-k*(Math.sqrt(distance*distance/(1+k*k))));
                if(GlobalConstant.DEBUG_LOG)Log.v(TAG,"if distance="+distance+",k="+k+",lx="+lx+",ly="+ly);
            } else if(xi<ox)
            {
                double k=(yi-oy)/(xi-ox);
                lx=(int)(195-Math.sqrt(distance*distance/(1+k*k)));
                ly=(int)(195+k*(Math.sqrt(distance*distance/(1+k*k))));
                if(GlobalConstant.DEBUG_LOG)Log.v(TAG,"else distance="+distance+",k="+k+",lx="+lx+",ly="+ly);
            }else {
                lx=195;
                ly=(int)(195+yi-oy);

            }
            addPoint(i,lx,ly);
        }
        // 加载后先亮起来
        for(int i=0;i<mImagesViewList.size();i++)
        {
            mImagesViewList.get(i).setVisibility(View.VISIBLE);
            mImagesViewList.get(i).setAlpha(0.5f);
        }

    }

    /**
     * 根据地图点 添加 表盘大小应该是380、380
     * @param x 坐标
     * @param y 坐标
     */
    public void addPoint(int id,int x, int y)
    {
        if(GlobalConstant.DEBUG_LOG)Log.v(TAG,"addPoint:"+id+",x="+x+",y="+y);
        ImageView imageView = new ImageView(mContext);
        LayoutParams layoutParams=new LayoutParams(37,37); //37像素大小
        layoutParams.leftMargin=x-19;
        layoutParams.topMargin=y-19;
        imageView.setImageResource(R.drawable.alert_point);
        imageView.setVisibility(View.VISIBLE);
        mViewGroup.addView(imageView,layoutParams);
        mImagesViewList.add(id,imageView);
    }

    /**
     * 设置报警的传感器id
     * @param errorIds 发送警报的编号
     */
    public void setErrorImageIds( ArrayList<Integer> errorIds)
    {
        if(GlobalConstant.DEBUG_LOG)Log.v(TAG,"setErrorImageIds errorIds="+errorIds.toString()
                +errorIds.get(0).toString());
        mErrorIds=errorIds;
    }

    /**
     * 开始动画
     */
    public void startAnim()
    {
        if(GlobalConstant.DEBUG_LOG)Log.v(TAG,"startAnim()");
        if(anim.isRunning())
        {
            anim.end();
        }

        anim.start();
    }

    private void initAnimation() {
            anim = ValueAnimator.ofInt(0, 20);
            // ofInt（）作用有两个
            // 1. 创建动画实例
            // 2. 将传入的多个Int参数进行平滑过渡:此处传入0和1,表示将值从0平滑过渡到1
            // 如果传入了3个Int参数 a,b,c ,则是先从a平滑过渡到b,再从b平滑过渡到C，以此类推
            // ValueAnimator.ofInt()内置了整型估值器,直接采用默认的.不需要设置，即默认设置了如何从初始值 过渡到 结束值
            // 关于自定义插值器我将在下节进行讲解
            // 下面看看ofInt()的源码分析 ->>关注1

            // 步骤2：设置动画的播放各种属性
            anim.setDuration(300);
            // 设置动画运行的时长

            anim.setStartDelay(10);
            // 设置动画延迟播放时间

            anim.setRepeatCount(50);
            // 设置动画重复播放次数 = 重放次数+1
            // 动画播放次数 = infinite时,动画无限重复

            anim.setRepeatMode(ValueAnimator.RESTART);
            // 设置重复播放动画模式
            // ValueAnimator.RESTART(默认):正序重放
            // ValueAnimator.REVERSE:倒序回放

            // 步骤3：将改变的值手动赋值给对象的属性值：通过动画的更新监听器
            // 设置 值的更新监听器
            // 即：值每次改变、变化一次,该方法就会被调用一次
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {

                    int currentValue = (Integer) animation.getAnimatedValue();
                    for (int i = 0; i < mErrorIds.size(); i++) {
                        if (currentValue % 2 == 0) {
                            mImagesViewList.get(mErrorIds.get(i)).setAlpha(0.5f);
                        } else {
                            mImagesViewList.get(mErrorIds.get(i)).setAlpha(1.0f);
                        }
                    }

                }

            });
            anim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    if(mListener!=null)
                    {
                        mListener.onAnimationStart();
                    }
                    for(int i=0;i<mImagesViewList.size();i++)
                    {
                        mImagesViewList.get(i).setVisibility(View.VISIBLE);
                        mImagesViewList.get(i).setAlpha(0.5f);
                    }
                    for (int i = 0; i < mErrorIds.size(); i++) {

                            mImagesViewList.get(mErrorIds.get(i)).setAlpha(1.0f);

                    }
                    if(mErrorIds.size()>0)
                    {
                        String text=String.valueOf(mErrorIds.get(mErrorIds.size()/2));
                        mTextView.setText(text);
                    }



                }

                @Override
                public void onAnimationEnd(Animator animation) {

                    for(int i=0;i<mImagesViewList.size();i++)
                    {
                        mImagesViewList.get(i).setVisibility(View.VISIBLE);
                        mImagesViewList.get(i).setAlpha(0.5f);
                    }
                    mTextView.setText("");
                    if(mListener!=null)
                    {
                        mListener.onAnimationEnd();
                    }

                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });


        }
    }

