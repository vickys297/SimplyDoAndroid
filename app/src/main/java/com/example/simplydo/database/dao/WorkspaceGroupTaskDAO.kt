package com.example.simplydo.database.dao

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*
import com.example.simplydo.model.privateWorkspace.WorkspaceGroupTaskModel

@Dao
interface WorkspaceGroupTaskDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWorkspaceDatabase(workspaceGroupTaskModel: WorkspaceGroupTaskModel): Long

    @Query("SELECT * FROM workspaceGroupTask WHERE dtId =:dtId")
    fun getWorkspaceGroupTaskById(dtId: Long): WorkspaceGroupTaskModel

    @Query("SELECT * FROM workspaceGroupTask WHERE taskStatus =:status")
    fun getWorkspaceGroupTaskByStatus(status: Int): List<WorkspaceGroupTaskModel>

    @Query("SELECT * FROM workspaceGroupTask WHERE groupId =:groupTaskId")
    fun getAllWorkspaceGroupTask(groupTaskId: Long): PagingSource<Int, WorkspaceGroupTaskModel>

    @Query("SELECT * FROM workspaceGroupTask WHERE groupId =:groupTaskId")
    fun getAllWorkspaceGroupTaskInLiveData(groupTaskId: Long): LiveData<List<WorkspaceGroupTaskModel>>

    @Query("SELECT * FROM workspaceGroupTask WHERE groupId =:groupTaskId")
    fun getWorkspaceGroupTaskByGroupId(groupTaskId: Long): List<WorkspaceGroupTaskModel>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateWorkspaceTaskData(workspaceGroupTaskModel: WorkspaceGroupTaskModel): Int

    @Delete
    fun deleteWorkspaceTaskById(task: WorkspaceGroupTaskModel): Int

    @Query(value = "SELECT * FROM workspaceGroupTask WHERE workspaceId = :workspaceId AND groupId = :groupId AND title LIKE '%'||:searchFilterText||'%' OR todo LIKE '%'||:searchFilterText||'%' OR taskParticipants LIKE '%'||:searchFilterText||'%'")
    fun workspaceSearchTask(
        searchFilterText: String,
        workspaceId: Long,
        groupId: Long
    ): PagingSource<Int, WorkspaceGroupTaskModel>
}