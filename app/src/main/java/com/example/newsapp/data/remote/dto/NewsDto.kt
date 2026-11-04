package com.example.newsapp.data.remote.dto

data class NewsDto(
    val id: Int,
    val title: String,
    val date: String,
    val shortDescription: String,
    val fullDescription: String? = null
)