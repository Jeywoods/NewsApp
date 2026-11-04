
package com.example.newsapp.domain.usecase

import com.example.newsapp.domain.model.Category
import com.example.newsapp.domain.model.News
import com.example.newsapp.domain.model.NewsDetails
import com.example.newsapp.domain.repository.NewsRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class UseCasesTest {

    private val repository = mockk<NewsRepository>()

    private val categoriesUseCase = GetCategoriesUseCase(repository)
    private val newsUseCase = GetNewsUseCase(repository)
    private val detailsUseCase = GetNewsDetailsUseCase(repository)

    private val category = Category(
        id = 0,
        name = "Много новостей"
    )

    private val news = News(
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
    fun `GetCategoriesUseCase returns categories from repository`() = runTest {
        coEvery {
            repository.getCategories()
        } returns listOf(category)

        val result = categoriesUseCase()

        assertEquals(listOf(category), result)

        coVerify(exactly = 1) {
            repository.getCategories()
        }
    }

    @Test
    fun `GetCategoriesUseCase propagates repository exception`() = runTest {
        coEvery { repository.getCategories() } throws Exception("Ошибка загрузки категорий")

        val exception = try {
            categoriesUseCase()
            null
        } catch (e: Exception) {
            e
        }

        assertEquals("Ошибка загрузки категорий", exception?.message)
    }

    @Test
    fun `GetNewsUseCase passes categoryId and page to repository`() = runTest {
        coEvery {
            repository.getNews(categoryId = 0, page = 2)
        } returns listOf(news)

        val result = newsUseCase(categoryId = 0, page = 2)

        assertEquals(listOf(news), result)

        coVerify(exactly = 1) {
            repository.getNews(categoryId = 0, page = 2)
        }
    }

    @Test
    fun `GetNewsUseCase propagates repository exception`() = runTest {
        coEvery {
            repository.getNews(categoryId = 0, page = 2)
        } throws Exception("Ошибка загрузки новостей")

        val exception = try {
            newsUseCase(categoryId = 0, page = 2)
            null
        } catch (e: Exception) {
            e
        }

        assertEquals("Ошибка загрузки новостей", exception?.message)
    }

    @Test
    fun `GetNewsDetailsUseCase passes newsId to repository`() = runTest {
        coEvery {
            repository.getNewsDetails(newsId = 18)
        } returns details

        val result = detailsUseCase(newsId = 18)

        assertEquals(details, result)

        coVerify(exactly = 1) {
            repository.getNewsDetails(newsId = 18)
        }
    }

    @Test
    fun `GetNewsDetailsUseCase propagates repository exception`() = runTest {
        coEvery {
            repository.getNewsDetails(newsId = 18)
        } throws Exception("Ошибка загрузки новости")

        val exception = try {
            detailsUseCase(newsId = 18)
            null
        } catch (e: Exception) {
            e
        }

        assertEquals("Ошибка загрузки новости", exception?.message)
    }
}
