package com.dtran.scanner.di

import androidx.room.Room
import com.dtran.scanner.BuildConfig
import com.dtran.scanner.data.database.AppDatabase
import com.dtran.scanner.data.database.dao.CountryDao
import com.dtran.scanner.data.network.repository.ApiRepository
import com.dtran.scanner.data.network.repository.FirebaseRepository
import com.dtran.scanner.data.network.service.ApiService
import com.dtran.scanner.data.network.service.FirebaseService
import com.dtran.scanner.ui.screen.flag.FlagViewModel
import com.dtran.scanner.ui.screen.list.ListViewModel
import com.dtran.scanner.ui.screen.login.LoginViewModel
import com.dtran.scanner.ui.screen.result.ResultViewModel
import com.dtran.scanner.ui.screen.scan.ScanViewModel
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val networkModule = module {
    // Ktor Client
    single {
        HttpClient(OkHttp) {
            engine {
                config {
                    followRedirects(true)
                    addInterceptor(HttpLoggingInterceptor().apply { setLevel(HttpLoggingInterceptor.Level.BODY) })
                }
            }

            expectSuccess = true
            HttpResponseValidator {
                validateResponse { response ->
                    when (response.status.value) {
                        in 300..399 -> throw RedirectResponseException(response, "300-399")
                        in 400..499 -> throw ClientRequestException(response, "400-499")
                        in 500..599 -> throw ServerResponseException(response, "500-599")
                    }
                }

                handleResponseExceptionWithRequest { exception, _ ->
                    throw exception
                }
            }

            install(DefaultRequest) {
                url(BuildConfig.BASE_URL)
                header(HttpHeaders.ContentType, ContentType.Application.Json)
            }
            install(ContentNegotiation) {
                json(Json {
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }
        }
    }

    // Repository
    single<FirebaseService> { FirebaseRepository() }
    single<ApiService> { ApiRepository(get(), get(), androidContext()) }
}

val databaseModule = module {
    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "Mydatabase")
            .fallbackToDestructiveMigration().build()
    }

    single<CountryDao> {
        get<AppDatabase>().countryDao
    }
}


val viewModelModule = module {
    // ViewModel
    viewModel { LoginViewModel(get()) }
    viewModel { ResultViewModel(get()) }
    viewModel { ListViewModel(get()) }
    viewModel { ScanViewModel(get()) }
    viewModel { ScanViewModel(get()) }
    viewModel { FlagViewModel(get()) }
}