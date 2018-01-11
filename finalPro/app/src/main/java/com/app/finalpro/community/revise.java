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
import java.sql.SQLException;
import java.sql.Statement;

public class revise extends AppCompatActivity {
    public Button revise;//新用户注册
    public EditText user1;//用户名
    public EditText password1;//密码
    public EditText password2;
    public LinearLayout back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revise);
        revise=(Button) findViewById(R.id.revise_but_landing);
        user1=(EditText) findViewById(R.id.revise_edit_userName);
        password1=(EditText) findViewById(R.id.revise_edit_password);
        password2=(EditText) findViewById(R.id.revise_edit_password2);
        String str1=user1.getText().toString();//得到用户名
        String str2= password1.getText().toString();
        String str3=password2.getText().toString();
        revise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( user1.length()==0){
                    Toast.makeText(revise.this, "请输入账号信息", Toast.LENGTH_SHORT).show();
                }
                else if( user1.length()!=0&&password1.length()==0){
                    Toast.makeText(revise.this, "请输入密码！", Toast.LENGTH_SHORT).show();
                }
                else if(!(str2.equals(str3))&&user1.length()!=0){
                    Toast.makeText(revise.this, "两次输入的密码不一致！", Toast.LENGTH_SHORT).show();
                }
                else{
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
                           // String sql="insert into User_info(User_name,password,number) values('"+name2+"','"+password2+"','"+number+"');";
                            String sql="update User_info set password="+str2+"where User_name="+str1+";";
                            try {
                                Connection conn = DriverManager.getConnection(url, user, password);
                                Statement st = (Statement) conn.createStatement();         //ResultSet rs = st.executeQuery(sql);
                                int cnt = st.executeUpdate(sql);
                                if (cnt>0) {
                                    handler.obtainMessage(123).sendToTarget();
                                }
                                else{
                                    handler.obtainMessage(124).sendToTarget();
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
    }
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 123:
                    String data="修改成功请重新登录";
                    Intent intp=new Intent(revise.this,Login.class);
                    intp.putExtra("extra_data",data);
                    startActivity(intp);
                    revise.this.finish();
                    break;
                case 124:
//                    Toast.makeText(Login.this, "请检查输入信息", Toast.LENGTH_SHORT).show();
                    break;
                default:break;
            }
        }
    };
}
