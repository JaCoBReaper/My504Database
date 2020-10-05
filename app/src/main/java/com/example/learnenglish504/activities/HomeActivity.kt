package com.example.learnenglish504.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.learnenglish504.App
import com.example.learnenglish504.R
import com.example.learnenglish504.adapter.HomeAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.home_prog_frame.*

class HomeActivity : AppCompatActivity(), HomeAdapter.IOnLessonClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        getAllLessons()
// fab fav
        home_fab_fav.setOnClickListener {
            val intent = Intent(this, FavouriteActivity::class.java)
            startActivity(intent)
        }

// check if learning started
        val startedLearning =
            App.learnLessonPref.getBoolean(Constants.LEARNING_STARTED, false)
//TODO USE THIS BOOLEAN LATER

// continue learning
        home_prog_btn_continue.setOnClickListener {

            // first time start learning
            if (!startedLearning) {
                home_prog_btn_continue.text == Constants.HOME_CONTINUE_LEARNING

                with(App.learnLessonPref.edit()) {

                    putInt(Constants.PREF_LEARNING_LESSON_Number, 1)
                    putBoolean(Constants.LEARNING_STARTED, true)
                    commit()
                }
            }

            val lessonNumber = App.learnLessonPref.getInt(Constants.PREF_LEARNING_LESSON_Number, 1)
// TODO CHANGE THIS VALUE IN SHAREDPREF LATER WHEN LESSON CHANGED

            val intent = Intent(this, LessonActivity::class.java)

//TODO CHANGE LEARNING LESSON LATER
            intent.putExtra(Constants.INTENT_VALUE_LESSON_NUMBER, lessonNumber)
            startActivityForResult(intent, Constants.REQ_CODET_TO_LearningLesson)
        }
    }

    private fun getAllLessons() {
        App.storyDao.getAllLessons()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

                App.lessons = it

                recyclerView_home.layoutManager = GridLayoutManager(this, 3)
                val myAdapter = HomeAdapter(App.lessons, this)
                recyclerView_home.adapter = myAdapter
            }, {

            }).let {
//                App.compositeDisposable.add(it)
            }
    }

    override fun onItemClick(lessonNum: Int) {

        val intent = Intent(this, LessonActivity::class.java)
        intent.putExtra(Constants.INTENT_VALUE_LESSON_NUMBER, lessonNum)
        startActivity(intent)
    }
}