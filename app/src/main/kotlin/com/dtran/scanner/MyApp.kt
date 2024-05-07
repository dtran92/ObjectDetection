package com.dtran.scanner

import android.app.Application
import com.dtran.scanner.di.databaseModule
import com.dtran.scanner.di.networkModule
import com.dtran.scanner.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@MyApp)
            modules(listOf(networkModule, viewModelModule, databaseModule))
        }
    }
}