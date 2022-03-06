package com.example.simplydo.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*


@Entity(tableName = "workspace")
data class WorkspaceModel(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "wId")
    val wId: Long = 0,

    val orgId: Long,

    val accountType: String,
    val title: String,
    val moreDetails: String,
    val createdAt: Long = System.currentTimeMillis(),
    val createdBy: UserIdModel,
    val accounts: ArrayList<AccountModel>
) : Serializable {

}


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
    val admin: AccountModel
) : Serializable


