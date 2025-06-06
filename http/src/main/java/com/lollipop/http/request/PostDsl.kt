package com.lollipop.http.request

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

fun HttpRequestBuilder.postForm(
    encode: Boolean = true,
    builder: @HttpBuilderDsl KeyValueDelegate.() -> Unit
) {
    val formBody = FromDataDelegate(encode).apply(builder).build()
    requestBuilder.post(formBody)
}

fun HttpRequestBuilder.postJson(builder: @HttpBuilderDsl JsonDelegate.() -> Unit) {
    val jsonObject = JsonDelegate().apply(builder).build()
    requestBuilder.post(
        jsonObject.toRequestBody(
            "application/json".toMediaType()
        )
    )
}

fun HttpRequestBuilder.postJson(json: String) {
    requestBuilder.post(
        json.toRequestBody(
            "application/json".toMediaType()
        )
    )
}
