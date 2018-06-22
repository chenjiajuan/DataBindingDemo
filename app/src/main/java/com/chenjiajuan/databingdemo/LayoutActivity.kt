package com.chenjiajuan.databingdemo

import android.app.Activity
import android.databinding.DataBindingUtil
import android.os.Bundle


/**
 * Created by chenjiajuan on 2018/6/22.
 * * layout的使用
 */
class LayoutActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
      DataBindingUtil.setContentView(this@LayoutActivity,R.layout.activity_layout)
    }
}
