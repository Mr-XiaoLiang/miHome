package com.lollipop.http.safe

import com.lollipop.http.request.HttpBuilderDsl

sealed class SafeResult<T> {

    class Success<T>(val data: T) : SafeResult<T>()

    class Error<T>(val error: Throwable) : SafeResult<T>()

}

inline fun <reified T> SafeResult<T>.onSuccess(block: ((T) -> Unit)): SafeResult<T> {
    if (this is SafeResult.Success) {
        block(data)
    }
    return this
}

inline fun <reified T> SafeResult<T>.onError(block: ((Throwable) -> Unit)): SafeResult<T> {
    if (this is SafeResult.Error) {
        block(error)
    }
    return this
}

inline fun <reified T, reified R> T.safe(block: @HttpBuilderDsl T.() -> R): SafeResult<R> {
    return try {
        SafeResult.Success(block())
    } catch (e: Throwable) {
        SafeResult.Error(e)
    }
}

inline fun <reified T, reified R> SafeResult<T>.safeMap(
    block: @HttpBuilderDsl (T) -> R
): SafeResult<R> {
    try {
        val input = this
        return when (input) {
            is SafeResult.Error<T> -> {
                SafeResult.Error(input.error)
            }

            is SafeResult.Success<T> -> {
                SafeResult.Success(block(input.data))
            }
        }
    } catch (e: Throwable) {
        return SafeResult.Error(e)
    }
}
