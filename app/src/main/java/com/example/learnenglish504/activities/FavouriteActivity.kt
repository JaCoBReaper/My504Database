package com.example.learnenglish504.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.learnenglish504.App.Companion.compositeDisposable
import com.example.learnenglish504.App.Companion.favWords
import com.example.learnenglish504.App.Companion.vocabularyDao
import com.example.learnenglish504.R
import com.example.learnenglish504.activities.Constants.Companion.REQ_CODET_TO_WORDACTIVITY
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


        vocabularyDao.findFavourites()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

                favWords = it

                fav_recycler.layoutManager = LinearLayoutManager(this)
                myAdapter = FavAdapter(this, favWords, this)
                fav_recycler.adapter = myAdapter
            }, {

            }).let {
                compositeDisposable.add(it)
            }
    }

    override fun onItemClicked(wordID: Int) {
        val intent = Intent(this, WordActivity::class.java)
        intent.putExtra(Constants.INTENT_VALUE_WORDID, wordID)
        startActivityForResult(intent, REQ_CODET_TO_WORDACTIVITY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQ_CODET_TO_WORDACTIVITY && resultCode == RESULT_OK && data != null) {

            finish()
            startActivity(intent)
        }
    }
}