package com.test.my.app.blogs.domain

import androidx.lifecycle.LiveData
import com.test.my.app.model.blogs.BlogRecommendationListModel
import com.test.my.app.model.blogs.BlogsCategoryModel
import com.test.my.app.model.blogs.BlogsListAllModel
import com.test.my.app.model.blogs.BlogsListByCategoryModel
import com.test.my.app.model.blogs.BlogsListBySearchModel
import com.test.my.app.repository.BlogsRepository
import com.test.my.app.repository.utils.Resource
import javax.inject.Inject

class BlogsManagementUseCase @Inject constructor(private val blogRepository: BlogsRepository) {

    /*    suspend fun invokeDownloadBlog(isForceRefresh: Boolean, data: String): LiveData<Resource<List<BlogModel.Blog>>> {

            return Transformations.map(blogRepository.downloadBlogs(isForceRefresh, data)) {
                it
            }
        }*/

    suspend fun invokeDownloadBlog(
        isForceRefresh: Boolean,
        data: BlogsListAllModel
    ): LiveData<Resource<BlogsListAllModel.BlogsListAllResponse>> {

        return blogRepository.downloadBlogs(isForceRefresh, data)
    }

    suspend fun invokeSearchBlog(
        isForceRefresh: Boolean,
        data: BlogsListBySearchModel
    ): LiveData<Resource<BlogsListBySearchModel.BlogsListBySearchResponse>> {

        return blogRepository.searchBlogs(isForceRefresh, data)
    }

    suspend fun invokeBlogsCategory(
        isForceRefresh: Boolean,
        data: BlogsCategoryModel
    ): LiveData<Resource<BlogsCategoryModel.BlogsCategoryResponse>> {

        return blogRepository.blogsCategory(isForceRefresh, data)
    }

    suspend fun invokeBlogsListByCategory(
        isForceRefresh: Boolean,
        data: BlogsListByCategoryModel
    ): LiveData<Resource<BlogsListByCategoryModel.BlogsCategoryResponse>> {

        return blogRepository.blogsListByCategory(isForceRefresh, data)
    }

    suspend fun invokeBlogsListSuggestion(
        isForceRefresh: Boolean,
        data: BlogRecommendationListModel
    ): LiveData<Resource<BlogRecommendationListModel.BlogsResponse>> {

        return blogRepository.blogsListSuggestion(isForceRefresh, data)
    }

    suspend fun invokeBlogsListRelatedTo(
        isForceRefresh: Boolean,
        data: BlogRecommendationListModel
    ): LiveData<Resource<BlogRecommendationListModel.BlogsResponse>> {

        return blogRepository.blogsRelatedTo(isForceRefresh, data)
    }

    /*    suspend fun invokeBlogsListByCategory(isForceRefresh: Boolean, data: BlogsListByCategoryModel): LiveData<Resource<BlogsListByCategoryModel.BlogsCategoryResponse>> {

            return Transformations.map(blogRepository.blogsListByCategory(isForceRefresh, data)) {
                it
            }
        }*/

}