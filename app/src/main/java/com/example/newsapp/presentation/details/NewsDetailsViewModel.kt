package com.example.newsapp.presentation.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.domain.model.News
import com.example.newsapp.domain.usecase.GetNewsDetailsUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NewsDetailsViewModel(
    private val initialNews: News,
    private val getNewsDetailsUseCase: GetNewsDetailsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        NewsDetailsUiState(
            title = initialNews.title,
            date = initialNews.date,
            shortDescription = initialNews.shortDescription
        )
    )

    val uiState: StateFlow<NewsDetailsUiState> = _uiState.asStateFlow()

    init {
        loadDetails()
    }

    fun loadDetails() {
        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            try {
                val details = getNewsDetailsUseCase(initialNews.id)

                _uiState.value = _uiState.value.copy(
                    title = details.title,
                    date = details.date,
                    shortDescription = details.shortDescription,
                    fullDescription = details.fullDescription,
                    isLoading = false,
                    error = null
                )
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = exception.message ?: "Не удалось загрузить новость"
                )
            }
        }
    }
}