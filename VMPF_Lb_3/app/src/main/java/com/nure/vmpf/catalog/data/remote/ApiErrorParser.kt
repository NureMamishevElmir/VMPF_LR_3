package com.nure.vmpf.catalog.data.remote

import com.google.gson.Gson
import com.nure.vmpf.catalog.data.model.ApiError
import retrofit2.HttpException
import java.io.IOException

/**
 * Перетворює виняток мережевого запиту на зрозуміле користувачу повідомлення.
 *
 * Сервер ЛР2 повертає помилки у форматі { error: "..." } або { errors: [...] }.
 * Тут ми дістаємо тіло помилки HTTP-відповіді та формуємо текст.
 */
fun parseApiError(throwable: Throwable): String {
    return when (throwable) {
        is HttpException -> {
            val raw = throwable.response()?.errorBody()?.string()
            val parsed = raw?.let {
                runCatching { Gson().fromJson(it, ApiError::class.java) }.getOrNull()
            }
            when {
                parsed?.errors?.isNotEmpty() == true -> parsed.errors.joinToString("\n")
                !parsed?.error.isNullOrBlank() -> parsed!!.error!!
                else -> "Помилка сервера (код ${throwable.code()})"
            }
        }

        is IOException ->
            "Немає з'єднання із сервером. Переконайтесь, що сервер ЛР2 запущено " +
                "(npm start у папці server) та доступний за адресою ${RetrofitClient.BASE_URL}."

        else -> throwable.message ?: "Невідома помилка"
    }
}
