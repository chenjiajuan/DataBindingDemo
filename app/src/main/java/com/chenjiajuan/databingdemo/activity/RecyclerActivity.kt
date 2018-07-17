package com.chenjiajuan.databingdemo.activity

import android.app.Activity
import android.databinding.DataBindingUtil
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.Toast
import com.chenjiajuan.databingdemo.R
import com.chenjiajuan.databingdemo.adapter.BookAdapter
import com.chenjiajuan.databingdemo.databinding.ActivityRecyclerBinding
import com.chenjiajuan.databingdemo.model.BookItem
import com.chenjiajuan.databingdemo.model.BooksBean
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url
import java.io.IOException
import java.util.*

/**
 * Created by chenjiajuan on 2018/5/23.
 */
class RecyclerActivity : Activity() {
    private var TAG: String = "RecyclerActivity"
    private var recyclerDataBiding: ActivityRecyclerBinding? = null
    private var count: Int = 16
    private var start: Int = 0
    private var url = "https://api.douban.com/v2/book/search?tag=科幻&count="
    private var basurl = "https://api.douban.com/v2/book/"
    private var bookAdapter: BookAdapter? = null
    private var bookData: BookItem? = null
    private var layoutManager: GridLayoutManager? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        recyclerDataBiding = DataBindingUtil.setContentView(this@RecyclerActivity, R.layout.activity_recycler)
        layoutManager = GridLayoutManager(this, 2)
        layoutManager?.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (bookAdapter?.getItemViewType(position) == 1) {
                    bookAdapter?.ITEM_TYPE_BOOK as Int
                } else {
                    bookAdapter?.ITEM_TYPE_BOTTOM as Int
                }
            }

        }
        recyclerDataBiding?.recyclerView?.layoutManager = layoutManager
        recyclerDataBiding?.recyclerView?.addItemDecoration(SpacesItemDecoration(10))
        recyclerDataBiding?.recyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                when (newState) {
                    RecyclerView.SCROLL_STATE_IDLE -> {
                        if (!isScrollToBottom())
                            return
                        start +=count
//                        requestDataRxjava2()
                        refroft()
                    }
                }
            }
        })
        bookAdapter = BookAdapter(this@RecyclerActivity)
        recyclerDataBiding?.recyclerView?.adapter = bookAdapter
        bookAdapter?.setOnItemClickListener(object : BookAdapter.onItemClickListener {
            override fun onItemClick(position: Int) {
                Toast.makeText(this@RecyclerActivity, "position : " + position, Toast.LENGTH_SHORT).show()
            }
        })
        refroft()
