package com.chenjiajuan.databingdemo.adapter

import android.content.Context
import android.databinding.BindingAdapter
import android.databinding.DataBindingComponent
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.chenjiajuan.databingdemo.R
import com.chenjiajuan.databingdemo.databinding.ItemBookBottomBinding
import com.chenjiajuan.databingdemo.databinding.ItemBookDescribeBinding
import com.chenjiajuan.databingdemo.model.BooksBean
import java.util.*


/**
 * Created by chenjiajuan on 2018/6/23.
 */
class BookAdapter:RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private var context:Context?=null
    private var  itemBookData:ItemBookDescribeBinding?=null
    private var itemBottomData:ItemBookBottomBinding?=null
    private var onItemClick: onItemClickListener?=null
     var ITEM_TYPE_BOOK:Int=2
     var ITEM_TYPE_BOTTOM:Int=1
    private var books:ArrayList<BooksBean> ?= ArrayList()
    constructor(context: Context){
        this.context=context
    }

    fun  addListData(bookList: ArrayList<BooksBean>){
        books?.addAll(bookList)
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is BookViewHolder){
            var  book=books?.get(position)
           holder.getBinding().bookItem=book
           holder.getBinding().root.setOnClickListener{
               onItemClick?.onItemClick(position)
           }
           holder.getBinding().executePendingBindings()
        }else if (holder is BottomViewHolder){

        }

    }

    override fun getItemViewType(position: Int): Int {
        return if (position<books?.size!!){
            ITEM_TYPE_BOOK
        }else{
            ITEM_TYPE_BOTTOM
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType==ITEM_TYPE_BOOK){
            itemBookData= DataBindingUtil.inflate(LayoutInflater.from(context),
                    R.layout.item_book_describe,parent,false, MyComponent())
            BookViewHolder(itemBookData!!)
        }else {
            itemBottomData=DataBindingUtil.inflate(LayoutInflater.from(context),
                    R.layout.item_book_bottom,parent,false)
            BottomViewHolder(itemBottomData!!)
        }

    }

    override fun getItemCount(): Int {
        return books?.size as Int +1
    }

    /**
     * 方式二，采用注入的方式，需要声明DataBindingComponent，在inflate的时候传入
     *  */

    open class ImageUrl{
        @BindingAdapter("imageUrl")
        fun  loadImage(view: ImageView, url:String){
            Glide.with(view.context).load(url).into(view)
        }
    }

    class MyComponent:DataBindingComponent{

         override fun getImageUrl(): ImageUrl {
            return ImageUrl()
        }
    }



    interface onItemClickListener{
        fun  onItemClick(position: Int)
    }

    fun setOnItemClickListener(onItemClickListener: onItemClickListener){
          this.onItemClick=onItemClickListener
    }



    class BookViewHolder: RecyclerView.ViewHolder {

        private var binding:ItemBookDescribeBinding?=null

        constructor(binding: ItemBookDescribeBinding):super(binding.root){
            this.binding=binding
        }

        fun getBinding():ItemBookDescribeBinding{
            return this.binding!!
        }
    }

    class BottomViewHolder:RecyclerView.ViewHolder{
        private var binding:ItemBookBottomBinding?=null
        constructor(binding: ItemBookBottomBinding):super(binding.root){
            this.binding=binding
        }
        fun  getBinding():ItemBookBottomBinding{
            return this.binding!!
        }
    }


}

