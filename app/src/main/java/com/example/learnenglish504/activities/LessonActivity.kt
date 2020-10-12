package com.example.learnenglish504.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.learnenglish504.App
import com.example.learnenglish504.App.Companion.compositeDisposable
import com.example.learnenglish504.App.Companion.lessonWords
import com.example.learnenglish504.App.Companion.lessons
import com.example.learnenglish504.R
import com.example.learnenglish504.activities.Constants.Companion.INTENT_LESSON_NUMBER
import com.example.learnenglish504.activities.Constants.Companion.SEND_WORD_TO_LEARN
import com.example.learnenglish504.activities.Constants.Companion.INTENT_WORD_ID
import com.example.learnenglish504.activities.Constants.Companion.REQ_CODE_TO_WORD_ACTIVITY
import com.example.learnenglish504.adapter.IOnWordClickListener
import com.example.learnenglish504.adapter.LessonAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_lesson.*
import kotlinx.android.synthetic.main.lesson_prog_frame.*


class LessonActivity : AppCompatActivity(), IOnWordClickListener {

    // region Variables
    var myAdapter: LessonAdapter? = null
    private var lessonNumber = 1
    private var firstWord = "Abandon"
// endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lesson)

// getting intent pushed values : coming from homeAdapter or fragmentTest
        lessonNumber = intent.getIntExtra(INTENT_LESSON_NUMBER, 1)

        setProgressStatus()

        getLessonWords()
// setting lesson title and words range
        getLessons()

// Going to Learning Act
        onContinueLearningClick()
    }

    private fun onContinueLearningClick() {
        lesson_btn_continue.setOnClickListener {

            with(App.learnLessonPref.edit()) {
                putInt(Constants.PREF_LEARNING_LESSON_NUMBER, lessonNumber)
                commit()
            }

            App.databaseInstance!!.vocabularyDao().findRemainingWords(lessonNumber)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ remainingWords ->

                    if (remainingWords.isNotEmpty()) {
                        firstWord = remainingWords[0].word!!

                        val intent = Intent(this, LearningActivity::class.java)
                        intent.putExtra(SEND_WORD_TO_LEARN, firstWord)
                        startActivity(intent)
                    } else
                        Toast.makeText(this, "این درس را فراگرفته اید!", Toast.LENGTH_SHORT).show()

                }, {}).let { compositeDisposable.add(it) }

            // TODO decrease the size when word read
        }
    }

    private fun getLessons() {
        App.databaseInstance!!.storyDao().getAllLessons()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

                lessons = it

                lesson_txv_title_number.text = it[lessonNumber - 1].id.toString()
                lesson_txv_range.text = it[lessonNumber - 1].range

            }, {}).let {
                compositeDisposable.add(it)
            }
    }

    private fun getLessonWords() {

        App.databaseInstance!!.vocabularyDao().getWordsByLesson(lessonNumber)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

                lessonWords = it

                lesson_recycler.layoutManager = LinearLayoutManager(this)
                myAdapter = LessonAdapter(this, lessonWords, this)
                lesson_recycler.adapter = myAdapter

            }, {}).let { compositeDisposable.add(it) }
    }

    private fun updateProgression() {

        App.databaseInstance!!.vocabularyDao().findLearnedWordsProgress(lessonNumber)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

                prog_lesson_txv_learned.text = it.size.toString()
                prog_lesson_progbar.progress = it.size

            }, {}).let { compositeDisposable.add(it) }
    }

    private fun setProgressStatus() {
        App.databaseInstance!!.vocabularyDao().findLearnedWordsProgress(lessonNumber)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

                prog_lesson_txv_learned.text = it.size.toString()
                prog_lesson_progbar.progress = it.size


            }, {}).let {
                compositeDisposable.add(it)
            }
    }

    override fun onItemClicked(wordID: Int) {

        val intent = Intent(this, WordActivity::class.java)
        intent.putExtra(INTENT_WORD_ID, wordID)
        startActivityForResult(intent, REQ_CODE_TO_WORD_ACTIVITY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        updateProgression()
        if (requestCode == REQ_CODE_TO_WORD_ACTIVITY && resultCode == RESULT_OK && data != null) {
            val wordID = data.getIntExtra("ChangedFavID", 0)

            myAdapter?.updateFav(wordID)
        }
    }
}