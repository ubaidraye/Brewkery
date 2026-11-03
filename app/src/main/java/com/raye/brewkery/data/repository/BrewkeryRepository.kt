package com.raye.brewkery.data.repository

import com.raye.brewkery.data.model.BrewkeryItem
import com.raye.brewkery.data.model.BrewkeryResponse
import com.raye.brewkery.data.remote.BrewkeryApiService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

interface BrewkeryRepository {
    suspend fun getBrewkeryData(): Result<BrewkeryResponse>
    suspend fun getItemDetails(id: Int): Result<BrewkeryItem>
}

@Singleton
class BrewkeryRepositoryImpl @Inject constructor(
    private val apiService: BrewkeryApiService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : BrewkeryRepository {

    override suspend fun getBrewkeryData(): Result<BrewkeryResponse> = withContext(ioDispatcher) {
        runCatching {
            apiService.getBrewkeryData()
        }
    }

    override suspend fun getItemDetails(id: Int): Result<BrewkeryItem> = withContext(ioDispatcher) {
        runCatching {
            apiService.getItemDetails(id)
        }
    }
}