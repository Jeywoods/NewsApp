package com.example.newsapp.presentation.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.domain.usecase.GetNewsUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NewsViewModel(
    private val categoryId: Int,
    private val getNewsUseCase: GetNewsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewsUiState())
    val uiState: StateFlow<NewsUiState> = _uiState.asStateFlow()

    init {
        loadNews()
    }

    fun loadNews() {
        if (_uiState.value.isLoading ||
            _uiState.value.isLoadingMore ||
            !_uiState.value.hasMorePages
        ) {
            return
        }

        viewModelScope.launch {
            val isFirstPage = _uiState.value.news.isEmpty()

            _uiState.value = _uiState.value.copy(
                isLoading = isFirstPage,
                isLoadingMore = !isFirstPage,
                error = null
            )

            try {
                val currentPage = _uiState.value.page

                val newNews = getNewsUseCase(
                    categoryId = categoryId,
                    page = currentPage
                )

                _uiState.value = _uiState.value.copy(
                    news = _uiState.value.news + newNews,
                    page = currentPage + 1,
                    hasMorePages = newNews.size == PAGE_SIZE,
                    isLoading = false,
                    isLoadingMore = false
                )
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isLoadingMore = false,
                    error = exception.message ?: "Не удалось загрузить новости"
                )
            }
        }
    }

    companion object {
        private const val PAGE_SIZE = 10
    }
}