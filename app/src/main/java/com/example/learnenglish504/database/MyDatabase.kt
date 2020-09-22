package com.example.learnenglish504.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.learnenglish504.*

@Database(entities = [Vocabulary::class, Story::class], version = 1)
abstract class MyDatabase : RoomDatabase() {


    abstract fun vocabularyDao(): IVocabularyDao

    abstract fun storyDao(): IStoryDao



    companion object {

        const val DATABASE_NAME = "my504db"

        private var Instance: MyDatabase? = null

        fun getDatabaseInstance(context: Context): MyDatabase? {

            if (Instance == null) {

                Instance =
                    Room.databaseBuilder(
                        context,
                        MyDatabase::class.java,
                        "my504db.db"
                    )
                        .createFromAsset("databases/my504db.db")
                        .build()
            }
            return Instance as MyDatabase
        }

        fun closeDatabase() {

            Instance?.close()
        }
    }

}