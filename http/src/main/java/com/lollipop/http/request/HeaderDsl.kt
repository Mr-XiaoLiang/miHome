package com.lollipop.http.request

fun HttpRequestBuilder.Header(
    builder: @HttpBuilderDsl HeaderDelegate.() -> Unit
) {
    HeaderDelegate(this).apply(builder)
}

class HeaderDelegate(
    private val builder: HttpRequestBuilder
) : KeyValueDelegate() {

    private fun addHeader(key: String, value: String) {
        builder.requestBuilder.addHeader(key, value)
    }

    private fun setHeader(key: String, value: String) {
        builder.requestBuilder.header(key, value)
    }

    override fun add(key: String, value: String) {
        addHeader(key, value)
    }

    fun set(key: String, value: String) {
        setHeader(key, value)
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