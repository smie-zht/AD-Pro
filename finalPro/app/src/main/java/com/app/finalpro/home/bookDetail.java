package com.app.finalpro.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.HashMap;
import java.util.Map;

public class bookDetail extends AppCompatActivity {
    private String key;
    private Thread thread;
    private Handler handler;

    private ImageView back;
    private ConstraintLayout imgBack;
    private ImageView bookPic;
    private Bitmap bitmap;

    private TextView bookName;
    private String bookNameSrc;
    private TextView bookInfo;
    private String bookInfoSrc;

    private ImageView SCstart;
    private boolean isSC;
    private ImageView GZheart;
    private boolean isGZ;
    private TextView stock;
    private String stockSrc;
    private Button subsc;
    private boolean isSub;
    private TextView indro;
    private String indroSrc;

    //用户（书本）信息
    public SharedPreferences player;
    public int user_id;
    public int book_id;
    public int author_id;
    public String authorName;
    public int fid;//关注记录号
    public int cid;//收藏记录号
    public int rid;//预约记录号
    public int store;//图书存货
    public int allStore;//图书总量
    public boolean isBorrow;//借阅标记

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //获取参数
        Intent intent = getIntent();
        key = intent.getStringExtra("key");
        //过度动画
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        Transition explode = TransitionInflater.from(this).inflateTransition(R.transition.explode);
        setContentView(R.layout.book_detail);
        getWindow().setEnterTransition(explode);
        getWindow().setReenterTransition(explode);

