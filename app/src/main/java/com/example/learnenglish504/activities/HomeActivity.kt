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

        if (startedLearning) {
            home_prog_btn_continue.text == Constants.HOME_CONTINUE_LEARNING
        }


// continue learning
        /*home_prog_btn_continue.setOnClickListener {

            if (home_prog_btn_continue.text == Constants.HOME_START_LEARNING)
                home_prog_btn_continue.text = Constants.HOME_CONTINUE_LEARNING

            App.learnLessonPrefEditor = App.learnLessonPref.edit()

            App.learnLessonPrefEditor.putBoolean(Constants.LEARNING_STARTED, false)
            App.learnLessonPrefEditor.apply()

            val intent = Intent(this, LessonActivity::class.java)

            val lessonNumber = App.learnLessonPref.getInt(Constants.LEARNING_LESSON, 1)


            intent.putExtra(Constants.INTENT_VALUE_LessonNumber, lessonNumber)
            startActivityForResult(intent, Constants.REQ_CODET_TO_LearningLesson)
        }*/


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
                App.compositeDisposable.add(it)
            }
    }

    override fun onItemClick(lessonNum: Int) {

        val intent = Intent(this, LessonActivity::class.java)
        intent.putExtra(Constants.INTENT_VALUE_LessonNumber, lessonNum)
        startActivity(intent)
    }
}