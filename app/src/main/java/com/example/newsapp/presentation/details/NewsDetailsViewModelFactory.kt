package com.example.newsapp.presentation.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.newsapp.domain.model.News
import com.example.newsapp.domain.usecase.GetNewsDetailsUseCase

class NewsDetailsViewModelFactory(
    private val initialNews: News,
    private val getNewsDetailsUseCase: GetNewsDetailsUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewsDetailsViewModel::class.java)) {
            return NewsDetailsViewModel(
                initialNews = initialNews,
                getNewsDetailsUseCase = getNewsDetailsUseCase
            ) as T
        }

        throw IllegalArgumentException(
            "Неизвестный класс ViewModel: ${modelClass.name}"
        )
    }
}