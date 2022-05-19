package info.learncoding.currencyconverter.database

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import info.learncoding.currencyconverter.data.local.AppDatabase
import info.learncoding.currencyconverter.data.local.dao.ConversionDao
import info.learncoding.currencyconverter.data.model.Conversion
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
class ConversionDaoTest {
    private lateinit var database: AppDatabase
    private lateinit var conversionDao: ConversionDao

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        conversionDao = database.conversionDao()
    }

    @After
    @Throws(IOException::class)
    fun close() {
        database.close()
    }

    @Test
    fun test_insert_conversions() {
        runBlocking(Dispatchers.Main) {
            val conversion = Conversion(
                id = 1,
                source = "USD",
                currency = "BDT",
                amount = 87.56,
                createdAt = Date()
            )

            conversionDao.insert(listOf(conversion))
            val insertedConversions = conversionDao.getAll().getOrAwaitValue()
            assertThat(insertedConversions.contains(conversion)).isTrue()
        }
    }

    @Test
    fun test_get_last_conversions() {
        runBlocking(Dispatchers.Main) {
            val conversions = mutableListOf<Conversion>()
            for (i in 1..10) {
                conversions.add(
                    Conversion(
                        id = i,
                        source = "USD",
                        currency = "BDT",
                        amount = 87.56,
                        createdAt = Date(System.currentTimeMillis() + i)
                    )
                )
            }
            conversionDao.insert(conversions)
            val insertedConversions = conversionDao.getLast()
            assertThat(insertedConversions.first() == conversions.last()).isTrue()
        }
    }

    @Test
    fun test_delete_all_conversions() {
        runBlocking(Dispatchers.Main) {
            val conversions = mutableListOf<Conversion>()
            for (i in 1..10) {
                conversions.add(
                    Conversion(
                        id = i,
                        source = "USD",
                        currency = "BDT",
                        amount = 87.56,
                        createdAt = Date(System.currentTimeMillis() + i)
                    )
                )
            }
            conversionDao.insert(conversions)
            conversionDao.deleteAll()
            val currenciesFromDb = conversionDao.getAll().getOrAwaitValue()
            assertThat(currenciesFromDb.isEmpty()).isTrue()
        }
    }
    @Test
    fun test_convert_bdt_to_usd() {
        runBlocking(Dispatchers.Main) {
            val conversion = Conversion(
                id = 1,
                source = "USD",
                currency = "BDT",
                amount = 87.56,
                createdAt = Date()
            )

            conversionDao.insert(listOf(conversion))
            val conversions = conversionDao.convertCurrency("BDT",1200.00).getOrAwaitValue()
            assertThat(conversions.first().amount).isAtMost(13.70)
        }
    }

}