package com.belomor

import android.app.Application
import com.belomor.currenciesgraph.activity.main.di.mainActivityModule
import com.belomor.currenciesgraph.network.networkModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class CurrenciesApp : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@CurrenciesApp)
            modules(listOf(networkModule,
                mainActivityModule
            ))
        }
    }
}