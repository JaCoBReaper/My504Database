package com.example.learnenglish504.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.learnenglish504.App
import com.example.learnenglish504.App.Companion.compositeDisposable
import com.example.learnenglish504.App.Companion.favWords
import com.example.learnenglish504.R
import com.example.learnenglish504.activities.Constants.Companion.REQ_CODE_TO_WORD_ACTIVITY
import com.example.learnenglish504.adapter.FavAdapter
import com.example.learnenglish504.adapter.IOnFavWordClickListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_favourite.*

class FavouriteActivity : AppCompatActivity(), IOnFavWordClickListener {

    var myAdapter: FavAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favourite)


        App.databaseInstance!!.vocabularyDao().findFavourites()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

                favWords = it

                fav_recycler.layoutManager = LinearLayoutManager(this)
                myAdapter = FavAdapter(this, favWords, this)
                fav_recycler.adapter = myAdapter

                if (favWords.isEmpty()) {
                    txv_toggle.visibility = View.VISIBLE
                } else
                    txv_toggle.visibility = View.GONE

            }, {

            }).let {
                compositeDisposable.add(it)
            }
    }

    override fun onItemClicked(wordID: Int) {
        val intent = Intent(this, WordActivity::class.java)
        intent.putExtra(Constants.INTENT_WORD_ID, wordID)
        startActivityForResult(intent, REQ_CODE_TO_WORD_ACTIVITY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQ_CODE_TO_WORD_ACTIVITY && resultCode == RESULT_OK && data != null) {

            finish()
            startActivity(intent)
        }
    }
}