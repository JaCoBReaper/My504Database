package com.example.learnenglish504.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.learnenglish504.App
import com.example.learnenglish504.App.Companion.compositeDisposable
import com.example.learnenglish504.App.Companion.learnLessonPrefEditor
import com.example.learnenglish504.App.Companion.lessonWords
import com.example.learnenglish504.App.Companion.lessons
import com.example.learnenglish504.App.Companion.storyDao
import com.example.learnenglish504.App.Companion.vocabularyDao
import com.example.learnenglish504.R
import com.example.learnenglish504.activities.Constants.Companion.INTENT_VALUE_LessonNumber
import com.example.learnenglish504.activities.Constants.Companion.INTENT_VALUE_WORD
import com.example.learnenglish504.activities.Constants.Companion.INTENT_VALUE_WORDID
import com.example.learnenglish504.activities.Constants.Companion.REQ_CODET_TO_WORDACTIVITY
import com.example.learnenglish504.adapter.IOnWordClickListener
import com.example.learnenglish504.adapter.LessonAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_lesson.*
import kotlinx.android.synthetic.main.lesson_prog_frame.*
import kotlinx.android.synthetic.main.word_details_top.*

class LessonActivity : AppCompatActivity(), IOnWordClickListener {

    var myAdapter: LessonAdapter? = null
    private var lessonNumber = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lesson)

// coming from homeAdapter
        lessonNumber = intent.getIntExtra(INTENT_VALUE_LessonNumber, 0)

        getLessonWords()
        getLessons()
// setting progress value
        updateProgression()
// coming from home, learning progress button
        /*lesson_btn_continue.setOnClickListener {

            learnLessonPrefEditor.putInt(Constants.PREF_LEARNING_LESSON, lessonNumber)
                .apply()

            val intent = Intent(this, LearningActivity::class.java)
            intent.putExtra(INTENT_VALUE_WORD, lessonNumber)
            startActivityForResult(intent, REQ_CODET_TO_WORDACTIVITY)
        }*/


    }

    private fun getLessons() {
        storyDao.getAllLessons()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

                lessons = it

                lesson_txv_title_number.text = lessons[lessonNumber - 1].id.toString()
                lesson_txv_range.text = lessons[lessonNumber - 1].range

            }, {}).let {
                compositeDisposable.add(it)
            }
    }

    private fun getLessonWords() {
        vocabularyDao.getWordsByLesson(lessonNumber)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

                lessonWords = it

                lesson_recycler.layoutManager = LinearLayoutManager(this)
                myAdapter = LessonAdapter(this, lessonWords, this)
                lesson_recycler.adapter = myAdapter
            }, {

            }).let {
                compositeDisposable.add(it)
            }
    }

    private fun updateProgression() {
        vocabularyDao.findLearned(lessonNumber)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

                prog_lesson_txv_learned.text = it.size.toString()
                prog_lesson_progbar.progress = it.size

            }, {}).let { compositeDisposable.add(it) }
    }

    override fun onItemClicked(wordID: Int) {

        val intent = Intent(this, WordActivity::class.java)
        intent.putExtra(INTENT_VALUE_WORDID, wordID)
        startActivityForResult(intent, REQ_CODET_TO_WORDACTIVITY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQ_CODET_TO_WORDACTIVITY && resultCode == RESULT_OK && data != null) {
            val wordID = data.getIntExtra("ChangedFavID", 0)

            myAdapter?.updateFav(wordID)
//            finish()
//            startActivity(intent)
        }
    }

    override fun onResume() {
        updateProgression()
        super.onResume()
    }

}