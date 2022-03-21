package com.openclassrooms.realestatemanager

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.android.gms.maps.model.LatLng
import com.google.common.truth.Truth.assertThat
import com.openclassrooms.realestatemanager.fakes.ListingDaoFake
import com.openclassrooms.realestatemanager.fakes.ServiceFake
import com.openclassrooms.realestatemanager.models.*
import com.openclassrooms.realestatemanager.repositories.ListingRepository
import com.openclassrooms.realestatemanager.utils.MinMax
import com.openclassrooms.realestatemanager.utils.SearchCriteria
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
class ListingRepositoryTest {

    private lateinit var classUnderTest: ListingRepository

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        classUnderTest = ListingRepository(ListingDaoFake(),ServiceFake())
    }

    @Test
    fun searchListingsTest() = runBlocking {
        var criteria = SearchCriteria(pointsOfInterest = mutableMapOf(6 to true))
        var result = classUnderTest.searchListings(criteria)
        assertThat(result[0].listing.address).isEqualTo("2 rue quelconque, 75002 Paris")

        criteria = SearchCriteria(photos = MinMax(R.string.number_of_photos,2,2))
        result = classUnderTest.searchListings(criteria)
        assertThat(result[0].listing.address).isEqualTo("3 rue inconnue, 75003 Paris")
    }

    @Test
    fun getListingTest() = runBlocking {
        val result = classUnderTest.getListing("listing0").first()
        assertThat(result.listing.type).isEqualTo("House")
    }

    @Test
    fun insertListingTest() = runBlocking {
        val listing = ListingFull(
            Listing("listing3","Apartment", address = "4 avenue perdue, 75004 Paris"),
            mutableListOf(Photo("photo3",title = "TestPhoto", listingId = "listing3")),
            mutableListOf(PointOfInterest("poi3","TestPOI",3, LatLng(0.0,0.0),"listing3"))
        )
        classUnderTest.insert(listing)
        val result = classUnderTest.getListing("listing3").first()
        assertThat(result.listing.address).isEqualTo("4 avenue perdue, 75004 Paris")
    }

    @Test
    fun getLocationTest() = runBlocking {
        var result = classUnderTest.getLocation("test")
        assertThat(result[0].formattedAddress).isEqualTo("Champ de Mars, 5 Av. Anatole France, 75007 Paris")

        result = classUnderTest.getLocation(LatLng(48.858370, 2.294481))
        assertThat(result[0].formattedAddress).isEqualTo("Champ de Mars, 5 Av. Anatole France, 75007 Paris")
    }

    @Test
    fun getNearbyPOIsTest() = runBlocking {
        val result = classUnderTest.getNearbyPOIs(LatLng(48.858370, 2.294481))
        assertThat(result[0].formattedAddress).isEqualTo("Champ de Mars, 5 Av. Anatole France, 75007 Paris")
    }
}