package com.example.learnenglish504.activities

import CustomPagerAdapter
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.learnenglish504.App
import com.example.learnenglish504.App.Companion.compositeDisposable
import com.example.learnenglish504.R
import com.example.learnenglish504.Vocabulary
import com.example.learnenglish504.activities.Constants.Companion.INTENT_WORD_ID
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_word.*
import kotlinx.android.synthetic.main.word_details_top.*
import java.util.*
import kotlin.collections.ArrayList


class WordActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private var isFavChanged = false
    private var wordID = 0
    lateinit var wordDao: Vocabulary
    private var tts: TextToSpeech? = null
    private var buttonSpeak_us: ImageView? = null
    private var buttonSpeak_uk: ImageView? = null


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_word)


// coming from LessonAdapter
        wordID = intent.getIntExtra(INTENT_WORD_ID, 0)

        setWord()

        onButtonsClick()

        setPager()

        buttonSpeak_us = this.word_btn_pronounce_us
//        buttonSpeak_uk = this.word_btn_pronounce_uk

        buttonSpeak_us!!.isEnabled = false;
//        buttonSpeak_uk!!.isEnabled = false;

        tts = TextToSpeech(this, this)

        word_btn_pronounce_us!!.setOnClickListener { speakOut() }
//        word_btn_pronounce_uk!!.setOnClickListener { speakOut() }

    }

    private fun speakOut() {

        val text = word_txv_actual_word.text.toString()
        tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")

        Toast.makeText(this, "clicked", Toast.LENGTH_SHORT).show()
    }

    private fun setPager() {
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

    private fun onButtonsClick() {
        word_fab_return.setOnClickListener {
            checkDataChanged()
        }

        // fav button behaviour
        word_fab_favourite.setOnClickListener {

            isFavChanged = true

            App.databaseInstance!!.vocabularyDao().checkFavourite(wordDao.word!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->

                    if (result == 0) {

                        App.databaseInstance!!.vocabularyDao().setFavourite(1, wordDao.word!!)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({

                                word_fab_favourite.setImageResource(R.drawable.ic_is_favorite)

                            }, {}).let { compositeDisposable.add(it) }

                    } else {

                        App.databaseInstance!!.vocabularyDao().setFavourite(0, wordDao.word!!)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({

                                word_fab_favourite.setImageResource(R.drawable.ic_not_favorite)

                            }, {}).let { compositeDisposable.add(it) }

                    }
                }, {

                }).let { compositeDisposable.add(it) }
        }
    }

    private fun setWord() {
        App.databaseInstance!!.vocabularyDao().getVocabById(wordID)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                wordDao = it
                setWord(wordDao)

                // for showing view pager item, the middle one first
                word_viewPager.currentItem = 1

            }, {}).let { compositeDisposable.add(it) }
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

    private fun setWord(wordDao: Vocabulary) {

        word_txv_actual_word.text = wordDao.word
        word_txv_pronounce.text = wordDao.pronunciation
        word_txv_translate.text = wordDao.persian
        word_txv_defenition.text = wordDao.definition

        if (wordDao.favorited == 1)
            word_fab_favourite.setImageResource(R.drawable.ic_is_favorite)
        else
            word_fab_favourite.setImageResource(R.drawable.ic_not_favorite)

        val examplesPer = ArrayList<String>()

        examplesPer.add(wordDao.pexa.toString())
        examplesPer.add(wordDao.pexb.toString())
        examplesPer.add(wordDao.pexc.toString())

        val examplesEng = wordDao.examples?.split("\n") as ArrayList

        val pagerAdapter = CustomPagerAdapter(this, examplesPer, examplesEng)
        word_viewPager.adapter = pagerAdapter
    }

    private fun getItem(i: Int): Int = word_viewPager.currentItem + i

    private fun checkDataChanged() {
        if (isFavChanged) {

            val output = Intent()
            output.putExtra("ChangedFavID", wordID)
            setResult(Activity.RESULT_OK, output)
        }
        finish()
    }

    override fun onBackPressed() {

        checkDataChanged()
    }

    override fun onInit(status: Int) {

        if (status == TextToSpeech.SUCCESS) {
            // set US English as language for tts
            val resultUS = tts!!.setLanguage(Locale.US)
            val resultUK = tts!!.setLanguage(Locale.UK)

            // US
            if (resultUS == TextToSpeech.LANG_MISSING_DATA || resultUS == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "The Language specified is not supported!")
            } else {
                buttonSpeak_us!!.isEnabled = true
            }
            // UK
            /*if (resultUK == TextToSpeech.LANG_MISSING_DATA || resultUK == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "The Language specified is not supported!")
            } else {
                buttonSpeak_uk!!.isEnabled = true
            }*/

        } else {
            Log.e("TTS", "Initialization Failed!")
        }

    }


    override fun onDestroy() {
        // Shutdown TTS
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
        super.onDestroy()
    }
}