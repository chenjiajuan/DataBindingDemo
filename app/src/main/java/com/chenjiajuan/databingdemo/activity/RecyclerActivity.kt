package com.chenjiajuan.databingdemo.activity

import android.app.Activity
import android.databinding.DataBindingUtil
import android.graphics.Rect
import android.os.*
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
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.annotations.NonNull
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Function
import io.reactivex.internal.operators.flowable.FlowableBlockingSubscribe.subscribe
import io.reactivex.schedulers.Schedulers
import okhttp3.*
import java.io.IOException
import java.util.*

/**
 * Created by chenjiajuan on 2018/5/23.
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
        //requestDate()
        requestDataRx2BuyOkHttp()
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
     *
     * 想必大家都知道，很多时候我们在使用 RxJava 的时候总是和 Retrofit 进行结合使用，而为了方便演示，这里我们就暂且采用 OkHttp3 进行演示，
     * 配合 map，doOnNext ，线程切换进行简单的网络请求：
    1）通过 Observable.create() 方法，调用 OkHttp 网络请求；
    2）通过 map 操作符集合 gson，将 Response 转换为 bean 类；
    3）通过 doOnNext() 方法，解析 bean 中的数据，并进行数据库存储等操作；
    4）调度线程，在子线程中进行耗时操作任务，在主线程中更新 UI ；
    5）通过 subscribe()，根据请求成功或者失败来更新 UI 。



    map(new Function<Response, MobileAddress>() {
    @Override
    public MobileAddress apply(@NonNull Response response) throws Exception {
    if (response.isSuccessful()) {
    ResponseBody body = response.body();
    if (body != null) {
    Log.e(TAG, "map:转换前:" + response.body());
    return new Gson().fromJson(body.string(), MobileAddress.class);
    }
    }
    return null;
    }
    }).observeOn(AndroidSchedulers.mainThread())
    .doOnNext(new Consumer<MobileAddress>() {
    @Override
    public void accept(@NonNull MobileAddress s) throws Exception {
    Log.e(TAG, "doOnNext: 保存成功：" + s.toString() + "\n");
    }
    }).subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())
    .subscribe(new Consumer<MobileAddress>() {
    @Override
    public void accept(@NonNull MobileAddress data) throws Exception {
    Log.e(TAG, "成功:" + data.toString() + "\n");
    }, new Consumer<Throwable>() {
    @Override
    public void accept(@NonNull Throwable throwable) throws Exception {
    Log.e(TAG, "失败：" + throwable.getMessage() + "\n");
    }
    });

     */


    private fun  requestDataRx2BuyOkHttp(){
        Observable.create(ObservableOnSubscribe<BookItem> { e ->
            var  call=OkHttpClient().newCall(Request.Builder().get().url("${url+count}").build())
            var  response=call.execute()
        })

    }


    /**
     * 获取网络消息  --先创建被观察者，由其发起事件
     *            ----设置被观察者执行的线程
     *            ----设置观察者的线程，（主线程or子线程由于处理事件）
     *            ----创建观察者，由其处理后续事件
     */
    private fun requestDadaRx2(){
        Observable.create(ObservableOnSubscribe<String> { e ->
            e.onNext("1")
            e.onComplete()
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object :Observer<String>{
            //初始化observer
            override fun onSubscribe(d: Disposable) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onComplete() {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onNext(t: String) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onError(e: Throwable) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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