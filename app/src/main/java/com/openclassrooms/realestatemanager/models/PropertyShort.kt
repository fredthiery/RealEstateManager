package com.openclassrooms.realestatemanager.models

data class PropertyShort(
        val id: Int,
        val type: String,
        val price: Int,
        val address: String
)