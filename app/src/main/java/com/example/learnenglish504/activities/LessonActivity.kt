package com.example.learnenglish504.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.learnenglish504.App.Companion.lessons
import com.example.learnenglish504.App.Companion.vocabularyDao
import com.example.learnenglish504.R
import com.example.learnenglish504.activities.Constants.Companion.INTENT_VALUE_LessonNumber
import com.example.learnenglish504.activities.Constants.Companion.INTENT_VALUE_WordID
import com.example.learnenglish504.adapter.IOnWordClickListener
import com.example.learnenglish504.adapter.LessonAdapter
import kotlinx.android.synthetic.main.activity_lesson.*

class LessonActivity : AppCompatActivity(), IOnWordClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lesson)


        // coming from homeAdapter
        val lessonNumber = intent.getIntExtra(INTENT_VALUE_LessonNumber, 0)

        lesson_txv_title_number.text = lessons[lessonNumber].id.toString()
        lesson_txv_range.text = lessons[lessonNumber].range

        lesson_recycler.layoutManager = LinearLayoutManager(this)

        Thread {
            // we add +1 because database starts with 1 lesson not 0
            val wordsList = vocabularyDao.getWordsByLesson(lessonNumber + 1)

            val myAdapter = LessonAdapter(wordsList, this)
            lesson_recycler.adapter = myAdapter
        }.start()
    }

    override fun onItemClicked(position: Int) {

        val intent = Intent(this, WordActivity::class.java)

        intent.putExtra(INTENT_VALUE_WordID, position)

        startActivity(intent)
    }
}