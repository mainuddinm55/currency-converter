package info.learncoding.currencyconverter.database

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import info.learncoding.currencyconverter.data.local.AppDatabase
import info.learncoding.currencyconverter.data.local.dao.CurrencyDao
import info.learncoding.currencyconverter.data.model.Currency
import info.learncoding.currencyconverter.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.*
import kotlin.jvm.Throws

@RunWith(AndroidJUnit4::class)
class CurrencyDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var currencyDao: CurrencyDao

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        currencyDao = database.currencyDao()
    }

    @After
    @Throws(IOException::class)
    fun close() {
        database.close()
    }

    @Test
    fun test_insert_currency() {
        runBlocking(Dispatchers.Main) {
            val currency = Currency(1, currency = "BDT", country = "Bangladesh", Date())
            currencyDao.insert(currency)
            val insertedCurrencies = currencyDao.getAll().getOrAwaitValue()
            assertThat(insertedCurrencies.contains(currency)).isTrue()
        }
    }

    @Test
    fun test_insert_currencies() {
        runBlocking(Dispatchers.Main) {
            val currencies = mutableListOf<Currency>()
            for (i in 1..10) {
                currencies.add(Currency(i, currency = "BDT", country = "Bangladesh", Date()))
            }
            currencyDao.insert(currencies)
            val insertedCurrencies = currencyDao.getAll().getOrAwaitValue()
            assertThat(insertedCurrencies.size == currencies.size).isTrue()
        }
    }

    @Test
    fun test_get_last_currency() {
        runBlocking(Dispatchers.Main) {
            val currencies = mutableListOf<Currency>()
            for (i in 1..10) {
                currencies.add(
                    Currency(
                        i,
                        currency = "BDT",
                        country = "Bangladesh",
                        Date(System.currentTimeMillis() + i)
                    )
                )
            }
            currencyDao.insert(currencies)
            val insertedCurrencies = currencyDao.getLast()
            assertThat(insertedCurrencies.first() == currencies.last()).isTrue()
        }
    }

    @Test
    fun test_delete_all_currencies() {
        runBlocking(Dispatchers.Main) {
            val currencies = mutableListOf<Currency>()
            for (i in 1..10) {
                currencies.add(
                    Currency(
                        i,
                        currency = "BDT",
                        country = "Bangladesh",
                        Date(System.currentTimeMillis() + i)
                    )
                )
            }
            currencyDao.insert(currencies)
            currencyDao.deleteAll()
            val currenciesFromDb = currencyDao.getAll().getOrAwaitValue()
            assertThat(currenciesFromDb.isEmpty()).isTrue()
        }
    }
}