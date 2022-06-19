package com.example.simplydo.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.simplydo.model.WorkspaceModel

@Dao
interface WorkspaceDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun createNewWorkspace(workspace: WorkspaceModel)

    @Query("SELECT * FROM workspace")
    fun getMyWorkspace(): List<WorkspaceModel>

    @Query("SELECT * FROM workspace")
    fun getUserFromWorkspace(): WorkspaceModel
}