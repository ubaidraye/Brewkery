package com.raye.brewkery.data.remote

import com.raye.brewkery.data.model.BrewkeryItem
import com.raye.brewkery.data.model.BrewkeryResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface BrewkeryApiService {

    @GET("data.json")
    suspend fun getBrewkeryData(): BrewkeryResponse

    @GET("api/items/{id}.json")
    suspend fun getItemDetails(@Path("id") itemId: Int): BrewkeryItem

    companion object {
        const val BASE_URL = "https://raw.githubusercontent.com/VivekShah138/Brewkery/main/"
    }
}