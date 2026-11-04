package com.example.newsapp.data.mapper

import com.example.newsapp.data.remote.dto.CategoryDto
import com.example.newsapp.data.remote.dto.NewsDto
import com.example.newsapp.domain.model.Category
import com.example.newsapp.domain.model.News
import com.example.newsapp.domain.model.NewsDetails

fun CategoryDto.toDomain(): Category {
    return Category(
        id = id,
        name = name
    )
}

fun NewsDto.toDomain(): News {
    return News(
        id = id,
        title = title,
        date = date,
        shortDescription = shortDescription,
        fullDescription = fullDescription
    )
}

fun NewsDto.toDetailsDomain(): NewsDetails {
    return NewsDetails(
        id = id,
        title = title,
        date = date,
        shortDescription = shortDescription,
        fullDescription = fullDescription
            ?: throw IllegalStateException("Полный текст новости отсутствует")
    )
}