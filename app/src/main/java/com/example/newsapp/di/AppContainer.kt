package com.example.newsapp.di

import android.content.Context
import androidx.room.Room
import com.example.newsapp.data.local.NewsDatabase
import com.example.newsapp.data.remote.RetrofitClient
import com.example.newsapp.data.repository.NewsRepositoryImpl
import com.example.newsapp.domain.usecase.GetCategoriesUseCase
import com.example.newsapp.domain.usecase.GetNewsDetailsUseCase
import com.example.newsapp.domain.usecase.GetNewsUseCase

class AppContainer(context: Context) {

    private val newsDatabase = Room.databaseBuilder(
        context.applicationContext,
        NewsDatabase::class.java,
        "news_database"
    ).build()

    val categoryDao = newsDatabase.categoryDao()
    val newsDao = newsDatabase.newsDao()
    val newsDetailsDao = newsDatabase.newsDetailsDao()

    private val newsApiService = RetrofitClient.newsApiService

    private val newsRepository = NewsRepositoryImpl(
        api = newsApiService,
        categoryDao = categoryDao,
        newsDao = newsDao,
        newsDetailsDao = newsDetailsDao
    )

    val getCategoriesUseCase = GetCategoriesUseCase(
        repository = newsRepository
    )

    val getNewsUseCase = GetNewsUseCase(
        repository = newsRepository
    )

    val getNewsDetailsUseCase = GetNewsDetailsUseCase(
        repository = newsRepository
    )
}