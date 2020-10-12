package com.example.learnenglish504.activities

import CustomPagerAdapter
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.learnenglish504.App
import com.example.learnenglish504.R
import com.example.learnenglish504.Vocabulary
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_repeat_word.*
import kotlinx.android.synthetic.main.rep_word_details_top.*

class RepeatLearningActivity : AppCompatActivity() {

    lateinit var vocabulary: Vocabulary


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_repeat_word)


        // coming from learningTest activity
        val actualWord = intent.getStringExtra(Constants.INTENT_VALUE_WORD_REPEAT_TO_LEARN)
        val viewedList = intent.getStringArrayListExtra(Constants.INTENT_VALUE_WORDS_LIST_TO_REPEAT)

        setPager()

        App.databaseInstance!!.vocabularyDao().getVocabByWord(actualWord!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ vocabulary ->

                setWord(vocabulary)

                // for showing view pager item, the middle one first
                rep_frg_viewPager.currentItem = 1

            }, {}).let { App.compositeDisposable.add(it) }

        rep_frg_btn_go_next.setOnClickListener {

            val intent = Intent(this, LearningAskActivity::class.java)
            intent.putStringArrayListExtra(Constants.INTENT_VALUE_WORDS_LIST_From_REPEAT,viewedList)
            startActivity(intent)

//            val learnTestActivity = LearningTestActivity()
//            learnTestActivity.beginTest()
        }
    }

    private fun setPager() {

        onNextPrevClicks()

        rep_frg_viewPager.currentItem = 1
        rep_frg_viewPager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                if (position == 0) {
                    rep_learn_detail_img_prev.visibility = View.INVISIBLE;
                } else {
                    rep_learn_detail_img_prev.visibility = View.VISIBLE;
                }
                if (position < rep_frg_viewPager.adapter!!.count - 1) {
                    rep_learn_detail_img_next.visibility = View.VISIBLE;
                } else {
                    rep_learn_detail_img_next.visibility = View.INVISIBLE;
                }
            }

            override fun onPageSelected(position: Int) {}
        })
    }

    private fun setWord(word: Vocabulary) {
//        Log.i(Constants.LogTag, "from rep: ${word.word}")

        rep_word_txv_actual_word.text = word.word
        rep_word_txv_pronounce.text = word.pronunciation
        rep_word_txv_translate.text = word.persian
        rep_word_txv_defenition.text = word.definition

        val examplesPer = ArrayList<String>()

        examplesPer.add(word.pexa.toString())
        examplesPer.add(word.pexb.toString())
        examplesPer.add(word.pexc.toString())

        val examplesEng = word.examples?.split("\n") as ArrayList
// TODO not filling the word i passed
        val pagerAdapter = CustomPagerAdapter(this, examplesPer, examplesEng)
        rep_frg_viewPager.adapter = pagerAdapter
    }

    private fun onNextPrevClicks() {

        rep_learn_detail_img_next.setOnClickListener {
            rep_frg_viewPager.setCurrentItem(
                getItem(1),
                true
            )
        }

        rep_learn_detail_img_prev.setOnClickListener {
            rep_frg_viewPager.setCurrentItem(
                getItem(-1),
                true
            )
        }
    }

    private fun getItem(i: Int): Int = rep_frg_viewPager.currentItem + i
}



