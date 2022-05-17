package info.learncoding.currencyconverter.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import info.learncoding.currencyconverter.data.local.AppDatabase.Companion.DATABASE_VERSION
import info.learncoding.currencyconverter.data.local.dao.ConversionRateDao
import info.learncoding.currencyconverter.data.local.dao.CurrencyDao
import info.learncoding.currencyconverter.data.model.ConversionRate
import info.learncoding.currencyconverter.data.model.Currency

@Database(
    entities = [Currency::class, ConversionRate::class],
    version = DATABASE_VERSION,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun currencyDao(): CurrencyDao
    abstract fun conversionRateDao(): ConversionRateDao

    companion object {
        const val DATABASE_NAME = "currency_converter"
        const val DATABASE_VERSION = 1

        val migrations = arrayOf<Migration>()
    }
}