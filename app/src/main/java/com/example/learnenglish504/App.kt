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

        lateinit var lessons: List<Story>
        lateinit var lessonWords: List<Vocabulary>
        lateinit var favWords: List<Vocabulary>

        var databaseInstance: MyDatabase? = null

        lateinit var learnLessonPref: SharedPreferences
    }

    //--------------------------
    override fun onCreate() {

        databaseInstance = MyDatabase.getDatabaseInstance(this)
        learnLessonPref =
            getSharedPreferences(Constants.PREF_LEARNING, Context.MODE_PRIVATE) ?: return
        super.onCreate()
    }
}