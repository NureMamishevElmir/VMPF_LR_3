package com.nure.vmpf.catalog.data.remote

import com.nure.vmpf.catalog.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Налаштування Retrofit-клієнта.
 *
 * BASE_URL = http://10.0.2.2:3000/ — адреса, за якою Android-емулятор
 * звертається до localhost хост-машини, де запущено сервер ЛР2.
 * Для запуску на фізичному пристрої замініть на IP комп'ютера в локальній мережі
 * (наприклад, http://192.168.0.10:3000/).
 */
object RetrofitClient {

    const val BASE_URL = "http://10.0.2.2:3000/"

    val api: ApiService by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
