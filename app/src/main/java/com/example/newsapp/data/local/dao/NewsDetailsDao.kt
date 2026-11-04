package com.example.newsapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.newsapp.data.local.entity.NewsDetailsEntity

@Dao
interface NewsDetailsDao {

    @Query("SELECT * FROM news_details WHERE newsId = :newsId")
    suspend fun getNewsDetails(newsId: Int): NewsDetailsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNewsDetails(details: NewsDetailsEntity)

    @Query("DELETE FROM news_details WHERE newsId = :newsId")
    suspend fun deleteNewsDetails(newsId: Int)
}