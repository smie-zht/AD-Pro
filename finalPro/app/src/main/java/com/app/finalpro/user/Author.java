package com.app.finalpro.user;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.app.finalpro.MainActivity;
import com.app.finalpro.R;
import com.app.finalpro.home.bookDetail;

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
import java.util.LinkedHashMap;

import static com.app.finalpro.user.ChangeBitmap.show;

public class Author extends AppCompatActivity {
    private String info;
    private String user;
    private String authorid;
    private Bitmap bitmap;
    User_info userifo=new User_info();
    ArrayList<HashMap<String,String>> booklist=new ArrayList<>();
    ImageView imageView;
    ImageView imageViewback;
    TextView textViewnation;
    TextView textViewinfo;
    ListView listView;
    Button buttonfollow;
    Button buttonback;
    TextView textViewname;
    HashMap<String,String> AuthorInfo;
    ArrayList<HashMap<String,String>> AuthorBook;
    SimpleAdapter simpleAdapter;
    HashMap<String,String> author=new HashMap<>();
    ArrayList<HashMap<String, String>> tem_generals;
    private String base_url="http://zhyhhh-1255732607.cosgz.myqcloud.com/image/authorimage/";
    private String urlimg;
    private int id;
    User_info user_info;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author);
        imageViewback=(ImageView)findViewById(R.id.authortouser3);
        imageView=(ImageView)findViewById(R.id.authorimg);
        textViewname=(TextView)findViewById(R.id.authorname);
        textViewnation=(TextView)findViewById(R.id.authorcountry);
        textViewinfo=(TextView)findViewById(R.id.authorinfomation);
        buttonfollow=(Button)findViewById(R.id.buttonlike);
        buttonback=(Button)findViewById(R.id.authorback);
        listView=(ListView)findViewById(R.id.booklistview);
        Intent intent = getIntent();
        final Bundle bundle = intent.getExtras();
        info = bundle.getString("author");
        user = bundle.getString("user");
        //Toast.makeText(Author.this,info+user,Toast.LENGTH_SHORT).show();
        getdatabase(info,user);
        //getdatabase(user,2);
        //setThread();
        //
        onClick();

    }
    public void onClick()
    {
        imageViewback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        final AlertDialog.Builder alterDialog = new  AlertDialog.Builder(this);
        buttonfollow.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                alterDialog.setTitle("关注作者").setMessage("是否关注该作者？").setNegativeButton("取消",new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick (DialogInterface v,int i) {
                        Toast.makeText(Author.this,"您选择了[取消]", Toast.LENGTH_SHORT).show();
                    }
                }).setPositiveButton("确定", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick (DialogInterface v,int i) {
                        authorid=author.get("authorid");
                        Toast.makeText(Author.this,"关注成功", Toast.LENGTH_SHORT).show();
                        Log.v("authorid",authorid);
                        insertFollow(user_info.getId(),Integer.parseInt(authorid));
                    }
                }).create().show();
            }
        });
        buttonback.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Author.this,"回到主界面", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Author.this, MainActivity.class);
                startActivity(intent);
            }
        });
        listView.setOnItemClickListener(new ListView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(Author.this,"跳转到《"+booklist.get(i).get("name").toString()+"》界面", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Author.this,bookDetail.class);
                intent.putExtra("key",booklist.get(i).get("name").toString());
                startActivity(intent);
            }
        });
    }
    public void getBooks()
    {
        if (booklist != null) {
            booklist = null;
        }
        booklist = new ArrayList<>();

        for (int i = 0; i < tem_generals.size(); i++) {
            HashMap<String, String> temp0 = new LinkedHashMap<>();
            temp0.put("name", tem_generals.get(i).get("bookname"));
            temp0.put("type", tem_generals.get(i).get("booktype"));
            temp0.put("publisher", tem_generals.get(i).get("publisher"));
            temp0.put("bookid", tem_generals.get(i).get("bookid"));
            Log.v("nihoa",tem_generals.get(i).get("bookname"));
            booklist.add(temp0);
        }
        simpleAdapter = new SimpleAdapter(this, booklist, R.layout.book_list, new String[]{"name", "type", "publisher"}, new int[]{R.id.listbookname, R.id.listbooktype, R.id.listbookpublish});
        listView.setAdapter(simpleAdapter);
    }
    public void setThread()
    {
        Thread thread=new Thread(){
            @Override
            public void run(){
                try {
                    URL url=new URL(urlimg);//通过url查询图片，显示。
                    InputStream is= url.openStream();
                    bitmap = BitmapFactory.decodeStream(is);
                    handler.sendEmptyMessage(1);
                    return ;
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==2){
                author=AuthorInfo;
                authorid=author.get("authorid");
                id=Integer.parseInt(author.get("authorid"));
                textViewname.setText(author.get("authorname"));
                textViewnation.setText(author.get("authornation"));
                textViewinfo.setText(author.get("authorintro"));
                Bitmap bitmap1=show(bitmap);
                imageView.setImageBitmap(bitmap1);
                user_info=userifo;
                tem_generals=AuthorBook;
                getBooks();
            }
        }
    };
    public void getdatabase(final String name,String name2)
    {
        AuthorInfo=new HashMap<>();
        AuthorBook=new ArrayList<HashMap<String, String>>();
         Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // 反复尝试连接，直到连接成功后退出循环
                while (!Thread.interrupted()) {
                    try {
                        Thread.sleep(100);  // 每隔0.1秒尝试连接
                        Class.forName("com.mysql.jdbc.Driver");//找到jar包的位置
                    } catch (InterruptedException e) {
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
                    // 3.连接JDBC
                    try {
                        Connection conn = DriverManager.getConnection(url, user, password);
                             Log.v("ss", "远程连接成功dsdsd!");
                            String sql = "select * from author where author_name='" + name+"'";
                            Statement st = (Statement) conn.createStatement();
                            ResultSet resultSet = st.executeQuery(sql);
                            if (resultSet.next()) {
                                AuthorInfo.put("authorid", resultSet.getString("author_id"));
                                AuthorInfo.put("authorname", resultSet.getString("author_name"));
                                AuthorInfo.put("authornation", resultSet.getString("author_nation"));
                                AuthorInfo.put("authorintro", resultSet.getString("author_intro"));
                                AuthorInfo.put("authorimage", resultSet.getString("author_image"));
                                urlimg=base_url+ resultSet.getString("author_image");
                                id=Integer.parseInt(resultSet.getString("author_id"));
                            }
                            resultSet.close();
                            st.close();

                            String sql2 = "select * from User_info where user_name='" + name2+"'";
                            Statement st2 = (Statement) conn.createStatement();
                            ResultSet resultSet2 = st2.executeQuery(sql2);
                            if (resultSet2.next()) {
                                userifo.setId(resultSet2.getInt(("user_id")));
                                userifo.setUser_name(resultSet2.getString(("user_name")));
                                userifo.setPassword(resultSet2.getString(("password")));
                                userifo.setNumber(resultSet2.getString(("number")));
                                userifo.setUserimage(resultSet2.getString(("image")));
                                userifo.setPhone(resultSet2.getString(("phone")));

                            }
                            resultSet2.close();
                            st2.close();
                            String sql3 = "select bookname,type,publisher,bookid"
                                    +" from book,author"
                                    +" where author.author_id=" + id+" and author.author_id=book.author_id";
                            Log.v("sql",sql3);
                            Statement st3 = (Statement) conn.createStatement();
                            ResultSet resultSet3 = st3.executeQuery(sql3);
                            while(resultSet3.next()) {
                                HashMap<String, String> item = new HashMap<String, String>();
                                item.put("bookname",resultSet3.getString("bookname"));
                                item.put("booktype",resultSet3.getString("type"));
                                item.put("publisher",resultSet3.getString("publisher"));
                                item.put("bookid",String.valueOf(resultSet3.getInt("bookid")));
                                AuthorBook.add(item);
                            }
                            URL url2=new URL(urlimg);//通过url查询图片，显示。
                            InputStream is= url2.openStream();
                            bitmap = BitmapFactory.decodeStream(is);
                             handler.obtainMessage(2).sendToTarget();
                            resultSet3.close();
                            st3.close();
                            conn.close();
                            return;
                            } catch (SQLException e1) {
                        e1.printStackTrace();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }
    public void insertFollow(final int name, final int uid)
    {
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // 反复尝试连接，直到连接成功后退出循环
                while (!Thread.interrupted()) {
                    try {
                        Thread.sleep(100);  // 每隔0.1秒尝试连接
                        Class.forName("com.mysql.jdbc.Driver");//找到jar包的位置
                    } catch (InterruptedException e) {
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
                    // 3.连接JDBC
                    try {
                        Connection conn = DriverManager.getConnection(url, user, password);
                        Log.v("ss", "远程连接成功!");
                        String sql0 = "select * from follow where user_id='" + uid+"' and author_id='"+name+"'";
                        Statement st0 = (Statement) conn.createStatement();
                        ResultSet resultSet = st0.executeQuery(sql0);
                        if(resultSet!=null)
                        {
                            Log.v("it's null","null");

                        }
                        else
                        {
                            String sql = "insert into follow values (" + uid+","+name+")";
                            Statement st = (Statement) conn.createStatement();
                            st.executeUpdate(sql);
                            st.close();
                        }
                        st0.close();
                        conn.close();
                        return;
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }
    public void getAuthorBook(final int id)
    {
        AuthorBook=new ArrayList<HashMap<String, String>>();
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // 反复尝试连接，直到连接成功后退出循环
                while (!Thread.interrupted()) {
                    try {
                        Thread.sleep(100);  // 每隔0.1秒尝试连接
                        Class.forName("com.mysql.jdbc.Driver");//找到jar包的位置
                    } catch (InterruptedException e) {
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
                    // 3.连接JDBC
                    try {
                        Connection conn = DriverManager.getConnection(url, user, password);
                        Log.v("ss", "远程连接成功!");
                        String sql = "select bookname,type,publisher,bookid"
                                +" from book,author"
                                +" where author.author_id=" + id+" and author.author_id=book.author_id";
                        Log.v("sql",sql);
                        Statement st = (Statement) conn.createStatement();
                        ResultSet resultSet = st.executeQuery(sql);
                        while(resultSet.next()) {
                            HashMap<String, String> item = new HashMap<String, String>();

                            item.put("bookname",resultSet.getString("bookname"));
                            item.put("booktype",resultSet.getString("type"));
                            item.put("publisher",resultSet.getString("publisher"));
                            item.put("bookid",String.valueOf(resultSet.getInt("bookid")));

                            AuthorBook.add(item);
                        }
                        handler.obtainMessage(3).sendToTarget();
                        resultSet.close();
                        st.close();
                        conn.close();
                        return;
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();

    }
}
