package com.lollipop.http

import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

fun HttpRequestBuilder.form(builder: @HttpBuilderDsl FormBody.Builder.() -> Unit) {

    val formBody = FormBody.Builder().apply(builder).build()
    requestBuilder.post(formBody)

}

fun HttpRequestBuilder.json(builder: @HttpBuilderDsl JSONObject.() -> Unit) {
    val jsonObject = JSONObject().apply(builder)
    requestBuilder.post(
        jsonObject.toString().toRequestBody(
            "application/json".toMediaType()
        )
    )
}

fun HttpRequestBuilder.json(json: String) {
    requestBuilder.post(
        json.toRequestBody(
            "application/json".toMediaType()
        )
    )
}
