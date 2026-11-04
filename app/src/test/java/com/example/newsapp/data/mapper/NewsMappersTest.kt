
package com.example.newsapp.data.mapper

import com.example.newsapp.data.remote.dto.CategoryDto
import com.example.newsapp.data.remote.dto.NewsDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test

class NewsMappersTest {

    @Test
    fun `CategoryDto toDomain maps all fields`() {
        val dto = CategoryDto(
            id = 1,
            name = "Много новостей"
        )

        val result = dto.toDomain()

        assertEquals(dto.id, result.id)
        assertEquals(dto.name, result.name)
    }

    @Test
    fun `NewsDto toDomain maps all fields`() {
        val dto = NewsDto(
            id = 18,
            title = "Заголовок новости",
            date = "2016-08-03T11:15:21.104Z",
            shortDescription = "Краткое описание",
            fullDescription = "<p>Полный текст новости</p>"
        )

        val result = dto.toDomain()

        assertEquals(dto.id, result.id)
        assertEquals(dto.title, result.title)
        assertEquals(dto.date, result.date)
        assertEquals(dto.shortDescription, result.shortDescription)
        assertEquals(dto.fullDescription, result.fullDescription)
    }

    @Test
    fun `NewsDto toDomain preserves null fullDescription`() {
        val dto = NewsDto(
            id = 18,
            title = "Заголовок новости",
            date = "2016-08-03T11:15:21.104Z",
            shortDescription = "Краткое описание",
            fullDescription = null
        )

        val result = dto.toDomain()

        assertNull(result.fullDescription)
    }

    @Test
    fun `NewsDto toDetailsDomain maps all fields`() {
        val dto = NewsDto(
            id = 18,
            title = "Заголовок новости",
            date = "2016-08-03T11:15:21.104Z",
            shortDescription = "Краткое описание",
            fullDescription = "<p>Полный текст новости</p>"
        )

        val result = dto.toDetailsDomain()

        assertEquals(dto.id, result.id)
        assertEquals(dto.title, result.title)
        assertEquals(dto.date, result.date)
        assertEquals(dto.shortDescription, result.shortDescription)
        assertEquals(dto.fullDescription, result.fullDescription)
    }

    @Test
    fun `NewsDto toDetailsDomain throws when fullDescription is null`() {
        val dto = NewsDto(
            id = 18,
            title = "Заголовок новости",
            date = "2016-08-03T11:15:21.104Z",
            shortDescription = "Краткое описание",
            fullDescription = null
        )

        assertThrows(IllegalStateException::class.java) {
            dto.toDetailsDomain()
        }
    }
}
