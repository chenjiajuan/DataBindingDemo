package com.chenjiajuan.databingdemo

import android.app.Activity
import android.databinding.DataBindingUtil
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.Log
import android.view.View
import com.chenjiajuan.databingdemo.databinding.ActivityRecyclerBinding
import com.chenjiajuan.databingdemo.model.BookItem
import com.google.gson.Gson
import okhttp3.*
import java.io.IOException

/**
 * Created by chenjiajuan on 2018/6/23.
 */
class RecyclerActivity:Activity() {
    private var TAG:String="RecyclerActivity"
    private var recyclerDataBiding:ActivityRecyclerBinding?=null
    private var url="https://api.douban.com/v2/book/search?tag=科幻&count=40"
    private var bookAdapter:BookAdapter?=null
    private var bookData:BookItem?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        recyclerDataBiding=DataBindingUtil.setContentView(this@RecyclerActivity,R.layout.activity_recycler)
        recyclerDataBiding?.recyclerView?.layoutManager= StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        recyclerDataBiding?.recyclerView?.addItemDecoration(SpacesItemDecoration(10))
        requestDate()
    }

    private fun requestDate() {
        var okphht = OkHttpClient()
        Log.d(TAG,"url : "+url)
        var builder = Request.Builder().get().url(url)
        var request = builder.build()
        var call = okphht.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {

            }

            override fun onResponse(call: Call?, response: Response?) {
                var string = response?.body()?.string()
                 bookData = Gson().fromJson(string,BookItem::class.java)
                Log.d(TAG,bookData.toString())
                bookAdapter=BookAdapter(this@RecyclerActivity ,bookData!!)
                myHandler.sendEmptyMessage(0)
            }
        })
    }
    private val myHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message?) {
            recyclerDataBiding?.recyclerView?.adapter = bookAdapter
        }
    }
    inner class SpacesItemDecoration(private var space:Int=16):RecyclerView.ItemDecoration(){
        override fun getItemOffsets(outRect: Rect?, view: View?, parent: RecyclerView?, state: RecyclerView.State?) {
            outRect?.top=space
            outRect?.bottom=space
        }
    }

}