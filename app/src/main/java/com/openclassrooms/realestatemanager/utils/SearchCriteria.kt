package com.openclassrooms.realestatemanager.utils

import com.openclassrooms.realestatemanager.R
import java.text.NumberFormat

data class SearchCriteria(
    var term: String = "",
    var area: MinMax = MinMax(R.string.area),
    var price: MinMax = MinMax(R.string.price),
    var rooms: MinMax = MinMax(R.string.number_of_rooms),
    var photos: MinMax = MinMax(R.string.number_of_photos),
    var pointsOfInterest: MutableMap<Int, Boolean> = mutableMapOf()
) {
    fun clear() {
        term = ""
        area.reset()
        price.reset()
        rooms.reset()
        photos.reset()
        pointsOfInterest.clear()
    }
}

data class MinMax(
    var name: Int,
    var min: Int? = null,
    var max: Int? = null
) {
    companion object {
        private val nf: NumberFormat = NumberFormat.getInstance()
    }

    fun getMinString(): String {
        min?.let { return nf.format(it) }
        return ""
    }

    fun getMaxString(): String {
        max?.let { return nf.format(it) }
        return ""
    }

    fun reset() {
        min = null
        max = null
    }
}