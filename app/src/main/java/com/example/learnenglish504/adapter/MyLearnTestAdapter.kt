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


class MyLearnTestAdapter(
    val context: Context,
    val list: List<Vocabulary>,
    private val inputWord: Vocabulary
) :
    RecyclerView.Adapter<MyLearnTestAdapter.MyViewHolder>() {

    var clickedOnce = false
    var righAnswerID = 0

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
        val vocabulary = list[position]

//        if (vocabulary.word == inputWord.word)
//            righAnswerID = position

        holder.txtRandomWord.text = vocabulary.persian

        setHolderColorAnim(holder, position)

        holder.txtRandomWord.setOnClickListener {
            if (!clickedOnce) {
                clickedOnce = true

                changeStatus(holder, position)
            }
        }
    }

    fun changeStatus(
        holder: MyViewHolder,
        position: Int
    ) {
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

    private fun onAnswerClick(holder: MyViewHolder, position: Int) {


    }

    fun setHolderColorAnim(holder: MyViewHolder, position: Int) {
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