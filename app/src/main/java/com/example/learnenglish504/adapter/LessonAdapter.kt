package com.example.learnenglish504.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.learnenglish504.R
import com.example.learnenglish504.Story
import com.example.learnenglish504.Vocabulary

class LessonAdapter(
    private val data: List<Vocabulary>,
//    private val lessonData: List<Story>,
    private val mClickListener: IOnWordClickListener
) :
    RecyclerView.Adapter<LessonAdapter.MyViewHolder>() {


    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val txtEng: TextView = itemView.findViewById(R.id.lesson_txv_eng_word)
        val txtPer: TextView = itemView.findViewById(R.id.lesson_per_translate)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.lesson_row, parent, false)

        return MyViewHolder(view)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        binding(holder, position)

    }

    private fun binding(holder: MyViewHolder, position: Int) {

        holder.txtEng.text = data[position].word.toString()
        holder.txtPer.text = data[position].persian.toString()

        holder.itemView.setOnClickListener {

            mClickListener.onItemClicked(data[position].id!!)
        }
    }
}

interface IOnWordClickListener {

    fun onItemClicked(position: Int)
}