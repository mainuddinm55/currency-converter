package info.learncoding.currencyconverter.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import info.learncoding.currencyconverter.data.model.Conversion

@Dao
interface ConversionDao {

    @Insert
    suspend fun insert(conversions: List<Conversion>)

    @Query("SELECT * FROM Conversion")
    fun getAll(): LiveData<List<Conversion>>

    @Query("SELECT * FROM Conversion ORDER BY createdAt DESC LIMIT 1")
    suspend fun getLast(): List<Conversion>

    /**
     * Calculation done by query
     * Based conversion rate on db from USD to others
     * First convert given currency to USD amount
     * Than based to USD currency rate convert to all other currencies
     */
    @Query(
        """
        SELECT id,:source as source,currency, amount * (SELECT :amount/amount FROM Conversion WHERE currency = :source LIMIT 1) as amount,createdAt FROM Conversion WHERE currency != :source
        UNION 
        SELECT id,:source as source ,'USD' as currency,:amount/amount as amount,createdAt FROM Conversion WHERE currency = :source 
        """
    )
    fun convertCurrency(source: String, amount: Double): LiveData<List<Conversion>>

    @Query("DELETE FROM Conversion")
    suspend fun deleteAll()
}