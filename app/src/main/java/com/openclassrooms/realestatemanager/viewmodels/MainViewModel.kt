package com.openclassrooms.realestatemanager.viewmodels

import androidx.lifecycle.*
import com.google.android.gms.maps.model.LatLng
import com.openclassrooms.realestatemanager.models.Photo
import com.openclassrooms.realestatemanager.models.Listing
import com.openclassrooms.realestatemanager.models.geoCodingResponse.Place
import com.openclassrooms.realestatemanager.repositories.ListingRepository
import kotlinx.coroutines.launch

class MainViewModel(private val repository: ListingRepository) : ViewModel() {
    val listings: LiveData<List<Listing>> = repository.listings.asLiveData()

    val listingId = MutableLiveData<String>()
    val currentListing = Transformations.switchMap(listingId) {
        repository.getListing(it).asLiveData()
    }

    fun getListing(id: String): LiveData<Listing> {
        return repository.getListing(id).asLiveData()
    }

    fun getPhotos(listingId: String): LiveData<List<Photo>> {
        return repository.getPhotos(listingId).asLiveData()
    }

    // TODO: Ajouter une fonction insertOrUpdate ?

    fun insert(listing: Listing) = viewModelScope.launch {
        repository.insert(listing)
    }

    fun update(listing: Listing) = viewModelScope.launch {
        repository.update(listing)
    }

    fun insert(photo: Photo) = viewModelScope.launch {
        repository.insert(photo)
    }

    fun getLocation(address: String): LiveData<List<Place>> {
        return repository.getLocation(address).asLiveData()
    }

    fun getLocation(latLng: LatLng): LiveData<List<Place>> {
        return repository.getLocation(latLng).asLiveData()
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