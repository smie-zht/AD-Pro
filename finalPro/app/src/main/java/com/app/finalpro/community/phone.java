package com.app.finalpro.community;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.finalpro.MainActivity;
import com.app.finalpro.R;

import org.json.JSONObject;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * @author donkor
 * csdn blog: http://blog.csdn.net/donkor_（昵称：donkor_）
 */
public class phone extends Activity implements View.OnClickListener {

    private Button btnSendMsg, btnSubmitCode;
    private EditText etPhoneNumber, etCode;
    private TextView tv,tv1;//得到输入的服务条款的控件
    private LinearLayout rtn;
    private int i = 60;//倒计时
    String name="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);
        tv=(TextView)findViewById(R.id.tv);
        tv1=(TextView)findViewById(R.id.text1);
        rtn=(LinearLayout)findViewById(R.id.rtn);
        Intent intentcheck=getIntent();
        Bundle bundle=intentcheck.getBundleExtra("data");
        name=bundle.getString("name");
        if(name.equals("register")){
            String str="注册即代表同意我们的<font color='#09A3C8'>使用条款</font>";
            tv.setTextSize(18);
            tv.setText(Html.fromHtml(str));
            tv.setOnClickListener(new View.OnClickListener() {  //当点击服务服务条款的时候！
                @Override
                public void onClick(View v) {
                    Intent intent1 = new Intent(phone.this,Rule.class);
                    startActivity(intent1);
                    phone.this.finish();
                }
            });
        }
        else if(name.equals("check")){
            tv1.setVisibility(View.GONE);
            String str1="请输入手机号码实现获取验证码登录。";
            tv.setTextSize(18);
            tv.setText(Html.fromHtml(str1));
        }
        else{
            tv1.setVisibility(View.GONE);
            String str1="请输入注册时的手机号码。";
            tv.setTextSize(18);
            tv.setText(Html.fromHtml(str1));
        }
//        tv.setVisibility(View.INVISIBLE);
        rtn.setOnClickListener(new View.OnClickListener() {  //取消注册返回界面
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(phone.this,Login.class);
                startActivity(intent1);
                phone.this.finish();
            }
        });
        etPhoneNumber = (EditText) findViewById(R.id.etPhoneNumber);
        etCode = (EditText) findViewById(R.id.etCode);
        btnSendMsg = (Button) findViewById(R.id.btnSendMsg);
        btnSubmitCode = (Button) findViewById(R.id.btnSubmitCode);

        // 启动短信验证sdk
        SMSSDK.initSDK(phone.this, "196e511258800", "ba98d7e9aa85eaa323cb4dc60435fd16");

        //initSDK方法是短信SDK的入口，需要传递您从MOB应用管理后台中注册的SMSSDK的应用AppKey和AppSecrete，如果填写错误，后续的操作都将不能进行
        EventHandler eventHandler = new EventHandler() {
            @Override
            public void afterEvent(int event, int result, Object data) {
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                handler.sendMessage(msg);
            }
        };
        //注册回调监听接口
        SMSSDK.registerEventHandler(eventHandler);

        btnSendMsg.setOnClickListener(this);
        btnSubmitCode.setOnClickListener(this);
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == -1) {
                btnSendMsg.setText(i + " s");
            } else if (msg.what == -2) {
                btnSendMsg.setText("重新发送");
                btnSendMsg.setClickable(true);
                i = 60;
            } else {
                int event = msg.arg1;
                int result = msg.arg2;
                Object data = msg.obj;
                Log.e("asd", "event=" + event + "  result=" + result + "  ---> result=-1 success , result=0 error");
                if (result == SMSSDK.RESULT_COMPLETE) {
                    // 短信注册成功后，返回MainActivity,然后提示
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE&&name.equals("register")) {
                        // 提交验证码成功,调用注册接口，之后直接登录
                        //当号码来自短信注册页面时调用登录注册接口
                        //当号码来自绑定页面时调用绑定手机号码接口
                        Log.v("AA","注册！");  //去注册界面
                        String phoneNumber = etPhoneNumber.getText().toString().trim();//得到输入的phone，用于注册使用！
                        Toast.makeText(getApplicationContext(), "短信验证成功，请输入注册信息！",
                                Toast.LENGTH_SHORT).show();
                        Intent intent1 = new Intent(phone.this,register.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("name", phoneNumber);
                        intent1.putExtra("data",bundle);
                        startActivity(intent1);
                        phone.this.finish();

                    }
                    else if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE&&name.equals("find")) {
                        // 提交验证码成功,调用注册接口，之后直接登录
                        //当号码来自短信注册页面时调用登录注册接口
                        //当号码来自绑定页面时调用绑定手机号码接口
                        Log.v("AA","修改密码！");//修改密码界面
                        String phoneNumber = etPhoneNumber.getText().toString().trim();//得到输入的phone，用于注册使用！
                        Toast.makeText(getApplicationContext(), "短信验证成功，请输入新的密码",
                                Toast.LENGTH_SHORT).show();
//                        Intent intent1 = new Intent(phone.this,revise.class);
//                        Bundle bundle = new Bundle();
//                        bundle.putString("name", phoneNumber);
//                        intent1.putExtra("data",bundle);
//                        startActivity(intent1);
//                        phone.this.finish();
                    }
                    else if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE&&name.equals("check2")) {
                        // 提交验证码成功,调用注册接口，之后直接登录
                        //当号码来自短信注册页面时调用登录注册接口
                        //当号码来自绑定页面时调用绑定手机号码接口
                        String phoneNumber = etPhoneNumber.getText().toString().trim();//得到输入的phone，用于注册使用！
                        Toast.makeText(getApplicationContext(), "登录成功！",
                                Toast.LENGTH_SHORT).show(); //使用短信验证登录的时候直接登录成功。
                        Intent intent1 = new Intent(phone.this,MainActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("name", phoneNumber);
                        intent1.putExtra("data",bundle);
                        startActivity(intent1);
                        phone.this.finish();
                    }
                    else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        Toast.makeText(getApplicationContext(), "验证码已经发送",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        ((Throwable) data).printStackTrace();
                    }
                } else if (result == SMSSDK.RESULT_ERROR) {
                    try {
                        Throwable throwable = (Throwable) data;
                        throwable.printStackTrace();
                        JSONObject object = new JSONObject(throwable.getMessage());
                        String des = object.optString("detail");//错误描述
                        int status = object.optInt("status");//错误代码
                        if (status > 0 && !TextUtils.isEmpty(des)) {
                            Log.e("asd", "des: " + des);
                            Toast.makeText(phone.this, des, Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } catch (Exception e) {
                        //do something
                    }
                }
            }
        }
    };

    @Override
    public void onClick(View v) {
        String phoneNum = etPhoneNumber.getText().toString().trim();
        switch (v.getId()) {
            case R.id.btnSendMsg:
                if (TextUtils.isEmpty(phoneNum)) {
                    Toast.makeText(getApplicationContext(), "手机号码不能为空",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                SMSSDK.getVerificationCode("86", phoneNum);
                btnSendMsg.setClickable(false);
                //开始倒计时
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (; i > 0; i--) {
                            handler.sendEmptyMessage(-1);
                            if (i <= 0) {
                                break;
                            }
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        handler.sendEmptyMessage(-2);
                    }
                }).start();
                break;
            case R.id.btnSubmitCode:
                String code = etCode.getText().toString().trim();
                if (TextUtils.isEmpty(phoneNum)) {
                    Toast.makeText(getApplicationContext(), "手机号码不能为空",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(code)) {
                    Toast.makeText(getApplicationContext(), "验证码不能为空",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                SMSSDK.submitVerificationCode("86", phoneNum, code);
                break;
            default:
                break;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 销毁回调监听接口
        SMSSDK.unregisterAllEventHandler();
    }
}



