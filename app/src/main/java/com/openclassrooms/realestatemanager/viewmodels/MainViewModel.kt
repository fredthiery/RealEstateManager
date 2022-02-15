package com.openclassrooms.realestatemanager.viewmodels

import androidx.lifecycle.*
import com.openclassrooms.realestatemanager.models.Photo
import com.openclassrooms.realestatemanager.models.Property
import com.openclassrooms.realestatemanager.models.PropertyShort
import com.openclassrooms.realestatemanager.repositories.PropertyRepository
import kotlinx.coroutines.launch

class MainViewModel(private val repository: PropertyRepository) : ViewModel() {
    val allProperties: LiveData<List<Property>> = repository.allProperties.asLiveData()
    val allShort: LiveData<List<PropertyShort>> = repository.allShort.asLiveData()

    val propertyId = MutableLiveData<String>()
    val currentProperty = Transformations.switchMap(propertyId) {
        repository.getProperty(it).asLiveData()
    }

    fun getProperty(id: String): LiveData<Property> {
        return repository.getProperty(id).asLiveData()
    }

    fun getPhotos(propertyId: String): LiveData<List<Photo>> {
        return repository.getPhotos(propertyId).asLiveData()
    }

    // TODO: Ajouter une fonction insertOrUpdate ?

    fun insert(property: Property) = viewModelScope.launch {
        repository.insert(property)
    }

    fun update(property: Property) = viewModelScope.launch {
        repository.update(property)
    }

    fun insertPhoto(photo: Photo) = viewModelScope.launch {
        repository.insertPhoto(photo)
    }
}

class MainViewModelFactory(private val repository: PropertyRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}