package com.example.simplydo.database.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.example.simplydo.model.entity.WorkspaceGroupModel

@Dao
interface WorkspaceGroupDAO {

    @Query("SELECT * FROM workspaceGroups")
    fun getAllWorkSpaceGroups(): List<WorkspaceGroupModel>

    @Query("SELECT * FROM workspaceGroups WHERE workspaceID = :workspaceID")
    fun getWorkSpaceGroupByWorkspaceId(workspaceID: Long): PagingSource<Int, WorkspaceGroupModel>

    @Query("SELECT * FROM workspaceGroups WHERE gId = :workspaceGroupId")
    fun getWorkspaceGroupById(workspaceGroupId: Long): WorkspaceGroupModel

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNewWorkspaceGroup(workspaceGroupModel: WorkspaceGroupModel): Long

    @Query("SELECT COUNT(*) FROM workspaceGroups WHERE workspaceID = :workspaceID")
    fun getWorkspaceGroupTaskCount(workspaceID: Long): Int

    @Delete
    fun deleteWorkspaceGroup(item: WorkspaceGroupModel)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateWorkspaceGroup(workspaceGroupModel: WorkspaceGroupModel)
}