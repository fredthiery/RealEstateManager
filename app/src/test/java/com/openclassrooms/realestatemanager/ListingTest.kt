package com.openclassrooms.realestatemanager

import com.google.android.gms.maps.model.LatLng
import com.google.common.truth.Truth.assertThat
import com.openclassrooms.realestatemanager.models.Listing
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.*

@RunWith(JUnit4::class)
class ListingTest {

    private val classUnderTest =
        Listing(
            0L,
            "House",
            100000,
            "New York",
            150,
            5,
            3,
            2,
            "Beautiful house",
            "1 rue de nulle part, 75001 Paris",
            LatLng(48.858370, 2.294481),
            onSaleDate = Calendar.Builder().setDate(2022,3,14).build(),
            null,
            "Realtor"
        )

    @Test
    fun getPositionTest() {
        assertThat(classUnderTest.position).isEqualTo(LatLng(48.858370, 2.294481))
        classUnderTest.latLng = null
        assertThat(classUnderTest.position).isEqualTo(LatLng(0.0,0.0))
    }

    @Test
    fun getTitleTest() {
        assertThat(classUnderTest.title).isEqualTo("House")
    }

    @Test
    fun getSnippetTest() {
        assertThat(classUnderTest.snippet).isEqualTo("Beautiful house")
    }

    @Test
    fun equalsTest() {
        val copy = classUnderTest.copy()
        assertThat(classUnderTest).isEqualTo(copy)

        copy.type = "Apartment"
        assertThat(classUnderTest).isNotEqualTo(copy)
    }
}