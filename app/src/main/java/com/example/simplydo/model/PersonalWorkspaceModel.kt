package com.example.simplydo.model

class PersonalWorkspaceModel(
    val createdAt: Long = System.currentTimeMillis(),
    val admin: AccountModel
) {
}