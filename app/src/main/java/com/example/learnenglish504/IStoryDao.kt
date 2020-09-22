package com.example.learnenglish504

import androidx.room.Dao
import androidx.room.Query


@Dao
interface IStoryDao {

    @Query("SELECT * FROM story")
    fun getAllLessons(): List<Story>

}