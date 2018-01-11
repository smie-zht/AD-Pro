package com.app.finalpro.user;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.finalpro.R;
import com.app.finalpro.community.Login;
import com.bumptech.glide.Glide;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.app.finalpro.user.GlideCircleTransform;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import static android.app.Activity.RESULT_OK;
import static com.app.finalpro.R.id.imageView;
import static com.app.finalpro.user.ChangeBitmap.show;

/**
 * Created by ZXG on 2018/1/7.
 */

public class user_fragment extends Fragment {
    private Bitmap bitmap;
    private String urlimg;
    ConstraintLayout constraintLayout;
    ConstraintLayout favoriteauthor;   //关注作者
    ConstraintLayout collectBook;     //图书收藏
    ConstraintLayout reserveBook;      //预约图书

    ConstraintLayout record;
    ConstraintLayout waiting;
    ConstraintLayout brorrow;
    ConstraintLayout back;
    ConstraintLayout finish;
    ImageView imageView;
    ImageView imageViewset;
    TextView textViewname;
    Handler handler;
    SharedPreferences player;
    String userName;
    User_info user_info;
    User_info userifo;
    View rootView;
    private String base_url="http://zhyhhh-1255732607.cosgz.myqcloud.com/image/userimage/";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        rootView = inflater.inflate(R.layout.activity_user_detail, null);
        userifo = new User_info();
        BindView();
        setHandle();
        update();
        setClick();
        return rootView;
    }

    private void myThread() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //1.查看本地环境（是否找到适用的jar包）
                try {
                    Thread.sleep(100);  // 每隔0.1秒尝试连接
                    Class.forName("com.mysql.jdbc.Driver");//找到jar包的位置
                } catch (InterruptedException e) {
                    Log.v("findJar", e.toString());
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                // 2.设置好IP/端口/数据库名/用户名/密码等必要的连接信息
                String ip = "119.29.229.194";
                int port = 53306;
                String dbName = "android";
                String url = "jdbc:mysql://" + ip + ":" + port
                        + "/" + dbName; // 构建连接mysql的字符串
                String user = "root";
                String password = "android123";
                // 3.连接JDBC
                try {
                    Connection conn = DriverManager.getConnection(url, user, password);
                    Log.v("ss", "远程连接成功!");
                    String sql = "select * from User_info where user_name='" + userName+"'";
                    Statement st = (Statement) conn.createStatement();
                    ResultSet resultSet = st.executeQuery(sql);
                    if (resultSet.next()) {
                        Log.v("user",resultSet.getString(("user_name")));
                        userifo.setId(resultSet.getInt(("user_id")));
                        userifo.setUser_name(resultSet.getString(("user_name")));
                        userifo.setPassword(resultSet.getString(("password")));
                        userifo.setNumber(resultSet.getString(("number")));
                        userifo.setUserimage(resultSet.getString(("image")));
                        userifo.setPhone(resultSet.getString(("phone")));
                        Log.v("我在user_fragment", String.valueOf(userifo.getId()));
                        handler.obtainMessage(2).sendToTarget();
                        resultSet.close();
                        st.close();
                        conn.close();
                        Log.v("用户信息","获取");
                        return;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    Log.v("用户信息异常:",String.valueOf(e.getMessage()));
                }
            }
        });
        thread.start();
    }

    private void setHandle() {
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if (msg.what==1) {
                    myThread();
                }else if(msg.what==2){
                    textViewname.setText(userName);
                    urlimg=base_url+userifo.getUserimage();
                    Log.v("urlimg",urlimg);

                    Glide.with(getActivity()).load(urlimg)
                            .transform(new GlideCircleTransform(getActivity()))
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(imageView);

                }
            }
        };
    }

    //绑定视图。
    public void BindView()
    {
        player = getActivity().getSharedPreferences("user",Context.MODE_PRIVATE);
        userName = player.getString("user","");
        imageView=(ImageView)rootView.findViewById(R.id.imagetx);                           //头像
        textViewname=(TextView)rootView.findViewById(R.id.username);
        imageViewset=(ImageView)rootView.findViewById(R.id.settingimage);
        constraintLayout=(ConstraintLayout)rootView.findViewById(R.id.menu);
        record=(ConstraintLayout)rootView.findViewById(R.id.record);
        waiting=(ConstraintLayout)rootView.findViewById(R.id.waiting);
        brorrow=(ConstraintLayout)rootView.findViewById(R.id.brorrow);
        back=(ConstraintLayout)rootView.findViewById(R.id.returnwait);
        finish=(ConstraintLayout)rootView.findViewById(R.id.returnbook);
        favoriteauthor=(ConstraintLayout)rootView.findViewById(R.id.favoriteauthor);
        collectBook=(ConstraintLayout)rootView.findViewById(R.id.favoritebook);
        reserveBook=(ConstraintLayout)rootView.findViewById(R.id.appointmentbook);
    }
    //初次进入界面的查询用户信息操作。
    public void update()
    {
        //User_info user_info;
        //user_info=db.getUserinfo(userName);
        textViewname.setText(userName);
        urlimg=base_url+userifo.getUserimage();
        handler.obtainMessage(1).sendToTarget();

    }
    //按键事件绑定。
    //constraintLayout--->用户栏，imageViewset------->用户设置图标
    //
    public void setClick()
    {
        constraintLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Bundle bundle=new Bundle();
                Log.v("跳转：","个人信息");
                bundle.putString("user",userName);
                Intent intent=new Intent(getActivity(),User_infomation.class);
                intent.putExtras(bundle);
                getActivity().startActivityForResult(intent,2);
            }
        });
        imageViewset.setOnClickListener(new ImageView.OnClickListener(){
            @Override
            public void onClick(View v){
                Bundle bundle=new Bundle();
                bundle.putString("user",userName);
                Log.v("跳转：","个人设置");
                Intent intent=new Intent(getActivity(),SetManger.class);
                intent.putExtras(bundle);
                getActivity().startActivityForResult(intent,4);
            }
        });
        record.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Bundle bundle=new Bundle();
                bundle.putString("user",userName);
                bundle.putString("status","all");
                Intent intent=new Intent(getActivity(),BookRecord.class);
                intent.putExtras(bundle);
                startActivityForResult(intent,3);
            }
        });
        waiting.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Bundle bundle=new Bundle();
                bundle.putString("user",userName);
                bundle.putString("status","waiting");
                Intent intent=new Intent(getActivity(),BookRecord.class);
                intent.putExtras(bundle);
                startActivityForResult(intent,3);
            }
        });
        brorrow.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Bundle bundle=new Bundle();
                bundle.putString("user",userName);
                bundle.putString("status","borrow");
                Intent intent=new Intent(getActivity(),BookRecord.class);
                intent.putExtras(bundle);
                startActivityForResult(intent,3);
            }
        });
        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Bundle bundle=new Bundle();
                bundle.putString("user",userName);
                bundle.putString("status","back");
                Intent intent=new Intent(getActivity(),BookRecord.class);
                intent.putExtras(bundle);
                startActivityForResult(intent,3);
            }
        });
        finish.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Bundle bundle=new Bundle();
                bundle.putString("user",userName);
                bundle.putString("status","finish");
                Intent intent=new Intent(getActivity(),BookRecord.class);
                intent.putExtras(bundle);
                startActivityForResult(intent,3);
            }
        });
        //关注作者
        favoriteauthor.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Bundle bundle=new Bundle();
                bundle.putString("user",userName);
                Intent intent=new Intent(getActivity(),Follow.class);
                intent.putExtras(bundle);
                startActivityForResult(intent,4);
            }
        });
        //图书收藏
        collectBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle=new Bundle();
                bundle.putString("user",userName);
                Intent intent=new Intent(getActivity(),collect.class);
                intent.putExtras(bundle);
                startActivityForResult(intent,5);
            }
        });
        //预约图书
        reserveBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle=new Bundle();
                bundle.putString("user",userName);
                Intent intent=new Intent(getActivity(),reserve.class);
                intent.putExtras(bundle);
                startActivityForResult(intent,5);
            }
        });

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v("urlback","jjj");
        if(resultCode == 2||resultCode==4) {
            Glide.with(getActivity()).load(urlimg).transform(new GlideCircleTransform(getActivity()))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(imageView);
        }
        Log.v("结束：",String.valueOf(resultCode));
    }

}
