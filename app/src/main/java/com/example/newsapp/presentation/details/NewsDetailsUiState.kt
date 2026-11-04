package com.example.newsapp.presentation.details

data class NewsDetailsUiState(
    val title: String = "",
    val date: String = "",
    val shortDescription: String = "",
    val fullDescription: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)