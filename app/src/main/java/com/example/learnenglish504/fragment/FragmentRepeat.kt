package com.example.learnenglish504.fragment

import CustomPagerAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.example.learnenglish504.App
import com.example.learnenglish504.R
import com.example.learnenglish504.Vocabulary
import com.example.learnenglish504.activities.Constants
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_repeat_word.*
import kotlinx.android.synthetic.main.rep_word_details_top.*
import java.util.*
import kotlin.concurrent.schedule


class FragmentRepeat(val onGotoNextClick: IOnGotoNextClick) : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_repeat_word, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val repeatedWord = arguments!!.getString(Constants.SEND_WORD_TO_REPEAT_FRAGMENT)

        App.databaseInstance!!.vocabularyDao().getVocabByWord(repeatedWord!!)

            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ vocabulary ->

                setWord(vocabulary)
                setPager()

            }, {}).let { App.compositeDisposable.add(it) }

        rep_frg_btn_go_next.setOnClickListener {

            Timer("WaitingForRepeatingWord", false).schedule(500) {
                onGotoNextClick.gotoFragmentTest()
            }
        }

    }

    private fun setPager() {
        rep_frg_viewPager.currentItem = 1
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

        rep_word_txv_actual_word.text = word.word
        rep_word_txv_pronounce.text = word.pronunciation
        rep_word_txv_translate.text = word.persian
        rep_word_txv_defenition.text = word.definition

        val examplesPer = ArrayList<String>()

        examplesPer.add(word.pexa.toString())
        examplesPer.add(word.pexb.toString())
        examplesPer.add(word.pexc.toString())

        val examplesEng = word.examples?.split("\n") as ArrayList

        val pagerAdapter = CustomPagerAdapter(activity!!, examplesPer, examplesEng)
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

interface IOnGotoNextClick {

    fun gotoFragmentTest()
}