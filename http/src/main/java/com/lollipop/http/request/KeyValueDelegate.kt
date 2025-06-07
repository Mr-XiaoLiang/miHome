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

