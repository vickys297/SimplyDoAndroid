package com.example.simplydo.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*


@Entity(tableName = "workspace")
data class WorkspaceModel(

    @PrimaryKey
    val orgId: String,

    val accountType: String,

    val title: String,

    val moreDetails: String,

    val createdAt: Long = System.currentTimeMillis(),

    val createdBy: UserIdModel,

    val users: ArrayList<UserModel>,


)


data class WorkspaceGroupsCollectionModel(
    val id: String,
    val name: String,
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val createdBy: UserIdModel,
    val people: ArrayList<UserAccountModel>,
    val task: ArrayList<TodoModel> = arrayListOf()
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

data class UserIdModel(
    val admin: UserModel
)


