package com.app.finalpro.community;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

import com.app.finalpro.R;

public class Rule extends AppCompatActivity {
    private LinearLayout RTT1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rule);
        RTT1=(LinearLayout)findViewById(R.id.year);
        RTT1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle=new Bundle();
                Intent intent1 = new Intent(Rule.this,phone.class);
                String str1="register";
                bundle.putString("name",str1);
                intent1.putExtra("data",bundle);
                startActivity(intent1);  //传递参数判断此时的是什么登录界面，假如是注册的时候和登录的时候显示不一样的内容。
                Rule.this.finish();
            }
        });
    }
}
