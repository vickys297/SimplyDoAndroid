package com.example.simplydo.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "todoTag", indices = [Index(value = ["tag"], unique = true)])
data class TagModel(
    @PrimaryKey
    @ColumnInfo(name = "tag")
    val tagName: String,
)
