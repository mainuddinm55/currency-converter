package info.learncoding.currencyconverter.data.network

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import info.learncoding.currencyconverter.BuildConfig
import info.learncoding.currencyconverter.R
import okhttp3.Interceptor
import okhttp3.Response
import org.jetbrains.annotations.NotNull
import java.io.IOException


class NetworkInterceptor(private val app: Application) : Interceptor {

    @NotNull
    @Throws(IOException::class)
    override fun intercept(@NotNull chain: Interceptor.Chain): Response {
        return if (isNetworkAvailable()) {
            val request = chain.request().newBuilder()
                .addHeader("apikey", BuildConfig.API_KEY)
                .build()
            chain.proceed(request)
        } else {
            throw IOException(app.getString(R.string.no_internet_connection))
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            app.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork
            if (activeNetwork != null) {
                val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
                networkCapabilities != null && (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || networkCapabilities.hasTransport(
                    NetworkCapabilities.TRANSPORT_WIFI
                ))
            } else {
                false
            }
        } else {
            val networkInfo = connectivityManager.activeNetworkInfo
            networkInfo != null && networkInfo.isConnected && (networkInfo.type == ConnectivityManager.TYPE_MOBILE ||
                    networkInfo.type == ConnectivityManager.TYPE_WIFI)
        }
    }
}