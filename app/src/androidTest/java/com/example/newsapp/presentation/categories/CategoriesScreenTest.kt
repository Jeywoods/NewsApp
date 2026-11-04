package com.example.newsapp.presentation.categories

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import com.example.newsapp.domain.model.Category
import com.example.newsapp.presentation.theme.NewsAppTheme
import io.mockk.every
import io.mockk.mockk
import io.mockk.just
import io.mockk.Runs
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class CategoriesScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private fun createViewModel(
        state: CategoriesUiState
    ): CategoriesViewModel {
        val viewModel = mockk<CategoriesViewModel>()

        every { viewModel.uiState } returns MutableStateFlow(state)
        every { viewModel.loadCategories() } just Runs

        return viewModel
    }

    @Test
    fun displaysScreenTitleAndDescription() {
        val viewModel = createViewModel(CategoriesUiState())

        composeRule.setContent {
            NewsAppTheme {
                CategoriesScreen(viewModel) {}
            }
        }

        composeRule.onNodeWithText("Новости").assertIsDisplayed()
        composeRule
            .onNodeWithText("Выберите интересующую вас категорию")
            .assertIsDisplayed()
    }

    @Test
    fun displaysLoadingIndicator() {
        val viewModel = createViewModel(
            CategoriesUiState(isLoading = true)
        )

        composeRule.setContent {
            NewsAppTheme {
                CategoriesScreen(viewModel) {}
            }
        }

        composeRule
            .onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate))
            .assertIsDisplayed()
    }

    @Test
    fun displaysCategories() {
        val categories = listOf(
            Category(0, "Много новостей"),
            Category(1, "Немного новостей"),
            Category(2, "Нет новостей")
        )

        val viewModel = createViewModel(
            CategoriesUiState(categories = categories)
        )

        composeRule.setContent {
            NewsAppTheme {
                CategoriesScreen(viewModel) {}
            }
        }

        categories.forEach { category ->
            composeRule
                .onNodeWithText(category.name)
                .assertIsDisplayed()
        }
    }

    @Test
    fun displaysEmptyMessageWhenCategoriesAreEmpty() {
        val viewModel = createViewModel(
            CategoriesUiState(categories = emptyList())
        )

        composeRule.setContent {
            NewsAppTheme {
                CategoriesScreen(viewModel) {}
            }
        }

        composeRule
            .onNodeWithText("Категории не найдены")
            .assertIsDisplayed()
    }

    @Test
    fun displaysErrorAndRetryButton() {
        val viewModel = createViewModel(
            CategoriesUiState(error = "Ошибка загрузки")
        )

        composeRule.setContent {
            NewsAppTheme {
                CategoriesScreen(viewModel) {}
            }
        }

        composeRule.onNodeWithText("Ошибка загрузки").assertIsDisplayed()
        composeRule.onNodeWithText("Повторить").assertIsDisplayed()
    }

    @Test
    fun retryButtonCallsLoadCategories() {
        val viewModel = createViewModel(
            CategoriesUiState(error = "Ошибка загрузки")
        )

        composeRule.setContent {
            NewsAppTheme {
                CategoriesScreen(viewModel) {}
            }
        }

        composeRule.onNodeWithText("Повторить").performClick()

        verify(exactly = 1) {
            viewModel.loadCategories()
        }
    }

    @Test
    fun clickingCategoryCallsCallback() {
        val category = Category(0, "Много новостей")
        val viewModel = createViewModel(
            CategoriesUiState(categories = listOf(category))
        )

        var clickedCategory: Category? = null

        composeRule.setContent {
            NewsAppTheme {
                CategoriesScreen(viewModel) {
                    clickedCategory = it
                }
            }
        }

        composeRule
            .onNodeWithText(category.name)
            .performClick()

        assertEquals(category, clickedCategory)
    }
}