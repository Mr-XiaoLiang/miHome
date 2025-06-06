package com.lollipop.http.request

import okhttp3.HttpUrl
import okhttp3.Request

@DslMarker
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE)
annotation class HttpBuilderDsl

class HttpRequestBuilder(private val url: HttpUrl) {

    val requestBuilder = Request.Builder().apply {
        url(url)
    }

}


