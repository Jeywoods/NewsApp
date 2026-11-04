package com.example.newsapp.domain.model

data class NewsDetails(
    val id: Int,
    val title: String,
    val date: String,
    val shortDescription: String,
    val fullDescription: String
)