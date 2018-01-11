package com.app.finalpro.community;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.finalpro.R;
import com.app.finalpro.user.GlideCircleTransform;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

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

/**
 * Created by ZXG on 2018/1/7.
 */

public class com_fragment extends Fragment {
    View rootView;
    private RecyclerView recyclerView;
    private ImageView image;
    private List<News> newsList,newdataList;
    private boolean flag=false;
    private boolean iffinish=false;
    private SwipeRefreshLayout mSwipeLayout;
    private boolean isRefresh = false;//是否刷新中
    private String base_url="http://zhyhhh-1255732607.cosgz.myqcloud.com/image/socialimage/";
    private String base_url0="http://zhyhhh-1255732607.cosgz.myqcloud.com/image/userimage/";
    private String urlimg;
    private String urlimg0;
    private Bitmap bitmap;
    private Bitmap bitmap1;
    private int count=0;
    private List< String> name;//存储取回来的名字；
    private List< String> main;
    private List< String> url1;
    private List< String> url2;//图片的名字
    private List< String> zan;
    private ArrayList<HashMap<String,String>> list;      //保存取到的来自数据库的原始内容
    private ArrayList<HashMap<String,Object>> mainlist;  //最终可以用的对象
    private List<News> objects;
    //    private newsList =new ArrayList<>();
    public String s;
    public String num;
    public String content;
    public String url_image1;
    public String url_image2;
    private RecyclerViewAdapter adapter;
    private int total=4; //每次显示的最大值。

    private Handler handler;
    private Thread thread;
    private Thread threadIcon;
    private int countData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        rootView = inflater.inflate(R.layout.main, null);

