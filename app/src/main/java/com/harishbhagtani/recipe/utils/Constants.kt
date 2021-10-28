package com.harishbhagtani.recipe.utils

object Constants {

    const val DISH_TYPE: String = "DishType"
    const val DISH_CATEGORY:String = "DishCategory"
    const val DISH_COOKING_TIME: String = "DishCookingTime"

    const val ALL_ITEMS = "AllItems"
    const val FILTER_SELECTION = "FilterSelection"

    const val DISH_IMAGE_SOURCE_LOCAL: String = "Local"
    const val DISH_IMAGE_SOURCE_ONLINE: String = "Online"

    const val EXTRA_DISH_DETAILS: String = "DishDetails"

    const val BASE_URL = "https://api.spoonacular.com/"
    const val API_ENDPOINT = "recipes/random"
    const val API_KEY_VALUE = "5d4288ee513a46d09729bc94a31626ae"

    const val TAGS = "tags"
    const val API_KEY = "apiKey"
    const val LIMIT_LICENSE = "limitLicense"
    const val NUMBER = "number"

    const val TAG_VALUES = "vegetarian, dessert"
    const val LIMIT_LICENCE_VALUE = true
    const val NUMBER_VALUE = 1

    fun dishTypes():ArrayList<String>{
        val list = ArrayList<String>()
        list.add("Breakfast")
        list.add("Lunch")
        list.add("Snack")
        list.add("Dinner")
        list.add("Salad")
        list.add("Side dish")
        list.add("Dessert")
        list.add("Other")
        return list
    }

    fun dishCategories():ArrayList<String>{
        val list = ArrayList<String>()
        list.add("Pizza")
        list.add("BBQ")
        list.add("Bakery")
        list.add("Burger")
        list.add("Cafe")
        list.add("Chicken")
        list.add("Dessert")
        list.add("Drink")
        list.add("Juice")
        list.add("Sandwiches")
        list.add("Tea & Coffee")
        list.add("Wraps")
        list.add("Others")
        return list
    }

    fun cookingTime():ArrayList<String>{
        val list = ArrayList<String>()
        list.add("10")
        list.add("15")
        list.add("20")
        list.add("30")
        list.add("45")
        list.add("50")
        list.add("60")
        list.add("90")
        list.add("100")
        list.add("120")
        list.add("150")
        list.add("180")
        return list
    }
}