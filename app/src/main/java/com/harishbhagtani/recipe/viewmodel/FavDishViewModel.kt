package com.harishbhagtani.recipe.viewmodel

import androidx.lifecycle.*
import com.harishbhagtani.recipe.model.database.FavDishRepository
import com.harishbhagtani.recipe.model.entities.FavDish
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException
import java.util.concurrent.Flow

class FavDishViewModel(private val repository: FavDishRepository): ViewModel() {
    fun insert(dish: FavDish) = viewModelScope.launch {
        repository.insertFavDish(dish)
    }

    fun update(dish: FavDish) = viewModelScope.launch {
        repository.updateFavDishData(dish)
    }

    fun delete(dish: FavDish) = viewModelScope.launch {
        repository.deleteFavDish(dish)
    }

    val allDataList: LiveData<List<FavDish>> = repository.allDishesList.asLiveData()

    val favDishesList: LiveData<List<FavDish>> = repository.favDishesList.asLiveData()

    fun filterList(value: String): LiveData<List<FavDish>> = repository.filteredListDishes(value).asLiveData()
}


class FavDishViewModelFactory(private val repository: FavDishRepository) : ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(FavDishViewModel::class.java)){
            return FavDishViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown view model class")
    }

}