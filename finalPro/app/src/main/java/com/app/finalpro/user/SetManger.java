package com.app.finalpro.user;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.finalpro.MainActivity;
import com.app.finalpro.R;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

public class SetManger extends AppCompatActivity {
    ImageView imageViewback;
    User_info userifo=new User_info();
    ConstraintLayout constraintLayoutuserinfo;
    ConstraintLayout constraintLayoutchangephone;
    ConstraintLayout constraintLayoutchangePass;
    TextView textView;
    Button buttonquit;
    Boolean ifchange2=false;
    String info;
    User_info user_info;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_manger);
        imageViewback=(ImageView)findViewById(R.id.backtomain);
        constraintLayoutuserinfo=(ConstraintLayout)findViewById(R.id.userinfo);
        constraintLayoutchangePass=(ConstraintLayout)findViewById(R.id.changepass);
        constraintLayoutchangephone=(ConstraintLayout)findViewById(R.id.changephonenum);
        textView=(TextView)findViewById(R.id.telenum);
        buttonquit=(Button)findViewById(R.id.quit);
        Intent intent = getIntent();
        final Bundle bundle=intent.getExtras();
        info=bundle.getString("user");
        getUserinfo(info);

        imageViewback.setOnClickListener(new ImageView.OnClickListener(){
            @Override
            public void onClick(View v){
                Animation animation=new AlphaAnimation(1.0f,0.0f);
                animation.setDuration(100);
                imageViewback.startAnimation(animation);
                Intent myintent  = new Intent();
                Bundle bundle=new Bundle();
                bundle.putBoolean("ifchange",ifchange2);
                myintent.putExtras(bundle);
                SetManger.this.setResult(4, myintent);
                finish();
            }
        });
        constraintLayoutuserinfo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Animation animation=new AlphaAnimation(1.0f,0.0f);
                animation.setDuration(100);
                constraintLayoutuserinfo.startAnimation(animation);
                Bundle bundle=new Bundle();
                bundle.putString("user",info);
                Intent intent=new Intent(SetManger.this,User_infomation.class);
                intent.putExtras(bundle);
                startActivityForResult(intent,1);
            }
        });
        constraintLayoutchangephone.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Animation animation=new AlphaAnimation(1.0f,0.0f);
                animation.setDuration(100);
                constraintLayoutchangephone.startAnimation(animation);
                Bundle bundle=new Bundle();
                bundle.putString("user",info);
                Intent intent=new Intent(SetManger.this,Changephone.class);
                intent.putExtras(bundle);
                startActivityForResult(intent,1);
            }
        });
        constraintLayoutchangePass.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Animation animation=new AlphaAnimation(1.0f,0.0f);
                animation.setDuration(100);
                constraintLayoutchangePass.startAnimation(animation);
                Bundle bundle=new Bundle();
                bundle.putString("user",info);
                Intent intent=new Intent(SetManger.this,ChangePass.class);
                intent.putExtras(bundle);
                startActivityForResult(intent,1);
            }
        });
        buttonquit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences player = getSharedPreferences("user", Context.MODE_PRIVATE);
                AlertDialog.Builder builder = new AlertDialog.Builder(SetManger.this);
                builder.setTitle("是否注销登录").setMessage("当前登录用户： "+player.getString("user",""))
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).setNeutralButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SharedPreferences.Editor editor = player.edit();
                        editor.putString("user","");
                        editor.commit();
                        Toast.makeText(SetManger.this,"成功退出",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SetManger.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }).create().show();
            }
        });
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bundle bundleget=data.getExtras();
            Boolean ifchange=bundleget.getBoolean("change2");
            ifchange2=bundleget.getBoolean("ifchange");
            if(ifchange)
            {
                getUserinfo(info);
                textView.setText(user_info.getPhone2());
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
                            handler.obtainMessage(1).sendToTarget();
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
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what==1) {
                user_info=userifo;
                textView.setText(user_info.getPhone2());
            }
        }
    };
}
