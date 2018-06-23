package com.chenjiajuan.databingdemo.data

import android.databinding.BaseObservable
import android.databinding.Bindable
import com.chenjiajuan.databingdemo.BR

/**
 * Created by chenjiajuan on 2018/6/22.
 */
class User(name:String,password:String,age:Int): BaseObservable() {

    @Bindable
    var name:String =name
    set(value) {
        field=name
        notifyPropertyChanged(BR.name)
    }

    @Bindable
    var password:String=password
    set(value) {
        field=password
        notifyPropertyChanged(BR.password)
    }

    @Bindable
    var age:Int=age
    set(value) {
        field=age
    }

}