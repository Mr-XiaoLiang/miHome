package com.lollipop.http.request

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

fun HttpRequestBuilder.PostForm(
    encode: Boolean = true,
    builder: @HttpBuilderDsl KeyValueDelegate.() -> Unit
) {
    val formBody = FromDataDelegate(encode).apply(builder).build()
    requestBuilder.post(formBody)
}

fun HttpRequestBuilder.PostJson(builder: @HttpBuilderDsl JsonDelegate.() -> Unit) {
    val jsonObject = JsonDelegate().apply(builder).build()
    requestBuilder.post(
        jsonObject.toRequestBody(
            "application/json".toMediaType()
        )
    )
}

fun HttpRequestBuilder.PostJson(json: String) {
    requestBuilder.post(
        json.toRequestBody(
            "application/json".toMediaType()
        )
    )
}


class JsonDelegate : KeyValueDelegate() {

    internal val data = JSONObject()

    override fun add(key: String, value: String) {
        data.put(key, value)
    }

    override fun addAny(key: String, value: Any) {
        when (value) {
            is String -> {
                add(key = key, value = value)
            }

            is Int -> {
                add(key = key, value = value)
            }

            is Long -> {
                add(key = key, value = value)
            }

            is Double -> {
                add(key = key, value = value)
            }

            is Boolean -> {
                add(key = key, value = value)
            }

            is JSONObject -> {
                data.put(key, value)
            }

            is JSONArray -> {
                data.put(key, value)
            }

            else -> {
                throw IllegalArgumentException("Unsupported type：${value::class.java.name}")
            }
        }
    }

    fun add(key: String, value: Int) {
        data.put(key, value)
    }

    fun add(key: String, value: Long) {
        data.put(key, value)
    }

    fun add(key: String, value: Double) {
        data.put(key, value)
    }

    fun add(key: String, value: Boolean) {
        data.put(key, value)
    }

    fun addObject(key: String, builder: @HttpBuilderDsl JsonDelegate.() -> Unit) {
        val json = JsonDelegate().apply(builder)
        data.put(key, json.data)
    }

    fun addArray(key: String, builder: @HttpBuilderDsl JsonArrayDelegate.() -> Unit) {
        val json = JsonArrayDelegate().apply(builder)
        data.put(key, json.data)
    }

    fun build(): String {
        return data.toString()
    }

}

class JsonArrayDelegate {

    internal val data = JSONArray()

    fun add(value: String) {
        data.put(value)
    }

    fun add(value: Int) {
        data.put(value)
    }

    fun add(value: Long) {
        data.put(value)
    }

    fun add(value: Double) {
        data.put(value)
    }

    fun add(value: Boolean) {
        data.put(value)
    }

    fun addObject(builder: @HttpBuilderDsl JsonDelegate.() -> Unit) {
        val json = JsonDelegate().apply(builder)
        data.put(json.data)
    }

    fun addArray(builder: @HttpBuilderDsl JsonArrayDelegate.() -> Unit) {
        val json = JsonArrayDelegate().apply(builder)
        data.put(json.data)
    }

}
