package com.app.finalpro.community;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.finalpro.MainActivity;
import com.app.finalpro.R;
import com.app.finalpro.community.top_snackbar.BaseTransientBottomBar;
import com.app.finalpro.community.top_snackbar.TopSnackBar;
import com.tencent.cos.xml.CosXmlService;
import com.tencent.cos.xml.CosXmlServiceConfig;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.model.object.PutObjectRequest;
import com.tencent.cos.xml.model.object.PutObjectResult;
import com.tencent.qcloud.core.network.QCloudProgressListener;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Add extends AppCompatActivity {
    public ImageView image;
    public TextView send,cancel;
    public EditText content;
    private CosXmlServiceConfig serviceConfig;
    private CosXmlService cosXmlService;
    private PutObjectRequest putObjectRequest;
    private static final int CROP_PHOTO = 2;
    private static final int REQUEST_CODE_PICK_IMAGE=3;
    private static final int CHANGE_INFO=4;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 6;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE2 = 7;
    private File output;
    private Uri imageUri;
    private int select=0;
    private String id;
    private String url1;//图片。
    private boolean ifupload=false;
    private boolean ifchange2=false;
    private Uri uri;
    private SharedPreferences player;
    private String userName;
    private boolean flag=false;
    private String Sql1;
    private int user_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add);
        player = getSharedPreferences("user",Context.MODE_PRIVATE);
        userName = player.getString("user","");
        send = (TextView) findViewById(R.id.send);
        content = (EditText) findViewById(R.id.content);//输入的内容
        image = (ImageView) findViewById(R.id.img1);
        cancel =(TextView) findViewById(R.id.cancel);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alterDialog=new AlertDialog.Builder(Add.this);
                alterDialog.setTitle("上传头像").setItems(new String[]{"拍摄","从相册选择"},new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick (DialogInterface v, int i) {
                        if(i==0)
                            photoOp(1);
                        else photoOp(2);
                    }
                }).setNegativeButton("取消",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface v, int i){
                    }
                }).create();
                alterDialog.show();
            }
        });
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
//                            while (!Thread.interrupted()) {     // 反复尝试连接，直到连接成功后退出循环
                try {
                    Thread.sleep(100);  // 每隔0.1秒尝试连接
                    Class.forName("com.mysql.jdbc.Driver");//找到jar包的位置
                }
                catch (InterruptedException e) {
                    Log.v("ss", e.toString());
                }
                catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                String ip = "119.29.229.194";  // 2.设置好IP/端口/数据库名/用户名/密码等必要的连接信息
                int port = 53306;
                String dbName = "android";
                String url = "jdbc:mysql://" + ip + ":" + port+ "/" + dbName; // 构建连接mysql的字符串
                String user = "root";
                String password = "android123";   // 3.连接JDB
                String Sql="select * from social,User_info where social.user_id = User_info.user_id order by id desc limit 1";  //查询id的值
                try {
                    Connection conn = DriverManager.getConnection(url, user, password);
                    Statement st = (Statement) conn.createStatement();         //ResultSet rs = st.executeQuery(sql);
                    ResultSet result = st.executeQuery(Sql);
                    if (result.next()) {
                        id = result.getString("id");//得到
                        int idnumber;                                       //将id的值加1.
                        idnumber= Integer.parseInt(id);
                        idnumber++;
                        id=String.valueOf(idnumber);
                    }
                    url1=id.toString()+".jpg";
                    st.close();
                    conn.close();
                }
                catch (SQLException e) {
                    Log.v("ss", "远程连接失败!");
                    Log.v("ss", e.getMessage());
                }  ///
            }
        });
        thread.start();
        send.setOnClickListener(new View.OnClickListener() {  //发送按钮
            @Override
            public void onClick(View v) {
                if(content.length()==0){
                    TopSnackBar.make(v, "请输入发布的信息。", BaseTransientBottomBar.LENGTH_SHORT)
                            .setAction("确认",new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                }
                            }).show();
                }
                else{
                    String content1=content.getText().toString();//得到输入的内容。
                    final Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
//                            while (!Thread.interrupted()) {     // 反复尝试连接，直到连接成功后退出循环
                            try {
                                Thread.sleep(100);  // 每隔0.1秒尝试连接
                                Class.forName("com.mysql.jdbc.Driver");//找到jar包的位置
                            }
                            catch (InterruptedException e) {
                                Log.v("ss", e.toString());
                            }
                            catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                            String ip = "119.29.229.194";  // 2.设置好IP/端口/数据库名/用户名/密码等必要的连接信息
                            int port = 53306;
                            String dbName = "android";
                            String url = "jdbc:mysql://" + ip + ":" + port+ "/" + dbName; // 构建连接mysql的字符串
                            String user = "root";
                            String password = "android123";   // 3.连接JDB
                            //   String Sql="select * from social,User_info where social.user_id = User_info.user_id order by id desc limit 1";  //查询id的值
                            try {
                                Connection conn = DriverManager.getConnection(url, user, password);
                                Statement st = (Statement) conn.createStatement();         //ResultSet rs = st.executeQuery(sql);
                                String sql = "select user_id from User_info where user_name = '"+userName+"'";
                                ResultSet rs = st.executeQuery(sql);
                                if(rs.next()){
                                    user_id = rs.getInt("user_id");
                                }
                                rs.close();
                                if(flag){
                                    Sql1="insert into social(user_id,content,likes,pic) values('"+user_id+"','"+content1+"',0,'"+url1+"');";
                                    flag=false;
                                }
                                else{
                                    Sql1="insert into social(user_id,content,likes) values('"+user_id+"','"+content1+"',0);";
                                    flag=false;
                                }
                                int Cnt=st.executeUpdate(Sql1);  //执行插入操作。
                                System.out.println(Cnt);
                                if(Cnt!=0){
                                    Intent myintent  = new Intent();
                                    Add.this.setResult(3,myintent);
                                    Add.this.finish();
                                }
                                st.close();
                                conn.close();
//                                return;
                            }

                            catch (SQLException e) {
                                Log.v("ss", "远程连接失败!");
                                Log.v("ss", e.getMessage());
                            }  ///
                        }
                    });
                    thread.start();
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {  //取消按钮
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
        public void photoOp( int op)
        {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_CALL_PHONE2);
            }else {
                if(op==1)
                    takePhoto();
                else if(op==2)
                    choosePhoto();
            }
        }
        public void takePhoto(){
            File file=new File(Environment.getExternalStorageDirectory(),"Picture");
            if(!file.exists()){
                file.mkdir();
            }
            output=new File(file,url1);
            try {
                if (output.exists()) {
                    output.delete();
                }
                output.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
            imageUri = Uri.fromFile(output);
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intent, CROP_PHOTO);
        }
        void choosePhoto(){
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");//相片类型
            startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);

        }

        public void onActivityResult(int req, int res, Intent data) {
            switch (req) {
                case CROP_PHOTO:
                    if (res==RESULT_OK) {
                        try {
                            Bitmap bit = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                            select=1;
                            init();
                            Thread thread2=new Thread(){
                                @Override
                                public void run(){
                                    try {
                                        PutObjectResult putObjectResult = cosXmlService.putObject(putObjectRequest);
                                        ifupload=true;
                                    } catch (CosXmlClientException e) {
                                        //抛出异常
                                        Log.w("TEST","CosXmlClientException =" + e.toString());
                                    } catch (CosXmlServiceException e) {
                                        //抛出异常
                                        Log.w("TEST","CosXmlServiceException =" + e.toString());
                                    }
                                }
                            };
                            thread2.start();
                            while(ifupload==false)
                                ;
                            ifupload=false;
                            ifchange2=true;
                            image.setImageBitmap(bit);
                            flag=true;
                        } catch (Exception e) {
                            Toast.makeText(this,"程序崩溃", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                /**
                 * 从相册中选取图片的请求标志
                 */

                case REQUEST_CODE_PICK_IMAGE:
                    if (res == RESULT_OK) {
                        try {
                            uri = data.getData();
                            Log.v("usi",uri.getEncodedPath());
                            Bitmap bit = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                            select=2;
                            init();
                            Thread thread2=new Thread(){
                                @Override
                                public void run(){
                                    try {
                                        PutObjectResult putObjectResult = cosXmlService.putObject(putObjectRequest);
                                        ifupload=true;
                                    } catch (CosXmlClientException e) {
                                        //抛出异常
                                        Log.w("TEST","CosXmlClientException =" + e.toString());
                                    } catch (CosXmlServiceException e) {
                                        //抛出异常
                                        Log.w("TEST","CosXmlServiceException =" + e.toString());
                                    }
                                }
                            };
                            thread2.start();
                            while(ifupload==false)
                                ;
                            ifupload=false;
                            ifchange2=true;
                            image.setImageBitmap(bit);
                            flag=true;
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(this,"程序崩溃", Toast.LENGTH_SHORT).show();
                        }

                    }
                    else{
                    }
                    break;
                default:
            }
        }
    public void init(){
        String appid = "1255732607";
        String region = "ap-guangzhou";
        String secretId = "AKIDZ8c0JW9hhcRBpOseN6jGh2IXinnghCqO";
        String secretKey ="hImDdxALoJm9QjDd5Ry3GUev7DD91oYC";
        String bucket = "zhyhhh-1255732607"; // cos v5 的 bucket格式为：xxx-appid, 如 test-1253960454
        String cosPath = "/image/socialimage/"+url1; //格式如 cosPath = "/test.txt";
        long keyDuration = 600; //SecretKey 的有效时间，单位秒
        long signDuration = 600; //签名的有效期，单位为秒
        serviceConfig = new CosXmlServiceConfig.Builder()
                .setAppidAndRegion(appid, region)
                .setDebuggable(true)
                .setConnectionTimeout(45000)
                .setSocketTimeout(30000)
                .build();

        //创建 CosXmlServiceConfig 对象，根据需要修改默认的配置参数
        //创建获取签名类
        LocalCredentialProvider localCredentialProvider = new LocalCredentialProvider(secretId, secretKey, keyDuration);
        //创建 CosXmlService 对象，实现对象存储服务各项操作.
        Context context = getApplicationContext();//应用的上下文
        cosXmlService = new CosXmlService(context,serviceConfig, localCredentialProvider);
        // String srcPath = Environment.getExternalStorageDirectory().getPath() + "/1.jpg";
        String src="";
        if(select==1)
            src=getRealFilePath(context,imageUri);
        else if(select==2)
            src=getRealFilePath(context,uri);
        String srcPath =src; // 如 srcPath = Environment.getExternalStorageDirectory().getPath() + "/test.txt";
        Log.v("src",src);
        putObjectRequest = new PutObjectRequest(bucket, cosPath, srcPath);
        putObjectRequest.setSign(signDuration,null,null);
     /*设置进度显示
        实现 QCloudProgressListener.onProgress(long progress, long max)方法，
        progress 已上传的大小， max 表示文件的总大小
     */
        putObjectRequest.setProgressListener(new QCloudProgressListener() {
            @Override
            public void onProgress(long progress, long max) {
                float result = (float) (progress * 200.0/max);
                Log.w("TEST","progress =" + (long)result + "%");
            }
        });

    }
    public static String getRealFilePath(final Context context, final Uri uri ) {
        if ( null == uri ) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if ( scheme == null )
            data = uri.getPath();
        else if ( ContentResolver.SCHEME_FILE.equals( scheme ) ) {
            data = uri.getPath();
        } else if ( ContentResolver.SCHEME_CONTENT.equals( scheme ) ) {
            Cursor cursor = context.getContentResolver().query( uri, new String[] { MediaStore.Images.ImageColumns.DATA }, null, null, null );
            if ( null != cursor ) {
                if ( cursor.moveToFirst() ) {
                    int index = cursor.getColumnIndex( MediaStore.Images.ImageColumns.DATA );
                    if ( index > -1 ) {
                        data = cursor.getString( index );
                    }
                }
                cursor.close();
            }
        }
        return data;
    }
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 123:
//                    String data="注册成功请登录！";
//                    Intent intp=new Intent(register.this,Login.class);
//                    intp.putExtra("extra_data",data);
//                    startActivity(intp);
//                    register.this.finish();
                    break;
                case 124:
//                    Toast.makeText(Login.this, "请检查输入信息", Toast.LENGTH_SHORT).show();
                    break;
                default:break;
            }
        }
    };
    }
