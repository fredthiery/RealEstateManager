package com.openclassrooms.realestatemanager.viewmodels

import androidx.lifecycle.*
import com.google.android.gms.maps.model.LatLng
import com.openclassrooms.realestatemanager.models.*
import com.openclassrooms.realestatemanager.repositories.ListingRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainViewModel(private val repository: ListingRepository) : ViewModel() {

    var searchCriteria = SearchCriteria()
    private val _listings: MutableLiveData<List<ListingFull>> =
        repository.listings.asLiveData() as MutableLiveData<List<ListingFull>>
    val listings: LiveData<List<ListingFull>>
        get() = _listings

    var editListing = ListingFull()

    private val _selectedListing = MutableLiveData<Int?>()
    val selectedListing: LiveData<Int?>
        get() = _selectedListing
    var previousSelected: Int? = null
        private set

    val currentListing = MutableLiveData<Listing>()
    val currentPhotos = MutableLiveData<List<Photo>>()
    val currentPOIs = MutableLiveData<List<PointOfInterest>>()
    val suggestions = MutableLiveData<List<Place>>()

    fun resetSearch() = viewModelScope.launch {
        searchCriteria.clear()
        repository.listings.collect {
            _listings.value = it
        }
    }

    fun setSearchCriteria(term: String? = null) {
        term?.let { searchCriteria.term = it }
        performSearch()
    }

    fun performSearch() = viewModelScope.launch {
        val result = repository.searchListings(searchCriteria)
        _listings.value = result
    }

    fun loadListing(nItem: Int?) = viewModelScope.launch {
        previousSelected = _selectedListing.value
        _selectedListing.value = nItem

        if (nItem != null) {
            listings.value?.get(nItem)?.let { loadListing(it.listing.id) }
        }
    }

    fun loadListing(id: String) = viewModelScope.launch {
        repository.getListing(id).collect { result ->
            currentListing.value = result.listing
            currentPhotos.value = result.photos
            currentPOIs.value = result.pois
            editListing = result.copy()
        }
    }

    fun editListing(listing: Listing) {
        currentListing.value = listing
        currentPhotos.value = ArrayList()
        currentPOIs.value = ArrayList()
        editListing = ListingFull(listing)
    }

    fun add(photo: Photo) {
        editListing.photos.add(photo)
        if (editListing.photos.size == 1) editListing.listing.thumbnailId = photo.id
        currentPhotos.value = editListing.photos
    }

    fun update(photo: Photo) {
        val i = editListing.photos.indexOfFirst { it.id == photo.id }
        editListing.photos[i] = photo.copy()
        editListing.photos = editListing.photos.toMutableList()
        currentPhotos.value = editListing.photos
    }

    fun delete(photo: Photo) = viewModelScope.launch {
        editListing.photos.remove(photo)
        currentPhotos.value = editListing.photos
    }

    fun saveListing() = viewModelScope.launch {
        repository.insert(editListing)
    }

    fun updateAddress(address: String): Boolean {
        return if (address == "" || address.equals(editListing.listing.address, true)) {
            // Address is empty or unchanged
            editListing.listing.address = address
            false
        } else {
            // Address has changed
            editListing.listing.address = address
            viewModelScope.launch {
                // Get address suggestions
                val places = repository.getLocation(address)
                suggestions.value = places
                if (places.size == 1 && address.equals(places[0].toString(), true)) {
                    // There's only one suggestion and address is it
                    editListing.listing.latLng = places[0].toLatLng()
                    updatePOIs(places[0].toLatLng())
                }
            }
            true
        }
    }

    suspend fun findAddress(latLng: LatLng): String {
        return repository.getLocation(latLng)[0].formattedAddress
    }

    private val typeFilter = listOf(
        "airport",
        "amusement_park",
        "bank",
        "church",
        "doctor",
        "fire_station",
        "gas_station",
        "gym",
        "hospital",
        "library",
        "museum",
        "park",
        "police",
        "post_office",
        "restaurant",
        "school",
        "shopping_mall",
        "stadium",
        "store",
        "zoo"
    )

    private fun updatePOIs(latLng: LatLng) = viewModelScope.launch {
        val response = repository.getNearbyPOIs(latLng)
        editListing.pois.clear()
        for (place in response) {
            val type = place.types.find { typeFilter.contains(it) }
            val typeId = typeFilter.indexOf(type)
            if (type != null) editListing.pois.add(
                PointOfInterest(
                    name = place.name,
                    type = typeId,
                    latLng = place.toLatLng(),
                    listingId = editListing.listing.id
                )
            )
        }
        currentPOIs.value = editListing.pois
    }
}

class MainViewModelFactory(private val repository: ListingRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}