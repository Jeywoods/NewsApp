package com.example.newsapp.presentation.categories

import com.example.newsapp.domain.model.Category

data class CategoriesUiState(
    val isLoading: Boolean = false,
    val categories: List<Category> = emptyList(),
    val error: String? = null
)