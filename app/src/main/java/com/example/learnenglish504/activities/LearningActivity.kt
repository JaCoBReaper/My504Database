package com.example.learnenglish504.activities

import CustomPagerAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.viewpager.widget.ViewPager
import com.example.learnenglish504.App
import com.example.learnenglish504.R
import com.example.learnenglish504.Vocabulary
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_learning.*
import kotlinx.android.synthetic.main.word_details_top.*

class LearningActivity : AppCompatActivity() {

    private var dailyWordsList = arrayListOf<String>()
    private var lessonNumber = 1
    private var numOfWordsPerDay = 4
    private var remainingWords = listOf<Vocabulary>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learning)

        // coming from lesson activity - learning section
        val actualWord = intent.getStringExtra(Constants.SEND_WORD_TO_LEARN)

        if (actualWord != null)
            setInitials(actualWord)

// 1 because first word is already set above in "setWord" function, and not to show again
        var count1 = 1
//        var count2 = 1

        lesson_btn_go_next.setOnClickListener {
// find Not Learned Words
            App.databaseInstance!!.vocabularyDao().findRemainingWords(lessonNumber)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({

                    remainingWords = it
// when there's 4 or more
                    if (remainingWords.size >= numOfWordsPerDay) {
                        if (count1 < numOfWordsPerDay) {

                            val vocabulary = remainingWords[count1]
                            setWord(vocabulary)
                            setPager()

                            dailyWordsList.add(vocabulary.word.toString())
                            count1++
                        } else
                            gotoTestAct()
// when there's less than 4
                    } else {
                        if (count1 < remainingWords.size) {

                            val vocabulary = remainingWords[count1]
                            setWord(vocabulary)
                            setPager()
                            count1++
                        } else
                            gotoTestAct()
                    }
                }, {}).let { App.compositeDisposable.add(it) }
        }
    }

    private fun setInitials(actualWord: String?) {
        App.databaseInstance!!.vocabularyDao().getVocabByWord(actualWord!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ vocabulary ->

                lessonNumber = vocabulary.lesson!!

                setWord(vocabulary)
// adding the first word to daily words
                dailyWordsList.add(vocabulary.word.toString())

                setPager()
// for showing view pager item, the middle one first
                learn_word_viewPager.currentItem = 1

            }, {}).let { App.compositeDisposable.add(it) }
    }

    private fun gotoTestAct() {
        val intent = Intent(this, LearningAskActivity::class.java)
        intent.putExtra(
            Constants.SEND_LIST_TO_TEST,
            dailyWordsList
        )
        intent.putExtra(
            Constants.INTENT_LESSON_NUMBER,
            lessonNumber
        )
        startActivity(intent)
    }

    private fun setPager() {

        onNextPrevClicks()

        learn_word_viewPager.currentItem = 1
        learn_word_viewPager?.addOnPageChangeListener(object :
            ViewPager.OnPageChangeListener {

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

    private fun setWord(vocabulary: Vocabulary) {

        word_txv_actual_word.text = vocabulary.word
        word_txv_pronounce.text = vocabulary.pronunciation
        word_txv_translate.text = vocabulary.persian
        word_txv_defenition.text = vocabulary.definition

        val examplesPer = ArrayList<String>()

        examplesPer.add(vocabulary.pexa.toString())
        examplesPer.add(vocabulary.pexb.toString())
        examplesPer.add(vocabulary.pexc.toString())

        val examplesEng = vocabulary.examples?.split("\n") as ArrayList

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

    override fun onBackPressed() {

        AlertDialog.Builder(this)

            .setMessage("آیا میخواهید از یادگیری کلمات خارج شوید؟")

            .setPositiveButton("بله") { dialog, _ ->

                dialog.dismiss()
                finishAffinity()
                startActivity(Intent(this, HomeActivity::class.java))
            }
            .setNegativeButton("خیر") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}