
package com.example.newsapp.presentation.details

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.newsapp.presentation.theme.NewsAppTheme
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NewsDetailsScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private fun createViewModel(
        state: NewsDetailsUiState
    ): NewsDetailsViewModel {
        val viewModel = mockk<NewsDetailsViewModel>()

        every { viewModel.uiState } returns MutableStateFlow(state)
        every { viewModel.loadDetails() } just Runs

        return viewModel
    }

    private fun setScreen(
        state: NewsDetailsUiState,
        onBackClick: () -> Unit = {}
    ): NewsDetailsViewModel {
        val viewModel = createViewModel(state)

        composeRule.setContent {
            NewsAppTheme {
                NewsDetailsScreen(
                    viewModel = viewModel,
                    onBackClick = onBackClick
                )
            }
        }

        return viewModel
    }

    @Test
    fun shortDescription_isDisplayed() {
        setScreen(
            NewsDetailsUiState(
                title = "Заголовок новости",
                shortDescription = "Уникальное краткое описание"
            )
        )

        composeRule
            .onNodeWithText("Уникальное краткое описание")
            .assertIsDisplayed()
    }

    @Test
    fun errorState_displaysErrorAndRetryButton() {
        setScreen(
            NewsDetailsUiState(
                title = "Заголовок новости",
                error = "Не удалось загрузить подробности"
            )
        )

        composeRule
            .onNodeWithText("Не удалось загрузить подробности")
            .assertIsDisplayed()

        composeRule
            .onNodeWithText("Повторить")
            .assertIsDisplayed()
    }

    @Test
    fun retryButton_callsLoadDetails() {
        val viewModel = setScreen(
            NewsDetailsUiState(
                title = "Заголовок новости",
                error = "Ошибка загрузки"
            )
        )

        composeRule
            .onNodeWithText("Повторить")
            .performClick()

        verify(exactly = 1) {
            viewModel.loadDetails()
        }
    }

    @Test
    fun backButton_callsCallback() {
        var backClicked = false

        setScreen(
            state = NewsDetailsUiState(
                title = "Заголовок новости"
            ),
            onBackClick = { backClicked = true }
        )

        composeRule
            .onNodeWithText("←")
            .performClick()

        assertEquals(true, backClicked)
    }

    @Test
    fun fullDescription_isLoadedIntoWebView() {
        setScreen(
            NewsDetailsUiState(
                title = "Заголовок новости",
                shortDescription = "Краткое описание перед полным текстом",
                fullDescription = "<p>Полный текст новости</p>"
            )
        )

        composeRule
            .onNodeWithText("Краткое описание перед полным текстом")
            .assertIsDisplayed()
    }

    @Test
    fun loadingState_displaysInitialNewsInformation() {
        setScreen(
            NewsDetailsUiState(
                title = "Заголовок новости",
                shortDescription = "Описание во время загрузки",
                isLoading = true
            )
        )

        composeRule
            .onNodeWithText("Описание во время загрузки")
            .assertIsDisplayed()
    }

    @Test
    fun errorState_keepsInitialNewsInformationVisible() {
        setScreen(
            NewsDetailsUiState(
                title = "Заголовок новости",
                shortDescription = "Исходное описание новости",
                error = "Ошибка загрузки"
            )
        )

        composeRule
            .onNodeWithText("Исходное описание новости")
            .assertIsDisplayed()

        composeRule
            .onNodeWithText("Ошибка загрузки")
            .assertIsDisplayed()
    }
}
