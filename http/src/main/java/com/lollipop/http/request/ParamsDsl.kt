package com.lollipop.http.request

import okhttp3.HttpUrl

fun HttpRequestBuilder.Params(
    builder: @HttpBuilderDsl UrlParamsDelegate.() -> Unit
) {
    val urlBuilder = currentUrl.newBuilder()
    UrlParamsDelegate(urlBuilder).apply(builder)
    updateUrl(urlBuilder.build())
}

class UrlParamsDelegate(
    private val builder: HttpUrl.Builder
) : KeyValueDelegate() {

    override fun add(key: String, value: String) {
        builder.addQueryParameter(key, value)
    }

    fun set(key: String, value: String) {
        builder.setQueryParameter(key, value)
    }

    override fun addAny(key: String, value: Any) {
        when (value) {
            is String -> {
                add(key = key, value = value)
            }

            else -> {
                throw IllegalArgumentException("Unsupported type：${value::class.java.name}")
            }
        }
    }

}
