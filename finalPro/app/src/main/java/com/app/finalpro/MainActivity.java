package com.app.finalpro;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.finalpro.community.Login;
import com.app.finalpro.community.com_fragment;
import com.app.finalpro.douban.douban_fragment;
import com.app.finalpro.home.home_fragment;
import com.app.finalpro.user.user_fragment;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends FragmentActivity {
    //要切换显示的四个Fragment
    private home_fragment homeFragment;
    private user_fragment userFragment;
    private com_fragment comFragment;
    private douban_fragment doubFragment;

    private int currentId = R.id.home;// 当前选中id,默认是主页
    private Map<Integer,Integer> normals;   //正常图片保存
    private Map<Integer,Integer> selects;   //选中图片保存

    //底栏
    private LinearLayout bottom;
    private String left = "bookDetail";
    private String right = "mainSearch";
    private ImageView com ;
    private ImageView user ;
    private ImageView home ;
    private ImageView dou ;
    private Map<Integer,ImageView> objects;   //保存操作对象

    //助手
    public Handler handler;
    public  Thread thread;
    public SharedPreferences player;//用户登录
    public boolean isLogin;
    public Date timer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setContentView(R.layout.activity_main);
        init();   //控件绑定 + 初始数据
        button(); //用户交互
    }
    public void init(){
        initData();
        bindView();  //绑定视图
        /**
         * 默认加载首页
         */
//        userFragment = new user_fragment();
//        getSupportFragmentManager().beginTransaction().add(R.id.main_container, userFragment).commit();
//        doubFragment = new douban_fragment();
//        getSupportFragmentManager().beginTransaction().add(R.id.main_container, doubFragment).commit();
        homeFragment = new home_fragment();
        getSupportFragmentManager().beginTransaction().add(R.id.main_container, homeFragment).commit();
    }
    //初始化数据
    private void initData() {
        player = getSharedPreferences("user", Context.MODE_PRIVATE);
        timer = new Date();
        if(player.getString("user","").toString().equals("")){
            isLogin = false;     //未登录
        }else{
            if(player.getLong("time",timer.getTime())-timer.getTime()>86400000){
                isLogin = false; //登录超时
            }else{
                isLogin = true; //已登录
            }
        }
        //normal状态下的图片
        normals = new HashMap<>();
        normals.put(R.id.home,R.drawable.home);
        normals.put(R.id.user,R.drawable.user);
        normals.put(R.id.community,R.drawable.users);
        normals.put(R.id.api,R.drawable.douban);
        //选中状态下的图片
        selects = new HashMap<>();
        selects.put(R.id.home,R.drawable.s_home);
        selects.put(R.id.user,R.drawable.s_user);
        selects.put(R.id.community,R.drawable.s_users);
        selects.put(R.id.api,R.drawable.s_douban);
    }

    //绑定主视图中五类视图
    public void bindView(){

        //底栏控件绑定
        bottom = (LinearLayout)findViewById(R.id.bottom);
        com = (ImageView)findViewById(R.id.community);
        user = (ImageView)findViewById(R.id.user);
        home = (ImageView)findViewById(R.id.home);
        dou = (ImageView)findViewById(R.id.api);

        objects = new HashMap<>();
        objects.put(R.id.home,home);
        objects.put(R.id.user,user);
        objects.put(R.id.community,com);
        objects.put(R.id.api,dou);

        home.setImageResource(R.drawable.s_home);

    }

    public  void button(){
        buttomLis();
    }
    //底部按键点击事件
    private void buttomLis() {
        View.OnClickListener tabClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() != currentId) {//如果当前选中跟上次选中的一样,不需要处理
                    if(player.getString("user","").toString().equals("")){
                        isLogin = false;     //未登录
                    }else{
                        if(player.getLong("time",timer.getTime())-timer.getTime()>86400000){
                            isLogin = false; //登录超时
                        }else{
                            isLogin = true; //已登录
                        }
                    }
                    if(isLogin){
                        changeSelect(currentId,v.getId());//图片更新操作
                        changeFragment(currentId,v.getId());//fragment的切换
                        //System.gc();
                        currentId = v.getId();//设置选中id
                    }else{
                        Toast.makeText(MainActivity.this,"请先登录",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this,Login.class);
                        startActivityForResult(intent,1);

                    }

                }
            }
        };

        home.setOnClickListener(tabClickListener);
        user.setOnClickListener(tabClickListener);
        com.setOnClickListener(tabClickListener);
        dou.setOnClickListener(tabClickListener);


    }

    /**
     * 改变ImageView 背景图片设置
     * @param oldId  撤销
     * @param newId  更新
     */
    private void changeSelect(int oldId,int newId) {
        objects.get(oldId).setImageResource(normals.get(oldId));
        objects.get(newId).setImageResource(selects.get(newId));
    }

    /**
     * 改变fragment的显示
     *
     * @param resId
     */
    private void changeFragment(int killId,int resId) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();//开启一个Fragment事务

        hideFragments(transaction);//隐藏所有fragment
        //killFragments(transaction,killId);
        if(resId==R.id.home){//主页
            if(homeFragment==null){//如果为空先添加进来.不为空直接显示
                homeFragment = new home_fragment();
                transaction.add(R.id.main_container,homeFragment);
            }else {
                transaction.show(homeFragment);
            }
        }else if(resId==R.id.user){//用户
            if(userFragment==null){
                userFragment = new user_fragment();
                transaction.add(R.id.main_container,userFragment,"fragment_tag_USER");
            }else {
                transaction.show(userFragment);
            }
        }else if(resId==R.id.community){//社区
            if(comFragment==null){
                comFragment = new com_fragment();
                transaction.add(R.id.main_container,comFragment,"fragment_tag_COM");
            }else {
                transaction.show(comFragment);
            }
        }else if(resId==R.id.api){//我
            if(doubFragment==null){
                doubFragment = new douban_fragment();
                transaction.add(R.id.main_container,doubFragment);
            }else {
                transaction.show(doubFragment);
            }
        }
        transaction.commit();//一定要记得提交事务
    }

    /**
     * 显示之前隐藏所有fragment
     * @param transaction
     */
    private void hideFragments(FragmentTransaction transaction){
        if (homeFragment != null)//不为空才隐藏,如果不判断第一次会有空指针异常
            transaction.hide(homeFragment);
        if (userFragment != null)
            transaction.hide(userFragment);
        if (comFragment != null)
            transaction.hide(comFragment);
        if (doubFragment != null)
            transaction.hide(doubFragment);
    }
    private void killFragments(FragmentTransaction transaction,int killId){
//        if (killId==R.id.home){
//            transaction.remove(homeFragment);
//            homeFragment = null;
//        }
        if (killId == R.id.user){
            transaction.remove(userFragment);
            userFragment = null;
        }
        if (killId == R.id.community){
            transaction.remove(comFragment);
            comFragment = null;
        }

//        else if (killId==R.id.api){
//            transaction.remove(doubFragment);
//            doubFragment = null;
//        }

        System.gc();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v("返回",String.valueOf(resultCode));
        if(resultCode==0)
        {   Log.v("返回","成功");
            if(player.getString("user","").toString().equals("")){
                isLogin = false;     //未登录
            }else{
                if(player.getLong("time",timer.getTime())-timer.getTime()>86400000){
                    isLogin = false; //登录超时
                }else{
                    Log.v("登录","成功");
                    isLogin = true; //已登录
                }
            }
        }
        else if(resultCode==2||resultCode==4){
            Log.v("返回----",String.valueOf(resultCode));
            this.getSupportFragmentManager().findFragmentByTag("fragment_tag_USER").onActivityResult(requestCode, resultCode, data);
        }else if(resultCode==3){
            this.getSupportFragmentManager().findFragmentByTag("fragment_tag_COM").onActivityResult(requestCode, resultCode, data);
        }

    }


}
