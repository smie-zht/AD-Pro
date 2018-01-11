package com.app.finalpro.community;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.app.finalpro.R;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class register extends AppCompatActivity {
    public Button register;//新用户注册
    public EditText user1;//用户名
    public EditText password1;//密码
    public EditText number1;
    public LinearLayout back;
   // private String sql;//输入注册信息之后实现查询的显示！
//    private String phonenumber;
    String s;
    private void findViewById() {
        back=(LinearLayout) findViewById(R.id.back1);
        register = (Button) findViewById(R.id.register_but_landing);
        user1 = (EditText) findViewById(R.id.register_edit_userName);
        password1= (EditText) findViewById(R.id.register_edit_password);
        number1 =(EditText) findViewById(R.id.register_edit_number);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        findViewById();
        Intent intent=getIntent();
        Bundle bundle=intent.getBundleExtra("data");
        String phonenumber=bundle.getString("name");//得到此时的电话号码
        Log.v("ss",phonenumber);
//        String
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( user1.length()==0){
                    Toast.makeText(register.this, "请输入注册信息", Toast.LENGTH_SHORT).show();
                }
                else if( user1.length()!=0&&password1.length()==0){
                    Toast.makeText(register.this, "请输入密码！", Toast.LENGTH_SHORT).show();
                }
                else if( user1.length()!=0&&password1.length()!=0&&number1.length()==0){
                    Toast.makeText(register.this, "请输入学号便于找回密码！", Toast.LENGTH_SHORT).show();
                }
                else{
                    String name2=user1.getText().toString();
                    String password2=password1.getText().toString();
                    String number_id=number1.getText().toString();
                    Log.v("SS","AAA");
                    final Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
//                            while (!Thread.interrupted()) {     // 反复尝试连接，直到连接成功后退出循环
                            try {
                                Thread.sleep(100);  // 每隔0.1秒尝试连接
                                Class.forName("com.mysql.jdbc.Driver");//找到jar包的位置
                            }
                            catch (InterruptedException e) {
                                Log.v("ss", e.toString());
                            }
                            catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                            String ip = "119.29.229.194";  // 2.设置好IP/端口/数据库名/用户名/密码等必要的连接信息
                            int port = 53306;
                            String dbName = "android";
                            String url = "jdbc:mysql://" + ip + ":" + port+ "/" + dbName; // 构建连接mysql的字符串
                            String user = "root";
                            String password = "android123";   // 3.连接JDBC
                            String Sql="select * from User_info where user_name='"+name2+"'";
                            String sql="insert into User_info(user_name,password,number,phone) values('"+name2+"','"+password2+"','"+number_id+"','"+phonenumber+"');";
                            try {
                                Connection conn = DriverManager.getConnection(url, user, password);
                                Statement st = (Statement) conn.createStatement();         //ResultSet rs = st.executeQuery(sql);
                                ResultSet Cnt = st.executeQuery(Sql);
                                Log.v("SS",Sql);
                                if(Cnt.next()){
                                    handler.obtainMessage(125).sendToTarget();
                                }
                                else{
                                    int cnt = st.executeUpdate(sql);
                                    Log.v("ss",sql);
                                    if (cnt>0) {
                                        handler.obtainMessage(123).sendToTarget();
                                    }
                                    else{
                                        handler.obtainMessage(124).sendToTarget();
                                    }
                                }

                                st.close();
                                conn.close();
                                return;
                            }
                            catch (SQLException e) {
                                Log.v("ss", "远程连接失败!");
                                Log.v("ss", e.getMessage());
                            }
//                            }
                        }
                    });
                    thread.start();
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {   //返回按钮的点击事件。
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(register.this,Login.class);
                startActivity(intent2);
                register.this.finish();
            }
        });
    }
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 123:
                    String data="注册成功请登录！";
                    Intent intp=new Intent(register.this,Login.class);
                    intp.putExtra("extra_data",data);
                    startActivity(intp);
                    register.this.finish();
                    break;
                case 124:
//                    Toast.makeText(Login.this, "请检查输入信息", Toast.LENGTH_SHORT).show();
                    break;
                case 125:
                    Toast.makeText(register.this, "用户已经存在，请重新输入！", Toast.LENGTH_SHORT).show();
                    break;
                default:break;
            }
        }
    };
}
