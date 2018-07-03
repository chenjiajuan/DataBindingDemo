package com.chenjiajuan.databingdemo.activity

import android.app.Activity
import android.content.Context
import android.databinding.DataBindingUtil
import android.graphics.Rect
import android.os.*
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.Log
import android.view.View
import android.widget.Toast
import com.chenjiajuan.databingdemo.BookAdapter
import com.chenjiajuan.databingdemo.R
import com.chenjiajuan.databingdemo.databinding.ActivityRecyclerBinding
import com.chenjiajuan.databingdemo.model.BookItem
import com.chenjiajuan.databingdemo.model.BooksBean
import com.google.gson.Gson
import okhttp3.*
import java.io.IOException

/**
 * Created by chenjiajuan on 2018/6/23.
 */
class RecyclerActivity:Activity() {
    private var TAG:String="RecyclerActivity"
    private var recyclerDataBiding:ActivityRecyclerBinding?=null
    private var url="https://api.douban.com/v2/book/search?tag=科幻&count=16"
    private var bookAdapter: BookAdapter?=null
    private var bookData:BookItem?=null
    private var staggeredGridLayoutManager:StaggeredGridLayoutManager?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        recyclerDataBiding=DataBindingUtil.setContentView(this@RecyclerActivity, R.layout.activity_recycler)
        staggeredGridLayoutManager=StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        recyclerDataBiding?.recyclerView?.layoutManager= staggeredGridLayoutManager
        recyclerDataBiding?.recyclerView?.addItemDecoration(SpacesItemDecoration(10))
        recyclerDataBiding?.recyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                Log.e(TAG,"onScrollStateChanged .....")
                if (newState==0)
                if (isScrollToBottom()){
                    requestDate()
                }


            }

            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                Log.d(TAG,"onScrolled dx : $dx , dy : $dy")
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
                myHandler.sendEmptyMessage(0)

            }
        })
    }

    private fun refresh(bookList: ArrayList<BooksBean>){
        bookAdapter?.addListData(bookList)
        bookAdapter?.notifyDataSetChanged()
    }
    private val myHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message?) {
            refresh(bookData?.books as ArrayList<BooksBean>)
        }
    }
    inner class SpacesItemDecoration(private var space:Int=16):RecyclerView.ItemDecoration(){
        override fun getItemOffsets(outRect: Rect?, view: View?, parent: RecyclerView?, state: RecyclerView.State?) {
            outRect?.top=space
            outRect?.bottom=space
        }
    }

    private fun isScrollToBottom():Boolean {
        var spaceCount = staggeredGridLayoutManager?.spanCount as Int
        var positions = intArrayOf(spaceCount)
        var nowBottoms = staggeredGridLayoutManager?.findLastVisibleItemPositions(null)
        var visibleCount = staggeredGridLayoutManager?.childCount as Int
        var totalCount = staggeredGridLayoutManager?.itemCount as Int
        Log.e(TAG," positions :$positions ,spaceCount :$spaceCount ," +
                "nowBottoms: $nowBottoms , visibleCount :$visibleCount , totalCount : $totalCount")
        if (nowBottoms!=null && nowBottoms.isNotEmpty()) {
            nowBottoms.indices
                    .filter {
                        visibleCount>0 && nowBottoms[it] ==totalCount-1
                    }
                    .forEach {
                        Log.e(TAG,"滑到底部")
                    }
                    .let {
                        Log.e(TAG,"再次请求数据")
                      return true
                    }
        }
        return false
        /**
         *
         *   //                var totalHeight = recyclerDataBiding?.recyclerView?.height as Int
        //                var positionHeight = staggeredGridLayoutManager?.findViewByPosition(bottom)!!.bottom
        //                Log.e(TAG,"total :$totalHeight , positionHeight : $positionHeight")
        //                if (bottom >= totalCount && positionHeight <= totalHeight) {
        //                    return true
        //                }
         *
         *
         *
         *
        public static boolean isVisBottom(RecyclerView recyclerView){
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        //屏幕中最后一个可见子项的position
        int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
        //当前屏幕所看到的子项个数
        int visibleItemCount = layoutManager.getChildCount();
        //当前RecyclerView的所有子项个数
        int totalItemCount = layoutManager.getItemCount();
        //RecyclerView的滑动状态
        int state = recyclerView.getScrollState();
        if(visibleItemCount > 0 && lastVisibleItemPosition == totalItemCount - 1 && state == recyclerView.SCROLL_STATE_IDLE){
        return true;
        }else {
        return false;
        }
        }
         */
    }

}