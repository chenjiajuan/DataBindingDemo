package com.chenjiajuan.databingdemo.data;

/**
 * Created by chenjiajuan on 2018/6/22.
 */

public class User {

    public final  String firstName;
    public final String lastName;
    public User(String firstName,String lastName){
        this.firstName=firstName;
        this.lastName=lastName;

    }
    public String getFirstName(){
        return this.firstName;
    }

    public String getLastName(){
        return this.lastName;
    }
}
