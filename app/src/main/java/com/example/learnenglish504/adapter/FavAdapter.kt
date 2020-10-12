package com.example.learnenglish504.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.learnenglish504.R
import com.example.learnenglish504.Vocabulary
import com.example.learnenglish504.database.MyDatabase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


class FavAdapter(
    context: Context,
    private val data: List<Vocabulary>,
    private val mClickListener: IOnFavWordClickListener
) :
    RecyclerView.Adapter<FavAdapter.MyViewHolder>() {

    private val compositeDisposable = CompositeDisposable()
    private val databaseInstance = MyDatabase.getDatabaseInstance(context)
    private val vocabularyDao = databaseInstance!!.vocabularyDao()

    private lateinit var mHolder: MyViewHolder

    var favFlag = 0

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val txtEng: TextView = itemView.findViewById(R.id.lesson_txv_eng_word)
        val txtPer: TextView = itemView.findViewById(R.id.lesson_per_translate)
        val imgFav: ImageView = itemView.findViewById(R.id.lesson_img_favourite)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.lesson_row, parent, false)

        mHolder = MyViewHolder(view)
        return mHolder
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) = binding(holder, position)

    private fun binding(holder: MyViewHolder, position: Int) {

        holder.txtEng.text = data[position].word.toString()
        holder.txtPer.text = data[position].persian.toString()

        val actualWord = data[position].word!!

        changeFavStatus(actualWord, holder)

        holder.itemView.setOnClickListener {

            mClickListener.onItemClicked(data[position].id!!)
        }
    }

    private fun changeFavStatus(
        actualWord: String,
        holder: MyViewHolder
    ) {
        vocabularyDao.checkFavourite(actualWord)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ isFav ->
                favFlag = 1
                if (isFav == 1) {

                    holder.imgFav.setImageResource(R.drawable.ic_is_favorite)

                } else {
                    favFlag = 0
                    holder.imgFav.setImageResource(R.drawable.ic_not_favorite)
                }
            }, {

            }).let { compositeDisposable.add(it) }
    }

    fun updateFav(wordID: Int) {

        vocabularyDao.getVocabById(wordID)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ wordDao ->

                if (wordDao.favorited == 1) {

                    mHolder.imgFav.setImageResource(R.drawable.ic_is_favorite)

                } else {
                    favFlag = 0
                    mHolder.imgFav.setImageResource(R.drawable.ic_not_favorite)
                }
            }, {

            }).let { compositeDisposable.add(it) }

        notifyDataSetChanged()
    }
}

interface IOnFavWordClickListener {

    fun onItemClicked(position: Int)
}
