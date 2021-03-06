package com.example.simplydo.database.dao

import androidx.room.*
import com.example.simplydo.model.TagModel


@Dao
interface TagDAO {

    @Query("Select * from todoTag")
    fun getAllTag(): List<TagModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTag(tagModel: TagModel)

    @Delete
    fun removeTag(tagModel: TagModel)

}