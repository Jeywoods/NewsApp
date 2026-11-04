package com.example.newsapp.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "news",
    indices = [
        Index(value = ["categoryId", "page"])
    ]
)
data class NewsEntity(
    @PrimaryKey
    val id: Int,
    val categoryId: Int,
    val page: Int,
    val title: String,
    val date: String,
    val shortDescription: String
)