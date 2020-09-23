package com.example.learnenglish504

import androidx.room.Dao
import androidx.room.Query
import io.reactivex.Completable
import io.reactivex.Single


@Dao
interface IVocabularyDao {

    @Query("SELECT * FROM vocabulary")
    fun getAllVocabs(): Single<List<Vocabulary>>

    @Query("SELECT * FROM vocabulary WHERE lesson=:inputLesson")
    fun getWordsByLesson(inputLesson: Int): Single<List<Vocabulary>>

    @Query("SELECT * FROM vocabulary WHERE id = :inputID")
    fun getWordDetailsByID(inputID: Int): Single<Vocabulary>

    @Query("UPDATE vocabulary SET favorited = :state WHERE word = :word")
    fun setFavourite(state: Int, word: String): Completable

    @Query("Select favorited FROM vocabulary Where word = :word")
    fun checkFavourite(word: String): Single<Int>

//    @Query("SELECT * FROM vocabulary WHERE id = 1")
//    fun <Vocabulary> Maybe()


}