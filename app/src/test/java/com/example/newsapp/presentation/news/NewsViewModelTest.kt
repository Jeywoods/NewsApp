
package com.example.newsapp.presentation.news

import com.example.newsapp.domain.model.News
import com.example.newsapp.domain.usecase.GetNewsUseCase
import com.example.newsapp.presentation.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class NewsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val useCase = mockk<GetNewsUseCase>()

    private fun createNews(id: Int) = News(
        id = id,
        title = "Новость $id",
        date = "2016-08-03T11:15:21.104Z",
        shortDescription = "Описание новости $id"
    )

    @Test
    fun `loads first page successfully`() = runTest {
        val news = (18 downTo 9).map { createNews(it) }

        coEvery {
            useCase(categoryId = 0, page = 0)
        } returns news

        val viewModel = NewsViewModel(
            categoryId = 0,
            getNewsUseCase = useCase
        )

        advanceUntilIdle()

        val state = viewModel.uiState.value

        assertEquals(news, state.news)
        assertEquals(1, state.page)
        assertTrue(state.hasMorePages)
        assertFalse(state.isLoading)
        assertFalse(state.isLoadingMore)
        assertNull(state.error)

        coVerify(exactly = 1) {
            useCase(categoryId = 0, page = 0)
        }
    }

    @Test
    fun `loads next page and appends news`() = runTest {
        val firstPage = (18 downTo 9).map { createNews(it) }
        val secondPage = listOf(createNews(8), createNews(7))

        coEvery {
            useCase(categoryId = 0, page = 0)
        } returns firstPage

        coEvery {
            useCase(categoryId = 0, page = 1)
        } returns secondPage

        val viewModel = NewsViewModel(0, useCase)
        advanceUntilIdle()

        viewModel.loadNews()
        advanceUntilIdle()

        val state = viewModel.uiState.value

        assertEquals(firstPage + secondPage, state.news)
        assertEquals(2, state.page)
        assertFalse(state.hasMorePages)
        assertFalse(state.isLoading)
        assertFalse(state.isLoadingMore)
        assertNull(state.error)

        coVerify(exactly = 1) {
            useCase(categoryId = 0, page = 0)
        }

        coVerify(exactly = 1) {
            useCase(categoryId = 0, page = 1)
        }
    }

    @Test
    fun `does not load another page when previous page contains less than page size`() = runTest {
        val shortPage = listOf(createNews(18), createNews(17))

        coEvery {
            useCase(categoryId = 0, page = 0)
        } returns shortPage

        val viewModel = NewsViewModel(0, useCase)
        advanceUntilIdle()

        viewModel.loadNews()
        advanceUntilIdle()

        assertEquals(shortPage, viewModel.uiState.value.news)
        assertFalse(viewModel.uiState.value.hasMorePages)

        coVerify(exactly = 1) {
            useCase(categoryId = 0, page = 0)
        }
    }

    @Test
    fun `shows error when first page loading fails`() = runTest {
        coEvery {
            useCase(categoryId = 0, page = 0)
        } throws Exception("Нет подключения к интернету")

        val viewModel = NewsViewModel(0, useCase)
        advanceUntilIdle()

        val state = viewModel.uiState.value

        assertTrue(state.news.isEmpty())
        assertEquals(0, state.page)
        assertFalse(state.isLoading)
        assertEquals("Нет подключения к интернету", state.error)
    }

    @Test
    fun `retries first page after error`() = runTest {
        val news = listOf(createNews(18))

        coEvery {
            useCase(categoryId = 0, page = 0)
        } throws Exception("Нет подключения к интернету") andThen news

        val viewModel = NewsViewModel(0, useCase)
        advanceUntilIdle()

        assertEquals(
            "Нет подключения к интернету",
            viewModel.uiState.value.error
        )

        viewModel.loadNews()
        advanceUntilIdle()

        val state = viewModel.uiState.value

        assertEquals(news, state.news)
        assertEquals(1, state.page)
        assertFalse(state.isLoading)
        assertNull(state.error)

        coVerify(exactly = 2) {
            useCase(categoryId = 0, page = 0)
        }
    }

    @Test
    fun `keeps already loaded news when next page fails`() = runTest {
        val firstPage = (18 downTo 9).map { createNews(it) }

        coEvery {
            useCase(categoryId = 0, page = 0)
        } returns firstPage

        coEvery {
            useCase(categoryId = 0, page = 1)
        } throws Exception("Ошибка загрузки следующей страницы")

        val viewModel = NewsViewModel(0, useCase)
        advanceUntilIdle()

        viewModel.loadNews()
        advanceUntilIdle()

        val state = viewModel.uiState.value

        assertEquals(firstPage, state.news)
        assertEquals(1, state.page)
        assertFalse(state.isLoadingMore)
        assertEquals("Ошибка загрузки следующей страницы", state.error)
    }
}
