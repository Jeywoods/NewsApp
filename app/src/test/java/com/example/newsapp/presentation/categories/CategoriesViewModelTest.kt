
package com.example.newsapp.presentation.categories

import com.example.newsapp.domain.model.Category
import com.example.newsapp.domain.usecase.GetCategoriesUseCase
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

class CategoriesViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val useCase = mockk<GetCategoriesUseCase>()

    private val categories = listOf(
        Category(0, "Много новостей"),
        Category(1, "Немного новостей"),
        Category(2, "Нет новостей")
    )

    @Test
    fun `loads categories successfully`() = runTest {
        coEvery { useCase() } returns categories

        val viewModel = CategoriesViewModel(useCase)
        advanceUntilIdle()

        val state = viewModel.uiState.value

        assertEquals(categories, state.categories)
        assertFalse(state.isLoading)
        assertNull(state.error)

        coVerify(exactly = 1) { useCase() }
    }

    @Test
    fun `shows error when loading categories fails`() = runTest {
        coEvery {
            useCase()
        } throws Exception("Ошибка загрузки категорий")

        val viewModel = CategoriesViewModel(useCase)
        advanceUntilIdle()

        val state = viewModel.uiState.value

        assertTrue(state.categories.isEmpty())
        assertFalse(state.isLoading)
        assertEquals("Ошибка загрузки категорий", state.error)
    }

    @Test
    fun `retries loading categories after error`() = runTest {
        coEvery {
            useCase()
        } throws Exception("Нет подключения к интернету") andThen categories

        val viewModel = CategoriesViewModel(useCase)
        advanceUntilIdle()

        assertEquals(
            "Нет подключения к интернету",
            viewModel.uiState.value.error
        )

        viewModel.loadCategories()
        advanceUntilIdle()

        val state = viewModel.uiState.value

        assertEquals(categories, state.categories)
        assertFalse(state.isLoading)
        assertNull(state.error)

        coVerify(exactly = 2) { useCase() }
    }
}
