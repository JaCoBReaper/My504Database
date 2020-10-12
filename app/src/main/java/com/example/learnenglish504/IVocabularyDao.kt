package com.example.learnenglish504

import androidx.room.Dao
import androidx.room.Query
import io.reactivex.Completable
import io.reactivex.Single


@Dao
interface IVocabularyDao {

    @Query("SELECT * FROM vocabulary")
    fun getAllVocabs(): Single<List<Vocabulary>>

    @Query("SELECT * FROM vocabulary WHERE lesson = :lesson")
    fun getWordsByLesson(lesson: Int): Single<List<Vocabulary>>

    @Query("SELECT id FROM vocabulary WHERE word = :word")
    fun getIdByWord(word: String): Single<Int>

    @Query("SELECT * FROM vocabulary WHERE id = :inputID")
    fun getVocabById(inputID: Int): Single<Vocabulary>

    @Query("SELECT * FROM vocabulary WHERE word = :word")
    fun getVocabByWord(word: String): Single<Vocabulary>

    @Query("UPDATE vocabulary SET favorited = :state WHERE word = :word")
    fun setFavourite(state: Int, word: String): Completable

    @Query("Select favorited FROM vocabulary Where word = :word")
    fun checkFavourite(word: String): Single<Int>

    @Query("Select * FROM vocabulary Where favorited = 1")
    fun findFavourites(): Single<List<Vocabulary>>

    @Query("UPDATE vocabulary SET is_read = :state WHERE word = :word")
    fun setLearned(state: Int, word: String): Completable

    @Query("Select * FROM vocabulary Where lesson = :lesson AND is_read = 0")
    fun findRemainingWords(lesson: Int): Single<List<Vocabulary>>

    @Query("Select * FROM vocabulary Where lesson = :lesson AND is_read = 1")
    fun findLearnedWordsProgress(lesson: Int): Single<List<Vocabulary>>

    @Query("Select * FROM vocabulary Where is_read = 1")
    fun findAllLearned(): Single<List<Vocabulary>>

}