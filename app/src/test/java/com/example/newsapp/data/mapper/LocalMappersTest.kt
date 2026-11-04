
package com.example.newsapp.data.mapper

import com.example.newsapp.data.local.entity.CategoryEntity
import com.example.newsapp.data.local.entity.NewsDetailsEntity
import com.example.newsapp.data.local.entity.NewsEntity
import com.example.newsapp.domain.model.Category
import com.example.newsapp.domain.model.News
import com.example.newsapp.domain.model.NewsDetails
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class LocalMappersTest {

    @Test
    fun `Category toEntity maps all fields`() {
        val category = Category(
            id = 1,
            name = "Много новостей"
        )

        val result = category.toEntity()

        assertEquals(category.id, result.id)
        assertEquals(category.name, result.name)
    }

    @Test
    fun `CategoryEntity toDomain maps all fields`() {
        val entity = CategoryEntity(
            id = 1,
            name = "Много новостей"
        )

        val result = entity.toDomain()

        assertEquals(entity.id, result.id)
        assertEquals(entity.name, result.name)
    }

    @Test
    fun `News toEntity maps all fields including category and page`() {
        val news = News(
            id = 18,
            title = "Заголовок новости",
            date = "2016-08-03T11:15:21.104Z",
            shortDescription = "Краткое описание",
            fullDescription = null
        )

        val result = news.toEntity(
            categoryId = 1,
            page = 0
        )

        assertEquals(news.id, result.id)
        assertEquals(1, result.categoryId)
        assertEquals(0, result.page)
        assertEquals(news.title, result.title)
        assertEquals(news.date, result.date)
        assertEquals(news.shortDescription, result.shortDescription)
    }

    @Test
    fun `NewsEntity toDomain maps fields and sets fullDescription to null`() {
        val entity = NewsEntity(
            id = 18,
            categoryId = 1,
            page = 0,
            title = "Заголовок новости",
            date = "2016-08-03T11:15:21.104Z",
            shortDescription = "Краткое описание"
        )

        val result = entity.toDomain()

        assertEquals(entity.id, result.id)
        assertEquals(entity.title, result.title)
        assertEquals(entity.date, result.date)
        assertEquals(entity.shortDescription, result.shortDescription)
        assertNull(result.fullDescription)
    }

    @Test
    fun `NewsDetails toEntity maps all fields`() {
        val details = NewsDetails(
            id = 18,
            title = "Заголовок новости",
            date = "2016-08-03T11:15:21.104Z",
            shortDescription = "Краткое описание",
            fullDescription = "<p>Полный текст новости</p>"
        )

        val result = details.toEntity()

        assertEquals(details.id, result.newsId)
        assertEquals(details.title, result.title)
        assertEquals(details.date, result.date)
        assertEquals(details.shortDescription, result.shortDescription)
        assertEquals(details.fullDescription, result.fullDescription)
    }

    @Test
    fun `NewsDetailsEntity toDomain maps all fields`() {
        val entity = NewsDetailsEntity(
            newsId = 18,
            title = "Заголовок новости",
            date = "2016-08-03T11:15:21.104Z",
            shortDescription = "Краткое описание",
            fullDescription = "<p>Полный текст новости</p>"
        )

        val result = entity.toDomain()

        assertEquals(entity.newsId, result.id)
        assertEquals(entity.title, result.title)
        assertEquals(entity.date, result.date)
        assertEquals(entity.shortDescription, result.shortDescription)
        assertEquals(entity.fullDescription, result.fullDescription)
    }
}
