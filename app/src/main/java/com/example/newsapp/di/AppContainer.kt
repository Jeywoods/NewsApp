package com.example.newsapp.di

import com.example.newsapp.data.remote.RetrofitClient
import com.example.newsapp.data.repository.NewsRepositoryImpl
import com.example.newsapp.domain.usecase.GetCategoriesUseCase
import com.example.newsapp.domain.usecase.GetNewsDetailsUseCase
import com.example.newsapp.domain.usecase.GetNewsUseCase

class AppContainer {

    private val newsApiService = RetrofitClient.newsApiService

    private val newsRepository = NewsRepositoryImpl(
        api = newsApiService
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