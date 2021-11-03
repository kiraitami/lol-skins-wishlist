package com.l.lolwishlist.model

sealed class Result<T>(
    val data: T? = null,
    val exception: Exception? = null
) {

    class Loading<T>(data: T? = null) : Result<T>(data)
    class Success<T>(data: T) : Result<T>(data)
    class Error<T>(exception: Exception) : Result<T>(null, exception)

    inline fun handle(
        onLoading: (() -> Unit) = {},
        onError: ((Exception?) -> Unit) = {},
        onSuccess: ((T?) -> Unit) = {}
    ) {
        when(this) {
            is Loading -> onLoading.invoke()
            is Success -> onSuccess.invoke(this.data)
            is Error -> onError.invoke(this.exception)
        }
    }
}

fun <T> T?.toResult(): Result<T> {
    return if (this == null) {
        Result.Error(NullPointerException())
    }
    else {
        Result.Success(this)
    }
}