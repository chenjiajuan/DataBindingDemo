package com.chenjiajuan.databingdemo.model

/**
 * Created by chenjiajuan on 2018/6/23.
 */
 data class BookItem(var  count:Int,var start:Int,var  total:Int,var books:List<BooksBean>)
 data class BooksBean(var rating:RatingBean,var  subtitle:String,var pubdate:String,
          var origin_title:String,var image:String,var binding:String,var catalog:String,
           var  pages:String,var  images:ImagesBean,var alt:String,var id:String,
           var  publisher:String,var  isbn10:String,var isbn13:String,var  title:String,
           var  url:String,var  alt_title:String,var author_intro:String,var summary:String,
           var series:SeriesBean,var price:String,var author:List<String>,var tags:List<TagsBean>,
           var translator:List<String>)
data  class  RatingBean(var max:Int,var numRaters:Int,var average:String,var min:Int)
data class  ImagesBean(var small:String,var large:String,var medium:String)
data class  SeriesBean(var id:String,var title:String)
data class TagsBean(var count:Int,var  name:String,var  title:String)