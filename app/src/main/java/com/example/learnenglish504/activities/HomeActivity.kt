package com.example.learnenglish504.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.example.learnenglish504.App.Companion.lessons
import com.example.learnenglish504.R
import com.example.learnenglish504.activities.Constants.Companion.INTENT_VALUE_LessonNumber
import com.example.learnenglish504.adapter.HomeAdapter
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity(), HomeAdapter.IOnLessonClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        recyclerView_home.layoutManager = GridLayoutManager(this, 3)

        val myAdapter = HomeAdapter(lessons, this)
        recyclerView_home.adapter = myAdapter
    }

    override fun onItemClick(position: Int, view: View) {

        val intent = Intent(this, LessonActivity::class.java)

        intent.putExtra(INTENT_VALUE_LessonNumber, position)

        startActivity(intent)
    }
    // Todo close database
}