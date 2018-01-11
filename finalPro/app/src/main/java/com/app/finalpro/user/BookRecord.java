package com.app.finalpro.user;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Objects;

import static com.app.finalpro.user.ChangeBitmap.getSquareBitmap;
import static com.app.finalpro.user.ChangeBitmap.show;

public class BookRecord extends AppCompatActivity {
    TextView jieshu;
    TextView yijie;
    TextView quanbu;
    TextView huanshu;
    TextView yihuan;
    TextView tjieshu;
    TextView tyihuan;
    TextView tquanbu;
    TextView tyijie;
    TextView thuanshu;
    ImageView imageView;
    ListView listView;
    private Bitmap bitmap;
    private Bitmap bitmap1;
    private boolean ifc=false;
    SimpleAdapter simpleAdapter;
    ArrayList<HashMap<String, Object>> List = new ArrayList<>();
    ArrayList<HashMap<String, Object>> List2 = new ArrayList<>();
    ArrayList<HashMap<String, Object>> tem_generals;
    private String base_url = "http://zhyhhh-1255732607.cosgz.myqcloud.com/image/bookimage/";
    private String urlimg;
    private String statusment;
    String info;
    ArrayList<HashMap<String, Object>> ItemList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_record);
        jieshu = (TextView) findViewById(R.id.jieshu);
        yijie = (TextView) findViewById(R.id.yijie);
        quanbu = (TextView) findViewById(R.id.quanbu);
        huanshu = (TextView) findViewById(R.id.huanshu);
        yihuan = (TextView) findViewById(R.id.yihuan);
        tjieshu = (TextView) findViewById(R.id.tjieshu);
        tyijie = (TextView) findViewById(R.id.tyijie);
        tquanbu = (TextView) findViewById(R.id.tquanbu);
        thuanshu = (TextView) findViewById(R.id.thuanshu);
        tyihuan = (TextView) findViewById(R.id.tyihuan);
        imageView = (ImageView) findViewById(R.id.imageback3);
        listView=(ListView)findViewById(R.id.listview);
        Intent intent = getIntent();
        final Bundle bundle = intent.getExtras();
        info = bundle.getString("user");
        statusment=bundle.getString("status");
        getList(info);
        if(statusment.equals("all"))
        {
            showquanbu();
        }
        else if(statusment.equals("waiting"))
        {
            showjieshu();
        }
        else if(statusment.equals("borrow"))
        {
            showyijie();
        }
        else if(statusment.equals("back"))
        {
            showhuanshu();
        }
        else if(statusment.equals("finish"))
        {
            showyihuan();
        }


        setOnClick();

    }

    public void initList(String status) {
        if (List != null) {
            List = null;
        }
        List = new ArrayList<>();
        ArrayList<HashMap<String, Object>> tem_generals =List2;
        for (int i = 0; i < tem_generals.size(); i++) {
            HashMap<String, Object> temp0 = new LinkedHashMap<>();
            if (status.equals(tem_generals.get(i).get("status"))||status.equals("total")) {
                temp0.put("name", tem_generals.get(i).get("name"));
                if(tem_generals.get(i).get("status").equals("wait"))
                {
                    temp0.put("status", "借书审核");
                }
                else if(tem_generals.get(i).get("status").equals("borrow"))
                {
                    temp0.put("status", "图书已借");
                }
                else if(tem_generals.get(i).get("status").equals("back"))
                {
                    temp0.put("status", "还书审核");
                }
                else if(tem_generals.get(i).get("status").equals("finish"))
                {
                    temp0.put("status", "图书已还");
                }
                temp0.put("time", tem_generals.get(i).get("time"));
                temp0.put("id", tem_generals.get(i).get("id"));
                temp0.put("image", tem_generals.get(i).get("image"));
                List.add(temp0);
            }
        }
        simpleAdapter = new SimpleAdapter(this, List, R.layout.bookrecorditem, new String[]{"name", "image", "status","time"}, new int[]{R.id.Itemname, R.id.Itemimage, R.id.Itemstatus,R.id.Itemtime});
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
    public void getList(){
        if (List2 != null) {
            List2 = null;
        }
        List2 = new ArrayList<>();

        for (int i = 0; i < tem_generals.size(); i++) {
            HashMap<String, Object> temp0 = new LinkedHashMap<>();
                temp0.put("name", tem_generals.get(i).get("bookname"));
                temp0.put("status", tem_generals.get(i).get("status"));
                temp0.put("time", tem_generals.get(i).get("booktime"));
                temp0.put("image", tem_generals.get(i).get("bookimage"));
                temp0.put("id", tem_generals.get(i).get("recordid"));
                List2.add(temp0);
            }
    }
    public void showquanbu() {
        tquanbu.setTextColor(Color.rgb(35, 204, 123));
        tjieshu.setTextColor(Color.rgb(128, 128, 128));
        tyijie.setTextColor(Color.rgb(128, 128, 128));
        thuanshu.setTextColor(Color.rgb(128, 128, 128));
        tyihuan.setTextColor(Color.rgb(128, 128, 128));
        quanbu.setVisibility(View.VISIBLE);
        yijie.setVisibility(View.GONE);
        jieshu.setVisibility(View.GONE);
        huanshu.setVisibility(View.GONE);
        yihuan.setVisibility(View.GONE);
        initList("total");
    }

    public void showjieshu() {
        tjieshu.setTextColor(Color.rgb(35, 204, 123));
        tyihuan.setTextColor(Color.rgb(128, 128, 128));
        tyijie.setTextColor(Color.rgb(128, 128, 128));
        thuanshu.setTextColor(Color.rgb(128, 128, 128));
        tquanbu.setTextColor(Color.rgb(128, 128, 128));
        jieshu.setVisibility(View.VISIBLE);
        yijie.setVisibility(View.GONE);
        yihuan.setVisibility(View.GONE);
        huanshu.setVisibility(View.GONE);
        quanbu.setVisibility(View.GONE);
        initList("wait");
    }

    public void showyihuan() {
        tyihuan.setTextColor(Color.rgb(35, 204, 123));
        tjieshu.setTextColor(Color.rgb(128, 128, 128));
        tyijie.setTextColor(Color.rgb(128, 128, 128));
        thuanshu.setTextColor(Color.rgb(128, 128, 128));
        tquanbu.setTextColor(Color.rgb(128, 128, 128));
        yihuan.setVisibility(View.VISIBLE);
        yijie.setVisibility(View.GONE);
        jieshu.setVisibility(View.GONE);
        huanshu.setVisibility(View.GONE);
        quanbu.setVisibility(View.GONE);
        initList("finish");
    }

    public void showyijie() {
        tyijie.setTextColor(Color.rgb(35, 204, 123));
        tjieshu.setTextColor(Color.rgb(128, 128, 128));
        tyihuan.setTextColor(Color.rgb(128, 128, 128));
        thuanshu.setTextColor(Color.rgb(128, 128, 128));
        tquanbu.setTextColor(Color.rgb(128, 128, 128));
        yijie.setVisibility(View.VISIBLE);
        yihuan.setVisibility(View.GONE);
        jieshu.setVisibility(View.GONE);
        huanshu.setVisibility(View.GONE);
        quanbu.setVisibility(View.GONE);
        initList("borrow");
    }

    public void showhuanshu() {
        thuanshu.setTextColor(Color.rgb(35, 204, 123));
        tjieshu.setTextColor(Color.rgb(128, 128, 128));
        tyijie.setTextColor(Color.rgb(128, 128, 128));
        tyihuan.setTextColor(Color.rgb(128, 128, 128));
        tquanbu.setTextColor(Color.rgb(128, 128, 128));
        huanshu.setVisibility(View.VISIBLE);
        yijie.setVisibility(View.GONE);
        jieshu.setVisibility(View.GONE);
        yihuan.setVisibility(View.GONE);
        quanbu.setVisibility(View.GONE);
        initList("back");
    }

    public void setOnClick() {
        tquanbu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showquanbu();
            }
        });
        tjieshu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showjieshu();
            }
        });
        tyijie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showyijie();
            }
        });
        tyihuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showyihuan();
            }
        });
        thuanshu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showhuanshu();
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myintent = new Intent();
                Bundle bundle1 = new Bundle();
                bundle1.putBoolean("change2", false);
                bundle1.putBoolean("ifchange", false);
                myintent.putExtras(bundle1);
                setResult(RESULT_OK, myintent);
                finish();
            }
        });
        final AlertDialog.Builder alterDialog = new  AlertDialog.Builder(this);
        listView.setOnItemLongClickListener(new ListView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(List.get(i).get("status").equals("图书已借"))
                {
                    final int id=Integer.parseInt(List.get(i).get("id").toString());
                    alterDialog.setTitle("一键还书").setMessage("您确认要归还《"+List.get(i).get("name")+"》吗？").setNegativeButton("取消",new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick (DialogInterface v,int i) {
                            Toast.makeText(BookRecord.this,"您选择了[取消]", Toast.LENGTH_SHORT).show();
                        }
                    }).setPositiveButton("确定", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick (DialogInterface v,int i) {
                            Toast.makeText(BookRecord.this,"您选择了[确定]", Toast.LENGTH_SHORT).show();
                            updateBookRecord(id);
                            getList();
                        }
                    }).create().show();
                }
                return false;
            }
        });
    }
    public void getList(final String username)
    {
        ItemList = new ArrayList<HashMap<String, Object>>();
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
                            + "/" + dbName + "?serverTimezone=UTC&verifyServerCertificate=false&useSSL=false"; // 构建连接mysql的字符串
                    String user = "root";
                    String password = "android123";
                    try {
                        Connection conn = DriverManager.getConnection(url, user, password);
                        Log.v("ss", "远程连接成功!");
                        String sql = "select BookRecord.did,book.bookname,book.coverimg,borrowtime,status from BookRecord,User_info,book "
                                + "where User_info.user_name='" + username + "' "
                                +" and BookRecord.uid=User_info.user_id and BookRecord.bookid=book.bookid";
                        Statement st = (Statement) conn.createStatement();
                        ResultSet resultSet = st.executeQuery(sql);
                        while (resultSet.next()) {
                            HashMap<String, Object> item = new HashMap<String, Object>();
                            item.put("recordid",String.valueOf(resultSet.getInt("BookRecord.did")));
                            item.put("bookname",resultSet.getString("book.bookname"));

                            item.put("booktime",resultSet.getString("borrowtime"));
                            item.put("status",resultSet.getString("status"));
                            urlimg = base_url + resultSet.getString("book.coverimg");
                            URL url2 = new URL(urlimg);//通过url查询图片，显示。
                            InputStream is = url2.openStream();
                            bitmap = BitmapFactory.decodeStream(is);
                            bitmap1 = getSquareBitmap(bitmap);
                            item.put("bookimage",bitmap1);
                            ItemList.add(item);
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
    public void updateBookRecord(final int id){
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
                        String sql = "update BookRecord "
                                +" set status ='back"
                                +"' where did=" + id;
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
                tem_generals=new ArrayList<HashMap<String,Object>>();
                tem_generals=ItemList;
                getList();
                if(statusment.equals("all"))
                {
                    showquanbu();
                }
                else if(statusment.equals("waiting"))
                {
                    showjieshu();
                }
                else if(statusment.equals("borrow"))
                {
                    showyijie();
                }
                else if(statusment.equals("back"))
                {
                    showhuanshu();
                }
                else if(statusment.equals("finish"))
                {
                    showyihuan();
                }

            }
        }
    };
}
