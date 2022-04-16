package com.example.simplydo.localDatabase.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.simplydo.model.entity.WorkspaceGroupModel

@Dao
interface WorkspaceGroupDAO {

    @Query("SELECT * FROM workspaceGroups")
    fun getAllWorkSpaceGroups(): List<WorkspaceGroupModel>

    @Query("SELECT * FROM workspaceGroups WHERE workspaceID = :workspaceID")
    fun getWorkSpaceGroupByWorkspaceId(workspaceID: Long): PagingSource<Int, WorkspaceGroupModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNewWorkspaceGroup(workspaceGroupModel: WorkspaceGroupModel): Long

    @Query("SELECT COUNT(*) FROM workspaceGroups WHERE workspaceID = :workspaceID")
    fun getWorkspaceGroupTaskCount(workspaceID: Long): Int
}