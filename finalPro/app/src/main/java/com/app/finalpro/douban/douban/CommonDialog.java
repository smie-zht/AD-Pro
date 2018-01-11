package com.app.finalpro.douban.douban;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.finalpro.R;


/**
 * Created by user on 2017/12/31.
 */

public class CommonDialog extends Dialog implements View.OnClickListener{
    private TextView contentTxt;
    private TextView titleTxt;
    private TextView submitTxt;
    private TextView cancelTxt;
    public EditText tagTxt;

    private Context mContext;
    private String content;
    private OnCloseListener listener;
    private String positiveName;
    private String negativeName;
    private String title;
    private String tag;

    public CommonDialog(Context context) {
        super(context);
        this.mContext = context;
    }

    public CommonDialog(Context context, int themeResId, String content) {
        super(context, themeResId);
        this.mContext = context;
        this.content = content;
    }

    public CommonDialog(Context context, int themeResId, String content, OnCloseListener listener) {
        super(context, themeResId);
        this.mContext = context;
        this.content = content;
        this.listener = listener;
    }

    protected CommonDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
    }

    public CommonDialog setTitle(String title){
        this.title = title;
        return this;
    }

    public CommonDialog setTag(String tag){
        this.tag = tag;
        return this;
    }

    public CommonDialog setPositiveButton(String name){
        this.positiveName = name;
        return this;
    }

    public CommonDialog setNegativeButton(String name){
        this.negativeName = name;
        return this;
    }
    public String getTag() {
        return tagTxt.getText().toString();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_common);
        setCanceledOnTouchOutside(false);

        initView();
    }

    private void initView(){
        contentTxt = (TextView)findViewById(R.id.contenttxt);
        contentTxt.setOnClickListener(this);
        titleTxt = (TextView)findViewById(R.id.titletxt);
        titleTxt.setOnClickListener(this);
        submitTxt = (TextView)findViewById(R.id.submitbtn);
        submitTxt.setOnClickListener(this);
        cancelTxt = (TextView)findViewById(R.id.cancelbtn);
        cancelTxt.setOnClickListener(this);
        tagTxt = (EditText) findViewById(R.id.tagtxt);
        //tagTxt.setOnKeyListener(this);
      //  tagTxt.setOnClickListener(this);
       // tagTxt.setOnKeyListener(this);

        contentTxt.setText(content);
        if(!TextUtils.isEmpty(positiveName)){
            submitTxt.setText(positiveName);
        }

        if(!TextUtils.isEmpty(negativeName)){
            cancelTxt.setText(negativeName);
        }

        if(!TextUtils.isEmpty(title)){
            titleTxt.setText(title);
        }

        if(!TextUtils.isEmpty(tag)){
            tagTxt.setText(tag);
        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.cancelbtn:
                if(listener != null){
                    listener.onClick(v,this, 1);
                }
                this.dismiss();
                break;
            case R.id.submitbtn:
                if(listener != null){
                    titleTxt.requestFocus();
                    listener.onClick(v,this, 2);
                }
                break;
            case R.id.contenttxt:
                if(listener != null){
                    titleTxt.requestFocus();
                    listener.onClick(v,this, 0);
                }
                break;
            case R.id.titletxt:
                if(listener != null){
                    titleTxt.requestFocus();
                    listener.onClick(v,this, 0);
                }
                break;
        }
    }
    public void loseFocus() {
        titleTxt.requestFocus();
    }
    public interface OnCloseListener{
        void onClick(View v, CommonDialog dialog, int confirm);
    }
}
