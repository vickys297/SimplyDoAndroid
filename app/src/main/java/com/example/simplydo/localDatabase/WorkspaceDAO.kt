package com.example.simplydo.localDatabase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.simplydo.model.WorkspaceAccountModel

@Dao
interface WorkspaceDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun createNewWorkspace(workspace: WorkspaceAccountModel)

    @Query("SELECT * FROM workspace")
    fun getMyWorkspace(): List<WorkspaceAccountModel>
}