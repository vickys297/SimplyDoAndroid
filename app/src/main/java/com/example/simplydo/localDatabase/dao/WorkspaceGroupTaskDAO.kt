package com.example.simplydo.localDatabase.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.simplydo.model.WorkspaceGroupTaskModel

@Dao
interface WorkspaceGroupTaskDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWorkspaceDatabase(workspaceGroupTaskModel: WorkspaceGroupTaskModel): Long

    @Query("SELECT * FROM workspaceGroupTask WHERE dtId =:dtId")
    fun getWorkspaceGroupTaskById(dtId: Long): List<WorkspaceGroupTaskModel>

    @Query("SELECT * FROM workspaceGroupTask WHERE taskStatus =:status")
    fun getWorkspaceGroupTaskByStatus(status: Int): List<WorkspaceGroupTaskModel>

    @Query("SELECT * FROM workspaceGroupTask WHERE groupId =:groupTaskId")
    fun getAllWorkspaceGroupTask(groupTaskId: Long): List<WorkspaceGroupTaskModel>

    @Query("SELECT * FROM workspaceGroupTask WHERE groupId =:groupTaskId")
    fun getWorkspaceGroupTaskByGroupId(groupTaskId: Long): List<WorkspaceGroupTaskModel>
}