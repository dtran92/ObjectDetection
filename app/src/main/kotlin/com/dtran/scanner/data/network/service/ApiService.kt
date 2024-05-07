package com.dtran.scanner.data.network.service

import com.dtran.scanner.data.common.Resource
import com.dtran.scanner.data.network.model.Country
import kotlinx.coroutines.flow.Flow

interface ApiService {
    fun fetchCountries(): Flow<Resource<List<Country>>>
}