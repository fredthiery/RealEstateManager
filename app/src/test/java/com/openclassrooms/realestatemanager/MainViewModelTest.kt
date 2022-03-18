package com.openclassrooms.realestatemanager

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.android.gms.maps.model.LatLng
import com.google.common.truth.Truth.assertThat
import com.openclassrooms.realestatemanager.fakes.ListingDaoFake
import com.openclassrooms.realestatemanager.fakes.ServiceFake
import com.openclassrooms.realestatemanager.models.*
import com.openclassrooms.realestatemanager.repositories.ListingRepository
import com.openclassrooms.realestatemanager.viewmodels.MainViewModel
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class MainViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var classUnderTest: MainViewModel

    @Before
    fun setup() {
        classUnderTest = MainViewModel(
            ListingRepository(
                ListingDaoFake(),
                ServiceFake()
            )
        )
    }

    @Test
    fun performSearchTest() = runBlocking {
        classUnderTest.searchCriteria.pointsOfInterest = mutableMapOf(6 to true)
        classUnderTest.performSearch()
        assertThat(classUnderTest.listings.value?.get(0)?.listing?.address).isEqualTo("2 rue quelconque, 75002 Paris")
        classUnderTest.resetSearch()
        assertThat(classUnderTest.listings.value?.get(0)?.listing?.address).isEqualTo("1 rue de nulle part, 75001 Paris")
    }

    @Test
    fun setSearchCriteriaTest() {
        classUnderTest.setSearchCriteria("test")
        assertThat(classUnderTest.searchCriteria.term).isEqualTo("test")
        classUnderTest.resetSearch()
        assertThat(classUnderTest.searchCriteria.term).isEqualTo("")
    }

    @Test
    fun loadListingTest() = runBlocking {
        classUnderTest.loadListing("listing1")
        assertThat(classUnderTest.currentListing.value?.address).isEqualTo("2 rue quelconque, 75002 Paris")
    }

    @Test
    fun setCurrentListingTest() {
        val listing = ListingFull(
            Listing("listing3","Apartment", address = "4 avenue perdue, 75004 Paris"),
            mutableListOf(Photo("photo3",title = "TestPhoto", listingId = "listing3")),
            mutableListOf(PointOfInterest("poi3","TestPOI",3, LatLng(0.0,0.0),"listing3"))
        )
        classUnderTest.setCurrentListing(listing)
        assertThat(classUnderTest.currentListing.value?.address).isEqualTo("4 avenue perdue, 75004 Paris")
        assertThat(classUnderTest.currentPhotos.value?.get(0)?.title).isEqualTo("TestPhoto")
        assertThat(classUnderTest.currentPOIs.value?.get(0)?.name).isEqualTo("TestPOI")
        assertThat(classUnderTest.editListing.listing.address).isEqualTo("4 avenue perdue, 75004 Paris")
    }

    @Test
    fun addAndDeletePhotoTest() = runBlocking{
        val photo = Photo("photo3",title = "TestPhoto", listingId = "listing2")
        // Add photo to editListing
        classUnderTest.add(photo)
        assertThat(classUnderTest.editListing.photos).contains(photo)

        // Save editListing
        classUnderTest.saveListing()
        classUnderTest.loadListing("listing2")
        assertThat(classUnderTest.currentPhotos.value).contains(photo)

        // Delete photo
        classUnderTest.delete(photo)
        assertThat(classUnderTest.editListing.photos).doesNotContain(photo)

        // Save editListing
        classUnderTest.saveListing()
        classUnderTest.loadListing("listing2")
        assertThat(classUnderTest.currentPhotos.value).doesNotContain(photo)
    }

    @Test
    fun findAddressTest() = runBlocking {
        val result = classUnderTest.findAddress(LatLng(0.0,0.0))
        assertThat(result).isEqualTo("Champ de Mars, 5 Av. Anatole France, 75007 Paris")
    }

    @Test
    fun updateAddressTest() = runBlocking {
        val place = Place(
            name = "Tour Eiffel",
            formattedAddress = "Champ de Mars, 5 Av. Anatole France, 75007 Paris",
            types = listOf("amusement_park"),
            geometry = Geometry(Location(48.858370, 2.294481))
        )

        classUnderTest.loadListing("listing0")
        // Address is unchanged
        var result = classUnderTest.updateAddress("1 rue de nulle part, 75001 Paris")
        assertThat(result).isFalse()

        // Address is modified, a suggestion should be thrown
        result = classUnderTest.updateAddress("2 rue quelconque, 75002 Paris")
        assertThat(result).isTrue()
        assertThat(classUnderTest.suggestions.value).contains(place)

        // Address is the same as suggested, add a Point of interest
        result = classUnderTest.updateAddress(place.formattedAddress)
        assertThat(result).isTrue()
        assertThat(classUnderTest.editListing.listing.latLng).isEqualTo(LatLng(48.858370, 2.294481))
        assertThat(classUnderTest.currentPOIs.value?.get(0)?.name).isEqualTo("Tour Eiffel")
    }
}