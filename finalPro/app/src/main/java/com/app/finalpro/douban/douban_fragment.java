package com.app.finalpro.douban;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.SearchView;
import android.widget.TextView;

import com.app.finalpro.R;
import com.app.finalpro.douban.douban.UltraPagerAdapter;
import com.app.finalpro.home.mainSearch;
import com.bumptech.glide.Glide;
import com.tmall.ultraviewpager.UltraViewPager;
import com.tmall.ultraviewpager.transformer.UltraDepthScaleTransformer;

import java.lang.reflect.Field;

import static java.lang.Math.abs;

/**
 * Created by ZXG on 2018/1/7.
 */

public class douban_fragment extends Fragment {
    ConstraintLayout clayout;
    ConstraintLayout top1cardall;
    ImageView clrglide;
    SearchView sv1;
    UltraViewPager.Orientation gravity_indicator;
    UltraViewPager ultraViewPager;
    UltraPagerAdapter adapter;
    TextView dialogTitle0;
    TextView dialogAuthor0;
    TextView dialogPublisher0;
    TextView dialogRating0;
    TextView dialogNumRaters0;
    TextView dialogSummary0;
    TextView dialogCancel0;
    ImageView dialogCover0;
    RatingBar dialogRatingbar0;
    Button dialogBack0;
    Button dialogLibrary0;
    View rootView;
    

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        rootView = inflater.inflate(R.layout.activity_douban, null);

