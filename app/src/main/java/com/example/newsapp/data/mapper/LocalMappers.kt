package com.example.newsapp.data.mapper

import com.example.newsapp.data.local.entity.CategoryEntity
import com.example.newsapp.data.local.entity.NewsEntity
import com.example.newsapp.data.local.entity.NewsDetailsEntity
import com.example.newsapp.domain.model.Category
import com.example.newsapp.domain.model.News
import com.example.newsapp.domain.model.NewsDetails

fun Category.toEntity(): CategoryEntity {
    return CategoryEntity(
        id = id,
        name = name
    )
}

fun CategoryEntity.toDomain(): Category {
    return Category(
        id = id,
        name = name
    )
}

fun News.toEntity(
    categoryId: Int,
    page: Int
): NewsEntity {
    return NewsEntity(
        id = id,
        categoryId = categoryId,
        page = page,
        title = title,
        date = date,
        shortDescription = shortDescription
    )
}

fun NewsEntity.toDomain(): News {
    return News(
        id = id,
        title = title,
        date = date,
        shortDescription = shortDescription,
        fullDescription = null
    )
}

fun NewsDetails.toEntity(): NewsDetailsEntity {
    return NewsDetailsEntity(
        newsId = id,
        title = title,
        date = date,
        shortDescription = shortDescription,
        fullDescription = fullDescription
    )
}

fun NewsDetailsEntity.toDomain(): NewsDetails {
    return NewsDetails(
        id = newsId,
        title = title,
        date = date,
        shortDescription = shortDescription,
        fullDescription = fullDescription
    )
}
