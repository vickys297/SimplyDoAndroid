package com.example.simplydo.model

import java.io.Serializable

data class SelectorDataModal(
    val value: String,
    var visibleValue: String = "",
    var selected: Boolean,
    val type: Int
) : Serializable

