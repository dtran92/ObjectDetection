package com.dtran.scanner.data.common

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

inline fun <ResultType, RequestType> networkBoundResource(
    crossinline query: () -> Flow<ResultType>,
    crossinline fetch: suspend () -> RequestType,
    crossinline saveFetchResult: suspend (RequestType) -> Unit,
    crossinline shouldFetch: (ResultType) -> Boolean = { true }
) = flow {

    val data = query().first()
    val resource = if (shouldFetch(data)) {

        emit(Resource.Loading(data))

        try {
            val resultType = fetch()

            saveFetchResult(resultType)

            query().map { Resource.Success(it) }

        } catch (throwable: Throwable) {

            query().map { Resource.Error(throwable, it) }

        }
    } else {
        query().map { Resource.Success(it) }
    }
    emitAll(resource)
}.flowOn(Dispatchers.IO)