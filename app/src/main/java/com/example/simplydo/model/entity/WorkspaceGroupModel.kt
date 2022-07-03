package com.example.simplydo.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.simplydo.model.UserAccountModel
import com.example.simplydo.model.UserIdModel
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

@Entity(tableName = "workspaceGroups")
data class WorkspaceGroupModel(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "gId")
    val gId: Long = 0,

    @ColumnInfo(name = "workspaceID")
    val workspaceID: Long,

    @ColumnInfo(name = "groupName")
    var name: String,

    @ColumnInfo(name = "groupDescription")
    var description: String = "",

    @ColumnInfo(name = "createdAt")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "createdBy")
    val createdBy: UserIdModel,

    @ColumnInfo(name = "participants")
    var people: ArrayList<UserAccountModel>

) : Serializable {

    fun getCreatedDataText(): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = createdAt
        val dateString = SimpleDateFormat("dd, MMM yyyy", Locale.getDefault()).format(calendar.time)
        return String.format("Created on %s", dateString)
    }

    fun getParticipantsCount(): String {
        return String.format("%d Participants", people.size)
    }
}