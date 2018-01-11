package com.app.finalpro.douban;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;


import com.app.finalpro.R;
import com.app.finalpro.douban.douban.AniUtils;
import com.app.finalpro.douban.douban.CommonDialog;
import com.app.finalpro.douban.douban.DbListAdpt;
import com.app.finalpro.douban.douban.Douban;
import com.app.finalpro.douban.douban.DoubanService;
import com.app.finalpro.douban.douban.DoubanServiceFactory;
import com.app.finalpro.douban.douban.EndLessOnScrollListener;
import com.app.finalpro.home.mainSearch;
import com.bumptech.glide.Glide;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Retrofit;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class DoubanSearch extends AppCompatActivity {
    TextView dialogTitle;
    TextView dialogAuthor;
    TextView dialogPublisher;
    TextView dialogRating;
    TextView dialogNumRaters;
    TextView dialogSummary;
    TextView dialogCancel;
    ImageView dialogCover;
    RatingBar dialogRatingbar;
    Button dialogBack;
    Button dialogLibrary;
    SearchView sv2;
    ConstraintLayout clayout2;
    ImageButton backbtn;
    ImageButton filterbtn;
    CommonDialog tagDialog;
    TextView tagLeft;
    TextView tagRight;
    String tag;
    LinearLayout taglayout;
    LinearLayout waitlayout;
    DoubanService dbservice;
    DbListAdpt dbListAdpt;
    RecyclerView dbRecylerView;
    ArrayList<Douban.Book> dbbookList;
    List<Douban.Book> book;
    boolean iftag; //判断是根据tag搜索还是关键词搜索

    //用于控制标签的显示与否以及其动画
    private static final int MSG_DISMISS_INDEXVIEW = 0;
    private static final int MSG_SHOW_INDEXVIEW = 1;
    Handler mIndexViewVisibilityHandler  = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what){
                case MSG_DISMISS_INDEXVIEW:
                    AniUtils.showAndHiddenAnimation(taglayout, AniUtils.AnimationState.STATE_HIDDEN,500);
                    break;
                case MSG_SHOW_INDEXVIEW:
                    AniUtils.showAndHiddenAnimation(taglayout, AniUtils.AnimationState.STATE_SHOW,300);
                    break;
            }
        }
    };
    //一次搜索加载20条，offset*20得到搜索的起始位置，totalbook记录用户一次搜索的搜索结果数量
    private static final int SEARCH_COUNT = 20;
    private static int offset = 0;
    private static int totalbook = 0;

    // 用于上拉加载更多，并且更新视图
    private LinearLayoutManager mLinearLayoutManager;
    private Handler mhandler=null;
    EndLessOnScrollListener endLessOnScrollListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_douban_search);
        waitlayout = (LinearLayout) findViewById(R.id.waitlayout);
        sv2 = (SearchView) findViewById(R.id.sv2);
        clayout2 = (ConstraintLayout) findViewById(R.id.clayout2);
        backbtn = (ImageButton) findViewById(R.id.backbtn);
        filterbtn = (ImageButton) findViewById(R.id.filterbtn);
        tagLeft = (TextView) findViewById(R.id.tagleft);
        tagRight = (TextView) findViewById(R.id.tagright);
        taglayout = (LinearLayout) findViewById(R.id.taglayout);
        dbRecylerView = (RecyclerView) findViewById(R.id.dbRecycleView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        dbRecylerView.setLayoutManager(mLinearLayoutManager);
        dbbookList = new ArrayList<Douban.Book>();
        dbListAdpt = new DbListAdpt(DoubanSearch.this,dbbookList);
        dbRecylerView.setAdapter(dbListAdpt);
        mhandler=new Handler();

        waitlayout.setVisibility(View.GONE);
        iftag = false;
        offset = 0;
        totalbook = 0;
        tag = "";
        //需要从别的页面接受数据才能正常运行
        Bundle sbundle = getIntent().getExtras();
        String q = sbundle.getString("q");
        if(q == null) {
            finish();
        }
        else if(!q.trim().equals("")) //传入搜索内容不为空则直接进行一次搜索
            search(q,1,offset*SEARCH_COUNT);
        if(tag.equals(""))
            taglayout.setVisibility(View.INVISIBLE);


        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //右上角的筛选标签按键
        filterbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReviseTagDialog();

            }
        });
        //点击小标签右边的叉删除tag
        tagRight.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ClearTag();
                return false;
            }
        });
        //点击布局其他部分收起键盘，searchview失去焦点
        clayout2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                clayout2.requestFocus();
                InputMethodManager imm = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return false;
            }
        });
        sv2.setIconified(false);
        sv2.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterbtn.setBackgroundResource(R.drawable.filter1);
                offset = 0;
                if(dbListAdpt.getItemCount()!=0)
                    dbListAdpt.clearData();
                endLessOnScrollListener.clearPreviousTotal();
                search(query,1,offset*SEARCH_COUNT);
                if(iftag) {
                    tag = "";
                    iftag = false;
                    Message msg = Message.obtain();
                    msg.what = MSG_DISMISS_INDEXVIEW;
                    mIndexViewVisibilityHandler.sendMessageDelayed(msg,100);
                }
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        dbRecylerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                sv2.clearFocus();
                return false;
            }
        });
        //上拉加载更多监听
        endLessOnScrollListener = new EndLessOnScrollListener(mLinearLayoutManager) {
            @Override
            public void onLoadMore() {
                if(totalbook > dbListAdpt.getItemCount()){
                    loadMoreData();
                }
                else if(dbListAdpt.getItemCount() >= totalbook)
                    Toast.makeText(DoubanSearch.this,"已经到达 底部",Toast.LENGTH_SHORT).show();
            }
        };
        dbRecylerView.addOnScrollListener(endLessOnScrollListener);

        //对searchview的动态更改样式
        if (sv2 != null) {
            try {
                Class<?> argClass = sv2.getClass();
                Field ownField = argClass.getDeclaredField("mSearchPlate");
                ownField.setAccessible(true);
                View mView = (View) ownField.get(sv2);
                mView.setBackgroundResource(R.drawable.searchview_line);
                int id = sv2.getContext().getResources().getIdentifier("android:id/search_src_text",null,null);
                TextView textView = (TextView) sv2.findViewById(id);
                textView.setText(q);
                textView.clearFocus();
                textView.setPadding(45,0,0,0);
               // textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //点击RecycleView每一项显示对应详情对话框
        dbListAdpt.setOnItemClickListener(new DbListAdpt.OnItemClickListener() {
            @Override
            public void onLongClick(int position) {
                //Toast.makeText(DoubanSearch.this,dbbookList.get(position).getAverageRating(),Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onClick(int position) {
                LayoutInflater factor = LayoutInflater.from(DoubanSearch.this);
                View view_in = factor.inflate(R.layout.douban_book_detail,null);
                AlertDialog.Builder detailbuilder = new AlertDialog.Builder(DoubanSearch.this);
                detailbuilder.setView(view_in);
                final AlertDialog dialog = detailbuilder.create();
                dialog.show();
                dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.alert_dialog));
                dialog.getWindow().setLayout(DensityUtil.dip2px(DoubanSearch.this,320),DensityUtil.dip2px(DoubanSearch.this,500));
                dialogCancel = view_in.findViewById(R.id.dbdetailcancel);
                dialogBack = view_in.findViewById(R.id.dbdetailback);
                dialogTitle = view_in.findViewById(R.id.dbdetailtitle);
                dialogAuthor = view_in.findViewById(R.id.dbdetailauthor);
                dialogSummary = view_in.findViewById(R.id.dbdetailsummary);
                dialogPublisher = view_in.findViewById(R.id.dbdetailpublisher);
                dialogRating = view_in.findViewById(R.id.dbdetailrating);
                dialogNumRaters = view_in.findViewById(R.id.dbdetailnumraters);
                dialogRatingbar = view_in.findViewById(R.id.dbdetailratingBar);
                dialogCover = view_in.findViewById(R.id.dbdetailcover);
                dialogLibrary = view_in.findViewById(R.id.dbdetail2library);
                dialogLibrary.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent  intent = new Intent(DoubanSearch.this,mainSearch.class);
                        intent.putExtra("key",dialogTitle.getText());
                        startActivity(intent);
                    }
                });

                View.OnClickListener dismiss = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                };
                dialogBack.setOnClickListener(dismiss);
                dialogCancel.setOnClickListener(dismiss);
                String dtitle = dbbookList.get(position).getTitle();
                String dauthor = dbbookList.get(position).getAllAuthor();
                String dpublisher = dbbookList.get(position).getPublisher();
                if(dtitle.length()>=25)
                    dialogTitle.setTextSize(15f);
                else
                    dialogTitle.setTextSize(20f);
                if(dauthor.length()>=14)
                    dialogAuthor.setTextSize(10f);
                else
                    dialogAuthor.setTextSize(14f);
                if(dpublisher.length()>=14)
                    dialogPublisher.setTextSize(10f);
                else
                    dialogPublisher.setTextSize(13f);
                dialogTitle.setText(dtitle);
                dialogAuthor.setText(dauthor);
                dialogPublisher.setText(dpublisher);
                dialogNumRaters.setText(dbbookList.get(position).getNumRaters());
                dialogSummary.setText(dbbookList.get(position).getSummary());
                dialogSummary.setMovementMethod(ScrollingMovementMethod.getInstance());
                dialogRating.setText(dbbookList.get(position).getAverageRating());
                float dstar = Float.parseFloat(dbbookList.get(position).getAverageRating())/2;
                dialogRatingbar.setRating(dstar);
                Glide.with(DoubanSearch.this).load(dbbookList.get(position).getImageURL()).into(dialogCover);
            }
        });
    }
    //删除标签
    private void ClearTag() {
        filterbtn.setBackgroundResource(R.drawable.filter1);
        tag = "";
        iftag = false;
        sv2.setIconified(false);
        setSvText("");
        Message msg = Message.obtain();
        msg.what = MSG_DISMISS_INDEXVIEW;
        mIndexViewVisibilityHandler.sendMessageDelayed(msg,100);
    }
    //修改搜索框内文字显示
    private void setSvText(String str) {
        try {
            Class<?> argClass = sv2.getClass();
            Field ownField = argClass.getDeclaredField("mSearchPlate");
            ownField.setAccessible(true);
            View mView = (View) ownField.get(sv2);
            mView.setBackgroundResource(R.drawable.searchview_line);
            int id = sv2.getContext().getResources().getIdentifier("android:id/search_src_text",null,null);
            TextView textView = (TextView) sv2.findViewById(id);
            textView.setText(str);
            textView.clearFocus();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //右上方修改标签的对话框
    private void ReviseTagDialog() {
        tagDialog = new CommonDialog(DoubanSearch.this, R.style.dialog, "当前标签:", new CommonDialog.OnCloseListener() {
        @Override
        public void onClick(View v, CommonDialog dialog, int confirm) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            if(confirm == 2) {
                if(dialog.getTag().trim().equals("")) {
                    ClearTag();
                    dialog.dismiss();
                }
                else {
                    if(iftag == false) {
                        iftag = true;
                        setSvText("");
                        sv2.setIconified(true);
                        filterbtn.setBackgroundResource(R.drawable.filter2);
                    }
                    tag = dialog.getTag();

                    Toast.makeText(DoubanSearch.this,"搜索图书标签:"+dialog.getTag(),Toast.LENGTH_SHORT).show();
                    tagLeft.setText(tag);
                    if(taglayout.getVisibility() == View.INVISIBLE){
                        Message msg = Message.obtain();
                        msg.what = MSG_SHOW_INDEXVIEW;
                        mIndexViewVisibilityHandler.sendMessageDelayed(msg,100);
                    }
                    offset = 0;
                    if(dbListAdpt.getItemCount()!=0)
                        dbListAdpt.clearData();
                    endLessOnScrollListener.clearPreviousTotal();
                    search(tagLeft.getText().toString(),2,offset*SEARCH_COUNT);
                    dialog.dismiss();
                }
            }
        }
        }).setTitle("按照图书标签检索");
        tagDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
          @Override
          public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
              if (keyCode==KeyEvent.KEYCODE_ENTER && event.getAction()==KeyEvent.ACTION_DOWN) {
                  tagDialog.loseFocus();
              }
              return false;
          }
        });
        if(tag!=null) {
            tagDialog.setTag(tag);
        }
        tagDialog.show();
    }

    //type 1-q-关键词搜索 2-tag-标签搜索
    public void search(String searchtext, int type ,int startoffset) {
        if (searchtext.trim().equals("")){
            Toast.makeText(DoubanSearch.this,"请输入关键词搜索",Toast.LENGTH_SHORT).show();
            return;
        }
        waitlayout.setVisibility(View.VISIBLE);
        Retrofit retrofit = DoubanServiceFactory.createRetrofit();
        dbservice = retrofit.create(DoubanService.class);
        if(type == 1 ) {
            dbservice.getSearchByQstart(searchtext,startoffset)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<Douban>() {
                        @Override
                        public void onCompleted() {
                            //System.out.println(book.size());
                            new Thread() {
                                public void run() {
                                    Message msg =new Message();
                                    try {
                                        for (int i = 0 ;i<book.size();i++) {
                                            Douban.Book tmp = book.get(i);
                                            dbbookList.add(tmp);
                                        }
                                        mhandler.post(runnableUiOfRV);
                                        msg.what = 3;
                                    } catch (Exception e) {    System.out.println(e.getMessage());    }
                                    mIndexViewVisibilityHandler.sendMessage(msg);
                                }
                            }.start();
                        }
                        @Override
                        public void onError(Throwable e) {
                            Toast.makeText(DoubanSearch.this, e.hashCode() + "请确认douban", Toast.LENGTH_SHORT)
                                    .show();
                        }
                        @Override
                        public void onNext(Douban douban) {
                            totalbook = douban.total;
                            if(totalbook <= 0)
                                Toast.makeText(DoubanSearch.this,"没有找到相关书籍",Toast.LENGTH_SHORT).show();
                            book = douban.books;
                        }
                    });
        }
        else if(type == 2) {
            dbservice.getSearchByTstart(searchtext,startoffset)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<Douban>() {
                        @Override
                        public void onCompleted() {
                            System.out.println(book.size());
                            new Thread() {
                                public void run() {
                                    try {
                                        for (int i = 0 ;i<book.size();i++) {
                                            Douban.Book tmp = book.get(i);
                                            dbbookList.add(tmp);
                                        }
                                        mhandler.post(runnableUiOfRV);
                                    } catch (Exception e) {     System.out.println(e.getMessage());     }
                                }
                            }.start();
                        }
                        @Override
                        public void onError(Throwable e) {
                            Toast.makeText(DoubanSearch.this, e.hashCode() + "请确认douban", Toast.LENGTH_SHORT)
                                    .show();
                        }
                        @Override
                        public void onNext(Douban douban) {
                            totalbook = douban.total;
                            book = douban.books;
                            if(totalbook <= 0)
                                Toast.makeText(DoubanSearch.this,"没有找到相关书籍",Toast.LENGTH_SHORT).show();

                        }
                    });
        }
        offset++;//搜索开始偏移量加一
    }
    Runnable runnableUiOfRV=new  Runnable(){
        @Override
        public void run() {
            //更新界面
            dbListAdpt.notifyDataSetChanged();
            waitlayout.setVisibility(View.GONE);
            String hint1 = "相关条目共"+String.valueOf(totalbook)+"个";
            String hint2 = "，建议增加关键词精准查找噢";
            if(offset == 1 )
                Toast.makeText(DoubanSearch.this,totalbook<=200?hint1:hint1+hint2,Toast.LENGTH_SHORT).show();
        }
    };
    private void loadMoreData(){
        if(!iftag) {
            search(sv2.getQuery().toString(),1,offset*SEARCH_COUNT);
        }
        else {
            search(tagLeft.getText().toString(),2,offset*SEARCH_COUNT);
        }

    }
}
