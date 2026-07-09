package com.yjp.functions.common.result

sealed class FunctionsResult<out T> {

    data class Success<out T>(
        val data: T,
    ) : FunctionsResult<T>()

    data class Fail(
        val message: String,
        val throwable: Throwable? = null,
    ) : FunctionsResult<Nothing>()
}