        init();
        hand();
        threads();
        refresh();

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1=new Intent(getActivity(),Add.class);
                getActivity().startActivityForResult(intent1,10);
             //   getActivity().finish();
            }
        });
        
        return rootView;
    }

    public void init(){
        recyclerView= (RecyclerView) rootView.findViewById(R.id.recyclerView);
        image=(ImageView) rootView.findViewById(R.id.add);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        mSwipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeLayout);
        //用户名。
        zan = new ArrayList<String>();  //点赞的数量
        name =new ArrayList<String>();  //用户名
        main =new ArrayList<String>();//主要内容
        url1 =new ArrayList<String>();//头像的URL
        url2 =new ArrayList<String>();//发表的URL
        ///图片的名字。
        newsList =new ArrayList<News>();
        newdataList=new ArrayList<News>();
        list=new ArrayList<HashMap<String, String>>();
        objects = new ArrayList<News>();
        count = 0;

        LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity());
        adapter=new RecyclerViewAdapter(objects,getActivity());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }
    public void threads(){
        //urlimg="http://zhyhhh-1255732607.cosgz.myqcloud.com/image/socialimage/1.jpg";
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!thread.isInterrupted()) {     // 反复尝试连接，直到连接成功后退出循环
                    try {
                        Thread.sleep(100);  // 每隔0.1秒尝试连接
                        Class.forName("com.mysql.jdbc.Driver");//找到jar包的位置
                    }
                    catch (InterruptedException e) {
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
                    String sql="select * from social,User_info where social.user_id = User_info.user_id order by id desc limit 25";

                    try {
                        Connection conn = DriverManager.getConnection(url, user, password);
                        Statement st = (Statement) conn.createStatement();
                        ResultSet rs = st.executeQuery(sql);

                        objects.add(new News("","","","",""));
                        while (rs.next()) {
                            String uname = rs.getString("user_name");
                            String uicon = base_url0 + rs.getString("image");
                            String likes = rs.getString("likes");
                            String content = rs.getString("content");
                            String pic = rs.getString("pic");
                            System.out.println(pic);
                            News coms = new News(uname,content,likes,uicon,pic);
                            objects.add(coms);
                            Log.v("objects.size:  ",String.valueOf(objects.size()));
                        }
                        thread.interrupt();
                        handler.obtainMessage(2).sendToTarget();
                        flag=true;
                        rs.close();
                        st.close();
                        conn.close();
                    }
                    catch (SQLException e) {
                        Log.v("ss", "远程连接失败!");
                        Log.v("ss", e.getMessage());
                    }
                }
            }
        });
        thread.start();
    }

    public void refresh(){
//        LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity());
//        adapter=new RecyclerViewAdapter(newdataList,getActivity());
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.setAdapter(adapter);

        //上面的方法已经废弃 但是用这种方法进度条的圆圈没有颜色
        mSwipeLayout.setColorSchemeColors(Color.BLUE,
                Color.GREEN,
                Color.YELLOW,
                Color.RED);
        // 设置手指在屏幕下拉多少距离会触发下拉刷新
        mSwipeLayout.setDistanceToTriggerSync(300);// 设定下拉圆圈的背景
        mSwipeLayout.setProgressBackgroundColorSchemeColor(Color.WHITE);// 设定下拉圆圈的背景
        mSwipeLayout.setSize(SwipeRefreshLayout.LARGE); // 设置圆圈的大小
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                //2秒后执行run方法里的代码，模拟刷新真正状态
                //2秒后显示刷新的数据
                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        //添加数据至头部 position为0
                        //newsList.clear();
                        //   newsList.add(new News("123","456",R.mipmap.news_one));
                        //  newsList.add(new News("786","456",R.mipmap.news_one));
                        //通知adapter同步刷新,意思就是咱们添加一个数据到顶部，那么就通知adapter进行更新
//                        adapter.notifyDataSetChanged();
//                        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
//                        adapter=new RecyclerViewAdapter(newdataList,getActivity());
//                        recyclerView.setHasFixedSize(true);
//                        recyclerView.setLayoutManager(layoutManager);
//                        recyclerView.setAdapter(adapter);
                        objects.clear();
                        adapter=new RecyclerViewAdapter(objects,getActivity());
                        threads();
                        adapter.notifyDataSetChanged();
                        //设置刷新的圆圈隐藏，如果不设置这个的话就会圆圈就会一直显示
                        mSwipeLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });
    }

    public void initial(){
        if(newdataList!=null)
            newsList=null;
        newsList=new ArrayList<News>();
        for (int i=0;i<mainlist.size();i++)
        {
            Log.v("name","book");
            ArrayList<News> temlist=new ArrayList<News>();
            Log.v("bookname",mainlist.get(i).get("bookname").toString());
            Log.v("bookname",mainlist.get(i).get("content").toString());
            Log.v("bookname",mainlist.get(i).get("number").toString());
            Log.v("bookname",mainlist.get(i).get("url1").toString());
            //News news=new News(mainlist.get(i).get("bookname").toString(),mainlist.get(i).get("content").toString(),stringtoBitmap(mainlist.get(i).get("url1").toString()),mainlist.get(i).get("number").toString());
            //temlist.add(news);
            /*

            Bitmap bt=(Bitmap)mainlist.get(i).get("url1");
            Bitmap bt2;
            if(mainlist.get(i).get("url2")!=null)bt2 = (Bitmap)mainlist.get(i).get("url2");
            else bt2 = null;

            newdataList.add(new News(mainlist.get(i).get("bookname").toString(),mainlist.get(i).get("content").toString(),bt,mainlist.get(i).get("number").toString(),bt2));
//            newdataList=temlist;*/
        }

    }
    public void hand(){
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                //1-
                switch (msg.what){
                    case 1:
                      //  newdataList.add(new News(s,content,bitmap,num,bitmap1));
                        break;
                    case 2:
                        Log.v("原始数据","获取成功");
                        adapter.notifyDataSetChanged();
                        Log.v("线程是否阻塞",String.valueOf(thread.isInterrupted()));
                        //threadIcon.start();
                    case 3:
                        Log.v("图片获取","获取成功");
                        adapter.notifyDataSetChanged();
                        if(countData == objects.size()){
                            threadIcon.interrupt();
                            countData = 0;
                        }

                }

            }
        };
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v("urlback","jjj");
        objects.clear();
        threads();
        adapter.notifyDataSetChanged();
        Log.v("结束：",String.valueOf(resultCode));
    }




    
}
