package com.example.newsapp.data.repository

import com.example.newsapp.data.local.dao.CategoryDao
import com.example.newsapp.data.local.dao.NewsDao
import com.example.newsapp.data.local.dao.NewsDetailsDao
import com.example.newsapp.data.mapper.toDetailsDomain
import com.example.newsapp.data.mapper.toDomain
import com.example.newsapp.data.mapper.toEntity
import com.example.newsapp.data.remote.ApiException
import com.example.newsapp.data.remote.ApiResponseHandler
import com.example.newsapp.data.remote.NewsApiService
import com.example.newsapp.domain.model.Category
import com.example.newsapp.domain.model.News
import com.example.newsapp.domain.model.NewsDetails
import com.example.newsapp.domain.repository.NewsRepository
import kotlinx.coroutines.CancellationException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import retrofit2.HttpException

class NewsRepositoryImpl(
    private val api: NewsApiService,
    private val categoryDao: CategoryDao,
    private val newsDao: NewsDao,
    private val newsDetailsDao: NewsDetailsDao
) : NewsRepository {

    override suspend fun getCategories(): List<Category> {
        val categories = try {
            safeApiCall {
                val response = api.getCategories()

                ApiResponseHandler
                    .handleCategories(response)
                    .map { it.toDomain() }
            }
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            val cachedCategories = categoryDao.getAllCategories()

            if (cachedCategories.isNotEmpty()) {
                return cachedCategories.map { it.toDomain() }
            }

            throw exception
        }

        categoryDao.insertCategories(
            categories.map { it.toEntity() }
        )

        return categories
    }

    override suspend fun getNews(
        categoryId: Int,
        page: Int
    ): List<News> {
        val news = try {
            safeApiCall {
                val response = api.getNews(
                    categoryId = categoryId,
                    page = page
                )

                ApiResponseHandler
                    .handleNews(response)
                    .map { it.toDomain() }
            }
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            val cachedNews = newsDao.getNewsByPage(
                categoryId = categoryId,
                page = page
            )

            if (cachedNews.isNotEmpty()) {
                return cachedNews.map { it.toDomain() }
            }

            throw exception
        }

        newsDao.insertNews(
            news.map { it.toEntity(categoryId, page) }
        )

        return news
    }

    override suspend fun getNewsDetails(newsId: Int): NewsDetails {
        val details = try {
            safeApiCall {
                val response = api.getNewsDetails(newsId)

                ApiResponseHandler
                    .handleNewsDetails(response)
                    .toDetailsDomain()
            }
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            val cachedDetails = newsDetailsDao.getNewsDetails(newsId)

            if (cachedDetails != null) {
                return cachedDetails.toDomain()
            }

            throw exception
        }

        newsDetailsDao.insertNewsDetails(
            details.toEntity()
        )

        return details
    }

    private suspend fun <T> safeApiCall(
        block: suspend () -> T
    ): T {
        return try {
            block()
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: ApiException) {
            throw exception
        } catch (exception: UnknownHostException) {
            throw Exception("Нет подключения к интернету", exception)
        } catch (exception: SocketTimeoutException) {
            throw Exception(
                "Сервер не отвечает. Попробуйте позже",
                exception
            )
        } catch (exception: ConnectException) {
            throw Exception(
                "Не удалось подключиться к серверу",
                exception
            )
        } catch (exception: HttpException) {
            throw Exception(
                "Ошибка сервера. Попробуйте позже",
                exception
            )
        } catch (exception: Exception) {
            throw Exception(
                "Произошла непредвиденная ошибка",
                exception
            )
        }
    }
}