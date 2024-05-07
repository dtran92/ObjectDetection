package com.dtran.scanner.ui.screen.flag

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dtran.scanner.data.common.Resource
import com.dtran.scanner.data.network.repository.ApiRepository
import com.dtran.scanner.ui.model.CountryUiModel
import com.dtran.scanner.ui.model.MediaUiModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

class FlagViewModel(private val apiRepository: ApiRepository) : ViewModel() {
    private val _countryList = MutableStateFlow(listOf<CountryUiModel>())
    val countryList = _countryList.asStateFlow()

    val countryFlow = apiRepository.fetchCountries().map {
        when (it) {
            is Resource.Loading -> Resource.Loading()
            is Resource.Error -> Resource.Error(throwable = it.error ?: Throwable())
            is Resource.Success -> Resource.Success(data = it.data?.map { country ->
                CountryUiModel(
                    abbreviation = country.abbreviation,
                    name = country.name,
                    capital = country.capital,
                    currency = country.currency,
                    id = country.id,
                    phone = country.phone,
                    media = country.media?.let { media ->
                        MediaUiModel(
                            flag = media.flag, emblem = media.emblem, orthographic = media.orthographic
                        )
                    },
                )
            } ?: emptyList())
        }
    }.flowOn(Dispatchers.IO).stateIn(
        viewModelScope, started = SharingStarted.Lazily,
        initialValue = Resource.Loading(null)
    )

    fun updateCountryList(newList: List<CountryUiModel>) {
        _countryList.value = newList
    }
}