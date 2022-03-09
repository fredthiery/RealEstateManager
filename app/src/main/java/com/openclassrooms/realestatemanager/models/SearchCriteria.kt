package com.openclassrooms.realestatemanager.models

import com.openclassrooms.realestatemanager.R
import java.text.NumberFormat

data class SearchCriteria(
    var term: String = "",
    var area: MinMax = MinMax(R.string.area),
    var price: MinMax = MinMax(R.string.price),
    var rooms: MinMax = MinMax(R.string.number_of_rooms),
    var photos: MinMax = MinMax(R.string.number_of_photos),
    var pointsOfInterest: List<String> = ArrayList()
)

data class MinMax(
    var name: Int,
    var min: Int = 0,
    var max: Int = Int.MAX_VALUE
) {
    companion object {
        private val nf: NumberFormat = NumberFormat.getInstance()
    }

    fun getMinString(): String {
        return if (min != 0) nf.format(min)
        else ""
    }

    fun getMaxString(): String {
        return if (max != Int.MAX_VALUE) nf.format(max)
        else ""
    }
}