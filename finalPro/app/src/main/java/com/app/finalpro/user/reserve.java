package com.app.finalpro.user;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

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

public class reserve extends AppCompatActivity {

    private String info;
    ArrayList<HashMap<String, Object>> reserveList = new ArrayList<>();
    private String base_url = "http://zhyhhh-1255732607.cosgz.myqcloud.com/image/bookimage/";
    private String urlimg;
    private Bitmap bitmap;
    private Bitmap bitmap1;
    private boolean ifc=false;
    ArrayList<HashMap<String, Object>> favoBookList;
    ArrayList<HashMap<String, Object>> tem_generals;
    private TextView title;
    private final String newTitle = "图书预约";
    ListView listView;
    ImageView imageViewback;
    SimpleAdapter simpleAdapter;
    SharedPreferences player;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow);
        title = (TextView)findViewById(R.id.followAuthor);
        title.setText(newTitle);
        listView=(ListView)findViewById(R.id.listviewfollow);
        title = (TextView)findViewById(R.id.followAuthor);
        imageViewback=(ImageView) findViewById(R.id.followtouser2);
        Intent intent = getIntent();
        final Bundle bundle=intent.getExtras();
        player = getSharedPreferences("user", Context.MODE_PRIVATE);
        info=player.getString("user","");
        getRbook(info);
        buttons();


    }
    public void getList(){
        if (reserveList != null) {
            reserveList = null;
        }
        reserveList = new ArrayList<>();
        //从数据库中获取该用户预约的图书（返回结构：bookname，coverImg，author,cid）

        Log.v("列表长度",String.valueOf(tem_generals.size()));
        for (int i = 0; i < tem_generals.size(); i++) {
            HashMap<String, Object> temp0 = new LinkedHashMap<>();
            temp0.put("bookname", tem_generals.get(i).get("bookname"));
            temp0.put("author", tem_generals.get(i).get("author"));
            temp0.put("id", tem_generals.get(i).get("rid"));
            temp0.put("image",tem_generals.get(i).get("coverImg"));
            reserveList.add(temp0);
        }
        simpleAdapter = new SimpleAdapter(this, reserveList, R.layout.book_reserve, new String[]{"bookname", "image","author"}, new int[]{R.id.Authorname, R.id.Authorimage,R.id.Authorcountry});
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
    }
    public void buttons(){
        imageViewback.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
//                Animation animation=new AlphaAnimation(1.0f,0.0f);
//                animation.setDuration(100);
//                imageViewback.startAnimation(animation);
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
//                Animation animation=new AlphaAnimation(1.0f,0.0f);
//                animation.setDuration(100);
//                listView.startAnimation(animation);
                Intent myintent  = new Intent(reserve.this,bookDetail.class);
                myintent.putExtra("key",reserveList.get(i).get("bookname").toString());

                startActivity(myintent, ActivityOptions.makeSceneTransitionAnimation(reserve.this).toBundle());
            }
        });
        final AlertDialog.Builder alterDialog = new  AlertDialog.Builder(this);
        listView.setOnItemLongClickListener(new ListView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {
                final int id=Integer.parseInt(reserveList.get(position).get("id").toString());
                alterDialog.setTitle("取消预约").setMessage("您是否取消预约"+reserveList.get(position).get("bookname")+"？").setNegativeButton("取消",new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick (DialogInterface v,int i) {
                        Toast.makeText(reserve.this,"您选择了[取消]", Toast.LENGTH_SHORT).show();
                    }
                }).setPositiveButton("确定", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick (DialogInterface v,int i) {
                        Toast.makeText(reserve.this,"您选择了[确定]", Toast.LENGTH_SHORT).show();
                        updatereserveRecord(id);
                        reserveList.remove(position);
                        simpleAdapter.notifyDataSetChanged();
                    }
                }).create().show();
                return true;
            }
        });
    }
    public void getRbook(final String username)
    {
        favoBookList = new ArrayList<HashMap<String, Object>>();
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
                        //               书名      封面        作者名字    记录Id
                        String sql = "select bookname,coverimg,author_name,rid "
                                +" from appointment,book,author,User_info"
                                +" where User_info.user_name ='"+username + "' and"
                                +" User_info.user_id = appointment.user_id and appointment.book_id = book.bookid"
                                +" and book.author_id = author.author_id";

                        Statement st = (Statement) conn.createStatement();
                        ResultSet resultSet = st.executeQuery(sql);
                        while (resultSet.next()) {
                            HashMap<String, Object> item = new HashMap<String, Object>();
                            item.put("rid",resultSet.getString("rid"));
                            item.put("bookname",resultSet.getString("bookname"));
                            item.put("coverImg",resultSet.getString("coverimg"));
                            item.put("author",resultSet.getString("author_name"));
                            urlimg = base_url + resultSet.getString("coverimg");
                            URL url2 = new URL(urlimg);//通过url查询图片，显示。
                            InputStream is = url2.openStream();
                            bitmap = BitmapFactory.decodeStream(is);
                            item.put("coverImg",bitmap);
                            favoBookList.add(item);
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
    public void updatereserveRecord(final int id){
        //其实是删除记录
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
                        String sql = "delete from appointment "
                                +" where cid=" + id;
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
            if (msg.what==1) {
                tem_generals=new ArrayList<HashMap<String, Object>>();
                tem_generals =favoBookList;
                getList();
            }
        }
    };
}
