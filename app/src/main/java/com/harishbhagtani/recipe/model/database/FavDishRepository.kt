package com.harishbhagtani.recipe.model.database

import androidx.annotation.WorkerThread
import com.harishbhagtani.recipe.model.entities.FavDish
import kotlinx.coroutines.flow.Flow

class FavDishRepository(private val favDishDao: FavDishDao) {

    val allDishesList: Flow<List<FavDish>> = favDishDao.getAllDishesList()
    val favDishesList: Flow<List<FavDish>> = favDishDao.getFavDishesList()

    @WorkerThread
    suspend fun insertFavDish(favDish: FavDish){
        favDishDao.insertFavDishes(favDish)
    }

    @WorkerThread
    suspend fun updateFavDishData(favDish: FavDish){
        favDishDao.updateFavDishDetails(favDish)
    }

    @WorkerThread
    suspend fun deleteFavDish(favDish: FavDish){
        favDishDao.deleteDish(favDish)
    }

    fun filteredListDishes(value: String): Flow<List<FavDish>>{
        return favDishDao.getFilteredList(value)
    }
}