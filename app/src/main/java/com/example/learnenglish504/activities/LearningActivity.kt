package com.example.learnenglish504.activities

import CustomPagerAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.viewpager.widget.ViewPager
import com.example.learnenglish504.App
import com.example.learnenglish504.R
import com.example.learnenglish504.Vocabulary
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_learning.*
import kotlinx.android.synthetic.main.activity_learning.learn_word_viewPager
import kotlinx.android.synthetic.main.activity_learning.learn_detail_imgb_next
import kotlinx.android.synthetic.main.activity_learning.learn_detail_imgb_prev
import kotlinx.android.synthetic.main.word_details_top.*

class LearningActivity : AppCompatActivity() {

    lateinit var wordDao: Vocabulary

    var dailyWordsList = arrayListOf<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learning)

        // coming from lesson activity - learning section
        val actualWord = intent.getStringExtra(Constants.INTENT_VALUE_WORD_TO_LEARN)
        val lessonNumber = intent.getIntExtra(Constants.INTENT_VALUE_LESSON_NUMBER, 1)

        // setting pager
        setPager()

        App.vocabularyDao.getWordDetailsByWord(actualWord!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                wordDao = it
                setWord(wordDao)

                dailyWordsList.add(it.word.toString())

                // for showing view pager item, the middle one first
                learn_word_viewPager.currentItem = 1

            }, {}).let { App.compositeDisposable.add(it) }

        var count = 1

        lesson_btn_go_next.setOnClickListener {

// findNotLearnedWords
            App.vocabularyDao.findRemainingWordsByLesson(lessonNumber)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ remainingWords ->

                    remainingWords.shuffled()
                    if (count < 4) {

                        val wordToBeLearned = remainingWords[count].id!!

// getWordDetailsByID
                        App.vocabularyDao.getWordDetailsByID(wordToBeLearned)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                wordDao = it
                                setWord(wordDao)

                                setPager()

                                dailyWordsList.add(it.word.toString())
                                count++
                            }, {}).let { App.compositeDisposable.add(it) }

                    } else {

                    }
                }, {}).let { App.compositeDisposable.add(it) }

            // for each click increase by 1

            // after finished showing words to be learned
            if (count >= 4) {
                val intent = Intent(this, LearningTestActivity::class.java)
                intent.putStringArrayListExtra(
                    Constants.INTENT_VALUE_WORDS_LIST_TO_TEST,
                    dailyWordsList
                )
                startActivity(intent)
            }
        }
    }

    private fun setPager() {

        onNextPrevClicks()

        learn_word_viewPager.currentItem = 1
        learn_word_viewPager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                if (position == 0) {
                    learn_detail_imgb_prev.visibility = View.INVISIBLE;
                } else {
                    learn_detail_imgb_prev.visibility = View.VISIBLE;
                }
                if (position < learn_word_viewPager.adapter!!.count - 1) {
                    learn_detail_imgb_next.visibility = View.VISIBLE;
                } else {
                    learn_detail_imgb_next.visibility = View.INVISIBLE;
                }
            }

            override fun onPageSelected(position: Int) {}
        })
    }

    private fun setWord(word: Vocabulary) {

        word_txv_actual_word.text = word.word
        word_txv_pronounce.text = word.pronunciation
        word_txv_translate.text = word.persian
        word_txv_defenition.text = word.definition

        val examplesPer = ArrayList<String>()

        examplesPer.add(word.pexa.toString())
        examplesPer.add(word.pexb.toString())
        examplesPer.add(word.pexc.toString())

        val examplesEng = word.examples?.split("\n") as ArrayList

        val pagerAdapter = CustomPagerAdapter(this, examplesPer, examplesEng)
        learn_word_viewPager.adapter = pagerAdapter
    }

    private fun onNextPrevClicks() {

        learn_detail_imgb_next.setOnClickListener {
            learn_word_viewPager.setCurrentItem(
                getItem(1),
                true
            )
        }

        learn_detail_imgb_prev.setOnClickListener {
            learn_word_viewPager.setCurrentItem(
                getItem(-1),
                true
            )
        }
    }

    private fun getItem(i: Int): Int = learn_word_viewPager.currentItem + i
}