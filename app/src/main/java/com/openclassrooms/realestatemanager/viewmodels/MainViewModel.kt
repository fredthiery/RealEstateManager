package com.openclassrooms.realestatemanager.viewmodels

import androidx.lifecycle.*
import com.openclassrooms.realestatemanager.models.Property
import com.openclassrooms.realestatemanager.repositories.PropertyRepository
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class MainViewModel(private val repository: PropertyRepository): ViewModel() {
    val allProperties: LiveData<List<Property>> = repository.allProperties.asLiveData()

    fun insert(property: Property) = viewModelScope.launch {
        repository.insert(property)
    }

    fun update(property: Property) = viewModelScope.launch {
        repository.update(property)
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