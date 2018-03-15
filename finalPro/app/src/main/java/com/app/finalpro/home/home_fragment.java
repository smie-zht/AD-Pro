package com.app.finalpro.home;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.finalpro.MainActivity;
import com.app.finalpro.community.Login;
import com.app.finalpro.home.Adapter.bookAdapter;
import com.app.finalpro.R;
import com.bumptech.glide.Glide;

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
import android.support.v4.app.Fragment;

/**
 * Created by ZXG on 2018/1/7.
 */

public class home_fragment extends Fragment {
    //标题区域
    private TextView head;

    //推荐区域
    private LinearLayout guide;
    private SearchView search;
    private ImageView[] recs;
    private String[] recsCover;
    private TextView[]  recsT;
    private String[] recsTname;

    //分类项
    private TextView[] types;
    private  int typeCount = 10;

    //数据展示
    private ProgressBar waitType;
    private List<Map<String,Object>> bookList; //数据源
    private bookAdapter adapter; //适配器
    private RecyclerView recyclerView; //展示视图


    //助手
    public  View rootView;
    public Handler handler;
    public String us;  //当前登录的用户
    public  Thread thread;
    private String left = "bookDetail";
    private String right = "mainSearch";
    private SharedPreferences player;
    private String user_name;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        rootView = inflater.inflate(R.layout.home, null);
        init();   //控件绑定 + 初始数据
        button(); //用户交互
        return rootView;
    }

    public void init(){
        bindView();  //绑定视图
        UIhand();    //handler定义
        cnet(null); //初始化推荐
        cnet("文学");//初始化分类
    }

    //绑定主视图中五类视图
    public void bindView(){
        player = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);

        //标题栏区域
        head = (TextView) rootView.findViewById(R.id.head);
        search = (SearchView)rootView.findViewById(R.id.search);

        //推荐区域绑定
        guide = (LinearLayout)rootView.findViewById(R.id.guide);
        recs = new ImageView[3];                                                                 //java基础：数组声明---》分配内存-----》数组使用
        recsT = new TextView[3];
        recsCover = new String[3];
        recsTname = new String[3];
        int[] recsTInt={R.id.recT1,R.id.recT2,R.id.recT3};
        int[] recsInt={R.id.rec1,R.id.rec2,R.id.rec3};
        for(int i=0;i<3;i++){
            recs[i] =(ImageView)rootView.findViewById(recsInt[i]);
            recsT[i]=(TextView)rootView.findViewById(recsTInt[i]);}

        //分类选项绑定
        waitType = (ProgressBar)rootView.findViewById(R.id.waitType);
        types = new TextView[typeCount];
        int[] typesInt={R.id.type0,R.id.type1,R.id.type2,
                R.id.type3,R.id.type4,R.id.type5,R.id.type6,
                R.id.type7,R.id.type8,R.id.type9};
        for(int i=0;i<typeCount;i++){
            types[i]=(TextView)rootView.findViewById(typesInt[i]);}
        types[0].setBackgroundColor(Color.argb(240,255,255,255));

        //分类内容绑定
        bookList = new ArrayList<>();
        recyclerView = (RecyclerView)rootView.findViewById(R.id.recV);
        adapter = new bookAdapter(getActivity(),R.layout.item,bookList);
        recyclerView.setAdapter(adapter);
        StaggeredGridLayoutManager layoutManager =
                new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager( layoutManager);

    }
    //handler任务-101：推荐内容，102：分类内容
    public void UIhand(){
        //助手
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 101:   //更新推荐
                        try {
                            if(loadRec()){
                                thread.interrupt();}
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.v("handler","failed");
                        }
                        break;
                    case 102:   //更新分类
                        if(loadData())
                            thread.interrupt();
                        break;

                }
            }
        };
    }
    //type:null 表示初始化,其他表示type
    public void cnet(String type){
        thread = new Thread(new Runnable(){
            @Override
            public void run() {
                while (!Thread.interrupted()){
                    //1.查看本地环境（是否找到适用的jar包）
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
                        if(type==null){
                            String sql = "select * from book";
                            String base_url = "http://zhyhhh-1255732607.cosgz.myqcloud.com/image/bookimage/";
                            String urlimg="http://zhyhhh-1255732607.cosgz.myqcloud.com/image/socialimage/1.jpg";
                            ResultSet rs = st.executeQuery(sql);
                            Log.v("数据库","执行查询");
                            int MaxCount = 0;
                            while (rs.next()) {  //直接处理结果
                                Log.v("处理","结果");
                                if(MaxCount++<3){
//                                    URL t = new URL(base_url+rs.getString("coverImg"));
//                                    Log.v("address:",base_url+rs.getString("coverImg"));
//                                    InputStream is = t.openStream();
//                                    Bitmap bitmap = BitmapFactory.decodeStream(is);
//                                    Log.v("Bitmap",String.valueOf(bitmap));
//                                    Log.v("memory",String.valueOf(bitmap.getRowBytes() * bitmap.getHeight()));
                                    recsCover[MaxCount-1] = base_url+rs.getString("coverImg");
                                    recsTname[MaxCount-1] = "《"+rs.getString("bookname")+"》";
                                }
                            }
                            handler.obtainMessage(101).sendToTarget();
                            rs.close();

                        }else{
                            Log.v("要查询的",type);
                            String sql = "select * from book where type = '"+type+"'";
                            ResultSet rs = st.executeQuery(sql);
                            String base_url = "http://zhyhhh-1255732607.cosgz.myqcloud.com/image/bookimage/";
                            while (rs.next()) {  //直接处理结果
                                Log.v("查询结果",rs.getString("bookname"));
                                Map<String,Object> tem = new HashMap<>();

                                String bkName = rs.getString("bookname");
                                String bkInfo = rs.getString("publisher");

                                tem.put("bookPic",base_url+rs.getString("coverImg"));
                                tem.put("bookName",bkName);
                                tem.put("bookInfo",bkInfo);
                                bookList.add(tem);
                            }
                            handler.obtainMessage(102).sendToTarget();
                            rs.close();
                        }
                        st.close();
                        conn.close();
                        return;
                    } catch (SQLException e) {
                        Log.v("ss", "远程连接失败!");
                        Log.v("ss", e.getMessage());
                    }
                }
            }
        });
        thread.start();
    }

    public  void button(){
        searchLis();//搜索栏监听事件
        slide();   //滑动伸缩推荐栏
        recLis();  //推荐图书点击事件
        typeLis(); //分类栏点击事件
        itemLis(); //分类图书点击事件
    }
    //RecyclerView 子项点击事件
    private void itemLis() {
        adapter.setOnItemClickListener(new bookAdapter.OnItemClickListener() {
            Context context = getActivity();
            @Override
            public void onClick(int position) {
                user_name = player.getString("user","");
                if(user_name.equals("")){
                    Toast.makeText(getActivity(),"请先登录",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(),Login.class);
                    startActivityForResult(intent,1);
                }else{
                    Intent intent = new Intent(getActivity(),bookDetail.class);
                    String bookName = bookList.get(position).get("bookName").toString();
                    intent.putExtra("key",bookName);
                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
                }

                //Toast.makeText(context,bookName,Toast.LENGTH_SHORT).show();
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
    //分类栏的点击事件
    private void typeLis() {
        //分类按钮
        for(int i=0;i<typeCount;i++){
            final String newType = types[i].getText().toString();
            final int tag = i;
            imgtestTouchListener touch = new imgtestTouchListener() {
                @Override
                public void click() {
                    waitType.setVisibility(View.VISIBLE);
                    bookList.clear();
                    adapter.clearData();
                    cnet(newType);
                    for (int j = 0; j < typeCount; j++) {
                        if (j == tag){
                            types[j].setTextColor(Color.parseColor("#000000"));
                            types[j].setBackgroundColor(Color.argb(240,255,255,255));
                        }
                        else{
                            types[j].setTextColor(Color.parseColor("#404040"));
                            types[j].setBackgroundColor(Color.argb(210,240,240,240));
                        }
                    }
                }
            };
            touch.setObject(guide);
            touch.setHandler();
            types[i].setOnTouchListener(touch);
//            types[i].setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    bookList.clear();
//                    adapter.clearData();
//                    cnet(newType);
//                    for(int j=0;j<typeCount;j++){
//                        if(j==tag)    types[j].setTextColor(Color.parseColor("#000000"));
//                        else          types[j].setTextColor(Color.parseColor("#404040"));
//                    }
//                }
//            });
            //types[i].setOnTouchListener(touch);                                                  //分类栏滑动失效，是因为它的子元素有监听事件             //点击事件和滑动事件冲突
        }
    }

    //推荐书籍点击事件
    private void recLis() {
        //推荐的三本书
        imgtestTouchListener[] recs_touch = new imgtestTouchListener[3];
        for(int i=0;i<3;i++){
            final int t = i;
            recs_touch[i] = new imgtestTouchListener() {
                @Override
                public void click() {
                    user_name = player.getString("user","");
                    if(user_name.equals("")){
                        Toast.makeText(getActivity(),"请先登录",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getActivity(),Login.class);
                        startActivityForResult(intent,1);
                    }else{
                        Intent intent = new Intent(getActivity(),bookDetail.class);
                        Log.v("要传送参数",recsT[t].getText().toString());

                        String bookName = recsT[t].getText().toString();
                        intent.putExtra("key",bookName.substring(1,bookName.length()-1));

                        Log.v("上传参数",intent.getStringExtra("key"));

                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
                    }

                    //Log.v("推荐点击",recsT[t].getText().toString());
                    //Toast.makeText(getActivity(),recsT[t].getText().toString(),Toast.LENGTH_SHORT).show();

                }
            };
            recs_touch[i].setObject(guide);
            recs_touch[i].setHandler();
            recs[i].setOnTouchListener(recs_touch[i]);
        }
    }

    //滑动收缩事件
    private void slide() {
        //滑动收缩
        imgtestTouchListener head_touch = new imgtestTouchListener() {
            @Override
            public void click() {

            }
        };
        head_touch.setObject(guide);
        head_touch.setHandler();
        head.setOnTouchListener(head_touch);
        search.setOnTouchListener(head_touch);
        guide.setOnTouchListener(head_touch);
    }

    //搜索框事件
    private void searchLis() {
        // 设置搜索文本监听
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // 当点击搜索按钮时触发该方法
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(getActivity(),mainSearch.class);
                intent.putExtra("key",query);
                startActivity(intent);
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


    //加载推荐图书（如何加载图片）
    public boolean loadRec() throws IOException {
        if(recs!=null){
            for (int i=0;i<3;i++){
                Glide.with(getActivity()).load(recsCover[i]).into(recs[i]);
                recsT[i].setText(recsTname[i]);
                Log.v("recsT",recsTname[i]);
            }
        }
        return true;
    }

    //加载分类图书
    public boolean loadData(){
        Log.v("更新","分类");
        waitType.setVisibility(View.GONE);
        adapter.notifyDataSetChanged();
        return true;
    }
}
