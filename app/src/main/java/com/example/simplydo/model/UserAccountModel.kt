package com.example.simplydo.model

import androidx.room.Ignore
import java.io.Serializable

data class UserAccountModel(

    val user: UserModel,

    @Ignore
    var selected: Boolean = false,

    val role: ArrayList<UserRolesModel>
) : Serializable {

}

data class UserRolesModel(
    val name: String,
    val constrain: ConstrainsModal
)