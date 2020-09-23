package com.example.learnenglish504.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.learnenglish504.IStoryDao
import com.example.learnenglish504.R
import com.example.learnenglish504.Story
import com.example.learnenglish504.Vocabulary
import com.example.learnenglish504.activities.Constants.Companion.INTENT_VALUE_LessonNumber
import com.example.learnenglish504.activities.Constants.Companion.INTENT_VALUE_WordID
import com.example.learnenglish504.adapter.IOnWordClickListener
import com.example.learnenglish504.adapter.LessonAdapter
import com.example.learnenglish504.database.MyDatabase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_lesson.*
import kotlinx.android.synthetic.main.word_details_bottom.*

class LessonActivity : AppCompatActivity(), IOnWordClickListener {

    var myAdapter: LessonAdapter? = null
    val requestCodeToWordActivity = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lesson)


        val compositeDisposable = CompositeDisposable()
        lateinit var lessonWords: List<Vocabulary>
        lateinit var lessons: List<Story>
        val databaseInstance = MyDatabase.getDatabaseInstance(this)
        val storyDao = databaseInstance!!.storyDao()
        val vocabularyDao = databaseInstance.vocabularyDao()

        // coming from homeAdapter
        val lessonNumber = intent.getIntExtra(INTENT_VALUE_LessonNumber, 0)

        vocabularyDao.getWordsByLesson(lessonNumber)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

                lessonWords = it

                lesson_recycler.layoutManager = LinearLayoutManager(this)
                myAdapter = LessonAdapter(this, lessonWords, this)
                lesson_recycler.adapter = myAdapter
                myAdapter!!.notifyDataSetChanged()
            }, {

            }).let {
                compositeDisposable.add(it)
            }

        storyDao.getAllLessons()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

                lessons = it

                lesson_txv_title_number.text = lessons[lessonNumber - 1].id.toString()
                lesson_txv_range.text = lessons[lessonNumber - 1].range

            }, {

            }).let {
                compositeDisposable.add(it)
            }
    }

    override fun onItemClicked(position: Int) {

        val intent = Intent(this, WordActivity::class.java)
        intent.putExtra(INTENT_VALUE_WordID, position)
        startActivityForResult(intent, requestCodeToWordActivity)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == requestCodeToWordActivity && resultCode == RESULT_OK && data != null) {
            val wordID = data.getIntExtra("ChangedFavID", 0)
            val action = data.getStringExtra("Action")

            myAdapter?.updateFav(wordID)
        }
    }
}