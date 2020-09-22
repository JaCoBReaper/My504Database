package com.example.learnenglish504.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.learnenglish504.R
import com.example.learnenglish504.Story

class HomeAdapter(private val data: List<Story>, private val mClickListener: IOnLessonClickListener) :
    RecyclerView.Adapter<HomeAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val imgLesson: ImageView = itemView.findViewById(R.id.home_row_img)
        val txtRange: TextView = itemView.findViewById(R.id.home_row_txv_from_to)
        val txtLessonNumber: TextView = itemView.findViewById(R.id.home_row_txv_lesson_number)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.home_row, parent, false)

        return MyViewHolder(view)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        binding(holder, position)
    }

    private fun binding(holder: MyViewHolder, position: Int) {

        holder.imgLesson.setImageResource(R.drawable.ic_book_notopened)
        holder.txtLessonNumber.text = data[position].id.toString()
        holder.txtRange.text = data[position].range.toString()

        holder.itemView.setOnClickListener {

            mClickListener.onItemClick(position, holder.itemView)
        }
    }

    interface IOnLessonClickListener {

        fun onItemClick(position: Int, view: View)
    }
}