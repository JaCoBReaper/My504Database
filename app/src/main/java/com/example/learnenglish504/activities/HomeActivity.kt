package com.example.learnenglish504.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.learnenglish504.App
import com.example.learnenglish504.R
import com.example.learnenglish504.adapter.HomeAdapter
import com.example.learnenglish504.adapter.IOnLessonClickListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.home_prog_frame.*
import kotlinx.android.synthetic.main.home_prog_frame.prog_lesson_progbar
import kotlinx.android.synthetic.main.lesson_prog_frame.*

class HomeActivity : AppCompatActivity(), IOnLessonClickListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        getAllLessons()

        setFavFabClick()

// continue learning
        setContinueLearningClick()
    }

    private fun setContinueLearningClick() {

        val startedLearning = checkLearningStarted()

        home_prog_btn_continue.setOnClickListener {

            if (!startedLearning) {
                with(App.learnLessonPref.edit()) {

                    putInt(Constants.PREF_LEARNING_LESSON_NUMBER, 1)
                    putBoolean(Constants.LEARNING_STARTED, true)
                    commit()
                }
            }

            val lessonNumber = App.learnLessonPref.getInt(Constants.PREF_LEARNING_LESSON_NUMBER, 1)
            val intent = Intent(this, LessonActivity::class.java)
            intent.putExtra(Constants.INTENT_LESSON_NUMBER, lessonNumber)
            startActivityForResult(intent, Constants.REQ_CODE_TO_LEARNING_LESSON)
        }
    }

    private fun checkLearningStarted(): Boolean {
        val startedLearning = App.learnLessonPref.getBoolean(Constants.LEARNING_STARTED, false)

        if (!startedLearning) {
            home_prog_btn_continue.setText(Constants.HOME_START_LEARNING)
        } else {
            home_prog_btn_continue.setText(Constants.HOME_CONTINUE_LEARNING)
        }
        return startedLearning
    }

    private fun setFavFabClick() {
        home_fab_fav.setOnClickListener {
            val intent = Intent(this, FavouriteActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getAllLessons() {

        App.databaseInstance!!.storyDao().getAllLessons()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

                recyclerView_home.layoutManager = GridLayoutManager(this, 3)

                var myAdapter = HomeAdapter(it, this)
                recyclerView_home.adapter = myAdapter

            }, {
            }).let { App.compositeDisposable.add(it) }
    }

    override fun onItemClick(lessonNum: Int) {

        val intent = Intent(this, LessonActivity::class.java)
        intent.putExtra(Constants.INTENT_LESSON_NUMBER, lessonNum)
        startActivity(intent)
    }

    private fun setProgressStatus() {
        App.databaseInstance!!.vocabularyDao().findAllLearned()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

                progression_txv_counter.text = it.size.toString()
                prog_lesson_progbar.progress = it.size

            }, {}).let { App.compositeDisposable.add(it) }
    }

    override fun onResume() {

        checkLearningStarted()

        setProgressStatus()

        super.onResume()
    }

    override fun onBackPressed() {

        AlertDialog.Builder(this)

            .setTitle("خارخ شدن از برنامه")
            .setMessage("آیا میخواهید از برنامه خارج شوید؟")
            .setPositiveButton("بله") { dialog, _ ->

                dialog.dismiss()
                finishAffinity()
            }
            .setNegativeButton("خیر") { dialog, _ ->

                dialog.dismiss()
            }
            .show()
    }
}