package info.learncoding.currencyconverter.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import info.learncoding.currencyconverter.data.local.AppDatabase
import info.learncoding.currencyconverter.data.network.ApiClient
import info.learncoding.currencyconverter.data.repository.CurrencyConverterRepository
import info.learncoding.currencyconverter.data.repository.CurrencyConverterRepositoryImp
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object RepositoryModule {

    @Provides
    @Singleton
    fun provideCurrencyConverterRepository(
        apiClient: ApiClient,
        database: AppDatabase
    ): CurrencyConverterRepository {
        return CurrencyConverterRepositoryImp(apiClient, database)
    }
}