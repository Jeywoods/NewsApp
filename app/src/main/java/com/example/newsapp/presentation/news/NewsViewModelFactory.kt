package com.example.newsapp.presentation.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.newsapp.domain.usecase.GetNewsUseCase

class NewsViewModelFactory(
    private val categoryId: Int,
    private val getNewsUseCase: GetNewsUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewsViewModel::class.java)) {
            return NewsViewModel(
                categoryId = categoryId,
                getNewsUseCase = getNewsUseCase
            ) as T
        }

        throw IllegalArgumentException(
            "Неизвестный класс ViewModel: ${modelClass.name}"
        )
    }
}