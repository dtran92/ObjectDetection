package com.dtran.scanner.data.network.repository

import android.content.Context
import com.dtran.scanner.data.common.networkBoundResource
import com.dtran.scanner.data.database.dao.CountryDao
import com.dtran.scanner.data.database.model.CountryEntity
import com.dtran.scanner.data.database.model.MediaEntity
import com.dtran.scanner.data.network.model.Country
import com.dtran.scanner.data.network.model.Media
import com.dtran.scanner.data.network.service.ApiService
import com.dtran.scanner.util.Util
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.coroutines.flow.map

class ApiRepository(
    private val httpClient: HttpClient,
    private val countryDao: CountryDao,
    private val context: Context
) :
    ApiService {

    override fun fetchCountries() = networkBoundResource(query = {
        countryDao.getAll().map {
            it.map { country ->
                Country(
                    abbreviation = country.abbreviation,
                    name = country.name,
                    capital = country.capital,
                    currency = country.currency,
                    id = country.id,
                    phone = country.phone,
                    media = country.media?.let { media ->
                        Media(
                            flag = media.flag, emblem = media.emblem, orthographic = media.orthographic
                        )
                    },
                )
            }
        }
    }, fetch = { httpClient.get {}.body<List<Country>>() }, saveFetchResult = { countries ->
        countryDao.deleteAll()
        countryDao.save(countries.map { country ->
            CountryEntity(
                abbreviation = country.abbreviation,
                name = country.name,
                capital = country.capital,
                currency = country.currency,
                id = country.id,
                phone = country.phone,
                media = country.media?.let { media ->
                    MediaEntity(
                        flag = media.flag, emblem = media.emblem, orthographic = media.orthographic
                    )
                },
            )
        })
    }, shouldFetch = { Util.isNetworkConnected(context) })
}