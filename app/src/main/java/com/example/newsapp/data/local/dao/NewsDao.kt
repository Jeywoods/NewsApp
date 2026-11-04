package com.example.newsapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.newsapp.data.local.entity.NewsEntity

@Dao
interface NewsDao {

    @Query(
        """
        SELECT * FROM news
        WHERE categoryId = :categoryId
        ORDER BY page ASC, id DESC
        """
    )
    suspend fun getNewsByCategory(categoryId: Int): List<NewsEntity>

    @Query(
        """
        SELECT * FROM news
        WHERE categoryId = :categoryId AND page = :page
        ORDER BY id DESC
        """
    )
    suspend fun getNewsByPage(
        categoryId: Int,
        page: Int
    ): List<NewsEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNews(news: List<NewsEntity>)

    @Query(
        """
        DELETE FROM news
        WHERE categoryId = :categoryId AND page = :page
        """
    )
    suspend fun deleteNewsByPage(
        categoryId: Int,
        page: Int
    )

    @Query("DELETE FROM news WHERE categoryId = :categoryId")
    suspend fun deleteNewsByCategory(categoryId: Int)
}