        init();
        buttons();
    }

    private void init() {
        player = getSharedPreferences("user", Context.MODE_PRIVATE);
        Log.v("书名",key);
        bindView(); //绑定视图
        UIhand();//配置UI助手
        cnet(key); //初始化搜索结果
        cnet("init");//配置用户选择信息

    }

    //三行视图的绑定
    private void bindView() {
        back = (ImageView)findViewById(R.id.back);
        imgBack = (ConstraintLayout)findViewById(R.id.picBack);
        bookPic = (ImageView)findViewById(R.id.bookPic);

        isGZ = false;
        isSC = false;
        isSub = false;
        isBorrow = false;

        bookName = (TextView)findViewById(R.id.bookName);
        bookInfo = (TextView)findViewById(R.id.info);
        SCstart = (ImageView)findViewById(R.id.scStar);
        GZheart = (ImageView)findViewById(R.id.gzStar);
        stock = (TextView)findViewById(R.id.kucun);
        subsc = (Button)findViewById(R.id.yuyue);

        indro = (TextView)findViewById(R.id.indro);
    }

    //handler任务-101：更新查询内容
    public void UIhand(){
        //助手
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 101:
                            togView();
                            setBack(bitmap);
                            Log.v("任务","执行");
                            if(store==0)subsc.setText("预约");
                            else subsc.setText("借阅");
                            thread.interrupt();
                        break;
                    case 102:
                            setUser();
                            thread.interrupt();
                        break;
                    case 103:
                        if(isGZ){
                            Toast.makeText(bookDetail.this,"关注作者",Toast.LENGTH_SHORT).show();
                            GZheart.setImageResource(R.drawable.heart_icon);
                        }
                        else{
                            Toast.makeText(bookDetail.this,"取消关注",Toast.LENGTH_SHORT).show();
                            GZheart.setImageResource(R.drawable.heart);
                        }
                        thread.interrupt();
                        break;
                    case 104:
                        if(isSC){
                            Toast.makeText(bookDetail.this,"收藏成功",Toast.LENGTH_SHORT).show();
                            SCstart.setImageResource(R.drawable.fullstar);
                        }else{
                            Toast.makeText(bookDetail.this,"取消收藏",Toast.LENGTH_SHORT).show();
                            SCstart.setImageResource(R.drawable.star);
                        }
                        thread.interrupt();
                        break;
                    case 105:
                        if(isSub){
                            Toast.makeText(bookDetail.this,"预约成功",Toast.LENGTH_SHORT).show();
                            subsc.setAlpha((float)0.4);
                        }else{
                            Toast.makeText(bookDetail.this,"取消预约",Toast.LENGTH_SHORT).show();
                            subsc.setAlpha((float)1.0);
                        }
                        thread.interrupt();
                        break;
                    case 106:
                        if(!isBorrow){
                            Toast.makeText(bookDetail.this,"借阅成功",Toast.LENGTH_SHORT).show();
                            stock.setText("当前库存:\n"+String.valueOf(store-1)+"/"+String.valueOf(allStore));
                            isBorrow = true;
                        }else{
                            Toast.makeText(bookDetail.this,"您已借阅此书\n请勿重复借阅",Toast.LENGTH_SHORT).show();
                        }
                        subsc.setText("借阅");
                        subsc.setAlpha((float)0.4);
                        thread.interrupt();
                        break;
                }
            }
        };
    }
    //任务101-子程序:配置书本基本信息
    private void togView() {
        bookPic.setImageBitmap(bitmap);
        bookName.setText(bookNameSrc);
        bookInfo.setText(bookInfoSrc);
        indro.setText("     "+indroSrc);
        stock.setText(stockSrc);
        indro.setMovementMethod(ScrollingMovementMethod.getInstance());

    }
    //任务102-子程序：配置用户偏好信息
    public void setUser(){
        if(isGZ)    GZheart.setImageResource(R.drawable.heart_icon);
        if(isSC)    SCstart.setImageResource(R.drawable.fullstar);
        if(store > 0){
            subsc.setText("借阅");
            subsc.setAlpha((float)1.0);
        }else{
            if(isSub){
                subsc.setText("预约");
                subsc.setAlpha((float)0.4);
            }else{
                subsc.setText("预约");
                subsc.setAlpha((float)1.0);
            }
        }
    }


    //新线程:查询远程数据库:
    //init(获取该用户选择）
    //follow(关注作者），unfollow（取关作者）
    //like（收藏图书），unlike（取消收藏）
    //sub（预约图书），unsub（取消预约）
    //borrow（借阅图书）
    //其他则为书本查询
    public void cnet(String name){

        thread = new Thread(new Runnable(){
            @Override
            public void run() {
                //while (!thread.isInterrupted()){
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
                        if(name.equals("init")){
                            Log.v("搜索数据","用户信息");
                            //获取图书id+作者id
                            String sqlBook = "select bookid,author_id from book where bookname = '"+key+"'";
                            //获取用户id
                            String sqlUser = "select user_id from User_info where BINARY user_name = '"+player.getString("user","")+"'";

                                                //执行book信息查询
                            ResultSet rsBook = st.executeQuery(sqlBook);
                            if(rsBook.next()){
                                book_id = rsBook.getInt("bookid");
                                author_id = rsBook.getInt("author_id");
                                Log.v("book_id",String.valueOf(book_id));
                                Log.v("author_id",String.valueOf(author_id));
                            }
                            rsBook.close();
                                                //执行user信息查询
                            ResultSet rsUser = st.executeQuery(sqlUser);
                            if(rsUser.next()){
                                user_id = rsUser.getInt("user_id");
                                Log.v("user_id",String.valueOf(user_id));
                            }
                            rsUser.close();

                            //查询收藏记录
                            String sql0 = "select cid,favorate.user_id,favorate.book_id from favorate,User_info,book where user_name = '"
                                    +player.getString("user","")+"' and bookname = '" +key
                                    + "' and User_info.user_id=favorate.user_id"
                                    +" and book.bookid = favorate.book_id";
                            //查询关注记录
                            String sql1 = "select * from follow where user_id ="+String.valueOf(user_id)+" and author_id = "+String.valueOf(author_id)+";";
                            //查询预约
                            String sql2 ="select rid,appointment.user_id,appointment.book_id from appointment,User_info,book where user_name = '"
                                    +player.getString("user","")+"' and bookname = '" +key
                                    + "' and User_info.user_id=appointment.user_id"
                                    +" and book.bookid = appointment.book_id";

                                                //执行查看是否收藏此书
                            ResultSet rs0 = st.executeQuery(sql0);
                            if(rs0.next()){
                                isSC = true;
                                cid = rs0.getInt("cid");
                                Log.v("collect","yes");
                            }else{
                                Log.v("exec",sql0);
                                isSC = false;
                                Log.v("collect","no");
                            }
                            rs0.close();
                                                //执行查看是否关注作者
                            ResultSet rs1 = st.executeQuery(sql1);
                            Log.v("查询关注作者:",sql1);
                            if(rs1.next()){
                                isGZ = true;
                                fid = rs1.getInt("fid");
                                Log.v("follow","yes");
                            }else{
                                Log.v("查询：","失败");
                                isGZ = false;
                                Log.v("follow","no");
                            }
                            rs1.close();
                                                //执行产看是否预约
                            ResultSet rs2 = st.executeQuery(sql2);
                            if(rs2.next()){
                                isSub = true;
                                rid = rs2.getInt("rid");
                                Log.v("reserve","yes");
                            }else{
                                isSub = false;
                                Log.v("reserve","no");
                            }
                            handler.obtainMessage(102).sendToTarget();
                        }
                        else if(name.equals("follow")){     //关注作者
                            String sqlFollow = "insert into follow(user_id,author_id) values("
                                        +user_id+" , "+author_id+" );";
                            int rs = st.executeUpdate(sqlFollow);
                            if(rs>0){
                                isGZ=true;
                                handler.obtainMessage(103).sendToTarget();
                            }
                        }
                        else if(name.equals("unfollow")){     //取关作者
                            String sqlunFollow = "delete from follow where user_id = "
                                    +user_id+" and author_id = "+author_id+" ;";
                            int rs = st.executeUpdate(sqlunFollow);
                            if(rs>0){
                                isGZ = false;
                                handler.obtainMessage(103).sendToTarget();
                            }
                        }
                        else if(name.equals("like")){     //收藏图书
                            String sqllike = "insert into favorate(user_id,book_id) values("
                                    +user_id+" , "+book_id+" );";
                            int rs = st.executeUpdate(sqllike);
                            if(rs>0){
                                isSC = true;
                                handler.obtainMessage(104).sendToTarget();
                            }
                        }
                        else if(name.equals("unlike")){     //取消收藏
                            String sqlunlike = "delete from favorate where user_id = "
                                    +user_id+" and book_id = "+book_id+" ;";
                            int rs = st.executeUpdate(sqlunlike);
                            if(rs>0){
                                isSC = false;
                                handler.obtainMessage(104).sendToTarget();
                            }
                        }
                        else if(name.equals("sub")){     //预约图书
                            String sqlsub = "insert into appointment(user_id,book_id) values("
                                    +user_id+" , "+book_id+" );";
                            int rs = st.executeUpdate(sqlsub);
                            if(rs>0){
                                isSub = true;
                                handler.obtainMessage(105).sendToTarget();
                            }
                        }
                        else if(name.equals("unsub")){     //取消预约
                            String sqlunsub = "delete from appointment where user_id = "
                                    +user_id+" and book_id = "+book_id+" ;";
                            int rs = st.executeUpdate(sqlunsub);
                            if(rs>0){
                                isSub = false;
                                handler.obtainMessage(105).sendToTarget();
                            }
                        }
                        else if(name.equals("borrow")){     //借阅图书
                            String sqlReq = "select * from BookRecord where bookid = "
                                        +book_id+" and uid = "+user_id+";";
                            ResultSet rs = st.executeQuery(sqlReq);
                            if(rs.next()){    //已经借阅
                                isBorrow = true;
                            }
                            rs.close();
                            if(!isBorrow){
                                Log.v("借书","查询！");
                                String sqlBor = "insert into BookRecord(bookid,uid,status) "
                                        +"values("+book_id+","+user_id+",'wait');";
                                int bor = st.executeUpdate(sqlBor);
                                //借阅图书，图书存货减1
                                if(bor>0){
                                    String sqlChange = "update book set restnum = restnum-1 where"
                                            +" bookid = "+book_id+" ;";
                                    int change = st.executeUpdate(sqlChange);
                                }
                            }
                            handler.obtainMessage(106).sendToTarget();

                        }
                        else{
                            Log.v("要查询的",name);
                            //                      书名      封面图片   出版社   ISBN     简介      作者         总库存   剩余库存
                            String sql = "select bookname,coverimg,publisher,ISBN,summary,author_name,totalnum,restnum"
                                    +" from book,author where bookname = '"+name+"'"
                                    +" and book.author_id = author.author_id";
                            ResultSet rs = st.executeQuery(sql);
                            String base_url = "http://zhyhhh-1255732607.cosgz.myqcloud.com/image/bookimage/";
                            if (rs.next()) {  //直接处理结果
                                Log.v("查询结果",rs.getString("bookname"));
                                Map<String,Object> tem = new HashMap<>();
                                                                                                    //只有主线程才有修改UI的能力
                                URL t = new URL(base_url+rs.getString("coverimg"));
                                InputStream is = t.openStream();
                                bitmap = BitmapFactory.decodeStream(is);   //图片
                                bookNameSrc = rs.getString("bookname"); //书名
                                indroSrc = rs.getString("summary");//简介
                                authorName = rs.getString("author_name"); //保存作者名
                                bookInfoSrc = "作者："+rs.getString("author_name") //信息
                                                +"\n出版社："+rs.getString("publisher")
                                                +"\nISBN："+rs.getString("ISBN");
                                stockSrc = "当前库存:\n"+String.valueOf(rs.getInt("restnum"))
                                        +"/"+String.valueOf(rs.getInt("totalnum"));
                                store = rs.getInt("restnum");   //获取当前存货数量
                                Log.v("存货：",String.valueOf(rs.getInt("restnum")));
                                allStore =rs.getInt("totalnum");  //图书总量
                            }
                            handler.obtainMessage(101).sendToTarget();
                            rs.close();
                        }
                        st.close();
                        conn.close();
                    } catch (SQLException e) {
                        Log.v("ss", "远程连接失败!");
                        Log.v("ss", e.getMessage());
                    }
                    catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            //}
        });
        thread.start();
    }


    private void buttons() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        SCstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isSC){
                    cnet("unlike");

                }else{
                    cnet("like");
                }

            }
        });
        GZheart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isGZ){
                        cnet("unfollow");
                }else{
                        cnet("follow");
                }
            }
        });
        subsc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(store>0){
                    if(isBorrow){
                        Toast.makeText(bookDetail.this,"您已借阅此书\n请勿重复借阅",Toast.LENGTH_SHORT).show();
                    }else{
                        Log.v("借书：","开始");
                        cnet("borrow");
                    }
                }else{
                    if(isSub){
                        cnet("unsub");
                    }else{
                        cnet("sub");
                    }
                }

            }
        });


    }
    //背景调节
    private void setBack(Bitmap bitmap) {
        int MaxW = bitmap.getWidth();
        int MaxH = bitmap.getHeight();
        Log.v("bitmap-宽：",String.valueOf(MaxW));
        Log.v("bitmap-高：",String.valueOf(MaxH));

        int alpha = 180;
        int[] colors = new int[4];
        colors[0] = Color.argb(alpha,131,175,155);
        colors[1] = Color.argb(alpha,200,200,169);
        colors[2] = Color.argb(alpha,249,205,173);
        colors[3] = Color.argb(alpha,167,220,224);

        int redValue ;
        int blueValue ;
        int greenValue ;

//        for(int i=10;i<MaxW;i+=10){
//            for(int j=10;j<MaxH;j+=10){
//                int  pixel= bitmap.getPixel(i,j);
//                    redValue += Color.red(pixel)*100/MaxH/MaxW;
//                    blueValue += Color.blue(pixel)*100/MaxH/MaxW;
//                    greenValue += Color.green(pixel)*100/MaxH/MaxW;
//
//            }
//        }

        int pixel= bitmap.getPixel(30,40);
        redValue = Color.red(pixel);
        blueValue = Color.blue(pixel);
        greenValue = Color.green(pixel);

        //将上述获取的颜色，获得新颜色
        int color ;
        if(redValue<50&&blueValue<50&&greenValue<50) color=colors[1];//灰
        else if(redValue>blueValue&&redValue>greenValue)  color = colors[2];  //红
        else if (blueValue>redValue&&blueValue>greenValue) color = colors[3];//蓝
        else if (greenValue>redValue&&greenValue>blueValue) color=colors[0];//绿
        else color=colors[1];//灰

        Log.v("生成颜色",String.valueOf(color));
        imgBack.setBackgroundColor(color);
    }

}
