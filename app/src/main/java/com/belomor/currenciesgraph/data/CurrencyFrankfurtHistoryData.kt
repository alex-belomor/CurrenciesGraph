package com.belomor.currenciesgraph.data

data class CurrencyFrankfurtHistoryData(
    val amount: Int,
    val base: String,
    val start_date: String,
    val end_date: String,
    val rates: Any
)