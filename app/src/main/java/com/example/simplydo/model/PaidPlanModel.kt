package com.example.simplydo.model

import java.util.*

data class PaidPlanModel(
    val id: String,
    val period: String,
    val price: String,
    val textDescription: String,
    val trailPeriodText: String,
    val trialPeriod: Int,
    val priority: Boolean,
    val currency: Currency,
    var selected: Boolean = false
) {
    fun getPriceDetails(): String {
        return String.format("%s %s/%s", currency.symbol, price, period)
    }
}
