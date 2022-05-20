package info.learncoding.currencyconverter.data.network

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import info.learncoding.currencyconverter.data.network.response.ApiResponse

/**
 * [NetworkResourceBounce] abstract class that responsible for fetch data from network or local
 * [T] type of result that you expecting
 */
abstract class NetworkResourceBounce<T> {

    fun asLiveData() = liveData<DataState<T>> {
        emit(DataState.Loading())
        if (shouldFetch(query())) {
            val disposable = emitSource(queryObservable().map { DataState.Loading(it) })
            val fetchedData = fetch()
            disposable.dispose()
            when (fetchedData) {
                is ApiResponse.Error -> {
                    emitSource(queryObservable().map { DataState.Failed(fetchedData.error) })
                }
                is ApiResponse.Success -> {
                    saveFetchResult(fetchedData.data)
                    emitSource(queryObservable().map { DataState.Loaded(it) })
                }
            }
        } else {
            emitSource(queryObservable().map { DataState.Loaded(it) })
        }
    }

    /**
     * This query return a response from database that need to check is there any data contain or not
     * If not then fetch api call
     * If contain then check need to refresh or not with max cache time in minute
     */
    abstract suspend fun query(): T

    /**
     * Return livedata from local database of expecting data
     */
    abstract fun queryObservable(): LiveData<T>

    /**
     * Call api to fetch data from network
     */
    abstract suspend fun fetch(): ApiResponse<T>

    /**
     * Saved data into local db which fetched from network
     */
    abstract suspend fun saveFetchResult(data: T)
    /**
     * check need to data refresh or not
     */
    abstract fun shouldFetch(data: T?): Boolean
}