package info.learncoding.currencyconverter.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import info.learncoding.currencyconverter.CurrencyConversionApp
import info.learncoding.currencyconverter.data.local.AppDatabase
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideApp(@ApplicationContext app: Context): CurrencyConversionApp {
        return app as CurrencyConversionApp
    }

    @Provides
    @Singleton
    fun provideAppDatabase(app: CurrencyConversionApp):AppDatabase{
        return Room.databaseBuilder(app,AppDatabase::class.java,AppDatabase.DATABASE_NAME)
            .addMigrations(*AppDatabase.migrations)
            .build()
    }

}