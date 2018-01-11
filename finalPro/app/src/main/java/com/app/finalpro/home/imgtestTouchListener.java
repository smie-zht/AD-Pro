package com.app.finalpro.home;

import android.content.Context;
import android.content.Intent;
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
    public LinearLayout head;    //要操作的对象
    public Context main;
    public  String left;    //需要的判断参数
    public String right;
    Date dt= new Date();
    Long time;
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // 可以通过 x ,y 来判断 滑动的手势
        //不过只可以判断：上滑，下滑，左滑，右滑
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //手指按下的时候：初始化 x,y 值
                x=(int) event.getX();
                y=(int) event.getY();
                time = dt.getTime();
                break;
            case MotionEvent.ACTION_MOVE:
                //滑动触发事件：根据最新坐标，修改对象
                Log.v("time",String.valueOf(dt.getTime()-time));
 //               if((dt.getTime()-time)%500==0){

 //               }
                if(abs((int)event.getY()-y)%5==0&&abs((int)event.getY()-y)<430){
                    moveEvent((int)event.getX(),(int)event.getY());
                }
                break;
            case MotionEvent.ACTION_UP:
                //松开触发事假：根据最终坐标，修改对象
                int upx=(int) event.getX();
                int upy=(int) event.getY();
                String result=drawTouch(upx,upy);
                Log.v("result",String.valueOf(event.getX()));
                break;
        }
        return true;
    }

    public void setObject(View v){
        head = (LinearLayout)v;
    }

    public void setToggle(Context main,String left,String right){
        this.main = main;
        this.left = left;
        this.right = right;
    }

    //松开触发事假：根据最终坐标，修改对象
    private String drawTouch(int upx,int upy){
        String str="没有滑动";
        //水平滑动
        if(upx-x>400){
            str="向右滑动";
//            Intent intent = new Intent(right);
//            main.startActivity(intent);
        }else if(x-upx>400){
            str="向左滑动";
//            Intent intent = new Intent();
//            intent.setAction(left);
//            main.startActivity(intent);
        }else if(upy-y>100){
            str="向下滑动";
            //改变图片
            //img_test.setImageResource(R.drawable.icon_down);

            ConstraintLayout.LayoutParams linearParams
                    =(ConstraintLayout.LayoutParams) head.getLayoutParams(); //取对象当前的布局参数
//            Log.v("is 0??",String.valueOf(linearParams.height));
//            if(linearParams.height == 0){
//                head.setVisibility(View.VISIBLE);
//                int frame = 0;
//                while (true){
//                    if(frame==25) break;
//                    if((dt.getTime()-time)%40==0){
//                        linearParams.height += 18;
//                        head.setLayoutParams(linearParams); //使设置好的布局参数应用到控件
//                        frame++;
//                        Log.v("height",String.valueOf(linearParams.height));
//                    }
//                }
                linearParams.height = 430;
                head.setVisibility(View.VISIBLE);
                head.setLayoutParams(linearParams); //使设置好的布局参数应用到控件
            //}
            Log.v("收缩","展开");
        }else if(y-upy>100){
            str="向上滑动";
            //改变图片
            //img_test.setImageResource(R.drawable.icon_up);

            ConstraintLayout.LayoutParams linearParams
                    =(ConstraintLayout.LayoutParams) head.getLayoutParams(); //取对象当前的布局参数
            Log.v("is Max??",String.valueOf(linearParams.height));
            //if(linearParams.height > 400){
//                int frame = 0;
//                linearParams.height = 430;
//                while (true){
//
//                    Delayed
//                    if(frame==25) break;
//                    if((dt.getTime()-time)%4==0){
//                        linearParams.height -= 18;
//                        head.setLayoutParams(linearParams); //使设置好的布局参数应用到控件
//                        Log.v("height1",String.valueOf(linearParams.height));
//                        frame++;
//                    }
//                    Log.v("height",String.valueOf(linearParams.height));
//                }
                linearParams.height = 0;
                head.setLayoutParams(linearParams); //使设置好的布局参数应用到控件
                head.setVisibility(View.GONE);
            //}
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
            if(up>30){
                linearParams.height = (up);
                head.setLayoutParams(linearParams); //使设置好的布局参数应用到控件
                head.setVisibility(View.VISIBLE);
            }
            else{linearParams.height = (430 + up);}
            if(linearParams.height<10)   linearParams.height = 0;
            if(linearParams.height>500 ) linearParams.height = 430;
            head.setLayoutParams(linearParams); //使设置好的布局参数应用到控件
        }
    }


    public abstract void click();




}