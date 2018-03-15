package com.app.finalpro.user;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.finalpro.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tencent.cos.xml.CosXmlService;
import com.tencent.cos.xml.CosXmlServiceConfig;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.model.object.PutObjectRequest;
import com.tencent.cos.xml.model.object.PutObjectResult;
import com.tencent.qcloud.core.network.QCloudProgressListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static com.app.finalpro.user.ChangeBitmap.show;


/**
 * Created by ER on 2017/12/31.
 */

public class User_infomation extends AppCompatActivity {
    User_info userifo=new User_info();
    TextView textViewname;
    TextView textViewnum;
    ImageView imageViewsettx;
    ConstraintLayout constraintLayoutimage;
    ConstraintLayout constraintLayoutnum;
    ImageView imageViewback;
    private String info;
    private Bitmap bitmap;
    private Bitmap bit;
    private boolean ifupload=false;
    private String urlimg;
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
    private  Uri uri;
    private int select=0;
    User_info user_info;
    boolean ifchange2=false;
    private String base_url="http://zhyhhh-1255732607.cosgz.myqcloud.com/image/userimage/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_infomation);
        textViewname=(TextView)findViewById(R.id.username2);
        textViewnum=(TextView)findViewById(R.id.usernum);
        imageViewsettx=(ImageView)findViewById(R.id.imagesettx);
        constraintLayoutimage=(ConstraintLayout)findViewById(R.id.userpicture);
        constraintLayoutnum=(ConstraintLayout)findViewById(R.id.usernum2);
        imageViewback=(ImageView)findViewById(R.id.ImageViewback);
        //init();
        Intent intent = getIntent();
        final Bundle bundle=intent.getExtras();
        info=bundle.getString("user");
        getUserinfo(info);

