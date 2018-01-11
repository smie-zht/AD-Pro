package com.app.finalpro.home;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.finalpro.home.Adapter.bookAdapter;
import com.app.finalpro.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class mainSearch extends AppCompatActivity {
    private String key;

    private ImageView back;
    private SearchView search;
    private TextView tip;
    //数据展示
    private List<Map<String,Object>> bookList = new ArrayList<>();//数据源
    private bookAdapter adapter; //适配器
    private RecyclerView recyclerView; //展示视图

    //助手
    public Handler handler;
    public  Thread thread;
    public String us;  //当前登录的用户

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //获取参数
        Intent intent = getIntent();
        String dir = intent.getStringExtra("dir");
        key = intent.getStringExtra("key");
        //过渡动画
//        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
//        Transition slide;
//        if(dir.equals("left"))
//            slide = TransitionInflater.from(this).inflateTransition(R.transition.leftslide);
//        else
//            slide = TransitionInflater.from(this).inflateTransition(R.transition.rightslide);
        
        setContentView(R.layout.activity_main_search);
        init();
        buttons();
    }

    private void init() {

        bindView(); //绑定视图
        UIhand();//配置UI助手
        cnet(key); //初始化搜索结果
    }
    //两行视图的绑定
    private void bindView() {
        back = (ImageView)findViewById(R.id.back);
        search = (SearchView)findViewById(R.id.search);
        tip = (TextView)findViewById(R.id.tip);
        recyclerView = (RecyclerView)findViewById(R.id.searchRecV);
        adapter = new bookAdapter(mainSearch.this,R.layout.news_item,bookList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    //handler任务-101：更新查询内容
    public void UIhand(){
        //助手
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 101:   //更新推荐
                            tip.setVisibility(View.GONE);
                            if(loadSearch()){
                                Log.v("任务","执行");
                                thread.interrupt();
                            }
                        break;
                    case 102:  //提升查找不到
                        tip.setVisibility(View.VISIBLE);
                        thread.interrupt();
                        break;

                }
            }
        };
    }
    //任务101-子程序
    private boolean loadSearch() {
        adapter.notifyDataSetChanged();
        return true;
    }

    //新线程:查询远程数据库
    public void cnet(String type){
        thread = new Thread(new Runnable(){
            @Override
            public void run() {
                while (!Thread.interrupted()){
                    try {
                        Thread.sleep(100);  // 每隔0.1秒尝试连接
                        Class.forName("com.mysql.jdbc.Driver");//找到jar包的位置
                    } catch (InterruptedException e) {
                        Log.v("findJar", e.toString());
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

                    // 3.连接JDBC，获取数据。
                    try {
                        Connection conn = DriverManager.getConnection(url, user, password);
                        Log.v("ss", "远程连接成功!");
                        Statement st = (Statement) conn.createStatement();
                        //初始化
                        if(type!=null){
                            Log.v("要查询的",type);
                            //String sql = "select * from book where bookname like '%"+type+"%'";
                            String sql = "select bookname,coverimg,publisher,ISBN,summary,author_name,totalnum,restnum"
                                    +" from book,author where bookname like '%"+type+"%'"
                                    +" and book.author_id = author.author_id";
                            ResultSet rs = st.executeQuery(sql);
                            String base_url = "http://zhyhhh-1255732607.cosgz.myqcloud.com/image/bookimage/";
                            boolean nothing = true;
                                while (rs.next()) {  //直接处理结果
                                    nothing = false;
                                    Log.v("查询结果",rs.getString("bookname"));
                                    Map<String,Object> tem = new HashMap<>();

                                  //  URL t = new URL(base_url+rs.getString("coverImg"));
                                //    InputStream is = t.openStream();
                                  //  Bitmap bitmap = BitmapFactory.decodeStream(is);
                                    String coverURL = base_url+rs.getString("coverImg");
                                    String bkName = rs.getString("bookname");
                                    String bookInfoSrc = "作者："+rs.getString("author_name") //信息
                                            +"\n出版社："+rs.getString("publisher");
//                                            +"\nISBN："+rs.getString("ISBN");

                                    tem.put("bookPic",coverURL);
                                    tem.put("bookName",bkName);
                                    tem.put("bookInfo",bookInfoSrc);
                                    bookList.add(tem);
                                }
                                if(nothing){
                                    handler.obtainMessage(102).sendToTarget();
                                }else{
                                    handler.obtainMessage(101).sendToTarget();
                                }
                                rs.close();
                            }
                        st.close();
                        conn.close();
                        return;
                    } catch (SQLException e) {
                        Log.v("ss", "远程连接失败!");
                        Log.v("ss", e.getMessage());
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }


    //返回按键，搜索响应，列表项点击响应
    private void buttons() {
        headLis();
        itemLis();
    }
    //首行监听事件
    private void headLis() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // 设置搜索文本监听
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // 当点击搜索按钮时触发该方法
            @Override
            public boolean onQueryTextSubmit(String query) {
                //调用远程数据库查询
                bookList.clear();
                adapter.clearData();

                return false;
            }

            // 当搜索内容改变时触发该方法
            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty(newText)){

                }else{

                }
                return false;
            }
        });
    }

    //RecyclerView 子项点击事件
    private void itemLis() {
        adapter.setOnItemClickListener(new bookAdapter.OnItemClickListener() {
            Context context = mainSearch.this;
            @Override
            public void onClick(int position) {
                Intent intent = new Intent(mainSearch.this,bookDetail.class);
                String bookName = bookList.get(position).get("bookName").toString();
                intent.putExtra("key",bookName);
                startActivity(intent);
                Toast.makeText(context,bookName,Toast.LENGTH_SHORT).show();
            }

            @Override
            public boolean onLongClick(int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("收藏提示").setMessage("您是否收藏本书？")
                        .setNeutralButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(context,"放弃收藏",Toast.LENGTH_SHORT).show();
                    }
                }).show();
                return false;
            }
        });
    }


}
