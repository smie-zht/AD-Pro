package com.app.finalpro.community;


import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.finalpro.MainActivity;
import com.app.finalpro.R;
//import com.james.project.top_snackbar.BaseTransientBottomBar;
//import com.james.project.top_snackbar.TopSnackBar;
import com.app.finalpro.community.top_snackbar.BaseTransientBottomBar;
import com.app.finalpro.community.top_snackbar.TopSnackBar;
import java.sql.Connection;
import java.util.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Login extends AppCompatActivity {
    private Snackbar snackBar; //定义snackbar的属性。
//    private int APP_DOWn = Snackbar.APPEAR_FROM_TOP_TO_DOWN;
    public Date dt;
    public Button Login; //登录按钮
    public TextView register;//新用户注册
    public TextView forget;//忘记密码
    public EditText user;//用户名
    public EditText password;//密码
    private String s;   //SQL获取用户名
    private TextView textView;
    private String sql;//输入登录信息之后实现查询的显示！
    private Toast toast;
    private View inflate;
    private Button choosePhoto;
    private Button takePhoto;
    private Button cancel;
    private Dialog dialog;
    public ImageView mBgView1;
    public ImageView mBgView2;
    public ImageView mBgView3;
    public ImageView mBgView4;
    private void findViewById() {
        Login = (Button) findViewById(R.id.login_but_landing);
        register = (TextView) findViewById(R.id.register);
        forget = (TextView) findViewById(R.id.forget);
        user = (EditText) findViewById(R.id.login_edit_userName);
        password = (EditText) findViewById(R.id.login_edit_password);
//        mBgView1=(ImageView) findViewById(R.id.login_bg_image4);
//        mBgView2=(ImageView) findViewById(R.id.login_bg_image3);
//        mBgView3=(ImageView) findViewById(R.id.login_bg_image2);
//        mBgView4=(ImageView) findViewById(R.id.login_bg_image1);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViewById();
//        ObjectAnimator animator1 = ObjectAnimator.ofFloat(mBgView1, "alpha", 1.0f, 0f);
//        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mBgView2, "alpha", 0f, 1.0f);
//        ObjectAnimator animatorScale1 = ObjectAnimator.ofFloat(mBgView1, "scaleX", 1.0f, 1.3f);
//        ObjectAnimator animatorScale2 = ObjectAnimator.ofFloat(mBgView1, "scaleY", 1.0f, 1.3f);
//        AnimatorSet animatorSet1 = new AnimatorSet();
//        animatorSet1.setDuration(5000);
//        animatorSet1.play(animator1).with(animator2).with(animatorScale1).with(animatorScale2);
//        ObjectAnimator animator3 = ObjectAnimator.ofFloat(mBgView2, "alpha", 1.0f, 0f);
//        ObjectAnimator animator4 = ObjectAnimator.ofFloat(mBgView3, "alpha", 0f, 1.0f);
//        ObjectAnimator animatorScale3 = ObjectAnimator.ofFloat(mBgView2, "scaleX", 1.0f, 1.3f);
//        ObjectAnimator animatorScale4 = ObjectAnimator.ofFloat(mBgView2, "scaleY", 1.0f, 1.3f);
//        AnimatorSet animatorSet2 = new AnimatorSet();
//        animatorSet2.setDuration(5000);
//        animatorSet2.play(animator3).with(animator4).with(animatorScale3).with(animatorScale4);
//        ObjectAnimator animator5 = ObjectAnimator.ofFloat(mBgView3, "alpha", 1.0f, 0f);
//        ObjectAnimator animator6 = ObjectAnimator.ofFloat(mBgView4, "alpha", 0f, 1.0f);
//        ObjectAnimator animatorScale5 = ObjectAnimator.ofFloat(mBgView3, "scaleX", 1.0f, 1.3f);
//        ObjectAnimator animatorScale6 = ObjectAnimator.ofFloat(mBgView3, "scaleY", 1.0f, 1.3f);
//        AnimatorSet animatorSet3 = new AnimatorSet();
//        animatorSet3.setDuration(5000);
//        animatorSet3.addListener(new Animator.AnimatorListener() {
//            @Override
//            public void onAnimationStart(Animator animation) {
//            }
//            @Override
//            public void onAnimationEnd(Animator animation) {// 放大的View复位
//                mBgView1.setScaleX(1.0f);
//                mBgView1.setScaleY(1.0f);
//            }
//            @Override
//            public void onAnimationCancel(Animator animation) {
//            }
//            @Override
//            public void onAnimationRepeat(Animator animation) {
//            }
//        });
//        animatorSet3.play(animator5).with(animator6).with(animatorScale5).with(animatorScale6);
//        ObjectAnimator animator7 = ObjectAnimator.ofFloat(mBgView4, "alpha", 1.0f, 0f);
//        ObjectAnimator animator8 = ObjectAnimator.ofFloat(mBgView1, "alpha", 0f, 1.0f);
//        ObjectAnimator animatorScale7 = ObjectAnimator.ofFloat(mBgView4, "scaleX", 1.0f, 1.3f);
//        ObjectAnimator animatorScale8 = ObjectAnimator.ofFloat(mBgView4, "scaleY", 1.0f, 1.3f);
//        AnimatorSet animatorSet4 = new AnimatorSet();
//        animatorSet4.setDuration(5000);
//        animatorSet4.play(animator7).with(animator8).with(animatorScale7).with(animatorScale8);
//        AnimatorSet animatorSet = new AnimatorSet();
//        animatorSet.playSequentially(animatorSet1, animatorSet2, animatorSet3, animatorSet4);
//        animatorSet.addListener(new Animator.AnimatorListener() {
//            @Override
//            public void onAnimationStart(Animator animation) {
//            }
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                // 将放大的View 复位
//                mBgView2.setScaleX(1.0f);
//                mBgView2.setScaleY(1.0f);
//                mBgView3.setScaleX(1.0f);
//                mBgView3.setScaleY(1.0f);
//                mBgView4.setScaleX(1.0f);
//                mBgView4.setScaleY(1.0f);
//                // 循环播放
//                animation.start();
//            }
//            @Override
//            public void onAnimationCancel(Animator animation) {
//            }
//            @Override
//            public void onAnimationRepeat(Animator animation) {
//            }
//        });
//        animatorSet.start();
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( user.length()==0){
                    TopSnackBar.make(v, "请输入账号信息。", BaseTransientBottomBar.LENGTH_SHORT)
                            .setAction("确认",new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Toast.makeText(Login.this,"你点击了action",Toast.LENGTH_SHORT).show();
                        }
                    }).show();

                }
                else if( user.length()!=0&&password.length()==0){
                    TopSnackBar.make(v, "请输入账号信息。", BaseTransientBottomBar.LENGTH_SHORT)
                            .setAction("确认",new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //Toast.makeText(Login.this,"你点击了action",Toast.LENGTH_SHORT).show();
                                }
                            }).show();
                }
                else{
                    String name1=user.getText().toString();
                    String password1=password.getText().toString();
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
                                String sql="select * from User_info where BINARY user_name='"+name1+"'and password='"+password1+"'";
                                try {
                                    Connection conn = DriverManager.getConnection(url, user, password);
                                    Statement st = (Statement) conn.createStatement();
                                    ResultSet rs = st.executeQuery(sql);
                                    if (rs.next()) {
                                        s = rs.getString("user_name");
                                        handler.obtainMessage(123).sendToTarget();
                                    }
                                    else{
                                        handler.obtainMessage(124).sendToTarget();
                                    }
                                    rs.close();
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
        register.setOnClickListener(new View.OnClickListener() {  //当点击注册按钮的时候！
            @Override
            public void onClick(View v) {
                Bundle bundle=new Bundle();
                Intent intent1 = new Intent(Login.this,phone.class);
                String str1="register";
                bundle.putString("name",str1);
                intent1.putExtra("data",bundle);
                startActivity(intent1);  //传递参数判断此时的是什么登录界面，假如是注册的时候和登录的时候显示不一样的内容。
                Login.this.finish();
            }
        });
        forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog();
            }
        });
    }
    public void dialog(){
        Dialog mCameraDialog = new Dialog(this, R.style.my_dialog);
        LinearLayout root = (LinearLayout) LayoutInflater.from(this).inflate(
                R.layout.layout_camera_control, null);
        mCameraDialog.setContentView(root);
        Window dialogWindow = mCameraDialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        dialogWindow.setWindowAnimations(R.style.dialogstyle); // 添加动画
        WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        lp.x = 0; // 新位置X坐标
        lp.y = -20; // 新位置Y坐标
        lp.width = (int) getResources().getDisplayMetrics().widthPixels; // 宽度
//      lp.height = WindowManager.LayoutParams.WRAP_CONTENT; // 高度
//      lp.alpha = 9f; // 透明度
        root.measure(0, 0);
        lp.height = root.getMeasuredHeight();
        lp.alpha = 9f; // 透明度
        dialogWindow.setAttributes(lp);
        mCameraDialog.show();
        View.OnClickListener btnlistener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_open_camera: // 打开照相机
                        String str2="find";
                        Bundle bundle=new Bundle();
                        Intent intent2 = new Intent(Login.this,phone.class);
                        bundle.putString("name",str2);
                        intent2.putExtra("data",bundle);
                        startActivity(intent2);  //传递参数判断此时的是什么登录界面，假如是注册的时候和登录的时候显示不一样的内容。
                        Login.this.finish();
                        break;
                    // 打开相册
                    case R.id.btn_choose_img:
                        String str3="check";
                        Bundle bundle1=new Bundle();
                        Intent intent3 = new Intent(Login.this,phone.class);
                        bundle1.putString("name",str3);
                        intent3.putExtra("data",bundle1);
                        startActivity(intent3);  //传递参数判断此时的是什么登录界面，假如是注册的时候和登录的时候显示不一样的内容。
                        Login.this.finish();
                        break;
                    // 取消
                    case R.id.btn_cancel:
                        if (mCameraDialog != null) {
                            mCameraDialog.dismiss();
                        }
                        break;
                }
            }
        };
        root.findViewById(R.id.btn_open_camera).setOnClickListener(btnlistener);
        root.findViewById(R.id.btn_choose_img).setOnClickListener(btnlistener);
        root.findViewById(R.id.btn_cancel).setOnClickListener(btnlistener);
    }
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 123:   //登录成功的时候！
                    Intent intent=new Intent(Login.this,MainActivity.class);
                    dt =  new Date();
                    SharedPreferences player = getSharedPreferences("user", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = player.edit();//获取编辑器
                    editor.putString("user", s);
                    editor.putLong("time",dt.getTime());
                    editor.commit();//提交修改
                    setResult(0, intent);
                    Login.this.finish();
                    break;
                case 124:
                    Toast.makeText(Login.this, "请检查输入信息", Toast.LENGTH_SHORT).show();
                    break;
                default:break;
            }
        }
    };
}
