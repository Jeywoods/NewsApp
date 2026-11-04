package com.example.newsapp.data.remote

import com.example.newsapp.data.remote.dto.CategoriesResponseDto
import com.example.newsapp.data.remote.dto.NewsDetailsResponseDto
import com.example.newsapp.data.remote.dto.NewsListResponseDto

object ApiResponseHandler {

    fun handleCategories(response: CategoriesResponseDto) =
        response.run {
            if (code != 0) {
                throw ApiException(
                    code = code,
                    message = message ?: "Не удалось загрузить категории"
                )
            }

            list ?: throw IllegalStateException(
                "Сервер не вернул список категорий"
            )
        }

    fun handleNews(response: NewsListResponseDto) =
        response.run {
            if (code != 0) {
                throw ApiException(
                    code = code,
                    message = message ?: "Не удалось загрузить новости"
                )
            }

            list ?: throw IllegalStateException(
                "Сервер не вернул список новостей"
            )
        }

    fun handleNewsDetails(response: NewsDetailsResponseDto) =
        response.run {
            if (code != 0) {
                throw ApiException(
                    code = code,
                    message = message ?: "Не удалось загрузить новость"
                )
            }

            news ?: throw IllegalStateException(
                "Сервер не вернул данные новости"
            )
        }
}