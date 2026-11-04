package com.example.newsapp.presentation.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.newsapp.domain.usecase.GetCategoriesUseCase

class CategoriesViewModelFactory(
    private val getCategoriesUseCase: GetCategoriesUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoriesViewModel::class.java)) {
            return CategoriesViewModel(getCategoriesUseCase) as T
        }

        throw IllegalArgumentException(
            "Неизвестный класс ViewModel: ${modelClass.name}"
        )
    }
}