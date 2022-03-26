package com.openclassrooms.realestatemanager.viewmodels

import androidx.lifecycle.*
import com.google.android.gms.maps.model.LatLng
import com.openclassrooms.realestatemanager.models.*
import com.openclassrooms.realestatemanager.repositories.ListingRepository
import com.openclassrooms.realestatemanager.utils.SearchCriteria
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainViewModel(private val repository: ListingRepository) : ViewModel() {

    // Livedata list of listings displayed in recycler view
    private val _listings: MutableLiveData<List<ListingFull>> = MutableLiveData<List<ListingFull>>()
    val listings: LiveData<List<ListingFull>> get() = _listings

    // Livedata of the listing currently displayed in detail view
    private val _detailListing = MutableLiveData<ListingFull>()
    val detailListing: LiveData<ListingFull> get() = _detailListing

    // The listing currently edited
    var editListing = ListingFull()

    // Livedata of the current models, displayed by edit view
    private val _currentListing = MutableLiveData<Listing>()
    val currentListing: LiveData<Listing> get() = _currentListing
    private val _currentPhotos = MutableLiveData<List<Photo>>()
    val currentPhotos: LiveData<List<Photo>> get() = _currentPhotos
    private val _currentPOIs = MutableLiveData<List<PointOfInterest>>()
    val currentPOIs: LiveData<List<PointOfInterest>> get() = _currentPOIs


    // Livedata of address suggestions
    private val _suggestions = MutableLiveData<List<Place>>()
    val suggestions: LiveData<List<Place>> get() = _suggestions

    // Search criteria
    var searchCriteria = SearchCriteria()

    // List of photos tagged to delete later
    private val photosToDelete = mutableListOf<Photo>()

    init {
        viewModelScope.launch {
            // Once the listings have been loaded, load the details of the first of the list
            val result = repository.listings.first { it.isNotEmpty() }
            _listings.value = result
            _detailListing.value = result[0]
            setCurrentListing(result[0])
        }
    }

    /**
     * Resets the search criteria
     */
    fun resetSearch() = viewModelScope.launch {
        searchCriteria.clear()
        repository.listings.collect { _listings.value = it }
    }

    /**
     * Updates the search term and launches the search
     */
    fun setSearchCriteria(term: String? = null) {
        term?.let { searchCriteria.term = it }
        performSearch()
    }

    /**
     * Performs the search according to searchCriteria
     * Updates livedata
     */
    fun performSearch() = viewModelScope.launch {
        val result = repository.searchListings(searchCriteria)
        _listings.value = result
    }

    /**
     * Loads the listing with id from database
     */
    fun loadDetails(id: Long) = viewModelScope.launch {
        repository.getListing(id).collect {
            _detailListing.value = it
            setCurrentListing(it)
        }
    }

    fun editListing(id: Long? = null ) = viewModelScope.launch {
        if (id != null) repository.getListing(id).collect(this@MainViewModel::setCurrentListing)
        else setCurrentListing(ListingFull())
    }

    /**
     * Sets the provided ListingFull as the current
     *
     * Updates livedatas accordingly
     */
    fun setCurrentListing(result: ListingFull) {
        _currentListing.value = result.listing
        _currentPhotos.value = result.photos
        _currentPOIs.value = result.pois
        _suggestions.value = mutableListOf()
        photosToDelete.clear()
        editListing = result.copy()
    }

    /**
     * Adds the photo
     */
    fun add(photo: Photo) {
        editListing.photos.add(photo)
        if (editListing.photos.size == 1) editListing.listing.thumbnailId = photo.id
        _currentPhotos.value = editListing.photos
    }

    /**
     * Updates the photo
     */
    fun update(photo: Photo) {
        val i = editListing.photos.indexOfFirst { it.id == photo.id }
        editListing.photos[i] = photo.copy()
        editListing.photos = editListing.photos.toMutableList()
        _currentPhotos.value = editListing.photos
    }

    /**
     * Marks the provided photo to delete
     */
    fun delete(photo: Photo) = viewModelScope.launch {
        photosToDelete.add(photo)
        editListing.photos.remove(photo)
        _currentPhotos.value = editListing.photos
    }

    /**
     * Saves the current editListing into room DB
     */
    fun saveListing() = viewModelScope.launch {
        repository.insert(editListing)
        repository.delete(photosToDelete)
        photosToDelete.clear()
        _detailListing.value = editListing
    }

    /**
     * Checks the provided address against Google Maps API
     * and updates the list of suggestions
     * If the address matches the only suggestion, updates latLng and POIs
     * returns false if address is empty or unchanged, true otherwise
     */
    suspend fun updateAddress(address: String): LatLng? {
        if (address == "" || address.equals(editListing.listing.address, true)) {
            // Address is empty or unchanged
            editListing.listing.address = address
            return null
        } else {
            // Address has changed
            editListing.listing.address = address
            // Get address suggestions
            val places = repository.getLocation(address)
            _suggestions.value = places
            if (places.size == 1 && address.equals(places[0].toString(), true)) {
                // There's only one suggestion and address is it
                val latLng = places[0].toLatLng()
                editListing.listing.latLng = latLng
                updatePOIs(latLng)
                return latLng
            }
            return null
        }
    }

    /**
     * Retrieves the nearest address to latLng
     */
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

    /**
     * Retrieves a list of points of interest near latLng
     */
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
        _currentPOIs.value = editListing.pois
    }

    /**
     * Given a list of points of interest and a list of types,
     * returns a map of each type present and its count
     */
    fun getSimplifiedPois(pois: List<PointOfInterest>, types: Array<String>): Map<String, Int> {
        val poiMap = mutableMapOf<String, Int>()
        for (place in pois) {
            // for each POI, increment the count of its type
            val x = poiMap[types[place.type]]
            poiMap[types[place.type]] = (x ?: 0) + 1
        }
        return poiMap
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