
package com.example.newsapp.data.repository

import com.example.newsapp.data.local.dao.CategoryDao
import com.example.newsapp.data.local.dao.NewsDao
import com.example.newsapp.data.local.dao.NewsDetailsDao
import com.example.newsapp.data.local.entity.CategoryEntity
import com.example.newsapp.data.local.entity.NewsDetailsEntity
import com.example.newsapp.data.local.entity.NewsEntity
import com.example.newsapp.data.remote.NewsApiService
import com.example.newsapp.data.remote.dto.CategoriesResponseDto
import com.example.newsapp.data.remote.dto.CategoryDto
import com.example.newsapp.data.remote.dto.NewsDetailsResponseDto
import com.example.newsapp.data.remote.dto.NewsDto
import com.example.newsapp.data.remote.dto.NewsListResponseDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.net.UnknownHostException

class NewsRepositoryImplTest {

    private val api = mockk<NewsApiService>()
    private val categoryDao = mockk<CategoryDao>()
    private val newsDao = mockk<NewsDao>()
    private val newsDetailsDao = mockk<NewsDetailsDao>()

    private val repository = NewsRepositoryImpl(
        api = api,
        categoryDao = categoryDao,
        newsDao = newsDao,
        newsDetailsDao = newsDetailsDao
    )

    private val categoryDto = CategoryDto(
        id = 0,
        name = "Много новостей"
    )

    private val categoryEntity = CategoryEntity(
        id = 0,
        name = "Много новостей"
    )

    private val newsDto = NewsDto(
        id = 18,
        title = "Заголовок новости",
        date = "2016-08-03T11:15:21.104Z",
        shortDescription = "Краткое описание"
    )

    private val newsEntity = NewsEntity(
        id = 18,
        categoryId = 0,
        page = 0,
        title = "Заголовок новости",
        date = "2016-08-03T11:15:21.104Z",
        shortDescription = "Краткое описание"
    )

    private val detailsDto = NewsDto(
        id = 18,
        title = "Заголовок новости",
        date = "2016-08-03T11:15:21.104Z",
        shortDescription = "Краткое описание",
        fullDescription = "<p>Полный текст новости</p>"
    )

    private val detailsEntity = NewsDetailsEntity(
        newsId = 18,
        title = "Заголовок новости",
        date = "2016-08-03T11:15:21.104Z",
        shortDescription = "Краткое описание",
        fullDescription = "<p>Полный текст новости</p>"
    )


    @Test
    fun `getCategories returns API data and saves it to database`() = runTest {
        coEvery {
            api.getCategories()
        } returns CategoriesResponseDto(
            code = 0,
            list = listOf(categoryDto)
        )

        coEvery {
            categoryDao.insertCategories(any())
        } returns Unit

        val result = repository.getCategories()

        assertEquals(1, result.size)
        assertEquals(0, result[0].id)
        assertEquals("Много новостей", result[0].name)

        coVerify(exactly = 1) {
            categoryDao.insertCategories(listOf(categoryEntity))
        }
    }

    @Test
    fun `getCategories returns cache when internet is unavailable`() = runTest {
        coEvery {
            api.getCategories()
        } throws UnknownHostException()

        coEvery {
            categoryDao.getAllCategories()
        } returns listOf(categoryEntity)

        val result = repository.getCategories()

        assertEquals(1, result.size)
        assertEquals("Много новостей", result[0].name)

        coVerify(exactly = 0) {
            categoryDao.insertCategories(any())
        }
    }

    @Test
    fun `getCategories throws exception when API fails and cache is empty`() = runTest {
        coEvery {
            api.getCategories()
        } throws UnknownHostException()

        coEvery {
            categoryDao.getAllCategories()
        } returns emptyList()

        val exception = try {
            repository.getCategories()
            null
        } catch (e: Exception) {
            e
        }

        assertTrue(exception != null)
        assertEquals("Нет подключения к интернету", exception?.message)
    }

