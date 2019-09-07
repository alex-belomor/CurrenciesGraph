package com.belomor.currenciesgraph.graph.data

class ChartValuesArray : ArrayList<Values>() {

    fun getAllMinValue(): Float {
        var minValue: Float = Float.MAX_VALUE
        for (values in this) {
            if (values.visible) {
                if (minValue > values.getMinValue()) {
                    minValue = values.getMinValue()
                }
            }
        }

        return minValue
    }

    fun getAllMaxValue(): Float {
        var maxValue: Float = Float.MIN_VALUE
        for (values in this) {
            if (values.visible) {
                if (maxValue < values.getMaxValue()) {
                    maxValue = values.getMaxValue()
                }
            }
        }

        return maxValue
    }

    fun getAllVisibleMinValue(begin: Int, end: Int): Float {
        var minValue: Float = Float.MAX_VALUE
        for (values in this) {
            if (values.visible) {
                if (minValue > values.getMinValueInRange(begin, end)) {
                    minValue = values.getMinValueInRange(begin, end)
                }
            }
        }

        return minValue
    }

    fun getAllVisibleMaxValue(begin: Int, end: Int): Float {
        var maxValue: Float = Float.MIN_VALUE
        for (values in this) {
            if (values.visible) {
                if (maxValue < values.getMaxValueInRange(begin, end)) {
                    maxValue = values.getMaxValueInRange(begin, end)
                }
            }
        }

        return maxValue
    }
}

data class Values(
    val name: String,
    val color: Int,
    val details: ArrayList<Details>,
    val visible: Boolean = true
) {
    fun getMinValue() = run {
        var minValue = Float.MAX_VALUE
        for (value in details) {
            if (value.value < minValue) {
                minValue = value.value
            }
        }

        minValue
    }

    fun getMaxValue() = run {
        var maxValue = Float.MIN_VALUE
        for (value in details) {
            if (value.value > maxValue) {
                maxValue = value.value
            }
        }

        maxValue
    }

    fun getMinValueInRange(begin : Int, end : Int) = run {
        var minValue = Float.MAX_VALUE
        for (value in begin..end) {
            if (details[value].value < minValue) {
                minValue = details[value].value
            }
        }

        minValue
    }

    fun getMaxValueInRange(begin : Int, end : Int) = run {
        var maxValue = Float.MIN_VALUE
        for (value in begin..end) {
            if (details[value].value > maxValue) {
                maxValue = details[value].value
            }
        }

        maxValue
    }
}

data class Details(
    val date: Long,
    val value: Float
)