package com.example.simplydo.model

data class SelectorDataModal(
    val value: String,
    var visibleValue: String = "",
    var selected: Boolean,
    val type: Int
)

