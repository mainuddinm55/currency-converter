package info.learncoding.currencyconverter.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import info.learncoding.currencyconverter.data.model.ConversionRate

@Dao
interface ConversionRateDao {

    @Insert
    suspend fun insert(conversionRates: List<ConversionRate>)

    @Query("SELECT * FROM ConversionRate")
    fun getAll(): LiveData<List<ConversionRate>>

    @Query("SELECT * FROM ConversionRate ORDER BY createdAt DESC LIMIT 1")
    suspend fun getLast(): List<ConversionRate>

    @Query(
        """
        SELECT id,:source as source,currency, rate * (SELECT :amount/rate FROM ConversionRate WHERE currency = :source LIMIT 1) as rate,createdAt FROM ConversionRate WHERE currency != :source
        UNION 
        SELECT id,:source as source ,'USD' as currency,76/rate as rate,createdAt FROM ConversionRate WHERE currency = :source 
        """
    )
    fun convertCurrency(source: String, amount: Double): LiveData<List<ConversionRate>>

    @Query("DELETE FROM ConversionRate")
    suspend fun deleteAll()
}