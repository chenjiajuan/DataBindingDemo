package com.chenjiajuan.databingdemo.activity

import android.app.Activity
import android.content.Context
import android.databinding.DataBindingUtil
import android.graphics.Rect
import android.os.*
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Toast
import com.chenjiajuan.databingdemo.R
import com.chenjiajuan.databingdemo.adapter.BookAdapter
import com.chenjiajuan.databingdemo.databinding.ActivityRecyclerBinding
import com.chenjiajuan.databingdemo.model.BookItem
import com.chenjiajuan.databingdemo.model.BooksBean
import com.google.gson.Gson
import okhttp3.*
import java.io.IOException
import java.util.*

/**
 * Created by chenjiajuan on 2018/6/23.
 */
class RecyclerActivity:Activity() {
    private var TAG:String="RecyclerActivity"
    private var recyclerDataBiding:ActivityRecyclerBinding?=null
    private var count:Int=16
    private var oldCount:Int=count
    private var url="https://api.douban.com/v2/book/search?tag=科幻&count="
    private var bookAdapter: BookAdapter?=null
    private var bookData:BookItem?=null
    private var layoutManager:GridLayoutManager?=null
    private var refresh:Boolean=false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        recyclerDataBiding=DataBindingUtil.setContentView(this@RecyclerActivity, R.layout.activity_recycler)
        layoutManager=GridLayoutManager(this,2)
        layoutManager?.spanSizeLookup=object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if ( bookAdapter?.getItemViewType(position)==1){
                    bookAdapter?.ITEM_TYPE_BOOK as Int
                }else{
                    bookAdapter?.ITEM_TYPE_BOTTOM as Int
                }
            }

        }
        recyclerDataBiding?.recyclerView?.layoutManager= layoutManager
        recyclerDataBiding?.recyclerView?.addItemDecoration(SpacesItemDecoration(10))
        recyclerDataBiding?.recyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                when(newState){
                     RecyclerView.SCROLL_STATE_IDLE->{
                        if (isScrollToBottom()){
                            oldCount=count
                            count+=22
                            refresh=!refresh
                            requestDate()
                        }
                    }
                }



            }
        })
        bookAdapter= BookAdapter(this@RecyclerActivity)
        recyclerDataBiding?.recyclerView?.adapter = bookAdapter
        bookAdapter?.setOnItemClickListener(object:BookAdapter.onItemClickListener{
            override fun onItemClick(position: Int) {
                Toast.makeText(this@RecyclerActivity,"position : "+position,Toast.LENGTH_SHORT).show()
            }
        })
        requestDate()
    }

    /**
     * 获取数据
     */
    private fun requestDate() {
        var okhttp = OkHttpClient()
        var newUrl=url+count+""
        var builder = Request.Builder().get().url(newUrl)
        var request = builder.build()
        var call = okhttp.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {

            }
            override fun onResponse(call: Call?, response: Response?) {
                var string = response?.body()?.string()
                bookData = Gson().fromJson(string,BookItem::class.java)
                myHandler.sendEmptyMessage(0)

            }
        })
    }

    /**
     * 底部刷新列表
     */
    private fun refresh( bookList: ArrayList<BooksBean>){
        var  bookList2: ArrayList<BooksBean> =ArrayList()
        if (refresh){
            (oldCount..(bookList.size-1)).mapTo(bookList2) { bookList[it] }
            bookAdapter?.addListData(bookList2)
            refresh=!refresh

        }else{
            bookAdapter?.addListData(bookList)
        }
        bookAdapter?.notifyDataSetChanged()
    }

    private val myHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message?) {
            refresh(bookData?.books as ArrayList<BooksBean>)
        }
    }

    /**
     * 分割线线
     */
    inner class SpacesItemDecoration(private var space:Int=16):RecyclerView.ItemDecoration(){
        override fun getItemOffsets(outRect: Rect?, view: View?, parent: RecyclerView?, state: RecyclerView.State?) {
            outRect?.top=space
            outRect?.bottom=space
        }
    }

    /**
     * 判断是否已经滑动到底部
     */
    private fun isScrollToBottom():Boolean {
        var nowBottoms = layoutManager?.findLastVisibleItemPosition()
        var visibleCount = layoutManager?.childCount as Int
        var totalCount = layoutManager?.itemCount as Int
        if (visibleCount>0 && nowBottoms==totalCount-1){
            return true
        }
        return false
    }


}