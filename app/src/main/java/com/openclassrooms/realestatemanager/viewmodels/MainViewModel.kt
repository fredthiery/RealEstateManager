package com.openclassrooms.realestatemanager.viewmodels

import androidx.lifecycle.*
import com.google.android.gms.maps.model.LatLng
import com.openclassrooms.realestatemanager.models.*
import com.openclassrooms.realestatemanager.repositories.ListingRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainViewModel(private val repository: ListingRepository) : ViewModel() {

    var searchCriteria = SearchCriteria()
    private val _listings: MutableLiveData<List<Listing>> =
        repository.listings.asLiveData() as MutableLiveData<List<Listing>>
    val listings: LiveData<List<Listing>>
        get() = _listings

    var editListing = ListingWithPhotos()
    var selectedItem = 0
        set(n) {
            viewModelScope.launch { repository.listings.collect { loadListing(it[n].id) } }
            field = n
        }
    val currentListing = MutableLiveData<Listing>()
    val currentPhotos = MutableLiveData<List<Photo>>()
    val currentPOIs = MutableLiveData<List<PointOfInterest>>()

    init {
        selectedItem = 0
    }

    fun setSearchCriteria(term: String? = null) {
        if (term != null) searchCriteria.term = term
        performSearch()
    }

    fun performSearch() = viewModelScope.launch {
        repository.searchListings(searchCriteria).collect { result ->
            _listings.value = result
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
        editListing = ListingWithPhotos(listing)
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
        return if (address.equals(editListing.listing.address, true) || address == "") {
            editListing.listing.address = address
            false
        } else {
            editListing.listing.address = address
            viewModelScope.launch {
                val places = repository.getLocation(address)
                if (places.isNotEmpty()) {
                    editListing.listing.latLng
                    updatePOIs(places[0].toLatLng())
                }
            }
            updateSuggestions(address)
            true
        }
    }

    suspend fun findAddress(latLng: LatLng): String {
        return repository.getLocation(latLng)[0].formattedAddress
    }

    val suggestions = MutableLiveData<List<Place>>()
    private fun updateSuggestions(address: String) {
        if (address.length > 8) {
            viewModelScope.launch {
                suggestions.value = repository.getLocation(address)
            }
        } else {
            suggestions.value = ArrayList()
        }
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