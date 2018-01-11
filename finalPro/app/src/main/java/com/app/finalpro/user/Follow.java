package com.app.finalpro.user;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

import static com.app.finalpro.user.ChangeBitmap.getSquareBitmap;

public class Follow extends AppCompatActivity {
    private String info;
    ArrayList<HashMap<String, Object>> AuthorList = new ArrayList<>();
    ArrayList<HashMap<String, String>> tem_generals;
    ArrayList<HashMap<String, String>> AuthorList1;
    private String base_url = "http://zhyhhh-1255732607.cosgz.myqcloud.com/image/authorimage/";
    private String urlimg;
    private Bitmap bitmap;
    private Bitmap bitmap1;
    private boolean ifc=false;
    ListView listView;
    ImageView imageViewback;
    SimpleAdapter simpleAdapter;
    SharedPreferences player;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow);
        listView=(ListView)findViewById(R.id.listviewfollow);
        imageViewback=(ImageView) findViewById(R.id.followtouser2);
        Intent intent = getIntent();
        final Bundle bundle=intent.getExtras();
        player = getSharedPreferences("user", Context.MODE_PRIVATE);
        info=player.getString("user","");
        getList();
        imageViewback.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent myintent  = new Intent();
                Bundle bundle1=new Bundle();
                bundle1.putBoolean("change2",false);
                bundle1.putBoolean("ifchange",false);
                myintent.putExtras(bundle1);
                setResult(RESULT_OK, myintent);
                finish();
            }
        });

        listView.setOnItemClickListener(new ListView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent myintent  = new Intent(Follow.this,Author.class);
                Bundle bundle1=new Bundle();
                bundle1.putString("author",AuthorList.get(i).get("name").toString());
                Log.v("author",AuthorList.get(i).get("name").toString());
                bundle1.putString("user",info);
                myintent.putExtras(bundle1);
                startActivity(myintent);
            }
        });
        final AlertDialog.Builder alterDialog = new  AlertDialog.Builder(this);




        listView.setOnItemLongClickListener(new ListView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {
                     final int id=Integer.parseInt(AuthorList.get(position).get("fid").toString());
                    alterDialog.setTitle("取消关注").setMessage("您是否取消关注"+AuthorList.get(position).get("name")+"？").setNegativeButton("取消",new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick (DialogInterface v,int i) {
                            Toast.makeText(Follow.this,"您选择了[取消]", Toast.LENGTH_SHORT).show();
                        }
                    }).setPositiveButton("确定", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick (DialogInterface v,int i) {
                            Toast.makeText(Follow.this,"您选择了[确定]", Toast.LENGTH_SHORT).show();
                            Log.v("remove",String.valueOf(position));
                            AuthorList.remove(position);
                            simpleAdapter.notifyDataSetChanged();
                            updateFollowRecord(id);
                            //getList();
                        }
                    }).create().show();
                //getList();
                return true;
            }
        });
    }
    public void getList(){
        if (AuthorList != null) {
            AuthorList = null;
        }
        AuthorList = new ArrayList<>();
        simpleAdapter = new SimpleAdapter(this, AuthorList, R.layout.authorfollow, new String[]{"name", "image","nation"}, new int[]{R.id.Authorname, R.id.Authorimage,R.id.Authorcountry});
        simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data,
                                        String textRepresentation) {
                if(view instanceof ImageView && data instanceof Bitmap){
                    ImageView i = (ImageView)view;
                    i.setImageBitmap((Bitmap) data);
                    return true;
                }
                return false;
            }
        });
        listView.setAdapter(simpleAdapter);
        getAuthor(info);
    }
    public void getAuthor(final String username)
    {
        AuthorList1 = new ArrayList<HashMap<String, String>>();
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.interrupted()) {
                    try {
                        Thread.sleep(100);  // 每隔0.1秒尝试连接
                        Class.forName("com.mysql.jdbc.Driver");
                    } catch (InterruptedException e) {
                        Log.v("ss", e.toString());
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    String ip = "119.29.229.194";
                    int port = 53306;
                    String dbName = "android";
                    String url = "jdbc:mysql://" + ip + ":" + port
                            + "/" + dbName; // 构建连接mysql的字符串
                    String user = "root";
                    String password = "android123";
                    try {
                        Connection conn = DriverManager.getConnection(url, user, password);
                        Log.v("ss", "远程连接成功!");
                        String sql = "select fid,author_name,author_nation,author_intro,author.author_image "
                                +" from follow,author,User_info "
                                + " where User_info.user_name='" + username + "' and "
                                +" User_info.user_id = follow.user_id and follow.author_id = author.author_id";

                        Statement st = (Statement) conn.createStatement();
                        ResultSet resultSet = st.executeQuery(sql);
                        while (resultSet.next()) {
                            HashMap<String, Object> item = new HashMap<String, Object>();
                            item.put("fid",String.valueOf(resultSet.getInt("fid")));
                            item.put("name",resultSet.getString("author_name"));
                            item.put("nation",resultSet.getString("author_nation"));
                            //item.put("authorimage",resultSet.getString("author.author_image")); //pic
                            URL urlPic = new URL( base_url+resultSet.getString("author.author_image").toString());//通过url查询图片，显示。
                            InputStream is = urlPic.openStream();
                            bitmap = BitmapFactory.decodeStream(is);
                            bitmap1 = getSquareBitmap(bitmap);
                            item.put("image",bitmap1);
                            AuthorList.add(item);
                        }
                        handler.obtainMessage(1).sendToTarget();
                        resultSet.close();
                        st.close();
                        conn.close();
                        return ;
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }); thread.start();
    }
    public void getPic(){
        Thread thread = new Thread() {
            int i = 0;
            @Override
            public void run() {
                for(int i=0;i<tem_generals.size();i++){
                    HashMap<String, Object> temp =   AuthorList.get(i);
                    String url1 = (String)temp.get("picUrl");
                    Log.v("图片地址：",url1);
                    try {
                        URL url = new URL(url1);//通过url查询图片，显示。
                        InputStream is = url.openStream();

                        bitmap = BitmapFactory.decodeStream(is);
                        bitmap1 = getSquareBitmap(bitmap);

                        Log.v("bitmap",String.valueOf(bitmap1));
                        AuthorList.get(i).put("image",bitmap1);
                        return;
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                handler.obtainMessage(2).sendToTarget();
                }
        };
        thread.start();
    }
    public void updateFollowRecord(final int id){
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.interrupted()) {
                    try {
                        Thread.sleep(100);  // 每隔0.1秒尝试连接
                        Class.forName("com.mysql.jdbc.Driver");
                    } catch (InterruptedException e) {
                        Log.v("ss", e.toString());
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    String ip = "119.29.229.194";
                    int port = 53306;
                    String dbName = "android";
                    String url = "jdbc:mysql://" + ip + ":" + port
                            + "/" + dbName; // 构建连接mysql的字符串
                    String user = "root";
                    String password = "android123";
                    try {
                        Connection conn = DriverManager.getConnection(url, user, password);
                        Log.v("ss", "远程连接成功!");
                        String sql = "delete from follow "
                                +" where fid=" + id;
                        Statement st = (Statement) conn.createStatement();
                        st.executeUpdate(sql);
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
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
           if(msg.what==1){
                simpleAdapter.notifyDataSetChanged();
            }
        }
    };
}
