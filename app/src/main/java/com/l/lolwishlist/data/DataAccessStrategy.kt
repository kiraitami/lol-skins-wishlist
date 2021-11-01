package com.l.lolwishlist.data

import android.util.Log
import com.l.lolwishlist.data.model.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

inline fun <ResultType> networkBoundResource(
    crossinline fromRoom: () -> Flow<ResultType>,
    crossinline fromNetwork: suspend () -> ResultType,
    crossinline saveNetworkResult: suspend (ResultType) -> Unit,
    crossinline shouldFetchFromNetwork: (ResultType) -> Boolean = { true }
) = flow {
    emit(Result.Loading<ResultType>())

    val roomData = fromRoom().first()

    emit(Result.Success<ResultType>(roomData))

    val flow = if (shouldFetchFromNetwork(roomData)) {
        try {
            val networkData = fromNetwork()

            saveNetworkResult(networkData)

            fromRoom().map { Result.Success<ResultType>(it) }
        }
        catch (e: Exception) {
            fromRoom().map { Result.Error<ResultType>(e) }
        }
    }
    else {
        fromRoom().map { Result.Success<ResultType>(it) }
    }

    emitAll(flow)

}.flowOn(Dispatchers.IO)