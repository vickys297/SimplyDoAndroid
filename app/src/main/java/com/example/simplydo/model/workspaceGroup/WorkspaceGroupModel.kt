package com.example.simplydo.model.workspaceGroup

import com.example.simplydo.model.AccountModel

data class WorkspaceGroupModel(
    val result: String,
    val message: String,
    val data: ArrayList<AccountModel>,
    val total: Int
) {

}
