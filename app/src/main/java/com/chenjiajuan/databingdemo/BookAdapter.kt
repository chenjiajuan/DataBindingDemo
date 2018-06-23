package com.chenjiajuan.databingdemo

import android.content.Context
import android.databinding.BindingAdapter
import android.databinding.DataBindingComponent
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.chenjiajuan.databingdemo.databinding.ItemBookDescribeBinding
import com.chenjiajuan.databingdemo.model.BookItem


/**
 * Created by chenjiajuan on 2018/6/23.
 */
class BookAdapter:RecyclerView.Adapter<BookAdapter.BookViewHolder> {
    private var context:Context?=null
    private var  itemBookData:ItemBookDescribeBinding?=null
    private var bookItem:BookItem?=null
    constructor(context: Context,bookItem: BookItem){
        this.context=context
        this.bookItem=bookItem

    }
    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        var  book=bookItem!!.books[position]
        /**
         * 方式一
         * Glide.with(context).load(book.images.small).into(holder.getBinding().ivBookPicture)
          holder.getBinding().tvBookName.text= book.title
        holder.getBinding().tvBookAuthor.text=book.author.toString()
        holder.getBinding().tvBookDescribe.text= book.summary
        holder.getBinding().tvAverage.text=book.rating.average
        holder.getBinding().rtNumRaters.rating = book.rating?.average!!.toFloat()
         */
        /**
         * 方式二
         */
        holder.getBinding().bookItem=book
        holder.getBinding().executePendingBindings()  //刷新数据

    }


    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): BookViewHolder? {
        itemBookData= DataBindingUtil.inflate(LayoutInflater.from(context),R.layout.item_book_describe,parent,false,MyComponent())
        return BookViewHolder(itemBookData!!)
    }

    override fun getItemCount(): Int {
        return bookItem?.books?.size as Int
    }

    /**
     * 方式二，采用注入的方式，需要声明DataBindingComponent，在inflate的时候传入
     */
    open class ImageUrl{
        @BindingAdapter("imageUrl")
        fun   loadImage(view:ImageView,url:String){
            Glide.with(view.context).load(url).into(view)
        }
    }
    class MyComponent:DataBindingComponent{
        override fun getImageUrl():ImageUrl{
            return ImageUrl()

        }
    }

    class BookViewHolder: RecyclerView.ViewHolder {

        private var mBinding:ItemBookDescribeBinding?=null

        constructor(binding: ItemBookDescribeBinding):super(binding.root){
            mBinding=binding
        }

        fun getBinding():ItemBookDescribeBinding{
            return this!!.mBinding!!
        }
    }


}

