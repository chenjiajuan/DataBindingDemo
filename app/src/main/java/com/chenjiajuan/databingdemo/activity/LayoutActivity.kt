package com.chenjiajuan.databingdemo.activity

import android.app.Activity
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.chenjiajuan.databingdemo.R
import com.chenjiajuan.databingdemo.data.User
import com.chenjiajuan.databingdemo.databinding.ActivityLayoutBinding


/**
 * Created by chenjiajuan on 2018/6/22.
 */
class LayoutActivity : Activity() {
    private var TAG:String="LayoutActivity"
    private var layoutData: ActivityLayoutBinding?= null
    private var user:User ?=null
    private var presenter: ClickPresenter?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutData= DataBindingUtil.setContentView(this@LayoutActivity, R.layout.activity_layout)
        user= User("Tom","123$23hgf",25)
        layoutData?.user=this.user
        presenter=ClickPresenter()
        layoutData?.presenter=this.presenter

}


    inner class ClickPresenter{
       fun submit(view: View){
            Log.e(TAG,"submit")
            Toast.makeText(this@LayoutActivity,"submit",Toast.LENGTH_SHORT).show()
        }

        fun submit2(){
            Log.e(TAG,"submit2")
            Toast.makeText(this@LayoutActivity,"submit2",Toast.LENGTH_SHORT).show()
        }

        fun submit3(view: View){
            Log.e(TAG,"submit3")
            Toast.makeText(this@LayoutActivity,"submit3",Toast.LENGTH_SHORT).show()
        }

         fun  submit4(password:String){
            Log.e(TAG,"submit4")
            Toast.makeText(this@LayoutActivity,"submit4 : "+password,Toast.LENGTH_SHORT).show()
        }

        fun onTextChanged(sequence:CharSequence,istart:Int,before:Int,count: Int ){
            Toast.makeText(this@LayoutActivity,sequence.toString(),Toast.LENGTH_SHORT).show()
        }


    }

}
