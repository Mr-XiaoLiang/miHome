package com.lollipop.http

import com.lollipop.http.request.HttpBuilderDsl
import com.lollipop.http.request.HttpRequestBuilder
import com.lollipop.http.request.postForm
import com.lollipop.http.request.postJson
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import java.net.URL

object HTTP {

    fun with(url: String, builder: @HttpBuilderDsl HttpRequestBuilder.() -> Unit) {
        with(url.toHttpUrl(), builder)
    }

    fun with(url: URL, builder: @HttpBuilderDsl HttpRequestBuilder.() -> Unit) {
        with(url.toString().toHttpUrl(), builder)
    }

    fun with(url: HttpUrl, builder: @HttpBuilderDsl HttpRequestBuilder.() -> Unit) {
        HttpRequestBuilder(url).apply(builder)
    }

}

fun test() {
    HTTP.with("") {
        "" to ""
        postJson {
            "a" to ""
            "123" to 1234
            123 to 1234.0

            add("a", 10)
            add("b", "hello")
            addObject("abc") {
                add("a", 10)
                addArray("a") {
                    add("")
                }
            }
        }
        postForm {
            "" to ""
            add("", "")
        }
    }
}