        top1cardall = (ConstraintLayout) rootView.findViewById(R.id.top1cardall);
        clayout = (ConstraintLayout) rootView.findViewById(R.id.outlayout);
        clrglide = (ImageView) rootView.findViewById(R.id.clearglide);
        sv1 = (SearchView) rootView.findViewById(R.id.sv);
        //清理图片缓存
        clrglide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sv1.clearFocus();
                clearImageAllCache(getActivity());
            }
        });
        //点击外部layout部分让搜索框失去焦点，收起键盘
        clayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                sv1.clearFocus();
                clayout.requestFocus();
                InputMethodManager imm = (InputMethodManager)
                        getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return false;
            }
        });

        // sv1 = (SearchView) rootView.findViewById(R.id.sv);
        //搜索框不可收起，输入完点"搜索"跳转到搜索页，并且向页面传搜索内容
        sv1.setIconifiedByDefault(false);
        sv1.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent todbsearch = new Intent(getActivity(),DoubanSearch.class);
                Bundle searchq = new Bundle();
                searchq.putString("q",query);
                todbsearch.putExtras(searchq);
                startActivityForResult(todbsearch,2);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        //对search view进行动态的修改央视，增加搜索图标的点击事件
        if (sv1 != null) {
            try {        //--拿到字节码
                Class<?> argClass = sv1.getClass();
                //--指定某个私有属性,mSearchPlate是搜索框父布局的名字
                Field ownField = argClass.getDeclaredField("mSearchPlate");
                //--暴力反射,只有暴力反射才能拿到私有属性
                ownField.setAccessible(true);
                View mView = (View) ownField.get(sv1);
                //--设置背景
                mView.setBackgroundResource(R.drawable.searchview_line);
                //获取到TextView的ID
                int id = sv1.getContext().getResources().getIdentifier("android:id/search_src_text",null,null);
                //获取到TextView的控件
                final TextView textView = (TextView) sv1.findViewById(id);
                textView.setPadding(45,0,0,0);
                //设置字体大小为14sp
                // textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);//14sp
                //设置字体颜色
                //textView.setTextColor(getResources().getColor(R.color.colorAccent));
                //设置提示文字颜色
                //textView.setHintTextColor(getActivity().getResources().getColor(R.color.search_hint_color));
                int search_mag_icon_id = sv1.getContext().getResources().getIdentifier("android:id/search_mag_icon", null, null);
                ImageView mSearchViewIcon = (ImageView) sv1.findViewById(search_mag_icon_id);// 获取搜索图标
                mSearchViewIcon.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        Intent todbsearch = new Intent(getActivity(),DoubanSearch.class);
                        Bundle searchq = new Bundle();
                        searchq.putString("q",textView.getText().toString());
                        todbsearch.putExtras(searchq);
                        startActivityForResult(todbsearch,3);
                        return false;
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //TOP10图书展示区
        ultraViewPager = (UltraViewPager)rootView.findViewById(R.id.ultra_viewpager);
        ultraViewPager.setPageTransformer(false, new UltraDepthScaleTransformer());
        ultraViewPager.setScrollMode(UltraViewPager.ScrollMode.HORIZONTAL);
        adapter = new UltraPagerAdapter(getActivity(),true);
        ultraViewPager.setAdapter(adapter);
        ultraViewPager.setMultiScreen(0.6f);
        ultraViewPager.setItemRatio(1.0f);
        ultraViewPager.setAutoMeasureHeight(false);
        gravity_indicator = UltraViewPager.Orientation.HORIZONTAL;
        ultraViewPager.setPageTransformer(false, new UltraDepthScaleTransformer());
        //使用OnTouch模拟viewpager的点击事件，点击每一页现实当前图书的详情
        ultraViewPager.getViewPager().setOnTouchListener(new View.OnTouchListener() {
            int flage = 0 ;
            int x = 0;
            int y = 0;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                sv1.clearFocus();
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:

                        x = (int)event.getX();
                        y = (int)event.getY();
                        break ;
                    case MotionEvent.ACTION_MOVE:

                        break ;
                    case  MotionEvent.ACTION_UP :
                        int upx = abs((int)event.getX()-x);
                        int upy = abs((int)event.getY()-y);
                        Log.v("chang",String.valueOf(upy));
                        if (upx<20&&upy<20) {
                            int item = ultraViewPager.getViewPager().getCurrentItem();
                            makeDetailDialog(item);
                        }
                        break ;

                }
                return false;
            }
        });
        //点击上方第一名的卡片会显示详情
        top1cardall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sv1.clearFocus();
                makeDetailDialog(0);
            }
        });
        return rootView;
    }
    //根据ViewPager的每一页来取相关信息，显示TOP10榜单的图书详情页
    public void makeDetailDialog(int item) {
        LayoutInflater factor = LayoutInflater.from(getActivity());
        View view_in = factor.inflate(R.layout.douban_book_detail,null);
        AlertDialog.Builder detailbuilder = new AlertDialog.Builder(getActivity());
        detailbuilder.setView(view_in);
        final AlertDialog dialog = detailbuilder.create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.alert_dialog);
        dialog.getWindow().setLayout(DensityUtil.dip2px(getActivity(),320),DensityUtil.dip2px(getActivity(),500));
        dialogCancel0 = view_in.findViewById(R.id.dbdetailcancel);
        dialogBack0 = view_in.findViewById(R.id.dbdetailback);
        dialogTitle0 = view_in.findViewById(R.id.dbdetailtitle);
        dialogAuthor0 = view_in.findViewById(R.id.dbdetailauthor);
        dialogSummary0 = view_in.findViewById(R.id.dbdetailsummary);
        dialogPublisher0 = view_in.findViewById(R.id.dbdetailpublisher);
        dialogRating0 = view_in.findViewById(R.id.dbdetailrating);
        dialogNumRaters0 = view_in.findViewById(R.id.dbdetailnumraters);
        dialogRatingbar0 = view_in.findViewById(R.id.dbdetailratingBar);
        dialogCover0 = view_in.findViewById(R.id.dbdetailcover);
        dialogLibrary0 = view_in.findViewById(R.id.dbdetail2library);
        dialogLibrary0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent  intent = new Intent(getActivity(),mainSearch.class);
                intent.putExtra("key",dialogTitle0.getText());
                startActivity(intent);
            }
        });
        /***************************************************************************************************************
         此处需要添加按钮FIND IN LIBRARY的按键点击监听事件，点击到图书馆内的搜索页面（看情况是否可以设置搜索框的内容为图书标题/剪切板拷贝标题文字
         */
        View.OnClickListener dismiss = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        };
        dialogBack0.setOnClickListener(dismiss);
        dialogCancel0.setOnClickListener(dismiss);
        String dtitle = adapter.getTitle(item);
        String dauthor = adapter.getAuthor(item);
        String dpublisher = adapter.getPublisher(item);
        if(dtitle.length()>=25)
            dialogTitle0.setTextSize(15f);
        else
            dialogTitle0.setTextSize(20f);
        if(dauthor.length()>=14)
            dialogAuthor0.setTextSize(10f);
        else
            dialogAuthor0.setTextSize(14f);
        if(dpublisher.length()>=14)
            dialogPublisher0.setTextSize(10f);
        else
            dialogPublisher0.setTextSize(13f);
        dialogTitle0.setTextSize(20f);
        dialogAuthor0.setTextSize(14f);
        dialogPublisher0.setTextSize(13f);
        dialogTitle0.setText(dtitle);
        dialogAuthor0.setText(dauthor);
        dialogPublisher0.setText(dpublisher);
        String numraters = String.valueOf(adapter.getNumraters(item))+"人评分";
        dialogNumRaters0.setText(numraters);
        dialogSummary0.setText(adapter.getSummaryRes(item));
        dialogSummary0.setMovementMethod(ScrollingMovementMethod.getInstance());
        dialogRating0.setText(String.valueOf(adapter.getRating(item)));
        float dstar = adapter.getRating(item)/2;
        dialogRatingbar0.setRating(dstar);
        dialogCover0.setImageResource(adapter.getCoverRes(item));
    }
    //点击清理按钮。显示AlertDialog提示清理图片缓存
    public void clearImageAllCache(final Context context) {
        AlertDialog clrdialog = new AlertDialog.Builder(getActivity())
                .setIcon(R.drawable.clean_blue_circle)
                .setTitle("清理图片缓存")
                .setPositiveButton("清理", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Glide.get(context).clearMemory();
                        new Thread() {
                            @Override
                            public void run() {
                                Glide.get(context).clearDiskCache();
                            }
                        }.start();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setMessage("图片缓存会占用磁盘和内存，建议定时清理。")
                .create();
        clrdialog.show();
    }

}
