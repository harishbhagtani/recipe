package com.harishbhagtani.recipe.model.database

import androidx.room.*
import com.harishbhagtani.recipe.model.entities.FavDish
import kotlinx.coroutines.flow.Flow

@Dao
interface FavDishDao {

    @Insert
    suspend fun insertFavDishes(favDish: FavDish)

    @Delete
    suspend fun deleteDish(favDish: FavDish)

    @Query("SELECT * FROM FAV_DISHES_TABLE ORDER BY id")
    fun getAllDishesList(): Flow<List<FavDish>>

    @Update
    suspend fun updateFavDishDetails(favDish: FavDish)

    @Query("SELECT * FROM FAV_DISHES_TABLE WHERE fav_dish = 1")
    fun getFavDishesList():Flow<List<FavDish>>

    @Query("SELECT * FROM FAV_DISHES_TABLE WHERE type = :filterType")
    fun getFilteredList(filterType: String): Flow<List<FavDish>>


}