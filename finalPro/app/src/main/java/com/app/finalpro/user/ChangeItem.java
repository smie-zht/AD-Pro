package com.app.finalpro.user;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.app.finalpro.R;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static com.app.finalpro.user.ChangeBitmap.show;

public class ChangeItem extends AppCompatActivity {
    private boolean ifchange=false;
    User_info userifo=new User_info();
    User_info user_info;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_item);
        ImageView imageView=(ImageView)findViewById(R.id.backtouser);
        final EditText editText=(EditText)findViewById(R.id.editnum);
        Button button=(Button)findViewById(R.id.buttonsure);
        Intent intent = getIntent();
        final Bundle bundle=intent.getExtras();
        String info=bundle.getString("user");
        getUserinfo(info);
        imageView.setOnClickListener(new ImageView.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent myintent  = new Intent();
                Bundle bundle1=new Bundle();
                bundle1.putBoolean("change",ifchange);
                myintent.putExtras(bundle1);
                setResult(RESULT_OK, myintent);
                finish();
            }
        });
        button.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                if(editText.getText().toString().isEmpty()||editText.getText().toString()==null)
                {
                    Toast.makeText(ChangeItem.this,"请输入学号",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    user_info.setNumber(editText.getText().toString());
                    updateUserinfo(user_info);
                    Toast.makeText(ChangeItem.this,"修改成功",Toast.LENGTH_SHORT).show();
                }
            }
        });
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
    public void updateUserinfo(final User_info user_info){
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.interrupted()) {
                    try {
                        Thread.sleep(100);  // 每隔0.1秒尝试连接
                        Class.forName("com.mysql.jdbc.Driver");
                    } catch (InterruptedException e) {
                        Log.v("ss", e.toString());
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    String ip = "119.29.229.194";
                    int port = 53306;
                    String dbName = "android";
                    String url = "jdbc:mysql://" + ip + ":" + port
                            + "/" + dbName; // 构建连接mysql的字符串
                    String user = "root";
                    String password = "android123";
                    try {
                        Connection conn = DriverManager.getConnection(url, user, password);
                        Log.v("ss", "远程连接成功!");
                        String sql = "update User_info "
                                +" set number='"+user_info.getNumber()
                                +"', password='"+user_info.getPassword()
                                +"', image='"+user_info.getUserimage()
                                +"', phone='"+user_info.getPhone()
                                +"' where user_name='" + user_info.getUser_name()+"'";
                        Statement st = (Statement) conn.createStatement();
                        st.executeUpdate(sql);
                        handler.obtainMessage(2).sendToTarget();
                        st.close();
                        conn.close();
                        return;
                    } catch (SQLException e) {
                        e.printStackTrace();
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
            }
            else if(msg.what==2){
                ifchange=true;
            }
        }
    };
}
