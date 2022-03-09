package com.openclassrooms.realestatemanager.viewmodels

import androidx.lifecycle.*
import com.google.android.gms.maps.model.LatLng
import com.openclassrooms.realestatemanager.models.*
import com.openclassrooms.realestatemanager.repositories.ListingRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainViewModel(private val repository: ListingRepository) : ViewModel() {

    var searchCriteria = SearchCriteria()
    private var _listings: MutableLiveData<List<Listing>> =
        repository.listings.asLiveData() as MutableLiveData<List<Listing>>
    val listings: LiveData<List<Listing>>
        get() = _listings

    fun setSearchCriteria(term: String? = null) {
        if (term != null) searchCriteria.term = term
        performSearch()
    }

    fun performSearch() = viewModelScope.launch {
        repository.searchListings(
            searchCriteria.term,
            searchCriteria.area,
            searchCriteria.price,
            searchCriteria.rooms
        ).collect { result ->
            _listings.value = result
        }
    }

    var editListing = ListingWithPhotos()
    val currentListing = MutableLiveData<ListingWithPhotos>()

    fun changeListing(id: String) = viewModelScope.launch {
        repository.getListing(id).collect { result ->

            // TODO utiliser une map avec comme index les id des listings
            val tmpListings = _listings.value!!.toMutableList()
            val prevSelected = tmpListings.indexOf(tmpListings.find{it.selected})
            if (prevSelected != -1) {
                val prevListing = tmpListings[prevSelected]
                prevListing.selected = false
                tmpListings[prevSelected] = prevListing.copy()
            }

            val newSelected = tmpListings.indexOf(tmpListings.find { it.id == result.listing.id })
            val newListing = tmpListings[newSelected]
            newListing.selected = true
            tmpListings[newSelected] = newListing.copy()

            _listings.value = tmpListings.toList()

            currentListing.value = result
            editListing = result.copy()
        }
    }

    fun add(photo: Photo) {
        editListing.photos.add(photo)
        currentListing.value = editListing.copy()
    }

    fun update(photo: Photo) {
        val i = editListing.photos.indexOfFirst{ it.id == photo.id}
        editListing.photos[i] = photo.copy()
        editListing.photos = editListing.photos.toMutableList()
        currentListing.value = editListing.copy()
    }

    fun delete(photo: Photo) = viewModelScope.launch {
        editListing.photos.remove(photo)
        currentListing.value = editListing.copy()
    }

    fun saveListing() = viewModelScope.launch {
        repository.insert(editListing)
    }

    val suggestions = MutableLiveData<List<Place>>()
    fun updateSuggestions(address: String) {
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
    fun updatePOIs(latLng: LatLng) {
        viewModelScope.launch {
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
            currentListing.value = editListing.copy()
        }
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