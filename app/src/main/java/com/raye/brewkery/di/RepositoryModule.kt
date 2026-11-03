package com.raye.brewkery.di

import com.raye.brewkery.data.repository.BrewkeryRepository
import com.raye.brewkery.data.repository.BrewkeryRepositoryImpl
import com.raye.brewkery.data.repository.CartRepository
import com.raye.brewkery.data.repository.CartRepositoryImpl
import com.raye.brewkery.domain.ItemPriceCalculator
import com.raye.brewkery.domain.OrderSummaryCalculator
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindBrewkeryRepository(
        impl: BrewkeryRepositoryImpl
    ): BrewkeryRepository

    @Binds
    @Singleton
    abstract fun bindCartRepository(
        impl: CartRepositoryImpl
    ): CartRepository

    companion object {
        @Provides
        @Singleton
        fun provideItemPriceCalculator(): ItemPriceCalculator {
            return ItemPriceCalculator()
        }

        @Provides
        @Singleton
        fun provideOrderSummaryCalculator(): OrderSummaryCalculator {
            return OrderSummaryCalculator()
        }
    }
}