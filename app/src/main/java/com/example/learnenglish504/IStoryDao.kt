package com.example.learnenglish504

import androidx.room.Dao
import androidx.room.Query
import io.reactivex.Single


@Dao
interface IStoryDao {

    @Query("SELECT * FROM story")
    fun getAllLessons(): Single<List<Story>>

}