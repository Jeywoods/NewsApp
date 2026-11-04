package com.example.newsapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "news_details")
data class NewsDetailsEntity(
    @PrimaryKey
    val newsId: Int,
    val title: String,
    val date: String,
    val shortDescription: String,
    val fullDescription: String
)