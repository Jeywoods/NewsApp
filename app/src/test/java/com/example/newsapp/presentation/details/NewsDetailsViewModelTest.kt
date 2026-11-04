
package com.example.newsapp.presentation.details

import com.example.newsapp.domain.model.News
import com.example.newsapp.domain.model.NewsDetails
import com.example.newsapp.domain.usecase.GetNewsDetailsUseCase
import com.example.newsapp.presentation.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

class NewsDetailsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val useCase = mockk<GetNewsDetailsUseCase>()

    private val initialNews = News(
        id = 18,
        title = "Заголовок новости",
        date = "2016-08-03T11:15:21.104Z",
        shortDescription = "Краткое описание"
    )

    private val details = NewsDetails(
        id = 18,
        title = "Заголовок новости",
        date = "2016-08-03T11:15:21.104Z",
        shortDescription = "Краткое описание",
        fullDescription = "<p>Полный текст новости</p>"
    )

    @Test
    fun `shows initial news data and then loads full details`() = runTest {
        coEvery {
            useCase(newsId = 18)
        } returns details

        val viewModel = NewsDetailsViewModel(initialNews, useCase)

        val initialState = viewModel.uiState.value

        assertEquals(initialNews.title, initialState.title)
        assertEquals(initialNews.date, initialState.date)
        assertEquals(initialNews.shortDescription, initialState.shortDescription)
        assertNull(initialState.fullDescription)

        advanceUntilIdle()

        val state = viewModel.uiState.value

        assertEquals(details.title, state.title)
        assertEquals(details.date, state.date)
        assertEquals(details.shortDescription, state.shortDescription)
        assertEquals(details.fullDescription, state.fullDescription)
        assertFalse(state.isLoading)
        assertNull(state.error)

        coVerify(exactly = 1) {
            useCase(newsId = 18)
        }
    }

    @Test
    fun `shows error when details loading fails`() = runTest {
        coEvery {
            useCase(newsId = 18)
        } throws Exception("Нет подключения к интернету")

        val viewModel = NewsDetailsViewModel(initialNews, useCase)
        advanceUntilIdle()

        val state = viewModel.uiState.value

        assertEquals(initialNews.title, state.title)
        assertEquals(initialNews.date, state.date)
        assertEquals(initialNews.shortDescription, state.shortDescription)

        assertNull(state.fullDescription)
        assertFalse(state.isLoading)
        assertEquals("Нет подключения к интернету", state.error)
    }

    @Test
    fun `retries loading details after error`() = runTest {
        coEvery {
            useCase(newsId = 18)
        } throws Exception("Нет подключения к интернету") andThen details

        val viewModel = NewsDetailsViewModel(initialNews, useCase)
        advanceUntilIdle()

        assertEquals(
            "Нет подключения к интернету",
            viewModel.uiState.value.error
        )

        viewModel.loadDetails()
        advanceUntilIdle()

        val state = viewModel.uiState.value

        assertEquals(details.fullDescription, state.fullDescription)
        assertFalse(state.isLoading)
        assertNull(state.error)

        coVerify(exactly = 2) {
            useCase(newsId = 18)
        }
    }
}
