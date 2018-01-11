package com.app.finalpro.user;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.finalpro.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static com.app.finalpro.user.ChangeBitmap.show;

public class User_detail extends AppCompatActivity {
    private Bitmap bitmap;
    private String urlimg;
    ConstraintLayout constraintLayout;
    ConstraintLayout favoriteauthor;
    ConstraintLayout record;
    ConstraintLayout waiting;
    ConstraintLayout brorrow;
    ConstraintLayout back;
    ConstraintLayout finish;
    ImageView imageView;
    ImageView imageViewset;
    TextView textViewname;
    User_info user_info;
    User_info userifo=new User_info();
    private String base_url="http://zhyhhh-1255732607.cosgz.myqcloud.com/image/userimage/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        BindView();
        update();
        setClick();
        setThread();
    }
    //绑定视图。
    public void BindView()
    {
        imageView=(ImageView)findViewById(R.id.imagetx);
        textViewname=(TextView)findViewById(R.id.username);
        imageViewset=(ImageView)findViewById(R.id.settingimage);
        constraintLayout=(ConstraintLayout)findViewById(R.id.menu);
        record=(ConstraintLayout)findViewById(R.id.record);
        waiting=(ConstraintLayout)findViewById(R.id.waiting);
        brorrow=(ConstraintLayout)findViewById(R.id.brorrow);
        back=(ConstraintLayout)findViewById(R.id.returnwait);
        finish=(ConstraintLayout)findViewById(R.id.returnbook);
        favoriteauthor=(ConstraintLayout)findViewById(R.id.favoriteauthor);
    }
    //初次进入界面的查询用户信息操作。
    public void update()
    {

        getUserinfo("user1");
        textViewname.setText(user_info.getUser_name());
        urlimg=base_url+user_info.getUserimage();
    }
    //按键事件绑定。
    public void setClick()
    {
        constraintLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Bundle bundle=new Bundle();
                bundle.putString("user","user1");
                Intent intent=new Intent(User_detail.this,User_infomation.class);
                intent.putExtras(bundle);
                startActivityForResult(intent,2);
            }
        });
        imageViewset.setOnClickListener(new ImageView.OnClickListener(){
            @Override
            public void onClick(View v){
                Bundle bundle=new Bundle();
                bundle.putString("user","user1");
                Intent intent=new Intent(User_detail.this,SetManger.class);
                intent.putExtras(bundle);
                startActivityForResult(intent,1);
            }
        });
        record.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Bundle bundle=new Bundle();
                bundle.putString("user","user1");
                bundle.putString("state","all");
                Intent intent=new Intent(User_detail.this,BookRecord.class);
                intent.putExtras(bundle);
                startActivityForResult(intent,3);
            }
        });
        waiting.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Bundle bundle=new Bundle();
                bundle.putString("user","user1");
                bundle.putString("state","waiting");
                Intent intent=new Intent(User_detail.this,BookRecord.class);
                intent.putExtras(bundle);
                startActivityForResult(intent,3);
            }
        });
        brorrow.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Bundle bundle=new Bundle();
                bundle.putString("user","user1");
                bundle.putString("state","brorrow");
                Intent intent=new Intent(User_detail.this,BookRecord.class);
                intent.putExtras(bundle);
                startActivityForResult(intent,3);
            }
        });
        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Bundle bundle=new Bundle();
                bundle.putString("user","user1");
                bundle.putString("state","back");
                Intent intent=new Intent(User_detail.this,BookRecord.class);
                intent.putExtras(bundle);
                startActivityForResult(intent,3);
            }
        });
        finish.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Bundle bundle=new Bundle();
                bundle.putString("user","user1");
                bundle.putString("state","finish");
                Intent intent=new Intent(User_detail.this,BookRecord.class);
                intent.putExtras(bundle);
                startActivityForResult(intent,3);
            }
        });
        favoriteauthor.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Bundle bundle=new Bundle();
                bundle.putString("user","user1");
                Intent intent=new Intent(User_detail.this,Follow.class);
                intent.putExtras(bundle);
                startActivityForResult(intent,4);
            }
        });
    }
    //设置线程操作，更新图片信息
    public void setThread()
    {
        Thread thread=new Thread(){
            @Override
            public void run(){
                try {
                    URL url=new URL(urlimg);//通过url查询图片，显示。
                    InputStream is= url.openStream();
                    bitmap = BitmapFactory.decodeStream(is);
                    handler.sendEmptyMessage(1);
                    return ;
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what==1) {
                Bitmap bitmap1=show(bitmap);
                imageView.setImageBitmap(bitmap1);
            }
            else if(msg.what==2){
                user_info=userifo;
            }
        }
    };
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==RESULT_OK)
        {
                Bundle bundle=data.getExtras();
                if(bundle.getBoolean("ifchange"))
                {
                    Thread thread2=new Thread(){
                        @Override
                        public void run(){
                            try {
                                URL url=new URL(urlimg);
                                InputStream is= url.openStream();
                                bitmap = BitmapFactory.decodeStream(is);
                                handler.sendEmptyMessage(1);
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    thread2.start();
                }

        }
    }
    public void getUserinfo(final String name)
    {
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // 反复尝试连接，直到连接成功后退出循环
                while (!Thread.interrupted()) {
                    try {
                        Thread.sleep(100);  // 每隔0.1秒尝试连接
                        Class.forName("com.mysql.jdbc.Driver");//找到jar包的位置
                    } catch (InterruptedException e) {
                        e.printStackTrace();
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
                        String sql = "select * from User_info where user_name='" + name+"'";
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
                            Log.v("eeeee", String.valueOf(userifo.getId()));
                            handler.obtainMessage(2).sendToTarget();
                            resultSet.close();
                            st.close();
                            conn.close();
                            Log.v("用户信息","获取");
                            return;
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                        Log.v("用户信息异常:", String.valueOf(e.getMessage()));
                    }
                }
            }

        });
        thread.start();
    }

}
