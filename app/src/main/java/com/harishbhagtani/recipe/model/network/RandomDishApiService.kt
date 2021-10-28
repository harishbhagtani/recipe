package com.harishbhagtani.recipe.model.network

import com.harishbhagtani.recipe.model.entities.RandomDish
import com.harishbhagtani.recipe.utils.Constants
import io.reactivex.rxjava3.core.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class RandomDishApiService {
    private val api = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .build()
        .create(RandomDishApi::class.java)

    fun getRandomDish():Single<RandomDish.Recipes>{
        return api.getRandomDish(Constants.API_KEY_VALUE,Constants.LIMIT_LICENCE_VALUE,Constants.TAG_VALUES,Constants.NUMBER_VALUE)
    }
}