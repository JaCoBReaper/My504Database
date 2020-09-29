package com.example.learnenglish504.activities

import CustomPagerAdapter
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.learnenglish504.App.Companion.compositeDisposable
import com.example.learnenglish504.App.Companion.vocabularyDao
import com.example.learnenglish504.R
import com.example.learnenglish504.Vocabulary
import com.example.learnenglish504.activities.Constants.Companion.INTENT_VALUE_WORDID
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_word.*
import kotlinx.android.synthetic.main.word_details_top.*


class WordActivity : AppCompatActivity() {

    private var isFavChanged = false
    private var wordID = 0

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_word)


// coming from LessonAdapter
        wordID = intent.getIntExtra(INTENT_VALUE_WORDID, 0)
        lateinit var wordDao: Vocabulary

// getting and setting word
        vocabularyDao.getWordDetailsByID(wordID)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                wordDao = it
                setWord(wordDao)

                // for showing view pager item, the middle one first
                word_viewPager.currentItem = 1

            }, {}).let { compositeDisposable.add(it) }

        word_fab_return.setOnClickListener {
            checkDataChanged()
        }

// fav button behaviour
        word_fab_favourite.setOnClickListener {

            isFavChanged = true

            vocabularyDao.checkFavourite(wordDao.word!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->

                    if (result == 0) {

                        vocabularyDao.setFavourite(1, wordDao.word!!)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({

                                word_fab_favourite.setImageResource(R.drawable.ic_is_favorite)

                            }, {}).let { compositeDisposable.add(it) }

                    } else {

                        vocabularyDao.setFavourite(0, wordDao.word!!)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({

                                word_fab_favourite.setImageResource(R.drawable.ic_not_favorite)

                            }, {}).let { compositeDisposable.add(it) }

                    }
                }, {

                }).let { compositeDisposable.add(it) }
        }

// learn progress
        /*word_txv_learned.setOnClickListener {
            if (word_txv_learned.currentTextColor == resources.getColor(R.color.myColor_red_light)) {

                wordLearned = wordPref.getInt(NUM_WORDS_LEARNED, 120) + 1
                wordPrefEditor.putInt(NUM_WORDS_LEARNED, wordLearned)

                word_txv_learned.setTextColor(resources.getColor(R.color.myColor_green_light))
                Toast.makeText(this, "$wordLearned", Toast.LENGTH_SHORT).show()
            }
        }*/

        word_txv_learned.setOnClickListener {

            if (word_txv_learned.currentTextColor == resources.getColor(R.color.myColor_red)) {

                word_txv_learned.setTextColor(resources.getColor(R.color.myColor_green_light))
                vocabularyDao.setLearned(1, wordDao.word!!)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({}, {}).let { compositeDisposable.add(it) }
            }
        }

// pager
        onNextPrevClicks()

        word_viewPager?.addOnPageChangeListener(
            object : ViewPager.OnPageChangeListener {

                override fun onPageScrollStateChanged(state: Int) {}

                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    if (position == 0) {
                        word_detail_imgb_prev.visibility = View.INVISIBLE;
                    } else {
                        word_detail_imgb_prev.visibility = View.VISIBLE;
                    }
                    if (position < word_viewPager.adapter!!.count - 1) {
                        word_detail_imgb_next.visibility = View.VISIBLE;
                    } else {
                        word_detail_imgb_next.visibility = View.INVISIBLE;
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

        if (word.favorited == 1)
            word_fab_favourite.setImageResource(R.drawable.ic_is_favorite)
        else
            word_fab_favourite.setImageResource(R.drawable.ic_not_favorite)

        if (word.is_read == 1)
            word_txv_learned.setTextColor(resources.getColor(R.color.myColor_green_light))
        else
            word_txv_learned.setTextColor(resources.getColor(R.color.myColor_red))


        val examplesPer = ArrayList<String>()

        examplesPer.add(word.pexa.toString())
        examplesPer.add(word.pexb.toString())
        examplesPer.add(word.pexc.toString())

        val examplesEng = word.examples?.split("\n") as ArrayList

        val pagerAdapter = CustomPagerAdapter(this, examplesPer, examplesEng)
        word_viewPager.adapter = pagerAdapter
    }

    private fun onNextPrevClicks() {

        word_detail_imgb_next.setOnClickListener { word_viewPager.setCurrentItem(getItem(1), true) }

        word_detail_imgb_prev.setOnClickListener {
            word_viewPager.setCurrentItem(
                getItem(-1),
                true
            )
        }
    }

    private fun getItem(i: Int): Int = word_viewPager.currentItem + i

    override fun onBackPressed() {

        checkDataChanged()
    }

    private fun checkDataChanged() {
        if (isFavChanged) {

            val output = Intent()
            output.putExtra("ChangedFavID", wordID)
            setResult(Activity.RESULT_OK, output)
        }
        finish()
    }
}