package com.example.learnenglish504.activities

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SimpleOnItemTouchListener
import com.example.learnenglish504.App
import com.example.learnenglish504.R
import com.example.learnenglish504.Vocabulary
import com.example.learnenglish504.adapter.MyLearnTestAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_learning_test.*
import kotlinx.android.synthetic.main.activity_lesson.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule


class LearningTestActivity : AppCompatActivity() {

    private var learnedList = arrayListOf<String>()
    private var randomWordsList = arrayListOf<Vocabulary>()
    lateinit var chosenWord: String
    private val wordDurationInSecond = 5L
    private lateinit var testAdapter: MyLearnTestAdapter
    private lateinit var shuffledList: List<Vocabulary>
    private lateinit var inputWord: Vocabulary

    private var quesNum = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learning_test)


// coming from LearnActivity
        learnedList =
            intent.getStringArrayListExtra(Constants.INTENT_VALUE_WORDS_LIST_TO_TEST) as ArrayList<String>
        val lessonNumber = intent.getIntExtra(Constants.INTENT_VALUE_LESSON_NUMBER, 1)

        chosenWord = learnedList[quesNum]

        find3RandomWord(chosenWord)

        startTimer(wordDurationInSecond)
    }

    private fun find3RandomWord(chosenWord: String) {

        App.vocabularyDao.getWordDetailsByWord(chosenWord)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ vocabulary ->

                inputWord = vocabulary

                App.vocabularyDao.getWordsByLesson(vocabulary.lesson!!)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ lessonWords ->

                        learn_test_chosen_word.text = vocabulary.word

                        randomWordsList.add(vocabulary)

                        var tempList = lessonWords.shuffled() as ArrayList

                        tempList.remove(vocabulary)
                        tempList.shuffled()

                        for (index in 0..2) {
                            randomWordsList.add(tempList[index])
                        }
                        shuffledList = randomWordsList.shuffled()

                        learn_test_recv.layoutManager = GridLayoutManager(this, 2)
                        testAdapter = MyLearnTestAdapter(this, shuffledList, inputWord)
                        learn_test_recv.adapter = testAdapter

// stop scrolling
                        learn_test_recv.addOnItemTouchListener(object :
                            SimpleOnItemTouchListener() {
                            override fun onInterceptTouchEvent(
                                rv: RecyclerView,
                                e: MotionEvent
                            ): Boolean {
                                return rv.scrollState == RecyclerView.SCROLL_STATE_DRAGGING
                            }
                        })

                    }, {}).let { App.compositeDisposable.add(it) }

            }, {}).let { App.compositeDisposable.add(it) }
    }

    private fun startTimer(durationInSeconds: Long) {

        circle_progress_normal.setProgressInTime(0, 100, durationInSeconds * 1000)
        val countDownTimer = object : CountDownTimer(durationInSeconds * 1000, 1000) {

            override fun onTick(p0: Long) {}

            override fun onFinish() {

                shuffledList.forEach {
                    if (it.word == inputWord.word)
                        it.status = 1
                    else {
                        it.status = -1
                    }
                }
                testAdapter = MyLearnTestAdapter(this@LearningTestActivity, shuffledList, inputWord)
                learn_test_recv.adapter = testAdapter

                Timer("BackToWordDefinition", false).schedule(500) {

                    
                }
            }
        }.start()
    }

    override fun onResume() {

        if (quesNum in 1..3) {

            chosenWord = learnedList[quesNum]

            find3RandomWord(chosenWord)

            startTimer(wordDurationInSecond)
        }


        super.onResume()
    }
}