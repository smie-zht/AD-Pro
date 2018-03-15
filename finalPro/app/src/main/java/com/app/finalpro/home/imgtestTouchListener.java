package com.app.finalpro.home;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import java.util.Date;
import java.util.concurrent.Delayed;

import static java.lang.StrictMath.abs;

/**
 * Created by ZXG on 2017/12/30.
 */

abstract class imgtestTouchListener implements View.OnTouchListener {

    private int x=0;   //用于记录坐标
    private int y=0;
    private int initHeight;
    public LinearLayout head;    //要操作的对象
    public Context main;
    public  String left;    //需要的判断参数
    public String right;
    private Handler handler;
    Date dt;
    Long time;
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // 可以通过 x ,y 来判断 滑动的手势
        //不过只可以判断：上滑，下滑，左滑，右滑
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //手指按下的时候：初始化 x,y 值
                x=(int) event.getRawX();
                y=(int) event.getRawY();
                dt = new Date(System.currentTimeMillis());
                ConstraintLayout.LayoutParams linearParams
                        =(ConstraintLayout.LayoutParams) head.getLayoutParams(); //取对象当前的布局参数
                initHeight = linearParams.height;
                time = dt.getTime();
                break;
            case MotionEvent.ACTION_MOVE:
                //滑动触发事件：根据最新坐标，修改对象
                Log.v("滑动",String.valueOf(abs((int)event.getRawY()-y)));
                if(abs((int)event.getRawY()-y)%1==0&&abs((int)event.getRawY()-y)<430){
                    moveEvent((int)event.getRawX(),(int)event.getRawY());
                }
                break;
            case MotionEvent.ACTION_UP:
                //松开触发事假：根据最终坐标，修改对象
                int upx=(int) event.getRawX();
                int upy=(int) event.getRawY();
                String result=drawTouch(upx,upy);
                Log.v("result",String.valueOf(upy));
                break;
        }
        return true;
    }

    public void setObject(View v){
        head = (LinearLayout)v;
    }

    public void setHandler(){
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                super.handleMessage(msg);
                ConstraintLayout.LayoutParams linearParams
                        =(ConstraintLayout.LayoutParams) head.getLayoutParams(); //取对象当前的布局参数
                switch (msg.what){
                    case 101:  //延时伸展
                        if(linearParams.height>=410){
                            linearParams.height=430;
                            head.setLayoutParams(linearParams);
                        }else{
                            linearParams.height+=20;
                            head.setLayoutParams(linearParams);
                            handler.sendEmptyMessageDelayed(101,15);
                        }break;
                    case 102:  //延时收缩
                        if(linearParams.height<=20){
                            linearParams.height=0;
                            head.setLayoutParams(linearParams);
                            head.setVisibility(View.GONE);
                        }else{
                            linearParams.height-=20;
                            head.setLayoutParams(linearParams);
                            handler.sendEmptyMessageDelayed(102,15);
                        }break;
                }
            }
        };
    }

    //松开触发事假：根据最终坐标，修改对象
    private String drawTouch(int upx,int upy){
        String str="没有滑动";
        //水平滑动
        if(upx-x>400){
            str="向右滑动";
        }else if(x-upx>400){
            str="向左滑动";
        }else if((upy-y>200)||((y-upy<200)&&(y-upy>10))){
            str="向下滑动";
            ConstraintLayout.LayoutParams linearParams
                    =(ConstraintLayout.LayoutParams) head.getLayoutParams(); //取对象当前的布局参数
            head.setVisibility(View.VISIBLE);
            dt = new Date(System.currentTimeMillis());
            time = dt.getTime();
            Log.v("滑动时间",String.valueOf(time));
//            while(linearParams.height<420){
//                Log.v("滑动-缓慢展开",String.valueOf(linearParams.height));
//                dt = new Date(System.currentTimeMillis());
//                time = dt.getTime();
//                Log.v("滑动-缓慢展开时间",String.valueOf(dt.getTime()));
//                linearParams.height+=1;
//                head.setLayoutParams(linearParams);
//            }
//                dt = new Date(System.currentTimeMillis());
//                time = dt.getTime();
//                Log.v("滑动时间",String.valueOf(time));
//                linearParams.height = 430;
//                head.setLayoutParams(linearParams); //使设置好的布局参数应用到控件
            handler.obtainMessage(101).sendToTarget();
            Log.v("收缩","展开");
        }else if((y-upy>200)||((upy-y<200)&&(upy-y>10))){
            str="向上滑动";
            ConstraintLayout.LayoutParams linearParams
                    =(ConstraintLayout.LayoutParams) head.getLayoutParams(); //取对象当前的布局参数
//                while(linearParams.height>10){
//                    Log.v("滑动-缓慢收缩",String.valueOf(linearParams.height));
//                    dt = new Date(System.currentTimeMillis());
//                    time = dt.getTime();
//                    Log.v("滑动-缓慢收缩-时间",String.valueOf(time));
//                    linearParams.height-=1;
//                    head.setLayoutParams(linearParams);
//                }
//            linearParams.height = 0;
//            head.setLayoutParams(linearParams); //使设置好的布局参数应用到控件
//            head.setVisibility(View.GONE);
            handler.obtainMessage(102).sendToTarget();
            Log.v("收缩","隐藏");
        }
        else{
            click();
            Log.v("触发","点击事件");
        }
        return str;
    }

    //滑动触发事件：根据最新坐标，修改对象
    private void moveEvent(int upx,int upy){
        if(head!=null){
            ConstraintLayout.LayoutParams linearParams = (ConstraintLayout.LayoutParams)head.getLayoutParams();
            int up = upy - y;
            if(up>20&&initHeight<400){      //up + 表示：下滑
                linearParams.height = (up);
                head.setLayoutParams(linearParams); //使设置好的布局参数应用到控件
                head.setVisibility(View.VISIBLE);
            }
            if(up<0&&initHeight>400){linearParams.height = (430 + up);}   //up- 表示：下滑
            if(linearParams.height<10)   linearParams.height = 0;
            if(linearParams.height>450 ) linearParams.height = 430;
            head.setLayoutParams(linearParams); //使设置好的布局参数应用到控件
        }
    }

    public abstract void click();
    
}