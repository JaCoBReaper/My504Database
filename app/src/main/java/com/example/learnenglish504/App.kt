package com.example.learnenglish504

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.example.learnenglish504.activities.Constants
import com.example.learnenglish504.database.MyDatabase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

open class App : Application() {

    companion object {

        val compositeDisposable = CompositeDisposable()

        lateinit var storyDao: IStoryDao
        lateinit var vocabularyDao: IVocabularyDao

        lateinit var lessons: List<Story>
        lateinit var lessonWords: List<Vocabulary>
        lateinit var favWords: List<Vocabulary>
        lateinit var notLessonLearnedWords: List<Vocabulary>
//        lateinit var learnedWords: List<Vocabulary>

        var databaseInstance: MyDatabase? = null

        var wordLearned = 0

        lateinit var learnLessonPref: SharedPreferences
//        lateinit var learnLessonPrefEditor: SharedPreferences.Editor
    }

    //--------------------------
    override fun onCreate() {
        super.onCreate()

        databaseInstance = MyDatabase.getDatabaseInstance(this)

        storyDao = databaseInstance!!.storyDao()
        vocabularyDao = databaseInstance!!.vocabularyDao()

        storyDao.getAllLessons()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ }, { }).let {
//                compositeDisposable.add(it)
            }

        vocabularyDao.getAllVocabs()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ }, { }).let {
                compositeDisposable.add(it)
            }

        learnLessonPref = getSharedPreferences(Constants.PREF_LEARNING,Context.MODE_PRIVATE) ?: return
//        learnLessonPref = getSharedPreferences(Constants.PREF_LEARNING_LESSON, Context.MODE_PRIVATE)
//        learnLessonPrefEditor = learnLessonPref.edit()
//        learnLessonPrefEditor.putInt(Constants.NUM_WORDS_LEARNED, 0)
//        learnLessonPrefEditor.apply()

    }
}