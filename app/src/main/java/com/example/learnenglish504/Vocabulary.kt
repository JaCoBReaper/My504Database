package com.example.learnenglish504

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "vocabulary")
data class Vocabulary(

    @PrimaryKey
    val id: Int?,
    val range: String?,
    val note: String?,
    val word: String?,
    val coding: String?,
    val pronunciation: String?,
    val examples: String?,
    val lesson: Int?,
    val lesson_order: Int?,
    val definition: String?,
    val persian: String?,
    val favorited: Int?,
    val viewed: Int?,
    val is_read: Int?,
    val pexa: String?,
    val pexb: String?,
    val pexc: String?
)