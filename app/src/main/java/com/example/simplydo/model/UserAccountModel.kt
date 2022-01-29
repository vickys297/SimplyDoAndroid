package com.example.simplydo.model

data class UserAccountModel(
    val user: UserModel,
    val role: ArrayList<UserRolesModel>
)

data class UserRolesModel(
    val name: String,
    val constrain: ConstrainsModal
)