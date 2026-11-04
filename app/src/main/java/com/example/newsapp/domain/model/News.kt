package com.example.newsapp.domain.model

data class News(
    val id: Int,
    val title: String,
    val date: String,
    val shortDescription: String,
    val fullDescription: String? = null
)