    @Test
    fun `getCategories loads fresh data after connection is restored`() = runTest {
        coEvery {
            api.getCategories()
        } throws UnknownHostException() andThen CategoriesResponseDto(
            code = 0,
            list = listOf(categoryDto)
        )

        coEvery {
            categoryDao.getAllCategories()
        } returns listOf(categoryEntity)

        coEvery {
            categoryDao.insertCategories(any())
        } returns Unit

        val cachedResult = repository.getCategories()

        assertEquals("Много новостей", cachedResult[0].name)

        val freshResult = repository.getCategories()

        assertEquals("Много новостей", freshResult[0].name)

        coVerify(exactly = 2) {
            api.getCategories()
        }

        coVerify(exactly = 1) {
            categoryDao.insertCategories(listOf(categoryEntity))
        }
    }


    @Test
    fun `getNews returns API data and saves it to database`() = runTest {
        coEvery {
            api.getNews(categoryId = 0, page = 0)
        } returns NewsListResponseDto(
            code = 0,
            list = listOf(newsDto)
        )

        coEvery {
            newsDao.insertNews(any())
        } returns Unit

        val result = repository.getNews(categoryId = 0, page = 0)

        assertEquals(1, result.size)
        assertEquals(18, result[0].id)
        assertEquals("Заголовок новости", result[0].title)

        coVerify(exactly = 1) {
            newsDao.insertNews(listOf(newsEntity))
        }
    }

    @Test
    fun `getNews returns cache when internet is unavailable`() = runTest {
        coEvery {
            api.getNews(categoryId = 0, page = 0)
        } throws UnknownHostException()

        coEvery {
            newsDao.getNewsByPage(categoryId = 0, page = 0)
        } returns listOf(newsEntity)

        val result = repository.getNews(categoryId = 0, page = 0)

        assertEquals(1, result.size)
        assertEquals(18, result[0].id)
        assertEquals("Заголовок новости", result[0].title)
        assertEquals(null, result[0].fullDescription)

        coVerify(exactly = 0) {
            newsDao.insertNews(any())
        }
    }

    @Test
    fun `getNews throws exception when API fails and cache is empty`() = runTest {
        coEvery {
            api.getNews(categoryId = 0, page = 0)
        } throws UnknownHostException()

        coEvery {
            newsDao.getNewsByPage(categoryId = 0, page = 0)
        } returns emptyList()

        val exception = try {
            repository.getNews(categoryId = 0, page = 0)
            null
        } catch (e: Exception) {
            e
        }

        assertTrue(exception != null)
        assertEquals("Нет подключения к интернету", exception?.message)
    }

    @Test
    fun `getNews loads fresh data after connection is restored`() = runTest {
        coEvery {
            api.getNews(categoryId = 0, page = 0)
        } throws UnknownHostException() andThen NewsListResponseDto(
            code = 0,
            list = listOf(newsDto)
        )

        coEvery {
            newsDao.getNewsByPage(categoryId = 0, page = 0)
        } returns listOf(newsEntity)

        coEvery {
            newsDao.insertNews(any())
        } returns Unit

        val cachedResult = repository.getNews(categoryId = 0, page = 0)

        assertEquals(18, cachedResult[0].id)

        val freshResult = repository.getNews(categoryId = 0, page = 0)

        assertEquals(18, freshResult[0].id)

        coVerify(exactly = 2) {
            api.getNews(categoryId = 0, page = 0)
        }

        coVerify(exactly = 1) {
            newsDao.insertNews(listOf(newsEntity))
        }
    }


    @Test
    fun `getNewsDetails returns API data and saves it to database`() = runTest {
        coEvery {
            api.getNewsDetails(newsId = 18)
        } returns NewsDetailsResponseDto(
            code = 0,
            news = detailsDto
        )

        coEvery {
            newsDetailsDao.insertNewsDetails(any())
        } returns Unit

        val result = repository.getNewsDetails(newsId = 18)

        assertEquals(18, result.id)
        assertEquals("Заголовок новости", result.title)
        assertEquals("<p>Полный текст новости</p>", result.fullDescription)

        coVerify(exactly = 1) {
            newsDetailsDao.insertNewsDetails(detailsEntity)
        }
    }

