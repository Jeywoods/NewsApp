
package com.example.newsapp.presentation.news

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.newsapp.domain.model.News
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
class NewsScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private fun createViewModel(state: NewsUiState): NewsViewModel {
        val viewModel = mockk<NewsViewModel>()

        every { viewModel.uiState } returns MutableStateFlow(state)
        every { viewModel.loadNews() } just Runs

        return viewModel
    }

    private fun setScreen(
        state: NewsUiState,
        onNewsClick: (News) -> Unit = {},
        onBackClick: () -> Unit = {}
    ): NewsViewModel {
        val viewModel = createViewModel(state)

        composeRule.setContent {
            NewsAppTheme {
                NewsScreen(
                    categoryName = "Много новостей",
                    viewModel = viewModel,
                    onNewsClick = onNewsClick,
                    onBackClick = onBackClick
                )
            }
        }

        return viewModel
    }

    private fun createNews(
        id: Int = 1,
        title: String = "Заголовок новости",
        date: String = "2016-08-03T11:15:21.104Z",
        description: String = "Краткое описание новости"
    ) = News(
        id = id,
        title = title,
        date = date,
        shortDescription = description
    )

    @Test
    fun categoryTitle_isDisplayed() {
        setScreen(
            NewsUiState(hasMorePages = false)
        )

        composeRule
            .onNodeWithText("Много новостей")
            .assertIsDisplayed()
    }

    @Test
    fun backButton_callsCallback() {
        var backClicked = false

        setScreen(
            state = NewsUiState(hasMorePages = false),
            onBackClick = { backClicked = true }
        )

        composeRule
            .onNodeWithText("←")
            .performClick()

        assertEquals(true, backClicked)
    }

    @Test
    fun newsList_displaysNewsItems() {
        val news = createNews()

        setScreen(
            NewsUiState(
                news = listOf(news),
                hasMorePages = false
            )
        )

        composeRule
            .onNodeWithText(news.title)
            .assertIsDisplayed()

        composeRule
            .onNodeWithText(news.shortDescription)
            .assertIsDisplayed()
    }

    @Test
    fun emptyState_displaysMessage() {
        setScreen(
            NewsUiState(hasMorePages = false)
        )

        composeRule
            .onNodeWithText("В этой категории пока нет новостей")
            .assertIsDisplayed()
    }

    @Test
    fun errorState_displaysErrorAndRetryButton() {
        setScreen(
            NewsUiState(
                error = "Не удалось загрузить новости",
                hasMorePages = false
            )
        )

        composeRule
            .onNodeWithText("Не удалось загрузить новости")
            .assertIsDisplayed()

        composeRule
            .onNodeWithText("Повторить")
            .assertIsDisplayed()
    }

    @Test
    fun retryButton_callsLoadNews() {
        val viewModel = setScreen(
            NewsUiState(
                error = "Ошибка загрузки",
                hasMorePages = false
            )
        )

        composeRule
            .onNodeWithText("Повторить")
            .performClick()

        verify(exactly = 1) {
            viewModel.loadNews()
        }
    }

    @Test
    fun newsItemClick_callsCallbackWithSelectedNews() {
        val news = createNews()
        var clickedNews: News? = null

        setScreen(
            state = NewsUiState(
                news = listOf(news),
                hasMorePages = false
            ),
            onNewsClick = { clickedNews = it }
        )

        composeRule
            .onNodeWithText(news.title)
            .performClick()

        assertEquals(news, clickedNews)
    }

    @Test
    fun errorWithNews_displaysErrorAndRetryButton() {
        setScreen(
            NewsUiState(
                news = listOf(createNews()),
                error = "Не удалось загрузить следующую страницу",
                hasMorePages = false
            )
        )

        composeRule
            .onNodeWithText("Не удалось загрузить следующую страницу")
            .assertIsDisplayed()

        composeRule
            .onNodeWithText("Повторить")
            .assertIsDisplayed()
    }

    @Test
    fun pagination_loadsNextPageWhenListReachesEnd() {
        val viewModel = setScreen(
            NewsUiState(
                news = listOf(createNews()),
                hasMorePages = true
            )
        )

        verify(timeout = 5_000, atLeast = 1) {
            viewModel.loadNews()
        }
    }
}
