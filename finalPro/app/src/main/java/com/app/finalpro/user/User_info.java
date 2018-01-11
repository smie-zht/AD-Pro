package com.app.finalpro.user;

import java.io.Serializable;

/**
 * Created by ER on 2017/12/30.
 */

public class User_info  implements Serializable {
    private int id;
    private String user_name;
    private String password;
    private String number;
    private String userimage;
    private String phone;
    public User_info(){
        id=0;
        user_name="";
        password="";
        number="";
        userimage="";
        phone="";
    }
    public void setId(int id){
        this.id=id;
    }
    public void setUser_name(String user_name){
        this.user_name=user_name;
    }
    public void setPassword(String password){
        this.password=password;
    }
    public void setNumber(String number){
        this.number=number;
    }
    public void setUserimage(String userimage){
        this.userimage=userimage;
    }
    public void setPhone(String phone){
        this.phone=phone;
    }
    public int getId(){
        return id;
    }
    public String getUser_name()
    {
        return user_name;
    }
    public String getPassword(){
        return password;
    }
    public String getUserimage(){
        return userimage;
    }
    public String getNumber(){
        return number;
    }
    public String getPhone(){
        return phone;
    }
    public String getPhone2(){
        String s;
        s=phone.substring(0,3)+"****"+phone.substring(7,11);
        return s;
    }
}
