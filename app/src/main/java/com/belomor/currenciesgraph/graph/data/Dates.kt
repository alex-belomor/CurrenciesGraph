package com.belomor.currenciesgraph.graph.data

data class Dates(
    val date : Long,
    val values : ArrayList<Values>
)

data class Values (
    val name : String,
    val values : Double
)