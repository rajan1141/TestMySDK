package com.test.my.app.wyh.common

import android.content.Context
import com.test.my.app.common.constants.Constants
import com.test.my.app.common.utils.Utilities.printLogError

object WyhHelper {

    fun getQuickReadsTitleByCode(code: String,context: Context): String {
        val title: String = when(code) {
            "bookMarkBlogs"-> "Book Marked"
            "mostRead"-> "Most Read"
            "trendingBlogs"-> "Trending"
            "tribeBlogs"-> "Tribe Blogs"
            "tribeVideos"-> "Tribe Videos"
            "blogs"-> "Blogs"
            else -> code
        }
        return title
    }

    fun getHealthContentCategoryList(context: Context): MutableList<Category> {
        //val localResource = LocaleHelper.getLocalizedResources(context,Locale(LocaleHelper.getLanguage(context)))!!
        val list: MutableList<Category> = ArrayList()
        list.add(Category(1,Constants.BLOGS,"Blogs"))
        list.add(Category(2,Constants.VIDEOS,"Videos"))
        list.add(Category(3,Constants.AUDIOS,"Audios"))
        list.add(Category(4,Constants.QUICK_READS,"Quick Reads"))
        return list
    }

    fun getWyhUrlToLoad(url: String): String {
        val wyhUrl = Constants.strWyhBaseURL + url
        printLogError("WyhUrl--->$wyhUrl")
        return wyhUrl
    }

}