        imageViewback.setOnClickListener(new ImageView.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent myintent  = new Intent();

                User_infomation.this.setResult(2, myintent);
                finish();
            }
        });
        constraintLayoutimage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                final AlertDialog.Builder alterDialog=new AlertDialog.Builder(User_infomation.this);
                alterDialog.setTitle("上传头像").setItems(new String[]{"拍摄","从相册选择"},new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick (DialogInterface v,int i) {
                        if(i==0)
                        photoOp(1);
                        else photoOp(2);
                        user_info.setUserimage(user_info.getId()+".jpg");
                        updateUserinfo(user_info);
                    }
                }).setNegativeButton("取消",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface v,int i){
                    }
                }).create();
                alterDialog.show();
            }
        });
        constraintLayoutnum.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Bundle bundle=new Bundle();
                bundle.putString("user",info);
                Intent intent=new Intent(User_infomation.this,ChangeItem.class);
                intent.putExtras(bundle);
                startActivityForResult(intent,CHANGE_INFO);
            }
        });
    }
    Thread thread=new Thread(){
        @Override
        public void run(){
            try {

                URL url=new URL(urlimg);
//                InputStream is= url.openStream();
//                bitmap = BitmapFactory.decodeStream(is);
                handler.sendEmptyMessage(1);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    };
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what==1) {
                Glide.with(User_infomation.this).load(urlimg)
                        .transform(new GlideCircleTransform(User_infomation.this))
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(imageViewsettx);
//                Bitmap bitmap1=show(bitmap);
//                imageViewsettx.setImageBitmap(bitmap1);

            }
            else if(msg.what==2) {
                user_info = userifo;
                if (!user_info.getUserimage().equals("0.jpg")) {
                    urlimg = base_url + user_info.getUserimage();
                    thread.start();
                }
            }
                else if(msg.what==3){
                    Bitmap bitmap1=show(bit);
                    Log.v("hello","hello");
                    imageViewsettx.setImageBitmap(bitmap1);
                }

                textViewname.setText(user_info.getUser_name());
                Log.v("username",user_info.getUser_name());
                textViewnum.setText(user_info.getNumber());

        }
    };
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
        output=new File(file,user_info.getUserimage());
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

                        select=1;
                        init();
                        Thread thread2=new Thread(){
                            @Override
                            public void run(){
                                try {
                                    bit = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                                    PutObjectResult putObjectResult = cosXmlService.putObject(putObjectRequest);
                                    handler.obtainMessage(3).sendToTarget();
                                    return;
                                } catch (CosXmlClientException e) {
                                    //抛出异常
                                    Log.w("TEST","CosXmlClientException =" + e.toString());
                                } catch (CosXmlServiceException e) {
                                    //抛出异常
                                    Log.w("TEST","CosXmlServiceException =" + e.toString());
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        thread2.start();

                    } catch (Exception e) {
                        Toast.makeText(this,"程序崩溃",Toast.LENGTH_SHORT).show();
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
                        select=2;
                        init();
                        Thread thread2=new Thread(){
                            @Override
                            public void run(){
                                try {
                                    bit = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                                    PutObjectResult putObjectResult = cosXmlService.putObject(putObjectRequest);
                                    ifupload=true;
                                    handler.obtainMessage(3).sendToTarget();
                                    return;
                                } catch (CosXmlClientException e) {
                                    //抛出异常
                                    Log.w("TEST","CosXmlClientException =" + e.toString());
                                } catch (CosXmlServiceException e) {
                                    //抛出异常
                                    Log.w("TEST","CosXmlServiceException =" + e.toString());
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        thread2.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this,"程序崩溃",Toast.LENGTH_SHORT).show();
                    }

                }
                break;
            default:
            case CHANGE_INFO:
                Bundle bundleget=data.getExtras();

                if(bundleget!=null)
                {
                    Boolean ifchange=bundleget.getBoolean("change");
                    if(ifchange)
                    {
                        getUserinfo(info);
                        textViewname.setText(user_info.getUser_name());
                        Log.v("username",user_info.getUser_name());
                        textViewnum.setText(user_info.getNumber());
                    }
                }
                break;
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {

        if (requestCode == MY_PERMISSIONS_REQUEST_CALL_PHONE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                takePhoto();
            } else
            {
                // Permission Denied
                Toast.makeText(User_infomation.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }


        if (requestCode == MY_PERMISSIONS_REQUEST_CALL_PHONE2)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                choosePhoto();
            } else
            {
                // Permission Denied
                Toast.makeText(User_infomation.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    public void init(){
        String appid = "1255732607";
        String region = "ap-guangzhou";
        String secretId = "AKIDZ8c0JW9hhcRBpOseN6jGh2IXinnghCqO";
        String secretKey ="hImDdxALoJm9QjDd5Ry3GUev7DD91oYC";
        String bucket = "zhyhhh-1255732607"; // cos v5 的 bucket格式为：xxx-appid, 如 test-1253960454
        String cosPath = "/image/userimage/"+user_info.getId()+".jpg"; //格式如 cosPath = "/test.txt";
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

    public static String getRealFilePath( final Context context, final Uri uri ) {
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
    public void getUserinfo(final String name)
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
                        e.printStackTrace();
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
                        String sql = "select * from User_info where user_name='" + name+"'";
                        Statement st = (Statement) conn.createStatement();
                        ResultSet resultSet = st.executeQuery(sql);
                        if (resultSet.next()) {
                            Log.v("user",resultSet.getString(("user_name")));
                            userifo.setId(resultSet.getInt(("user_id")));
                            userifo.setUser_name(resultSet.getString(("user_name")));
                            userifo.setPassword(resultSet.getString(("password")));
                            userifo.setNumber(resultSet.getString(("number")));
                            userifo.setUserimage(resultSet.getString(("image")));
                            userifo.setPhone(resultSet.getString(("phone")));
                            Log.v("eeeee", String.valueOf(userifo.getId()));
                            handler.obtainMessage(2).sendToTarget();
                            resultSet.close();
                            st.close();
                            conn.close();
                            Log.v("用户信息","获取");
                            return;
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                        Log.v("用户信息异常:", String.valueOf(e.getMessage()));
                    }
                }
            }

        });
        thread.start();
    }
    public void updateUserinfo(final User_info user_info){
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
                        String sql = "update User_info "
                                +" set number='"+user_info.getNumber()
                                +"', password='"+user_info.getPassword()
                                +"', image='"+user_info.getUserimage()
                                +"', phone='"+user_info.getPhone()
                                +"' where user_name='" + user_info.getUser_name()+"'";
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
}
