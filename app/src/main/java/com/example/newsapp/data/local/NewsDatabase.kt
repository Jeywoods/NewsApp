package com.example.newsapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.newsapp.data.local.dao.CategoryDao
import com.example.newsapp.data.local.dao.NewsDao
import com.example.newsapp.data.local.dao.NewsDetailsDao
import com.example.newsapp.data.local.entity.CategoryEntity
import com.example.newsapp.data.local.entity.NewsEntity
import com.example.newsapp.data.local.entity.NewsDetailsEntity

@Database(
    entities = [
        CategoryEntity::class,
        NewsEntity::class,
        NewsDetailsEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class NewsDatabase : RoomDatabase() {

    abstract fun categoryDao(): CategoryDao

    abstract fun newsDao(): NewsDao

    abstract fun newsDetailsDao(): NewsDetailsDao
}