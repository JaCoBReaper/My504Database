package com.example.learnenglish504.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.example.learnenglish504.App
import com.example.learnenglish504.App.Companion.lessons
import com.example.learnenglish504.App.Companion.storyDao
import com.example.learnenglish504.R
import com.example.learnenglish504.activities.Constants.Companion.INTENT_VALUE_LessonNumber
import com.example.learnenglish504.adapter.HomeAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity(), HomeAdapter.IOnLessonClickListener{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)


        storyDao.getAllLessons()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

                lessons = it

                recyclerView_home.layoutManager = GridLayoutManager(this, 3)
                val myAdapter = HomeAdapter(lessons, this)
                recyclerView_home.adapter = myAdapter

            }, {

            }).let {
                App.compositeDisposable.add(it)
            }
    }

    override fun onItemClick(lessonNum: Int) {

        val intent = Intent(this, LessonActivity::class.java)
        intent.putExtra(INTENT_VALUE_LessonNumber, lessonNum)
        startActivity(intent)
    }
    // Todo close database
}