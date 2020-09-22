package com.example.learnenglish504

import android.app.Application
import com.example.learnenglish504.database.MyDatabase

class App : Application() {

    companion object {

        var databaseInstance: MyDatabase? = null

        lateinit var storyDao: IStoryDao
        lateinit var vocabularyDao: IVocabularyDao

        lateinit var lessons: List<Story>
        lateinit var vocabularies: List<Vocabulary>
    }

    override fun onCreate() {
        super.onCreate()

        databaseInstance = MyDatabase.getDatabaseInstance(this)

        storyDao = databaseInstance!!.storyDao()
        vocabularyDao = databaseInstance!!.vocabularyDao()

        Thread {
            lessons = storyDao.getAllLessons()
            vocabularies = vocabularyDao.getAll()
        }.start()


    }

}