package com.example.learnenglish504

import android.app.Application
import android.util.Log
import android.widget.Toast
import com.example.learnenglish504.database.MyDatabase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

open class App : Application() {

    companion object {

        var databaseInstance: MyDatabase? = null

        val compositeDisposable = CompositeDisposable()

        lateinit var storyDao: IStoryDao
        lateinit var vocabularyDao: IVocabularyDao

        lateinit var lessons: List<Story>
        lateinit var vocabularies: List<Vocabulary>
        lateinit var lessonWords: List<Vocabulary>
//        lateinit var word: List<Vocabulary>

    }

    //--------------------------
    override fun onCreate() {
        super.onCreate()

        databaseInstance = MyDatabase.getDatabaseInstance(this)

        storyDao = databaseInstance!!.storyDao()
        vocabularyDao = databaseInstance!!.vocabularyDao()
    }
}