//        requestDataRxjava2()
        //requestDate()
    }

    /**
     * 获取数据
     */
    private fun requestDate() {
        var okhttp = OkHttpClient()
        var newUrl = url + count + ""
        var builder = Request.Builder().get().url(newUrl)
        var request = builder.build()
        var call = okhttp.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
            }

            override fun onResponse(call: Call?, response: Response?) {
                var string = response?.body()?.string()
                bookData = Gson().fromJson(string, BookItem::class.java)
                myHandler.sendEmptyMessage(0)
            }
        })
    }

    interface BookService{
        @GET("search")
       fun getBooList(@Query("tag")tag :String ,@Query("start")start :Int,@Query("count") count :Int):retrofit2.Call<BookItem>
    }
     private fun refroft(){
         var retrofit =Retrofit.Builder().baseUrl(basurl)
                 .addConverterFactory(GsonConverterFactory.create()).build()
         var bookService=retrofit.create(BookService::class.java)
         var  call=bookService.getBooList("科幻",start,count)
              Log.e(TAG,"url :${call.request().url()}")
         call.enqueue(object :retrofit2.Callback<BookItem>{
             override fun onResponse(call: retrofit2.Call<BookItem>?, response: retrofit2.Response<BookItem>?) {
                 Log.e(TAG," response : "+response?.body().toString())
                 bookData=response?.body()
                 refresh(bookData?.books as ArrayList<BooksBean>)
             }

             override fun onFailure(call: retrofit2.Call<BookItem>?, t: Throwable?) {
             }
         })


     }


    private fun requestDataRxjava2() {
        Observable.create(ObservableOnSubscribe<Response> { e ->
            var newUrl = url + count + ""
            Log.e(TAG, "newUrl : $newUrl")
            var request = Request.Builder().get().url(newUrl).build()
            var call = OkHttpClient().newCall(request)
            var response = call.execute()
            e.onNext(response)
        }).map(Function<Response, BookItem> { t ->
            if (!t.isSuccessful || t == null || t.body() == null)
                null
            var string = t.body()?.string()
            var book = Gson().fromJson(string, BookItem::class.java)
            return@Function book
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { t ->
                    bookData = t
                    refresh(bookData?.books as ArrayList<BooksBean>)
                }
    }

    /**
     *

    private fun requestDataRxjava2() {
    Observable.create(ObservableOnSubscribe<Response> { e ->
    var newUrl = url + count + ""
    Log.e(TAG, "newUrl : $newUrl")
    var request = Request.Builder().get().url(newUrl).build()
    var call = OkHttpClient().newCall(request)
    var response = call.execute()
    //            Log.e(TAG,"subscribe 1 , currentThread : ${Thread.currentThread()}")
    e.onNext(response)
    //            Log.e(TAG,"subscribe 2 , currentThread : ${Thread.currentThread()}")
    }).map(Function<Response, BookItem> { t ->
    if (!t.isSuccessful || t == null || t.body() == null)
    null
    var string = t.body().string()
    var book = Gson().fromJson(string, BookItem::class.java)
    //            Log.e(TAG,"map 2 , currentThread : ${Thread.currentThread()}")
    return@Function book
    }).subscribeOn(Schedulers.newThread())
    .observeOn(AndroidSchedulers.mainThread())
    .subscribe { t ->
    //                    Log.e(TAG,"accept , currentThread : ${Thread.currentThread()}")
    bookData = t
    refresh(bookData?.books as ArrayList<BooksBean>)
    }
    }

     */

//    private fun requestDataRxjava2() {
//        Observable.create(ObservableOnSubscribe<BookItem> { e ->
//            var newUrl = url + count + ""
//            var request = Request.Builder().get().url(newUrl).build()
//            var call = OkHttpClient().newCall(request)
//            var response = call.execute()
//            if (!response.isSuccessful || response.body() == null)
//                null
//            var string = response.body()!!.string()
//            var book = Gson().fromJson(string, BookItem::class.java)
//            e.onNext(book)
//        }).subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe { t ->
//                    bookData = t
//                    refresh(bookData?.books as ArrayList<BooksBean>)
//                }
//    }


    private fun testMap() {
        Observable.create(ObservableOnSubscribe<Int> { e ->
            Log.e(TAG, "subscribe 1 , currentThread : ${Thread.currentThread()}")
            e.onNext(1)
            Log.e(TAG, "subscribe 2 , currentThread : ${Thread.currentThread()}")
        }).map(Function<Int, String> { t ->
            Log.e(TAG, "map 2 , currentThread : ${Thread.currentThread()}")
            "hello $t"
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribe { t ->
                    Log.e(TAG, "accept , currentThread : ${Thread.currentThread()} + t :$t")
                }

    }

    /**
     * 底部刷新列表
     */
    private fun refresh(bookList: ArrayList<BooksBean>) {
        bookAdapter?.addListData(bookList)
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
    inner class SpacesItemDecoration(private var space: Int = 16) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect?, view: View?, parent: RecyclerView?, state: RecyclerView.State?) {
            outRect?.top = space
            outRect?.bottom = space
        }
    }

    /**
     * 判断是否已经滑动到底部
     */
    private fun isScrollToBottom(): Boolean {
        var nowBottoms = layoutManager?.findLastVisibleItemPosition()
        var visibleCount = layoutManager?.childCount as Int
        var totalCount = layoutManager?.itemCount as Int
        if (visibleCount > 0 && nowBottoms == totalCount - 1) {
            return true
        }
        return false
    }


}