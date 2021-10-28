package com.harishbhagtani.recipe.application

import android.app.Application
import com.harishbhagtani.recipe.model.database.FavDishRepository
import com.harishbhagtani.recipe.model.database.FavDishRoomDatabase

class FavDishApplication : Application() {
    private val database by lazy {
        FavDishRoomDatabase.getDatabase(this@FavDishApplication)
    }

    val repository by lazy {
        FavDishRepository(database.favDishDao())
    }
}