    @Test
    fun `getNewsDetails returns cache when internet is unavailable`() = runTest {
        coEvery {
            api.getNewsDetails(newsId = 18)
        } throws UnknownHostException()

        coEvery {
            newsDetailsDao.getNewsDetails(newsId = 18)
        } returns detailsEntity

        val result = repository.getNewsDetails(newsId = 18)

        assertEquals(18, result.id)
        assertEquals("<p>Полный текст новости</p>", result.fullDescription)

        coVerify(exactly = 0) {
            newsDetailsDao.insertNewsDetails(any())
        }
    }

    @Test
    fun `getNewsDetails throws exception when API fails and cache is empty`() = runTest {
        coEvery {
            api.getNewsDetails(newsId = 18)
        } throws UnknownHostException()

        coEvery {
            newsDetailsDao.getNewsDetails(newsId = 18)
        } returns null

        val exception = try {
            repository.getNewsDetails(newsId = 18)
            null
        } catch (e: Exception) {
            e
        }

        assertTrue(exception != null)
        assertEquals("Нет подключения к интернету", exception?.message)
    }

    @Test
    fun `getNewsDetails loads fresh data after connection is restored`() = runTest {
        coEvery {
            api.getNewsDetails(newsId = 18)
        } throws UnknownHostException() andThen NewsDetailsResponseDto(
            code = 0,
            news = detailsDto
        )

        coEvery {
            newsDetailsDao.getNewsDetails(newsId = 18)
        } returns detailsEntity

        coEvery {
            newsDetailsDao.insertNewsDetails(any())
        } returns Unit

        val cachedResult = repository.getNewsDetails(newsId = 18)

        assertEquals("<p>Полный текст новости</p>", cachedResult.fullDescription)

        val freshResult = repository.getNewsDetails(newsId = 18)

        assertEquals("<p>Полный текст новости</p>", freshResult.fullDescription)

        coVerify(exactly = 2) {
            api.getNewsDetails(newsId = 18)
        }

        coVerify(exactly = 1) {
            newsDetailsDao.insertNewsDetails(detailsEntity)
        }
    }


    @Test
    fun `getCategories rethrows cancellation exception`() = runTest {
        coEvery {
            api.getCategories()
        } throws CancellationException("Request cancelled")

        try {
            repository.getCategories()
            throw AssertionError("CancellationException was expected")
        } catch (e: CancellationException) {
            assertEquals("Request cancelled", e.message)
        }

        coVerify(exactly = 0) {
            categoryDao.getAllCategories()
        }
    }


    @Test
    fun `getCategories uses cache when API returns error code`() = runTest {
        coEvery {
            api.getCategories()
        } returns CategoriesResponseDto(
            code = 14,
            message = "Ошибка сервера"
        )

        coEvery {
            categoryDao.getAllCategories()
        } returns listOf(categoryEntity)

        val result = repository.getCategories()

        assertEquals(1, result.size)
        assertEquals("Много новостей", result[0].name)

        coVerify(exactly = 0) {
            categoryDao.insertCategories(any())
        }
    }

    @Test
    fun `getNews uses cache when API returns error code`() = runTest {
        coEvery {
            api.getNews(categoryId = 0, page = 0)
        } returns NewsListResponseDto(
            code = 14,
            message = "Ошибка сервера"
        )

        coEvery {
            newsDao.getNewsByPage(categoryId = 0, page = 0)
        } returns listOf(newsEntity)

        val result = repository.getNews(categoryId = 0, page = 0)

        assertEquals(1, result.size)
        assertEquals(18, result[0].id)

        coVerify(exactly = 0) {
            newsDao.insertNews(any())
        }
    }

    @Test
    fun `getNewsDetails uses cache when API returns error code`() = runTest {
        coEvery {
            api.getNewsDetails(newsId = 18)
        } returns NewsDetailsResponseDto(
            code = 14,
            message = "Ошибка сервера"
        )

        coEvery {
            newsDetailsDao.getNewsDetails(newsId = 18)
        } returns detailsEntity

        val result = repository.getNewsDetails(newsId = 18)

        assertEquals(18, result.id)
        assertEquals("<p>Полный текст новости</p>", result.fullDescription)

        coVerify(exactly = 0) {
            newsDetailsDao.insertNewsDetails(any())
        }
    }
}