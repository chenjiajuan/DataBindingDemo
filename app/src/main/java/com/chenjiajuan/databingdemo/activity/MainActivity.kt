package com.chenjiajuan.databingdemo.activity

import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.chenjiajuan.databingdemo.R
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import java.util.concurrent.TimeUnit

class MainActivity : Activity() {
    private var TAG: String = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        exampleFlowableBuffer()
    }


    private fun example1() {
        //create产生一个被观察者对象，view.setonClickListener(new OnClickListener{ .......})
        Observable.create(ObservableOnSubscribe<Int> { e ->
            //触发规则，触发顺序，返回一个被观察者对象
            e.onNext(1)
            e.onNext(2)
            e.onComplete()
            e.onNext(3)
            Log.e(TAG, "subscribe 3")
            e.onNext(4)
            Log.e(TAG, "subscribe 4")
        }).subscribe(object : Observer<Int> {
            //创建一个观察者，subscribe将Observable与Observer关联起来
            override fun onNext(t: Int) {
                Log.e(TAG, "onNext t : $t")
            }

            override fun onSubscribe(d: Disposable) {
                Log.e(TAG, "onSubscribe d : ${d.isDisposed}")

            }

            override fun onError(e: Throwable) {
                Log.e(TAG, "onError  e : ${e.message}")
            }

            override fun onComplete() {
                Log.e(TAG, "onComplete ")
            }
        })
    }

    /**
     * map关键词
     * Consumer是简易版的Observer
     * https://blog.csdn.net/tyrantu1989/article/details/69062990
     */

    private fun  exampleMap(){
        var str=""
        Observable.create(ObservableOnSubscribe<String> { e->
            e.onNext("Hello World !!！")
            e.onNext("Hello People !!!")
            e.onComplete()
        }).map { t ->
            Log.e(TAG, " apply $t")
            str +=t
            str
        }.subscribe(object :Consumer<String>{
            override fun accept(t: String?) {
                Log.e(TAG,"accept $t")
            }
        })
    }

    /**
     * flatMap关键词
     * 把一个发射器 Observable 通过某种方法转换为多个 Observables，
     * 然后再把这些分散的 Observables装进一个单一的发射器 Observable。
     * 但有个需要注意的是，flatMap 并不能保证事件的顺序，如果需要保证，
     * 需要使用 ConcatMap。
     */
    private fun  exampleFlatMap(){
        var list= arrayListOf(" World !!!"," People !!!")
        var nextList= ArrayList<String>(3)
        Observable.create(ObservableOnSubscribe<String> {
            e ->  e.onNext(list[0])

        }).flatMap(object :Function<String, ObservableSource<String>>{
            override fun apply(t: String): ObservableSource<String> {
                for (i in list.indices){
                    nextList.add("$t , nothing")
                }
                 val delayTime = 2000.toLong()
                //延迟开始发送
                return Observable.fromIterable(nextList).delay(delayTime,TimeUnit.MILLISECONDS)
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object: Consumer<String>{
                    override fun accept(t: String?) {
                        Log.e(TAG,"accept $t")
                        sample_text.text=t
                    }

                })
    }

    private fun  exampleConcatMap(){
        var list= arrayListOf("11111"," 22222","333333")
        var j=0
        Observable.create(ObservableOnSubscribe <String>{
            e ->  e.onNext(list[0])
                  e.onNext(list[1])
                 e.onNext(list[2])
        }).flatMap(object :Function<String,ObservableSource<String>>{
            override fun apply(t: String): ObservableSource<String> {
                j++
                var nextList= ArrayList<String>(3)
                for ( i in list){
                    nextList.add("i : $j , $t , nothing")
                }
                //延迟
                val delayTime = (1 + Math.random() * 10).toLong()
                return Observable.fromIterable(nextList).delay(delayTime,TimeUnit.MILLISECONDS)
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object :Consumer<String>{
                    override fun accept(t: String) {
                        Log.e(TAG,"accept $t")
                        sample_text.text=t
                    }
                })
    }

    /**
     * 去重复
     */

    private fun  exampleDistinct(){
        Observable.just(1,1,1,2,2,3,3)
                .distinct()
                .subscribe { t -> Log.e(TAG,"t : $t") }

    }

    private fun exampleFilter(){

    }


    /**
     * 背压问题案例：
     */

    private fun  exampleBackpressure(){
        Observable.create(ObservableOnSubscribe<Int> {
            e->  var  i=0
             while (true) {
                 i++
                 e.onNext(i)
             }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .subscribe { t ->
                    Thread.sleep(5000)
                    Log.e(TAG,"$t")
                }
    }

    /**
     * Flowable背压
     * BackpressureStrategy 背压策略
     * ERROR，缓存池超过128就会抛出异常
     *  io.reactivex.exceptions.MissingBackpressureException: create: could not emit value due to lack of requests
     */

    private fun  exampleFlowableError(){
        Flowable.create(FlowableOnSubscribe<Int> { e ->
            for (i in 1..129){
                e.onNext(i)
            }
            e.onComplete()
        },BackpressureStrategy.ERROR) //多了一个背压策略参数
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .subscribe(object :Subscriber<Int>{
                    override fun onComplete() {
                        Log.e(TAG,"onComplete")
                    }

                    override fun onSubscribe(s: Subscription) {
                        s.request(Long.MAX_VALUE)

                    }

                    override fun onNext(t: Int) {
                        Thread.sleep(1000)
                        Log.e(TAG,"onNext t: $t")
                    }

                    override fun onError(t: Throwable) {
                        t.printStackTrace()
                    }

                })
    }

    /**
     * backpressure策略为buffer
     * 不会丢弃，没有限流。直到OOMdalvikvm-heap: Out of memory on a 28-byte allocation.
     */

    private fun exampleFlowableBuffer(){
        Flowable.create(FlowableOnSubscribe<Int> {
            e ->var  i=0
                while (true){
                    i++
                    e.onNext(i)
              }
        },BackpressureStrategy.BUFFER)
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .subscribe{
                    t: Int ->  Thread.sleep(1000)
                    Log.e(TAG,"accept t : $t")
                }

        //另一种写法。
        Flowable.range(0,500)
                .onBackpressureBuffer()
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .subscribe { t: Int? -> Log.e(TAG," t : $t") }
    }


}


