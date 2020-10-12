package com.example.learnenglish504.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.learnenglish504.R
import com.example.learnenglish504.Vocabulary


class LearnTestAdapter(
    private val context: Context,
    private val onAnswerClick: IOnAnswerClick,
    private val list: List<Vocabulary>,
    private val inputWord: Vocabulary
) :
    RecyclerView.Adapter<LearnTestAdapter.MyViewHolder>() {

    var clickedOnce = false


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val txtRandomWord: TextView = itemView.findViewById(R.id.learn_test_txv_word)
        val cardView: CardView = itemView.findViewById(R.id.learn_test_card)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.learn_test_card_item, parent, false)


        return MyViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.txtRandomWord.text = list[position].persian

        setHolderColorAnim(holder, position)

        holder.cardView.setOnClickListener {
            if (!clickedOnce) {
                clickedOnce = true

                if (list[position].word == inputWord.word)
                    onAnswerClick.onCorrectAnswerClick(inputWord)
                else
                    onAnswerClick.onWrongAnswerClick(inputWord)

                changeStatus(holder, position)
            }
        }
    }

    private fun changeStatus(holder: MyViewHolder, position: Int) {
        if (inputWord.persian == holder.txtRandomWord.text) {
            list[position].status = 1

        } else {
            list[position].status = -1

            list.forEach {
                if (it.word == inputWord.word) {
                    it.status = 1
                }
            }
        }
        notifyDataSetChanged()
    }

    private fun setHolderColorAnim(holder: MyViewHolder, position: Int) {
        if (list[position].status == 1) {
            holder.txtRandomWord.setTextColor(context.resources.getColor(R.color.myColor_white))
            holder.cardView.setCardBackgroundColor(context.resources.getColor(R.color.myColor_green_light))
            setAnimation(holder)

        } else if (list[position].status == -1) {
            holder.txtRandomWord.setTextColor(context.resources.getColor(R.color.myColor_white))
            holder.cardView.setCardBackgroundColor(context.resources.getColor(R.color.myColor_red_light))
        }
    }

    private fun setAnimation(holder: MyViewHolder) {
        val animZoomIn = AnimationUtils.loadAnimation(context, R.anim.anim_scale_2x)

        holder.cardView.startAnimation(animZoomIn)
    }
}

interface IOnAnswerClick {
    fun onCorrectAnswerClick(word: Vocabulary)

    fun onWrongAnswerClick(word: Vocabulary)
}