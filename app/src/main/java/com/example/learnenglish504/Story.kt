package com.example.learnenglish504

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "story")
data class Story(

    @PrimaryKey
    val id: Int?,
    val title: String?,
    val text: String?,
    val per_title: String?,
    val per_text: String?,
    val range: String?

)