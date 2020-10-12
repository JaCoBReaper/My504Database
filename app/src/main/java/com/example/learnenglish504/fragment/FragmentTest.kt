package com.example.learnenglish504.fragment

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.learnenglish504.App
import com.example.learnenglish504.R
import com.example.learnenglish504.Vocabulary
import com.example.learnenglish504.activities.*
import com.example.learnenglish504.adapter.IOnAnswerClick
import com.example.learnenglish504.adapter.LearnTestAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_learning_test.*
import kotlinx.android.synthetic.main.fragment_test.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule


class FragmentTest(
    val onTimeExpiredOrWrongAnswer: IOnTimeExpiredOrWrongAnswer,
    private val askNewWord: IOnAskNewWord
) : Fragment(),
    IOnAnswerClick {
    // region Variables
    private lateinit var inputWord: Vocabulary
    private var randomWordsList = arrayListOf<Vocabulary>()
    private lateinit var shuffledList: List<Vocabulary>
    private lateinit var testAdapter: LearnTestAdapter
    private lateinit var countDownTimer: CountDownTimer
    private val wordDurationInSecond = 10L
    private lateinit var viewedList: ArrayList<String>
    private var lessonNumber = 2
// endregion

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_test, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        lessonNumber = (activity as LearningAskActivity).lessonNumberInActivity

        viewedList = arguments!!.getStringArrayList(Constants.SEND_LIST_TO_TEST_FRAGMENT)!!

        if (viewedList.size > 0) {
            find3RandomWord(viewedList[0])
            startTimer(wordDurationInSecond)
        } else {
            AlertDialog.Builder(activity!!)

                .setMessage("درس امروز تمام شد، آیا میخواهید ادامه دهید؟")

                .setPositiveButton("بله") { dialog, _ ->

                    dialog.dismiss()
                    activity!!.finishAffinity()

                    val intent = Intent(activity, LessonActivity::class.java)
                    intent.putExtra(Constants.INTENT_LESSON_NUMBER, lessonNumber)
                    startActivity(intent)
                }
                .setNegativeButton("خیر") { dialog, _ ->
                    dialog.dismiss()
                    activity!!.finishAffinity()
                    startActivity(Intent(activity, HomeActivity::class.java))
                }
                .show()
        }
    }

    private fun find3RandomWord(chosenWord: String) {

        App.databaseInstance!!.vocabularyDao().getVocabByWord(chosenWord)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ vocabulary ->

                inputWord = vocabulary

                App.databaseInstance!!.vocabularyDao().getWordsByLesson(vocabulary.lesson!!)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ lessonWords ->

                        frgmnt_test_word.text = vocabulary.word

                        randomWordsList.add(vocabulary)

                        val tempList = lessonWords.shuffled() as ArrayList

                        tempList.remove(vocabulary)
                        tempList.shuffled()

                        for (index in 0..2) {
                            randomWordsList.add(tempList[index])
                        }
                        shuffledList = randomWordsList.shuffled()

                        frgmnt_test_recv.layoutManager = GridLayoutManager(activity, 2)
                        testAdapter = LearnTestAdapter(activity!!, this, shuffledList, inputWord)
                        frgmnt_test_recv.adapter = testAdapter

// stop scrolling
                        frgmnt_test_recv.addOnItemTouchListener(object :
                            RecyclerView.SimpleOnItemTouchListener() {
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

        activity!!.circle_progress_normal.setProgressInTime(0, 100, durationInSeconds * 1000)

        countDownTimer = object : CountDownTimer(durationInSeconds * 1000, 1000) {

            override fun onTick(p0: Long) {}

            override fun onFinish() {

                shuffledList.forEach {
                    if (it.word == inputWord.word)
                        it.status = 1
                    else {
                        it.status = -1
                    }
                }
                testAdapter = LearnTestAdapter(
                    activity!!,
                    this@FragmentTest,
                    shuffledList,
                    inputWord
                )
                frgmnt_test_recv.adapter = testAdapter

                Timer("WaitingBeforeRepeat", false).schedule(1000) {
                    onTimeExpiredOrWrongAnswer.gotoRepeatFragment(inputWord.word!!)
                }
            }
        }.start()
    }

    override fun onCorrectAnswerClick(word: Vocabulary) {

        viewedList.remove(word.word)

        Timer("WaitingForNextWord", false).schedule(500) {

            askNewWord.updateFragment(viewedList)
            countDownTimer.cancel()
        }

        App.databaseInstance!!.vocabularyDao().setLearned(1, inputWord.word!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({}, {}).let { App.compositeDisposable.add(it) }
    }

    override fun onWrongAnswerClick(word: Vocabulary) {

        countDownTimer.cancel()

        Timer("WaitingForRepeatingWord", false).schedule(500) {
            onTimeExpiredOrWrongAnswer.gotoRepeatFragment(word.word!!)
        }
    }

    override fun onDestroy() {

        if (this::countDownTimer.isInitialized) {
            countDownTimer.cancel()
        }
        super.onDestroy()
    }
}


interface IOnTimeExpiredOrWrongAnswer {

    fun gotoRepeatFragment(word: String)
}

interface IOnAskNewWord {

    fun updateFragment(newList: ArrayList<String>)
}