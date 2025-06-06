package com.lollipop.http.request

import okhttp3.FormBody
import org.json.JSONArray
import org.json.JSONObject

abstract class KeyValueDelegate {

    abstract fun add(key: String, value: String)

    abstract fun addAny(key: String, value: Any)

    infix fun <K, T> K.to(that: T) {
        val key = this
        if (key == null) {
            return
        }
        if (key !is String) {
            throw IllegalArgumentException("Unsupported KEY：${key::class.java.name}")
        }
        when (that) {
            is String -> {
                add(key = this, value = that)
            }

            is Any -> {
                addAny(key = this, value = that)
            }

            else -> {
                throw IllegalArgumentException("Unsupported type：${that::class.java.name}")
            }
        }
    }

}


class FromDataDelegate(
    private val encode: Boolean = true
) : KeyValueDelegate() {

    private val data = FormBody.Builder()

    override fun add(key: String, value: String) {
        if (encode) {
            data.addEncoded(key, value)
        } else {
            data.add(key, value)
        }
    }

    override fun addAny(key: String, value: Any) {
        if (value is String) {
            add(key = key, value = value)
            return
        }
        throw IllegalArgumentException("Unsupported type：${value::class.java.name}")
    }

    fun build(): FormBody {
        return data.build()
    }

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
