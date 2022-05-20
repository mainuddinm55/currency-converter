package info.learncoding.currencyconverter.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import info.learncoding.currencyconverter.data.local.dao.ConversionDao
import info.learncoding.currencyconverter.data.local.dao.CurrencyDao
import info.learncoding.currencyconverter.data.model.Conversion
import info.learncoding.currencyconverter.data.model.Currency
import info.learncoding.currencyconverter.utils.AppConstraint.DATABASE_VERSION

@Database(
    entities = [Currency::class, Conversion::class],
    version = DATABASE_VERSION,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun currencyDao(): CurrencyDao
    abstract fun conversionDao(): ConversionDao

    companion object {
        val migrations = arrayOf<Migration>()
    }
}