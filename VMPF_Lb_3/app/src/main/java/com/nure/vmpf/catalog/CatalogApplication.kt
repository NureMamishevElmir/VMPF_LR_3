package com.nure.vmpf.catalog

import android.app.Application
import com.nure.vmpf.catalog.data.CartRepository

/**
 * Клас застосунку. Ініціалізує сховище кошика один раз при старті процесу,
 * щоб надалі мати доступ до збереженого стану кошика з будь-якого екрана.
 */
class CatalogApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        CartRepository.init(this)
    }
}
