
package com.example.newsapp.data.remote

import com.example.newsapp.data.remote.dto.CategoriesResponseDto
import com.example.newsapp.data.remote.dto.CategoryDto
import com.example.newsapp.data.remote.dto.NewsDetailsResponseDto
import com.example.newsapp.data.remote.dto.NewsDto
import com.example.newsapp.data.remote.dto.NewsListResponseDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class ApiResponseHandlerTest {


    @Test
    fun `handleCategories returns list when code is zero`() {
        val categories = listOf(
            CategoryDto(0, "Много новостей"),
            CategoryDto(1, "Немного новостей")
        )

        val response = CategoriesResponseDto(
            code = 0,
            list = categories
        )

        val result = ApiResponseHandler.handleCategories(response)

        assertEquals(categories, result)
    }

    @Test
    fun `handleCategories throws ApiException when code is not zero`() {
        val response = CategoriesResponseDto(
            code = 14,
            message = "Ошибка сервера"
        )

        val exception = assertThrows(ApiException::class.java) {
            ApiResponseHandler.handleCategories(response)
        }

        assertEquals(14, exception.code)
        assertEquals("Ошибка сервера", exception.message)
    }

    @Test
    fun `handleCategories uses default message when message is null`() {
        val response = CategoriesResponseDto(code = 14)

        val exception = assertThrows(ApiException::class.java) {
            ApiResponseHandler.handleCategories(response)
        }

        assertEquals("Не удалось загрузить категории", exception.message)
    }

    @Test
    fun `handleCategories throws when list is null`() {
        val response = CategoriesResponseDto(code = 0, list = null)

        val exception = assertThrows(IllegalStateException::class.java) {
            ApiResponseHandler.handleCategories(response)
        }

        assertEquals("Сервер не вернул список категорий", exception.message)
    }


    @Test
    fun `handleNews returns list when code is zero`() {
        val news = listOf(
            NewsDto(
                id = 18,
                title = "Заголовок",
                date = "2016-08-03T11:15:21.104Z",
                shortDescription = "Краткое описание"
            )
        )

        val response = NewsListResponseDto(
            code = 0,
            list = news
        )

        val result = ApiResponseHandler.handleNews(response)

        assertEquals(news, result)
    }

    @Test
    fun `handleNews throws ApiException when code is not zero`() {
        val response = NewsListResponseDto(
            code = 14,
            message = "Ошибка загрузки новостей"
        )

        val exception = assertThrows(ApiException::class.java) {
            ApiResponseHandler.handleNews(response)
        }

        assertEquals(14, exception.code)
        assertEquals("Ошибка загрузки новостей", exception.message)
    }

    @Test
    fun `handleNews uses default message when message is null`() {
        val response = NewsListResponseDto(code = 14)

        val exception = assertThrows(ApiException::class.java) {
            ApiResponseHandler.handleNews(response)
        }

        assertEquals("Не удалось загрузить новости", exception.message)
    }

    @Test
    fun `handleNews throws when list is null`() {
        val response = NewsListResponseDto(code = 0, list = null)

        val exception = assertThrows(IllegalStateException::class.java) {
            ApiResponseHandler.handleNews(response)
        }

        assertEquals("Сервер не вернул список новостей", exception.message)
    }


    @Test
    fun `handleNewsDetails returns news when code is zero`() {
        val news = NewsDto(
            id = 18,
            title = "Заголовок",
            date = "2016-08-03T11:15:21.104Z",
            shortDescription = "Краткое описание",
            fullDescription = "<p>Полный текст</p>"
        )

        val response = NewsDetailsResponseDto(
            code = 0,
            news = news
        )

        val result = ApiResponseHandler.handleNewsDetails(response)

        assertEquals(news, result)
    }

    @Test
    fun `handleNewsDetails throws ApiException when code is not zero`() {
        val response = NewsDetailsResponseDto(
            code = 14,
            message = "Ошибка загрузки новости"
        )

        val exception = assertThrows(ApiException::class.java) {
            ApiResponseHandler.handleNewsDetails(response)
        }

        assertEquals(14, exception.code)
        assertEquals("Ошибка загрузки новости", exception.message)
    }

    @Test
    fun `handleNewsDetails uses default message when message is null`() {
        val response = NewsDetailsResponseDto(code = 14)

        val exception = assertThrows(ApiException::class.java) {
            ApiResponseHandler.handleNewsDetails(response)
        }

        assertEquals("Не удалось загрузить новость", exception.message)
    }

    @Test
    fun `handleNewsDetails throws when news is null`() {
        val response = NewsDetailsResponseDto(code = 0, news = null)

        val exception = assertThrows(IllegalStateException::class.java) {
            ApiResponseHandler.handleNewsDetails(response)
        }

        assertEquals("Сервер не вернул данные новости", exception.message)
    }
}
